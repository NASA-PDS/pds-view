package gov.nasa.pds.search.core.util;

import static org.junit.Assert.assertTrue;
import gov.nasa.pds.search.core.test.SearchCoreTest;

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

	@Test
	public void testUrlExists() {
		String url = "http://pdsbeta.jpl.nasa.gov:8080/registry";
		assertTrue(Utility.urlExists(url));
	}

}
