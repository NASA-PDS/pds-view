package gov.nasa.arc.pds.lace.server.parse;

import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.io.OutputStream;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 * Implements a facility for writing model objects to an output
 * stream as a fully-formed XML document.
 */
public class LabelWriter {

	/** The DOM implementation feature string for the load-save implementation. */
	private static final String LOAD_SAVE_FEATURE = "LS";

	private SchemaLoader loader;
	private DOMImplementationLS ls;

	/**
	 * Creates a new instance of the label writer, with given schema
	 * URIs.
	 *
	 * @param schemaURIs the URIs to the XML schemas
	 * @throws SAXException if there is an error reading the schemas
	 * @throws IllegalAccessException if there is an error getting the load-save implementation
	 * @throws InstantiationException if there is an error getting the load-save implementation
	 * @throws ClassNotFoundException if there is an error getting the load-save implementation
	 * @throws ClassCastException if there is an error getting the load-save implementation
	 */
	public LabelWriter(URI... schemaURIs) throws SAXException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		loader = new SchemaLoader(schemaURIs);

		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
		ls = (DOMImplementationLS) registry.getDOMImplementation(LOAD_SAVE_FEATURE);
	}

	/**
	 * Writes a model to an output stream as an XML document.
	 *
	 * @param root the root item in the model
	 * @param out the output stream
	 * @throws ParserConfigurationException if there is an error configuring the document builder
	 */
	public void writeLabel(LabelItem root, OutputStream out) throws ParserConfigurationException {
		DocumentBuilder builder = loader.getDocumentBuilder();
		Document doc = builder.newDocument();

		copyItem(root, doc, doc);

		LSOutput destination = ls.createLSOutput();
		destination.setByteStream(out);
		LSSerializer serializer = ls.createLSSerializer();
		serializer.getDomConfig().setParameter("format-pretty-print", true);
		serializer.write(doc, destination);
	}

	private void copyItem(LabelItem item, Node parent, Document doc) {
		if (item instanceof LabelElement) {
			copyLabelElement((LabelElement) item, parent, doc);
		}
	}

	private void copyLabelElement(LabelElement item, Node parent, Document doc) {
		LabelItemType type = item.getType();

		Element e = doc.createElementNS(type.getElementNamespace(), type.getElementName());
		copyAttributes(item, e);

		if (item instanceof SimpleItem) {
			copySimpleItem((SimpleItem) item, e, parent);
		} else if (item instanceof Container) {
			copyContainer((Container) item, e, doc);
			parent.appendChild(e);
		} else {
			throw new IllegalArgumentException("Unexpected LabelElement subtype: " + item.getClass().getName());
		}
	}

	private void copySimpleItem(SimpleItem item, Element e, Node parent) {
		// Only copy the item if it's required or has non-empty content.
		String value = (item.getValue() != null ? item.getValue() : "");

		if (!value.trim().isEmpty()
				|| item.getType().getMinOccurrences() > 0) {
			e.setTextContent(value);
			parent.appendChild(e);
		}
	}

	private void copyContainer(Container container, Node parent, Document doc) {
		for (LabelItem item : container.getContents()) {
			copyItem(item, parent, doc);
		}
	}

	private void copyAttributes(LabelElement item, Element e) {
		//TODO: Decide how to handle attributes.
	}

}
