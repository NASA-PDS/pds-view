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

//import gov.nasa.pds.registry.query.ExtrinsicFilter;
//import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.server.connection.ConnectionManager;
import gov.nasa.pds.registry.ui.client.ServiceBindingService;
import gov.nasa.pds.registry.ui.client.SerializableProductResponse;
import gov.nasa.pds.registry.ui.shared.ViewServiceBinding;
import gov.nasa.pds.registry.ui.shared.ViewService;
import gov.nasa.pds.registry.ui.shared.ViewServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortInfo;
import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortList;
import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the service that retrieves registered services from
 * the registry service.
 * 
 * @author hyunlee
 */
public class ServiceBindingServiceImpl extends RemoteServiceServlet implements
		ServiceBindingService {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Retrieve a set of services from the registry with the given request
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
	public SerializableResponse<ViewService> requestRows(String serverUrl, Request request) {
		// get the results, offsetting the start row by one to deal with index
		// inconsistency
		ViewServices services = ConnectionManager.getServices(serverUrl, request.getStartRow() + 1, request.getNumRows());

		// return the GWT appropriate wrapping of the returned results
		return new SerializableProductResponse<ViewService>(services);
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
			return "home";
		case 3:
			return "versionName";
		case 4:
			return "objectType";
		case 5:
			return "status";
		default:
			return null;
		}
	}
}
