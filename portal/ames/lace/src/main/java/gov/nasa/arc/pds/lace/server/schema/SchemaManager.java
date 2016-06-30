package gov.nasa.arc.pds.lace.server.schema;

import gov.nasa.arc.pds.lace.server.IDGenerator;
import gov.nasa.arc.pds.lace.server.parse.ModelAnalyzer;
import gov.nasa.arc.pds.lace.server.parse.ValidationAnalyzer;
import gov.nasa.arc.pds.lace.server.validation.SchematronValidator;
import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Implements an object that understands all the schemas
 * the application knows about and can load the schemas
 * and associate them with validation rules.
 */
public class SchemaManager {

	private static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	private static final String XML_SCHEMA_ROOT_ELEMENT = "schema";
	private static final String XML_SCHEMA_TARGET_NAMESPACE_ATTR = "targetNamespace";

	private static final String SCHEMATRON_NAMESPACE = "http://purl.oclc.org/dsdl/schematron";
	private static final String SCHEMATRON_ROOT_ELEMENT = "schema";
	private static final String SCHEMATRON_NS_ELEMENT = "ns";
	private static final String SCHEMATRON_NS_URI_ATTR = "uri";

	private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

	private IDGenerator idFactory;

	private final List<URI> schemaURIs = new ArrayList<URI>();
	private final Set<String> xmlSchemaNamespaces = new TreeSet<String>();

	private SchemaLoader loader;
	private ModelAnalyzer analyzer;
	private final Map<String, SchematronValidator> schematronValidators = new HashMap<String, SchematronValidator>();
	private final Map<String, ValidationAnalyzer> validationAnalyzers = new HashMap<String, ValidationAnalyzer>();

	@Inject
	public SchemaManager(IDGenerator idFactory) {
		this.idFactory = idFactory;
	}

	/**
	 * Add a schema file.
	 *
	 * @param schemaURI the URI of the schema file
	 */
	public void addSchemaFile(URI schemaURI) {
		schemaURIs.add(schemaURI);
	}

	/**
	 * Finish configuration of the schema manager by loading all
	 * schemas and creating the model analyzer.
	 */
	public void loadSchemas() {
		Map<String, URI> xmlSchemas = new HashMap<String, URI>();
		xmlSchemaNamespaces.clear();

		Set<String> schematronNamespaces = new HashSet<String>();

		for (URI uri : schemaURIs) {
			try {
				Document doc = loadDocument(uri);
				if (isXMLSchema(doc)) {
					String ns = getXMLSchemaNamespace(doc);
					if (xmlSchemaNamespaces.contains(ns)) {
						System.err.println("Ignoring schema file from existing namespace "
								+ "(ns=" + ns
								+ ", file=" + uri + ")");
					} else {
						xmlSchemas.put(ns, uri);
						xmlSchemaNamespaces.add(ns);
					}
				} else if (isSchematron(doc)) {
					String ns = getSchematronNamespace(doc);
					if (schematronNamespaces.contains(ns)) {
						System.err.println("Ignoring schema file from existing namespace "
								+ "(ns=" + ns
								+ ", file=" + uri + ")");
					} else {
						try {
							createSchematronValidator(ns, uri);
						} catch (TransformerException e) {
							System.err.println("Error loading schematron file - ignored: " + uri);
							e.printStackTrace();
						} catch (URISyntaxException e) {
							System.err.println("Error loading schematron file - ignored: " + uri);
							e.printStackTrace();
						}
						schematronNamespaces.add(ns);

						ValidationAnalyzer validationAnalyzer = new ValidationAnalyzer();
						try {
							validationAnalyzer.analyzeSchematronRules(uri);
						} catch (XPathExpressionException e) {
							System.err.println("Error loading schematron file - ignored: " + uri);
							e.printStackTrace();
						}
						validationAnalyzers.put(ns, validationAnalyzer);
					}
				} else {
					System.err.println("Ignoring schema file of unknown type: " + uri);
				}
			} catch (SAXException e) {
				System.err.println("Fatal error loading schema file: " + e.getMessage());
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				System.err.println("Fatal error configuring XML parser: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Fatal error reading schema file: " + e.getMessage());
				e.printStackTrace();
			}
		}

		try {
			loader = new SchemaLoader(xmlSchemas);
		} catch (SAXException e) {
			// Can't happen unless there is a configuration problem.
			System.err.println("Fatal error configuring schemas: " + e.getMessage());
			e.printStackTrace();
		}

		analyzer = new ModelAnalyzer(idFactory, this, xmlSchemas);
	}

	private boolean isXMLSchema(Document doc) {
		Element root = doc.getDocumentElement();
		return root.getLocalName().equals(XML_SCHEMA_ROOT_ELEMENT)
				&& root.getNamespaceURI().equals(XML_SCHEMA_NAMESPACE);
	}

	private String getXMLSchemaNamespace(Document doc) {
		Element root = doc.getDocumentElement();
		return root.getAttribute(XML_SCHEMA_TARGET_NAMESPACE_ATTR);
	}

	private boolean isSchematron(Document doc) {
		Element root = doc.getDocumentElement();
		return root.getLocalName().equals(SCHEMATRON_ROOT_ELEMENT)
				&& root.getNamespaceURI().equals(SCHEMATRON_NAMESPACE);
	}

	private String getSchematronNamespace(Document doc) {
		Element root = doc.getDocumentElement();
		NodeList children = root.getChildNodes();
		for (int i=0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if ((child instanceof Element)
					&& child.getLocalName().equals(SCHEMATRON_NS_ELEMENT)
					&& child.getNamespaceURI().equals(SCHEMATRON_NAMESPACE)) {
				Element nsElement = (Element) child;
				return nsElement.getAttribute(SCHEMATRON_NS_URI_ATTR);
			}
		}

		// No namespace found.
		return null;
	}

	private Document loadDocument(URI uri) throws SAXException, ParserConfigurationException, IOException {
		builderFactory.setNamespaceAware(true);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();

		builder.setErrorHandler(new ErrorHandler() {

			@Override
			public void error(SAXParseException arg0) throws SAXException {
				// Ignore errors.
			}

			@Override
			public void fatalError(SAXParseException arg0) throws SAXException {
				// Ignore errors.
			}

			@Override
			public void warning(SAXParseException arg0) throws SAXException {
				// Ignore errors.
			}

		});

		File f = new File(uri);
		return builder.parse(new FileInputStream(f));
	}

	/**
	 * Gets the schema constructed from all available XML Schema files.
	 *
	 * @return the schema
	 */
	public Schema getSchema() {
		return loader.getSchema();
	}

	/**
	 * Gets a document builder for the configured schemas.
	 *
	 * @return a document builder
	 * @throws ParserConfigurationException if there is an error configuring the document builder
	 */
	public DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		return loader.getDocumentBuilder();
	}

	/**
	 * Gets the model analyzer constructed from all available XML Schema files.
	 *
	 * @return the model analyzer
	 */
	public ModelAnalyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * Gets the namespaces that we know about.
	 *
	 * @return an array of namespace URIs
	 */
	public String[] getNamespaces() {
		return xmlSchemaNamespaces.toArray(new String[xmlSchemaNamespaces.size()]);
	}

	/**
	 * Gets the Schematron validator for a namespace.
	 *
	 * @param namespaceURI the namespace URI
	 * @return a Schematron validator, or null if no Schematron validator is configured for the namespace
	 */
	public SchematronValidator getSchematronValidator(String namespaceURI) {
		return schematronValidators.get(namespaceURI);
	}

	/**
	 * Gets all the Schematron validators that have been configured.
	 *
	 * @return a collection of Schematron validators
	 */
	public Collection<SchematronValidator> getSchematronValidators() {
		return schematronValidators.values();
	}

	/**
	 * Gets the validation analyzer for a namespace.
	 *
	 * @param namespaceURI the namespace URI
	 * @return a validation analyzer, or null if no analyzer is configured for the namespace
	 */
	public ValidationAnalyzer getValidationAnalyzer(String namespaceURI) {
		return validationAnalyzers.get(namespaceURI);
	}

	private void createSchematronValidator(
			String namespaceURI,
			URI schematronURI
	) throws MalformedURLException, TransformerException, IOException, URISyntaxException {
		SchematronValidator validator = new SchematronValidator(new File(schematronURI));
		schematronValidators.put(namespaceURI, validator);
	}

	/**
	 * Adds valid values to a list of model items.
	 *
	 * @param items a list of model items
	 * @param parent the parent container of the items, or null for no parent
	 */
	public void addValidValues(List<LabelItem> items, Container parent) {
		for (LabelItem item : items) {
			addValidValues(item, parent);
		}
	}

	/**
	 * Adds valid values for a single model item.
	 *
	 * @param item the model item
	 * @param parent the parent container of the item, or null if no parent
	 */
	public void addValidValues(LabelItem item, Container parent) {
		if (item instanceof Container) {
			Container container = (Container) item;
			addContextPath(container, parent);

			for (LabelItem child : container.getContents()) {
				addValidValues(child, container);
			}
		}

		if (item instanceof LabelElement) {
			addValidValuesForElement((LabelElement) item, parent);
		}
	}

	private void addValidValuesForElement(LabelElement element, Container parent) {
		String parentContext = (parent==null ? "" : parent.getContext());
		ValidationAnalyzer validation = getValidationAnalyzer(element.getType().getElementNamespace());
		if (validation != null) {
			validation.addValidValues(element, parentContext);

			for (AttributeItem attr : element.getAttributes()) {
				String component = new QName(element.getType().getElementNamespace(), element.getType().getElementName()).toString();
				String elementContext = (parent==null ? component : parentContext + "/" + component);
				validation.addValidValues(attr, elementContext);
			}
		}
	}

	private void addContextPath(Container child, Container parent) {
		String component = new QName(child.getType().getElementNamespace(), child.getType().getElementName()).toString();
		String context = (parent==null ? component : parent.getContext() + "/" + component);
		child.setContext(context);
	}

}
