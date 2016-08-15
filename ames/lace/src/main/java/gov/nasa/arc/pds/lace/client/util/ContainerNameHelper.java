package gov.nasa.arc.pds.lace.client.util;

import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements helper methods to find the right name string to
 * show in the UI for a container. The name may contain child
 * element content to help distinguish similar, adjacent
 * containers.
 */
public class ContainerNameHelper {

	/**
	 * Names of elements that should contribute to the display of
	 * the name of their parent container, in priority order from
	 * highest to lowest.
	 */
	private static final String[] DISTINGUISHING_FIELD_NAMES = {
		"name",
		"title",
		"logical_identifier",
		"lid_reference",
		"lidvid_reference",
		"doi",
		"axis_name",
		"local_identifier",
		"local_identifier_reference",
		"file_name",
		"alternate_title",
		"alternate_id",
		"modification_date",
		"discipline_name",
		"value",
	};

	/** Defines the priority of elements for contributing to the display
	 * of the name of its parent container.
	 */
	private static final Map<String, Integer> DISTINGUISHING_FIELD_PRIORITY = new HashMap<String, Integer>();
	static {
		int priority = 0;
		for (String fieldName : DISTINGUISHING_FIELD_NAMES) {
			DISTINGUISHING_FIELD_PRIORITY.put(fieldName, ++priority);
		}
	}

	/**
	 * Gets the element name for the specified container. If the container's
	 * content includes a distinguishing field, the value of that field will be appended
	 * to the element name inside parentheses.
	 *
	 * @param container
	 * @return a name for the container
	 */
	public static String getContainerElementName(Container container) {
		String name = container.getType().getElementName();
		String value = getNameFieldValue(container);
		if (value != null && value.length() > 0) {
			name = name + " (" + value + ")";
		}

		return name;
	}

	/**
	 * Gets the value of a 'name' field if it exists in the specified container's content.
	 *
	 * @param container
	 * @return name field value if it exists, or null
	 */
	private static String getNameFieldValue(Container container) {
		String value = null;
		int priority = Integer.MAX_VALUE;

		for (LabelItem item : container.getContents()) {
			if (item instanceof SimpleItem) {
				SimpleItem simpleItem = (SimpleItem) item;
				Integer newPriority = DISTINGUISHING_FIELD_PRIORITY.get(simpleItem.getType().getElementName());
				if (newPriority!=null && newPriority < priority
						&& simpleItem.getValue()!=null && !simpleItem.getValue().trim().isEmpty()) {
					 value = simpleItem.getValue();
					 priority = newPriority;
				}
			}
		}

		return value;
	}
}
