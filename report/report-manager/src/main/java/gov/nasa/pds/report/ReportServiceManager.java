package gov.nasa.pds.report;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.logs.LogsManager;
import gov.nasa.pds.report.logs.LogsManagerException;
import gov.nasa.pds.report.logs.PDSLogsManager;
import gov.nasa.pds.report.processing.ProcessingException;
import gov.nasa.pds.report.processing.ProcessingManager;
import gov.nasa.pds.report.processing.Processor;
import gov.nasa.pds.report.profile.ProfileManager;
import gov.nasa.pds.report.profile.SimpleProfileManager;
import gov.nasa.pds.report.sawmill.PDSSawmillManager;
import gov.nasa.pds.report.sawmill.SawmillException;
import gov.nasa.pds.report.sawmill.SawmillManager;
import gov.nasa.pds.report.util.FileUtil;
import gov.nasa.pds.report.util.GenericReportServiceObjectFactory;
import gov.nasa.pds.report.util.Utility;

public class ReportServiceManager {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private LogsManager logsManager;
	private ProfileManager profileManager;
	private SawmillManager sawmillManager;
	
	private List<Properties> propsList = null;
	
	// If at some point we need multiple filters on the same key, then this
	// will change to a Map<String, List>
	private Map<String, String> profileFilters;
	
	public ReportServiceManager() {
		
		// TODO: When there is more than one implementation of these interfaces
		// these objects should be created by the
		// GenericReportServiceObjectFactory
		this.logsManager = new PDSLogsManager();
		this.profileManager = new SimpleProfileManager();
		this.profileFilters = new HashMap<String, String>();
		this.sawmillManager = new PDSSawmillManager();
		
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
	
	public void createStagingDirTree() throws ReportManagerException{
		
		log.info("Creating staging directory tree");
		FileUtil.createDirTree(this.propsList, Constants.STAGING_DIR);
		
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

	public void processLogs(){
		
		log.info("Processing logs");
		
		ProcessingManager pm = new ProcessingManager();
		
		for(Properties props: propsList){
			
			try{
				
				pm.processLogs(props);
				
			}catch(ProcessingException e){
				log.warning("An error occurred during processing: " + e.getMessage());
			}
			
		}
		
		// Remove the processing directory--nobody must find the evidence!
		pm.cleanupProcessingDir();
		
	}
	
	/**
	 * Update or rebuild the Sawmill database for each profile
	 * 
	 * @param rebuild	Whether to rebuild the Sawmill database for each
	 * 					profile
	 */
	public void buildSawmillDB(boolean rebuild){
		
		if(rebuild){
			log.info("Rebuilding Sawmill databases");
		}else{
			log.info("Updating Sawmill databases");
		}
		
		
		for(Properties props: propsList){
			
			String sawmillProfile = null;
			try {
				sawmillProfile = Utility.getNodePropsString(props,
						Constants.NODE_SAWMILL_PROFILE, true);
				if(rebuild){
					this.sawmillManager.buildDatabase(sawmillProfile);
				}else{
					this.sawmillManager.updateDatabase(sawmillProfile);
				}
			} catch (ReportManagerException e) {
				try{
					String profileName = Utility.getNodePropsString(props,
							Constants.NODE_ID_KEY, false);
					log.warning("Unable to obtain Sawmill profile name from " +
							"profile " + profileName);
				}catch(ReportManagerException ignore){
					// This should not happen since we specified that the
					// property is not needed with the false parameter above.
				}
			} catch (SawmillException e) {
				log.warning("An error occurred while building the Sawmill " +
						"database for Sawmill profile " + sawmillProfile +
						": " + e.getMessage());
			}
			
		}
		
	}
	
	/**
	 * Generate Sawmill reports.
	 */
	public void generateReports(String runName){
		
		log.info("Generating Sawmill reports");
		
		String sawmillReportHome = System.getProperty(
				Constants.SAWMILL_REPORT_PROP);
		if(sawmillReportHome == null){
			log.severe("Sawmill report directory root not defined in " +
					"default properties");
			return;
		}
		
		for(Properties props: propsList){
			
			String sawmillProfile = null;
			String[] reports = null;
			String outputPath = null;
			String profileName = null;
			
			// Get Sawmill details needed to create report
			try{
				profileName = Utility.getNodePropsString(props,
						Constants.NODE_ID_KEY, false);
				sawmillProfile = Utility.getNodePropsString(props,
						Constants.NODE_SAWMILL_PROFILE, true);
			}catch (ReportManagerException e){
				log.warning("Unable to obtain Samill profile name " +
						"from profile " + profileName);
				continue;
			}
			try{
				reports = Utility.getNodePropsString(props,
						Constants.NODE_REPORT_LIST, true).split(",");
			}catch (ReportManagerException e){
				log.warning("Unable to obtain Sawmill report list " +
						"from profile " + profileName);
				continue;
			}
			try{
				// TODO: This output directory tree should be automatically created
				outputPath = Utility.getNodePropsString(props,
						Constants.NODE_SAWMILL_OUTPUT, true);
				outputPath = outputPath + File.separator +
						new SimpleDateFormat("yyyy-MM").format(new Date());
				if(runName != null){
					outputPath = outputPath + File.separator + runName;
				}
			} catch (ReportManagerException e) {
				log.warning("Unable to obtain Samill output path " +
						"from profile " + profileName);
				continue;
			}
			
			// Generate the reports
			for(String report: reports){
				try{
					this.sawmillManager.generateReport(sawmillProfile,
							report.trim(), outputPath);
				}catch(SawmillException e){
					log.warning("An error occurred while generating the " +
							"Sawmill report " + report + ": " + e.getMessage());
				}
			}
				
		}
		
	}
	
	/**
	 * Output all Sawmill operation previously specified in a manner
	 * determined by the implementation of SawmillInterface being used
	 */
	public void outputSawmillOps(){
		
		try{
			this.sawmillManager.outputCommands();
		}catch(SawmillException e){
			log.warning("An error occurred while outputing Sawmill " +
					"operations: " + e.getMessage());
		}
		
	}
	
	/**
	 * Backup the contents of the staging directory
	 */
	public void backupStaging(){
		
		log.info("Backing up logs in the staging directory");
		
		for(Properties props: this.propsList){
			
			String profileID = null;
			try{
				Utility.getNodePropsString(props, Constants.NODE_ID_KEY,
						true);
				FileUtil.backupDir(props, Constants.STAGING_DIR, 
						Constants.BACKUP_DIR);
			}catch(ReportManagerException e){
				this.log.warning("An error occurred while backing up logs " +
						"from " + profileID + ": " + e.getMessage());
			}
			
		}
		
	}
	
	/**
	 * Remove all logs from the staging directory and any old logs from final
	 * and backup
	 */
	public void cleanup(){
		
		log.info("Cleaning out logs under staging");
		FileUtil.cleanupLogs(new File(
				System.getProperty(Constants.DIR_ROOT_PROP),
				Constants.STAGING_DIR));
		
		log.info("Cleaning out old logs under final and backup");
		long age = System.currentTimeMillis() -
				Long.parseLong(System.getProperty(Constants.LOG_AGE_PROP));
		FileUtil.cleanupOldLogs(new File(
				System.getProperty(Constants.DIR_ROOT_PROP),
				Constants.SAWMILL_DIR), age);
		FileUtil.cleanupOldLogs(new File(
				System.getProperty(Constants.DIR_ROOT_PROP),
				Constants.BACKUP_DIR), age);
		
	}
	
}
