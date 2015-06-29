package gov.nasa.pds.report.processing;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.rules.ReportManagerTest;

public class WholeLineProcessorTest extends ReportManagerTest{
	
	WholeLineProcessor processor = null;
	Properties props = null;
	File testDir = null;
	File outputDir = null;
	
	@Before
	public void setUp() throws Exception{
		
		this.processor = new WholeLineProcessor();
		this.props = new Properties();
		
		// Set-up test dir and file
		this.testDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test");
		this.outputDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test-output");
		FileUtils.forceMkdir(testDir);
		FileUtils.copyFileToDirectory(
				new File(TestConstants.TEST_DIR_RELATIVE, 
				"u_ex150331.log"), testDir);
		outputDir.mkdirs();
		
	}
	
	@After
	public void tearDown() throws Exception {
		
		FileUtils.forceDelete(this.testDir);
		FileUtils.forceDelete(this.outputDir);
		
	}
	
	@Test
	public void testProcessNominal() throws IOException{
		
		// Configure the processor
		try{
			this.props.setProperty(Constants.NODE_LINE_REFORMAT_INPUT, 
					"<pre;\\d{4}-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d \\S+ [0-9.-]+ [A-Z-]+ \\S+ \\S+ [0-9-]+ \\S+ [0-9.-]+> <client_browser;.+> <referrer;\\S+> <post;[0-9-]+ [0-9-]+ [0-9-]+ [0-9-]+ [0-9-]+>");
			this.props.setProperty(Constants.NODE_LINE_REFORMAT_OUTPUT,
					"<pre> \"<client_browser>\" \"<referrer>\" <post>");
			this.processor.configure(this.props);
		}catch(ProcessingException e){
			fail("A failure occurred while configuring the log reformat " +
					"processor with a valid input pattern: " + e.getMessage());
		}
		
		// Run the processor and get the output directory
		try{
			this.processor.process(testDir, outputDir);
		}catch(ProcessingException e){
			fail("An error occurred during the nominal log reformatting " +
					"test: " + e.getMessage());
		}
		
		// Verify that the output directory was created and that the test file
		// was properly processed
		assertTrue("The output directory was not created by the rings " +
				"decryption processor", outputDir.exists());
		File outputFile = new File(outputDir, "u_ex150331.log");
		assertTrue("The reformatted log does not exist", outputFile.exists());
		String line = null;
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(outputFile));
			line = reader.readLine();
			reader.close();
		}catch(Exception e){
			try{
				reader.close();
			}catch(IOException ignore){
				// Ignore this
			}
			fail("An error occurred while checking the content of the " +
					"reformatted test log: " + e.getMessage());
		}
		String desiredOutput = "2015-03-31 11:39:03 W3SVC1234849874 10.10.1.73 GET /data/LRO-L-LROC-5-RDR-V1.0/LROLRC_2001/CATALOG/DSMAP.CAT - 80 - 59.94.42.179 \"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36\" \"-\" 200 0 0 8192 -";
		assertEquals("The test log line was not reformatted correctly",
				desiredOutput, line);	
		
	}
	
}