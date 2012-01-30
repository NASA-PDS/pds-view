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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

/**
 * Class to do referential integrity checking on volumes.
 *
 * @author mcayanan
 *
 */
public class VolumeRIChecker extends RIChecker {
    private static String MEDIUM_TYPE = "MEDIUM_TYPE";

    public void performCheck(List<Label> parentLabels, List<Label> childLabels) {
        List<AttributeStatement> parents = new ArrayList<AttributeStatement>();
        List<AttributeStatement> children = new ArrayList<AttributeStatement>();

        parents = StatementFinder.getStatementsRecursively(parentLabels, MEDIUM_TYPE);

        for(Label child : childLabels) {
            List<AttributeStatement> childStmts = StatementFinder
            .getStatementsRecursively(child, MEDIUM_TYPE);
            children.addAll(childStmts);
            Map<String, AttributeStatement> results = getUnmatchedValues(parents, childStmts);
            for (Map.Entry<String, AttributeStatement> entry : results.entrySet()) {
                AttributeStatement a = entry.getValue();
                String statement = a.getIdentifier().getId() + " = " + entry.getKey();
                Object[] arguments = {statement, "volume.cat"};
                LabelParserException le = new LabelParserException(
                        a.getSourceURI(), null, null,
                        "referentialIntegrity.error.missingIdInParent",
                        Constants.ProblemType.MISSING_ID, arguments);
                addProblem(le);
            }
        }
        ValueMatcher matcher = new ValueMatcher(children);
        Map<String, AttributeStatement> missingFromChildren = matcher.getUnmatched(parents);
        if (!missingFromChildren.isEmpty()) {
            for (Map.Entry<String, AttributeStatement> entry : missingFromChildren.entrySet()) {
                AttributeStatement a = entry.getValue();
                String statement = a.getIdentifier().getId() + " = " + entry.getKey();
                Object[] arguments = {statement, "non volume.cat"};
                LabelParserException le = new LabelParserException(
                        a.getSourceURI(), null, null,
                        "referentialIntegrity.error.missingIdInChildren",
                        Constants.ProblemType.MISSING_ID, arguments);
                addProblem(le);
            }
        }
    }

    @Override
    public RIType getType() {
        return RIType.VOLUME;
    }
}
