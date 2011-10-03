/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gov.nasa.arc.pds.visualizer.client;

import gov.nasa.arc.pds.visualizer.client.event.DisplayDetailsEvent;
import gov.nasa.arc.pds.visualizer.client.event.DisplayDetailsEventHandler;
import gov.nasa.arc.pds.visualizer.client.event.ErrorReportEvent;
import gov.nasa.arc.pds.visualizer.client.event.ErrorReportEventHandler;
import gov.nasa.arc.pds.visualizer.client.event.PresentDataEvent;
import gov.nasa.arc.pds.visualizer.client.event.PresentDataEventHandler;
import gov.nasa.pds.domain.PDSObject;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * This application demonstrates how to construct a relatively complex user
 * interface, similar to many common email readers. It has no back-end,
 * populating its components with hard-coded data.
 */
public class Visualizer implements EntryPoint {

	interface GlobalResources extends ClientBundle {
		@NotStrict
		@Source("global.css")
		CssResource css();
	}
	
    static final String ClEAR_TEXT = "";
	interface Binder extends UiBinder<DockLayoutPanel, Visualizer> { }
	private static final Binder binder = GWT.create(Binder.class);  
	private static final SimpleEventBus eventBus = new SimpleEventBus();
	private static final DataServiceAsync dataSvc = GWT.create(DataService.class);

	@UiField (provided=true) ListView listView = new ListView();
	private ListPresenter listPresenter = new ListPresenter(dataSvc, eventBus, listView);

	@UiField TopPanel topPanel;
	@UiField ErrorReportUI errorReportUI;
	@UiField (provided=true) ProductView productView = new ProductView(dataSvc, eventBus);

	/**
	 * This method constructs the application user interface by instantiating
	 * controls and hooking up event handler.
	 */
	public void onModuleLoad() {
		//http://java.ociweb.com/mark/programming/GWT.html
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable e) {
				doReportError("UncaughtException: "+ e.getCause() +" "+ e.getMessage());
			}
		});


		// Inject global styles.
		GWT.<GlobalResources>create(GlobalResources.class).css().ensureInjected();

		// Create the UI defined in Visualizer.ui.xml.
		DockLayoutPanel outer = binder.createAndBindUi(this);

		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");

		// Special-case stuff to make topPanel overhang a bit.
		Element topElem = outer.getWidgetContainerElement(topPanel);
		topElem.getStyle().setZIndex(2);
		topElem.getStyle().setOverflow(Overflow.VISIBLE);

		// Bind to the DOM root
		RootLayoutPanel root = RootLayoutPanel.get();
		root.add(outer);
		bind();
	}

	public static EventBus getEventBus() {
		return eventBus;
	}

	private void bind() {
		eventBus.addHandler(ErrorReportEvent.TYPE,
				new ErrorReportEventHandler() {
			@Override
			public void onEvent(ErrorReportEvent event) {					
				doReportError(event.getMessage());
			}	         
		});  	

		eventBus.addHandler(PresentDataEvent.TYPE,
				new PresentDataEventHandler() {
			@Override
			public void onEvent(PresentDataEvent event) {	
				doPresentData(event.getDataItem());
			}	         
		});  
		
		eventBus.addHandler(DisplayDetailsEvent.TYPE,
				new DisplayDetailsEventHandler() {
			@Override
			public void onEvent(DisplayDetailsEvent event) {
				Window.alert("Visualizer hears displayDetailsEvent");						
			}				         
		});
	}

	protected void doReportError(String msg) {
		System.err.println("doReportError(): "+msg);
		this.errorReportUI.setText(msg);
	}

	protected void doPresentData(PDSObject dataItem) {
		this.errorReportUI.setText(ClEAR_TEXT);
		listPresenter.go(dataItem);
	}

}
