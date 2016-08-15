package gov.nasa.arc.pds.lace.server.parse;

import gov.nasa.arc.pds.lace.server.schema.SchemaManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Implements a parser for XML labels. The parser attempts
 * to do a validated parse against a specified XML schema.
 */
public class LabelParser {
	
	/**
	 * Parses an XML file at a given path and returns the resulting
	 * document.
	 *
	 * @param manager the schema manager
	 * @param path the path to the XML file
	 * @return the XML document
	 * @throws ParserConfigurationException if there is an error configuring the XML parser
	 * @throws SAXException if there is an error parsing the XML
	 * @throws IOException if there is an I/O error
	 */
	public Document parse(SchemaManager manager, String path) throws ParserConfigurationException, SAXException, IOException {
		return parse(manager, new FileInputStream(path));
	}

	/**
	 * Parses an XML file from an input stream and returns the resulting
	 * document.
	 *
	 * @param manager the schema manager
	 * @param in the input stream
	 * @return the XML document
	 * @throws ParserConfigurationException if there is an error configuring the XML parser
	 * @throws SAXException if there is an error parsing the XML
	 * @throws IOException if there is an I/O error
	 */
	public Document parse(SchemaManager manager, InputStream in) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = manager.getDocumentBuilder();
		
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

		return builder.parse(in);
	}

}
