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

package gov.nasa.pds.report.logs.pushpull;

import static org.junit.Assert.*;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.logs.LogsManager;
import gov.nasa.pds.report.logs.LogsManagerException;
import gov.nasa.pds.report.logs.pushpull.PushPull;
import gov.nasa.pds.report.logs.pushpull.PushPullImpl;
import gov.nasa.pds.report.rules.PDSTest;
import gov.nasa.pds.report.util.TestUtility;
import gov.nasa.pds.report.util.Utility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@RunWith(JUnit4.class)
@Ignore		// Ignoring Broken Tests
public class PushPullTest extends PDSTest {

	/**
	 * ${CAS_PP_RESOURCES}/push_pull_framework.properties \
     * --remoteSpecsFile ${CAS_PP_RESOURCES}/conf/RemoteSpecs.xml 
	 */
	
	private PushPull pp;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@BeforeClass
	public static void oneTimeSetUp() throws IOException, LogsManagerException {
		TestUtility.reportMgrSetUp();
		
		LogsManager mgr = new LogsManager();
		mgr.createStagingAreas(new File(Utility.getAbsolutePath(TestConstants.SITES_TEST_FILE_PATH)));
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws IOException {
		TestUtility.reportMgrTearDown();
	}
	
	@Before
	public void setUp() throws IOException {
		
	}
	
	/**
	 * Tests pull method(). Assert must be updated depending on the configuration if more logs are planned to be grabbed
	 */
	@Test
	public void testPull() {
		try {
			this.pp = new PushPullImpl(9999, 
					new File(Utility.getAbsolutePath(TestConstants.PROPERTIES_TEST_FILE_PATH)), 
					new File(Utility.getAbsolutePath(TestConstants.SITES_TEST_FILE_PATH)));
			this.pp.pull();
			
			assertEquals(FileUtils.listFiles(
					new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE) + "logs"), 
					new String[]{"txt"}, 
					true).size(), 
					3);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
