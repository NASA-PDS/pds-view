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
public class ViewProduct implements IsSerializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Version provided by user
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getUserVersion()
	 */
	private String userVersion;

	/**
	 * Version provided by registry
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getVersion()
	 */
	private String version;

	/**
	 * Submitted, Approved, Deprecated, etc.
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getStatus()
	 */
	private String status;

	/**
	 * Type of registry object which should map to policy about the slots
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getObjectType()
	 */
	private String objectType;

	/**
	 * Logical identifier supplied by submitter
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getLid()
	 */
	private String lid;

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
	 * Display name
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getName()
	 */
	private String name;

	/**
	 * Brief description of the entry
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getDescription()
	 */
	private String description;

	/**
	 * Arbitrary name value pairs of properties associated with the product
	 * 
	 * @see gov.nasa.pds.registry.model.RegistryObject#getSlots()
	 */
	private List<ViewSlot> slots;

	/**
	 * Get the product name
	 * 
	 * @return the product name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the product name, should only be used when transforming an actual
	 * Product instance into a ViewProduct or in tests.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the user defined version
	 * 
	 * @return the user defined version
	 */
	public String getUserVersion() {
		return this.userVersion;
	}

	/**
	 * Set the user defined version, should only be used when transforming an
	 * actual Product instance into a ViewProduct or in tests.
	 */
	public void setUserVersion(final String userVersion) {
		this.userVersion = userVersion;
	}

	/**
	 * Get the version provided by the registry
	 * 
	 * @return the version provided by the registry
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Set the version, should only be used when transforming an actual Product
	 * instance into a ViewProduct or in tests.
	 */
	public void setVersion(final String version) {
		this.version = version;
	}

	/**
	 * Get the product status
	 * 
	 * @return product status
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Set the status, should only be used when transforming an actual Product
	 * instance into a ViewProduct or in tests.
	 */
	public void setStatus(final String status) {
		this.status = status;
	}

	/**
	 * Get object type
	 * 
	 * @return object type
	 */
	public String getObjectType() {
		return this.objectType;
	}

	/**
	 * Set the object type, should only be used when transforming an actual
	 * Product instance into a ViewProduct or in tests.
	 */
	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	/**
	 * Get logical identifier
	 * 
	 * @return logical identifier
	 */
	public String getLid() {
		return this.lid;
	}

	/**
	 * Set the logical identifier, should only be used when transforming an
	 * actual Product instance into a ViewProduct or in tests.
	 */
	public void setLid(final String lid) {
		this.lid = lid;
	}

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

	/**
	 * Get the product description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Set the product description, should only be used when transforming an
	 * actual Product instance into a ViewProduct or in tests.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

}
