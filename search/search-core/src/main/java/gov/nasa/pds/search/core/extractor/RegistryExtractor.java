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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
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
	/** Prefix used for in properties files specifying product classes. */
	private static final String EXTRACTOR_PREFIX = "registry.extractor";

	/** Directory containing the product class configuration files. **/
	private File confDir;

	/** Directory created to hold the output Product Class data. **/
	private File dataDir;
	
	/** Standard output logger. **/
	private static Logger log = Logger.getLogger(RegistryExtractor.class.getName());

	/** Map of product classes to accompanying XML config files. **/
	private HashMap<String, String> mappings;

	/** Maximum query per product class. **/
	private int queryMax;
	
	/** List of Registry URLs. **/
	private String registryUrl;
	
	/** Total number of queried objects. **/
	private int totalCount;
	
	/** Total time to run each product class query. **/
	private long totalTime;
	
	/** Writes to output log. **/
	private PrintWriter writer;

	/**
	 * Object initializer method with an output directory and clean boolean
	 * parameter specified.
	 * 
	 * @param outDir
	 * @param clean
	 * @throws ProductClassException	thrown when directories cannot be created
	 */
	public RegistryExtractor(String outDir, File confDir, String registryUrl, boolean clean)
			throws SearchCoreFatalException {
		this.confDir = confDir;
		this.registryUrl = registryUrl;
		this.queryMax = -1;

		this.writer = null;
		this.totalTime = 0;
		this.totalCount = 0;

		createOutputs(outDir, clean);
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
	private void createOutputs(String outDir, boolean clean)
			throws SearchCoreFatalException {
		try {
			this.dataDir = new File(outDir, Constants.REGISTRY_DATA_DIR);
		    log.log(new ToolsLogRecord(ToolsLevel.DEBUG, "Creating directory "
		    		+ outDir));
			// Create registry directory to hold XML files containing desired
			// registry data

			// Back up old registry-data if it exists
			if (this.dataDir.isDirectory()) {
				File backupDir = new File(outDir, Constants.REGISTRY_DATA_DIR + "_old");

				if (backupDir.isDirectory()) {
					FileUtils.deleteDirectory(backupDir);
				}

				// Remove dataDir if clean flag is true
				if (clean) {
					FileUtils.moveDirectory(this.dataDir, backupDir);
				} else {
					FileUtils.copyDirectory(this.dataDir, backupDir);
				}
			}

			FileUtils.forceMkdir(this.dataDir);

		} catch (IOException e) {
		      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
		              outDir));
			throw new SearchCoreFatalException("Could not setup directories in "
					+ this.dataDir);
		}

		// Try to create run log file that will count the results
		try {
			this.writer = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(this.dataDir, "run.log"))));
		} catch (IOException e) {
		      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
		              outDir));
			throw new SearchCoreFatalException("Could not create run log");
		}
	}

	/**
	 * Complete the output for the log and flush/close the PrintWriter.
	 */
	public final void close() {
		// Log complete run stats
		this.writer.println("total.count=" + this.totalCount);
		this.writer.println("total.time=" + this.totalTime);
		this.writer.flush();
		this.writer.close();

		log.log(new ToolsLogRecord(ToolsLevel.SUCCESS, "Data extraction complete"));
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
		this.writer.println("run.date=" + new Date());
		// Run extract method on each class
		ProductClass productClass;
		for (File coreConfig : getCoreConfigs(this.confDir)) {
			// Create directory for writing results to

	        log.log(new ToolsLogRecord(ToolsLevel.DEBUG,
	        		"Querying for " + coreConfig.getAbsolutePath()));
	
			// Create new productClass instance for given class.
			productClass = new ProductClass(this.writer,
					this.dataDir, this.registryUrl,
					this.queryMax);
	
			Date start = new Date();
			uids = productClass.query(coreConfig);
			Date end = new Date();
	
			// Write to run log and track information for complete run
			this.writer.println("ProductClass.count=" + uids.size());
			this.writer.println(coreConfig + ".time="
					+ (end.getTime() - start.getTime()));
			this.totalTime += end.getTime() - start.getTime();
	        log.log(new ToolsLogRecord(ToolsLevel.SUCCESS,
	        		"Completed extraction for " + coreConfig.getAbsolutePath()));
		}
	}

	/**
	 * Return the set of product classes to be parsed, as denoted in the
	 * product-classes.txt configuration file.
	 * 
	 * @throws ProductClassException	thrown when error loading properties file
	 * @return	list of product class properties
	 */
	public List<File> getCoreConfigs(File configDir) throws ProductClassException {
		this.mappings = new HashMap<String, String>();
		String[] extensions = { "xml" };
		
		return new ArrayList<File>(FileUtils.listFiles(configDir, extensions, true));
		/*Properties props = new Properties();
		try {
			if (this.confDir != null) {

					props.load(new FileInputStream(new File(this.confDir
							.getAbsolutePath() + "/" + Constants.PC_PROPS)));
			}
		} catch (Exception e) {
			throw new ProductClassException("Error loading product class properties.\n"
					+ e.getMessage());
		}

		// Loop through the properties and map the extractor name to the name of
		// its
		// configuration file.
		for (Object key : props.keySet()) {
			String value = null;
			if (((String) key).startsWith(EXTRACTOR_PREFIX)) {
				value = props.getProperty((String) key);
				String name = ((String) key).substring(EXTRACTOR_PREFIX
						.length() + 1);
				this.mappings.put(name, value);
			}
		}*/

		//return this.mappings.keySet();
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
	 * @return the registryUrl
	 */
	public final String getRegistryUrl() {
		return registryUrl;
	}

	/**
	 * @param registryUrl
	 *            the registryUrl to set
	 */
	public final void setRegistryUrl(String registryUrl) {
		this.registryUrl = registryUrl;
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
}
