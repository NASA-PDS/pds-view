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

public class NcftpReformatProcessorTest extends ReportManagerTest{
	
	NcftpReformatProcessor processor;
	File testDir = null;
	File outputDir = null;
	
	@Before
	public void setUp() throws Exception{
		
		this.processor = new NcftpReformatProcessor();
		
		// Set-up test dir and file
		this.testDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test");
		this.outputDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test-output");
		FileUtils.forceMkdir(testDir);
		FileUtils.copyFileToDirectory(
				new File(TestConstants.TEST_DIR_RELATIVE, 
				"atm-atmos-ncftpd.2015-01-01"), testDir);
		outputDir.mkdirs();
		
		// Allow no errors during processing
		System.setProperty(Constants.REFORMAT_ERRORS_PROP, "0");
		
	}
	
	@After
	public void tearDown() throws Exception {
		
		FileUtils.forceDelete(this.testDir);
		FileUtils.forceDelete(this.outputDir);
		
	}
	
	@Test
	public void testProcessNominal() throws IOException{
		
		// Configure the processor
		Properties props = new Properties();
		try{
			props.setProperty(Constants.NODE_NCFTP_REFORMAT_OUTPUT,
					"<client_ip;required> <user_id> <username> [<date_time;required,datetime=dd/MMM/yyyy:HH:mm:ss Z>] \"<http_method> <requested_resource> <http_version;default=HTTP/1.1>\" <status_code> <bytes_transfered> \"<referrer>\" \"<client_browser>\"");
			this.processor.configure(props);
		}catch(ProcessingException e){
			fail("A failure occurred while configuring the log reformat " +
					"processor with a valid output pattern: " + e.getMessage());
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
		assertTrue("The output directory was not created by the NcFTP " +
				"reformat processor", outputDir.exists());
		File outputFile = new File(outputDir, "atm-atmos-ncftpd.2015-01-01");
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
		String desiredOutput = "crawl-66-249-67-120.googlebot.com - anonymous [01/Dec/2014:00:47:36 -0800] \"GET /export/atmos7/anonymous/robots.txt HTTP/1.1\" - 352 \"-\" \"-\"";
		assertEquals("The test log line was not reformatted correctly",
				desiredOutput, line);	
		
	}
	
}