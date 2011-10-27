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

import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.server.connection.ConnectionManager;
import gov.nasa.pds.registry.ui.client.AssociationService;
import gov.nasa.pds.registry.ui.client.SerializableProductResponse;
import gov.nasa.pds.registry.ui.shared.ViewAssociation;
import gov.nasa.pds.registry.ui.shared.ViewAssociations;
import gov.nasa.pds.registry.ui.shared.ViewProducts;
import gov.nasa.pds.registry.ui.shared.ViewProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortInfo;
import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortList;
import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the association service that retrieves registered associations from
 * the registry service.
 * 
 * @author hyunlee
 */
public class AssociationServiceImpl extends RemoteServiceServlet implements
    AssociationService {

  private static final long serialVersionUID = 1L;

  public SerializableResponse<ViewAssociation> getAssociations(final String guid) {
    AssociationFilter filter = new AssociationFilter.Builder().sourceObject(
        guid).targetObject(guid).build();
    RegistryQuery<AssociationFilter> query = new RegistryQuery.Builder<AssociationFilter>()
        .filter(filter).operator(QueryOperator.OR).build();

    List<ViewAssociation> products = ConnectionManager.getAssociations(query,
        null, null);

    // return the GWT appropriate wrapping of the returned results
    return new SerializableProductResponse<ViewAssociation>(products);
  }
  
  /**
	 * Retrieve a set of associations from the registry with the given request
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
	public SerializableResponse<ViewAssociation> requestRows(Request request,
			Map<String, String> filters) {
		// get the sort list from the request
		ColumnSortList sort = request.getColumnSortList();

		// only get the primary sort info as multiple column sort is not part of
		// the UI at this time
		ColumnSortInfo sortInfo = sort.getPrimaryColumnSortInfo();

		// create a builder for assembling sort and filter info into a service
		// specific query
		RegistryQuery.Builder<AssociationFilter> queryBuilder = new RegistryQuery.Builder<AssociationFilter>();

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
			// create a filter builder
			AssociationFilter.Builder filterBuilder = new AssociationFilter.Builder();

			// for each sortable column, if the filter exists, add it

			if (filters.containsKey("targetObject")) {
				filterBuilder.targetObject(filters.get("targetObject"));
			}

			if (filters.containsKey("sourceObject")) {
				filterBuilder.sourceObject(filters.get("sourceObject"));
			}

			if (filters.containsKey("associationType")) {
				filterBuilder.associationType(filters.get("associationType"));
			}

			// build the filter
			AssociationFilter filter = filterBuilder.build();

			// add the filter to the query
			queryBuilder.filter(filter);
		}

		// build the assembled query
		RegistryQuery<AssociationFilter> query = queryBuilder.build();

		// get the results, offsetting the start row by one to deal with index
		// inconsistency
		ViewAssociations associations = ConnectionManager.getAssociations(query, request
				.getStartRow() + 1, request.getNumRows());

		
		for (ViewAssociation vAssoc: associations) {
			// these don't have corresponding extrinsic object, therefore, skip it
			if (vAssoc.getAssociationType().equals("urn:registry:AssociationType:HasMember"))
				continue;
			/*
			// this is too slow.....need to find other way
			ExtrinsicFilter extfilter = new ExtrinsicFilter.Builder().guid(vAssoc.getSourceGuid()).build();
			RegistryQuery<ExtrinsicFilter> prodQuery = new RegistryQuery.Builder<ExtrinsicFilter>().filter(extfilter).build();
			ViewProducts products = ConnectionManager.getProducts(prodQuery, null, null);
			if (products.size()>0) 
				vAssoc.setSourceLid(((ViewProduct)products.get(0)).getLid());
			
			// TODO: need to check the target guid is really guid or lid 
			extfilter = new ExtrinsicFilter.Builder().guid(vAssoc.getTargetGuid()).build();
			prodQuery = new RegistryQuery.Builder<ExtrinsicFilter>().filter(extfilter).build();
			products = ConnectionManager.getProducts(prodQuery, null, null);
			if (products.size()>0)
				vAssoc.setTargetLid(((ViewProduct)products.get(0)).getLid());
			*/	
				
			// get source lid from the product
			ViewProduct aProduct = ConnectionManager.getProduct(vAssoc.getSourceGuid());
			if (aProduct!=null)
				vAssoc.setSourceLid(aProduct.getLid());
			
			// get target lid from the product
		    aProduct = ConnectionManager.getProduct(vAssoc.getTargetGuid());
		    if (aProduct!=null) 
		    	vAssoc.setTargetLid(aProduct.getLid());
		}

		// return the GWT appropriate wrapping of the returned results
		return new SerializableProductResponse<ViewAssociation>(associations);
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
	 * @see gov.nasa.pds.registry.ui.client.RegistryUI#createTableDefinition()
	 */
	@SuppressWarnings("nls")
	public static String indexToColumnName(int index) {
		switch (index) {
		case 0:
			return "sourceObject";
		case 1:
			return "associationType";
		case 2:
			return "targetObject";
		default:
			return null;
		}
	}
}
