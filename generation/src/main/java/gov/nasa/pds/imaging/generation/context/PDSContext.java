package gov.nasa.pds.imaging.generation.context;

/**
 * Interface for the PDS Context to be used for extracting values
 * for the Velocity Templates.
 * 
 * @author jpadams
 *
 */
public interface PDSContext {
	public String getContext();
	public String get(String key);
	public String getUnits(String key);
}
