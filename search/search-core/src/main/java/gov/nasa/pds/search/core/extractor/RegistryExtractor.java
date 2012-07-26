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
	private Logger LOG = Logger.getLogger(this.getClass().getName());

	private File confDir = null;
	private File outDir = null;
	private String registryUrl;
	private int queryMax;
	// private File extractDir = null;

	private HashMap<String, String> mappings;

	// Variables are dependent upon extractor configuration file format.
	// If format changes, these variables will need to be changed.
	private static final String EXTRACTOR_PREFIX = "registry.extractor";
	//private static final String EXTRACTOR_CONFIG = "product-classes.txt";

	/**
	 * Initializing method used when a base directory is given.
	 * 
	 * @param base
	 *            - Directory used as the base for all extractor files.
	 */
	public RegistryExtractor(String registryUrl, String outDir, String confDir) {
		// baseDir = new File(System.getProperty("extractor.basedir", base));
		this.confDir = new File(confDir);
		this.outDir = new File(outDir);
		this.registryUrl = registryUrl;
		this.queryMax = -1;
	}
	
	public RegistryExtractor(String registryUrl, String outDir) {
		// baseDir = new File(System.getProperty("extractor.basedir", base));
		this.confDir = new File("");
		this.outDir = new File(outDir);
		this.registryUrl = registryUrl;
		this.queryMax = -1;
	}
	
	public RegistryExtractor(String registryUrl) {
		// baseDir = new File(System.getProperty("extractor.basedir", base));
		this.confDir = new File("");
		this.outDir = new File("../");
		this.registryUrl = registryUrl;
		this.queryMax = -1;
	}

	/**
	 * Driver method for extraction of data for all classes to be included in
	 * catalog.
	 * 
	 * @throws ProductClassException
	 */
	public void run() throws ProductClassException {// throws InvalidExtractor {
		PrintWriter writer = null;
		File extractDir = null;
		File dataDir = null;

		// Create output directories
		try {
			dataDir = new File(this.outDir, "registry-data");
			this.LOG.info("Create registry dir: " + dataDir.getAbsolutePath());
			// Create registry directory to hold XML files containing desired registry data
			FileUtils.forceMkdir(dataDir);

			// Next create a place to put extracted files
			//extractDir = new File(dataDir, "extract");
			//this.LOG.info("Create extract dir: " + extractDir.getAbsolutePath());
			//FileUtils.forceMkdir(extractDir);
			
		} catch (IOException e) {
			LOG.warning(e.getMessage());
			throw new ProductClassException("Could not setup directories in "
					+ dataDir);
		}

		// Try to create run log file that will count the results
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(dataDir, "run.log"))));
		} catch (IOException e) {
			this.LOG.warning(e.getMessage());
			throw new ProductClassException("Could not create run log");
		}

		this.LOG.info("------- Beginning Registry Data Extraction --------");
		this.LOG.info("Registry URL: " + this.registryUrl);
		this.LOG.info("---------------------------------------------------------");
		
		List uids = null;
		long totalTime = 0;
		int totalCount = 0;
		writer.println("run.date=" + new Date());
		// Run extract method on each class
		for (String pc : getProductClassList()) {
			ProductClass productClass;
			try {
				// Create directory for writing results to
				File productClassDir = new File(dataDir, pc);
				FileUtils.forceMkdir(productClassDir);

				this.LOG.info("Querying " + pc + " objects");

				// Create new productClass instance for given class.
				productClass = new ProductClass(writer, pc,
						this.confDir.getAbsolutePath() + "/" + mappings.get(pc), this.registryUrl, this.queryMax);

				Date start = new Date();
				uids = productClass.query(productClassDir);
				Date end = new Date();

				// Write to run log and track information for complete run
				// writer.println(extractorName + ".count=" + uids.size());
				writer.println(pc + ".time="
						+ (end.getTime() - start.getTime()));
				totalTime += end.getTime() - start.getTime();
				this.LOG.info("Completed data extraction for " + pc);
			} catch (ProductClassException e) {
				this.LOG.warning(e.getMessage());
			} catch (IOException e) {
				this.LOG.warning(e.getMessage());
			}
		}

		// Log complete run stats
		writer.println("total.count=" + totalCount);
		writer.println("total.time=" + totalTime);
		writer.flush();
		writer.close();

		this.LOG.info("Finished data extraction");
	}

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
	 * @return the outDir
	 */
	public File getOutDir() {
		return outDir;
	}

	/**
	 * @param outDir the outDir to set
	 */
	public void setOutDir(File outDir) {
		this.outDir = outDir;
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

	public static void main(String[] args) throws Exception {
		String confHome = "";
		String outDir = "../";
		String registryUrl = "";
		int queryMax = -1;
		if (args.length == 4) {
			registryUrl = args[0];
			outDir = args[1];
			confHome = args[2];
			queryMax = Integer.parseInt(args[3]);
		} else if (args.length == 3) {
			registryUrl = args[0];
			outDir = args[1];
			confHome = args[2];
		} else if (args.length == 2) {
			registryUrl = args[0];
			outDir = args[1];
		}

		RegistryExtractor extractor = new RegistryExtractor(registryUrl,
				outDir, confHome);
		extractor.setQueryMax(queryMax);
		extractor.run();
	}

}
