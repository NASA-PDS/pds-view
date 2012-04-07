package gov.nasa.pds.search.core.extractor.registry;

import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.search.core.constants.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class RegistrySlots {

	private Logger LOG = Logger.getLogger(this.getClass().getName());

	private Map<String, List<String>> slotMap;
	private ExtrinsicObject extObj;
	private boolean missingSlots;
	private List<String> missingSlotList;

	public RegistrySlots() {
		this.extObj = null;
		this.slotMap = new HashMap<String, List<String>>();
		this.missingSlotList = new ArrayList<String>();
		this.missingSlots = false;
	}

	public RegistrySlots(ExtrinsicObject extObj) {
		this.slotMap = new HashMap<String, List<String>>();
		this.extObj = extObj;
		this.missingSlotList = new ArrayList<String>();
		this.missingSlots = false;

		setSlotMap(extObj.getSlots());
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
		if (key.equals(Constants.LOGICAL_IDENTIFIER)) {
			return Arrays.asList(this.extObj.getLid());
		}
		if (key.equals("name")) {
			return Arrays.asList(this.extObj.getName());
		}
		if (this.slotMap.containsKey(key.trim())) {
			return this.slotMap.get(key.trim());
		} else {
			// this.LOG.warning("Key not found : " + key);
			this.missingSlotList.add(this.extObj.getLid() + " - " + key);
			this.missingSlots = true;
			return Arrays.asList("UNK");
		}
	}

	public boolean isMissingSlots() {
		return this.missingSlots;
	}

	public String getObjectType() {
		return this.extObj.getObjectType();
	}

	public List<String> getMissingSlotList() {
		return this.missingSlotList;
	}
}
