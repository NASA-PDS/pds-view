package gov.nasa.arc.pds.lace.server.parse;

import static org.testng.Assert.assertEquals;
import gov.nasa.arc.pds.lace.server.schema.SchemaManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

@Guice(modules = {ParsingTestModule.class})
public class LabelParserTest {

	@Inject
	private Provider<LabelParser> parserProvider;
	
	@Inject
	private SchemaManager manager;
	
	@Test
	public void testParseLabel() throws ParserConfigurationException, SAXException, IOException {
		LabelParser parser = parserProvider.get();
		Document doc = parser.parse(manager, "src/test/resources/Product_Table_Character.xml");
		Element root = doc.getDocumentElement();
		assertEquals(root.getNodeName(), "Product_Observational");
	}

	@Test
	public void testParseFromStream() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		LabelParser parser = parserProvider.get();
		Document doc = parser.parse(manager, new FileInputStream("src/test/resources/Product_Table_Character.xml"));
		Element root = doc.getDocumentElement();
		assertEquals(root.getTagName(), "Product_Observational");
	}

}
