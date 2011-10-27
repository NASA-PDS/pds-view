// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations 
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
// is subject to U.S. export control laws and regulations, the recipient has 
// the responsibility to obtain export licenses or other export authority as 
// may be required before exporting such information to foreign countries or 
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.registry.ui.shared;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The display container for associations. Necessary to be able to use an object
 * representation of Associations on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.Association
 * 
 * @author hyunlee
 */
public class ViewAssociations extends ArrayList<ViewAssociation> implements
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
