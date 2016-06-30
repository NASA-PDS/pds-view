package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseViewAction;

/*
 * Javascript in dowloadProgress.jsp interacts with DownloadStatus to update
 * status message to users as tabular file is created from sliceContainer
 * 
 * @author Laura Baalman
 */
public class DownloadProgress extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected String executeInner() throws Exception {
		// TODO: is process object needed?
		@SuppressWarnings("unused")
		final TabularDataProcess process = (TabularDataProcess) getProcess();

		return SUCCESS;
	}

}
