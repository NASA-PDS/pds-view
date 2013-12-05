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
import gov.nasa.pds.registry.server.connection.ConnectionManager;
import gov.nasa.pds.registry.ui.client.PackageService;
import gov.nasa.pds.registry.ui.client.SerializableProductResponse;
import gov.nasa.pds.registry.ui.shared.ViewRegistryPackages;
import gov.nasa.pds.registry.ui.shared.ViewRegistryPackage;
import gov.nasa.pds.registry.query.PackageFilter;
import gov.nasa.pds.registry.query.RegistryQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortInfo;
import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortList;
import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Implementation of the package service that retrieves registered packages from
 * the registry service.
 * 
 * @author hyunlee
 */
public class PackageServiceImpl extends RemoteServiceServlet implements
		PackageService {

	private static final long serialVersionUID = 1L;

	/**
	 * Retrieve a set of package from the registry with the given request
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
	public SerializableResponse<ViewRegistryPackage> requestRows(Request request, Map<String, String> filters) {

		// get the sort list from the request
		ColumnSortList sort = request.getColumnSortList();

		// only get the primary sort info as multiple column sort is not part of
		// the UI at this time
		ColumnSortInfo sortInfo = sort.getPrimaryColumnSortInfo();

		// create a builder for assembling sort and filter info into a service
		// specific query
		RegistryQuery.Builder<PackageFilter> queryBuilder = new RegistryQuery.Builder<PackageFilter>();

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
			PackageFilter.Builder filterBuilder = new PackageFilter.Builder();

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
			
			if (filters.containsKey("status")) {
				// TODO: make sure is a valid status
				filterBuilder.status(ObjectStatus.valueOf(filters.get("status")));
			}
			
			// build the filter
			PackageFilter filter = filterBuilder.build();

			// add the filter to the query
			queryBuilder.filter(filter);
		}

		// build the assembled query
		RegistryQuery<PackageFilter> query = queryBuilder.build();

		// get the results, offsetting the start row by one to deal with index
		// inconsistency

		ViewRegistryPackages packages = ConnectionManager.getPackages(query, 
				request.getStartRow() + 1, request.getNumRows());
		
		// return the GWT appropriate wrapping of the returned results
		return new SerializableProductResponse<ViewRegistryPackage>(packages);
	}

	public boolean updatePackage(final ViewRegistryPackage registryPackage) {
		return ConnectionManager.updatePackage(registryPackage);
	}
	
	public boolean deletePackage(ViewRegistryPackage registryPackage) {
		return ConnectionManager.deletePackage(registryPackage);
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
		case 5:
			return "description";
		default:
			return null;
		}
	}
	
	//private HttpSession getSession() {
	//	return this.getThreadLocalRequest().getSession();
	//}
	
	public String getRemoteUser() {
		//logger.log(Level.FINEST, "header = " + this.getRequests().getHeaders().toString());
		//System.out.println("header = " + this.getRequest().getHeaders().toString());
		for (Cookie aCookie: this.getRequest().getCookies()) {
			System.out.println("cookie name = " + aCookie.getName() + " :  value = " + aCookie.getValue() );
		}
		return this.getRequest().getRemoteUser();
	}
	
	public String getRemotePassword() {
		return this.getRequest().getRemoteUser();
		//return this.getRequest().getRemotePassword();
	}
	
	public String getSessionId() {
		//return this.getRequest().getCookies().toString();
		return this.getRequest().getRequestedSessionId();
	}
	
	private HttpServletRequest getRequest() {
		return this.getThreadLocalRequest();
	}
}
