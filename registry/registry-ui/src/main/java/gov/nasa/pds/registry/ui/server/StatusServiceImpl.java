package gov.nasa.pds.registry.ui.server;

import gov.nasa.pds.registry.server.connection.ConnectionManager;
import gov.nasa.pds.registry.ui.client.StatusService;
import gov.nasa.pds.registry.ui.shared.StatusInformation;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the status service that retrieves registry status
 * information.
 * 
 * @author jagander
 */
public class StatusServiceImpl extends RemoteServiceServlet implements
		StatusService {

	private static final long serialVersionUID = 1L;

	@Override
	public StatusInformation getStatus() {
		StatusInformation status = ConnectionManager.getStatus();
		return status;
	}

}
