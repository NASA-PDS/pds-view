//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id$ 
//

package gov.nasa.pds.search.core.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
	 */
	public static StringBuffer parse(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		org.w3c.dom.Document document = null;
		StringBuffer indexDoc = null;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new FileInputStream(file));
			indexDoc = retrieveMetadata(document);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
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
	 */
	private static StringBuffer retrieveMetadata(org.w3c.dom.Document document) {
		StringBuffer indexDoc = new StringBuffer();
		StringBuffer contents = new StringBuffer();
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
				if (child.getNodeType() != Node.TEXT_NODE) {
					String name = child.getLocalName();
					String value = child.getTextContent();
					
					LOG.fine("name: " + name);
					LOG.fine("value: " + value);
					
					if (value.toUpperCase().equals("UNKNOWN"))
							value = "UNK";
					
					if ("title".equals(name)
							&& ("N/A".equals(value.toUpperCase())
									|| "UNK".equals(value.toUpperCase()) || "NULL"
									.equals(value.toUpperCase()))) {
						invalidDoc = true;
					}

					if (!"N/A".equals(value.toUpperCase())
							&& !"UNK".equals(value.toUpperCase())
							&& !"NULL".equals(value.toUpperCase())) {
						if (!"resClass".equals(name)) {
							if (name.endsWith("date") || name.endsWith("time")) {
								value = value.toUpperCase();
								if (value.length() < 4 || !value.matches(".*[0-9].*"))
									value = "3000-01-01T00:00:00Z";
								else if (value.length() == 4)
									value += "-01-01T00:00:00Z";
								else if (value.length() == 7)
									value += "-01T00:00:00Z";
								else if (value.length() == 10)
									value += "T00:00:00Z";
								else if (value.length() == 16)
									value += ":00Z";
								else if (value.length() < 24)
									value += "Z";
								
								indexDoc.append("<field name=\"" + name + "\">"
										+ value + "</field>\n");
							} else if (name.endsWith("time")) {
								indexDoc.append("<field name=\"" + name + "\">"
										+ value.toUpperCase() + "Z</field>\n");
							} else {
								indexDoc.append("<field name=\"" + name
										+ "\"><![CDATA[" + value
										+ "]]></field>\n");
							}
						} else {
							indexDoc.append("<field name=\"" + name
									+ "\"><![CDATA[" + resClass
									+ "]]></field>\n");
						}
					}
				}
			}
		}
		indexDoc.append("</doc>\n");

		if (invalidDoc)
			indexDoc = null;

		return indexDoc;
	}

	/**
	 * Used to test the current java program.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		// System.out.println(DocumentParser.retrieveMetadata(builder.parse(args[0])));
		System.out
				.println(DocumentParser.retrieveMetadata(builder
						.parse("/Users/jpadams/dev/workspace/solr/tse/extract/target/tse_target_10001.xml")));
	}

}
