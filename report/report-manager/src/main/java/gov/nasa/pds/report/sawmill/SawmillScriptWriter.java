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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import gov.nasa.pds.report.constants.Constants;

/**
 * Class that creates a shell script at location specified by
 * gov.nasa.pds.report.sawmill.SawmillScriptWriter.output.location in
 * default.properties.  The shell script is then run automatically after the
 * Report Service finishes execution.
 * 
 * The runCommands() method should only be run once since the script created
 * will be written each time the method is invoked.
 * 
 * @author resneck
 *
 */
public class SawmillScriptWriter implements SawmillInterface{

	public static final String SCRIPT_NAME = "report-mgr-sawmill";
	
	// The path to the directory where the script is written
	private static String scriptDirPath;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	// A flag to indicate whether this interface has previously been used to
	// output commands
	private boolean runPreviously = false;

	@Override
	public void runCommands(List<String> commandList) throws SawmillException {
		
		if(commandList == null){
			throw new SawmillException("A null list of commands was given to " +
					"the Sawmill script writer");
		}
		
		if(commandList.isEmpty()){
			log.warning("An empty list of commands was given to the Sawmill " +
					"script writer");
			return;
		}
		
		if(runPreviously){
			throw new SawmillException("Running commands with the Sawmill " +
					"script writer a second time will overwrite the " +
					"existing script");
		}
		
		File scriptFile = new File(getOutputPath(), SCRIPT_NAME);
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(scriptFile);
		}catch(FileNotFoundException e){
			throw new SawmillException("The Sawmill script writer could not " +
					"obtain a handle on the script file");
		}
		
		writer.println("#!/bin/sh");
		for(String cmd: commandList){
			writer.println(cmd);
		}
		writer.close();
		
		this.runPreviously = true;
		
	}
	
	private String getOutputPath() throws SawmillException{
		
		if(scriptDirPath != null){
			return scriptDirPath;
		}
		
		// Validate the script path
		String scriptPathProp = System.getProperty(Constants.SAWMILL_SCRIPT_PROP);
		if(scriptPathProp == null || scriptPathProp.equals("")){
			throw new SawmillException("The Sawmill script output path must " +
					"be specified");
		}
		File scriptDir = new File(scriptPathProp);
		if(scriptDir.exists()){
			if(!scriptDir.isDirectory()){
				throw new SawmillException("The Sawmill script directory " +
						"path points to a file");
			}
		}else{
			
			// Create the directory if it does not exist already
			log.fine("Creating directory " + scriptDir.getAbsolutePath());
			if(!scriptDir.mkdirs()){
				throw new SawmillException("An error occured while creating " +
						"the directory where the Sawmill script will be written");
			}
			
		}
		scriptDirPath = scriptPathProp;
		return scriptDirPath;
		
	}
	
}