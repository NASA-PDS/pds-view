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
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.Label;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for Referential Integrity checking.
 *
 * @author mcayanan
 *
 */
public abstract class RIChecker {
    protected URL supportFile = null;
    private List<LabelParserException> problems =
        new ArrayList<LabelParserException>();

    public Map<String, AttributeStatement> getUnmatchedValues(
            List<AttributeStatement> parents,
            List<AttributeStatement> children) {
        ValueMatcher matcher = new ValueMatcher(parents);
        return matcher.getUnmatched(children);
    }

    public Map<String, AttributeStatement> getUnmatchedValues(
            Map<String, List<AttributeStatement>> parents,
            Map<String, List<AttributeStatement>> children) {
        Map<String, AttributeStatement> result =
            new LinkedHashMap<String, AttributeStatement>();

        for(String identifier : children.keySet()) {
            result.putAll(getUnmatchedValues(parents.get(identifier),
                    children.get(identifier)));
        }
        return result;
    }

    public void setSupportFile(URL url) {
        supportFile = url;
    }

    public void addProblem(LabelParserException problem) {
        problems.add(problem);
    }

    public void addProblems(List<LabelParserException> problems) {
        this.problems.addAll(problems);
    }

    public List<LabelParserException> getProblems() {
        return problems;
    }

    public boolean hasProblems() {
        if (problems.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determine if the supplied list of catalog files has
     * referential integrity.
     *
     * @param parents A list of parent labels.
     * @param children A list of child labels.
     *
     */
    abstract public void performCheck(List<Label> parents,
            List<Label> children);

    abstract public RIType getType();
}
