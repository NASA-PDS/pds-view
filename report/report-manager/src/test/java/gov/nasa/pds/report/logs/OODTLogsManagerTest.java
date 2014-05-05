//	Copyright 2013, by the California Institute of Technology.
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
//	$Id$
//
package gov.nasa.pds.report.logs;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.logs.OODTLogsManager;
import gov.nasa.pds.report.logs.LogsManagerException;
import gov.nasa.pds.report.logs.pushpull.OODTPushPull;
import gov.nasa.pds.report.logs.pushpull.OODTPushPullTest;
import gov.nasa.pds.report.rules.ReportManagerTest;
import gov.nasa.pds.report.rules.PDSTest.SingleTestRule;
import gov.nasa.pds.report.util.Debugger;
import gov.nasa.pds.report.util.TestUtility;
import gov.nasa.pds.report.util.Utility;

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
 * @author jpadams
 * @version $Revision$
 *
 */
@RunWith(JUnit4.class)
public class OODTLogsManagerTest extends ReportManagerTest {

	private static OODTLogsManager logsMgr;
	private File tempFile1;
	private File tempFile2;
	private static final String TEST_STAGING_DIR = TestConstants.TEST_DUMP_RELATIVE + "/test/create/staging/areas/";
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@BeforeClass
	public static void oneTimeSetUp() {
		Debugger.debugFlag = true;
		try {
			TestUtility.reportMgrSetUp();
			
			logsMgr = new OODTLogsManager(9999, 
					new File(Utility.getAbsolutePath(TestConstants.PROPERTIES_TEST_FILE_PATH)), 
					new File(Utility.getAbsolutePath(TestConstants.SITES_TEST_FILE_PATH)),
					Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE));
			
			FileUtils.forceMkdir(new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws IOException {
		//TestUtility.reportMgrTearDown();
	}
	
	@Before
	public void setUp() throws Exception {
		OODTPushPullTest.oneTimeSetUp();
		this.tempFile1 = new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE + "/temp1.txt"));
		this.tempFile2 = new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE + "/temp2.txt"));
	}
	
	@After
	public void tearDown() {
		try {
			FileUtils.forceDelete(this.tempFile1);
			FileUtils.forceDelete(this.tempFile2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore // Ignoring broken test
	public void testPullLogFiles() {
		try {
			logsMgr.pullLogFiles();
			
			assertEquals(FileUtils.listFiles(new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE) + "logs"), new String[]{"txt"}, true).size(), 3);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreateStagingAreas() {	
		try {
			File sitesTest = new File(Utility.getAbsolutePath(TestConstants.TEST_DIR_RELATIVE + TestConstants.STAGING_AREA_TESTS_FILE));

			// Create test dir paths list
			List<String> stagingDirPathsList = Arrays.asList(
					Utility.getAbsolutePath(TEST_STAGING_DIR) + "0",
					Utility.getAbsolutePath(TEST_STAGING_DIR) + "1",
					Utility.getAbsolutePath(TEST_STAGING_DIR) + "2");
	
			// Copy the test file to a temp
			FileUtils.copyFile(sitesTest, this.tempFile1);
			for (int i=0; i<stagingDirPathsList.size(); i++) {
				Utility.replaceStringInFile("STAGING_AREA_" + i, stagingDirPathsList.get(i), this.tempFile1, this.tempFile2);
				FileUtils.copyFile(this.tempFile2, this.tempFile1);
			}
			
			// FIXME Not sure what these paths should be
			List<String> pathList = Utility.getValuesFromXML(logsMgr.getStagingPaths(Utility.getAbsolutePath(TestConstants.SITES_TEST_FILE_PATH), this.tempFile2), 
					OODTPushPull.STAGING_TAG_NAME,
					OODTPushPull.STAGING_ATTRIBUTE_NAME);
			logsMgr.createStagingAreas(pathList);
			
			for (String path : stagingDirPathsList) {
				File stagingDir = new File(path);
				if (!stagingDir.isDirectory()) {
					fail(stagingDir.getAbsolutePath() + " failed to be created.");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
