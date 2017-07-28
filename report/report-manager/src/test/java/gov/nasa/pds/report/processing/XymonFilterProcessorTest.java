package gov.nasa.pds.report.processing;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.rules.ReportManagerTest;
import gov.nasa.pds.report.rules.PDSTest.SingleTestRule;

public class XymonFilterProcessorTest extends ReportManagerTest{
	
	private XymonFilterProcessor processor;
	File testDir = null;
	File outputDir = null;
	
	@Rule
	public SingleTestRule test = new SingleTestRule("");
	
	@Before
	public void setUp() throws Exception{
		
		this.processor = new XymonFilterProcessor();
		
		// Set-up test and output dirs
		this.testDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test");
		this.outputDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test-output");
		FileUtils.forceMkdir(testDir);
		outputDir.mkdirs();
		
	}
	
	@After
	public void tearDown() throws Exception {
		try {	
			FileUtils.forceDelete(this.testDir);
			FileUtils.forceDelete(this.outputDir);
		} catch (IOException ex) {}
	}
	
	@Test
	public void testEmptyOutput(){
		
		// Write test file
		try{
			PrintWriter writer = new PrintWriter(new File(this.testDir,
					"test_empty.log"));
			writer.println("- - - [16/Jul/2017:17:05:42 -0700] \"GET /\" 200 12472 \"-\" \"-\" 681 15674");
			writer.flush();
		}catch(FileNotFoundException e){
			fail("A failure occurred while opening the test file: " + e.getMessage());
		}
		
		// Process test file
		try{
			this.processor.process(testDir, outputDir);
		}catch(ProcessingException e){
			fail("An error occurred while filtering a file with empty output: " +
					e.getMessage());
		}
		
		// Verify that no output file was created
		assertFalse("The output file should not have been created",
				new File(this.outputDir, "test_empty.log").exists());
		
	}
	
	@Test
	public void testProcessNominal(){
		
		// Write test file
		final String dummyRequest = "163.172.71.23 - - [16/Jul/2017:17:00:01 -0700] \"GET /data/mro/mars_reconnaissance_orbiter/ctx/mrox_2756/voldesc.cat HTTP/1.1\" 200 1653 \"-\" \"Mozilla/5.0 ((Windows; U; Windows NT 6.1; fr; rv:1.9.2) Gecko/20100115 Firefox/3.6)\" 550 2159";
		try{
			PrintWriter writer = new PrintWriter(new File(this.testDir,
					"test_empty.log"));
			writer.println("- - - [16/Jul/2017:17:05:42 -0700] \"GET /\" 200 12472 \"-\" \"-\" 681 15674");
			writer.println(dummyRequest);
			writer.flush();
		}catch(FileNotFoundException e){
			fail("A failure occurred while opening the test file: " + e.getMessage());
		}
		
		// Process test file
		try{
			this.processor.process(testDir, outputDir);
		}catch(ProcessingException e){
			fail("An error occurred while filtering a file with empty output: " +
					e.getMessage());
		}
		
		// Verify that the output directory was created and that the test file
		// was properly processed
		assertTrue("The output directory was not created by the Xymon " +
				"filter processor", outputDir.exists());
		File outputFile = new File(outputDir, "test_empty.log");
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
		assertEquals("The test log line was not reformatted correctly",
				dummyRequest, line);
		
	}
	
}