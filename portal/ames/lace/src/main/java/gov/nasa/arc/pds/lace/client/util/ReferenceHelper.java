package gov.nasa.arc.pds.lace.client.util;

import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implements helper methods for identifying which elements define
 * local identifiers, and which elements refer to those local
 * identifiers.
 */
public class ReferenceHelper {

	private static final String PDS4_1x_NAMESPACE = "http://pds.nasa.gov/pds4/pds/v1";
	private static final String PDS4_NAMESPACE_PREFIX = "http://pds.nasa.gov/pds4/";

	/**
	 * Tests whether a model item is a reference to another element value.
	 * It is a reference if either it is an XML IDREF type, or if it is
	 * a PDS4 reference element.
	 */
	public static boolean isReference(Container parent, SimpleItem item) {
		return isPDS4Reference(parent, item) || item.getType().isIDREF();
	}

	private static boolean isPDS4Reference(Container parent, SimpleItem item) {
		LabelItemType parentType = (parent==null ? null : parent.getType());
		LabelItemType type = item.getType();

		if (type.getElementNamespace() != null
				&& type.getElementNamespace().startsWith(PDS4_NAMESPACE_PREFIX)
				&& type.getElementName().equals("local_identifier_reference")) {
			return true;
		}

		if (parentType != null
				&& parentType.getElementNamespace().equals(PDS4_1x_NAMESPACE)
				&& parentType.getElementName().equals("DD_Association")
				&& type.getElementNamespace() != null
				&& type.getElementNamespace().equals(PDS4_1x_NAMESPACE)
				&& type.getElementName().equals("local_identifier")) {
			return true;
		}

		return false;
	}

	/**
	 * Gets the set of values which a reference may take on. If the reference
	 * element is an XML IDREF type, it may take on any XML ID value. If it
	 * is a PDS4 reference, it may take on any PDS4 local_identifier value.
	 */
	public static Collection<String> getReferenceValues(SimpleItem item, Container root) {
		List<String> values = new ArrayList<String>();

		LabelItemType type = item.getType();
		if (type.getElementNamespace() != null
				&& type.getElementNamespace().startsWith(PDS4_NAMESPACE_PREFIX) ) {
			findPDS4ReferenceValues(root, values);
		} else {
			findXMLReferenceValues(root, values);
		}

		return values;
	}

	private static void findPDS4ReferenceValues(Container container, List<String> values) {
		for (LabelItem item : container.getContents()) {
			if (item instanceof Container) {
				findPDS4ReferenceValues((Container) item, values);
			} else if (item instanceof SimpleItem) {
				SimpleItem simpleItem = (SimpleItem) item;
				if (isPDS4Identifier(container, simpleItem)) {
					if (simpleItem.getValue()!=null && !simpleItem.getValue().trim().isEmpty()) {
						values.add(simpleItem.getValue().trim());
					}
				}
			}
		}
	}

	private static boolean isPDS4Identifier(Container parent, SimpleItem item) {
		LabelItemType parentType = parent.getType();
		LabelItemType type = item.getType();

		if (parentType.getElementNamespace().equals(PDS4_1x_NAMESPACE)
				&& parentType.getElementName().equals("DD_Association")
				&& type.getElementNamespace() != null
				&& type.getElementNamespace().equals(PDS4_1x_NAMESPACE)
				&& type.getElementName().equals("local_identifier")) {
			return false;
		}

		return (type.getElementNamespace() != null
				&& type.getElementNamespace().equals(PDS4_1x_NAMESPACE)
				&& type.getElementName().equals("local_identifier"));
	}

	private static void findXMLReferenceValues(Container container, List<String> values) {
		for (LabelItem item : container.getContents()) {
			if (item instanceof Container) {
				findXMLReferenceValues((Container) item, values);
			} else if (item instanceof SimpleItem) {
				SimpleItem simpleItem = (SimpleItem) item;
				if (simpleItem.getType().isIDREF()) {
					if (simpleItem.getValue()!=null && !simpleItem.getValue().trim().isEmpty()) {
						values.add(simpleItem.getValue().trim());
					}
				}
			}
		}
	}

}
