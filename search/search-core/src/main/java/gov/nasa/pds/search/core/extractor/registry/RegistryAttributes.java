package gov.nasa.pds.search.core.extractor.registry;

import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.extractor.SolrSchemaField;
import gov.nasa.pds.search.core.schema.DataType;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides the values for a given attribute specified in a product class
 * configuration file.
 * 
 * @author jpadams
 * @version $Revision$
 * 
 */
public enum RegistryAttributes {
	/** Attribute for Logical Identifier. **/
	LOGICAL_IDENTIFIER ("lid") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getLid();
		}
	},

	/** Attribute for Product name. **/
	NAME ("name") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getName();
		}
	},

	/** Attribute for Object Type. **/
	OBJECT_TYPE ("objectType") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getObjectType();
		}
	},

	/** Attribute for MIME Type. **/
	MIME_TYPE ("mimeType") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getMimeType();
		}
	},

	/** Attribute for Description. **/
	DESCRIPTION ("description") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getDescription();
		}
	},

	/** Attribute for GUID. **/
	GUID ("guid") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getGuid();
		}
	},

	/** Attribute for Home. **/
	HOME ("home") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getHome();
		}
	},

	/** Attribute for Version Name. **/
	VERSION_NAME ("versionName") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getVersionName();
		}
	},

	/** Attribute for Content Version. **/
	CONTENT_VERSION ("contentVersion") {
		@Override
		public String getValueFromExtrinsic(ExtrinsicObject extObj) {
			return extObj.getContentVersion();
		}
	};

	private String attributeName;
    private static final Map<String, RegistryAttributes> lookup = new HashMap<String, RegistryAttributes>();

	static {
	    for(RegistryAttributes ra : EnumSet.allOf(RegistryAttributes.class))
	         lookup.put(ra.getAttributeName(), ra);
	}
	
	private RegistryAttributes(String attributeName) {
		this.attributeName = attributeName;
	}
	
	public String getAttributeName() {
		return this.attributeName;
	}
	
	public static RegistryAttributes get(String attributeName) {
		return lookup.get(attributeName);
	}
	
	public abstract String getValueFromExtrinsic(ExtrinsicObject extObj);
	
	

}
