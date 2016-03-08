package gov.nasa.arc.pds.lace.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complex element within a label template
 * which can contain one or more <code>LabelItem</code>.
 */
public class Container extends LabelElement {

	private static final long serialVersionUID = 1L;

	private List<LabelItem> contents = new ArrayList<LabelItem>();

	/**
	 * Creates an instance of <code>Container</code>.
	 */
	public Container() {
		// nothing to do
	}

	/**
	 * Gets the contents of this container.
	 *
	 * @return the container's contents as a list of <code>LabelItem</code>
	 */
	public List<LabelItem> getContents() {
		return contents;
	}

	/**
	 * Sets the contents of this container.
	 *
	 * @param contents the container's contents to set as a list of <code>LabelItem</code>
	 */
	public void setContents(List<LabelItem> contents) {
		this.contents = contents;
	}

	/**
	 * Adds a label item to the list of contents at the specified index.
	 *
	 * @param index the index at which to add the item
	 * @param item the item to add
	 */
	public void addItem(int index, LabelItem item) {
		this.contents.add(index, item);
	}

	/**
	 * Removes a label item at the specified position from the list of contents.
	 *
	 * @param index the index of the item to be removed
	 * @return the item previously at the specified position
	 */
	public LabelItem removeItem(int index) {
		return this.contents.remove(index);
	}

	/**
	 * Removes an item from the container contents.
	 *
	 * @param item the item to remove
	 * @return true, if the item was found and removed, false if the item was not found
	 */
	public boolean removeItem(LabelItem item) {
		return this.contents.remove(item);
	}

	@Override
	public String toString() {
		return "[Container: " + getType().getElementName() + "]";
	}

	@Override
	public LabelItem copy() {
		Container copy = new Container();
		copyData(copy);
		return copy;
	}

	@Override
	protected void copyData(LabelItem destination) {
		super.copyData(destination);
		if (destination instanceof Container) {
			Container copy = (Container) destination;
			copy.contents = new ArrayList<LabelItem>();
			for (LabelItem item : contents) {
				copy.contents.add(item.copy());
			}
		}
	}

	@Override
	public boolean isComplete() {
		// A container is incomplete if it has incomplete children
		// TODO: or incomplete attributes
		for (LabelItem item : contents) {
			if (!item.isComplete()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isDeletable() {		
		return false;
	}

}
