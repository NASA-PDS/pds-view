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

	/**
	 * 
	 * @return
	 */	
	@Source({"tree-arrow-collapsed.png"})
	ImageResource treeClosed();
	
	/**
	 * 
	 * @return
	 */
	@Source({"tree-arrow-expanded.png"})
	ImageResource treeOpen();
	
	/**
	 * 
	 * @return
	 */
	@Source({"tree-arrow-collapsed.png"})
	ImageResource treeLeaf();
	
	/**
	 * 
	 * @return
	 */	
	@Source({"edit-arrow-collapsed.png"})
	ImageResource editArrowCollapsed();
	
	/**
	 * 
	 * @return
	 */
	@Source({"edit-arrow-expanded.png"})
	ImageResource editArrowExpanded();
	
	/**
	 * 
	 * @return
	 */
	@Source({"required-product-icon.png"})
	ImageResource getRequiredProductIcon();
	
	/**
	 * 
	 * @return
	 */
	@Source({"object-icon.png"})
	ImageResource getObjectIcon();
	
	/**
	 * 
	 * @return
	 */
	@Source({"required-object-icon.png"})
	ImageResource getRequiredObjectIcon();
	
	
	/**
	 * 
	 * @return
	 */
	@Source({"icon-cube-large.png"})
	ImageResource getLargeCubeIcon();
	
	/**
	 * 
	 * @return
	 */
	@Source({"icon-cube-large-alert.png"})
	ImageResource getLargeCubeAlertIcon();
		
	@Source({"btn-plus.png"})
	ImageResource getPlusButton();
	
	@Source({"icon-alert-lg.png"})
	ImageResource getLargeAlertIcon();

	@Source({"icon-alert-sm.png"})
	ImageResource getSmallAlertIcon();

	@Source({"button-close.png"})
	ImageResource getCloseButtonIcon();
	
	@Source({"icon-choice-lg.png"})	
	ImageResource getLargeChoiceIcon();
	
	@Source({"icon-completed-sm.png"})
	ImageResource getSmallCompletedIcon();
	
	/**
	 * 
	 * @return
	 */
	@Source({"icon-cube-dimmed.png"})
	ImageResource getDimmedCubeIcon();
	
	/**
	 * 
	 * @return
	 */
	@Source({"logo-lace.png"})
	ImageResource getLaceLogo();
	
	/**
	 * 
	 * @return
	 */
	@Source({"logo-nasa.png"})
	ImageResource getNasaLogo();
}
