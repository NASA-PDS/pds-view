//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id$ 
//

package gov.nasa.pds.search.core.catalog.extractor;

import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.ObjectFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.search.core.catalog.ExtractionException;
import gov.nasa.pds.search.core.catalog.InvalidExtractorException;
import gov.nasa.pds.search.core.catalog.TseConstants;
import gov.nasa.pds.search.core.catalog.extractor.registry.ExtrinsicFilterTypes;
import gov.nasa.pds.search.core.catalog.extractor.registry.MappingTypes;
import gov.nasa.pds.search.core.catalog.extractor.registry.RegistrySlots;
import gov.nasa.pds.search.core.catalog.extractor.registry.ResponseTypes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.solr.common.util.DateUtil;
import org.apache.solr.schema.DateField;

/**
 * @author pramirez
 * @modifiedby Jordan Padams
 * @version $Revision$
 * 
 */
public class Extractor { // implements Extractor {
	// Database configuration file default

    public static final List<String> DATE_FIELDS = new ArrayList<String>() {
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
    };
    
	
	private int oidseq = 10000;

	private String classname, classFilename;
	private String propName, propVal;
	private String fname = "", fnameprefix = "tse", fnameext = "xml";
	private String tval1;

	private ColumnNodes columns;

	private Map colNames;
	private Map finalVals;

	private RegistrySlots slots;

	private String lid;
	private String guid;

	private List valArray;

	private Logger log = Logger.getLogger(this.getClass().getName());
	private PrintWriter xmlDisplay;

	//private RegistryClient client;
	private PrintWriter writer;
	private Map<String, List<RegistrySlots>> associationMap;
	
	private Map<String, List<String>> missingSlotsMap;
	private List<Association> missingAssociations;

	/**
	 * Initialize global variables used throughout
	 * 
	 * @param name
	 *            - class of catalog to be extracted
	 * @param value
	 *            - name of column properties file
	 */
	public Extractor(PrintWriter writer, String name, String file) {
		log.fine("In Generic Extractor");

		this.writer = writer;
		finalVals = new HashMap();

		classname = name;
		classFilename = file;

		associationMap = new HashMap<String, List<RegistrySlots>>();
		missingSlotsMap = new HashMap<String, List<String>>();
		missingAssociations = new ArrayList<Association>();

		log.fine("Class name: " + classname);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see gov.nasa.pds.tse.catalog.Extractor#extract()
	 */
	public List extract(File extractorDir)
			throws ExtractionException {
		//log.fine("confdir : " + confDir);
		List instkeys = new ArrayList();

		this.columns = new ColumnNodes(this.classFilename);

		try {
			// Initialize the global RegistryClient
			//this.client = new RegistryClient(TseConstants.REGISTRY_URL);

			PagedResponse<ExtrinsicObject> response = getExtrinsics(ExtrinsicFilterTypes.OBJECT_TYPE,
					this.columns.getObjectType());

			for (ExtrinsicObject object : (List<ExtrinsicObject>) response
					.getResults()) {
				oidseq++;

				// Get class properties
				log.fine(object.getLid() + " - " + object.getObjectType());
				setColumnProperties(object);

				XMLWriter xml = new XMLWriter(finalVals, extractorDir, oidseq,
						classname);

				// Create the XML file
				// log.info("Files placed in dir : " + extractorDir);
				createXML(extractorDir);
			}
			
			displayWarnings();
			
			// rs1.close();
			// connection1.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName()
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
			fname = fnameprefix + "_" + classname + "_" + itemoid + "."
					+ fnameext;
			xmlDisplay = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(baseDir, fname), false)));
			printFileHeader();

			Set set2 = finalVals.keySet();
			Iterator iter2 = set2.iterator();
			while (iter2.hasNext()) {
				propName = (String) iter2.next();
				valArray = (ArrayList) finalVals.get(propName);
				for (Iterator i = valArray.iterator(); i.hasNext();) {
					propVal = (String) i.next();
					printXml(propName, propVal, isCleanedAttr(propName));
				}
			}
			printFileFooter();
			xmlDisplay.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName()
					+ ex.getMessage());
		}
	}

	/**
	 * Get all of the attributes and their values and place them into a HashMap,
	 * valArray. The HashMap is made of of attrName->value pairs. The value in
	 * the pair depends upon the current attribute's index, where it is either
	 * the value in attrVals or a value queried from the database.
	 */
	private void setColumnProperties(ExtrinsicObject object) {
		try {
			/* Initialize local variables */
			String currName, currType, currVal;

			setIdentifiers(object);
			setAssociations();
			this.slots = new RegistrySlots(object);

			// Loop through class results beginning from top
			for (int i = 0; i < columns.getNumAttr(); i++) {
				currName = columns.getName(i);
				currType = columns.getType(i);
				currVal = columns.getValue(i);

				if (currType.equals(MappingTypes.OUTPUT)) {				// Output value given in XML
					valArray = new ArrayList();
					valArray.add(currVal);
					finalVals.put(currName, valArray);
				} else if (currType.equals(MappingTypes.ATTRIBUTE)) {	// Specific attributes that can
					if (currName.equals("identifier")) {				// be queried from a RegistryObject
						valArray = new ArrayList();						// method
						valArray.add(object.getLid());
						finalVals.put(currName, valArray);
					} else if (currName.equals("title")) {
						valArray = new ArrayList();
						valArray.add(object.getName());
						finalVals.put(currName, valArray);
					}
				} else if (currType.equals(MappingTypes.SLOT)) {		// Value maps to a specific
					valArray = new ArrayList();						// slot in the current object type
					for (String value : this.slots.get(currVal)) {
						tval1 = remNull(value);
						// tval1 = tval1.trim();
						valArray.add(cleanText(tval1));
						
					}
//					if (DATE_FIELDS.contains(currName))
//						valArray = reformatDateFields(valArray);
					finalVals.put(currName, valArray);
				} else if (currType.equals(MappingTypes.SLOT_SINGLE)) {	// Values maps to a specific
					valArray = new ArrayList();							// slot in the current object type
					tval1 = remNull(this.slots.get(currVal).get(0));	// AND ensures that certain columns
					valArray.add(cleanText(tval1));						// that are only allowed one value
					finalVals.put(currName, valArray);					// do not output a list
																		// (i.e. description, mission_desc)
				} else if (currType.equals(MappingTypes.ASSOCIATION)) { // Value maps to an associated object
					String[] values = currVal.split("\\.");				// slot
					valArray = new ArrayList();

					if (this.associationMap.get(values[0]) == null) {
						valArray.add("UNK");
						finalVals.put(currName, valArray);
					} else {
						for (RegistrySlots assocSlots : this.associationMap.get(values[0])) {
							for (String value : assocSlots.get(values[1])) {
								tval1 = cleanText(remNull(value));
								if (!valArray.contains(tval1)) {
									valArray.add(tval1);
								}
							}
							
							recordMissingSlots(assocSlots);
							
							finalVals.put(currName, valArray);
						}
					}
				} else if (currType.equals(MappingTypes.OTHER)) {		// Unknown mapping that is currently ignored

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
			
			recordMissingSlots(this.slots);

			setResLocation();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName()
					+ ex.getMessage());
		}
	}

	/**
	 * Sets the associationMap values.
	 * @throws  
	 */
	private void setAssociations() throws Exception {
		for (String assocType : (List<String>) this.columns.getAssociations()) {
			//System.out.println(assocType + " - " + this.guid);
			this.associationMap.put(assocType, getAssociationSlots(this.guid, assocType));
		}
	}

	/**
	 * Query the associated objects and map the objects to their slots
	 * @param guid
	 * @param assocType
	 * @return
	 * @throws Exception 
	 */
	private List<RegistrySlots> getAssociationSlots(String guid, String assocType) throws Exception {
		PagedResponse<Association> assocResponse = getAssociations(assocType, guid);
		//System.out.println("Num Associations: " + assocResponse.getNumFound());
		List<RegistrySlots> slotLst = new ArrayList<RegistrySlots>();

		// Get list of associations for specific association type
		for (Association association : (List<Association>) assocResponse
				.getResults()) {
			
			PagedResponse<ExtrinsicObject> extResponse = getExtrinsics(
					ExtrinsicFilterTypes.GUID, association.getTargetObject());

			if (extResponse.getNumFound() == 0) {
				//this.log.warning("Association not found : "
				//		+ association.getAssociationType() + " - "
				//		+ association.getTargetObject());
				//this.writer.println("Association not found : "
				//		+ association.getAssociationType() + " - "
				//		+ association.getTargetObject());
				this.missingAssociations.add(association);
				slotLst.add(new RegistrySlots());
			} else {
				this.log.fine("Association found : "
						+ association.getAssociationType() + " - "
						+ association.getTargetObject());
				for (ExtrinsicObject extObj : extResponse.getResults()) {
					slotLst.add(new RegistrySlots(extObj));
				}
			}
		}
		return slotLst;
	}

	private PagedResponse<ExtrinsicObject> getExtrinsics(String type, String value) throws Exception {
		//Build the filter
		ExtrinsicFilter filter = new ExtrinsicFilter.Builder().lid(lid).build();
		if (type.equals(ExtrinsicFilterTypes.LIDVID)) {
			//filter = new ExtrinsicFilter.Builder().lid(value.split("::")[0]).build();
			filter = new ExtrinsicFilter.Builder().lid(value).build();
		} else if (type.equals(ExtrinsicFilterTypes.GUID)) {
			filter = new ExtrinsicFilter.Builder().guid(value).build();
		} else if (type.equals(ExtrinsicFilterTypes.OBJECT_TYPE)) {
			filter = new ExtrinsicFilter.Builder().objectType(value).build();
		}
		//Create the query
		RegistryQuery<ExtrinsicFilter> query = new
		RegistryQuery.Builder<ExtrinsicFilter>().filter(filter).build();
		try {
			RegistryClient client = new RegistryClient(TseConstants.REGISTRY_URL);
					//securityContext, user, password);
			PagedResponse<ExtrinsicObject> pr = client.getExtrinsics(query, 1, TseConstants.QUERY_MAX);
			//Examine the results of the query
			/*if (pr.getNumFound() != 0) {
				for (ExtrinsicObject extrinsic : pr.getResults()) {
					for (Slot slot : extrinsic.getSlots()) {
						if (slot.getName().equals(Constants.PRODUCT_VERSION) &&
								slot.getValues().contains(version)) {
							result = extrinsic;
						}
					}
				}
			}*/
			return pr;
		} catch (RegistryServiceException rse) {
			//Ignore. Nothing found.
		} catch (RegistryClientException rce) {
			throw new Exception(rce.getMessage());
		}
		return null;
	}
	
	private PagedResponse<Association> getAssociations(String type, String value) throws Exception {
		//Build the filter
		AssociationFilter assocFilter = new AssociationFilter.Builder()
			.sourceObject(value).associationType(type).build();
		RegistryQuery<AssociationFilter> query = new RegistryQuery.Builder<AssociationFilter>()
			.filter(assocFilter).build();

		try {
			RegistryClient client = new RegistryClient(TseConstants.REGISTRY_URL);
					//securityContext, user, password);
			PagedResponse<Association> pr = client.getAssociations(query, 1, TseConstants.QUERY_MAX);
			//Examine the results of the query
			/*if (pr.getNumFound() != 0) {
				for (Association assoc : pr.getResults()) {
					System.out.println("Association found - " + assoc.getAssociationType() + " - " + assoc.getTargetObject());
					/*for (Slot slot : extrinsic.getSlots()) {
						if (slot.getName().equals(Constants.PRODUCT_VERSION) &&
								slot.getValues().contains(version)) {
							result = extrinsic;
						}
					}
				}
			}*/
			
			return pr;
		} catch (RegistryServiceException rse) {
			//Ignore. Nothing found.
		} catch (RegistryClientException rce) {
			throw new Exception(rce.getMessage());
		}
		return null;
	}
	
	private void recordMissingSlots(RegistrySlots slots) {
		if (!this.missingSlotsMap.containsKey(slots.getObjectType()) && slots.isMissingSlots())
			this.missingSlotsMap.put(slots.getObjectType(), slots.getMissingSlotList());
	}
	
	private void displayWarnings() {
		String out = "";
		if (!this.missingAssociations.isEmpty()) {
			out += "\nWARNING - Missing the following Associations:\n";
			for (Association assoc : this.missingAssociations) {
				out += "  Type: " + assoc.getAssociationType() + "\n";
				out += "  Source: " + assoc.getSourceObject() + "\n";
				out += "  Target: " + assoc.getTargetObject() + "\n\n";
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
		
		this.writer.println(out);
		System.out.println(out);
	}
	
	/** 
	 * Registry 0.2.0-dev implementation
	 * 
	 * Depending upon the responseType, sets the necessary filter
	 * and gets a RegistryResponse
	 * 
	 * @param responseType
	 * @param type
	 * @param value
	 * @return
	 * @throws ParseException 
	 * @throws DateParseException 
	 */	
	/*private RegistryResponse getResponse(int responseType, String type, String value) {

		if (responseType == ResponseTypes.EXTRINSIC) {
			ObjectFilter filter = null;
			// TODO account for version which is second part of split value
			if (type.equals(ExtrinsicFilterTypes.LIDVID)) {
				filter = new ObjectFilter.Builder().lid(value.split("::")[0])
						.build();
			} else if (type.equals(ExtrinsicFilterTypes.GUID)) {
				filter = new ObjectFilter.Builder().guid(value).build();
			} else if (type.equals(ExtrinsicFilterTypes.OBJECT_TYPE)) {
				filter = new ObjectFilter.Builder().objectType(value).build();
			}

			RegistryQuery<ExtrinsicFilter> query = new RegistryQuery<ExtrinicFilter>().Builder<ExtrinicFilter>().filter(filter)
					.build();
			return this.client.getExtrinsics(query, 1, TseConstants.QUERY_MAX)
					.getEntity(RegistryResponse.class);

		} else if (responseType == ResponseTypes.ASSOCIATION) {
			AssociationFilter assocFilter = new AssociationFilter.Builder()
					.sourceObject(value).associationType(type).build();
			AssociationQuery assocQuery = new AssociationQuery.Builder()
					.filter(assocFilter).build();
			return this.client.getAssociations(assocQuery, 1, TseConstants.QUERY_MAX).getEntity(
					RegistryResponse.class);
		}

		return null;
	}*/
	
	private List reformatDateFields(List array) throws ParseException {
		System.out.println("FOUND Date Field - " + array.get(0));
		SimpleDateFormat frmt = new SimpleDateFormat(DATE_FORMATS.get(1));
		
		List newVals = new ArrayList<String>();
		String newDate = "";
		for (Object date : array) {
				if (((String)date).matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}")) {
					newDate = (String)date + ":00";
				} else if (((String)date).matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
					newDate = (String)date + "T00:00:00";
				} else if (((String)date).equals("unknown")) {
					newDate = "UNK";
				} else {
					newDate = (String)date;
				}
				System.out.println("New Date: " + newDate);
				newVals.add(date);
		}
		return newVals;
		
	}

	private void setIdentifiers(RegistryObject object) {
		this.lid = object.getLid();
		this.guid = object.getGuid();
	}

	private void setResLocation() throws UnsupportedEncodingException {
		int start, end;
		String tval, key;

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
				tval = this.associationMap.get(values[0]).get(0).get(values[1])
						.get(0);
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
	public int getNumAttr() {
		return colNames.size();
	}

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
