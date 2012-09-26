//  Copyright 2009, California Institute of Technology.
//  ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
//  $Id$

package gov.nasa.pds.search.core.extractor;


import gov.nasa.pds.search.core.constants.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * Utilizes XML configuration files to extract data from the Registry
 * Service and create XML files for each Product containing the
 * raw data found in the Registry
 * 
 * @author Jordan Padams
 * @date 05/04/09
 * @version $Revision$
 */
public class RegistryExtractor {
	/** Standard output logger **/
	private Logger LOG = Logger.getLogger(this.getClass().getName());

	/** Directory containing the product class configuration files **/
	private File confDir;
	
	/** Directory created to hold the output Product Class data **/
	private File dataDir;
	
	/** URL for Registry **/
	private String registryUrl;
	
	/** Maximum query per product class **/ 
	private int queryMax;

	/** Map of product classes to accompanying XML config files **/
	private HashMap<String, String> mappings;
	
	/** Writes to output log **/
	private PrintWriter writer;
	
	/** Total time to run each product class query **/
	private long totalTime;
	
	/** Total number of queried objects **/
	private int totalCount;

	/** Prefix used for in properties files	specifying product classes */
	private static final String EXTRACTOR_PREFIX = "registry.extractor";

	/**
	 * Object initializer method with an output directory and clean boolean parameter specified.
	 * @param outDir
	 * @param clean
	 * @throws ProductClassException
	 */
	public RegistryExtractor(String outDir, boolean clean) throws ProductClassException {
		this.confDir = null;
		this.registryUrl = null;
		this.queryMax = -1;
		
		this.writer = null;
		this.totalTime = 0;
		this.totalCount = 0;
		
		createOutputs(outDir, clean);
	}
	
	/**
	 * Create the output directory for the Registry Extractor data
	 * 
	 * @param outDir - The base directory where the registry-data directory is created.
	 * @param clean - Boolean parameter used to determine whether or not the previous run data should be removed.
	 * 				This is used in case user decides to append to previous runs data.
	 * @throws ProductClassException
	 */
	private void createOutputs(String outDir, boolean clean) throws ProductClassException {
		try {
			this.dataDir = new File(outDir, "registry-data");
			this.LOG.info("Create registry dir: " + this.dataDir.getAbsolutePath());
			// Create registry directory to hold XML files containing desired registry data
			
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
			LOG.warning(e.getMessage());
			throw new ProductClassException("Could not setup directories in "
					+ this.dataDir);
		}

		// Try to create run log file that will count the results
		try {
			this.writer = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(this.dataDir, "run.log"))));
		} catch (IOException e) {
			this.LOG.warning(e.getMessage());
			throw new ProductClassException("Could not create run log");
		}
	}
	
	/**
	 * Complete the output for the log and flush/close the PrintWriter
	 */
	public void close() {
		// Log complete run stats
		this.writer.println("total.count=" + this.totalCount);
		this.writer.println("total.time=" + this.totalTime);
		this.writer.flush();
		this.writer.close();

		this.LOG.info("Finished data extraction");
	}

	/**
	 * Driver method for extraction of data for all classes to be included in
	 * catalog.
	 * 
	 * @throws ProductClassException
	 */
	public void run() throws ProductClassException {// throws InvalidExtractor {
		this.LOG.info("------- Beginning Registry Data Extraction --------");
		this.LOG.info("Registry URL: " + this.registryUrl);
		this.LOG.info("---------------------------------------------------------");
		
		List uids = null;
		this.writer.println("run.date=" + new Date());
		// Run extract method on each class
		for (String pc : getProductClassList()) {
			ProductClass productClass;
			try {
				// Create directory for writing results to
				File productClassDir = new File(this.dataDir, pc);
				FileUtils.forceMkdir(productClassDir);
				this.LOG.info("Querying " + pc + " objects");

				// Create new productClass instance for given class.
				productClass = new ProductClass(this.writer, pc,
						this.confDir.getAbsolutePath() + "/" + this.mappings.get(pc), this.registryUrl, this.queryMax);

				Date start = new Date();
				uids = productClass.query(productClassDir);
				Date end = new Date();

				// Write to run log and track information for complete run
				// writer.println(extractorName + ".count=" + uids.size());
				this.writer.println(pc + ".time="
						+ (end.getTime() - start.getTime()));
				this.totalTime += end.getTime() - start.getTime();
				this.LOG.info("Completed data extraction for " + pc);
			} catch (ProductClassException e) {
				this.LOG.warning(e.getMessage());
			} catch (IOException e) {
				this.LOG.warning(e.getMessage());
			}
		}
	}

	/**
	 * Return the set of product classes to be parsed, as denoted in the product-classes.txt
	 * configuration file.
	 * 
	 * @return
	 */
	public Set<String> getProductClassList() {
		this.mappings = new HashMap<String, String>();
		Properties props = new Properties();
		try {
			if (this.confDir != null) {
				props.load(new FileInputStream(new File(this.confDir.getAbsolutePath() + "/" + Constants.PC_PROPS)));
			}/* else {
				props.load(RegistryExtractor.class
						.getResourceAsStream(Constants.PC_PROPS));
			}*/
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	public File getConfDir() {
		return confDir;
	}

	/**
	 * @param confDir the confDir to set
	 */
	public void setConfDir(File confDir) {
		this.confDir = confDir;
	}

	/**
	 * @return the registryUrl
	 */
	public String getRegistryUrl() {
		return registryUrl;
	}

	/**
	 * @param registryUrl the registryUrl to set
	 */
	public void setRegistryUrl(String registryUrl) {
		this.registryUrl = registryUrl;
	}

	/**
	 * @return the queryMax
	 */
	public int getQueryMax() {
		return queryMax;
	}

	/**
	 * @param queryMax the queryMax to set
	 */
	public void setQueryMax(int queryMax) {
		this.queryMax = queryMax;
	}
}
