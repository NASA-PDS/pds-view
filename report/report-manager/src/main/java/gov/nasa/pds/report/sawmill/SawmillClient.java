//	Copyright 2014, by the California Institute of Technology.
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
//	$Id: SawmillDB.java 11670 2013-06-20 17:14:33Z jpadams $
//

package gov.nasa.pds.report.sawmill;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.CommandLineWorker;
import gov.nasa.pds.report.util.DateLogFilter;

import java.io.File;
import java.util.logging.Logger;

/**
 * Class that interacts with Sawmill CLI and rebuilds/updates
 * Sawmill databases.
 * 
 * @author jpadams
 * @author resneck
 *
 */
public class SawmillClient {
	
	private static final String SAWMILL_DATE_FORMAT = "dd/MMM/yyyy";
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private static String sawmillHome;
	
	/**
	 * Runs a Sawmill command to update the database for a given Sawmill
	 * profile.
	 * 
	 * @param profileName		The name of the profile that will be updated
	 * @throws SawmillException	If a null or empty profile name is given
	 */
	public void updateDatabase(String profileName) throws SawmillException{
		
		if(profileName == null || profileName.equals("")){
			throw new SawmillException("Cannot update a Sawmill profile " +
					"with no name");
		}
		
		log.info("Updating the " + profileName + " Sawmill profile");
		String cmd = "-p " + profileName + " -a ud";
		this.runSawmillCommand(cmd);
		
	}
	
	/**
	 * Runs a Sawmill command to rebuild the database for a given Sawmill
	 * profile.  This will delete all historical data.
	 * 
	 * @param profileName		The name of the profile that will be built
	 * @throws SawmillException	If a null or empty profile name is given
	 */
	public void buildDatabase(String profileName) throws SawmillException{
		
		if(profileName == null || profileName.equals("")){
			throw new SawmillException("Cannot build a Sawmill profile " +
					"with no name");
		}
		
		log.info("Building the " + profileName + " Sawmill profile");
		String cmd = "-p " + profileName + " -a bd";
		this.runSawmillCommand(cmd);
		
	}
	
	/**
	 * Run a Sawmill command to generate a report specified using the given
	 * Sawmill profile and report at the given location.
	 * 
	 * @param profileName		The name of the profile that specifies the
	 * 							report
	 * @param reportName		The name of the report
	 * @param outputPath		The path to the directory where the report will
	 * 							be placed
	 * @throws SawmillException	If any of the parameters are null or empty
	 */
	public void generateReport(String profileName, String reportName, 
			String outputPath) throws SawmillException{
		
		if(profileName == null || profileName.equals("")){
			throw new SawmillException("Cannot build a Sawmill profile " +
					"with no name");
		}else if(reportName == null || reportName.equals("")){
			throw new SawmillException("Cannot build a Sawmill report " +
					"with no name");
		}else if(outputPath == null || outputPath.equals("")){
			throw new SawmillException("Cannot build a Sawmill report " +
			"with no output path");
		}
		
		String cmd = "-p " + profileName + " -a ect -rn " + reportName +
				" -et true -er -1 -od " + outputPath;
		this.runSawmillCommand(cmd);
		
	}
	
	private void runSawmillCommand(String command) throws SawmillException{
		
		if(command == null || command.equals("")){
			throw new SawmillException("The given Sawmill command is empty");
		}
		
		String cmd = getSawmillHome() + File.separator + "sawmill " + command;
		String sawmillDateOption = getSawmillDateOption();
		if(sawmillDateOption != null){
			cmd = cmd + sawmillDateOption;
		}
		
		CommandLineWorker worker = new CommandLineWorker(cmd);
		int exitValue = worker.execute();
		if(exitValue == -1){
			log.warning("The Sawmill command '" + cmd + "' timed out");
		}else if(exitValue != 0){
			log.warning("The Sawmill command '" + cmd + "' failed with exit " +
					"code " + exitValue);
		}
		
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
	
	/**
	 * Get the value of the Sawmill home property (the directory where the
	 * Sawmill executable resides) and validate the property if we have not yet
	 * done so
	 * 
	 * @return					The location of the Sawmill executable as a
	 * 							String
	 * @throws SawmillException	If the location is invalid
	 */
	private static String getSawmillHome() throws SawmillException{
		
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
	
}