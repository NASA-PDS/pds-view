package gov.nasa.arc.pds.lace.server.schema;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.validation.ValidatorHandler;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;

public class SchemaLoaderTest {

	@Test
	public void testLoadSchema() throws SAXException, URISyntaxException {
		Map<String, URI> schemas = new HashMap<String, URI>();
		URI schemaURI = getClass().getResource("/schema/pds4/1100/PDS4_PDS_1100.xsd").toURI();
		schemas.put("http://pds.nasa.gov/pds4/pds/v1", schemaURI);
		SchemaLoader loader = new SchemaLoader(schemas);

		MockContentHandler handler = new MockContentHandler();
		assertFalse(handler.isDocumentStarted());
		ValidatorHandler validatorHandler = loader.getSchema().newValidatorHandler();
		validatorHandler.setContentHandler(handler);
		validatorHandler.startDocument();
		assertTrue(handler.isDocumentStarted());
	}

}
