package gov.nasa.arc.pds.lace.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a result type for a container for which
 * its contents is changed either by the means of 
 * insertion or deletion.
 */
public class ResultType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List<LabelItem> contents = new ArrayList<LabelItem>();	
	private int fromIndex;
	private int toIndex;	
	
	/**
	 * Creates a new <code>ResultType</code> instance.
	 */
	public ResultType() {
		// nothing to do
	}
	
	/**
	 * Returns a list of new items to add to the container's contents list.
	 */
	public List<LabelItem> getContents() {
		return contents;
	}

	/**
	 * Sets a list of new items to add to the container's contents list.
	 * 
	 * @param contents a list of new items to add 
	 */
	public void setContents(List<LabelItem> contents) {
		this.contents = contents;
	}

	/**
	 * Gets the index within the container's contents list
	 * used as a starting position to remove items.
	 * <br />
	 * This index is also used as a starting position to
	 * insert new items to the container's contents list.
	 */
	public int getFromIndex() {
		return fromIndex;
	}
	
	/**
	 * Sets the index within the container's contents list
	 * used as a starting position to remove items.
	 * 
	 * @param fromIndex the start index
	 */
	public void setFromIndex(int fromIndex) {
		this.fromIndex = fromIndex;
	}

	/**
	 * Gets the index within the container's contents list
	 * used as an ending position for removing items.
	 */
	public int getToIndex() {
		return toIndex;
	}
	
	/**
	 * Sets the index within the container's contents list
	 * used as an ending position for removing items.
	 *  
	 * @param toIndex the to index
	 */
	public void setToIndex(int toIndex) {
		this.toIndex = toIndex;
	}
}