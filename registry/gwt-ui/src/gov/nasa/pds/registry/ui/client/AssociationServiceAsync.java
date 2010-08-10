package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.shared.ViewAssociation;

import com.google.gwt.gen2.table.client.TableModelHelper.SerializableResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous interface to the association service
 * 
 * @author jagander
 */
public interface AssociationServiceAsync {

	void getAssociations(final String lid, final String userVersion,
			AsyncCallback<SerializableResponse<ViewAssociation>> callback);
}
