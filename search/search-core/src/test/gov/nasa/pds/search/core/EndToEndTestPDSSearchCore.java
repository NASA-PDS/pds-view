package gov.nasa.pds.search.core;

import java.io.File;

import org.junit.After;
import org.junit.Before;

import gov.nasa.pds.search.core.SearchCoreLauncher;
import gov.nasa.pds.search.core.constants.TestConstants;

//JUnit imports
import junit.framework.TestCase;

/**
 * Tests SearchCoreLauncher CLI for proper behavior
 * 
 * @author jpadams
 *
 */
public class EndToEndTestPDSSearchCore extends TestCase {
	
	@Before public void setUp() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		testDir.mkdirs();
	}
	
	@After public void tearDown() {
		//File testDir = new File(TEST_ABSOLUTE_PATH + TEST_SERVICE_HOME_RELATIVE);
		//testDir.delete();
	}
	
    /**
     * Test End to End Search Core.  Use -m flag to put cap on query for testing purposes
     * @throws Exception 
     */
    public void testPDSEndToEnd() {
    	try {
	    	String[] args = { "-r", TestConstants.PDS3_REGISTRY_URL, 
	    			"-H", System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-m", "5",
	    			"-c", System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE + "pds", };
	    	SearchCoreLauncher.main(args);
    	} catch (Exception e) {
    		fail("Registry Extractor with Absolute Paths failed: " + e.getMessage());
    	}
    }
	
}
