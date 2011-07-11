package gov.nasa.pds.imaging.generation.label;

import java.util.List;

import gov.nasa.pds.imaging.generation.context.PDSContext;

/**
 * Specific PDSContext applying to those contexts that contain PDS Data in DOM object form.
 * 
 * @author jpadams
 *
 */
public interface PDSObject extends PDSContext {
	public String getFilePath();
	public void setIndexedGroup(String[] keys);
	public IndexedGroup getIndexedGroup();
	public List getList(String key);
	public String toString();
}
