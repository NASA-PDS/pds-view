package gov.nasa.arc.pds.lace.server.validation;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class DocumentCreationHandler extends DefaultHandler {

	private Document document;
	private Node currentNode;

	public DocumentCreationHandler(Document document) {
		this.document = document;
		currentNode = document;
	}

	public Document getDocument() {
		return document;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		Element element = document.createElementNS(uri, qName);
		currentNode.appendChild(element);
		currentNode = element;
		for (int i=0; i < attributes.getLength(); ++i) {
			Attr attr = document.createAttributeNS(attributes.getURI(i), attributes.getQName(i));
			attr.setValue(attributes.getValue(i));
			element.setAttributeNodeNS(attr);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		currentNode = currentNode.getParentNode();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String value = new String(ch, start, length);
		if (!value.trim().isEmpty()) {
			Text text = document.createTextNode(value);
			currentNode.appendChild(text);
		}
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		ProcessingInstruction pi = document.createProcessingInstruction(target, data);
		currentNode.appendChild(pi);
	}

}