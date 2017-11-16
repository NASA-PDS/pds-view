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

	public static final String INST_TABLENAME  = "instrument_reference";
	public static final String INVES_TABLENAME  = "investigation_reference";
	public static final String NODE_TABLENAME  = "node_reference";
	
	public static final String LOG_IDENTIFIERCOLUMN = "logical_identifier";
	public static final String REFERENCECOLUMN = "reference";
	public static final String TITLECOLUMN = "title";
		
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement prepareStm = null;
	private ResultSet resultSet = null;
	
	private String log_identifier = null;
	private String reference = null;
	private String title = null;

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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title, the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Reference() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param identifier
	 * @param tableName
	 * @return  a list of product references for the logical identifier.
	 */
	@SuppressWarnings("finally")
	public List<Reference> getProductReferences(String identifier, String tableName) {
		
		List<Reference> refs = new ArrayList<Reference>();

		if (tableName.equalsIgnoreCase(INST_TABLENAME) || tableName.equalsIgnoreCase(INVES_TABLENAME) 
				||tableName.equalsIgnoreCase(NODE_TABLENAME)){

			Reference ref = null;
			try {
				// Setup the connection with the DB
				connect = getConnection();
	
				statement = connect.createStatement();
	
				logger.debug("select * from " + tableName
												+ " where " + LOG_IDENTIFIERCOLUMN + " = '" + identifier
												+ "' order by " + TITLECOLUMN);
				
				resultSet = statement.executeQuery("select * from " + tableName
												+ " where " + LOG_IDENTIFIERCOLUMN + " = '" + identifier
												+ "' order by " + TITLECOLUMN);
	
				while (resultSet.next()) {
					ref = new Reference();
	
					ref.setLog_identifier(resultSet.getString(LOG_IDENTIFIERCOLUMN));
					ref.setReference(resultSet.getString(REFERENCECOLUMN));
					ref.setTitle(resultSet.getString(TITLECOLUMN));
									
					refs.add(ref);
				}
	
			} catch (Exception e) {
				logger.error(e);
			} finally {
				close(statement);
			}
		}else{
			logger.error("Please check the reference table name: instrument_reference, investigation_reference or node_reference.");
		}
		return refs;
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
	 * @param title
	 * @param tableName
	 */
	public void insertReference(String logicalIdentifier, String reference, String title, String tableName) {
		
		if (tableName.equalsIgnoreCase(INST_TABLENAME) || tableName.equalsIgnoreCase(INVES_TABLENAME) 
				||tableName.equalsIgnoreCase(NODE_TABLENAME)){
			try {
				// Setup the connection with the DB
				connect = getConnection();
				connect.setAutoCommit(false);
				
				prepareStm = connect.prepareStatement("INSERT INTO " + tableName + " (" + LOG_IDENTIFIERCOLUMN + ", "
																						+ REFERENCECOLUMN + ", "
																						+ TITLECOLUMN + ") VALUES (?, ?, ?)");
				prepareStm.setString(1, logicalIdentifier);
				prepareStm.setString(2, reference);
				prepareStm.setString(3, title);
				
				prepareStm.executeUpdate();
				
				connect.commit();
				logger.info("The reference, " + title + ", for  " + logicalIdentifier + " has been added in table: " + tableName +".");
				
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
		}else{
			logger.error("Please check the reference table name: instrument_reference, investigation_reference or node_reference.");
		}
		
	}
	
	/**
	 * @param logicalIdentifier
	 * @param reference
	 * @param title
	 * @param tableName
	 */
	public void updateReference(String logicalIdentifier, String reference, String title, String tableName) {
		
		if (tableName.equalsIgnoreCase(INST_TABLENAME) || tableName.equalsIgnoreCase(INVES_TABLENAME) 
				||tableName.equalsIgnoreCase(NODE_TABLENAME)){
			try {
				// Setup the connection with the DB
				connect = getConnection();
				connect.setAutoCommit(false);
				prepareStm = connect.prepareStatement("UPDATE " + tableName + " SET " + TITLECOLUMN + " = ? "
															+ "WHERE " + LOG_IDENTIFIERCOLUMN + " = ? "
															+ "AND " + REFERENCECOLUMN + " = ?");
				
				prepareStm.setString(1, title);
				prepareStm.setString(2, logicalIdentifier);
				prepareStm.setString(3, reference);
				
				
				prepareStm.executeUpdate();
				
				connect.commit();
				logger.info("The reference for  " + title + " has been updated in table " + tableName + ".");
				
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
		}else{
			logger.error("Please check the reference table name: instrument_reference, investigation_reference or node_reference.");
		}
	}

}
