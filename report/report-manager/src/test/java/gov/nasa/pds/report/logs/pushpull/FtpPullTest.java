package gov.nasa.pds.report.logs.pushpull;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
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
import gov.nasa.pds.report.constants.FtpTestConstants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.logs.pushpull.model.LogSet;
import gov.nasa.pds.report.rules.PDSTest;
import gov.nasa.pds.report.rules.PDSTest.SingleTestRule;
import gov.nasa.pds.report.util.DateLogFilter;
import gov.nasa.pds.report.util.Utility;

public class FtpPullTest extends PDSTest{
	
	private static FtpPull ftpPull;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ftpPull = new FtpPull();
	}
	
	@Before
	public void setUp() throws Exception {
		
		FileUtils.forceMkdir(new File(Utility.getAbsolutePath(
				TestConstants.TEST_DUMP_RELATIVE)));
		System.setProperty(Constants.DATE_FILTER_PROP,
		"gov.nasa.pds.report.util.DelimitedDateFilter");
		
	}
	
	@After
	public void tearDown() throws Exception {
		try {
			FileUtils.forceDelete(new File(Utility.getAbsolutePath(
				TestConstants.TEST_DUMP_RELATIVE)));
		} catch (IOException ex) {}
	}
	
	@Test
	public void testConnect() throws PushPullException {
		assertTrue(ftpPull.connect(FtpTestConstants.TEST_HOST,
				FtpTestConstants.TEST_USER, FtpTestConstants.TEST_PASSWORD,
				true));
	}
	
	@Test
	public void testGetLogs() throws Exception {
			
		// Get the hostname because we will use it a few times
		String hostname = FtpTestConstants.TEST_HOST;
		
		// Create the directory to which files will be pulled during the test
		String destination = TestConstants.TEST_STAGING_DIR + hostname;
		FileUtils.forceMkdir(new File(destination));
		
		// Remove any Date Logs Filters from previous tests
		DateLogFilter.unsetAll();
		
		// Connect to the node
		boolean connected = ftpPull.connect(hostname,
				FtpTestConstants.TEST_USER, FtpTestConstants.TEST_PASSWORD,
				false);
		if(!connected){
			fail("Connecting to " + hostname + " while testing the FTP log " +
					"puller failed.");
		}
		
		// Pull the logs to the desired destination
		ftpPull.pull(FtpTestConstants.TEST_PATH, destination);
        
        // Check if it worked
        assertTrue(hostname + " failed", (new File(destination + "/" + 
        		new File(FtpTestConstants.TEST_PATH).getName())).exists());
        
	}
	
}
