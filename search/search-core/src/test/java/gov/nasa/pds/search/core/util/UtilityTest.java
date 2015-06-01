package gov.nasa.pds.search.core.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.nasa.pds.search.core.SearchCoreLauncher;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.test.SearchCoreTest;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link Utility}
 * 
 * @author jpadams
 * 
 */
@RunWith(JUnit4.class)
public class UtilityTest extends SearchCoreTest {

	private static File tempDir;
	
	@BeforeClass
	public static void oneTimeSetUp () {
		tempDir = new File(System.getProperty("user.dir") + "/"
				+ TestConstants.SEARCH_HOME_RELATIVE
				+ "/utility-test-dir");
		
		tempDir.mkdirs();
	}
	
	@AfterClass
	public static void oneTimeTearDown () throws IOException {
		FileUtils.forceDelete(tempDir);
	}
	
	
	@Test
	@Ignore
	public void testUrlExists() {
		String url = "http://pds-gamma.jpl.nasa.gov:8080/registry-pds3";
		assertTrue(Utility.urlExists(url));
	}
	
	@Test
	public void testGetFileList() {
			try {
				// Create temporary files
				File file1 = new File(tempDir.getAbsolutePath()
						+ "/file1.txt");
				PrintWriter writer = new PrintWriter(file1);
				writer.write("file1");
				writer.flush();
				writer.close();
				
				File file2 = new File(tempDir.getAbsolutePath()
						+ "/file2.txt");
				writer = new PrintWriter(file2);
				writer.write("file2");
				writer.flush();
				writer.close();
				
				File file3 = new File(tempDir.getAbsolutePath()
						+ "/file3.txt");
				writer = new PrintWriter(file3);
				writer.write("file3");
				writer.flush();
				writer.close();
				
				List<File> fileList = new ArrayList<File>(FileUtils.listFiles(
						tempDir, new String[]{"txt"}, false));

				System.out.println("Using FileUtils");
				for (File file : fileList) {
					System.out.println(file.getAbsolutePath());
				}
				
				System.out.println("Using utility class");
				for (File file : Utility.getFileList(tempDir.getAbsolutePath(), Arrays.asList("file"))) {
					System.out.println(file.getAbsolutePath());
				}

				assertTrue(
						"Expected XML files not found in"
								+ System.getProperty("user.dir") + "/"
								+ TestConstants.SEARCH_HOME_RELATIVE, true);
			} catch (Exception e) {
				fail(e.getCause() + " - " + e.getMessage());
			}
	}

}
