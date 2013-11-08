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
package gov.nasa.pds.report.manager;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import gov.nasa.pds.report.manager.cli.options.Flag;
import gov.nasa.pds.report.manager.constants.TestConstants;
import gov.nasa.pds.report.manager.rules.ReportManagerTest;
import gov.nasa.pds.report.manager.rules.PDSTest.SingleTestRule;
import gov.nasa.pds.report.manager.util.Utility;
import gov.nasa.pds.report.update.util.TestUtility;

/**
 * @author jpadams
 * @version $Revision$
 *
 */
@RunWith(JUnit4.class)
public class ReportManagerLauncherTest extends ReportManagerTest {

	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@BeforeClass
	public static void oneTimeSetUp() throws IOException {
		TestUtility.reportMgrSetUp();
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws IOException {
		TestUtility.reportMgrTearDown();
	}
	
	@Test
	public void testLauncherPullWithParams() {
		String[] args = { "-" + Flag.PULL.getShortName(), 
				"-" + Flag.PP_PORT.getShortName(), "9999", 
				"-" + Flag.PP_PROPERTIES.getShortName(), Utility.getAbsolutePath(TestConstants.PROPERTIES_TEST_FILE_PATH),
				"-" + Flag.PP_SPECS.getShortName(), Utility.getAbsolutePath(TestConstants.SITES_TEST_FILE_PATH)
		};
		
		ReportManagerLauncher.main(args);
		
		assertEquals(FileUtils.listFiles(
				new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE) + "logs"), 
				new String[]{"txt"}, true).size(), 
				3);
	}
	
	@Test
	public void testLauncherPullWithDefaults() {
		String[] args = { "-" + Flag.PULL.getShortName() };
		
		ReportManagerLauncher.main(args);
		
		assertEquals(FileUtils.listFiles(
				new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE) + "logs"), 
				new String[]{"txt"}, true).size(), 
				3);
	}
}
