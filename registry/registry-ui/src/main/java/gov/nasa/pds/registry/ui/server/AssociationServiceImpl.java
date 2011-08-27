package gov.nasa.pds.registry.ui.server;

import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.query.RegistryQuery;
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

}
