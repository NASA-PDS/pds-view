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
  private String[] userSchemaFiles;
  private String modelVersion;
  private Validator cachedValidator;
  private XMLCatalogResolver resolver;
  public final static String USER_VERSION = "User Supplied Version";

  public static final String SCHEMA_CHECK = "gov.nasa.pds.tools.label.SchemaCheck";

  public LabelValidator() {
    this.configurations.put(SCHEMA_CHECK, true);
    modelVersion = VersionInfo.getDefaultModelVersion();
    cachedValidator = null;
    resolver = null;
  }

  public void setSchema(String[] schemaFiles) throws SAXException {
    userSchemaFiles = schemaFiles;
    modelVersion = USER_VERSION;
  }

  public void setCatalogs(String[] catalogFiles) {
    resolver = new XMLCatalogResolver();
    resolver.setPreferPublic(true);
    resolver.setCatalogList(catalogFiles);
    modelVersion = USER_VERSION;
  }

  private List<StreamSource> loadSchemaSources(String[] schemaFiles) {
    List<StreamSource> sources = new ArrayList<StreamSource>();
    for (String schemaFile : schemaFiles) {
      sources.add(new StreamSource(new File(schemaFile)));
    }
    return sources;
  }

  private List<StreamSource> loadSchemaSourcesFromJar() {
    String[] schemaFiles = VersionInfo.getSchemasFromJar(modelVersion).toArray(
        new String[0]);
    List<StreamSource> sources = new ArrayList<StreamSource>();
    for (String schemaFile : schemaFiles) {
      sources.add(new StreamSource(LabelValidator.class
          .getResourceAsStream(VersionInfo.getSchemaRefFromJar(modelVersion,
              schemaFile))));
    }
    return sources;
  }

  /**
   * Currently this method only validates the label against schema constraints
   * 
   * @param container
   *          to store output messages in
   * @param labelFile
   *          to validate
   * @throws SAXException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public synchronized void validate(ExceptionContainer container, File labelFile)
      throws SAXException, IOException, ParserConfigurationException {
    // Are we perfoming schema validation?
    if (performsSchemaValidation()) {
      // Do we have a schema we have loaded previously?
      if (cachedValidator == null) {
        // Support for XSD 1.1
        SchemaFactory schemaFactory = SchemaFactory
            .newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
        // If catalog is used allow resources to be loaded for schemas
        if (resolver != null) {
          schemaFactory.setProperty("http://apache.org/xml/properties/internal/entity-resolver", resolver);
        }
        // Allow errors that happen in the schema to be logged there
        if (container != null) {
          schemaFactory.setErrorHandler(new LabelErrorHandler(container));
        }
        // Time to load schema that will be used for validation
        Schema validatingSchema = null;
        if (userSchemaFiles != null) {
          // User has specified schema files to use
          validatingSchema = schemaFactory.newSchema(loadSchemaSources(
              userSchemaFiles).toArray(new StreamSource[0]));
        } else if (resolver == null) {
          // There is no catalog file
          if (VersionInfo.isInternalMode()) {
            // No external schema directory was specified so load from jar
            validatingSchema = schemaFactory
                .newSchema(loadSchemaSourcesFromJar().toArray(
                    new StreamSource[0]));
          } else {
            // Load from user specified external directory
            validatingSchema = schemaFactory.newSchema(loadSchemaSources(
                VersionInfo.getSchemasFromDirectory().toArray(new String[0]))
                .toArray(new StreamSource[0]));
          }
        } else {
          // We're only going to use the catalog to validate against.
          validatingSchema = schemaFactory.newSchema();
        }
        // Time to create a validator from our schema
        Validator validator = validatingSchema.newValidator();
        // Allow access to the catalog from the parser
        if (resolver != null) {
          validator.setProperty("http://apache.org/xml/properties/internal/entity-resolver", resolver);
        }
        // Capture messages in a container
        if (container != null) {
          validator.setErrorHandler(new LabelErrorHandler(container));
        }
        // Cache once we have loaded so we don't have to do again
        cachedValidator = validator;
      }
      // Finally validate the file
      cachedValidator.validate(new StreamSource(labelFile));
    }
  }

  public void validate(File labelFile) throws SAXException, IOException,
      ParserConfigurationException {
    validate(null, labelFile);
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

  public static void main(String[] args) throws Exception {
    LabelValidator lv = new LabelValidator();
    String[] catalogs = new String[] { args[0] };
    lv.setCatalogs(catalogs);
    lv.validate(new File(args[1]));
  }
}
