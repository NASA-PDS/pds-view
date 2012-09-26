package gov.nasa.pds.search.core;

import gov.nasa.pds.search.core.constants.TestConstants;

import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

public class SearchCorePDSTest extends TestCase {

	@Before public void setUp() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SERVICE_HOME_RELATIVE);
		testDir.mkdirs();
	}
	
	@After public void tearDown() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SERVICE_HOME_RELATIVE);
		testDir.delete();
	}
	
    /**
     * Test Registry Extractor with PSA data and max query = 5
     * @throws Exception 
     */
    public void testExtractorMultipleProps() {
    	try {
	    	String[] args = { "-p",  
	    					TestConstants.CONFIG_DIR_RELATIVE + "pds/pds-search-service.properties",
	    			//"-e", 
	    			"-m", "5"
	    			};
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			fail("Registry Extractor with Properties File failed: " + e.getMessage());
		}
    }
	
}
