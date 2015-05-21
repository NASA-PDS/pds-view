package gov.nasa.pds.report.processing;

import static org.junit.Assert.*;

import java.io.File;
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

public class SimpleCommandProcessorTest extends ReportManagerTest{
	
	private File testDir = null;
	private File outputDir = null;
	private Properties props = null;
	private SimpleCommandProcessor processor = null;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@Before
	public void setUp() throws Exception {
		
		this.props = new Properties();
		this.processor = new SimpleCommandProcessor();
		this.testDir = new File(TestConstants.TEST_STAGING_DIR,
				"simple-command-test");
		this.outputDir = new File(TestConstants.TEST_STAGING_DIR,
				"simple-command-test-output");
		FileUtils.forceMkdir(this.testDir);
		FileUtils.copyFileToDirectory(
				new File(TestConstants.TEST_DIR_RELATIVE, 
				"access.log-20150430.gz"), this.testDir);
		this.outputDir.mkdirs();
		
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.forceDelete(this.testDir);
		FileUtils.forceDelete(this.outputDir);
	}
	
	@Test
	public void testNoConfigure(){
		
		try{
			this.processor.process(this.testDir, this.outputDir);	// this should fail
			fail("No execpetion was thrown when the simple command " +
					"processor was invoked without being configured");
		}catch(ProcessingException e){
			// We expect this
		}
		
	}
	
	@Test
	public void testNominal(){
		
		// Configure the processor
		this.props.setProperty(Constants.NODE_SIMPLE_COMMAND_KEY,
				"zcat <input> > <output>");
		try {
			this.processor.configure(this.props);
		} catch (ProcessingException e) {
			fail("An error occurred while configuring the simple command " +
					"processor for the nominal test: " + e.getMessage());
		}
		
		// Run the processor and get the output directory
		try{
			this.processor.process(this.testDir, this.outputDir);
		}catch(ProcessingException e){
			fail("An error occurred during the nominal simple command test: " +
					e.getMessage());
		}
		
		// Verify that the output directory was created and the test file was
		// properly processed
		assertTrue("The output directory was not created by the simple " +
				"command processor", this.outputDir.exists());
		File outputFile = new File(this.outputDir,
				"access.log-20150430");
		assertTrue("The test file was not properly unzipped",
				outputFile.exists());
		
	}
	
}