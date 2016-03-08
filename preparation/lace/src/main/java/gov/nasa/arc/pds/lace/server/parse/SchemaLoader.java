package gov.nasa.arc.pds.lace.server.parse;

import java.io.File;
import java.net.URI;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * Implements a facility for loading an XML schema from a
 * list of schema locations.
 */
public class SchemaLoader {

	// Not using XML 1.1 features yet.
	@SuppressWarnings("unused")
	private static final String W3C_XML_SCHEMA_1_1_NS_URI = "http://www.w3.org/XML/XMLSchema/v1.1";

	private static SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	static {
		builderFactory.setValidating(false); // We're not using DTD validation.
	}

	private Schema schema;

	/**
	 * Creates a new instance of the parser with a set of schema paths.
	 *
	 * @param schemaPaths an array of schema paths
	 * @throws SAXException if there is an error loading the schemas
	 */
	public SchemaLoader(String... schemaPaths) throws SAXException {
		StreamSource[] sources = new StreamSource[schemaPaths.length];

		for (int i=0; i < schemaPaths.length; ++i) {
			sources[i] = new StreamSource(new File(schemaPaths[i]));
		}

		loadSchema(sources);
	}

	/**
	 * Creates a new instance of the parser with a set of schema paths.
	 *
	 * @param schemaURIs an array of schema locations
	 * @throws SAXException if there is an error loading the schemas
	 */
	public SchemaLoader(URI... schemaURIs) throws SAXException {
		StreamSource[] sources = new StreamSource[schemaURIs.length];

		for (int i=0; i < schemaURIs.length; ++i) {
			sources[i] = new StreamSource(new File(schemaURIs[i]));
		}

		loadSchema(sources);
	}

	private void loadSchema(StreamSource[] schemaSources) throws SAXException {
		schema = schemaFactory.newSchema(schemaSources);
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

}
