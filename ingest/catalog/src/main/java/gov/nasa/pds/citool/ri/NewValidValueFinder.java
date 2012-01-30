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
import gov.nasa.pds.tools.dict.Dictionary;
import gov.nasa.pds.tools.dict.ElementDefinition;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.GroupStatement;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.Sequence;
import gov.nasa.pds.tools.label.Set;
import gov.nasa.pds.tools.label.Statement;
import gov.nasa.pds.tools.label.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Class that finds new standard values in a statement or set of statements.
 *
 * @author mcayanan
 *
 */
public class NewValidValueFinder {

    /**
     * Finds new standard values in a given statement.
     *
     * @param statement A class representation of a PDS statement.
     * @param dictionary A class representation of a PDS data dictionary.
     * @return A unique list of new standard values.
     */
    public static List<AttributeStatement> getNewValues(Statement statement, Dictionary dictionary) {
        List<AttributeStatement> results = new ArrayList<AttributeStatement>();
        if(statement instanceof AttributeStatement) {
            results.addAll(getNewValues((AttributeStatement) statement, dictionary));
        }
        else if(statement instanceof ObjectStatement) {
            results.addAll(getNewValues((ObjectStatement) statement, dictionary));
        }
        else if(statement instanceof GroupStatement) {
            results.addAll(getNewValues((GroupStatement) statement, dictionary));
        }
        return results;
    }

    /**
     * Finds new standard values in a given attribute.
     *
     * @param attribute A class representation of a PDS attribute statement.
     * @param dictionary A class representation of a PDS data dictionary.
     * @return A unique list of new standard values.
     */
    public static List<AttributeStatement> getNewValues(AttributeStatement attribute, Dictionary dictionary) {
        List<Value> values = new ArrayList<Value>();
        List<AttributeStatement> results = new ArrayList<AttributeStatement>();
        Value value = attribute.getValue();
        ElementDefinition definition = dictionary.getElementDefinition(attribute.getIdentifier());

        if(value == null || (definition == null) || !(definition.hasValidValues()) ||
          (gov.nasa.pds.citool.util.Utility.isNull(value.toString())) ) {
            return results;
        }
        else if(value instanceof Set || value instanceof Sequence) {
            //If the value is a sequence or set, add each value to
            //the list of values to check
            for(Iterator i = ((Collection) value).iterator(); i.hasNext();) {
                Value v = (Value) i.next();
                if(!gov.nasa.pds.citool.util.Utility.isNull(v.toString())) {
                    values.add(v);
                }
            }
        }
        else {
            values.add(value);
        }

        for(Value v : values) {
            //Check against valid values
            if(!definition.getValues().contains(v.toString())) {
                boolean foundValue = false;

                //Perform whitespace stripping
                String filteredValue = Utility.stripNewLines(v.toString());

                if(definition.getValues().contains(filteredValue)) {
                    foundValue = true;
                }
                else if(definition.getValues().contains(filteredValue.toUpperCase())) {
                    foundValue = true;
                }
                else {
                    filteredValue = Utility.filterString(filteredValue.toUpperCase());
                    if(definition.getValues().contains(filteredValue))
                        foundValue = true;
                }
                if(!foundValue) {
                    results.add(new AttributeStatement(attribute.getLabel(), attribute.getIdentifier().getId(), v));
                }
            }
        }
        return results;
    }

    /**
     * Finds new standard values in a given attribute.
     *
     * @param object A class representation of a PDS object statement.
     * @param dictionary A class representation of a PDS data dictionary.
     * @return A unique list of new standard values.
     */
    public static List<AttributeStatement> getNewValues(ObjectStatement object, Dictionary dictionary) {
        List<AttributeStatement> results = new ArrayList<AttributeStatement>();
        List<Statement> statements = (List<Statement>) object.getStatements();
        for(Statement s : statements) {
            if(s instanceof AttributeStatement) {
                results.addAll(getNewValues((AttributeStatement) s, dictionary));
            }
            else if(s instanceof ObjectStatement) {
                results.addAll(getNewValues((ObjectStatement) s, dictionary));
            }
            else if(s instanceof GroupStatement) {
                results.addAll(getNewValues((GroupStatement) s, dictionary));
            }
        }
        return results;
    }

    /**
     * Finds new standard values in a given attribute.
     *
     * @param group A class representation of a PDS group statement.
     * @param dictionary A class representation of a PDS data dictionary.
     * @return A unique list of new standard values.
     */
    public static List<AttributeStatement> getNewValues(GroupStatement group, Dictionary dictionary) {
        List<AttributeStatement> results = new ArrayList<AttributeStatement>();
        List<Statement> statements = (List<Statement>) group.getStatements();
        for(Statement s : statements) {
            if(s instanceof AttributeStatement) {
                results.addAll(getNewValues((AttributeStatement) s, dictionary));
            }
        }
        return results;
    }
}
