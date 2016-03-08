package gov.nasa.arc.pds.lace.shared;

import java.io.Serializable;

/**
 * Represents items within a label template which are either insertion points or elements.
 * The insertion points and elements have underlying types.
 *
 * @author psarram
 */
public abstract class LabelItem implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	/** Creates a new label item instance. */
	public LabelItem() {
		// nothing to do
	}

	/**
	 * Tests whether this item is required.
	 *
	 * @return true, if this item is required, false otherwise
	 */
	public abstract boolean isRequired();

	/**
	 * Returns a copy of this label item. An alternative to clone(),
	 * since that is not supported in GWT.
	 *
	 * @return a copy of this label item
	 */
	public abstract LabelItem copy();

	/**
	 * Copies any member data from this instance to a copy instance.
	 * Used by the {@link #copy()} method.
	 *
	 * @param destination the new copy
	 */
	protected void copyData(LabelItem destination) {
		// nothing to do for this class
	}

}

