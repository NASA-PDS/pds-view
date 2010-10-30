//	Copyright 2009-2010, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//

package gov.nasa.pds.tools.label;

import gov.nasa.pds.tools.util.VersionInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.xpath.XPathEvaluator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class is responsible for providing utility functions for validating PDS
 * XML Labels.
 * 
 * @author pramirez
 * 
 */
public class LabelValidator {
  private Map<String, Boolean> configurations = new HashMap<String, Boolean>();
  private Map<String, Map<String, Schema>> schemas = new HashMap<String, Map<String, Schema>>();
  private String modelVersion;
  private final static Boolean internalMode;

  public static final String SCHEMA_CHECK = "gov.nasa.pds.tools.label.SchemaCheck";

  private static final String PDS_DEFAULT_NAMESPACE = "http://pds.nasa.gov/schema/pds4/pds";

  static {
    internalMode = VersionInfo.isInternalMode();
  }

  public LabelValidator() {
    this.configurations.put(SCHEMA_CHECK, true);
    modelVersion = VersionInfo.getModelVersion();
  }

  public synchronized Schema getSchema(String modelVersion, String productClass)
      throws SAXException {
    Map<String, Schema> modelMap = schemas.get(modelVersion);
    if (modelMap == null) {
      modelMap = new HashMap<String, Schema>();
      schemas.put(modelVersion, modelMap);
    }
    Schema schema = modelMap.get(productClass);
    if (schema == null) {
      SchemaFactory factory = SchemaFactory
          .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      if (internalMode) {
        factory.setResourceResolver(new ModelResourceResolver(modelVersion));
      }
      schema = factory.newSchema(this.getSchemaSources(modelVersion,
          productClass).toArray(new StreamSource[0]));
      modelMap.put(productClass, schema);
    }
    return schema;
  }

  private List<StreamSource> getSchemaSources(String modelVersion,
      String productClass) {
    List<StreamSource> sources = new ArrayList<StreamSource>();
    // Load product schema
    StreamSource productSource;
    if (internalMode) {
      productSource = new StreamSource(LabelValidator.class
          .getResourceAsStream(VersionInfo.getSchemaReference(modelVersion,
              productClass)), VersionInfo.getSchemaName(modelVersion,
          productClass));
    } else {
      productSource = new StreamSource(new File(VersionInfo.getSchemaReference(
          modelVersion, productClass)));
    }
    sources.add(productSource);
    return sources;
  }

  private String getProductClass(File labelFile)
      throws XPathExpressionException, ParserConfigurationException,
      SAXException, IOException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder parser = dbf.newDocumentBuilder();
    Document labelDocument = parser.parse(labelFile);
    XPathEvaluator xpath = new XPathEvaluator();
    xpath.getStaticContext().setDefaultElementNamespace(PDS_DEFAULT_NAMESPACE);
    return xpath.evaluate("//product_class/text()", labelDocument);
  }

  public void validate(ExceptionContainer container, File labelFile)
      throws SAXException, IOException, ParserConfigurationException,
      XPathExpressionException {
    String productClass = this.getProductClass(labelFile);
    if (productClass == null || "".equals(productClass.trim())) {
      container.addException(new LabelException(ExceptionType.ERROR,
          "Could not find product class for label.", null, labelFile
              .getAbsolutePath(), -1, -1));
      return;
    }
    Schema schema = this.getSchema(modelVersion, productClass);
    this.validate(container, labelFile, schema);
  }

  public void validate(ExceptionContainer container, File labelFile,
      Schema schema) throws SAXException, IOException,
      ParserConfigurationException {
    if (performsSchemaValidation()) {
      Validator validator = schema.newValidator();
      validator.setErrorHandler(new LabelErrorHandler(container));
      validator.validate(new StreamSource(labelFile));
    }
  }

  public String getModelVersion() {
    return modelVersion;
  }

  public void setModelVersion(String modelVersion) {
    this.modelVersion = modelVersion;
  }

  public Boolean performsSchemaValidation() {
    return getConfiguration(SCHEMA_CHECK);
  }

  public void setSchemaCheck(Boolean value) {
    this.setConfiguration(SCHEMA_CHECK, value);
  }

  public Boolean getConfiguration(String key) {
    return this.configurations.containsKey(key) ? this.configurations.get(key)
        : false;
  }

  public void setConfiguration(String key, Boolean value) {
    this.configurations.put(key, value);
  }

  public Boolean isInternalMode() {
    return internalMode;
  }

  public static void main(String[] args) throws Exception {
    LabelValidator validator = new LabelValidator();
    ExceptionContainer container = new ExceptionContainer();
    validator.validate(container, new File(args[0]));
    System.out.println("Model Version: " + validator.getModelVersion());
    System.out.println("Internal Mode: " + validator.isInternalMode());
    for (LabelException exception : container.getExceptions()) {
      System.out.println(exception.getExceptionType() + " "
          + exception.getLineNumber() + " " + exception.getSource() + ": "
          + exception.getMessage());
    }
    System.out.println("Exiting Main!");
  }
}
