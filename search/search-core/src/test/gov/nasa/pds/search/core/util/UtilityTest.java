package gov.nasa.pds.search.core.util;

import static org.junit.Assert.*;
import gov.nasa.pds.search.core.util.Utility;

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
public class UtilityTest {

	@Test
	public void testUrlExists() {
		String url = "http://espn.com";
		assertTrue(Utility.urlExists(url));
	}
	
}
