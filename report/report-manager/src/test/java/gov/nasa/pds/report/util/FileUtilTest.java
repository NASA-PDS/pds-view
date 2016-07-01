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
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.SftpTestConstants;
import gov.nasa.pds.report.constants.TestConstants;
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
		try {
			FileUtils.forceDelete(new File(Utility.getAbsolutePath(
				TestConstants.TEST_DUMP_RELATIVE)));
		} catch (IOException ex) {}
	}
	
	@Test
	public void testGetDir(){
		
		File testFile = null;
		try{
			testFile = FileUtil.getDir(Constants.STAGING_DIR,
					SftpTestConstants.TEST_NODE, SftpTestConstants.TEST_ID);
		}catch(ReportManagerException e){
			fail("An error occurred while creating the test staging " +
					"directory: " + e.getMessage());
		}
		
		File file = new File(
				Utility.getAbsolutePath(TestConstants.TEST_STAGING_DIR) + 
				File.separator + Constants.STAGING_DIR +
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
				File.separator + Constants.PROCESSING_DIR +
				File.separator + SftpTestConstants.TEST_NODE + 
				File.separator + SftpTestConstants.TEST_ID +
				File.separator + TEST_PROCESS_NAME);
		
		assertTrue("The test directory was not created", file.exists());
		assertEquals("The test directory was not created at the correct location",
				file.getAbsolutePath(), testFile.getAbsolutePath());
		
	}
	
	@Test
	public void testBackupDir(){
		
		String testLogName = "rings-pds-rings-apache2.2014-10-01.tar.gz";
		
		// Create a test profile to specify the directory that will be
		// backed up
		Properties props = new Properties();
		props.setProperty(Constants.NODE_ID_KEY,
				SftpTestConstants.TEST_ID);
		props.setProperty(Constants.NODE_NODE_KEY, SftpTestConstants.TEST_NODE);
		
		try{
		
			// Create test staging directory using the test properties
			File srcFile = FileUtil.getDir(Constants.STAGING_DIR,
					SftpTestConstants.TEST_NODE, SftpTestConstants.TEST_ID);
			
			// Copy a test log into the test staging dir
			FileUtils.copyFileToDirectory(
					new File(TestConstants.TEST_DIR_RELATIVE, testLogName),
					srcFile);
		
		}catch(Exception e){
			fail("An error occurred while setting up to test staging file " +
					"backup: " + e.getMessage());
		}
		
		// Backup the test dir
		try{
			FileUtil.backupDir(props, Constants.STAGING_DIR, 
					Constants.BACKUP_DIR);
		}catch(ReportManagerException e){
			fail("An error occurred while testing backup functionality: " +
					e.getMessage());
		}
		
		// Check that the backup dir strcture is correct and that the log was
		// copied and original still exists
		File backupDir = new File(TestConstants.TEST_STAGING_DIR +
				File.separator + Constants.STAGING_DIR +
				File.separator + SftpTestConstants.TEST_NODE +
				File.separator + SftpTestConstants.TEST_ID);
		if(!backupDir.exists()){
			fail("The backup directory was not created");
		}
		File testLog = new File(backupDir, testLogName);
		if(!testLog.exists()){
			fail("The test log was not copied into the backup directory");
		}
		File srcLog = null;
		try{
			srcLog = new File(FileUtil.getDir(Constants.STAGING_DIR,
					SftpTestConstants.TEST_NODE, SftpTestConstants.TEST_ID),
					testLogName);
		}catch(ReportManagerException e){
			fail("An error occurred while fetching the source directory: " +
					e.getMessage());
		}
		if(!srcLog.exists()){
			fail("The test log was removed from the source directory");
		}
		
	}
	
}
