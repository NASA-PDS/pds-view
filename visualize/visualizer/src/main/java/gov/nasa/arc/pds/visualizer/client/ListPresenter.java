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
import gov.nasa.arc.pds.visualizer.client.event.ErrorReportEvent;
import gov.nasa.arc.pds.visualizer.client.presenter.Presenter;
import gov.nasa.pds.domain.Array2DImageProduct;
import gov.nasa.pds.domain.DocumentProduct;
import gov.nasa.pds.domain.FieldInfo;
import gov.nasa.pds.domain.HasFields;
import gov.nasa.pds.domain.HasParsableChildren;
import gov.nasa.pds.domain.PDSObject;
import gov.nasa.pds.domain.TableCharacterProduct;
import gov.nasa.pds.domain.TokenizedLabel;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * http://code.google.com/webtoolkit/articles/testing_methodologies_using_gwt.html
 */
public class ListPresenter implements Presenter {  

	public interface Display {
		int getClickedRow(ClickEvent event);
		HasClickHandlers getList();
		void updateChildlistView(PDSObject[] data, int totalRows);
	    void updateImageView(PDSObject[] items);
	    void updateDocumentView(PDSObject[] items);
	    void updateTableView(List<FieldInfo> fieldInfos, int totalRows, TokenizedLabel tokenizedLabel);
		void updatePending();
	}

	private DataServiceAsync rpcService;
	private EventBus eventBus;
	private Display listView;

	public ListPresenter(DataServiceAsync rpcService, EventBus eventBus, ListView listView) {
		super();
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.listView = listView;
	}

	/** 
	 * Updates a view of the child contents.
	 */
	void fetchAndUpdateChildlist(PDSObject dataItem)  {

		AsyncCallback<PDSObject[]> callbackData = new AsyncCallback<PDSObject[]>() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				String s = "ListPresenter.fetchItems.onFailure: " + ((caught.getCause() != null) ? caught.getCause() : "") + " "+ caught.getMessage();
				eventBus.fireEvent(new ErrorReportEvent(s));
			}

			public void onSuccess(PDSObject[] result) {
				System.err.println("List Presenter RPC get children success " );

				try {		
					listView.updateChildlistView(result, result.length); 
				} catch (Exception e) {
					System.err.println("List Presenter RPC onSuccess contents failed: "+e.getMessage());
					e.printStackTrace();
				}				
			}
		};

		rpcService.parseCollection(dataItem.getArchiveRoot(), dataItem.getThis_file_name(), callbackData);

	}

	/** 
	 * Updates a view of image child contents.
	 */
	void fetchAndUpdateImageChildlist(PDSObject dataItem)  {

		AsyncCallback<PDSObject[]> callbackData = new AsyncCallback<PDSObject[]>() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				String s = "ListPresenter.fetchAndUpdateImageChildlist.onFailure: " + ((caught.getCause() != null) ? caught.getCause() : "") + " "+ caught.getMessage();
				eventBus.fireEvent(new ErrorReportEvent(s));
			}

			public void onSuccess(PDSObject[] result) {
				System.err.println("fetchAndUpdateImageChildlist success: ");
				try {				
					listView.updateImageView(result); 
				} catch (Exception e) {
					System.err.println("List Presenter RPC fetchAndUpdateImageChildlist onSuccess contents failed: "+e.getMessage());
					e.printStackTrace();
				}
				
			}
		};
        // fetch images for Product Array.xml
		rpcService.parseImageProduct(dataItem.getArchiveRoot(), dataItem.getThis_file_name(), callbackData);
		listView.updatePending();

	}
	
	/** 
	 * Updates a view of image child contents.
	 */
	void fetchAndUpdateTableChildlist(final PDSObject dataItem)  {

		AsyncCallback<PDSObject[]> callbackData = new AsyncCallback<PDSObject[]>() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				String s = "fetchAndUpdateTableChildlist: " + ((caught.getCause() != null) ? caught.getCause() : "") + " "+ caught.getMessage();
				eventBus.fireEvent(new ErrorReportEvent(s));
			}

			public void onSuccess(PDSObject[] result) {
				System.err.println("fetchAndUpdateTableChildlist success: ");
				try {	
				
					PDSObject firstResult = result[0]; 	// TODO is result.size() always == 1 ?					
					List<FieldInfo> fields = ((HasFields)firstResult).getFields();
					assert fields.size() > 0: "no table fields returned from RPC";
					int totalRows = fields.get(0).getValues().size();
					listView.updateTableView(fields, totalRows, firstResult.getTokenizedLabel());
				} catch (Exception e) {
					System.err.println("List Presenter RPC fetchAndUpdateImageChildlist onSuccess contents failed: "+e.getMessage());
					e.printStackTrace();
				}
				
			}
		};
        // fetch tables for Product Table .xml
		rpcService.parseTableCharacterProduct(dataItem.getArchiveRoot(), dataItem.getThis_file_name(), callbackData);
		listView.updatePending();

	}
	
	/** 
	 * Updates a view of document child contents.
	 */
	void fetchAndUpdateDocumentChildlist(PDSObject dataItem)  {

		AsyncCallback<PDSObject[]> callbackData = new AsyncCallback<PDSObject[]>() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				String s = "ListPresenter.fetchAndUpdateDocumentChildlist.onFailure: " + ((caught.getCause() != null) ? caught.getCause() : "") + " "+ caught.getMessage();
				eventBus.fireEvent(new ErrorReportEvent(s));
			}

			public void onSuccess(PDSObject[] result) {
				try {				
					listView.updateDocumentView(result); 
				} catch (Exception e) {
					System.err.println("List Presenter RPC fetchAndUpdateDocumentChildlist onSuccess contents failed: "+e.getMessage());
					e.printStackTrace();
				}
				
			}
		};
        // fetch images for Doc.xml
		rpcService.parseDocumentProduct(dataItem.getArchiveRoot(), dataItem.getThis_file_name(), callbackData);

	}
	
	@Override
	public void go(PDSObject dataItem) {
		if (dataItem instanceof HasParsableChildren) {
			fetchAndUpdateChildlist(dataItem);
		} else {
			if (dataItem instanceof Array2DImageProduct) {
				fetchAndUpdateImageChildlist(dataItem);
			} else {
				if (dataItem instanceof DocumentProduct) {
					fetchAndUpdateDocumentChildlist(dataItem);
				} else {
					if (dataItem instanceof TableCharacterProduct) {
						fetchAndUpdateTableChildlist(dataItem);
					} else {
						System.err.println("List Presenter dispatch failed. Unknown type.");
					}
				}
			}
		}
	}

	public void bind() {
		listView.getList().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int selectedRow = listView.getClickedRow(event); //TODO this returns void

				if (selectedRow >= 0) {
					eventBus.fireEvent(new DisplayDetailsEvent());
				}
			}
		});
	}
}
