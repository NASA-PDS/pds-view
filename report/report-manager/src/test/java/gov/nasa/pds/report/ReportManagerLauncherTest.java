// Copyright 2013, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.report;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import gov.nasa.pds.report.ReportManagerLauncher;
import gov.nasa.pds.report.cli.options.Flag;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.rules.ReportManagerTest;
import gov.nasa.pds.report.util.TestUtility;
import gov.nasa.pds.report.util.Utility;

/**
 * @author jpadams
 * @version $Revision$
 *
 */
@RunWith(JUnit4.class)
public class ReportManagerLauncherTest extends ReportManagerTest {

	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	/*
	@BeforeClass
	public static void oneTimeSetUp() throws IOException {
		TestUtility.reportMgrSetUp();
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws IOException {
		TestUtility.reportMgrTearDown();
	}
	*/
	
	@Test
	public void testLoadConfiguration(){
		
		System.setProperty(Constants.DIR_ROOT_PROP,
				TestConstants.TEST_STAGING_DIR);
		
		Properties defaults = new Properties();
		String defaultFilePath = TestConstants.CONFIG_DIR_RELATIVE +
				File.separator + "default.properties";
		try{
			defaults.load(new FileInputStream(defaultFilePath));
		}catch(IOException e){
			fail("An error occurred while reading in the default " +
					"configuration from " + defaultFilePath + 
					": " + e.getMessage());
		}
		
		ReportManagerLauncher launcher = new ReportManagerLauncher();
		
		Method method = null;
		try{
			method = ReportManagerLauncher.class.getDeclaredMethod(
					"loadConfiguration", String.class);
			method.setAccessible(true);
			method.invoke(launcher, defaultFilePath);
		}catch(NoSuchMethodException e){
			fail("The loadConfiguration method could not be found: " + 
					e.getMessage());
		} catch (IllegalArgumentException e) {
			fail("An illegal argument was passed to the loadConfiguration " +
					"method: " + e.getMessage());
		} catch (IllegalAccessException e) {
			fail("The loadConfiguration method is not accessible for " +
					"testing: " + e.getMessage());
		} catch (InvocationTargetException e) {
			fail("An error occurred while executing the loadConfiguration " +
					"method: " + e.getCause().toString());
		}
		
		assertEquals("The log staging home was overwritten while loading " +
				"the Report Service configuration",
				System.getProperty(Constants.DIR_ROOT_PROP),
				TestConstants.TEST_STAGING_DIR);
		assertEquals("The profile home was changed during the Report " +
				"Service configuration",
				System.getProperty(Constants.PROFILE_HOME_PROP),
				defaults.getProperty(Constants.PROFILE_HOME_PROP));
		
	}
	
	/**
	 * Need to update test to set REPORT_MGR_HOME environment variable
	 */
	@Test
	@Ignore 
	public void testLauncherPullWithDefaults() {
		String[] args = { "-" + Flag.PULL.getShortName() };
		
		ReportManagerLauncher.main(args);
		
		assertEquals(FileUtils.listFiles(
				new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE) + "logs"), 
				new String[]{"txt"}, true).size(), 
				3);
	}
}
