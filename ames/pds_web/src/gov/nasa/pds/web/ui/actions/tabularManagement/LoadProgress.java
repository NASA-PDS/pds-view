package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseViewAction;

/*
 * Javascript in loadProgress.jsp interacts with LoadStatus.java to update
 * status message to user as Label and tabular data files are read
 * 
 * @author Laura Baalman
 */
public class LoadProgress extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected String executeInner() throws Exception {
		@SuppressWarnings("unused")
		final TabularDataProcess process = (TabularDataProcess) getProcess();

		return SUCCESS;
	}

}
