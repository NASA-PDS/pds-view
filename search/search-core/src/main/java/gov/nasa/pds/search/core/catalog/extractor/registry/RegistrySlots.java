package gov.nasa.pds.search.core.catalog.extractor.registry;

import gov.nasa.pds.registry.model.Slot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class RegistrySlots {

	private Logger LOG = Logger.getLogger(this.getClass().getName());

	private Map<String, List<String>> slotMap;

	public RegistrySlots() {
		slotMap = new HashMap<String, List<String>>();
	}

	public RegistrySlots(Set<Slot> initSlots) {
		slotMap = new HashMap<String, List<String>>();

		setSlotMap(initSlots);
	}

	public void setSlotMap(Set<Slot> slotSet) {
		for (Slot slot : slotSet) {
			this.slotMap.put(slot.getName().trim(), slot.getValues());
		}
	}

	public Map<String, List<String>> getSlotMap() {
		return this.slotMap;
	}

	public List<String> get(String key) {
		if (this.slotMap.containsKey(key.trim())) {
			return this.slotMap.get(key.trim());
		} else {
			this.LOG.warning("Key not found : " + key);
			return Arrays.asList("UNK");
		}
	}
}
