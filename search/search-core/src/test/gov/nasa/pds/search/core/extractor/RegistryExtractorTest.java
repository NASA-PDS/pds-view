package gov.nasa.pds.search.core.extractor;

import org.junit.Ignore;

import junit.framework.TestCase;
import gov.nasa.pds.search.core.SearchCoreLauncher;
import gov.nasa.pds.search.core.constants.TestConstants;

@Ignore
public class RegistryExtractorTest extends TestCase {

    /**
     * Test Registry Extractor with absolute paths and max query = 5
     * @throws Exception 
     */
    public void testExtractorAbsolute() {
    	try {
	    	String[] args = { "-r", TestConstants.PDS3_REGISTRY_URL, 
	    			"-H", System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-e", 
	    			"-m", "5", 
	    			"-c", System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE + "pds", };
	    	SearchCoreLauncher.main(args);
    	} catch (Exception e) {
    		fail("Registry Extractor with Absolute Paths failed: " + e.getMessage());
    	}
    }
}
