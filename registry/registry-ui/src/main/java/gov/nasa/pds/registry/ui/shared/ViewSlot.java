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
