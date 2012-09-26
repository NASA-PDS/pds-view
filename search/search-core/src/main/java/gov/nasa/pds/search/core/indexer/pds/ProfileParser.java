//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id$ 
//

package gov.nasa.pds.search.core.indexer.pds;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parses a profile for text search and creates a Lucene document.
 * 
 * @author pramirez
 * @modifiedby Jordan Padams
 * @modifieddate 05/04/09
 * @version $Revision$
 * 
 */
public class ProfileParser {
	private static Logger LOG = Logger.getLogger(ProfileParser.class.getName());

	/**
	 * Creates the document object a parses out all of the data from each
	 * individual XML documents.
	 * 
	 * @param file
	 *            - The individual profile document for each record for the
	 *            current class.
	 * @return Document - Lucene DOM document returned with parsed data.
	 */
	public static Document parse(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document indexDoc = null;
		Reader reader = null;
		org.w3c.dom.Document document = null;
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
				if (reader != null) {
					reader.close();
				}
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
	 * @return Document - DOM Document object that takes parsed pairs and
	 *         recreates document with added fields and values.
	 */
	private static Document retrieveMetadata(org.w3c.dom.Document document) {
		Document indexDoc = new Document();
		StringBuffer contents = new StringBuffer();
		Element root = document.getDocumentElement();
		NodeList children = root.getChildNodes();
		String resClass = "";
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

					if (!name.equals("resClass")) {
						// indexDoc.add(new Field(name, value, true, true, true,
						// false));
						indexDoc.add(new Field(name, value, Field.Store.YES,
								Field.Index.ANALYZED));
					} else {
						// indexDoc.add(new Field(name, resClass, true, true,
						// true, false));
						indexDoc.add(new Field(name, resClass, Field.Store.YES,
								Field.Index.ANALYZED));
					}
					contents.append(value + " ");
					LOG.fine("name: " + name);
					LOG.fine("value: " + value);
					LOG.fine("contents: " + contents);
				}
			}
		}
		// indexDoc.add(Field.Text("contents", contents.toString()));
		indexDoc.add(new Field("contents", contents.toString(),
				Field.Store.YES, Field.Index.ANALYZED));
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
		// System.out.println(ProfileParser.retrieveMetadata(builder.parse(args[0])));
		System.out
				.println(ProfileParser.retrieveMetadata(builder
						.parse("/Users/jpadams/dev/workspace/solr/tse/extract/target/tse_target_10001.xml")));
	}

}
