// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$

package gov.nasa.pds.citool.ri;

import gov.nasa.pds.citool.diff.DiffException;
import gov.nasa.pds.citool.diff.DiffRecord;
import gov.nasa.pds.citool.diff.ValueDiff;
import gov.nasa.pds.citool.util.Utility;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ObjectStatement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/**
 * Class to do referential integrity checking on references.
 *
 * @author mcayanan
 *
 */
public class ReferenceRIChecker extends RIChecker {
    private Map<AttributeStatement, AttributeStatement> idToDescriptions;
    private List<String> refFiles;
    private List<AttributeStatement> parentIDs;
    private final String ID = "REFERENCE_KEY_ID";
    private final String DESCRIPTION = "REFERENCE_DESC";

    /**
     * Constructor
     *
     */
    public ReferenceRIChecker() {
        idToDescriptions = new HashMap<AttributeStatement, AttributeStatement>();
        parentIDs = new ArrayList<AttributeStatement>();
        refFiles = new ArrayList<String>();
    }

    /**
     * Determines whether the supplied list of catalog files has referential
     * integrity when compared to the REF.CAT file.
     *
     * @param parentLabels A set of catalog files containing the REFERENCE
     * object.
     * @param childLabels A set of catalog files that do not contain the
     * REFERENCE object.
     */
    public void performCheck(List<Label> parentLabels, List<Label> childLabels) {
        List<AttributeStatement> children = new ArrayList<AttributeStatement>();
        //Get reference attributes
        setParentAttributes(parentLabels);

        //Check references against the allrefs dictionary support file
        if (supportFile != null) {
            addProblems(validate(supportFile));
        }
        //Check that reference key IDs in the parent are all found in the
        //child labels
        Map<String, List<AttributeStatement>> parentMap = new HashMap<String, List<AttributeStatement>>();
        parentMap.put(ID, parentIDs);
        for (Label child : childLabels) {
            List<AttributeStatement> childStmts = StatementFinder.getStatementsRecursively(child, ID);
            children.addAll(childStmts);
            Map<String, AttributeStatement> results = getUnmatchedValues(parentIDs, childStmts);
            for (Map.Entry<String, AttributeStatement> entry : results.entrySet()) {
                AttributeStatement a = entry.getValue();
                String statement = a.getIdentifier().getId() + " = " + entry.getKey();
                Object[] arguments = {statement, "ref.cat"};
                LabelParserException le = new LabelParserException(
                        a.getSourceURI(), null, null,
                        "referentialIntegrity.error.missingIdInParent",
                        Constants.ProblemType.MISSING_ID, arguments);
                addProblem(le);
            }
        }
        //Check that each ref key ID in the parent references is specified
        //in at least one other catalog file
        ValueMatcher matcher = new ValueMatcher(children);
        Map<String, AttributeStatement> missingFromChildren = matcher.getUnmatched(parentIDs);
        if (!missingFromChildren.isEmpty()) {
            for (Map.Entry<String, AttributeStatement> entry : missingFromChildren.entrySet()) {
                AttributeStatement a = entry.getValue();
                //TODO: Do we really need this?
/*
                log.log(new ToolsLogRecord(CIToolLevel.INFO,
                        a.getIdentifier() + " = " + entry.getKey() + " is not specified in a non REF.CAT file.",
                        a.getFilename(), refFiles.toString(), a.getLineNumber()));
*/
            }
        }
    }

    private void setParentAttributes(List<Label> parents) {
        for (Label parent : parents) {
            List<ObjectStatement> references = (List<ObjectStatement>)
            parent.getObjects(RIType.REFERENCE.getName().toUpperCase());
            for (ObjectStatement ref : references) {
                AttributeStatement id = null;
                if (ref.getAttribute(ID) != null) {
                    id = ref.getAttribute(ID);
                    if (!Utility.isNull(id.getValue().toString())) {
                        parentIDs.add(id);
                        idToDescriptions.put(id, null);
                    }
                }
                idToDescriptions.put(id, ref.getAttribute(DESCRIPTION));
            }
        }
    }

    /**
     * Validates that all reference key IDs and descriptions in the parent
     * exist in the allrefs dictionary support file.
     *
     * @param allrefs dictionary support file for references
     * @return true if all reference key IDs and descriptions in the parent
     * exist in the allrefs file.
     */
    private List<LabelParserException> validate(URL allrefs) {
        List<LabelParserException> problems =
            new ArrayList<LabelParserException>();
        try {
            Map<String, String> allrefsReferences = parse(allrefs);
            for (AttributeStatement idStmt : idToDescriptions.keySet()) {
                String id = idStmt.getValue().toString();
                if (allrefsReferences.containsKey(id)) {
                    AttributeStatement descStmt = idToDescriptions.get(idStmt);
                    String description = descStmt.getValue().toString()
                    .replaceAll("\\s+", " ").trim();
                    String allrefsDesc = allrefsReferences.get(id)
                    .replaceAll("\\s+", " ").trim();
                    if (!description.equals(allrefsDesc)) {
                        URI uri = null;
                        try {
                            uri = allrefs.toURI();
                        } catch (URISyntaxException u) {
                            //Don't do anything
                        }
                        List<DiffRecord> diffRecords = ValueDiff.diff(
                                allrefsDesc, description);
                        DiffException de = new DiffException(uri, null,
                                descStmt.getLineNumber(),
                                "referenceDescription.error.allRefsMisMatch",
                                diffRecords, new Object[0]);
                        problems.add(de);
/*
                        log.log(new ToolsLogRecord(CIToolLevel.SEVERE, "Description for " + id + " does not match description in allrefs dictionary.", refFiles.toString(), descStmt.getLineNumber()));
                        log.log(new ToolsLogRecord(CIToolLevel.SEVERE_NOTIFY, "Source", allrefs.toString(), refFiles.toString()));
                        DiffLogger.logRecord(CIToolLevel.DIFF, diffRecords, refFiles.toString());
*/
                    }
                } else {
                    LabelParserException le = new LabelParserException(
                            new File(refFiles.toString()),
                            new Integer(idStmt.getLineNumber()),
                            null, "allrefs.error.missingId",
                            Constants.ProblemType.MISSING_ID, id);
                    problems.add(le);
                }
            }
        } catch (IOException io) {
            problems.add(new LabelParserException(io, null, null, Constants.ProblemType.INVALID_LABEL_WARNING));
//            log.log(new ToolsLogRecord(CIToolLevel.SEVERE, io.getMessage(), refFiles.toString()));
//            isValid = false;
        }
        return problems;
    }

    /**
     * Parses the allrefs file.
     *
     * @param allrefs The allrefs file.
     *
     * @return A mapping of reference IDs to its description.
     * @throws IOException
     */
    private Map<String, String> parse(URL allrefs) throws IOException {
        Map<String, String> results = new HashMap<String, String>();
        List<String> lines = null;
        InputStream in = null;
        String id = null;
        String desc = null;
        try {
            in = allrefs.openStream();
            lines = (List<String>) IOUtils.readLines(new InputStreamReader(in));
        } finally {
            IOUtils.closeQuietly(in);
        }
        for (String line : lines) {
            String[] values = line.split("[|]", 3);
            if (values[1].equals("1")) {
                if ((id != null) && (id != null))
                    results.put(id, desc);
                id = values[0];
                desc = values[2];
            }
            else {
                desc = desc + " " + values[2];
            }
        }
        if ((id != null) && (desc != null)) {
            results.put(id, desc);
        }
        return results;
    }

    @Override
    public RIType getType() {
        return RIType.REFERENCE;
    }
}
