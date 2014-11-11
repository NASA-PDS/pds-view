package gov.nasa.pds.report;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.logs.LogsManager;
import gov.nasa.pds.report.logs.LogsManagerException;
import gov.nasa.pds.report.logs.PDSLogsManager;
import gov.nasa.pds.report.processing.ProcessingException;
import gov.nasa.pds.report.processing.RingsDecryptionProcessor;
import gov.nasa.pds.report.profile.ProfileManager;
import gov.nasa.pds.report.profile.SimpleProfileManager;
import gov.nasa.pds.report.util.Utility;

public class ReportServiceManager {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private LogsManager logsManager;
	private ProfileManager profileManager;
	
	private List<Properties> propsList = null;
	
	// If at some point we need multiple filters on the same key, then this
	// will change to a Map<String, List>
	private Map<String, String> profileFilters;
	
	public ReportServiceManager() {
		
		// TODO: When there is more than one implementation of these interfaces
		// these objects should be created by a factory, similar to OODT
		this.logsManager = new PDSLogsManager();
		this.profileManager = new SimpleProfileManager();
		this.profileFilters = new HashMap<String, String>();
		
	}
	
	public void addProfileFilter(String key, String pattern)
			throws ReportManagerException{
		
		// Validate input
		if(key == null || key.equals("")){
			throw new ReportManagerException("The provided profile filter " +
					"key is empty");
		}
		if(pattern == null || pattern.equals("")){
			throw new ReportManagerException("The provided profile filter " +
					"pattern with key " + key + " is empty");
		}
		
		log.fine("Adding filter: " + key + "=" + pattern);
		this.profileFilters.put(key, pattern);
		
	}
	
	public void readProfiles() throws IOException{
		
		// Use the ProfileManager to read in the profiles from disk
		String path = System.getProperty("gov.nasa.pds.report.profile.dir");
		List<Properties> props = this.profileManager.readProfiles(path);
		log.info("Found " + props.size() + " profiles");
		
		// Determine which profiles match the profile filters
		this.propsList = new Vector<Properties>();
		String nodeID = null;
		for(Properties p: props){
			try{
				nodeID = Utility.getNodePropsString(p,
						Constants.NODE_ID_KEY, false);
				if(nodeID == null || nodeID.equals("")){
					log.warning("Found a profile without an ID: " +
							p.toString());
				}else if(this.profileFilters.isEmpty()){
					this.propsList.add(p);
					log.fine("Profile recongized for " + nodeID);
				}else{
					
					//Iterate through the filters to check matching
					boolean match = true;
					for(Iterator<String> i = 
							this.profileFilters.keySet().iterator();
							i.hasNext() && match;){
						String key = i.next();
						String pattern = this.profileFilters.get(key);
						if(((String)p.get(key)).matches(pattern)){
							log.finer("Profile recongized for " + nodeID +
									" since it matches " + key + "=" + pattern);
						}else{
							log.finer("Profile not recongized for " + nodeID +
									" since it does not match " + key + "=" +
									pattern);
							match = false;
						}
					}
					
					if(match){
						this.propsList.add(p);
						log.fine("Profile " + nodeID + " matches all given " +
								"filters");
					}
					
				}
			}catch(ReportManagerException e){
				log.warning("An error occurred while reading the profile " + 
						nodeID + ": " + e.getMessage());
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
				String profileID = null;
				try{
					profileID = Utility.getNodePropsString(props,
							Constants.NODE_ID_KEY, false);
				}catch(ReportManagerException ex){
					// This should never happen, since the parameter
					// designating if the variable is needed is set to false 
				}
				log.severe("An error occurred while pulling log files using " +
						"profile " + profileID + ": " + e.getMessage());
			}
		}
	
	}
	
	public void processLogs(){
		
		for(Properties props: propsList){
			
			// TODO: Actually implement this
			
			String nodeName = null;
			String profileID = null;
			try{
				nodeName = Utility.getNodePropsString(props, 
						Constants.NODE_NODE_KEY, true);
				profileID = Utility.getNodePropsString(props,
						Constants.NODE_ID_KEY, true);
				if(nodeName.equals("rings")){
					File in = Utility.getStagingDir(nodeName, profileID, 
							LogsManager.OUTPUT_DIR_NAME);
					RingsDecryptionProcessor p = new RingsDecryptionProcessor();
					p.process(in);
				}
			}catch(Exception e){
				log.warning("Error: " + e.getMessage());
				continue;
			}
			
		}
		
	}
	
}
