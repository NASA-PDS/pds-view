package gov.nasa.pds.report.profile;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.constants.SftpTestConstants;
import gov.nasa.pds.report.rules.PDSTest;

public class SimpleProfileManagerTest extends PDSTest{
	
	private static final String TEST_DIR_NAME = "test-profiles"; 
	
	private SimpleProfileManager manager;
	private Set<Properties> correctSet;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@Before
	public void setUp(){
		
		manager = new SimpleProfileManager();
		this.correctSet = new HashSet<Properties>();
		
		Properties p = new Properties();
		p.setProperty(Constants.NODE_NAME_KEY, SftpTestConstants.TEST_NODE);
		p.setProperty(Constants.NODE_HOST_KEY, SftpTestConstants.TEST_HOST);
		p.setProperty(Constants.NODE_USER_KEY, SftpTestConstants.TEST_USER);
		p.setProperty(Constants.NODE_PASSWORD_KEY,
				SftpTestConstants.TEST_PASSWORD);
		p.setProperty(Constants.NODE_ENCRYPT_KEY,
				Boolean.toString(SftpTestConstants.TEST_ENCRYPTED));
		p.setProperty(Constants.NODE_XFER_TYPE_KEY, "sftp");
		this.correctSet.add(p);
		
	}
	
	@Test
	public void testReadProfiles() throws Exception{
		
		String testPath = new File(TestConstants.TEST_DIR_RELATIVE,
				TEST_DIR_NAME).getAbsolutePath();
		Set<Properties> propsSet = new HashSet<Properties>(
				manager.readProfiles(testPath));
		
		assertFalse("No properties were read", propsSet.isEmpty());
		assertTrue("The read properties do not contain the correct values", 
				propsSet.equals(this.correctSet));
		
	}
	
}