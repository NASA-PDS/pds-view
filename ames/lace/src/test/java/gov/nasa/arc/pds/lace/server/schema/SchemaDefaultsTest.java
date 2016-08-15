package gov.nasa.arc.pds.lace.server.schema;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;

@Guice(modules={SchemaDefaultsTest.Module.class})
public class SchemaDefaultsTest {

	@Inject
	private Provider<SchemaDefaults> defaultsProvider;
	
	private SchemaDefaults defaults;
	
	@BeforeMethod
	public void init() {
		defaults = defaultsProvider.get();
		defaults.clearDefaults();
	}
	
	@Test
	public void testEmptyDefaults() {
		assertEquals(defaults.getSchemaURIs().length, 0);
		assertEquals(defaults.getSchemaFiles().length, 0);
	}
	
	@Test
	public void testAddDefault() throws URISyntaxException {
		String schemaPath = "pds4/1100/PDS4_PDS_1100.sch";
		URI schemaURI = getClass().getResource("/schema/" + schemaPath).toURI();
		File schemaFile = new File(schemaURI);
		defaults.addSchemaFile(schemaPath);
		
		File[] schemaFiles = defaults.getSchemaFiles();
		assertEquals(schemaFiles.length, 1);
		assertEquals(schemaFiles[0], schemaFile);
		
		URI[] schemaURIs = defaults.getSchemaURIs();
		assertEquals(schemaURIs.length, 1);
		assertEquals(schemaURIs[0], schemaURI);
	}
	
	public static class Module extends AbstractModule {

		@Override
		protected void configure() {
			bind(SchemaDefaults.class).toInstance(new SchemaDefaults());
		}
		
	}
	
}
