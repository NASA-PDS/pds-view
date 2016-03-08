package gov.nasa.arc.pds.lace.server.parse;

import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

	private LabelParser parser;
	private ModelAnalyzer analyzer;

	/**
	 * Creates a new instance of the label reader, with given schema
	 * paths.
	 *
	 * @param schemaPaths the paths to the XML schema documents
	 * @throws SAXException if there is an error reading the schemas
	 */
	public LabelReader(String... schemaPaths) throws SAXException {
		parser = new LabelParser(schemaPaths);
		List<URI> uris = new ArrayList<URI>();
		for (String path : schemaPaths) {
			uris.add(new File(path).toURI());
		}
		analyzer = new ModelAnalyzer(uris.toArray(new URI[uris.size()]));
	}

	/**
	 * Creates a new instance of the label reader, with given schema
	 * document URIs.
	 *
	 * @param schemaURIs an array of URIs to the XML schema documents
	 * @throws SAXException if there is an error reading the schemas
	 */
	public LabelReader(URI... schemaURIs) throws SAXException {
		parser = new LabelParser(schemaURIs);
		analyzer = new ModelAnalyzer(schemaURIs);
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
		Document doc = parser.parse(in);
		Element root = doc.getDocumentElement();

		Container content = analyzer.getContainerForElement(root.getTagName(), root.getNamespaceURI());
		matchContainer(root, content);
		return content;
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
				} else if (!curItem.isRequired()) {
					// Optional element, skip it.
					curItem = nextItem(contentIt);
				} else {
					// Item mismatch.
					raiseMismatch(child, curItem);
				}
			}
		}

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

	private boolean matchItem(Element e, LabelItem curItem) {
		if (curItem instanceof InsertionPoint) {
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
		return (e.getTagName().equals(curItem.getType().getElementName()));
	}

	private void processLabelElementMatch(Element e, LabelElement curItem) {
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
				+ " (" + e.getTagName() + "!=" + item.getType().getElementName() + ")");
	}

	private void processSimpleItemMatch(Element e, SimpleItem item) {
		item.setValue(e.getTextContent());
	}

	private void processContainerMatch(Element e, Container item) {
		matchContainer(e, item);
	}


	private boolean matchInsertionPoint(Element e, InsertionPoint item) {
		return findAlternative(e.getTagName(), item) >= 0;
	}

	private LabelItem processInsertionPointMatch(Element e, ListIterator<LabelItem> it, InsertionPoint insPoint) {
		int alternativeIndex = findAlternative(e.getTagName(), insPoint);
		assert alternativeIndex >= 0;

		LabelItemType type = insPoint.getAlternatives().get(alternativeIndex);

		InsertionPoint newInsertionPoint = (InsertionPoint) insPoint.copy();

		insPoint.setInsertLast(alternativeIndex);
		insPoint.setUsedAfter(alternativeIndex);

		LabelElement newElement = createLabelElement(type);
		if (newElement instanceof Container) {
			matchContainer(e, (Container) newElement);
		} else if (newElement instanceof SimpleItem) {
			processSimpleItemMatch(e, (SimpleItem) newElement);
		} else {
			throw new IllegalArgumentException("Unexpected label item type: " + newElement.getClass().getName());
		}

		// If this is an optional element insertion point with max occurrences of 1,
		// just add the container to the list. Otherwise, change the display type of
		// the insertion point to "plus_button" and split it.
		String newDisplayType = insPoint.getDisplayType();
		if (insPoint.getDisplayType().equals(InsertionPoint.DisplayType.OPTIONAL.getDisplayType())) {
			if (type.getMaxOccurrences() == 1) {
				it.set(newElement);
				return nextItem(it);
			}

			newDisplayType = InsertionPoint.DisplayType.PLUS_BUTTON.getDisplayType();
		} else if (insPoint.getDisplayType().equals(InsertionPoint.DisplayType.CHOICE.getDisplayType())) {
			newDisplayType = InsertionPoint.DisplayType.PLUS_BUTTON.getDisplayType();
		}

		insPoint.setDisplayType(newDisplayType);
		newInsertionPoint.setDisplayType(newDisplayType);

		it.add(newElement);

		newInsertionPoint.setInsertFirst(alternativeIndex);
		newInsertionPoint.setUsedBefore(alternativeIndex);
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
		for (LabelItemType alternative : item.getAlternatives()) {
			if (builder.length() > 0) {
				builder.append(' ');
			}
			builder.append(alternative.getElementName());
		}
		builder.append("})");
		throw new IllegalArgumentException(
				"Cannot insert element ("
				+ e.getTagName()
				+ " not in {"
				+ builder.toString()
				+ "})"
		);
	}

	private LabelElement createLabelElement(LabelItemType type) {
		if (!type.isComplex()) {
			SimpleItem item = new SimpleItem();
			item.setType(type);
			item.setValue("");
			return item;
		} else {
			Container item = new Container();
			item.setType(type);
			setInitialContents(item);

			return item;
		}
	}

	private void setInitialContents(Container container) {
		container.setContents(new ArrayList<LabelItem>());

		for (LabelItem item : container.getType().getInitialContents()) {
			LabelItem newItem = item.copy();

			if (newItem instanceof Container) {
				setInitialContents((Container) newItem);
			}
			container.getContents().add(newItem);
		}
	}

	private int findAlternative(String tagName, InsertionPoint item) {
		for (int i=0; i < item.getAlternatives().size(); ++i) {
			if (item.getAlternatives().get(i).getElementName().equals(tagName)) {
				return i;
			}
		}

		return -1;
	}

	private void copyAttributes(Element e, Container c) {
		// Do nothing, since containers don't yet have attributes.
		//TODO: Add attributes to containers, and flesh out the code here.
	}

	private void copyNonElementData(Node n, Container c) {
		//TODO: Anything to do here?
	}

}
