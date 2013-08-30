package gov.nasa.pds.search.core.registry.objects;

import gov.nasa.pds.search.core.util.Debugger;

import java.util.LinkedHashMap;
import java.util.logging.Logger;


public class AssociationCache {

	private static Logger log = Logger.getLogger(AssociationCache.class.getName());
	
	private static final int CACHE_SIZE = 50;
	
	private static LinkedHashMap<String, SearchCoreExtrinsic> searchExtMap = new LinkedHashMap<String, SearchCoreExtrinsic>();
	
	/**
	 * Checks if extrinsic is already in cache. If so, send the extrinsic
	 * back to the caller, remove it, and put it at the end of the queue.
	 * If it isn't in the cache, but the cache size is maxed out, pop
	 * off the oldest entry.
	 * 
	 * @param searchExt
	 */
	public static void push(SearchCoreExtrinsic searchExt) {
	    Debugger.debug("AssociationCache.push - " + searchExt.getLidvid());
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
		
		Debugger.debug("AssociationCache.pop - " + key);

		searchExtMap.remove(key);
	}
	
	public static SearchCoreExtrinsic get(String lidvid) {
		Debugger.debug("AssociationCache.get - " + lidvid);
		
		return searchExtMap.get(lidvid);
	}
	
	public static void flush() {
		searchExtMap.clear();
	}
	
	public static void main(String args[]) {
		LinkedHashMap<String, String> test = new LinkedHashMap<String, String>();
		test.put("1", "a");
		test.put("2", "b");
		test.put("3", "c");
		test.put("4", "d");
		
		for (String str : test.keySet()) {
			System.out.println(str + " - " + test.get(str));
		}
		
		String key = test.keySet().iterator().next();
		System.out.println("Removing " + key);
		
		test.remove(key);
		
		for (String str : test.keySet()) {
			System.out.println(str + " - " + test.get(str));
		}
		
	}
	
}
