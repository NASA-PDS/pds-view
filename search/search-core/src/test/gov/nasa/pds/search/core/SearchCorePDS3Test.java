package gov.nasa.pds.search.core;

import gov.nasa.pds.search.core.constants.TestConstants;

import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

public class SearchCorePDS3Test extends TestCase {

	@Before public void setUp() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		testDir.mkdirs();
	}
	
	@After public void tearDown() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		testDir.delete();
	}
	
    /**
     * Test End-To-End with PDS Data, relative paths and max query = 5
     * @throws Exception
     */
    public void testExtractorRelative() {
    	try {
	    	String[] args = { "-r", TestConstants.PDS3_REGISTRY_URL, 
	    			"-H", TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-m", "5", 
	    			"-c", TestConstants.CONFIG_DIR_RELATIVE + "pds/pds3", };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			fail("Registry Extractor with Relative Paths failed: " + e.getMessage());
		}
    }
	
}
