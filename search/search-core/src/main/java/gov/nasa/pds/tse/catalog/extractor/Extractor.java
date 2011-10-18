//Copyright (c) 2009, California Institute of Technology.
//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
//
// $Id$ 
//

package gov.nasa.pds.tse.catalog.extractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.net.URLEncoder;

//import gov.nasa.pds.tse.catalog.extractor.DBXMLReader;
import gov.nasa.pds.tse.catalog.ExtractionException;
//import gov.nasa.pds.tse.catalog.Extractor;

/**
 * @author pramirez
 * @modifiedby Jordan Padams
 * @version $Revision$
 * 
 */
public class Extractor { //implements Extractor {
	// Database configuration file default
	private static final String DB_CONFIG = "dbconfig.xml";

	private int oidseq = 10000;
	
	private String classname, classFilename;
	private String propName, propVal;	
	private String fkey="", fkey1="", fkey2="";
	private String fname = "", fnameprefix = "tse", fnameext = "xml";
	private String tsql, tval1, tval2, tval3;
	
	private Attributes attributes;

	private Map attrNames;
	private Map finalVals;
	
	private ArrayList valArray;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	private Connection connection1; 
	private PrintWriter xmlDisplay;
	private StringBuffer sbuff = new StringBuffer(10000);
	
	/**
	 * Initialize global variables used throughout
	 * 
	 * @param name - class of catalog to be extracted
	 * @param value - name of attribute properties file
	 */
	public Extractor(String name, String file) {
		log.fine("In Generic Extractor");

		finalVals = new HashMap();

		classname = name;
		classFilename = file;

		log.fine("Class name: " + classname);
	}

	/** (non-Javadoc)
	 * @see gov.nasa.pds.tse.catalog.Extractor#extract()
	 */	
	public List extract(File baseDir, File extractorDir) throws ExtractionException {
		log.fine("basedir : " + baseDir);
		ArrayList instkeys = new ArrayList();

		attributes = new Attributes(classFilename);
		
		try {
			/* Make connection with database */
			DBInfo dbInfo = new DBInfo(new File(baseDir,"/conf/"+DB_CONFIG));
			
			Class.forName(dbInfo.getDriver());
			connection1 = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getProperties()); 

			Statement statement1 = connection1.createStatement();

			// Display current database environment
			// This section does not work with new MySQL DB
			ResultSet rs1 = statement1.executeQuery("select db_name()");
			rs1.next();
			log.fine("database in use:" + remNull(rs1.getString(1)));
			rs1 = statement1.executeQuery("select @@servername");
			rs1.next();
			log.fine("server in use:" + remNull(rs1.getString(1)));
			rs1.close();

			//	Get class instances using attributes object
			rs1 = statement1.executeQuery(attributes.getQuery());
			while (rs1.next()) {
				oidseq++;
				fkey = remNull(rs1.getString(1));
				fkey = fkey.trim();

				// Get second foreign key if Instrument class
				if (classname.equals("Instrument")) {
					fkey2 = remNull(rs1.getString(2));
					fkey2 = fkey2.trim();	
				}
				log.fine("fkey:" + fkey);
				instkeys.add(fkey);

				//  Get class properties
				getAttrProperties(rs1);

				//  Fix up special values  according to class name
				if (classname.equals("Target")) {
					log.fine("Fixing Target");
					fixTargetVals();
				} else if (classname.equals("DataSet")) {
					log.fine("Fixing DataSet");
					fixDatasetVals();
				} else if (classname.equals("Instrument")) {
					log.fine("Fixing Instrument");
					fixInstrumentVals();
				} else if (classname.equals("InstrumentHost")) {
					log.fine("Fixing InstrumentHost");
					fixInstrumentHostVals();
				} else if (classname.equals("Mission")) {
					log.fine("Fixing Mission");
					fixMissionVals();
				} else if (classname.equals("Node")) {
					log.fine("Fixing Node");
					fixNodeVals();
				}
				
				XMLWriter xml = new XMLWriter(finalVals, extractorDir, oidseq, classname);
				
				// Create the XML file
				//createXML(extractorDir);
			}
			rs1.close();
			connection1.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName() + ex.getMessage());
		}

		return instkeys;
	}

	/**
	 * Creates the XML file using the properties specified
	 * @param baseDir - directory where the new XML should be placed
	 */
	private void createXML(File baseDir) {
		try {
			Integer oidseqi = new Integer( oidseq );
			String itemoid = oidseqi.toString();

			/* Start profile output */
			fname = fnameprefix + "_" + classname +  "_" + itemoid + "." + fnameext;
			xmlDisplay = new PrintWriter(new BufferedWriter(new FileWriter(new File(baseDir, fname), false)));
			printFileHeader();

			Set set2 = finalVals.keySet();
			Iterator iter2 = set2.iterator();
			while(iter2.hasNext()) {
				propName = (String) iter2.next();
				valArray = (ArrayList) finalVals.get(propName);
				for (Iterator i = valArray.iterator(); i.hasNext();) {
					propVal = (String) i.next();
					printXml(propName,propVal,isCleanedAttr(propName));
				}
			}
			printFileFooter();
			xmlDisplay.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName() + ex.getMessage());
		}
	}

	/**
	 * Get all of the attributes and their values and place them into a HashMap, valArray.
	 * The HashMap is made of of attrName->value pairs.  The value in the pair depends upon
	 * the current attribute's index, where it is either the value in attrVals or a value
	 * queried from the database.
	 * @param ResultSet - Object that contains the results from the instance query
	 * 				for the current class.
	 */
	private void getAttrProperties(ResultSet rs1) {
		try {
			/* Initialize local variables */
			String currName, currVal;
			int currIndex;
			int numAttr;

			Statement statement2 = connection1.createStatement();

			//numAttr = getNumAttr();

			// Loop through class results beginning from top
			for (int i = 0; i < attributes.getNumAttr(); i++) {				
				currName = attributes.getName(i);
				currIndex = attributes.getIndex(i);
				currVal = attributes.getValue(i);
				
				
				if (currIndex >= 0) {
					// If currIndex==0 then current attribute equals current value
					if (currIndex == 0) {
						valArray = new ArrayList();
						valArray.add(currVal);		
						finalVals.put(currName,valArray);
					} else {	// Else query the database using the index as its column number
						tval1 = remNull(rs1.getString(currIndex));  /* For example, in the Target class, the attribute target_name has
																		index of 1 when database is queried */
						tval1 = tval1.trim();
						valArray = new ArrayList();
						valArray.add(tval1);		
						finalVals.put(currName,valArray);
					}
				} else if (currIndex == -1) {	/* If currIndex==-1 then currVal contains a database query.  Replace the # in the query
													with the value of the first field of the instance query */
					tsql = currVal;
					tsql = repCharWStr(tsql, '#', fkey);
					ResultSet rs2 = statement2.executeQuery(tsql);
					valArray = new ArrayList();
					while (rs2.next()) {
						tval1 = remNull(rs2.getString(1));
						tval1 = tval1.trim();
						valArray.add(tval1);
					}
					finalVals.put(currName,valArray);
					rs2.close();
				} else if (currIndex == -2) {	/* If currIndex==-2 then currVal contains a database query.  Replace the # in currVal
													with the value of the first field of the instance query */
					tval2 = "";
					fkey1 = repCharWStr(fkey, '\'', "_");
					tsql = currVal;
					tsql = repCharWStr(tsql, '#', fkey1);

					// Special case where @ in currVal is replaced with the second field of the instance query
					if (classname.equals("Instrument"))
						tsql = repCharWStr(tsql, '@', fkey2);

					sbuff.setLength(0);
					log.fine("tsql: " + tsql);
					ResultSet rs2 = statement2.executeQuery(tsql);

					// Appends a break to any results where more than one record is queried.
					while (rs2.next()) {
						sbuff.append(remNull(rs2.getString(1)));
						if (!(rs2.isFirst())) {
							sbuff.append("   <br />");
						}
					}

					valArray = new ArrayList();
					valArray.add(sbuff.toString());		
					finalVals.put(currName,valArray);

					rs2.close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName() + ex.getMessage());
		}
	}

	/**
	 * Modify values specific to the Target class
	 * 
	 * Modifications:
	 * 	identifier = target_name
	 * 	title = target_name
	 *  description = target_desc
	 * 	Substitute # with target_desc in resLocation URL or other URL depending upon value of sbn_target_locator
	 */
	private void fixTargetVals() {
		try {
			valArray = (ArrayList) finalVals.get("target_name");
			tval1 = (String) valArray.get(0);
			finalVals.put("identifier",valArray);
			finalVals.put("title",valArray);		
			valArray = (ArrayList) finalVals.get("target_desc");				
			finalVals.put("description",valArray);
			valArray = (ArrayList) finalVals.get("resLocation");	
			tval2 = (String) valArray.get(0);
			
			// Encode attribute value before adding it to URL
			String ts = repCharWStr(tval2, '#', URLEncoder.encode(tval1,"UTF-8"));
			
			valArray = new ArrayList();
			valArray.add(ts);		
			finalVals.put("resLocation",valArray);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName() + ex.getMessage());
		}
	}

	/**
	 * Modify values specific to the Dataset class
	 * 
	 * Modifications:
	 * 	identifier = data_set_id
	 * 	title = data_set_name
	 *  description = data_set_terse_desc
	 * 	Substitute # with data_set_terse_desc in resLocation URL
	 */
	private void fixDatasetVals() {
		try {
			valArray = (ArrayList) finalVals.get("data_set_id");	
			tval1 = (String) valArray.get(0);
			finalVals.put("identifier",valArray);
			valArray = (ArrayList) finalVals.get("data_set_name");				
			finalVals.put("title",valArray);		
			valArray = (ArrayList) finalVals.get("data_set_terse_desc");				
			finalVals.put("description",valArray);		
			valArray = (ArrayList) finalVals.get("resLocation");	
			tval2 = (String) valArray.get(0);
			
			// Encode attribute value before adding it to URL
			String ts = repCharWStr(tval2, '#', URLEncoder.encode(tval1,"UTF-8"));
			
			valArray = new ArrayList();
			valArray.add(ts);		
			finalVals.put("resLocation",valArray);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName() + ex.getMessage());
		}
	}

	/**
	 * Modify values specific to the Instrument class
	 * 
	 * Modifications:
	 * 	identifier = instrument_name
	 * 	title = instrument_name
	 *  description = instrument_desc
	 * 	Substitute # with instrument_id and instrument_host_id information in resLocation URL
	 */
	private void fixInstrumentVals() {
		try {
			valArray = (ArrayList) finalVals.get("instrument_name");	
			finalVals.put("identifier",valArray);				
			finalVals.put("title",valArray);		
			valArray = (ArrayList) finalVals.get("instrument_desc");				
			finalVals.put("description",valArray);		

			valArray = (ArrayList) finalVals.get("resLocation");	
			tval3 = (String) valArray.get(0);

			valArray = (ArrayList) finalVals.get("instrument_id");	
			tval1 = (String) valArray.get(0);

			valArray = (ArrayList) finalVals.get("instrument_host_id");	
			tval2 = (String) valArray.get(0);

			// Encode attribute value before adding it to URL
			tval3 = repCharWStr(tval3, '#', URLEncoder.encode(tval1,"UTF-8"));
			tval3 += "&INSTRUMENT_HOST_ID=#";
			tval3 = repCharWStr(tval3, '#', URLEncoder.encode(tval2,"UTF-8"));

			valArray = new ArrayList();
			valArray.add(tval3);		
			finalVals.put("resLocation",valArray);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName() + ex.getMessage());
		}
	}

	/**
	 * Modify values specific to the Instrument Host class
	 * 
	 * Modifications:
	 * 	identifier = instrument_host_name
	 * 	title = instrument_host_name
	 *  description = host_desc
	 * 	Substitute # with instrument_host_id in resLocation URL
	 */
	private void fixInstrumentHostVals() {
		try {
			valArray = (ArrayList) finalVals.get("instrument_host_name");	
			finalVals.put("identifier",valArray);
			finalVals.put("title",valArray);		
			valArray = (ArrayList) finalVals.get("host_desc");				
			finalVals.put("description",valArray);		
			valArray = (ArrayList) finalVals.get("instrument_host_id");				
			tval1 = (String) valArray.get(0);
			valArray = (ArrayList) finalVals.get("resLocation");	
			tval2 = (String) valArray.get(0);
			
			// Encode attribute value before adding it to URL
			String ts = repCharWStr(tval2, '#', URLEncoder.encode(tval1,"UTF-8"));
			
			valArray = new ArrayList();
			valArray.add(ts);		
			finalVals.put("resLocation",valArray);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName() + ex.getMessage());
		}
	}

	/**
	 * Modify values specific to the Mission class
	 * 
	 * Modifications:
	 * 	identifier = mission_name
	 * 	title = mission_name
	 *  description = mission_desc
	 * 	Substitute # with mission_desc in resLocation URL
	 */
	private void fixMissionVals() {
		try {
			valArray = (ArrayList) finalVals.get("mission_name");	
			tval1 = (String) valArray.get(0);
			finalVals.put("identifier",valArray);
			finalVals.put("title",valArray);		
			valArray = (ArrayList) finalVals.get("mission_desc");				
			finalVals.put("description",valArray);				
			valArray = (ArrayList) finalVals.get("resLocation");	
			tval2 = (String) valArray.get(0);
			
			// Encode attribute value before adding it to URL
			String ts = repCharWStr(tval2, '#', URLEncoder.encode(tval1,"UTF-8"));
			
			valArray = new ArrayList();
			valArray.add(ts);		
			finalVals.put("resLocation",valArray);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName() + ex.getMessage());
		}	
	}

	/**
	 * Modify values specific to the Node class.
	 * 
	 * Modifications:
	 * 	identifier = node_name
	 * 	title = node_name
	 * 	Substitute # with node_id in resLocation URL
	 */
	private void fixNodeVals() {
		try {
			valArray = (ArrayList) finalVals.get("node_name");
			finalVals.put("identifier",valArray);
			finalVals.put("title",valArray);
			valArray = (ArrayList) finalVals.get("node_id");	
			tval1 = (String) valArray.get(0);
			valArray = (ArrayList) finalVals.get("resLocation");	
			tval2 = (String) valArray.get(0);
			
			// Encode attribute value before adding it to URL
			String ts = repCharWStr(tval2, '#', URLEncoder.encode(tval1,"UTF-8"));
			
			valArray = new ArrayList();
			valArray.add(ts);		
			finalVals.put("resLocation",valArray);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.warning("Exception " + ex.getClass().getName() + ex.getMessage());
		}
	}

	/**
	 * Find the number of attributes
	 * 
	 * @return attrNames.size() - number of names in attrNames Map
	 */
	public int getNumAttr() {
		return attrNames.size();
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
		xmlDisplay.println("\t</"+ classname +">");
		xmlDisplay.println("</doc>");
	}

	/**
	 * Display the field name and value in XML format.
	 * 
	 * @param name - name of field
	 * @param value - value for specific record
	 */
	public void printXml (String name, String value, boolean clean) {
		// Temporary variables to hold name and value
		String tName, tValue;  

		tName = name.trim(); 
		tValue = value.trim();

		// Utilize JTidy servlet extension to encode all non-letter characters
		//tName = HTMLEncode.encode(tName);
		//tValue = HTMLEncode.encode(tValue);

		if (clean) {
			tValue = tValue.toLowerCase();
			tValue = tValue.replace(' ','_');
		}
		
		// Previous method used to encode only select HTML entities
		tName = repAllCharWStr(tName);
		tValue = repAllCharWStr(tValue);

		xmlDisplay.println("\t\t<"+tName+">" + tValue + "</"+tName+">");
	}

	/**
	 *  Replace HTML entities & with &amp;, < with &lt;, > with &gt;, and " with &quot;  
	 */
	public String repAllCharWStr (String s1) {
		String s2;

		s2 = repCharWStr(remNull(s1), '&', "&amp;");
		s2 = repCharWStr(s2, '<', "&lt;");
		s2 = repCharWStr(s2, '>', "&gt;");
		s2 = repCharWStr(s2, '\"', "&quot;");
		return s2;
	}
	
	/**
	 * Remove String Nulls
	 */
	public String remNull (String s1)
	{ 	
		if (s1 == null) {
			return "NULL";
		}
		return s1;
	}

	/**
	 * Replace character with string
	 */
	public String repCharWStr (String str1, char rc, String rstr) {
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
	
	public boolean isCleanedAttr (String s1) {
		String [] elemfacet = {
			"identifier",
			"title",
			"format",
			"description",
			"publisher",
			"language",
			"resContext",
			"resClass",
			"resLocation",
			"data_set_terse_desc", 
			"data_set_desc",
			"mission_desc",
			"target_desc",
			"host_desc",
			"instrument_desc",
			"volume_name",
			"volume_desc"};        
		
		for (int ind = 0; ind < elemfacet.length; ind++) {
			if (s1.compareTo(elemfacet[ind]) == 0) {
				return false;
			}
		}
		return true;
	}   

}
