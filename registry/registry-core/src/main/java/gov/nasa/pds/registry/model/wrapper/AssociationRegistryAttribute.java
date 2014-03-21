package gov.nasa.pds.registry.model.wrapper;

import gov.nasa.pds.registry.query.AssociationFilter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides the values for a given attribute specified in a product class
 * configuration file.
 * 
 * @author jpadams
 * @version $Revision: 11832 $
 * 
 */
public enum AssociationRegistryAttribute {
	/** Attribute for Logical Identifier. **/
	ASSOCIATION_TYPE ("associationType") {
		@Override
		public AssociationFilter.Builder appendToFilterBuilder(AssociationFilter.Builder builder, String value) {
			return builder.associationType(value);
		}
	},

	/** Attribute for Product name. **/
	SOURCE_OBJECT ("sourceObject") {
		@Override
		public AssociationFilter.Builder appendToFilterBuilder(AssociationFilter.Builder builder, String value) {
			return builder.sourceObject(value);
		}
	},
	
	TARGET_OBJECT ("targetObject") {
		@Override
		public AssociationFilter.Builder appendToFilterBuilder(AssociationFilter.Builder builder, String value) {
			return builder.sourceObject(value);
		}
	};

	private String attributeName;
    private static final Map<String, AssociationRegistryAttribute> lookup = new HashMap<String, AssociationRegistryAttribute>();

	static {
	    for(AssociationRegistryAttribute ra : EnumSet.allOf(AssociationRegistryAttribute.class))
	         lookup.put(ra.getAttributeName(), ra);
	}
	
	private AssociationRegistryAttribute(String attributeName) {
		this.attributeName = attributeName;
	}
	
	public String getAttributeName() {
		return this.attributeName;
	}
	
	public static AssociationRegistryAttribute get(String attributeName) {
		return lookup.get(attributeName);
	}
	
	public abstract AssociationFilter.Builder appendToFilterBuilder(AssociationFilter.Builder builder, String value);
	
	

}
