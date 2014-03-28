//	Copyright 2013, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id: SearchCoreLauncher.java 12098 2013-09-18 15:53:49Z jpadams $
//

package gov.nasa.pds.registry.client.results;

import gov.nasa.pds.registry.cache.AssociationCache;
import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.wrapper.AssociationRegistryAttribute;
import gov.nasa.pds.registry.model.wrapper.ExtendedExtrinsicObject;
import gov.nasa.pds.registry.model.wrapper.RegistryAttributeWrapper;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.util.Debugger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class to handle the communication with the Registry Service
 * 
 * @author jpadams
 *
 */
public class RegistryHandler {

	/** Output logger **/
	private static Logger log = Logger.getLogger(RegistryHandler.class.getName());
	
	public static final int DFLT_QUERY_MAX = 999999999;
	
	private int queryMax = DFLT_QUERY_MAX;
	
	private boolean checkAssociations;
	
	/** List of registry URLs that will be queried against for Extrinsics **/
	private List<String> primaryRegistries;
	
	/** List of registry URLs used only for searching for association values **/
	private List<String> secondaryRegistries;
	
	private List<String> allRegistries;
	
	public RegistryHandler(List<String> primaryRegistries, List<String> secondaryRegistries,
			int queryMax) {
		this(primaryRegistries, secondaryRegistries, queryMax, false);
	}
	
	public RegistryHandler(List<String> primaryRegistries, List<String> secondaryRegistries,
			int queryMax, boolean checkAssociations) {
		this.primaryRegistries = new ArrayList<String>(primaryRegistries);
		this.secondaryRegistries = new ArrayList<String>(secondaryRegistries);
		
		this.allRegistries = new ArrayList<String>();
		this.queryMax = queryMax;
		
		this.checkAssociations = checkAssociations;

		resetAllRegistries();
	}
	
	/**
	 * Get the ExtrinsicObjects from the given query params
	 * 
	 * @deprecated			Use getExtrinsicsWithFilter method
	 * 
	 * @param queryMap		query map for attribute name->value pairs
	 * @return				RegistryResults object containing filtered results
	 * @throws Exception	thrown if there are issues with the RegistryClient
	 */
	@Deprecated
	public RegistryResults getExtrinsicsByQuery(Map<String, String> queryMap) throws RegistryHandlerException {
		Map<RegistryAttributeWrapper, String> map = new HashMap<RegistryAttributeWrapper, String>();
		for (String path : queryMap.keySet()) {
			if (RegistryAttributeWrapper.get(path) != null) {
				map.put(RegistryAttributeWrapper.get(path), queryMap.get(path));
			} else {
				log.log(Level.WARNING, "Couldn't find query: " +  path + " - " + queryMap.get(path));
			}
		}
	    return getExtrinsics(this.primaryRegistries, map, null, this.queryMax);
	}
	
	/**
	 * Get the ExtrinsicObjects after results have been filtered. Queries against
	 * only the primary registries for extrinsics.
	 * 
	 * @param resultsFilterList
	 * @return
	 * @throws RegistryHandlerException
	 */
	public RegistryResults getExtrinsicsWithFilter(List<ResultsFilter> resultsFilterList) throws RegistryHandlerException {
		// Get Extrinsics With only primary registry because we don't want
		// to search against ALL registries. Only use secondary when looking
		// for associated values
	    return getExtrinsicsWithFilter(this.primaryRegistries, resultsFilterList, this.queryMax);
	}
	

	/**
	 * Get the ExtrinsicObjects after results have been filtered. Queries against
	 * all registries specified in the initializer, including primary
	 * and secondary registries.
	 * 
	 * @param resultsFilterList
	 * @return
	 * @throws RegistryHandlerException
	 */
	public RegistryResults getExtrinsicsWithAllRegistries(List<ResultsFilter> resultsFilterList) throws RegistryHandlerException {
		// Get Extrinsics With only primary registry because we don't want
		// to search against ALL registries. Only use secondary when looking
		// for associated values
	    return getExtrinsicsWithFilter(this.allRegistries, resultsFilterList, this.queryMax);
	}
	
	/**
	 * Get the ExtrinsicObjects associated with the current object
	 * being queried.
	 * 
	 * @param lidvid	identifies the ExtrinsicObject list to be queried for
	 * @return			list of ExtrinsicObjects
	 * @throws Exception
	 */
	public ExtendedExtrinsicObject getExtrinsicByLidvid(String lidvid)
			throws Exception {
		List<ResultsFilter> filterList = new ArrayList<ResultsFilter>();
		//ResultsFilter filter = new AttributeFilter("version_id", lidvid);
		//ResultsFilter filter = new SlotFilter("version_id", lidvid);
		
		// Build the filter
		String lid = null;
		String version = null;

		List<String> splitLidvid = Arrays.asList(lidvid.split("::"));
		//lid = splitLidvid.get(0);
		filterList.add(new AttributeFilter(RegistryAttributeWrapper.LOGICAL_IDENTIFIER, splitLidvid.get(0)));

		//Debugger.debug("LID from lidvid: " + lid);

		if (splitLidvid.size() > 1) {
			filterList.add(new SlotFilter("version_id", splitLidvid.get(1)));
		} else if (splitLidvid.size() == 0) { // Handles lidvids with bad format (:
											// instead of ::)
			splitLidvid = Arrays.asList(lidvid.split(":"));
			filterList.add(new AttributeFilter(RegistryAttributeWrapper.LOGICAL_IDENTIFIER, lidvid.substring(0, lidvid.lastIndexOf(":"))));
			filterList.add(new SlotFilter("version_id", splitLidvid.get(1)));

			log.log(Level.SEVERE, "BAD LIDVID - " + lid + " -- " + version);
		}
		
		// Get Extrinsics with ALL registries because we are searching for a specific
		// object, which means we will want to search in all available registries for it
		RegistryResults results = getExtrinsicsWithFilter(this.allRegistries, filterList, DFLT_QUERY_MAX);
		
		List<Object> extList = new ArrayList<Object>();
		if (results.nextPage()) {
			extList = results.getResultObjects();
    		return new ExtendedExtrinsicObject((ExtrinsicObject) extList.get(0));		// We know it will only return one object because
															// it either queries for latest object or the specific
															// version we specify
    	} else {
    		return null;
    	}
    	
	}
	
	/**
	 * Get the ExtrinsicObjects associated with the current object
	 * being queried.
	 * 
	 * @param lidvid	identifies the ExtrinsicObject list to be queried for
	 * @return			list of ExtrinsicObjects
	 * @throws Exception
	 */
	public ExtendedExtrinsicObject getExtrinsicByGuid(String guid)
			throws Exception {
		ResultsFilter filter = new AttributeFilter(RegistryAttributeWrapper.GUID, guid);
		RegistryResults results = getExtrinsicsWithFilter(this.allRegistries, Arrays.asList(filter), this.queryMax);
		
		List<Object> extList = new ArrayList<Object>();
		if (results.nextPage()) {
			extList = results.getResultObjects();
    		return new ExtendedExtrinsicObject((ExtrinsicObject)extList.get(0));		// We know it will only return one object because
    															// it either queries for latest object or the specific
    															// version we specify
    	} else {
    		return null;
    	}
	}
	
	/**
	 * Query the associated objects and map the objects to their slots.
	 * 
	 * @param guid
	 * @param referenceType
	 * @return
	 * @throws Exception
	 */
	public List<ExtendedExtrinsicObject> getAssociatedExtrinsicsByReferenceType(
			ExtendedExtrinsicObject searchExtrinsic, String referenceType) 
					throws Exception {

		List<String> assocLidvids = searchExtrinsic.getSlotValues(referenceType);

		List<ExtendedExtrinsicObject> assocSearchExtList = new ArrayList<ExtendedExtrinsicObject>();
		ExtendedExtrinsicObject assocSearchExt = null;
		
		// Get list of associations for specific association type
		if (assocLidvids != null && !assocLidvids.isEmpty()) {
			for (String assocLidvid : assocLidvids) {
				//Debugger.debug("Associated lidvid - " + assocLidvid);

				// First, check the cache
				assocSearchExt = AssociationCache.get(assocLidvid);
				
				// Second, query for the Extrinsic by the lidvid
				if (assocSearchExt == null) {
					//Debugger.debug(assocLidvid + " -  Not in AssociationCache.");
					assocSearchExt = getExtrinsicByLidvid(assocLidvid);
				} else {
					//Debugger.debug(assocLidvid + " - ASSOCIATION CACHE WORKED");
					//SearchCoreStats.assocCacheHits++;
				}
				
				// Finally, either add it to the list, or throw it in the garbage log
				if (assocSearchExt != null) {
					assocSearchExtList.add(assocSearchExt);
					
					AssociationCache.push(assocSearchExt);
				} else {
					//SearchCoreStats.addMissingAssociationTarget(searchExtrinsic.getLid(), assocLidvid);
				}
			}
		} else {	// We still have a shot, let's check for Association objects
			if (this.checkAssociations) {
				log.log(Level.WARNING, "Couldn't find reference. Checking Association objects.");
				assocSearchExtList = getAssociationsBySourceObject(searchExtrinsic, referenceType);
			
			// TODO Could also check Target Objects, but not needed right now
			}
			
			// The association didn't want to be found
			if (assocSearchExtList == null || assocSearchExtList.isEmpty()) {
				//SearchCoreStats.addMissingSlot(searchExtrinsic.getLid(), referenceType);
			}
		}
		
		return assocSearchExtList;
	}
	
	/**
	 * Query the association objects by the Extrinsic's guid.
	 * 
	 * @param guid
	 * @param referenceType
	 * @return
	 * @throws Exception
	 */
	public List<ExtendedExtrinsicObject> getAssociationsBySourceObject(
			ExtendedExtrinsicObject searchExtrinsic, String associationType) 
					throws Exception {

		//Debugger.debug("Source Object GUID: " + searchExtrinsic.getGuid());
		
	    Map<AssociationRegistryAttribute, String> map = new HashMap<AssociationRegistryAttribute, String>();
	    map.put(AssociationRegistryAttribute.SOURCE_OBJECT, searchExtrinsic.getGuid());
	    map.put(AssociationRegistryAttribute.ASSOCIATION_TYPE, associationType);
	    
	    List<Object> associationList = new ArrayList<Object>();
	    
	    RegistryResults results = getAssociations(this.allRegistries, map);
	    
	    List<ExtendedExtrinsicObject> searchExtList = new ArrayList<ExtendedExtrinsicObject>();
	    
	    Association assoc;
		while (results.nextPage()) {
			associationList = results.getResultObjects();
			for (Object obj : associationList) {
				 assoc = (Association) obj;
				 searchExtList.add(getExtrinsicByGuid(assoc.getTargetObject()));
			}
    	}
	    
	    return searchExtList;
	}
	
	/**
	 * Get all extrinsics based on the RegistryQuery, applying the applicable ResultsFilters
	 * 
	 * @param registryUrlList
	 * @param resultsFilterList
	 * @param version
	 * @param queryMax
	 * @return
	 * @throws RegistryHandlerException
	 */
	private RegistryResults getExtrinsicsWithFilter(List<String> registryUrlList, List<ResultsFilter> resultsFilterList, int queryMax) throws RegistryHandlerException {
	
	ExtrinsicFilter.Builder builder = new ExtrinsicFilter.Builder();
	List<ResultsFilter> slotFilters = new ArrayList<ResultsFilter>();
	slotFilters.addAll(resultsFilterList);
	for (ResultsFilter resultsFilter : resultsFilterList) {
		if (resultsFilter instanceof AttributeFilter) {
			// Apply the filter for the attribute
			resultsFilter.applyFilter(builder);
			slotFilters.remove(resultsFilter);
			
			Debugger.debug(resultsFilter.toString());
		}
	}
	
	ExtrinsicFilter filter = builder.build();
	
	// Create the query
	RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>()
			.filter(filter).build();
	
		try {	
			return new RegistryResults(registryUrlList, query, queryMax, slotFilters);
		} catch (RegistryClientException e) {
			throw new RegistryHandlerException(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	/**
	 * @deprecated							No longer applicable. Use getExtrinsicsWithFilter instead.
	 * @param registryUrlList
	 * @param regAttrValMap
	 * @param version
	 * @param queryMax
	 * @return
	 * @throws RegistryHandlerException
	 */
	@Deprecated
	private RegistryResults getExtrinsics(List<String> registryUrlList, Map<RegistryAttributeWrapper, String> regAttrValMap, String version, int queryMax) throws RegistryHandlerException
			 {
		
		// Create the builder that we will start to populate with filters
		ExtrinsicFilter.Builder builder = new ExtrinsicFilter.Builder();
	    
		// Loop thought the attribute filters and build onto the extrinsic filter
	    for (RegistryAttributeWrapper attribute : regAttrValMap.keySet()) {
	    	attribute.buildOntoFilter(builder, regAttrValMap.get(attribute));
	    }
	    
	    // Lock in the extrinsic filter
		ExtrinsicFilter filter = builder.build();

		// Lock in the query with the extrinsic filter
		RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>()
				.filter(filter).build();
		
		try {
			return new RegistryResults(registryUrlList, query, queryMax);
		} catch (RegistryClientException e) {
			throw new RegistryHandlerException(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	private RegistryResults getAssociations(List<String> registryUrlList, Map<AssociationRegistryAttribute, String> regAttrValMap) throws Exception {
		int queryMax =  DFLT_QUERY_MAX;
		AssociationFilter.Builder builder = new AssociationFilter.Builder();

		for (AssociationRegistryAttribute attribute : regAttrValMap.keySet()) {
			attribute.appendToFilterBuilder(builder, regAttrValMap.get(attribute));
		}

		AssociationFilter filter = builder.build();

		// Create the query
		RegistryQuery<AssociationFilter> query = new RegistryQuery.Builder<AssociationFilter>()
				.filter(filter).build();

		try {
			return new RegistryResults(registryUrlList, query, queryMax);
		} catch (RegistryClientException rce) {
			throw new Exception(rce.getMessage());
		}
	}
	
	/**
	 * Check if the value comes from an association field (*_ref).
	 * If so, need to verify it is a lidvid otherwise query registry
	 * for version number to append to lid.
	 * 
	 * TODO Refactor into SearchCoreExtrinsic
	 * 
	 * @param value
	 * @param registryRef
	 * @return
	 * @throws RegistryClientException 
	 * @throws RegistryServiceException 
	 */
	public String checkForReference(String value, String slotName) throws RegistryClientException {
		if (slotName.contains("_ref")) {
			try {
				if (!value.contains("::")) {
					// This for loop probably isn't right but I'm not worried about it right now
					for (String registryUrl : this.allRegistries) {
					RegistryClient client = new RegistryClient(registryUrl);
					String version = client.getLatestObject(value, ExtrinsicObject.class).getSlot(ExtendedExtrinsicObject.VERSION_ID_SLOT).getValues().get(0);
					value += "::" + version;
					}
				}
			} catch (RegistryServiceException e) {	// Case when association is not found, append version 1.0
				// If ref isn't found, do not append a version.
			}
		}
		
		return value;
	}

	/**
	 * @return the queryMax
	 */
	public int getQueryMax() {
		return queryMax;
	}

	/**
	 * @param queryMax the queryMax to set
	 */
	public void setQueryMax(int queryMax) {
		this.queryMax = queryMax;
	}

	/**
	 * @return the primaryRegistries
	 */
	public List<String> getPrimaryRegistries() {
		return primaryRegistries;
	}

	/**
	 * @param primaryRegistries the primaryRegistries to set
	 */
	public void setPrimaryRegistries(List<String> primaryRegistries) {
		this.primaryRegistries = primaryRegistries;
		resetAllRegistries();
	}
	
	/**
	 * @param primaryRegistry the primaryRegistryUrl to append
	 */
	public void addPrimaryRegistry(String primaryRegistry) {
		if (!this.primaryRegistries.contains(primaryRegistry)) {
			this.primaryRegistries.add(primaryRegistry);
			addToAllRegistries(primaryRegistry);
		}
	}

	/**
	 * @return the secondaryRegistries
	 */
	public List<String> getSecondaryRegistries() {
		return secondaryRegistries;
	}

	/**
	 * @param secondaryRegistries the secondaryRegistries to set
	 */
	public void setSecondaryRegistries(List<String> secondaryRegistries) {
		this.secondaryRegistries = secondaryRegistries;
		
		resetAllRegistries();
	}
	
	/**
	 * @param secondaryRegistry the secondaryRegistry Url to append
	 */
	public void addSecondaryRegistry(String secondaryRegistry) {
		if (!this.secondaryRegistries.contains(secondaryRegistry)) {
			this.secondaryRegistries.add(secondaryRegistry);
			addToAllRegistries(secondaryRegistry);
		}
	}
	
	/**
	 * 
	 */
	public void resetAllRegistries() {
		this.allRegistries.clear();
		this.allRegistries.addAll(this.primaryRegistries);
		this.allRegistries.addAll(this.secondaryRegistries);
	}
	
	public void addToAllRegistries(String registry) {
		if (!this.allRegistries.contains(registry)) {
			this.allRegistries.add(registry);
		}
	}
	
	public boolean doPrimaryRegistriesExist() throws RegistryHandlerException {
		// Verify a registry URL was specified
		if (this.primaryRegistries.isEmpty()) {
			throw new RegistryHandlerException (
					"No primary registries specified, hence no data can be queried.");
		} else {
			return true;
		}
	}
	
	public void setCheckAssociations(boolean checkAssociations) {
		this.checkAssociations = checkAssociations;
	}
	
}
