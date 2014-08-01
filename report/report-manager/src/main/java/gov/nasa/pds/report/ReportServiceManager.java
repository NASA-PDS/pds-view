package gov.nasa.pds.report;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import gov.nasa.pds.report.logs.LogsManager;
import gov.nasa.pds.report.logs.LogsManagerException;
import gov.nasa.pds.report.logs.PDSLogsManager;
import gov.nasa.pds.report.profile.ProfileManager;
import gov.nasa.pds.report.profile.SimpleProfileManager;

public class ReportServiceManager {
	
	private LogsManager logsManager;
	private ProfileManager profileManager;
	
	private List<Properties> propsList = null;
	
	public ReportServiceManager() {
		
		// TODO: When there is more than one implementation of these interfaces
		// these objects should be created by a factor, similar to OODT
		this.logsManager = new PDSLogsManager();
		this.profileManager = new SimpleProfileManager();
		
	}
	
	public void setProfileDir(String path) throws ReportManagerException{
		
		if(path == null || path.equals("")){
			throw new ReportManagerException("Cannot provide an empty path " +
					"as the profile directory");
		}
		
		System.setProperty("gov.nasa.pds.report.profile.dir", path);
		
	}
	
	public void readProfiles() throws IOException{
		
		String path = System.getProperty("gov.nasa.pds.report.profile.dir");
		this.propsList = this.profileManager.readProfiles(path);
		
	}
	
	public void pullLogs(){
		
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
