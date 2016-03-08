package gov.nasa.arc.pds.lace.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * HTML constants used by the client to insert HTML into the UI.
 */
public interface HtmlResources extends ClientBundle {

	/** An instance of the HTML resources. */
	HtmlResources INSTANCE = GWT.create(HtmlResources.class);

	/**
	 * Gets the HTML for the page footer.
	 * 
	 * @return the footer HTML
	 */
	@Source({"footer.html"})
	TextResource getFooterHtml();
}
