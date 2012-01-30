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
package gov.nasa.pds.citool.validate;

import gov.nasa.pds.citool.ri.StatementFinder;
import gov.nasa.pds.citool.util.Utility;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.Sequence;
import gov.nasa.pds.tools.label.Statement;
import gov.nasa.pds.tools.label.Value;
import gov.nasa.pds.tools.label.validate.LabelValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that finds references in a statement and makes sure
 * that it is found in a REFERENCE_KEY_ID within the label.
 *
 * References in a non REFERENCE_KEY_ID statement are denoted
 * within square brackets [].
 *
 * @author mcayanan
 *
 */
public class ReferenceFinderValidator implements LabelValidator {
    /**
     * Validates a label by checking references and matching that
     * with REFERENCE_KEY_ID values found within the given label.
     *
     * @param catalog An object representation of a catalog file.
     *
     * @return A flag indicating wheter or not a reference catalog if valid.
     */
    public boolean validate(Label catalog) {
        List<String> refKeyIDValues = new ArrayList<String>();
        boolean passFlag = true;

        //Find REFERENCE_KEY_ID statements in the label
        List<AttributeStatement> refKeyIDStatements = StatementFinder
        .getStatementsRecursively(catalog, "REFERENCE_KEY_ID");
        //Store the REFENCE_KEY_ID values into a list
        //Ignore NULL values
        for(AttributeStatement refKeyIDStatement : refKeyIDStatements) {
            Value value = refKeyIDStatement.getValue();
            if (value instanceof Sequence) {
                Sequence sequence = (Sequence) value;
                for (Iterator i = sequence.iterator(); i.hasNext();) {
                    String v = i.next().toString();
                    if (!Utility.isNull(v)) {
                        refKeyIDValues.add(v);
                    }
                }
            }
            else if(value instanceof Set) {
                gov.nasa.pds.tools.label.Set set =
                    (gov.nasa.pds.tools.label.Set) value;
                for (Iterator i = set.iterator(); i.hasNext();) {
                    String v = i.next().toString();
                    if (!Utility.isNull(v)) {
                        refKeyIDValues.add(v);
                    }
                }
            }
            else {
                if (!Utility.isNull(value.toString())) {
                    refKeyIDValues.add(value.toString());
                }
            }
        }
        //Search the rest of the label for references IDs used in a
        //description
        for(Statement statement : (List<Statement>) catalog.getStatements()) {
            boolean result = true;
            result = findIDs(catalog, statement, refKeyIDValues);
            if (result == false) {
                passFlag = false;
            }
        }
        return passFlag;
    }

    /**
     * Checks an object statement for references and matches those
     * against REFERENCE_KEY_ID values found in the label.
     *
     * @param object An object statement within a label.
     * @param referenceKeyIDs A list of reference key IDs to match up against
     * references found in the given object statement.
     *
     * @return 'True' if all reference key IDs were found in a
     * REFERENCE_KEY_ID statement within a label.
     */
    private boolean findIDs(Label catalog, ObjectStatement object,
            List<String> referenceKeyIDs) {
        boolean passFlag = true;
        boolean result = true;
        List<Statement> statements = (List<Statement>) object.getStatements();
        for (Statement statement : statements) {
            result = findIDs(catalog, statement, referenceKeyIDs);
            if (result == false) {
                passFlag = false;
            }
        }
        return passFlag;
    }

    /**
     * Checks a statement for references and matches those
     * against REFERENCE_KEY_ID values found in the label.
     *
     * @param statement A statement within a label.
     * @param referenceKeyIDs A list of reference key IDs to match up against
     * references found in the given statement.
     *
     * @return 'True' if all reference key IDs were found in a
     * REFERENCE_KEY_ID statement within a label.
     */
    private boolean findIDs(Label catalog, Statement statement,
            List<String> referenceKeyIDs) {
        boolean passFlag = true;
        boolean result = true;

        if (statement instanceof ObjectStatement) {
            ObjectStatement o = (ObjectStatement) statement;
            result = findIDs(catalog, o, referenceKeyIDs);
        }
        else if (statement instanceof AttributeStatement) {
            AttributeStatement a = (AttributeStatement) statement;
            result = findIDs(catalog, a, referenceKeyIDs);
        }
        if (result == false) {
            passFlag = false;
        }
        return passFlag;
    }

    /**
     * Checks an attribute statement for references and matches those
     * against REFERENCE_KEY_ID values found in the label.
     *
     * @param attribute An attribute statement within a label.
     * @param referenceKeyIDs A list of reference key IDs to match up against
     * references found in the given attribute.
     *
     * @return 'True' if all reference key IDs were found in a
     * REFERENCE_KEY_ID statement within a label.
     */
    private boolean findIDs(Label catalog, AttributeStatement attribute,
            List<String> referenceKeyIDs) {
        boolean passFlag = true;
        // Check for a null value
        if (attribute.getValue() == null) {
          return false;
        }

        //Looking for reference key IDs enclosed in square brackets
        String regularExpression =
            "(\\[{1}[a-zA-Z0-9]+[&-]?[a-zA-Z0-9]+[0-9]{4,}[a-zA-Z]?\\]{1})";
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(attribute.getValue().toString());

        Set<String> uniqueRefIDsFound = new HashSet<String>();
        while (matcher.find()) {
            boolean foundID = false;
            boolean manipulated = false;
            String id = matcher.group();
            //Remove brackets from the ID found
            id = id.replace('[', ' ').trim();
            id = id.replace(']', ' ').trim();
            if (!referenceKeyIDs.contains(id)) {
                foundID = false;
                if (referenceKeyIDs.contains(id.toUpperCase())) {
                    foundID = true;
                    manipulated = true;
                }
            }
            else {
                foundID = true;
            }

            if (!uniqueRefIDsFound.contains(id)) {
                if (foundID) {
                    if (manipulated) {
                        catalog.addProblem(attribute.getSourceURI(),
                                new Integer(attribute.getLineNumber()),
                                null, "referenceId.notUppercase",
                                ProblemType.MISMATCHED_CASE, id);
                    }
                    /*
                    log.log(new ToolsLogRecord(CIToolLevel.INFO,
                            "Found a reference, " + id + ", which is defined in a REFERENCE_KEY_ID within the label.",
                            attribute.getFilename(), attribute.getLineNumber()));
                    */
                }
                else {
                    catalog.addProblem(attribute.getSourceURI(),
                            new Integer(attribute.getLineNumber()),
                            null, "referenceId.notDefined",
                            ProblemType.INVALID_VALUE, id);
                    passFlag = false;
                }
                uniqueRefIDsFound.add(id);
            }
        }
        return passFlag;
    }
}
