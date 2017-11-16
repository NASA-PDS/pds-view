package gov.nasa.pds.tracking.tracking.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class ArchiveStatus extends DBConnector {

	public static Logger logger = Logger.getLogger(ArchiveStatus.class);

	private static final String TABLENAME = "archive_status";
	private static final String PRODUCTTABLENAME= "product";

	public static final String LOGIDENTIFIERCOLUMN = "logical_identifier";
	public static final String VERSIONCOLUMN = "version_id";
	public static final String DATECOLUMN = "status_date_time";
	public static final String STATUSCOLUMN = "status";
	public static final String EMAILCOLUMN = "electronic_mail_address";
	public static final String COMMENTCOLUMN = "comment";

	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement prepareStm = null;
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
			connect = getConnection();

			statement = connect.createStatement();

			logger.debug("select a.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " a" + " where p."
					+ LOGIDENTIFIERCOLUMN + " = " + "a." + LOGIDENTIFIERCOLUMN + " order by a." + VERSIONCOLUMN);
			resultSet = statement
					.executeQuery("select a.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " a" + " where p."
							+ LOGIDENTIFIERCOLUMN + " = " + "a." + LOGIDENTIFIERCOLUMN + " order by a." + VERSIONCOLUMN);

			while (resultSet.next()) {
				archStatus = new ArchiveStatus();

				archStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				archStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				archStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				archStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				archStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				archStatus.setDate(resultSet.getString(DATECOLUMN));

				archStatuses.add(archStatus);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
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
			connect = getConnection();

			statement = connect.createStatement();

			logger.debug("select a.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " a" + " where p."
					+ LOGIDENTIFIERCOLUMN + " = " + "a." + LOGIDENTIFIERCOLUMN + " and p." + Product.TITLECOLUMN + " = '"
					+ title + "' order by a." + VERSIONCOLUMN);
			resultSet = statement.executeQuery("select a.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " a"
					+ " where p." + LOGIDENTIFIERCOLUMN + " = " + "a." + LOGIDENTIFIERCOLUMN + " and p."
					+ Product.TITLECOLUMN + " = '" + title + "' order by a." + VERSIONCOLUMN);

			while (resultSet.next()) {
				archStatus = new ArchiveStatus();

				archStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				archStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				archStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				archStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				archStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				archStatus.setDate(resultSet.getString(DATECOLUMN));

				archStatuses.add(archStatus);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return archStatuses;
		}
	}
	
	/**
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public ArchiveStatus getLatestArchiveStatus(String logical_identifier, String ver) {

		ArchiveStatus archStatus = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOGIDENTIFIERCOLUMN + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUMN + " = '" + ver + "' order by " + DATECOLUMN + " DESC");

			if (resultSet.next()){
				archStatus = new ArchiveStatus();

				archStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				archStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				archStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				archStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				archStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				archStatus.setDate(resultSet.getString(DATECOLUMN));
			}	
			else{
				logger.info("Can not find any Archive Status!");
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return archStatus;
		}
	}
	
	/**
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public List<ArchiveStatus> getArchiveStatusList(String logical_identifier, String ver) {
		
		List<ArchiveStatus> archStatuses = new ArrayList<ArchiveStatus>();
		ArchiveStatus archStatus = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOGIDENTIFIERCOLUMN + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUMN + " = '" + ver + "' order by " + DATECOLUMN);

			while (resultSet.next()){
				archStatus = new ArchiveStatus();

				archStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				archStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				archStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				archStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				archStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				archStatus.setDate(resultSet.getString(DATECOLUMN));
				
				archStatuses.add(archStatus);
			}	

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return archStatuses;
		}
	}
	
	/**
	 * @param logical_identifier
	 * @param version
	 * @param date
	 * @param status
	 * @param mail
	 * @param comment
	 */
	public void insertArchiveStatus(String logical_identifier, String version, String date, String status, String email, String comment) {
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" 
													+ LOGIDENTIFIERCOLUMN + ", " 
													+ VERSIONCOLUMN + ", "
													+ DATECOLUMN + ", "
													+ STATUSCOLUMN + ", "
													+ EMAILCOLUMN + ", "
													+ COMMENTCOLUMN + ") VALUES (?, ?, ?, ?, ?, ?)");
			prepareStm.setString(1, logical_identifier);
			prepareStm.setString(2, version);
			prepareStm.setString(3, date);
			prepareStm.setString(4, status);
			prepareStm.setString(5, email);
			prepareStm.setString(6, comment);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The archive status " + status + " for the product: " + logical_identifier + ", has been added.");
			
		} catch (Exception e) {
			logger.error(e);
			if (connect != null) {
	            try {
	            	logger.error("Transaction is being rolled back");
	                connect.rollback();
	            } catch(SQLException excep) {
	            	logger.error(excep);
	            }
	        }
	    } finally {
	        close(prepareStm);
	    }
	}
	
	/**
	 * @param stm
	 */
	private void close(Statement stm) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (stm != null) {
				stm.close();
			}

			if (connect != null) {
				connect.setAutoCommit(true);
				connect.close();				
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
