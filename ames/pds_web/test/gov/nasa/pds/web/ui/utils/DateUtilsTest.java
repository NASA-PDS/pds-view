package gov.nasa.pds.web.ui.utils;

import gov.nasa.pds.web.ui.actions.BaseTestAction;

import java.util.Date;

import org.junit.Test;

@SuppressWarnings("nls")
public class DateUtilsTest extends BaseTestAction {

	@Test
	public void testToEpochDate() {
		try {
			// string reperesents wed mar 10 1999 00:08:39 169
			// convert pds data string to long (epoch date)
			long epochDate = DateUtils.toEpochDate("1999  69  0  8 39 169"); //$NON-NLS-1$
			Date date = new Date(epochDate);
			// check parts of date are as expected
			// tostring returns dow mon dd hh:mm:ss zzz yyyy
			assertEquals("Wed Mar 10 00:08:39 PST 1999", date.toString()); //$NON-NLS-1$

		} catch (Exception e) {
			// +
		}
	}

	@Test
	public void testGetMillisecondsToDuration() {
		// less than a second, should drop it to nothing
		assertEquals("00:00:00", DateUtils.getMillisecondsToDuration(999));
		// check that 1000 milliseconds shows as a second
		assertEquals("00:00:01", DateUtils
				.getMillisecondsToDuration(DateUtils.SECOND));
		// check that 61 seconds rolls over into minutes
		assertEquals("00:01:01", DateUtils
				.getMillisecondsToDuration(61 * DateUtils.SECOND));
		// check that 61 minutes rolls into hours
		assertEquals("01:01:00", DateUtils
				.getMillisecondsToDuration(61 * DateUtils.MINUTE));
		// check that 25 hours doesn't roll into anything
		assertEquals("25:00:00", DateUtils
				.getMillisecondsToDuration(25 * DateUtils.HOUR));
		// check that hours can go beyond 2 places
		assertEquals("100:00:00", DateUtils
				.getMillisecondsToDuration(100 * DateUtils.HOUR));
		// check largest value from int(11) in db
		assertEquals("27777:46:39", DateUtils.getMillisecondsToDuration(Long
				.valueOf("99999999999")));
	}

	@Override
	protected void clearAction() {
		// TODO Auto-generated method stub

	}

}
