package gov.nasa.arc.pds.lace.shared;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents an element item within a label template which can either
 * be a <code>Container</code> or a <code>SimpleItem</code>.
 */
public abstract class LabelElement extends LabelItem {

	private static final long serialVersionUID = 1L;

	private String id;
	private LabelItemType type;
	private InsertOption alternative = null;
	private List<AttributeItem> attributes = new ArrayList<AttributeItem>();
	private List<String> errorMessages = new ArrayList<String>();

	/**
	 * Creates a new <code>LabelElement</code> instance.
	 */
	public LabelElement() {
		// nothing to do
	}

	/**
	 * Gets the unique ID of this item.
	 * 
	 * @return the unique ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Sets the unique ID of this item.
	 * 
	 * @param newID the new, unique ID
	 */
	public void setID(String newID) {
		id = newID;
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
	 * Gets a list of attributes for this item.
	 * 
	 * @return a list of attributes
	 */
	public List<AttributeItem> getAttributes() {
		return attributes;
	}
	
	/**
	 * Adds an attribute to the list of attributes for this item.
	 * 
	 * @param attribute the new attribute
	 */
	public void addAttribute(AttributeItem attribute) {
		attributes.add(attribute);
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
	
	/**
	 * Gets the error messages associated with this item.
	 * 
	 * @return an array of error messages
	 */
	public String[] getErrorMessages() {
		return errorMessages.toArray(new String[errorMessages.size()]);
	}
	
	/**
	 * Adds a new error message to this item.
	 * 
	 * @param message the new error message
	 */
	public void addErrorMessage(String message) {
		errorMessages.add(message);
	}
	
	/**
	 * Removes all error messages for this item.
	 */
	public void clearErrorMessages() {
		errorMessages.clear();
	}

	@Override
	protected void copyData(LabelItem destination) {
		super.copyData(destination);
		if (destination instanceof LabelElement) {
			LabelElement copy = (LabelElement) destination;
			copy.id = id;
			copy.type = type;
			copy.alternative = (alternative != null) ? alternative.copy() : alternative;
			copy.attributes = new ArrayList<AttributeItem>();
			for (AttributeItem item : attributes) {
				AttributeItem attributeCopy = new AttributeItem();
				item.copyData(attributeCopy);
				copy.attributes.add(attributeCopy);
			}
		}
	}
	
}
