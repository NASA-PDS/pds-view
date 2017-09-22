/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.db;

import java.sql.Connection;
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
public class SubmissionStatus extends DBConnector {
	
	public static Logger logger = Logger.getLogger(SubmissionStatus.class);

	private static String TABLENAME  = "submission_status";
	
	private static String DEL_IDENTIFIERCOLUME = "delivery_identifier";
	private static String SUBMISSIONDATECOLUME = "submission_date_time";
	private static String STATUSDATECOLUME = "status_date_time";
	private static String STATUSCOLUME = "status";
	private static String EMAILCOLUME = "electronic_mail_address";
	private static String COMMENTCOLUME = "comment";
	
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement prepareStm = null;
	private ResultSet resultSet = null;
	
	private int del_identifier = 0;
	private String submissionDate = null;
	private String statusDate = null;
	private String status = null;
	private String email = null;
	private String comment = null;

	/**
	 * @return the del_identifier
	 */
	public int getDel_identifier() {
		return del_identifier;
	}

	/**
	 * @param del_identifier, the del_identifier to set
	 */
	public void setDel_identifier(int del_identifier) {
		this.del_identifier = del_identifier;
	}

	/**
	 * @return the submissionDate
	 */
	public String getSubmissionDate() {
		return submissionDate;
	}

	/**
	 * @param submissionDate, the submissionDate to set
	 */
	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}

	/**
	 * @return the statusDate
	 */
	public String getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statusDate, the statusDate to set
	 */
	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status, the status to set
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
	 * @param email, the email to set
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
	 * @param comment, the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public SubmissionStatus() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return a list of submission status objects for the delivery.
	 */
	@SuppressWarnings("finally")
	public List<SubmissionStatus> getDeliveryStatus(String identifier) {

		List<SubmissionStatus> statuses = new ArrayList<SubmissionStatus>();
		SubmissionStatus status = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			/*System.out.println("select * from " + TABLENAME
											+ " where " + DEL_IDENTIFIERCOLUME + " = '" + identifier
											+ "' order by " + STATUSDATECOLUME);*/
			resultSet = statement.executeQuery("select * from " + TABLENAME
											+ " where " + DEL_IDENTIFIERCOLUME + " = '" + identifier
											+ "' order by " + STATUSDATECOLUME);

			while (resultSet.next()) {
				status = new SubmissionStatus();

				status.setDel_identifier(resultSet.getInt(DEL_IDENTIFIERCOLUME));
				status.setSubmissionDate(resultSet.getString(SUBMISSIONDATECOLUME));
				status.setStatusDate(resultSet.getString(STATUSDATECOLUME));
				status.setStatus(resultSet.getString(STATUSCOLUME));
				status.setEmail(resultSet.getString(EMAILCOLUME));
				status.setComment(resultSet.getString(COMMENTCOLUME));
				
				statuses.add(status);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return statuses;
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

	/**
	 * @param deliveryIdentifier
	 * @param subDateTime
	 * @param statusDateTime
	 * @param status
	 * @param email
	 * @param comment
	 */
	public void insertSubmissionStatus(int deliveryIdentifier, String subDateTime, String statusDateTime,
			String status, String email, String comment) {
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" + DEL_IDENTIFIERCOLUME + ", " 
																					+ SUBMISSIONDATECOLUME + ", "
																					+ STATUSDATECOLUME + ", "
																					+ STATUSCOLUME + ", "
																					+ EMAILCOLUME + ", "
																					+ COMMENTCOLUME
																					+ ") VALUES (?, ?, ?, ?, ?, ?)");
			prepareStm.setInt(1, deliveryIdentifier);
			prepareStm.setString(2, subDateTime);
			prepareStm.setString(3, statusDateTime);
			prepareStm.setString(4, status);
			prepareStm.setString(5, email);
			prepareStm.setString(6, comment);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("Submission Status for " + deliveryIdentifier + " has been added.");
			
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
	 * @param deliveryIdentifier
	 * @param subDateTime
	 * @param statusDateTime
	 * @param status
	 * @param email
	 * @param comment
	 */
	public void updateSubmissionStatus(int deliveryIdentifier, String subDateTime, String statusDateTime,
			String status, String email, String comment) {
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " 
																		+ STATUSDATECOLUME + " = ?, "
																		+ STATUSCOLUME + " = ?, "
																		+ EMAILCOLUME + " = ?, "
																		+ COMMENTCOLUME + " = ? "
														+ "WHERE " + DEL_IDENTIFIERCOLUME + " = ? "
														+ "AND " + SUBMISSIONDATECOLUME + " = ?");
			
			
			prepareStm.setString(1, statusDateTime);
			prepareStm.setString(2, status);
			prepareStm.setString(3, email);
			prepareStm.setString(4, comment);
			prepareStm.setInt(5, deliveryIdentifier);
			prepareStm.setString(6, subDateTime);
			
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("Submission Status for " + deliveryIdentifier + " has been updated.");
			
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
}
