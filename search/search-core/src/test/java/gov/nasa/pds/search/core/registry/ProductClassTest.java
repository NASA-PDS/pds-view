/**
 * 
 */
package gov.nasa.pds.search.core.registry;

import static org.junit.Assert.*;
import gov.nasa.pds.registry.client.results.RegistryHandler;
import gov.nasa.pds.registry.model.wrapper.ExtendedExtrinsicObject;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.schema.OutputString;
import gov.nasa.pds.search.core.schema.OutputStringFormat;
import gov.nasa.pds.search.core.test.SearchCoreTest;
import gov.nasa.pds.search.core.util.Debugger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link ProductClass}
 * 
 * @author jpadams
 * 
 */
@RunWith(JUnit4.class)
public class ProductClassTest extends SearchCoreTest {

	private static final File TEST_DIR = new File(
			System.getProperty("user.dir") + "/"
					+ TestConstants.SEARCH_HOME_RELATIVE + "/"
					+ Constants.SOLR_DOC_DIR);

	private ProductClass pc;

	@Rule
	public SingleTestRule test = new SingleTestRule("");

	@BeforeClass
	public static void oneTimeSetUp() throws IOException {
		FileUtils.forceMkdir(TEST_DIR);
		Debugger.debugFlag = true;
		gov.nasa.pds.registry.util.Debugger.debugFlag = true;
	}

	/*
	 * @AfterClass public static void oneTimeTearDown() throws IOException {
	 * FileUtils.deleteDirectory(TEST_DIR); }
	 */

	@Before
	public void setUp() throws IOException {
		// Create new productClass instance for given class.
		this.pc = new ProductClass(TEST_DIR,
				Arrays.asList(TestConstants.PDS3_REGISTRY_URL),
				new ArrayList<String>());
		this.pc.setQueryMax(1);
	}

	@Test
	public void testQuery() throws SearchCoreFatalException {
		try {
			assertFalse(this.pc.query(
					new File(System.getProperty("user.dir") + "/"
							+ TestConstants.TEST_DIR_RELATIVE
							+ "config/core-config-test-1.xml")).isEmpty());
		} catch (ProductClassException e) {
			e.printStackTrace();
			fail("Query Test failed. See stack trace.");
		}
	}

	@Test
	@Ignore // Ignoring for now. Fails on archiveinfo because we have no Approved archive info products.
	public void testQueryPDS3() throws SearchCoreFatalException {
		try {
			String[] extensions = { "xml" };
			for (File file : FileUtils
					.listFiles(
							new File(System.getProperty("user.dir") + "/"
									+ TestConstants.CONFIG_DIR_RELATIVE
									+ "/pds/pds3/"), extensions, false)) {
				if (this.pc.query(file).isEmpty()) {
					fail("Test failed - Config returned empty list of Extrinsics: "
							+ file.getAbsolutePath());
				}
			}
		} catch (ProductClassException e) {
			e.printStackTrace();
			fail("PDS3 Query Test failed. See stack trace.");
		}
	}

	@Test
	@Ignore
	public void testQueryPDS4() throws SearchCoreFatalException {
		try {
			String[] extensions = { "xml" };
			this.pc = new ProductClass(TEST_DIR,
					Arrays.asList(TestConstants.PDS4_REGISTRY_URL), // primary
																		// registry
					new ArrayList<String>()); // secondary
																		// registry
			this.pc.setQueryMax(1);
			for (File file : FileUtils
					.listFiles(
							new File(System.getProperty("user.dir") + "/"
									+ TestConstants.TEST_DIR_RELATIVE
									+ "/pds4-config/"), extensions, false)) {
				if (this.pc.query(file).isEmpty()) {
					fail("Test failed - Config returned empty list of Extrinsics: "
							+ file.getAbsolutePath());
				}
			}
		} catch (ProductClassException e) {
			e.printStackTrace();
			fail("PDS4 Query Test failed. See stack trace.");
		}
	}

	@Test
	public void testQueryPSA() throws SearchCoreFatalException {
		try {
			String[] extensions = { "xml" };
			this.pc = new ProductClass(TEST_DIR,
					Arrays.asList(TestConstants.PSA_REGISTRY_URL), // primary
																	// registry
					Arrays.asList(TestConstants.PDS3_REGISTRY_URL)); // secondary
																		// registry
			this.pc.setQueryMax(1);

			File file = new File(System.getProperty("user.dir") + "/"
					+ TestConstants.TEST_DIR_RELATIVE
					+ "config/psa/psa-dataset.xml");
			if (this.pc.query(file).isEmpty()) {
			fail("Test failed - Config returned empty list of Extrinsics: "
					+ file.getAbsolutePath());
			}
		} catch (ProductClassException e) {
			fail("PDS3 Query Test failed. See stack trace.");
		}
	}

	@Test
	public void testQueryPSAUsingConfigSpecifiedRegistry()
			throws SearchCoreFatalException {
		try {
			String[] extensions = { "xml" };
			this.pc = new ProductClass(TEST_DIR,
					Arrays.asList(TestConstants.PSA_REGISTRY_URL), // primary
																	// registry
					new ArrayList<String>()); // secondary registry
			this.pc.setQueryMax(1);

			File file = new File(System.getProperty("user.dir") + "/"
									+ TestConstants.TEST_DIR_RELATIVE
									+ "config/psa/psa-dataset.xml");
			if (this.pc.query(file).isEmpty()) {
				fail("Test failed - Config returned empty list of Extrinsics: "
						+ file.getAbsolutePath());
			}
		} catch (ProductClassException e) {
			fail("PDS3 Query Test failed. See stack trace.");
		}
	}

	@Test
	public void testQueryWithoutPrimaryRegistry()
			throws SearchCoreFatalException {
		try {
			this.pc = new ProductClass(TEST_DIR, new ArrayList<String>(), // primary
																			// registry
					new ArrayList<String>()); // secondary registry
			this.pc.setQueryMax(1);

			assertTrue(this.pc.query(
					new File(System.getProperty("user.dir") + "/"
							+ TestConstants.TEST_DIR_RELATIVE
							+ "config/core-config-test-1.xml")).isEmpty());
		} catch (ProductClassException e) {
			e.printStackTrace();
			fail("Query Test failed. See stack trace.");
		}
	}
	
	@Test
	@Ignore
	public void testCheckForSubstring() {
		try {
			String lidvid = "urn:nasa:pds:context_pds3:instrument:instrument.mri__dif::8.0";
			RegistryHandler handler = new RegistryHandler(
					Arrays.asList(TestConstants.PDS3_REGISTRY_URL),
					new ArrayList<String>(), 5);
			handler.setQueryMax(10);
			ExtendedExtrinsicObject extObject = new ExtendedExtrinsicObject(handler.getExtrinsicByLidvid(lidvid));
			OutputString outputString = new OutputString();
			outputString.setValue("pds4:{lid}");
			outputString.setFormat(OutputStringFormat.TEXT);
			assertTrue(this.pc.checkForSubstring(outputString, extObject).equals("pds4:urn:nasa:pds:context_pds3:instrument:instrument.mri__dif"));
		} catch (Exception e) {
			fail("Check substring unit test failed.");
		}
	}
	
	@Test
	public void testLastVersionOnlyQueried() throws SearchCoreFatalException {
		try {
			this.pc = new ProductClass(TEST_DIR,
					Arrays.asList(TestConstants.PDS3_REGISTRY_URL),
					new ArrayList<String>());
			this.pc.setQueryMax(1000);
			List<String> keys = this.pc.query(
					new File(System.getProperty("user.dir") + "/"
							+ TestConstants.TEST_DIR_RELATIVE
							+ "config/coreConfigTestLatestVersion.xml"));

			assertEquals("Returned more than one version", keys.size(), 1);
		} catch (ProductClassException e) {
			e.printStackTrace();
			fail("PDS3 Query Test failed. See stack trace.");
		}
	}
	
	@Test
	public void testApprovedOnlyQueried() throws SearchCoreFatalException {
		try {
			this.pc = new ProductClass(TEST_DIR,
					Arrays.asList(TestConstants.PDS3_REGISTRY_URL),
					new ArrayList<String>());
			this.pc.setQueryMax(10);
			List<String> keys = this.pc.query(
					new File(System.getProperty("user.dir") + "/"
							+ TestConstants.TEST_DIR_RELATIVE
							+ "config/coreConfigTestApproved.xml"));

			assertTrue("Approved product was not grabbed", keys.contains("urn:nasa:pds:context_pds3:data_set:data_set.msl-m-mahli-4-rdr-z-v1.0"));
		} catch (ProductClassException e) {
			e.printStackTrace();
			fail("Approved Status Query Test failed. See stack trace.");
		}
	}

}
