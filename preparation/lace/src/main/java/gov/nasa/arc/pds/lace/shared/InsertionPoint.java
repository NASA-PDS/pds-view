package gov.nasa.arc.pds.lace.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a model object to represent a location where
 * new content can be inserted.
 */
public class InsertionPoint extends LabelItem {

	private static final long serialVersionUID = 1L;

	private List<InsertOption> alternatives = new ArrayList<InsertOption>();
	private String displayType;

	/**
	 * Defines the ways in which an insertion point can be
	 * displayed.
	 */
	public static enum DisplayType {

		/** Display as a choice of options. */
		CHOICE("choice"),

		/** Display as an optional item. */
		OPTIONAL("optional"),

		/** Display as a button to push to insert new content. */
		PLUS_BUTTON("plus_button"),
		
		/** Display as a required item. */
		REQUIRED("required");

		private String displayType;

		private DisplayType(String displayType) {
			this.displayType = displayType;
		}

		/**
		 * Gets the display type, as a string.
		 *
		 * @return the display type
		 */
		public String getDisplayType() {
			return this.displayType;
		}
	}

	/**
	 * Creates a new <code>InsertionPoint</code> instance.
	 */
	public InsertionPoint() {
		// nothing to do
	}

	/**
	 * Gets a list of <code>InsertOption</code> that indicates the types and the
	 * minimum and maximum number of elements that can be inserted for each type.
	 *
	 * @return a list of insert options
	 */
	public List<InsertOption> getAlternatives() {
		return alternatives;
	}

	/**
	 * Sets a list of <code>InsertOption</code> that indicates the types and the
	 * minimum and maximum number of elements that can be inserted for each type.
	 *
	 * @param alternatives a list of insert options
	 */
	public void setAlternatives(List<InsertOption> alternatives) {
		this.alternatives = alternatives;
	}

	/**
	 * Sets the display type, as string.
	 *
	 * @param type the new display type
	 */
	public void setDisplayType(String type) {
		this.displayType = type;
	}

	/**
	 * Gets the display type, as a string.
	 *
	 * @return the display type
	 */
	public String getDisplayType() {
		return this.displayType;
	}

	@Override
	public boolean isRequired() {
		for (InsertOption alternative : getAlternatives()) {
			if (alternative.getUsedOccurrences() == 0
				&& alternative.getMinOccurrences() > 0) {
					return true;
			}		
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (InsertOption alternative : getAlternatives()) {
			if (builder.length() > 0) {
				builder.append(',');
			}
			builder.append(alternative.toString());
		}
		return "[InsertionPoint: " + builder.toString()
			+ " - type=" + displayType
			+ "]";
	}

	@Override
	public LabelItem copy() {
		InsertionPoint copy = new InsertionPoint();
		copyData(copy);
		return copy;
	}

	@Override
	protected void copyData(LabelItem destination) {
		super.copyData(destination);
		if (destination instanceof InsertionPoint) {
			InsertionPoint copy = (InsertionPoint) destination;
			copy.displayType = displayType;			
			copy.alternatives = new ArrayList<InsertOption>();
			copy.alternatives.addAll(alternatives);
		}
	}

	@Override
	public boolean isComplete() {
		// A required choice is always incomplete.
		if (displayType.equals(DisplayType.CHOICE.getDisplayType()) && isRequired()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isDeletable() {	
		return false;
	}

}
