//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id$ 
//

package gov.nasa.pds.search.core.indexer.solr;

import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.util.PDSDateConvert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parses a document for text search and creates a Lucene document.
 * 
 * @author pramirez
 * @modifiedby Jordan Padams
 * @modifieddate 05/04/09
 * @version $Revision$
 * 
 */
public class DocumentParser {
	private static Logger LOG = Logger
			.getLogger(DocumentParser.class.getName());

	/**
	 * Creates the document object a parses out all of the data from each
	 * individual XML documents.
	 * 
	 * @param file
	 *            - The individual profile document for each record for the
	 *            current class.
	 * @return indexDoc - Lucene DOM document returned with parsed data.
	 * @throws ParseException
	 */
	public static StringBuffer parse(File file) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Reader reader = null;
		org.w3c.dom.Document document = null;
		StringBuffer indexDoc = null;
		try {
			try {
				builder = factory.newDocumentBuilder();

				// Modified 2/2/2011 per MalformedByteSequenceException
				// Need to ensure read in UTF-8 format, and override SAX input
				// source
				InputStream stream = new FileInputStream(file); // Get
																// InputStream
				reader = new InputStreamReader(stream, "UTF-8"); // Specify
																	// reading
																	// content
																	// in UTF-8
				InputSource is = new InputSource(reader); // Init input source
				is.setEncoding("UTF-8"); // Overriding SAX input source to UTF-8
				document = builder.parse(is); // Send to parser

				indexDoc = retrieveMetadata(document);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} finally {
				if (reader != null)
					reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return indexDoc;
	}

	/**
	 * Parse out all the data from the document and add a field name and value.
	 * 
	 * @param document
	 *            - DOM Document object parsed into name-value pairs.
	 * @return indexDoc - Lucene DOM Document object that takes parsed pairs and
	 *         recreates document with added fields and values, and makes it
	 *         ready for SOLR.
	 * @throws ParseException
	 */
	private static StringBuffer retrieveMetadata(org.w3c.dom.Document document)
			throws Exception {
		StringBuffer indexDoc = new StringBuffer();
		Element root = document.getDocumentElement();
		NodeList children = root.getChildNodes();
		String resClass = "";
		boolean invalidDoc = false;
		indexDoc.append("<doc>\n");

		// The name of the cataloged item should be the child at position
		if (children.getLength() > 1) {
			Node catalogItem = children.item(1);
			resClass = catalogItem.getLocalName();
			resClass = resClass.replaceAll("_", "");
			LOG.fine("resClass: " + resClass);
			children = catalogItem.getChildNodes();
			for (int i = 1; i < children.getLength(); i++) {
				Node child = children.item(i);
				String appendStr = "";
				if (child.getNodeType() != Node.TEXT_NODE) {
					String name = child.getLocalName();
					String value = child.getTextContent();

					LOG.fine("name: " + name);
					LOG.fine("value: " + value);

					// Slightly changed functionality from original document
					// parser
					// Now UNK/N/A/UNKNOWN etc are handled in date conversion
					if (name.equals("title") && Arrays.asList(Constants.VALID_UNK_VALUES).contains(
							value.toUpperCase())) {
						invalidDoc = true;
					} else if (name.endsWith("date") || name.endsWith("time")) {
						//System.out.println(name + " - " + value);
						value = PDSDateConvert.convert(name, value);
						//System.out.println("CONVERTED " + name + " - " + value);

						appendStr = "<field name=\"" + name + "\">"
								+ value + "</field>\n";
					} else {
						if (!name.equals("resClass")) {
							appendStr = "<field name=\"" + name
									+ "\"><![CDATA[" + value
									+ "]]></field>\n";
						} else {
							appendStr = "<field name=\"" + name
									+ "\"><![CDATA[" + resClass
									+ "]]></field>\n";
						}
					}
				}
				indexDoc.append(appendStr);
			}
		}
		indexDoc.append("</doc>\n");

		if (invalidDoc)
			indexDoc = null;

		return indexDoc;
	}

}
