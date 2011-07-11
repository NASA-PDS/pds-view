package gov.nasa.pds.imaging.generation.context;

public interface PDSContext {
	public String getContext();
	public String get(String key);
	public String getUnits(String key);
}
