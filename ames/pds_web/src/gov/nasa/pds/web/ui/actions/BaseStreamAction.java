package gov.nasa.pds.web.ui.actions;

import java.io.InputStream;

/**
 * This class is a wrapper class for Actions of type Stream
 * 
 * @author lbaalman
 * @version 1.0
 */

public abstract class BaseStreamAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Input stream for streaming output
	 */
	protected InputStream inputStream;

	/**
	 * Get inputstream for retrieving output
	 */
	public InputStream getInputStream() throws Exception {
		return this.inputStream;
	}

	/**
	 * Do action execution
	 */
	@Override
	public String execute() {
		try {
			// execute inner action
			String resultVal = executeInner();

			// return result of inner action
			return resultVal;
		} catch (Exception e) {
			setException(e);
		}

		// if making it here, there was an uncaught error in the inner action,
		// send to error page
		return ERROR;
	}

	/**
	 * Inner action main method
	 */
	protected abstract String executeInner();

}
