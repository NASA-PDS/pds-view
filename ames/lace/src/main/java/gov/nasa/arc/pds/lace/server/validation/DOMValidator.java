package gov.nasa.arc.pds.lace.server.validation;

import gov.nasa.arc.pds.lace.server.schema.SchemaManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.TransformerException;
import javax.xml.validation.ValidatorHandler;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Implements a method to validate an XML document represented
 * as a DOM tree. Errors encountered during the validation are
 * added as attributes on the DOM tree elements.
 */
public class DOMValidator {
	
	/**
	 * Validates a document
	 * 
	 * @param schemaManager the schema manager
	 * @param document the document to be validated
	 * @throws SAXException if there is an error parsing the document
	 * @throws TransformerException if there is an error creating the XML transformer 
	 * @throws XPathExpressionException if there is an error using XPath expressions
	 */
	public void validate(SchemaManager schemaManager, Document document, FailureHandler failureHandler) throws SAXException, XPathExpressionException, TransformerException {
		ValidatorHandler validatorHandler = schemaManager.getSchema().newValidatorHandler();
		ErrorCapturer capturer = new ErrorCapturer(failureHandler);
		validatorHandler.setErrorHandler(capturer);
		walkNode(document, validatorHandler, capturer);
		
		// Find the set of namespaces used, in order to determine which Schematron
		// rules to validate against.
		Set<String> namespaces = new HashSet<String>();
		findNamespaces(document.getDocumentElement(), namespaces);
		for (String ns : namespaces) {
			SchematronValidator schematronValidator = schemaManager.getSchematronValidator(ns);
			if (schematronValidator != null) {
				schematronValidator.findErrors(document, failureHandler);
			}
		}
	}
	
	private void findNamespaces(Element element, Set<String> namespaces) {
		namespaces.add(element.getNamespaceURI());
		
		NodeList children = element.getChildNodes();
		for (int i=0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child instanceof Element) {
				findNamespaces((Element) child, namespaces);
			}
		}
	}

	private void walkNode(Node node, ContentHandler handler, ErrorCapturer capturer) throws SAXException {
		if (node instanceof Document) {
			handler.startDocument();
			walkChildren(node, handler, capturer);
			handler.endDocument();
			capturer.captureMessages(node);
		} else if (node instanceof Comment) {
			// ignore
		} else if (node instanceof CharacterData) {
			CharacterData text = (CharacterData) node;
			char[] chars = text.getNodeValue().toCharArray();
			handler.characters(chars, 0, chars.length);
		} else if (node instanceof ProcessingInstruction) {
			ProcessingInstruction pi = (ProcessingInstruction) node;
			handler.processingInstruction(pi.getTarget(), pi.getData());
		} else if (node instanceof Element) {
			Element e = (Element) node;
			AttributesImpl attributes = new AttributesImpl();
			
			NamedNodeMap map = e.getAttributes();
			for (int i=0; i < map.getLength(); ++i) {
				Attr attr = (Attr) map.item(i);
				attributes.addAttribute(attr.getNamespaceURI(), attr.getLocalName(), attr.getNodeName(), "", attr.getNodeValue());
			}

			handler.startElement(e.getNamespaceURI(), e.getLocalName(), e.getNodeName(), attributes);
			walkChildren(e, handler, capturer);
			handler.endElement(e.getNamespaceURI(), e.getLocalName(), e.getNodeName());
			capturer.captureMessages(node);
		} else {
			System.err.println("Unknown node type in DOM tree: " + node.getClass().getName());
		}
	}

	private void walkChildren(Node node, ContentHandler handler, ErrorCapturer capturer) throws SAXException {
		NodeList nodes = node.getChildNodes();
		for (int i=0; i < nodes.getLength(); ++i) {
			walkNode(nodes.item(i), handler, capturer);
		}
	}
	
	private static class ErrorCapturer implements ErrorHandler {

		private List<String> pendingMessages = new ArrayList<String>();
		private FailureHandler failureHandler;
		
		public ErrorCapturer(FailureHandler handler) {
			failureHandler = handler;
		}
		
		@Override
		public void error(SAXParseException ex) throws SAXException {
			addError(ex);
		}

		@Override
		public void fatalError(SAXParseException ex) throws SAXException {
			addError(ex);
		}

		@Override
		public void warning(SAXParseException ex) throws SAXException {
			addError(ex);
		}
		
		private void addError(SAXParseException ex) {
			pendingMessages.add(ex.getMessage());
		}
		
		public void captureMessages(Node node) {
			for (String message : pendingMessages) {
				failureHandler.handleFailure(node, message);
			}
			pendingMessages = new ArrayList<String>();
		}
		
	}
	
}
