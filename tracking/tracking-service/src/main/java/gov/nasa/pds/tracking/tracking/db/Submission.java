/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class Submission extends DBConnector {
	
	public static Logger logger = Logger.getLogger(Submission.class);

	private static String TABLENAME  = "submission";
	
	private static String DEL_IDENTIFIERCOLUME = "delivery_identifier";
	private static String SUBMISSIONDATECOLUME = "submission_date_time";
	
	
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement prepareStm = null;
	private ResultSet resultSet = null;
	
	private int del_identifier = 0;
	private String submissionDate = null;

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
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Submission() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param deliveryIdentifier
	 * @param subDateTime
	 */
	public void insertSubmission(int deliveryIdentifier, String subDateTime) {
	
	try {
		// Setup the connection with the DB
		connect = getConnection();
		connect.setAutoCommit(false);
		
		prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" + DEL_IDENTIFIERCOLUME + ", " + SUBMISSIONDATECOLUME + ") VALUES (?, ?)");
		prepareStm.setInt(1, deliveryIdentifier);
		prepareStm.setString(2, subDateTime);
		
		prepareStm.executeUpdate();
		
		connect.commit();
		logger.info("Submission for " + deliveryIdentifier + " has been added.");
		
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
