package gov.nasa.pds.search.core;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import gov.nasa.pds.search.core.constants.TestConstants;
import junit.framework.TestCase;

public class SearchCorePSATest  extends TestCase {
	
	@Before public void setUp() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		testDir.mkdirs();
	}
	
	@After public void tearDown() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		testDir.delete();
	}
	
    /**
     * Test End-To-End with PSA Data, relative paths and max query = 5
     * @throws Exception 
     */
    public void testExtractorRelative() {
    	try {
	    	String[] args = { "-r", TestConstants.PSA_REGISTRY_URL, 
	    			"-H", TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-m", "5", 
	    			"-c", TestConstants.CONFIG_DIR_RELATIVE + "psa/pds3", };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			fail("Registry Extractor with Relative Paths failed: " + e.getMessage());
		}
    }

}
