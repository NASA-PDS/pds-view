package gov.nasa.pds.web.ui.actions.stats;

import gov.nasa.arc.pds.tools.util.LocaleUtils;
import gov.nasa.arc.pds.tools.util.StrUtils;
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

public class ViewTabularManagementStats extends BaseViewAction {

	private static final long serialVersionUID = 1L;
	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:MM"); //$NON-NLS-1$

	private TableContainer statsByLabel;
	private TableContainer statsByUrl;

	private String totalLabels;
	private String totalTables; // useful?
	// list of all table types and number of each - would have to be entirely
	// dynamic
	private String totalDownloads;
	// private String csvDownloads;
	// private String tabDownloads;
	// percentage including headers

	// are max/min# of columns/rows in orig and downloaded file useful? average?
	// what about percentage kept in final product ie downloaded/orig

	private String avgFilters; // number of conditions per download
	private String avgDownloadsPerTable; // shows how many retries

	private String avgLabelsPerVisit;

	private String avgVisitDuration; // calculate with time of first label under

	// a given session id to last download
	// under same session id

	public TableContainer getStatsByLabel() {
		return this.statsByLabel;
	}

	public TableContainer getStatsByUrl() {
		return this.statsByUrl;
	}

	public String getTotalLabels() {
		return this.totalLabels;
	}

	public String getTotalTables() {
		return this.totalTables;
	}

	public String getTotalDownloads() {
		return this.totalDownloads;
	}

	public String getAvgFilters() {
		return this.avgFilters;
	}

	public String getAvgDownloadsPerTable() {
		return this.avgDownloadsPerTable;
	}

	public String getAvgLabelsPerVisit() {
		return this.avgLabelsPerVisit;
	}

	public String getAvgVisitDuration() {
		return this.avgVisitDuration;
	}

	@SuppressWarnings("nls")
	@Override
	protected String executeInner() throws Exception {
		if (!MCAuthenticate.authenticated()) {
			addError("error.protectedResource");
			return ILLEGAL_ACCESS;
		}
		setTitle("slicerStats.title");

		final Connection connection = DBManager.getConnection();
		final LocaleUtils lu = getUIManager().getLocaleUtils();
		String query = null;
		Statement queryStmt = null;
		ResultSet rs = null;

		// LAB 04/20/10 check if the following should be count(*) instead of
		// count(distince(id))
		query = "SELECT total_labels, total_tables, total_downloads, avg_filters , avg_downloads, avg_labels "
				+ "FROM "
				+ "(SELECT COUNT(DISTINCT slice_id) AS total_labels FROM slice) num_slices_table, "
				+ "(SELECT COUNT(DISTINCT tabulardata_id) AS total_tables FROM tabulardata) num_tabulardata_table, "
				+ "(SELECT COUNT(DISTINCT download_id) AS total_downloads FROM downloads) num_downloads_table, "
				+ "(SELECT AVG(filter_count) AS avg_filters FROM downloads) avg_filter_table, "
				+ "(SELECT ROUND(AVG(download_count)) AS avg_downloads FROM (SELECT tabulardata.tabulardata_id, COUNT(downloads.download_id) as download_count FROM tabulardata LEFT JOIN downloads ON tabulardata.tabulardata_id =downloads.tabulardata_id GROUP BY tabulardata.tabulardata_id) download_count_table ) avg_download_table,  "
				+ "(SELECT ROUND(AVG(label_count)) AS avg_labels FROM (SELECT count(slice_id) AS label_count FROM slice GROUP BY session_id) avg_labels_per_visit_table ) avg_label_count_table ; ";
		// avgVisitDuration

		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);

		rs.next();
		final long totalLabelsRaw = rs.getLong("total_labels");
		this.totalLabels = lu.getNumber(totalLabelsRaw);
		final long totalTablesRaw = rs.getLong("total_tables");
		this.totalTables = lu.getNumber(totalTablesRaw);
		final long totalDownloadsRaw = rs.getLong("total_downloads");
		this.totalDownloads = lu.getNumber(totalDownloadsRaw);
		final long avgFiltersRaw = rs.getLong("avg_filters");
		this.avgFilters = lu.getNumber(avgFiltersRaw);
		final long avgDownloadsRaw = rs.getLong("avg_downloads");
		this.avgDownloadsPerTable = lu.getNumber(avgDownloadsRaw);
		final long avgLabelsRaw = rs.getLong("avg_labels");
		this.avgLabelsPerVisit = lu.getNumber(avgLabelsRaw);

		final RowContainer statsByLabelHeader = new RowContainer("Date",
				"Label", "Tabular File", "Table Type", "Original Format",
				"Percentage of columns", "Percentage of rows",
				"# of Row Filters", "File Format"); // , "Columns Reordered");
		this.statsByLabel = new TableContainer("Downloads", statsByLabelHeader);
		query = "select d.download_id, d.tabulardata_id, d.headers_included, d.columns_selected, d.rows_selected, d.filter_count, d.time_created, d.file_type, s.label_url, t.table_url, t.columns_orig, t.rows_orig, t.table_type, t.load_time, t.cancelled, t.interchange_format from downloads d, slice s, tabulardata t where s.slice_id = t.slice_id AND t.tabulardata_id = d.tabulardata_id;";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);
		while (rs.next()) {
			final String tabulardataId = rs.getString("tabulardata_id");
			final String dateString = rs.getString("time_created");
			final Date dateRaw = DateUtils.MYSQL_DATE_FORMAT.parse(dateString);
			final String date = this.DATE_FORMAT.format(dateRaw);
			final String label = StrUtils.getURLFilename(rs
					.getString("label_url"));
			final String tableUrl = StrUtils.getURLFilename(rs
					.getString("table_url"));

			// get rid of?
			final String tableType = rs.getString("table_type");
			final String format = rs.getString("interchange_format");

			final String cols = new Long((rs.getLong("columns_selected") * 100)
					/ rs.getLong("columns_orig")).toString().concat("%");
			final String rows = new Long((rs.getLong("rows_selected") * 100)
					/ rs.getLong("rows_orig")).toString().concat("%");

			final String fileType = rs.getString("file_type");

			final long filters = rs.getLong("filter_count");

			// final String columnsReordered = "FALSE";//
			// rs.getBoolean("columns_reordered");

			final RowContainer row = new RowContainer(tabulardataId, date,
					label, tableUrl, tableType, format, cols, rows, filters,
					fileType); // , columnsReordered);
			this.statsByLabel.addRow(row);
		}

		// visits by URL
		final RowContainer statsByUrlHeader = new RowContainer("link",
				"Label URL", "Tabular File", "Table Type", "Date of visit");
		this.statsByUrl = new TableContainer("Visits by Url", statsByUrlHeader);
		query = "select s.label_url, t.tabulardata_id, t.table_url, t.table_type, t.time_created "
				+ "from slice s, tabulardata t where s.slice_id = t.slice_id "
				+ " order by s.label_url";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);

		while (rs.next()) {
			final String tabulardataId = rs.getString("tabulardata_id");

			final String labelUrl = rs.getString("label_url");
			final String tabularFile = StrUtils.getURLFilename(rs
					.getString("table_url"));

			final String dateString = rs.getString("time_created");
			final Date dateRaw = DateUtils.MYSQL_DATE_FORMAT.parse(dateString);
			final String date = this.DATE_FORMAT.format(dateRaw);

			final String tableType = rs.getString("table_type");
			final RowContainer row = new RowContainer(tabulardataId, labelUrl,
					tabularFile, tableType, date);
			this.statsByUrl.addRow(row);

		}

		connection.close();

		return SUCCESS;
	}
}
