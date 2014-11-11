package gov.nasa.pds.report.logs;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.constants.SftpTestConstants;
import gov.nasa.pds.report.rules.PDSTest;
import gov.nasa.pds.report.util.DateLogFilter;
import gov.nasa.pds.report.util.Utility;

public class PDSLogsManagerTest extends PDSTest{
	
	private static PDSLogsManager manager;
	private static Properties props;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@Before
	public void setUp() throws Exception {
		manager = new PDSLogsManager();
		props = new Properties();
		props.setProperty(Constants.NODE_XFER_TYPE_KEY, "sftp");
		props.setProperty(Constants.NODE_ID_KEY,
				SftpTestConstants.TEST_ID);
		props.setProperty(Constants.NODE_NODE_KEY, SftpTestConstants.TEST_NODE);
		props.setProperty(Constants.NODE_HOST_KEY,
				SftpTestConstants.TEST_HOST);
		props.setProperty(Constants.NODE_USER_KEY,
				SftpTestConstants.TEST_USER);
		props.setProperty(Constants.NODE_PASSWORD_KEY,
				SftpTestConstants.TEST_PASSWORD);
		props.setProperty(Constants.NODE_ENCRYPT_KEY,
				Boolean.toString(SftpTestConstants.TEST_ENCRYPTED));
		props.setProperty(Constants.NODE_PATH_KEY, SftpTestConstants.TEST_PATH);
		//props.setProperty(Constants.NODE_STAGING_DIR_KEY,
		//		TestConstants.TEST_STAGING_DIR + SftpTestConstants.TEST_HOST);
		props.setProperty(Constants.NODE_FILENAME_PATTERN_KEY,
				"log-|yyyy-mm-dd|.txt");
		System.setProperty("gov.nasa.pds.report.staging.home",
				TestConstants.TEST_STAGING_DIR);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.forceDelete(Utility.getStagingDir(
				props.getProperty(Constants.NODE_NODE_KEY),
				props.getProperty(Constants.NODE_ID_KEY),
				LogsManager.OUTPUT_DIR_NAME));
	}
	
	@Test
	public void testPullLogFiles() throws Exception{
		
		// Grab some frequently used variables from the node properties
		// to help keep code clean
		File stagingFile = new File(TestConstants.TEST_STAGING_DIR + File.separator + 
				props.getProperty(Constants.NODE_NODE_KEY) + File.separator +
				props.getProperty(Constants.NODE_ID_KEY) + File.separator +
				LogsManager.OUTPUT_DIR_NAME);
		String hostname = props.getProperty(Constants.NODE_HOST_KEY);
		
		// Remove any Date Logs Filters from previous tests
		DateLogFilter.unsetAll();
		
		// Pull the logs files
		manager.pullLogFiles(props);
		
		// Check that staging directory was created
		assertTrue("Failed to create staging directory for " + 
				hostname + " at " + stagingFile.getAbsolutePath(),
				stagingFile.exists());
		
		// Check that logs files were pulled and placed in staging directory
		File file = new File(stagingFile,
				new File(props.getProperty(Constants.NODE_PATH_KEY)).getName());
		assertTrue("Pulled log file not found at " + file.getAbsolutePath(), 
				file.exists());
		
	}
	
}