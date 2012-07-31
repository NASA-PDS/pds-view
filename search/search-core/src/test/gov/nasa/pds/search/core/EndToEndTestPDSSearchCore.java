package gov.nasa.pds.search.core;

import java.io.File;

import org.junit.After;
import org.junit.Before;

import gov.nasa.pds.search.core.SearchCoreLauncher;

//JUnit imports
import junit.framework.TestCase;

/**
 * Tests SearchCoreLauncher CLI for proper behavior
 * 
 * @author jpadams
 *
 */
public class EndToEndTestPDSSearchCore extends TestCase {
	
	private static final String TEST_ABSOLUTE_PATH="/Users/jpadams/Documents/workspace/2010-workspace/search/search-core/";
	private static final String TEST_CONFIG_DIR_RELATIVE="./src/main/resources/conf/";
	private static final String TEST_SERVICE_HOME_RELATIVE="./target/test";
	private static final String TEST_PDS_REGISTRY_URL="http://pdsdev.jpl.nasa.gov:8080/registry";
	private static final String TEST_PSA_REGISTRY_URL="http://ipda.jpl.nasa.gov/registry";
	
	@Before public void setUp() {
		File testDir = new File(TEST_ABSOLUTE_PATH + TEST_SERVICE_HOME_RELATIVE);
		testDir.mkdirs();
	}
	
	@After public void tearDown() {
		File testDir = new File(TEST_ABSOLUTE_PATH + TEST_SERVICE_HOME_RELATIVE);
		testDir.delete();
	}
	
    /**
     * Test Registry Extractor with absolute paths.  Use -m flag to put cap on query for testing purposes
     * @throws Exception 
     */
    public void testExtractorAbsolute() {
    	try {
	    	String[] args = { "-r", TEST_PDS_REGISTRY_URL, 
	    			"-H", TEST_ABSOLUTE_PATH + TEST_SERVICE_HOME_RELATIVE, 
	    			"-e", 
	    			"-c", TEST_ABSOLUTE_PATH + TEST_CONFIG_DIR_RELATIVE + "pds", };
	    	SearchCoreLauncher.main(args);
    	} catch (Exception e) {
    		fail("Registry Extractor with Absolute Paths failed: " + e.getMessage());
    	}
    }
	
}
