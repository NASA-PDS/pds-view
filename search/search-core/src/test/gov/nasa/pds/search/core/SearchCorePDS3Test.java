package gov.nasa.pds.search.core;

import static org.junit.Assert.*;
import gov.nasa.pds.search.core.constants.TestConstants;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests of PDS3 Data for {@link SearchCoreLauncher}.
 *
 * @author jpadams
 */
@RunWith(JUnit4.class)
public class SearchCorePDS3Test {

	@BeforeClass
	public static void oneTimeSetUp() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		testDir.mkdirs();
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws IOException {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		FileUtils.deleteDirectory(testDir);
	}
	
    /**
     * Test End-To-End with PDS Data, relative paths and max query = 5
     * @throws Exception
     */
	@Test
    public void testExtractorRelative() {
    	try {
	    	String[] args = { "-d", "-r", TestConstants.PDS3_REGISTRY_URL, 
	    			"-H", TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-m", "1", 
	    			"-c", TestConstants.CONFIG_DIR_RELATIVE + "pds/pds3", };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Registry Extractor with Relative Paths failed: " + e.getMessage());
		}
    }
	
}
