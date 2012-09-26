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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
	private Logger log = Logger.getLogger(this.getClass().getName());

	/** Map of product classes to accompanying XML config files. **/
	private HashMap<String, String> mappings;

	/** Maximum query per product class. **/
	private int queryMax;
	
	/** URL for Registry. **/
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
	public RegistryExtractor(String outDir, boolean clean)
			throws ProductClassException {
		this.confDir = null;
		this.registryUrl = null;
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
			throws ProductClassException {
		try {
			this.dataDir = new File(outDir, "registry-data");
			this.log.info("Create registry dir: "
					+ this.dataDir.getAbsolutePath());
			// Create registry directory to hold XML files containing desired
			// registry data

			// Back up old registry-data if it exists
			if (this.dataDir.isDirectory()) {
				File backupDir = new File(outDir, "registry-data_old");

				if (backupDir.isDirectory()) {
					FileUtils.forceDelete(backupDir);
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
			log.warning(e.getMessage());
			throw new ProductClassException("Could not setup directories in "
					+ this.dataDir);
		}

		// Try to create run log file that will count the results
		try {
			this.writer = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(this.dataDir, "run.log"))));
		} catch (IOException e) {
			this.log.warning(e.getMessage());
			throw new ProductClassException("Could not create run log");
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

		this.log.info("Finished data extraction");
	}

	/**
	 * Driver method for extraction of data for all classes to be included in
	 * catalog.
	 * 
	 * @throws ProductClassException	thrown if error found during registry queries
	 * @throws IOException 				thrown if files cannot be created
	 */
	public final void run() throws ProductClassException, IOException {
		this.log.info("------- Beginning Registry Data Extraction --------");
		this.log.info("Registry URL: " + this.registryUrl);
		this.log.info("---------------------------------------------------------");

		List uids = null;
		this.writer.println("run.date=" + new Date());
		// Run extract method on each class
		for (String pc : getProductClassList()) {
			ProductClass productClass;
			// Create directory for writing results to
			File productClassDir = new File(this.dataDir, pc);
			FileUtils.forceMkdir(productClassDir);
			this.log.info("Querying " + pc + " objects");
	
			// Create new productClass instance for given class.
			productClass = new ProductClass(this.writer, pc,
					this.confDir.getAbsolutePath() + "/"
							+ this.mappings.get(pc), this.registryUrl,
					this.queryMax);
	
			Date start = new Date();
			uids = productClass.query(productClassDir);
			Date end = new Date();
	
			// Write to run log and track information for complete run
			// writer.println(extractorName + ".count=" + uids.size());
			this.writer.println(pc + ".time="
					+ (end.getTime() - start.getTime()));
			this.totalTime += end.getTime() - start.getTime();
			this.log.info("Completed data extraction for " + pc);
		}
	}

	/**
	 * Return the set of product classes to be parsed, as denoted in the
	 * product-classes.txt configuration file.
	 * 
	 * @throws ProductClassException	thrown when error loading properties file
	 * @return	list of product class properties
	 */
	public Set<String> getProductClassList() throws ProductClassException {
		this.mappings = new HashMap<String, String>();
		Properties props = new Properties();
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
		}

		return this.mappings.keySet();
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
