//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id$ 
//

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
import gov.nasa.pds.search.core.extractor.registry.ExtrinsicObjectSlots;
import gov.nasa.pds.search.core.extractor.registry.RegistryAttributes;
import gov.nasa.pds.search.util.XMLWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.httpclient.util.DateParseException;

/**
 * @author pramirez
 * @author jpadams
 * @version $Revision$
 * 
 */
public class ProductClass { // implements Extractor {

	private static final int OUT_SEQ_START=10000;
	
	private boolean debug = true;

	/** Product class name **/
	private String classname;
	
	/** User-specified product class config file **/
	private String classFilename;

	/** Collection of all search fields **/
	private SearchFields searchFields;

	/** Map of all values for desired search fields **/
	private Map<String, List<String>> finalVals;

	/** All slots for current queried extrinsic object **/
	//private ExtrinsicObjectSlots slots;

	/** Logical Identifier for the current data object **/
	private String lid;

	/** Output logger **/
	private Logger log = Logger.getLogger(this.getClass().getName());

	/** PrintWriter for outputting XML files **/
	private PrintWriter writer;
	
	/** Map of all associations for the current data product **/
	//private Map<String, List<ExtrinsicObjectSlots>> associationMap;
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
	 * List of associationTypes that do not exist for extrinsic object
	 */
	// private List<String> missingAssociations;

	/**
	 * Initialize global variables used throughout
	 * 
	 * @param name
	 *            - class of catalog to be extracted
	 * @param value
	 *            - name of column properties file
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
		//int outSeqNum = OUT_SEQ_START;

		debug("Starting with outSeqNum: " + outSeqNum);
		
		this.searchFields = new SearchFields(this.classFilename);

		try {

			//PagedResponse<ExtrinsicObject> response = getObjectTypeExtrinsics(ExtrinsicFilterTypes.OBJECT_TYPE, this.columns.getObjectType());
			List<ExtrinsicObject> extList = getObjectTypeExtrinsics(this.searchFields.getObjectType());

			//for (ExtrinsicObject object : (List<ExtrinsicObject>) response.getResults()) {
			for (ExtrinsicObject extObj : extList) {
				clearMaps();
				
				outSeqNum++;

				// Get class properties
				setColumnProperties(extObj);

				XMLWriter writer = new XMLWriter(this.finalVals, outputDir, outSeqNum,
						this.classname);
				writer.write();
				
				debug("----- Finished for " + extObj.getLid() + " -----\n\n");
			}

			displayWarnings();

			// rs1.close();
			// connection1.close();
		} catch (Exception ex) {
			throw new ProductClassException("Exception " + ex.getClass().getName()
					+ ex.getMessage());
		}

		return instkeys;
	}
	
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
	 * @param ExtrinsicObject - 
	 * @throws ProductClassException 
	 */
	private void setColumnProperties(ExtrinsicObject extObject) throws ProductClassException {
		try {
			/* Initialize local variables */
			String currName, currType, currVal;
			
			List<String> valArray;

			setIdentifiers(extObject);
			//this.slots = new ExtrinsicObjectSlots(extObject);
			//setAssociations(this.slots);

			// Loop through class results beginning from top
			for (int i = 0; i < this.searchFields.getNumAttr(); i++) {				
				currName = this.searchFields.getName(i);
				currType = this.searchFields.getType(i);
				currVal = this.searchFields.getValue(i);

				if (currType.equals(MappingTypes.OUTPUT)) { // Output value
															// given in XML
					valArray = new ArrayList<String>();
					valArray.add(currVal);
					this.finalVals.put(currName, valArray);

					// TODO should refactor this entire else-if and create a
					// separate RegistryAttribute class that handles all those
					// search parameters that fit the specific criteria
					// very similar code is being replicated in the
					// RegistrySlots class
				} else if (currType.equals(MappingTypes.ATTRIBUTE)) { // Specific
																		// attribute value for the extrinsic object
					
					valArray = new ArrayList<String>();
					valArray.add(RegistryAttributes.getAttributeValue(currVal, extObject));
					this.finalVals.put(currName, valArray);
				} else if (currType.equals(MappingTypes.SLOT)) { // Value maps
																	// to a
																	// specific
					valArray = new ArrayList<String>(); // slot in the current object
												// type
					String tval;
					try {
						for (String value : extObject.getSlot(currVal).getValues()) {
							 tval = remNull(value);
							// tval1 = tval1.trim();
							valArray.add(cleanText(tval));
						}
					} catch (NullPointerException e) {
						recordMissingSlot(extObject, currVal);
					}
					this.finalVals.put(currName, valArray);
				} else if (currType.equals(MappingTypes.ASSOCIATION)) { // Value
																		// maps
																		// to an
																		// associated
																		// object
					String[] values = currVal.split("\\."); // slot
					valArray = new ArrayList<String>();
					
					String assocType = values[0];
					
					debug("Getting associations for " + extObject.getLid() + " - " + currVal);
					this.finalVals.put(currName, getAssociatedSlotValues(assocType, values[1], extObject));

				} else if (currType.equals(MappingTypes.SUBSTRING)) {					
					valArray = new ArrayList<String>();
					valArray.add(setSubstring(currVal, extObject));
					this.finalVals.put(currName, valArray);
				} else {
					throw new InvalidProductClassException(
							"Unknown Mapping Type - " + currType + ".  Please use mapping types designated in API.");
				}
			}

			/*try {
				recordMissingSlots(this.slots);
			} catch (NullPointerException e) {
				System.err.println("NullPointerException - " + this.lid);
			}*/
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ProductClassException("Exception " + ex.getClass().getName()
					+ ex.getMessage());
		}
	}
	
	private List<ExtrinsicObject> getObjectTypeExtrinsics(String objectType) throws Exception {
		// Build the filter
		ExtrinsicFilter filter = new ExtrinsicFilter.Builder().objectType(objectType).build();
		
		// Create the query
		RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>()
				.filter(filter).build();
		try {
			RegistryClient client = new RegistryClient(this.registryUrl);
			// securityContext, user, password);
			
			List<ExtrinsicObject> results = new ArrayList<ExtrinsicObject>();
			PagedResponse<ExtrinsicObject> pr = client.getExtrinsics(query, 1,
					this.queryMax);
			
			// Examine the results of the query to grab the latest product for each
			// ExtrinsicObject
			String lid = "";
			List<String> lidList = new ArrayList<String>();
			if (pr.getNumFound() != 0) {
				for (ExtrinsicObject extrinsic : pr.getResults()) {
					lid = extrinsic.getLid();
					debug("\n\n----- " + lid + " -----");
					
					// Use list to verify we haven't already included this product in the results
					if (!lidList.contains(lid) && lid != null) { 	
						results.add(client.getLatestObject(lid, ExtrinsicObject.class));
						lidList.add(lid);
					}
				}
				return results;
			} else {
				return pr.getResults();
			}
		} catch (RegistryServiceException rse) {
			// Ignore. Nothing found.
		} catch (RegistryClientException rce) {
			throw new Exception(rce.getMessage());
		}
		return null;
	}
	
	private List<ExtrinsicObject> getAssociatedExtrinsics(String lidvid) throws Exception {
		// Build the filter
		ExtrinsicFilter filter = null;
		String assocLid = null;
		String version = null;
		
		List<String> lidList = Arrays.asList(lidvid.split("::"));
		assocLid = lidList.get(0);
		
		debug("Associated LID: " + assocLid);
		
		if (lidList.size() > 1) {
			version = lidList.get(1);
			//this.log.info("***** GOOD LIDVID - " + assocLid + " -- " + version);
		} else if (lidList.size() == 0) {	// Handles lidvids with bad format (: instead of ::)
			lidList = Arrays.asList(lidvid.split(":"));
			assocLid = lidvid.substring(0, lidvid.lastIndexOf(":"));
			version = lidList.get(lidList.size()-1);
			
			this.log.warning("***** BAD LIDVID - " + assocLid + " -- " + version);
		} else {
			version = "1.0";
		}
		
		/*if (!lidvid.contains("::")) {	// Throw error when lidvid is not formatted properly
			throw new RegistryClientException("BAD PRODUCT - " + lid);
		} else {
			
		}*/
		
		filter = new ExtrinsicFilter.Builder().lid(assocLid).build();
		
		// Create the query
		RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>()
				.filter(filter).build();
		try {
			RegistryClient client = new RegistryClient(this.registryUrl);
			// securityContext, user, password);
			
			List<ExtrinsicObject> results = new ArrayList<ExtrinsicObject>();
			PagedResponse<ExtrinsicObject> pr = client.getExtrinsics(query, 1,
					this.queryMax);
			
			// Examine the results of the query
			// Looping through the associated extrinsic objects
			// Since we need to search through the slots for the version id,
			// COULD create RegistrySlots object because that is what is of interest to us.
			// So we wouldn't have to loop through the slots twice.
			// But we would then have to store a map that we may just throw away.
			Slot versionIdSlot = null;
			if (pr.getNumFound() != 0) {
				for (ExtrinsicObject extrinsic : pr.getResults()) {
					// Product version should only have 1 value, check if it is the version we want
					versionIdSlot = extrinsic.getSlot(Constants.PRODUCT_VERSION);
					
					if (versionIdSlot != null) {
						if (versionIdSlot.getValues().get(0).equals(version)) {
							debug("Adding associated extrinsic - " + extrinsic.getLid());
							results.add(extrinsic);
						}
					}
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
	
	private List<String> getAssociatedSlotValues(String assocType, String slotName, ExtrinsicObject extObject) throws Exception {
		String tval;
		List<String> valArray = new ArrayList<String>();
		if (setAssociation(assocType, extObject)) {	// Add association type to association map (if needed)
			for (ExtrinsicObject assocExtObj : this.associationMap.get(assocType)) {	// Iterate through the associations for the given association type
				//assocExtObj.getSlot(arg0)
				if (!RegistryAttributes.isAttribute(slotName)) {	// If slotName is not an attribute in the registry, search the Extrinsic Object slots
					try { 
						for (String slotValue : assocExtObj.getSlot(slotName).getValues()) {	// Get the associated object slot values
							tval = cleanText(remNull(slotValue));
							if (!valArray.contains(tval)) {
								valArray.add(tval);
							}
						}
					} catch (NullPointerException e) {	// Occurs when no slot is found
						recordMissingSlot(assocExtObj, slotName);
					}
				} else {
					valArray.add(RegistryAttributes.getAttributeValue(slotName, assocExtObj));
				}

			}
		}  else {
			valArray.add("UNK");
		}
		return valArray;
	}

	/**
	 * Sets the associationMap values.
	 * 
	 * @throws
	 */
	//private void setAssociation(ExtrinsicObjectSlots slots) throws Exception {
	private boolean setAssociation(String assocType, ExtrinsicObject extObject) throws Exception {
		if (!this.associationMap.containsKey(assocType)) {	// TODO Create association map class and hold some threshold of associations for specific assocTypes like node, mission, target	
			List<ExtrinsicObject> assocExtObjList = getAssociatedObjects(extObject, assocType);
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
	 * Query the associated objects and map the objects to their slots
	 * 
	 * @param guid
	 * @param assocType
	 * @return
	 * @throws Exception
	 */
	private List<ExtrinsicObject> getAssociatedObjects(ExtrinsicObject extObject, String assocType) throws Exception {
		// PagedResponse<Association> assocResponse =
		// getAssociationResponse(assocType, guid);
		// System.out.println("Num Associations: " +
		// assocResponse.getNumFound());
		// PagedResponse<ExtrinsicObject> extResponse =
		// getExtrinsics(ExtrinsicFilterTypes.LIDVID, this.guid);
		
		Slot assocObjSlot = extObject.getSlot(assocType);
		
		//List<ExtrinsicObjectSlots> assocExtObjList = new ArrayList<ExtrinsicObjectSlots>();
		List<ExtrinsicObject> assocExtObjList = new ArrayList<ExtrinsicObject>();

		// Get list of associations for specific association type
		// for (Association association : (List<Association>) assocResponse
		// .getResults()) {
		if (assocObjSlot != null) {
			for (String assocLidVid : assocObjSlot.getValues()) {
				// Modified 2/2/12 - Test run showed some associations (instrument_host)
				// were not found because contained suffix (::29)
				// But doesn't appear to coincide with version, as expected
				// i.e. has_instrument_host - urn:nasa:pds:instrument_host.MRO:29.0
				//PagedResponse<ExtrinsicObject> extResponse = getAssociatedExtrinsics(ExtrinsicFilterTypes.LIDVID, assocLid);
				debug("Associated lidvid - " + assocLidVid);
				List<ExtrinsicObject> extList = getAssociatedExtrinsics(assocLidVid);
	
				//if (extResponse.getNumFound() == 0) {
				if (extList.size() != 0) {
					// Remove has_investigation or has_mission from missing targets if the other is found
					/*for (ExtrinsicObject extObj : extList) {
						assocExtObjList.add(new ExtrinsicObjectSlots(extObj));
					}*/
					assocExtObjList.addAll(extList);
				} else {
					// this.log.warning("Association not found : "
					// + association.getAssociationType() + " - "
					// + association.getTargetObject());
					// this.writer.println("Association not found : "
					// + association.getAssociationType() + " - "
					// + association.getTargetObject());
					this.missingAssocTargets.add(this.lid + " - " + assocType
							+ " - " + assocLidVid);
				}
			}
		} else {
			recordMissingSlot(extObject, assocType);
		}
		
		return assocExtObjList;
	}
	
	private void recordMissingSlot(ExtrinsicObject extObject, String slot) {
		List<String> slotList;
		String objectType = extObject.getObjectType();
		if (this.missingSlotsMap.containsKey(objectType)) {	// Verify object type has not been included in missing slots map
			slotList = this.missingSlotsMap.get(objectType);	// If it has, get the list
		} else {	// Else create a new ArrayList
			slotList = new ArrayList<String>();
		}
		slotList.add(this.lid + " - " + slot);
		this.missingSlotsMap.put(objectType, slotList);
	}

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
				for (String slot : this.missingSlotsMap.get(objectType))
					out += "    " + slot + "\n";
			}
			out += "\n\n";
		}

		if (!out.equals("")) {
			this.writer.println(out);
			this.log.warning(out);
		}
	}

	/**
	 * 
	 * @param object
	 */
	private void setIdentifiers(RegistryObject object) {
		this.lid = object.getLid();
	}

	/**
	 * 
	 * @param str
	 * @param extObject
	 * @return
	 * @throws Exception
	 */
	private String setSubstring(String str, ExtrinsicObject extObject) throws Exception {
		int start, end;
		String tval = "", key;

		try {
			while (str.contains("{")) {
				start = str.indexOf("{");
				end = str.indexOf("}");
				key = str.substring(start + 1, end);
	
				// TODO ASSUMPTION: These substring slots/associated slots will only have 1 value - may want to specify multiple values here instead
				if (!key.contains(".")) {	// If key is not an associated object
					//tval = this.slots.get(key).get(0);
					tval = extObject.getSlot(key).getValues().get(0);	// Get the first slot value
				} else { // Association mapping
					String[] values = key.split("\\.");
					//List<ExtrinsicObject> assocExtObjList = this.associationMap.get(values[0]);
					tval = getAssociatedSlotValues(values[0], values[1], extObject).get(0);
					/*if (assocExtObjList != null) {
						if (!RegistryAttributes.isAttribute(values[1])) {
							tval = assocExtObjList.get(0).getSlot(values[1]).getValues().get(0);
						} else {
							tval = RegistryAttributes.getAttributeValue(values[1], assocExtObjList.get(0));
						}
					}*/
				}
	
				str = str.replace('#', '&').replace("{" + key + "}",
						URLEncoder.encode(((tval == null) ? "" : tval), "UTF-8"));
			}
		} catch (NullPointerException e) {	// Associated Extrinsic Object does not have the slot requested
			this.missingAssocTargets.add(this.lid + " - " + str);
		}
		return str;
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	private String cleanText(String text) {
		return text.trim().replace("\n", "<br />").replaceAll("    *", "   ");
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

	private void debug(String msg) {
		if (this.debug) {
			System.out.println(msg);
		}
	}
	
}
