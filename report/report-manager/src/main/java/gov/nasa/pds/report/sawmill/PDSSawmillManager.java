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

import java.util.LinkedList;
import java.util.logging.Logger;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.GenericReportServiceObjectFactory;
import gov.nasa.pds.report.util.SawmillUtil;

/**
 * This class creates a queue of Sawmill operations and executes them by
 * invoking the outputCommands() method.  This will make the queued commands
 * execute in a manner specific to the implementation of SawmillInterface being
 * used.
 * 
 * @author resneck
 * 
 * TODO: Write a unit test for this class using a dummy SawmillInterface
 *
 */
public class PDSSawmillManager implements SawmillManager {

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	// The class used to actually execute Sawmill commands
	private SawmillInterface sawmillInterface;
	
	// The queue that will hold Sawmill commands
	private LinkedList<String> commandQueue = new LinkedList<String>();
	
	public PDSSawmillManager(){
		
		this.sawmillInterface = GenericReportServiceObjectFactory.
				getSawmillInterface(System.getProperty(
				Constants.SAWMILL_INTERFACE_PROP));
		
	}
	
	@Override
	public void updateDatabase(String profileName) throws SawmillException {
		
		if(profileName == null || profileName.equals("")){
			throw new SawmillException("Cannot update a Sawmill profile " +
					"with no name");
		}
		
		log.info("Updating the " + profileName + " Sawmill profile");
		String cmd = "-p " + profileName + " -a ud";
		cmd = SawmillUtil.formatSawmillCommand(cmd, false);
		this.addCommand(cmd);

	}

	@Override
	public void buildDatabase(String profileName) throws SawmillException {
		
		if(profileName == null || profileName.equals("")){
			throw new SawmillException("Cannot build a Sawmill profile " +
					"with no name");
		}
		
		log.info("Building the " + profileName + " Sawmill profile");
		String cmd = "-p " + profileName + " -a bd";
		cmd = SawmillUtil.formatSawmillCommand(cmd, false);
		this.addCommand(cmd);

	}

	@Override
	public void generateReport(String profileName, String reportName,
			String outputPath) throws SawmillException {
		
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
		cmd = SawmillUtil.formatSawmillCommand(cmd, true);
		this.addCommand(cmd);

	}

	@Override
	public void outputCommands() {
		
		if(this.commandQueue.isEmpty()){
			log.info("There are no commands in the queue to output");
			return;
		}
		
		try{
			this.sawmillInterface.runCommands(this.commandQueue);
		}catch(SawmillException e){
			log.warning("An error occured while outputing Sawmill commands: " +
					e.getMessage());
		}finally{
			this.commandQueue.clear();
		}

	}
	
	private boolean addCommand(String cmd){
		
		if(this.commandQueue.contains(cmd)){
			return false;
		}
		this.commandQueue.add(cmd);
		return true;
		
	}

}
