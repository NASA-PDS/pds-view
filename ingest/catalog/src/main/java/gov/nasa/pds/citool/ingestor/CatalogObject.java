// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.citool.ingestor;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
//import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.parser.LabelParser;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.label.Numeric;
import gov.nasa.pds.tools.label.Scalar;
import gov.nasa.pds.tools.label.Set;
import gov.nasa.pds.tools.label.Sequence;
import gov.nasa.pds.tools.label.Statement;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.tools.label.Value;
import gov.nasa.pds.tools.util.Utility;
import gov.nasa.pds.tools.containers.FileReference;

import gov.nasa.pds.citool.report.IngestReport;
import gov.nasa.pds.citool.file.FileObject;
import gov.nasa.pds.citool.file.MD5Checksum;
import gov.nasa.pds.citool.CIToolIngester;
import gov.nasa.pds.registry.model.ExtrinsicObject;

import org.apache.oodt.cas.metadata.Metadata;

/**
 * Class to parse a PDS catalog file 
 *
 * @author hlee
 */
public class CatalogObject {
/*
	private final static String[] CAT_OBJ_TYPES = { "MISSION",
			"INSTRUMENT_HOST", "INSTRUMENT", "DATA_SET", "REFERENCE",
			"PERSONNEL", "DATA_SET_COLLECTION", "INVENTORY", "SOFTWARE",
			"TARGET", "VOLUME", "NSSDC_DATA_SET_ID", "NSSDCDSID",
			"DIS_DATA_SET", "DATA_SET_RELEASE", "DATA_SET_HOUSEKEEPING",
			"RESOURCE", "ELEMENT_DEFINITION", "OBJECT_DEFINITION" };
*/
	private final static String[] CAT_OBJ_TYPES = { "MISSION",
		"INSTRUMENT_HOST", "INSTRUMENT", "DATA_SET", "REFERENCE",
		"PERSONNEL", "TARGET", "VOLUME" };
		
	private String _catObjType;
	private boolean _isLocal;
	private IngestReport _report;
	private Label _label;
	private Map<String, AttributeStatement> _pdsLabelMap;
	private List<Reference> _references;
	private FileObject _fileObj;
	private List<String> _pointerFiles;
	private String _filename;
	private float _version;
	private ExtrinsicObject _product;
	//private List<PointerCatalog> _pointers;
	private Metadata _metadata;
	//private Map<String, String> _refInfo;
	
	public CatalogObject(IngestReport report) {
		this._report = report;
		this._catObjType = null;
		this._label = null;
		this._fileObj = null;
		this._pointerFiles = null;
		this._isLocal = true;
		this._filename = null;
		this._version = 1.0f;
		this._product = null;
		this._metadata = null;
	}
	
	public String getFilename() {
		return this._filename;
	}
	
	public Label getLabel() {
		return this._label;
	}

	public String getCatObjType() {
		return _catObjType;
	}
	
	public void setIsLocal(boolean local) {
		this._isLocal = local;
	}
	
	public boolean getIsLocal() {
		return this._isLocal;
	}

	public void setReferences(List<Reference> refs) {
		this._references = refs;
	}
	
	public List<Reference> getReferences() {
		return this._references;
	}
	
	public void setVersion(float version) {
		this._version = version;
	}
	
	public float getVersion() {
		return this._version;
	}
	
	public Metadata getMetadata() {
		return this._metadata;
	}
	
	/**
	 * Set a file object 
	 */
	public void setFileObject() {
		try {
		File product = new File(_label.getLabelURI().toURL().getFile());
        
        SimpleDateFormat format = new SimpleDateFormat(
        	"yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'");      
        String lastModified = format.format(new Date(product.lastModified()));
        
        // Create a file object of the label file
        _fileObj = new FileObject(product.getName(),
        		product.getParent(), product.length(),
        	    lastModified, MD5Checksum.getMD5Checksum(product.toString()));
        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public FileObject getFileObject() {
		return this._fileObj;
	}
	
	public List<String> getPointerFiles() {
		return this._pointerFiles;
	}
	
	//public Map<String, String> getRefInfo() {
	//	return this._refInfo;
	//}
	
	public ExtrinsicObject getExtrinsicObject() {
		return this._product;
	}
	
	public void setExtrinsicObject(ExtrinsicObject product) {
		this._product = product;
	}
	
	/**
	 * Gets the catalog object from the LIST, sets to hash map 
	 *
	 * @param objList
	 *            List of the catalog object statement(s)
	 * @param pdsLabelMap
	 *            Hashmap of the PDS label keyword and value for all ATTRIBUTE
	 */
	protected Map<String, AttributeStatement> getCatalogObj(
			List<ObjectStatement> objList,
			Map<String, AttributeStatement> pdsLabelMap) {
		String objType = this._catObjType;
		Map<String, AttributeStatement> lblMap = null;

		// first level catalog object
		for (ObjectStatement objSmt : objList) {
			//System.out.println("CATALOG object type = " + objType);
			if (objType.equalsIgnoreCase("VOLUME")) {
				//List<ObjectStatement> objList2 = objSmt.getObjects("CATALOG");
				List<ObjectStatement> objList2 = objSmt.getObjects();
								
				for (ObjectStatement objSmt2: objList2) {
					//System.out.println("object name = " + objSmt2.getIdentifier());
					
					// how to handle multiple CATALOG objects???? or DATA_PRODUCER
					if (objSmt2.getIdentifier().toString().equalsIgnoreCase("CATALOG")) {
						List<PointerStatement> ptList = ((ObjectStatement) objList2.get(0)).getPointers();
						_pointerFiles = new ArrayList<String>();
						for (PointerStatement ptSmt : ptList) {
							if (ptSmt.hasMultipleReferences()) {
								//if (!ptSmt.getIdentifier().toString().equalsIgnoreCase("REFERENCE_CATALOG")
								//		&& !ptSmt.getIdentifier().toString().equalsIgnoreCase("PERSONNEL_CATALOG")) {
								List<FileReference> refFiles = ptSmt.getFileRefs();
								for (FileReference fileRef : refFiles) {
									_pointerFiles.add(fileRef.getPath());
									//System.out.println("pointer file = " + fileRef.getPath());
								}
								//}
							} else {
								// Don't add REFERENCE_CATALOG or PERSONNEL_CATALOG
								// references
								//if (!ptSmt.getIdentifier().toString().equalsIgnoreCase("REFERENCE_CATALOG")
								//		&& !ptSmt.getIdentifier().toString().equalsIgnoreCase("PERSONNEL_CATALOG")) {
								_pointerFiles.add(ptSmt.getValue().toString());
								//System.out.println("pointer file = " + ptSmt.getValue());
								//}
							}
						}
					}
				}
			}
			List<AttributeStatement> attrList = objSmt.getAttributes();
			String keyId = null;
			String keyDesc = null;
			for (AttributeStatement attrSmt : attrList) {
				pdsLabelMap.put(attrSmt.getElementIdentifier(), attrSmt);
				//System.out.println(attrSmt.getElementIdentifier() + " = " + attrSmt.getValue().toString());
				
				// multivalues
				if (attrSmt.getValue() instanceof Set) {
					List<String> valueList = getValueList(attrSmt.getValue());
					for (int i=0; i<valueList.size(); i++) {
						_metadata.addMetadata(attrSmt.getElementIdentifier(), valueList.get(i));
					}
				}
				else {
					_metadata.addMetadata(attrSmt.getElementIdentifier(), attrSmt.getValue().toString());
					//System.out.println(attrSmt.getElementIdentifier() + " is added into the metadata...");
				    if (attrSmt.getElementIdentifier().equals("REFERENCE_KEY_ID"))
				    	keyId = attrSmt.getValue().toString();
				    
				    if (attrSmt.getElementIdentifier().equals("REFERENCE_DESC"))
				    	keyDesc = attrSmt.getValue().toString();
				}
			}
			if (objType.equalsIgnoreCase("REFERENCE"))
				CIToolIngester.refInfo.put(keyId, keyDesc);

			if (!objType.equalsIgnoreCase("VOLUME")) {
				// second nested level
				List<ObjectStatement> objList2 = objSmt.getObjects();
				for (ObjectStatement smt2 : objList2) {
					List<AttributeStatement> objAttr = smt2.getAttributes();
					lblMap = new HashMap<String, AttributeStatement>(pdsLabelMap);

					//System.out.println("2nd object name = " + smt2.getIdentifier());
					// TODO: how to handle same keywords in different
					// object?????
					for (AttributeStatement attrSmt : objAttr) {
						lblMap.put(attrSmt.getElementIdentifier(), attrSmt);
						//System.out.println(attrSmt.getElementIdentifier() + " = " + attrSmt.getValue().toString());
						if (attrSmt.getValue() instanceof Set) {
							List<String> valueList = getValueList(attrSmt.getValue());
							for (int i=0; i<valueList.size(); i++) {
								_metadata.addMetadata(attrSmt.getElementIdentifier(), valueList.get(i));
							}
						}
						else 
							_metadata.addMetadata(attrSmt.getElementIdentifier(), attrSmt.getValue().toString());
						
					}

					// third nested level
					List<ObjectStatement> objList3 = smt2.getObjects();
					for (ObjectStatement smt3 : objList3) {
						List<AttributeStatement> objAttr2 = smt3.getAttributes();
						//System.out.println("3rd object name = " + smt3.getIdentifier());
						for (AttributeStatement attrSmt2 : objAttr2) {
							lblMap.put(attrSmt2.getElementIdentifier(), attrSmt2);
							//System.out.println(attrSmt2.getElementIdentifier() + " = " + attrSmt2.getValue().toString());
							if (attrSmt2.getValue() instanceof Set) {
								List<String> valueList = getValueList(attrSmt2.getValue());
								for (int i=0; i<valueList.size(); i++) {
									_metadata.addMetadata(attrSmt2.getElementIdentifier(), valueList.get(i));
								}
							}
							else 
								_metadata.addMetadata(attrSmt2.getElementIdentifier(), attrSmt2.getValue().toString());
						}
					}

					// if there is no object nested third times, just copy from
					// the
					// top level obejct
					if (lblMap == null)
						lblMap = new HashMap<String, AttributeStatement>(pdsLabelMap);

					pdsLabelMap = lblMap;
				}
			}
		}
		
		//if (CIToolIngester.refInfo.size()>0) 
		//	System.out.println("refInfo = " + CIToolIngester.refInfo.toString());
		
		return pdsLabelMap;
	}

	/**
	 * Converts a label object to List type
	 *
	 * @param label
	 *            Label object
	 */
	protected List<ObjectStatement> object2List(Label label) {
		List<ObjectStatement> objList = null;
		for (int i = 0; i < CAT_OBJ_TYPES.length; i++) {
			if (label.getObjects(CAT_OBJ_TYPES[i]).size() > 0) {
				_catObjType = CAT_OBJ_TYPES[i];
				objList = label.getObjects(CAT_OBJ_TYPES[i]);
			}
		}
		return objList;
	}

	private boolean isValidCatalogFile(String objType) {
		for (int i=0; i<CAT_OBJ_TYPES.length; i++) {
			if (CAT_OBJ_TYPES[i].equals(objType)) {
				return true;
			}
		}
		return false;
	}
		
	public Map<String, AttributeStatement> getPdsLabelMap() {
		return this._pdsLabelMap;
	}

	/**
	 * Processes the given label and converts attributes into the hashmap
	 * object.
	 * 
	 * @param label Label object
	 */
	public boolean processLabel(Label label) {
		this._label = label;
		boolean isValid = true;
		try {
			this._filename = label.getLabelURI().toURL().getFile();
			this._metadata = new Metadata();
			
			List<AttributeStatement> attrList = label.getAttributes();
			_pdsLabelMap = new HashMap<String, AttributeStatement>();
			for (Iterator i = attrList.iterator(); i.hasNext();) {
				AttributeStatement attrSmt = (AttributeStatement) i.next();
				_pdsLabelMap.put(attrSmt.getElementIdentifier(), attrSmt);
				_metadata.addMetadata(attrSmt.getElementIdentifier(), attrSmt.getValue().toString());
			}

			List<ObjectStatement> objLists = label.getObjects();
			for (Iterator i=objLists.iterator(); i.hasNext();) {
				ObjectStatement objSmt = (ObjectStatement) i.next();
				//System.out.println("object = " + objSmt.getIdentifier());
				isValid = isValidCatalogFile(objSmt.getIdentifier().toString());
				//System.out.println("isValidCatalogFile = " + isValidCatalogFile(objSmt.getIdentifier().toString()));
			}

			if (isValid) {			
				List<ObjectStatement> objList = object2List(label);			
				//System.out.println("catalog object type = " + _catObjType);			
				_pdsLabelMap = getCatalogObj(objList, _pdsLabelMap);
			}	
			
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		}
		return isValid;
	}
	
	/**
	 * Determine the given string value is a multivalued or not
	 * 
	 * @return list of string value if the given value is multivalued (surrounding within { ... })
	 * 	otherwise, null value is returned.
	 */
	private List<String> getValueList(Value value) {

		List<String> valueList = new ArrayList<String>();
		if (value instanceof Set) {			
			Iterator<Scalar> it = ((Set) value).iterator();
			while (it.hasNext()) {
				final String strValue = it.next().toString();          
            	valueList.add(strValue);
			}
		}
		else if (value instanceof Sequence) {			
			// TODO: add algorithm here
		}
		else 
			return null;
        return valueList;
	}
}
