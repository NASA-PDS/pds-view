package gov.nasa.arc.pds.lace.shared;


/**
 * Represents an element item within a label template which can either
 * be a <code>Container</code> or a <code>SimpleItem</code>.
 */
public abstract class LabelElement extends LabelItem {

	private static final long serialVersionUID = 1L;

	private LabelItemType type;

	/**
	 * Creates a new label element instance.
	 */
	public LabelElement() {
		// nothing to do
	}

	/**
	 *
	 * @return the type
	 */
	public LabelItemType getType() {
		return type;
	}

	/**
	 *
	 * @param type the type to set
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
