package gov.nasa.arc.pds.lace.shared;

/**
 * Defines constant strings for project item attributes.
 */
public interface ItemAttributes {

	/** The style of variables in a template. */
	public static final String VARIABLE_STYLE = "variable.style";

	/** The template does not contain variables. */
	public static final String NO_STYLE = "NO_STYLE";
	/** The template uses Velocity-style variables. */
	public static final String VELOCITY_STYLE = "VELOCITY_STYLE";
	/** The template uses FreeMarker-style variables. */
	public static final String FREEMARKER_STYLE = "FREEMARKER_STYLE";

}
