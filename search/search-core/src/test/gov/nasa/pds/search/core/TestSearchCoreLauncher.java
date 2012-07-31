package gov.nasa.pds.search.core;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.junit.After;
import org.junit.Before;

import gov.nasa.pds.search.core.SearchCoreLauncher;
import gov.nasa.pds.search.core.extractor.RegistryExtractor;
import gov.nasa.pds.search.core.indexer.pds.Indexer;
import gov.nasa.pds.search.core.indexer.solr.SolrIndexer;

//JUnit imports
import junit.framework.TestCase;

/**
 * Tests SearchCoreLauncher CLI for proper behavior
 * 
 * @author jpadams
 *
 */
public class TestSearchCoreLauncher extends TestCase {
	
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
	 * Test arguments are not empty
	 */
	public void testEmptyArgs() {
		// Test empty args
        String[] args = { };
        try {
        	SearchCoreLauncher.main(args);
        	fail("Allows for no arguments.");
        } catch (Exception e) { /* Expected */ }
	}
	
	/**
	 * Test for missing required arguments
	 */
	public void testRequiredArgs() {
	    String[] args = { "-H", TEST_SERVICE_HOME_RELATIVE, 
	    		"-e", 
	    		"-m", "5", 
	    		"-c", TEST_CONFIG_DIR_RELATIVE + "pds" };
	    try {
	    	SearchCoreLauncher.main(args);
	    	fail("Allows for registry not specified");
	    } catch (Exception e) { /* Expected */ }
	}
	
	/**
	 * Test using an invalid argument
	 */
	public void testInvalidArg() {
        String[] args = { "-H", TEST_SERVICE_HOME_RELATIVE, 
        		"-e", 
        		"-m", "5", 
        		"-c", TEST_CONFIG_DIR_RELATIVE + "pds", 
        		"-x" };
        try {
        	SearchCoreLauncher.main(args);
        	fail("Allows invalid flag.");
        } catch (Exception e) { /* Expected */ }  
	}

    /**
     * Test Registry Extractor with absolute paths and max query = 5
     * @throws Exception 
     */
    public void testExtractorAbsolute() {
    	try {
	    	String[] args = { "-r", TEST_PDS_REGISTRY_URL, 
	    			"-H", TEST_ABSOLUTE_PATH + TEST_SERVICE_HOME_RELATIVE, 
	    			"-e", 
	    			"-m", "5", 
	    			"-c", TEST_ABSOLUTE_PATH + TEST_CONFIG_DIR_RELATIVE + "pds", };
	    	SearchCoreLauncher.main(args);
    	} catch (Exception e) {
    		fail("Registry Extractor with Absolute Paths failed: " + e.getMessage());
    	}
    }
    
    /**
     * Test Registry Extractor with relative paths and max query = 5
     * @throws Exception 
     */
    public void testExtractorRelative() {
    	try {
	    	String[] args = { "-r", TEST_PDS_REGISTRY_URL, 
	    			"-H", TEST_SERVICE_HOME_RELATIVE, 
	    			"-e", 
	    			"-m", "5", 
	    			"-c", TEST_CONFIG_DIR_RELATIVE + "pds", };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			fail("Registry Extractor with Absolute Paths failed: " + e.getMessage());
		}
    }
	
    /**
     * Test Registry Extractor with PSA data and max query = 5
     * @throws Exception 
     */
    // TODO - Change once 
    /*public void testExtractorPSA() {
    	try {
	        String[] args = { "-r", TEST_PSA_REGISTRY_URL,
	        		"-H", TEST_SERVICE_HOME_RELATIVE, 
	        		"-e", 
	        		"-m", "5", 
	        		"-c", TEST_CONFIG_DIR_RELATIVE + "psa" };
	        SearchCoreLauncher.main(args);
    	} catch (Exception e) {
    		fail("Could not extract PSA data: " + e.getMessage());
    	}
    }*/
	
}
