package gov.nasa.pds.report.logs;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.logs.pushpull.FtpPull;
import gov.nasa.pds.report.logs.pushpull.HttpPull;
import gov.nasa.pds.report.logs.pushpull.PDSPull;
import gov.nasa.pds.report.logs.pushpull.PDSPullImpl;
import gov.nasa.pds.report.logs.pushpull.PushPullException;
import gov.nasa.pds.report.util.DateLogFilter;
import gov.nasa.pds.report.util.FileUtil;
import gov.nasa.pds.report.util.Utility;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class PDSLogsManager implements LogsManager {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public void pullLogFiles(Properties nodeProps) throws LogsManagerException{
		
		if(nodeProps == null){
			throw new LogsManagerException("Null node information given.");
		}
		
		// The profile ID, since we use it a few times
		String profileID = null;
		
		// The object used to pull logs from the node using the specified
		// protocol
		PDSPull logPuller = null;
		
		try{
			
			profileID = Utility.getNodePropsString(nodeProps,
					Constants.NODE_ID_KEY, false);
			
			log.info("Now pulling logs using the profile " + profileID);
			
			// Create the proper PDSPull object that will pull logs from the
			// node using the specified protocol
			String xferType = Utility.getNodePropsString(nodeProps,
					Constants.NODE_XFER_TYPE_KEY, false);
			if(xferType == null || xferType.trim().equals("")){
				log.info("No logs will be pulled using profile " + profileID +
						" since no transfer type is specified");
				return;
			}
			logPuller = this.getPdsPull(xferType);
			
			// Get staging directory
			String nodeName = Utility.getNodePropsString(nodeProps,
					Constants.NODE_NODE_KEY, true);
			File stagingDir = FileUtil.getDir(Constants.STAGING_DIR, nodeName,
					profileID);
			
			// Connect to the node machines
			this.connect(nodeProps, logPuller);
			
			// Set the filename pattern to that used by the node in order to
			// filter logs by date
			DateLogFilter.setPattern(Utility.getNodePropsString(nodeProps,
					Constants.NODE_FILENAME_PATTERN_KEY, false));
			
			// Download the node logs
			String remotePath = Utility.getNodePropsString(nodeProps,
					Constants.NODE_PATH_KEY, true);
			logPuller.pull(remotePath, stagingDir.getAbsolutePath());
			
		}catch(ReportManagerException e){
			
			throw new LogsManagerException("An error occurred while " +
					"preparing to pull logs from node using profile " + 
					profileID + ": " + e.getMessage());
			
		}catch(IllegalArgumentException e){
		
			throw new LogsManagerException("An error occurred while " +
					"setting the filename pattern for logs pulled using " +
					"profile " + profileID + ": " + e.getMessage());
			
		}catch(PushPullException e){
			
			throw new LogsManagerException("An error occurred while " +
					"pulling logs using profile " + profileID + ": " + 
					e.getMessage());
			
		}finally{
			
			try{
				
				if(logPuller != null){
					logPuller.disconnect();
				}
				
			}catch(PushPullException e){
				
				throw new LogsManagerException("An error occurred while " +
						"attempting to disconnect from the node using profile" + 
						profileID + ": " + e.getMessage());
				
			}
			
		}
		
	}
	
	// TODO: This functionality should really be handled by the
	// GenericReportServiceObjectFactory
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
		
		String name = null, host = null, user = null, password = null;
		boolean encrypt = false;
		
		try{
			
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
			
			log.info("Connecting to " + user + "@" + host + " using " +
					logPuller.getClass().getName());
		
			if(!logPuller.connect(host, user, password, encrypt)){
				throw new LogsManagerException("Failed to connect to node " + 
						name);
			}
		
		}catch(ReportManagerException e){
			throw new LogsManagerException("Some required details are " +
					"missing from the " + name + " profile");
		}catch(PushPullException e){
			throw new LogsManagerException(
					"An error occurred while connecting to node " + name + 
					": " + e.getMessage());
		}
		
	}
	
}
