package gov.nasa.arc.pds.lace.server;

import javax.inject.Singleton;

/**
 * Implements an object that generates unique IDs for model
 * items.
 */
@Singleton
public class IDGenerator {

	private long count = 0;
	
	/**
	 * Generates a new, unique ID, based on a counter.
	 * 
	 * @return the unique ID
	 */
	public synchronized String getID() {
		return Long.toString(count++);
	}
	
}
