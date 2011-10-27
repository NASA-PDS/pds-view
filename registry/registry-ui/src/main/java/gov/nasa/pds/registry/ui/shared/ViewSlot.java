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

import java.io.Serializable;
import java.util.List;

/**
 * The display container for product slots. Necessary to be able to use an
 * object representation of Slot on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.Slot
 * 
 * @author jagander
 */
public class ViewSlot implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> values;

	/**
	 * The name of the product property
	 */
	private String name;

	/**
	 * The identifier of the product property
	 */
	private String id;

	/**
	 * Get the name of the product property
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name of the product property, should only be used when
	 * transforming an actual Slot instance into a ViewSlot or in tests.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the identifier of the product property
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Set the id of the product property, should only be used when transforming
	 * an actual Slot instance into a ViewSlot or in tests.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get the values associated with the product property
	 */
	public List<String> getValues() {
		return this.values;
	}

	/**
	 * Set the values associated with the product property, should only be used
	 * when transforming an actual Slot instance into a ViewSlot or in tests.
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}
}
