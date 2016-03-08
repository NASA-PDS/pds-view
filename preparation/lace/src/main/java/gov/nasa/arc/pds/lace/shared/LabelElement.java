package gov.nasa.arc.pds.lace.shared;


/**
 * Represents an element item within a label template which can either
 * be a <code>Container</code> or a <code>SimpleItem</code>.
 */
public abstract class LabelElement extends LabelItem {

	private static final long serialVersionUID = 1L;

	private LabelItemType type;
	private InsertOption alternative = null;

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

	/**
	 * Gets the insert option that was the source for this element.
	 * 
	 * @return the <code>InsertOption</code> instance or null if
	 * the label element not inserted from an insertion point
	 */
	public InsertOption getInsertOption() {
		return alternative;
	}
	
	/**
	 * Links this label element to the specified insert
	 * option that was it's source.
	 * 
	 * @param alternative the <code>InsertOption</code> instance
	 * that was the source for this label element. Or, null for
	 * label elements not inserted from an insertion point.
	 */
	public void setInsertOption(InsertOption alternative) {
		this.alternative = alternative;
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
			copy.alternative = (alternative != null) ? alternative.copy() : alternative;
		}
	}
}
