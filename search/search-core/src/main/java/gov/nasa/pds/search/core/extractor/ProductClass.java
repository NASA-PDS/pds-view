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

package gov.nasa.pds.search.core.extractor;

import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.extractor.registry.MappingTypes;
import gov.nasa.pds.search.core.extractor.registry.RegistryAttributes;
import gov.nasa.pds.search.core.extractor.registry.SearchCoreExtrinsic;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;
import gov.nasa.pds.search.core.schema.CoreConfigReader;
import gov.nasa.pds.search.core.schema.Field;
import gov.nasa.pds.search.core.schema.Product;
import gov.nasa.pds.search.core.stats.SearchCoreStats;
import gov.nasa.pds.search.core.util.Debugger;
import gov.nasa.pds.search.core.util.Utility;
import gov.nasa.pds.search.core.util.XMLWriter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author pramirez
 * @author jpadams
 * 
 */
public class ProductClass {

	/** Default start number to be appended to output files */
	private static final int OUT_SEQ_START = 10000;
	
	/** In order to scale for very large registry products, need to page the registry output **/
	private static final int QUERY_PAGE_MAX = 500;

	/** Product class name **/
	//private String classname;

	/** User-specified output directory **/
	private File outputDir;

	// TODO Refactor to use JAXB bindings
	/** Collection of all search fields **/
	//private SearchFields searchFields;
	
	/** Data Product object from JAXB Bindings **/
	private Product product;

	// TODO Refactor this to start outputtin to XML immediately.
	// Some PSA Data has such large description that it kills the buffers
	/** Map of all values for desired search fields **/
	private Map<String, List<String>> finalVals;
	
	/** Logical Identifier for the current data object **/
	private String lid;

	/** Output logger **/
	private static Logger log = Logger.getLogger(ProductClass.class.getName());

	/** PrintWriter for outputting XML files **/
	private PrintWriter writer;

	/** Map of all associations for the current data product **/
	private Map<String, List<ExtrinsicObject>> associationMap;

	/** The URL of the desired registry to query. **/
	private String registryUrl;

	/** The maximum records returned from registry query, as specified by user. **/
	private int queryMax;

	/**
	 * Initialize global variables used throughout
	 * 
	 * @param writer		writer for run log
	 * @param name			class of catalog to be extracted
	 * @param file			object type XML config file
	 * @param registryUrl	URL for registry to query
	 * @param queryMax		maximum number of queried results
	 */
	public ProductClass(PrintWriter writer, String name, File outputDir,
			 String registryUrl, int queryMax) {
	    log.log(new ToolsLogRecord(ToolsLevel.FINE, "Extracting from "
	    		+ registryUrl));

		this.writer = writer;

		//this.classname = name;
		this.registryUrl = registryUrl;
		
		if (queryMax > -1) {
			this.queryMax = queryMax;
		} else {
			this.queryMax = Constants.QUERY_MAX;
		}

		//this.missingSlotsMap = new HashMap<String, List<String>>();
		//this.missingAssocTargets = new ArrayList<String>();

		this.finalVals = new HashMap<String, List<String>>();
		this.associationMap = new HashMap<String, List<ExtrinsicObject>>();
		
		this.product = null;

		//this.log.fine("Class name: " + classname);
	}
	
	public ProductClass(PrintWriter writer, File outputDir,
			String registryUrl, int queryMax) {
	    log.log(new ToolsLogRecord(ToolsLevel.FINE, "Extracting from "
	    		+ registryUrl));

		this.writer = writer;
		this.outputDir = outputDir;

		//this.classname = name;
		this.registryUrl = registryUrl;
		
		if (queryMax > -1) {
			this.queryMax = queryMax;
		} else {
			this.queryMax = Constants.QUERY_MAX;
		}

		//this.missingSlotsMap = new HashMap<String, List<String>>();
		//this.missingAssocTargets = new ArrayList<String>();

		this.finalVals = new HashMap<String, List<String>>();
		this.associationMap = new HashMap<String, List<ExtrinsicObject>>();
		
		this.product = null;

		//this.log.fine("Class name: " + classname);
	}

	/**
	 * Clears the maps used to store values for the current product class.
	 */
	private void clearMaps() {
		this.finalVals.clear();
		this.associationMap.clear();
	}

	/**
	 * Driving method for querying data from the registry
	 * 
	 * @param coreConfig
	 * @return
	 * @throws ProductClassException
	 */
	public List<String> query(File coreConfig) throws ProductClassException, SearchCoreFatalException {
		// log.fine("confdir : " + confDir);
		List<String> instkeys = new ArrayList<String>();
		int outSeqNum = 0;
		
		//this.searchFields = new SearchFields(this.classFilename);
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
		File registryOutputDir = null;
		try {
			registryOutputDir = new File(this.outputDir, this.product.getSpecification().getTitle());
			FileUtils.forceMkdir(registryOutputDir);
		} catch (IOException e) {
		    log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
		    		this.outputDir + this.product.getSpecification().getTitle()));
		}
		
		outSeqNum = getOutputSeqNumber(registryOutputDir);
	    log.log(new ToolsLogRecord(ToolsLevel.DEBUG,
	    		"Starting with outSeqNum: " + outSeqNum));

	    // Make sure registry URL works
	    checkRegistryExists(this.registryUrl);
	    
		try {
			List<ExtrinsicObject> extList = getExtrinsicsByObjectType();
			
			for (ExtrinsicObject extObj : extList) {
				clearMaps();

				outSeqNum++;

				// Get class properties
				setColumnProperties(extObj);

				XMLWriter writer = new XMLWriter(this.finalVals, this.outputDir,
						outSeqNum, this.product.getSpecification().getTitle());
				writer.write();

				Debugger.debug("----- Finished for " + extObj.getLid() + " -----\n\n");
			}

			displayWarnings();
		} catch (Exception ex) {
			throw new ProductClassException("Exception "
					+ ex.getClass().getName() + ex.getMessage());
		}
		instkeys.addAll(this.finalVals.keySet());
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
	private void setColumnProperties(ExtrinsicObject extObject)
			throws ProductClassException {
		try {
			/* Initialize local variables */
			//String currName, currType, currVal;
			SearchCoreExtrinsic searchExtrinsic = new SearchCoreExtrinsic(extObject);
			List<String> valueList;

			setIdentifiers(searchExtrinsic);
			
			String value;
			
			// Loop through class results beginning from top
			for (Field field : this.product.getIndexFields().getField()) {
				//TODO uncomment to use suffixes for field names
				String schemaName = field.getName(); //+ SolrSchemaField.getSuffix(field.getType());
				
				// Handle registry path
				if ((value = field.getRegistryPath()) != null) {
					this.finalVals.put(schemaName, registryPathHandler(value, searchExtrinsic));
				} else if ((value = field.getOutputString()) != null) {	// Handle outputString
					valueList = new ArrayList<String>();
					valueList.add(checkForSubstring(value, searchExtrinsic));
					this.finalVals.put(schemaName, valueList);
				}
				
				
				// Check if field is a slot
				
				// Check if field is an association
				
				// Else output as is
				
				// Substitute into output string if needed
						
						
			}
			
			//System.exit(1);
			/*for (int i = 0; i < this.searchFields.getNumAttr(); i++) {
				currName = this.searchFields.getName(i);
				currType = this.searchFields.getType(i);
				currVal = this.searchFields.getValue(i);

				valArray = new ArrayList<String>();
				
				if (currType.equals(MappingTypes.OUTPUT)) {
					valArray = new ArrayList<String>();
					valArray.add(currVal);
					this.finalVals.put(currName, valArray);

					// TODO should refactor this entire else-if and create a
					// separate RegistryAttribute class that handles all those
					// search parameters that fit the specific criteria
					// very similar code is being replicated in the
					// RegistrySlots class
				} else if (currType.equals(MappingTypes.ATTRIBUTE)) {
					valArray.add(RegistryAttributes.getAttributeValue(currVal,
							extObject));
					this.finalVals.put(currName, valArray);
				} else if (currType.equals(MappingTypes.SLOT)) {
					String tval;
					try {
						for (String value : extObject.getSlot(currVal)
								.getValues()) {
							tval = remNull(value);
							// tval1 = tval1.trim();
							valArray.add(cleanText(checkRef(tval, currVal)));
						}
					} catch (NullPointerException e) {
						recordMissingSlot(extObject, currVal);
					}
					this.finalVals.put(currName, valArray);
				} else if (currType.equals(MappingTypes.ASSOCIATION)) {
					String[] values = currVal.split("\\.");

					String assocType = values[0];

					Debugger.debug("Getting associations for " + extObject.getLid()
							+ " - " + currVal);
					this.finalVals.put(
							currName,
							getAssociatedSlotValues(assocType, values[1],
									extObject));

				} else if (currType.equals(MappingTypes.SUBSTRING)) {
					valArray = new ArrayList<String>();
					valArray.add(setSubstring(currVal, extObject));
					this.finalVals.put(currName, valArray);
				} else {
					throw new InvalidProductClassException(
							"Unknown Mapping Type in Search Core Config - "
									+ currType
									+ ".  Please use mapping types designated in API.");
				}
			}
			*/

			/*
			 * try { recordMissingSlots(this.slots); } catch
			 * (NullPointerException e) {
			 * System.err.println("NullPointerException - " + this.lid); }
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ProductClassException("Exception "
					+ ex.getClass().getName() + ex.getMessage());
		}
	}
	
	private List<String> registryPathHandler(String value, ExtrinsicObject extObj) throws Exception {
		List<String> slotValueList;
		String[] pathArray;
		
		List<String> valueList = new ArrayList<String>();
		
		SearchCoreExtrinsic searchExtrinsic = new SearchCoreExtrinsic(extObj);
		
		if ((pathArray = value.split("\\.")).length > 1) {
			
			//String assocType = values[0];

			Debugger.debug("Association found. Traversing path - " + extObj.getLid()
					+ " - " + value);
			return traverseRegistryPath(Arrays.asList(pathArray), Arrays.asList(extObj));
		} else if (!(slotValueList = searchExtrinsic.getSlotValues(value)).isEmpty()) {	// Field is a slot
			Debugger.debug("Getting slot values - " + searchExtrinsic.getLid()
					+ " - " + value);
			for (String slotValue : slotValueList) {
				valueList.add(checkForReference(checkForNull(slotValue), value));
			}
			
			return valueList;
		} // Else does not matter because the missing slot was recorded in the SearchCoreExtrinsic object
		
		return valueList;
	}
	
	private List<String> traverseRegistryPath(List<String> pathList, List<ExtrinsicObject> searchExtrinsicList) throws Exception {
		//for (int i=0; i < pathArray.length-1; i++) {
			//slotList = getAssociatedSlotValues(pathArray[i], pathArray[i+1],
			//		searchExtrinsic);
		List<String> newPathList = null;
		if (pathList.size() > 1) {
			newPathList = new ArrayList<String>();
			newPathList.addAll(pathList.subList(1, pathList.size()));
			
			for (ExtrinsicObject extObj : searchExtrinsicList) {
				return traverseRegistryPath(newPathList, getAssociationsByReferenceType(extObj, pathList.get(0)));
			}
		} else {
			List<String> slotValueList = new ArrayList<String>();
			for (ExtrinsicObject extObj : searchExtrinsicList) { 
				SearchCoreExtrinsic searchExtrinsic = new SearchCoreExtrinsic(extObj);
				slotValueList.addAll(searchExtrinsic.getSlotValues(pathList.get(0)));
			}
			return slotValueList;
		}
		return null;
	}

	/**
	 * Get the ExtrinsicObjects from the given object type.
	 * 
	 * @param objectType	@see gov.nasa.pds.registry.model.ExtrinsicObject
	 * @return				list of ExtrinsicObject for given objectType
	 * @throws Exception	thrown if there are issues with the RegistryClient
	 */
	private List<ExtrinsicObject> getExtrinsicsByObjectType()
			throws Exception {
		// Build the filter
		//ExtrinsicFilter filter;
		Debugger.debug("----- " + this.product.getSpecification().getRegistryObjectType() + " -----");
//		ExtrinsicFilter filter = new ExtrinsicFilter.Builder().objectType(
//				this.searchFields.getObjectType()).name(this.searchFields.getObjectName()).build();
		
		ExtrinsicFilter filter = new ExtrinsicFilter.Builder()
			.objectType(this.product.getSpecification().getRegistryObjectType())
			.name(this.product.getSpecification().getRegistryObjectName())
			.build();

		// Create the query
		RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>()
				.filter(filter).build();
		
		List<ExtrinsicObject> results = null;
		try {
			RegistryClient client = new RegistryClient(this.registryUrl);

			results = new ArrayList<ExtrinsicObject>();
			
			PagedResponse<ExtrinsicObject> pr;
			
			int pageLength = Math.min(this.queryMax, QUERY_PAGE_MAX);

			for (int start=1; start<this.queryMax+pageLength; start+=pageLength) {
				Debugger.debug("start: " + start + ", queryPageMax: " + QUERY_PAGE_MAX + ", pageLength: " + pageLength);
				
				if (start+pageLength > this.queryMax) {
					pr = client.getExtrinsics(query, start, this.queryMax-start+1);
				} else {
					pr = client.getExtrinsics(query, start, pageLength);
				}
				
				// Examine the results of the query to grab the latest product for
				// each ExtrinsicObject
				//if (pr.getNumFound() != 0 ) {
				if (pr.getResults().size() != 0 ) {
					List<String> lidList = new ArrayList<String>();
					String lid;
					for (ExtrinsicObject extrinsic : pr.getResults()) {
						lid = extrinsic.getLid();
						Debugger.debug("\n\n----- " + lid + " -----");
	
						// Use list to verify we haven't already included this
						// product in the results
						if (!lidList.contains(lid) && lid != null) {
							results.add(client.getLatestObject(lid,
									ExtrinsicObject.class));
							lidList.add(lid);
						}
					}
					lidList.clear();
				} else {
					Debugger.debug("\n\n No More Results Found \n\n");
					results.addAll(pr.getResults());
					break;
				}
			}
			return results;
		} catch (RegistryServiceException rse) {
			// Ignore. Nothing found.
		} catch (RegistryClientException rce) {
			throw new Exception(rce.getMessage());
		}
		return null;
	}

	/**
	 * Get the ExtrinsicObjects associated with the current object
	 * being queried.
	 * 
	 * @param lidvid	identifies the ExtrinsicObject list to be queried for
	 * @return			list of ExtrinsicObjects
	 * @throws Exception
	 */
	public List<ExtrinsicObject> getExtrinsicsByLidvid(String lidvid)
			throws Exception {
		// Build the filter
		ExtrinsicFilter filter = null;
		String assocLid = null;
		String version = null;

		List<String> lidList = Arrays.asList(lidvid.split("::"));
		assocLid = lidList.get(0);

		Debugger.debug("Associated LID: " + assocLid);

		if (lidList.size() > 1) {
			version = lidList.get(1);
		} else if (lidList.size() == 0) { // Handles lidvids with bad format (:
											// instead of ::)
			lidList = Arrays.asList(lidvid.split(":"));
			assocLid = lidvid.substring(0, lidvid.lastIndexOf(":"));
			version = lidList.get(lidList.size() - 1);

			this.log.warning("***** BAD LIDVID - " + assocLid + " -- "
					+ version);
		}
		
		// TODO This should be refactored to do paging similar to getExtrinsicsByObjectType 
		// 		Also, this needs to always query ALL extrinsics with this lid in order to
		//		ensure that the version is found
		List<ExtrinsicObject> results = new ArrayList<ExtrinsicObject>();
		try {
			RegistryClient client = new RegistryClient(this.registryUrl);
			// securityContext, user, password);
			

			if (version != null) {
				filter = new ExtrinsicFilter.Builder().lid(assocLid).build();

				// Create the query
				RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>()
						.filter(filter).build();
				PagedResponse<ExtrinsicObject> pr = client.getExtrinsics(query, 1, QUERY_PAGE_MAX);
				 
				// Examine the results of the query
				// Looping through the associated extrinsic objects
				// Since we need to search through the slots for the version id,
				// COULD create RegistrySlots object because that is what is of
				// interest to us.
				// So we wouldn't have to loop through the slots twice.
				// But we would then have to store a map that we may just throw
				// away.
				Slot versionIdSlot = null;
				if (pr.getNumFound() != 0) {
					for (ExtrinsicObject extrinsic : pr.getResults()) {
						// Product version should only have 1 value, check if it is
						// the version we want
						versionIdSlot = extrinsic.getSlot(Constants.VERSION_ID_SLOT);
						//Debugger.debug("version_id: " + versionIdSlot.getValues().get(0));
						if (versionIdSlot != null) {
							if (versionIdSlot.getValues().get(0).equals(version)) {
								Debugger.debug("Adding associated extrinsic - "
										+ extrinsic.getLid());
								results.add(extrinsic);
								return results;
							}
						}
					}
				}
			} else {
				ExtrinsicObject extrinsic = client.getLatestObject(assocLid, ExtrinsicObject.class);
				Debugger.debug("Adding associated extrinsic - " + extrinsic.getLid());
				results.add(extrinsic);
				return results;
			}
		} catch (RegistryServiceException rse) {
			Debugger.debug("LID not found: " + assocLid);
		} catch (RegistryClientException rce) {
			throw new Exception(rce.getMessage());
		}
		return results;
	}

	/**
	 * Get the slots from the Associated Extrinsic Object.
	 * 
	 * @param assocType
	 * @param slotName
	 * @param extObject
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	private List<String> getAssociatedSlotValues(String referenceSlot,
			String assocSlotName, SearchCoreExtrinsic extObject) throws Exception {
		String tval;
		List<String> valueList = new ArrayList<String>();
		SearchCoreExtrinsic searchExtrinsic;
		if (setAssociation(referenceSlot, extObject)) { // Add association type to
													// association map (if
													// needed)
			for (ExtrinsicObject assocExtObj : this.associationMap.get(referenceSlot)) { // Iterate through the associations for
										// the given association type
				searchExtrinsic = new SearchCoreExtrinsic(assocExtObj);
				valueList.addAll(searchExtrinsic.getSlotValues(assocSlotName));
				
				// If slotName is not an attribute in the registry, search the ExtrinsicObject slots
				//if (!RegistryAttributes.isAttribute(slotName)) {
				/*if (!RegistryAttributes.isAttribute(assocSlotName)) {
					try {
						for (String slotValue : assocExtObj.getSlot(assocSlotName)
								.getValues()) { // Get the associated object
												// slot values
							tval = checkForNull(slotValue);
							if (!valArray.contains(tval)) {
								valArray.add(tval);
							}
						}
					} catch (NullPointerException e) { // Occurs when no slot is found
						recordMissingSlot(assocExtObj, assocSlotName);
					}
				} else {
					valArray.add(RegistryAttributes.getAttributeValue(assocSlotName,
							assocExtObj));
				}*/

			}
		} else {
			valueList.add("UNK");
		}
		return valueList;
	}

	/**
	 * Map the association type (slot name) with the list of associated
	 * ExtrinsicObjects.
	 * 
	 * @param assocType	slot name for the association (i.e. target_ref)
	 * @param extObject	object to find associations for
	 * @return
	 * @throws Exception
	 */
	private boolean setAssociation(String assocType, SearchCoreExtrinsic extObject)
			throws Exception {
		// TODO We may be able to refactor this and just get the slot values
		//		for the association type since it is just a slot, and then
		//		do the rest of this
		
		// TODO Create association cache class and hold some threshold of
		// associations for specific assocTypes like node, mission, target
		if (!this.associationMap.containsKey(assocType)) { 
			List<ExtrinsicObject> assocExtObjList = getAssociationsByReferenceType(
					extObject, assocType);
			if (!assocExtObjList.isEmpty()) {
				this.associationMap.put(assocType, assocExtObjList);
			} else {
				SearchCoreStats.addMissingAssociation(extObject.getLid(), assocType);
				return false;
			}
		}

		return true;
	}

	/**
	 * Query the associated objects and map the objects to their slots.
	 * 
	 * @param guid
	 * @param referenceType
	 * @return
	 * @throws Exception
	 */
	private List<ExtrinsicObject> getAssociationsByReferenceType(
			ExtrinsicObject extObject, String referenceType) 
					throws Exception {

		Slot assocObjSlot = extObject.getSlot(referenceType);

		List<ExtrinsicObject> assocExtObjList = new ArrayList<ExtrinsicObject>();
		
		// Get list of associations for specific association type
		// for (Association association : (List<Association>) assocResponse
		if (assocObjSlot != null) {
			for (String assocLidVid : assocObjSlot.getValues()) {
				Debugger.debug("Associated lidvid - " + assocLidVid);
				List<ExtrinsicObject> extList = getExtrinsicsByLidvid(assocLidVid);

				if (extList.size() != 0) {
					assocExtObjList.addAll(extList);
				} else {
					SearchCoreStats.addMissingAssociationTarget(extObject.getLid(), assocLidVid);
				}
			}
		} else {
			SearchCoreStats.addMissingSlot(extObject.getLid(), referenceType);
		}
		return assocExtObjList;
	}

	/**
	 * Record all missing slots
	 * 
	 * @param extObject	ExtrinisicObject with a missing slot
	 * @param slot		slot name that is missing
	 */
	/*private void recordMissingSlot(ExtrinsicObject extObject, String slot) {
		List<String> slotList;
		String objectType = extObject.getObjectType();
		
		// Verify object type has not been included in missing 
		// slots map If it has, get the list
		if (this.missingSlotsMap.containsKey(objectType)) {
			slotList = this.missingSlotsMap.get(objectType);
		} else { // Else create a new ArrayList
			slotList = new ArrayList<String>();
		}
		slotList.add(this.lid + " - " + slot);
		this.missingSlotsMap.put(objectType, slotList);
	}*/

	/**
	 * Display all missing associations and slots
	 */
	private void displayWarnings() {
		String out = "";

		/*if (!this.missingAssocTargets.isEmpty()) {
			out += "Missing the following Association Targets:\n";
			for (String missingAssociation : this.missingAssocTargets) {
				out += missingAssociation + "\n";
			}
		}*/

		/*if (!this.missingSlotsMap.isEmpty()) {
			out += "Missing the following Registry Slots:\n";
			for (String objectType : this.missingSlotsMap.keySet()) {
				out += "--- " + objectType + " ---\n";
				for (String slot : this.missingSlotsMap.get(objectType)) {
					out += "    " + slot + "\n";
				}
			}
			out += "\n\n";
		}*/

		if (!out.equals("")) {
			this.writer.println(out);
			this.log.warning(out);
		}
	}

	/**
	 * Sets the logical identifier
	 * @param object
	 */
	private void setIdentifiers(RegistryObject object) {
		this.lid = object.getLid();
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
		//String tval = "", 
		String key;

		List<String> valueList = new ArrayList<String>();
		while (str.contains("{")) {
			start = str.indexOf("{");
			end = str.indexOf("}");
			key = str.substring(start + 1, end);

			
			// TODO ASSUMPTION: These substring slots/associated slots will
			// only have 1 value - may want to specify multiple values here
			// instead
			/*if (!key.contains(".")) { // If key is not an associated object
				// Get the first slot value
				tval = extObject.getSlot(key).getValues().get(0);
			} else { // Association mapping
				String[] values = key.split("\\.");
				tval = getAssociatedSlotValues(values[0], values[1],
						extObject).get(0);
			}
			str = str.replace('#', '&').replace("{" + key + "}",URLEncoder.encode(((tval == null) ? "" : tval), "UTF-8"));
			*/
			valueList = registryPathHandler(key, extObject);

			
			if (valueList != null && valueList.size() > 0) {
				str = str.replace("{" + key + "}",URLEncoder.encode(valueList.get(0), "UTF-8"));
			}
		}
		return str;
	}
	
	/**
	 * Check if the value comes from an association field (*_ref).
	 * If so, need to verify it is a lidvid otherwise query registry
	 * for version number to append to lid.
	 * 
	 * @param value
	 * @param registryRef
	 * @return
	 * @throws RegistryClientException 
	 * @throws RegistryServiceException 
	 */
	private String checkForReference(String value, String registryRef) throws RegistryClientException {
		if (registryRef.contains("_ref")) {
			try {
				if (!value.contains("::")) {
					RegistryClient client = new RegistryClient(this.registryUrl);
					String version = client.getLatestObject(value, ExtrinsicObject.class).getSlot(Constants.VERSION_ID_SLOT).getValues().get(0);
					value += "::" + version;
				}
			} catch (RegistryServiceException e) {	// Case when association is not found, append version 1.0
				// If ref isn't found, do not append a version.
			}
		}
		
		return value;
	}

	/**
	 * Removes extra whitespace and line breaks from the description-like fields
	 * 
	 * @param text
	 * @return
	 */
	@Deprecated
	private String cleanText(String text) {
		return text.trim().replace("\n", "<br />").replaceAll("    *", "   ");
	}

	/**
	 * Remove String Nulls
	 */
	private String checkForNull(String s1) {
		if (s1 == null) {
			return "UNK";
		}
		return s1;
	}
	
	private String checkRegistryExists(String url) throws ProductClassException {
		if (Utility.urlExists(url)) {
			return url;
		} else {
		    log.log(new ToolsLogRecord(ToolsLevel.SEVERE,
		    		url + " cannot be found."));
			throw new ProductClassException(url + " Not Found. Verify Registry Exists.");
		}
	}

	/**
	 * @return the lid
	 */
	public String getLid() {
		return lid;
	}

	/**
	 * @param lid the lid to set
	 */
	public void setLid(String lid) {
		this.lid = lid;
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
