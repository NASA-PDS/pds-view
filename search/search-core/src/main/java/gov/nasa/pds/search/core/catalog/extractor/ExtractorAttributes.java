/**
 * Copyright (c) 2009, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 * 
 * $Id$
 *  
 */
package gov.nasa.pds.search.core.catalog.extractor;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Jordan Padams
 * 
 */
public class ExtractorAttributes {

	private Document doc;
	private ArrayList attrNames;
	private ArrayList attrIndexes;
	private ArrayList attrValues;
	private int totalColumns;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public ExtractorAttributes(String filename) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			doc = docBuilder.parse(new File(filename));

			// normalize text representation
			doc.getDocumentElement().normalize();
			log.info("New attribute list for "
					+ doc.getDocumentElement().getNodeName());

			setAttrInfo();

		} catch (SAXParseException e) {
			System.err.println("** Parsing error" + ", line "
					+ e.getLineNumber() + ", uri " + e.getSystemId());
			System.err.println(" " + e.getMessage());

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void setAttrInfo() {
		String tValue;
		String name;
		String index;

		NodeList columnList = doc.getElementsByTagName("column");

		attrNames = new ArrayList();
		attrIndexes = new ArrayList();
		attrValues = new ArrayList();

		totalColumns = columnList.getLength();
		log.fine("Total no of columns : " + totalColumns);

		try {
			for (int i = 0; i < columnList.getLength(); i++) {
				Node columnNode = columnList.item(i);

				if (columnNode.getNodeType() == Node.ELEMENT_NODE) {
					// Read the attribute, index, and value and place it into
					// its appropriate ArrayList.
					Element columnElement = (Element) columnNode;

					attrNames.add((columnElement
							.getElementsByTagName("name").item(0)
							.getChildNodes().item(0)).getNodeValue().trim());
					attrIndexes.add((columnElement
							.getElementsByTagName("index").item(0)
							.getChildNodes().item(0)).getNodeValue().trim());
					attrValues.add((columnElement
							.getElementsByTagName("value").item(0)
							.getChildNodes().item(0)).getNodeValue().trim());
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.err
					.println("Error: A tag in class XML file contains no text node.");
		}
	}

	public String getQuery() {
		return (doc.getElementsByTagName("query").item(0)
				.getChildNodes().item(0)).getNodeValue().trim();
	}

	public String getName(int column) {
		return (String) attrNames.get(column);
	}

	public int getIndex(int column) {
		return Integer.parseInt((String) attrIndexes.get(column));
	}

	public String getValue(int column) {
		return (String) attrValues.get(column);
	}

	public int getNumAttr() {
		return totalColumns;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExtractorAttributes attributes = new ExtractorAttributes(
				"targetconfig.xml");

		System.out.println("query = " + attributes.getQuery());
		// System.out.println(attributes.attrNames.size());
		// System.out.println(attributes.getName(0));
		for (int i = 0; i < attributes.getNumAttr(); i++) {
			System.out.println("name = " + attributes.getName(i));
			System.out.println("index = "
					+ Integer.toString(attributes.getIndex(i)));
			System.out.println("value = " + attributes.getValue(i));
		}

	}

}
