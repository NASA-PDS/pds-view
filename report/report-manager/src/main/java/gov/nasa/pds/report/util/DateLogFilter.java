//	Copyright 2013, by the California Institute of Technology.
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
package gov.nasa.pds.report.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import gov.nasa.pds.report.ReportManagerException;

/**
 * 
 * @author resneck
 *
 * This class is used to filter specific logs based upon the date of the data
 * that they contain. At the time of implementation, we did'nt have enough
 * filters to justify making this a subclass of a Filter class or interface or
 * such, but this could be done in the future if more numerous and complex 
 * filters (especially log filters) are required for the Report Service.
 * 
 * The class works by taking in up to two Date Strings using the setStartDate()
 * and setEndDate() methods to mark the start and end days (both inclusive) of
 * the desired range of logs and setting the filename pattern using the
 * setPattern() method.
 *
 */
public class DateLogFilter{
	
	// The delimiter used to separate the parts of the filename pattern String
	// that contains the date of the log file.
	// An example using the vertical bar (|) as delimiter:
	// "apacheLog-|yyyy-MM-dd|.txt"
	private static final String patternDelimiter = "|";
	
	private static Logger log = Logger.getLogger(DateLogFilter.class.getName());
	
	private static Date startDate, endDate = null;
	private static DateFormat dateFormat = null;
	private static String preDateSubstring, postDateSubstring = null;
	
	public static void setStartDate(String dateStr)
			throws IllegalArgumentException{
		
		if(dateStr == null || dateStr.equals("")){
			startDate = null;
			return;
		}
		
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
		try{
			startDate = format.parse(dateStr);
			log.fine("Date Log Filter start date now set to: " + startDate);
		}catch(ParseException e){
			log.severe("An error occurred while parsing the start date: " + 
					e.getMessage());
			return;
		}
		
		if(endDate != null && startDate.after(endDate)){
			throw new IllegalArgumentException("The start and end dates " +
					"specified for the date log filter are reversed");
		}
		
	}
	
	public static void setEndDate(String dateStr)
			throws IllegalArgumentException{
		
		if(dateStr == null || dateStr.equals("")){
			endDate = null;
			return;
		}
		
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
		try{
			endDate = format.parse(dateStr);
			log.fine("Date Log Filter end date now set to: " + endDate);
		}catch(ParseException e){
			log.severe("An error occurred while parsing the end date: " + 
					e.getMessage());
			return;
		}
		
		if(startDate != null && endDate.before(startDate)){
			throw new IllegalArgumentException("The start and end dates " +
			"specified for the date log filter are reversed");
		}
		
	}
	
	public static void setPattern(String pattern)
			throws IllegalArgumentException{
		
		preDateSubstring = null;
		postDateSubstring = null;
		
		if(pattern == null || pattern.equals("")){
			dateFormat = null;
			return;
		}
		
		validatePattern(pattern);
		
		// Extract the DateFormat style from the pattern and determine the
		// portions of the log filenames that we discard
		preDateSubstring = pattern.substring(0,
				pattern.indexOf(patternDelimiter));
		if(!pattern.endsWith(patternDelimiter)){
			postDateSubstring = pattern.substring(
					pattern.lastIndexOf(patternDelimiter) + 1);
		}
		String dateFormatStyle = pattern.replace(
				preDateSubstring + patternDelimiter, "");
		if(postDateSubstring != null){
			dateFormatStyle = dateFormatStyle.replace(
					patternDelimiter + postDateSubstring, "");
		}else{
			dateFormatStyle = dateFormatStyle.replace(patternDelimiter, "");
		}
		dateFormat = new SimpleDateFormat(dateFormatStyle);
		log.fine("Date Log Filter pattern set to: " + preDateSubstring +
				"<date>" + postDateSubstring);
		log.fine("Date Log Filter date format style set to: " +
				dateFormatStyle);
		
	}
	
	public static boolean match(String filename) throws ParseException, 
			ReportManagerException{
		
		if(startDate == null && endDate == null){
			return true;
		}
		
		if(dateFormat == null){
			throw new ReportManagerException("Cannot use the DateLogFilter " +
					"until it has been properly initialized by setting the " +
					"log filename pattern");
		}
		
		// Extract the portion of the filename that specifies the log date and
		// use it to create a Date to compare with startDate and endDate
		String dateString = filename.replace(preDateSubstring, "");
		if(postDateSubstring != null){
			dateString = dateString.replace(postDateSubstring, "");
		}
		Date logDate = dateFormat.parse(dateString);
		
		// Check if the Date from the filename matches the start and end Dates
		if(startDate != null && logDate.before(startDate)){
			log.finer("File " + filename + " does not match date log filter " +
					"because it is from before the specified start date");
			return false;
		}
		if(endDate != null && logDate.after(endDate)){
			log.finer("File " + filename + " does not match date log filter " +
					"because it is from after the specified end date");
			return false;
		}
		
		return true;
		
	}
	
	public static void unsetAll(){
		
		startDate = null;
		endDate = null;
		dateFormat = null;
		preDateSubstring = null;
		postDateSubstring = null;
		
	}
	
	// Verify that pattern contains two instances of the delimiter and that the
	// first instance is not the first character.
	private static void validatePattern(String pattern)
			throws IllegalArgumentException{
		
		// TODO: Though nobody currently has log filenames that start the with
		// log date, we should handle it at some point by making
		// preDateSubstring optional, similar to postDateSubstring
		if(pattern.startsWith(patternDelimiter)){
			throw new IllegalArgumentException("The Date Log Filter " +
					"pattern cannot start with the delimiter");
		}
		
		int lastIndex = 0;
		int count = 0;
		while(lastIndex != -1){
		       lastIndex = pattern.indexOf(patternDelimiter, lastIndex);
		       if(lastIndex != -1){
		             count++;
		             lastIndex += patternDelimiter.length();
		      }
		}
		if(count != 2){
			throw new IllegalArgumentException("The Date Log Filter pattern " +
					"must contain exactly two instances of the pattern " +
					"delimiter '" + patternDelimiter + "', even one at the " +
					"end if the log date is at the end of the filename.");
		}
		
	}
	
}