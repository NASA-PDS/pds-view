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

import gov.nasa.pds.registry.ui.client.Tab.TabInfo;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;


/**
 * The left panel that contains all of the tabs, along with a short description
 * of each.
 */
public class TabList extends Composite {

    private class MouseLink extends Hyperlink {

        private int index;

        public MouseLink(String name, int index) {
            super(name, name);
            this.index = index;
            sinkEvents(Event.MOUSEEVENTS);
        }

        public void onBrowserEvent(Event event) {
            switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEOVER:
                mouseOver(index);
                break;

            case Event.ONMOUSEOUT:
                mouseOut(index);
                break;
            }

            super.onBrowserEvent(event);
        }
    }

    private HorizontalPanel list = new HorizontalPanel();
    private ArrayList tabs = new ArrayList();

    private int selectedTab = -1;

    public TabList() {
        initWidget(list);
        HorizontalPanel tmp = new HorizontalPanel();
        list.add(tmp);
        setStyleName("ks-List");
    }

    public void addTab(final TabInfo info) {
        String name = info.getName();
        int index = list.getWidgetCount() - 1;

        MouseLink link = new MouseLink(name, index);
        list.add(link);
        tabs.add(info);

        list.setCellVerticalAlignment(link, HorizontalPanel.ALIGN_BOTTOM);
        styleTab(index, false);
    }
    
    public TabInfo find(String tabName) {
        for (int i = 0; i < tabs.size(); ++i) {
            TabInfo info = (TabInfo) tabs.get(i);
            if (info.getName().equals(tabName)) {
                return info;
            }
        }

        return null;
    }
    
    public int getIndex(String tabName) {
    	for (int i=0; i<tabs.size(); ++i) {
    		TabInfo info = (TabInfo) tabs.get(i);
    		if (info.getName().equals(tabName)) {
    			return i;
    		}
    	}
    	return -1;
    }

    public void setTabSelection(String name) {
        if (selectedTab != -1) {
            styleTab(selectedTab, false);
        }

        for (int i = 0; i < tabs.size(); ++i) {
            TabInfo info = (TabInfo) tabs.get(i);
            if (info.getName().equals(name)) {
                selectedTab = i;
                styleTab(selectedTab, true);
                return;
            }
        }
    }

    private void colorTab(int index, boolean on) {
        String color = "";
        if (on) {
            color = ((TabInfo) tabs.get(index)).getColor();
        }

        Widget w = list.getWidget(index + 1);
        DOM.setStyleAttribute(w.getElement(), "backgroundColor", color);
    }

    private void mouseOut(int index) {
        if (index != selectedTab) {
            colorTab(index, false);
        }
    }

    private void mouseOver(int index) {
        if (index != selectedTab) {
            colorTab(index, true);
        }
    }
    
    private void styleTab(int index, boolean selected) {
        String style = (index == 0) ? "ks-FirstSinkItem" : "ks-SinkItem";
        if (selected) {
            style += "-selected";
        }

        Widget w = list.getWidget(index + 1);
        w.setStyleName(style);
        colorTab(index, selected);
    }
}
