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

import gov.nasa.pds.citool.util.Utility;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.Sequence;
import gov.nasa.pds.tools.label.Set;
import gov.nasa.pds.tools.label.Value;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValueMatcher {
    private List<AttributeStatement> statements;

    public ValueMatcher(List<AttributeStatement> statements) {
        this.statements = statements;
    }

    public boolean contains(String value) {
        boolean result = false;
        for (AttributeStatement s : statements) {
            Value v = s.getValue();
            if (v instanceof Sequence) {
                Sequence sequence = (Sequence) v;
                for (Iterator i = sequence.iterator(); i.hasNext();) {
                    if (isEqual(i.next().toString(), value)) {
                        return true;
                    }
                }
//TODO: Implement equals methods in the product-tools package
/*
                if(sequence.contains(value))
                    return true;
*/
            } else if (v instanceof Set) {
                Set set = (Set) v;
                for (Iterator i = set.iterator(); i.hasNext();) {
                    if (isEqual(i.next().toString(), value)) {
                        return true;
                    }
                }
/*
                if(set.contains(value))
                    return true;
*/
            } else {
                if (isEqual(v.toString(), value)) {
                    return true;
                }
            }
        }
        return result;
    }

    public Map<String, AttributeStatement> getUnmatched(List<AttributeStatement> otherStatements) {
        Map<String, AttributeStatement> results = new LinkedHashMap<String, AttributeStatement>();
        for(AttributeStatement otherStatement : otherStatements) {
            Value value = otherStatement.getValue();
            if(value instanceof Sequence) {
                Sequence sequence = (Sequence) value;
                for(Iterator i = sequence.iterator(); i.hasNext();) {
                    String v = i.next().toString();
                    if(!(Utility.isNull(v)) && !(contains(v)))
                        results.put(v, otherStatement);
                }
            }
            else if(value instanceof Set) {
                Set set = (Set) value;
                for(Iterator i = set.iterator(); i.hasNext();) {
                    String v = i.next().toString();
                    if(!(Utility.isNull(v)) && !(contains(v)))
                        results.put(v, otherStatement);
                }
            }
            else {
                if(!(Utility.isNull(value.toString())) && !(contains(value.toString())))
                    results.put(value.toString(), otherStatement);
            }
        }
        return results;
    }

    /**
     * Compare string values for equality. Additionaly, string manipulation
     * is performed in order to match values.
     *
     * @param value1 The first string to compare
     * @param value2 The second string to compare
     * @return true if the values are equal, false otherwise.
     */
    private boolean isEqual(String value1, String value2) {
        boolean passFlag = false;

        if (value1.equals(value2)) {
            passFlag = true;
        } else {
            String filteredValue1 = value1.trim();
            String filteredValue2 = value2.trim();
            if (filteredValue1.equals(filteredValue2)) {
                passFlag = true;
            }
        }
        return passFlag;
    }
}
