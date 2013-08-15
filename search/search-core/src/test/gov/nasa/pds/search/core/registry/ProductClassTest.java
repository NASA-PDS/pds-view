/**
 * 
 */
package gov.nasa.pds.search.core.registry;

import static org.junit.Assert.*;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.formatter.SearchCoreFormatter;
import gov.nasa.pds.search.core.logging.handler.SearchCoreStreamHandler;
import gov.nasa.pds.search.core.registry.ProductClass;
import gov.nasa.pds.search.core.registry.ProductClassException;
import gov.nasa.pds.search.core.util.Debugger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

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
	
	@BeforeClass
	public static void oneTimeSetUp() {
		TEST_DIR.mkdirs();
		
		//Debugger.debugFlag = true;
    	Logger logger = Logger.getLogger("");
	    logger.addHandler(new SearchCoreStreamHandler(System.out,
	    		  ToolsLevel.DEBUG, new SearchCoreFormatter()));
	}
	
	/*@AfterClass
	public static void oneTimeTearDown() throws IOException {
		FileUtils.deleteDirectory(TEST_DIR);
	}*/
	
	@Before 
	public void setUp() throws IOException {
		// Create new productClass instance for given class.
		this.pc = new ProductClass(
				TEST_DIR,
				Arrays.asList(TestConstants.PDS3_REGISTRY_URL),
				new ArrayList<String>());
		this.pc.setQueryMax(1);
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
	public void testQueryPDS4() throws SearchCoreFatalException {
		try {			
			System.out.println("---------------------------------------------------");
			System.out.println("--- Testing query method with PDS - PDS4 Config ---");
			System.out.println("---------------------------------------------------");
			String[] extensions = { "xml" };
			this.pc = new ProductClass(
					TEST_DIR,
					Arrays.asList(TestConstants.PDS4_ATM_REGISTRY_URL),	// primary registry
					Arrays.asList(TestConstants.PDS4_REGISTRY_URL));	// secondary registry
			this.pc.setQueryMax(1);
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
	public void testQueryPSA() throws SearchCoreFatalException {
		try {			
			System.out.println("---------------------------------------------------");
			System.out.println("--- Testing query method with PSA - PDS3 Config ---");
			System.out.println("---------------------------------------------------");
			String[] extensions = { "xml" };
			this.pc = new ProductClass(
					TEST_DIR,
					Arrays.asList(TestConstants.PSA_REGISTRY_URL),	// primary registry
					Arrays.asList(TestConstants.PDS3_REGISTRY_URL));	// secondary registry
			this.pc.setQueryMax(1);
			
			for (File file : FileUtils.listFiles(new File(System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE + "/psa/pds3/"), extensions, false)) {
				if (this.pc.query(file).isEmpty()) {
					fail("Test failed - Config returned empty list of Extrinsics: " + file.getAbsolutePath());
				}
			}
		} catch (ProductClassException e) {
			fail("PDS3 Query Test failed. See stack trace.");
		}
	}
	
	@Test
	public void testQueryPSAUsingConfigSpecifiedRegistry() throws SearchCoreFatalException {
		try {			
			System.out.println("---------------------------------------------------");
			System.out.println("--- Testing testQueryPSAUsingConfigSpecifiedRegistry ---");
			System.out.println("---------------------------------------------------");
			String[] extensions = { "xml" };
			this.pc = new ProductClass(
					TEST_DIR,
					Arrays.asList(TestConstants.PSA_REGISTRY_URL),	// primary registry
					new ArrayList<String>());	// secondary registry
			this.pc.setQueryMax(1);
			
			for (File file : FileUtils.listFiles(new File(System.getProperty("user.dir") + "/" + TestConstants.CONFIG_DIR_RELATIVE + "/psa/pds3/"), extensions, false)) {
				if (this.pc.query(file).isEmpty()) {
					fail("Test failed - Config returned empty list of Extrinsics: " + file.getAbsolutePath());
				}
			}
		} catch (ProductClassException e) {
			fail("PDS3 Query Test failed. See stack trace.");
		}
	}
	
	@Test
	public void testQueryWithoutPrimaryRegistry() throws SearchCoreFatalException {
		try {			
			System.out.println("----------------------------------------------------------------");
			System.out.println("--- Testing bad query with no primary registries Test config ---");
			System.out.println("----------------------------------------------------------------");
			
			this.pc = new ProductClass(
					TEST_DIR,
					new ArrayList<String>(),	// primary registry
					new ArrayList<String>());	// secondary registry
			
			assertTrue(this.pc.query(new File(System.getProperty("user.dir") + "/" + TestConstants.TEST_DIR_RELATIVE + "core-config-test-1.xml")).isEmpty());
		} catch (ProductClassException e) {
			e.printStackTrace();
			fail("Query Test failed. See stack trace.");
		}
	}
	
}
