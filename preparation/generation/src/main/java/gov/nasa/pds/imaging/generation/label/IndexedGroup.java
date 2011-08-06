package gov.nasa.pds.imaging.generation.label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexedGroup {

	public List<String> indexList;
	private Map<String, List<String>> subElementMap;
	
	public IndexedGroup(List<String> indexList) {
		this.subElementMap = new HashMap<String, List<String>>();
		this.indexList = new ArrayList<String>(indexList);
	}
	
	public void addSubElement(String key, List<String> subElementList) {
		this.subElementMap.put(key, subElementList);
	}
	
	public String getIndexedValue (String key, int count) {
		return this.subElementMap.get(key).get(count).trim();
	}
	
	public List<String> getIndexList() {
		return this.indexList;
	}	
}
