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

import gov.nasa.pds.citool.ingestor.Constants;
import gov.nasa.pds.citool.ingestor.Reference;
import gov.nasa.pds.citool.file.FileObject;

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.client.SecurityContext;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.query.AssociationFilter;

import org.apache.oodt.cas.metadata.Metadata;
import org.apache.commons.io.FilenameUtils;

import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;
import java.net.URL;

public class CatalogRegistryIngester {
	
	public static int fileObjCount = 0;
	public static int storageCount = 0;
	public static int registryCount = 0;
	public static int failCount = 0;
	
	private ExtrinsicObject product, latestProduct;
	private RegistryClient client;
	private String transportURL;
	private StorageIngester storageIngester;
	private String storageProductName;
	//private String productLid;
		
	/**
	 * Constructor
	 * @param registryURL The URL to the registry service
	 * 
	 */
	public CatalogRegistryIngester(String registryURL) {		
		try {
			initialize();
			client = new RegistryClient(registryURL, null, null, null);
		} catch (RegistryClientException rce) {
			System.err.println("RegistryClientException occurred..." + rce.getMessage());
		}	
	}
	
	public CatalogRegistryIngester(String registryURL, SecurityContext securityContext,
			String username, String password) {
		try {
			initialize();
			client = new RegistryClient(registryURL, securityContext, username, password);
		} catch (RegistryClientException rce) {
			System.err.println("RegistryClientException occurred..." + rce.getMessage());
		}
	}
	
	private void initialize() {
		this.product = null;
		this.latestProduct = null;
		this.transportURL = null;
		this.storageProductName = null;
		//this.productLid = null;
		this.storageIngester = null;
	}
	
	public StorageIngester getStorageIngester() {
		return this.storageIngester;
	}
	
	public void setStorageService(String storageURL, String productName) {
		try {
			storageIngester = new StorageIngester(new URL(storageURL));
			if (productName != null) {
				storageIngester.setProductName(productName);
				this.storageProductName = productName;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setTransportURL(String transportURL) {
		this.transportURL = transportURL;
	}
	
	public String ingest(CatalogObject catObj) {
		// ingest an extrinsic object to the registry service
		String productGuid = ingestExtrinsicObject(catObj);
		
		if (productGuid != null) registryCount++;
		
		// ingest a file object to the registry service
		String fileObjGuid = ingestFileObject(catObj);
		if (fileObjGuid != null) fileObjCount++;

		return productGuid;
	}
	
	public String ingestFileObject(CatalogObject catObj) {
		// initialize a FileObject for given CatalogObject
		catObj.setFileObject();
		String guid = null;	
		LabelParserException lp = null;
		ExtrinsicObject fileExtrinsic = null;
		try {
			fileExtrinsic = createProduct(catObj.getFileObject());	
			// retrieve the version info from the registry service so that it can use for the storage service version 
			if (catObj.getCatObjType().equalsIgnoreCase("PERSONNEL")
					|| catObj.getCatObjType().equalsIgnoreCase("REFERENCE")) {
				if (productExists(fileExtrinsic.getLid())) {
					catObj.setVersion(Float.valueOf(latestProduct.getVersionName()).floatValue()+1.0f);
				} else {
					catObj.setVersion(1.0f);
				}			
			}
			// ingest to the storage service
			String productId = storageIngester.ingestToStorage(catObj);
			if (productId != null) {
				storageCount++;
				lp = new LabelParserException(catObj.getLabel().getLabelURI(),
						null, null, "ingest.text.recordAdded",
						ProblemType.SUCCEED,
						"Successfully ingested to the storage service. productID - "
								+ productId);
				catObj.getLabel().addProblem(lp);
			}

			// sets the storage product id to the file object,
			// so that it can be added as the slot value for the registry
			catObj.getFileObject().setStorageServiceProductId(productId);
			catObj.getFileObject().setAccessUrl(transportURL + productId);
				
			// fileobject registration
			fileExtrinsic.getSlots().add(new Slot("storage_service_productId", 
	        			Arrays.asList(new String[] { productId })));
	        fileExtrinsic.getSlots().add(new Slot("access_url", 
	        			Arrays.asList(new String[] { transportURL + productId })));

			if (productExists(fileExtrinsic.getLid())) {
				guid = client.versionObject(fileExtrinsic);
			} else {
				guid = client.publishObject(fileExtrinsic);
			}
			
			catObj.getReferences().add(new Reference(fileExtrinsic.getLid(), String.valueOf(catObj.getVersion()), Constants.HAS_FILE));
			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
	  				"ingest.text.recordAdded", ProblemType.SUCCEED,
	  				"Successfully ingested a file object. GUID - " + guid);
	  		catObj.getLabel().addProblem(lp);
	  		
		} catch (RegistryServiceException re) {
			// throw new IngestException(re.getMessage());
			re.printStackTrace();
			System.err.println("Error occurred..." + re.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return guid;		
	}
	
	/**
	 * Ingest an extrinsic object to the registry service
	 * 
	 * @param catObj  a catalog object
	 * @return the guid of the registered extrinsic object 
	 * 
	 */
	public String ingestExtrinsicObject(CatalogObject catObj) {
		Map<String, AttributeStatement> pdsLabelMap = catObj.getPdsLabelMap();
		String objType = catObj.getCatObjType();
		String guid = null;
		LabelParserException lp = null;
		try {				
			ExtrinsicObject product = createProduct(pdsLabelMap, objType);			
			if (productExists(product.getLid())) {
				guid = client.versionObject(product);
				catObj.setVersion(Float.valueOf(latestProduct.getVersionName()).floatValue()+1.0f);
			}
			else {
				guid = client.publishObject(product);
				catObj.setVersion(1.0f);
			}
			//product.setGuid(guid);	  
			
			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
	  				"ingest.text.recordAdded", ProblemType.SUCCEED,
	  				"Successfully registed product - " + product.getLid()+"::" + catObj.getVersion());
	  		catObj.getLabel().addProblem(lp);
	  		
	  		lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
	  				"ingest.text.recordAdded", ProblemType.SUCCEED,
	  				"Product has the following GUID - " + guid);
	  		catObj.getLabel().addProblem(lp);
	  		
        } catch (RegistryServiceException re) {
        	lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
  		} catch (RegistryClientException rce) {
  			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
  		} catch (Exception e) {
  			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
  		}
  		
		return guid;
	}
	
	public ExtrinsicObject getExtrinsicObject() {
		return this.product;
	}
		
	/**
	   * Determines whether a product is already in the registry.
	   *
	   * @param lid The PDS4 logical identifier.
	   *
	   * @return 'true' if the logical identifier was found in the registry.
	   * 'false' otherwise.
	   *
	   * @throws RegistryClientException exception ignored.
	**/
	public boolean productExists(String lid) throws RegistryClientException {
		try {
			client.setMediaType("application/xml");
			latestProduct = client.getLatestObject(lid,ExtrinsicObject.class);
			return true;
		} catch (RegistryServiceException re) {
			// Do nothing
			//re.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Create an extrinsic object
	 * 
	 * @param pdsLabelMap hashmap that contains metadata of the pds label
	 * @param objType    a catalog object type
	 * @return an extrinsic object
	 *  
	 */
	private ExtrinsicObject createProduct(Map<String, AttributeStatement> pdsLabelMap, String objType)
		throws  RegistryServiceException {
		ExtrinsicObject product = new ExtrinsicObject();
		Set<Slot> slots = new HashSet<Slot>();
		String productLid = null;
		
    	for (Map.Entry<String,AttributeStatement> entry: pdsLabelMap.entrySet()) {
    		String key = entry.getKey();
    		String value = ((AttributeStatement)entry.getValue()).getValue().toString();
   
			List<String> values = new ArrayList<String>();
			List<String> tmpVals = new ArrayList<String>();
			if (objType.equalsIgnoreCase(Constants.MISSION_OBJ) && key.equals("MISSION_NAME")) {
				// need to replacce empty space with _
				if (value.contains(" "))
    				value = value.replace(' ', '_');
				productLid = Constants.LID_PREFIX+"mission."+value;
				product.setLid(productLid);
				product.setObjectType(Constants.MISSION_PROD);
				product.setName(value);	
				tmpVals.add("Context.Investigation");
				slots.add(new Slot("product_subclass", tmpVals));
			}
			else if (objType.equalsIgnoreCase(Constants.TARGET_OBJ) && key.equals("TARGET_NAME")) {
				// may need to replace " " to "_" ????
				productLid = Constants.LID_PREFIX+"target."+value;
				product.setLid(productLid);
				product.setObjectType(Constants.TARGET_PROD);
				product.setName(value);
				tmpVals.add("Context.Target");
				slots.add(new Slot("product_subclass", tmpVals));
			}
			else if (objType.equalsIgnoreCase(Constants.INST_OBJ) && key.equals("INSTRUMENT_ID")) {
				String instHostId = ((AttributeStatement)pdsLabelMap.get("INSTRUMENT_HOST_ID")).getValue().toString();
				productLid = Constants.LID_PREFIX+"instrument."+value+"__" + instHostId;
				product.setLid(productLid);
				product.setObjectType(Constants.INST_PROD);
				product.setName(value);
				tmpVals.add("Context.Instrument");
				slots.add(new Slot("product_subclass", tmpVals));
			}
			else if (objType.equalsIgnoreCase(Constants.INSTHOST_OBJ) && key.equals("INSTRUMENT_HOST_ID")) {
				productLid = Constants.LID_PREFIX+"instrument_host."+value;
				product.setLid(productLid);
				product.setObjectType(Constants.INSTHOST_PROD);
				product.setName(value);
				tmpVals.add("Context.Instrument_Host");
				slots.add(new Slot("product_subclass", tmpVals));
			}
			else if (objType.equalsIgnoreCase(Constants.DATASET_OBJ) && key.equals("DATA_SET_ID")) {
				productLid = Constants.LID_PREFIX+"data_set."+value;
				product.setLid(productLid);
				product.setObjectType(Constants.DS_PROD);
				product.setName(value);
				tmpVals.add("Context.DataSet");
				slots.add(new Slot("prudct_subclass", tmpVals));
			}
			else if (objType.equalsIgnoreCase(Constants.RESOURCE_OBJ) && key.equals("RESOURCE_ID")) {
				///??? value should be "<DATA_SET_ID>__<RESOURCE_ID>????
				if (value.contains("/"))
    				value = value.replace('/', '-');
				productLid = Constants.LID_PREFIX+"resource."+value;
				product.setLid(productLid);
				product.setObjectType(Constants.RESOURCE_PROD);
				product.setName(value); //need to get from RESOURCE_NAME????
				tmpVals.add("Context.Resource");
				slots.add(new Slot("product_subclass", tmpVals));
			}
			else if (objType.equalsIgnoreCase(Constants.VOLUME_OBJ) && key.equals("VOLUME_ID")) {
				productLid = Constants.LID_PREFIX+"volume."+value;
				product.setLid(productLid);
				product.setObjectType(Constants.VOLUME_PROD);
				product.setName(value);
				tmpVals.add("Context.Volume");
				slots.add(new Slot("product_subclass", tmpVals));
			}
			//??????
			/*else if (objType.equalsIgnoreCase("PERSONNEL") && key.equals("PDS_USER_ID")) {
				product.setLid(Constants.LID_PREFIX+"personnel."+value);
				product.setObjectType(Constants.)
			}*/
			
            values.add(value);              

			if (getKey(key) != null) 
				slots.add(new Slot(getKey(key), values));
		}
		product.setSlots(slots);
		this.product = product;
		
		return product;
	}
	
	/**
	 * Create an extrinsic object with the file object
	 * 
	 * @param fileObject a file object
	 * @return an extrinsic object
	 * 
	 */
	private ExtrinsicObject createProduct(FileObject fileObject)
		throws  RegistryServiceException {
		ExtrinsicObject product = new ExtrinsicObject();
		Set<Slot> slots = new HashSet<Slot>();
		
		// for PERSONNEL & REFERENCE object
		if (this.product.getLid()==null) 
			product.setLid(storageProductName + ":" + fileObject.getName());
		else 
			product.setLid(this.product.getLid() + ":" + fileObject.getName());
		product.setObjectType(Constants.FILE_PROD);
		product.setName(FilenameUtils.getBaseName(fileObject.getName()));
		
		slots.add(new Slot("file_name", Arrays.asList(new String[] {fileObject.getName()})));
		slots.add(new Slot("file_location", Arrays.asList(new String[] {fileObject.getLocation()})));
		slots.add(new Slot("file_size", Arrays.asList(new String[] {Long.toString(fileObject.getSize())})));
        slots.add(new Slot("md5_checksum", Arrays.asList(new String[] {fileObject.getChecksum()})));
        slots.add(new Slot("creation_date_time", Arrays.asList(new String[] {fileObject.getCreationDateTime()})));
        if (fileObject.getStorageServiceProductId()!=null)
        	slots.add(new Slot("storage_service_productId", 
        			Arrays.asList(new String[] {fileObject.getStorageServiceProductId()})));
        if (fileObject.getAccessUrl()!=null)
        	slots.add(new Slot("access_url", 
        			Arrays.asList(new String[] {fileObject.getAccessUrl()})));
        
        product.setSlots(slots);
        
        return product;
	}
	
	private String getKey(String key) {		
		if (key.equalsIgnoreCase("VOLUMES"))
			return "volume_size";
		else if (key.equalsIgnoreCase("START_TIME")) 
			return "start_date_time";
		else if (key.equalsIgnoreCase("STOP_TIME"))
			return "stop_date_time";
		else if (key.equalsIgnoreCase("CITATION_DESC"))
			return "citation_text";
		else if (key.equalsIgnoreCase("PDS_VERSION_ID") ||
				key.equalsIgnoreCase("LABEL_REVISION_NOTE") ||
				key.equalsIgnoreCase("RECORD_TYPE")) 
			return null;
		//else if (key.equalsIgnoreCase("REFERENCE_KEY_ID"))
			// should be in Bibliographic_Reference => local_identifier
		else 
			return key.toLowerCase();
	}
	
	/**
	 * Method to publish associations with given catalog object
	 * 
	 * @param catObj  a catalog object
	 */
	public void publishAssociations(CatalogObject catObj) {
		LabelParserException lp = null;
		try {
			List<Reference> catRefs = catObj.getReferences();
			for (Reference aRef: catRefs) {
				Association association = createAssociation(aRef);			
				if (!associationExists(association)) {
					try {
						String guid = client.publishObject(association);
					} catch (RegistryServiceException rse) {
						lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
			        			"ingest.error.failExecution",ProblemType.EXECUTE_FAIL, "publishAssociations");
			        	catObj.getLabel().addProblem(lp);
			        	failCount++;
					}
				}
			}
		} catch (RegistryClientException rce) {
			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "publishAssociations");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
		}
	}
	
	/**
	 * Method to create an association object
	 * 
	 * @param aRef A class representation of the reference metadata
	 * @return an association object
	 */
	private Association createAssociation(Reference aRef) throws RegistryClientException {
    	Association association = new Association();
    	Boolean verifiedFlag = false;

    	if (this.product!=null) {
    		association.setSourceObject(this.product.getGuid());
    		// need to generate this as lidvid    		
    		ExtrinsicObject target = getExtrinsic(aRef.getLogicalId(), aRef.getVersion());
    		String lidvid = aRef.getLogicalId()+"::" + aRef.getVersion();
    		if (target!=null) {
    			association.setTargetObject(target.getGuid());
    			verifiedFlag = true;
    		}
    		else {
    			association.setTargetObject(lidvid);
    		}
    		association.setAssociationType(aRef.getAssociationType());
    	}
    	
    	Set<Slot> slots = new HashSet<Slot>();
    	slots.add(new Slot("verified", 
    			Arrays.asList(new String[] { verifiedFlag.toString() })));
    	association.setSlots(slots);

    	return association;
    }
	
	/**
	 * Determines whether an association is already in the registry
	 * 
	 * @param assocation The association object
	 * @return 'true' if the association was found in the registry.
	 * 'false' otherwise
	 * 
	 * @throws RegistryClientException exception ignored.
	 */
	public boolean associationExists(Association association) throws RegistryClientException {
		boolean result = false;
		AssociationFilter filter = new AssociationFilter.Builder()
			.sourceObject(association.getSourceObject())
			.targetObject(association.getTargetObject())
			.associationType(association.getAssociationType()).build();
		RegistryQuery<AssociationFilter> query = new RegistryQuery
			.Builder<AssociationFilter>().filter(filter).build();
		try {
			//why? 1, 10
			PagedResponse<Association> response = client.getAssociations(
					query, 1, 10);
			if (response.getNumFound() != 0) {
				result = true;
			}
		} catch (RegistryServiceException r) {
			//Do nothing
		}
		return result;
	}	

	/**
	 * Retrieve an extrinsic object from the registry
	 * @param lid The PDS4 logical identifier
	 * @param version The versionName
	 * 
	 * @return an extrinsic object
	 */
	public ExtrinsicObject getExtrinsic(String lid, String version) {
		//throws IngestException {
		ExtrinsicObject result = null;
		ExtrinsicFilter filter = new ExtrinsicFilter.Builder().lid(lid).versionName(version).build();
		RegistryQuery<ExtrinsicFilter> query = new RegistryQuery
			.Builder<ExtrinsicFilter>().filter(filter).build();
		try {
			PagedResponse<ExtrinsicObject> pr = client.getExtrinsics(query,null, null);
			if (pr.getNumFound() != 0) {
				// it shoudl find only one
				for (ExtrinsicObject extrinsic : pr.getResults()) {
					for (Slot slot : extrinsic.getSlots()) {
						result = extrinsic;				         
					}
				}
			}
		} catch (RegistryServiceException rse) {
			rse.printStackTrace();
		}
		return result;
	}
}