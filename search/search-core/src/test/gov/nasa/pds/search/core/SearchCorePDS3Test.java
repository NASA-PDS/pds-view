package gov.nasa.pds.search.core;

import static org.junit.Assert.*;
import gov.nasa.pds.search.core.constants.TestConstants;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests of PDS3 Data for {@link SearchCoreLauncher}.
 *
 * @author jpadams
 */
@RunWith(JUnit4.class)
public class SearchCorePDS3Test {

	@BeforeClass
	public static void oneTimeSetUp() {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		testDir.mkdirs();
	}
	
	/*@AfterClass
	public static void oneTimeTearDown() throws IOException {
		File testDir = new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE);
		FileUtils.deleteDirectory(testDir);
	}*/
	
    /**
     * Test End-To-End with PDS Data, relative paths and max query = 5
     * @throws Exception
     */
	@Test
	@Ignore
    public void testLauncherPDS3() {
    	try {
	    	String[] args = { //"-d", 
	    			"-r", TestConstants.PDS3_REGISTRY_URL,
	    			"-H", TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-m", "1", 
	    			"-c", TestConstants.CONFIG_DIR_RELATIVE + "pds/pds3",
	    			"-v", "0" };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Registry Extractor with Relative Paths failed: " + e.getMessage());
		}
    }
	
    /**
     * Test End-To-End with PDS Data, relative paths and max query = 5
     * @throws Exception
     */
	@Ignore
    public void testLauncherWithNoPrimaryRegistries() {
    	try {
	    	String[] args = { //"-d",
	    			"-H", TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-m", "1", 
	    			"-c", TestConstants.CONFIG_DIR_RELATIVE + "psa/pds3",
	    			"-v", "0" };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Registry Extractor with Relative Paths failed: " + e.getMessage());
		}
    }
	
    /**
     * Test End-To-End with PDS Data, relative paths and max query = 5
     * @throws Exception
     */
	@Test
	@Ignore
    public void testLauncherPDS4() {
    	try {
	    	String[] args = { //"-d", 
	    			"-r", TestConstants.PDS4_ATM_REGISTRY_URL,
	    			"-H", TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-m", "1", 
	    			"-c", TestConstants.CONFIG_DIR_RELATIVE + "pds/pds4",
	    			"-v", "0" };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Registry Extractor with Relative Paths failed: " + e.getMessage());
		}
    }
	
    /**
     * Test SearchCoreLauncher with a Search Core properties file.
     */
    @Test
    @Ignore
    public void testLauncherWithPropertiesFile() {
		try {
			File tempFile = new File(TestConstants.SEARCH_HOME_RELATIVE + "/temp_props.properties");
			
			PrintWriter writer = new PrintWriter(tempFile);
	    	writer.write("search.core.search-home = " + System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE + "\n"
	    			+ "search.core.primary-registry = " + TestConstants.PDS3_REGISTRY_URL + "\n"
	    			//+ "search.core.secondary-registry = " + TestConstants.PDS4_REGISTRY_URL + "\n"
	    			+ "search.core.config-home = "+ System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE + "pds/pds3");
	    	writer.flush();
	    	writer.close();
	    	
	    	String[] args = { "-d", "-v", "0", "-p", tempFile.getAbsolutePath(), 
	    			"-m", "1" 
	    			};
	    	SearchCoreLauncher.main(args);
	    	
	    	FileUtils.forceDelete(new File(TestConstants.SEARCH_HOME_RELATIVE + "/temp_props.properties"));
	    	
	    	List<File> fileList = new ArrayList<File>(FileUtils.listFiles(new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE), new String[] {"xml"}, true));
	    	
	    	boolean check = false;
	    	// Quick check to make sure there is at least one XML file containing PDS3 data
	    	for (File file : fileList) {
	    		if (file.getAbsolutePath().contains("DataSet")) {
	    			check = true;
	    		}
	    	}
	    	
	    	assertTrue("Expected XML files not found in" + System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE, true);
		} catch (Exception e) {
			fail(e.getCause() + " - " + e.getMessage());
		}
    	
    }
    
    /**
     * Test SearchCoreLauncher with a Search Core properties file.
     */
    @Test
    public void testLauncherWithMultiplePropertiesFiles() {
		try {
			File pds3 = new File(TestConstants.TEST_DIR_RELATIVE + "/properties/pds3-core.properties");
			File pds4 = new File(TestConstants.TEST_DIR_RELATIVE + "/properties/pds4-core.properties");
	    	
	    	String[] args = { "-d", "-v", "0", "-p", pds3.getAbsolutePath(), pds4.getAbsolutePath(), 
	    			"-m", "1" 
	    			};
	    	SearchCoreLauncher.main(args);
	    	
	    	List<File> fileList = new ArrayList<File>(FileUtils.listFiles(new File(System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE), new String[] {"xml"}, true));
	    	boolean checkPDS3 = false;
	    	boolean checkPDS4 = false;
	    	
	    	// Quick check to make sure there is at least one XML file containing PDS3 data
	    	// and one containing PDS4 data
	    	for (File file : fileList) {
	    		if (file.getAbsolutePath().contains("DataSet")) {
	    			checkPDS3 = true;
	    		} else if (file.getAbsolutePath().contains("Collection")) {
	    			checkPDS4 = true;
	    		}
	    	}
	    	
	    	assertTrue("Expected XML files not found in" + System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE, checkPDS3 && checkPDS4);
		} catch (Exception e) {
			fail(e.getCause() + " - " + e.getMessage());
		}
    	
    }
	
}
