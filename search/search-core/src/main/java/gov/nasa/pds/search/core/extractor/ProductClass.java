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
import gov.nasa.pds.search.core.extractor.registry.MappingTypes;
import gov.nasa.pds.search.core.extractor.registry.RegistryAttributes;
import gov.nasa.pds.search.util.Debugger;
import gov.nasa.pds.search.util.XMLWriter;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
	private String classname;

	/** User-specified product class config file **/
	private String classFilename;

	/** Collection of all search fields **/
	private SearchFields searchFields;

	/** Map of all values for desired search fields **/
	private Map<String, List<String>> finalVals;
	
	/** Logical Identifier for the current data object **/
	private String lid;

	/** Output logger **/
	private Logger log = Logger.getLogger(this.getClass().getName());

	/** PrintWriter for outputting XML files **/
	private PrintWriter writer;

	/** Map of all associations for the current data product **/
	private Map<String, List<ExtrinsicObject>> associationMap;

	/** Map of missing slots mapped to the LID **/
	private Map<String, List<String>> missingSlotsMap;

	/** List of Associations where target extrinsic is not found */
	private List<String> missingAssocTargets;

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
	public ProductClass(PrintWriter writer, String name, String file,
			String registryUrl, int queryMax) {
		this.log.fine("In Generic Extractor");

		this.writer = writer;

		this.classname = name;
		this.classFilename = file;
		this.registryUrl = registryUrl;
		if (queryMax > -1) {
			this.queryMax = queryMax;
		} else {
			this.queryMax = Constants.QUERY_MAX;
		}

		this.missingSlotsMap = new HashMap<String, List<String>>();
		this.missingAssocTargets = new ArrayList<String>();

		this.finalVals = new HashMap<String, List<String>>();
		this.associationMap = new HashMap<String, List<ExtrinsicObject>>();

		this.log.fine("Class name: " + classname);
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
	 * @param outputDir
	 * @return
	 * @throws ProductClassException
	 */
	public List<String> query(File outputDir) throws ProductClassException {
		// log.fine("confdir : " + confDir);
		List<String> instkeys = new ArrayList<String>();
		int outSeqNum = getOutputSeqNumber(outputDir);
		// int outSeqNum = OUT_SEQ_START;

		Debugger.debug("Starting with outSeqNum: " + outSeqNum);

		this.searchFields = new SearchFields(this.classFilename);

		try {
			List<ExtrinsicObject> extList = getObjectTypeExtrinsics();
			
			for (ExtrinsicObject extObj : extList) {
				clearMaps();

				outSeqNum++;

				// Get class properties
				setColumnProperties(extObj);

				XMLWriter writer = new XMLWriter(this.finalVals, outputDir,
						outSeqNum, this.classname);
				writer.write();

				Debugger.debug("----- Finished for " + extObj.getLid() + " -----\n\n");
			}

			displayWarnings();
		} catch (Exception ex) {
			throw new ProductClassException("Exception "
					+ ex.getClass().getName() + ex.getMessage());
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
			String currName, currType, currVal;

			List<String> valArray;

			setIdentifiers(extObject);

			// Loop through class results beginning from top
			for (int i = 0; i < this.searchFields.getNumAttr(); i++) {
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

	/**
	 * Get the ExtrinsicObjects from the given object type.
	 * 
	 * @param objectType	@see gov.nasa.pds.registry.model.ExtrinsicObject
	 * @return				list of ExtrinsicObject for given objectType
	 * @throws Exception	thrown if there are issues with the RegistryClient
	 */
	private List<ExtrinsicObject> getObjectTypeExtrinsics()
			throws Exception {
		// Build the filter
		//ExtrinsicFilter filter;
		Debugger.debug("----- " + this.searchFields.getObjectName() + " -----");
		ExtrinsicFilter filter = new ExtrinsicFilter.Builder().objectType(
				this.searchFields.getObjectType()).name(this.searchFields.getObjectName()).build();

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
	private List<ExtrinsicObject> getAssociatedExtrinsics(String lidvid)
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
		
		List<ExtrinsicObject> results = new ArrayList<ExtrinsicObject>();
		try {
			RegistryClient client = new RegistryClient(this.registryUrl);
			// securityContext, user, password);
			

			if (version != null) {
				filter = new ExtrinsicFilter.Builder().lid(assocLid).build();

				// Create the query
				RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>()
						.filter(filter).build();
				PagedResponse<ExtrinsicObject> pr = client.getExtrinsics(query, 1, this.queryMax);
				 
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

						if (versionIdSlot != null) {
							if (versionIdSlot.getValues().get(0).equals(version)) {
								Debugger.debug("Adding associated extrinsic - "
										+ extrinsic.getLid());
								results.add(extrinsic);
							}
						}
					}
				}
			} else {
				ExtrinsicObject extrinsic = client.getLatestObject(assocLid, ExtrinsicObject.class);
				Debugger.debug("Adding associated extrinsic - " + extrinsic.getLid());
				results.add(extrinsic); 	
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
	private List<String> getAssociatedSlotValues(String assocType,
			String slotName, ExtrinsicObject extObject) throws Exception {
		String tval;
		List<String> valArray = new ArrayList<String>();
		if (setAssociation(assocType, extObject)) { // Add association type to
													// association map (if
													// needed)
			for (ExtrinsicObject assocExtObj : this.associationMap.get(assocType)) { // Iterate through the associations for
										// the given association type
				
				// If slotName is not an attribute in the registry, search the ExtrinsicObject slots
				if (!RegistryAttributes.isAttribute(slotName)) { 
					try {
						for (String slotValue : assocExtObj.getSlot(slotName)
								.getValues()) { // Get the associated object
												// slot values
							tval = cleanText(remNull(slotValue));
							if (!valArray.contains(tval)) {
								valArray.add(tval);
							}
						}
					} catch (NullPointerException e) { // Occurs when no slot is found
						recordMissingSlot(assocExtObj, slotName);
					}
				} else {
					valArray.add(RegistryAttributes.getAttributeValue(slotName,
							assocExtObj));
				}

			}
		} else {
			valArray.add("UNK");
		}
		return valArray;
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
	private boolean setAssociation(String assocType, ExtrinsicObject extObject)
			throws Exception {
		// TODO We may be able to refactor this and just get the slot values
		//		for the association type since it is just a slot, and then
		//		do the rest of this
		
		// TODO Create association map class and hold some threshold of
		// associations for specific assocTypes like node, mission, target
		if (!this.associationMap.containsKey(assocType)) { 
			List<ExtrinsicObject> assocExtObjList = getAssociatedObjects(
					extObject, assocType);
			if (!assocExtObjList.isEmpty()) {
				this.associationMap.put(assocType, assocExtObjList);
			} else {
				this.missingAssocTargets.add(this.lid + " - " + assocType);
				return false;
			}
		}

		return true;
	}

	/**
	 * Query the associated objects and map the objects to their slots.
	 * 
	 * @param guid
	 * @param assocType
	 * @return
	 * @throws Exception
	 */
	private List<ExtrinsicObject> getAssociatedObjects(
			ExtrinsicObject extObject, String assocType) 
					throws Exception {

		Slot assocObjSlot = extObject.getSlot(assocType);

		List<ExtrinsicObject> assocExtObjList = new ArrayList<ExtrinsicObject>();
		
		// Get list of associations for specific association type
		// for (Association association : (List<Association>) assocResponse
		if (assocObjSlot != null) {
			for (String assocLidVid : assocObjSlot.getValues()) {
				Debugger.debug("Associated lidvid - " + assocLidVid);
				List<ExtrinsicObject> extList = getAssociatedExtrinsics(assocLidVid);

				if (extList.size() != 0) {
					assocExtObjList.addAll(extList);
				} else {
					this.missingAssocTargets.add(this.lid + " - " + assocType
							+ " - " + assocLidVid);
				}
			}
		} else {
			recordMissingSlot(extObject, assocType);
		}
		return assocExtObjList;
	}

	/**
	 * Record all missing slots
	 * 
	 * @param extObject	ExtrinisicObject with a missing slot
	 * @param slot		slot name that is missing
	 */
	private void recordMissingSlot(ExtrinsicObject extObject, String slot) {
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
	}

	/**
	 * Display all missing associations and slots
	 */
	private void displayWarnings() {
		String out = "";

		if (!this.missingAssocTargets.isEmpty()) {
			out += "Missing the following Association Targets:\n";
			for (String missingAssociation : this.missingAssocTargets) {
				out += missingAssociation + "\n";
			}
		}

		if (!this.missingSlotsMap.isEmpty()) {
			out += "Missing the following Registry Slots:\n";
			for (String objectType : this.missingSlotsMap.keySet()) {
				out += "--- " + objectType + " ---\n";
				for (String slot : this.missingSlotsMap.get(objectType)) {
					out += "    " + slot + "\n";
				}
			}
			out += "\n\n";
		}

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
	private String setSubstring(String str, ExtrinsicObject extObject)
			throws Exception {
		int start, end;
		String tval = "", key;

		try {
			while (str.contains("{")) {
				start = str.indexOf("{");
				end = str.indexOf("}");
				key = str.substring(start + 1, end);

				// TODO ASSUMPTION: These substring slots/associated slots will
				// only have 1 value - may want to specify multiple values here
				// instead
				if (!key.contains(".")) { // If key is not an associated object
					// Get the first slot value
					tval = extObject.getSlot(key).getValues().get(0);
				} else { // Association mapping
					String[] values = key.split("\\.");
					tval = getAssociatedSlotValues(values[0], values[1],
							extObject).get(0);
				}

				str = str.replace('#', '&').replace("{" + key + "}",URLEncoder.encode(((tval == null) ? "" : tval), "UTF-8"));
			}
		} catch (NullPointerException e) { // Associated Extrinsic Object does
											// not have the slot requested
			this.missingAssocTargets.add(this.lid + " - " + str);
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
	private String checkRef(String value, String registryRef) throws RegistryClientException {
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
	private String cleanText(String text) {
		return text;
		//return text.trim().replace("\n", "<br />").replaceAll("    *", "   ");
	}

	/**
	 * Remove String Nulls
	 */
	private String remNull(String s1) {
		if (s1 == null) {
			return "UNK";
		}
		return s1;
	}

}
