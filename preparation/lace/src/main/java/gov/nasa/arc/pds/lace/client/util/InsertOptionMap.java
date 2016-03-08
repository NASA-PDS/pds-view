package gov.nasa.arc.pds.lace.client.util;

import gov.nasa.arc.pds.lace.shared.InsertOption;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements a singleton object to associate an
 * <code>InsertOption</code> object with it's unique key.
 */
public class InsertOptionMap {
	
	private static InsertOptionMap instance = null;
	private Map<Integer, InsertOption> map = new HashMap<Integer, InsertOption>();
	
	/**
	 * Creates a new instance of <code>InsertOptionMap</code>.
	 */
	protected InsertOptionMap() {
		// nothing to do		
	}
	
	public static InsertOptionMap getInstance() {
		if (instance == null) {
			instance = new InsertOptionMap();
		}
		return instance;
	}
	
	/**
	 * Returns an <code>InsertOption</code> object that's associated with the specified key.
	 * 
	 * @param key the key whose associated value is to be returned
	 * @return the <code>InsertOption</code> object to which the specified key is mapped, or null
	 * if this map contains no mapping for the key
	 */
	public InsertOption get(int key) {
		return map.get(key);
	}
	
	/**
	 * Associates the specified <code>InsertOption</code> object with the specified key.
	 * 
	 * @param key key with which the specified value is to be associated
	 * @param value the <code>InsertOption</code> object to be associated with the specified key
	 */
	public void put(int key, InsertOption value) {
		map.put(key, value);
	}
}
