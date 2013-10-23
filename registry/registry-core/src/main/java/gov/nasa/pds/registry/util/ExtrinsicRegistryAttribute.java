package gov.nasa.pds.registry.util;

import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.query.ExtrinsicFilter;

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
public enum ExtrinsicRegistryAttribute {
	/** Attribute for Logical Identifier. **/
	LOGICAL_IDENTIFIER ("lid") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getLid();
		}
		
		@Override
		public ExtrinsicFilter.Builder appendToFilterBuilder(ExtrinsicFilter.Builder builder, String value) {
			return builder.lid(value);
		}
	},

	/** Attribute for Product name. **/
	NAME ("name") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getName();
		}
		
		@Override
		public ExtrinsicFilter.Builder appendToFilterBuilder(ExtrinsicFilter.Builder builder, String value) {
			return builder.name(value);
		}
	},

	/** Attribute for Object Type. **/
	OBJECT_TYPE ("objectType") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getObjectType();
		}
		
		@Override
		public ExtrinsicFilter.Builder appendToFilterBuilder(ExtrinsicFilter.Builder builder, String value) {
			return builder.objectType(value);
		}
	},

	/** Attribute for MIME Type. **/
	MIME_TYPE ("mimeType") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getMimeType();
		}
		
		@Override
		public ExtrinsicFilter.Builder appendToFilterBuilder(ExtrinsicFilter.Builder builder, String value) {
			return builder.mimeType(value);
		}
	},

	/** Attribute for Description. **/
	DESCRIPTION ("description") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getDescription();
		}
		
		@Override
		public ExtrinsicFilter.Builder appendToFilterBuilder(ExtrinsicFilter.Builder builder, String value) {
			return null;	// Cannot filter on description
		}
	},

	/** Attribute for GUID. **/
	GUID ("guid") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getGuid();
		}
		
		@Override
		public ExtrinsicFilter.Builder appendToFilterBuilder(ExtrinsicFilter.Builder builder, String value) {
			return builder.guid(value);
		}
	},

	/** Attribute for Home. **/
	HOME ("home") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getHome();
		}
		
		@Override
		public ExtrinsicFilter.Builder appendToFilterBuilder(ExtrinsicFilter.Builder builder, String value) {
			return null;	// Not yet implemented by Registry
		}
	},

	/** Attribute for Version Name. **/
	VERSION_NAME ("versionName") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getVersionName();
		}
		
		@Override
		public ExtrinsicFilter.Builder appendToFilterBuilder(ExtrinsicFilter.Builder builder, String value) {
			return builder.versionName(value);
		}
	},

	/** Attribute for Content Version. **/
	CONTENT_VERSION ("contentVersion") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getContentVersion();
		}
		
		@Override
		public ExtrinsicFilter.Builder appendToFilterBuilder(ExtrinsicFilter.Builder builder, String value) {
			return builder.contentVersion(value);
		}
	};

	private String attributeName;
    private static final Map<String, ExtrinsicRegistryAttribute> lookup = new HashMap<String, ExtrinsicRegistryAttribute>();

	static {
	    for(ExtrinsicRegistryAttribute ra : EnumSet.allOf(ExtrinsicRegistryAttribute.class))
	         lookup.put(ra.getAttributeName(), ra);
	}
	
	private ExtrinsicRegistryAttribute(String attributeName) {
		this.attributeName = attributeName;
	}
	
	public String getAttributeName() {
		return this.attributeName;
	}
	
	public static ExtrinsicRegistryAttribute get(String attributeName) {
		return lookup.get(attributeName);
	}
	
	public abstract String getValueFromExtrinsic(ExtrinsicObject extObj);
	
	public abstract ExtrinsicFilter.Builder appendToFilterBuilder(ExtrinsicFilter.Builder builder, String value);
	
	

}
