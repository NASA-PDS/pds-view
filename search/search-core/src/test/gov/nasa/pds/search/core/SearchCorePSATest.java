package gov.nasa.pds.search.core;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import gov.nasa.pds.search.core.constants.TestConstants;
import junit.framework.TestCase;

/**
 * Search Core End-to-end Test with PSA Data for {@link SearchCoreLauncher}.
 *
 * @author jpadams
 */
@RunWith(JUnit4.class)
public class SearchCorePSATest {
	
	@BeforeClass
	public static void oneTimeSetUp() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		testDir.mkdirs();
	}
	
	@AfterClass
	public static void oneTimeTearDown() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		testDir.delete();
	}
	
    /**
     * Test End-To-End with PSA Data, relative paths and max query = 5
     * @throws Exception 
     */
	@Test
    public void testExtractorRelative() {
    	try {
	    	String[] args = { //"-d", 
	    			"-r", TestConstants.PSA_REGISTRY_URL, 
	    			"-H", TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-m", "5", 
	    			"-c", TestConstants.CONFIG_DIR_RELATIVE + "psa/pds3", };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			fail("Registry Extractor with Relative Paths failed: " + e.getMessage());
		}
    }

}
