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

import gov.nasa.pds.tools.label.validate.DocumentValidator;
import gov.nasa.pds.tools.label.validate.ExternalValidator;
import gov.nasa.pds.tools.label.validate.FileReferenceValidator;
import gov.nasa.pds.tools.util.VersionInfo;
import gov.nasa.pds.tools.util.XMLErrorListener;
import gov.nasa.pds.tools.util.XslURIResolver;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.ParseOptions;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.xpath.XPathEvaluator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
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
  private String[] userSchematronFiles;
  private String modelVersion;
  private Validator cachedValidator;
  private List<Transformer> cachedSchematron;
  private XMLCatalogResolver resolver;
  public static final String SCHEMA_CHECK = "gov.nasa.pds.tools.label.SchemaCheck";
  public static final String SCHEMATRON_CHECK = "gov.nasa.pds.tools.label.SchematronCheck";
  public static final String FILE_REF_CHECK = "gov.nasa.pds.tools.label.fileReferenceCheck";

  public List<ExternalValidator> externalValidators;
  public List<DocumentValidator> documentValidators;

  public LabelValidator() {
    this.configurations.put(SCHEMA_CHECK, true);
    this.configurations.put(SCHEMATRON_CHECK, true);
    this.configurations.put(FILE_REF_CHECK, true);
    modelVersion = VersionInfo.getDefaultModelVersion();
    cachedValidator = null;
    cachedSchematron = new ArrayList<Transformer>();
    userSchemaFiles = null;
    userSchematronFiles = null;
    resolver = null;
    externalValidators = new ArrayList<ExternalValidator>();
    documentValidators = new ArrayList<DocumentValidator>();
  }

  public void setSchema(String[] schemaFiles) throws SAXException {
    userSchemaFiles = schemaFiles;
    cachedValidator = null;
  }

  public void setSchematronFiles(String[] schematronFiles) {
    userSchematronFiles = schematronFiles;
    cachedSchematron = new ArrayList<Transformer>();
  }

  public void setCatalogs(String[] catalogFiles) {
    resolver = new XMLCatalogResolver();
    resolver.setPreferPublic(true);
    resolver.setCatalogList(catalogFiles);
  }

  private List<StreamSource> loadSchemaSources(String[] schemaFiles) {
    List<StreamSource> sources = new ArrayList<StreamSource>();
    for (String schemaFile : schemaFiles) {
      sources.add(new StreamSource(schemaFile));
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

  public synchronized void validate(ExceptionContainer container, File labelFile)
  throws SAXException, IOException, ParserConfigurationException,
  TransformerException {
    validate(container, labelFile.toURI().toURL());
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
   * @throws TransformerException
   */
  public synchronized void validate(ExceptionContainer container, URL url)
      throws SAXException, IOException, ParserConfigurationException,
      TransformerException {
    // Are we perfoming schema validation?
    if (performsSchemaValidation()) {
      // Do we have a schema we have loaded previously?
      if (cachedValidator == null) {
        // Support for XSD 1.1
        SchemaFactory schemaFactory = SchemaFactory
            .newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
        // If catalog is used allow resources to be loaded for schemas
        if (resolver != null) {
          schemaFactory.setProperty(
              "http://apache.org/xml/properties/internal/entity-resolver",
              resolver);
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
          validator.setProperty(
              "http://apache.org/xml/properties/internal/entity-resolver",
              resolver);
        }
        // Capture messages in a container
        if (container != null) {
          validator.setErrorHandler(new LabelErrorHandler(container));
        }
        // Cache once we have loaded so we don't have to do again
        cachedValidator = validator;
      }
      // Finally validate the file
      cachedValidator.validate(new StreamSource(url.openStream()));
    }
    // Validate with any schematron files we have
    if (performsSchematronValidation()) {
      if (cachedSchematron.isEmpty()) {
        // Use saxon for schematron (i.e. the XSLT generation).
        System.setProperty("javax.xml.transform.TransformerFactory",
            "net.sf.saxon.TransformerFactoryImpl");
        TransformerFactory isoFactory = TransformerFactory.newInstance();
        // Set the resolver that will look in the jar for imports
        isoFactory.setURIResolver(new XslURIResolver());
        // Load the isoSchematron stylesheet that will be used to transform each
        // schematron file
        Source isoSchematron = new StreamSource(LabelValidator.class
            .getResourceAsStream("/schematron/iso_svrl_for_xslt2.xsl"));
        Transformer isoTransformer = isoFactory.newTransformer(isoSchematron);
        // Setup a different factory for user schematron files as it will not
        // use
        // the same URIResolver
        TransformerFactory factory = TransformerFactory.newInstance();
        // If user does not provide schematron then use ones in jar if available
        if (userSchematronFiles == null) {
          for (String schematronFile : VersionInfo
              .getSchematronsFromJar(modelVersion)) {
            // Will hold stylesheet that encompasses the tests as specified in
            // the schematron file
            StringWriter schematronStyleSheet = new StringWriter();
            // Create the stylesheet by transforming using the iso schematron
            isoTransformer.transform(new StreamSource(LabelValidator.class
                .getResourceAsStream(VersionInfo.getSchematronRefFromJar(
                    modelVersion, schematronFile))), new StreamResult(
                schematronStyleSheet));
            // Save for later
            cachedSchematron.add(factory.newTransformer(new StreamSource(
                new StringReader(schematronStyleSheet.toString()))));
          }
        } else {
          // For each schematron file we need to setup a transformer that will
          // be applied to the label
          for (String schematronFile : userSchematronFiles) {
            // Will hold stylesheet that encompasses the tests as specified in
            // the schematron file
            StringWriter schematronStyleSheet = new StringWriter();
            // Create the stylesheet by transforming using the iso schematron
            isoTransformer.transform(new StreamSource(schematronFile),
                new StreamResult(schematronStyleSheet));
            // Save for later
            cachedSchematron.add(factory.newTransformer(new StreamSource(
                new StringReader(schematronStyleSheet.toString()))));
          }
        }
      }
      // Boiler plate to handle parsing report outputs from schematron
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      DocumentBuilder parser = dbf.newDocumentBuilder();
      for (Transformer schematron : cachedSchematron) {
        StringWriter report = new StringWriter();
        // Apply the rules specified in the schematron file
        schematron.transform(new StreamSource(url.openStream()),
            new StreamResult(report));
        // Output is svrl:schematron-output document
        // Select out svrl:failed-assert nodes and put into exception container
        Document reportDoc = parser.parse(new InputSource(new StringReader(
            report.toString())));
        NodeList nodes = reportDoc.getElementsByTagNameNS(
            "http://purl.oclc.org/dsdl/svrl", "failed-assert");
        for (int i = 0; i < nodes.getLength(); i++) {
          Node node = nodes.item(i);
          // Add an error for each failed asssert
          container.addException(new LabelException(ExceptionType.ERROR, node
              .getTextContent().trim(), url.toString(), node
              .getAttributes().getNamedItem("location").getTextContent(), node
              .getAttributes().getNamedItem("test").getTextContent()));

        }
      }
    }
    // Add the File Reference Validator to the list of External Validators
    // if the flag was set to 'true'
    if (getConfiguration(FILE_REF_CHECK)) {
      externalValidators.add(new FileReferenceValidator());
    }
    if (!externalValidators.isEmpty()) {
      // Perform any other additional checks that were added
      for(ExternalValidator ev : externalValidators) {
        ev.validate(container, url);
      }
    }
    // Perform any additional checks that were added
    if (!documentValidators.isEmpty()) {
      DocumentInfo xml = parse(new StreamSource(url.openStream()));
      for (DocumentValidator dv : documentValidators) {
        dv.validate(container, xml);
      }
    }
  }

  public void validate(File labelFile) throws SAXException, IOException,
      ParserConfigurationException, TransformerException {
    validate(null, labelFile);
  }

  private DocumentInfo parse(StreamSource source) throws XPathException {
    XPathEvaluator xpath = new XPathEvaluator();
    Configuration configuration = xpath.getConfiguration();
    configuration.setLineNumbering(true);
    ParseOptions options = new ParseOptions();
    options.setErrorListener(new XMLErrorListener());
    return configuration.buildDocument(source);
  }

  public String getModelVersion() {
    return modelVersion;
  }

  public void setModelVersion(String modelVersion) throws ValidatorException {
    if (!VersionInfo.getSupportedModels().contains(modelVersion)) {
      throw new ValidatorException(ExceptionType.ERROR, "Unsupported model version \""
          + modelVersion + "\" use one of "
          + VersionInfo.getSupportedModels().toString());
    }
    this.modelVersion = modelVersion;
  }

  public Boolean performsSchemaValidation() {
    return getConfiguration(SCHEMA_CHECK);
  }

  public void setSchemaCheck(Boolean value) {
    this.setConfiguration(SCHEMA_CHECK, value);
  }

  public Boolean performsSchematronValidation() {
    return getConfiguration(SCHEMATRON_CHECK);
  }

  public void setSchematronCheck(Boolean value) {
    this.setConfiguration(SCHEMATRON_CHECK, value);
  }

  public Boolean getConfiguration(String key) {
    return this.configurations.containsKey(key) ? this.configurations.get(key)
        : false;
  }

  public void setConfiguration(String key, Boolean value) {
    this.configurations.put(key, value);
  }

  public void addValidator(ExternalValidator validator) {
    this.externalValidators.add(validator);
  }

  public void addValidator(DocumentValidator validator) {
    this.documentValidators.add(validator);
  }

  public static void main(String[] args) throws Exception {
    LabelValidator lv = new LabelValidator();
    lv.setCatalogs(new String[]{args[1]});
    ExceptionContainer container = new ExceptionContainer();
    lv.validate(container, new File(args[0]));
    for (LabelException ex : container.getExceptions()) {
      System.out.println(ex.getMessage());
    }
  }
}
