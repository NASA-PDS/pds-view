package gov.nasa.arc.pds.lace.server.parse;

import static org.testng.Assert.assertEquals;
import gov.nasa.arc.pds.lace.server.schema.SchemaManager;
import gov.nasa.arc.pds.lace.shared.Container;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

@Guice(modules = {ParsingTestModule.class})
public class LabelWriterTest {

	private static final String ASCII_ENCODING = "US-ASCII";

	private static final String SCHEMA_NAMESPACE = "http://arc.nasa.gov/pds4/label-designer/test";

	@Inject
	private SchemaManager manager;
	
	@Inject
	private Provider<LabelReader> readerProvider;
	
	@Inject
	private Provider<LabelWriter> writerProvider;
	
	@Inject
	private Provider<LabelParser> parserProvider;

	private LabelReader reader;
	private LabelWriter writer;
	private LabelParser parser;
	
	@BeforeMethod
	public void init() {
		reader = readerProvider.get();
		reader.setSchemaManager(manager);
		writer = writerProvider.get();
		parser = parserProvider.get();
	};

	@Test
	public void testWriteEmptyContentModel() throws SAXException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		String doc = ""
			+ "<EmptyProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "</EmptyProduct>";

		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes(ASCII_ENCODING)));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.writeLabel(manager, root, out);

		String result = out.toString(ASCII_ENCODING);
		checkSameXML(doc, result);
	}

	@Test
	public void testPhoenixProduct() throws SAXException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		String phoenixLabelPath = "src/test/resources/TELLTALE_01_30.xml";
		Container root = reader.readLabel(new FileInputStream(phoenixLabelPath));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.writeLabel(manager, root, out);

		String result = out.toString(ASCII_ENCODING);
		checkSameXML(
				new FileInputStream(phoenixLabelPath),
				new ByteArrayInputStream(result.getBytes(ASCII_ENCODING))
		);
	}

	private void checkSameXML(String expected, String actual) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		checkSameXML(
				new ByteArrayInputStream(expected.getBytes(ASCII_ENCODING)),
				new ByteArrayInputStream(actual.getBytes(ASCII_ENCODING))
		);
	}

	private void checkSameXML(InputStream expectedIn, InputStream actualIn) throws ParserConfigurationException, SAXException, IOException {
		Document expectedDoc = parser.parse(manager, expectedIn);
		Document actualDoc = parser.parse(manager, actualIn);

		trimNodes(expectedDoc);
		trimNodes(actualDoc);

		checkSame(expectedDoc, actualDoc);
	}

	private void trimNodes(Node parent) {
		NodeList nodes = parent.getChildNodes();
		int i = 0;
		while (i < nodes.getLength()) {
			Node node = nodes.item(i);
			if ((node instanceof ProcessingInstruction)
					|| (node instanceof Comment)
					|| (node instanceof Notation)
					|| ((node instanceof Text) && node.getTextContent().trim().isEmpty())) {
				parent.removeChild(node);
			} else {
				trimNodes(node);
				++i;
			}
		}
	}

	private void checkSame(Node expected, Node actual) {
		assertEquals(actual.getNodeName(), expected.getNodeName());

		NodeList expectedChildren = expected.getChildNodes();
		NodeList actualChildren = actual.getChildNodes();

		assertEquals(actualChildren.getLength(), expectedChildren.getLength());
		for (int i=0; i < actualChildren.getLength(); ++i) {
			checkSame(expectedChildren.item(i), actualChildren.item(i));
		}
	}

}
