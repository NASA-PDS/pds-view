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
public class SubmissionAndStatusDao extends DBConnector {
	
	public static Logger logger = Logger.getLogger(SubmissionAndStatusDao.class);

	private static String TABLENAME  = "submission";
	private static String STATUSTABLENAME  = "submission_status";
	
	public static String DEL_IDENTIFIERCOLUME = "delivery_identifier";
	public static String SUBMISSIONDATECOLUME = "submission_date_time";
	public static final String STATUSDATECOLUME = "status_date_time";
	public static final String STATUSCOLUME = "status";
	public static final String EMAILCOLUME = "electronic_mail_address";
	public static final String COMMENTCOLUME = "comment";

	private ResultSet resultSet = null;

	
	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public SubmissionAndStatusDao() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("finally")
	private SubmissionAndStatus getSubmissionsAndStatus(int del_identifier, String submissionDate, String statusDate) {
		
		SubmissionAndStatus status = null;
		
		Connection connect = null;
		Statement statement = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			resultSet = statement.executeQuery("select * from " + STATUSTABLENAME
												+ " WHERE " + DEL_IDENTIFIERCOLUME + " = '" + del_identifier
												+ "' AND " + SUBMISSIONDATECOLUME  + " = '" +  submissionDate
												+ "' AND " + STATUSDATECOLUME  + " = '" +  statusDate + "'");

			while (resultSet.next()) {
				status = new SubmissionAndStatus();

				status.setDel_identifier(resultSet.getInt(DEL_IDENTIFIERCOLUME));
				status.setSubmissionDate(resultSet.getString(SUBMISSIONDATECOLUME));
				status.setStatusDate(resultSet.getString(STATUSDATECOLUME));
				status.setStatus(resultSet.getString(STATUSCOLUME));
				status.setEmail(resultSet.getString(EMAILCOLUME));
				status.setComment(resultSet.getString(COMMENTCOLUME));
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return status;
		}		
	}
	@SuppressWarnings("finally")
	public List<SubmissionAndStatus> getSubmissionsAndStatusList() {

		List<SubmissionAndStatus> statuses = new ArrayList<SubmissionAndStatus>();
		SubmissionAndStatus status;
		
		Connection connect = null;
		Statement statement = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			/*System.out.println("select * from " + STATUSTABLENAME
											+ " order by " + STATUSDATECOLUME);*/
			resultSet = statement.executeQuery("select * from " + STATUSTABLENAME
											+ " order by " + STATUSDATECOLUME);

			while (resultSet.next()) {
				status = new SubmissionAndStatus();

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
			close(connect, statement);
			return statuses;
		}
	}
	
	@SuppressWarnings("finally")
	public List<SubmissionAndStatus> getDeliveryStatus(String identifier) {

		List<SubmissionAndStatus> statuses = new ArrayList<SubmissionAndStatus>();
		SubmissionAndStatus status = null;
		
		Connection connect = null;
		Statement statement= null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			/*System.out.println("select * from " + STATUSTABLENAME
											+ " where " + DEL_IDENTIFIERCOLUME + " = '" + identifier
											+ "' order by " + STATUSDATECOLUME);*/
			resultSet = statement.executeQuery("select * from " + STATUSTABLENAME
											+ " where " + DEL_IDENTIFIERCOLUME + " = '" + identifier
											+ "' order by " + STATUSDATECOLUME);

			while (resultSet.next()) {
				status = new SubmissionAndStatus();

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
			close(connect, statement);
			return statuses;
		}
	}
	/**
	 * @param deliveryIdentifier
	 * @param subDateTime
	 */
	public int insertSubmissionAndStatus(SubmissionAndStatus subMS) {
		int success = 0;
		Connection connect = null;
		PreparedStatement prepareStm = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" + DEL_IDENTIFIERCOLUME + ", " + SUBMISSIONDATECOLUME + ") VALUES (?, ?)");
			prepareStm.setInt(1, subMS.getDel_identifier());
			//String currentTime = ISO_BASIC.format(new Date());
			prepareStm.setString(2, subMS.getSubmissionDate());
			
			prepareStm.executeUpdate();
			
			int statusSuccess = insertSubmissionStatus(subMS);
			if (statusSuccess == 1){
				connect.commit();
				logger.info("Submission and Submissin status for " + subMS.getDel_identifier() + " has been added.");
				success = 1;
			}
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
			success = 0;
	    } finally {
	        close(connect, prepareStm);
	    }
		return success;
	}
	
	public int insertSubmissionStatus(SubmissionAndStatus subMS) {
		int success = 0;
		
		Connection connect = null;
		PreparedStatement prepareStm = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + STATUSTABLENAME + " (" + DEL_IDENTIFIERCOLUME + ", " 
																					+ SUBMISSIONDATECOLUME + ", "
																					+ STATUSDATECOLUME + ", "
																					+ STATUSCOLUME + ", "
																					+ EMAILCOLUME + ", "
																					+ COMMENTCOLUME
																					+ ") VALUES (?, ?, ?, ?, ?, ?)");
			logger.debug("Get vaules: \n" + subMS.getDel_identifier());
			logger.debug(subMS.getSubmissionDate());
			logger.debug(subMS.getStatusDate());
			logger.debug(subMS.getStatus());
			logger.debug(subMS.getEmail());
			logger.debug(subMS.getComment());
			
			
			prepareStm.setInt(1, subMS.getDel_identifier());
			prepareStm.setString(2, subMS.getSubmissionDate());
			prepareStm.setString(3, subMS.getStatusDate());
			prepareStm.setString(4, subMS.getStatus());
			prepareStm.setString(5, subMS.getEmail());
			prepareStm.setString(6, subMS.getComment());
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("Submission Status for " + subMS.getDel_identifier() + " has been added.");
			success = 1;
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
			success = 0;
	    } finally {
    		close(connect, prepareStm);
	    }
		return success;
	}
	
	public SubmissionAndStatus updateSubmissionStatus(SubmissionAndStatus subMS) {

		SubmissionAndStatus updatedSubMS = null;
		
		Connection connect = null;
		PreparedStatement prepareStm = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			boolean hasComment = false;
			String setComment = "";
			if (subMS.getComment() != null && subMS.getComment().length() > 0){
				hasComment = true;
				setComment = ", " + COMMENTCOLUME + " = ?";
			}
			prepareStm = connect.prepareStatement("UPDATE " + STATUSTABLENAME + " SET "
																		+ STATUSCOLUME + " = ?, "
																		+ EMAILCOLUME + " = ?" 
																		+ setComment
														+ " WHERE " + DEL_IDENTIFIERCOLUME + " = ? "
														+ "AND " + SUBMISSIONDATECOLUME + " = ? "
														+ "AND " + STATUSDATECOLUME + " = ?");
			
			logger.debug("update vaules: \n" + subMS.getDel_identifier());
			logger.debug(subMS.getSubmissionDate());
			logger.debug(subMS.getStatusDate());
			logger.debug(subMS.getStatus());
			logger.debug(subMS.getEmail());
			logger.debug(subMS.getComment());
			
			
			prepareStm.setString(1, subMS.getStatus());
			prepareStm.setString(2, subMS.getEmail());
			
			if (hasComment)
				prepareStm.setString(3, subMS.getComment());
			prepareStm.setInt(hasComment ? 4:3, subMS.getDel_identifier());
			prepareStm.setString(hasComment ? 5:4, subMS.getSubmissionDate());
			prepareStm.setString(hasComment ? 6:4, subMS.getStatusDate());
			
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("Submission Status for " + subMS.getDel_identifier() + " has been updated.");
			updatedSubMS = getSubmissionsAndStatus(subMS.getDel_identifier(), subMS.getSubmissionDate(), subMS.getStatusDate());
			
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
	        close(connect, prepareStm);
	    }
		return updatedSubMS;
	}
	

	/**
	 * @param stm
	 */
	private void close(Connection connect, Statement stm) {
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
