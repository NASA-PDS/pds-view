package gov.nasa.pds.web.ui.actions.misc;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import javax.servlet.http.HttpSession;

/**
 * Trivial password protection on management council resources. This is not
 * meant to be particularly secure, just prevent casual access to certain
 * resources such as user research and analytics.
 * 
 * @author jagander
 */
public class MCAuthenticate extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Session key for flag that management council log in is valid
	 */
	public final static String MC_AUTHENTICATE = "mc_authenticate"; //$NON-NLS-1$

	/**
	 * Session key for the resource the user requested when they were redirected
	 * here
	 */
	public final static String MC_REQUESTED = "mc_requested"; //$NON-NLS-1$

	/**
	 * Username submitted
	 */
	private String username;

	/**
	 * Password submitted
	 */
	private String password;

	/**
	 * Url requested
	 */
	private String requestedURL;

	/**
	 * Get url to send user to once authenticated
	 * 
	 * @return url to redirect user to once validated
	 */
	public String getRedirect() {
		return this.requestedURL;
	}

	/**
	 * Set the user submitted username
	 * 
	 * @param username
	 *            username
	 */
	public void setUsername(final String username) {
		this.username = username;
	}

	/**
	 * Set the user submitted password
	 * 
	 * @param password
	 *            password
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * Main method of the action
	 */
	@Override
	protected String executeInner() throws Exception {

		// check that the user and pass are correct, just hard coded here as
		// this is trivial protection. If resources become things that need
		// actual protection, tear this out and use provide more secure access
		if (!this.username.equals("pdsmc") || !this.password.equals("council")) { //$NON-NLS-1$ //$NON-NLS-2$

			// add an error message that user or pass is wrong
			addError("mcauthenticate.badUserOrPass"); //$NON-NLS-1$

			// return the error page
			return ERROR;
		}

		// user and pass correct, set session as authenticated
		HTTPUtils.getSession().setAttribute(MC_AUTHENTICATE, true);

		// retrieve requested url, used in forward in struts action def
		this.requestedURL = (String) HTTPUtils.getSession().getAttribute(
				MC_REQUESTED);

		return SUCCESS;
	}

	@Override
	protected void pushBackUserInput() {
		// TODO Auto-generated method stub

	}

	/**
	 * Make sure the user input is valid, only a cursory check to see if can
	 * proceed further
	 */
	@Override
	protected void validateUserInput() {

		// check that something was submitted for username
		if (StrUtils.nullOrEmpty(this.username)) {
			addError("error.requiredstring", "username"); //$NON-NLS-1$//$NON-NLS-2$
		}

		// check that something submitted for password
		if (StrUtils.nullOrEmpty(this.password)) {
			addError("error.requiredstring", "password"); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	/**
	 * Test for the user being authenticated
	 * 
	 * @return authentication state
	 */
	public static boolean authenticated() {

		// get session
		HttpSession session = HTTPUtils.getSession();

		// see if authenticated flag appears in session, if it does, they're
		// authenticated
		final boolean authenticated = session.getAttribute(MC_AUTHENTICATE) != null;

		// if not authenticated, store requested resource for later redirect
		if (!authenticated && session.getAttribute(MC_REQUESTED) == null) {
			session.setAttribute(MC_REQUESTED, HTTPUtils.getFullURL());
		}

		return authenticated;
	}

}
