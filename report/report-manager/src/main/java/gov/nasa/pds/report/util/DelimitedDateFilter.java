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

import gov.nasa.pds.report.ReportManagerException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * TODO: Write a proper javadoc!
 * 
 * @author resneck
 *
 */
public class DelimitedDateFilter extends DateFilter{
	
	private static Logger log = Logger.getLogger(DelimitedDateFilter.class.getName());
	
	// The delimiter used to separate the parts of the filename pattern String
	// that contains the date of the log file.
	// An example using the vertical bar (|) as delimiter:
	// "apacheLog-|yyyy-MM-dd|.txt"
	private static final String patternDelimiter = "|";
	
	// The format of the log date as specified in the filename pattern
	private static DateFormat dateFormat = null;
	
	// The portions of the filename before and after the date
	private static String preDateSubstring = null;
	private static String postDateSubstring = null;

	@Override
	public void setPattern(String pattern) throws IllegalArgumentException{
		
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

	@Override
	public boolean match(String filename) throws
			ReportManagerException, ParseException{
		
		if(startDate == null && endDate == null){
			return true;
		}
		
		if(dateFormat == null){
			throw new ReportManagerException("Cannot use the DateLogFilter " +
					"until it has been properly initialized by setting the " +
					"log filename pattern");
		}
		
		// Check that the file name matches the pattern(s) specified
		if(!filename.startsWith(preDateSubstring)){
			throw new ParseException("The filename " + filename +
					" does not match the pattern provided for date filtering",
					0);
		}
		if(postDateSubstring != null && !filename.endsWith(postDateSubstring)){
			throw new ParseException("The filename " + filename +
					"does not match the pattern provided for date filtering",
					filename.length() - postDateSubstring.length());
		}
		
		// Extract the portion of the filename that specifies the log date and
		// use it to create a Date to compare with startDate and endDate
		String dateString = filename.replace(preDateSubstring, "");
		if(postDateSubstring != null){
			dateString = dateString.replace(postDateSubstring, "");
		}
		Date logDate = dateFormat.parse(dateString);
		
		return compareLogDate(logDate, filename);
		
	}

	@Override
	public void unsetAll() {
		
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