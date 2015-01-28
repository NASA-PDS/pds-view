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
//	$Id: SawmillDB.java 11670 2013-06-20 17:14:33Z jpadams $
//

package gov.nasa.pds.report.sawmill;

/**
 * These classes allow the user to execute Sawmill operations, abstracting
 * the manner in which the commands for those operations are performed.  The
 * user can specify the precise mechanism that executes the commands by setting
 * gov.nasa.pds.report.sawmill.interface in the default.properties file.
 * 
 * @author resneck
 *
 */
public interface SawmillManager {

	/**
	 * Add a Sawmill command to the queue to update the database for a given
	 * Sawmill profile.
	 * 
	 * @param profileName		The name of the profile that will be updated
	 * @throws SawmillException	If a null or empty profile name is given
	 */
	public void updateDatabase(String profileName) throws SawmillException;
	
	
	/**
	 * Add a Sawmill command to the queue to rebuild the database for a given
	 * Sawmill profile.  This will delete all historical data and will most
	 * likely take a while.
	 * 
	 * @param profileName		The name of the profile that will be built
	 * @throws SawmillException	If a null or empty profile name is given
	 */
	public void buildDatabase(String profileName) throws SawmillException;
	
	/**
	 * Add a Sawmill command to the queue to generate a report specified using
	 * the given Sawmill profile and report at the given location.
	 * 
	 * @param profileName		The name of the profile that specifies the
	 * 							report
	 * @param reportName		The name of the report
	 * @param outputPath		The path to the directory where the report will
	 * 							be placed
	 * @throws SawmillException	If any of the parameters are null or empty
	 */
	public void generateReport(String profileName, String reportName, 
			String outputPath) throws SawmillException;
	
	/**
	 * Make the queued Sawmill commands execute in a manner determined by the
	 * SawmillInterface implementation being used.
	 * 
	 * @throws SawmillException	If an error occurs
	 */
	public void outputCommands() throws SawmillException;
	
}
