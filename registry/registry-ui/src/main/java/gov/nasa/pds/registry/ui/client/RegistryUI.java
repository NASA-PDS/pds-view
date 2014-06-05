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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
//import com.google.gwt.user.client.HistoryListener;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;


import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;

import com.google.gwt.i18n.client.Dictionary;

import gov.nasa.pds.registry.ui.shared.StatusInformation;
import gov.nasa.pds.registry.ui.client.Tab.TabInfo;
import gov.nasa.pds.registry.ui.client.TabList;
import gov.nasa.pds.registry.ui.shared.InputContainer;

//import java.util.Dictionary;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Entry point for the registry front end application. Front end currently
 * supplies browse, filter, and sort capabilities around registered products.
 * 
 * @author hyunlee
 * 
 */
public class RegistryUI implements EntryPoint, ValueChangeHandler<String> {
	
	protected TabList list = new TabList();
	private TabInfo curInfo;
	private Tab curTab;
	
	private HTML description = new HTML();
	
	private VerticalPanel panel = new VerticalPanel();
	
	public static Label lbl = new Label();
	
	public final static String TABLE_HEIGHT = "608px";	    // with page size 20 with CHECKBOX
	public final static String TABLE_HEIGHT_B = "588px";    // with ONE_ROW selection
	
	public final static String TBL_HGT_50 = "1420px";       // 50 records with CHECKBOX
	public final static String TBL_HGT_50_B = "1370px";     // 50 records with ONE_ROW
	
	public final static String TBL_HGT_100 = "2770px";      // 100 records with CHECKBOX
	public final static String TBL_HGT_100_B = "2670px";
		
	/**
	 * Default number of records for each page
	 */
	public static final int PAGE_SIZE1 = 20;
	public static final int PAGE_SIZE2 = 50;
	public static final int PAGE_SIZE3 = 100;
	public static final int FETCH_ROW_SIZE = 100;
	
	public static Logger logger = Logger.getLogger("registry-ui");
	private final ListBox serversList = new ListBox(false);
	
	public static String serverUrl = "http://localhost:8080/registry-pds3/";	

	public void onValueChange(ValueChangeEvent<String> event) {
		String historyToken = event.getValue();

		logger.log(Level.FINE, "*****current token = " + historyToken);
		TabInfo info = list.find(historyToken);
		if (info == null) {
			//logger.log(Level.FINE, "info == null....call showInfo()...");
			showInfo();
			return;
		}
		show(info, false);
	}
	
	public static StatusInfo statusInfo = new StatusInfo();
	
	private void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
    
	public void onModuleLoad() {		
		String[] appServers = null;		
		Dictionary services = Dictionary.getDictionary("services");
		if (services==null) {
			logger.log(Level.FINE, "services... is null");
		}
		else {
			String endpoint = services.get("endpoint");
			logger.log(Level.FINEST, "endpoint  = " + endpoint);			
			appServers = endpoint.split(",");
		}		
		
		serversList.setName("registryServices");		
		for (int i=0; i<appServers.length; i++) {
			String serverUrl = appServers[i];
			serversList.addItem(serverUrl);
			logger.log(Level.FINEST, "server list  = " + serverUrl);
			setServerUrl(appServers[0]);
		}

		HorizontalPanel serverPanel = new HorizontalPanel();
		Label serverLabel = new Label("Registry Service(s):");
		serverLabel.setStyleName("inputLabel");
		serverLabel.setWidth("150px");
		
		serverPanel.add(serverLabel);
		serverPanel.add(serversList);
		
		panel.add(serverPanel);

		// add handler to leverage the refresh button
		serversList.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				logger.log(Level.FINEST, "a server is choosed.." +
						serversList.getValue(serversList.getSelectedIndex()));
				setServerUrl(serversList.getValue(serversList.getSelectedIndex()));
				showInfo();
				History.fireCurrentHistoryState();
			}
		});
			
		loadTabs();
		
		panel.add(list);
		panel.add(description);
		panel.setWidth("100%");
		
		description.setStyleName("ks-Info");
		
		RootPanel.get("productsContainer").add(panel);
			
		// Show the initial screen.
        String initToken = History.getToken();
        logger.log(Level.FINE, "initToken = *" + initToken + "*");   
        
        
        if (initToken.isEmpty())
        	showInfo();
        else {
        	show(list.find(initToken), false);
        }
 
        History.addValueChangeHandler(this);
    }

    public void show(TabInfo info, boolean affectHistory) { 	
        // Don't bother re-displaying the existing tab. This can be an issue
        // in practice, because when the history context is set, our
        // onHistoryChanged() handler will attempt to show the currently-visible tab.
        //if (info == curInfo) {
        //    return;
        //}
        curInfo = info;
    	
        // Remove the old tab from the display area.
        if (curTab != null) {
            curTab.onHide();
            panel.remove(curTab);
            //logger.log(Level.FINE, "curTab is not null....removing curTab..");
        }

        // Get the new tab instance, and display its description in the
        // tab list.
        curTab = info.getInstance();
        list.setTabSelection(info.getName());
        description.setHTML(info.getDescription());

    	//logger.log(Level.FINE, "****info = "  + info.getName() + "    History.getToken() = " + History.getToken());

    	
        // If affectHistory is set, create a new item on the history stack. This
        // will ultimately result in onHistoryChanged() being called. It will call
        // show() again, but nothing will happen because it will request the exact
        // same sink we're already showing.
        if (affectHistory) {
            History.newItem(info.getName());
        }

        // Change the description background color.
        DOM.setStyleAttribute(description.getElement(), "backgroundColor", 
        		info.getColor());
        
        // Display the new tab.
        curTab.setVisible(false);
        panel.add(curTab);
        curTab.setVisible(true);
        curTab.onShow();
    }
    
    /**
     * Adds all tabs to the list. Note that this does not create actual instances
     * of all tabs yet (they are created on-demand). This can make a significant
     * difference in startup time.
     */
    protected void loadTabs() {
        list.addTab(Products.init());
        list.addTab(Associations.init());
        list.addTab(Packages.init());
        list.addTab(Services.init());
        list.addTab(Events.init());
        list.addTab(Schemes.init());
        list.addTab(ClassificationNodes.init());
    }

    private void showInfo() {
        show(list.find("Products"), false);
    }	
}