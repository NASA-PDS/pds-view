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
package gov.nasa.pds.registry.ui.client;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.TreeImages;

public abstract class Tab extends Composite {

	private Tab instance;
	private String name, description;
	
	/**
     * An image provider to make available images to Tabs.
     */
    public interface Images extends ImageBundle, TreeImages {
        AbstractImagePrototype gwtLogo();
    }

    /**
     * Encapsulated information about a tab. Each tab is expected to have a
     * static <code>init()</code> method that will be called by the atlas
     * search on startup.
     */
    public abstract static class TabInfo {
        private Tab instance;

        private String name, description;

        public TabInfo(String name, String desc) {
            this.name = name;
            description = desc;

        }
        
        public abstract Tab createInstance();

        public String getColor() {
            return "#003366";
        }

        public String getDescription() {
            return description;
        }
        
        public final Tab getInstance() {
        	if (instance != null) 
        		return instance;
        	return (instance = createInstance());
        }
        
        public String getName() {
            return name;
        }
    }
    
	/**
	 * Called just before this tab is hidden.
	 */
	public void onHide() {
	}

	/**
	 * Called just after this tab is shown.
	 */
	public void onShow() {
	}
}