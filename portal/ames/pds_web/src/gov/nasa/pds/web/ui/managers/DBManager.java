package gov.nasa.pds.web.ui.managers;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.ui.constants.ApplicationConstants;
import gov.nasa.pds.web.ui.utils.ApplicationProperties;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBManager {

	private static ApplicationProperties appProps;

	public static final int SCHEMA_VERSION = 7;

	public static Connection getConnection() {
		return getConnection(null, null, null, null);
	}

	// primarily used for test purposes so that you can instantiate with
	// different connect info
	public static Connection getConnection(final String server,
			final String user, final String pass, final String db) {

		return initConnection(server, user, pass, db);
	}

	private static Connection initConnection(final String serverOverride,
			final String userOverride, final String passOverride,
			final String dbOverride) {
		appProps = new ApplicationProperties();
		Connection connection = null;

		try {
			final String server = serverOverride == null ? appProps
					.getMysqlServer() : serverOverride;
			final String port = serverOverride == null ? (":" + appProps //$NON-NLS-1$
					.getMysqlPort()) : ""; //$NON-NLS-1$
			final String user = userOverride == null ? appProps.getMysqlUser()
					: userOverride;
			final String pass = passOverride == null ? appProps.getMysqlPass()
					: passOverride;
			final String db = dbOverride == null ? appProps.getMysqlDB()
					: dbOverride;

			Class.forName("com.mysql.jdbc.Driver").newInstance(); //$NON-NLS-1$
			connection = DriverManager.getConnection(
					"jdbc:mysql://" + server + port, user, pass); //$NON-NLS-1$

			if (!connection.isClosed()) {
				// System.out.println("Successfully connected to MySQL server...");

				Statement st = connection.createStatement();
				try {
					st.execute("USE " + db); //$NON-NLS-1$
				} catch (SQLException e) {
					// database does not exist, initialize it
					initDB(connection);
				}
				// only check the version if you're using the default db
				if (dbOverride == null) {
					checkSchemaVersion(connection);
				}
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage()); //$NON-NLS-1$
		}
		return connection;
	}

	// TODO: use external .sql file
	private static void initDB(Connection connection) throws SQLException {
		System.out.println("Database was not initialized. Setting up now."); //$NON-NLS-1$
		Statement st = null;
		st = connection.createStatement();

		// create db
		st.execute("CREATE DATABASE IF NOT EXISTS " + appProps.getMysqlDB()); //$NON-NLS-1$

		// use db
		st.execute("USE " + appProps.getMysqlDB()); //$NON-NLS-1$

		// do setup
		executeSQLFile(ApplicationConstants.SETUP_SQL_FILENAME, connection);
	}

	private static void executeSQLFile(final String name, Connection connection) {

		// get setup sql
		final InputStream is = DBManager.class.getResourceAsStream("/" + name); //$NON-NLS-1$
		try {
			Statement st = connection.createStatement();
			final String sql = StrUtils.toString(is);
			final List<String> statements = StrUtils.toStatements(sql);
			for (final String currStatement : statements) {
				st.executeUpdate(currStatement);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean isActive(Connection connection) {
		if (connection == null) {
			return false;
		}

		// try simple query to see if connection is open and working
		try {
			Statement st = connection.createStatement();
			st.execute("SHOW tables"); //$NON-NLS-1$
		} catch (SQLException e) {
			return false;
		}

		return true;
	}

	private static int getVersion(Connection connection) throws SQLException {
		Statement st = connection.createStatement();
		ResultSet result = st.executeQuery("SELECT db_schema FROM version"); //$NON-NLS-1$
		result.absolute(1);
		return result.getInt("db_schema"); //$NON-NLS-1$
	}

	private static void checkSchemaVersion(Connection connection)
			throws SQLException {
		int version = 0;
		try {
			version = getVersion(connection);
		} catch (SQLException e) {
			// version is 0, update to 1
			executeSQLFile(ApplicationConstants.SQL_SCHEMA_VERSION_ROOT
					+ "1.sql", connection); //$NON-NLS-1$
			version = getVersion(connection);
		}

		while (version < SCHEMA_VERSION) {
			executeSQLFile(ApplicationConstants.SQL_SCHEMA_VERSION_ROOT
					+ (version + 1) + ".sql", connection); //$NON-NLS-1$
			version = getVersion(connection);
		}

	}

	/*
	 * clear old validation data, this is a hack to compensate for premature
	 * session death on the production server
	 */
	public static void clearOldValidationData() {
		// get a connection to the db to the information schema db
		Connection connection = getConnection(null, null, null,
				"information_schema"); //$NON-NLS-1$

		// get a list of the tables that are older than 1 day and drop them
		Statement st;
		Statement ds;
		try {
			st = connection.createStatement();
			ds = connection.createStatement();
			ResultSet result = st
					.executeQuery("SELECT TABLE_NAME from TABLES WHERE TABLE_NAME LIKE 'temp_errors_%' && DATEDIFF(NOW(), CREATE_TIME) > 0"); //$NON-NLS-1$
			// drop the tables
			while (result.next()) {
				// delete all tables starting with sliceTablePrefix
				ds.execute("DROP TABLE `" + result.getString(1) + "` ; "); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// drop the found tables
	}

}
