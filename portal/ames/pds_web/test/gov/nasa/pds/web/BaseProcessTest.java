package gov.nasa.pds.web;

import gov.nasa.pds.web.ui.actions.dataManagement.ValidationProcess;

import org.junit.Test;

public class BaseProcessTest extends BaseTestCase {

	@SuppressWarnings("nls")
	@Test
	public void testIdOverride() {
		BaseProcess process = new ValidationProcess("234");
		assertEquals("234", process.getID());
	}
}
