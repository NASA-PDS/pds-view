//	Copyright 2009-2014, by the California Institute of Technology.
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

import gov.nasa.pds.tools.label.validate.DefaultDocumentValidator;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.ParseOptions;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.xpath.XPathEvaluator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
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
  private DocumentBuilder cachedValidator;
  private List<Transformer> cachedSchematron;
  private XMLCatalogResolver resolver;
  private Boolean useLabelSchema;
  private Boolean useLabelSchematron;
  private Map<String, Transformer> cachedLabelSchematrons;
  private Transformer isoTransformer;
  private TransformerFactory transformerFactory;
  
  
  public static final String SCHEMA_CHECK = "gov.nasa.pds.tools.label.SchemaCheck";
  public static final String SCHEMATRON_CHECK = "gov.nasa.pds.tools.label.SchematronCheck";
  public static final String FILE_REF_CHECK = "gov.nasa.pds.tools.label.fileReferenceCheck";

  private List<ExternalValidator> externalValidators;
  private List<DocumentValidator> documentValidators;
  private CachedEntityResolver externalEntityResolver;
  private DocumentBuilderFactory docBuilderFactory;
  private SchemaFactory schemaFactory; 
  private Schema validatingSchema;
  
  public LabelValidator() throws ParserConfigurationException {
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
    useLabelSchema = false;
    useLabelSchematron = false;
    cachedLabelSchematrons = new HashMap<String, Transformer>();
    isoTransformer = null;
    transformerFactory = null;
    externalEntityResolver = new CachedEntityResolver();
    validatingSchema = null;
    // Support for XSD 1.1
    schemaFactory = SchemaFactory
        .newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
    docBuilderFactory = DocumentBuilderFactory.newInstance();
    docBuilderFactory.setNamespaceAware(true);
    docBuilderFactory.setXIncludeAware(true);
    //TODO: Do we want to omit the xml:base attribute from the merged xml?
    docBuilderFactory.setFeature(
        "http://apache.org/xml/features/xinclude/fixup-base-uris",
        false);    

    documentValidators.add(new DefaultDocumentValidator());
    // Add the File Reference Validator to the list of Validators
    // if the flag was set to 'true'
    if (getConfiguration(FILE_REF_CHECK)) {
      documentValidators.add(new FileReferenceValidator());
    }
  }

  public void setSchema(String[] schemaFiles) throws SAXException {
    userSchemaFiles = schemaFiles;
  }

  public void setSchematronFiles(String[] schematronFiles) {
    userSchematronFiles = schematronFiles;
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
  TransformerException, MissingLabelSchemaException {
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
   * @throws MissingLabelSchemaException 
   */
  public synchronized void validate(ExceptionContainer container, URL url)
      throws SAXException, IOException, ParserConfigurationException,
      TransformerException, MissingLabelSchemaException {
    List<String> labelSchematronRefs = new ArrayList<String>();
    Document xml = null;
    // Are we perfoming schema validation?
    if (performsSchemaValidation()) {
      // Do we have a schema we have loaded previously?
      if (cachedValidator == null) {
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
        if (userSchemaFiles != null) {
          // User has specified schema files to use
          validatingSchema = schemaFactory.newSchema(loadSchemaSources(
              userSchemaFiles).toArray(new StreamSource[0]));
        } else if (resolver == null) {
          if (useLabelSchema) {
            validatingSchema = schemaFactory.newSchema(); 
          } else if (VersionInfo.isInternalMode()) {
            // There is no catalog file
            
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
        docBuilderFactory.setSchema(validatingSchema);
        
        cachedValidator = docBuilderFactory.newDocumentBuilder();
        // Allow access to the catalog from the parser
        if (resolver != null) {
          cachedValidator.setEntityResolver(resolver);
        } else if (useLabelSchema) {
          cachedValidator.setEntityResolver(externalEntityResolver);
        }
        // Capture messages in a container
        if (container != null) {
          cachedValidator.setErrorHandler(new LabelErrorHandler(container));
        }
      } else {
        // Create a new instance of the DocumentBuilder if validating
        // against a label's schema.
        if (useLabelSchema) {
          cachedValidator = docBuilderFactory.newDocumentBuilder();
          // Capture messages in a container
          if (container != null) {
            cachedValidator.setErrorHandler(new LabelErrorHandler(container));
          }
          cachedValidator.setEntityResolver(externalEntityResolver);
        }
      }
      // Finally validate the file
      xml = cachedValidator.parse(url.openStream(), url.toString());
      
      // If validating against the label supplied schema, check
      // if the xsi:schemalocation attribute was defined in the label.
      // If it is not found, throw an exception.
      if (useLabelSchema) {
        Element root = xml.getDocumentElement();
        if (!root.hasAttribute("xsi:schemaLocation")) {
          throw new MissingLabelSchemaException("No schema specified in the label.");
        }
      }
    } else {
      // No Schema validation will be performed. Just parse the label
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      // Capture messages in a container
      if (container != null) {
        docBuilder.setErrorHandler(new LabelErrorHandler(container));
      }
      if (resolver != null) {
        docBuilder.setEntityResolver(resolver);
      } else if (useLabelSchema) {
        docBuilder.setEntityResolver(externalEntityResolver);
      }
      xml = docBuilder.parse(url.openStream(), url.toString());      
    }
    
    // Validate with any schematron files we have
    if (performsSchematronValidation()) {
      // Look for schematron files specified in a label 
      if (useLabelSchematron) {
        labelSchematronRefs = getSchematrons(xml.getChildNodes());
      }
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
        isoTransformer = isoFactory.newTransformer(isoSchematron);
        // Setup a different factory for user schematron files as it will not
        // use
        // the same URIResolver
        transformerFactory = TransformerFactory.newInstance();
        if (useLabelSchematron) {
          cachedSchematron = loadLabelSchematrons(labelSchematronRefs, url, container);
        } else if (userSchematronFiles == null) {
          // If user does not provide schematron then use ones in jar if available
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
            cachedSchematron.add(transformerFactory.newTransformer(new StreamSource(
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
            cachedSchematron.add(transformerFactory.newTransformer(new StreamSource(
                new StringReader(schematronStyleSheet.toString()))));
          }
        }
      } else {
        if (useLabelSchematron) {
          cachedSchematron = loadLabelSchematrons(labelSchematronRefs, url, container);
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
          container.addException(processFailedAssert(url, node));
        }
      }
    }
    if (!externalValidators.isEmpty()) {
      // Perform any other additional checks that were added
      for(ExternalValidator ev : externalValidators) {
        ev.validate(container, url);
      }
    }

    // Perform any additional checks that were added
    if (!documentValidators.isEmpty()) {
      SAXSource saxSource = new SAXSource(new InputSource(url.toString()));
      saxSource.setSystemId(url.toString());
      DocumentInfo docInfo = parse(saxSource);
      for (DocumentValidator dv : documentValidators) {
        dv.validate(container, docInfo);
      }
    }
  }

  public void validate(File labelFile) throws SAXException, IOException,
      ParserConfigurationException, TransformerException, MissingLabelSchemaException {
    validate(null, labelFile);
  }

  public List<String> getSchematrons(NodeList nodeList) {
    List<String> results = new ArrayList<String>();
    for (int i = 0; i < nodeList.getLength(); i++) {
      if (nodeList.item(i).getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
        ProcessingInstruction pi = (ProcessingInstruction) nodeList.item(i);
        if ("xml-model".equalsIgnoreCase(pi.getTarget())) {        
          Pattern pattern = Pattern.compile("href=\\\"([^=]*)\\\"( schematypens=\\\"([^=]*)\\\")?");
          String filteredData = pi.getData().replaceAll("\\s+", " ");
          Matcher matcher = pattern.matcher(filteredData);
          if (matcher.matches()) {
            results.add(matcher.group(1).trim());
          }
        }
      }
    }
    return results;
  }  
  
  private List<Transformer> loadLabelSchematrons(List<String> schematronSources, URL url, ExceptionContainer container) {
    StringWriter schematronStyleSheet = new StringWriter();
    List<Transformer> transformers = new ArrayList<Transformer>();
    for (String source : schematronSources) {
      try {
        Transformer transformer = cachedLabelSchematrons.get(source);
        if (transformer != null) {
          transformers.add(transformer);
        } else {
          isoTransformer.transform(new StreamSource(source), 
          new StreamResult(schematronStyleSheet));
          transformer = transformerFactory.newTransformer(new StreamSource(
            new StringReader(schematronStyleSheet.toString())));
          transformers.add(transformer);
          cachedLabelSchematrons.put(source, transformer);
        }
      } catch (Exception e) {
        String message = "Error occurred while loading schematron: " + e.getMessage(); 
        container.addException(new LabelException(ExceptionType.ERROR, message, url.toString()));
      }
    }
    return transformers;
  }
  
  /**
   * Process a failed assert message from the schematron report.
   *
   * @param url The url of the xml being validated.
   * @param node The node object containing the failed assert message.
   *
   * @return A LabelException object.
   */
  private LabelException processFailedAssert(URL url, Node node) {
    ExceptionType exceptionType = ExceptionType.ERROR;
    if (node.getAttributes().getNamedItem("role") != null) {
      String type = node.getAttributes().getNamedItem("role")
      .getTextContent();
      if ("warn".equalsIgnoreCase(type) ||
          "warning".equalsIgnoreCase(type)) {
        exceptionType = ExceptionType.WARNING;
      } else if ("info".equalsIgnoreCase(type)) {
        exceptionType = ExceptionType.INFO;
      }
    }
    return new LabelException(
        exceptionType,
        node.getTextContent().trim(),
        url.toString(),
        node.getAttributes().getNamedItem("location").getTextContent(),
        node.getAttributes().getNamedItem("test").getTextContent()
        );

  }

  private DocumentInfo parse(SAXSource source) throws TransformerException {    
    XPathEvaluator xpath = new XPathEvaluator();
    Configuration configuration = xpath.getConfiguration();
    configuration.setLineNumbering(true);
    configuration.setXIncludeAware(true);
    ParseOptions options = new ParseOptions();
    options.setErrorListener(new XMLErrorListener());
    options.setLineNumbering(true);
    options.setXIncludeAware(true);
    return configuration.buildDocument(source, options);
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
    setSchemaCheck(value, false);
  }
  
  public void setSchemaCheck(Boolean value, Boolean useLabelSchema) {
    this.setConfiguration(SCHEMA_CHECK, value);
    this.useLabelSchema = useLabelSchema;
  }

  public Boolean performsSchematronValidation() {
    return getConfiguration(SCHEMATRON_CHECK);
  }

  public void setSchematronCheck(Boolean value) {
    setSchematronCheck(value, false);
  }
  
  public void setSchematronCheck(Boolean value, Boolean useLabelSchematron) {
    this.setConfiguration(SCHEMATRON_CHECK, value);
    this.useLabelSchematron = useLabelSchematron;
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
