package gov.nasa.pds.imaging.generation.label;

import gov.nasa.pds.imaging.generation.context.PDSContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Specific PDSContext applying to those contexts that contain PDS Data in DOM object form.
 * 
 * @author jpadams
 *
 */
public interface PDSObject extends PDSContext {
	public String getFilePath();
	//public void setIndexedGroup(String[] keys);
	//public IndexedGroup getIndexedGroup();
	public void setDictionary(String[] keys);
	public List<Map<String, String>> getDictionary();
	public List getList(String key);
}
