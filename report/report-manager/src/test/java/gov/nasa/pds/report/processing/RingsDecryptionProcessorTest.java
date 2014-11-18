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
	RingsDecryptionProcessor processor = null;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@Before
	public void setUp() throws Exception {
		this.processor = new RingsDecryptionProcessor();
		this.testDir = new File(TestConstants.TEST_STAGING_DIR,
				"rings-decrypt-test");
		FileUtils.forceMkdir(this.testDir);
		FileUtils.copyFileToDirectory(
				new File(TestConstants.TEST_DIR_RELATIVE, 
				"rings-pds-rings-apache2.2014-10-01.tar.gz"),
				this.testDir);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.forceDelete(this.testDir);
	}
	
	@Test
	public void testNominal(){
		
		File outputDir = null;
		
		// Run the processor and get the output directory
		try{
			this.processor.process(this.testDir);
			outputDir = Utility.getStagingDir(this.testDir,
					this.processor.getDirName());
		}catch(ProcessingException e){
			fail("An error occurred during the nominal rings log decryption " +
					"test: " + e.getMessage());
		}catch(ReportManagerException e){
			fail("An error occurred while fetching the output directory: " + 
					e.getMessage());
		}
		
		// Verify that the output directory was created and the test file was
		// properly processed
		assertTrue("The output directory was not created by the rings " +
				"decryption processor", outputDir.exists());
		File outputFile = new File(outputDir,
				"rings-pds-rings-apache2.2014-10-01.txt");
		assertTrue("The test rings tarball was not properly decrypted",
				outputFile.exists());
			
	}
	
}
