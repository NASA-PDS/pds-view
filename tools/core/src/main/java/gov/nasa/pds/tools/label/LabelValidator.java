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
import javax.xml.transform.Source;
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
  private static Schema DEFAULT_SCHEMA;

  public static final String SCHEMA_CHECK = "gov.nasa.pds.tools.label.SchemaCheck";

  public LabelValidator() {
    this.configurations.put(SCHEMA_CHECK, true);
  }

  public void validate(ExceptionContainer container, File labelFile)
      throws SAXException, IOException {
    if (performsSchemaValidation()) {
      initDefaultSchema();
    }
    this.validate(container, labelFile, DEFAULT_SCHEMA);
  }

  public void validate(ExceptionContainer container, File labelFile,
      List<File> schemaFiles) throws SAXException, IOException {
    List<StreamSource> schemas = new ArrayList<StreamSource>();
    for (File schema : schemaFiles) {
      schemas.add(new StreamSource(schema));
    }
    SchemaFactory factory = SchemaFactory
        .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = factory.newSchema((Source[]) schemas.toArray());
    this.validate(container, labelFile, schema);
  }

  public void validate(ExceptionContainer container, File labelFile,
      Schema schema) throws SAXException, IOException {
    Validator validator = schema.newValidator();
    Source source = new StreamSource(labelFile);
    validator.setErrorHandler(new LabelErrorHandler(container));
    validator.validate(source);
  }

  protected static synchronized void initDefaultSchema() throws SAXException {
    if (DEFAULT_SCHEMA == null) {
      List<StreamSource> schemas = new ArrayList<StreamSource>();
      for (String schema : VersionInfo.getSchemas()) {
        schemas.add(new StreamSource(LabelValidator.class
            .getResourceAsStream("/schemas/" + schema), schema));
      }
      SchemaFactory factory = SchemaFactory
          .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      DEFAULT_SCHEMA = factory.newSchema((Source[]) schemas.toArray());
    }
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
}
