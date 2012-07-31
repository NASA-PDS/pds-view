package gov.nasa.pds.search.core.extractor.registry;

import java.util.Arrays;

import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.search.core.extractor.InvalidProductClassException;

/**
 * Provides the values for a given attribute specified in a product
 * class configuration file.
 * 
 * @author jpadams
 * @version $Revision$
 *
 */
public class RegistryAttributes {
	/** Attribute for Logical Identifier **/
	public final static String LOGICAL_IDENTIFIER = "lid";
	
	/** Attribute for Product name **/
	public final static String NAME = "name";
	
	/** Attribute for Object Type **/
	public final static String OBJECT_TYPE = "objectType";
	
	/** Attribute for MIME Type **/
	public final static String MIME_TYPE = "mimeType";

	/** Attribute for Description **/
	public final static String DESCRIPTION = "description";

	/** Attribute for GUID **/
	public final static String GUID = "guid";
	
	/** Attribute for Home **/
	public final static String HOME = "home";
	
	/** Attribute for Version Name **/
	public final static String VERSION_NAME = "versionName";
	
	/** Attribute for Content Version **/
	public final static String CONTENT_VERSION = "contentVersion";
	
	public final static String[] ATTR_LIST = {LOGICAL_IDENTIFIER, NAME, OBJECT_TYPE, MIME_TYPE, DESCRIPTION, GUID, HOME, VERSION_NAME, CONTENT_VERSION};


	/**
	 * Gets the value of the input attribute from the given Extrinsic object.
	 * Returns attribute value as a String.  Throws InvalidProductClassException when
	 * attribute is one of known Extrinsic object attributes.
	 * 
	 * @param attribute
	 * @param extObject
	 * @return
	 */
	public static String getAttributeValue(String attribute, ExtrinsicObject extObject) throws InvalidProductClassException {
		if (attribute.equals(LOGICAL_IDENTIFIER)) {
			return extObject.getLid();
		} else if (attribute.equals(NAME)) {
			return extObject.getName();
		} else if (attribute.equals(DESCRIPTION)) {
			return extObject.getDescription();
		} else if (attribute.equals(GUID)) {
			return extObject.getGuid();
		} else if (attribute.equals(HOME)) {
			return extObject.getHome();
		} else if (attribute.equals(VERSION_NAME)) {
			return extObject.getVersionName();
		} else if (attribute.equals(CONTENT_VERSION)) {
			return extObject.getContentVersion();
		} else if (attribute.equals(MIME_TYPE)) {
			return extObject.getMimeType();
		} else if (attribute.equals(OBJECT_TYPE)) {
			return extObject.getObjectType();
		} else {
			throw new InvalidProductClassException("Attribute '" + attribute + "' does not exist.");
		}
	}
	
	public static boolean isAttribute(String attribute) {
		return Arrays.asList(ATTR_LIST).contains(attribute);
	}

}
