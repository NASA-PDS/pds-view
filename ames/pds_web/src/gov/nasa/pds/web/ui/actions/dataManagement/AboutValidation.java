package gov.nasa.pds.web.ui.actions.dataManagement;

import gov.nasa.pds.web.ui.actions.BaseViewAction;

/**
 * Skeleton action for displaying about page
 * 
 * @author jagander
 */
public class AboutValidation extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Main method of action
	 * 
	 * @return result string
	 */
	@Override
	protected String executeInner() throws Exception {

		// set title of page
		setTitle("about.title"); //$NON-NLS-1$

		// no work performed, just return success
		return SUCCESS;
	}
}
