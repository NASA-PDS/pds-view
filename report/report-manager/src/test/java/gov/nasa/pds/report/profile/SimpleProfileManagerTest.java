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
import gov.nasa.pds.report.rules.PDSTest;
import gov.nasa.pds.report.util.Debugger;

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
		p.setProperty(Constants.NODE_NAME_KEY, TestConstants.TEST_NODE);
		p.setProperty(Constants.NODE_HOST_KEY, TestConstants.TEST_HOST);
		p.setProperty(Constants.NODE_USER_KEY, TestConstants.TEST_USER);
		p.setProperty(Constants.NODE_PASSWORD_KEY, TestConstants.TEST_PASSWORD);
		p.setProperty(Constants.NODE_ENCRYPT_KEY,
				Boolean.toString(TestConstants.TEST_ENCRYPTED));
		p.setProperty(Constants.NODE_XFER_TYPE_KEY, "sftp");
		this.correctSet.add(p);
		
	}
	
	@Test
	public void testReadProfiles() throws Exception{
		
		String testPath = new File(TestConstants.TEST_DIR_RELATIVE,
				TEST_DIR_NAME).getAbsolutePath();
		Debugger.debug("Test Path: " + testPath);
		Set<Properties> propsSet = new HashSet<Properties>(
				manager.readProfiles(testPath));
		
		Debugger.debug("Correct props set: " + this.correctSet.toString());
		Debugger.debug("Read props set: " + propsSet.toString());
		
		assertFalse("No properties were read", propsSet.isEmpty());
		assertTrue("The read properties do not contain the correct values", 
				propsSet.equals(this.correctSet));
		
	}
	
}