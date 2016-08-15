package gov.nasa.arc.pds.lace.shared;

/**
 * Implements a model object representing an attribute value.
 */
public class AttributeItem extends SimpleItem {

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[Attribute: " + getValue() + "]";
	}

	@Override
	public boolean isDeletable() {
		// Attributes aren't repeating, so aren't deletable.
		return false;
	}

	@Override
	public boolean isMultiline() {
		// Attributes are always one-line.
		return false;
	}
	
	@Override
	public AttributeItem copy() {
		AttributeItem copy = new AttributeItem();
		copyData(copy);
		return copy;
	}

	@Override
	protected void copyData(LabelItem destination) {
		super.copyData(destination);
	}

}
