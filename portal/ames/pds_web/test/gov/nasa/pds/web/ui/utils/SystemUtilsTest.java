package gov.nasa.pds.web.ui.utils;

import gov.nasa.pds.web.BaseTestCase;
import org.junit.Test;

@SuppressWarnings("nls")
public class SystemUtilsTest extends BaseTestCase {

	@Test
	public void testCheckAvailableMemoryMegs() {
		long requiredMemory = 99999;
		try {
			SystemUtils.checkAvailableMemory(requiredMemory);

			assertTrue(SystemUtils.bytesToMeg(SystemUtils.getAvailableMemory())
					+ " megs is greater than the expected excessive value "
					+ requiredMemory, false);
		} catch (Exception e) {
			assertTrue(true);
		}

		requiredMemory = 2;
		try {
			SystemUtils.checkAvailableMemory(requiredMemory);

		} catch (Exception e) {
			assertTrue(SystemUtils.bytesToMeg(SystemUtils.getAvailableMemory())
					+ " megs is less than the minimal value of "
					+ requiredMemory, false);
		}
	}

	@Test
	public void testCheckAvailableMemoryPercent() {
		double requiredMemory = 99.99;
		try {
			SystemUtils.checkAvailableMemory(requiredMemory);

			assertTrue(
					SystemUtils.getPercentMemoryFree()
							+ "% memory free is greater than the expected excessive value "
							+ requiredMemory + "%", false);
		} catch (Exception e) {
			assertTrue(true);
		}

		requiredMemory = 1.99;
		try {
			SystemUtils.checkAvailableMemory(requiredMemory);
		} catch (Exception e) {
			assertTrue(SystemUtils.getPercentMemoryFree()
					+ "% memory free is less than the minimal value of "
					+ requiredMemory + "%", false);
		}
	}
}
