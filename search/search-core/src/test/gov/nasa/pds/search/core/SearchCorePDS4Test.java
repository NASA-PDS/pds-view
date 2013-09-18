//	Copyright 2009-2012, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	'$'Id
//
package gov.nasa.pds.search.core;

import static org.junit.Assert.*;

import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.test.SearchCoreTest;
import gov.nasa.pds.search.core.test.SearchCoreTest.SingleTestRule;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Search Core End-to-end Test with PDS4 Data for {@link SearchCoreLauncher}.
 *
 * @author jpadams
 */
@RunWith(JUnit4.class)
public class SearchCorePDS4Test extends SearchCoreTest {
	
	/** Search Service Solr URL **/
	public static final String SOLR_SERVER_URL="http://localhost:8080/search-service/pds";
	
	/** Relative Search Service Directory Path for testing locally **/
	public static final String SEARCH_HOME_RELATIVE="./target/test";
	
	/** Relative Config Directory Path for testing locally **/
	public static final String CONFIG_DIR_RELATIVE="./src/main/resources/conf/";
	
	@BeforeClass
	public static void oneTimeSetUp() {
		File testDir = new File(System.getProperty("user.dir") + "/" + SEARCH_HOME_RELATIVE);
		testDir.mkdirs();
	}
	
	/*@AfterClass
	public static void oneTimeTearDown() throws IOException {
		File testDir = new File(System.getProperty("user.dir") + "/" + SEARCH_HOME_RELATIVE);
		FileUtils.deleteDirectory(testDir);
	}*/
	
    /**
     * Test End-To-End with PDS Data, relative paths and max query = 5
     * @throws Exception
     */
	@Test
    public void testLauncherPDS4() {
    	try {
	    	String[] args = { "-i", "-e",	// Only run Extractor and Indexer components
	    			"-r", TestConstants.PDS4_ATM_REGISTRY_URL,
	    			"-H", TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-m", "5", 
	    			"-c", TestConstants.TEST_DIR_RELATIVE+ "pds4-config",
	    			"-v", "0" };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Registry Extractor with Relative Paths failed: " + e.getMessage());
		}
    }
    /**
     * Test End-To-End with PDS Data, including Solr Post
     * @throws Exception
     */
	@Test
	@Ignore
    public void testLauncherPDS4WithSolrPost() {
    	try {
	    	String[] args = { "-r", TestConstants.PDS4_ATM_REGISTRY_URL,
	    			"-H", TestConstants.SEARCH_HOME_RELATIVE, 
	    			"-m", "5", 
	    			"-c", TestConstants.CONFIG_DIR_RELATIVE + "pds/pds4",
	    			"-v", "0" };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Registry Extractor with Relative Paths failed: " + e.getMessage());
		}
    }
	
}