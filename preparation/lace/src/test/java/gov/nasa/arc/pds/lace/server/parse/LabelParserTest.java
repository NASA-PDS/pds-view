package gov.nasa.arc.pds.lace.server.parse;

import static org.testng.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import gov.nasa.arc.pds.lace.server.parse.LabelParser;

public class LabelParserTest {

	private static final String SCHEMA_PATH = "gov/nasa/arc/pds/lace/server/PDS4_OPS_0300a.xsd";

	@Test
	public void testInitialization() throws SAXException, URISyntaxException {
		URL schemaURL = ClassLoader.getSystemResource(SCHEMA_PATH);
		@SuppressWarnings("unused")
		LabelParser parser = new LabelParser(schemaURL.toURI());
		// Should get no exceptions if the schema was loaded successfully.
	}

	@Test
	public void testParseLabel() throws SAXException, URISyntaxException, ParserConfigurationException, IOException {
		URL schemaURL = ClassLoader.getSystemResource(SCHEMA_PATH);
		LabelParser parser = new LabelParser(schemaURL.toURI());
		Document doc = parser.parse("src/test/resources/Product_Table_Character.xml");
		Element root = doc.getDocumentElement();
		assertEquals(root.getNodeName(), "Product_Observational");
	}

	@Test
	public void testParseFromStream() throws SAXException, URISyntaxException, ParserConfigurationException, IOException {
		URL schemaURL = ClassLoader.getSystemResource(SCHEMA_PATH);
		LabelParser parser = new LabelParser(schemaURL.toURI());
		Document doc = parser.parse(new FileInputStream("src/test/resources/Product_Table_Character.xml"));
		Element root = doc.getDocumentElement();
		assertEquals(root.getTagName(), "Product_Observational");
	}

}
