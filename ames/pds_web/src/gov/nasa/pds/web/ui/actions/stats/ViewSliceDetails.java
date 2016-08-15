package gov.nasa.pds.web.ui.actions.stats;

import gov.nasa.pds.web.ui.actions.BaseViewAction;
import gov.nasa.pds.web.ui.actions.misc.MCAuthenticate;
import gov.nasa.pds.web.ui.containers.RowContainer;
import gov.nasa.pds.web.ui.containers.TableContainer;
import gov.nasa.pds.web.ui.managers.DBManager;
import gov.nasa.pds.web.ui.utils.DateUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewSliceDetails extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:MM"); //$NON-NLS-1$

	private String tabId;

	private long columnsOrig;

	private long rowsOrig;

	private String labelUrl;

	private String tabUrl;

	private String tableType;

	private String tableFormat;

	private TableContainer stats;

	public void setTabId(final String tabId) {
		this.tabId = tabId;
	}

	public String getTabId() {
		return this.tabId;
	}

	public long getColumnsOrig() {
		return this.columnsOrig;
	}

	public long getRowsOrig() {
		return this.rowsOrig;
	}

	public String getLabelUrl() {
		return this.labelUrl;
	}

	public String getTabUrl() {
		return this.tabUrl;
	}

	public String getTableType() {
		return this.tableType;
	}

	public String getTableFormat() {
		return this.tableFormat;
	}

	public TableContainer getStats() {
		return this.stats;
	}

	@SuppressWarnings("nls")
	@Override
	protected String executeInner() throws Exception {
		if (!MCAuthenticate.authenticated()) {
			addError("error.protectedResource");
			return ILLEGAL_ACCESS;
		}

		setTitle("SliceDetailStats.title", this.tabId);

		// loop through tabulardata objects
		// columns orig rows orig
		// downloads columns selected (percentage? ratio?)
		// rows selected same as columns
		// # of filters

		final Connection connection = DBManager.getConnection();

		String query = null;
		Statement queryStmt = null;
		ResultSet rs = null;

		// query the general info on tabulardata
		query = "SELECT s.label_url, t.table_url, t.table_type, t.columns_orig, t.rows_orig, t.time_created, t.interchange_format "
				+ "FROM slice s , tabulardata t "
				+ "WHERE s.slice_id = t.slice_id AND t.tabulardata_id = '"
				+ this.tabId + "' ";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);

		if (rs.next()) {
			// fill the values that should be the same across all rows
			this.labelUrl = rs.getString("label_url");
			this.tabUrl = rs.getString("table_url");
			this.tableType = rs.getString("table_type");
			this.columnsOrig = rs.getLong("columns_orig");
			this.rowsOrig = rs.getLong("rows_orig");
			// final String dateString = rs.getString("time_created");
			// final Date dateRaw =
			// DateUtils.MYSQL_DATE_FORMAT.parse(dateString);
			// final String date = DATE_FORMAT.format(dateRaw);
			this.tableFormat = rs.getString("interchange_format");
		}

		query = "SELECT d.time_created, filter_count, columns_selected, rows_selected, d.file_type, headers_included "
				+ "FROM tabulardata t , downloads d   "
				+ "WHERE t.tabulardata_id = d.tabulardata_id AND t.tabulardata_id = '"
				+ this.tabId + "' ORDER BY t.time_created;";

		// could we use the column names from the query to fill the first/
		// leftmost field of each row?
		// loop through the metadata for the result set
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);

		// now build downloads
		final RowContainer statsHeader = new RowContainer("Date",
				"# of Filters", "# Cols Selected", "# Rows Selected",
				"Download Format", "Headers Included");
		this.stats = new TableContainer("Downloads", statsHeader);
		rs.beforeFirst();
		while (rs.next()) {
			// d.time_created, filter_count, columns_selected, rows_selected,
			// format, headers_included
			final String downloadDateString = rs.getString("time_created");
			final Date downloadDateRaw = DateUtils.MYSQL_DATE_FORMAT
					.parse(downloadDateString);
			final String downloadDate = DATE_FORMAT.format(downloadDateRaw);
			// should this be numeric?
			final String filterCount = rs.getString("filter_count");
			final String columnsSelected = rs.getString("columns_selected");
			final String rowsSelected = rs.getString("rows_selected");
			final String format = rs.getString("file_type");
			final boolean headersIncluded = rs.getBoolean("headers_included");
			final RowContainer row = new RowContainer(downloadDate,
					filterCount, columnsSelected, rowsSelected, format,
					headersIncluded);
			this.stats.addRow(row);
		}

		connection.close();

		return SUCCESS;
	}
}
