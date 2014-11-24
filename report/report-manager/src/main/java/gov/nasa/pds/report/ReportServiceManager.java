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
import gov.nasa.pds.report.processing.Processor;
import gov.nasa.pds.report.processing.RingsDecryptionProcessor;
import gov.nasa.pds.report.profile.ProfileManager;
import gov.nasa.pds.report.profile.SimpleProfileManager;
import gov.nasa.pds.report.util.GenericReportServiceObjectFactory;
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
		String path = System.getProperty(Constants.PROFILE_HOME_PROP);
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
	
	// TODO: At some point, we should create a class to handle this, similar
	// to using a LogsManager to pull logs 
	public void processLogs(){
		
		log.info("Processing logs");
		
		for(Properties props: propsList){
			
			String processesStr = null;
			String nodeName = null;
			String profileID = null;
				
			// Get profile details needed for processing
			try{
				processesStr = Utility.getNodePropsString(props, 
						Constants.NODE_PROCESSES_KEY, false);
				nodeName = Utility.getNodePropsString(props, 
						Constants.NODE_NODE_KEY, true);
				profileID = Utility.getNodePropsString(props,
						Constants.NODE_ID_KEY, true);
				log.info("Processing logs from profile " + profileID);
			}catch(ReportManagerException e){
				log.warning("An error occurred while collecting profile " +
						"details to process profile " + profileID + ": " + 
						e.getMessage());
				continue;
			}
			
			// Skip over profiles that don't require any processing
			if(processesStr == null || processesStr.equals("")){
				continue;
			}
				
			// Create a list of Processors to run
			String[] processes = processesStr.split(",");
			List<Processor> processors = new Vector<Processor>();
			for(int i = 0; i < processes.length; i++){
				Processor p =
						GenericReportServiceObjectFactory.getProcessor(
						processes[i].trim());
				processors.add(p);
				if(p == null){
					log.warning("An error occurred while creating " +
							"Processors using profile " + profileID);
					i = processes.length;
					processors = null;
				}
			}
			if(processors == null){
				continue;
			}
			log.finer("Found " + processors.size() + " processes to run on " +
					"logs from profile " + profileID);
			
			// The directory where Processor input comes from.  This will be
			// set after each Processor is run to be used by the next one.
			File in = null;
				
			try{
			
				for(Iterator<Processor> i = processors.iterator(); i.hasNext();){
					
					// If the input directory location has been set, use the 
					// directory where the logs were placed when downloaded
					if(in == null){
						in = Utility.getStagingDir(nodeName, profileID, 
								LogsManager.OUTPUT_DIR_NAME);
					}
					
					// Configure and run the Processor
					Processor p = i.next();
					p.configure(props);
					p.process(in);
					
					// Determine where output was placed so that it can be used by
					// the next Processor
					in = Utility.getStagingDir(in, p.getDirName());
					
				}
				
			}catch(ReportManagerException e){
				log.warning("An error occurred while handling staging " +
						"directories while processing " + profileID + ": " +
						e.getMessage());
				continue;
			}catch(ProcessingException e){
				log.warning("An error occurred while processing " + profileID +
						": " + e.getMessage());
				continue;
			}
			
		}
		
	}
	
}
