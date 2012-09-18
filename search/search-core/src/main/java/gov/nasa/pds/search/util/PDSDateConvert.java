package gov.nasa.pds.search.util;

import gov.nasa.pds.search.core.constants.Constants;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * The DateTimeConverter class is necessary to convert
 * PDS4-compliant date/time strings into Solr-Compliant date/time Strings
 * 
 * The Solr DateField, which is necessary for performing time queries,
 * is ISO 8601 standard-compliant format, while PDS4 uses a variable
 * number of ASCII date/time formats.
 * 
 * @author jpadams
 *
 */
public class PDSDateConvert {

	/** Valid Date Time Formats **/
	private static final String[] DATE_TIME_FORMATS = {
		"yyyyMMddHHmmss",
		"yyyy-MM-dd'T'HH:mm:ss",
		"yyyy-DDD'T'HH:mm:ss",
		"yyyy-MM-dd'T'HH:mm",
		"yyyy-MM-dd'T'HH",
	};
	
	private static final String POS_YMD_FORMAT = "yyyy-MM-dd";
	
	private static final String POS_DOY_FORMAT = "yyyy-DDD";
	
	private static final String POS_YM_FORMAT = "yyyy-MM";
	
	private static final String POS_Y_FORMAT = "yyyy";
	
	/**
	 * Converts PDS4-Compliant Datetime Strings into Solr-Compliant Datetime Strings
	 * 
	 * @param dateTime
	 * @return
	 * @throws ParseException
	 */
	public static String convert(String input) throws Exception{
		SimpleDateFormat newFrmt = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		
		String datetime = input.toUpperCase().replaceAll("Z", "").replace("PROCESSING__", "");
		
		String prefix = "";			// String to hold negative sign if datetime starts with a "-" , designating BC
		if (datetime.startsWith("-")) {
			prefix = "-";
			datetime = datetime.substring(1);
		}
		
		// Before doing anything, check if date/time value is
		// a valid unknown, in which case return default
		if (Arrays.asList(Constants.VALID_UNK_VALUES).contains(datetime)) {
			return Constants.DEFAULT_DATETIME;
		} else if(datetime.equals("TBD")) {
			System.out.println("Bad Date Value: " + datetime);
			return Constants.DEFAULT_DATETIME;
		} else if(datetime.equals("NOT_APPLICABLE")) {
			System.out.println("Bad Date Value: " + datetime);
			return Constants.DEFAULT_DATETIME;
		} else if(datetime.matches("[A-Z]*")) {
			System.out.println("Bad Date Value: " + datetime);
			return Constants.DEFAULT_DATETIME;			
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
					return prefix + newFrmt.format(parseDate(strFrmt, datetime)) + milliseconds;
				} catch (ParseException e) { }
			}
		} else {
			Date outputDate = null;
			int dtLength = datetime.length();
			if (dtLength == POS_YMD_FORMAT.length() || dtLength == POS_YMD_FORMAT.length()-1 ) {
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
		throw new Exception("Bad Date Value: " + input);
		//return null;
	}
	
	/**
	 * Fix too many values after decimal
	 * When there are > 3 values after decimal for time value SimpleDateFormat adds the value in the
	 * tenths place to seconds and moves the other values to the left i.e ...:00.2456 -> ...:02.456
	 * 
	 * TODO - Check about milliseconds.  If datetime is ...:00.9 is it :00.900 or :00.009
	 * 
	 * @param value
	 * @return
	 */
	private static String getMilliseconds(String[] timeArray) {
		if (timeArray.length > 1) {
			String fractions = timeArray[1];
			if (fractions.length() > 3) {
				fractions = fractions.substring(0,3);
			} else if (fractions.length() == 2) {
				fractions = fractions + "0";
			} else if (fractions.length() == 1) {
				fractions = fractions + "00";
			}
			return "." + fractions + "Z";
		} else {
			return ".000Z";
		}
		
	}
	
	/**
	 * Parse the given date/time string using input format and date
	 * and return the resulting <code>Date</code> object.
	 * 
	 * @param inputDate
	 * @return
	 * @throws ParseException
	 */
	private static Date parseDate(String format, String inputDate) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(format);
    	return dateFormat.parse(inputDate);
	}
}
