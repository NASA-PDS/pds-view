//	Copyright 2013-2014, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//
package gov.nasa.pds.registry.client.results;

import static org.junit.Assert.*;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.client.results.SlotFilter;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.test.RegistryCoreTest;
import gov.nasa.pds.registry.test.constants.TestConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SlotFilterTest  extends RegistryCoreTest {

	private static final int NUM_ARCHIVE_INFO = 5;
	
	private static ExtrinsicObject extObj;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			ExtrinsicFilter filter = new ExtrinsicFilter.Builder().lid("urn:nasa:pds:context_pds3:data_set:data_set.mer2-m-hazcam-5-mesh-ops-v1.0").build();
	
			// Create the query
			RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>().filter(filter).build();
			
			PagedResponse<ExtrinsicObject> pr = null;
	
			RegistryClient client = new RegistryClient(TestConstants.PDS3_REGISTRY_URL);	// Initialize the client
			//if (this.query.getFilter() instanceof ExtrinsicFilter) {
				//System.out.println(start + " - " + pageLength);
			pr = client.getExtrinsics((RegistryQuery<ExtrinsicFilter>)query, 0, 100);	// Get PagedResponse with pageLength
	
			extObj = pr.getResults().get(0);
			System.out.println("Testing against lid: " + extObj.getLid());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {	
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test to ensure the filter allows those matching objects to pass through
	 */
	@Test
	public void testFilterAccept() {
		try {
			// Create our slot filter
			SlotFilter slotFilter = new SlotFilter("data_set_id", "MER2-M-HAZCAM-5-MESH-OPS-V1.0");
			
			// Apply the filter to the resultObjects list and see if the extObj will be accepted
			Object returnedObject = slotFilter.applyFilter(extObj);
			
			// Check if we got our extrinsic returned as valid
			assertFalse(returnedObject == null);
			System.out.println("lid of object returned: "+((ExtrinsicObject)returnedObject).getLid());
		} catch (Exception e){
			e.printStackTrace();
			fail("testFilterSuccess Test failed. See stack trace.");
		}

	}
	
	/**
	 * Test to ensure the filter rejects those objects that do not match
	 */
	@Test
	public void testFilterReject() {
		try {
			// Create our slot filter
			SlotFilter slotFilter = new SlotFilter("data_set_id", "REJECT_THIS_DSID");
			
			// Apply the filter to the resultObjects list and see if the extObj will be accepted
			Object returnedObject = slotFilter.applyFilter(extObj);
			
			// Check if we got our extrinsic returned as valid
			assertTrue(returnedObject == null);
			System.out.println("object returned was null, as expected");
		} catch (Exception e){
			e.printStackTrace();
			fail("testFilterReject Test failed. See stack trace.");
		}

	}

}
