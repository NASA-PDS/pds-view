package gov.nasa.pds.web.ui.actions.stats;

import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.actions.misc.MCAuthenticate;
import gov.nasa.pds.web.ui.containers.ExceptionContainer;
import gov.nasa.pds.web.ui.managers.DBManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * View the details of a single exception logged during a user event failure.
 * 
 * @author jagander
 */
public class ViewException extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	/**
	 * View container for exception details
	 */
	private ExceptionContainer exception;

	/**
	 * Exception id
	 */
	private Integer id;

	/**
	 * Set exception id for display
	 * 
	 * @param id
	 *            id of exception
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get exception for display
	 * 
	 * @return exception container
	 */
	public ExceptionContainer getExceptionObj() {
		return this.exception;
	}

	/**
	 * Main method of action
	 */
	@Override
	protected String executeInner() throws Exception {

		// this is a protected resource, make sure authenticated before
		// proceeding
		if (!MCAuthenticate.authenticated()) {
			addError("error.protectedResource"); //$NON-NLS-1$
			return ILLEGAL_ACCESS;
		}

		// create query string for retrieving exception details
		final String query = "SELECT * FROM exceptions WHERE exception_id = " + this.id; //$NON-NLS-1$

		// get connection to db
		final Connection connection = DBManager.getConnection();

		// create query statement
		Statement queryStmt = connection.createStatement();

		// get results of query
		ResultSet rs = queryStmt.executeQuery(query);

		// TODO: make sure an error was found, should never happen considering
		// how links are generated but there are concurrency issues possible

		// get first (only) element in result set
		rs.next();

		// get details for display
		final String url = rs.getString("url"); //$NON-NLS-1$
		final String date = rs.getTimestamp("date").toString(); //$NON-NLS-1$
		final String message = rs.getString("message"); //$NON-NLS-1$
		final String stack = rs.getString("stack"); //$NON-NLS-1$

		// wrap details in excpetion container
		this.exception = new ExceptionContainer(this.id, date, url, message,
				stack);

		// close connection
		connection.close();

		return SUCCESS;
	}

	@Override
	protected void pushBackUserInput() {
		// TODO Auto-generated method stub

	}

	/**
	 * Make sure the id was present in the request.
	 */
	@Override
	protected void validateUserInput() {
		if (this.id == null) {
			addError("error.requiredstring", "id"); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}
}