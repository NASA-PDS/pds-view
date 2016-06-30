package gov.nasa.pds.web.ui.actions.stats;

import gov.nasa.pds.web.ui.actions.BaseViewAction;
import gov.nasa.pds.web.ui.actions.misc.MCAuthenticate;
import gov.nasa.pds.web.ui.containers.ExceptionContainer;
import gov.nasa.pds.web.ui.managers.DBManager;
import gov.nasa.pds.web.ui.utils.Comparators;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * View a list of exceptions that occurred for users during the normal operation
 * of one of the tools hosted on the site. This is a developer resource to see
 * what problems crop up on the production version of the site.
 * 
 * @author jagander
 */
public class ViewExceptions extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	/**
	 * List of exceptions, wrapped in objects for display
	 */
	private List<ExceptionContainer> exceptions = new ArrayList<ExceptionContainer>();

	/**
	 * Get the list of exceptions stored in the db
	 * 
	 * @return the list of exceptions
	 */
	public List<ExceptionContainer> getExceptions() {
		return this.exceptions;
	}

	/**
	 * Main method of the action
	 */
	@Override
	protected String executeInner() throws Exception {

		// protected resource, force log-in if not already
		if (!MCAuthenticate.authenticated()) {
			addError("error.protectedResource"); //$NON-NLS-1$
			return ILLEGAL_ACCESS;
		}

		// query string to get exceptions, no filtering
		final String query = "SELECT * FROM exceptions"; //$NON-NLS-1$

		// get db connection
		final Connection connection = DBManager.getConnection();

		// create query statement
		Statement queryStmt = connection.createStatement();

		// perform query and get result set
		ResultSet rs = queryStmt.executeQuery(query);

		// iterate over results and convert into display exceptions
		while (rs.next()) {
			final int id = rs.getInt("exception_id"); //$NON-NLS-1$
			final String url = rs.getString("url"); //$NON-NLS-1$
			final String date = rs.getTimestamp("date").toString(); //$NON-NLS-1$
			final String message = rs.getString("message"); //$NON-NLS-1$
			// final String stack = rs.getString("stack");

			final ExceptionContainer e = new ExceptionContainer(id, date, url,
					message);
			this.exceptions.add(e);
		}

		// sort the exceptions
		Collections.sort(this.exceptions,
				Comparators.EXCEPTION_CONTAINER_COMPARATOR);

		// close the db connection
		connection.close();

		return SUCCESS;
	}
}