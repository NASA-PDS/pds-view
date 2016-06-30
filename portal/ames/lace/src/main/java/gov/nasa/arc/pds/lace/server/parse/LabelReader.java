package gov.nasa.arc.pds.lace.server.parse;

import gov.nasa.arc.pds.lace.server.IDGenerator;
import gov.nasa.arc.pds.lace.server.schema.SchemaManager;
import gov.nasa.arc.pds.lace.shared.AttributeItem;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.InsertionPoint.DisplayType;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;
import gov.nasa.arc.pds.lace.shared.WildcardType;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Implements a class that reads a label and correlates it with
 * a model to generate the model objects that correspond to the
 * current state of the label.
 */
public class LabelReader {

	private SchemaManager schemaManager;
	private LabelParser parser;
	private IDGenerator idFactory;

	/**
	 * Creates a new instance of the label reader.
	 *
	 * @param parser the label parser
	 * @param idFactory a factory for generating unique IDs
	 */
	@Inject
	public LabelReader(LabelParser parser, IDGenerator idFactory) {
		this.parser = parser;
		this.idFactory = idFactory;
	}

	/**
	 * Sets the schema manager to use for subsequence parsing and model analysis.
	 *
	 * @param manager the schema manager
	 */
	public void setSchemaManager(SchemaManager manager) {
		this.schemaManager = manager;
	}

	/**
	 * Reads a label from an input stream and returns the model content
	 * corresponding to the label contents.
	 *
	 * @param in the input stream
	 * @return the model contents corresponding to the label contents
	 * @throws ParserConfigurationException if there is an error configuring the XML parser
	 * @throws SAXException if there is an error parsing the XML
	 * @throws IOException if there is an I/O error reading the label
	 */
	public Container readLabel(InputStream in) throws ParserConfigurationException, SAXException, IOException {
		Document doc = parser.parse(schemaManager, in);
		Element root = doc.getDocumentElement();

		ModelAnalyzer analyzer = schemaManager.getAnalyzer();
		Container container = analyzer.getContainerForElement(root.getLocalName(), root.getNamespaceURI(), false);
		matchContainer(root, container);
		analyzer.expandInsertionPoints(container);
		return container;
	}

	private void matchContainer(Element e, Container container) {
		copyAttributes(e, container);

		NodeList nodes = e.getChildNodes();
		int docIndex = 0;
		ListIterator<LabelItem> contentIt = container.getContents().listIterator();
		LabelItem curItem = (contentIt.hasNext() ? contentIt.next() : null);

		// While we're not at the end of either list.
		while (docIndex < nodes.getLength() && curItem != null) {
			Node node = nodes.item(docIndex);
			if (!(node instanceof Element)) {
				copyNonElementData(node, container);
				++docIndex;
			} else {
				Element child = (Element) node;

				if (matchItem(child, curItem)) {
					curItem = processMatch(child, contentIt, curItem);
					++docIndex;
				} else if (!curItem.isRequired() || matchItem(child, peekItem(contentIt))) {
					// Optional element or matches next item, skip it.
					curItem = nextItem(contentIt);
				} else {
					// Item mismatch.
					raiseMismatch(child, curItem);
				}
			}
		}

		// Merge the insertion points, if any, in the updated contents list.
		ModelAnalyzer analyzer = schemaManager.getAnalyzer();
		analyzer.mergeInsertionPoints(container.getContents());

		// No more content items. If we have more document elements, then
		// there is unmatched content.

		// Skip any empty text nodes.
		while (docIndex < nodes.getLength()) {
			Node node = nodes.item(docIndex);
			if ((node instanceof Text) && node.getTextContent().trim().isEmpty()) {
				// ignore
			} else if (node instanceof Comment) {
				// ignore
			} else if (node instanceof ProcessingInstruction) {
				// ignore
			} else {
				break;
			}
			++docIndex;
		}

		if (docIndex < nodes.getLength()) {
			// Unmatched document elements.
			Node node = nodes.item(docIndex);
			throw new IllegalArgumentException("Unmatched document nodes begininning with " + node.getNodeName());
		}
	}

	private LabelItem nextItem(ListIterator<LabelItem> it) {
		if (it.hasNext()) {
			return it.next();
		} else {
			return null;
		}
	}

	private LabelItem peekItem(ListIterator<LabelItem> it) {
		if (!it.hasNext()) {
			return null;
		}

		LabelItem next = it.next();
		it.previous();
		return next;
	}

	private boolean matchItem(Element e, LabelItem curItem) {
		if (curItem == null) {
			return false;
		} else if (curItem instanceof InsertionPoint) {
			return matchInsertionPoint(e, (InsertionPoint) curItem);
		} else {
			return matchLabelElement(e, (LabelElement) curItem);
		}
	}

	private LabelItem processMatch(Element e, ListIterator<LabelItem> it, LabelItem curItem) {
		if (curItem instanceof InsertionPoint) {
			return processInsertionPointMatch(e, it, (InsertionPoint) curItem);
		} else {
			processLabelElementMatch(e, (LabelElement) curItem);
			return nextItem(it);
		}
	}

	private void raiseMismatch(Element e, LabelItem item) {
		if (item instanceof InsertionPoint) {
			raiseInsertionPointMismatch(e, (InsertionPoint) item);
		} else {
			raiseLabelElementMismatch(e, (LabelElement) item);
		}
	}

	private boolean matchLabelElement(Element e, LabelElement curItem) {
		LabelItemType type = curItem.getType();

		return (e.getLocalName().equals(type.getElementName()) && e.getNamespaceURI().equals(type.getElementNamespace()));
	}

	private void processLabelElementMatch(Element e, LabelElement curItem) {
		copyAttributes(e, curItem);

		if (curItem instanceof SimpleItem) {
			processSimpleItemMatch(e, (SimpleItem) curItem);
		} else if (curItem instanceof Container) {
			processContainerMatch(e, (Container) curItem);
		} else {
			throw new IllegalArgumentException("Unepxected LabelItem in model (" + curItem.getClass().getName() + ")");
		}
	}

	private void raiseLabelElementMismatch(Element e, LabelElement item) {
		throw new IllegalArgumentException("Element does not match expected tag name"
				+ " (" + e.getLocalName() + "!=" + item.getType().getElementName() + ")");
	}

	private void processSimpleItemMatch(Element e, SimpleItem item) {
		item.setValue(e.getTextContent());
	}

	private void processContainerMatch(Element e, Container item) {
		matchContainer(e, item);
	}

	private boolean matchInsertionPoint(Element e, InsertionPoint item) {
		return findAlternative(e.getLocalName(), e.getNamespaceURI(), item) >= 0;
	}

	private LabelItem processInsertionPointMatch(Element e, ListIterator<LabelItem> it, InsertionPoint insPoint) {
		List<InsertOption> alternatives = insPoint.getAlternatives();
		int alternativeIndex = findAlternative(e.getLocalName(), e.getNamespaceURI(), insPoint);
		assert alternativeIndex >= 0;

		InsertOption alternative = alternatives.get(alternativeIndex);
		LabelItemType type = null;
		for (LabelItemType itemType : alternative.getTypes()) {
			if (tagNameMatchesType(e.getLocalName(), e.getNamespaceURI(), itemType)) {
				type = itemType;
			}
		}

		LabelElement newElement =
				(type instanceof WildcardType)
				? schemaManager.getAnalyzer().getItemForElement(e.getLocalName(), e.getNamespaceURI(), false)
				: createLabelElement(type);

		newElement.setInsertOption(alternative);
		if (newElement instanceof Container) {
			matchContainer(e, (Container) newElement);
		} else if (newElement instanceof SimpleItem) {
			processSimpleItemMatch(e, (SimpleItem) newElement);
		} else {
			throw new IllegalArgumentException("Unexpected label item type: " + newElement.getClass().getName());
		}

		if (!insPoint.getDisplayType().equals(DisplayType.ANY.getDisplayType())) {
			insPoint.setDisplayType(InsertionPoint.DisplayType.PLUS_BUTTON.getDisplayType());
		}
		alternative.setUsedOccurrences(alternative.getUsedOccurrences() + 1);

		InsertionPoint newInsertionPoint = insPoint.copy(false);
		it.add(newElement);
		it.add(newInsertionPoint);

		// If the type is unbounded, stay on the insertion point, else skip ahead.
		if (type.getMaxOccurrences() != 1) {
			return newInsertionPoint;
		} else {
			return nextItem(it);
		}
	}

	private void raiseInsertionPointMismatch(Element e, InsertionPoint item) {
		StringBuilder builder = new StringBuilder();
		for (InsertOption alternative : item.getAlternatives()) {
			for (LabelItemType type : alternative.getTypes()) {
				if (builder.length() > 0) {
					builder.append(' ');
				}
				builder.append(type.getElementName());
			}
		}
		builder.append("})");
		throw new IllegalArgumentException(
				"Cannot insert element ("
				+ e.getLocalName()
				+ " not in {"
				+ builder.toString()
				+ "})"
		);
	}

	// TODO: replace the calls to this method with the same method in ModelAnalyzer
	private LabelElement createLabelElement(LabelItemType type) {
		if (!type.isComplex()) {
			SimpleItem item = new SimpleItem();
			item.setID(idFactory.getID());
			item.setType(type);
			item.setValue("");
			return item;
		} else {
			Container item = new Container();
			item.setID(idFactory.getID());
			item.setType(type);
			ModelAnalyzer analyzer = schemaManager.getAnalyzer();
			analyzer.setInitialContents(item);
			return item;
		}
	}

	private int findAlternative(String tagName, String namespace, InsertionPoint item) {
		for (int i=0; i < item.getAlternatives().size(); i++) {
			for (LabelItemType type : item.getAlternatives().get(i).getTypes()) {
				if (tagNameMatchesType(tagName, namespace, type)) {
					return i;
				}
			}
		}

		return -1;
	}

	private boolean tagNameMatchesType(String tagName, String namespace, LabelItemType type) {
		if (type instanceof WildcardType) {
			return namespaceMatchesWildcard(namespace, (WildcardType) type);
		} else {
			return type.getElementName().equals(tagName) && type.getElementNamespace().equals(namespace);
		}
	}

	private boolean namespaceMatchesWildcard(String namespace, WildcardType type) {
		return type.getNamespaces().contains(namespace);
	}

	private void copyAttributes(Element e, LabelElement labelElement) {
		NamedNodeMap nodeMap = e.getAttributes();
		for (int i=0; i < nodeMap.getLength(); ++i) {
			Attr attr = (Attr) nodeMap.item(i);
			copyAttribute(attr, labelElement);
		}
	}

	private void copyAttribute(Attr attrNode, LabelElement labelElement) {
		for (AttributeItem attribute : labelElement.getAttributes()) {
			LabelItemType type = attribute.getType();
			if (attributeNamesMatch(attrNode.getNamespaceURI(), attrNode.getName(), type.getElementNamespace(), type.getElementName())) {
				attribute.setValue(attrNode.getNodeValue());
			}
		}

		// If we get here, we didn't find a match. Ignore, for now, since
		// XML has attributes that are always allowed.
	}

	private boolean attributeNamesMatch(String ns1, String name1, String ns2, String name2) {
		// We ignore the namespace, for now.
		return (name1!=null && name1.equals(name2));
	}

	private void copyNonElementData(Node n, Container c) {
		//TODO: Anything to do here?
	}

}
