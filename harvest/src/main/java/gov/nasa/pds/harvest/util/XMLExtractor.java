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

import gov.nasa.pds.harvest.constants.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import net.sf.saxon.xpath.XPathEvaluator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class to extract data from an XML file.
*/
public class XMLExtractor {
    /** The DOM source. */
    private DOMSource xml = null;

    /** The XPath evaluator object. */
    private XPathEvaluator xpath = null;

    /** Default namespace uri */
    private static String defaultNamespaceUri = Constants.PDS_NAMESPACE;

    /** Namespace Context */
    private static PDSNamespaceContext namespaceContext = null;

    /**
     * Constructor.
     *
     */
    public XMLExtractor() {
        xpath = new XPathEvaluator();
        xpath.getStaticContext().setDefaultElementNamespace(
                defaultNamespaceUri);
        if(namespaceContext != null) {
            xpath.getStaticContext().setNamespaceContext(namespaceContext);
        }
    }

    /**
     * Parse the given file.
     *
     * @param src An XML file.
     *
     * @throws SAXException If the given file had errors.
     * @throws IOException If the given file could not be read.
     * @throws ParserConfigurationException If an error occurred while parsing.
     */
    public void parse(File src) throws SAXException, IOException,
    ParserConfigurationException {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(src);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            builder.setErrorHandler(new XMLErrorHandler());
            Node doc = builder.parse(stream);
            xml = new DOMSource(doc);
        } finally {
            stream.close();
        }
    }

    /**
     * Parse the given file.
     *
     * @param src An XML file.
     *
     * @throws SAXException If the given file had errors.
     * @throws IOException If the given file could not be read.
     * @throws ParserConfigurationException If an error occurred while parsing.
     */
    public void parse(String src) throws SAXException, IOException,
    ParserConfigurationException {
        parse(new File(src));
    }

    /**
     * Sets the default namespace URI.
     *
     * @param uri A URI.
     */
    public static void setDefaultNamespace(String uri) {
        //xpath.getStaticContext().setDefaultElementNamespace(uri);
        defaultNamespaceUri = uri;
    }

    /**
     * Sets the Namespace Context to support handling of namespaces
     * in XML documents.
     *
     * @param context The NamespaceContext object.
     */
    public static void setNamespaceContext(PDSNamespaceContext context) {
        //xpath.getStaticContext().setNamespaceContext(context);
        namespaceContext = context;
    }

    /**
     * Gets the value of the given expression.
     *
     * @param expression An XPath expression.
     *
     * @return The resulting value or null if nothing was found.
     *
     * @throws XPathExpressionException If the given expression was malformed.
     */
    public String getValueFromDoc(String expression)
    throws XPathExpressionException {
        return getValueFromItem(expression, xml);
    }

    /**
     * Gets the value of the given expression.
     *
     * @param expression An XPath expression.
     * @param item The starting point from which to evaluate the
     * XPath expression.
     *
     * @return The resulting value or null if nothing was found.
     *
     * @throws XPathExpressionException If the given expression was malformed.
     */
    public String getValueFromItem(String expression, Object item)
    throws XPathExpressionException {
        return xpath.evaluate(expression, item);
    }

    /**
     * Gets a Node object from the given expression.
     *
     * @param expression An XPath expression.
     *
     * @return A Node object.
     *
     * @throws XPathExpressionException If the given expression was malformed.
     */
    public Node getNodeFromDoc(String expression)
    throws XPathExpressionException {
        return getNodeFromItem(expression, xml);
    }

    /**
     * Gets a Node object from the given expression.
     *
     * @param expression An XPath expression.
     * @param item The starting point from which to evaluate the
     * XPath expression.
     *
     * @return A Node object.
     *
     * @throws XPathExpressionException If the given expression was malformed.
     */
    public Node getNodeFromItem(String expression, Object item)
    throws XPathExpressionException {
        return (Node) xpath.evaluate(expression, item, XPathConstants.NODE);
    }

    /**
     * Gets the values of the given expression.
     *
     * @param expression An XPath expression.
     *
     * @return The resulting values or null if nothing was found.
     *
     * @throws XPathExpressionException If the given expression was malformed.
     */
    public List<String> getValuesFromDoc(String expression)
    throws XPathExpressionException {
        return getValuesFromItem(expression, xml);
    }

    /**
     * Gets the values of the given expression.
     *
     * @param expression An XPath expression.
     * @param item The starting point from which to evaluate the
     * XPath expression.
     *
     * @return The resulting values or null if nothing was found.
     *
     * @throws XPathExpressionException If the given expression was malformed.
     */
    public List<String> getValuesFromItem(String expression, Object item)
    throws XPathExpressionException {
        List<String> vals = new ArrayList<String>();
        NodeList nList = (NodeList) xpath.evaluate(expression, item,
                XPathConstants.NODESET);
        if (nList != null) {
            for (int i = 0, sz = nList.getLength(); i < sz; i++) {
                Node aNode = nList.item(i);
                vals.add(aNode.getTextContent());
            }
        }
        return vals;
    }

    /**
     * Gets the document node of the XML document.
     *
     * @return The Document Node.
     */
    public Node getDocNode() {
        Document doc = (Document) xml.getNode();
        return doc.getDocumentElement();
    }

    /**
     * Gets Node objects from the given expression.
     *
     * @param expression An XPath expression.
     *
     * @return A NodeList object.
     *
     * @throws XPathExpressionException If the given expression was malformed.
     */
    public NodeList getNodesFromDoc(String expression)
    throws XPathExpressionException {
        return getNodesFromItem(expression, xml);
    }

    /**
     * Gets Node objects from the given expression.
     *
     * @param expression An XPath expression.
     * @param item The starting point from which to evaluate the
     * XPath expression.
     *
     * @return A NodeList object.
     *
     * @throws XPathExpressionException If the given expression was malformed.
     */
    public NodeList getNodesFromItem(String expression, Object item)
    throws XPathExpressionException {
        return (NodeList) xpath.evaluate(
                expression, item, XPathConstants.NODESET);
    }
}
