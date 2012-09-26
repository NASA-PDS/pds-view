package gov.nasa.pds.search.core.extractor.registry;

import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Slot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Extension of Registry Client functionality to specifically address needs of
 * the Search Core. This class is used to maintain a map of all the slots for an
 * extrinsic object, as well as maintain a list of all missing slots.
 * 
 * @author jpadams
 * @version $Revision$
 * 
 */
public class ExtrinsicObjectSlots {

	private Map<String, List<String>> slotMap;
	private ExtrinsicObject extObj;
	private boolean missingSlots;
	private List<String> missingSlotList;

	public ExtrinsicObjectSlots() {
		this.extObj = null;
		this.slotMap = new HashMap<String, List<String>>();
		this.missingSlotList = new ArrayList<String>();
		this.missingSlots = false;
	}

	public ExtrinsicObjectSlots(ExtrinsicObject extObj) {
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
		/*
		 * if (key.equals(RegistryAttributes.LOGICAL_IDENTIFIER)) { return
		 * Arrays.asList(this.extObj.getLid()); } if (key.equals("name")) {
		 * return Arrays.asList(this.extObj.getName()); }
		 */
		if (this.slotMap.containsKey(key.trim())) {
			return this.slotMap.get(key.trim());
		} else {
			// this.LOG.warning("Key not found : " + key);
			this.missingSlotList.add(this.extObj.getLid() + " - " + key);
			this.missingSlots = true;
			return new ArrayList<String>();
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
