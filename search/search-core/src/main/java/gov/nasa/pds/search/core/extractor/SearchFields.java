/**
 * Copyright (c) 2009, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 * 
 * $Id$
 *  
 */
package gov.nasa.pds.search.core.extractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
public class SearchFields {

	private Document doc;
	private ArrayList attrNames;
	private ArrayList attrTypes;
	private ArrayList attrValues;
	private int totalColumns;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public SearchFields(String filename) throws ProductClassException {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			this.doc = docBuilder.parse(new File(filename));

			// normalize text representation
			this.doc.getDocumentElement().normalize();

			setAttrInfo();
		} catch (SAXParseException e) {
			throw new ProductClassException("SAXParseException: Parsing error"
					+ ", line " + e.getLineNumber() + ", uri "
					+ e.getSystemId());
		} catch (SAXException e) {
			throw new ProductClassException("SAXException: " + e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void setAttrInfo() {
		String tValue;
		String name;
		String index;

		NodeList columnList = this.doc.getElementsByTagName("field");

		this.attrNames = new ArrayList();
		this.attrTypes = new ArrayList();
		this.attrValues = new ArrayList();

		this.totalColumns = columnList.getLength();
		log.fine("Total no of columns : " + totalColumns);

		try {
			for (int i = 0; i < columnList.getLength(); i++) {
				Node columnNode = columnList.item(i);

				if (columnNode.getNodeType() == Node.ELEMENT_NODE) {
					// Read the attribute, index, and value and place it into
					// its appropriate ArrayList.
					Element columnElement = (Element) columnNode;

					this.attrNames.add((columnElement.getElementsByTagName("name")
							.item(0).getChildNodes().item(0)).getNodeValue()
							.trim());
					this.attrTypes.add((columnElement.getElementsByTagName("type")
							.item(0).getChildNodes().item(0)).getNodeValue()
							.trim());
					this.attrValues.add((columnElement.getElementsByTagName("value")
							.item(0).getChildNodes().item(0)).getNodeValue()
							.trim());
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.err.println("Error: A tag in class XML file contains no text node.");
		}
	}

	/**
	 * Used under old search tools and database back end. See getResponse() for
	 * use with Registry Service
	 * 
	 * @return
	 */
	@Deprecated
	public String getQuery() {
		return (this.doc.getElementsByTagName("query").item(0).getChildNodes()
				.item(0)).getNodeValue().trim();
	}

	public List getAssociations() {
		String associationType;
		List assocList = new ArrayList();
		NodeList assocNodes = this.doc.getElementsByTagName("association");
		for (int i = 0; i < assocNodes.getLength(); i++) {
			associationType = assocNodes.item(i).getChildNodes().item(0)
					.getNodeValue().trim();
			assocList.add(associationType);
		}
		return assocList;
	}

	public String getObjectType() {
		return (this.doc.getElementsByTagName("object_type").item(0).getChildNodes()
				.item(0)).getNodeValue().trim();
	}
	
	public String getObjectName() {
		if (this.doc.getElementsByTagName("object_name").item(0) != null) {
			return (this.doc.getElementsByTagName("object_name").item(0).getChildNodes()
					.item(0)).getNodeValue().trim();
		} else {
			return "*";
		}
	}

	public String getName(int column) {
		return (String) attrNames.get(column);
	}

	public String getType(int column) {
		return (String) attrTypes.get(column);
	}

	public String getValue(int column) {
		return (String) attrValues.get(column);
	}

	public List<String> getPKs() {
		return null;
	}

	public int getNumAttr() {
		return totalColumns;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SearchFields attributes;
		try {
			attributes = new SearchFields("targetconfig.xml");

			System.out.println("query = " + attributes.getQuery());
			for (int i = 0; i < attributes.getNumAttr(); i++) {
				System.out.println("name = " + attributes.getName(i));
				System.out.println("index = " + attributes.getType(i));
				System.out.println("value = " + attributes.getValue(i));
			}
		} catch (ProductClassException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
