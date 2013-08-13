//	Copyright 2009-2012, by the California Institute of Technology.
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
//	$Id$
//

package gov.nasa.pds.search.core.extractor;

import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;
import gov.nasa.pds.search.core.stats.SearchCoreStats;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * Utilizes XML configuration files to extract data from the Registry Service
 * and create XML files for each Product containing the raw data found in the
 * Registry.
 * 
 * @author jpadams
 */
public class RegistryExtractor {
	/** Flag to ensure the directory prep method is only run once **/
	private static boolean prep = false;

	/** Directory containing the product class configuration files. **/
	private File confDir;

	/** Directory created to hold the output Product Class data. **/
	private File dataDir;
	
	/** Standard output logger. **/
	private static Logger log = Logger.getLogger(RegistryExtractor.class.getName());

	/** Maximum query per product class. **/
	private int queryMax;
	
	/** List of Registry URLs. **/
	private List<String> primaryRegistries;
	
	private List<String> backupRegistries;
	
	/** Total time to run each product class query. **/
	private long totalTime;

	/**
	 * Object initializer method with an output directory and clean boolean
	 * parameter specified.
	 * 
	 * @param outDir
	 * @param clean
	 * @throws ProductClassException	thrown when directories cannot be created
	 */
	public RegistryExtractor(String outDir, File confDir, 
			List<String> primaryRegistries, List<String> backupRegistries, boolean clean)
			throws SearchCoreFatalException {
		this.confDir = confDir;
		this.queryMax = -1;
		this.totalTime = 0;
		
		this.primaryRegistries = primaryRegistries;
		this.backupRegistries = backupRegistries;

		this.dataDir = getOutputDataDirectory(outDir);
	}

	/**
	 * Driver method for extraction of data for all classes to be included in
	 * catalog.
	 * 
	 * @throws ProductClassException	thrown if error found during registry queries
	 * @throws IOException 				thrown if files cannot be created
	 * @throws SearchCoreFatalException 
	 */
	public final void run() throws Exception {
        log.log(new ToolsLogRecord(ToolsLevel.DEBUG,
        		"------- Beginning Registry Data Extraction --------"));

		List uids = null;
		// Run extract method on each class
		ProductClass productClass;
		for (File coreConfig : getCoreConfigs(this.confDir)) {
	        log.log(new ToolsLogRecord(ToolsLevel.INFO,
	        		"Querying with config: " + coreConfig.getAbsolutePath()));
	
			// Create new productClass instance for given class.
			productClass = new ProductClass(this.dataDir, this.primaryRegistries, this.backupRegistries);
			
			if (this.queryMax > 0) {
				productClass.setQueryMax(this.queryMax);
			}
	
			SearchCoreStats.localStartTime = new Date();
			uids = productClass.query(coreConfig);
			SearchCoreStats.recordLocalTime(coreConfig.getName());

	        log.log(new ToolsLogRecord(ToolsLevel.SUCCESS,
	        		"Completed extraction:  " + coreConfig.getAbsolutePath()));
		}
		log.log(new ToolsLogRecord(ToolsLevel.INFO, "Total time (ms): " + this.totalTime));
	}

	/**
	 * Return the set of product classes to be parsed, as denoted in the
	 * product-classes.txt configuration file.
	 * 
	 * @throws ProductClassException	thrown when error loading properties file
	 * @return	list of product class properties
	 */
	public List<File> getCoreConfigs(File configDir) throws ProductClassException {	
		if (configDir.isDirectory()) {
			return new ArrayList<File>(FileUtils.listFiles(configDir, new String[] {"xml"}, true));
		} else if (configDir.isFile()) {
			return Arrays.asList(configDir);
		} else {
			throw new ProductClassException (configDir.getAbsolutePath() + " does not exist.");
		}
	}

	/**
	 * @return the confDir
	 */
	public final File getConfDir() {
		return confDir;
	}

	/**
	 * @param confDir
	 *            the confDir to set
	 */
	public final void setConfDir(File confDir) {
		this.confDir = confDir;
	}

	/**
	 * @return the queryMax
	 */
	public final int getQueryMax() {
		return queryMax;
	}

	/**
	 * @param queryMax
	 *            the queryMax to set
	 */
	public final void setQueryMax(int queryMax) {
		this.queryMax = queryMax;
	}

	/**
	 * @return the primaryRegistries
	 */
	public List<String> getPrimaryRegistries() {
		return primaryRegistries;
	}

	/**
	 * @param primaryRegistries the primaryRegistries to set
	 */
	public void setPrimaryRegistries(List<String> primaryRegistries) {
		this.primaryRegistries = primaryRegistries;
	}

	/**
	 * @return the backupRegistries
	 */
	public List<String> getBackupRegistries() {
		return backupRegistries;
	}

	/**
	 * @param backupRegistries the backupRegistries to set
	 */
	public void setBackupRegistries(List<String> backupRegistries) {
		this.backupRegistries = backupRegistries;
	}
	
	/**
	 * Create the output directory for the Registry Extractor data.
	 * 
	 * @param outDir	The base directory where the registry-data directory is
	 *            		created.
	 * @param clean		Boolean parameter used to determine whether or not the
	 *            		previous run data should be removed. This is used in case user
	 *            		decides to append to previous runs data.
	 * @throws ProductClassException	thrown when directories cannot be created
	 */
	public static void prepForRun(String outDir, boolean clean)
			throws SearchCoreFatalException {
		if (!prep) {
			File dataDir = getOutputDataDirectory(outDir);
			try {
			    log.log(new ToolsLogRecord(ToolsLevel.DEBUG, "Creating directory "
			    		+ outDir));
				// Create registry directory to hold XML files containing desired
				// registry data
	
				// Back up old registry-data if it exists
				if (dataDir.isDirectory()) {
					File backupDir = new File(outDir, Constants.REGISTRY_DATA_DIR + "_old");
	
					if (backupDir.isDirectory()) {
						FileUtils.deleteDirectory(backupDir);
					}
	
					// Remove dataDir if clean flag is true
					if (clean) {
						FileUtils.moveDirectory(dataDir, backupDir);
					} else {
						FileUtils.copyDirectory(dataDir, backupDir);
					}
				}
	
				FileUtils.forceMkdir(dataDir);
	
			} catch (IOException e) {
			      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
			              outDir));
				throw new SearchCoreFatalException("Could not setup directories in "
						+ dataDir.getAbsolutePath());
			}
			prep = true;
		}
	}
	
	public final static File getOutputDataDirectory(String outDir) {
		return new File(outDir, Constants.REGISTRY_DATA_DIR);
	}
	
}
