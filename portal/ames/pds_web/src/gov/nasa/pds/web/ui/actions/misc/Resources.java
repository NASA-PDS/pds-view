package gov.nasa.pds.web.ui.actions.misc;

import gov.nasa.pds.web.ui.actions.BaseViewAction;

/**
 * Skeleton action for displaying resources page
 * 
 * @author jagander
 */
public class Resources extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Main method of action
	 * 
	 * @return result string
	 */
	@Override
	protected String executeInner() throws Exception {

		// set the title key
		setTitle("resources.title"); //$NON-NLS-1$

		// no work performed, just return success
		return SUCCESS;
	}

}
