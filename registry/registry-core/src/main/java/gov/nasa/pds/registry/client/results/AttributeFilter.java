package gov.nasa.pds.registry.client.results;

import gov.nasa.pds.registry.model.wrapper.RegistryAttributeWrapper;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter.Builder;
import gov.nasa.pds.registry.util.Debugger;

public class AttributeFilter implements ResultsFilter {

	private String name;
	private String value;
	
	private RegistryAttributeWrapper raw;

	public AttributeFilter(RegistryAttributeWrapper raw, String value) {
		this.raw = raw;
		this.value = value;
		
		this.name = raw.getName();
	}
	
	public AttributeFilter(String name, String value) {
		this.name = name;
		this.value = value;
		
		this.raw = RegistryAttributeWrapper.get(name);
	}

	/**
	 * 
	 * @param filter	ExtrinsicFilter.Builder object expected
	 * @param value		String value expected containing the expected attribute value
	 * @return			ExtrinsicFilter.Builder with updated filter or the filter object that was input
	 */
	public Object applyFilter(Object filter) {
		// Check if the object is Builder object, as expected
		if (filter instanceof ExtrinsicFilter.Builder) {
			// Cast the object so it is easier to work with
			ExtrinsicFilter.Builder builder = (ExtrinsicFilter.Builder) filter;
			
			// Build onto the builder using the RegistryAttribute wrapper
			//this.name.buildOntoFilter(builder, (String)value);
			Debugger.debug("Applying AttributeFilter: " + this.raw.getName() + " - " + this.value);
			this.raw.buildOntoFilter(builder, this.value);

			return builder;
		} else {	// One of the objects is not as expected, throw error
			// TODO add logger to registry core
			return filter;
		}
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

	public RegistryAttributeWrapper getRaw() {
		return raw;
	}

	public void setRaw(RegistryAttributeWrapper raw) {
		this.raw = raw;
	}
	
	@Override
	public String toString() {
		return "Applying Filter:  AttributeFilter.name: " + this.name + ", AttributeFilter.value: " + this.value;
	}
	
}
