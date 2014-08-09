//	Copyright 2013-2014, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//
package gov.nasa.pds.registry.client.results;

import java.util.List;

import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.model.wrapper.ExtendedExtrinsicObject;
import gov.nasa.pds.registry.util.Debugger;

public class SlotFilter implements ResultsFilter {
	
	protected String name;
	protected String value;
	
	public SlotFilter(String name, String value) throws RegistryHandlerException {
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
		//List<Object> results = (List<Object>) filterObject;
		ExtendedExtrinsicObject extObj = new ExtendedExtrinsicObject((ExtrinsicObject)filterObject);
		List<String> values = extObj.getSlotValues(this.name);
		if (values != null && values.contains(this.value)) {
			return extObj;
		}
			// TODO add logger to registry core
		return null;
	}
	
	@Override
	public Object applyFilter(RegistryClient client, Object filterObject) throws RegistryServiceException {
		if (filterObject instanceof ExtrinsicObject) {
			if (name.equals(SlotFilter.VERSION_ID)
					&& value.equals(SlotFilter.LATEST_VERSION)) {
				filterObject = client.getLatestObject(((ExtrinsicObject) filterObject).getLid(), 
						ExtrinsicObject.class);
				return filterObject;
			} else {
				return applyFilter(filterObject);
			}
		}
		
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws RegistryHandlerException {
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
