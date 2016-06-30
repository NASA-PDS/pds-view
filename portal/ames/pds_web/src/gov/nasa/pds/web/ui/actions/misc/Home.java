package gov.nasa.pds.web.ui.actions.misc;

import gov.nasa.pds.web.ui.actions.BaseViewAction;

/**
 * Skeleton action for displaying home page
 * 
 * @author jagander
 */
public class Home extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Main method of action
	 * 
	 * @return result string
	 */
	@Override
	public String executeInner() throws Exception {

		// set the title key
		setTitle("home.title"); //$NON-NLS-1$

		// no work performed, just return success
		return SUCCESS;
	}
}