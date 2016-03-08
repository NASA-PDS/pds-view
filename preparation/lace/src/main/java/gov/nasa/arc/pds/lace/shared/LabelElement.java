package gov.nasa.arc.pds.lace.shared;


/**
 * Represents an element item within a label template which can either
 * be a <code>Container</code> or a <code>SimpleItem</code>.
 */
public abstract class LabelElement extends LabelItem {

	private static final long serialVersionUID = 1L;

	private LabelItemType type;

	/**
	 * Creates a new <code>LabelElement</code> instance.
	 */
	public LabelElement() {
		// nothing to do
	}

	/**
	 * Gets the label element's type.
	 * 
	 * @return a <code>LabelItemType</code> object that
	 * holds the typing information for this element
	 */
	public LabelItemType getType() {
		return type;
	}

	/**
	 * Sets the label element's type.
	 * 
	 * @param type a <code>LabelItemType</code> object that
	 * holds the typing information for this element
	 */
	public void setType(LabelItemType type) {
		this.type = type;
	}

	@Override
	public boolean isRequired() {
		return getType().getMinOccurrences() > 0;
	}

	@Override
	protected void copyData(LabelItem destination) {
		super.copyData(destination);
		if (destination instanceof LabelElement) {
			LabelElement copy = (LabelElement) destination;
			copy.type = type;
		}
	}

}
