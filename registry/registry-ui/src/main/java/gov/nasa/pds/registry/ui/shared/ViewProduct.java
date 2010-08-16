package gov.nasa.pds.registry.ui.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The display container for a product. Necessary to be able to use an object
 * representation of Product on the client side of GWT (with a minimum of
 * overhead).
 * 
 * @see gov.nasa.pds.registry.model.Product
 * 
 * @author jagander
 */
public class ViewProduct extends ViewRegistryObject implements IsSerializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Source url
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getHome()
	 */
	private String home;

	/**
	 * Globally unique identifier
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getGuid()
	 */
	private String guid;

	/**
	 * Arbitrary name value pairs of properties associated with the product
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getSlots()
	 */
	private List<ViewSlot> slots;

	/**
	 * Get source url for product
	 */
	public String getHome() {
		return this.home;
	}

	/**
	 * Set the home attribute, should only be used when transforming an actual
	 * Product instance into a ViewProduct or in tests.
	 */
	public void setHome(final String home) {
		this.home = home;
	}

	/**
	 * Get the globally unique identifier
	 */
	public String getGuid() {
		return this.guid;
	}

	/**
	 * Set the globally unique identifier, should only be used when transforming
	 * an actual Product instance into a ViewProduct or in tests.
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Get the arbitrary property name value pairs
	 */
	public List<ViewSlot> getSlots() {
		return this.slots;
	}

	/**
	 * Set the arbitrary property name value pairs, should only be used when
	 * transforming an actual Product instance into a ViewProduct or in tests.
	 */
	public void setSlots(final List<ViewSlot> slots) {
		this.slots = slots;
	}

}
