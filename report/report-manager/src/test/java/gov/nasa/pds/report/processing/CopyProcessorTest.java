package gov.nasa.pds.report.processing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.rules.ReportManagerTest;
import gov.nasa.pds.report.rules.PDSTest.SingleTestRule;

public class CopyProcessorTest extends ReportManagerTest{
	
	private File testDir = null;
	private File outputDir = null;
	private Properties props = null;
	private CopyProcessor processor = null;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@Before
	public void setUp() throws Exception {
		
		this.props = new Properties();
		this.processor = new CopyProcessor();
		this.testDir = new File(TestConstants.TEST_STAGING_DIR,
				"copy-test");
		this.outputDir = new File(TestConstants.TEST_STAGING_DIR,
				"copy-test-output");
		FileUtils.forceMkdir(this.testDir);
		FileUtils.copyFileToDirectory(
				new File(TestConstants.TEST_DIR_RELATIVE, 
				"u_ex150331.log"), this.testDir);
		this.outputDir.mkdirs();
		
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.forceDelete(this.testDir);
		FileUtils.forceDelete(this.outputDir);
	}
	
	@Test
	public void testNominal(){
		
		// Configure the processor
		this.props.setProperty(Constants.NODE_COPY_INPUT,
				this.testDir.getAbsolutePath());
		try {
			this.processor.configure(this.props);
		} catch (ProcessingException e) {
			fail("An error occurred while configuring the copy " +
					"processor for the nominal test: " + e.getMessage());
		}
		
		// Run the processor
		try{
			this.processor.process(File.createTempFile("copy", "test"), this.outputDir);
		}catch(ProcessingException e){
			fail("An error occurred during the nominal copy test: " +
					e.getMessage());
		}catch(IOException e){
			fail("An error occurred while creating the temporary input " +
					"directory");
		}
		
		// Verify that the output directory was created and the test file was
		// properly processed
		assertTrue("The output directory was not created by the copy processor",
				this.outputDir.exists());
		File outputFile = new File(this.outputDir,
				"u_ex150331.log");
		assertTrue("The test file was not properly copied",
				outputFile.exists());
		
	}
	
}