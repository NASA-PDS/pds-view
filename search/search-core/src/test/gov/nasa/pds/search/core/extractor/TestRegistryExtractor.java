package gov.nasa.pds.search.core.extractor;

import org.junit.Ignore;

import junit.framework.TestCase;
import gov.nasa.pds.search.core.SearchCoreLauncher;
import gov.nasa.pds.search.core.constants.TestConstants;

@Ignore
public class TestRegistryExtractor extends TestCase {

    /**
     * Test Registry Extractor with absolute paths and max query = 5
     * @throws Exception 
     */
    public void testExtractorAbsolute() {
    	try {
	    	String[] args = { "-r", TestConstants.PDS_REGISTRY_URL, 
	    			"-H", TestConstants.CORE_HOME + TestConstants.SERVICE_HOME_RELATIVE, 
	    			"-e", 
	    			"-m", "5", 
	    			"-c", TestConstants.CORE_HOME + TestConstants.CONFIG_DIR_RELATIVE + "pds", };
	    	SearchCoreLauncher.main(args);
    	} catch (Exception e) {
    		fail("Registry Extractor with Absolute Paths failed: " + e.getMessage());
    	}
    }
}
