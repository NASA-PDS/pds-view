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
package gov.nasa.pds.report.util;

import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.util.Utility;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;

/**
 * @author jpadams
 * @version $Revision$
 *
 */
@Ignore
public class TestUtility {

	/**
	 * This is a utility method for all the test classses used to create
	 * some temporary files as well as copy over some of the 
	 * @throws IOException
	 */
	public static void reportMgrSetUp() throws IOException {
		FileUtils.forceMkdir(new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE + "etc/conf/")));
		
		File sitesTest = new File(Utility.getAbsolutePath(TestConstants.SITES_TEST_FILE_PATH));
		File propertiesTest = new File(Utility.getAbsolutePath(TestConstants.PROPERTIES_TEST_FILE_PATH));
		File tempFile1 = new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE + "/temp1.txt"));
		File tempFile2 = new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE + "/temp2.txt"));
		
		File sitesOps = new File(Utility.getAbsolutePath(TestConstants.REPORT_MGR_HOME + "etc/conf/RemoteSpecs.xml"));
	
		Map<String,String> map = new HashMap<String,String>();
		map.put("\\[REPORT_MGR_HOME\\]", Utility.getAbsolutePath(TestConstants.TEST_DIR_RELATIVE));
		map.put("\\[LOGS_HOME\\]", Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE + "logs"));
		
		FileUtils.copyFile(sitesOps, tempFile1);
		for (String key : map.keySet()) {
			Utility.replaceStringInFile(key, map.get(key), tempFile1, tempFile2);
			FileUtils.copyFile(tempFile2, tempFile1);
		}
		FileUtils.copyFile(tempFile1, sitesTest);
		
		//Utility.replaceStringInFile("\\[STAGING_AREA\\]", Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE) + "logs", sitesOps, temp);
		//Utility.replaceStringInFile("\\[CAS_PP_RESOURCES\\]", Utility.getAbsolutePath(TestConstants.TEST_DIR_RELATIVE +"etc"), sitesOps, sitesTest);
		
		File propertiesOps = new File(Utility.getAbsolutePath(TestConstants.REPORT_MGR_HOME + "etc/push_pull_framework.properties"));
		
		map.clear();
		map.put("\\[REPORT_MGR_HOME\\]", Utility.getAbsolutePath(TestConstants.REPORT_MGR_HOME));
		map.put("\\[LOGS_HOME\\]", Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE + "logs"));
		
		FileUtils.copyFile(propertiesOps, tempFile1);
		for (String key : map.keySet()) {
			Utility.replaceStringInFile(key, map.get(key), tempFile1, tempFile2);
			FileUtils.copyFile(tempFile2, tempFile1);
		}
		FileUtils.copyFile(tempFile1, propertiesTest);
	}
	
	public static void reportMgrTearDown() throws IOException {
		FileUtils.forceDelete(new File(Utility.getAbsolutePath(TestConstants.TEST_DUMP_RELATIVE)));
	}
}
