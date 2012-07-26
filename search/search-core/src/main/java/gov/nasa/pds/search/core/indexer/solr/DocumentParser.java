//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id$ 
//

package gov.nasa.pds.search.core.indexer.solr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	public static StringBuffer parse(File file) {
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
				// Need to ensure read in UTF-8 format, and override SAX input source
				InputStream stream = new FileInputStream(file);		// Get InputStream
				reader = new InputStreamReader(stream, "UTF-8");	// Specify reading content in UTF-8
				InputSource is = new InputSource(reader);			// Init input source
				is.setEncoding("UTF-8");							// Overriding SAX input source to UTF-8
				document = builder.parse(is);						// Send to parser
				
				indexDoc = retrieveMetadata(document);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				System.err.println("Error parsing date: " + file.getAbsolutePath());
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
			throws ParseException {
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
								value = configureDateTimes(value);

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
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	private static String configureDateTimes(String value)
			throws ParseException {
		// System.out.println("Configuring Datetime: "+ value);
		SimpleDateFormat newFrmt = new SimpleDateFormat(
				"yyyy-MM-dd'T'kk:mm:ss.SSS'Z'");

		// System.out.println("Input datetime: " + value);
		value = value.toUpperCase();
		value = value.replaceAll("(T|Z)", "");

		if (value.matches("PROCESSING__.*"))
			value = value.replace("PROCESSING__", "");

		int length = value.length();

		if (length < 4 || !value.matches(".*[0-9].*")) { // Invalid/null/unknown
															// datetime
			value = "3000-01-01T00:00:00Z";
		} else if (length == 4) {
			SimpleDateFormat yearFrmt = new SimpleDateFormat("yyyy");
			value = newFrmt.format(yearFrmt.parse(value));
		} else if (length == 7) {
			SimpleDateFormat yearMonthFrmt = new SimpleDateFormat("yyyy-MM");
			value = newFrmt.format(yearMonthFrmt.parse(value));
		} else if (length == 8) {
			SimpleDateFormat doyFrmt = new SimpleDateFormat("yyyy-DDD");
			value = newFrmt.format(doyFrmt.parse(value));
		} else if (length == 10) {
			SimpleDateFormat yearMonthFrmt = new SimpleDateFormat("yyyy-MM-dd");
			value = newFrmt.format(yearMonthFrmt.parse(value));
		} else if (length == 12) {
			SimpleDateFormat yearMonthFrmt = new SimpleDateFormat(
					"yyyy-MM-ddkk");
			value = newFrmt.format(yearMonthFrmt.parse(value));
		} else if (length == 15) {
			SimpleDateFormat yearMonthFrmt = new SimpleDateFormat(
					"yyyy-MM-ddkk:mm");
			value = newFrmt.format(yearMonthFrmt.parse(value));
		} else if (length == 16) {
			SimpleDateFormat doyFrmt = new SimpleDateFormat("yyyy-DDDkk:mm:ss");
			value = newFrmt.format(doyFrmt.parse(value));
		} else if (length == 18 && value.contains("-")) {
			SimpleDateFormat dateFrmt = new SimpleDateFormat(
					"yyyy-MM-ddkk:mm:ss");
			value = newFrmt.format(dateFrmt.parse(value));
		} else if (length == 18) {
			SimpleDateFormat dateFrmt = new SimpleDateFormat(
					"yyyyMMddkkmmss.SSS");
			value = newFrmt.format(dateFrmt.parse(value));
		} else if (length == 19) {
			SimpleDateFormat dateFrmt = new SimpleDateFormat(
					"yyyy-DDDkk:mm:ss.SS");
			value = newFrmt.format(dateFrmt.parse(value));
		} else if (length == 20) {
			SimpleDateFormat doyFrmt = new SimpleDateFormat(
					"yyyy-DDDkk:mm:ss.SSS");
			value = newFrmt.format(doyFrmt.parse(value));
		} else if (length == 21) {
			SimpleDateFormat msFrmt = new SimpleDateFormat(
					"yyyy-MM-ddkk:mm:ss.SS");
			value = newFrmt.format(msFrmt.parse(value));
		} else if (length == 22) {
			SimpleDateFormat msFrmt = new SimpleDateFormat(
					"yyyy-MM-ddkk:mm:ss.SSS");
			value = newFrmt.format(msFrmt.parse(value));
		} else if (length == 23) {
			SimpleDateFormat msFrmt = new SimpleDateFormat(
					"yyyy-MM-ddkk:mm:ss.SSSS");
			value = newFrmt.format(msFrmt.parse(value));
		} else {	// TODO - capture all possible time values so we can be sure to only NULL bad values
			System.err.println("ERROR: Bad Time value.");
			value = "1900-01-01T00:00:00Z";;
		}

		return value;
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
		// System.out.println(DocumentParser.retrieveMetadata(builder.parse("/Users/jpadams/dev/workspace/solr/tse/extract/target/tse_target_10001.xml")));
		System.out
				.println(DocumentParser.configureDateTimes("processing__unk"));
		System.out.println(DocumentParser
				.configureDateTimes("processing__1988-08-01"));
		System.out.println(DocumentParser.configureDateTimes("unknown"));
		System.out.println(DocumentParser.configureDateTimes("1999"));
		System.out.println(DocumentParser.configureDateTimes("2000-03"));
		System.out.println(DocumentParser.configureDateTimes("2001-04-30"));
		System.out.println(DocumentParser
				.configureDateTimes("2002-05-15t14:20"));
		System.out.println(DocumentParser
				.configureDateTimes("1994-07-20t20:16:32"));
		System.out.println(DocumentParser
				.configureDateTimes("1977-09-05t00:14z"));
		System.out.println(DocumentParser
				.configureDateTimes("1999-354t06:53:12z"));
		System.out.println(DocumentParser
				.configureDateTimes("1979-02-26T00:00:35.897"));
		System.out.println(DocumentParser
				.configureDateTimes("19851108070408.649"));
		System.out.println(DocumentParser
				.configureDateTimes("2009-06-30t00:00:00.00"));
		System.out.println(DocumentParser
				.configureDateTimes("2004-001t00:00:00.000"));
		System.out.println(DocumentParser
				.configureDateTimes("2002-02-19t19:00:29.6236"));
		System.out.println(DocumentParser
				.configureDateTimes("2001-266t10:44:29.81"));
	}

}
