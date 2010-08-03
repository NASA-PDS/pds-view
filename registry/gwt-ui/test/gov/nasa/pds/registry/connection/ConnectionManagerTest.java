package gov.nasa.pds.registry.connection;

import gov.nasa.pds.registry.server.connection.ConnectionManager;
import gov.nasa.pds.registry.ui.shared.ViewProducts;
import junit.framework.TestCase;

/**
 * Test connectivity with the REST interface to the product service. Note that
 * this requires the service to be running at the specified location.
 * 
 * @author jagander
 */
public class ConnectionManagerTest extends TestCase {

	public static void testGetProducts() {

		ViewProducts products = ConnectionManager.getProducts();
		assertEquals(1, products.getStart());
		assertEquals(20, products.size());

	}

}
