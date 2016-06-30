package gov.nasa.pds.web.ui.actions.tabularManagement;

import gov.nasa.pds.web.ui.actions.BaseViewAction;

public class Help extends BaseViewAction {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected String executeInner() throws Exception {
		setTitle("help.TableExplorer.title"); //$NON-NLS-1$
        return SUCCESS;
	}
	

}
