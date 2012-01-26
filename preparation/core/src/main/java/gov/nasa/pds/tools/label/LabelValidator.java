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

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.apache.xerces.util.XMLCatalogResolver;
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
  private Schema userSchema;
  private String modelVersion;
  private Boolean internalMode;
  private XMLCatalogResolver resolver;
  public final static String USER_VERSION = "User Supplied Version";

  public static final String SCHEMA_CHECK = "gov.nasa.pds.tools.label.SchemaCheck";

  public LabelValidator() {
    this.configurations.put(SCHEMA_CHECK, true);
    modelVersion = VersionInfo.getDefaultModelVersion();
    internalMode = true;
    resolver = null;
  }

  public void setSchema(String[] schemaFiles) throws SAXException {
    setSchema(loadSchema(schemaFiles));
  }

  public void setSchema(Schema schema) {
    internalMode = false;
    this.userSchema = schema;
  }

  public void setCatalogs(String[] catalogFiles) {
    internalMode = false;
    resolver = new XMLCatalogResolver();
    resolver.setPreferPublic(true);
    resolver.setCatalogList(catalogFiles);
  }
  
  private Schema loadSchema(String[] schemaFiles) throws SAXException {
    List<StreamSource> sources = new ArrayList<StreamSource>();
    for (String schemaFile : schemaFiles) {
      sources.add(new StreamSource(new File(schemaFile)));
    }
    SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
    return schemaFactory.newSchema(sources.toArray(new StreamSource[0]));
  }

  /**
   * Currently this method only validates the label against schema constraints
   * @param container to store output messages in
   * @param labelFile to validate
   * @throws SAXException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public void validate(ExceptionContainer container, File labelFile)
      throws SAXException, IOException, ParserConfigurationException {
    Schema validatingSchema = null;
    if (internalMode) {
      validatingSchema = loadSchema(VersionInfo.getSchemas().toArray(new String[0]));
    } else {
      validatingSchema = userSchema;
    }
    
    if (performsSchemaValidation()) {
      Validator validator = validatingSchema.newValidator();
      if (container != null) {
        validator.setErrorHandler(new LabelErrorHandler(container));
      }
      if (resolver != null) {
        validator.setResourceResolver(resolver);
      }
      validator.validate(new StreamSource(labelFile));
    }
  }

  public void validate(File labelFile) throws SAXException, IOException,
      ParserConfigurationException {  
    validate(null, labelFile);
  }
  
  public String getModelVersion() {
    if (internalMode) {
      return modelVersion;
    }
    return USER_VERSION;
  }

  public void setModelVersion(String modelVersion) {
    this.internalMode = true;
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

}
