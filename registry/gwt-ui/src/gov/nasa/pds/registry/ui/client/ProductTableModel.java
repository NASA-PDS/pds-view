package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.shared.ViewProduct;
import gov.nasa.pds.registry.ui.shared.ViewProducts;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.gen2.table.client.MutableTableModel;
import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Table model for products. Determines the behaviors for retrieving data to
 * fill the paging scrolling table.
 * 
 * @author jagander
 */
public class ProductTableModel extends MutableTableModel<ViewProduct> {

	/**
	 * The RPC service used to generate data.
	 */
	private ProductsServiceAsync dataService = null;

	/**
	 * A store of filter params as they are not supported in the request object
	 */
	private Map<String, String> filters = new HashMap<String, String>();

	// TODO: needed?
	/*
	 * // we keep a map so we can index by id private Map<String, ViewProduct>
	 * map;
	 * 
	 * 
	 * public void setData(ArrayList<ViewProduct> list) { // toss the list,
	 * index by id in a map. this.map = new HashMap<String,
	 * ViewProduct>(list.size()); for (ViewProduct m : list) {
	 * this.map.put(m.getGuid(), m); } }
	 */

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
			final Callback<ViewProduct> callback) {

		// if the data service is null, instantiate it
		if (this.dataService == null) {

			// create a new instance of the class
			this.dataService = GWT.create(ProductsService.class);

			// cast as a target
			ServiceDefTarget endpoint = (ServiceDefTarget) this.dataService;

			// set the REST base url
			String moduleRelativeURL = GWT.getModuleBaseURL() + "products"; //$NON-NLS-1$

			// set the REST base url on the endpoint
			endpoint.setServiceEntryPoint(moduleRelativeURL);
		}

		// create a reference to this so it may be accessed in the anonymous
		// AsyncCallback
		final ProductTableModel instance = this;

		// Send RPC request for data, including previously set filters
		this.dataService.requestRows(request, this.filters,
				new AsyncCallback<SerializableResponse<ViewProduct>>() {
					@SuppressWarnings("nls")
					public void onFailure(Throwable caught) {
						callback.onFailure(new Exception("RPC Failure"));
						System.out.println("RPC Failure");
					}

					public void onSuccess(
							SerializableResponse<ViewProduct> result) {
						// get results
						SerializableProductResponse<ViewProduct> spr = (SerializableProductResponse<ViewProduct>) result;

						// cast values to ViewProducts to get extended data
						ViewProducts products = (ViewProducts) spr.getValues();

						// get number of values found in the registry
						instance.setRowCount((int) products.getSize());

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
	protected boolean onSetRowValue(int row, ViewProduct rowValue) {
		return true;
	}

}
