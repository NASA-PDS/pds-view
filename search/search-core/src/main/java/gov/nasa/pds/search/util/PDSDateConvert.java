//	Copyright 2009-2012, by the California Institute of Technology.
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

package gov.nasa.pds.search.util;

import gov.nasa.pds.search.core.constants.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

/**
 * The DateTimeConverter class is necessary to convert PDS4-compliant date/time
 * strings into Solr-Compliant date/time Strings
 * 
 * The Solr DateField, which is necessary for performing time queries, is ISO
 * 8601 standard-compliant format, while PDS4 uses a variable number of ASCII
 * date/time formats.
 * 
 * @author jpadams
 * 
 */
public class PDSDateConvert {

	/** Valid Date Time Formats. **/
	private static final String[] DATE_TIME_FORMATS = { "yyyyMMddHHmmss",
			"yyyy-MM-dd'T'HH:mm:ss", "yyyy-DDD'T'HH:mm:ss",
			"yyyy-MM-dd'T'HH:mm", "yyyy-MM-dd'T'HH", };

	/** Positive Year-Month-Day Date Format. **/
	private static final String POS_YMD_FORMAT = "yyyy-MM-dd";

	/** Positive Year-DayOfYear Date Format. **/
	private static final String POS_DOY_FORMAT = "yyyy-DDD";

	/** Positive Year-Month Date Format. **/
	private static final String POS_YM_FORMAT = "yyyy-MM";

	/** Positive Year Date Format. **/
	private static final String POS_Y_FORMAT = "yyyy";
	
	/** Maximum number of milliseconds for ISO-8601 */
	private static final int MAX_NUM_MS = 3;

	/**
	 * Converts PDS4-Compliant Datetime Strings into Solr-Compliant Datetime
	 * Strings.
	 * 
	 * @param dateTime
	 * @return
	 * @throws ParseException
	 */
	public static String convert(String name, String input) throws Exception {
		SimpleDateFormat newFrmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		newFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		newFrmt.setLenient(false);
		String datetime = input.toUpperCase().replaceAll("Z", "").replace("PROCESSING__", "");
		

		// String to hold negative sign if datetime starts with a "-" ,
		// designating BC
		String prefix = "";
		if (datetime.startsWith("-")) {
			prefix = "-";
			datetime = datetime.substring(1);
		}

		// Before doing anything, check if date/time value is
		// a valid unknown, in which case return default
		if (Arrays.asList(Constants.VALID_UNK_VALUES).contains(datetime)) {
			return getDefaultTime(name);
		} else if (datetime.equals("TBD") || datetime.equals("NOT_APPLICABLE")
				|| datetime.matches("[A-Z]*")) {
			recordInvalidDate(datetime);
			return getDefaultTime(name);
		} else if (datetime.contains("_")) { // TODO Should we replace this or
												// throw error?
			// recordInvalidDate(datetime);
			datetime = datetime.replace("_", "T");
		}
		// Split datetime from fraction of seconds
		// SimpleDateFormat only handles milliseconds
		String[] timeArray = datetime.split("\\.");

		// Get only datetime of value, ignoring fractions of second
		datetime = timeArray[0];
		String milliseconds = getMilliseconds(timeArray);

		// Loop through datetime formats
		if (datetime.length() > 10) {
			for (String strFrmt : DATE_TIME_FORMATS) {
				try {
					return prefix
							+ newFrmt.format(parseDate(strFrmt, datetime))
							+ milliseconds;
				} catch (ParseException e) { /* Ignore then parse fails */
				}
			}
		} else {
			Date outputDate = null;
			int dtLength = datetime.length();
			if (dtLength == POS_YMD_FORMAT.length()
					|| dtLength == POS_YMD_FORMAT.length() - 1) {
				outputDate = parseDate(POS_YMD_FORMAT, datetime);
			} else if (dtLength == POS_DOY_FORMAT.length()) {
				outputDate = parseDate(POS_DOY_FORMAT, datetime);
			} else if (dtLength == POS_YM_FORMAT.length()) {
				outputDate = parseDate(POS_YM_FORMAT, datetime);
			} else if (dtLength == POS_Y_FORMAT.length()) {
				outputDate = parseDate(POS_Y_FORMAT, datetime);
			}

			if (outputDate != null) {
				return prefix + newFrmt.format(outputDate) + milliseconds;
			}
		}

		// Remaining formats are invalid or not captured

		// TODO Throw error instead of returning default
		recordInvalidDate(input);
		// throw new Exception("Bad Date Value: " + input);
		return getDefaultTime(name);
	}

	/**
	 * Fix too many values after decimal. When there are > 3 values after
	 * decimal for time value SimpleDateFormat adds the value in the tenths
	 * place to seconds and moves the other values to the left i.e ...:00.2456
	 * -> ...:02.456
	 * 
	 * TODO - Check about milliseconds. If datetime is ...:00.9 is it :00.900 or
	 * :00.009
	 * 
	 * @param value
	 * @return
	 */
	private static String getMilliseconds(String[] timeArray) {
		if (timeArray.length > 1) {
			String fractions = timeArray[1];
			if (fractions.length() > MAX_NUM_MS) {
				fractions = fractions.substring(0, 3);
			} else if (fractions.length() == MAX_NUM_MS-1) {
				fractions = fractions + "0";
			} else if (fractions.length() == MAX_NUM_MS-2) {
				fractions = fractions + "00";
			}
			return "." + fractions + "Z";
		} else {
			return ".000Z";
		}

	}

	/**
	 * Parse the given date/time string using input format and date and return
	 * the resulting <code>Date</code> object.
	 * 
	 * @param inputDate
	 * @return
	 * @throws ParseException
	 */
	private static Date parseDate(String format, String inputDate)
			throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.parse(inputDate);
	}

	/**
	 * Capture Invalid Dates
	 * 
	 * @param datetime
	 */
	private static void recordInvalidDate(String datetime) {
		System.out.println("Ignoring Invalid Date Value: " + datetime);
	}

	/**
	 * Get a default time depending on the name of the field. "start" fields
	 * will receive an "early" default, while remaining fields will receive a
	 * default in the future.
	 * 
	 * @param name
	 * @return
	 */
	private static String getDefaultTime(String name) {
		if (name.toLowerCase().contains("start")) {
			return Constants.DEFAULT_STARTTIME;
		} else {
			return Constants.DEFAULT_STOPTIME;
		}
	}
}
