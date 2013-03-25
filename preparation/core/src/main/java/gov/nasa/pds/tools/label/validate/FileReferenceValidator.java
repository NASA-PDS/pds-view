//  Copyright 2009-2013, by the California Institute of Technology.
//  ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//  Any commercial use must be negotiated with the Office of Technology
//  Transfer at the California Institute of Technology.
//
//  This software is subject to U. S. export control laws and regulations
//  (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
//  is subject to U.S. export control laws and regulations, the recipient has
//  the responsibility to obtain export licenses or other export authority as
//  may be required before exporting such information to foreign countries or
//  providing access to foreign nationals.
//
//  $Id$
//
package gov.nasa.pds.tools.label.validate;

import gov.nasa.pds.tools.label.ExceptionContainer;
import gov.nasa.pds.tools.label.ExceptionType;
import gov.nasa.pds.tools.label.LabelException;
import gov.nasa.pds.tools.util.MD5Checksum;
import gov.nasa.pds.tools.util.XMLExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import net.sf.saxon.tinytree.TinyElementImpl;

/**
 * Validator class that looks for file references in the PDS4 product label
 * and performs the following checks:
 *
 * - Verify that the generated checksum matches the supplied checksum,
 *   if provided
 *
 * - Verify that the casing of the file reference matches the file name
 *  casing on the file system
 *
 * @author mcayanan
 *
 */
public class FileReferenceValidator implements ExternalValidator {

  /**
   * XPath to the file references within a PDS4 data product label.
   */
  private final String FILE_OBJECTS_XPATH =
    "//*[starts-with(name(), 'File_Area')]/File | "
    + "//Document_Format_Set/Document_File";

  @Override
  public boolean validate(ExceptionContainer container, File labelFile) {
    boolean passFlag = true;
    List<LabelException> problems = new ArrayList<LabelException>();
    try {
      XMLExtractor extractor = new XMLExtractor(labelFile);
      try {
        List<TinyElementImpl> fileObjects = extractor.getNodesFromDoc(
            FILE_OBJECTS_XPATH);
        for (TinyElementImpl fileObject : fileObjects) {
          String fileLocation = labelFile.getParent();
          String name = "";
          String checksum = "";
          List<TinyElementImpl> children = new ArrayList<TinyElementImpl>();
          try {
            children = extractor.getNodesFromItem("*", fileObject);
          } catch (XPathExpressionException xpe) {
            problems.add(new LabelException(ExceptionType.FATAL,
                "Problem occurred while trying to get all the children "
                + "of the file object node: " + xpe.getMessage(),
                labelFile.toString(),
                labelFile.toString(),
                new Integer(fileObject.getLineNumber()),
                null));
            passFlag = false;
            continue;
          }
          for (TinyElementImpl child : children) {
            if ("file_name".equals(child.getLocalPart())) {
              name = child.getStringValue();
            } else if ("md5_checksum".equals(child.getLocalPart())) {
              checksum = child.getStringValue();
            } else if ("directory_path_name".equals(child.getLocalPart())) {
              fileLocation = new File(fileLocation, child.getStringValue())
              .toString();
            }
          }
          if (name.isEmpty()) {
            problems.add(new LabelException(ExceptionType.ERROR,
              "Missing 'file_name' element tag",
              labelFile.toString(),
              labelFile.toString(),
              new Integer(fileObject.getLineNumber()),
              null)
            );
            passFlag = false;
          } else {
            File fileRef = new File(fileLocation, name);
            if (!fileRef.exists()) {
              problems.add(new LabelException(ExceptionType.ERROR,
                "File reference does not exist: " + fileRef,
                labelFile.toString(),
                labelFile.toString(),
                new Integer(fileObject.getLineNumber()),
                null));
              passFlag = false;
            } else {
              // Check that the casing of the file reference matches the
              // casing of the file located on the file system.
              try {
                if (!fileRef.getCanonicalPath().endsWith(fileRef.getName())) {
                  container.addException(new LabelException(
                      ExceptionType.WARNING,
                      "File reference'" + labelFile
                      + "' exists but the case doesn't match.",
                      labelFile.toString(),
                      labelFile.toString(),
                      new Integer(fileObject.getLineNumber()),
                      null));
                }
              } catch (IOException io) {
                problems.add(new LabelException(ExceptionType.FATAL,
                    "Error occurred while checking for the existence of the "
                    + "file reference '" + fileRef.toString() + "': "
                    + io.getMessage(),
                    labelFile.toString(),
                    labelFile.toString(),
                    new Integer(fileObject.getLineNumber()),
                    null));
                passFlag = false;
              }
              if (!checksum.isEmpty()) {
                try {
                  String generatedChecksum = MD5Checksum.getMD5Checksum(
                    fileRef.toString());
                  if (!checksum.equals(generatedChecksum)) {
                    problems.add(new LabelException(ExceptionType.ERROR,
                        "Generated checksum '" + generatedChecksum
                        + "' does not match supplied checksum '" + checksum
                        + "' in the product label",
                        labelFile.toString(),
                        labelFile.toString(),
                        new Integer(fileObject.getLineNumber()),
                        null));
                    passFlag = false;
                  }
                } catch (Exception e) {
                  problems.add(new LabelException(ExceptionType.ERROR,
                      "Error occurred while calculating checksum for "
                      + fileRef.getName() + ": " + e.getMessage(),
                      labelFile.toString(),
                      labelFile.toString(),
                      new Integer(fileObject.getLineNumber()),
                      null));
                  passFlag = false;
                }
              }
            }
          }
        }
      } catch (XPathExpressionException xpe) {
        problems.add(new LabelException(ExceptionType.FATAL,
            "Error occurred while evaluating the following xpath expression '"
            + FILE_OBJECTS_XPATH + "': " + xpe.getMessage(),
            labelFile.toString(),
            labelFile.toString(),
            null,
            null));
        passFlag = false;
      }
    } catch (Exception e) {
      problems.add(new LabelException(ExceptionType.FATAL,
          "Error occurred while parsing the file: " + e.getMessage(),
          labelFile.toString(),
          labelFile.toString(),
          null,
          null)
      );
      passFlag = false;
    }
    // Add the problems to the exception container.
    for (LabelException problem : problems) {
      container.addException(problem);
    }
    return passFlag;
  }
}
