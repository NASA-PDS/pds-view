package gov.nasa.pds.report;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.logs.LogsManager;
import gov.nasa.pds.report.logs.LogsManagerException;
import gov.nasa.pds.report.logs.PDSLogsManager;
import gov.nasa.pds.report.profile.ProfileManager;
import gov.nasa.pds.report.profile.SimpleProfileManager;
import gov.nasa.pds.report.util.Debugger;
import gov.nasa.pds.report.util.Utility;

public class ReportServiceManager {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private LogsManager logsManager;
	private ProfileManager profileManager;
	
	private List<Properties> propsList = null;
	private String nodePattern;
	
	public ReportServiceManager() {
		
		// TODO: When there is more than one implementation of these interfaces
		// these objects should be created by a factory, similar to OODT
		this.logsManager = new PDSLogsManager();
		this.profileManager = new SimpleProfileManager();
		this.nodePattern = null;
		
	}
	
	public void setProfileDir(String path) throws ReportManagerException{
		
		if(path == null || path.equals("")){
			throw new ReportManagerException("Cannot provide an empty path " +
					"as the profile directory");
		}
		
		System.setProperty("gov.nasa.pds.report.profile.dir", path);
		
	}
	
	public void setNodePattern(String pattern){
		
		if(pattern == null || pattern.equals("")){
			// TODO: Log a warning
			this.nodePattern = null;
		}
		
		this.nodePattern = pattern;
		Debugger.debug("Only nodes matching the pattern " + this.nodePattern + " will be processed");
		//this.log.fine("Only nodes matching the pattern " + this.nodePattern + " will be processed");
		
	}
	
	public void readProfiles() throws IOException{
		
		String path = System.getProperty("gov.nasa.pds.report.profile.dir");
		List<Properties> props = this.profileManager.readProfiles(path);
		this.propsList = new Vector<Properties>();
		for(Properties p: props){
			try{
				String nodeName = Utility.getNodePropsString(p,
						Constants.NODE_NAME_KEY, true);
				if(this.nodePattern == null){
					this.propsList.add(p);
					Debugger.debug("Profile recongized for " + nodeName);
					//this.log.fine("Profile recongized for " + nodeName);
				}else if(nodeName.matches(this.nodePattern)){
					this.propsList.add(p);
					Debugger.debug("Profile recongized for " + nodeName + " since it matched the node pattern " + this.nodePattern);
					//this.log.fine("Profile recongized for " + nodeName + " since it matched the node pattern " + this.nodePattern);
				}
			}catch(LogsManagerException e){
				// TODO: Log a warning
			}
		}
		
	}
	
	public void pullLogs(){
		
		if(this.propsList == null || this.propsList.isEmpty()){
			log.warning("The list of nodes to pull logs from is empty.  Make" +
					" sure that the node name filter is not too restrictive.");
			return;
		}
		
		for(Properties props: propsList){
			try{
				logsManager.pullLogFiles(props);
			}catch(LogsManagerException e){
				// TODO: Log an error
				System.out.println("An error occurred while pulling log files: " + e.getMessage());
				e.printStackTrace();
			}
		}
	
	}
	
}
