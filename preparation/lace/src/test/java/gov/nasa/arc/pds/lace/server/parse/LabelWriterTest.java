package gov.nasa.arc.pds.lace.server.parse;

import static org.testng.Assert.assertEquals;
import gov.nasa.arc.pds.lace.shared.Container;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.Test;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class LabelWriterTest {

	private static final String ASCII_ENCODING = "US-ASCII";

	private static final URI SCHEMA_URI = new File("src/test/resources/test-schema.xsd").toURI();
	private static final String SCHEMA_NAMESPACE = "http://arc.nasa.gov/pds4/label-designer/test";

	private static final URI PDS4_0300a_SCHEMA_URI = new File("src/main/resources/gov/nasa/arc/pds/lace/server/PDS4_OPS_0300a.xsd").toURI();

	private LabelReader reader;
	private LabelWriter writer;
	private LabelParser parser;

	@Test
	public void testWriteEmptyContentModel() throws SAXException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		reader = new LabelReader(SCHEMA_URI);
		writer = new LabelWriter(SCHEMA_URI);
		parser = new LabelParser(SCHEMA_URI);

		String doc = ""
			+ "<EmptyProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "</EmptyProduct>";

		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes(ASCII_ENCODING)));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.writeLabel(root, out);

		String result = out.toString(ASCII_ENCODING);
		checkSameXML(doc, result);
	}

	@Test
	public void testPhoenixProduct() throws SAXException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		reader = new LabelReader(PDS4_0300a_SCHEMA_URI);
		writer = new LabelWriter(PDS4_0300a_SCHEMA_URI);
		parser = new LabelParser(PDS4_0300a_SCHEMA_URI);

		String phoenixLabelPath = "src/test/resources/TELLTALE_01_30.xml";
		Container root = reader.readLabel(new FileInputStream(phoenixLabelPath));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writer.writeLabel(root, out);

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
		Document expectedDoc = parser.parse(expectedIn);
		Document actualDoc = parser.parse(actualIn);

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
