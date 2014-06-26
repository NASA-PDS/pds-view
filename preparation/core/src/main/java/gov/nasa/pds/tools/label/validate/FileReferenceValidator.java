//  Copyright 2009-2014, by the California Institute of Technology.
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.tinytree.TinyNodeImpl;

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
public class FileReferenceValidator implements DocumentValidator {

  /**
   * XPath to the file references within a PDS4 data product label.
   */
  private final String FILE_OBJECTS_XPATH =
    "//*[starts-with(name(), 'File_Area')]/File | "
    + "//Document_Format_Set/Document_File";

  @Override
  public boolean validate(ExceptionContainer container, DocumentInfo xml) {
    boolean passFlag = true;
    List<LabelException> problems = new ArrayList<LabelException>();
    try {
      XMLExtractor extractor = new XMLExtractor(xml);
      try {
        List<TinyNodeImpl> fileObjects = extractor.getNodesFromDoc(
            FILE_OBJECTS_XPATH);
        for (TinyNodeImpl fileObject : fileObjects) {
          URL labelUrl = new URL(xml.getSystemId());
          URL parent = labelUrl.toURI().getPath().endsWith("/") ?
              labelUrl.toURI().resolve("..").toURL() :
                labelUrl.toURI().resolve(".").toURL();
          String name = "";
          String checksum = "";
          String directory = "";
          List<TinyNodeImpl> children = new ArrayList<TinyNodeImpl>();
          try {
            children = extractor.getNodesFromItem("*", fileObject);
          } catch (XPathExpressionException xpe) {
            problems.add(new LabelException(ExceptionType.FATAL,
                "Problem occurred while trying to get all the children "
                + "of the file object node: " + xpe.getMessage(),
                xml.getSystemId(),
                xml.getSystemId(),
                new Integer(fileObject.getLineNumber()),
                null));
            passFlag = false;
            continue;
          }
          for (TinyNodeImpl child : children) {
            if ("file_name".equals(child.getLocalPart())) {
              name = child.getStringValue();
            } else if ("md5_checksum".equals(child.getLocalPart())) {
              checksum = child.getStringValue();
            } else if ("directory_path_name".equals(child.getLocalPart())) {
              directory = child.getStringValue();
            }
          }
          if (name.isEmpty()) {
            problems.add(new LabelException(ExceptionType.ERROR,
              "Missing 'file_name' element tag",
              xml.getSystemId(),
              xml.getSystemId(),
              new Integer(fileObject.getLineNumber()),
              null)
            );
            passFlag = false;
          } else {
            URL urlRef = null;
            if (!directory.isEmpty()) {
              urlRef = new URL(parent, directory + "/" + name);
            } else {
              urlRef = new URL(parent, name);
            }
            try {
              urlRef.openStream().close();
              // Check that the casing of the file reference matches the
              // casing of the file located on the file system.
              try {
                File fileRef = FileUtils.toFile(urlRef);
                if (fileRef != null &&
                    !fileRef.getCanonicalPath().endsWith(fileRef.getName())) {
                  container.addException(new LabelException(
                      ExceptionType.WARNING,
                      "File reference'" + fileRef.toString()
                      + "' exists but the case doesn't match.",
                      xml.getSystemId(),
                      xml.getSystemId(),
                      new Integer(fileObject.getLineNumber()),
                      null));
                }
              } catch (IOException io) {
                problems.add(new LabelException(ExceptionType.FATAL,
                    "Error occurred while checking for the existence of the "
                    + "uri reference '" + urlRef.toString() + "': "
                    + io.getMessage(),
                    xml.getSystemId(),
                    xml.getSystemId(),
                    new Integer(fileObject.getLineNumber()),
                    null));
                passFlag = false;
              }
              if (!checksum.isEmpty()) {
                try {
                  String generatedChecksum = MD5Checksum.getMD5Checksum(
                    urlRef);
                  if (!checksum.equals(generatedChecksum)) {
                    problems.add(new LabelException(ExceptionType.ERROR,
                        "Generated checksum '" + generatedChecksum
                        + "' does not match supplied checksum '" + checksum
                        + "' in the product label for the following uri reference: "
                        + urlRef.toString(),
                        xml.getSystemId(),
                        xml.getSystemId(),
                        new Integer(fileObject.getLineNumber()),
                        null));
                    passFlag = false;
                  }
                } catch (Exception e) {
                  problems.add(new LabelException(ExceptionType.ERROR,
                      "Error occurred while calculating checksum for "
                      + FilenameUtils.getName(urlRef.toString()) + ": "
                      + e.getMessage(),
                      xml.getSystemId(),
                      xml.getSystemId(),
                      new Integer(fileObject.getLineNumber()),
                      null));
                  passFlag = false;
                }
              }


            } catch (IOException io) {
              problems.add(new LabelException(ExceptionType.ERROR,
                  "URI reference does not exist: " + urlRef.toString(),
                  xml.getSystemId(),
                  xml.getSystemId(),
                  new Integer(fileObject.getLineNumber()),
                  null));
              passFlag = false;
            }
          }
        }
      } catch (XPathExpressionException xpe) {
        problems.add(new LabelException(ExceptionType.FATAL,
            "Error occurred while evaluating the following xpath expression '"
            + FILE_OBJECTS_XPATH + "': " + xpe.getMessage(),
            xml.getSystemId(),
            xml.getSystemId(),
            null,
            null));
        passFlag = false;
      }
    } catch (Exception e) {
      problems.add(new LabelException(ExceptionType.FATAL,
          "Error occurred while reading the uri: " + e.getMessage(),
          xml.getSystemId(),
          xml.getSystemId(),
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
