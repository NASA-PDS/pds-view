package gov.nasa.pds.registry.ui.server;

import gov.nasa.pds.registry.query.AssociationQuery;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.query.AssociationQuery.Builder;
import gov.nasa.pds.registry.server.connection.ConnectionManager;
import gov.nasa.pds.registry.ui.client.AssociationService;
import gov.nasa.pds.registry.ui.client.SerializableProductResponse;
import gov.nasa.pds.registry.ui.shared.ViewAssociation;

import java.util.List;

import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AssociationServiceImpl extends RemoteServiceServlet implements
		AssociationService {

	private static final long serialVersionUID = 1L;

	public SerializableResponse<ViewAssociation> getAssociations(
			final String guid) {

	  Builder queryBuilder = new AssociationQuery.Builder();
	  queryBuilder.operator(QueryOperator.OR);
	  AssociationFilter.Builder filterBuilder = new AssociationFilter.Builder();
	  filterBuilder.sourceObject(guid);
	  filterBuilder.targetObject(guid);
	  // build the filter
    AssociationFilter filter = filterBuilder.build();
    // add the filter to the query
    queryBuilder.filter(filter);
    // Look for targetObject
    // Look for sourceObject
		// get the results
		List<ViewAssociation> products = ConnectionManager.getAssociations(queryBuilder.build(), null, null);

		// return the GWT appropriate wrapping of the returned results
		return new SerializableProductResponse<ViewAssociation>(products);
	}

}
