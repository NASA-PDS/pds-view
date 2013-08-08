/**
 * 
 */
package gov.nasa.pds.search.core.extractor;

import static org.junit.Assert.*;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.util.Debugger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link ProductClass}
 * 
 * @author jpadams
 *
 */
@RunWith(JUnit4.class)
public class ProductClassTest {

	private static final File TEST_DIR = new File (System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE + "/" + Constants.REGISTRY_DATA_DIR);
	
	private ProductClass pc;
	private PrintWriter writer;
	
	@BeforeClass
	public static void oneTimeSetUp() {
		TEST_DIR.mkdirs();
		
		Debugger.debugFlag = true;
	}
	
	@AfterClass
	public static void oneTimeTearDown() throws IOException {
		FileUtils.deleteDirectory(TEST_DIR);
	}
	
	@Before 
	public void setUp() throws IOException {
		// Create new productClass instance for given class.
		this.writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(TEST_DIR, "run.log"))));
		this.pc = new ProductClass(
				this.writer,
				TEST_DIR,
				TestConstants.PDS3_REGISTRY_URL,
				1);
	}
	
	@After
	public void tearDown() throws IOException {
		this.writer.close();
	}
	
	@Test
	public void testQuery() throws SearchCoreFatalException {
		try {			
			System.out.println("---------------------------------------------");
			System.out.println("--- Testing query method with Test config ---");
			System.out.println("---------------------------------------------");
			
			
			assertFalse(this.pc.query(new File(System.getProperty("user.dir") + "/" + TestConstants.TEST_DIR_RELATIVE + "core-config-test-1.xml")).isEmpty());
		} catch (ProductClassException e) {
			e.printStackTrace();
			fail("Query Test failed. See stack trace.");
		}
	}
	
	@Test
	public void testGetExtrinsicsByLidvid() {
		try {
			System.out.println("--------------------------------------------");
			System.out.println("--- Testing getExtrinsicsByLidvid method ---");
			System.out.println("--------------------------------------------");
			
			String lidvid = "urn:nasa:pds:context_pds3:instrument:instrument.mri__dif::8.0";
			
			assertFalse(this.pc.getExtrinsicsByLidvid(lidvid).isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
			fail("getExtrinsicsByLidvid Test failed. See stack trace.");
		}
	}
	
	@Test
	public void testQueryPDS3() throws SearchCoreFatalException {
		try {			
			System.out.println("---------------------------------------------------");
			System.out.println("--- Testing query method with PDS - PDS3 Config ---");
			System.out.println("---------------------------------------------------");
			String[] extensions = { "xml" };
			for (File file : FileUtils.listFiles(new File(System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE + "/pds/pds3/"), extensions, false)) {
				if (this.pc.query(file).isEmpty()) {
					fail("Test failed - Config returned empty list of Extrinsics: " + file.getAbsolutePath());
				}
			}
		} catch (ProductClassException e) {
			e.printStackTrace();
			fail("PDS3 Query Test failed. See stack trace.");
		}
	}
	
	@Test
	@Ignore // PDS4 Registry is down
	public void testQueryPDS4() throws SearchCoreFatalException {
		try {			
			System.out.println("---------------------------------------------------");
			System.out.println("--- Testing query method with PDS - PDS4 Config ---");
			System.out.println("---------------------------------------------------");
			String[] extensions = { "xml" };
			this.pc.setRegistryUrl(TestConstants.PDS4_REGISTRY_URL);
			for (File file : FileUtils.listFiles(new File(System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE + "/pds/pds4/"), extensions, false)) {
				if (this.pc.query(file).isEmpty()) {
					fail("Test failed - Config returned empty list of Extrinsics: " + file.getAbsolutePath());
				}
			}
		} catch (ProductClassException e) {
			e.printStackTrace();
			fail("PDS4 Query Test failed. See stack trace.");
		}
	}
	
	@Test
	@Ignore
	public void testQueryPSA() throws SearchCoreFatalException {
		try {			
			System.out.println("---------------------------------------------------");
			System.out.println("--- Testing query method with PSA - PDS3 Config ---");
			System.out.println("---------------------------------------------------");
			String[] extensions = { "xml" };
			this.pc.setRegistryUrl(TestConstants.PSA_REGISTRY_URL);
			for (File file : FileUtils.listFiles(new File(System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE + "/psa/pds3/"), extensions, false)) {
				if (this.pc.query(file).isEmpty()) {
					fail("Test failed - Config returned empty list of Extrinsics: " + file.getAbsolutePath());
				}
			}
		} catch (ProductClassException e) {
			fail("PDS3 Query Test failed. See stack trace.");
		}
	}
	
}
