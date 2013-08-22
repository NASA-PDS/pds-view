// Copyright 2012-2013, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
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
			client = new RegistryClient(registryURL, securityContext, username, password);
		} catch (RegistryClientException rce) {
			System.err.println("RegistryClientException occurred..." + rce.getMessage());
		}
	}
	
	public List<String> getResourceRefs(ExtrinsicObject extObj) {
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
	
	public List<ExtrinsicObject> getObjects(String lid, String objectType) {
		ExtrinsicFilter.Builder filterBuilder = new ExtrinsicFilter.Builder();
		if (lid!=null)
			filterBuilder.lid(lid);
		
		if (objectType !=null) 
		    filterBuilder.objectType(objectType);
		    
		ExtrinsicFilter filter = filterBuilder.build();
		
		RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>().filter(filter).build();
		
		try {
			List<ExtrinsicObject> objs = new ArrayList<ExtrinsicObject>();
			PagedResponse<ExtrinsicObject> pr = client.getExtrinsics(query, 1, 10);
			if (pr.getNumFound()==0) {
				return null;
			}
			else {
				for (ExtrinsicObject extObj: pr.getResults()) {
					ExtrinsicObject tmpObj = getExtrinsic(extObj.getLid());
					// returns only latest products
					if (extObj.getLid().equalsIgnoreCase(tmpObj.getLid()) &&
					    extObj.getVersionName().equalsIgnoreCase(tmpObj.getVersionName()))
					   objs.add(extObj);
				}
				return objs;		
			}
		} catch (RegistryServiceException rse) {
		    rse.printStackTrace();
		    return null;
		}
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
            rse.printStackTrace();
        }
        return aProduct;
    }
}