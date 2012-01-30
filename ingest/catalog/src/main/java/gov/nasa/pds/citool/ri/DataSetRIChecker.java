// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
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

import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.Label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to perform referential integrity on data sets.
 *
 * @author mcayanan
 *
 */
public class DataSetRIChecker extends RIChecker {
    private static String ID = "DATA_SET_ID";
    private static String COLLECTION_ID = "DATA_SET_COLLECTION_ID";
    private static String COLL_OR_DS_ID = "DATA_SET_COLL_OR_DATA_SET_ID";
    private static String PRODUCT_DS_ID = "PRODUCT_DATA_SET_ID";

    public void performCheck(List<Label> parentLabels,
            List<Label> childLabels) {
        Map<String, List<AttributeStatement>> parents =
            new HashMap<String, List<AttributeStatement>>();
        List<AttributeStatement> children = new ArrayList<AttributeStatement>();

        //Get the DATA_SET_ID and DATA_SET_COLLECTION_ID values from the parents
        for (Label label : parentLabels) {
            if (!label.getObjects(RIType.DATA_SET.getName().toUpperCase())
                    .isEmpty()) {
                 List<AttributeStatement> statements = parents.get(ID);
                 if (statements == null) {
                     statements = new ArrayList<AttributeStatement>();
                     parents.put(ID, statements);
                 }
                 statements.addAll(StatementFinder.getStatementsRecursively(
                         label, ID));
            }
            else {
                List<AttributeStatement> statements = parents.get(
                        COLLECTION_ID);
                if (statements == null) {
                    statements = new ArrayList<AttributeStatement>();
                    parents.put(COLLECTION_ID, statements);
                }
                statements.addAll(StatementFinder.getStatementsRecursively(
                        label, COLLECTION_ID));
            }
        }
        List<String> identifiers = new ArrayList<String>(parents.keySet());
        for (Label child : childLabels) {
            Map<String, List<AttributeStatement>> childStmts =
                StatementFinder.getStatementsRecursively(child, identifiers);
            for (List<AttributeStatement> list : childStmts.values()) {
                children.addAll(list);
            }
            Map<String, AttributeStatement> results = getUnmatchedValues(
                    parents, childStmts);
            results.putAll(getUnmatchedAssociatedValues(parents, child,
                    PRODUCT_DS_ID));
            results.putAll(getUnmatchedAssociatedValues(parents, child,
                    COLL_OR_DS_ID));
            for(Map.Entry<String, AttributeStatement> entry
                    : results.entrySet()) {
                AttributeStatement a = entry.getValue();
                String statement = a.getIdentifier().getId() + " = "
                + entry.getKey();
                Object[] arguments = {statement, "dataset.cat"};
                LabelParserException le = new LabelParserException(
                        a.getSourceURI(), null, null,
                        "referentialIntegrity.error.missingIdInParent",
                        Constants.ProblemType.MISSING_ID, arguments);
                addProblem(le);
            }
        }
        List<AttributeStatement> pStmts = new ArrayList<AttributeStatement>();
        for(List<AttributeStatement> list : parents.values()) {
            pStmts.addAll(list);
        }
        ValueMatcher matcher = new ValueMatcher(children);
        Map<String, AttributeStatement> missingFromChildren =
            matcher.getUnmatched(pStmts);
        if(!missingFromChildren.isEmpty()) {
            for(Map.Entry<String, AttributeStatement> entry
                    : missingFromChildren.entrySet()) {
                AttributeStatement a = entry.getValue();
                String statement = a.getIdentifier().getId() + " = "
                + entry.getKey();
                Object[] arguments = {statement, "non dataset.cat"};
                LabelParserException le = new LabelParserException(
                        a.getSourceURI(), null, null,
                        "referentialIntegrity.error.missingIdInChildren",
                        Constants.ProblemType.MISSING_ID, arguments);
                addProblem(le);
            }
        }
    }

    private Map<String, AttributeStatement> getUnmatchedAssociatedValues(
            Map<String, List<AttributeStatement>> parents,
            Label child, String identifier) {
        List<AttributeStatement> parentStatements =
            new ArrayList<AttributeStatement>();

        for(List<AttributeStatement> p : parents.values()) {
            parentStatements.addAll(p);
        }
        List<AttributeStatement> childStatements = StatementFinder
        .getStatementsRecursively(child, identifier);
        ValueMatcher matcher = new ValueMatcher(parentStatements);
        return matcher.getUnmatched(childStatements);
    }

    @Override
    public RIType getType() {
        return RIType.DATA_SET;
    }
}
