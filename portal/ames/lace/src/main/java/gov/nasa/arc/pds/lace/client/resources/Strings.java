package gov.nasa.arc.pds.lace.client.resources;

import java.util.Map;

import com.google.gwt.i18n.client.Constants;

/**
 * Defines translatable strings for the application.
 */
public interface Strings extends Constants {

	/**
	 * Gets a map between the IDS for variable styles and their UI captions.
	 *
	 * @return a map between variable style IDs and UI strings
	 */
	Map<String, String> variableStyles();

}
