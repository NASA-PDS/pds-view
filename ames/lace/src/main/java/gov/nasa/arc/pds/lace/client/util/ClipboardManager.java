package gov.nasa.arc.pds.lace.client.util;

import gov.nasa.arc.pds.lace.shared.LabelItem;

import javax.inject.Singleton;


/**
 * Implements a service that manages the clipboard.
 */
@Singleton
public class ClipboardManager {

	private LabelItem clipboardItem;

	/**
	 * Sets the clipboard item. A deep copy of the item is saved
	 * to the clipboard so that the caller can modify their copy.
	 *
	 * @param item the item to save to the clipboard
	 */
	public void saveItem(LabelItem item) {
		clipboardItem = item.copy();
	}

	/**
	 * Gets the clipboard item. A copy of the clipboard item is returned, so
	 * that the caller can manipulate the returned object without affecting
	 * the clipboard item.
	 *
	 * @return the clipboard item, or null if no clipboard item
	 */
	public LabelItem getItem() {
		return clipboardItem.copy();
	}

	/**
	 * Tests whether there is an item in the clipboard that can be pasted.
	 *
	 * @return true, if the clipboard has an item
	 */
	public boolean hasItem() {
		return clipboardItem != null;
	}

}
