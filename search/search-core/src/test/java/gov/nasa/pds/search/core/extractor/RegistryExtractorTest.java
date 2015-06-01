package gov.nasa.pds.search.core.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.test.SearchCoreTest;
import gov.nasa.pds.search.core.util.Debugger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link RegistryExtractor}.
 * 
 * @author jpadams
 */
@RunWith(JUnit4.class)
public class RegistryExtractorTest extends SearchCoreTest {

	private static final String TEST_DIR = System.getProperty("user.dir") + "/"
			+ TestConstants.SEARCH_HOME_RELATIVE;
	private RegistryExtractor re;

	@Rule
	public SingleTestRule test = new SingleTestRule("testRunPDS4Context");

	@Before
	public void setUp() {
		try {
			Debugger.debugFlag = true;
			gov.nasa.pds.registry.util.Debugger.debugFlag = true;
			this.re = new RegistryExtractor(new File(
					System.getProperty("user.dir") + "/"
							+ TestConstants.TEST_DIR_RELATIVE + "config"),
					Arrays.asList(TestConstants.PDS3_REGISTRY_URL),
					new ArrayList<String>());
			this.re.setOutputDir((new File(TEST_DIR, Constants.SOLR_DOC_DIR)));
			this.re.setQueryMax(2);
		} catch (Exception e) {
			fail("Setup failed." + e.getMessage());
		}
	}

	@Test
	public void testCoreConfigList() {
		// Create list to compare to output from test run
		List<File> correctList = new ArrayList<File>();
		correctList.add(new File(System.getProperty("user.dir") + "/"
				+ TestConstants.TEST_DIR_RELATIVE
				+ "config_list_test/core-config-test-1.xml"));
		correctList.add(new File(System.getProperty("user.dir") + "/"
				+ TestConstants.TEST_DIR_RELATIVE
				+ "config_list_test/core-config-test-2.xml"));

		try {
			List<File> configList = this.re.getCoreConfigs(new File(System.getProperty("user.dir") + "/"
                    + TestConstants.TEST_DIR_RELATIVE + "config_list_test"));
			assertEquals(correctList, configList);
		} catch (Exception e) {
			e.printStackTrace();
			fail("RegistryExtractor.getCoreConfigList Test Failed.  See stack trace for error.");
		}
	}

	@Test
	public void testRun() {
		try {
			this.re.run();
		} catch (Exception e) {
			e.printStackTrace();
			fail("RegistryExtractor.run Test Failed.  See stack trace for error.");
		}
	}

	@Test
	public void testRunPDS3() {
		try {
			this.re.setConfDir(new File(System.getProperty("user.dir") + "/"
					+ TestConstants.CONFIG_DIR_RELATIVE + "/pds/pds3"));
			this.re.run();
		} catch (Exception e) {
			e.printStackTrace();
			fail("RegistryExtractor.run Test Failed.  See stack trace for error.");
		}
	}

	@Test
	@Ignore
	public void testRunPDS4() {
		try {
			this.re.setConfDir(new File(System.getProperty("user.dir") + "/"
					+ TestConstants.CONFIG_DIR_RELATIVE + "/pds/pds4"));
			this.re.setPrimaryRegistries(Arrays
					.asList(TestConstants.PDS4_REGISTRY_URL));
			this.re.run();
		} catch (Exception e) {
			e.printStackTrace();
			fail("RegistryExtractor.run Test Failed.  See stack trace for error.");
		}
	}

	@Test
	public void testRunPSA() throws SearchCoreFatalException {
		try {
			this.re.setConfDir(new File(System.getProperty("user.dir") + "/"
					+ TestConstants.TEST_DIR_RELATIVE + "/config/psa/psa-dataset.xml"));
			this.re.setPrimaryRegistries(Arrays
					.asList(TestConstants.PSA_REGISTRY_URL));
			// this.re.setBackupRegistries(Arrays.asList(TestConstants.PDS3_REGISTRY_URL));
			this.re.run();
		} catch (Exception e) {
			e.printStackTrace();
			fail("RegistryExtractor.run Test Failed.  See stack trace for error.");
		}
	}

	@Test
	public void testRunPDS4Context() {
		try {
			this.re.setConfDir(new File(System.getProperty("user.dir") + "/"
					+ TestConstants.TEST_DIR_RELATIVE
					+ "config/pds4-context.xml"));
			this.re.setPrimaryRegistries(Arrays
					.asList(TestConstants.PDS4_REGISTRY_URL));
			this.re.run();
		} catch (Exception e) {
			e.printStackTrace();
			fail("RegistryExtractor.run Test Failed.  See stack trace for error.");
		}
	}
}
