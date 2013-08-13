package gov.nasa.pds.search.core.registry.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO Hold a certain cache of associations in case common ones appear
public class AssociationCache {

	private static final int CACHE_SIZE = 10;
	
	private static Map<String, SearchCoreExtrinsic> extMap = new HashMap<String, SearchCoreExtrinsic>();
	
	/**
	 * Checks if extrinsic is already in cache, then adds to list.
	 * @param searchExt
	 */
	public static void addExtrinsic(SearchCoreExtrinsic searchExt) {
		String lidvid = searchExt.getLidvid();
		if (extMap.containsKey(lidvid)) {
			extMap.remove(lidvid);
			extMap.put(lidvid, searchExt);
		} else if (extMap.size() == CACHE_SIZE) {
			pop();
			extMap.put(lidvid, searchExt);
		}
	}
	
	/**
	 * Removes the 0th element from the list
	 */
	private static void pop() {
		extMap.remove(extMap.get(0));
	}
	
	public static void main(String args[]) {
		List<String> test = new ArrayList<String>();
		test.add("a");
		test.add("b");
		
		test.remove(0);
		
		System.out.println(test.get(0));
		
	}
	
}
