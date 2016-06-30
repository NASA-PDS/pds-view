package gov.nasa.pds.web.ui.actions.misc;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.ui.actions.BaseViewAction;
import gov.nasa.pds.web.ui.managers.DBManager;

import java.sql.Connection;

/**
 * Test action used by hosting provider to verify that the application is
 * running as expected. Do NOT change the output without first conferring with
 * hosting staff. Additional checks should be added such that they only print
 * output when something fails.
 */
@SuppressWarnings("nls")
public class Test extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Test message output, starts at "" and only grows when failures are
	 * discovered
	 */
	private String status = "";

	/**
	 * Get status message
	 * 
	 * @return status message
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Add a status message. Calling this beyond the default "OK" will cause the
	 * hosting facility test to fail and report a problem.
	 */
	private void addStatusMessage(final String message) {
		this.status += message + "<br />";
	}

	/**
	 * Main method of the action
	 */
	// TODO: test a wider variety of issues, but make sure they do no tax the
	// system as this will be called frequently
	@Override
	protected String executeInner() throws Exception {

		// test getting a message from properties file
		final String testResourceString = getUIManager().getTxt(
				"test.text.testValue", 10);
		if (!testResourceString.equals("I am 10 ninjas")) {
			addStatusMessage("Retrieval of message resource failed. The value returned was \""
					+ testResourceString + "\".");

		}

		// test connecting to database
		Connection connection = DBManager.getConnection();
		if (!DBManager.isActive(connection)) {
			addStatusMessage("Unable to connect to database.");
		}

		// TODO: test opening a file handle (update: may no longer be a
		// necessary test)

		// if no errors, report OK
		if (StrUtils.nullOrEmpty(this.status)) {
			addStatusMessage("OK");
		}

		return SUCCESS;
	}

}
