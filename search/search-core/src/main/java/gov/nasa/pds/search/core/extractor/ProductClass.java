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
	
	private static final String FNAMEPREFIX = "product";
	private static final int OUT_SEQ_START=10000;

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
		this.finalVals = new HashMap<String, List<String>>();

		this.classname = name;
		this.classFilename = file;
		this.registryUrl = registryUrl;
		if (queryMax > -1) {
			this.queryMax = queryMax;
		} else {
			this.queryMax = Constants.QUERY_MAX;
		}

		this.associationMap = new HashMap<String, List<ExtrinsicObject>>();

		this.missingSlotsMap = new HashMap<String, List<String>>();
		this.missingAssocTargets = new ArrayList<String>();

		this.log.fine("Class name: " + classname);
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
		int outSeqNum = OUT_SEQ_START;

		this.searchFields = new SearchFields(this.classFilename);

		try {

			//PagedResponse<ExtrinsicObject> response = getObjectTypeExtrinsics(ExtrinsicFilterTypes.OBJECT_TYPE, this.columns.getObjectType());
			List<ExtrinsicObject> extList = getObjectTypeExtrinsics(this.searchFields.getObjectType());

			//for (ExtrinsicObject object : (List<ExtrinsicObject>) response.getResults()) {
			for (ExtrinsicObject extObj : extList) {
				outSeqNum++;

				// Get class properties
				setColumnProperties(extObj);

				XMLWriter writer = new XMLWriter(this.finalVals, outputDir, outSeqNum,
						this.classname);
				writer.write();
				
				createXML(outputDir, outSeqNum);

				// Create the XML file
				// log.info("Files placed in dir : " + extractorDir);
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

	/**
	 * Creates the XML file using the properties specified
	 * 
	 * @param baseDir
	 *            - directory where the new XML should be placed
	 * @throws ProductClassException 
	 */
	private void createXML(File baseDir, int seqNum) throws ProductClassException {
		PrintWriter xmlDisplay = null;
		try {
			String fname = FNAMEPREFIX + "_" + this.classname + "_" + String.valueOf(seqNum) + ".xml";
			xmlDisplay = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(baseDir, fname), false)));
			printFileHeader(xmlDisplay);
			
			for (String propName : this.finalVals.keySet()) {
				for (String propVal : this.finalVals.get(propName)) {
					printXml(xmlDisplay, propName, propVal, isCleanedAttr(propName));
				}
			}
			printFileFooter(xmlDisplay);
		} catch (Exception ex) {
			throw new ProductClassException("Exception " + ex.getClass().getName()
					+ ex.getMessage());
		} finally {
			xmlDisplay.close();
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
		
		if (lidList.size() > 1) {
			version = lidList.get(1);
			//this.log.info("***** GOOD LIDVID - " + assocLid + " -- " + version);
		} else if (lidList.size() == 0) {	// Handles lidvids with bad format (: instead of ::)
			lidList = Arrays.asList(lidvid.split(":"));
			assocLid = lidvid.substring(0, lidvid.lastIndexOf(":"));
			version = lidList.get(lidList.size()-1);
			
			this.log.info("***** BAD LIDVID - " + assocLid + " -- " + version);
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
			if (!RegistryAttributes.isAttribute(slotName)) {
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
		if (!this.associationMap.containsKey(assocType)) {
			//List<String> assocLidList = extObject.getSlot(assocType).getValues();
			
			//if (assocLidList.isEmpty()) {
			//	recordMissingAssocSlot(this.lid, assocType);
			//	return false;
			//} else {
				
				List<ExtrinsicObject> assocExtObjList = getAssociatedObjects(extObject, assocType);
				if (!assocExtObjList.isEmpty()) {
					this.associationMap.put(assocType, assocExtObjList);
				} else {
					this.missingAssocTargets.add(this.lid + " - " + assocType);
				}
			//}
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

	/*
	 * private PagedResponse<Association> getAssociationResponse(String
	 * assocType, String id) throws Exception { // Build the filter
	 * AssociationFilter assocFilter = new AssociationFilter.Builder()
	 * .sourceObject(id).associationType(assocType).build();
	 * RegistryQuery<AssociationFilter> query = new
	 * RegistryQuery.Builder<AssociationFilter>() .filter(assocFilter).build();
	 * 
	 * try { RegistryClient client = new RegistryClient(Constants.REGISTRY_URL);
	 * // securityContext, user, password); PagedResponse<Association> pr =
	 * client.getAssociations(query, 1, Constants.QUERY_MAX);
	 * 
	 * return pr; } catch (RegistryServiceException rse) { // Ignore. Nothing
	 * found. } catch (RegistryClientException rce) { throw new
	 * Exception(rce.getMessage()); } return null; }
	 */
	
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
	
	/*private void recordMissingSlots(ExtrinsicObject extObj) {
		if (slots.isMissingSlots()) {
			if (this.missingSlotsMap.containsKey(slots.getObjectType())) {
				List<String> slotList = this.missingSlotsMap.get(slots.getObjectType());
				slotList.addAll(slots.getMissingSlotList());
				this.missingSlotsMap.put(slots.getObjectType(),
						slotList);
			} else {
				this.missingSlotsMap.put(slots.getObjectType(),
						slots.getMissingSlotList());
			}
		}
	}*/

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
	 * Registry 0.2.0-dev implementation
	 * 
	 * Depending upon the responseType, sets the necessary filter and gets a
	 * RegistryResponse
	 * 
	 * @param responseType
	 * @param type
	 * @param value
	 * @return
	 * @throws ParseException
	 * @throws DateParseException
	 */
	/*
	 * private RegistryResponse getResponse(int responseType, String type,
	 * String value) {
	 * 
	 * if (responseType == ResponseTypes.EXTRINSIC) { ObjectFilter filter =
	 * null; // TODO account for version which is second part of split value if
	 * (type.equals(ExtrinsicFilterTypes.LIDVID)) { filter = new
	 * ObjectFilter.Builder().lid(value.split("::")[0]) .build(); } else if
	 * (type.equals(ExtrinsicFilterTypes.GUID)) { filter = new
	 * ObjectFilter.Builder().guid(value).build(); } else if
	 * (type.equals(ExtrinsicFilterTypes.OBJECT_TYPE)) { filter = new
	 * ObjectFilter.Builder().objectType(value).build(); }
	 * 
	 * RegistryQuery<ExtrinsicFilter> query = new
	 * RegistryQuery<ExtrinicFilter>().Builder<ExtrinicFilter>().filter(filter)
	 * .build(); return this.client.getExtrinsics(query, 1,
	 * TseConstants.QUERY_MAX) .getEntity(RegistryResponse.class);
	 * 
	 * } else if (responseType == ResponseTypes.ASSOCIATION) { AssociationFilter
	 * assocFilter = new AssociationFilter.Builder()
	 * .sourceObject(value).associationType(type).build(); AssociationQuery
	 * assocQuery = new AssociationQuery.Builder() .filter(assocFilter).build();
	 * return this.client.getAssociations(assocQuery, 1,
	 * TseConstants.QUERY_MAX).getEntity( RegistryResponse.class); }
	 * 
	 * return null; }
	 */

	/*
	 * private List reformatDateFields(List array) throws ParseException {
	 * System.out.println("FOUND Date Field - " + array.get(0));
	 * SimpleDateFormat frmt = new SimpleDateFormat(DATE_FORMATS.get(1));
	 * 
	 * List newVals = new ArrayList<String>(); String newDate = ""; for (Object
	 * date : array) { if (((String) date)
	 * .matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}")) { newDate =
	 * (String) date + ":00"; } else if (((String)
	 * date).matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) { newDate = (String) date +
	 * "T00:00:00"; } else if (((String) date).equals("unknown")) { newDate =
	 * "UNK"; } else { newDate = (String) date; }
	 * System.out.println("New Date: " + newDate); newVals.add(date); } return
	 * newVals;
	 * 
	 * }
	 */

	private void setIdentifiers(RegistryObject object) {
		this.lid = object.getLid();
	}

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

	private String cleanText(String text) {
		return text.trim().replace("\n", "<br />").replaceAll("    *", "   ");
	}

	/**
	 * Display the header for the XML file.
	 */
	public void printFileHeader(PrintWriter xmlDisplay) {
		xmlDisplay.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		xmlDisplay.println("<doc>");
		xmlDisplay.println("\t<" + classname + ">");
	}

	/**
	 * Display the footer for the XML file.
	 */
	public void printFileFooter(PrintWriter xmlDisplay) {
		xmlDisplay.println("\t</" + classname + ">");
		xmlDisplay.println("</doc>");
	}

	/**
	 * Display the field name and value in XML format.
	 * 
	 * @param name
	 *            - name of field
	 * @param value
	 *            - value for specific record
	 */
	public void printXml(PrintWriter xmlDisplay, String name, String value, boolean clean) {
		// Temporary variables to hold name and value
		String tName, tValue;

		tName = name.trim();
		tValue = value.trim();

		if (clean) {
			tValue = tValue.toLowerCase();
			tValue = tValue.replace(' ', '_');
		}

		// Previous method used to encode only select HTML entities
		tName = repAllCharWStr(tName);
		tValue = repAllCharWStr(tValue);

		xmlDisplay.println("\t\t<" + tName + ">" + tValue + "</" + tName + ">");
	}

	/**
	 * Replace HTML entities & with &amp;, < with &lt;, > with &gt;, and " with
	 * &quot;
	 */
	public String repAllCharWStr(String s1) {
		String s2;

		s2 = repCharWStr(remNull(s1), '&', "&amp;");
		s2 = repCharWStr(s2, '<', "&lt;");
		s2 = repCharWStr(s2, '>', "&gt;");
		s2 = repCharWStr(s2, '\"', "&quot;");
		s2 = repCharWStr(s2, '[', "&#91;");
		s2 = repCharWStr(s2, ']', "&#93;");
		return s2;
	}

	/**
	 * Remove String Nulls
	 */
	public String remNull(String s1) {
		if (s1 == null) {
			return "UNK";
		}
		return s1;
	}

	/**
	 * Replace character with string
	 */
	public String repCharWStr(String str1, char rc, String rstr) {
		int p1, str1len;
		char tc;
		StringBuffer sbuff1 = new StringBuffer(str1), sbuff2 = new StringBuffer();

		p1 = 0;
		str1len = sbuff1.length();
		while (p1 < str1len) {
			tc = sbuff1.charAt(p1);
			if (tc == rc) {
				sbuff2.append(rstr);
			} else {
				sbuff2.append(tc);
			}
			p1++;
		}
		return sbuff2.toString();
	}

	public boolean isCleanedAttr(String s1) {
		String[] elemfacet = { "identifier", "title", "format", "description",
				"publisher", "language", "resContext", "resClass",
				"resLocation", "data_set_terse_desc", "data_set_desc",
				"mission_desc", "target_desc", "host_desc", "instrument_desc",
				"volume_name", "volume_desc" };

		for (int ind = 0; ind < elemfacet.length; ind++) {
			if (s1.compareTo(elemfacet[ind]) == 0) {
				return false;
			}
		}
		return true;
	}

}
