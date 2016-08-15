package gov.nasa.arc.pds.lace.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements an object that holds typing information for model objects.
 */
public class LabelItemType implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isComplex;	    			// True, if this type is a container for sub-elements
	private int minOccurrences;					// Holds the minOccurrences value from the type particle
	private int maxOccurrences;	    			// Holds the maxOccurrences value from the type particle
	private int minLength = -1;					// For simple types, the minimum value length
	private int maxLength = -1;					// For simple types, the maximum value length
	private String elementName;		 			// The name of the element that will hold the type
	private String elementNamespace; 			// The namespace of the element that will hold the type
	private String typeName;		 			// The name for this type, or null if this is an anonymous type
	private String typeNamespace;				// The namespace for this type
	private List<AttributeItem> initialAttributes;  // For all types, a list of attributes.
	private List<LabelItem> initialContents;	// For complex types, a list of items that should be the initial contents when this type is added
	private List<String> validValues;			// A list of valid values, or null if there is no validation.
	private String defaultValue;                // The default value, if there is one.
	private boolean isWildcard = false;			// True, if this is a wildcard type
	private boolean isWhitespacePreserved;		// For simple types, true, if the whitespace constraint is set to "preserve"
	private List<String> patterns;				// For simple types, a list of regular expressions, for REGEX types
	//private enum valueType {};				// Either NUMERIC, DATE, or REGEX (needs to be an enumeration), depending on the value type constraints

	private boolean isID;						// True if XML type is "ID".
	private boolean isIDREF;					// True if XML type is "IDREF".

	/** Documentation on how to use the type. */
	private String documentation;

	/**
	 * Creates a new <code>LabelItemType</code> instance.
	 */
	public LabelItemType() {
		initialAttributes = new ArrayList<AttributeItem>();
		initialContents = new ArrayList<LabelItem>();
	}

	/**
	 * Gets the minimum occurrences value for the label element
	 * associated with this type.
	 *
	 * @return the minimum occurrences value
	 */
	public int getMinOccurrences() {
		return minOccurrences;
	}

	/**
	 * Sets the minimum occurrences value from the type particle.
	 *
	 * @param minOccurrences the minimum occurrences value
	 */
	public void setMinOccurrences(int minOccurrences) {
		this.minOccurrences = minOccurrences;
	}

	/**
	 * Gets the maximum occurrences value for the label element
	 * associated with this type.
	 *
	 * @return the maximum occurrences value
	 */
	public int getMaxOccurrences() {
		return maxOccurrences;
	}

	/**
	 * Sets the maximum occurrences value from the type particle.
	 *
	 * @param maxOccurrences the maximum occurrences value
	 */
	public void setMaxOccurrences(int maxOccurrences) {
		this.maxOccurrences = maxOccurrences;
	}

	/**
	 * Tests whether this type is a container for sub-elements.
	 *
	 * @return true, if this type is a container for sub-elements, false, otherwise
	 */
	public boolean isComplex() {
		return isComplex;
	}

	/**
	 * Sets a flag to indicate whether this type is a container for sub-elements or not.
	 *
	 * @param flag true, if the item is a container for sub-elements, false, otherwise
	 */
	public void setComplex(boolean flag) {
		this.isComplex = flag;
	}

	/**
	 * Gets the minimum value length for simple elements.
	 *
	 * @return the minimum value length, or -1 if no value is set
	 */
	public int getMinLength() {
		return minLength;
	}

	/**
	 * Sets the minimum value length for simple elements.
	 *
	 * @param minLength the minimum value length
	 */
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	/**
	 * Gets the maximum value length for simple elements.
	 *
	 * @return the maximum value length, or -1 if no value is set
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * Sets the maximum value length for simple elements.
	 *
	 * @param maxLength the maximum value length
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * Gets a list of attributes for the type. The returned
	 * list is not modifiable.
	 *
	 * @return a list of attributes
	 */
	public List<AttributeItem> getInitialAttributes() {
		return Collections.unmodifiableList(initialAttributes);
	}

	/**
	 * Gets the initial contents for this type.
	 * TODO: The view is modifiable--Change the method to
	 * return an unmodifiable view of the initial contents.
	 *
	 * @return a list of label items
	 */
	public List<LabelItem> getInitialContents() {
		//return Collections.unmodifiableList(initialContents);
		return initialContents;
	}

	/**
	 * Sets the list of attributes for this type.
	 *
	 * @param initialAttributes a list of attributes
	 */
	public void setInitialAttributes(List<AttributeItem> initialAttributes) {
		this.initialAttributes.clear();
		if (initialAttributes != null) {
			this.initialAttributes.addAll(initialAttributes);
		}
	}

	/**
	 * Sets the initial contents for this type.
	 *
	 * @param initialContents a list of label items
	 */
	public void setInitialContents(List<LabelItem> initialContents) {
		this.initialContents.clear();
		if (initialContents != null) {
			this.initialContents.addAll(initialContents);
		}
	}

	/**
	 * Gets the name of the element that will hold the type.
	 *
	 * @return the element name
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 * Sets the name of the element that will hold the type.
	 *
	 * @param elementName the element name
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * Gets the namespace of the element that will hold the type.
	 *
	 * @return a string value
	 */
	public String getElementNamespace() {
		return elementNamespace;
	}

	/**
	 * Sets the namespace of the element that will hold the type.
	 *
	 * @param elementNamespace a string value of the element namespace
	 */
	public void setElementNamespace(String elementNamespace) {
		this.elementNamespace = elementNamespace;
	}

	/**
	 * Gets the name for this type.
	 *
	 * @return a string value
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Sets a name for this type.
	 *
	 * @param typeName the name for this type
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * Gets the namespace for this type.
	 *
	 * @return a string value
	 */
	public String getTypeNamespace() {
		return typeNamespace;
	}

	/**
	 * Sets the namespace for this type.
	 *
	 * @param typeNamespace the type namespace
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

	/**
	 * Adds to the set of valid values for the type. If the valid values
	 * list has not been initialized, set it to a zero length list first.
	 *
	 * @param values an array of string values
	 */
	public void addValidValues(String[] values) {
		if (validValues == null) {
			validValues = new ArrayList<String>();
		}
		for (String value : values) {
			validValues.add(value);
		}
	}

	/**
	 * Gets the default value, if there is one.
	 *
	 * @return the default value, or null if there is no default
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value.
	 *
	 * @param value the new default value
	 */
	public void setDefaultValue(String value) {
		defaultValue = value;
	}

	/**
	 * Tests whether this type is a wildcard.
	 *
	 * @return true, if this type is a wildcard, false, otherwise
	 */
	public boolean isWildcard() {
		return isWildcard;
	}

	/**
	 * Sets a flag to indicate whether this type is a wildcard.
	 *
	 * @param flag true if the item is a wildcard
	 */
	public void setWildcard(boolean flag) {
		this.isWildcard = flag;
	}

	/**
	 * Sets whether the whitespace is preserved.
	 *
	 * @param preserve true if "preserve", false otherwise
	 */
	public void setWhitespacePreserved(boolean preserve) {
		this.isWhitespacePreserved = preserve;
	}

	/**
	 * Tests whether whitespace is preserved for this type.
	 *
	 * @return true if whitespace is preserved
	 */
	public boolean isWhitespacePreserved() {
		return isWhitespacePreserved;
	}

	/**
	 * Gets a list of regular expressions for simple types.
	 *
	 * @return a list of regular expressions
	 */
	public List<String> getPatterns() {
		return patterns;
	}

	/**
	 * Sets a list of regular expressions for simple types.
	 *
	 * @param patterns a list of regular expressions
	 */
	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}

	/**
	 * Gets the documentation for this type.
	 *
	 * @return the documentation as safe HTML, or null if no documentation
	 */
	public String getDocumentation() {
		return documentation;
	}

	/**
	 * Sets the documentation for this type.
	 *
	 * @param documentation the documentation as safe HTML, or null if no documentation
	 */
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	/**
	 * Tests whether the type is an XML "ID" type.
	 *
	 * @return true if the type is an XML ID
	 */
	public boolean isID() {
		return isID;
	}

	/**
	 * Sets whether the type is an XML "ID" type.
	 *
	 * @param flag true if the type is an XML ID
	 */
	public void setID(boolean flag) {
		isID = flag;
	}

	/**
	 * Tests whether the type is an XML "IDREF" type.
	 *
	 * @return true if the type is an XML IDREF
	 */
	public boolean isIDREF() {
		return isIDREF;
	}

	/**
	 * Sets whether the type is an XML "IDREF" type.
	 *
	 * @param flag true if the type is an XML IDREF
	 */
	public void setIDREF(boolean flag) {
		isIDREF = flag;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null || !(obj instanceof LabelItemType)) {
			return false;
		}

		LabelItemType other = (LabelItemType) obj;

		return
			isComplex == other.isComplex
			&& isWildcard == other.isWildcard
			&& maxLength == other.maxLength
			&& minLength == other.minLength
			&& maxOccurrences == other.maxOccurrences
			&& minOccurrences == other.minOccurrences
			&& isWhitespacePreserved == other.isWhitespacePreserved
			&& ((elementName==null && other.elementName==null)
					|| (elementName!=null && elementName.equals(other.elementName)))
			&& ((elementNamespace==null && other.elementNamespace==null)
					|| (elementNamespace!=null && elementNamespace.equals(other.elementNamespace)))
			&& ((typeName==null && other.typeName==null)
					|| (typeName!=null && typeName.equals(other.typeName)))
			&& ((typeNamespace==null && other.typeNamespace==null)
					|| (typeNamespace!=null && typeNamespace.equals(other.typeNamespace)))
			&& ((initialAttributes==null && other.initialAttributes==null)
					|| (initialAttributes!=null && initialAttributes.equals(other.initialAttributes)))
			&& ((initialContents==null && other.initialContents==null)
					|| (initialContents!=null && initialContents.equals(other.initialContents)))
			&& ((patterns==null && other.patterns==null)
					|| (patterns!=null && patterns.equals(other.patterns)))
			&& ((defaultValue==null && other.defaultValue==null)
					|| (defaultValue!=null && defaultValue.equals(other.defaultValue)));
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
