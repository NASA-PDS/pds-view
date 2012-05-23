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
import gov.nasa.pds.registry.ui.shared.ViewClassificationNodes;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.gen2.table.client.MutableTableModel;
import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Table model for ClassificationNodes. Determines the behaviors for retrieving data to
 * fill the paging scrolling table.
 * 
 * @author hyunlee
 */
public class ClassificationNodeTableModel extends MutableTableModel<ViewClassificationNode> {

	/**
	 * The RPC service used to generate data.
	 */
	private ClassificationNodeServiceAsync dataService = null;

	/**
	 * A store of filter params as they are not supported in the request object
	 */
	private Map<String, String> filters = new HashMap<String, String>();

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
			final Callback<ViewClassificationNode> callback) {

		// if the data service is null, instantiate it
		if (this.dataService == null) {

			// create a new instance of the class
			this.dataService = GWT.create(ClassificationNodeService.class);
		}

		// create a reference to this so it may be accessed in the anonymous
		// AsyncCallback
		final ClassificationNodeTableModel instance = this;

		// Send RPC request for data, including previously set filters
		this.dataService.requestRows(request, this.filters,
				new AsyncCallback<SerializableResponse<ViewClassificationNode>>() {
					@SuppressWarnings("nls")
					public void onFailure(Throwable caught) {
						callback.onFailure(new Exception("ClassificationNodes RPC Failure"));
						Window.alert("ClassificationNodes RPC Failure" + caught.getMessage());
						System.out.println("RPC Failure");
					}

					public void onSuccess(
							SerializableResponse<ViewClassificationNode> result) {
						// get results
						SerializableProductResponse<ViewClassificationNode> spr = (SerializableProductResponse<ViewClassificationNode>) result;

						// cast values to ViewClassificationNodes to get extended data
						ViewClassificationNodes nodes = (ViewClassificationNodes) spr.getValues();

						// get number of values found in the registry
						instance.setRowCount((int)nodes.getSize());

						// do callback
						callback.onRowsReady(request, result);
					}
				});
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
	protected boolean onSetRowValue(int row, ViewClassificationNode rowValue) {
		return true;
	}
}
