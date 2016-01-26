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
 */
public abstract class DateFilter{
	
	private static Logger log = Logger.getLogger(DateFilter.class.getName());
	
	protected Date startDate = null;
	protected Date endDate = null;
	
	public void setStartDate(String dateStr){
		
		log.fine("Setting date filter start date to: " + dateStr);
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
		Date start = null;
		
		if(dateStr == null || dateStr.equals("")){
			return;
		}
		
		// Parse start date string
		if(dateStr != null){
			try{
				start = format.parse(dateStr);
				
			}catch(ParseException e){
				log.severe("An error occurred while parsing the start date: " + 
						e.getMessage());
				return;	//TODO: This should really be a fatal error
			}
		}
		
		// Check that start date is not after end date
		if(endDate != null && start.after(endDate)){
			throw new IllegalArgumentException("The start and end dates " +
					"specified for the date log filter are reversed");
		}
		
		this.startDate = start;
		
	}
	
	public void setEndDate(String dateStr){
		
		log.fine("Setting date filter end date to: " + dateStr);
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
		Date end = null;
		
		if(dateStr == null || dateStr.equals("")){
			return;
		}
		
		// Parse end date string
		if(dateStr != null){
			try{
				end = format.parse(dateStr);
				
			}catch(ParseException e){
				log.severe("An error occurred while parsing the end date: " + 
						e.getMessage());
				return;	//TODO: This should really be a fatal error
			}
		}
		
		// Check that end date is not before end date
		if(startDate != null && end.before(startDate)){
			throw new IllegalArgumentException("The start and end dates " +
			"specified for the date log filter are reversed");
		}
		
		this.endDate = end;
		
	}
	
	public String getStartDateString(String format){
		
		if(startDate == null){
			return null;
		}
		return new SimpleDateFormat(format).format(startDate);
		
	}
	
	public String getEndDateString(String format){
		
		if(endDate == null){
			return null;
		}
		return new SimpleDateFormat(format).format(endDate);
		
	}
	
	public abstract void setPattern(String pattern);
	
	public abstract boolean match(String filename) throws
			ReportManagerException, ParseException;
	
	public abstract void unsetAll();
	
	/**
	 * Check if the Date from the filename matches the start and end Dates
	 * 
	 * @param logDate	A {@link Date} object that represents the date of the
	 * 					log file in question.
	 * @param filename	The name of the log file.
	 * @return			True if the log file is in the accepted date range,
	 * 					otherwise false.
	 */
	protected boolean compareLogDate(Date logDate, String filename){
		
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
	
}
