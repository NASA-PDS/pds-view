package gov.nasa.pds.web.ui.managers;

import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.tools.constants.Constants.ProblemType;
import gov.nasa.pds.web.ui.containers.dataSet.ValidationResults;
import gov.nasa.pds.web.ui.containers.tabularManagement.SliceContainer;
import gov.nasa.pds.web.ui.containers.tabularManagement.TabularDataContainer;
import gov.nasa.pds.web.ui.utils.DateUtils;
import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class LogManager {

	public static final String LOG_VALIDATION_STATS = "INSERT INTO volume_stats SET validating_ip = ?, volume_id = ?, volume_size = ?, date_validated = ?, cancelled = ?, duration = ?"; //$NON-NLS-1$

	public static final String LOG_VALIDATION_ERRORS = "INSERT INTO volume_errors SET volume_stat_id = ?, error_type = ?, num_found = ?"; //$NON-NLS-1$

	public static final String LOG_QUEUE_SIZE = "INSERT INTO queue SET test_date = ?, queue_size = ?"; //$NON-NLS-1$

	public static final String LOG_EXCEPTION = "INSERT INTO exceptions SET date = ?, url = ?, stack = ?, message = ?"; //$NON-NLS-1$

	public static final String LOG_GENERIC = "INSERT INTO logs SET message = ?"; //$NON-NLS-1$

	public static PreparedStatement LOG_VALIDATION_STATS_STMNT;

	public static PreparedStatement LOG_VALIDATION_ERRORS_STMNT;

	public static PreparedStatement LOG_VALIDATION_QUEUE_SIZE_STMNT;

	public static PreparedStatement LOG_EXCEPTION_STMNT;

	public static PreparedStatement LOG_GENERIC_STMNT;

	public static final String LOG_TABDATA_LABEL = "INSERT INTO slice SET slice_id = ?, session_id = ?, label_url = ?, user_ip = ?"; //$NON-NLS-1$

	public static final String LOG_TABDATA_TABLE = "INSERT INTO tabulardata SET  slice_id = ?, table_url = ?, columns_orig = ?, rows_orig = ?, table_type = ?, load_time = ?, cancelled = ?, interchange_format = ?"; //$NON-NLS-1$

	public static final String LOG_TABDATA_FILTER = "INSERT INTO filters SET tabulardata_id = ?, column_name = ?, column_type = ?, filter_condition = ?, condition_value = ? , if_deleted = ?"; //$NON-NLS-1$

	public static final String LOG_TABDATA_FILTER_REMOVAL = "UPDATE filters SET if_deleted = ? WHERE tabulardata_id = ? AND column_name = ? AND filter_condition = ? AND condition_value = ? "; //$NON-NLS-1$

	public static final String LOG_TABDATA_DOWNLOADS = "INSERT INTO downloads SET tabulardata_id = ?, headers_included = ?, columns_selected = ?, rows_selected = ?, filter_count  = ?,  file_type = ?, interchange_format = ?"; //$NON-NLS-1$

	public static PreparedStatement LOG_TABDATA_LABEL_STMT;

	public static PreparedStatement LOG_TABDATA_TABLE_STMT;

	public static PreparedStatement LOG_TABDATA_FILTER_STMT;

	public static PreparedStatement LOG_TABDATA_FILTER_REMOVAL_STMT;

	public static PreparedStatement LOG_TABDATA_DOWNLOAD_STMT;

	public static void logValidation(final ValidationResults results) {
		// TODO: get pertinent values from results

		// add args to prepared statement
		String ip = HTTPUtils.getRequestIP();
		if (ip == null) {
			ip = "127.0.0.1"; //$NON-NLS-1$
		}

		try {
			Connection connection = DBManager.getConnection();
			LOG_VALIDATION_ERRORS_STMNT = connection
					.prepareStatement(LOG_VALIDATION_ERRORS);

			LOG_VALIDATION_STATS_STMNT = connection.prepareStatement(
					LOG_VALIDATION_STATS, Statement.RETURN_GENERATED_KEYS);

			// set user ip
			LOG_VALIDATION_STATS_STMNT.setString(1, ip);

			// set volume id
			LOG_VALIDATION_STATS_STMNT.setString(2, results.getVolumeId());

			// set volume size
			// TODO: stuff the size into the results
			LOG_VALIDATION_STATS_STMNT.setLong(3, results.getNumFiles()
					+ results.getNumFolders());

			// set date validated
			java.util.Date now = new java.util.Date();
			Timestamp ts = DateUtils.toSQLTimeStamp(now);
			LOG_VALIDATION_STATS_STMNT.setTimestamp(4, ts);

			// set cancelled
			LOG_VALIDATION_STATS_STMNT.setBoolean(5, false);

			// set duration
			LOG_VALIDATION_STATS_STMNT.setLong(6, results.getDuration());

			// do exec
			LOG_VALIDATION_STATS_STMNT.executeUpdate();

			// get keys from insert
			ResultSet keys = LOG_VALIDATION_STATS_STMNT.getGeneratedKeys();
			// update cursor position to first row
			keys.next();
			// retrieve id
			final Long id = keys.getLong(1);

			// get id of new stats record

			// log errors by type
			final Map<ProblemType, Integer> groupCount = results
					.getGroupCount();
			Iterator<Entry<ProblemType, Integer>> it = groupCount.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<ProblemType, Integer> entry = it.next();
				ProblemType probType = entry.getKey();
				// set id from prev summary insert
				LOG_VALIDATION_ERRORS_STMNT.setLong(1, id);

				LOG_VALIDATION_ERRORS_STMNT.setString(2, probType.toString());

				LOG_VALIDATION_ERRORS_STMNT.setLong(3, entry.getValue());

				LOG_VALIDATION_ERRORS_STMNT.executeUpdate();
			}

			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void logException(final Exception e) {
		try {
			Connection connection = DBManager.getConnection();

			LOG_EXCEPTION_STMNT = connection.prepareStatement(LOG_EXCEPTION);

			// set date occurred
			java.util.Date now = new java.util.Date();
			Timestamp ts = DateUtils.toSQLTimeStamp(now);
			LOG_EXCEPTION_STMNT.setTimestamp(1, ts);

			// exception url
			final String url = HTTPUtils.getFullURL();
			LOG_EXCEPTION_STMNT.setString(2, url);

			// exception string
			final String stack = StrUtils.toString(e, true, null);
			LOG_EXCEPTION_STMNT.setString(3, stack);

			// message string
			final String message = StrUtils.toString(e, false, 0);
			LOG_EXCEPTION_STMNT.setString(4, message);

			// execute exception
			LOG_EXCEPTION_STMNT.executeUpdate();

			connection.close();
		} catch (SQLException sqle) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void logLabel(SliceContainer slice) {
		String ip = HTTPUtils.getRequestIP();
		if (ip == null) {
			ip = "127.0.0.1"; //$NON-NLS-1$
		}
		// public static final String LOG_TABDATA_LABEL =
		// "INSERT INTO slice SET slice_id = ?, session_id = ?,
		// label_url = ?, user_ip = ?, time_created = ?";
		// add args to prepared statement
		try {
			Connection connection = DBManager.getConnection();

			LOG_TABDATA_LABEL_STMT = connection.prepareStatement(
					LOG_TABDATA_LABEL, Statement.RETURN_GENERATED_KEYS);

			// set slice id
			LOG_TABDATA_LABEL_STMT.setLong(1, slice.getDbId());

			// set session id
			LOG_TABDATA_LABEL_STMT.setString(2, HTTPUtils.getSessionId());

			// set label_url
			LOG_TABDATA_LABEL_STMT.setString(3, slice.getLabelURLString());

			// set user ip
			LOG_TABDATA_LABEL_STMT.setString(4, ip);

			LOG_TABDATA_LABEL_STMT.executeUpdate();

			// get keys from insert
			ResultSet keys = LOG_TABDATA_LABEL_STMT.getGeneratedKeys();
			if (keys.next()) {
				// update cursor position to first row

				// retrieve id
				final Long id = keys.getLong(1);
				// set value in slice passed in
				slice.setDbId(id);
				// pass slice back?
			} else {
				System.out.println("There are no generated keys."); //$NON-NLS-1$
			}

			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void logTabulardata(SliceContainer slice) {
		// LOG_TABDATA_TABLE = "INSERT INTO tabulardata
		// SET slice_id = ?, table_url = ?, columns_orig = ?,
		// rows_orig = ?, table_type = ?, load_time = ?,
		//cancelled = ?, interchange_format"; //$NON-NLS-1$

		// INSERT INTO tabulardata SET slice_id = ?, table_url = ?,
		// columns_orig = ?, rows_orig = ?, table_type = ?, load_time = ?,
		//cancelled = ?, interchange_format"; //$NON-NLS-1$
		Connection connection = null;

		TabularDataContainer table = slice.getActiveTabularData();
		try {

		    connection = DBManager.getConnection();

			LOG_TABDATA_TABLE_STMT = connection.prepareStatement(
					LOG_TABDATA_TABLE, Statement.RETURN_GENERATED_KEYS);
			// set slice_id
			LOG_TABDATA_TABLE_STMT.setLong(1, slice.getDbId());
			// set table_url
			LOG_TABDATA_TABLE_STMT.setString(2, table.getTabFileUrl());
			// set columns_orig
			LOG_TABDATA_TABLE_STMT.setLong(3, table.getColumns().size());
			// set rows_orig
			LOG_TABDATA_TABLE_STMT.setLong(4, table.getTotalRows());
			// set table_type
			LOG_TABDATA_TABLE_STMT.setString(5, table.getType());
			// set load_time
			// LAB 04/12/10 NEED TO CALCULATE LOAD TIME
			LOG_TABDATA_TABLE_STMT.setLong(6, 0);
			// set canceled
			// LAB 04/12/10 NEED TO SAVE IF CANCELLED
			LOG_TABDATA_TABLE_STMT.setBoolean(7, false);

			LOG_TABDATA_TABLE_STMT.setString(8, table.getFormat());

			LOG_TABDATA_TABLE_STMT.executeUpdate();

			// get returned key,
			// get keys from insert
			ResultSet keys = LOG_TABDATA_TABLE_STMT.getGeneratedKeys();
			// update cursor position to first row
			keys.next();
			// retrieve id
			final Long id = keys.getLong(1);
			// set value in table of slice passed in
			table.setDbId(id);
			// pass slice back?

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
				}
		}
	}

	public static void logFilter(TabularDataContainer table,
			String column_name, String condition, String value,
			boolean if_deleted) {

		try {
			Connection connection = DBManager.getConnection();
			LOG_TABDATA_FILTER_REMOVAL_STMT = connection
					.prepareStatement(LOG_TABDATA_FILTER_REMOVAL);
			if (if_deleted) {
				// LOG_TABDATA_FILTER_REMOVAL = "UPDATE filters SET if_deleted =
				// ? WHERE tabulardata_id = ?, column_name = ?, filter_condition
				// = ?, condition_value = ? "; //$NON-NLS-1$

				LOG_TABDATA_FILTER_REMOVAL_STMT.setBoolean(1, if_deleted);
				LOG_TABDATA_FILTER_REMOVAL_STMT.setLong(2, table.getDbId());
				LOG_TABDATA_FILTER_REMOVAL_STMT.setString(3, column_name);
				LOG_TABDATA_FILTER_REMOVAL_STMT.setString(4, condition);
				LOG_TABDATA_FILTER_REMOVAL_STMT.setString(5, value);
				LOG_TABDATA_FILTER_REMOVAL_STMT.executeUpdate();

			} else {
				LOG_TABDATA_FILTER_STMT = connection
						.prepareStatement(LOG_TABDATA_FILTER);
				// public static final String LOG_TABDATA_FILTERS =
				// "INSERT INTO filters SET tabulardata_id = ?,
				// column_name = ?, column_type = ?, filter_condition = ?,
				// condition_value = ?, if_deleted = ? ";

				// set tabulardata_id
				LOG_TABDATA_FILTER_STMT.setLong(1, table.getDbId());
				// set column name
				LOG_TABDATA_FILTER_STMT.setString(2, column_name);
				// set column type
				LOG_TABDATA_FILTER_STMT.setString(3, table.getColumn(
						column_name).getDataType());
				// set condition
				LOG_TABDATA_FILTER_STMT.setString(4, condition);
				// set value
				LOG_TABDATA_FILTER_STMT.setString(5, value);
				// set if_deleted
				LOG_TABDATA_FILTER_STMT.setBoolean(6, if_deleted);

				LOG_TABDATA_FILTER_STMT.executeUpdate();
			}

			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void logDownload(SliceContainer slice) {
		try {
			Connection connection = DBManager.getConnection();

			LOG_TABDATA_DOWNLOAD_STMT = connection
					.prepareStatement(LOG_TABDATA_DOWNLOADS);
			// LOG_TABDATA_DOWNLOADS = "INSERT INTO downloads
			// SET tabulardata_id = ?, headers_included = ?,
			// columns_selected = ?, rows_selected = ?, filter_count = ?,
			//file_type = ?, interchange_format;"; //$NON-NLS-1$
			TabularDataContainer table = slice.getActiveTabularData();

			LOG_TABDATA_DOWNLOAD_STMT.setLong(1, table.getDbId());
			LOG_TABDATA_DOWNLOAD_STMT.setBoolean(2, slice.getIncludeHeaders());
			LOG_TABDATA_DOWNLOAD_STMT.setInt(3, table.getSelectedColumnCount());
			LOG_TABDATA_DOWNLOAD_STMT.setInt(4, table.getRowsReturned());
			LOG_TABDATA_DOWNLOAD_STMT.setInt(5, table.getConditions().size());
			LOG_TABDATA_DOWNLOAD_STMT.setString(6, slice.getFileType());
			// LAB 04/23/10 - NEED to change to the output format when that is
			// saved
			LOG_TABDATA_DOWNLOAD_STMT.setString(7, table.getFormat());

			LOG_TABDATA_DOWNLOAD_STMT.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void logGeneric(final String message) {

		try {

			Connection connection = DBManager.getConnection();

			LOG_GENERIC_STMNT = connection.prepareStatement(LOG_GENERIC);
			// store generic error string, specific data should be formatted
			// into the message string
			LOG_GENERIC_STMNT.setString(1, message);

			LOG_GENERIC_STMNT.executeUpdate();

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
