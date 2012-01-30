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
import java.util.Date;
import java.util.Set;
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
import gov.nasa.pds.tools.label.Statement;
import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.label.PointerStatement;
import gov.nasa.pds.tools.util.Utility;
import gov.nasa.pds.citool.report.IngestReport;
import gov.nasa.pds.citool.file.FileObject;
import gov.nasa.pds.citool.file.MD5Checksum;

/**
 * Class to parse a PDS catalog file and to call a CatalogDB class to ingest the
 * data into DB.
 *
 * @author hlee
 */
public class CatalogObject {

	private final static String[] CAT_OBJ_TYPES = { "MISSION",
			"INSTRUMENT_HOST", "INSTRUMENT", "DATA_SET", "REFERENCE",
			"PERSONNEL", "DATA_SET_COLLECTION", "INVENTORY", "SOFTWARE",
			"TARGET", "VOLUME", "NSSDC_DATA_SET_ID", "NSSDCDSID",
			"DIS_DATA_SET", "DATA_SET_RELEASE", "DATA_SET_HOUSEKEEPING",
			"RESOURCE", "ELEMENT_DEFINITION", "OBJECT_DEFINITION" };
	
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
	
	public CatalogObject(IngestReport report) {
		this._report = report;
		this._catObjType = null;
		this._label = null;
		this._fileObj = null;
		this._pointerFiles = null;
		this._isLocal = true;
		this._filename = null;
		this._version = 1.0f;
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
	
	/**
	 * Gets the catalog object from the LIST, sets to hash map, and calls
	 * CatalogDB object to ingest the data into the database.
	 *
	 * @param objList
	 *            List of the catalog object statement(s)
	 * @param pdsLabelMap
	 *            Hashmap of the PDS label keyword and value for all ATTRIBUTE
	 */
	protected Map<String, AttributeStatement> getCatalogObj(
			List<ObjectStatement> objList, Map<String, AttributeStatement> pdsLabelMap) {
		String objType = this._catObjType;
		Map<String, AttributeStatement> lblMap = null;

		for (ObjectStatement objSmt : objList) {
			if (objType.equalsIgnoreCase("VOLUME")) {
				List<ObjectStatement> objList2 = objSmt.getObjects("CATALOG");
				List<PointerStatement> ptList = ((ObjectStatement) objList2.get(0)).getPointers();

				// need to check these files are same is lbl file sets
				_pointerFiles = new ArrayList<String>();
				for (PointerStatement ptSmt : ptList) {
					// Don't add REFERENCE_CATALOG or PERSONNEL_CATALOG
					// references
					if (!ptSmt.getIdentifier().toString().equalsIgnoreCase("REFERENCE_CATALOG")
							&& !ptSmt.getIdentifier().toString().equalsIgnoreCase("PERSONNEL_CATALOG")) {
						_pointerFiles.add(ptSmt.getValue().toString());
					}
				}
			}
			
			List<AttributeStatement> attrList = objSmt.getAttributes();
			for (AttributeStatement attrSmt : attrList) {
				pdsLabelMap.put(attrSmt.getElementIdentifier(), attrSmt);
			}
			List<ObjectStatement> objList2 = objSmt.getObjects();

			for (ObjectStatement smt2 : objList2) {
				List<AttributeStatement> objAttr = smt2.getAttributes();
				lblMap = new HashMap<String, AttributeStatement>(pdsLabelMap);
				for (AttributeStatement attrSmt : objAttr) {
					lblMap.put(attrSmt.getElementIdentifier(), attrSmt);
				}

				List<ObjectStatement> objList3 = smt2.getObjects();
				for (ObjectStatement smt3 : objList3) {
					List<AttributeStatement> objAttr2 = smt3.getAttributes();
					for (AttributeStatement attrSmt2 : objAttr2) {
						lblMap.put(attrSmt2.getElementIdentifier(), attrSmt2);
					}
				}

				// if there is no object nested third times, just copy from the
				// top level obejct
				if (lblMap == null)
					lblMap = new HashMap<String, AttributeStatement>(pdsLabelMap);

				pdsLabelMap = lblMap;
			}
		}
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
	
	public Map<String, AttributeStatement> getPdsLabelMap() {
		return this._pdsLabelMap;
	}

	/**
	 * Processes the given label and converts attributes into the hashmap
	 * object.
	 * 
	 * @param label Label object
	 */
	public void processLabel(Label label) {
		this._label = label;
		try {
			this._filename = label.getLabelURI().toURL().getFile();

			List<AttributeStatement> attrList = label.getAttributes();
			_pdsLabelMap = new HashMap<String, AttributeStatement>();
			for (Iterator i = attrList.iterator(); i.hasNext();) {
				AttributeStatement attrSmt = (AttributeStatement) i.next();
				_pdsLabelMap.put(attrSmt.getElementIdentifier(), attrSmt);
			}

			List<ObjectStatement> objList = object2List(label);
			_pdsLabelMap = getCatalogObj(objList, _pdsLabelMap);

		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		}
	}
}
