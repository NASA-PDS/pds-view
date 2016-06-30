package gov.nasa.arc.pds.lace.shared;

import java.util.Collections;
import java.util.List;

/**
 * Implements an object that holds typing information for wildcard model objects.
 */
public class WildcardType extends LabelItemType {
	
	private static final long serialVersionUID = 1L;

	// A list of allowed namespaces
	private List<String> namespaces;
	
	/**
	 * Creates a new <code>WildcardType</code> instance.
	 */
	public WildcardType() {
		super.setWildcard(true);
	}

	/**
	 * Gets a list of allowed namespaces for the type. The returned
	 * list is not modifiable.
	 * 
	 * @return a list of namespaces
	 */
	public List<String> getNamespaces() {
		return Collections.unmodifiableList(namespaces);
	}
	
	/**
	 * Sets the list of allowed namespaces for this type.
	 * 
	 * @param namespaces a list of namespaces
	 */
	public void setNamespaces(List<String> namespaces) {
		this.namespaces = namespaces;
	}
}
