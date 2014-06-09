/**
 * 
 */
package gov.nasa.pds.registry.util;

import static org.junit.Assert.*;
import gov.nasa.pds.registry.client.results.AttributeFilter;
import gov.nasa.pds.registry.client.results.RegistryHandler;
import gov.nasa.pds.registry.client.results.RegistryHandlerException;
import gov.nasa.pds.registry.client.results.RegistryResults;
import gov.nasa.pds.registry.client.results.ResultsFilter;
import gov.nasa.pds.registry.client.results.SlotFilter;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.wrapper.ExtendedExtrinsicObject;
import gov.nasa.pds.registry.test.RegistryCoreTest;
import gov.nasa.pds.registry.test.constants.TestConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link RegistryHandler}
 * 
 * @author jpadams
 * 
 */
@RunWith(JUnit4.class)
public class RegistryHandlerTest extends RegistryCoreTest {
	
	private static final int NUM_ARCHIVE_INFO = 5;
	
	private RegistryHandler handler;
	private Map<String, String> queryMap;

	@Rule
	public SingleTestRule test = new SingleTestRule("");

	@BeforeClass
	public static void oneTimeSetUp() {
		Debugger.debugFlag = true;
	}

	@Before
	public void setUp() throws RegistryHandlerException {
		this.handler = new RegistryHandler(
				Arrays.asList(TestConstants.PDS3_REGISTRY_URL),
				new ArrayList<String>(), 5);
		
		this.queryMap = new HashMap<String, String>();
	}

	@Test
	public void testGetExtrinsicsByObjectInfo() {
		this.queryMap.put("objectType", "Product_Data_Set_PDS3");
		this.queryMap.put("name", "*");

		try {
			RegistryResults results = this.handler.getExtrinsicsByQuery(this.queryMap);
			results.nextPage();
			assertEquals(5, results.getResultObjects().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("GetExtrinsicsByObjectInfo Test Failed. See stack trace.");
		}
	}

	@Test
	public void testGetExtrinsicsByArchiveInfo() {
		this.queryMap.put("objectType", "Product_Context");
		this.queryMap.put("name", "*Archive Information");

		try {
			RegistryResults results = this.handler.getExtrinsicsByQuery(this.queryMap);
			results.nextPage();
			assertEquals(NUM_ARCHIVE_INFO, results.getResultObjects().size());
		} catch (Exception e) {
			e.printStackTrace();
			fail("GetExtrinsicsByObjectInfo Test Failed. See stack trace.");
		}
	}
	
	@Test
	public void testGetExtrinsicsWithAttributeFilter() {
		try {
			this.handler = new RegistryHandler(
					Arrays.asList(TestConstants.PDS3_REGISTRY_URL),
					new ArrayList<String>(), 999999999);
			
			List<ResultsFilter> filterList = new ArrayList<ResultsFilter>();
			filterList.add(new AttributeFilter("objectType", "Product_Data_Set_PDS3"));
			//filterList.add(new SlotFilter("lid", "urn:nasa:pds:context_pds3:data_set:data_set.mer2-m-hazcam-5-mesh-ops-v1.0"));
			RegistryResults results = this.handler.getExtrinsicsWithFilter(filterList);
			results.nextPage();
			ExtrinsicObject extObj = (ExtrinsicObject)results.getResultObjects().get(0);
			assertEquals("Product_Data_Set_PDS3", extObj.getObjectType());
		} catch (Exception e) {
			e.printStackTrace();
			fail("GetExtrinsicsByObjectInfo Test Failed. See stack trace.");
		}
	}
	
	@Test
	public void testGetExtrinsicsWithSlotFilter() {
		try {
			this.handler = new RegistryHandler(
					Arrays.asList(TestConstants.PDS4_REGISTRY_URL),
					new ArrayList<String>(), 999999999);
			
			List<ResultsFilter> filterList = new ArrayList<ResultsFilter>();
			filterList.add(new AttributeFilter("objectType", "Product_Context"));
			filterList.add(new SlotFilter("data_class", "Instrument"));
			RegistryResults results = this.handler.getExtrinsicsWithFilter(filterList);
			results.nextPage();
			//Debugger.debug(String.valueOf(results.getResultObjects().size()));
			ExtendedExtrinsicObject extObj;
			
			for (Object obj : results.getResultObjects()) {
				 extObj = new ExtendedExtrinsicObject((ExtrinsicObject)obj); 
				 if (!extObj.getSlotValues("data_class").get(0).equals("Instrument")) {
					 fail("'data_class' slot not equal to 'Instrument' as expected.");
				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("GetExtrinsicsByObjectInfo Test Failed. See stack trace.");
		}
	}
	
	@Test
	public void testGetExtrinsicsWithMultipleSlotFilters() {
		try {
			this.handler = new RegistryHandler(
					Arrays.asList(TestConstants.PDS4_REGISTRY_URL),
					new ArrayList<String>(), 999999999);
			
			List<ResultsFilter> filterList = new ArrayList<ResultsFilter>();
			filterList.add(new AttributeFilter("objectType", "Product_Context"));
			filterList.add(new SlotFilter("data_class", "Instrument"));
			filterList.add(new SlotFilter("instrument_type", "Unknown"));
			//filterList.add(new AttributeFilter("lid", "urn:nasa:pds:context:target:comet.c-soho_2004_x10"));
			RegistryResults results = this.handler.getExtrinsicsWithFilter(filterList);
			results.nextPage();
			//Debugger.debug(String.valueOf(results.getResultObjects().size()));
			ExtendedExtrinsicObject extObj;
			
			for (Object obj : results.getResultObjects()) {
				 extObj = new ExtendedExtrinsicObject((ExtrinsicObject)obj); 
				 if (!extObj.getSlotValues("data_class").get(0).equals("Instrument")) {
					 fail("'data_class' slot not equal to 'Instrument' as expected.");
				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("GetExtrinsicsByObjectInfo Test Failed. See stack trace.");
		}
	}

	@Test
	public void testGetExtrinsicsByLidvid() {
		try {
			String lidvid = "urn:nasa:pds:context_pds3:instrument:instrument.mri__dif::8.0";
			this.handler.setQueryMax(10);
			assertFalse(this.handler.getExtrinsicByLidvid(lidvid) == null);
		} catch (Exception e) {
			e.printStackTrace();
			fail("getExtrinsicsByLidvid Test failed. See stack trace.");
		}
	}
	
	@Test
	public void testGetExtrinsicsByGuid() {
		try {
			String guid = "urn:uuid:1574b4d6-6f64-402f-8b9f-147b06a6ce67";
			this.handler.setQueryMax(10);
			assertFalse(this.handler.getExtrinsicByGuid(guid) == null);
		} catch (Exception e) {
			e.printStackTrace();
			fail("getExtrinsicsByLidvid Test failed. See stack trace.");
		}
	}

	@Test
	public void testGetExtrinsicsByLidvidWithSecondaryRegistries() {
		try {
			this.handler.setPrimaryRegistries(Arrays
					.asList(TestConstants.PSA_REGISTRY_URL));
			this.handler.setSecondaryRegistries(Arrays
					.asList(TestConstants.PDS3_REGISTRY_URL));

			String lidvid = "urn:nasa:pds:context_pds3:node:node.psa";
			assertFalse(this.handler.getExtrinsicByLidvid(lidvid) == null);
		} catch (Exception e) {
			e.printStackTrace();
			fail("GetExtrinsicsByLidvidWithSecondaryRegistries Test failed. See stack trace.");
		}
	}

	@Test
	@Ignore
	public void testGetAssociations() {
		try {
			String lidvid = "urn:nasa:pds:phx_met:reduced:MS107RMH_00905704961_1C6EM1::1.0";

			this.handler
					.addPrimaryRegistry(TestConstants.PDS4_REGISTRY_URL);
			this.handler.setCheckAssociations(true);
			ExtendedExtrinsicObject ext = this.handler
					.getExtrinsicByLidvid(lidvid);
			List<ExtendedExtrinsicObject> searchExtList = this.handler
					.getAssociationsBySourceObject(ext, "file_ref");
			for (ExtendedExtrinsicObject sce : searchExtList) {
				System.out.println("file_name: "
						+ sce.getSlotValues("file_name").get(0));
				System.out.println("access_url: "
						+ sce.getSlotValues("access_url").get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("testGetAssociations failed. See stack trace.");
		}
	} 
}