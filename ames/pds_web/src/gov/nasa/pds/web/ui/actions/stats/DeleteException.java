package gov.nasa.pds.web.ui.actions.stats;

import gov.nasa.pds.web.ui.actions.BaseSubmitAction;
import gov.nasa.pds.web.ui.actions.misc.MCAuthenticate;
import gov.nasa.pds.web.ui.managers.DBManager;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Delete a logged exception as no longer pertinent
 * 
 * @author jagander
 */
public class DeleteException extends BaseSubmitAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Id of exception to delete
	 */
	private Integer id;

	/**
	 * Set id to delete
	 * 
	 * @param id
	 *            id to delete
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Main method of the action
	 */
	@Override
	protected String executeInner() throws Exception {

		// this is a protected resource, if not authenticated, kick them out
		if (!MCAuthenticate.authenticated()) {
			addError("error.protectedResource"); //$NON-NLS-1$
			return ILLEGAL_ACCESS;
		}

		// create query string to delete exception
		final String query = "DELETE FROM exceptions WHERE exception_id = " + this.id; //$NON-NLS-1$

		// get db connection
		final Connection connection = DBManager.getConnection();

		// create the query to delete
		Statement queryStmt = connection.createStatement();

		// execute the query
		queryStmt.execute(query);

		// close the connection
		connection.close();

		return SUCCESS;
	}

	@Override
	protected void pushBackUserInput() {
		// TODO Auto-generated method stub

	}

	/**
	 * Validate user submitted input
	 */
	@Override
	protected void validateUserInput() {

		// make sure an id was submitted for deletion
		if (this.id == null) {
			addError("error.requiredstring", "id"); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}
}