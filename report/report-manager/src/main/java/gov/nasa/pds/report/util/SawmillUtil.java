//	Copyright 2015, by the California Institute of Technology.
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

import java.io.File;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.sawmill.SawmillException;

public class SawmillUtil{
	
	// The format in which Sawmill expects dates to be formatted
	private static final String SAWMILL_DATE_FORMAT = "dd/MMM/yyyy";
	
	// The path to the directory containing the Sawmill executable
	private static String sawmillHome;
	
	/**
	 * Get the value of the Sawmill home property (the directory where the
	 * Sawmill executable resides) and validate the property if we have not yet
	 * done so
	 * 
	 * @return					The location of the Sawmill executable as a
	 * 							String
	 * @throws SawmillException	If the location is invalid
	 */
	public static String getSawmillHome() throws SawmillException{
		
		if(sawmillHome != null){
			return sawmillHome;
		}
		
		// Validate the location of the Sawmill executable
		String sawmillHomeProp = System.getProperty(Constants.SAWMILL_HOME_PROP);
		if(sawmillHomeProp == null || sawmillHomeProp.equals("")){
			throw new SawmillException("The location of the Sawmill " +
					"executable must be specified");
		}
		File sawmillDir = new File(sawmillHomeProp);
		if(!sawmillDir.exists() || !sawmillDir.isDirectory()){
			throw new SawmillException("The given Sawmill home directory " +
					"does not exist");
		}
		File sawmillExe = new File(sawmillDir, "sawmill");
		if(!sawmillExe.exists()){
			throw new SawmillException("The Sawmill executable does not " +
					"exist is the given Sawmill home");
		}
		sawmillHome = sawmillHomeProp;
		return sawmillHome;
		
	}
	
	/**
	 * Accept the Sawmill operation flags and values and add the common command
	 * component (i.e. path to Sawmill directory, name of Sawmill executable,
	 * and date filter flag and value).
	 * 
	 * @param command			The flags and values of the Sawmill command
	 * @throws SawmillException	If the given command is empty
	 */
	public static String formatSawmillCommand(String command,
			boolean filterDates) throws SawmillException{
		
		if(command == null || command.equals("")){
			throw new SawmillException("The given Sawmill command is empty");
		}
		
		String cmd = getSawmillHome() + File.separator + "sawmill " + command;
		if(filterDates){
			String sawmillDateOption = getSawmillDateOption();
			if(sawmillDateOption != null){
				cmd = cmd + sawmillDateOption;
			}
		}
		
		return cmd;
		
	}
	
	private static String getSawmillDateOption(){
		
		String startDateString =
				DateLogFilter.getStartDateString(SAWMILL_DATE_FORMAT);
		String endDateString =
				DateLogFilter.getEndDateString(SAWMILL_DATE_FORMAT);
		String sawmillDateOption = null;
		if(startDateString != null || endDateString != null){
			sawmillDateOption = " -df \"start - end\"";
			if(startDateString != null){
				sawmillDateOption = sawmillDateOption.replace("start",
						startDateString);
			}
			if(endDateString != null){
				sawmillDateOption = sawmillDateOption.replace("end",
						endDateString);
			}
		}
		return sawmillDateOption;
		
	}
	
}