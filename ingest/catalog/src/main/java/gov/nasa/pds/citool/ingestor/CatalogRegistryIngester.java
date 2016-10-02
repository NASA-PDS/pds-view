// Copyright 2009-2016, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain xport licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.citool.ingestor;

import gov.nasa.pds.citool.ingestor.Constants;
import gov.nasa.pds.citool.ingestor.Reference;
import gov.nasa.pds.citool.file.FileObject;
import gov.nasa.pds.citool.CIToolIngester;
import gov.nasa.pds.citool.util.Utility;

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.client.SecurityContext;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.query.AssociationFilter;

import org.apache.oodt.cas.metadata.Metadata;
import org.apache.commons.io.FilenameUtils;
import org.apache.oodt.cas.filemgr.structs.exceptions.ConnectionException;

import gov.nasa.pds.tools.label.ObjectStatement;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.dict.Dictionary;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Date;
import java.net.URI;
import java.net.URL;

import java.text.SimpleDateFormat;
import com.sun.jersey.api.client.ClientResponse;

public class CatalogRegistryIngester {
	
	public static int fileObjCount = 0;
	public static int storageCount = 0;
	public static int registryCount = 0;
	public static int failCount = 0;
	
	public static String registryPackageName;
	
	private ExtrinsicObject product, latestProduct;
	private RegistryClient client;
	private String transportURL;
	private StorageIngester storageIngester;
	private String storageProductName;
	private String registryPackageGuid;
	private String archiveStatus = null;
	private boolean ingestedProduct = false;
	private String volumeLid; 
		
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
	
	/**
	 * Constructor with security context
	 * @param registryURL The URL to the registry service
	 * @param securityContext context required for the security service
	 * @param username Name of the user
	 * @param password Password
	 */
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
		this.storageIngester = null;
	}
	
	/**
	 * Return storage ingester instance
	 */
	public StorageIngester getStorageIngester() {
		return this.storageIngester;
	}
	
	/**
	 * Set parameters for the storage service instance
	 * @param storageURL the URL of the storage service
	 * @param productName Product name used in the storage service
	 */
	public void setStorageService(String storageURL, String productName) throws ConnectionException {
		try {			
			if (productName != null) {
				this.storageProductName = productName;
				storageIngester = new StorageIngester(new URL(storageURL));
				storageIngester.setProductName(productName);
			}
		} catch (ConnectionException ce) {
			throw ce;
		} catch(Exception e) {
			System.err.println("Exception occurred in setStorageService..." + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Set URL of the transport service
	 * @param transportURL the URL of the transport service
	 */
	public void setTransportURL(String transportURL) {
		this.transportURL = transportURL;
	}
	
	public void setArchiveStatus(String status) {
		this.archiveStatus = status;
	}
	
	public void setVolumeLid(String lid) {
		this.volumeLid = lid;
	}
	
	/**
	 * Method to ingest given catalog object to the registry service
	 * It calls ingestExtrinsicObject() for the product registry.
	 * Then, it calls ingestFileObject() for the corresponding file object registry.
	 * 
	 * @param catObj a Catalog Object instance
	 * @return the guid of the registered extrinsic object
	 * 
	 */
	public String ingest(CatalogObject catObj) {
		this.ingestedProduct = false;
		// initialize a FileObject for given CatalogObject
		catObj.setFileObject();
		String productGuid = "";
		if (catObj.getCatObjType().equalsIgnoreCase("DATA_SET_HOUSEKEEPING")) {
			registryCount += ingestHKExtrinsicObject(catObj);
		}
		else {		
			// ingest an extrinsic object to the registry service
			productGuid = ingestExtrinsicObject(catObj);	
			if (productGuid != null) registryCount++;
		}

		// ingest a file object to the registry service
		String fileObjGuid = ingestFileObject(catObj);
		if (fileObjGuid != null) fileObjCount++;
		
		return productGuid;
	}
	
	private String ingestFileObject(CatalogObject catObj) {	
		String guid = null;	
		LabelParserException lp = null;
		ExtrinsicObject fileExtrinsic = null;
		try {		
			fileExtrinsic = createProduct(catObj.getFileObject(), catObj.getCatObjType());	
			// retrieve the version info from the registry service so that it can use for the storage service version 
			if (catObj.getCatObjType().equalsIgnoreCase("PERSONNEL")
					|| catObj.getCatObjType().equalsIgnoreCase("REFERENCE")) {
				if (productExists(fileExtrinsic.getLid())) {
					catObj.setVersion(Float.valueOf(latestProduct.getVersionName()).floatValue()+1.0f);
				} else {
					catObj.setVersion(1.0f);
				}			
			}

			if (storageIngester==null) {
				lp = new LabelParserException(catObj.getLabel().getLabelURI(),
						null, null, "ingest.warning.failIngestion",
						ProblemType.SUCCEED,
						"Failed delivering to the storage service.");
				catObj.getLabel().addProblem(lp);
			}
			else {
				if (ingestedProduct) {
					// ingest to the storage service
					String productId = storageIngester.ingestToStorage(catObj);
					if (productId != null) {
						storageCount++;
						lp = new LabelParserException(catObj.getLabel().getLabelURI(),
								null, null, "ingest.text.recordAdded",
								ProblemType.SUCCEED,
								"Successfully delivered a catalog file to the storage service. productID - "
										+ productId);
						catObj.getLabel().addProblem(lp);

						// sets the storage product id to the file object,
						// so that it can be added as the slot value for the registry
						catObj.getFileObject().setStorageServiceProductId(productId);
						catObj.getFileObject().setAccessUrl(transportURL + productId);

						// fileobject registration
						fileExtrinsic.getSlots().add(new Slot("storage_service_productId", 
								Arrays.asList(new String[] { productId })));
						fileExtrinsic.getSlots().add(new Slot("access_url", 
								Arrays.asList(new String[] { transportURL + productId })));
					}	 
					else {
						lp = new LabelParserException(catObj.getLabel().getLabelURI(),
								null, null, "ingest.warning.failIngestion",
								ProblemType.SUCCEED,
								"Failed delivering to the storage service.");
						catObj.getLabel().addProblem(lp);
					}
				}
				else {
					lp = new LabelParserException(catObj.getLabel().getLabelURI(),
							null, null, "ingest.warning.skipFile",
							ProblemType.SUCCEED,
							"File is already delivered to the storage service. Won't deliver this file.");
					catObj.getLabel().addProblem(lp);
				}
			}

			if (!this.registryPackageGuid.isEmpty()) {
				client.setRegistrationPackageId(registryPackageGuid);
			}

			if (productExists(fileExtrinsic.getLid())) {	
				lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
						"ingest.warning.skipFile", ProblemType.SUCCEED,
						"File object already exists in the registry. Won't ingest this file object.");
				catObj.getLabel().addProblem(lp);
				return null;

			} else {
				guid = client.publishObject(fileExtrinsic);
			}

			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
					"ingest.text.recordAdded", ProblemType.SUCCEED,
					"Successfully ingested a file object. GUID - " + guid);
			catObj.getLabel().addProblem(lp);

			// HAS_FILE is only association type to publish to the Registry Service, 
			// other association types will be added as Slot
			Reference ref = new Reference(fileExtrinsic.getLid(), String.valueOf(catObj.getVersion()), Constants.HAS_FILE);
			publishAssociation(catObj, ref);  	
		} catch (RegistryServiceException re) {			
			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestFileObject");
        	catObj.getLabel().addProblem(lp);
		} catch (Exception e) {
			e.printStackTrace();
			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestFileObject");
        	catObj.getLabel().addProblem(lp);
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
		String guid = null;
		LabelParserException lp = null;
		try {	
			
			if (!this.registryPackageGuid.isEmpty()) {
				client.setRegistrationPackageId(registryPackageGuid);
			}					
					
			// don't ingest if the catalog object is PERSONNEL or REFERENCE
			if (catObj.getCatObjType().equalsIgnoreCase("PERSONNEL")
					|| catObj.getCatObjType().equalsIgnoreCase("REFERENCE")
					|| catObj.getCatObjType().equalsIgnoreCase("TARGET")
					|| catObj.getCatObjType().equalsIgnoreCase("SOFTWARE")
					|| catObj.getCatObjType().equalsIgnoreCase("DATA_SET_MAP_PROJECTION")
	                || catObj.getCatObjType().equalsIgnoreCase("DATA_SET_RELEASE")) {
				// TODO: need to add warning problemtype instead of using INVALID_LABEL
				lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
                        "ingest.warning.skipFile",
                        ProblemType.INVALID_LABEL_WARNING, "This file is not required to ingest into the registry.");
				catObj.getLabel().addProblem(lp);
				this.ingestedProduct = true;
				return null;		
			}
			this.product = createProduct(catObj);
			
			if (productExists(product.getLid())) {
				// need to check whether the LABEL_REVISION_NOTE is same or not
				boolean sameProduct = isSame(this.product, latestProduct);				
				if (sameProduct) {
					lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
			  				"ingest.warning.skipFile", ProblemType.SUCCEED,
			  			    "Extrinsic object already exists in the registry. Won't ingest this extrinsic object.");
			  		catObj.getLabel().addProblem(lp);
			  		this.ingestedProduct = false;
			  		return null;
				}
				else {
					guid = client.versionObject(product);				
					catObj.setVersion(Float.valueOf(latestProduct.getVersionName()).floatValue()+1.0f);
					this.product.setVersionName(String.valueOf(catObj.getVersion()));
					this.ingestedProduct = true;
				}
				
			}
			else {
				guid = client.publishObject(product);
				catObj.setVersion(1.0f);
				this.product.setVersionName("1.0");
				this.ingestedProduct = true;
			}
			this.product.setGuid(guid);
			catObj.setExtrinsicObject(this.product);
			
			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
	  				"ingest.text.recordAdded", ProblemType.SUCCEED,
	  			    "Successfully registered a product. LIDVID - " + product.getLid()+"::" + catObj.getVersion());
	  		catObj.getLabel().addProblem(lp);
	  		
	  		lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
	  				"ingest.text.recordAdded", ProblemType.SUCCEED,
	  				"Product GUID - " + guid);
	  		catObj.getLabel().addProblem(lp);
	  		
        } catch (RegistryServiceException re) {
        	lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
        	//re.printStackTrace();
  		} catch (RegistryClientException rce) {
  			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
        	//rce.printStackTrace();
  		} catch (Exception e) {
  			lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "ingestExtrinsicObject");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
        	//e.printStackTrace();
  		}
  		this.product = product;
  		
		return guid;
	}
	
	private String getSlotValues(ExtrinsicObject prod, String key) {
		Set<Slot> slots = prod.getSlots();

		Iterator setIter = slots.iterator();
		while (setIter.hasNext()) {
			Slot tmpSlot = (Slot)setIter.next();
			if (tmpSlot.getName().equalsIgnoreCase(key)) {		
				List<String> tmpValues = tmpSlot.getValues();
				if (tmpValues!=null && tmpValues.size()>0) {
					if (tmpValues.size()==1)
						return tmpValues.get(0);
					else {
						// TODO TODO TODO: should grab each component and trim it and
						// return as a long string??? or array of string????
						return Arrays.toString(tmpValues.toArray());
					}
				}
				else 
					return "";
			}
		}		
		return "";		
	}
		
	private boolean isSame(ExtrinsicObject oldProd, ExtrinsicObject newProd) {
		String oldProdSlotValues = getSlotValues(oldProd, "modification_description");	
		String newProdSlotValues = getSlotValues(newProd, "modification_description");
		
		if (oldProdSlotValues.trim().equals(newProdSlotValues.trim()))
			return true;
		else 
			return false;
	}
	
	
	/**
	 * Ingest housekeeping extrinsic object(s) to the registry service
	 * 
	 * @param catObj  a catalog object
	 * @return the guid of the registered extrinsic object 
	 * 
	 */
	
	public int ingestHKExtrinsicObject(CatalogObject catObj) {
		String guid = null;
		LabelParserException lp = null;
		int i=0;
		try {	
			
			if (!this.registryPackageGuid.isEmpty()) {
				client.setRegistrationPackageId(registryPackageGuid);
			}	
			
			for (ObjectStatement resrcObj: catObj.getResrcObjs()) {
				ExtrinsicObject resrcProduct = createResrcProduct(resrcObj, catObj);
				String resrcID = getSlotValues(resrcProduct, "resource_id");
				
				if (productExists(resrcProduct.getLid())) {
					boolean sameProduct = isSame(resrcProduct, getExtrinsic(resrcProduct.getLid()));					
					if (sameProduct) {
						lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
				  				"ingest.warning.skipFile", ProblemType.SUCCEED,
				  			    "RESOURCE_INFORMATION already exists in the registry. Won't ingest " + resrcID + ".");
				  		catObj.getLabel().addProblem(lp);
				  		this.ingestedProduct = false;
				  		continue;
					}
					else {
						guid = client.versionObject(resrcProduct);	
						this.ingestedProduct = true;
					}
				}
				else {
					guid = client.publishObject(resrcProduct);
					//catObj.setVersion(1.0f);
					this.ingestedProduct = true;
				}
				resrcProduct.setGuid(guid);
			
				lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
	  				"ingest.text.recordAdded", ProblemType.SUCCEED,
	  			    "Successfully registered a product. LID - " + resrcProduct.getLid());
				catObj.getLabel().addProblem(lp);
	  		
				lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null,
						"ingest.text.recordAdded", ProblemType.SUCCEED,
						"Product GUID - " + guid);
				catObj.getLabel().addProblem(lp);
								
				i++;
			}
	  		
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
		return i;
	}
	
	/**
	 * Add reference information as slot values 
	 * then, update the registered product
	 * 
	 * @param catObj a CatalogObject of the registered extrinsic object
	 * @param refs Hashmap that holds reference information
	 */
	public void updateProduct(CatalogObject catObj, Map<String, List<String>> refs) {
		LabelParserException lp = null;
		Set<Slot> slots = null;
		// DATA_SET_HOUSEKEEPING should be null
		if (catObj.getExtrinsicObject()==null) {
			return;
		}
		
		ExtrinsicObject product = catObj.getExtrinsicObject();
		
		if (catObj.getExtrinsicObject().getSlots()==null)
			slots = new HashSet<Slot>();
		else 
			slots = catObj.getExtrinsicObject().getSlots();

		String catObjType = catObj.getCatObjType();   		
		String version = String.valueOf(catObj.getVersion());
		
		// currently, there is only one version for the TARGET object. 
		// will get a version from the extrinsic object...it may slow down the processing
		// TODO: check for getRefValues() return 0 size or bigger..add when its size>0
		if (catObjType.equalsIgnoreCase(Constants.MISSION_OBJ)) { 	
			if (refs.get(Constants.HAS_INSTHOST)!=null)
				slots.add(new Slot(Constants.HAS_INSTHOST, getRefValues(version, Constants.HAS_INSTHOST, refs)));
			if (refs.get(Constants.HAS_INST)!=null)
				slots.add(new Slot(Constants.HAS_INST, getRefValues(version, Constants.HAS_INST, refs)));
			if (CIToolIngester.targetAvailable) {
				if (refs.get(Constants.HAS_TARGET)!=null) {
				    slots.add(new Slot(Constants.HAS_TARGET, getRefValues("1.0", Constants.HAS_TARGET, refs, catObj)));
				}
			}
		}
		else if (catObjType.equalsIgnoreCase(Constants.INSTHOST_OBJ)) {
			if (refs.get(Constants.HAS_MISSION)!=null)
				slots.add(new Slot(Constants.HAS_MISSION, getRefValues(version, Constants.HAS_MISSION, refs)));
			if (refs.get(Constants.HAS_INST)!=null)
				slots.add(new Slot(Constants.HAS_INST, getRefValues(version, Constants.HAS_INST, refs)));
			if (CIToolIngester.targetAvailable) {
				if (refs.get(Constants.HAS_TARGET)!=null)
					slots.add(new Slot(Constants.HAS_TARGET, getRefValues("1.0", Constants.HAS_TARGET, refs)));
			}
		}
		else if (catObjType.equalsIgnoreCase(Constants.INST_OBJ)) {
			if (refs.get(Constants.HAS_INSTHOST)!=null)
				slots.add(new Slot(Constants.HAS_INSTHOST, getRefValues(version, Constants.HAS_INSTHOST, refs, catObj)));
			if (refs.get(Constants.HAS_DATASET)!=null) {
				// need to find proper dataset catalog object with given INSTRUMENT_ID
				Map<String, List<String>> dsRefs = getDSRefs("INSTRUMENT_ID", catObj.getMetadata().getMetadata("INSTRUMENT_ID"));
				slots.add(new Slot(Constants.HAS_DATASET, getRefValues(version, Constants.HAS_DATASET, dsRefs)));
			}
		}
		else if (catObjType.equalsIgnoreCase(Constants.DATASET_OBJ)) {
			// need to add only available instrument in the data set catalog file
			if (refs.get(Constants.HAS_MISSION)!=null)
				slots.add(new Slot(Constants.HAS_MISSION, getRefValues(version, Constants.HAS_MISSION, refs, catObj)));
			if (CIToolIngester.targetAvailable) {
				if (refs.get(Constants.HAS_TARGET)!=null) {
					slots.add(new Slot(Constants.HAS_TARGET, getRefValues("1.0", Constants.HAS_TARGET, refs, catObj)));
				}
			}
			if (refs.get(Constants.HAS_INSTHOST)!=null) {
				slots.add(new Slot(Constants.HAS_INSTHOST, getRefValues(version, Constants.HAS_INSTHOST, refs, catObj)));
			}
			if (refs.get(Constants.HAS_INST)!=null) {
				slots.add(new Slot(Constants.HAS_INST, getRefValues(version, Constants.HAS_INST, refs, catObj)));
			}
			// how to get this version properly for each resource?????
			// should only add same dataset id
			if (refs.get(Constants.HAS_RESOURCE)!=null) {
				Map<String, List<String>> resrcRefs = new HashMap<String, List<String>>();
				List<String> values = new ArrayList<String>();
				for (String aValue: refs.get(Constants.HAS_RESOURCE)) {
					String tmpLid = aValue;
					String tmpDsid = catObj.getMetadata().getMetadata("DATA_SET_ID");
					tmpDsid = Utility.replaceChars(tmpDsid);

					if (tmpLid.contains(tmpDsid.toLowerCase()))
						values.add(aValue);
				}
			    if (values.size()>0) {
			    	resrcRefs.put(Constants.HAS_RESOURCE, values);
			    	slots.add(new Slot(Constants.HAS_RESOURCE, getRefValues(version, Constants.HAS_RESOURCE, resrcRefs)));	
			    }
			}
			if (refs.get(Constants.HAS_NODE)!=null)
				slots.add(new Slot(Constants.HAS_NODE, getRefValues(version, Constants.HAS_NODE, refs)));
		}
		else if (catObjType.equalsIgnoreCase(Constants.TARGET_OBJ)) {
			if (refs.get(Constants.HAS_MISSION)!=null)
				slots.add(new Slot(Constants.HAS_MISSION, getRefValues(version, Constants.HAS_MISSION, refs)));
			if (refs.get(Constants.HAS_INSTHOST)!=null)
				slots.add(new Slot(Constants.HAS_INSTHOST, getRefValues(version, Constants.HAS_INSTHOST, refs)));
			if (refs.get(Constants.HAS_INST)!=null)
				slots.add(new Slot(Constants.HAS_INST, getRefValues(version, Constants.HAS_INST, refs)));			
		}
		else if (catObjType.equalsIgnoreCase(Constants.VOLUME_OBJ)) {
			if (refs.get(Constants.HAS_DATASET)!=null)
				slots.add(new Slot(Constants.HAS_DATASET, getRefValues(version, Constants.HAS_DATASET, refs)));
		}
		
		List<String> verValue = new ArrayList<String>();
		verValue.add(String.valueOf(catObj.getVersion()));
		slots.add(new Slot("version_id", verValue));
		product.setSlots(slots);
		
		try {
			client.updateObject(product);
		} catch (RegistryServiceException re) {
        	lp = new LabelParserException(catObj.getLabel().getLabelURI(), null, null, 
        			"ingest.error.failExecution", ProblemType.EXECUTE_FAIL, "updateProduct");
        	catObj.getLabel().addProblem(lp);
        	failCount++;
		}  
	}
	
	private Map<String, List<String>> getDSRefs(String keyToLook, String valueToFind) {
		Map<String, List<String>> dsRefs = new HashMap<String, List<String>>();
		List<String> values = new ArrayList<String>();
		for (CatalogObject catObj: CIToolIngester.catObjs) {
			if (catObj.getCatObjType().equalsIgnoreCase(Constants.DATASET_OBJ)) {
				Metadata md = catObj.getMetadata();				
				if (md.isMultiValued(keyToLook)) {
					List<String> tmpValues = md.getAllMetadata(keyToLook);
					for (String instId: tmpValues) {					
						if (instId.equalsIgnoreCase(valueToFind)) {
							String dsId = md.getMetadata("DATA_SET_ID");
							dsId = Utility.replaceChars(dsId);
			    			dsId = dsId.toLowerCase(); 
			    			String dsLid = Constants.LID_PREFIX+"data_set:data_set."+dsId;
			    			if (!Utility.valueExists(dsLid, values)) {
								values.add(dsLid);
			    			}
						}
					}
				}
				else {
					String instId = md.getMetadata(keyToLook);
					if (instId.equalsIgnoreCase(valueToFind)) {
						String dsId = md.getMetadata("DATA_SET_ID");
						dsId = Utility.replaceChars(dsId);
		    			dsId = dsId.toLowerCase();
		    			String dsLid = Constants.LID_PREFIX+"data_set:data_set."+dsId;
		    			if (!Utility.valueExists(dsLid, values)) {
							values.add(dsLid);
		    			}
					}
				}
			}	
		}
		dsRefs.put(Constants.HAS_DATASET, values);
		
		return dsRefs;
	}
		
	private List<String> getRefValues(String version, String associationType, 
			Map<String, List<String>> allRefs) {	
		List<String> values = new ArrayList<String>();
		for (String aValue: allRefs.get(associationType)) {
			String tmpLid = aValue;
			String tmpVer = "";
			if (getExtrinsic(tmpLid)!=null) {
				tmpVer = getExtrinsic(tmpLid).getVersionName();
				if (!Utility.valueExists(aValue+"::"+tmpVer, values)) {
					values.add(aValue+"::" + tmpVer);
				}
			}
			else {
				if (!Utility.valueExists(aValue, values)) {
					values.add(aValue);
				}
			}	
		}
		return values;
	}
	
	private List<String> getRefs(String key, String associationType, Map<String, List<String>> allRefs, CatalogObject catObj) {
		List<String> values = new ArrayList<String>();
		Metadata md = catObj.getMetadata();
		
		if (md.isMultiValued(key)) {
			List<String> tmpValues = md.getAllMetadata(key);			
			// remove duplicate value from the tmpValues list
			tmpValues = new ArrayList<String>(new HashSet<String>(tmpValues));
				
			for (String valueToMatch: tmpValues) {
				valueToMatch = Utility.collapse(valueToMatch);
				valueToMatch = Utility.replaceChars(valueToMatch);
				values.addAll(getRefsList(key, valueToMatch, allRefs, associationType, catObj));
			}
		} 
		else {
			String valueToMatch = md.getMetadata(key);
			valueToMatch = Utility.collapse(valueToMatch);
			valueToMatch = Utility.replaceChars(valueToMatch);
			values = getRefsList(key, valueToMatch, allRefs, associationType, catObj);
		}
		return values;
	}
	
	private List<String> getRefsList(String key, String valueToMatch, Map<String, List<String>> allRefs, 
			String associationType, CatalogObject catObj) {
		List<String> values = new ArrayList<String>();
		String valueToCompare = "";
		for (String aValue: allRefs.get(associationType)) {		
			if (key.equalsIgnoreCase("TARGET_NAME")) {
				valueToCompare = aValue.substring(aValue.lastIndexOf(".")+1);
				
				if (valueToCompare.equalsIgnoreCase(valueToMatch.toLowerCase())) {
					String tmpVer = "";
					if (getExtrinsic(aValue)!=null) {
						tmpVer = getExtrinsic(aValue).getVersionName();
						if (!Utility.valueExists(aValue+"::"+tmpVer, values)) {
							values.add(aValue+"::" + tmpVer);
						}
					}
					else {
						if (!Utility.valueExists(aValue, values)) {
							values.add(aValue); 
						}
					}
				}						
			}
			else {				
				boolean okToAdd = false;
				if (associationType.equalsIgnoreCase("instrument_host_ref"))
					okToAdd = aValue.endsWith(valueToMatch.toLowerCase());
				else 
					okToAdd = aValue.contains(valueToMatch.toLowerCase());
				
				if (okToAdd) {
					String tmpVer = "";
					if (getExtrinsic(aValue)!=null) {
						tmpVer = getExtrinsic(aValue).getVersionName();
						
						if (!Utility.valueExists(aValue+"::"+tmpVer, values)) {
							values.add(aValue+"::" + tmpVer);
						}
					}
					else {
						//don't add all instrument_host_ref for instrument and data_set objects.
						if (!(key.equalsIgnoreCase("instrument_host_id") && 
								(catObj.getCatObjType().equalsIgnoreCase("instrument") ||
								 catObj.getCatObjType().equalsIgnoreCase("data_set")))) {
							if (!Utility.valueExists(aValue, values)) {
								values.add(aValue); 
							}
						}
					}
				}
			}
		}
		return values;
	}

    // only retrieve the list from the same catalog object
	private List<String> getRefValues(String version, String associationType, 
			Map<String, List<String>> allRefs, CatalogObject catObj) {	
		List<String> values = new ArrayList<String>();
		if (associationType==Constants.HAS_MISSION) {
			return getRefs("MISSION_NAME", associationType, allRefs, catObj);
		}
		else if (associationType==Constants.HAS_INSTHOST) {
			return getRefs("INSTRUMENT_HOST_ID", associationType, allRefs, catObj);
		}
		else if (associationType==Constants.HAS_INST) {
			return getRefs("INSTRUMENT_ID", associationType, allRefs, catObj);
		}
		else if (associationType==Constants.HAS_TARGET) {
			return getRefs("TARGET_NAME", associationType, allRefs, catObj);
		}
		else if (associationType==Constants.HAS_DATASET) {
			return getRefs("DATA_SET_ID", associationType, allRefs, catObj);
		}
		return values;
	}
	
	/**
	 * Create an extrinsic object
	 * 
	 * @param catObj    a catalog object
	 * @return an extrinsic object
	 *  
	 */
	private ExtrinsicObject createProduct(CatalogObject catObj) 
		throws  RegistryServiceException {
		ExtrinsicObject product = new ExtrinsicObject();
		Set<Slot> slots = new HashSet<Slot>();
		String productLid = null;
		String objType = catObj.getCatObjType();
		
		Metadata md = catObj.getMetadata();
		for (String key: md.getKeys()) {
			String value = md.getMetadata(key).trim();
			List<String> values = new ArrayList<String>();
		
			if (objType.equalsIgnoreCase(Constants.MISSION_OBJ) && key.equals("MISSION_NAME")) {
				value = Utility.collapse(value);
				product.setName(value);	
				String tmpValue = value;	
				tmpValue = Utility.replaceChars(value);
				productLid = Constants.LID_PREFIX+"investigation:mission."+tmpValue;
				productLid = productLid.toLowerCase();
				product.setLid(productLid);
				product.setObjectType(Constants.MISSION_PROD);			
				slots.add(new Slot(getKey("product_class"), Arrays.asList(new String[] { Constants.MISSION_PROD })));
			}
			else if (objType.equalsIgnoreCase(Constants.TARGET_OBJ) && key.equals("TARGET_NAME")) {
				value = Utility.collapse(value);
				product.setName(value);
				// need to replace empty space with _ for the lid
				String tmpValue = value;
				String targetType = md.getMetadata("TARGET_TYPE");	
				productLid = Constants.LID_PREFIX+"target:" +targetType + "."+tmpValue;
				productLid = Utility.replaceChars(productLid);
				productLid = productLid.toLowerCase();
				product.setLid(productLid);
				product.setObjectType(Constants.TARGET_PROD);		
				slots.add(new Slot(getKey("product_class"), Arrays.asList(new String[] { Constants.TARGET_PROD })));
			}
			else if (objType.equalsIgnoreCase(Constants.INST_OBJ) && key.equals("INSTRUMENT_ID")) {
				value = Utility.collapse(value);
				String instHostId = md.getMetadata("INSTRUMENT_HOST_ID");
				instHostId = Utility.collapse(instHostId);
				productLid = Constants.LID_PREFIX+"instrument:instrument."+value+"." + instHostId;
				productLid = Utility.replaceChars(productLid);
				productLid = productLid.toLowerCase();
				product.setLid(productLid);
				product.setObjectType(Constants.INST_PROD);
				product.setName(md.getMetadata("INSTRUMENT_NAME") + " for " + instHostId);
				slots.add(new Slot(getKey("product_class"), Arrays.asList(new String[] { Constants.INST_PROD })));
			}
			else if (objType.equalsIgnoreCase(Constants.INSTHOST_OBJ) && key.equals("INSTRUMENT_HOST_ID")) {	
				value = Utility.collapse(value);
				String instHostType = md.getMetadata("INSTRUMENT_HOST_TYPE");
				String tmpValue = value;
    			if (instHostType!=null)
    				tmpValue = instHostType + "." + tmpValue; 
    			else 
    				tmpValue = "instrument_host." + tmpValue; 
    			tmpValue = Utility.collapse(tmpValue);
				productLid = Constants.LID_PREFIX+"instrument_host:"+tmpValue;
				productLid = Utility.replaceChars(productLid);
				productLid = productLid.toLowerCase();
				product.setLid(productLid);
				product.setObjectType(Constants.INSTHOST_PROD);
				product.setName(md.getMetadata("INSTRUMENT_HOST_NAME"));
				slots.add(new Slot(getKey("product_class"), Arrays.asList(new String[] { Constants.INSTHOST_PROD })));
			}
			else if (objType.equalsIgnoreCase(Constants.DATASET_OBJ) && key.equals("DATA_SET_ID")) {
				value = md.getMetadata(key);
				value = Utility.collapse(value);
				String tmpValue = value;
				product.setName(md.getMetadata("DATA_SET_NAME"));	
				tmpValue = Utility.replaceChars(value);
				productLid = Constants.LID_PREFIX+"data_set:data_set."+tmpValue;
				productLid = productLid.toLowerCase();
				product.setLid(productLid);
				product.setObjectType(Constants.DS_PROD);
				slots.add(new Slot(getKey("product_class"), Arrays.asList(new String[] { Constants.DS_PROD })));
			}
			else if (objType.equalsIgnoreCase(Constants.VOLUME_OBJ) && key.equals("VOLUME_ID")) {
				value = Utility.collapse(value);
				String volumeSetId = md.getMetadata("VOLUME_SET_ID");
				volumeSetId = Utility.collapse(volumeSetId);
				productLid = Constants.LID_PREFIX+"volume:volume."+value+"__" + volumeSetId;
				productLid = Utility.replaceChars(productLid);
				productLid = productLid.toLowerCase();
				product.setLid(productLid);
				//volumeLid = productLid;
				product.setObjectType(Constants.VOLUME_PROD);
				product.setName(value);
				slots.add(new Slot(getKey("product_class"), Arrays.asList(new String[] { Constants.VOLUME_PROD })));
			}
			// how to handle multiple PERSONNEL objects????
			/*
			else if (objType.equalsIgnoreCase("PERSONNEL") && key.equals("PDS_USER_ID")) {
				product.setLid(Constants.LID_PREFIX+"personnel:personnel."+value);
				product.setObjectType(Constants.PERSON_PROD);
				product.setName(value);
			}
	        */
			
			if (objType.equalsIgnoreCase(Constants.DATASET_OBJ) && this.archiveStatus!=null) {
				if (key.equals("ARCHIVE_STATUS")) {
				    // when ARCHIVE_STATUS are in the DataSet object and DataSet Release object, 
				    // DataSet release object take preference.
					value = this.archiveStatus;
				}
			}
			
			if (md.isMultiValued(key)) {
				List<String> tmpValues = md.getAllMetadata(key);
				tmpValues = new ArrayList<String>(new HashSet<String>(tmpValues));
				for (String aVal : tmpValues) {
					if (key.equals("REFERENCE_KEY_ID")) {
						aVal = CIToolIngester.refInfo.get(aVal);
					}
					values.add(aVal);
				}
			} else {
				if (key.equals("REFERENCE_KEY_ID")) {
					value = CIToolIngester.refInfo.get(value);
				}
				values.add(value);
			}

			if (getKey(key) != null) {
				slots.add(new Slot(getKey(key), values));
			}

			// need to add this one for alternate_id for MISSION object
			if (key.equals("MISSION_ALIAS_NAME"))
				slots.add(new Slot("alternate_id", values));
		}
		
		if (objType.equalsIgnoreCase(Constants.DATASET_OBJ) && this.archiveStatus!=null) {
			if (!md.containsKey("ARCHIVE_STATUS")) {
		    	slots.add(new Slot(getKey("ARCHIVE_STATUS"), Arrays.asList(new String[] { this.archiveStatus })));
		    }
		}
		
		List<String> tmpVals = new ArrayList<String>();
		tmpVals.add(catObj.getFileObject().getCreationDateTime());
		slots.add(new Slot("modification_date", tmpVals));
		slots.add(new Slot("modification_version_id", Arrays.asList(new String[] {"1.0"})));
		slots.add(new Slot("information_model_version", Arrays.asList(new String[] {"1.7.0.0"})));
		product.setSlots(slots);	

		return product;
	}
	
	/**
	 * Create an extrinsic object with the file object
	 * 
	 * @param fileObject a file object
	 * @return an extrinsic object
	 * 
	 */
	private ExtrinsicObject createProduct(FileObject fileObject, String objType)
		throws  RegistryServiceException {
		ExtrinsicObject product = new ExtrinsicObject();
		Set<Slot> slots = new HashSet<Slot>();
		String tmpLid = "";
		
		if (objType.equalsIgnoreCase("PERSONNEL")
				|| objType.equalsIgnoreCase("REFERENCE")
				|| objType.equalsIgnoreCase("TARGET")
				|| objType.equalsIgnoreCase("SOFTWARE")
				|| objType.equalsIgnoreCase("DATA_SET_HOUSEKEEPING")
				|| objType.equalsIgnoreCase("DATA_SET_MAP_PROJECTION")
				|| objType.equalsIgnoreCase("DATA_SET_RELEASE")) {
			tmpLid = volumeLid + ":" + fileObject.getName();
		}
		else {
			tmpLid = this.product.getLid() + ":" + fileObject.getName();
		}

		product.setLid(tmpLid.toLowerCase());
		product.setObjectType(Constants.FILE_PROD);
		product.setName(FilenameUtils.getBaseName(fileObject.getName()));
		
		slots.add(new Slot("file_name", Arrays.asList(new String[] {fileObject.getName()})));
		slots.add(new Slot("file_location", Arrays.asList(new String[] {fileObject.getLocation()})));
		// add the unit using slotType (PDS-291)
		Slot fsSlot = new Slot("file_size", Arrays.asList(new String[] {Long.toString(fileObject.getSize())}));
		fsSlot.setSlotType("byte");
		slots.add(fsSlot);		
        slots.add(new Slot("md5_checksum", Arrays.asList(new String[] {fileObject.getChecksum()})));
        slots.add(new Slot("creation_date_time", Arrays.asList(new String[] {fileObject.getCreationDateTime()})));
        if (fileObject.getStorageServiceProductId()!=null)
        	slots.add(new Slot("storage_service_productId", 
        			Arrays.asList(new String[] {fileObject.getStorageServiceProductId()})));
        if (fileObject.getAccessUrl()!=null)
        	slots.add(new Slot("access_url", 
        			Arrays.asList(new String[] {fileObject.getAccessUrl()})));
        slots.add(new Slot("file_type", Arrays.asList(new String[] {"Label"})));
        slots.add(new Slot("mime_type", Arrays.asList(new String[] {"application/xml"})));
        
        product.setSlots(slots);
        
        return product;
	}
	
	/**
	 * Create an extrinsic object
	 * 
	 * @param catObj    a catalog object
	 * @return an extrinsic object
	 *  
	 */
	private ExtrinsicObject createResrcProduct(ObjectStatement resrcObj, CatalogObject catObj) 
		throws  RegistryServiceException {
		ExtrinsicObject product = new ExtrinsicObject();
		Set<Slot> slots = new HashSet<Slot>();
		String productLid = null;
		Metadata md = catObj.getMetadata();
		
		slots.add(new Slot(getKey("LABEL_REVISION_NOTE"), 
				Arrays.asList(new String[] { md.getMetadata("LABEL_REVISION_NOTE") })));
		
		List<AttributeStatement> objAttr = resrcObj.getAttributes();
		for (AttributeStatement attrSmt : objAttr) {
			String key = attrSmt.getElementIdentifier().toString();
			String value = attrSmt.getValue().toString();
			List<String> values = new ArrayList<String>();

			if (key.equals("RESOURCE_ID")) {
				// need to use RESOURCE_NAME for the name
				product.setName(resrcObj.getAttribute("RESOURCE_NAME").getValue().toString());
				String dsId = md.getMetadata("DATA_SET_ID");
				productLid = Constants.LID_PREFIX+"resource:resource."+dsId + "__" + value;
				productLid = Utility.replaceChars(productLid);
				productLid = productLid.toLowerCase();
				product.setLid(productLid);
				product.setObjectType(Constants.CONTEXT_PROD);		
			}	
				
			if (attrSmt.getValue() instanceof Set) {
				List<String> valueList = catObj.getValueList(attrSmt.getValue());
				values = valueList;
			}
			else {
				values.add(value);
			}

			if (key.equals("DESCRIPTION")) {
				slots.add(new Slot("resource_description", values));
			}
			else {
				if (getKey(key) != null)
					slots.add(new Slot(getKey(key), values));
			}
		}
		
		List<String> tmpVals = new ArrayList<String>();
		tmpVals.add(catObj.getFileObject().getCreationDateTime());
		slots.add(new Slot("modification_date", tmpVals));
		slots.add(new Slot("modification_version_id", Arrays.asList(new String[] {"1.0"})));
		slots.add(new Slot("data_class", Arrays.asList(new String[] {"Resource"})));
		slots.add(new Slot("resource_type", Arrays.asList(new String[] {"Information.Science_Portal"})));
		
		product.setSlots(slots);	

		return product;
	}
	
    private String getKey(String key) {
		if (key.equalsIgnoreCase("PDS_VERSION_ID") ||
			key.equalsIgnoreCase("RECORD_TYPE"))
			return null;
		
		for (Entry<String, String> entry: Constants.pds3ToPds4Map.entrySet()) {
			if (key.equalsIgnoreCase(entry.getKey())) 
				return entry.getValue(); 
		}
		if (key.endsWith("_DESC"))
			return key.toLowerCase()+"ription";
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
	 * Publish assocation with one reference info
	 * 
	 * @param catObj an instance of CatalogObject
	 * @param ref an instance of Reference 
	 */
	public void publishAssociation(CatalogObject catObj, Reference ref) {
		LabelParserException lp = null;
		try {
			if (!this.registryPackageGuid.isEmpty()) {
				client.setRegistrationPackageId(registryPackageGuid);
			}
			
			Association association = createAssociation(ref);
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
    		  		
    		ExtrinsicObject target = getExtrinsic(aRef.getLogicalId(), aRef.getVersion());
    		// need to generate this as lidvid 
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
	
	/* 
	 * Get a latest extrinsic object with given lid
	 * 
	 */
	public ExtrinsicObject getExtrinsic(String lid)  {
		ExtrinsicObject aProduct = null;
		try {
			client.setMediaType("application/xml");
			aProduct = client.getLatestObject(lid, ExtrinsicObject.class);
		} catch (RegistryServiceException rse) {
			//rse.printStackTrace();
		}
		return aProduct;
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
					result = extrinsic;		
				}
			}
		} catch (RegistryServiceException rse) {
			rse.printStackTrace();
		}
		return result;
	}
	
	public void createRegistryPackage() {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			this.registryPackageName = "Catalog-Package_";
			
			if (this.storageProductName==null)
				registryPackageName += "unknown";
			else 
				registryPackageName += storageProductName;
			
			registryPackageName += "_" + dateFormat.format(new Date().getTime());
			RegistryPackage registryPackage = new RegistryPackage();
			registryPackage.setName(registryPackageName);
			
			this.registryPackageGuid = client.publishObject(registryPackage);
		} 
		catch (RegistryServiceException rse) {
			if (!((rse.getStatus()==ClientResponse.Status.ACCEPTED) ||
		         (rse.getStatus()==ClientResponse.Status.OK))) {
				System.err.println("FAILURE: Error occurred to create a registry package. Error Status = " + rse.getStatus());
				
				// PDS-89 jira issue
				if (rse.getStatus()==ClientResponse.Status.UNAUTHORIZED ||
					rse.getStatus()==null) {
					System.err.println("         Please provide correct username/password for the registry service.\n");
				}
				System.exit(1);
			}
		}		
		catch (com.sun.jersey.api.client.ClientHandlerException ex) {
			if (ex.getMessage().contains("Connection refused"))
				System.err.println("Can't connect to the registry service.\n"+
						"Please make sure that the registry service is up and running. \n" + 
						"Or, provide correct information (registryUrl/username/password).");
			System.exit(1);
		}
	}
}
