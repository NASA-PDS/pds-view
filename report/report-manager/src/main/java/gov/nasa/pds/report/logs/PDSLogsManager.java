package gov.nasa.pds.report.logs;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.logs.pushpull.PDSPull;
import gov.nasa.pds.report.logs.pushpull.PDSPullImpl;
import gov.nasa.pds.report.logs.pushpull.PushPullException;
import gov.nasa.pds.report.util.Utility;

import java.io.File;
import java.util.Properties;

public class PDSLogsManager implements LogsManager {
	
	public PDSLogsManager(){}
	
	public void pullLogFiles(Properties nodeProps) throws LogsManagerException{
		
		if(nodeProps == null){
			throw new LogsManagerException("Null node information given.");
		}
		
		// Create the proper PDSPull object to grab logs from the given node
		PDSPull logPuller = this.getPdsPull(Utility.getNodePropsString(
				nodeProps, Constants.NODE_XFER_TYPE_KEY, true));
		
		// Create staging directory
		String stagingDirStr = Utility.getNodePropsString(
				nodeProps, Constants.NODE_STAGING_DIR_KEY, true).trim();
		File stagingDir = new File(stagingDirStr);
		if(!stagingDir.exists()){
			stagingDir.mkdirs();
		}
		
		// Connect to the node machines
		this.connect(nodeProps, logPuller);
		
		// Download the node logs
		try{
			logPuller.pull(Utility.getNodePropsString(
					nodeProps, Constants.NODE_PATH_KEY, true), stagingDirStr);
		}catch(PushPullException e){
			String nodeName = Utility.getNodePropsString(nodeProps, Constants.NODE_NAME_KEY, false);
			throw new LogsManagerException("An error occurred while pulling " +
					"logs from node " + nodeName +
					": " + e.getMessage());
		}
		
		// TODO: Reformat the logs into something Sawmill can parse
		
	}
	
	// TODO: It would be cool to use code similar to OODT, where the config
	// contains the class name of the object to be created
	private PDSPull getPdsPull(String xferType) throws LogsManagerException{
		
		
		if(xferType.toLowerCase().equals("sftp")){
			return new PDSPullImpl();
		}else{
			throw new LogsManagerException("Log puller with trasnfer type " + 
					xferType + "not supported");
		}
		
	}
	
	private void connect(Properties nodeProps, PDSPull logPuller)
			throws LogsManagerException{
		
		if(logPuller == null){
			throw new LogsManagerException(
					"Cannot connect to a null object for pulling logs.");
		}
		
		String name, host, user, password;
		boolean encrypt = false;
		
		name = Utility.getNodePropsString(nodeProps, Constants.NODE_NAME_KEY, true);
		host = Utility.getNodePropsString(nodeProps, Constants.NODE_HOST_KEY, true);
		user = Utility.getNodePropsString(nodeProps, Constants.NODE_USER_KEY, true);
		password = Utility.getNodePropsString(nodeProps,
				Constants.NODE_PASSWORD_KEY, false);
		encrypt = Utility.getNodePropsBool(nodeProps, Constants.NODE_ENCRYPT_KEY);
		
		try{
			if(!logPuller.connect(host, user, password, encrypt)){
				throw new LogsManagerException("Failed to connect to node " + name);
			}
		}catch(PushPullException e){
			throw new LogsManagerException(
					"An error occurred while connecting to node " + name + 
					": " + e.getMessage());
		}
		
	}
	
}
