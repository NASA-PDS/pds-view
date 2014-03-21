package gov.nasa.pds.registry.util;

import java.util.List;

import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.query.ExtrinsicFilter.Builder;

public class SlotFilter implements ResultsFilter {
	
	private String name;
	private String value;	
	
	public SlotFilter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Check the input "filter" (ExtrinsicObject) contains the
	 * "value" (Slot)
	 * 
	 */
	@Override
	public Object applyFilter(Object filterObject) {
		if (filterObject instanceof ExtrinsicObject) {
			//List<Object> results = (List<Object>) filterObject;
			ExtendedExtrinsicObject extObj = new ExtendedExtrinsicObject((ExtrinsicObject)filterObject);
			List<String> values = extObj.getSlotValues(this.name);
			if (values != null && values.contains(this.value)) {
				return extObj;
			}
		}
			// TODO add logger to registry core
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "Applying Filter:  SlotFilter.name: " + this.name + ", SlotFilter.value: " + this.value;
	}
	
}
