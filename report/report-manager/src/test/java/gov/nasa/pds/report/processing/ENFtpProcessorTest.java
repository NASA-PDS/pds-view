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
import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.rules.ReportManagerTest;

public class ENFtpProcessorTest extends ReportManagerTest{
	
	private ENFtpProcessor processor;
	private Properties props;
	private File testDir = null;
	private File outputDir = null;
	
	private static final String[] expectedPds3Output =
			{"pds2jpeg", "pds2jpeg", "nasaview", "nasaview", "pds2jpeg",
			"nasaview", "nasaview", "datadictionary", "pds2jpeg",
			"datadictionary", "vtool", "vtool"};
	private static final String[] expectedPds4Output =
			{"search-service", "search-core", "search-ui", "registry-service",
			"validate"};
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@Before
	public void setUp() throws Exception{
		
		this.processor = new ENFtpProcessor();
		
		// Set-up test dir and file
		this.testDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test");
		this.outputDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test-output");
		FileUtils.forceMkdir(testDir);
		FileUtils.copyFileToDirectory(
				new File(TestConstants.TEST_DIR_RELATIVE, 
				"xferlog-20170216"), testDir);
		outputDir.mkdirs();
		
		// Allow no errors during processing
		System.setProperty(Constants.REFORMAT_ERRORS_PROP, "0");
		
		// Specify log input and output formats
		this.props = new Properties();
		props.setProperty(Constants.NODE_DETAIL_REFORMAT_INPUT,
				"<date_time;\\w{3} \\w{3}\\s+\\d{1,2} \\d\\d:\\d\\d:\\d\\d \\d{4};required,datetime=EEE MMM d HH:mm:ss yyyy> <time_taken;\\d+> <client_ip;\\S+;required> <bytes_transfered;\\d+> <requested_resource;\\S+> <transfer_type;[ab]> <action_flag;[CUT_]> <direction_flag;[dio]> <access_mode;[ar]> <user_id;\\S+> <service_name;\\S+> <auth_method;[01]> <username;\\S+;emptyvalue=*> <completion;[ci]>");
		props.setProperty(Constants.NODE_DETAIL_REFORMAT_OUTPUT,
				"<client_ip;required> <user_id> <username> [<date_time;required,datetime=dd/MMM/yyyy:HH:mm:ss Z>] \"<http_method> <requested_resource> <http_version;default=HTTP/1.1>\" <status_code> <bytes_transfered> \"<referrer>\" \"<client_browser>\"");
		
	}
	
	@After
	public void tearDown() throws Exception {
		try {	
			FileUtils.forceDelete(this.testDir);
			FileUtils.forceDelete(this.outputDir);
		} catch (IOException ex) {}
	}
	
	@Test
	public void testPds3Nominal(){
		
		this.props.setProperty(Constants.NODE_ID_KEY, "pds3");
		
		this.runTest("pds3", expectedPds3Output);
		
	}
	
	@Test
	public void testPds4Nominal(){
		
		this.props.setProperty(Constants.NODE_ID_KEY, "pds4");
		
		this.runTest("pds4", expectedPds4Output);
		
	}
	
	private void runTest(String pdsVersion, String[] expectedOutput){
		
		// Configure the processor
		try{
			this.processor.configure(this.props);
		}catch(ProcessingException e){
			fail("A failure occurred while configuring the EN FTP " +
					pdsVersion + " processor with a valid output pattern: " +
					e.getMessage());
		}
		
		// Run the processor and get the output directory
		try{
			this.processor.process(testDir, outputDir);
		}catch(ProcessingException e){
			fail("An error occurred during the nominal log reformatting " +
					"test for " + pdsVersion + ": " + e.getMessage());
		}
		
		// Verify that the output directory and log were created
		assertTrue("The output directory was not created by the " +
				"EN FTP processor", outputDir.exists());
		File outputFile = new File(outputDir, "xferlog-20170216");
		assertTrue("The reformatted log does not exist", outputFile.exists());
		
		// Verify that the test file was properly processed
		List<String> lines = new Vector<String>();
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(outputFile));
			for(String line = reader.readLine(); line != null;
					line = reader.readLine()){
				if(line != null){
					lines.add(line);
				}
			}
			reader.close();
		}catch(Exception e){
			try{
				reader.close();
			}catch(IOException ignore){
				// Ignore this
			}
			fail("An error occurred while checking the content of the " +
					pdsVersion + " reformatted test log: " + e.getMessage());
		}
		assertEquals("Not all lines were read from the " + pdsVersion +
				" output log", lines.size(), expectedOutput.length);
		for(int i = 0; i < expectedOutput.length; i++){
			assertTrue("The test log line was not reformatted correctly",
					lines.get(i).contains(expectedOutput[i]));	
		}
		
	}
	
}