//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id$ 
//

package gov.nasa.pds.search.core.extractor;

import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Identifiable;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.search.core.ExtractionException;
import gov.nasa.pds.search.core.InvalidExtractorException;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.extractor.registry.ExtrinsicFilterTypes;
import gov.nasa.pds.search.core.extractor.registry.MappingTypes;
import gov.nasa.pds.search.core.extractor.registry.RegistrySlots;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class Extractor { // implements Extractor {
	// Database configuration file default

	/*public static final List<String> DATE_FIELDS = new ArrayList<String>() {
		private static final long serialVersionUID = 4549083437446527668L;
		{
			add("start_time");
			add("stop_time");
		}
	};

	public static final List<String> DATE_FORMATS = new ArrayList<String>() {
		private static final long serialVersionUID = -790664498543223562L;
		{
			add("yyyy-MM-dd");
			add("yyyy-MM-dd'T'kk:mm:ss.SSS'Z'");
			add("yyyy-MM-dd'T'kk:mm:ss.SSS");
			add("yyyy-MM-dd'T'kk:mm");
		}
	};*/

	private int oidseq = 10000;

	private String classname, classFilename;
	private String propName, propVal;
	private String fname = "", fnameprefix = "tse", fnameext = "xml";
	private String tval1;

	private SearchAttributes searchAttrs;

	//private Map colNames;
	private Map finalVals;

	private RegistrySlots slots;

	private String lid;
	//private String guid;

	private List valArray;

	private Logger log = Logger.getLogger(this.getClass().getName());
	private PrintWriter xmlDisplay;

	// private RegistryClient client;
	private PrintWriter writer;
	private Map<String, List<RegistrySlots>> associationMap;

	
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
	public Extractor(PrintWriter writer, String name, String file,
			String registryUrl, int queryMax) {
		this.log.fine("In Generic Extractor");

		this.writer = writer;
		this.finalVals = new HashMap();

		this.classname = name;
		this.classFilename = file;
		this.registryUrl = registryUrl;
		if (queryMax > -1) {
			this.queryMax = queryMax;
		} else {
			this.queryMax = Constants.QUERY_MAX;
		}

		this.associationMap = new HashMap<String, List<RegistrySlots>>();

		this.missingSlotsMap = new HashMap<String, List<String>>();
		this.missingAssocTargets = new ArrayList<String>();

		this.log.fine("Class name: " + classname);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.tse.catalog.Extractor#extract()
	 */
	public List extract(File extractorDir) throws ExtractionException {
		// log.fine("confdir : " + confDir);
		List instkeys = new ArrayList();

		this.searchAttrs = new SearchAttributes(this.classFilename);

		try {

			//PagedResponse<ExtrinsicObject> response = getObjectTypeExtrinsics(ExtrinsicFilterTypes.OBJECT_TYPE, this.columns.getObjectType());
			List<ExtrinsicObject> extList = getObjectTypeExtrinsics(this.searchAttrs.getObjectType());

			//for (ExtrinsicObject object : (List<ExtrinsicObject>) response.getResults()) {
			for (ExtrinsicObject object : extList) {
				this.oidseq++;

				// Get class properties
				this.log.fine(object.getLid() + " - " + object.getObjectType());
				setColumnProperties(object);

				XMLWriter xml = new XMLWriter(this.finalVals, extractorDir, this.oidseq,
						this.classname);

				// Create the XML file
				// log.info("Files placed in dir : " + extractorDir);
				createXML(extractorDir);
			}

			displayWarnings();

			// rs1.close();
			// connection1.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			this.log.warning("Exception " + ex.getClass().getName()
					+ ex.getMessage());
		}

		return instkeys;
	}

	/**
	 * Creates the XML file using the properties specified
	 * 
	 * @param baseDir
	 *            - directory where the new XML should be placed
	 */
	private void createXML(File baseDir) {
		try {
			Integer oidseqi = new Integer(oidseq);
			String itemoid = oidseqi.toString();

			/* Start profile output */
			this.fname = this.fnameprefix + "_" + this.classname + "_" + itemoid + "."
					+ this.fnameext;
			this.xmlDisplay = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(baseDir, this.fname), false)));
			printFileHeader();

			Set set2 = this.finalVals.keySet();
			Iterator iter2 = set2.iterator();
			while (iter2.hasNext()) {
				this.propName = (String) iter2.next();
				this.valArray = (ArrayList) this.finalVals.get(this.propName);
				for (Iterator i = this.valArray.iterator(); i.hasNext();) {
					this.propVal = (String) i.next();
					printXml(this.propName, this.propVal, isCleanedAttr(this.propName));
				}
			}
			printFileFooter();
			this.xmlDisplay.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			this.log.warning("Exception " + ex.getClass().getName()
					+ ex.getMessage());
		}
	}

	/**
	 * Get all of the attributes and their values and place them into a HashMap,
	 * valArray. The HashMap is made of of attrName->value pairs. The value in
	 * the pair depends upon the current attribute's index, where it is either
	 * the value in attrVals or a value queried from the database.
	 * 
	 * @param ExtrinsicObject - 
	 */
	private void setColumnProperties(ExtrinsicObject extObject) {
		try {
			/* Initialize local variables */
			String currName, currType, currVal;

			setIdentifiers(extObject);
			this.slots = new RegistrySlots(extObject);
			setAssociations(this.slots);

			// Loop through class results beginning from top
			for (int i = 0; i < this.searchAttrs.getNumAttr(); i++) {				
				currName = this.searchAttrs.getName(i);
				currType = this.searchAttrs.getType(i);
				currVal = this.searchAttrs.getValue(i);

				if (currType.equals(MappingTypes.OUTPUT)) { // Output value
															// given in XML
					this.valArray = new ArrayList();
					this.valArray.add(currVal);
					this.finalVals.put(currName, this.valArray);

					// TODO should refactor this entire else-if and create a
					// separate RegistryAttribute class that handles all those
					// search parameters that fit the specific criteria
					// very similar code is being replicated in the
					// RegistrySlots class
				} else if (currType.equals(MappingTypes.ATTRIBUTE)) { // Specific
																		// attributes
																		// that
																		// can
					if (currName.equals("identifier")) { // be queried from a
															// RegistryObject
						this.valArray = new ArrayList(); // method
						this.valArray.add(extObject.getLid());
						this.finalVals.put(currName, this.valArray);
					} else if (currName.equals("title")) {
						this.valArray = new ArrayList();
						this.valArray.add(extObject.getName());
						this.finalVals.put(currName, this.valArray);
					}
				} else if (currType.equals(MappingTypes.SLOT)) { // Value maps
																	// to a
																	// specific
					this.valArray = new ArrayList(); // slot in the current object
												// type
					for (String value : this.slots.get(currVal)) {
						this.tval1 = remNull(value);
						// tval1 = tval1.trim();
						this.valArray.add(cleanText(this.tval1));

					}
					// if (DATE_FIELDS.contains(currName))
					// valArray = reformatDateFields(valArray);
					this.finalVals.put(currName, valArray);
				} else if (currType.equals(MappingTypes.SLOT_SINGLE)) { // Values
																		// maps
																		// to a
																		// specific
					this.valArray = new ArrayList(); // slot in the current object
												// type
					this.tval1 = remNull(this.slots.get(currVal).get(0)); // AND
																		// ensures
																		// that
																		// certain
																		// columns
					this.valArray.add(cleanText(this.tval1)); // that are only allowed one
													// value
					this.finalVals.put(currName, this.valArray); // do not output a list
														// (i.e. description,
														// mission_desc)
				} else if (currType.equals(MappingTypes.ASSOCIATION)) { // Value
																		// maps
																		// to an
																		// associated
																		// object
					String[] values = currVal.split("\\."); // slot
					this.valArray = new ArrayList();
					String assocType = values[0];
					boolean foundAssociation = this.associationMap
							.containsKey(assocType);

					// Code hack in order to handle has_mission and
					// has_investigation overlap
					// Assumption that product cannot have both
					// has_investigation and has_mission associations
					/*if (!foundAssociation) {
						if (assocType.equals("has_mission")) {
							assocType = "has_investigation";
							foundAssociation = this.associationMap
									.containsKey(assocType);
						} else if (assocType.equals("has_investigation")) {
							assocType = "has_mission";
							foundAssociation = this.associationMap
									.containsKey(assocType);
						}
					}*/

					if (foundAssociation) {
						for (RegistrySlots assocSlots : this.associationMap
								.get(assocType)) {
							for (String value : assocSlots.get(values[1])) {
								this.tval1 = cleanText(remNull(value));
								if (!valArray.contains(this.tval1)) {
									this.valArray.add(this.tval1);
								}
							}

							try {
								recordMissingSlots(assocSlots);
							} catch (NullPointerException e) {
								System.err.println("NullPointerException - "
										+ this.lid + " - " + assocType);
							}
							this.finalVals.put(currName, this.valArray);
						}
					} else {
						this.valArray.add("UNK");
						this.finalVals.put(currName, this.valArray);
					}

				} else if (currType.equals(MappingTypes.OTHER)) { // Unknown
																	// mapping
																	// that is
																	// currently
																	// ignored

					/*
					 * tval1 = "UNK"; valArray = new ArrayList();
					 * valArray.add(tval1); // Fix common special case to build
					 * identifier finalVals.put(currName,valArray);
					 */

				} else {
					throw new InvalidExtractorException(
							"Unknown Mapping Type - " + currType);
				}
			}

			try {
				recordMissingSlots(this.slots);
			} catch (NullPointerException e) {
				System.err.println("NullPointerException - " + this.lid);
			}

			setResLocation();
		} catch (Exception ex) {
			ex.printStackTrace();
			this.log.warning("Exception " + ex.getClass().getName()
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
					if (!lidList.contains(lid)) { 	
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
		
		//this.log.info("*********** " + lid + " -- " + version);
		
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

	/**
	 * Sets the associationMap values.
	 * 
	 * @throws
	 */
	private void setAssociations(RegistrySlots slots) throws Exception {
		for (String assocType : (List<String>) this.searchAttrs.getAssociations()) {
			// System.out.println(assocType + " - " + this.guid);

			List<String> assocLidList = slots.get(assocType);

			// PagedResponse<ExtrinsicObject> extObjResponse =
			// getExtrinsics(ExtrinsicFilterTypes.GUID, this.guid);
			if (assocLidList.isEmpty()) {
				this.log.fine("Missing Association Slot : " + this.lid + " - "
						+ assocType);
			} else {
				List<RegistrySlots> assocSlotList = getAssociationSlots(
						assocLidList, assocType);
				if (!assocSlotList.isEmpty()) {
					this.associationMap.put(assocType, assocSlotList);
				}
			}
		}
	}

	/**
	 * Query the associated objects and map the objects to their slots
	 * 
	 * @param guid
	 * @param assocType
	 * @return
	 * @throws Exception
	 */
	private List<RegistrySlots> getAssociationSlots(List<String> assocLidList,
			String assocType) throws Exception {
		// PagedResponse<Association> assocResponse =
		// getAssociationResponse(assocType, guid);
		// System.out.println("Num Associations: " +
		// assocResponse.getNumFound());
		// PagedResponse<ExtrinsicObject> extResponse =
		// getExtrinsics(ExtrinsicFilterTypes.LIDVID, this.guid);
		List<RegistrySlots> slotLst = new ArrayList<RegistrySlots>();

		// Get list of associations for specific association type
		// for (Association association : (List<Association>) assocResponse
		// .getResults()) {
		for (String assocLid : assocLidList) {
			// Modified 2/2/12 - Test run showed some associations (instrument_host)
			// were not found because contained suffix (::29)
			// But doesn't appear to coincide with version, as expected
			// i.e. has_instrument_host - urn:nasa:pds:instrument_host.MRO:29.0
			//PagedResponse<ExtrinsicObject> extResponse = getAssociatedExtrinsics(ExtrinsicFilterTypes.LIDVID, assocLid);
			List<ExtrinsicObject> extList = getAssociatedExtrinsics(assocLid);

			//if (extResponse.getNumFound() == 0) {
			if (extList.size() == 0) {
				// this.log.warning("Association not found : "
				// + association.getAssociationType() + " - "
				// + association.getTargetObject());
				// this.writer.println("Association not found : "
				// + association.getAssociationType() + " - "
				// + association.getTargetObject());
				this.missingAssocTargets.add(this.lid + " - " + assocType
						+ " - " + assocLid);
				// slotLst.add(new RegistrySlots());
			} else {
				// Remove has_investigation or has_mission from missing targets if the other is found
				for (ExtrinsicObject extObj : extList) {
					slotLst.add(new RegistrySlots(extObj));
				}
			}
		}
		return slotLst;
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

	private void recordMissingSlots(RegistrySlots slots) {
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
	}

	private void displayWarnings() {
		String out = "";

		if (!this.missingAssocTargets.isEmpty()) {
			out += "\nWARNING - Missing the following Association Targets:\n";
			for (String missingAssociation : this.missingAssocTargets) {
				out += missingAssociation + "\n";
			}
		}

		if (!this.missingSlotsMap.isEmpty()) {
			out += "\nWARNING - Missing the following Registry Slots:\n";
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
		//this.guid = object.getGuid();
	}

	private void setResLocation() throws UnsupportedEncodingException {
		int start, end;
		String tval = "", key;

		String resLoc = (String) ((ArrayList) finalVals.get("resLocation"))
				.get(0);
		while (resLoc.contains("{")) {
			start = resLoc.indexOf("{");
			end = resLoc.indexOf("}");
			key = resLoc.substring(start + 1, end);

			if (!key.contains("."))
				tval = this.slots.get(key).get(0);
			else { // Association mapping
				String[] values = key.split("\\.");
				List<RegistrySlots> slotList = this.associationMap
						.get(values[0]);
				if (slotList != null)
					tval = this.associationMap.get(values[0]).get(0)
							.get(values[1]).get(0);
			}

			resLoc = resLoc.replace('#', '&').replace("{" + key + "}",
					URLEncoder.encode(((tval == null) ? "" : tval), "UTF-8"));
		}

		valArray = new ArrayList();
		valArray.add(resLoc);
		finalVals.put("resLocation", valArray);
	}

	private String cleanText(String text) {
		return text.trim().replace("\n", "<br />").replaceAll("    *", "   ");
	}

	/**
	 * Find the number of attributes
	 * 
	 * @return attrNames.size() - number of names in attrNames Map
	 */
	/*
	 * public int getNumAttr() { return colNames.size(); }
	 */

	/**
	 * Display the header for the XML file.
	 */
	public void printFileHeader() {
		xmlDisplay.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		xmlDisplay.println("<doc>");
		xmlDisplay.println("\t<" + classname + ">");
	}

	/**
	 * Display the footer for the XML file.
	 */
	public void printFileFooter() {
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
	public void printXml(String name, String value, boolean clean) {
		// Temporary variables to hold name and value
		String tName, tValue;

		tName = name.trim();
		tValue = value.trim();

		// Utilize JTidy servlet extension to encode all non-letter characters
		// tName = HTMLEncode.encode(tName);
		// tValue = HTMLEncode.encode(tValue);

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
			return "NULL";
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
