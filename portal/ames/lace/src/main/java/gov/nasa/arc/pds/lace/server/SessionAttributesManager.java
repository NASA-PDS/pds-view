package gov.nasa.arc.pds.lace.server;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

/**
 * Implements a repository of attributes for user sessions. These
 * attributes are not persisted across restarts, so are a good choice
 * for values that cannot be serialized or are large, but can be
 * recreated if needed.
 */
@Singleton
public class SessionAttributesManager {

	private Map<String, Map<String, Object>> attributesForSession = new HashMap<String, Map<String, Object>>();

	/**
	 * Removes all attributes of a session.
	 */
	public void removeSession(String sessionID) {
		attributesForSession.remove(sessionID);
	}

	/**
	 * Gets an attribute of the session, cast to the desired type.
	 *
	 * @param sessionID the session ID
	 * @param key the attribute key
	 * @param clazz the desired class of the attribute
	 * @return the attribute value
	 */
	public <T> T getAttribute(String sessionID, String key, Class<T> clazz) {
		return clazz.cast(getAttributes(sessionID).get(key));
	}

	/**
	 * Sets an attribute of the session.
	 *
	 * @param sessionID the session ID
	 * @param key the attribute key
	 * @param value the attribute value
	 */
	public void setAttribute(String sessionID, String key, Object value) {
		getAttributes(sessionID).put(key, value);
	}

	/**
	 * Removes a single attribute of a session.
	 *
	 * @param sessionID the session ID
	 * @param key the attribute key
	 */
	public void removeAttribute(String sessionID, String key) {
		getAttributes(sessionID).remove(key);
	}

	private Map<String, Object> getAttributes(String sessionID) {
		Map<String, Object> attributes = attributesForSession.get(sessionID);
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
			attributesForSession.put(sessionID, attributes);
		}

		return attributes;
	}

	/**
	 * Clears all attributes in all sessions. Default scope, for unit
	 * testing.
	 */
	void clear() {
		attributesForSession.clear();
	}

}
