package gov.nasa.pds.web;

import gov.nasa.arc.pds.tools.util.FileUtils;
import gov.nasa.pds.web.ui.actions.dataManagement.PostValidationBucket;
import gov.nasa.pds.web.ui.constants.TabularManagementConstants;
import gov.nasa.pds.web.ui.managers.DBManager;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.mysql.jdbc.ConnectionImpl;

/**
 * Servlet listener class to associate activities with servlet events such as
 * startup and destruction.
 * 
 * @author jagander
 */
public class ServletListener implements ServletContextListener {

	/**
	 * Temp directory that session based or context based files will be stored.
	 */
	private File tempDir;

	/**
	 * Set of activities to perform on context being destroyed. Currently this
	 * includes cleaning out temp files and cleaning up database connections.
	 * 
	 * @param context
	 *            context destroyed event
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {

		// delete temp files
		if (this.tempDir != null) {
			this.tempDir.delete();
		}
		// suggestions for dealing with leaks per article at
		// http://ampedandwired.com/2008/05/09/causes-of-java-permgen-memory-leaks/

		// flushing cache
		Introspector.flushCaches();

		// MySQL Connector/J Statement Cancellation Timer
		try {
			if (ConnectionImpl.class.getClassLoader() == getClass()
					.getClassLoader()) {
				Field f = ConnectionImpl.class.getDeclaredField("cancelTimer"); //$NON-NLS-1$
				f.setAccessible(true);
				Timer timer = (Timer) f.get(null);
				timer.cancel();
			}
		} catch (Exception e) {
			System.out
					.println("Exception cleaning up MySQL cancellation timer: " //$NON-NLS-1$
							+ e.getMessage());
		}

		// JDBC Driver Manager registration issues
		try {
			for (Enumeration<Driver> e = DriverManager.getDrivers(); e
					.hasMoreElements();) {
				Driver driver = e.nextElement();
				if (driver.getClass().getClassLoader() == getClass()
						.getClassLoader()) {
					DriverManager.deregisterDriver(driver);
				}
			}
		} catch (Throwable e) {
			System.out.println("Unable to clean up JDBC driver: " //$NON-NLS-1$
					+ e.getMessage());
		}
	}

	/**
	 * Set of activities to perform on context initialization. Currently
	 * includes cleaning out the temp dir (redundant in case of bad shut down)
	 * and cleaning out temp db data.
	 * 
	 * @param event
	 *            initialization event
	 */
	@SuppressWarnings("nls")
	@Override
	public void contextInitialized(ServletContextEvent event) {
		cleanTempDir(event);

		// drop any tables with a name starting with "tabdata"
		// delete all rows from column table
		Connection connection = null;

		try {
		    connection = DBManager.getConnection();

			Statement queryStmt = connection.createStatement();
			Statement dropStmt = connection.createStatement();

			ResultSet rs = queryStmt.executeQuery("SHOW TABLES LIKE '"
					+ TabularManagementConstants.TABULARDATA_TABLE_PREFIX
					+ "%'");

			while (rs.next()) {
				// delete all tables starting with sliceTablePrefix
				dropStmt.execute("DROP TABLE `" + rs.getString(1) + "` ; ");
			}

			@SuppressWarnings("unused")
			boolean result = queryStmt.execute("DELETE FROM `columns`;"); //$NON-NLS-1$

			// delete any validation tables
			rs = queryStmt.executeQuery("SHOW TABLES LIKE '"
					+ PostValidationBucket.TEMP_ERROR_PREFIX + "%'");

			while (rs.next()) {
				// delete all tables starting with TEMP_ERROR_PREFIX
				dropStmt.execute("DROP TABLE `" + rs.getString(1) + "`");
			}
			connection.close();
		} catch (SQLException sqle) {
			throw new RuntimeException("SQL Exception: " + sqle.getMessage()); //$NON-NLS-1$
		} catch (NullPointerException e) {
			throw new RuntimeException(" NullPointerException: cannot connect to database", e); //$NON-NLS-1$
		} finally {
			try {
				if (connection != null) connection.close();
			} catch (SQLException e) {
			}
		}
	}

	private void cleanTempDir(ServletContextEvent event) {
		this.tempDir = HTTPUtils.getTempDir(event.getServletContext());
		FileUtils.forceDeleteAll(this.tempDir);

		// final Boolean deleted = FileUtils.forceDeleteAll(this.tempDir);
		// System.out.println("Tempdir delete = \"" + deleted.toString() +
		// "\".");
	}
}
