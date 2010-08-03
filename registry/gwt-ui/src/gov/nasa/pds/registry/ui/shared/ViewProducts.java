package gov.nasa.pds.registry.ui.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The display container for products. Necessary to be able to use an object
 * representation of Products on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.Products
 * 
 * @author jagander
 */
public class ViewProducts extends ArrayList<ViewProduct> implements
		IsSerializable {

	private static final long serialVersionUID = 1L;

	/**
	 * start index of the record set, the first element index is 1
	 */
	private int start = 1;

	/**
	 * number of records found, not the size of the list
	 */
	private long size = 0;

	/**
	 * Set the number of records found, should only be used when transforming an
	 * actual Products instance into a ViewProducts or in tests.
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * Get the number of records found
	 */
	public long getSize() {
		return this.size;
	}

	/**
	 * Get the start index of the record set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * Set the start index of the record set, should only be used when
	 * transforming an actual Products instance into a ViewProducts or in tests.
	 */
	public int getStart() {
		return this.start;
	}

}
