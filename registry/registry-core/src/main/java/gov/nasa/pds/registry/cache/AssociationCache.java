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
package gov.nasa.pds.registry.cache;

//import gov.nasa.pds.search.core.util.Debugger;

import gov.nasa.pds.registry.model.wrapper.ExtendedExtrinsicObject;

import java.util.LinkedHashMap;

/**
 * Maintains a cache of associated SearchCoreExtrinsic objects
 * for easy access during reference queries. This improves performance
 * by minimizing queries to the Registry Client
 * 
 * @author jpadams
 */
public class AssociationCache {
	
	/** Defined size for number of objects in map of SearchCoreExtrinsic objects **/
	private static final int CACHE_SIZE = 100;
	
	/** Map of lidvid to SearchCoreExtrinsic object for easy retrieval.
	 *  Used LinkedHashMap to maintain ordering for easy push/pop from cache
	 */
	private static LinkedHashMap<String, ExtendedExtrinsicObject> searchExtMap = new LinkedHashMap<String, ExtendedExtrinsicObject>();
	
	/**
	 * Checks if extrinsic is already in cache. If so, send the extrinsic
	 * back to the caller, remove it, and put it at the end of the queue.
	 * If it isn't in the cache, but the cache size is maxed out, pop
	 * off the oldest entry.
	 * 
	 * @param searchExt
	 */
	public static void push(ExtendedExtrinsicObject searchExt) {
	    //Debugger.debug("AssociationCache.push - " + searchExt.getLidvid());
		String lidvid = searchExt.getLidvid();
		if (searchExtMap.containsKey(lidvid)) {
			searchExtMap.remove(lidvid);
		} else if (searchExtMap.size() == CACHE_SIZE) {
			pop();
		}
		searchExtMap.put(lidvid, searchExt);
	}
	
	/**
	 * Removes the first element from the map
	 */
	private static void pop() {
		String key = searchExtMap.keySet().iterator().next();
		
		//Debugger.debug("AssociationCache.pop - " + key);

		searchExtMap.remove(key);
	}
	
	/**
	 * Get the object by lidvid
	 * @param lidvid
	 * @return
	 */
	public static ExtendedExtrinsicObject get(String lidvid) {
		return searchExtMap.get(lidvid);
	}
	
	/**
	 * Clear out the cache
	 */
	public static void flush() {
		searchExtMap.clear();
	}
	
}
