package gov.nasa.arc.pds.lace.shared;

/**
 * Represents a simple element within a label template
 * with a textual value.
 */
public class SimpleItem extends LabelElement {

	private static final long serialVersionUID = 1L;

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
	public LabelItem copy() {
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
		// A simple item is incomplete if it's required and has no value
		// TODO: or has incomplete children
		if (isRequired() && (value == null || value.trim().isEmpty())) {
			return false;
		}
		return true;
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
				&& source.getTypes().size() == 1) {
			return false;
		}
		
		return true;
	}
}
