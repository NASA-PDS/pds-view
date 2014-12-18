// Copyright 2014, by the California Institute of Technology.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.SftpTestConstants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.logs.LogsManager;
import gov.nasa.pds.report.processing.Processor;
import gov.nasa.pds.report.rules.ReportManagerTest;

public class FileUtilTest extends ReportManagerTest{
	
	private static final String TEST_PROCESS_NAME = "test_process_name";
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@BeforeClass
	public static void setUpBeforeClass(){
		System.setProperty(Constants.DIR_ROOT_PROP,
				TestConstants.TEST_STAGING_DIR);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.forceDelete(new File(Utility.getAbsolutePath(
				TestConstants.TEST_DUMP_RELATIVE)));
	}
	
	@Test
	public void testGetDir(){
		
		File testFile = null;
		try{
			testFile = FileUtil.getDir(LogsManager.DIR_NAME,
					SftpTestConstants.TEST_NODE, SftpTestConstants.TEST_ID);
		}catch(ReportManagerException e){
			fail("An error occurred while creating the test staging " +
					"directory: " + e.getMessage());
		}
		
		File file = new File(
				Utility.getAbsolutePath(TestConstants.TEST_STAGING_DIR) + 
				File.separator + LogsManager.DIR_NAME +
				File.separator + SftpTestConstants.TEST_NODE + 
				File.separator + SftpTestConstants.TEST_ID);
		
		assertTrue("The test directory was not created", file.exists());
		assertEquals("The test directory was not created at the correct location",
				file.getAbsolutePath(), testFile.getAbsolutePath());
		
	}
	
	@Test
	public void testGetProcessingDir(){
		
		File testFile = null;
		try{
			testFile = FileUtil.getProcessingDir(SftpTestConstants.TEST_NODE,
					SftpTestConstants.TEST_ID, TEST_PROCESS_NAME);
		}catch(ReportManagerException e){
			fail("An error occurred while creating the test staging " +
					"directory: " + e.getMessage());
		}
		
		File file = new File(
				Utility.getAbsolutePath(TestConstants.TEST_STAGING_DIR) + 
				File.separator + Processor.DIR_NAME +
				File.separator + SftpTestConstants.TEST_NODE + 
				File.separator + SftpTestConstants.TEST_ID +
				File.separator + TEST_PROCESS_NAME);
		
		assertTrue("The test directory was not created", file.exists());
		assertEquals("The test directory was not created at the correct location",
				file.getAbsolutePath(), testFile.getAbsolutePath());
		
	}
	
}
