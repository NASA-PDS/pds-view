/**
 * Copyright (c) 2009, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 * 
 * $Id$
 *  
 */
package gov.nasa.pds.tse.catalog.extractor;

import java.io.File;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author jpadams
 * @version $Revision$
 * 
 */
public class DBInfo {

	private Document doc;
	private Properties dbProps;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
    public DBInfo(File configFile) {
    	try {
    		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    		
    		doc = docBuilder.parse(configFile);

    		// normalize text representation
    		doc.getDocumentElement().normalize ();
    		log.fine("Root element of the doc is " + doc.getDocumentElement().getNodeName());
    		
    		dbProps = new Properties();
    		setProperties();
    	}catch (SAXParseException err) {
    		System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
    		System.out.println(" " + err.getMessage ());

    	}catch (SAXException e) {
    		Exception x = e.getException ();
    		((x == null) ? e : x).printStackTrace ();

    	}catch (Throwable t) {
    		t.printStackTrace ();
    	}

    }	

    public String getDriver() {
    	return ((Node)doc.getElementsByTagName("driver").item(0).getChildNodes().item(0)).getNodeValue().trim();
    }
    
    public String getUrl() {
		return ((Node)doc.getElementsByTagName("url").item(0).getChildNodes().item(0)).getNodeValue().trim();
    }
    
    public String getPropValue(String tagName) {
		return ((Node)doc.getElementsByTagName(tagName).item(0).getChildNodes().item(0)).getNodeValue().trim();
    }
    
    public void setProperties() {
    	String propName;
    	
    	// Gets all of the child nodes of the root.
    	NodeList propList = doc.getDocumentElement().getChildNodes();
    	for (int i=0; i<propList.getLength(); i++) {
    		Node propNode = propList.item(i);
    		
    		if (propNode.getNodeType() == Node.ELEMENT_NODE) {
    			propName = propNode.getNodeName();
    			if (!propName.equals("driver") || !propName.equals("url")) {
    				dbProps.setProperty(propName,getPropValue(propName));
    			}
    		}
    	}
    }
    
     public Properties getProperties() {
    	 return dbProps;
     }

 	public static void main(String [] args) throws Exception {
		DBInfo reader = new DBInfo(new File(System.getProperty("user.home") + "/dbconfig.xml"));
		System.out.println("driver = " + reader.getDriver());
	}

}
