//  Copyright 2009, California Institute of Technology.
//  ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
//  $Id$

package gov.nasa.pds.tse.catalog;

import gov.nasa.pds.tse.catalog.extractor.Extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import java.io.IOException;

/**
 * Utilizes the Extractor Factory to create instances of each class
 * that needs to be included in the catalog.
 * @author pramirez
 * @modifiedby Jordan Padams
 * @modifieddate 05/04/09
 * @version $Revision$
 */
public class CatalogExtractor {
	private Logger log = Logger.getLogger(this.getClass().getName());

	private File baseDir = null;
	private File extractDir = null;
	
	private HashMap mappings;
	
	//Variables are dependent upon extractor configuration file format.
	//If format changes, these variables will need to be changed.
	private static final String EXTRACTOR_PREFIX = "extractor";
	private static final String EXTRACTOR_CONFIG = "extractors.txt";

	/**
	 * Initializing method used when a base directory is given.
	 * @param base - Directory used as the base for all extractor files.
	 */
	public CatalogExtractor(String base) {
		baseDir = new File(System.getProperty("extractor.basedir", base));
	}

	/**
	 * Driver method for extraction of data for all classes to be included
	 * in catalog.
	 * @throws ExtractionException
	 */
	public void run() throws ExtractionException {//throws InvalidExtractor {
		PrintWriter writer = null;
		File dataDir = null;

		try {
			dataDir = new File(baseDir, "tse");
			//First create base data directory in case this is the first run
			FileUtils.forceMkdir(dataDir);
			
			//Next create a place to put extracted files
			extractDir = new File(dataDir, "extract");
			
			FileUtils.forceMkdir(extractDir);
		} catch (IOException e) {
			log.warning(e.getMessage());
			throw new ExtractionException("Could not setup directories in " + dataDir);
		}

		//Try to create run log file that will count the results
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(dataDir, "run.log"))));
		} catch (IOException e) {
			log.warning(e.getMessage());
			throw new ExtractionException("Could not create run log");
		}

		log.info("Beginning extraction");
		List uids = null;
		long totalTime = 0;
		int totalCount = 0;
		writer.println("run.date=" + new Date());
		//Run extract method on each class
		for (Iterator i = getExtractors().iterator(); i.hasNext();) {
			String extractorName = (String) i.next();
			log.fine("currExtName:" + extractorName);
			Extractor extractor;
			try {
				//Create directory for writing results to
				File extractorDir = new File(extractDir, extractorName);
				FileUtils.forceMkdir(extractorDir);

				log.info("Performing extraction for " + extractorName);

				//Create new extractor instance for given class.
				extractor = new Extractor(extractorName, (String) mappings.get(extractorName));

				Date start = new Date();

				uids = extractor.extract(baseDir,extractorDir);

				Date end = new Date();

				//Write to run log and track information for complete run
				writer.println(extractorName + ".count=" + uids.size());
				writer.println(extractorName + ".time=" + (end.getTime() - start.getTime()));
				totalCount += uids.size();
				totalTime += end.getTime() - start.getTime();
				log.info("Completed extraction for " + extractorName);
			} catch (ExtractionException e) {
				log.warning(e.getMessage());
			} catch (IOException e) {
				log.warning(e.getMessage());
			}
		} 

		//Log complete run stats
		writer.println("total.count=" + totalCount);
		writer.println("total.time=" + totalTime);
		writer.flush();
		writer.close();

		log.info("Finished extraction");
	}
	
	public Set getExtractors() {
		mappings = new HashMap();
		String extractorFile = System.getProperty("extractor.file", System.getProperty("user.home") + "/" + EXTRACTOR_CONFIG);
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(extractorFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Loop through the properties and map the extractor name to the name of its
		//configuration file.
		for (Iterator i = props.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			String value = null;
			if (key.startsWith(EXTRACTOR_PREFIX)) {
				value = props.getProperty(key);
				String name = key.substring(EXTRACTOR_PREFIX.length() + 1);
				mappings.put(name, value);
			}
		}
		
		return mappings.keySet();
	}

	public static void main(String [] args) throws Exception {
		String base = null;
		if (args.length == 1)
			base = args[0];
		else
			base = System.getProperty("user.home") + "/dev/workspace/tse";
			//base = System.getProperty("user.home") + "/tse";
		CatalogExtractor extractor = new CatalogExtractor(base);
		extractor.run();
	}

}
