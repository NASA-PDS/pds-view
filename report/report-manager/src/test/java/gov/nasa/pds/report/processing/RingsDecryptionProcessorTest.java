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
		
		// Run the processor and get the output directory
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
	
}
