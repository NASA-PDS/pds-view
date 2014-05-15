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

import gov.nasa.pds.registry.ui.shared.ViewClassificationNode;
//import gov.nasa.pds.registry.ui.shared.ViewClassificationNodes;
import gov.nasa.pds.registry.ui.shared.ViewScheme;
import gov.nasa.pds.registry.ui.shared.ViewSchemes;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.gen2.table.client.MutableTableModel;
import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Table model for Schemes. Determines the behaviors for retrieving data to
 * fill the paging scrolling table.
 * 
 * @author hyunlee
 */
public class SchemeTableModel extends MutableTableModel<ViewScheme> {

	/**
	 * The RPC Scheme used to generate data.
	 */
	private SchemeServiceAsync dataService = null;
	
	/**
	 * The RPC service used to generate classification node data. Required separate from
	 * other service due to difference in endpoint url.
	 */
	private ClassificationNodeServiceAsync nodeService = null;

	/**
	 * A store of filter params as they are not supported in the request object
	 */
	private Map<String, String> filters = new HashMap<String, String>();
	
	private String serverUrl = "";
	
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	/**
	 * Add a filter condition
	 * 
	 * @param key
	 *            filter name
	 * @param value
	 *            filter value
	 */
	public void addFilter(final String key, final String value) {
		this.filters.put(key, value);
	}

	/**
	 * Clear filters prior to new request
	 */
	public void clearFilters() {
		this.filters.clear();
	}

	/**
	 * Get rows from table as necessary. Call only supports the request, filters
	 * are added separately, prior to calling the row request.
	 * 
	 * @param request
	 *            request containing paging and sort info
	 * @param callback
	 *            callback function to handle the response
	 */
	@Override
	public void requestRows(final Request request,
			final Callback<ViewScheme> callback) {

		// if the data service is null, instantiate it
		if (this.dataService == null) {
			// create a new instance of the class
			this.dataService = GWT.create(SchemeService.class);
		}

		// create a reference to this so it may be accessed in the anonymous
		// AsyncCallback
		final SchemeTableModel instance = this;

		// Send RPC request for data
		this.dataService.requestRows(this.serverUrl, request, 
				new AsyncCallback<SerializableResponse<ViewScheme>>() {
					@SuppressWarnings("nls")
					public void onFailure(Throwable caught) {
						callback.onFailure(new Exception("Schemes RPC Failure"));
						Window.alert("Schemes RPC Failure" + caught.getMessage());
						System.out.println("RPC Failure");
					}

					public void onSuccess(
							SerializableResponse<ViewScheme> result) {
						// get results
						SerializableProductResponse<ViewScheme> spr = (SerializableProductResponse<ViewScheme>) result;

						// cast values to ViewSchemes to get extended data
						ViewSchemes schemes = (ViewSchemes) spr.getValues();

						// get number of values found in the registry
						instance.setRowCount((int) schemes.getSize());

						// do callback
						callback.onRowsReady(request, result);
					}
				});
	}
	
	public void getClassificationNodes(
			final String guid,
			final AsyncCallback<SerializableResponse<ViewClassificationNode>> asyncCallback) {

		// if the data service is null, instantiate it
		if (this.nodeService == null) {
			// create a new instance of the class
			this.nodeService = GWT.create(ClassificationNodeService.class);
		}

		// Send RPC request for data, including previously set filters
		this.nodeService.getClassificationNodes(this.serverUrl, guid, asyncCallback);
		/*
		this.nodeService.getClassificationNodes(guid) {
				//new AsyncCallback<SerializableResponse<ViewClassificationNode>>() {
				@SuppressWarnings("nls")
				public void onFailure(Throwable caught) {
					System.out.println("RPC Failure");
					callback.onFailure(new Exception("Schemes.getClassificationNodes() RPC Failure"));
					Window.alert("Schemes.getClassificationNodes() RPC Failure" + caught.getMessage());
				}

				public void onSuccess(
					SerializableResponse<ViewClassificationNode> result) {
					System.out.println("success called on getting classification nodes");
					// get results
					SerializableProductResponse<ViewClassificationNode> spr = 
							(SerializableProductResponse<ViewClassificationNode>) result;

					// cast values to ViewProducts to get
					// extended data
					ViewClassificationNodes nodes = (ViewClassificationNodes) spr.getValues();
					return nodes;
				}
			});
			*/
	}
	
	// Default behaviors for common table access, add functionality as necessary
	@Override
	protected boolean onRowInserted(int beforeRow) {
		return true;
	}

	@Override
	protected boolean onRowRemoved(int row) {
		return true;
	}

	@Override
	protected boolean onSetRowValue(int row, ViewScheme rowValue) {
		return true;
	}
}
