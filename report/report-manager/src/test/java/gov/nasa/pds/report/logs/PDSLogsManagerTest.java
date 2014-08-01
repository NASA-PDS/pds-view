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
import gov.nasa.pds.report.rules.PDSTest;

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
		props.setProperty(Constants.NODE_NAME_KEY,
				TestConstants.TEST_NODE);
		props.setProperty(Constants.NODE_HOST_KEY,
				TestConstants.TEST_HOST);
		props.setProperty(Constants.NODE_USER_KEY,
				TestConstants.TEST_USER);
		props.setProperty(Constants.NODE_PASSWORD_KEY,
				TestConstants.TEST_PASSWORD);
		props.setProperty(Constants.NODE_ENCRYPT_KEY,
				Boolean.toString(TestConstants.TEST_ENCRYPTED));
		props.setProperty(Constants.NODE_PATH_KEY, TestConstants.TEST_PATH);
		props.setProperty(Constants.NODE_STAGING_DIR_KEY,
				TestConstants.TEST_STAGING_DIR + TestConstants.TEST_HOST);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.forceDelete(new File(props.getProperty(
				Constants.NODE_STAGING_DIR_KEY)));
	}
	
	@Test
	public void testPullLogFiles() throws Exception{
		
		// Grab some frequently used variables from the node properties
		// to help keep code clean
		String stagingDir = props.getProperty(Constants.NODE_STAGING_DIR_KEY);
		String hostname = props.getProperty(Constants.NODE_HOST_KEY);
		
		// Pull the logs files
		manager.pullLogFiles(props);
		
		// Check that staging directory was created
		assertTrue("Failed to create staging directory for " + 
				hostname + " at " + stagingDir, new File(stagingDir).exists());
		
		// Check that logs files were pulled and placed in staging directory
		File file = new File(stagingDir,
				new File(props.getProperty(Constants.NODE_PATH_KEY)).getName());
		assertTrue("Pulled log file not found at " + file.getAbsolutePath(), 
				file.exists());
		
	}
	
}