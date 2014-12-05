package gov.nasa.pds.search.core;

import static org.junit.Assert.fail;
import gov.nasa.pds.search.core.constants.TestConstants;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Search Core End-to-end Test with PSA Data for {@link SearchCoreLauncher}.
 * 
 * @author jpadams
 */
@RunWith(JUnit4.class)
public class SearchCorePSATest {

	@BeforeClass
	public static void oneTimeSetUp() {
		File testDir = new File(System.getProperty("user.dir") + "/"
				+ TestConstants.SEARCH_HOME_RELATIVE);
		testDir.mkdirs();
	}

	/*
	 * @AfterClass public static void oneTimeTearDown() throws IOException {
	 * File testDir = new File(System.getProperty("user.dir") + "/" +
	 * TestConstants.SEARCH_HOME_RELATIVE); FileUtils.deleteDirectory(testDir);
	 * }
	 */

	/**
	 * Test End-To-End with PSA Data
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testLauncherPSA() {
		try {
			String[] args = { "-d",
					"-i",
					"-e", // Only run Extractor and Indexer components
					"-r", TestConstants.PSA_REGISTRY_URL, "-H",
					TestConstants.SEARCH_HOME_RELATIVE, "-m", "5", "-c",
					TestConstants.TEST_DIR_RELATIVE + "config/psa", "-v", "0" };
			SearchCoreLauncher.main(args);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Registry Extractor with Relative Paths failed: "
					+ e.getMessage());
		}
	}
}
