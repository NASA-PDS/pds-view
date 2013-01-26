package gov.nasa.pds.search.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

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
public class SearchCoreLauncherTest extends TestCase {
	
	@Before public void setUp() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		testDir.mkdirs();
	}
	
	@After public void tearDown() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		testDir.delete();
	}
	
	/**
	 * Test arguments are not empty
	 */
	@Ignore
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
	@Ignore
	public void testRequiredArgs() {
	    String[] args = { "-H", TestConstants.SEARCH_HOME_RELATIVE, 
	    		"-e", 
	    		"-m", "5", 
	    		"-c", TestConstants.CONFIG_DIR_RELATIVE + "pds" };
	    try {
	    	SearchCoreLauncher.main(args);
	    	fail("Allows for registry not specified");
	    } catch (Exception e) { /* Expected */ }
	}
	
	/**
	 * Test using an invalid argument
	 */
	@Ignore
	public void testInvalidArg() {
        String[] args = { "-H", TestConstants.SEARCH_HOME_RELATIVE, 
        		"-e", 
        		"-m", "5", 
        		"-c", TestConstants.CONFIG_DIR_RELATIVE + "pds", 
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
	@Ignore
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
    
    /**
     * Test Registry Extractor with relative paths and max query = 5
     * @throws Exception 
     */
    @Ignore
    public void testExtractorRelative() {
    	try {
	    	String[] args = { "-r", TestConstants.PDS3_REGISTRY_URL, 
	    			"-H", TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-e", 
	    			"-m", "5", 
	    			"-c", TestConstants.CONFIG_DIR_RELATIVE + "pds", };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			fail("Registry Extractor with Relative Paths failed: " + e.getMessage());
		}
    }
    
    /**
     * Test SearchCoreLauncher with a Search Core properties file.
     */
    public void testProps() {
		try {
			File tempFile = new File(TestConstants.SEARCH_HOME_RELATIVE + "/temp_props.properties");
			
			PrintWriter writer = new PrintWriter(tempFile);
	    	writer.write("search.core.search-home = " + TestConstants.SEARCH_HOME_RELATIVE + "\n"
	    			+ "search.core.registry-url = " + TestConstants.PDS3_REGISTRY_URL + "\n"
	    			+ "search.core.config-home = "+ TestConstants.CONFIG_DIR_RELATIVE + "pds");
	    	writer.flush();
	    	writer.close();
	    	
	    	String[] args = { "-p", tempFile.getAbsolutePath(), 
	    			"-m", "1" 
	    			};
	    	SearchCoreLauncher.main(args);
	    	
	    	FileUtils.forceDelete(new File(TestConstants.SEARCH_HOME_RELATIVE + "/temp_props.properties"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
	
}
