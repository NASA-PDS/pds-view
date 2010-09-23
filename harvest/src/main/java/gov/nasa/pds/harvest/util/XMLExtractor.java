// Copyright 2006-2010, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.harvest.util;

import gov.nasa.pds.harvest.policy.Namespace;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import net.sf.saxon.xpath.XPathEvaluator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author pramirez
*/
public class XMLExtractor {
    private DocumentBuilder builder = null;
    private DOMSource xml = null;
    private XPathEvaluator xpath = null;

    public XMLExtractor()
    throws ParserConfigurationException, XPathFactoryConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        builder = dbf.newDocumentBuilder();
        builder.setErrorHandler(new XMLErrorHandler());
        xpath = new XPathEvaluator();
    }

    public XMLExtractor(File src)
    throws ParserConfigurationException, XPathFactoryConfigurationException,
    SAXException, IOException {
        this();
        Node doc = builder.parse(new InputSource(src.toURI().toString()));
        xml = new DOMSource(doc);
    }

    public XMLExtractor(String src)
    throws ParserConfigurationException, SAXException, IOException,
    XPathFactoryConfigurationException {
        this(new File(src));
    }

    public void setDefaultNamespace(String uri) {
        xpath.getStaticContext().setDefaultElementNamespace(uri);
    }

    public void setNamespaceContext(NamespaceContext context) {
        xpath.getStaticContext().setNamespaceContext(context);
    }

    public void validate(String schema) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema s = factory.newSchema(new StreamSource(new File(schema)));
        Validator validator = s.newValidator();
        validator.validate(xml);
    }

    public String getValueFromDoc(String expression)  throws XPathExpressionException {
        return this.getValueFromItem(expression, xml);
    }

    public String getValueFromItem(String expression, Object item) throws XPathExpressionException {
        return xpath.evaluate(expression, item);
    }

    public Node getNodeFromDoc(String expression) throws XPathExpressionException {
        return this.getNodeFromItem(expression, xml);
    }

    public Node getNodeFromItem(String expression, Object item)  throws XPathExpressionException {
        return (Node) xpath.evaluate(expression, item, XPathConstants.NODE);
    }

    public List<String> getValuesFromDoc(String expression) throws XPathExpressionException {
        return this.getValuesFromItem(expression, xml);
    }

    public List<String> getValuesFromItem(String expression, Object item) throws XPathExpressionException {
        List<String> vals = new ArrayList<String>();
        NodeList nList = (NodeList) xpath.evaluate(expression, item,  XPathConstants.NODESET);
        if (nList != null) {
            for (int i = 0, sz = nList.getLength(); i < sz; i++) {
                Node aNode = nList.item(i);
                vals.add(aNode.getTextContent());
            }
        }
        return vals;
    }

    public Node getDocNode() {
        Document doc = (Document) this.xml.getNode();
        return doc.getDocumentElement();
    }

    public NodeList getNodesFromDoc(String expression) throws XPathExpressionException {
        return this.getNodesFromItem(expression, xml);
    }

    public NodeList getNodesFromItem(String expression, Object item) throws XPathExpressionException {
        return (NodeList) xpath.evaluate(expression, item, XPathConstants.NODESET);
    }

    public String getValue(String expression, String xmlStr) throws XPathExpressionException, SAXException, IOException {
        String val = null;
        Document doc = builder.parse(new ByteArrayInputStream(xmlStr.getBytes()));
        Node aNode = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
        if (aNode != null) {
            val = aNode.getTextContent();
        }
        return val;
    }

    public static void main(String[] args) {
        Namespace ns = new Namespace();
        ns.setPrefix("pds");
        ns.setUri("http://pds.nasa.gov/schema/pds4/pds");
        List<Namespace> nSpaces = new ArrayList<Namespace>();
        nSpaces.add(ns);
        try {
            XMLExtractor xe = new XMLExtractor(args[0]);
            xe.setDefaultNamespace("http://pds.nasa.gov/schema/pds4/pds");
            xe.setNamespaceContext(new PDSNamespaceContext(nSpaces));
            System.out.println("ROOT: " + xe.getDocNode().getNodeName());
            String value = "";
            String[] expressions = {
                    "//Product_Identification_Area/logical_identifier",
                    "//pds:Product_Identification_Area/pds:logical_identifier",
                    "//pds:Product_Identification_Area/logical_identifier",
                    "//*[substring(name(),string-length(name()) - string-length('Identification_Area') + 1) = 'Identification_Area']/logical_identifier",
                    "//pds:*[substring(name(),string-length(name()) - string-length('Identification_Area') + 1) = 'Identification_Area']/pds:logical_identifier"
                    };
            for(int i=0; i < expressions.length; i++) {
                value = xe.getValueFromDoc(expressions[i]);
                System.out.println("EXPRESSION: " + expressions[i]);
                System.out.println("VALUE: " + value + "\n");
            }
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XPathFactoryConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
