package gov.nasa.pds.tracking.tracking.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ArchiveStatus extends DBConnector {

	public static Logger logger = Logger.getLogger(ArchiveStatus.class);

	private static final String tableName = "archive_status";
	private static String productTableName = "product";

	public static final String logIdentifierColume = "logical_identifier";
	public static final String versionColume = "version_id";
	public static final String dateColume = "status_date_time";
	public static final String statusColume = "status";
	public static final String emailColume = "electronic_mail_address";
	public static final String commentColume = "comment";

	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;

	private String logIdentifier = null;
	private String version = null;
	private String date = null;
	private String status = null;
	private String email = null;
	private String comment = null;

	/**
	 * @return the logIdentifier
	 */
	public String getLogIdentifier() {
		return logIdentifier;
	}

	/**
	 * @param logIdentifier
	 *            the logIdentifier to set
	 */
	public void setLogIdentifier(String logIdentifier) {
		this.logIdentifier = logIdentifier;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	public ArchiveStatus() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	@SuppressWarnings("finally")
	public List<ArchiveStatus> getArchiveStatusOrderByVersion() {

		List<ArchiveStatus> archStatuses = new ArrayList<ArchiveStatus>();
		ArchiveStatus archStatus = null;
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);

			statement = connect.createStatement();

			System.out.println("select * from " + productTableName + " p, " + tableName + " a" + " where p."
					+ logIdentifierColume + " = " + "a." + logIdentifierColume + " order by " + versionColume);
			resultSet = statement
					.executeQuery("select * from " + productTableName + " p, " + tableName + " a" + " where p."
							+ logIdentifierColume + " = " + "a." + logIdentifierColume + " order by " + versionColume);

			while (resultSet.next()) {
				archStatus = new ArchiveStatus();

				archStatus.setLogIdentifier(resultSet.getString(logIdentifierColume));
				archStatus.setVersion(resultSet.getString(versionColume));
				archStatus.setStatus(resultSet.getString(statusColume));
				archStatus.setEmail(resultSet.getString(emailColume));
				archStatus.setComment(resultSet.getString(commentColume));
				archStatus.setDate(resultSet.getString(dateColume));

				archStatuses.add(archStatus);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close();
			return archStatuses;
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("finally")
	public List<ArchiveStatus> getArchiveStatusOrderByVersion(String title) {

		List<ArchiveStatus> archStatuses = new ArrayList<ArchiveStatus>();
		ArchiveStatus archStatus = null;
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);

			statement = connect.createStatement();

			System.out.println("select * from " + productTableName + " p, " + tableName + " a" + " where p."
					+ logIdentifierColume + " = " + "a." + logIdentifierColume + " and p." + Product.TITLECOLUME + " = "
					+ title + " order by " + versionColume);
			resultSet = statement.executeQuery("select * from " + productTableName + " p, " + tableName + " a"
					+ " where p." + logIdentifierColume + " = " + "a." + logIdentifierColume + " and p."
					+ Product.TITLECOLUME + " = " + title + " order by " + versionColume);

			while (resultSet.next()) {
				archStatus = new ArchiveStatus();

				archStatus.setLogIdentifier(resultSet.getString(logIdentifierColume));
				archStatus.setVersion(resultSet.getString(versionColume));
				archStatus.setStatus(resultSet.getString(statusColume));
				archStatus.setEmail(resultSet.getString(emailColume));
				archStatus.setComment(resultSet.getString(commentColume));
				archStatus.setDate(resultSet.getString(dateColume));

				archStatuses.add(archStatus);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close();
			return archStatuses;
		}
	}

	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}
}
