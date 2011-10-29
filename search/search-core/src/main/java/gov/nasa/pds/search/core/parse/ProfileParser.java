//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id$ 
//

package gov.nasa.pds.search.core.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
		org.w3c.dom.Document document = null;
		try {
			builder = factory.newDocumentBuilder();
			StringBuffer fileData = new StringBuffer(1000);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();
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

					if (!"resClass".equals(name)) {
						//indexDoc.add(new Field(name, value, true, true, true, false));
						indexDoc.add(new Field(name, value, Field.Store.YES, Field.Index.ANALYZED));
					} else {
						//indexDoc.add(new Field(name, resClass, true, true, true, false));
						indexDoc.add(new Field(name, resClass, Field.Store.YES, Field.Index.ANALYZED));
					}
					contents.append(value + " ");
					LOG.fine("name: " + name);
					LOG.fine("value: " + value);
					LOG.fine("contents: " + contents);
				}
			}
		}
		//indexDoc.add(Field.Text("contents", contents.toString()));
		indexDoc.add(new Field("contents", contents.toString(), Field.Store.YES, Field.Index.ANALYZED));
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
