package gov.nasa.arc.pds.lace.shared;

import java.util.List;

/**
 * Represents a simple element within a label template
 * with a textual value.
 */
public class SimpleItem extends LabelElement {

	private static final long serialVersionUID = 1L;
	
	private static final String BASIC_LATIN_PATTERN = "\\p{IsBasicLatin}*";
	
	private String value;

	/**
	 * Creates a new <code>SimpleItem</code> instance.
	 */
	public SimpleItem() {
		// nothing to do
	}

	/**
	 * Gets the simple element's textual value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the simple element's textual value.
	 *
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "[SimpleItem: " + getType().getElementName() + "]";
	}

	@Override
	public SimpleItem copy() {
		SimpleItem copy = new SimpleItem();
		copyData(copy);
		return copy;
	}

	@Override
	protected void copyData(LabelItem destination) {
		super.copyData(destination);
		if (destination instanceof SimpleItem) {
			SimpleItem copy = (SimpleItem) destination;
			copy.value = value;
		}
	}

	@Override
	public boolean isComplete() {
		return (getErrorMessages().length == 0);
		
//		// A simple item is incomplete if it's required and has no value
//		// TODO: or has incomplete children
//		if (isRequired() && (value == null || value.trim().isEmpty())) {
//			return false;
//		} else if (getErrorMessages().length > 0) {
//			return false;
//		}
//		return true;
	}

	@Override
	public boolean isDeletable() {		
		// Cannot delete an item if it's not linked to an InsertOption.	
		InsertOption source = getInsertOption();
		if (source == null) {
			return false;
		}

		// Cannot delete an item if it's the last occurrence.
		int used = source.getUsedOccurrences();
		if ((used == 1 || used == getType().getMinOccurrences())
				&& (source.getTypes().size() == 1 && !source.getTypes().get(0).isWildcard())) {
			return false;
		}
		
		return true;
	}

	/**
	 * Tests whether this item is multi-line.
	 *  
	 * @return true if multi-line, false if one-line
	 */
	public boolean isMultiline() {
		List<String> patterns;
		List<String> enumerations;
		LabelItemType type = getType();
		
		if (type.getMaxLength() >= 0 && type.getMaxLength() <= 255) {
			return false;
		} else if (!type.isWhitespacePreserved()) {
			return false;
		} else if (((patterns = type.getPatterns()) != null && patterns.size() > 0 && !patterns.contains(BASIC_LATIN_PATTERN)) 
				|| ((enumerations = type.getValidValues()) != null && enumerations.size() > 0)) {
			return false;
		} else {
			return true;
		}		
	}

}
