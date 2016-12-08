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

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import gov.nasa.pds.report.util.CommandLineWorker;

/**
 * Class that interacts with Sawmill CLI and rebuilds/updates
 * Sawmill databases.
 * 
 * @author jpadams
 * @author resneck
 *
 */
public class SawmillCommandLineRunner implements SawmillInterface {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void runCommands(List<String> commandList) throws SawmillException {
		
		if(commandList.isEmpty()){
			throw new SawmillException("The given list of commands is empty");
		}
		
		for(Iterator<String> i = commandList.iterator(); i.hasNext();){
			String cmd = i.next();
			if(cmd == null || cmd.equals("")){
				log.warning("An empty command was detected");
				continue;
			}
			CommandLineWorker worker = new CommandLineWorker(cmd);
			worker.setDebugMode(CommandLineWorker.DEBUG_ERRORS);
			int exitValue = worker.execute();
			if(exitValue == -1){
				log.warning("The Sawmill command '" + cmd + "' timed out");
			}else if(exitValue != 0){
				log.warning("The Sawmill command '" + cmd + "' failed with exit " +
						"code " + exitValue);
			}
		}
		
	}
	
}
