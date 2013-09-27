package gov.nasa.pds.search.core.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.constants.TestConstants;
import gov.nasa.pds.search.core.registry.RegistryHandler;
import gov.nasa.pds.search.core.registry.objects.SearchCoreExtrinsic;
import gov.nasa.pds.search.core.schema.Query;
import gov.nasa.pds.search.core.test.SearchCoreTest;
import gov.nasa.pds.search.core.test.SearchCoreTest.SingleTestRule;
import gov.nasa.pds.search.core.util.Debugger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class RegistryHandlerTest extends SearchCoreTest {

	private static final int NUM_ARCHIVE_INFO = 2;
	private static final File TEST_DIR = new File (System.getProperty("user.dir") + "/" + TestConstants.SEARCH_HOME_RELATIVE + "/" + Constants.SOLR_DOC_DIR);
	private RegistryHandler handler;
	
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
	}

	@Test
	public void testGetExtrinsicsByObjectInfo() {
		//this.handler.setQueryMax(Constants.QUERY_MAX);
		List<Query> queryList = new ArrayList<Query>();
		Query queryParam = new Query();
		
		queryParam.setRegistryPath("objectType");
		queryParam.setValue("Product_Data_Set_PDS3");
		queryList.add(queryParam);
		
		queryParam = new Query();
		queryParam.setRegistryPath("name");
		queryParam.setValue("*");
		queryList.add(queryParam);
		
		try {
			RegistryResults results = this.handler.getExtrinsicsByQuery(
					queryList);
			results.nextPage();
			assertEquals(results.getResultObjects().size(), 5);
		} catch (Exception e) {
			e.printStackTrace();
			fail("GetExtrinsicsByObjectInfo Test Failed. See stack trace.");
		}
	}
	
	@Test
	public void testGetExtrinsicsByArchiveInfo() {
		List<Query> queryList = new ArrayList<Query>();
		Query queryParam = new Query();
		
		queryParam.setRegistryPath("objectType");
		queryParam.setValue("Product_Context");
		queryList.add(queryParam);
		
		queryParam = new Query();
		queryParam.setRegistryPath("name");
		queryParam.setValue("*Archive Information");
		queryList.add(queryParam);
		
		try {
			RegistryResults results = this.handler.getExtrinsicsByQuery(
					queryList);
			results.nextPage();
			assertEquals(results.getResultObjects().size(), NUM_ARCHIVE_INFO);
		} catch (Exception e) {
			e.printStackTrace();
			fail("GetExtrinsicsByObjectInfo Test Failed. See stack trace.");
		}
	}
	
	@Test
	public void testGetExtrinsicsByLidvid() {
		try {
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
			this.handler.setPrimaryRegistries(Arrays.asList(TestConstants.PSA_REGISTRY_URL));
			this.handler.setSecondaryRegistries(Arrays.asList(TestConstants.PDS3_REGISTRY_URL));
			
			String lidvid = "urn:nasa:pds:context_pds3:node:node.psa";
			assertFalse(this.handler.getExtrinsicByLidvid(lidvid) == null);
		} catch (Exception e) {
			e.printStackTrace();
			fail("GetExtrinsicsByLidvidWithSecondaryRegistries Test failed. See stack trace.");
		}
	}
	
	@Test
	public void testGetAssociations() {
		try {
			Debugger.debugFlag = true;
			String lidvid = "urn:nasa:pds:phx_met:reduced:MS107RMH_00905704961_1C6EM1::1.0";
			
			this.handler.addPrimaryRegistry(TestConstants.PDS4_ATM_REGISTRY_URL);
			this.handler.setCheckAssociations(true);
			SearchCoreExtrinsic ext = this.handler.getExtrinsicByLidvid(lidvid);
			List<SearchCoreExtrinsic> searchExtList = this.handler.getAssociationsBySourceObject(ext, "file_ref");
			for (SearchCoreExtrinsic sce : searchExtList) {
				System.out.println("file_name: " + sce.getSlotValues("file_name").get(0));
				System.out.println("access_url: " + sce.getSlotValues("access_url").get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("testGetAssociations failed. See stack trace.");
		}
	}
}
