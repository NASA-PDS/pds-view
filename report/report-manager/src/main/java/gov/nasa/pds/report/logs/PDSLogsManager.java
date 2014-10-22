package gov.nasa.pds.report.logs;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.logs.pushpull.FtpPull;
import gov.nasa.pds.report.logs.pushpull.HttpPull;
import gov.nasa.pds.report.logs.pushpull.PDSPull;
import gov.nasa.pds.report.logs.pushpull.PDSPullImpl;
import gov.nasa.pds.report.logs.pushpull.PushPullException;
import gov.nasa.pds.report.util.DateLogFilter;
import gov.nasa.pds.report.util.Utility;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class PDSLogsManager implements LogsManager {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public PDSLogsManager(){}
	
	public void pullLogFiles(Properties nodeProps) throws LogsManagerException{
		
		if(nodeProps == null){
			throw new LogsManagerException("Null node information given.");
		}
		
		log.info("Now pulling logs using the profile " + 
				getNodeName(nodeProps));
		
		// The object that will pull logs from the node using the specified
		// protocol
		PDSPull logPuller;
		
		try{
			
			// Create the proper PDSPull object to grab logs from the given
			// node
			logPuller = this.getPdsPull(Utility.getNodePropsString(nodeProps,
					Constants.NODE_XFER_TYPE_KEY, true));
			
			// Create staging directory
			this.createStagingDir(nodeProps);
			
		}catch(LogsManagerException e){
			
			throw new LogsManagerException("An error occurred while " +
					"preparing to pull logs from node " + 
					getNodeName(nodeProps) + ": " + e.getMessage());
			
		}
		
		try{
			
			// Connect to the node machines
			this.connect(nodeProps, logPuller);
			
			// Set the filename pattern to that used by the node in order to
			// filter logs by date
			DateLogFilter.setPattern(Utility.getNodePropsString(nodeProps,
					Constants.NODE_FILENAME_PATTERN_KEY, false));
			
			// Download the node logs
			logPuller.pull(Utility.getNodePropsString(nodeProps,
					Constants.NODE_PATH_KEY, true),
					this.getStagingDirPath(nodeProps));
			
		}catch(LogsManagerException e){
			
			log.severe("An error occurred while connecting to node " +
					 getNodeName(nodeProps) + ": " + e.getMessage());
			throw new LogsManagerException("An error occurred while " +
					"connecting to node " + getNodeName(nodeProps) + ": " +
					e.getMessage());
			
		}catch(IllegalArgumentException e){
		
			log.severe("An error occurred while setting the filename " +
					"pattern for node " + getNodeName(nodeProps) + ": " + 
					e.getMessage());
			throw new LogsManagerException("An error occurred while setting " +
					"the filename pattern for node " + getNodeName(nodeProps) + 
					": " + e.getMessage());
			
		}catch(PushPullException e){
			
			log.severe("An error occurred while pulling" +
					" logs from node " + getNodeName(nodeProps) +
					": " + e.getMessage());
			throw new LogsManagerException("An error occurred while pulling" +
					" logs from node " + getNodeName(nodeProps) +
					": " + e.getMessage());
			
		}finally{
			
			try{
				
				logPuller.disconnect();
				
			}catch(PushPullException e){
				
				throw new LogsManagerException("An error occurred while " +
						"attempting to disconnect from the node " + 
						getNodeName(nodeProps) + ": " + e.getMessage());
				
			}
			
		}
		
		// TODO: Reformat the logs into something Sawmill can parse
		
	}
	
	// TODO: It would be cool to use code similar to OODT, where the config
	// contains the class name of the object to be created
	private PDSPull getPdsPull(String xferType) throws LogsManagerException{
		
		
		if(xferType.toLowerCase().equals("sftp")){
			return new PDSPullImpl();
		}else if(xferType.toLowerCase().equals("ftp")){
			return new FtpPull();
		}else if(xferType.toLowerCase().equals("http")){
			return new HttpPull();
		}else{
			throw new LogsManagerException("Log puller with transfer type " + 
					xferType + " not supported");
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
		
		name = Utility.getNodePropsString(nodeProps,
				Constants.NODE_ID_KEY, true);
		host = Utility.getNodePropsString(nodeProps,
				Constants.NODE_HOST_KEY, true);
		user = Utility.getNodePropsString(nodeProps,
				Constants.NODE_USER_KEY, false);
		password = Utility.getNodePropsString(nodeProps,
				Constants.NODE_PASSWORD_KEY, false);
		encrypt = Utility.getNodePropsBool(nodeProps,
				Constants.NODE_ENCRYPT_KEY);
		
		this.log.info("Connecting to " + user + "@" + host + " using " +
				logPuller.getClass().getName());
		
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
	
	private void createStagingDir(Properties nodeProps)
			throws LogsManagerException{
		
		
		File stagingDir = new File(this.getStagingDirPath(nodeProps));
		if(!stagingDir.exists()){
			if(!stagingDir.mkdirs()){
				throw new LogsManagerException("Failed to create the staging" +
						" directory " + stagingDir.getAbsolutePath() +
						" for node " + getNodeName(nodeProps));
			}
		}
		
	}
	
	private String getNodeName(Properties nodeProps){
		
		try{
			return Utility.getNodePropsString(nodeProps,
					Constants.NODE_ID_KEY, false);
		}catch(LogsManagerException e){
			return null;	// We don't absolutely need the node name (hence
							// the false parameter above) so it's OK to return
							// null
		}
		
	}
	
	private String getStagingDirPath(Properties nodeProps)
			throws LogsManagerException{
		
		return Utility.getNodePropsString(nodeProps,
				Constants.NODE_STAGING_DIR_KEY, true).trim();
		
	}
	
}
