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


package gov.nasa.pds.citool.comparator;

import gov.nasa.pds.citool.catalog.CatalogIndex;
import gov.nasa.pds.citool.catalog.CatalogList;
import gov.nasa.pds.citool.diff.DiffException;
import gov.nasa.pds.citool.diff.DiffRecord;
import gov.nasa.pds.citool.diff.ValueDiff;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.PointerStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to compare two objects of a PDS label.
 *
 * @author mcayanan
 *
 */
public class ObjectStatementComparator {

    /**
     * Determines if the two supplied objects are equal.
     *
     * @param source Class representation of the "source" PDS object.
     * @param target Class representation of the "target" PDS object.
     *
     * @return true if the objects are the same, false otherwise.
     */
    public List<LabelParserException> checkEquality (ObjectStatement source,
            ObjectStatement target) {
        List<LabelParserException> problems =
            new ArrayList<LabelParserException>();
        problems.addAll(checkAttributeEquality(
                (ArrayList<AttributeStatement>) source.getAttributes(),
                target));
        problems.addAll(checkPointerEquality(
                (ArrayList<PointerStatement>) source.getPointers(), target));
        problems.addAll(checkObjectEquality(
                (ArrayList<ObjectStatement>) source.getObjects(), target));

        return problems;
    }

    /**
     * Checks if the list of attributes are equal to the attributes in the
     * "target" object.
     *
     * @param sources A list of attribute statements.
     * @param target Class representation of the "target" PDS object.
     *
     * @return true if the attributes are the same as the attributes in the
     * "target" PDS object.
     */
    public List<LabelParserException> checkAttributeEquality(
            List<AttributeStatement> sources, ObjectStatement target) {
        List<LabelParserException> problems =
            new ArrayList<LabelParserException>();
        List<AttributeStatement> targetAttributes = target.getAttributes();

        for (AttributeStatement source : sources) {
            AttributeStatement targetAttribute = target.getAttribute(
                    source.getIdentifier().getId());
            if (targetAttribute == null) {
                Object[] arguments = {"Element", source.getIdentifier().getId()};
                problems.add(new LabelParserException(source.getSourceURI(),
                        source.getLineNumber(), null,
                        "compare.target.missingStatement",
                        ProblemType.MISSING_ID,
                        arguments));
           /*
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE,
                        "Element " + source.getIdentifier() + " not present in target",
                        target.getFilename()));

                log.log(new ToolsLogRecord(CIToolLevel.SEVERE_NOTIFY, "Source",
                        source.getFilename(), target.getFilename(),
                        source.getLineNumber()));
           */
            }
            else {
                if (!source.equals(targetAttribute)) {
                    List<DiffRecord> diffRecords = ValueDiff.diff(
                            source.getValue(), targetAttribute.getValue(),
                            source.getLineNumber(),
                            targetAttribute.getLineNumber());
                    Object[] arguments = {"Element",
                            targetAttribute.getIdentifier().getId()};
                    DiffException de = new DiffException(source.getSourceURI(),
                            source.getLineNumber(),
                            targetAttribute.getLineNumber(),
                            "compare.nonMatchingValues", diffRecords,
                            arguments);
                    problems.add(de);
/*
                    log.log(new ToolsLogRecord(CIToolLevel.SEVERE,
                            "Element " + targetAttribute.getIdentifier() + " has different value than source",
                            targetAttribute.getFilename(),
                            targetAttribute.getLineNumber()));

                    log.log(new ToolsLogRecord(CIToolLevel.SEVERE_NOTIFY, "Source",
                            source.getFilename(), targetAttribute.getFilename(),
                            source.getLineNumber()));
*/
                }
                targetAttributes.remove(targetAttribute);
            }
        }
        for (AttributeStatement ta : targetAttributes) {
            Object[] arguments = {"Element", ta.getIdentifier().getId()};
            problems.add(new LabelParserException(ta.getSourceURI(),
                    ta.getLineNumber(), null,
                    "compare.source.missingStatement", ProblemType.MISSING_ID,
                    arguments));
/*
            log.log(new ToolsLogRecord(CIToolLevel.SEVERE,
                    "Element " + targetAttribute.getIdentifier() + " not present in source",
                    targetAttribute.getFilename(),
                    targetAttribute.getLineNumber()));
*/
        }
        return problems;
    }

    /**
     * Checks if the list of pointer statements are equal to the pointers
     * found in the "target" object.
     *
     * @param sources A list of pointer statements.
     * @param target Class representation of the "target" PDS object.
     *
     *
     * @return true if the pointers are the same as the pointers in the PDS
     * object.
     */
    public List<LabelParserException> checkPointerEquality(
            List<PointerStatement> sources, ObjectStatement target) {
        List<LabelParserException> problems =
            new ArrayList<LabelParserException>();
        List<PointerStatement> targetPointers = target.getPointers();

        for (PointerStatement source : sources) {
            PointerStatement targetPointer = null;
            for (PointerStatement t : targetPointers) {
                if (t.getIdentifier().equals(source.getIdentifier())) {
                    targetPointer = t;
                    break;
                }
            }
            if (targetPointer == null) {
                //Pointer was found in the source, but not in the target.
                Object[] arguments = {"Pointer",
                        source.getIdentifier().getId()};
                problems.add(new LabelParserException(source.getSourceURI(),
                        source.getLineNumber(), null,
                        "compare.target.missingStatement",
                        ProblemType.MISSING_ID,
                        arguments));
/*
                log.log(new ToolsLogRecord(CIToolLevel.SEVERE,
                        "Pointer " + source.getIdentifier() + " not present in target",
                        target.getFilename()));

                log.log(new ToolsLogRecord(CIToolLevel.SEVERE_NOTIFY, "Source",
                        source.getFilename(), target.getFilename(),
                        source.getLineNumber()));
*/
            }
            else {
                if (!source.equals(targetPointer)) {
                    List<DiffRecord> diffRecords = ValueDiff.diff(
                            source.getValue(), targetPointer.getValue(),
                            source.getLineNumber(),
                            targetPointer.getLineNumber());
                    Object[] arguments = {"Pointer",
                            targetPointer.getIdentifier().getId()};
                    DiffException de = new DiffException(source.getSourceURI(),
                            source.getLineNumber(),
                            targetPointer.getLineNumber(),
                            "compare.nonMatchingValues", diffRecords,
                            arguments);
                    problems.add(de);
/*
                    log.log(new ToolsLogRecord(CIToolLevel.SEVERE,
                            "Pointer " + targetPointer.getIdentifier() + " has different value than source",
                            targetPointer.getFilename(),
                            targetPointer.getLineNumber()));
                    log.log(new ToolsLogRecord(CIToolLevel.SEVERE_NOTIFY, "Source",
                            source.getFilename(), targetPointer.getFilename(),
                            source.getLineNumber()));
*/
                }
                targetPointers.remove(targetPointer);
            }
        }
        //Report any attributes found in the target, but not in the source.
        for (PointerStatement t : targetPointers) {
            Object[] arguments = {"Pointer", t.getIdentifier().getId()};
            problems.add(new LabelParserException(t.getSourceURI(),
                    t.getLineNumber(), null,
                    "compare.source.missingStatement", ProblemType.MISSING_ID,
                    arguments));
/*
            log.log(new ToolsLogRecord(CIToolLevel.SEVERE,
                    "Pointer " + t.getIdentifier() + " not present in source",
                    t.getFilename(), t.getLineNumber()));
*/
        }
        return problems;
    }

    /**
     * Checks if the list of nested object statements are equal to the nested
     * objects found in the "target" PDS object.
     *
     * @param sources A list of nested objects from the "source" PDS
     * object.
     * @param target Class representation of the "target" PDS object.
     *
     *
     * @return true if the objects in the list are equal to the objects
     * in the "target" PDS object.
     */
    public List<LabelParserException> checkObjectEquality(
            List<ObjectStatement> sources, ObjectStatement target) {
        List<LabelParserException> problems =
            new ArrayList<LabelParserException>();
        List<ObjectStatement> targetObjects = target.getObjects();

        for (ObjectStatement source : sources) {
            List<ObjectStatement> possibleMatchingObjects = target.getObjects(
                        source.getIdentifier());
            //If only 1 possible match is returned, then compare the objects, logging messages for differences
            if (possibleMatchingObjects.size() == 1) {
                ObjectStatementComparator comparator =
                    new ObjectStatementComparator();
                problems.addAll(comparator.checkEquality(source,
                        possibleMatchingObjects.get(0)));
                targetObjects.remove(possibleMatchingObjects.get(0));
            }
            else {
                try {
                    CatalogList list = new CatalogList(
                            possibleMatchingObjects);
                    String identifier = new CatalogIndex().getIdentifier(
                            source.getIdentifier().getId());
                    if ("NULL".equals(source.getAttribute(identifier)
                            .getValue().toString())
                            || "N/A".equals(source.getAttribute(identifier)
                                    .getValue().toString())) {
                        throw new NullPointerException("No match");
                    }
                    ObjectStatement matchingObject = list.get(
                            source.getAttribute(identifier).getValue());
                    ObjectStatementComparator comparator =
                        new ObjectStatementComparator();
                    problems.addAll(comparator.checkEquality(source,
                            matchingObject));
                    targetObjects.remove(matchingObject);
                } catch(NullPointerException n) {
                    //Default behavior for comparing two objects. They are
                    //identical if and only if its attributes are equal.
                    //Log differences only if an exact matching object could
                    //not be found.
                    boolean foundMatch = false;
                    for (ObjectStatement targetNestedObject
                            : possibleMatchingObjects) {
                        if ((foundMatch = source.equals(targetNestedObject))) {
                            targetObjects.remove(targetNestedObject);
                            break;
                        }
                    }
                    if (foundMatch == false) {
                        Object[] arguments = {"Object",
                                source.getIdentifier().getId()};
                        problems.add(new LabelParserException(
                                source.getSourceURI(),
                                source.getLineNumber(), null,
                                "compare.target.missingStatement",
                                ProblemType.MISSING_ID,
                                arguments));
                        //log a missing object message
/*
                        log.log(new ToolsLogRecord(CIToolLevel.SEVERE,
                            "Object " + source.getIdentifier() + " not present in target",
                            target.getFilename()));
                        log.log(new ToolsLogRecord(CIToolLevel.SEVERE_NOTIFY,
                            "Source", source.getFilename(),
                            target.getFilename(), source.getLineNumber()));
*/
                    }
                }
            }
        }
        for (ObjectStatement t : targetObjects) {
            Object[] arguments = {"Object", t.getIdentifier().getId()};
            problems.add(new LabelParserException(t.getSourceURI(),
                    t.getLineNumber(), null,
                    "compare.source.missingStatement", ProblemType.MISSING_ID,
                    arguments));
//            log.log(new ToolsLogRecord(CIToolLevel.SEVERE, "Object " + t.getIdentifier() + " not present in source", t.getFilename(), t.getLineNumber()));
        }
        return problems;
    }
}
