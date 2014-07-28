package gov.nasa.pds.search.core;

import static org.junit.Assert.fail;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.test.SearchCoreTest;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests SearchCoreLauncher CLI for proper behavior. These are some pretty basic
 * tests for specifying correct arguments.
 * 
 * @author jpadams
 * 
 */
@RunWith(JUnit4.class)
public class SearchCoreLauncherTest extends SearchCoreTest {

	@BeforeClass
	public static void oneTimeSetUp() {
		File testDir = new File(System.getProperty("user.dir") + "/"
				+ TestConstants.SEARCH_HOME_RELATIVE);
		testDir.mkdirs();
	}

	@AfterClass
	public static void oneTimeTearDown() throws IOException {
		File testDir = new File(System.getProperty("user.dir") + "/"
				+ TestConstants.SEARCH_HOME_RELATIVE);
		FileUtils.deleteDirectory(testDir);
	}

	@Rule
	public SingleTestRule test = new SingleTestRule("");

	/**
	 * Test arguments are not empty
	 */
	@Test
	public void testEmptyArgs() {
		// Test empty args
		String[] args = {};
		try {
			SearchCoreLauncher.main(args);
			fail("Allows for no arguments.");
		} catch (Exception e) { /* Expected */
		}
	}

	/**
	 * Test Registry Extractor with absolute paths and max query = 1
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExtractorAbsolute() {
		try {
			String[] args = {
					"-i",
					"-e", // Only run Extractor and Indexer components
					"-r",
					TestConstants.PDS3_REGISTRY_URL,
					"-H",
					System.getProperty("user.dir") + "/"
							+ TestConstants.SEARCH_HOME_RELATIVE,
					"-e",
					"-m", "1",
					"-c", System.getProperty("user.dir") + "/"
							+ TestConstants.CONFIG_DIR_RELATIVE + "pds/pds3", };
			SearchCoreLauncher.main(args);
		} catch (Exception e) {
			fail("Registry Extractor with Absolute Paths failed: "
					+ e.getMessage());
		}
	}

	/**
	 * Test Registry Extractor with relative paths and max query = 1
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExtractorRelative() {
		try {
			String[] args = {
					"-i",
					"-e", // Only run Extractor and Indexer components
					"-r", TestConstants.PDS3_REGISTRY_URL, 
					"-H", TestConstants.SEARCH_HOME_RELATIVE,
					"-e",
					"-m", "1", 
					"-c", TestConstants.CONFIG_DIR_RELATIVE + "pds/pds3", };
			SearchCoreLauncher.main(args);
		} catch (Exception e) {
			fail("Registry Extractor with Relative Paths failed: "
					+ e.getMessage());
		}
	}

}
