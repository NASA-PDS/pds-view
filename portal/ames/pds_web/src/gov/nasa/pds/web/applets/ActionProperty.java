package gov.nasa.pds.web.applets;

/**
 * Defines a set of properties on which actions can fire change events.
 * Each event detail is a new string value for the property.
 */
public enum ActionProperty {

	/** The status of the action. */
	STATUS,
	
	/** Path to the volume to be validated. */
	VOLUME_TO_VALIDATE,
	
	/** Count of total problems found. */
	PROBLEM_COUNT,
	
}
