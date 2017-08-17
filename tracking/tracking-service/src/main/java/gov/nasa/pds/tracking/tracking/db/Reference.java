/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
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
public class Reference extends DBConnector {

	public static Logger logger = Logger.getLogger(Reference.class);

	private static final String TABLENAME  = "reference";
	
	public static final String LOG_IDENTIFIERCOLUME = "logical_identifier";
	public static final String REFERENCECOLUME = "reference";
	public static final String TYPECOLUME = "type";
	
	
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement prepareStm = null;
	private ResultSet resultSet = null;
	
	private String log_identifier = null;
	private String reference = null;
	private String type = null;

	/**
	 * @return the log_identifier
	 */
	public String getLog_identifier() {
		return log_identifier;
	}

	/**
	 * @param log_identifier, the log_identifier to set
	 */
	public void setLog_identifier(String log_identifier) {
		this.log_identifier = log_identifier;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference, the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type, the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Reference() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return a list of product references for the logical identifier.
	 */
	@SuppressWarnings("finally")
	public List<Reference> getProductReferences(String identifier) {

		List<Reference> refs = new ArrayList<Reference>();
		Reference ref = null;
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);

			statement = connect.createStatement();

			/*System.out.println("select * from " + TABLENAME
											+ " where " + LOG_IDENTIFIERCOLUME + " = '" + identifier
											+ "' order by " + TYPECOLUME);*/
			resultSet = statement.executeQuery("select * from " + TABLENAME
											+ " where " + LOG_IDENTIFIERCOLUME + " = '" + identifier
											+ "' order by " + TYPECOLUME);

			while (resultSet.next()) {
				ref = new Reference();

				ref.setLog_identifier(resultSet.getString(LOG_IDENTIFIERCOLUME));
				ref.setReference(resultSet.getString(REFERENCECOLUME));
				ref.setType(resultSet.getString(TYPECOLUME));
								
				refs.add(ref);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return refs;
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
	 * @param logicalIdentifier
	 * @param reference
	 * @param type
	 */
	public void insertReference(String logicalIdentifier, String reference, String type) {
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" + LOG_IDENTIFIERCOLUME + ", "
																					+ REFERENCECOLUME + ", "
																					+ TYPECOLUME + ") VALUES (?, ?, ?)");
			prepareStm.setString(1, logicalIdentifier);
			prepareStm.setString(2, reference);
			prepareStm.setString(3, type);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The reference, " + reference + ", for  " + logicalIdentifier + " has been added.");
			
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
	 * @param logicalIdentifier
	 * @param reference
	 * @param type
	 */
	public void updateReference(String logicalIdentifier, String reference, String type) {
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);
			connect.setAutoCommit(false);
			prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " + TYPECOLUME + " = ? "
														+ "WHERE " + LOG_IDENTIFIERCOLUME + " = ? "
														+ "AND " + REFERENCECOLUME + " = ?");
			
			prepareStm.setString(1, type);
			prepareStm.setString(2, logicalIdentifier);
			prepareStm.setString(3, reference);
			
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The type for  " + logicalIdentifier + " has been updated to " + type + ".");
			
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
