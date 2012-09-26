package gov.nasa.pds.search.core;

import java.io.File;

import org.junit.After;
import org.junit.Before;

import gov.nasa.pds.search.core.constants.TestConstants;
import junit.framework.TestCase;

public class SearchCorePSATest  extends TestCase {
	
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
	    					TestConstants.CONFIG_DIR_RELATIVE + "psa/psa-search-service.properties",
	    			//"-e", 
	    			"-m", "5",
	    			};
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			fail("Registry Extractor with Properties File failed: " + e.getMessage());
		}
    }

}
