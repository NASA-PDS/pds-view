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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class searches through filenames using a given set of regular
 * expressions to determine the date of the file.
 * 
 * The following date formats as found in filenames are supported:
 * 
 * *MMddyyyy*
 * *yyyyMMdd*
 * *yyyy-MM-dd*
 * *MM-dd-yyyy*
 * *yyMMdd*
 * 
 * Note that this class may cease to function reliably in 2080 due to the
 * implementation of the SimpleDateFormat class.  If you are the poor sod who
 * has to fix it, I'm sorry.
 * 
 * @author resneck
 */
public class DynamicDateFilter extends DateFilter{

	@Override
	/**
	 * Since this class determines the date of a file using only the filename,
	 * it doesn't need a filename pattern.
	 */
	public void setPattern(String pattern) {
		return;
	}

	@Override
	public boolean match(String filename) throws ReportManagerException,
			ParseException {
		
		if(startDate == null && endDate == null){
			return true;
		}
		
		Date logDate = null;
		
		if(filename.matches("\\D*\\d{8}\\D*")){
			if(filename.matches("\\D*\\d{4}20\\d{2}\\D*")){
				// We assume that the month is before the day, but it could 
				// just as easily be visa versa.
				logDate = this.getLogDate(filename, "\\D*(\\d{8})\\D*", "MMddyyyy");
			}else{
				logDate = this.getLogDate(filename, "\\D*(\\d{8})\\D*", "yyyyMMdd");
			}
		}else if(filename.matches("\\D*\\d{4}-\\d{2}-\\d{2}\\D*")){
			logDate = this.getLogDate(filename, "\\D*(\\d{4}-\\d{2}-\\d{2})\\D*",
					"yyyy-MM-dd");
		}else if(filename.matches("\\D*\\d{2}-\\d{2}-\\d{4}\\D*")){
			// We assume that the month is before the day, but it could just as
			// easily be visa versa.
			logDate = this.getLogDate(filename, "\\D*(\\d{2}-\\d{2}-\\d{4})\\D*",
					"MM-dd-yyyy");
		}else if(filename.matches("\\D*\\d{6}\\D*")){
			// We assume that the date format is yyMMdd, but it could just as
			// easily be otherwise.
			logDate = this.getLogDate(filename, "\\D*(\\d{6})\\D*", "yyMMdd");
		}else{
			throw new ParseException(
					"The date of log file " + filename + " is not in a " +
					"recognized format", 0);
		}
		
		return compareLogDate(logDate, filename);
		
	}

	@Override
	public void unsetAll() {
		startDate = null;
		endDate = null;
	}
	
	private Date getLogDate(String filename, String filenamePattern,
			String dateFormat) throws ParseException, ReportManagerException{
		
		Matcher m = Pattern.compile(filenamePattern).matcher(filename);
		if(!m.matches()){
			throw new ReportManagerException("The filename " + filename +
					" date could not be interepretted with the provided RE " +
					"pattern: " + filenamePattern);
		}
		String dateStr = m.group(1);
		return new SimpleDateFormat(dateFormat).parse(dateStr);
	
	}
	
}