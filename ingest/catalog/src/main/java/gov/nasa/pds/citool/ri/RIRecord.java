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

import java.util.ArrayList;
import java.util.List;

/**
 * An object representation of a referential integrity record.
 *
 * @author mcayanan
 *
 */
public class RIRecord {
    private List<LabelParserException> problems;
    private RIType type;
    private List<String> parentFiles;

    public RIRecord(RIType type, List<String> parentFiles) {
        this.parentFiles = parentFiles;
        this.type = type;
        this.problems = new ArrayList<LabelParserException>();
    }

    public List<String> getParentFiles() {
        return parentFiles;
    }

    public RIType getType() {
       // String filteredString = type.replaceAll("_", " ");
       // return filteredString;
        return type;
    }

    public void addProblem(LabelParserException problem) {
        this.problems.add(problem);
    }

    public void addProblems(List<LabelParserException> problems) {
        this.problems.addAll(problems);
    }

    public List<LabelParserException> getProblems() {
        return problems;
    }

}
