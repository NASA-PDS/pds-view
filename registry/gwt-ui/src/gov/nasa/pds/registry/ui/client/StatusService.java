package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.shared.StatusInformation;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Interface to the not-implemented status service
 * 
 * @author jagander
 */
@RemoteServiceRelativePath("status")
public interface StatusService extends RemoteService {
	public StatusInformation getStatus();
}
