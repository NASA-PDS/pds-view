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
package gov.nasa.pds.registry.ui.server;

import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.server.connection.ConnectionManager;
import gov.nasa.pds.registry.ui.client.ProductsService;
import gov.nasa.pds.registry.ui.client.SerializableProductResponse;
import gov.nasa.pds.registry.ui.shared.ViewProduct;
import gov.nasa.pds.registry.ui.shared.ViewProducts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortInfo;
import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortList;
import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the product service that retrieves registered products from
 * the registry service.
 * 
 * @author jagander
 */
public class ProductsServiceImpl extends RemoteServiceServlet implements
		ProductsService {

	private static final long serialVersionUID = 1L;

	/**
	 * Get a default set of products. Not particularly useful as it will only
	 * retrieve the default number of products with a default filter. Primarily
	 * used for verifying connectivity.
	 */
	/*
	public ViewProducts getProducts() {
		ViewProducts products = ConnectionManager.getProducts();
		return products;
	}
    */
	
	public SerializableResponse<ViewProduct> getProduct(String serverUrl, final String guid) {
		ExtrinsicFilter filter = new ExtrinsicFilter.Builder().guid(guid)
				.build();
		RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>()
				.filter(filter).build();

		ViewProducts products = ConnectionManager.getProducts(serverUrl, query, null, null);

		// return the GWT appropriate wrapping of the returned results
		return new SerializableProductResponse<ViewProduct>(products);
	}

	public boolean updateProduct(String serverUrl, final ViewProduct product) {
		return ConnectionManager.updateProduct(serverUrl, product);
	}
	
	public boolean deleteProduct(String serverUrl, ViewProduct product) {
	    return ConnectionManager.deleteProduct(serverUrl, product);
	}
	
	/**
	 * Retrieve a set of products from the registry with the given request
	 * parameters.
	 * 
	 * @param request
	 *            number of rows, offset and sort
	 * @param filters
	 *            filter and search params
	 * 
	 * @return a GWT appropriate encapsulation of products
	 */
	@SuppressWarnings("nls")
	@Override
	public SerializableResponse<ViewProduct> requestRows(String serverUrl, Request request,
			Map<String, String> filters) {
		
		System.out.println("ProductServiceImpl....serverUrl = " + serverUrl);

		// get the sort list from the request
		ColumnSortList sort = request.getColumnSortList();

		// only get the primary sort info as multiple column sort is not part of
		// the UI at this time
		ColumnSortInfo sortInfo = sort.getPrimaryColumnSortInfo();

		// create a builder for assembling sort and filter info into a service
		// specific query
		RegistryQuery.Builder<ExtrinsicFilter> queryBuilder = new RegistryQuery.Builder<ExtrinsicFilter>();

		// if there is sort info, assemble transform into query format
		if (sortInfo != null) {

			// get the column name from the index
			String columnName = indexToColumnName(sortInfo.getColumn());

			// get an ascending string from boolean property
			String asc = sortInfo.isAscending() ? "ASC" : "DESC";

			// assemble sort string
			String sortString = columnName + " " + asc;

			// create a sort list and add the sort
			List<String> sortList = new ArrayList<String>();
			sortList.add(sortString);

			// add the sort to the query
			queryBuilder = queryBuilder.sort(sortList);
		}

		
		// if there are filters, add them
		if (filters.size() > 0) {
			System.out.println("filters = " + filters.toString());
			// create a filter builder
			ExtrinsicFilter.Builder filterBuilder = new ExtrinsicFilter.Builder();

			// for each sortable column, if the filter exists, add it

			if (filters.containsKey("lid")) {
				filterBuilder.lid(filters.get("lid"));
			}

			if (filters.containsKey("guid")) {
				filterBuilder.guid(filters.get("guid"));
			}

			if (filters.containsKey("name")) {
				filterBuilder.name(filters.get("name"));
			}

			if (filters.containsKey("objectType")) {
				filterBuilder.objectType(filters.get("objectType"));
			}

			if (filters.containsKey("status")) {
				// TODO: make sure is a valid status
				filterBuilder.status(ObjectStatus
						.valueOf(filters.get("status")));
			}
			
			if (filters.containsKey("versionName")) {
				filterBuilder.versionName(filters.get("versionName"));
			}
			
			//if (filters.containsKey("home")) {
			//	filterBuilder.home(filters.get("home"));
			//}

			// build the filter
			ExtrinsicFilter filter = filterBuilder.build();

			// add the filter to the query
			queryBuilder.filter(filter);
		}

		// build the assembled query
		RegistryQuery<ExtrinsicFilter> query = queryBuilder.build();

		// get the results, offsetting the start row by one to deal with index
		// inconsistency
		ViewProducts products = ConnectionManager.getProducts(serverUrl, query, request
				.getStartRow() + 1, request.getNumRows());

		// return the GWT appropriate wrapping of the returned results
		return new SerializableProductResponse<ViewProduct>(products);
	}

	/**
	 * Convert index to column name
	 * 
	 * NOTE: This is a hack to deal with the fact that the request object only
	 * knows the column index of any sort.
	 * 
	 * TODO: The mapping between the index and the id should be consolidated
	 * with Registry_ui which defines the columns.
	 * 
	 * @see gov.nasa.pds.registry.ui.client.Registry_ui#createTableDefinition()
	 */
	@SuppressWarnings("nls")
	public static String indexToColumnName(int index) {
		switch (index) {
		case 0:
			return "name";
		case 1:
			return "lid";
		case 2:
			return "versionName";
		case 3:
			return "objectType";
		case 4:
			return "status";
		default:
			return null;
		}
	}
}
