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
import gov.nasa.pds.registry.model.EventType;
import gov.nasa.pds.registry.query.EventFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.server.connection.ConnectionManager;
import gov.nasa.pds.registry.ui.client.EventsService;
import gov.nasa.pds.registry.ui.client.SerializableProductResponse;
import gov.nasa.pds.registry.ui.shared.ViewAuditableEvent;
import gov.nasa.pds.registry.ui.shared.ViewAuditableEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.ParseException;

import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortInfo;
import com.google.gwt.gen2.table.client.TableModelHelper.ColumnSortList;
import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the event service that retrieves registered events from
 * the registry service.
 * 
 * @author hyunlee
 */
public class EventsServiceImpl extends RemoteServiceServlet implements
		EventsService {

	private static final long serialVersionUID = 1L;

	/**
	 * Retrieve a set of Events from the registry with the given request
	 * parameters.
	 * 
	 * @param request
	 *            number of rows, offset and sort
	 * @param filters
	 *            filter and search params
	 * 
	 * @return a GWT appropriate encapsulation of Events
	 */
	@SuppressWarnings("nls")
	@Override
	public SerializableResponse<ViewAuditableEvent> requestRows(Request request,
			Map<String, String> filters) {

		// get the sort list from the request
		ColumnSortList sort = request.getColumnSortList();

		// only get the primary sort info as multiple column sort is not part of
		// the UI at this time
		ColumnSortInfo sortInfo = sort.getPrimaryColumnSortInfo();

		// create a builder for assembling sort and filter info into a service
		// specific query
		RegistryQuery.Builder<EventFilter> queryBuilder = new RegistryQuery.Builder<EventFilter>();

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
			EventFilter.Builder filterBuilder = new EventFilter.Builder();

			// for each sortable column, if the filter exists, add it
/*
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
			
			if (filters.containsKey("user")) {
				filterBuilder.user(filters.get("user"));
			}
*/			
			// need to convert to enum type
			if (filters.containsKey("eventType")) {
				filterBuilder.eventType(EventType.valueOf(filters.get("eventType")));
			}
			
			java.text.DateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			
			try {
				if (filters.containsKey("eventStart")) {
					filterBuilder.eventStart((Date)formatter.parse(filters.get("eventStart")));
				}

				// need to convert string to Date
				if (filters.containsKey("eventEnd")) {
					filterBuilder.eventEnd((Date)formatter.parse(filters.get("eventEnd")));
				}
			} catch (ParseException e) {
				System.err.println("ParseException: " + e);
			}
			
			if (filters.containsKey("requestId")) {
				filterBuilder.requestId(filters.get("requestId"));
				//System.out.println("requestId = " + filters.get("requestId"));
			}

			// build the filter
			EventFilter filter = filterBuilder.build();

			// add the filter to the query
			queryBuilder.filter(filter);
		}

		// build the assembled query
		RegistryQuery<EventFilter> query = queryBuilder.build();

		// get the results, offsetting the start row by one to deal with index
		// inconsistency
		ViewAuditableEvents events = ConnectionManager.getEvents(query, request
				.getStartRow() + 1, request.getNumRows());

		// return the GWT appropriate wrapping of the returned results
		return new SerializableProductResponse<ViewAuditableEvent>(events);
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
			return "requestId";
		case 1:
			return "timestamp";
		case 2:
			return "eventType";
		case 3:
			return "user";
		case 4:
			return "objectType";
		case 5:
			return "status";
		default:
			return null;
		}
	}
}
