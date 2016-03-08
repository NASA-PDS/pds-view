package gov.nasa.arc.pds.lace.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Implements a set of resources that should be available 
 * as constants to the application.
 */
public interface Resources extends ClientBundle {
	
	/** The singleton instance of the resources. */	
	Resources INSTANCE = GWT.create(Resources.class);

	@Source({"tree-arrow-collapsed.png"})
	ImageResource treeClosed();
	
	@Source({"tree-arrow-expanded.png"})
	ImageResource treeOpen();
	
	@Source({"tree-arrow-collapsed.png"})
	ImageResource treeLeaf();
	
	@Source({"edit-arrow-collapsed.png"})
	ImageResource editArrowCollapsed();
	
	@Source({"edit-arrow-expanded.png"})
	ImageResource editArrowExpanded();
	
	@Source({"btn-plus.png"})
	ImageResource getPlusButton();

	@Source({"icon-alert-sm.png"})
	ImageResource getSmallAlertIcon();

	@Source({"button-close.png"})
	ImageResource getCloseButtonIcon();
	
	@Source({"logo-lace.png"})
	ImageResource getLaceLogo();
	
	@Source({"logo-nasa.png"})
	ImageResource getNasaLogo();
}
