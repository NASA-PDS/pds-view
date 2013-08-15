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
//	$Id: ProductClass.java 11851 2013-08-13 23:19:44Z jpadams $

package gov.nasa.pds.search.core.registry;

import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;
import gov.nasa.pds.search.core.registry.objects.SearchCoreExtrinsic;
import gov.nasa.pds.search.core.schema.CoreConfigReader;
import gov.nasa.pds.search.core.schema.DataSource;
import gov.nasa.pds.search.core.schema.Field;
import gov.nasa.pds.search.core.schema.Product;
import gov.nasa.pds.search.core.schema.SourcePriority;
import gov.nasa.pds.search.core.schema.SourceType;
import gov.nasa.pds.search.core.stats.SearchCoreStats;
import gov.nasa.pds.search.core.util.Debugger;
import gov.nasa.pds.search.core.util.XMLWriter;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * @author jpadams
 * 
 */
public class ProductClass {

	/** Default start number to be appended to output files */
	private static final int OUT_SEQ_START = 10000;

	/** User-specified output directory **/
	private File outputDir;
	
	/** Data Product object from JAXB Bindings **/
	private Product product;

	// TODO Refactor this to start outputting to XML immediately.
	// Some PSA Data has such large description that it kills the buffers
	/** Map of all values for desired search fields **/
	private Map<String, List<String>> finalVals;

	/** Output logger **/
	private static Logger log = Logger.getLogger(ProductClass.class.getName());
	
	/** List of primary registries to query against **/
	private List<String> primaryRegistries;
	
	/** List of secondary registries to query against **/
	private List<String> secondaryRegistries;
	
	/** The registry handler being used for the specific product class **/
	private RegistryHandler registryHandler;
	
	private int queryMax = Constants.QUERY_MAX;

	/**
	 * Initialize global variables used throughout
	 * 
	 * @param writer		writer for run log
	 * @param name			class of catalog to be extracted
	 * @param file			object type XML config file
	 * @param registryUrl	URL for registry to query
	 * @param queryMax		maximum number of queried results
	 */
	public ProductClass(String name, File outputDir,
			 List<String> primaryRegistries, List<String> secondaryRegistries) {
		
		this.primaryRegistries = primaryRegistries;
		this.secondaryRegistries = secondaryRegistries;

		this.finalVals = new HashMap<String, List<String>>();
		
		this.product = null;
	}
	
	public ProductClass(File outputDir,
			List<String> primaryRegistries, List<String> secondaryRegistries) {
		
		this.outputDir = outputDir;
		
		this.primaryRegistries = primaryRegistries;
		this.secondaryRegistries = secondaryRegistries;

		this.finalVals = new HashMap<String, List<String>>();
		
		this.product = null;
	}

	/**
	 * Clears the maps used to store values for the current product class.
	 */
	private void clearMaps() {
		this.finalVals.clear();
	}

	/**
	 * Driving method for querying data from the registry
	 * 
	 * @param coreConfig
	 * @return
	 * @throws ProductClassException
	 */
	public List<String> query(File coreConfig) throws ProductClassException, SearchCoreFatalException {
		List<String> instkeys = new ArrayList<String>();
		int outSeqNum = 0;
		try {
			this.product = CoreConfigReader.unmarshall(coreConfig);
		} catch (Exception e) {
		    log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
		    		coreConfig.getAbsolutePath()));
			throw new SearchCoreFatalException("Error: Problem parsing " + coreConfig
					+ "\nStack Trace: " + e.getStackTrace().toString()
					+ "\nError Message: " + e.getMessage() 
					+ "\nCause: " + e.getCause().getMessage());
		}
			
			
		// Create output directory
		File registryOutputDir = createOutputDirectory();
		
		// Append registries from core config
		this.registryHandler = getRegistryHandler(this.product);
		
		outSeqNum = getOutputSeqNumber(registryOutputDir);
		
		XMLWriter writer;
		try {
			if (this.registryHandler.doPrimaryRegistriesExist()) {
				List<SearchCoreExtrinsic> extList = this.registryHandler.getExtrinsicsByObjectInfo(
						this.product.getSpecification().getRegistryObjectType(),
						this.product.getSpecification().getRegistryObjectName());
			
				for (SearchCoreExtrinsic searchExtrinsic : extList) {
					clearMaps();
	
					outSeqNum++;
	
					// Get class properties
					setFieldValues(searchExtrinsic);
	
					writer = new XMLWriter(this.finalVals, registryOutputDir,
							outSeqNum, this.product.getSpecification().getTitle());
					writer.write();
					
					instkeys.add(searchExtrinsic.getLid());
				    log.log(new ToolsLogRecord(ToolsLevel.DEBUG, "Completed: " + searchExtrinsic.getLid()));
				    SearchCoreStats.numProducts++;
				}
			}
		} catch (RegistryHandlerException e) {
		    log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage() 
		    		+ " Check Registry connections and configuration - ",
		    		coreConfig.getAbsolutePath()));
		} catch (Exception e) {
			throw new ProductClassException("Exception: "
					+ e.getClass().getName() + ": " + e.getMessage());
		}
		return instkeys;
	}

	/**
	 * Check if there are files in the output directory. If so, assuming the files
	 * are from a previous run of the Search Core, add the count to the sequence
	 * number constant in order to add to the files instead of overwriting them.
	 * 
	 * @param 	outDir
	 * @return	start number for the suffix for the index docs
	 */
	private int getOutputSeqNumber(File outDir) {
		if (outDir.list().length > 0) {
			return OUT_SEQ_START + outDir.list().length;
		} else {
			return OUT_SEQ_START;
		}
	}
	
	private File createOutputDirectory() throws SearchCoreFatalException {
		try {
			File registryOutputDir = new File(this.outputDir, this.product.getSpecification().getTitle());
			FileUtils.forceMkdir(registryOutputDir);
			return registryOutputDir;
		} catch (IOException e) {
		    log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
		    		this.outputDir + "/" + this.product.getSpecification().getTitle()));
		    throw new SearchCoreFatalException("Could not create directory: "
		    		+ this.outputDir + "/" + this.product.getSpecification().getTitle());
		}
	}

	/**
	 * Get all of the attributes and their values and place them into a HashMap,
	 * valArray. The HashMap is made of of attrName->value pairs. The value in
	 * the pair depends upon the current attribute's index, where it is either
	 * the value in attrVals or a value queried from the database.
	 * 
	 * @see gov.nasa.pds.search.core.extractor.registry.MappingTypes
	 * 
	 * @param ExtrinsicObject			object to be used to query information for the index
	 * @throws ProductClassException	any errors throughout the querying of registry and
	 * 									managing the data
	 */
	private void setFieldValues(SearchCoreExtrinsic searchExtrinsic)
			throws ProductClassException {
		try {
			/* Initialize local variables */
			List<String> valueList = new ArrayList<String>();
			
			String value;
			
			// Loop through class results beginning from top
			for (Field field : this.product.getIndexFields().getField()) {
				//TODO uncomment to use suffixes for field names
				String fieldName = field.getName(); //+ SolrSchemaField.getSuffix(field.getType());
				
				// Handle registry path
				if ((value = field.getRegistryPath()) != null) {
					valueList = registryPathHandler(value, searchExtrinsic);
				} else if ((value = field.getOutputString()) != null) {	// Handle outputString
					valueList = new ArrayList<String>();
					valueList.add(checkForSubstring(value, searchExtrinsic));
				}
				
				if (valueList.isEmpty() && field.getDefault() != null) {
					valueList.add(field.getDefault());
				}
				
				this.finalVals.put(fieldName, valueList);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ProductClassException("Exception "
					+ ex.getClass().getName() + ex.getMessage());
		}
	}
	
	/**
	 * Figures out if the registry path is an association (dot-connected string) or
	 * just a slot. If its an association, it starts traversing the path to get the values.
	 * If its a slot, it returns the value list.
	 * 
	 * @param registryPath
	 * @param searchExtrinsic
	 * @return
	 * @throws Exception
	 */
	private List<String> registryPathHandler(String registryPath, SearchCoreExtrinsic searchExtrinsic) throws Exception {
		String[] pathArray;
		
		if ((pathArray = registryPath.split("\\.")).length > 1) {
			Debugger.debug("Traversing registry path - " + searchExtrinsic.getLid()
					+ " - " + registryPath);
			return traverseRegistryPath(Arrays.asList(pathArray), Arrays.asList(searchExtrinsic));
		} else {	// Field is a slot
			Debugger.debug("Getting slot values - " + searchExtrinsic.getLid()
					+ " - " + registryPath);
			return getValidSlotValues(searchExtrinsic, registryPath);
		}
	}
	
	/**
	 * Traverses down a registry path tree by looping through the dot-connected path.
	 * Uses a fun little recursion to make it happen.
	 * 
	 * @param pathList
	 * @param searchExtrinsicList
	 * @return
	 * @throws Exception
	 */
	private List<String> traverseRegistryPath(List<String> pathList, List<SearchCoreExtrinsic> searchExtrinsicList) throws Exception {
		ArrayList<String> newPathList = null;
		if (pathList.size() > 1 && !searchExtrinsicList.isEmpty()) {
			newPathList = new ArrayList<String>();
			newPathList.addAll(pathList.subList(1, pathList.size()));
			
			for (SearchCoreExtrinsic searchExtrinsic : searchExtrinsicList) {
					return traverseRegistryPath(newPathList, 
							this.registryHandler.getAssociationsByReferenceType(
									searchExtrinsic, pathList.get(0)));
			}
		} else if (pathList.size() == 1 && !searchExtrinsicList.isEmpty()) {	// Let's get some slot values
			List<String> slotValueList = new ArrayList<String>();
			for (SearchCoreExtrinsic searchExtrinsic : searchExtrinsicList) {
				slotValueList.addAll(getValidSlotValues(searchExtrinsic, pathList.get(0)));
			}
			return slotValueList;
		}
		return new ArrayList<String>();
	}
	
	/**
	 * Search Service requires that when an association reference slot is queried from the index,
	 * it returns a lidvid, if available. In the case where a reference value is a lid instead of
	 * a lidvid, we will query the registry and attempt to build a lidvid from the most recent
	 * version of the product.
	 * 
	 * If the slot in question is not an association, then it is just passed along like a hot potato.
	 * 
	 * @param searchExt
	 * @param slotName
	 * @return
	 * @throws Exception
	 */
	private List<String> getValidSlotValues(SearchCoreExtrinsic searchExt, String slotName) throws Exception {
		List<String> slotValues = new ArrayList<String>();
		if (searchExt.getSlotValues(slotName) != null) {
			slotValues.addAll(searchExt.getSlotValues(slotName));
			if (searchExt.slotIsAssociationReference(slotName)) {		// If slot is an association reference
				if (!searchExt.hasValidAssociationValues()) {		// If associations have values not are not lidvids
																	// We will have to make the lidvids for them
					//Debugger.debug("-- INVALID ASSOCIATION VALUE FOUND for " + searchExt.getLid() + " - " + slotName);
					List<String> newSlotValues = new ArrayList<String>();
					SearchCoreExtrinsic assocSearchExt;
					for(String lid : slotValues) {
						assocSearchExt = this.registryHandler.getExtrinsicByLidvid(lid);
						if (assocSearchExt != null) {	// if association is found, add the lidvid to slot values
							//Debugger.debug("New slot value: " + assocSearchExt.getLidvid());
							newSlotValues.add(assocSearchExt.getLidvid());
						} else {
							//Debugger.debug("Association not found for new slot value, adding lid instead : " + lid);
							newSlotValues.add(lid);
						}
					}
					return newSlotValues;
				}
			}
		}
		
		return slotValues;
	}

	/**
	 * Extract the attribute/slot/association from the String
	 * specified and query the Registry for the value to replace
	 * it wit.
	 * 
	 * @param str			input string from config
	 * @param extObject
	 * @return				the string with the embedded attribute/slot/association
	 * 						to be queried, replaced with the value from the Registry
	 * @throws Exception
	 */
	private String checkForSubstring(String str, SearchCoreExtrinsic extObject)
			throws Exception {
		int start, end; 
		String key;

		List<String> valueList = new ArrayList<String>();
		while (str.contains("{")) {
			start = str.indexOf("{");
			end = str.indexOf("}", start);
			key = str.substring(start + 1, end);

			valueList = registryPathHandler(key, extObject);			
			if (valueList != null && !valueList.isEmpty()) {
				str = str.replace("{" + key + "}",URLEncoder.encode(valueList.get(0), "UTF-8"));
			} else {
				str = str.replace("{" + key + "}", "Unknown");
			}
		}
		return str;
	}
	
	/**
	 * Return a registry handler object to be used for querying for data. The primary and secondary registries
	 * have already been set when this class was instantiated. Depending on whether or not the {@link DataSource} append
	 * flag is set will determine if the original primary/secondary registries are maintained or cleared prior to
	 * instantiating a new Registry Handler object.
	 * 
	 * @param product
	 * @return
	 */
	private RegistryHandler getRegistryHandler(Product product) {
		ArrayList<String> primaryRegistries = new ArrayList<String>();
		primaryRegistries.addAll(this.primaryRegistries);
		
		ArrayList<String> secondaryRegistries = new ArrayList<String>();
		secondaryRegistries.addAll(this.secondaryRegistries);
		
		if (this.product.getSpecification().getDataSources() != null) {
			for (DataSource source : this.product.getSpecification().getDataSources().getSource()) {
				if (source.getSourceType().equals(SourceType.REGISTRY)) {
					if (source.getPriority().equals(SourcePriority.PRIMARY)) {
						if (!source.isAppend()) {
							primaryRegistries.clear();
						}
						primaryRegistries.addAll(source.getUrl());
					} else if (source.getPriority().equals(SourcePriority.SECONDARY)) {
						if (!source.isAppend()) {
							secondaryRegistries.clear();
						}
						secondaryRegistries.addAll(source.getUrl());
					}
				}
			}
		}
		
		return new RegistryHandler(primaryRegistries, secondaryRegistries, this.queryMax);
	}

	/**
	 * @return the queryMax
	 */
	public int getQueryMax() {
		return this.queryMax;
	}

	/**
	 * @param queryMax the queryMax to set
	 */
	public void setQueryMax(int queryMax) {
		this.queryMax = queryMax;
	}

}
