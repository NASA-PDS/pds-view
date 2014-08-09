package gov.nasa.pds.registry.client.results;

import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryServiceException;

public interface ResultsFilter {
	
    public static final String VERSION_ID = "version_id";
	public static final String LATEST_VERSION = "latest";
	
    /**
     * Apply the filter on the object.
     * 
     * @param filterObject
     * @return	if object passes through filter, returns the filterObject
     * 			else returns null
     */
	public Object applyFilter(Object filterObject);
	
	/**
	 * Apply the filter on the object and also pass a registry client object
	 * for potential queries to the registry
	 * 
	 * @param client
	 * @param filterObject
	 * @return
	 */
	public Object applyFilter(RegistryClient client, Object filterObject)  throws RegistryServiceException;

}
