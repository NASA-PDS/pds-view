package gov.nasa.pds.web.ui.actions.misc;

import gov.nasa.pds.web.ui.actions.BaseViewAction;

/**
 * Log in page for protected resources.
 * 
 * @author jagander
 */
public class MCLogIn extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Main method of the action
	 */
	@Override
	protected String executeInner() throws Exception {

		// if already logged in, forward to management section (currently stats)
		if (MCAuthenticate.authenticated()) {
			return "authenticated"; //$NON-NLS-1$
		}

		return SUCCESS;
	}

}
