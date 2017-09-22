/**
 * Copyright 2010-2017, by the California Institute of Technology.
 * 
 * The object class represents archive_status, certification_status or nssdca_status table.
 *
 */
package gov.nasa.pds.tracking.tracking.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class Status extends DBConnector {

	public static Logger logger = Logger.getLogger(Status.class);

	private String tableName = null; // archive_status, certification_status or
										// nssdca_status
	private final static String PRODUCTTABLENAME  = "product";

	private final static String LOGIDENTIFIERCOLUME = "logical_identifier";
	private final static  String VERSIONCOLUME = "version_id";
	private final static  String DATECOLUME = "status_date_time";
	private String statusColume = null;
	private final static  String EMAILCOLUME = "electronic_mail_address";
	private final static  String COMMENTCOLUME = "comment";

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
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the statusColume
	 */
	public String getStatusColume() {
		return statusColume;
	}

	/**
	 * @param statusColume
	 *            the statusColume to set
	 */
	public void setStatusColume(String statusColume) {
		this.statusColume = statusColume;
	}

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

	public Status() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}

	public Status(String tblName) throws ClassNotFoundException, SQLException {
		setTableName(tblName);
		if (tableName.equals(NSSDCA_STATUS_TABLE_NAME)) {
			setStatusColume("nssdca_identifier");
		} else {
			setStatusColume("status");
		}
	}

	/**
	 * @return a list of all Status objects with the title.
	 */
	@SuppressWarnings("finally")
	public List<Status> getStatusOrderByVersion() {

		List<Status> statuses = new ArrayList<Status>();
		Status status = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			/*System.out.println("select * from " + PRODUCTTABLENAME + " p, " + tableName + " a" + " where p."
					+ LOGIDENTIFIERCOLUME + " = " + "a." + LOGIDENTIFIERCOLUME + " order by " + VERSIONCOLUME);*/
			resultSet = statement
					.executeQuery("select * from " + PRODUCTTABLENAME + " p, " + tableName + " a" + " where p."
							+ LOGIDENTIFIERCOLUME + " = " + "a." + LOGIDENTIFIERCOLUME + " order by " + VERSIONCOLUME);

			while (resultSet.next()) {
				status = new Status();

				status.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUME));
				status.setVersion(resultSet.getString(VERSIONCOLUME));
				status.setStatus(resultSet.getString(statusColume));
				status.setEmail(resultSet.getString(EMAILCOLUME));
				status.setComment(resultSet.getString(COMMENTCOLUME));
				status.setDate(resultSet.getString(DATECOLUME));

				statuses.add(status);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close();
			return statuses;
		}
	}

	/**
	 * @return a list of Status objects with the title.
	 */
	@SuppressWarnings("finally")
	public List<Status> getStatusOrderByVersion(String title) {

		List<Status> statuses = new ArrayList<Status>();
		Status status = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			/*System.out.println("select * from " + PRODUCTTABLENAME + " p, " + tableName + " a" + " where p."
					+ LOGIDENTIFIERCOLUME + " = " + "a." + LOGIDENTIFIERCOLUME + " and p." + Product.titleColume + " = "
					+ title + " order by " + VERSIONCOLUME);*/
			resultSet = statement.executeQuery("select * from " + PRODUCTTABLENAME + " p, " + tableName + " a"
					+ " where p." + LOGIDENTIFIERCOLUME + " = " + "a." + LOGIDENTIFIERCOLUME + " and p."
					+ Product.TITLECOLUME + " = " + title + " order by " + VERSIONCOLUME);

			while (resultSet.next()) {
				status = new Status();

				status.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUME));
				status.setVersion(resultSet.getString(VERSIONCOLUME));
				status.setStatus(resultSet.getString(statusColume));
				status.setEmail(resultSet.getString(EMAILCOLUME));
				status.setComment(resultSet.getString(COMMENTCOLUME));
				status.setDate(resultSet.getString(DATECOLUME));

				statuses.add(status);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close();
			return statuses;
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
