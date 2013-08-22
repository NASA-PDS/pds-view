package gov.nasa.pds.search.core.registry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.registry.RegistryHandler;
import gov.nasa.pds.search.core.util.Debugger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RegistryHandlerTest {

	private static final File TEST_DIR = new File (System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE + "/" + Constants.SOLR_DOC_DIR);
	private RegistryHandler handler;
	
	@BeforeClass
	public static void oneTimeSetUp() {
		//Debugger.debugFlag = true;
	}
	
	@Before
	public void setUp() throws RegistryHandlerException {
		this.handler = new RegistryHandler(
				Arrays.asList(TestConstants.PDS3_REGISTRY_URL),
				new ArrayList<String>(), 5);
	}

	@Test
	public void testGetExtrinsicsByObjectInfo() {
		System.out.println("------------------------------------------------");
		System.out.println("--- Testing GetExtrinsicsByObjectInfo method ---");
		System.out.println("------------------------------------------------");
		
		String objectType = "Product_Data_Set_PDS3";
		String objectName = "*";
		
		try {
			assertFalse(this.handler.getExtrinsicsByObjectInfo(
					objectType, objectName).isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
			fail("GetExtrinsicsByObjectInfo Test Failed. See stack trace.");
		}
	}
	
	@Test
	public void testGetExtrinsicsByLidvid() {
		try {
			System.out.println("--------------------------------------------");
			System.out.println("--- Testing getExtrinsicsByLidvid method ---");
			System.out.println("--------------------------------------------");
			
			String lidvid = "urn:nasa:pds:context_pds3:instrument:instrument.mri__dif::8.0";
			
			assertFalse(this.handler.getExtrinsicByLidvid(lidvid) == null);
		} catch (Exception e) {
			e.printStackTrace();
			fail("getExtrinsicsByLidvid Test failed. See stack trace.");
		}
	}
	
	@Test
	public void testGetExtrinsicsByLidvidWithSecondaryRegistries() {
		try {
			System.out.println("--------------------------------------------------------");
			System.out.println("--- GetExtrinsicsByLidvidWithSecondaryRegistries method ---");
			System.out.println("--------------------------------------------------------");
			
			this.handler.setPrimaryRegistries(Arrays.asList(TestConstants.PSA_REGISTRY_URL));
			this.handler.setSecondaryRegistries(Arrays.asList(TestConstants.PDS3_REGISTRY_URL));
			
			String lidvid = "urn:nasa:pds:context_pds3:node:node.psa";
			assertFalse(this.handler.getExtrinsicByLidvid(lidvid) == null);
		} catch (Exception e) {
			e.printStackTrace();
			fail("GetExtrinsicsByLidvidWithSecondaryRegistries Test failed. See stack trace.");
		}
	}
}
