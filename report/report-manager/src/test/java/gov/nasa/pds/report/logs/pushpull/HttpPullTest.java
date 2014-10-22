package gov.nasa.pds.report.logs.pushpull;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jasypt.util.text.StrongTextEncryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.HttpTestConstants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.logs.pushpull.model.LogSet;
import gov.nasa.pds.report.rules.PDSTest;
import gov.nasa.pds.report.rules.PDSTest.SingleTestRule;
import gov.nasa.pds.report.util.DateLogFilter;
import gov.nasa.pds.report.util.Utility;

public class HttpPullTest extends PDSTest{
	
	private static HttpPull httpPull;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		httpPull = new HttpPull();
	}
	
	@Before
	public void setUp() throws Exception {
		FileUtils.forceMkdir(new File(Utility.getAbsolutePath(
				TestConstants.TEST_DUMP_RELATIVE)));
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.forceDelete(new File(Utility.getAbsolutePath(
				TestConstants.TEST_DUMP_RELATIVE)));
	}
	
	@Test
	public void testGetLog() throws Exception {
			
		// Get the hostname because we will use it a few times
		String hostname = HttpTestConstants.TEST_HOST;
		
		// Create the directory to which files will be pulled during the test
		String destination = TestConstants.TEST_STAGING_DIR +
				HttpTestConstants.TEST_NODE;
		FileUtils.forceMkdir(new File(destination));
		
		// Remove any Date Logs Filters from previous tests
		DateLogFilter.unsetAll();
		
		// Connect to the node
		boolean connected = httpPull.connect(hostname,
				HttpTestConstants.TEST_USER, HttpTestConstants.TEST_PASSWORD,
				false);
		if(!connected){
			fail("Connecting to " + hostname + " while testing the FTP log " +
					"puller failed.");
		}
		
		// Pull the logs to the desired destination
		httpPull.pull(HttpTestConstants.TEST_PATH, destination);
        
        // Check if it worked
		String filename = HttpTestConstants.TEST_PATH.substring(
				HttpTestConstants.TEST_PATH.lastIndexOf("/") + 1);
		File file = new File(destination, filename);
        assertTrue(file.getAbsolutePath() + " was not downloaded", file.exists());
        
	}
	
	@Test
	public void testGetLogs() throws Exception {
			
		// Get the URL of the page that contains the file at the test path
		String filepath = HttpTestConstants.TEST_PATH;
		String path = filepath.substring(0, filepath.lastIndexOf("/"));
		
		// Get the hostname because we will use it a few times
		String hostname = HttpTestConstants.TEST_HOST;
		
		// Create the directory to which files will be pulled during the test
		String destination = TestConstants.TEST_STAGING_DIR +
				HttpTestConstants.TEST_NODE;
		FileUtils.forceMkdir(new File(destination));
		
		// Remove any Date Logs Filters from previous tests
		DateLogFilter.unsetAll();
		
		// Connect to the node
		boolean connected = httpPull.connect(hostname,
				HttpTestConstants.TEST_USER, HttpTestConstants.TEST_PASSWORD,
				false);
		if(!connected){
			fail("Connecting to " + hostname + " while testing the FTP log " +
					"puller failed.");
		}
		
		// Pull the logs to the desired destination
		httpPull.pull(path, destination);
        
        // Check if it worked
		String filename = HttpTestConstants.TEST_PATH.substring(
				HttpTestConstants.TEST_PATH.lastIndexOf("/") + 1);
		File file = new File(destination, filename);
        assertTrue(file.getAbsolutePath() + " was not downloaded", file.exists());
        
	}
	
}
