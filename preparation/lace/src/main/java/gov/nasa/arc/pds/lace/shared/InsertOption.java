package gov.nasa.arc.pds.lace.shared;

import java.io.Serializable;
import java.util.List;

/**
 * Implements a model object that holds information used
 * by the <code>InsertionPoint</code> object.
 */
public class InsertOption implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Holds the minimum number of elements that can be inserted from this insert option. */
	private int minOccurrences;
	/** Holds the maximum number of elements that can be inserted from this insert option. */
	private int maxOccurrences;
	/** Counts the number of elements that have been inserted from this insert option. */
	private int usedOccurrences;
	/** List of LabelItemType that indicates the types that can be inserted. */
	private List<LabelItemType> types;
	
	/**
	 * Creates a new <code>InsertOption</code> instance.
	 */
	public InsertOption() {
		// nothing to do
	}

	/**
	 * Gets the minimum number of elements that can
	 * be inserted from this insert option.
	 * 
	 * @return the value of the minimum occurrences
	 */
	public int getMinOccurrences() {
		return minOccurrences;
	}

	/**
	 * Sets the minimum number of elements that can
	 * be inserted from this insert option.
	 * 
	 * @param minOccurrences the value of the minimum occurrences
	 */
	public void setMinOccurrences(int minOccurrences) {
		if (minOccurrences < 0) {
			String msg = "The value of minOccurrences cannot be negative.";
			throw new IllegalArgumentException(msg);
		}
		this.minOccurrences = minOccurrences;
	}

	/**
	 * Gets the maximum number of elements that can
	 * be inserted from this insert option.
	 * 
	 * @return the value of the maximum occurrences
	 */
	public int getMaxOccurrences() {
		return maxOccurrences;
	}

	/**
	 * Sets the maximum number of elements that can be
	 * inserted from this insert option.
	 * 
	 * @param maxOccurrences the value of the maximum occurrences
	 */
	public void setMaxOccurrences(int maxOccurrences) {
		if (maxOccurrences == 0 || maxOccurrences < -1) {
			String msg = "The value of maxOccurrences cannot be negative.";
			throw new IllegalArgumentException(msg);
		}
		this.maxOccurrences = maxOccurrences;
	}

	/**
	 * Gets the number of elements that have been
	 * inserted from this insert option.
	 * 
	 * @return the value of the used occurrences
	 */
	public int getUsedOccurrences() {
		return usedOccurrences;
	}

	/**
	 * Sets the number of elements that have been
	 * inserted from this insert option.
	 * 
	 * @param usedOccurrences the value of the used occurrences
	 * @throws IllegalArgumentException if usedOccurrences is negative
	 */
	public void setUsedOccurrences(int usedOccurrences) {
		if (usedOccurrences < 0) {
			String msg = "The value of usedOccurrences cannot be negative.";
			throw new IllegalArgumentException(msg);
		}
		this.usedOccurrences = usedOccurrences;
	}

	/**
	 * Gets a list of LabelItemType that indicates
	 * the types that can be inserted.
	 * 
	 * @return a list of label item types
	 */
	public List<LabelItemType> getTypes() {
		return types;
	}

	/**
	 * Sets a list of <code>LabelItemType</code> that indicates
	 * the types that can be inserted.
	 * 
	 * @param types a list of label item types that can be inserted
	 */
	public void setTypes(List<LabelItemType> types) {
		this.types = types;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (LabelItemType type : getTypes()) {
			if (builder.length() > 0) {
				builder.append(',');
			}
			builder.append(type.getElementName());
		}
		return "[InsertOption: " + builder.toString()
			+ " - min=" + minOccurrences
			+ " max="   + maxOccurrences
			+ " used="  + usedOccurrences
			+ "]";
	}
}
