//  Copyright 2009, California Institute of Technology.
//  ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
//  $Id$

package gov.nasa.pds.search.core.catalog;

import gov.nasa.pds.search.core.catalog.extractor.Extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
public class CatalogExtractor {
	private Logger LOG = Logger.getLogger(this.getClass().getName());

	private File baseDir = null;
	private File extractDir = null;

	private HashMap<String, String> mappings;

	// Variables are dependent upon extractor configuration file format.
	// If format changes, these variables will need to be changed.
	private static final String EXTRACTOR_PREFIX = "catalog.extractor";
	private static final String EXTRACTOR_CONFIG = "extractors.txt";

	/**
	 * Initializing method used when a base directory is given.
	 * 
	 * @param base
	 *            - Directory used as the base for all extractor files.
	 */
	public CatalogExtractor(String base) {
		baseDir = new File(System.getProperty("extractor.basedir", base));
	}

	/**
	 * Driver method for extraction of data for all classes to be included in
	 * catalog.
	 * 
	 * @throws ExtractionException
	 */
	public void run() throws ExtractionException {// throws InvalidExtractor {
		PrintWriter writer = null;
		File dataDir = null;

		try {
			dataDir = new File(baseDir, "tse");
			// First create base data directory in case this is the first run
			FileUtils.forceMkdir(dataDir);

			// Next create a place to put extracted files
			extractDir = new File(dataDir, "extract");

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

		LOG.info("Beginning extraction");
		List uids = null;
		long totalTime = 0;
		int totalCount = 0;
		writer.println("run.date=" + new Date());
		// Run extract method on each class
		for (String extractorName : getExtractors()) {
			LOG.info("currExtName: " + extractorName);
			Extractor extractor;
			try {
				// Create directory for writing results to
				File extractorDir = new File(extractDir, extractorName);
				FileUtils.forceMkdir(extractorDir);

				LOG.info("Performing extraction for " + extractorName);

				// Create new extractor instance for given class.
				extractor = new Extractor(writer, extractorName,
						mappings.get(extractorName));

				Date start = new Date();

				uids = extractor.extract(baseDir, extractorDir);

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
		mappings = new HashMap<String, String>();
		// TODO Change back
		// String extractorFile = System.getProperty("extractor.file",
		// System.getProperty("user.home") + "/" + EXTRACTOR_CONFIG);
		// String extractorFile = System.getProperty("extractor.file", baseDir +
		// "/" + EXTRACTOR_CONFIG);
		System.out.println("In here");
		String extractorFile = System.getProperty("extractor.file", baseDir
				+ "/" + TseConstants.CONF_BASE + EXTRACTOR_CONFIG);
		System.out.println(extractorFile);
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(extractorFile));
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
				mappings.put(name, value);
			}
		}

		return mappings.keySet();
	}

	public static void main(String[] args) throws Exception {
		String base = null;
		if (args.length == 1)
			base = args[0];
		else
			base = "/Users/jpadams/dev/workspace/search-tools-workspace/search/search-core/src/main/resources/";
		// base = System.getProperty("user.home") + "/dev/workspace/tse";
		// base = System.getProperty("user.home") + "/tse";
		CatalogExtractor extractor = new CatalogExtractor(base);
		extractor.run();
	}

}
