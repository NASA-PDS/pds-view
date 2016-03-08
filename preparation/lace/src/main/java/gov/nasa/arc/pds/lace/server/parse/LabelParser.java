package gov.nasa.arc.pds.lace.server.parse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Implements a parser for XML labels. The parser attempts
 * to do a validated parse against a specified XML schema.
 */
public class LabelParser {

	private SchemaLoader loader;

	/**
	 * Creates a new instance of the parser with a set of schema paths.
	 *
	 * @param schemaPaths an array of schema paths
	 * @throws SAXException if there is an error loading the schemas
	 */
	public LabelParser(String... schemaPaths) throws SAXException {
		loader = new SchemaLoader(schemaPaths);
	}

	/**
	 * Creates a new instance of the parser with a set of schema URIs.
	 *
	 * @param schemaURIs an array of schema URIs
	 * @throws SAXException if there is an error loading the schemas
	 */
	public LabelParser(URI... schemaURIs) throws SAXException {
		loader = new SchemaLoader(schemaURIs);
	}

	/**
	 * Parses an XML file at a given path and returns the resulting
	 * document.
	 *
	 * @param path the path to the XML file
	 * @return the XML document
	 * @throws ParserConfigurationException if there is an error configuring the XML parser
	 * @throws SAXException if there is an error parsing the XML
	 * @throws IOException if there is an I/O error
	 */
	public Document parse(String path) throws ParserConfigurationException, SAXException, IOException {
		return parse(new FileInputStream(path));
	}

	/**
	 * Parses an XML file from an input stream and returns the resulting
	 * document.
	 *
	 * @param in the input stream
	 * @return the XML document
	 * @throws ParserConfigurationException if there is an error configuring the XML parser
	 * @throws SAXException if there is an error parsing the XML
	 * @throws IOException if there is an I/O error
	 */
	public Document parse(InputStream in) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = loader.getDocumentBuilder();
		return builder.parse(in);
	}

}
