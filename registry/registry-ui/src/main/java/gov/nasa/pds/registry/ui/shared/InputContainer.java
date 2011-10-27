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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A display container to simplify and standardize the display of input
 * elements. This will typically be used to create elements in filter or search
 * bars, though it may be useful elsewhere.
 * 
 * Structurally, the container consists of an optional label displayed above an
 * input element widget. There are two pertinent style class names,
 * "inputContainer" and "inputLabel" that will typically be used to control the
 * display of the label and the padding/spacing around/between input elements.
 * 
 * @author jagander
 */
public class InputContainer extends VerticalPanel {

	/**
	 * The primary constructor for the container.
	 * 
	 * @param label
	 *            the optional label for the input. This should be present
	 *            unless the input item use is easily inferred or the item is a
	 *            button.
	 * @param input
	 *            the input widget
	 */
	@SuppressWarnings("nls")
	public InputContainer(String label, Widget input) {

		// add style to the container
		this.addStyleName("inputContainer");

		// put the label in the first row if present
		if (label != null && !label.equals("")) {
			// wrap the label in a span, applying the style classname
			// "inputLabel"
			this
					.add(new HTML("<span class=\"inputLabel\">" + label
							+ "</span>"));
		}

		// put the input item in the second row
		this.add(input);
	}

}
