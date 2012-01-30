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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.validate.LabelValidator;

/**
 * Class that performs validation for reference catalog files.
 *
 * @author mcayanan
 *
 */
public class ReferenceValidator implements LabelValidator {
    private final String REFID = "REFERENCE_KEY_ID";
    private final String REFDESC = "REFERENCE_DESC";

    /**
     * Determines whether or not a reference catalog file is valid.
     *
     * @param label A label representation of a reference catalog file.
     *
     * @return A flag indicating whether or not a reference catalog if valid.
     */
    public boolean validate(Label label) {
        boolean passFlag = true;
        boolean hasYearInID = false;
        List<ObjectStatement> refObjects =
            (List<ObjectStatement>) label.getObjects("REFERENCE");
        for (ObjectStatement ref : refObjects) {
            String id = null;
            String description = null;
            AttributeStatement refIdStmt = null;
            AttributeStatement refDescStmt = null;
            if (ref.getAttribute(REFID) != null) {
                refIdStmt = ref.getAttribute(REFID);
                id = refIdStmt.getValue().toString();
            }
            if (ref.getAttribute(REFDESC) != null) {
                refDescStmt = ref.getAttribute(REFDESC);
                description = refDescStmt.getValue().toString();
            }

            //A REFERENCE_KEY_ID can have an author and year field. If it
            //appears that it has both components, do reference checking.
            if (id.matches("[a-zA-Z&]*[0-9]{4}[A-Z]?")) {
                int idYear = -1;
                try {
                    //Parse the year from the REFERENCE_KEY_ID
                    if (Character.isLetter(id.charAt(id.length()-1))) {
                        idYear = Integer.parseInt(
                                id.substring(id.length() - 5,
                                id.length() - 1));
                    } else {
                        idYear = Integer.parseInt(
                                id.substring(id.length() - 4));
                    }
                    hasYearInID = true;
                } catch(Exception e) {
                    hasYearInID = false;
                    passFlag = false;
                }
                //If a year field was found in the REFERENCE_KEY_ID, check to
                //see if it matches the year found at the end of the citation
                if (hasYearInID) {
                    int descriptionYear = -1;
                    String value = "";
                    try {
                        String filteredDescription =
                            description.replaceAll("\\s+", " ").trim();
                        value = filteredDescription.substring(
                                filteredDescription.lastIndexOf(' ') + 1)
                                .trim();
                        String filteredValue = value.replace('.', ' ').trim();
                        descriptionYear = Integer.parseInt(filteredValue);
                    } catch(NumberFormatException n) {
                        label.addProblem(refDescStmt.getSourceURI(),
                                new Integer(refDescStmt.getLineNumber()), null,
                                "referenceDescription.missingYear",
                                ProblemType.MISSING_VALUE, value);
                    }
                    if ( (descriptionYear != -1)
                            && (idYear != descriptionYear) ) {
                        //Product-tools library adds commas in year values.
                        //So the years were converted into strings to resolve
                        //the issue.
                        Object[] arguments = {new Integer(idYear).toString(),
                                new Integer(descriptionYear).toString()};
                        label.addProblem(refIdStmt.getSourceURI(),
                                new Integer(refIdStmt.getLineNumber()), null,
                                "referenceId.misMatchedYear",
                                ProblemType.UNKNOWN_VALUE, arguments);
                    }
                    int end = -1;
                    String firstAuthor = "";
                    if ( ((end = id.indexOf("ETAL")) != -1)
                            || ((end = id.indexOf("&")) != -1) )  {
                        firstAuthor = id.substring(0, end);
                    } else if (hasYearInID) {
                        firstAuthor = id.substring(0, id.indexOf(
                                new Integer(idYear).toString()));
                    }
                    if ("".equals(firstAuthor)) {
                        label.addProblem(refIdStmt.getSourceURI(),
                                new Integer(refIdStmt.getLineNumber()), null,
                                "referenceId.missingAuthor",
                                ProblemType.INVALID_VALUE, id);
                        passFlag = false;
                    }
                    else {
                        //Manipulate the author id so that authors with spaces
                        //or apostrophes in the description will be
                        //a match (A'Hearn, DE Kwok, etc.).
                        StringBuffer manipulatedAuthor = new StringBuffer();
                        for (int i = 0; i < firstAuthor.length(); i++) {
                            manipulatedAuthor.append(firstAuthor.charAt(i));
                            if (i != (firstAuthor.length() - 1)) {
                                manipulatedAuthor.append("[\\s'-]?");
                            }
                        }
                        Pattern pattern = Pattern.compile(manipulatedAuthor
                                + ".*", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(description);
                        if (!matcher.find()) {
                            label.addProblem(refDescStmt.getSourceURI(),
                                    new Integer(refDescStmt.getLineNumber()),
                                    null,
                                    "referenceDescription.missingAuthor",
                                    ProblemType.MISSING_VALUE, firstAuthor);
                        }
                    }
                }
            } else {
          /*
                log.log(new ToolsLogRecord(CIToolLevel.INFO,
                "Skipping " + id + ". It does not have author and year fields.",
                        refDescStmt.getFilename(), refDescStmt.getLineNumber()));
          */
            }
        }
        return passFlag;
    }
}
