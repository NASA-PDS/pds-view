package gov.nasa.pds.search.core.extractor;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.formatter.SearchCoreFormatter;
import gov.nasa.pds.search.core.logging.handler.SearchCoreStreamHandler;
import gov.nasa.pds.search.core.test.SearchCoreTest;
import gov.nasa.pds.search.core.test.SearchCoreTest.SingleTestRule;
import gov.nasa.pds.search.core.util.Debugger;

/**
 * Tests for {@link RegistryExtractor}.
 *
 * @author jpadams
 */
@RunWith(JUnit4.class)
public class RegistryExtractorTest extends SearchCoreTest {
	
	private static final String TEST_DIR = System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE;
	private RegistryExtractor re;
	
    @Rule
    public SingleTestRule test = new SingleTestRule("");
	
	@Before
	public void setUp() throws SearchCoreFatalException {
		RegistryExtractor.prepForRun(TEST_DIR, true);
		this.re = new RegistryExtractor(TEST_DIR, new File(System.getProperty("user.dir") + "/" + TestConstants.TEST_DIR_RELATIVE + "config"), 
				Arrays.asList(TestConstants.PDS3_REGISTRY_URL), new ArrayList<String>());
		this.re.setQueryMax(2);
	}
	
	@Test
	public void testCoreConfigList() {
		System.out.println("---------------------------------------------");
		System.out.println("--- Test RegistryExtractor.coreConfigList ---");
		System.out.println("---------------------------------------------");
		// Create list to compare to output from test run
		List<File> correctList = new ArrayList<File>();
		correctList.add(new File(System.getProperty("user.dir") + "/" + TestConstants.TEST_DIR_RELATIVE + "config/core-config-test-1.xml"));
		correctList.add(new File(System.getProperty("user.dir") + "/" + TestConstants.TEST_DIR_RELATIVE + "config/core-config-test-2.xml"));
		
		try {
			List<File> configList = this.re.getCoreConfigs(new File(System.getProperty("user.dir") + "/" + TestConstants.TEST_DIR_RELATIVE + "config"));
			assertEquals(correctList, configList);
		} catch (Exception e) {
			e.printStackTrace();
			fail("RegistryExtractor.getCoreConfigList Test Failed.  See stack trace for error.");
		}
	}
	
	@Test
	public void testRun() {
		System.out.println("-----------------------------------------");
		System.out.println("--- Test run method with Test Configs ---");
		System.out.println("-----------------------------------------");
		try {
			this.re.run();
		} catch (Exception e) {
			e.printStackTrace();
			fail("RegistryExtractor.run Test Failed.  See stack trace for error.");
		}
	}
	
	@Test
	public void testRunPDS3() {
		System.out.println("-----------------------------------------------");
		System.out.println("--- Test run method with PDS - PDS3 Configs ---");
		System.out.println("-----------------------------------------------");
		try {
			this.re.setConfDir(new File(System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE + "/pds/pds3"));
			this.re.run();
		} catch (Exception e) {
			e.printStackTrace();
			fail("RegistryExtractor.run Test Failed.  See stack trace for error.");
		}
	}
	
	@Test
	public void testRunPDS4() {
		System.out.println("-----------------------------------------------");
		System.out.println("--- Test run method with PDS - PDS4 Configs ---");
		System.out.println("-----------------------------------------------");
		try {
			this.re.setConfDir(new File(System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE + "/pds/pds4"));
			this.re.setPrimaryRegistries(Arrays.asList(TestConstants.PDS4_ATM_REGISTRY_URL));
			this.re.run();
		} catch (Exception e) {
			e.printStackTrace();
			fail("RegistryExtractor.run Test Failed.  See stack trace for error.");
		}
	}
	
	@Test
	public void testRunPSA() throws SearchCoreFatalException {
		System.out.println("-----------------------------------------------");
		System.out.println("--- Test run method with PSA - PDS3 Configs ---");
		System.out.println("-----------------------------------------------");
		try {
			this.re.setConfDir(new File(System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE + "/psa/pds3"));
			this.re.setPrimaryRegistries(Arrays.asList(TestConstants.PSA_REGISTRY_URL));
			//this.re.setBackupRegistries(Arrays.asList(TestConstants.PDS3_REGISTRY_URL));
			this.re.run();
		} catch (Exception e) {
			e.printStackTrace();
			fail("RegistryExtractor.run Test Failed.  See stack trace for error.");
		}
	}
}
