package gov.nasa.pds.report.processing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.rules.ReportManagerTest;
import gov.nasa.pds.report.util.Utility;

public class RingsDecryptionProcessorTest extends ReportManagerTest{
	
	private File testDir = null;
	private File outputDir = null;
	private RingsDecryptionProcessor processor = null;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@Before
	public void setUp() throws Exception {
		this.processor = new RingsDecryptionProcessor();
		this.testDir = new File(TestConstants.TEST_STAGING_DIR,
				"rings-decrypt-test");
		this.outputDir = new File(TestConstants.TEST_STAGING_DIR,
				"rings-decrypt-test-output");
		FileUtils.forceMkdir(this.testDir);
		FileUtils.copyFileToDirectory(
				new File(TestConstants.TEST_DIR_RELATIVE, 
				"rings-pds-rings-apache2.2014-10-01.tar.gz"),
				this.testDir);
		System.setProperty(Constants.COMMANDLINE_TIMEOUT_PROP, "0");
		this.outputDir.mkdirs();
	}
	
	@After
	public void tearDown() throws Exception {
		try {
			FileUtils.forceDelete(this.testDir);
			FileUtils.forceDelete(this.outputDir);
		} catch (IOException ex) {}
	}
	
	@Test
	public void testNominal(){
		
		// Copy the nominal input into the input directory
		try {
			FileUtils.copyFileToDirectory(
					new File(TestConstants.TEST_DIR_RELATIVE, 
					"rings-pds-rings-apache2.2014-10-01.tar.gz"),
					this.testDir);
		}catch(IOException e){
			fail("An error occurred while staging the log to process: " +
					e.getMessage());
		}
		
		// Run the processor
		try{
			this.processor.process(this.testDir, this.outputDir);
		}catch(ProcessingException e){
			fail("An error occurred during the nominal rings log decryption " +
					"test: " + e.getMessage());
		}
		
		// Verify that the output directory was created and the test file was
		// properly processed
		assertTrue("The output directory was not created by the rings " +
				"decryption processor", this.outputDir.exists());
		File outputFile = new File(this.outputDir,
				"rings-pds-rings-apache2.2014-10-01.txt");
		assertTrue("The test rings tarball was not properly decrypted",
				outputFile.exists());
		assertTrue("The unzipped rings tarball is an empty file",
				outputFile.length() > 0);
			
	}
	
	@Test
	public void testDirTree(){
		
		// Copy the input into the input directory that will create a directory
		// tree when decompressed
		try {
			FileUtils.copyFileToDirectory(
					new File(TestConstants.TEST_DIR_RELATIVE, 
					"server2-pds-rings-apache2.2017-10-01.tar.gz"),
					this.testDir);
		}catch(IOException e){
			fail("An error occurred while staging the log to process: " +
					e.getMessage());
		}
		
		// Run the processor
		try{
			this.processor.process(this.testDir, this.outputDir);
		}catch(ProcessingException e){
			fail("An error occurred during the nominal rings log decryption " +
					"test: " + e.getMessage());
		}
		
		// Verify that the output directory was created, that the test file was
		// properly processed, and that the left-over directory tree was deleted
		assertTrue("The output directory was not created by the rings " +
				"decryption processor", this.outputDir.exists());
		File outputFile = new File(this.outputDir,
				"server2-pds-rings-apache2.2017-10-01.log");
		assertTrue("The test rings tarball was not properly decrypted",
				outputFile.exists());
		assertTrue("The unzipped rings tarball is an empty file",
				outputFile.length() > 0);
		assertFalse("The left-over directory tree was not deleted",
				new File(outputFile, "usr").exists());
		
	}
	
}
