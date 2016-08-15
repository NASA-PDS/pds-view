package gov.nasa.arc.pds.lace.server.actions;

import gov.nasa.arc.pds.lace.server.BaseAction;
import gov.nasa.arc.pds.lace.server.ServerConfiguration;

import javax.inject.Inject;

@SuppressWarnings("serial")
public class HelloAction extends BaseAction {

	private ServerConfiguration serverConfig;

	@Inject
	private void setServerConfiguration(ServerConfiguration config) {
		this.serverConfig = config;
	}

	@Override
	public String executeInner() throws Exception {
		return SUCCESS;
	}

	public String getProjectRoot() {
		return serverConfig.getProjectRoot().getAbsolutePath();
	}

}
