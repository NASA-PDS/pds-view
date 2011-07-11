package gov.nasa.pds.imaging.generation.util;

import gov.nasa.pds.imaging.generation.TemplateException;
import gov.nasa.pds.imaging.generation.generate.elements.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtil {
	
	public static Map<String, Class<?>> getGeneratedMappings(String file, String key, String value) throws TemplateException, Exception {
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();
		
	    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true);
	    
	    //try {
		    DocumentBuilder builder = domFactory.newDocumentBuilder();
		    Document doc = builder.parse(file);
	
	    	NodeList contexts = doc.getElementsByTagName(key);
	    	NodeList classes = doc.getElementsByTagName(value);
    	
	    	for (int i = 0; i < contexts.getLength(); i++) {
	    		map.put(contexts.item(i).getTextContent(), Class.forName(classes.item(i).getTextContent()));
	    		//map.put(contexts.item(i).getTextContent(), Md5Checksum.class);
	    	}
	    /*} catch (ClassNotFoundException e) {
	 		throw new TemplateException("Mappings class not found.");
	    }*/
    	
    	return map;
	}
	
	public static List<String> getClassList(String file, String tag) throws TemplateException, Exception {
		List<String> classList = new ArrayList<String>();
		
	    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true);

	    DocumentBuilder builder = domFactory.newDocumentBuilder();
	    Document doc = builder.parse(file);

    	NodeList classes = doc.getElementsByTagName(tag);
	
    	for (int i = 0; i < classes.getLength(); i++) {
    		classList.add(classes.item(i).getTextContent());
    	}
    	
    	return classList;
	}
	
    public static void main(String[] args) {
    	try {
    		XMLUtil.getGeneratedMappings("src/main/resources/conf/generated-mappings.xml", "context", "class");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    }
}
