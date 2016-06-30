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

/**
 * View validation statistics
 * 
 * @author jagander
 */
// TODO: refactor, this was created before there were other types of stats to
// view. Now, the name is too generic.
public class ViewStats extends BaseViewAction {

	private static final long serialVersionUID = 1L;

	/**
	 * General volume statistics, rolled up by volume and sorted by last
	 * validated
	 */
	private TableContainer generalVolStats;

	/**
	 * Improvement statistics, compares max and min errors by total and type
	 */
	private TableContainer improvement;

	/**
	 * Error statistics broken down by error type rather than volume
	 */
	private TableContainer errorStats;

	/**
	 * Improvement across validations of volumes broken down by error type
	 */
	private TableContainer errorImprovementStats;

	/**
	 * Total number of volumes validated (multiple validations of the same
	 * volume do not increase this number)
	 */
	private String totalVolumes;

	/**
	 * Total validations
	 */
	private String totalValidations;

	/**
	 * Max number of files seen in a volume
	 */
	private String maxNumFiles;

	/**
	 * Id for volume with max number of files
	 */
	private String maxNumFilesId;

	/**
	 * Min number of files seen in a volume
	 */
	private String minNumFiles;

	/**
	 * Id for volume with min number of files
	 */
	private String minNumFilesId;

	/**
	 * Average number of files seen in a volume
	 */
	private String avgNumFiles;

	/**
	 * Max amount of time it took to run a validation
	 */
	private String maxDuration;

	/**
	 * Id for volume that took the longest time to validate
	 */
	private String maxDurationId;

	/**
	 * Min amount of time it took to run a validation
	 */
	private String minDuration;

	/**
	 * Id for volume that took the least amount of time to validate
	 */
	private String minDurationId;

	/**
	 * Average time it took to run a validation
	 */
	private String avgDuration;

	/**
	 * Number of times someone validated a volume once and never validated again
	 * (to confirm changes presumably)
	 */
	private String bounces;

	/**
	 * Percent of validations that were counted as bounces
	 */
	private double bouncePercent;

	/**
	 * Get general volume stats, rolled up by volume
	 * 
	 * @return general volume stats
	 */
	public TableContainer getGeneralVolStats() {
		return this.generalVolStats;
	}

	/**
	 * Get improvement stats, rolled up by volume
	 * 
	 * @return improvement stats
	 */
	public TableContainer getImprovement() {
		return this.improvement;
	}

	/**
	 * Get stats by error type
	 * 
	 * @return error stats
	 */
	public TableContainer getErrorStats() {
		return this.errorStats;
	}

	/**
	 * Get improvement stats by error type
	 * 
	 * @return error improvement stats
	 */
	public TableContainer getErrorImprovementStats() {
		return this.errorImprovementStats;
	}

	/**
	 * Get max number of files
	 * 
	 * @return max number of files
	 */
	public String getMaxNumFiles() {
		return this.maxNumFiles;
	}

	/**
	 * Get id of volume with max num files
	 * 
	 * @return id of volume with max num files
	 */
	public String getMaxNumFilesId() {
		return this.maxNumFilesId;
	}

	/**
	 * Get min num files in a volume
	 * 
	 * @min num files
	 */
	public String getMinNumFiles() {
		return this.minNumFiles;
	}

	/**
	 * Get id of volume with min num files
	 * 
	 * @return id of volume with min num files
	 */
	public String getMinNumFilesId() {
		return this.minNumFilesId;
	}

	/**
	 * Get average number of files in a volume
	 * 
	 * @return average number of files in a volume
	 */
	public String getAvgNumFiles() {
		return this.avgNumFiles;
	}

	/**
	 * Get max amount of time it took to validate a volume
	 * 
	 * @return max amount of time it took to validate a volume
	 */
	public String getMaxDuration() {
		return this.maxDuration;
	}

	/**
	 * Get id of volume that took longest to validate
	 * 
	 * @return id of volume that took longest to validate
	 */
	public String getMaxDurationId() {
		return this.maxDurationId;
	}

	/**
	 * Get shortest amount of time it took to validate a volume
	 * 
	 * @return shortest amount of time it took to validate a volume
	 */
	public String getMinDuration() {
		return this.minDuration;
	}

	/**
	 * Get id of volume that took shortest amount of time to validate
	 * 
	 * @return id of volume that took shortest amount of time to validate
	 */
	public String getMinDurationId() {
		return this.minDurationId;
	}

	/**
	 * Get average time it took to validate volumes
	 * 
	 * @return average time it took to validate volumes
	 */
	public String getAvgDuration() {
		return this.avgDuration;
	}

	/**
	 * Get number of one-time-only validations
	 * 
	 * @return number of bounces
	 */
	public String getBounces() {
		return this.bounces;
	}

	/**
	 * Get percentage of validations that were counted as bounces
	 * 
	 * @return percentage of bounces
	 */
	public double getBouncePercent() {
		return this.bouncePercent;
	}

	/**
	 * Get total number of validations
	 * 
	 * @return total number of validations
	 */
	public String getTotalValidations() {
		return this.totalValidations;
	}

	/**
	 * Get total number of volumes validated
	 * 
	 * @return total number of volumes validated
	 */
	public String getTotalVolumes() {
		return this.totalVolumes;
	}

	@SuppressWarnings("nls")
	@Override
	protected String executeInner() throws Exception {

		// protected resource, kick to log in if not authenticated
		if (!MCAuthenticate.authenticated()) {
			addError("error.protectedResource");
			return ILLEGAL_ACCESS;
		}

		// set title
		setTitle("volumeStats.title");

		// TODO: have get params that determine what is shown on page, note that
		// the jsp will need a switch on the markup that does ajax request for
		// chart data

		// TEST ONLY, KILLING CONNECTION TO RE_INSTANTIATE WITH ALTERNATE DB
		// DBManager.flushConnection();
		// final Connection connection = DBManager.getConnection(null, null,
		// null, "pds");

		// get database connection
		final Connection connection = DBManager.getConnection();

		// get an instance of locale utils to do number formatting
		final LocaleUtils lu = getUIManager().getLocaleUtils();

		// create reusable instances of query strings, statements and results
		// for getting stats
		String query = null;
		Statement queryStmt = null;
		ResultSet rs = null;

		// Add General Statistics
		query = "SELECT num_validations, num_volumes, max_size, max_size_id, min_size, min_size_id, avg_size, max_duration, max_duration_id, min_duration, min_duration_id, avg_duration FROM (SELECT FLOOR(AVG(volume_size)) AS avg_size FROM volume_stats) avg_size_table, (SELECT volume_size as max_size, volume_id as max_size_id from volume_stats WHERE volume_size in (SELECT MAX(volume_size) from volume_stats) limit 1) max_size_table, (SELECT volume_size as min_size, volume_id as min_size_id from volume_stats WHERE volume_size in (SELECT MIN(volume_size) from volume_stats) limit 1) min_size_table, (SELECT duration as max_duration, volume_id as max_duration_id from volume_stats WHERE duration in (SELECT MAX(duration) from volume_stats) limit 1) max_duration_table, (SELECT duration as min_duration, volume_id as min_duration_id from volume_stats WHERE duration in (SELECT MIN(duration) from volume_stats) limit 1) min_duration_table, (SELECT FLOOR(AVG(duration)) AS avg_duration from volume_stats) avg_duration_table, (SELECT COUNT(*) AS num_validations FROM volume_stats) num_validations_table, (SELECT COUNT(DISTINCT volume_id) AS num_volumes FROM volume_stats) num_volumes_table;";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);

		// this only has one row so just advance to first record and get summary
		// data
		rs.next();
		final long totalVolumesRaw = rs.getLong("num_volumes");
		this.totalVolumes = lu.getNumber(totalVolumesRaw);
		final long totalValidationsRaw = rs.getLong("num_validations");
		this.totalValidations = lu.getNumber(totalValidationsRaw);
		final long maxSizeRaw = rs.getLong("max_size");
		this.maxNumFiles = lu.getNumber(maxSizeRaw);
		this.maxNumFilesId = rs.getString("max_size_id");
		final long minSizeRaw = rs.getLong("min_size");
		this.minNumFiles = lu.getNumber(minSizeRaw);
		this.minNumFilesId = rs.getString("min_size_id");
		final long avgSizeRaw = rs.getLong("avg_size");
		this.avgNumFiles = lu.getNumber(avgSizeRaw);
		final long maxDurationRaw = rs.getLong("max_duration");
		this.maxDuration = DateUtils.getMillisecondsToDuration(maxDurationRaw);
		this.maxDurationId = rs.getString("max_duration_id");
		final long minDurationRaw = rs.getLong("min_duration");
		this.minDuration = DateUtils.getMillisecondsToDuration(minDurationRaw);
		this.minDurationId = rs.getString("min_duration_id");
		final long avgDurationRaw = rs.getLong("avg_duration");
		this.avgDuration = DateUtils.getMillisecondsToDuration(avgDurationRaw);

		// Add Bounce Rates
		query = "SELECT COUNT(volume_id) AS bounces, TRUNCATE(count(volume_id) * 100 / (SELECT COUNT(distinct volume_id) from volume_stats), 2) AS bounce_percent FROM (SELECT volume_id, COUNT(volume_id) AS total FROM volume_stats GROUP BY volume_id) validations WHERE total = 1;";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);
		rs.next();
		final long bouncesRaw = rs.getLong("bounces");
		this.bounces = lu.getNumber(bouncesRaw);
		this.bouncePercent = rs.getDouble("bounce_percent");

		// Add Unique IPs

		// Add General Volume Statistics
		final RowContainer genVolStatsHeader = new RowContainer("Volume ID",
				"Date", "Size", "Num Errors", "Types of Errors", "Duration");
		this.generalVolStats = new TableContainer(
				"General Volume Stats (Last Validation By Volume)",
				genVolStatsHeader);
		// query =
		// "SELECT stats.volume_id, stats.date_validated, stats.duration, stats.volume_size, SUM(errors.num_found) AS num_found FROM volume_stats stats INNER JOIN volume_errors errors ON stats.volume_stat_id = errors.volume_stat_id WHERE stats.date_validated = (SELECT MAX(stats2.date_validated) FROM volume_stats stats2 WHERE stats2.volume_id = stats.volume_id) GROUP BY stats.volume_id ORDER BY stats.duration DESC;";
		query = "SELECT stats.volume_id, stats.date_validated, stats.duration, stats.volume_size, SUM(errors.num_found) AS num_found, COUNT(DISTINCT errors.error_type) AS num_error_type FROM volume_stats stats INNER JOIN volume_errors errors ON stats.volume_stat_id = errors.volume_stat_id WHERE stats.date_validated = (SELECT MAX(stats2.date_validated) FROM volume_stats stats2 WHERE stats2.volume_id = stats.volume_id) GROUP BY stats.volume_id ORDER BY num_found DESC";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);

		while (rs.next()) {
			final String volId = rs.getString("volume_id");
			final String date = rs.getString("date_validated");
			final long size = rs.getLong("volume_size");
			final String sizeString = lu.getNumber(size);
			final long numFound = rs.getLong("num_found");
			final String numFoundString = lu.getNumber(numFound);
			final long duration = rs.getLong("duration");
			final String durationString = DateUtils
					.getMillisecondsToDuration(duration);
			final long numErrorTypesRaw = rs.getLong("num_error_type");
			final String numErrorTypes = lu.getNumber(numErrorTypesRaw);
			final RowContainer row = new RowContainer(volId, date, sizeString,
					numFoundString, numErrorTypes, durationString);

			this.generalVolStats.addRow(row);
		}

		// Add Improvement Statistics
		final RowContainer improvementHeader = new RowContainer("Volume ID",
				"Num Validations", "Max Errors", "Min Errors", "Error Diff",
				"Error % Diff", "Max Error Types", "Min Error Types",
				"Error Type Diff", "Error Type % Diff");
		this.improvement = new TableContainer("Improvement By Volume",
				improvementHeader);
		// query =
		// "SELECT stats.volume_id, COUNT(stats.volume_id) AS num_validations, (MAX(errors.sum_num_found) - MIN(errors.sum_num_found)) AS diff FROM volume_stats stats INNER JOIN (SELECT SUM(num_found) AS sum_num_found, volume_stat_id FROM volume_errors GROUP BY volume_stat_id) errors ON stats.volume_stat_id = errors.volume_stat_id GROUP BY stats.volume_id HAVING num_validations > 1 ORDER BY diff DESC";
		query = "SELECT stats.volume_id, COUNT(stats.volume_id) AS validations, MAX(errors.sum_num_found) AS max_errors, MIN(errors.sum_num_found) AS min_errors, (MAX(errors.sum_num_found) - MIN(errors.sum_num_found)) AS error_diff, TRUNCATE((MAX(errors.sum_num_found) - MIN(errors.sum_num_found)) * 100 / MAX(errors.sum_num_found), 2) AS percent_error_diff, MAX(errors.num_error_type) AS max_error_type, MIN(errors.num_error_type) AS min_error_type, (MAX(errors.num_error_type) - MIN(errors.num_error_type)) AS type_diff, TRUNCATE((MAX(errors.num_error_type) - MIN(errors.num_error_type)) * 100 / MAX(errors.num_error_type), 2) AS percent_type_diff FROM volume_stats stats INNER JOIN (SELECT SUM(num_found) AS sum_num_found, COUNT(DISTINCT error_type) AS num_error_type, volume_stat_id FROM volume_errors GROUP BY volume_stat_id) errors ON stats.volume_stat_id = errors.volume_stat_id GROUP BY stats.volume_id ORDER BY percent_error_diff DESC";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);

		while (rs.next()) {
			final String volId = rs.getString("volume_id");
			final long numValidationsRaw = rs.getLong("validations");
			final String numValidations = lu.getNumber(numValidationsRaw);
			final long maxErrorsRaw = rs.getLong("max_errors");
			final String maxErrors = lu.getNumber(maxErrorsRaw);
			final long minErrorsRaw = rs.getLong("min_errors");
			final String minErrors = lu.getNumber(minErrorsRaw);
			final long diffRaw = rs.getLong("error_diff");
			final String diff = lu.getNumber(diffRaw);
			final double percentErrorDiff = rs.getDouble("percent_error_diff");
			final long maxErrorTypeRaw = rs.getLong("max_error_type");
			final String maxErrorType = lu.getNumber(maxErrorTypeRaw);
			final long minErrorTypeRaw = rs.getLong("min_error_type");
			final String minErrorType = lu.getNumber(minErrorTypeRaw);
			final long typeDiffRaw = rs.getLong("type_diff");
			final String typeDiff = lu.getNumber(typeDiffRaw);
			final double typeDiffPercent = rs.getDouble("percent_type_diff");
			final RowContainer row = new RowContainer(volId, numValidations,
					maxErrors, minErrors, diff, percentErrorDiff, maxErrorType,
					minErrorType, typeDiff, typeDiffPercent);
			this.improvement.addRow(row);
		}

		// Add Improvement Statistics
		final RowContainer errorstatsHeader = new RowContainer("Error Type",
				"Num Errors", "In Validations", "Validation %", "In Volumes",
				"Volume %");
		this.errorStats = new TableContainer(
				"Error Statistics By Validation and Unique Volumes",
				errorstatsHeader);
		query = "SELECT volume_errors.error_type, SUM(num_found) AS sum, COUNT(volume_stats.volume_stat_id) as in_validations, TRUNCATE(COUNT(volume_stats.volume_stat_id) * 100 / (SELECT COUNT(DISTINCT volume_stat_id) FROM volume_errors), 2) AS percent_validations, COUNT(DISTINCT volume_stats.volume_id) as in_vols, TRUNCATE(COUNT(DISTINCT volume_stats.volume_id) * 100 / (SELECT COUNT(DISTINCT volume_id) FROM volume_stats), 2) AS percent_vols FROM volume_errors INNER JOIN volume_stats ON volume_stats.volume_stat_id = volume_errors.volume_stat_id GROUP BY error_type ORDER BY percent_vols DESC";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);

		while (rs.next()) {
			final String errorType = rs.getString("error_type");
			final long numErrorsRaw = rs.getLong("sum");
			final String numErrors = lu.getNumber(numErrorsRaw);
			final long inValidationsRaw = rs.getLong("in_validations");
			final String inValidations = lu.getNumber(inValidationsRaw);
			final String percentValidations = rs
					.getString("percent_validations");
			final long inVolsRaw = rs.getLong("in_vols");
			final String inVols = lu.getNumber(inVolsRaw);
			final String percentVols = rs.getString("percent_vols");

			final RowContainer row = new RowContainer(errorType, numErrors,
					inValidations, percentValidations, inVols, percentVols);
			this.errorStats.addRow(row);
		}

		// Add Error Specific Improvement Statistics
		final RowContainer errorImprovementHeader = new RowContainer(
				"Error Type", "Max", "Min", "Diff", "Percent Diff");
		this.errorImprovementStats = new TableContainer(
				"Improvement By Error Type", errorImprovementHeader);
		query = "SELECT SUM(max_found) as max, SUM(min_found) as min, SUM(max_found) - SUM(min_found) as diff, TRUNCATE((SUM(max_found) - SUM(min_found)) * 100 / sum(max_found), 2) as percent_fixed, error_type FROM (SELECT volume_id, error_type, MAX(num_found) as max_found, MIN(num_found) as min_found FROM (SELECT vstats.volume_id, types.error_type, ifnull(errors.num_found,0) AS num_found FROM (SELECT DISTINCT error_type FROM volume_errors) types INNER JOIN volume_stats vstats LEFT OUTER JOIN volume_errors errors ON vstats.volume_stat_id = errors.volume_stat_id AND types.error_type = errors.error_type) vstats GROUP BY volume_id, error_type) type_rollup GROUP BY error_type ORDER BY percent_fixed DESC;";
		queryStmt = connection.createStatement();
		rs = queryStmt.executeQuery(query);
		while (rs.next()) {
			final String errorType = rs.getString("error_type");
			final long maxRaw = rs.getLong("max");
			final String max = lu.getNumber(maxRaw);
			final long minRaw = rs.getLong("min");
			final String min = lu.getNumber(minRaw);
			final long diffRaw = rs.getLong("diff");
			final String diff = lu.getNumber(diffRaw);
			final String percentDiff = rs.getString("percent_fixed");

			final RowContainer row = new RowContainer(errorType, max, min,
					diff, percentDiff);
			this.errorImprovementStats.addRow(row);
		}

		connection.close();

		return SUCCESS;
	}

}
