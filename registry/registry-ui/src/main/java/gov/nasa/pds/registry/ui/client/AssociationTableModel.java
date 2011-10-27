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

import gov.nasa.pds.registry.ui.shared.ViewAssociation;
import gov.nasa.pds.registry.ui.shared.ViewAssociations;
import gov.nasa.pds.registry.ui.shared.ViewProduct;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.gen2.table.client.MutableTableModel;
import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Table model for associations. Determines the behaviors for retrieving data to
 * fill the paging scrolling table.
 * 
 * @author hyunlee
 */
public class AssociationTableModel extends MutableTableModel<ViewAssociation> {

	/**
	 * The RPC service used to generate data.
	 */
	private AssociationServiceAsync dataService = null;

	/**
	 * The RPC service used to generate a product data. Required separate from
	 * other service due to difference in endpoint url.
	 */
	private ProductsServiceAsync productService = null;

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
			final Callback<ViewAssociation> callback) {

		// if the data service is null, instantiate it
		if (this.dataService == null) {

			// create a new instance of the class
			this.dataService = GWT.create(AssociationService.class);
		}

		// create a reference to this so it may be accessed in the anonymous
		// AsyncCallback
		final AssociationTableModel instance = this;

		// Send RPC request for data, including previously set filters
		this.dataService.requestRows(request, this.filters,
				new AsyncCallback<SerializableResponse<ViewAssociation>>() {
					@SuppressWarnings("nls")
					public void onFailure(Throwable caught) {
						callback.onFailure(new Exception("Associations RPC Failure"));
						Window.alert("Associations RPC Failure" + caught.getMessage());
						System.out.println("RPC Failure");
					}

					public void onSuccess(
							SerializableResponse<ViewAssociation> result) {
						// get results
						SerializableProductResponse<ViewAssociation> spr = (SerializableProductResponse<ViewAssociation>) result;

						// cast values to ViewAssociations to get extended data
						ViewAssociations associations = (ViewAssociations) spr.getValues();

						// get number of values found in the registry
						instance.setRowCount((int) associations.getSize());

						// do callback
						callback.onRowsReady(request, result);
					}
				});
	}

	public void getProduct(
			final String guid,
			final AsyncCallback<SerializableResponse<ViewProduct>> asyncCallback) {

		// if the data service is null, instantiate it
		if (this.productService == null) {
			// create a new instance of the class
			this.productService = GWT.create(ProductsService.class);
		}

		// Send RPC request for data, including previously set filters
		this.productService.getProduct(guid, asyncCallback);
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
	protected boolean onSetRowValue(int row, ViewAssociation rowValue) {
		return true;
	}
}
