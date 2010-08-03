package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.shared.StatusInformation;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous interface to the not-implemented status service
 * 
 * @author jagander
 */
public interface StatusServiceAsync {
	void getStatus(AsyncCallback<StatusInformation> callback)
			throws IllegalArgumentException;
}
