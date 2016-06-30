package gov.nasa.arc.pds.lace.server.schema;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.lace.server.parse.ModelAnalyzer;
import gov.nasa.arc.pds.lace.server.parse.ValidationAnalyzer;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelElement;
import gov.nasa.arc.pds.lace.shared.LabelItem;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

@Guice(modules = {DefaultSchemaManagerModule.class})
public class SchemaManagerTest {

	private static final String PDS4_V1_PREFIX = "{http://pds.nasa.gov/pds4/pds/v1}";
	private static final String PDS4_V1_NS = "http://pds.nasa.gov/pds4/pds/v1";
	private static final String SPASE_NS = "http://www.spase-group.org/data/schema";

	@Inject
	private SchemaManager manager;

	@Test
	public void testNamespaces() {
		List<String> namespaces = Arrays.asList(manager.getNamespaces());
		assertTrue(namespaces.contains(PDS4_V1_NS));
	}

	@Test
	public void testSchema() throws SAXException {
		Schema schema = manager.getSchema();

		MockContentHandler handler = new MockContentHandler();
		assertFalse(handler.isDocumentStarted());
		ValidatorHandler validatorHandler = schema.newValidatorHandler();
		validatorHandler.setContentHandler(handler);
		validatorHandler.startDocument();
		assertTrue(handler.isDocumentStarted());
	}

	@Test
	public void testAnalyzer() {
		ModelAnalyzer analyzer = manager.getAnalyzer();
		Container container = analyzer.getContainerForElement("Product_Observational", PDS4_V1_NS, false);
		assertEquals(container.getType().getElementName(), "Product_Observational");
	}

	@Test
	public void testSchematronValidator() {
		// PDS4 has a Schematron validator, but SPASE does not use Schematron.
		assertNotNull(manager.getSchematronValidator(PDS4_V1_NS));
		assertNull(manager.getSchematronValidator(SPASE_NS));
	}

	@Test
	public void testDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilder builder = manager.getDocumentBuilder();
		Document doc = builder.newDocument();
		Element e = doc.createElementNS(PDS4_V1_NS, "Product_Observational");
		assertEquals(e.getNamespaceURI(), PDS4_V1_NS);
		assertEquals(e.getLocalName(), "Product_Observational");
	}

	@Test(dataProvider="ValidValuesTests")
	public void testValidValues(String context, String[] expectedValues) {
		ValidationAnalyzer validation = manager.getValidationAnalyzer(PDS4_V1_NS);
		List<String> actualValues = validation.getValidValues(context);
		assertEquals(actualValues.toArray(new String[actualValues.size()]), expectedValues);
	}

	@DataProvider(name="ValidValuesTests")
	private Object[][] getValidValuesTests() {
		return new Object[][] {
				// context, valid values
				{
					PDS4_V1_PREFIX + "Product_Observational/"
					+ PDS4_V1_PREFIX + "File_Area_Observational/"
					+ PDS4_V1_PREFIX + "Table_Character/"
					+ PDS4_V1_PREFIX + "Record_Character/"
					+ PDS4_V1_PREFIX + "Field_Character/"
					+ PDS4_V1_PREFIX + "field_location/"
					+ "@unit",
					new String[] {"byte"}
				},

				{
					PDS4_V1_PREFIX + "Product_Observational/"
					+ PDS4_V1_PREFIX + "Identification_Area/"
					+ PDS4_V1_PREFIX + "information_model_version",
					new String[] {"1.2.0.1"}
				},
		};
	}

	@Test
	public void testAddValidValues() {
		ModelAnalyzer analyzer = manager.getAnalyzer();

		Container container = analyzer.getContainerForElement("Product_Observational", PDS4_V1_NS);
		manager.addValidValues(container, null);
		LabelElement infoModelVersion = findElement("information_model_version", container);
		assertEquals(infoModelVersion.getType().getValidValues().size(), 1);

		LabelElement startDateTime = findElement("start_date_time", container);
		assertTrue(startDateTime.getType().getValidValues()==null || startDateTime.getType().getValidValues().size()==0);
	}

	private LabelElement findElement(String tag, LabelElement item) {
		if (item.getType().getElementName().equals(tag)) {
			return item;
		}

		if (!(item instanceof Container)) {
			return null;
		}

		Container container = (Container) item;
		for (LabelItem child : container.getContents()) {
			if (child instanceof LabelElement) {
				LabelElement result = findElement(tag, (LabelElement) child);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

}
