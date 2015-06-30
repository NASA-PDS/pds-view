package gov.nasa.pds.report.processing;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.rules.ReportManagerTest;

public class DetailByDetailProcessorTest extends ReportManagerTest{
	
	DetailByDetailProcessor processor = null;
	Properties props = null;
	File testDir = null;
	File outputDir = null;
	
	@Before
	public void setUp() throws Exception{
		
		this.processor = new DetailByDetailProcessor();
		this.props = new Properties();
		
		// Set-up test dir and file
		this.testDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test");
		this.outputDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test-output");
		FileUtils.forceMkdir(testDir);
		FileUtils.copyFileToDirectory(
				new File(TestConstants.TEST_DIR_RELATIVE, 
				"u_ex150131.log"), testDir);
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
	public void testConfigure(){
		
		this.props = new Properties();
		this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
				"<test;\\w+>");
		
		// Verify failure when input pattern is not provided
		try{
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with a null input pattern");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT,
					"hey look no brackets");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<test;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with an input pattern lacking brackets");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"mismatched <brackets");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<test;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with only one opening bracket in the input " +
					"pattern");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"mismatched >brackets");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<test;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with only one closing bracket in the input " +
					"pattern");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<very;mismatched>> brackets");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<very;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with an extra internal bracket in the input " +
					"pattern");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<rather;mismatched> <brackets");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<rather;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with an extra external bracket in the input " +
					"pattern");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<uh oh <nested> brackets>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<test;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with nested brackets in the input pattern");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<too few sections>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<test;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with a substring with too few sections in the" +
					" input pattern");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<much;too;many;sections>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<much;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with a substring with too many sections in the" +
					" input pattern");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<empty;section;>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<empty;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with a substring with an empty section in the" +
					" input pattern");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<blank;section; >");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<blank;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with a substring with a blank section in the" +
					" input pattern");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<improperly;formatted;datetime>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<improperly;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with a date-time substring with no equals sign");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<missing;formatted;datetime=>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<missing;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with a date-time substring with no date-time " +
					"format");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<improperly;formatted;default>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<improperly>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor containing a log detail with a default with " +
					"no equals sign");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<improperly;formatted;emptyvalue>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<improperly>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor containing a log detail with an empty value " +
					"with no equals sign");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<missing;formatted;default=>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<missing>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor containing a log detail with a missing " +
					"default value");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<missing;formatted;emptyvalue=>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<missing>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor containing a log detail with a missing " +
					"empty value");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<test;\\w+>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<othertest;required>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with a required log detail in the output " +
					"pattern that is not defined in the input pattern");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<test;\\w+>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<test;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
			fail("No failure occurred while configuring the log reformat " +
					"processor with a log detail specified as different " +
					"types in the input and output patterns");
		}catch(ProcessingException e){
			// Desired outcome
		}
		
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<date_time;\\d{4}-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d;required,datetime=yyyy-MM-dd HH:mm:ss> <server_ip;[0-9.]+> <http_method;GET|PUT|POST|DELETE> <requested_resource;\\S+> <uri_query;\\S+> <server_port;\\d+> <username;\\S+> <client_ip;[0-9.]+;required> <client_browser;\\S+> <referrer;\\S+> <status_code;\\d{3}> <substatus;\\d+> <win32_status;\\d+> <bytes_transfered;\\d+> <bytes_received;\\d+> <time_taken;\\d+>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<test;datetime=yyyy-MM-dd>");
			this.processor.configure(this.props);
		}catch(ProcessingException e){
			fail("A failure occurred while configuring the log reformat " +
					"processor with a valid input pattern: " + e.getMessage());
		}
		
	}
	
	@Test
	public void testProcessNominal() throws IOException{
		
		// Configure the processor
		try{
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT, 
					"<date_time;\\d{4}-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d;required,datetime=yyyy-MM-dd HH:mm:ss> <server_ip;[0-9.]+> <http_method;GET|PUT|POST|DELETE> <requested_resource;\\S+> <uri_query;\\S+> <server_port;\\d+> <username;\\S+> <client_ip;[0-9.]+;required> <client_browser;\\S+> <referrer;\\S+> <status_code;\\d{3}> <substatus;\\d+> <win32_status;\\d+> <bytes_transfered;\\d+> <bytes_received;\\d+> <time_taken;\\d+>");
			this.props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
					"<client_ip;required> <user_id> <username> [<date_time;required,datetime=dd/MMM/yyyy:HH:mm:ss Z>] \"<http_method> <requested_resource> <http_version;default=HTTP/1.1>\" <status_code> <bytes_transfered> \"<referrer>\" \"<client_browser>\"");
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
		
		// Verify that the output directory and log were created
		assertTrue("The output directory was not created by the rings " +
				"decryption processor", outputDir.exists());
		File outputFile = new File(outputDir, "u_ex150131.log");
		assertTrue("The reformatted log does not exist", outputFile.exists());
		
		// Verify that the test file was properly processed
		List<String> lines = new Vector<String>();
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(outputFile));
			lines.add(reader.readLine());
			lines.add(reader.readLine());
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
		assertEquals("Not all lines were read from the output log",
				lines.size(), 2);
		String desiredOutput = "10.10.1.37 - - [31/Jan/2015:06:03:21 -0800] \"GET /apollo/inst.aspx HTTP/1.1\" 200 4145 \"-\" \"Mozilla/4.0+(compatible;+MSIE+7.0;+PA+Server+Monitor+Service;+)\"";
		assertEquals("The test log line was not reformatted correctly",
				desiredOutput, lines.get(0));	
		assertEquals("The test log line was not reformatted correctly",
				desiredOutput, lines.get(1));
		
	}
	
}