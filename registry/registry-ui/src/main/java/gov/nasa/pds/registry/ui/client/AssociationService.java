package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.shared.ViewAssociation;

import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Interface to the products service
 * 
 * @author jagander
 */
@RemoteServiceRelativePath("associations")
public interface AssociationService extends RemoteService {
	SerializableResponse<ViewAssociation> getAssociations(final String guid);
}
