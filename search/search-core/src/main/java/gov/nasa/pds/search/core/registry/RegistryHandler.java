package gov.nasa.pds.search.core.registry;

import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.search.core.cli.options.Flag;
import gov.nasa.pds.search.core.cli.options.InvalidOptionException;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;
import gov.nasa.pds.search.core.registry.objects.RegistryAttribute;
import gov.nasa.pds.search.core.registry.objects.SearchCoreExtrinsic;
import gov.nasa.pds.search.core.schema.Product;
import gov.nasa.pds.search.core.stats.SearchCoreStats;
import gov.nasa.pds.search.core.util.Debugger;
import gov.nasa.pds.search.core.util.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

//TODO Class that will take a lot of the registry related code from the registryExtractor
public class RegistryHandler {

	/** Output logger **/
	private static Logger log = Logger.getLogger(RegistryHandler.class.getName());
	
	/** In order to scale for very large registry products, need to page the registry output **/
	private static final int QUERY_PAGE_MAX = 500;
	
	private int queryMax = Constants.QUERY_MAX;
	
	private List<String> primaryRegistries;
	
	private List<String> secondaryRegistries;
	
	private List<String> allRegistries;
	
	public RegistryHandler(List<String> primaryRegistries, List<String> secondaryRegistries,
			int queryMax) {
		this.primaryRegistries = new ArrayList<String>(primaryRegistries);
		this.secondaryRegistries = new ArrayList<String>(secondaryRegistries);
		
		this.allRegistries = new ArrayList<String>();
		this.queryMax = queryMax;

		resetAllRegistries();
	}
	
	/**
	 * Get the ExtrinsicObjects from the given object type.
	 * 
	 * @param objectType	@see gov.nasa.pds.registry.model.ExtrinsicObject
	 * @return				list of ExtrinsicObject for given objectType
	 * @throws Exception	thrown if there are issues with the RegistryClient
	 */
	public List<SearchCoreExtrinsic> getExtrinsicsByObjectInfo(
			String objectType, String objectName) throws Exception
			 {
	    log.log(new ToolsLogRecord(ToolsLevel.DEBUG, "----- " + objectType + " -----"));
		
	    Map<RegistryAttribute, String> map = new HashMap<RegistryAttribute, String>();
	    map.put(RegistryAttribute.OBJECT_TYPE, objectType);
	    map.put(RegistryAttribute.NAME, objectName);
	    
	    List<SearchCoreExtrinsic> extList = new ArrayList<SearchCoreExtrinsic>();
	    for (String registryUrl : this.primaryRegistries) {
	    	try {
			 extList.addAll(SearchCoreExtrinsic.asSearchCoreExtrinsics(
					getExtrinsics(registryUrl,
					map, null, queryMax)));
	    	} catch (NullPointerException e) {
	    		throw new RegistryHandlerException("No " + objectType + " Extrinsics found in " + registryUrl);
	    	}
	    }
	    
	    return extList;
	}
	

	/**
	 * Get the ExtrinsicObjects associated with the current object
	 * being queried.
	 * 
	 * @param lidvid	identifies the ExtrinsicObject list to be queried for
	 * @return			list of ExtrinsicObjects
	 * @throws Exception
	 */
	public SearchCoreExtrinsic getExtrinsicByLidvid(String lidvid)
			throws Exception {
		// Build the filter
		String lid = null;
		String version = null;

		List<String> lidList = Arrays.asList(lidvid.split("::"));
		lid = lidList.get(0);

		Debugger.debug("LID from lidvid: " + lid);

		if (lidList.size() > 1) {
			version = lidList.get(1);
		} else if (lidList.size() == 0) { // Handles lidvids with bad format (:
											// instead of ::)
			lidList = Arrays.asList(lidvid.split(":"));
			lid = lidvid.substring(0, lidvid.lastIndexOf(":"));
			version = lidList.get(lidList.size() - 1);

			log.warning("***** BAD LIDVID - " + lid + " -- "
					+ version);
		}
		
	    Map<RegistryAttribute, String> map = new HashMap<RegistryAttribute, String>();
	    map.put(RegistryAttribute.LOGICAL_IDENTIFIER, lid);
	    
	    List<ExtrinsicObject> extList = new ArrayList<ExtrinsicObject>();
	    for (String registryUrl : this.allRegistries) {
	    	extList = getExtrinsics(registryUrl, map, version, Constants.QUERY_MAX);
	    	if (extList != null && !extList.isEmpty()) {
	    		return new SearchCoreExtrinsic(extList.get(0));		// We know it will only return one object because
	    															// it either queries for latest object or the specific
	    															// version we specify
	    	}
	    }
	    return null;
	}
	
	/**
	 * Query the associated objects and map the objects to their slots.
	 * 
	 * @param guid
	 * @param referenceType
	 * @return
	 * @throws Exception
	 */
	public List<SearchCoreExtrinsic> getAssociationsByReferenceType(
			SearchCoreExtrinsic searchExtrinsic, String referenceType) 
					throws Exception {

		List<String> assocLidvids = searchExtrinsic.getSlotValues(referenceType);

		List<SearchCoreExtrinsic> assocSearchExtList = new ArrayList<SearchCoreExtrinsic>();
		
		// Get list of associations for specific association type
		if (assocLidvids != null && !assocLidvids.isEmpty()) {
			for (String assocLidVid : assocLidvids) {
				Debugger.debug("Associated lidvid - " + assocLidVid);

				SearchCoreExtrinsic assocSearchExt = getExtrinsicByLidvid(assocLidVid);

				if (assocSearchExt != null) {
					assocSearchExtList.add(assocSearchExt);
				} else {
					SearchCoreStats.addMissingAssociationTarget(searchExtrinsic.getLid(), assocLidVid);
				}
			}
		} else {
			SearchCoreStats.addMissingSlot(searchExtrinsic.getLid(), referenceType);
		}
		return assocSearchExtList;
	}
	
	private List<ExtrinsicObject> getExtrinsics(String registryUrl, Map<RegistryAttribute, String> regAttrValMap, String version, int queryMax) throws Exception
			 {
		
		ExtrinsicFilter.Builder builder = new ExtrinsicFilter.Builder();
	    
	    for (RegistryAttribute attribute : regAttrValMap.keySet()) {
	    	attribute.appendToFilterBuilder(builder, regAttrValMap.get(attribute));
	    }
	    
		ExtrinsicFilter filter = builder.build();

		// Create the query
		RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>()
				.filter(filter).build();
		
		List<ExtrinsicObject> results = null;
		try {
			if (registryExists(registryUrl)) {
				RegistryClient client = new RegistryClient(registryUrl);
	
				results = new ArrayList<ExtrinsicObject>();
				
				PagedResponse<ExtrinsicObject> pr;
				
				int pageLength = Math.min(queryMax, QUERY_PAGE_MAX);
				SearchCoreExtrinsic searchExtrinsic;
				
				for (int start=1; start<queryMax+pageLength; start+=pageLength) {
					Debugger.debug("start: " + start + ", queryPageMax: " + QUERY_PAGE_MAX + ", pageLength: " + pageLength);
					
					if (start+pageLength > queryMax) {
						pr = client.getExtrinsics(query, start, queryMax-start+1);
					} else {
						pr = client.getExtrinsics(query, start, pageLength);
					}
					
					// Examine the results of the query to grab the latest product for
					// each ExtrinsicObject
					//if (pr.getNumFound() != 0 ) {
					if (pr.getResults().size() != 0 ) {
						List<String> lidList = new ArrayList<String>();		// Used to maintain list of objects we have
						String lid, lidvid;
						for (ExtrinsicObject extrinsic : pr.getResults()) {
							searchExtrinsic = new SearchCoreExtrinsic(extrinsic);
							lid = searchExtrinsic.getLid();
							lidvid = searchExtrinsic.getLidvid();
							Debugger.debug("\n\n----- " + lid + " -----");
		
							// Use list to verify we haven't already included this
							// product in the results. Handles ignoring multiple version of same product
							if (!lidList.contains(lid) && lid != null) {
								if (version == null || lidvid == null) {
									results.add(client.getLatestObject(lid,
										ExtrinsicObject.class));
									lidList.add(lid);
								} else {
									//if (new SearchCoreExtrinsic(extrinsic).getLidvid().equals(lid + "::" + version)) {
									//versionId = new SearchCoreExtrinsic(extrinsic).getSlotValues(Constants.VERSION_ID_SLOT);
									
									// TODO refactor this into search extrinsic
									//if (versionId != null) {
										Debugger.debug("lidvid: " + lidvid);
										if (lidvid.equals(lid + "::" + version)) {
											Debugger.debug("Adding associated extrinsic - "
													+ extrinsic.getLid());
											results.add(extrinsic);
											return results;
										}
									//}
								}
							}
						}
						lidList.clear();
					} else {
						Debugger.debug("\n\n No More Results Found \n\n");
						results.addAll(pr.getResults());
						break;
					}
				}
				return results;
			}
		} catch (RegistryServiceException rse) {
			// Ignore. Nothing found.
		} catch (RegistryClientException rce) {
			throw new Exception(rce.getMessage());
		}
		return null;
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
					String version = client.getLatestObject(value, ExtrinsicObject.class).getSlot(Constants.VERSION_ID_SLOT).getValues().get(0);
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
	 * Returns the url is the registry exists, else returns null
	 * 
	 * @param url
	 * @return
	 */
	private boolean registryExists(String url) {
		if (!Utility.urlExists(url)) {
			SearchCoreStats.addBadRegistry(url);
			return false;
		} else {
			return true;
		}
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
	}
	
	/**
	 * @param secondaryRegistry the primaryRegistryUrl to append
	 */
	public void addSecondaryRegistry(String secondaryRegistry) {
		if (!this.secondaryRegistries.contains(secondaryRegistry)) {
			this.secondaryRegistries.add(secondaryRegistry);
			addToAllRegistries(secondaryRegistry);
		}
	}
	
	/**
	 * @param secondaryRegistries the secondaryRegistries to set
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
					"No primary registries specified, hence no data can be queried. "
							+ "Try specifying via command-line using the "
							+ Flag.PRIMARY.getShortName() + " or "
							+ Flag.PROPERTIES.getShortName() + " flags.");
		} else {
			return true;
		}
	}
	
	
	
}
