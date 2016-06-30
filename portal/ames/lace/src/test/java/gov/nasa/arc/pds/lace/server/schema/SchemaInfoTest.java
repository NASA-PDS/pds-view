package gov.nasa.arc.pds.lace.server.schema;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

public class SchemaInfoTest {

	@Test
	public void testInitialization() {
		SchemaInfo info = new SchemaInfo();
		assertNull(info.getDescription());
		assertNull(info.getNamespaceURI());
		assertEquals(info.getSchemaPaths().length, 0);
		assertNull(info.getSchematronPath());
	}
	
	@Test
	public void testGettersSetters() {
		SchemaInfo info = (new SchemaInfo())
				.namespaceURI("ns")
				.description("description")
				.schemaPath("path1")
				.schematronPath("path2");
		
		assertEquals(info.getNamespaceURI(), "ns");
		assertEquals(info.getDescription(), "description");
		assertEquals(info.getSchemaPaths(), new String[] {"path1"});
		assertEquals(info.getSchematronPath(), "path2");
	}
	
}
