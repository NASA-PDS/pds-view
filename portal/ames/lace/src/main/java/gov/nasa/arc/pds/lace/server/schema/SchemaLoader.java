package gov.nasa.arc.pds.lace.server.schema;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

/**
 * Implements a facility for loading an XML schema from a
 * list of schema locations.
 */
public class SchemaLoader {

	// Not using XML 1.1 features yet.
	@SuppressWarnings("unused")
	private static final String W3C_XML_SCHEMA_1_1_NS_URI = "http://www.w3.org/XML/XMLSchema/v1.1";

	private SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

	private Schema schema;

	/**
	 * Creates a new instance of the parser with a set of schema paths.
	 *
	 * @param xmlSchemas a map from namespaces to the corresponding schema locations
	 * @throws SAXException if there is an error loading the schemas
	 */
	public SchemaLoader(final Map<String, URI> xmlSchemas) throws SAXException {
		builderFactory.setValidating(false); // We're not using DTD validation.
		final DOMImplementationLS domLS = getDomImplementationLS();

		LSResourceResolver resolver = new LSResourceResolver() {
			@Override
			public LSInput resolveResource(
					String type,
					String namespaceURI,
					String publicID,
					String systemID,
					String baseURI
			) {
				URI schemaURI = xmlSchemas.get(namespaceURI);
				LSInput input = domLS.createLSInput();

				if (schemaURI != null) {
					input.setSystemId(schemaURI.toString());
				}

				return input;
			}
		};

		List<StreamSource> sources = new ArrayList<StreamSource>();
		for (URI uri : xmlSchemas.values()) {
			sources.add(new StreamSource(new File(uri)));
		}

		schemaFactory.setResourceResolver(resolver);
		schema = schemaFactory.newSchema(sources.toArray(new StreamSource[sources.size()]));
		builderFactory.setIgnoringComments(true);
		builderFactory.setCoalescing(true);
		builderFactory.setNamespaceAware(true);
		builderFactory.setSchema(schema);
	}

	/**
	 * Gets the schema loaded from the source locations.
	 *
	 * @return the loaded schema
	 */
	public Schema getSchema() {
		return schema;
	}

	/**
	 * Gets a document builder for the schema.
	 *
	 * @return a document builder
	 * @throws ParserConfigurationException if there is an error configuring the builder
	 */
	public DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		return builderFactory.newDocumentBuilder();
	}

	private DOMImplementationLS getDomImplementationLS() {
		DOMImplementationRegistry registry = null;
		try {
			registry = DOMImplementationRegistry.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Cannot instantiate DOMImplementationRegistry: " + e.getMessage());
		}

		return (DOMImplementationLS) registry.getDOMImplementation("LS");
	}

}
