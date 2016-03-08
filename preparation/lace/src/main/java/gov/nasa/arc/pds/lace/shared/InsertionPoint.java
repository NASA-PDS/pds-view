package gov.nasa.arc.pds.lace.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a model object to represent a location where
 * new content can be inserted.
 */
public class InsertionPoint extends LabelItem {

	private static final long serialVersionUID = 1L;

	private List<LabelItemType> alternatives = new ArrayList<LabelItemType>();
	private String displayType;
	private int insertFirst;
	private int insertLast;
	private int usedBefore;
	private int usedAfter;

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
		PLUS_BUTTON("plus_button");

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
	 * Creates an instance of <code>InsertionPoint</code>.
	 */
	public InsertionPoint() {
		// nothing to do
	}

	/**
	 * Gets a list of <code>LabelItemType</code> that indicates the types that can be inserted.
	 * Note that whether the type is required is indicated within LabelItemType. Length is N.
	 *
	 * @return the list of label item types
	 */
	public List<LabelItemType> getAlternatives() {
		return alternatives;
	}

	/**
	 * Sets a list of <code>LabelItemType</code> that indicates the types that can be inserted.
	 *
	 * @param alternatives the list of label item types
	 */
	public void setAlternatives(List<LabelItemType> alternatives) {
		this.alternatives = alternatives;
	}

	/**
	 * Gets an index within alternatives of first type that can be inserted (0..N-1).
	 *
	 * @return the index of first type within alternatives that can be inserted
	 */
	public int getInsertFirst() {
		return insertFirst;
	}

	/**
	 * Sets an index within alternatives of first type that can be inserted (0..N-1).
	 *
	 * @param insertFirst the index of first type within alternatives that can be inserted
	 */
	public void setInsertFirst(int insertFirst) {
		this.insertFirst = insertFirst;
	}

	/**
	 * Gets an index within alternatives of last type that can be inserted (0..N-1).
	 *
	 * @return the insertLast the index of last type within alternatives that can be inserted
	 */
	public int getInsertLast() {
		return insertLast;
	}

	/**
	 * Sets an index within alternatives of last type that can be inserted (0..N-1).
	 *
	 * @param insertLast the index of last type within alternatives that can be inserted
	 */
	public void setInsertLast(int insertLast) {
		this.insertLast = insertLast;
	}

	/**
	 * Gets an index within alternatives of first type that has been inserted before,
	 * or Ð1 (as a sentinel value) if no items have been inserted before this insertion point.
	 *
	 * @return the usedBefore
	 */
	public int getUsedBefore() {
		return usedBefore;
	}

	/**
	 * Sets an index within alternatives of first type that has been inserted before,
	 * or Ð1 (as a sentinel value) if no items have been inserted before this insertion point.
	 *
	 * @param usedBefore the usedBefore to set
	 */
	public void setUsedBefore(int usedBefore) {
		this.usedBefore = usedBefore;
	}

	/**
	 * Gets an index within alternatives of last type that has been inserted after,
	 * or N (as a sentinel value) if no items have been inserted after.
	 *
	 * @return the usedAfter
	 */
	public int getUsedAfter() {
		return usedAfter;
	}

	/**
	 * Sets an index within alternatives of last type that has been inserted after,
	 * or N (as a sentinel value) if no items have been inserted after.
	 *
	 * @param usedAfter the usedAfter to set
	 */
	public void setUsedAfter(int usedAfter) {
		this.usedAfter = usedAfter;
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
		for (int i=insertFirst; i <= insertLast; ++i) {
			if (0 <= i && i < alternatives.size()) {
				LabelItemType type = alternatives.get(i);
				if (type.getMinOccurrences() > 0) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (LabelItemType type : getAlternatives()) {
			if (builder.length() > 0) {
				builder.append(',');
			}
			builder.append(type.getElementName());
		}
		return "[InsertionPoint: " + builder.toString()
			+ " - type=" + displayType
			+ " insert=" + insertFirst + ".." + insertLast
			+ " used=" + usedBefore + ".." + usedAfter
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
			copy.insertFirst = insertFirst;
			copy.insertLast = insertLast;
			copy.usedBefore = usedBefore;
			copy.usedAfter = usedAfter;
			copy.alternatives = new ArrayList<LabelItemType>();
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

}
