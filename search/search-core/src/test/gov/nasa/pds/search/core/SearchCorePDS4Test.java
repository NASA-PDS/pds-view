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

import java.io.File;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

public class SearchCorePDS4Test extends TestCase {

	/** Registry URL for PDS4 Products used for testing purposes **/
	public static final String PDS4_REGISTRY_URL="http://pdsdev.jpl.nasa.gov:8080/registry-phx";
	
	/** Search Service Solr URL **/
	public static final String SOLR_SERVER_URL="http://localhost:8080/search-service/pds";
	
	/** Relative Search Service Directory Path for testing locally **/
	public static final String SEARCH_HOME_RELATIVE="./target/test";
	
	/** Relative Config Directory Path for testing locally **/
	public static final String CONFIG_DIR_RELATIVE="./src/main/resources/conf/";
	
	@Before public void setUp() {
		File testDir = new File(System.getProperty("user.dir") + "/" + SEARCH_HOME_RELATIVE);
		testDir.mkdirs();
	}
	
	@After public void tearDown() {
		File testDir = new File(System.getProperty("user.dir") + "/" + SEARCH_HOME_RELATIVE);
		testDir.delete();
	}
	
    /**
     * Test End-To-End with PDS Data, relative paths and max query = 5
     * @throws Exception 
     */
    public void testCorePDS4() {
    	try {
	    	String[] args = { "-r", PDS4_REGISTRY_URL, 
	    			"-H", SEARCH_HOME_RELATIVE, 
	    			"-m", "5", 
	    			"-c", CONFIG_DIR_RELATIVE + "pds/pds4", };
	    	SearchCoreLauncher.main(args);
		} catch (Exception e) {
			fail("Search Core for PDS4 Products failed: " + e.getMessage());
		}
    }
	
}