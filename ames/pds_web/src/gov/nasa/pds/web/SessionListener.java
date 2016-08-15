package gov.nasa.pds.web;

import gov.nasa.arc.pds.tools.util.FileUtils;
import gov.nasa.pds.web.ui.managers.DBManager;
import gov.nasa.pds.web.ui.managers.LogManager;
import gov.nasa.pds.web.ui.utils.DateUtils;
import gov.nasa.pds.web.ui.utils.HTTPUtils;
import gov.nasa.pds.web.ui.utils.TabularDataUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Session listener class to associate activities with session events such as
 * session creation and death.
 * 
 * @author jagander
 */
public class SessionListener implements HttpSessionListener {

	/**
	 * Session temp directory, used for storing temporary cache files.
	 */
	private File tempDir;

	/**
	 * Database prefix for all cached sliced table data for a specific session.
	 */
	private String tabDataTablePrefix;

	/**
	 * Start timestamp, used for logging session duration.
	 */
	private long start = 0;

	/**
	 * Session creation event. Currently used for creating a temp directory,
	 * setting up a temp table prefix for slicing and setting start time.
	 * 
	 * @param event
	 *            session created event
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		Date now = new Date();
		this.start = now.getTime();

		final HttpSession session = event.getSession();

		// store the file now since context will be gone when sessionDestroyed()
		// is called
		this.tempDir = HTTPUtils.getSessionTempDir(session);

		// get session id dependent tabular data prefix
		this.tabDataTablePrefix = TabularDataUtils
				.getTabDataTablePrefix(session.getId());
	}

	/**
	 * Session destroyed event. Currently used for deleting the session temp
	 * directory and clearing temp table data.
	 * 
	 * @param event
	 *            session destroyed event
	 */
	@SuppressWarnings("nls")
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {

		// delete the temp directory
		if (this.tempDir != null) {
			FileUtils.forceDeleteAll(this.tempDir);
		} else {
			System.out.println("Temp dir is null for some reason");
		}
		
		HTTPUtils.deleteFilesForSession(event.getSession().getId(),event.getSession().getServletContext());

		try {
			// connect to the database
			Connection connection = DBManager.getConnection();

			Statement queryStmt = connection.createStatement();
			Statement dropStmt = connection.createStatement();

			// clear slicer temp tables
			ResultSet rs = queryStmt.executeQuery("SHOW TABLES LIKE '"
					+ this.tabDataTablePrefix + "%'");
			while (rs.next()) {
				// delete all tables starting with sliceTablePrefix
				dropStmt.execute("DROP TABLE `" + rs.getString(1) + "` ; ");
			}

			// drop all column tables associated with this session
			rs = queryStmt.executeQuery("SHOW TABLES LIKE 'columns';"); //$NON-NLS-1$
			if (rs.next()) {
				dropStmt
						.executeUpdate("DELETE FROM columns WHERE session_id = '"
								+ event.getSession().getId() + "';");
			}

			// clear validation temp tables
			// NOTE: disabled here since sessions expiring too fast in
			// production, see PostValidation reference to
			// DBManager.clearOldValidationData();
			// for (final String errorId : this.tempErrorIds) {
			// dropStmt.executeUpdate("DROP TABLE IF EXISTS `" + errorId +
			// "`;");
			// }
			Date now = new Date();
			long end = now.getTime();
			long duration = end - this.start;
			String durationString = DateUtils
					.getMillisecondsToDuration(duration);
			LogManager.logGeneric("Session destroyed with a duration of "
					+ durationString);
			connection.close();
		} catch (SQLException sqle) {
			throw new RuntimeException("SQL Exception: " + sqle.getMessage()); //$NON-NLS-1$
		}

	}
}
