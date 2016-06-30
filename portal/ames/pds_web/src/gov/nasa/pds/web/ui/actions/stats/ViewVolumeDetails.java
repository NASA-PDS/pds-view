package gov.nasa.pds.web.ui.actions.stats;

import gov.nasa.arc.pds.tools.util.LocaleUtils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Display statistics for a single volume over time
 * 
 * @author jagander
 */
public class ViewVolumeDetails extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Date format used for display
	 */
	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:MM"); //$NON-NLS-1$

	/**
	 * Id of volume displaying details of
	 */
	private String volumeId;

	/**
	 * Container for displaying statistics
	 */
	private TableContainer stats;

	private LocaleUtils localeUtils;

	// max numeric value for day
	private final Map<String, Long> maxVals = new HashMap<String, Long>();

	// dates found, used for index lookup
	private final List<String> dates = new ArrayList<String>();

	public void setVolume(final String volumeId) {
		this.volumeId = volumeId;
	}

	public TableContainer getErrorStats() {
		return this.stats;
	}

	@SuppressWarnings("nls")
	@Override
	protected String executeInner() throws Exception {
		if (!MCAuthenticate.authenticated()) {
			addError("error.protectedResource");
			return ILLEGAL_ACCESS;
		}

		setTitle("volumeDetailStats.title", this.volumeId);

		// key to value set (ie, leftmost values identify row)
		final Map<String, RowContainer> errorTypeRows = new HashMap<String, RowContainer>();

		// date to sum mapping - separate from row value since final value to be
		// a
		// formatted string
		final Map<String, Long> sums = new HashMap<String, Long>();

		// TEST ONLY, KILLING CONNECTION TO RE_INSTANTIATE WITH ALTERNATE DB
		// DBManager.flushConnection();
		// final Connection connection = DBManager.getConnection(null, null,
		// null, "pds");
		final Connection connection = DBManager.getConnection();
		this.localeUtils = getUIManager().getLocaleUtils();
		String query = null;
		Statement queryStmt = null;
		ResultSet rs = null;

		final RowContainer genVolStatsHeader = new RowContainer("");
		this.stats = new TableContainer("Validations for " + this.volumeId,
				genVolStatsHeader);

		// get all dates for validations to use for filling table with zero
		// values and index lookup
		query = "SELECT date_validated FROM volume_stats WHERE volume_id = '"
				+ this.volumeId + "' ORDER BY date_validated";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);

		while (rs.next()) {
			final String date = rs.getString("date_validated");
			sums.put(date, (long) 0);
			this.dates.add(date);
		}

		// get all found error types and instantiate rows adding to both the
		// table and the map
		query = "SELECT DISTINCT error_type FROM volume_errors ORDER BY error_type";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);

		while (rs.next()) {
			final String errorType = rs.getString("error_type");
			final RowContainer row = new RowContainer(errorType);
			this.stats.addRow(row);
			errorTypeRows.put(errorType, row);
		}

		// add duration, sum rows and arow to separate from the remainder of the
		// table
		final RowContainer sumRow = new RowContainer("Sum");
		final RowContainer durationRow = new RowContainer("Duration");
		final RowContainer volumeSizeRow = new RowContainer(
				"Volume Size (num files)");
		final RowContainer fillerRow = new RowContainer("&nbsp;");

		// add rows to table
		this.stats.addRow(fillerRow);
		this.stats.addRow(sumRow);
		this.stats.addRow(volumeSizeRow);
		this.stats.addRow(durationRow);

		// add column title and fill for all columns
		for (String dateString : this.dates) {
			final Date dateRaw = DateUtils.MYSQL_DATE_FORMAT.parse(dateString);
			final String date = DATE_FORMAT.format(dateRaw);
			genVolStatsHeader.add(date);
			sumRow.add((long) 0);
			durationRow.add((long) 0);
			volumeSizeRow.add((long) 0);
			fillerRow.add("&nbsp;");
			for (final RowContainer row : errorTypeRows.values()) {
				row.add((long) 0);
			}
		}

		// get values for each column
		query = "SELECT error_type, SUM(num_found) AS num_errors, volume_stats.date_validated, volume_stats.duration, volume_stats.volume_size FROM volume_errors INNER JOIN volume_stats ON volume_stats.volume_stat_id = volume_errors.volume_stat_id WHERE volume_stats.volume_id='"
				+ this.volumeId
				+ "' GROUP BY error_type, volume_stats.volume_stat_id ORDER BY date_validated;";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);

		while (rs.next()) {
			final String date = rs.getString("date_validated");
			final String errorType = rs.getString("error_type");
			final long numErrorsRaw = rs.getLong("num_errors");
			final long durationRaw = rs.getLong("duration");
			final String duration = DateUtils
					.getMillisecondsToDuration(durationRaw);
			final long volumeSizeRaw = rs.getLong("volume_size");

			// get error type row from map
			final RowContainer errorRow = errorTypeRows.get(errorType);

			// get index for column (add one for leftmost col)
			final int index = this.dates.indexOf(date) + 1;
			// update error display

			// override fill value
			errorRow.set(index, numErrorsRaw);

			// set duration for date
			durationRow.set(index, duration);

			// set volume size
			volumeSizeRow.set(index, volumeSizeRaw);

			// update sum for date
			final long newSum = sums.get(date) + numErrorsRaw;
			sums.put(date, newSum);

		}

		// fill sums row with raw values and set max val for day
		for (int i = 0; i < this.dates.size(); i++) {
			// get index for column
			final int index = i + 1;
			final String date = this.dates.get(i);
			final long sumRaw = sums.get(date);
			sumRow.set(index, sumRaw);
			long volumeSizeForDate = (Long) volumeSizeRow.get(index);
			long maxVal = Math.max(sumRaw, volumeSizeForDate);
			this.maxVals.put(date, maxVal);
		}

		// get max values for each date

		// decorate values
		for (final RowContainer row : errorTypeRows.values()) {
			decorateRow(row);
		}

		decorateRow(sumRow);
		decorateRow(volumeSizeRow, false);

		connection.close();

		return SUCCESS;
	}

	private void decorateRow(final RowContainer row) {
		decorateRow(row, true);
	}

	// iterate over row elements now that filled and decorate with modifiers
	// indicating change and do number formatting, only used for integer values
	@SuppressWarnings("nls")
	private void decorateRow(final RowContainer row, final boolean doColor) {
		Long lastVal = null;
		for (int i = 1; i < row.size(); i++) {
			// get padded length for value
			final String date = this.dates.get(i - 1);
			final String maxVal = this.maxVals.get(date).toString();
			int paddedLength = (int) (maxVal.length() + Math.floor((maxVal
					.length() / 3)));

			long curValRaw = (Long) row.get(i);
			String curVal = this.localeUtils.getNumber(curValRaw);
			curVal = leftPad(curVal, paddedLength);
			String presentation = curVal;
			if (doColor) {
				if (lastVal != null) {
					if (lastVal > curValRaw) {
						presentation = "<span class=\"better\">" + curVal
								+ "</span>";
					} else if (lastVal < curValRaw) {
						presentation = "<span class=\"worse\">" + curVal
								+ "</span>";
					} else if (lastVal == 0 && curValRaw == 0) {
						presentation = "<span class=\"zero\">" + curVal
								+ "</span>";
					} else {
						presentation = "<span class=\"numeric\">" + curVal
								+ "</span>";
					}
				} else if (curValRaw == 0) {
					presentation = "<span class=\"zero\">" + curVal + "</span>";
				} else {
					presentation = "<span class=\"numeric\">" + curVal
							+ "</span>";
				}
			}
			row.set(i, presentation);
			lastVal = curValRaw;
		}
	}

	// TODO: move to pds utils
	private String leftPad(final String value, final int pad) {
		String out = value;
		final int numSpaces = pad - value.length();
		if (numSpaces > 0) {
			for (int i = 0; i < numSpaces; i++) {
				out = "&nbsp;" + out; //$NON-NLS-1$
			}
		}
		return out;
	}

}
