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
package gov.nasa.pds.citool;

import gov.nasa.pds.citool.comparator.CatalogComparator;
import gov.nasa.pds.citool.ingestor.Constants;
import gov.nasa.pds.citool.ingestor.CatalogObject;
import gov.nasa.pds.citool.ingestor.CatalogRegistryIngester;
import gov.nasa.pds.citool.ingestor.Reference;
import gov.nasa.pds.citool.report.IngestReport;
import gov.nasa.pds.citool.target.Target;
import gov.nasa.pds.citool.util.Utility;

import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.ManualPathResolver;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.tools.label.AttributeStatement;
import gov.nasa.pds.registry.client.SecurityContext;

import org.apache.oodt.cas.filemgr.structs.Product;
import org.apache.oodt.cas.filemgr.structs.exceptions.CatalogException;
import org.apache.oodt.cas.filemgr.structs.exceptions.ConnectionException;
import org.apache.oodt.cas.metadata.Metadata;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.ConnectException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.io.IOException;

public class CIToolIngester {

	private IngestReport report;
	
    private String registryUrl;
    private String storageUrl;
    private String username;
    private String password;
    private String basePath;
    
    // use VOLUME_ID for now (12-8-11)
    private String storageProductName;
    
    private String transportUrl = "/data?productID=";       
    /** Security context to support handling of the PDS Security. */
    private SecurityContext securityContext;
    
    private List<Label> catLabels;
    public static List<CatalogObject> catObjs;  
    private CatalogRegistryIngester catIngester;
    public static Map<String, String> refInfo;
    
    private boolean storageAvailable = false;
    public static boolean targetAvailable = false;
    
    public CIToolIngester(IngestReport report) {
        this.report = report;
    }
    
    public void setRegistryUrl(String registryUrl) {
    	this.registryUrl = registryUrl;
    }
    
    public void setStorageUrl(String storageUrl) {
    	this.storageUrl = storageUrl;
    }
    
    public void setTransportUrl(String transportUrl) {
    	this.transportUrl = transportUrl + this.transportUrl;
    }

    /**
     * Sets the security.
     *
     * @param securityContext An object containing the keystore information.
     * @param username Username of an authorized user.
     * @param password Password associated with the given username.
     *
     * @throws RegistryClientException If an error occurred while initializing
     * the security.
     */
    public void setSecurity(SecurityContext securityContext, String username,
        String password) {
    	this.securityContext = securityContext;
    	this.username = username;
    	this.password = password;
    }
    
    /**
     * Ingest catalog file(s).
     *
     * @param target URL of the target (directory or file)
     * @throws Exception
     */
    public void ingest(Target target, boolean recurse) throws Exception {
    	catLabels = new ArrayList<Label>();
        if (target.isDirectory()) {
            List<URL> list = target.traverse(recurse);
            this.parseLabels(list);
        }
        else {
        	this.parseLabel(target.toURL());
        }
        
        catObjs = new ArrayList<CatalogObject>();
        process();
    }

    /**
     * Parse a label
     *
     * @param source URL of the catalog file.
     */
    public void parseLabel(URL source) {
        Label lbl = parse(source);
          if (lbl == null) {
            return;
        } 
        else {    
        	catLabels.add(lbl);
        }
    }
    
    public void process() {   	
    	catIngester = new CatalogRegistryIngester(registryUrl, securityContext, username, password);
    	CIToolIngester.refInfo = new HashMap<String, String>();
    	convertToCatalogObject();
 
    	// generate Reference info for each catObj, it will be used to create associations
    	Map<String, List<String>> refs = populateReferenceEntries();
 
    	// create a package registry
    	catIngester.createRegistryPackage();
    	
    	// publish extrinsic products and fileobject products 
    	/* don't ingest for testing... uncomment it out for actual ingestion..... */     	
    	for (CatalogObject obj: catObjs) {	
    		if (obj.getIsLocal()) {
    			catIngester.ingest(obj);  // ingest extrinsic & file object	
    			report.record(obj.getLabel().getLabelURI(), obj.getLabel().getProblems());
    		}    		
       	} 
       	
    	//Call updateProduct after ingestion to get correct versionNumber
    	for (CatalogObject obj: catObjs) {
    		if (obj.getIsLocal()) 
    			catIngester.updateProduct(obj, refs); // update extrinsic object with associations
    	} 	
    }
 
    private void convertToCatalogObject() {
		List<String> pointerFiles = null;
		// read a set of catalog archive volume files
		// TODO: need to make sure to read voldesc.cat first
		// how to handle the multiple sets of CATALOG object
		boolean isVolumeCatalog = false;
		
		// TODO: need to add to handle multiple catalog objects (sets of catalog references???)
		for (Label lbl : catLabels) {		
			//System.out.println("lbl.getLabelURI() = " + lbl.getLabelURI());
			CatalogObject catObj = new CatalogObject(this.report);
			boolean validFile = catObj.processLabel(lbl);
			//System.out.println("catObj.getCatObjType() = " + catObj.getCatObjType() +  "    validFile = " + validFile);
			if (validFile) {
				if (catObj.getCatObjType().equalsIgnoreCase("VOLUME")) {
					pointerFiles = catObj.getPointerFiles();
					storageProductName = catObj.getPdsLabelMap().get("VOLUME_ID")
							.getValue().toString();
					basePath = catObj.getFilename().substring(0, catObj.getFilename().lastIndexOf(File.separator));
					isVolumeCatalog = true;
				}
				catObjs.add(catObj);
			}
			else {
				LabelParserException lp = new LabelParserException(
                        lbl.getLabelURI(), null, null,
                        "ingest.warning.skipFile",
                        ProblemType.INVALID_LABEL_WARNING, "This file is not required to ingest into the registry service.");
                report.recordSkip(lbl.getLabelURI(), lp);
			}
		}
	    
		if (!isVolumeCatalog) {
			System.err.println("\nError: VOLUME catalog object is missing in this archive volume. Can't process further.\n");
			System.exit(1);
		}

		List<String> requiredFiles = new ArrayList<String>();
		for (String ptrFile : pointerFiles) {
			// ignore the reference file when it's N/A
			if (ptrFile.equals("N/A")) {
				continue;
			}
		
			// need to find the file on the local system before trying to get from the file manager
			String completeFilename = basePath + File.separator + "catalog" + File.separator + ptrFile.toLowerCase();
			File aFile = new File(completeFilename);
			try {
				// check whether this reference file is already in the label list
				if (!findLabel(ptrFile)) {					
					// TODO: may refactor this part ???
					if (aFile.exists()) {	
						CatalogObject catObj = new CatalogObject(this.report);
						Label tmpLbl = parse(aFile.toURL());
						if (tmpLbl != null) {
							boolean okToProcess = catObj.processLabel(tmpLbl);
							if (okToProcess) {
								catObj.setIsLocal(true);
								catObjs.add(catObj);
								if (catObj.getCatObjType().equalsIgnoreCase(Constants.RELEASE_OBJ)) {
									catIngester.setArchiveStatus(catObj.getMetadata().getMetadata("ARCHIVE_STATUS"));
								}
							}
							else {
								LabelParserException lp = new LabelParserException(
										tmpLbl.getLabelURI(), null, null,
										"ingest.warning.skipFile",
										ProblemType.INVALID_LABEL_WARNING, "This file is not required to ingest into the registry service.");
								report.recordSkip(tmpLbl.getLabelURI(), lp);
							}
						}
						else {
							System.err.println("\n" + aFile.toURL() + " is NULL so it can't be processed. Please check the file.\n");
							System.exit(1);
						}
					}			
					else {
						// also, check this reference file is in the same directory as VOLDESC.CAT
						File tmpFile = new File(basePath + File.separator + ptrFile.toLowerCase());
						if (tmpFile.exists()) {
							CatalogObject catObj = new CatalogObject(this.report);
							Label tmpLbl = parse(tmpFile.toURL());
							if (tmpLbl != null) {
								boolean okToProcess = catObj.processLabel(tmpLbl);
								if (okToProcess) {
									catObj.setIsLocal(true);
									catObjs.add(catObj);
									if (catObj.getCatObjType().equalsIgnoreCase(Constants.RELEASE_OBJ)) {
										catIngester.setArchiveStatus(catObj.getMetadata().getMetadata("ARCHIVE_STATUS"));
									}		
								} else {
									LabelParserException lp = new LabelParserException(
											tmpLbl.getLabelURI(), null, null,
											"ingest.warning.skipFile",
											ProblemType.INVALID_LABEL_WARNING, "This file is not required to ingest into the registry service.");
									report.recordSkip(tmpLbl.getLabelURI(), lp);
								}
							}
							else {
								System.err.println("\n" + aFile.toURL() + " is NULL so it can't be processed. Please check the file.\n");
								System.exit(1);
							}
						}
						else {
							System.err.println("\nWARNING: " + tmpFile + " is missing.");
							requiredFiles.add(ptrFile);
						}
					}
				}
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}			
		}
		
		try {
			catIngester.setStorageService(storageUrl, storageProductName);
			storageAvailable = true;
		} catch (ConnectionException ce) {
			System.err.println("\nWARNING: The storage service - " + storageUrl + 
					" is not accessble. Please check the connection. " + 
					"\n\t The catalog file(s) CANNOT be delivered to the storage service.");
		}
			
		catIngester.setTransportURL(transportUrl);
		// there are some files to retrieve from the file manager server
		if (requiredFiles.size()>0) {
			getFilesFromStorageService(requiredFiles);
		}
    }
    
    private boolean findLabel(String filename) {
    	for (CatalogObject aCatObj: catObjs) {
    		String tmpFilename = aCatObj.getFilename();
            tmpFilename = tmpFilename.substring(tmpFilename.lastIndexOf(File.separator) + 1);
            if (tmpFilename.equalsIgnoreCase(filename)) {
            	return true;
            }
    	}
    	return false;
    }
    
    /*
     * Retrieves a list of files from the storage service (transport?)
     */
    public void getFilesFromStorageService(List<String> requiredFiles) {
    	String productId = null;
    	try {
			for (String aFile : requiredFiles) {
				if (storageAvailable) {
					Product aProduct = getProductFromStorageService(aFile);
					if (aProduct==null) {
						System.err.println("Error: Catalog file (" + aFile + ") is missing in the archive volume and " + "" +
								"can't get it from the storage service.");
						System.exit(1);	
					}				
					productId = aProduct.getProductId();

					// need to get a transport URL from the users
					URL tmpUrl = new URL(transportUrl + productId);
					HttpURLConnection conn = (HttpURLConnection) tmpUrl.openConnection();

					if (conn.getResponseCode()==HttpURLConnection.HTTP_OK) {
						Label tmpLbl = parse(tmpUrl);

						CatalogObject catObj = new CatalogObject(this.report);
						boolean okToProcess = catObj.processLabel(tmpLbl);

						String fileLoc = catIngester.getStorageIngester()
								.getFMClient().getMetadata(aProduct).getMetadata("FileLocation");
						float fileVersion = Float.valueOf(
								fileLoc.substring(fileLoc.lastIndexOf(File.separator) + 1)).floatValue();

						catObj.setVersion(fileVersion);
						catObj.setIsLocal(false);
						catObjs.add(catObj);
					}
					else {
						// PDS-88 jira issue ???? ToDo: Check this part again...
						// how to handle this case...(when there is no catalog file exist on the storage server?????)
						System.err.println("Error: Catalog file (" + aFile + ") is missing in the archive volume and " + "" +
								"can't get it from the storage service.");
						System.exit(1);					
					}
				} else {
					// PDS-88 jira issue
					// how to handle this case...(when there is no catalog file exist on the storage server?????)
					System.err.println("Error: Catalog file (" + aFile + ") is missing in the archive volume and " + "" +
							"can't get it from the storage service.");
					System.exit(1);	
				}
			}

    	} catch (MalformedURLException mue) {
    		mue.printStackTrace();
    		System.exit(1);
    	} catch (CatalogException ce) {
    		ce.printStackTrace();
    		System.exit(1);
    	} catch (ConnectException ioe) {  		
    		System.err.println("\nFAILURE: " + transportUrl + productId + " is not accessble. " 
    				+ "\nPlease make sure you provide a correct transport URL.\n");
    		System.exit(1);
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	}
    }
    
    public Product getProductFromStorageService(String fileName) {
    	return catIngester.getStorageIngester().getProductByName(this.storageProductName+":"+fileName.toLowerCase());
    }
    
    /**
     * Generates reference class object for association information
     */
    private Map populateReferenceEntries() {
    	Map<String, List<String>> refs = new HashMap<String, List<String>>();
    	String lidValue = null;
    	List<String> values = null;
    	
    	for (CatalogObject tmpCatObj: catObjs) {
    		String catObjType = tmpCatObj.getCatObjType();
    		Map<String, AttributeStatement> pdsLbl = tmpCatObj.getPdsLabelMap();
    		Metadata md = tmpCatObj.getMetadata();
    		if (catObjType.equalsIgnoreCase(Constants.MISSION_OBJ)) {
    			lidValue = pdsLbl.get("MISSION_NAME").getValue().toString();
    			lidValue = Utility.collapse(lidValue);
    			lidValue = Utility.replaceChars(lidValue);
    			lidValue = lidValue.toLowerCase();
    			if (refs.get(Constants.HAS_MISSION)!=null) {
    				values = refs.get(Constants.HAS_MISSION);
    			}
    			else {
    				values = new ArrayList<String>();
    			}
    			values.add(Constants.LID_PREFIX+"investigation:mission."+lidValue);
    			refs.put(Constants.HAS_MISSION, values);

    			String key = "INSTRUMENT_HOST_ID";
    			if (refs.get(Constants.HAS_INSTHOST)!=null) {
    				values = refs.get(Constants.HAS_INSTHOST);
    			}
    			else {
    				values = new ArrayList<String>();
    			}
    			if (md.containsKey(key)) {
    				if (md.isMultiValued(key)) {
    					List<String> tmpValues = md.getAllMetadata(key);
    					for (String aVal: tmpValues) {
    						lidValue = aVal;
    						lidValue = Utility.collapse(lidValue);
    						lidValue = lidValue.toLowerCase();
    						if (!Utility.valueExists(Constants.LID_PREFIX+"instrument_host:instrument_host."+lidValue, values))
    							values.add(Constants.LID_PREFIX+"instrument_host:instrument_host."+lidValue);
    					}
    				}
    				else {
    					lidValue = md.getMetadata(key);
    					lidValue = Utility.collapse(lidValue);
    					lidValue = lidValue.toLowerCase();
    					if (!Utility.valueExists(Constants.LID_PREFIX+"instrument_host:instrument_host."+lidValue, values))
    						values.add(Constants.LID_PREFIX+"instrument_host:instrument_host."+lidValue);              
    				}
    				refs.put(Constants.HAS_INSTHOST, values);
    			}
    		}
    		else if (catObjType.equalsIgnoreCase(Constants.DATASET_OBJ)) {
    			lidValue = pdsLbl.get("DATA_SET_ID").getValue().toString();
    			lidValue = Utility.collapse(lidValue);
    			lidValue = Utility.replaceChars(lidValue);   			
    			lidValue = lidValue.toLowerCase();
    			
    			if (refs.get(Constants.HAS_DATASET)!=null) {
    				values = refs.get(Constants.HAS_DATASET);
    			}
    			else {
    				values = new ArrayList<String>();
    			}
    			
    			if (!Utility.valueExists(Constants.LID_PREFIX+"data_set:data_set."+lidValue, values)) {
    				values.add(Constants.LID_PREFIX+"data_set:data_set."+lidValue);
    			}
    			
    			refs.put(Constants.HAS_DATASET, values);
    			
    			String key = "CURATING_NODE_ID";
    			if (md.containsKey(key)) {
    				if (refs.get(Constants.HAS_NODE)!=null) {
    					values = refs.get(Constants.HAS_NODE);
    				}
    				else {
    					values = new ArrayList<String>();
    				}
    				if (md.isMultiValued(key)) {
    					List<String> tmpValues = md.getAllMetadata(key);
    					for (String aVal: tmpValues) {
    						lidValue = aVal;
    						lidValue = Utility.collapse(lidValue);
    						lidValue = lidValue.toLowerCase();
    						if (!Utility.valueExists(Constants.LID_PREFIX+"node:node."+lidValue, values))
    							values.add(Constants.LID_PREFIX+"node:node."+lidValue);
    					}
    				}
    				else {
    					lidValue = md.getMetadata(key);
    					lidValue = Utility.collapse(lidValue);
    					lidValue = lidValue.toLowerCase();
    					if (!Utility.valueExists(Constants.LID_PREFIX+"node:node."+lidValue, values))
    						values.add(Constants.LID_PREFIX+"node:node."+lidValue);              
    				}
    				refs.put(Constants.HAS_NODE, values);      
    			}
    			
    			key = "INSTRUMENT_HOST_ID";
    			if (refs.get(Constants.HAS_INSTHOST)!=null) {
    				values = refs.get(Constants.HAS_INSTHOST);
    			}
    			else {
    				values = new ArrayList<String>();
    			}
    			if (md.containsKey(key)) {
    				if (md.isMultiValued(key)) {
    					List<String> tmpValues = md.getAllMetadata(key);
    					for (String aVal: tmpValues) {
    						lidValue = aVal;
    						lidValue = Utility.collapse(lidValue);
    						lidValue = lidValue.toLowerCase();
    						if (!Utility.valueExists(Constants.LID_PREFIX+"instrument_host:instrument_host."+lidValue, values))
    							values.add(Constants.LID_PREFIX+"instrument_host:instrument_host."+lidValue);
    					}
    				}
    				else {
    					lidValue = md.getMetadata(key);
    					lidValue = Utility.collapse(lidValue);
    					lidValue = lidValue.toLowerCase();
    					if (!Utility.valueExists(Constants.LID_PREFIX+"instrument_host:instrument_host."+lidValue, values))
    						values.add(Constants.LID_PREFIX+"instrument_host:instrument_host."+lidValue);              
    				}
    				refs.put(Constants.HAS_INSTHOST, values);
    			}
    		}
    		else if (catObjType.equalsIgnoreCase(Constants.INST_OBJ)) {
    			lidValue = pdsLbl.get("INSTRUMENT_ID").getValue().toString();
    			String hostId = pdsLbl.get("INSTRUMENT_HOST_ID").getValue().toString();
   
    			if (refs.get(Constants.HAS_INST)!=null) {
    				values = refs.get(Constants.HAS_INST);
    			}
    			else {
    				values = new ArrayList<String>();
    			}
    			lidValue += "__" + hostId;
    			lidValue = Utility.collapse(lidValue);
    			lidValue = lidValue.toLowerCase();
    			values.add(Constants.LID_PREFIX+"instrument:instrument."+lidValue);
    			refs.put(Constants.HAS_INST, values);
    		}
    		else if (catObjType.equalsIgnoreCase(Constants.INSTHOST_OBJ)) {
    			lidValue = pdsLbl.get("INSTRUMENT_HOST_ID").getValue().toString();
    			lidValue = Utility.collapse(lidValue);
    			lidValue = lidValue.toLowerCase();
    			if (refs.get(Constants.HAS_INSTHOST)!=null) {
    				values = refs.get(Constants.HAS_INSTHOST);
    			}
    			else {
    				values = new ArrayList<String>();
    			}
    			if (!Utility.valueExists(Constants.LID_PREFIX+"instrument_host:instrument_host."+lidValue, values))
    				values.add(Constants.LID_PREFIX+"instrument_host:instrument_host."+lidValue);
    			refs.put(Constants.HAS_INSTHOST, values);
    		}
    		else if (catObjType.equalsIgnoreCase(Constants.TARGET_OBJ)) {
    			this.targetAvailable = true;
    			
    			lidValue = pdsLbl.get("TARGET_NAME").getValue().toString();
    			String targetType = getTargetType(lidValue);
    			lidValue = targetType + "." + lidValue;
    			lidValue = Utility.collapse(lidValue);
    			lidValue = Utility.replaceChars(lidValue);
    			lidValue = lidValue.toLowerCase();
    			if (refs.get(Constants.HAS_TARGET)!=null) {
    				values = refs.get(Constants.HAS_TARGET);
    			}
    			else {
    				values = new ArrayList<String>();
    			}
    			 			
    			if (!Utility.valueExists(Constants.LID_PREFIX+"target:" + lidValue, values)) {
    				values.add(Constants.LID_PREFIX+"target:" + lidValue);
    			}
    			refs.put(Constants.HAS_TARGET, values);
    		}
    		else if (catObjType.equalsIgnoreCase(Constants.RESOURCE_OBJ) || 
    				 catObjType.equalsIgnoreCase(Constants.HK_OBJ)) {	
    			if (refs.get(Constants.HAS_RESOURCE)!=null) {
    				values = refs.get(Constants.HAS_RESOURCE);
    			}
    			else {
    				values = new ArrayList<String>();
    			}
    			
    			String dsId = pdsLbl.get("DATA_SET_ID").getValue().toString();   
    			String key = "RESOURCE_ID";
    			if (md.containsKey(key)) {
    				if (md.isMultiValued(key)) {
    					List<String> tmpValues = md.getAllMetadata(key);    				
    					for (String aVal: tmpValues) {				
    						lidValue = dsId + "__" + aVal; 
    						lidValue = Utility.collapse(lidValue);
    						lidValue = Utility.replaceChars(lidValue);
    						lidValue = lidValue.toLowerCase();
    						if (!Utility.valueExists(Constants.LID_PREFIX+"resource:resource."+lidValue, values))
    							values.add(Constants.LID_PREFIX+"resource:resource."+lidValue);
    					}
    				}
    				else {
    					lidValue = md.getMetadata(key);
    					lidValue = dsId + "__" + lidValue;
    					lidValue = Utility.collapse(lidValue);
    					lidValue = Utility.replaceChars(lidValue);
    					lidValue = lidValue.toLowerCase();
    					if (!Utility.valueExists(Constants.LID_PREFIX+"resource:resource."+lidValue, values))
    						values.add(Constants.LID_PREFIX+"resource:resource."+lidValue);
    				}
    				refs.put(Constants.HAS_RESOURCE, values);   
    			}
    			
    			key = "CURATING_NODE_ID";
    			if (md.containsKey(key)) {
					if (refs.get(Constants.HAS_NODE) != null) {
						values = refs.get(Constants.HAS_NODE);
					} else {
						values = new ArrayList<String>();
					}
					if (md.isMultiValued(key)) {
						List<String> tmpValues = md.getAllMetadata(key);
						for (String aVal : tmpValues) {
							lidValue = aVal;
							lidValue = Utility.collapse(lidValue);
							lidValue = lidValue.toLowerCase();
							if (!Utility.valueExists(Constants.LID_PREFIX + "node:node." + lidValue, values))
								values.add(Constants.LID_PREFIX + "node:node." + lidValue);
						}
					} else {
						lidValue = md.getMetadata(key);
						lidValue = Utility.collapse(lidValue);
						lidValue = lidValue.toLowerCase();
						if (!Utility.valueExists(Constants.LID_PREFIX + "node:node." + lidValue, values))
							values.add(Constants.LID_PREFIX + "node:node." + lidValue);
					}
					refs.put(Constants.HAS_NODE, values);  
    			}
    		}
    		else if (catObjType.equalsIgnoreCase(Constants.VOLUME_OBJ)) {
    			lidValue = pdsLbl.get("VOLUME_ID").getValue().toString();
    			storageProductName = lidValue;
    			
    			String key = "DATA_SET_ID";  
    			key = Utility.replaceChars(key);
    			if (refs.get(Constants.HAS_DATASET)!=null)
    				values = refs.get(Constants.HAS_DATASET);
    			else
    				values = new ArrayList<String>();
    			
    			if (md.isMultiValued(key)) {
    				List<String> tmpValues = md.getAllMetadata(key);
    				for (String aVal: tmpValues) {
    					lidValue = aVal;
    					lidValue = Utility.collapse(lidValue);
    					lidValue = Utility.replaceChars(lidValue);
    					lidValue = lidValue.toLowerCase();
    					// shouldn't add if the value is already exists in the list
    					if (!Utility.valueExists(Constants.LID_PREFIX+"data_set:data_set."+lidValue, values)) {
    						values.add(Constants.LID_PREFIX+"data_set:data_set."+lidValue);
    					}
    				}
    			}
    			else {
    				lidValue = md.getMetadata(key);
    				lidValue = Utility.collapse(lidValue);
    				lidValue = Utility.replaceChars(lidValue);
    				lidValue = lidValue.toLowerCase();
    				if (!Utility.valueExists(Constants.LID_PREFIX+"data_set:data_set."+lidValue, values)) {
    					values.add(Constants.LID_PREFIX+"data_set:data_set."+lidValue);         
    				}
    			}
    			refs.put(Constants.HAS_DATASET, values);
    		}
    	}
    	return refs;
    }
    
    public Map<String, String> getTargetInfos(String targetName) {
    	for (CatalogObject catObj: catObjs) {
    		if (catObj.getCatObjType().equalsIgnoreCase(Constants.TARGET_OBJ))
    			if (catObj.getMetadata().getMetadata("TARGET_NAME").equalsIgnoreCase(targetName)) {  				
    				return catObj.getTargetInfos();
    			}
    	}
    	return null;
    }
    
    private String getTargetType(String targetName) {
    	Map<String, String> targetInfos = getTargetInfos(targetName);
    	String targetType = "target";
    	if (targetInfos!=null) {
			targetType = targetInfos.get(targetName);
			if (targetType==null)
				targetType = "target";
			else {
				if (targetType.equalsIgnoreCase("n/a"))
					targetType = "unk";
				if (targetType.toLowerCase().startsWith("unk")) 
					targetType = "unk";
			}
    	}

    	return targetType;
    }
 
    /**
     * Ingest catalog files from the given directories.
     *
     * @param sources URL of the directory of catalog files
     *
     */
    public void parseLabels(List<URL> sources) {
        for (int i=0; i<sources.size(); i++) {
            parseLabel((URL)sources.get(i));
        }
    }

    /**
     * Method to parse the PDS catalog file
     * @param url URL of the pds catalog file
     * 
     * @return a Label object
     */
    public Label parse(URL url) {
    	URI uri = null;
        try {
            uri = url.toURI();
        } catch (URISyntaxException u) {
            //Ignore
        }
        ManualPathResolver resolver = new ManualPathResolver();
        resolver.setBaseURI(ManualPathResolver.getBaseURI(uri));
        //Parser must have "parser.pointers" set to false
        DefaultLabelParser parser = new DefaultLabelParser(false, true, resolver);
        Label label = null;
        try {
            label = parser.parseLabel(url);
        } catch (LabelParserException lp) {
            //Product tools library records files that have a missing
            //PDS_VERSION_ID as an error. However, we want CITool to record
            //this as a warning, so we need to instantiate a new
            //LabelParserException.
            if("parser.error.missingVersion".equals(lp.getKey())) {
                report.recordSkip(uri, new LabelParserException(
                        lp.getSourceFile(), null, null,
                        lp.getKey(), ProblemType.INVALID_LABEL_WARNING,
                        lp.getArguments()));
            }
            else {
                report.recordSkip(uri, lp);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            report.recordSkip(uri, e);
        }
        return label;
    }
    
    
    /**
     * Perform a comparison between 2 files.
     *
     * @param source URL of the source file.
     * @param target URL of the target file.
     * @throws LabelParserException
     * @throws IOException
     */
    public boolean compare(URL source, URL target) {
        Label sourceLabel = parse(source);
        Label targetLabel = parse(target);
        if (sourceLabel == null || targetLabel == null) {
            return false;
        }
        if (sourceLabel.getProblems().isEmpty()
            && targetLabel.getProblems().isEmpty()) {
            CatalogComparator comparator = new CatalogComparator();
            targetLabel = comparator.checkEquality(sourceLabel, targetLabel);
            report.record(targetLabel.getLabelURI(),
                    targetLabel.getProblems());
            
            if (targetLabel.getProblems().isEmpty()) { // same file
            	return true;
            }
            else 
            	return false;           
        }
        else {
            if (!sourceLabel.getProblems().isEmpty()) {
                LabelParserException lp = new LabelParserException(
                        sourceLabel.getLabelURI(), null, null,
                        "compare.source.UnParseable",
                        ProblemType.INVALID_LABEL, sourceLabel.getLabelURI());
                report.recordSkip(sourceLabel.getLabelURI(), lp);
            }
            if (!targetLabel.getProblems().isEmpty()) {
                LabelParserException lp = new LabelParserException(
                        sourceLabel.getLabelURI(), null, null,
                        "compare.target.UnParseable",
                        ProblemType.INVALID_LABEL, targetLabel.getLabelURI());
                report.recordSkip(targetLabel.getLabelURI(), lp);
            }
            return false;
        }
    }
}
