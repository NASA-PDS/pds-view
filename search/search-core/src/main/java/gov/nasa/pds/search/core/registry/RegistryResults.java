/**
 * 
 */
package gov.nasa.pds.search.core.registry;

import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.registry.objects.SearchCoreExtrinsic;
import gov.nasa.pds.search.core.util.Debugger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jpadams
 *
 */
public class RegistryResults {
	
	private int start;
	
	private int queryMax;
	
	private int registryIndex;
	
	private String version;
	
	private String currentRegistryUrl;
	
	private List<String> registryUrlList;
	
	private RegistryClient client;
	
	private List<ExtrinsicObject> results;
	
	private RegistryQuery<ExtrinsicFilter> query;
	
	public RegistryResults(String registryUrl, RegistryQuery<ExtrinsicFilter> query, String version, int queryMax) throws RegistryClientException {
		this(Arrays.asList(registryUrl), query, version, queryMax);
	}
	
	public RegistryResults(List<String> registryUrlList, RegistryQuery<ExtrinsicFilter> query, String version, int queryMax) throws RegistryClientException {
		this.registryIndex = -1;
		this.start = 1;
		this.registryUrlList = registryUrlList;
		this.query = query;
		this.version = version;
		this.queryMax = queryMax;
		
		this.currentRegistryUrl = null;
		
		this.results = new ArrayList<ExtrinsicObject>();
		
		nextRegistry();
	}
	
	public List<SearchCoreExtrinsic> next() throws RegistryServiceException, RegistryClientException {
		int pageLength = Math.min(this.queryMax, Constants.QUERY_PAGE_MAX);
		
		if (this.start+pageLength > this.queryMax) {
			pageLength = this.queryMax - this.start + 1;
		}
		
		//System.out.println(this.start + " to " + (this.start+pageLength-1));
		performRegistryQuery(this.start, pageLength);
		
		this.start += pageLength;
		
		return SearchCoreExtrinsic.asSearchCoreExtrinsics(this.results);
	}
	
	public void performRegistryQuery(int start, int pageLength) throws RegistryServiceException, RegistryClientException {
		PagedResponse<ExtrinsicObject> pr;
		
		SearchCoreExtrinsic searchExtrinsic;
		this.results.clear();
		
		//
		// CHANGE THIS.  LOOP SHOULD BE REMOVED AND ADD TO "NEXT()" type method
		// 
		//for (int start=1; start<queryMax+pageLength; start+=pageLength) {
			//Debugger.debug("start: " + start + ", queryPageMax: " + QUERY_PAGE_MAX + ", pageLength: " + pageLength);

			//System.out.println(this.currentRegistryUrl);
			this.client = new RegistryClient(this.currentRegistryUrl);
			pr = this.client.getExtrinsics(this.query, start, pageLength);
			
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
						if (this.version == null || lidvid == null) {
							this.results.add(this.client.getLatestObject(lid,
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
									this.results.add(extrinsic);
									//return results;
								}
							//}
						}
					}
				}
				
			} else {
				Debugger.debug("\n\n No More Results Found \n\n");
				this.results.addAll(pr.getResults());
				//break;
			}
			
			this.client = null;
			
			if (this.results.isEmpty() && nextRegistry()) {
				this.start = 1;
				next();
			}
		//}
	}
	
	private boolean nextRegistry() throws RegistryClientException {
		if (++this.registryIndex < this.registryUrlList.size()) {
			this.currentRegistryUrl = this.registryUrlList.get(this.registryIndex);
			return true;
		} else {
			return false;
		}
	}

}
