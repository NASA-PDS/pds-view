package gov.nasa.arc.pds.lace.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complex element within a label template
 * which can contain one or more <code>LabelItem</code>.
 */
public class Container extends LabelElement {

	private static final long serialVersionUID = 1L;

	private String context;
	private List<LabelItem> contents = new ArrayList<LabelItem>();

	/**
	 * Creates a new <code>Container</code> instance.
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

	/**
	 * Gets the context path for this element. The context path
	 * is the path of QNames followed from the root to this element.
	 * Each component of the path is obtained by calling the
	 * <code>toString</code> method for the QName of the element,
	 * and the components are separated by slashes.
	 * 
	 * @return the context path, as a string
	 */
	public String getContext() {
		return context;
	}
	
	/**
	 * Sets the context path for this element.
	 * 
	 * @param newContext the new context path, as a string
	 */
	public void setContext(String newContext) {
		context = newContext;
	}
	
	@Override
	public String toString() {
		return "[Container: " + getType().getElementName() + "]";
	}

	@Override
	public Container copy() {
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
			copy.context = context;
			for (LabelItem item : contents) {
				copy.contents.add(item.copy());
			}
		}
	}

	@Override
	public boolean isComplete() {
		return (getErrorMessages().length == 0);
		
//		// A container is incomplete if it has incomplete children
//		// TODO: or incomplete attributes
//		for (LabelItem item : contents) {
//			if (!item.isComplete()) {
//				return false;
//			}
//		}
//		
//		if (getErrorMessages().length > 0) {
//			return false;
//		}
//
//		return true;
	}

	@Override
	public boolean isDeletable() {		
		// Cannot delete a container if it's not linked to an
		// InsertOption (which covers min==max==1).
		InsertOption source = getInsertOption();
		if (source == null) {
			return false;
		}
		
		// Cannot delete a container if it's the last
		// occurrences (used == min).	
		if (source.getUsedOccurrences() == getType().getMinOccurrences() 				
				&& (source.getTypes().size() == 1 && !source.getTypes().get(0).isWildcard())) {
			return false;
		}
		
		return true;
	}
}
