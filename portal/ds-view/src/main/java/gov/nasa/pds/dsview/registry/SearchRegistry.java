package gov.nasa.pds.dsview.registry;

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

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class SearchRegistry {
		
	private ExtrinsicObject product, latestProduct;
	private RegistryClient client;
	
	/**
	 * Constructor
	 * @param registryURL The URL to the registry service
	 * 
	 */
	public SearchRegistry(String registryURL) {		
		try {
			//initialize();
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
	public SearchRegistry(String registryURL, SecurityContext securityContext,
			String username, String password) {
		try {
			//initialize();
			client = new RegistryClient(registryURL, securityContext, username, password);
		} catch (RegistryClientException rce) {
			System.err.println("RegistryClientException occurred..." + rce.getMessage());
		}
	}
	
	public List<String> getResourceRefs(ExtrinsicObject extObj) {
		//if (extObj==null) return null;
		//List<String> resourceRefs = new ArrayList<String>();
				
		for (Slot slot : extObj.getSlots()) {
			if (slot.getName().equals("resource_ref")) {
				return slot.getValues();
			}
		}
		return null;
	}
	
	public List<String> getMissionName(ExtrinsicObject extObj) {
		for (Slot slot : extObj.getSlots()) {
			if (slot.getName().equals("investigation_ref") ||
				slot.getName().equals("mission_ref")) {
				return slot.getValues();
			}
		}
		return null;
	}
	
	public List<String> getTargetName(ExtrinsicObject extObj) {
		for (Slot slot : extObj.getSlots()) {
			if (slot.getName().equals("target_ref")) {
				return slot.getValues();
			}
		}
		return null;
	}
	
	public List<String> getSlotValues(ExtrinsicObject extObj, String keyword) {
		for (Slot slot : extObj.getSlots()) {
			if (slot.getName().equals(keyword)) {
				return slot.getValues();
			}
		}
		return null;
	}
	
	/*
	public ExtrinsicObject getProxyProducts(String identifier) {
		//RegistryQuery.Builder<ExtrinsicFilter> queryBuilder = new RegistryQuery.Builder<ExtrinsicFilter>();
	    //Map<String, String> filters = new HashMap<String, String>();
	    
	    // need to ask to Sean about the identifier???
		ExtrinsicFilter filter = new ExtrinsicFilter.Builder().objectType("Product_Proxy_PDS3").lid(identifier).build();
		
		RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>().filter(filter).build();
		
		try {
			PagedResponse<ExtrisicObject> pr = client.getExtrinsic(query, null, null);
			if (pr.getNumFound()==0) {
				return null;
			}
			else {
				return pr.getResults();
			}
		}    
	}
	*/
	
	public List<ExtrinsicObject> getObjects(String lid, String objectType) {
		ExtrinsicFilter.Builder filterBuilder = new ExtrinsicFilter.Builder();
		//RegistryQuery.Builder<ExtrinsicFilter> queryBuilder = new RegistryQuery.Builder<ExtrinsicFilter>();
		
		if (lid!=null)
			filterBuilder.lid(lid);
		
		if (objectType !=null) 
		    filterBuilder.objectType(objectType);
		    
		ExtrinsicFilter filter = filterBuilder.build();
		//queryBuilder.filter(filter);
		
		RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>().filter(filter).build();
		
		try {
			PagedResponse<ExtrinsicObject> pr = client.getExtrinsics(query, 1, 10);
			if (pr.getNumFound()==0) {
				return null;
			}
			else {
				return pr.getResults();			
			}
		} catch (RegistryServiceException rse) {
		    rse.printStackTrace();
		    return null;
		}
		//return null;
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
	/*
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
	*/
	
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
            rse.printStackTrace();
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
	/*
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
					//System.out.println("found an extrinsic object...." + lid + "    version = " + version);					
					result = extrinsic;		
					//System.out.println("result...lid = " + result.getLid() + "    guid = " + result.getGuid());
				}
			}
		} catch (RegistryServiceException rse) {
			rse.printStackTrace();
		}
		return result;
	}
	*/
}