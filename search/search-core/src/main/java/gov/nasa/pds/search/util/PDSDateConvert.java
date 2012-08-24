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
	
	private static final String YMD_FORMAT = "yyyy-MM-dd";
	
	private static final String DOY_FORMAT = "yyyy-DDD";
	
	private static final String YM_FORMAT = "yyyy-MM";
	
	private static final String Y_FORMAT = "yyyy";
	
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
				//System.out.println(strFrmt + " - " + datetime);
				DateFormat format = new SimpleDateFormat(strFrmt);
				try {
					return newFrmt.format(format.parse(datetime)) + milliseconds;
				} catch (ParseException e) { }
			}
		} else {
			Date outputDate = null;
			int dtLength = datetime.length();
			if (dtLength == YMD_FORMAT.length() || dtLength == YMD_FORMAT.length()-1 ) {
				outputDate = parseDate(YMD_FORMAT, datetime);
			} else if (dtLength == DOY_FORMAT.length()) {
				outputDate = parseDate(DOY_FORMAT, datetime);
			} else if (dtLength == YM_FORMAT.length()) { 
				outputDate = parseDate(YM_FORMAT, datetime);
			} else if (dtLength == Y_FORMAT.length()) { 
				outputDate = parseDate(Y_FORMAT, datetime);
			}

			if (outputDate != null) {
				return newFrmt.format(outputDate) + milliseconds;
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
	
	/**
	 * Parse the given date/time string in Year-Month format,
	 * which is the YMD format without the day specified,
	 * and return the resulting <code>Date</code> object.
	 * 
	 * The format is as follows: "yyyy-MM"
	 * 
	 * @param inputDate
	 * @return
	 * @throws ParseException
	 */
	private static Date ymParse(String inputDate) throws ParseException {
		DateFormat format = new SimpleDateFormat(YM_FORMAT);
    	return format.parse(inputDate);
	}

	/**
	 * Parse the given date/time string in Day-of-year format,
	 * and return the resulting <code>Date</code> object.
	 * 
	 * The format is as follows: "yyyy-DDD"
	 * 
	 * @param inputDate
	 * @return
	 * @throws ParseException
	 */
	private static Date doyParse(String inputDate) throws ParseException {
		DateFormat format = new SimpleDateFormat(DOY_FORMAT);
    	return format.parse(inputDate);
	}
	
	/**
	 * Parse the given date/time string in Year format,
	 * which is the YMD format without the day or month specified,
	 * and return the resulting <code>Date</code> object.
	 * 
	 * The format is as follows: "yyyy"
	 * 
	 * @param inputDate
	 * @return
	 * @throws ParseException
	 */
	private static Date yParse(String inputDate) throws ParseException {
		DateFormat format = new SimpleDateFormat(Y_FORMAT);
    	return format.parse(inputDate);
	}
}
