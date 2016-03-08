package gov.nasa.arc.pds.lace.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements an object that holds typing information for model objects.
 */
public class LabelItemType implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isComplex;	    			// True, if this type is a container for subelements.
	private int minOccurrences;					// Holds the minOccurrences value from the type particle
	private int maxOccurrences;	    			// Holds the maxOccurrences value from the type particle
	private int minLength;						// For simple types, the minimum value length
	private int maxLength;						// For simple types, the maximum value length
	private String elementName;		 			// The name of the element that will hold the type
	private String elementNamespace; 			// The namespace of the element that will hold the type
	private String typeName;		 			// The name for this type, or null if this is an anonymous type
	private String typeNamespace;
	private List<LabelItem> initialContents;	// For complex types, a list of template items that should be the initial contents when this type is added
	//private enum valueType {};				// Either NUMERIC, DATE, or REGEX (needs to be an enumeration), depending on the value type constraints
	//patterns									// A list of regular expressions, for REGEX types

	private List<String> validValues;			// A list of valid values, or null if there is no validation.

	/** Creates a new label item type instance. */
	public LabelItemType() {
		// nothing to do
	}

	/**
	 *
	 * @return the minOccurrences
	 */
	public int getMinOccurrences() {
		return minOccurrences;
	}

	/**
	 *
	 * @param minOccurrences the minOccurrences to set
	 */
	public void setMinOccurrences(int minOccurrences) {
		this.minOccurrences = minOccurrences;
	}

	/**
	 *
	 * @return the maxOccurrences
	 */
	public int getMaxOccurrences() {
		return maxOccurrences;
	}

	/**
	 *
	 * @param maxOccurrences the maxOccurrences to set
	 */
	public void setMaxOccurrences(int maxOccurrences) {
		this.maxOccurrences = maxOccurrences;
	}

	/**
	 *
	 * @return true, if this type is a container for sub-elements, false, otherwise
	 */
	public boolean isComplex() {
		return isComplex;
	}

	/**
	 * Sets a flag to indicate whether this type is a container for sub-elements or not.
	 *
	 * @param flag true, if the item is a container for sub-elements
	 */
	public void setComplex(boolean flag) {
		this.isComplex = flag;
	}

	/**
	 *
	 * @return the minLength
	 */
	public int getMinLength() {
		return minLength;
	}

	/**
	 *
	 * @param minLength the minLength to set
	 */
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	/**
	 *
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 *
	 * @param maxLength the maxLength to set
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 *  Gets an unmodifiable view of the initial contents for this type.
	 *
	 * @return list of label items
	 */
	public List<LabelItem> getInitialContents() {
		//return Collections.unmodifiableList(initialContents);
		return initialContents;
	}

	/**
	 * Sets the initial contents for this type.
	 *
	 * @param initialContents list of label items
	 */
	public void setInitialContents(List<LabelItem> initialContents) {
		this.initialContents = initialContents;
	}

	/**
	 *
	 * @return the element name
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 *
	 * @param elementName the element name to set
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 *
	 * @return the elementNamespace
	 */
	public String getElementNamespace() {
		return elementNamespace;
	}

	/**
	 *
	 * @param elementNamespace the elementNamespace to set
	 */
	public void setElementNamespace(String elementNamespace) {
		this.elementNamespace = elementNamespace;
	}

	/**
	 *
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 *
	 * @param typeName the typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 *
	 * @return the typeNamespace
	 */
	public String getTypeNamespace() {
		return typeNamespace;
	}

	/**
	 *
	 * @param typeNamespace the typeNamespace to set
	 */
	public void setTypeNamespace(String typeNamespace) {
		this.typeNamespace = typeNamespace;
	}

	/**
	 * Gets the valid values.
	 *
	 * @return a list of valid values, or null if there is no validation
	 */
	public List<String> getValidValues() {
		return validValues;
	}

	/**
	 * Sets the valid values.
	 *
	 * @param values an array of string values
	 */
	public void setValidValues(String[] values) {
		validValues = new ArrayList<String>();
		for (String value : values) {
			validValues.add(value);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null || !(obj instanceof LabelItemType)) {
			return false;
		}

		LabelItemType other = (LabelItemType) obj;

		return
			isComplex == other.isComplex
			&& maxLength == other.maxLength
			&& minLength == other.minLength
			&& maxOccurrences == other.maxOccurrences
			&& minOccurrences == other.minOccurrences
			&& ((elementName==null && other.elementName==null)
					|| (elementName!=null && elementName.equals(other.elementName)))
			&& ((elementNamespace==null && other.elementNamespace==null)
					|| (elementNamespace!=null && elementNamespace.equals(other.elementNamespace)))
			&& ((typeName==null && other.typeName==null)
					|| (typeName!=null && typeName.equals(other.typeName)))
			&& ((typeNamespace==null && other.typeNamespace==null)
					|| (typeNamespace!=null && typeNamespace.equals(other.typeNamespace)))
			&& ((initialContents==null && other.initialContents==null)
					|| (initialContents!=null && initialContents.equals(other.initialContents)));
	}

	@Override
	public int hashCode() {
		String s =
			elementName + "/"
			+ elementNamespace + "/"
			+ typeName + "/"
			+ typeNamespace;

		return
			((((s.hashCode() * 17
			+ (initialContents==null ? 0 : initialContents.hashCode())) * 17
			+ (isComplex ? 1 : 0) * 17
			+ maxLength) * 17
			+ maxOccurrences) * 17
			+ minLength) * 17
			+ minOccurrences;
	}
}
