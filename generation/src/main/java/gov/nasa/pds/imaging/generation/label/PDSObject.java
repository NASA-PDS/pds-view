package gov.nasa.pds.imaging.generation.label;

import gov.nasa.pds.imaging.generation.context.PDSContext;

import java.util.List;

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
}
