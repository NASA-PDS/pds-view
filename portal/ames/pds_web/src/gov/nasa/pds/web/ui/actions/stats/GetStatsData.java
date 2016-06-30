package gov.nasa.pds.web.ui.actions.stats;

import gov.nasa.pds.web.ui.actions.BaseJSONAction;
import gov.nasa.pds.web.ui.containers.GoogleAnnotatedTimeline;
import gov.nasa.pds.web.ui.containers.GoogleVisRow;
import gov.nasa.pds.web.ui.managers.DBManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Action for getting validation statistics data
 * 
 * @author jagander
 */
public class GetStatsData extends BaseJSONAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Date formatter for dates related to validatoins
	 */
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(
			"yyyy.MM.dd"); //$NON-NLS-1$

	/**
	 * Rows of found data, wrapped in containers appropriate to google
	 * visualizations used
	 */
	final List<GoogleVisRow> rows = new ArrayList<GoogleVisRow>();

	/**
	 * Main method of action
	 */
	@SuppressWarnings( { "nls", "null" })
	@Override
	protected void executeInner() throws Exception {

		// generate GoogleAnnotatedTimeline container
		final GoogleAnnotatedTimeline timeline = new GoogleAnnotatedTimeline(
				"0");

		// set X axis of timeline
		timeline.setXAxis("Date", "date");

		// set Y axis of timeline
		timeline.setYAxis("Number Validated", "number");

		// create query string to get number of validations per day with
		// easy-to-use date values, date string must be of form
		// "Date(year, month, day)"
		final String query = "SELECT COUNT(*) AS num_validated, YEAR(date_validated) AS year, MONTH(date_validated) AS month, DAY(date_validated) AS day FROM volume_stats GROUP BY DATE(date_validated)"; //$NON-NLS-1$

		// get db connection
		final Connection connection = DBManager.getConnection();

		// create query from query string
		Statement queryStmt = connection.createStatement();

		// get results
		ResultSet rs = queryStmt.executeQuery(query);

		// init earliest date as null
		Calendar minDate = null;

		// init latest date as today
		Calendar maxDate = Calendar.getInstance();
		maxDate.set(Calendar.HOUR, 0);
		maxDate.set(Calendar.MINUTE, 0);
		maxDate.set(Calendar.SECOND, 0);
		// maxDate.add(Calendar.DAY_OF_YEAR, +1);

		// create a collection for days that had validations, used to create
		// filler values on empty days
		final List<String> foundDates = new ArrayList<String>();

		// add row data
		while (rs.next()) {
			// get number of validations on day
			final int num = rs.getInt("num_validated");

			// get date data
			final int year = rs.getInt("year");
			final int month = rs.getInt("month") - 1;
			final int day = rs.getInt("day");

			// convert date data to usable format
			final Calendar curDate = Calendar.getInstance();
			curDate.set(year, month, day, 0, 0, 0);
			foundDates.add(FORMATTER.format(curDate.getTime()));

			// update min date
			if (minDate == null) {
				minDate = curDate;
			} else if (minDate.after(curDate)) {
				minDate = curDate;
			}

			// convert to json safe date string used by google visualization
			final String dateString = toJSONDate(year, month, day);

			// create a new row container for data and add to rows
			this.rows.add(new GoogleVisRow(dateString, num));
		}

		// fill out 0 values for dates with no entries starting at earliest
		// known date
		Calendar testDate = (Calendar) minDate.clone();

		// check that we haven't passed today
		while (testDate.before(maxDate)) {

			// increment test date
			testDate.add(Calendar.DAY_OF_YEAR, +1);

			// convert date to appropriate format
			final String dateString = FORMATTER.format(testDate.getTime());

			// see if date is in found dates collection, if not, add a new empty
			// row
			if (!foundDates.contains(dateString)) {

				// create the json date string the google visualization needs
				final String jsonString = toJSONDate(testDate
						.get(Calendar.YEAR), testDate.get(Calendar.MONTH),
						testDate.get(Calendar.DAY_OF_MONTH));

				// add row with zero validations
				this.rows.add(new GoogleVisRow(jsonString, 0));
			}
		}

		// add releases to timeline data
		setEvent(2010, 8, 24, "Version 1.0.10");
		setEvent(2010, 7, 15, "Version 1.0.9");
		setEvent(2010, 6, 28, "Version 1.0.8");
		setEvent(2010, 6, 25, "Version 1.0.7");
		setEvent(2010, 6, 11, "Version 1.0.6");
		setEvent(2010, 4, 6, "Version 1.0.5");
		setEvent(2010, 3, 19, "Version 1.0.4");
		setEvent(2010, 3, 15, "Version 1.0.3");
		setEvent(2010, 2, 24, "Version 1.0.2");
		setEvent(2010, 2, 8, "Version 1.0.1");
		setEvent(2009, 9, 21, "Version 1.0.0");
		setEvent(2009, 8, 17, "Version 0.2.04");
		setEvent(2009, 7, 31, "Version 0.2.03");
		setEvent(2009, 7, 6, "Version 0.2.02");
		setEvent(2009, 5, 25, "Version 0.2.01");
		setEvent(2009, 3, 16, "Version 0.2.00");
		// below version is before gathering data so not pertinent
		// setEvent(2008, 11, 29, "Version 0.1.00");

		// add rows to timeline
		timeline.addLine("Unfiltered", this.rows); //$NON-NLS-1$

		// convert to JSON object
		this.jsonContainer = timeline.toJSONObj();

		connection.close();
	}

	/**
	 * Convert date params to a javascript date string
	 * 
	 * @param year
	 *            year of the date
	 * @param month
	 *            month of year
	 * @param day
	 *            day of month
	 * 
	 * @return javascript day string
	 */
	@SuppressWarnings("nls")
	private String toJSONDate(final int year, final int month, final int day) {
		return "Date(" + year + ", " + month + ", " + day + ")";
	}

	/**
	 * Add an event to the timeline. NOTE: this must be called after creating
	 * filler events or it may not find an object to attach the event to.
	 * 
	 * @param year
	 *            year event occurred
	 * @param month
	 *            month event occurred
	 * @param day
	 *            day event occurred
	 * @param event
	 *            event string to display on timeline
	 */
	private void setEvent(final int year, final int month, final int day,
			final String event) {
		// subtract 1 from month since javascript Date uses 0-11 index, day is
		// 1-31
		final String dateString = toJSONDate(year, month - 1, day);

		// find row to associate event with
		for (final GoogleVisRow row : this.rows) {

			// if row is the same date, set the event and exit
			if (row.getXVal().contains(dateString)) {
				row.setEvent(event);
				return;
			}
		}
	}

	/**
	 * Custom renderer that packages the json output into a google visualization
	 * construct.
	 * 
	 * @return json rendering of data
	 */
	@SuppressWarnings("nls")
	@Override
	protected void renderJsonString() {
		this.json = "google.visualization.Query.setResponse("
				+ this.jsonContainer.toString() + ");";
	}

}
