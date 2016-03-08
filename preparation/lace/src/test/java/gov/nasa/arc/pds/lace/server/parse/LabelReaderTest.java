package gov.nasa.arc.pds.lace.server.parse;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

public class LabelReaderTest {

	private static final String SCHEMA_PATH = "src/test/resources/test-schema.xsd";
	private static final String SCHEMA_NAMESPACE = "http://arc.nasa.gov/pds4/label-designer/test";

	private static final String PDS4_0300a_SCHEMA_PATH = "src/main/resources/gov/nasa/arc/pds/lace/server/PDS4_OPS_0300a.xsd";

	private LabelReader reader;

	@BeforeMethod
	public void init() throws SAXException {
		reader = new LabelReader(SCHEMA_PATH);
	}

	@Test
	public void testEmptyProduct() throws SAXException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		String doc = ""
			+ "<EmptyProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "</EmptyProduct>";

		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
		assertEquals(root.getType().getElementName(), "EmptyProduct");
		assertEquals(root.getContents().size(), 0);
	}

	@Test(dataProvider="IgnorableTrailingContentTests")
	public void testIgnorableTrailingContent(String trailingContent) throws SAXException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		String doc = ""
			+ "<EmptyProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ trailingContent
			+ "</EmptyProduct>";

		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
		assertEquals(root.getType().getElementName(), "EmptyProduct");
		assertEquals(root.getContents().size(), 0);
	}

	@Test(dataProvider="BadTrailingContentTests", expectedExceptions={IllegalArgumentException.class})
	public void testUnmatchedContent(String trailingContent) throws SAXException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		String doc = ""
			+ "<EmptyProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ trailingContent
			+ "</EmptyProduct>";

		@SuppressWarnings("unused")
		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
	}

	@Test(expectedExceptions={IllegalArgumentException.class})
	public void testElementMismatch() throws SAXException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		String doc = ""
			+ "<SimpleItemProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "  <UnexpectedElement />"
			+ "</SimpleItemProduct>";

		@SuppressWarnings("unused")
		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
	}

	@Test(expectedExceptions={IllegalArgumentException.class})
	public void testInsertionPointMismatch() throws SAXException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		String doc = ""
			+ "<SimpleRequiredChoiceProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "  <UnexpectedElement />"
			+ "</SimpleRequiredChoiceProduct>";

		@SuppressWarnings("unused")
		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
	}

	@SuppressWarnings("unused")
	@DataProvider(name="BadTrailingContentTests")
	private Object[][] getBadTrailingContentTests() {
		return new Object[][] {
				// trailing content
				{ "<UnexpectedElement />" },
				{ "  non-empty text node  " },
		};
	}

	@SuppressWarnings("unused")
	@DataProvider(name="IgnorableTrailingContentTests")
	private Object[][] getIgnorableTrailingContentTests() {
		return new Object[][] {
				// trailing content
				{ "<!-- a comment -->" },
				{ "    " }, // empty text node
				{ "<?dummy-processing-instruction ?>" },
		};
	}

	@Test
	public void testMatchSimpleItem() throws SAXException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		String doc = ""
			+ "<SimpleItemProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "  <String>hello</String>"
			+ "</SimpleItemProduct>";

		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
		assertEquals(root.getType().getElementName(), "SimpleItemProduct");
		assertEquals(root.getContents().size(), 1);
		assertTrue(root.getContents().get(0) instanceof SimpleItem);

		SimpleItem item = (SimpleItem) root.getContents().get(0);
		assertEquals(item.getType().getElementName(), "String");
	}

	@Test
	public void testMatchSimpleContainer() throws SAXException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		String doc = ""
			+ "<SimpleContainerProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "  <Container>"
			+ "    <String>hello</String>"
			+ "  </Container>"
			+ "</SimpleContainerProduct>";

		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
		assertEquals(root.getType().getElementName(), "SimpleContainerProduct");
		assertEquals(root.getContents().size(), 1);
		assertTrue(root.getContents().get(0) instanceof Container);

		Container container = (Container) root.getContents().get(0);
		assertEquals(container.getType().getElementName(), "Container");

		assertEquals(container.getContents().size(), 1);
		assertTrue(container.getContents().get(0) instanceof SimpleItem);


		SimpleItem item = (SimpleItem) container.getContents().get(0);
		assertEquals(item.getType().getElementName(), "String");
	}

	@Test
	public void testMatchSimpleInsertionPoint() throws SAXException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		String doc = ""
			+ "<SimpleInsertionPointProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "  <Container>"
			+ "    <String>hello</String>"
			+ "  </Container>"
			+ "</SimpleInsertionPointProduct>";

		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
		assertEquals(root.getType().getElementName(), "SimpleInsertionPointProduct");
		assertEquals(root.getContents().size(), 3); // ins point, SimpleContainer, ins point

		assertTrue(root.getContents().get(0) instanceof InsertionPoint);
		@SuppressWarnings("unused")
		InsertionPoint insPoint = (InsertionPoint) root.getContents().get(0);

		assertTrue(root.getContents().get(2) instanceof InsertionPoint);
		insPoint = (InsertionPoint) root.getContents().get(2);

		Container container = (Container) root.getContents().get(1);
		assertEquals(container.getType().getElementName(), "Container");

		assertEquals(container.getContents().size(), 1);
		assertTrue(container.getContents().get(0) instanceof SimpleItem);
		SimpleItem item = (SimpleItem) container.getContents().get(0);
		assertEquals(item.getType().getElementName(), "String");
	}

	@Test
	public void testMatchSimpleChoice() throws SAXException, UnsupportedEncodingException, ParserConfigurationException, IOException {
		String doc = ""
			+ "<SimpleChoiceProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "  <Integer>123</Integer>"
			+ "  <String>hello</String>"
			+ "</SimpleChoiceProduct>";

		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
		assertEquals(root.getType().getElementName(), "SimpleChoiceProduct");
		assertEquals(root.getContents().size(), 5); // ins point, SimpleNumber, ins point, SimpleItem, ins point

		assertTrue(root.getContents().get(0) instanceof InsertionPoint);
		InsertionPoint insPoint = (InsertionPoint) root.getContents().get(0);
		assertEquals(insPoint.getAlternatives().size(), 1);
		assertEquals(insPoint.getAlternatives().get(0).getTypes().size(), 2);
		assertEquals(insPoint.getAlternatives().get(0).getTypes().get(0).getElementName(), "String");
		assertEquals(insPoint.getAlternatives().get(0).getTypes().get(1).getElementName(), "Integer");

		assertTrue(root.getContents().get(2) instanceof InsertionPoint);
		insPoint = (InsertionPoint) root.getContents().get(2);
		assertEquals(insPoint.getAlternatives().size(), 1);
		assertEquals(insPoint.getAlternatives().get(0).getTypes().size(), 2);
		assertEquals(insPoint.getAlternatives().get(0).getTypes().get(0).getElementName(), "String");
		assertEquals(insPoint.getAlternatives().get(1).getTypes().get(0).getElementName(), "Integer");

		assertTrue(root.getContents().get(4) instanceof InsertionPoint);
		insPoint = (InsertionPoint) root.getContents().get(4);
		assertEquals(insPoint.getAlternatives().size(), 1);
		assertEquals(insPoint.getAlternatives().get(0).getTypes().size(), 2);
		assertEquals(insPoint.getAlternatives().get(0).getTypes().get(0).getElementName(), "String");
		assertEquals(insPoint.getAlternatives().get(1).getTypes().get(0).getElementName(), "Integer");

		SimpleItem item = (SimpleItem) root.getContents().get(1);
		assertEquals(item.getType().getElementName(), "Integer");

		item = (SimpleItem) root.getContents().get(3);
		assertEquals(item.getType().getElementName(), "String");
	}

	@Test
	public void testMissingOptionalElements() throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		String doc = ""
			+ "<OptionalElementsProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "  <Required1><String>abc</String></Required1>"
			+ "  <Required2><String>def</String></Required2>"
			+ "</OptionalElementsProduct>";

		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
		assertEquals(root.getType().getElementName(), "OptionalElementsProduct");
		assertEquals(root.getContents().size(), 5); // opt, req, opt (ins point), req, opt
	}

	@Test
	public void testMultipleRequiredElements() throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		String doc = ""
			+ "<MultipleRepeatingRequiredProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "  <Required1><String>abc</String></Required1>"
			+ "  <Required2><String>def</String></Required2>"
			+ "  <Required3><String>ghi</String></Required3>"
			+ "</MultipleRepeatingRequiredProduct>";

		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
		assertEquals(root.getType().getElementName(), "MultipleRepeatingRequiredProduct");
		assertEquals(root.getContents().size(), 7); // ins, req1, ins, req2, ins, req3, ins
	}

	@Test
	public void testDifferingRepeatedElements() throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		String doc = ""
			+ "<SimpleInsertionPointProduct xmlns='" + SCHEMA_NAMESPACE + "'>"
			+ "  <Container><String>value1</String></Container>"
			+ "  <Container><String>value2</String></Container>"
			+ "  <Container><String>value3</String></Container>"
			+ "</SimpleInsertionPointProduct>";

		Container root = reader.readLabel(new ByteArrayInputStream(doc.getBytes("US-ASCII")));
		assertEquals(root.getType().getElementName(), "SimpleInsertionPointProduct");
		assertEquals(root.getContents().size(), 7); // ins, container, ins, container, ins, container, ins
		for (int i=1; i<=3; ++i) {
			Container container = (Container) root.getContents().get(2*i - 1);
			SimpleItem item = (SimpleItem) container.getContents().get(0);
			assertEquals(item.getValue(), "value" + i);
		}
	}

	@Test
	public void testPDS4Product() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		LabelReader pds4Reader = new LabelReader(PDS4_0300a_SCHEMA_PATH);
		Container root = pds4Reader.readLabel(new FileInputStream("src/test/resources/Table_Character_0300a.xml"));
		assertEquals(root.getType().getElementName(), "Product_Observational");

		// Check to see that the character table is inside the File_Area_Observational.
		Container fileAreaObservational = findChildContainer(root, "File_Area_Observational");
		assertNotNull(fileAreaObservational, "File_Area_Observational not found");

		Container tableCharacter = findChildContainer(fileAreaObservational, "Table_Character");
		assertNotNull(tableCharacter, "Table_Character not found");
	}

	private Container findChildContainer(Container parent, String elementName) {
		for (LabelItem item : parent.getContents()) {
			if (item instanceof Container) {
				Container c = (Container) item;
				if (c.getType().getElementName().equals(elementName)) {
					return c;
				}
			}
		}

		return null;
	}

	private SimpleItem findChildSimpleItem(Container parent, String elementName) {
		for (LabelItem child : parent.getContents()) {
			if (child instanceof SimpleItem) {
				SimpleItem item = (SimpleItem) child;
				if (item.getType().getElementName().equals(elementName)) {
					return item;
				}
			}
		}

		return null;
	}

	@Test
	public void testPhoenixProduct() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		LabelReader pds4Reader = new LabelReader(PDS4_0300a_SCHEMA_PATH);
		Container root = pds4Reader.readLabel(new FileInputStream("src/test/resources/TELLTALE_01_30.xml"));
		assertEquals(root.getType().getElementName(), "Product_Observational");

		// Check to see that the character table is inside the File_Area_Observational.
		Container fileAreaObservational = findChildContainer(root, "File_Area_Observational");
		assertNotNull(fileAreaObservational, "File_Area_Observational not found");

		Container tableCharacter = findChildContainer(fileAreaObservational, "Table_Character");
		assertNotNull(tableCharacter, "Table_Character not found");

		Container referenceList = findChildContainer(root, "Reference_List");
		Container internalReference = findChildContainer(referenceList, "Internal_Reference");
		SimpleItem lid_reference = findChildSimpleItem(internalReference, "lid_reference");
		assertEquals(lid_reference.getValue(), "urn:nasa:pds:phx:tt:document:TELLTALECALRPT");
	}

}
