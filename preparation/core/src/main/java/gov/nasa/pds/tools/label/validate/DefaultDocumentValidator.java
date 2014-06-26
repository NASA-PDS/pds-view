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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.pds.tools.label.ExceptionContainer;
import gov.nasa.pds.tools.label.ExceptionType;
import gov.nasa.pds.tools.label.LabelException;
import gov.nasa.pds.tools.util.VersionInfo;
import gov.nasa.pds.tools.util.XMLExtractor;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.tinytree.TinyNodeImpl;

/**
 * The intent of this class is to perform some default semantic validation
 * on the parsed PDS4 label.
 * 
 * @author mcayanan
 *
 */
public class DefaultDocumentValidator implements DocumentValidator {

  private final String XML_MODEL_XPATH = "/processing-instruction('xml-model')";
  
  @Override
  public boolean validate(ExceptionContainer container, DocumentInfo xml) {
    boolean passFlag = true;
    // Check the xml-model processing instructions specification
    List<TinyNodeImpl> xmlModels = new ArrayList<TinyNodeImpl>();
    try {
      XMLExtractor extractor = new XMLExtractor(xml);
      xmlModels = extractor.getNodesFromDoc(XML_MODEL_XPATH);
    } catch (Exception e) {
      //Ignore
    }
    if (xmlModels.isEmpty()) {
      container.addException(new LabelException(ExceptionType.WARNING, 
          "No schematron specification found in the label.", xml.getSystemId()));
    } else {
      Pattern pattern = Pattern.compile("href=\\\"([^=]*)\\\"( schematypens=\\\"([^=]*)\\\")?");
      for (TinyNodeImpl xmlModel : xmlModels) {
        String filteredValue = xmlModel.getStringValue().replaceAll("\\s+", " ");
        Matcher matcher = pattern.matcher(filteredValue);
        if (matcher.matches()) {
          if (matcher.group(3) != null) {
            if (!VersionInfo.getSchematronNamespace().equals(
                matcher.group(3).trim())) {
              container.addException(
                  new LabelException(ExceptionType.WARNING, 
                    "Value of the 'schematypens' attribute, '"
                      + matcher.group(3).trim() 
                      + "', in the schematron specification is not equal to the recommended value of '"
                      + VersionInfo.getSchematronNamespace() + "'.", 
                    xml.getSystemId(), 
                    xml.getSystemId(), 
                    new Integer(xmlModel.getLineNumber()), 
                    null));
              }
          } else {
            container.addException(new LabelException(ExceptionType.WARNING, 
                "Missing 'schematypens' attribute from the schematron specification.", 
                xml.getSystemId(), 
                xml.getSystemId(), 
                new Integer(xmlModel.getLineNumber()), 
                null));
          }
        }
      }
    }
    return passFlag;
  }
}
