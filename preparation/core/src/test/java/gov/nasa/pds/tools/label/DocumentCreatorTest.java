package gov.nasa.pds.tools.label;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Implements a test of the line-number-aware content handler.
 */
@RunWith(JUnit4.class)
public class DocumentCreatorTest {

	private DocumentCreator creator;
	private XMLReader reader;

	// Creates the reader and the content handler.
	@Before
	public void init() throws ParserConfigurationException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = factory.newDocumentBuilder().newDocument();
		creator = new DocumentCreator(document);

 		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		parserFactory.setXIncludeAware(true);
		parserFactory.setValidating(false);
		reader = parserFactory.newSAXParser().getXMLReader();
		reader.setContentHandler(creator);
	}

	/**
	 * Tests that no start- or end-element events result in
	 * an empty document.
	 *
	 * @throws SAXException
	 */
	@Test
	public void testEmptyDocument() throws SAXException {
		creator.startDocument();
		creator.endDocument();

		assertThat(creator.getDocument(), notNullValue());
		Document doc = creator.getDocument();
		assertThat(doc.getDocumentElement(), equalTo(null));
	}

	/**
	 * Tests that a single element source document results in
	 * a single-element tree.
	 *
	 * @throws IOException
	 * @throws SAXException
	 */
	@Test
	public void testSingleElementDocument() throws IOException, SAXException {
		String docStr = "<element />";
		reader.parse(new InputSource(new StringReader(docStr)));

		Document doc = creator.getDocument();
		assertThat(doc.getDocumentElement(), notNullValue());
		Element root = doc.getDocumentElement();
		assertThat(root.getLocalName(), equalTo("element"));
	}

	/**
	 * Tests that the generated nodes in the tree have the right
	 * line and column numbers.
	 *
	 * @throws IOException
	 * @throws SAXException
	 */
	@Test
	public void testLineNumbers() throws IOException, SAXException {
		String docStr =
				"<root>\n"
				+ "  <child1 />\n"
				+ "  <child2 />\n"
				+ "</root>\n";
		reader.parse(new InputSource(new StringReader(docStr)));

		// Note that column numbers are the column of the end of the element,
		// not the beginning!

		Document doc = creator.getDocument();
		assertThat(doc.getDocumentElement(), notNullValue());
		Element root = doc.getDocumentElement();
		assertThat(root.getLocalName(), equalTo("root"));
		checkLocation(root, 1, 7);

		Node child1 = root.getElementsByTagName("child1").item(0);
		assertThat(child1, notNullValue());
		checkLocation(child1, 2, 13);

		Node child2 = root.getElementsByTagName("child2").item(0);
		assertThat(child2, notNullValue());
		checkLocation(child2, 3, 13);
	}

	private void checkLocation(Node node, int lineNumber, int columnNumber) {
		SourceLocation location = (SourceLocation) node.getUserData(SourceLocation.class.getName());
		assertThat(location, notNullValue());

		assertThat(location.getLineNumber(), equalTo(lineNumber));
		assertThat(location.getColumnNumber(), equalTo(columnNumber));
	}

}
