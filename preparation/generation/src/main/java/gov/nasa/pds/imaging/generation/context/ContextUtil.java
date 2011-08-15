package gov.nasa.pds.imaging.generation.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContextUtil {

	private List<Map<String, String>> objectList;
	private Map<String, List<String>> subElementMap;
	private int elementCount;
	
	public ContextUtil() {
		this.objectList = new ArrayList<Map<String, String>>();
		this.subElementMap = new HashMap<String, List<String>>();
		//this.indexList = new ArrayList<String>(indexList);
	}
	
	public void addDictionaryElement(String key, List<String> subElementList) {
		this.subElementMap.put(cleanKey(key), subElementList);
		this.elementCount = subElementList.size();
	}
	
	public void setDictionary() {
		Map<String, String> map;
		Set<String> keyList = this.subElementMap.keySet();
		for (int i=0; i<this.elementCount; i++) {
			map = new HashMap<String, String>();
			for (String key : keyList) {
				map.put(key, this.subElementMap.get(key).get(i).trim());
			}
			this.objectList.add(map);
		}
	}
	
	public List<Map<String, String>> getDictionary() {
		return this.objectList;
	}
	
	private String cleanKey(String key) {
		String[] keys = key.split("\\.");
		return keys[keys.length-1];
	}
	
	//public String getIndexedValue (String key, int count) {
	//	return this.subElementMap.get(key).get(count).trim();
	//}
	
	//public List<String> getIndexList() {
	//	return this.indexList;
	//}	
}
