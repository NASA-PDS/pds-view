package gov.nasa.pds.report.processing;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import gov.nasa.pds.report.constants.TestConstants;
import gov.nasa.pds.report.rules.ReportManagerTest;

public class ProxyFilterProcessorTest extends ReportManagerTest{
	
	private ProxyFilterProcessor processor;
	File testDir = null;
	File outputDir = null;
	
	@Test
	public void testProcessNominal() throws IOException{
		
		ProxyFilterProcessor processor = new ProxyFilterProcessor();
		
		// Set-up test and output dirs
		this.testDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test");
		this.outputDir = new File(TestConstants.TEST_STAGING_DIR,
				"log-reformat-test-output");
		FileUtils.forceMkdir(testDir);
		FileUtils.copyFileToDirectory(
				new File(TestConstants.TEST_DIR_RELATIVE, 
				"access_log.2017-07-29.txt"), testDir);
		outputDir.mkdirs();
		
		// Process test file
		try{
			processor.process(testDir, outputDir);
		}catch(ProcessingException e){
			fail("An error occurred while filtering a file with empty output: " +
					e.getMessage());
		}
		
		// Verify that the output directory was created and that the test file
		// was properly processed
		assertTrue("The output directory was not created by the proxy " +
				"filter processor", outputDir.exists());
		File outputFile = new File(outputDir, "access_log.2017-07-29.txt");
		assertTrue("The reformatted log does not exist", outputFile.exists());
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(outputFile));
			String line = reader.readLine();
			assertEquals("The test log line was not reformatted correctly",
					"141.8.144.21 - - [28/Jul/2017:17:17:37 -0700] \"GET /w10n/m3/thumbnail/CH1M3_0002/20090415_20090816/200907/L1B/M3G20090729T022657_V01_RDN.jpeg_small HTTP/1.1\" 200 4093 \"-\" \"Mozilla/5.0 (compatible; YandexBot/3.0; +http://yandex.com/bots)\" 608 4609",
					line);
			line = reader.readLine();
			assertEquals("The test log line was not reformatted correctly",
					"66.249.75.4 - - [28/Jul/2017:17:08:09 -0700] \"GET /pds/prod?q=OFSN+%3D+/data/voyager/vg_0008/extras/pds/jupiter/c2069xxx//c2069127.img+AND+RT+%3D+PDS_LABEL HTTP/1.1\" 200 33851 \"-\" \"Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)\" 689 34444",
					line);
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
		
	}
	
}