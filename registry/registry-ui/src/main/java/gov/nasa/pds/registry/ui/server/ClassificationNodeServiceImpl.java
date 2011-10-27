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

import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.server.connection.ConnectionManager;
import gov.nasa.pds.registry.ui.client.ClassificationNodeService;
import gov.nasa.pds.registry.ui.client.SerializableProductResponse;
import gov.nasa.pds.registry.ui.shared.ViewClassificationNode;
import gov.nasa.pds.registry.ui.shared.ViewClassificationNodes;
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
 * Implementation of the ClassificationNode service that retrieves registered
 * ClassificationNodes from the registry service.
 * 
 * @author hyunlee
 */
public class ClassificationNodeServiceImpl extends RemoteServiceServlet
		implements ClassificationNodeService {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Retrieve a set of classification nodes from the registry with the given request
	 * parameters.
	 * 
	 * @param request
	 *            number of rows, offset and sort
	 * @param filters
	 *            filter and search params
	 * 
	 * @return a GWT appropriate encapsulation of classification nodes
	 */
	@SuppressWarnings("nls")
	@Override
	public SerializableResponse<ViewClassificationNode> requestRows(Request request,
			Map<String, String> filters) {
		// get the results, offsetting the start row by one to deal with index
		// inconsistency
		ViewClassificationNodes nodes = ConnectionManager.getClassificationNodes(null, 
					request.getStartRow() + 1, request.getNumRows());

		// return the GWT appropriate wrapping of the returned results
		return new SerializableProductResponse<ViewClassificationNode>(nodes);
	}

	public SerializableResponse<ViewClassificationNode> getClassificationNodes(
			final String guid) {
		//ExtrinsicFilter filter = new ExtrinsicFilter.Builder().guid(guid)
		//		.build();
		
		//RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>()
		//		.filter(filter).build();

		ViewClassificationNodes nodes = ConnectionManager.getClassificationNodes(null, 0, 10);

		// return the GWT appropriate wrapping of the returned results
		return new SerializableProductResponse<ViewClassificationNode>(nodes);
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
			return "parent";
		case 1:
			return "code";
		case 2:
			return "path";
		default:
			return null;
		}
	}
}
