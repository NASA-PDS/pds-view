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
import gov.nasa.pds.report.constants.Constants;

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
abstract public class DateLogFilter{
	
	private static DateFilter filter = null;
	
	private static Logger log = Logger.getLogger(DateLogFilter.class.getName());
	
	public static void setStartDate(String dateStr)
			throws IllegalArgumentException, ReportManagerException{
		getFilter().setStartDate(dateStr);
	}
	
	public static void setEndDate(String dateStr)
			throws IllegalArgumentException, ReportManagerException{
		getFilter().setEndDate(dateStr);
	}
	
	public static String getStartDateString(String format){
		
		try{
			return getFilter().getStartDateString(format);
		}catch(ReportManagerException e){
			log.fine("An error occurred while obtaining the date filter " +
					"start date: " + e.getMessage());
			return null;
		}
		
	}
	
	public static String getEndDateString(String format){
		
		try{
			return getFilter().getEndDateString(format);
		}catch(ReportManagerException e){
			log.fine("An error occurred while obtaining the date filter " +
					"end date: " + e.getMessage());
			return null;
		}
	
	}
	
	public static void setPattern(String pattern)
			throws IllegalArgumentException, ReportManagerException{
		getFilter().setPattern(pattern);
	}
	
	public static boolean match(String filename) throws ParseException, 
			ReportManagerException{
		return getFilter().match(filename);
	}
	
	public static void unsetAll() throws ReportManagerException{
		getFilter().unsetAll();
	}
	
	public static void forceUnsetFilter(){
		filter = null;
	}
	
	private static DateFilter getFilter() throws ReportManagerException{
		
		if(filter != null){
			return filter;
		}
		
		DateFilter f = GenericReportServiceObjectFactory.getDateFilter(
				System.getProperty(Constants.DATE_FILTER_PROP));
		if(f == null){
			throw new ReportManagerException("The date log filter could not " +
					"be instantiated");
		}
		filter = f;
		return f;
		
	}
	
}