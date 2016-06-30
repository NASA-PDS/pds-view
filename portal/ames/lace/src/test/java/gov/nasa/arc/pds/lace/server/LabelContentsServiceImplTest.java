package gov.nasa.arc.pds.lace.server;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import gov.nasa.arc.pds.lace.server.parse.MockHttpSession;
import gov.nasa.arc.pds.lace.server.schema.SchemaDefaults;
import gov.nasa.arc.pds.lace.server.schema.SchemaManager;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.exception.ServiceException;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpSession;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

@Guice(modules = {LabelContentsServiceImplTest.Module.class})
public class LabelContentsServiceImplTest {

	private static final String USER_ID = "user1";

	@Inject
	private ServerConfiguration serverConfig;

	@Inject
	private Provider<LabelContentsServiceImpl> implProvider;

	private LabelContentsServiceImpl impl;

	private File tempDir;

	@BeforeMethod
	public void init() throws ServiceException {
		String tempDirName = getClass().getSimpleName() + "-temp";
		tempDir = new File(new File(System.getProperty("java.io.tmpdir")), tempDirName);
		if (tempDir.exists()) {
			recursivelyDelete(tempDir);
		}
		tempDir.mkdir();
		(new File(tempDir, "projects")).mkdir();
		(new File(tempDir, "upload")).mkdir();

		serverConfig.setDataRoot(tempDir);
		impl = implProvider.get();
		impl.setUser(USER_ID);
	}

	@AfterMethod
	public void cleanup() {
		recursivelyDelete(tempDir);
	}

	private void recursivelyDelete(final File f) {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				recursivelyDelete(child);
			}
		}

		f.delete();
	}

	@Test
	public void testDefaultSchema() throws URISyntaxException, ClassCastException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		assertTrue(impl.getRootContainer("Product_Observational", "http://pds.nasa.gov/pds4/pds/v03") != null);
	}

	@Test
	public void testGetContainerForFile() throws URISyntaxException, ClassCastException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Container container = impl.getContainerForRealPath("src/test/resources/Table_Character_0300a.xml");
		assertEquals(container.getType().getElementName(), "Product_Observational");
	}

	@Test
	public void testGetTypeForElement() {
		LabelItemType type = impl.getTypeForElement("Product_Observational", "http://pds.nasa.gov/pds4/pds/v1");
		assertEquals(type.getElementName(), "Product_Observational");

		type = impl.getTypeForElement("Role", "http://www.spase-group.org/data/schema");
		assertEquals(type.getElementName(), "Role");
	}

	@Test
	public void testGetElementNamesByNamespace() {
		Collection<String> names = impl.getElementNamesByNamespace("");
		assertEquals(names.size(), 0);

		names = impl.getElementNamesByNamespace("http://arc.nasa.gov/pds4/label-designer/test");
		assertEquals(names.size(), 10);
	}

	public static class Module extends AbstractModule {

		@Override
		protected void configure() {
			SchemaDefaults schemaDefaults = new SchemaDefaults();
			schemaDefaults.addSchemaFile("pds4/1100/PDS4_PDS_1100.xsd"); // PDS4 1.1.0.0 schema
			schemaDefaults.addSchemaFile("pds4/1100/PDS4_PDS_1100.sch"); // PDS4 1.1.0.0 Schematron
			schemaDefaults.addSchemaFile("spase/2.2.2/spase-2_2_2.xsd"); // SPASE 2.2.2
			schemaDefaults.addSchemaFile("pds4/0300a/PDS4_OPS_0300a.xsd"); // PDS4 0.3.0.0.a schema
			schemaDefaults.addSchemaFile("pds4/0300a/PDS4_OPS_0300a.sch"); // PDS4 0.3.0.0.a Schematron

			schemaDefaults.addSchemaFile("test/test-schema.xsd"); // For label reader unit testing.
			schemaDefaults.addSchemaFile("test/model-analyzer-schema.xsd"); // For analyzer unit testing.

			bind(SchemaDefaults.class).toInstance(schemaDefaults);

			IDGenerator idFactory = new IDGenerator();
			bind(IDGenerator.class).toInstance(idFactory);

			HttpSession session = new MockHttpSession();
			bind(HttpSession.class).toInstance(session);

			// We only need a single instance of the SchemaManager, but it needs to
			// be specific to this test class.
			bind(SchemaManager.class).in(Singleton.class);
		}

	}

}
