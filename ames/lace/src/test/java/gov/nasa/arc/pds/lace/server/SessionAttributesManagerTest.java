package gov.nasa.arc.pds.lace.server;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import gov.nasa.arc.pds.lace.test.util.EmptyModule;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

@Guice(modules = {EmptyModule.class})
public class SessionAttributesManagerTest {

	private static final String SESSION1 = "session1";
	private static final String SESSION2 = "session2";

	@Inject
	private SessionAttributesManager manager;

	@BeforeMethod
	public void init() {
		manager.clear();
	}

	@Test
	public void testNoSuchAttribute() {
		assertNull(manager.getAttribute(SESSION1, "x", String.class));
	}

	@Test
	public void testGetAttribute() {
		manager.setAttribute(SESSION1, "x", "y");
		manager.setAttribute(SESSION1, "a", new Integer(123));
		manager.setAttribute(SESSION2, "x", "z");

		assertEquals(manager.getAttribute(SESSION1, "x", String.class), "y");
		assertEquals(manager.getAttribute(SESSION1, "a", Integer.class), new Integer(123));
		assertEquals(manager.getAttribute(SESSION2, "x", String.class), "z");

		assertNull(manager.getAttribute(SESSION2, "a", Integer.class));
	}

	@Test
	public void testRemoveAttribute() {
		manager.setAttribute(SESSION1, "x", "y");
		manager.setAttribute(SESSION1, "a", new Integer(123));
		manager.setAttribute(SESSION2, "x", "z");

		assertEquals(manager.getAttribute(SESSION1, "x", String.class), "y");
		manager.removeAttribute(SESSION1, "x");
		assertNull(manager.getAttribute(SESSION1, "x", String.class));
	}

	@Test
	public void testRemoveSession() {
		manager.setAttribute(SESSION1, "x", "y");
		manager.setAttribute(SESSION1, "a", new Integer(123));
		manager.setAttribute(SESSION2, "x", "z");

		manager.removeSession(SESSION1);

		assertNull(manager.getAttribute(SESSION1, "x", String.class));
		assertNull(manager.getAttribute(SESSION1, "a", Integer.class));
		assertEquals(manager.getAttribute(SESSION2, "x", String.class), "z");
	}

	@Test(expectedExceptions = {ClassCastException.class})
	public void testWrongClass() {
		manager.setAttribute(SESSION1, "a", new Integer(123));
		@SuppressWarnings("unused")
		String value = manager.getAttribute(SESSION1, "a", String.class);
	}

}
