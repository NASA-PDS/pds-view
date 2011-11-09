//  Copyright 2009, California Institute of Technology.
//  ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
//  $Id$

package gov.nasa.pds.search.core;

import gov.nasa.pds.search.core.extractor.Extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
 * Utilizes the Extractor Factory to create instances of each class that needs
 * to be included in the catalog.
 * 
 * @author pramirez
 * @modifiedby Jordan Padams
 * @modifieddate 05/04/09
 * @version $Revision$
 */
public class RegistryExtractor {
	private Logger LOG = Logger.getLogger(this.getClass().getName());

	private File confDir = null;
	private File outDir = null;
	private String registryUrl;
	// private File extractDir = null;

	private HashMap<String, String> mappings;

	// Variables are dependent upon extractor configuration file format.
	// If format changes, these variables will need to be changed.
	private static final String EXTRACTOR_PREFIX = "registry.extractor";
	private static final String EXTRACTOR_CONFIG = "extractors.txt";

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
	}

	/**
	 * Driver method for extraction of data for all classes to be included in
	 * catalog.
	 * 
	 * @throws ExtractionException
	 */
	public void run() throws ExtractionException {// throws InvalidExtractor {
		PrintWriter writer = null;
		File extractDir = null;
		File dataDir = null;

		try {
			System.out.println("Creating tse dir: " + this.outDir + "/tse");
			dataDir = new File(this.outDir, "tse");
			this.LOG.info("Create tse dir: " + dataDir.getAbsolutePath());
			// First create base data directory in case this is the first run
			FileUtils.forceMkdir(dataDir);

			// Next create a place to put extracted files
			extractDir = new File(dataDir, "extract");
			this.LOG.info("Create extract dir: " + extractDir.getAbsolutePath());

			FileUtils.forceMkdir(extractDir);
		} catch (IOException e) {
			LOG.warning(e.getMessage());
			throw new ExtractionException("Could not setup directories in "
					+ dataDir);
		}

		// Try to create run log file that will count the results
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(dataDir, "run.log"))));
		} catch (IOException e) {
			LOG.warning(e.getMessage());
			throw new ExtractionException("Could not create run log");
		}

		this.LOG.info("Beginning extraction");
		List uids = null;
		long totalTime = 0;
		int totalCount = 0;
		writer.println("run.date=" + new Date());
		// Run extract method on each class
		for (String extractorName : getExtractors()) {
			LOG.fine("currExtName: " + extractorName);
			Extractor extractor;
			try {
				// Create directory for writing results to
				File extractorDir = new File(extractDir, extractorName);
				FileUtils.forceMkdir(extractorDir);

				LOG.info("Performing extraction for " + extractorName);

				// Create new extractor instance for given class.
				extractor = new Extractor(writer, extractorName,
						mappings.get(extractorName), this.registryUrl);

				Date start = new Date();

				uids = extractor.extract(extractorDir);

				Date end = new Date();

				// Write to run log and track information for complete run
				// writer.println(extractorName + ".count=" + uids.size());
				writer.println(extractorName + ".time="
						+ (end.getTime() - start.getTime()));
				// totalCount += uids.size();
				totalTime += end.getTime() - start.getTime();
				LOG.info("Completed extraction for " + extractorName);
			} catch (ExtractionException e) {
				LOG.warning(e.getMessage());
			} catch (IOException e) {
				LOG.warning(e.getMessage());
			}
		}

		// Log complete run stats
		writer.println("total.count=" + totalCount);
		writer.println("total.time=" + totalTime);
		writer.flush();
		writer.close();

		LOG.info("Finished extraction");
	}

	public Set<String> getExtractors() {
		this.mappings = new HashMap<String, String>();
		// TODO Change back
		// String extractorFile = System.getProperty("extractor.file",
		// System.getProperty("user.home") + "/" + EXTRACTOR_CONFIG);
		// String extractorFile = System.getProperty("extractor.file", baseDir +
		// "/" + EXTRACTOR_CONFIG);
		// String extractorFile = this.confDir
		// + "/" + TseConstants.CONF_BASE + EXTRACTOR_CONFIG;
		// String extractorFile = this.confDir
		// + "/" + EXTRACTOR_CONFIG;
		// System.out.println(extractorFile);
		Properties props = new Properties();
		try {
			props.load(RegistryExtractor.class
					.getResourceAsStream(EXTRACTOR_CONFIG));
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

	public static void main(String[] args) throws Exception {
		String confHome = "";
		String outDir = "../";
		String registryUrl = "";
		if (args.length == 3) {
			registryUrl = args[0];
			outDir = args[1];
			confHome = args[2];
		} else if (args.length == 2) {
			registryUrl = args[0];
			outDir = args[1];
		}

		RegistryExtractor extractor = new RegistryExtractor(registryUrl,
				outDir, confHome);
		extractor.run();
	}

}
