//	Copyright 2009-2013, by the California Institute of Technology.
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

import gov.nasa.pds.search.core.exception.SearchCoreException;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;
import gov.nasa.pds.search.core.registry.ProductClass;
import gov.nasa.pds.search.core.registry.ProductClassException;
import gov.nasa.pds.search.core.registry.objects.AssociationCache;
import gov.nasa.pds.search.core.stats.SearchCoreStats;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * Utilizes XML configuration files to extract data from the Registry Service
 * and create XML files for each Product containing the raw data found in the
 * <code>RegistryService</code>.
 * 
 * TODO This should be refactored a bit using a DataExtractor interface to allow
 * for extension to other data sources (i.e. databases)
 * 
 * @author jpadams
 */
public class RegistryExtractor implements DataExtractor {
	
	/** Directory containing the product class configuration files. **/
	private File confDir;

	/** Directory created to hold the output <code>ProductClass</code> data. **/
	private File outputDir;
	
	/** Standard output logger. **/
	private static Logger log = Logger.getLogger(RegistryExtractor.class.getName());

	/** Maximum query per product class. **/
	private int queryMax;
	
	/** List of Registry URLs. **/
	private List<String> primaryRegistries;
	
	/** List of Secondary Registry URLs. **/
	private List<String> secondaryRegistries;

	/**
	 * Primary Constructor for <code>RegistryExtractor</code> class, including output directory
	 * and the registry to query against.
	 * @param outDir
	 * @param confDir
	 * @param primaryRegistries
	 * @param secondaryRegistries
	 * @throws SearchCoreFatalException
	 */
	public RegistryExtractor(File confDir, List<String> primaryRegistries, 
			List<String> secondaryRegistries, File outputDir)
			throws SearchCoreFatalException {
		this.confDir = confDir;
		this.queryMax = -1;
		
		this.primaryRegistries = primaryRegistries;
		this.secondaryRegistries = secondaryRegistries;

		this.outputDir = outputDir;
	}
	
	/**
	 * Constructor used when an output directory is not specified and needs to be
	 * found using the <code>java.class.path</code>
	 * 
	 * @param confDir
	 * @param primaryRegistries
	 * @param secondaryRegistries
	 * @throws SearchCoreFatalException
	 */
	public RegistryExtractor(File confDir, List<String> primaryRegistries, 
			List<String> secondaryRegistries)
			throws SearchCoreFatalException {		
		this(confDir, primaryRegistries, secondaryRegistries, 
				new File(new File(System.getProperty("java.class.path"))
				.getParentFile().getParent()));
	}

	/**
	 * @see DataExtractor#run()
	 */
	public void run() throws SearchCoreException {
		List uids = null;
		// Run extract method on each class
		try {
		ProductClass productClass;
		for (File coreConfig : getCoreConfigs(this.confDir)) {
			System.out.println("Processing config: " + coreConfig.getName());
	        log.log(new ToolsLogRecord(ToolsLevel.DEBUG,
	        		"Querying with config: " + coreConfig.getAbsolutePath()));
	
			// Create new productClass instance for given class.
			productClass = new ProductClass(this.outputDir, this.primaryRegistries, this.secondaryRegistries);
			
			if (this.queryMax > 0) {
				productClass.setQueryMax(this.queryMax);
			}
	
			SearchCoreStats.resetStart();
			uids = productClass.query(coreConfig);
			SearchCoreStats.recordLocalTime(coreConfig.getName());

	        log.log(new ToolsLogRecord(ToolsLevel.SUCCESS,
	        		"Completed extraction:  " + coreConfig.getName() + "\n"));
	        
	        AssociationCache.flush();
		}
		} catch (ProductClassException e) {
			//e.printStackTrace();
			throw new SearchCoreException("Problems in ProductClass object:" + e.getMessage());
		} catch (SearchCoreFatalException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * @see DataExtractor#getCoreConfigs(File)
	 */
	public List<File> getCoreConfigs(File configDir) throws SearchCoreException {	
		if (configDir.isDirectory()) {
			return new ArrayList<File>(FileUtils.listFiles(configDir, new String[] {"xml"}, true));
		} else if (configDir.isFile()) {
			return Arrays.asList(configDir);
		} else {
			throw new SearchCoreException (configDir.getAbsolutePath() + " does not exist.");
		}
	}

	/**
	 * @see DataExtractor#getConfDir()
	 */
	public final File getConfDir() {
		return this.confDir;
	}

	/**
	 * @see DataExtractor#setConfDir(File)
	 */
	public final void setConfDir(File confDir) {
		this.confDir = confDir;
	}

	/**
	 * @see DataExtractor#getQueryMax()
	 */
	public final int getQueryMax() {
		return this.queryMax;
	}

	/**
	 * @see DataExtractor#setQueryMax(int)
	 */
	public final void setQueryMax(int queryMax) {
		this.queryMax = queryMax;
	}

	/**
	 * @return the primaryRegistries
	 */
	public List<String> getPrimaryRegistries() {
		return this.primaryRegistries;
	}

	/**
	 * @param primaryRegistries the primaryRegistries to set
	 */
	public void setPrimaryRegistries(List<String> primaryRegistries) {
		this.primaryRegistries = primaryRegistries;
	}

	/**
	 * @return the secondaryRegistries
	 */
	public List<String> getSecondaryRegistries() {
		return this.secondaryRegistries;
	}

	/**
	 * @param secondaryRegistries the backupRegistries to set
	 */
	public void setSecondaryRegistries(List<String> secondaryRegistries) {
		this.secondaryRegistries = secondaryRegistries;
	}

	/**
	 * @return the outputDir
	 */
	public File getOutputDir() {
		return outputDir;
	}

	/**
	 * @param outputDir the outputDir to set
	 */
	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}
	
}
