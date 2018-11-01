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
public class DoiDao extends DBConnector {

	public DoiDao() throws ClassNotFoundException, SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static Logger logger = Logger.getLogger(DoiDao.class);

	private final static String TABLENAME = "doi";
	
	public final static String LOG_IDENTIFIERCOLUME = "logical_identifier";
	public final static String VERSIONCOLUME  = "version_id";
	public final static String DOICOLUME  = "doi";
	public final static String DATECOLUME  = "registration_date";
	public final static String URLCOLUME  = "site_url";
	public final static String EMAILCOLUME  = "electronic_mail_address";
	public final static String COMMENTCOLUME = "comment";

	private ResultSet resultSet = null;	
	/**
	 * @param doi
	 */
	public int insertDOI(Doi doi) {
		int success = 0;
		Connection connect = null;
		PreparedStatement prepareStm = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" 
													+ LOG_IDENTIFIERCOLUME + ", " 
													+ VERSIONCOLUME + ", "
													+ DOICOLUME + ", "
													+ DATECOLUME + ", "
													+ URLCOLUME + ", "
													+ EMAILCOLUME + ", "
													+ COMMENTCOLUME + ") VALUES (?, ?, ?, ?, ?, ?, ?)");
			prepareStm.setString(1, doi.getLog_identifier());
			prepareStm.setString(2, doi.getVersion());
			prepareStm.setString(3, doi.getDoi());
			prepareStm.setString(4, doi.getDate());
			prepareStm.setString(5, doi.getUrl());
			prepareStm.setString(6, doi.getEmail());
			prepareStm.setString(7, doi.getComment());
			
			prepareStm.executeUpdate();
			
			connect.commit();
			success = 1;
			
			logger.info("The DOI " + doi.getDoi() + " for the product: " + doi.getLog_identifier() + ", has been added.");
			
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
	
	/**
	 * @param doi
	 */
	public Doi updateDOI(Doi doi) {
		
		Doi updatedDoi = null; 
		Connection connect = null;
		PreparedStatement prepareStm = null;

		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			boolean hasComment = false;
			String setComment = "";
			if (doi.getComment() != null && doi.getComment().length() > 0){
				hasComment = true;
				setComment = ", " + COMMENTCOLUME + " = ?";
			}
			
			prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " + URLCOLUME + " = ?, " 
																				  + EMAILCOLUME + " = ?" 
																				  + setComment 
																				  + " WHERE " + LOG_IDENTIFIERCOLUME + " = ? AND " 
																				  + VERSIONCOLUME + " = ?");
			prepareStm.setString(1, doi.getUrl());
			prepareStm.setString(2, doi.getEmail());
			if (hasComment)
				prepareStm.setString(3, doi.getComment());
			prepareStm.setString(hasComment ? 4:3, doi.getLog_identifier());
			prepareStm.setString(hasComment ? 5:4, doi.getVersion());
			
			prepareStm.executeUpdate();
			
			connect.commit();

			logger.info("The site url for product: " + doi.getLog_identifier() + ", has been updated to " + doi.getUrl() + ".");
			updatedDoi = getDOI(doi.getLog_identifier(), doi.getVersion());
			
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
		return updatedDoi;
	}
	
	@SuppressWarnings("finally")
	public List<Doi> getDOIList() {
		
		List<Doi> DOIList = new ArrayList<Doi>();
		Doi doi = null;
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME);

			while (resultSet.next()){
				doi = new Doi();
				doi.setLog_identifier(resultSet.getString(LOG_IDENTIFIERCOLUME));
				doi.setVersion(resultSet.getString(VERSIONCOLUME));
				doi.setDoi(resultSet.getString(DOICOLUME));
				doi.setDate(resultSet.getString(DATECOLUME));
				doi.setUrl(resultSet.getString(URLCOLUME));
				doi.setEmail(resultSet.getString(EMAILCOLUME));
				doi.setComment(resultSet.getString(COMMENTCOLUME));
				
				
				DOIList.add(doi);
			}	

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return DOIList;
		}
	}
	/*
	 * DOI Query - Query the doi table for the DOI and associated information of a given product.
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public Doi getDOI(String logical_identifier, String ver) {
		Doi doi = null;
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOG_IDENTIFIERCOLUME + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUME + " = '" + ver + "'");

			if (resultSet.next()){
				doi = new Doi();
				doi.setLog_identifier(resultSet.getString(LOG_IDENTIFIERCOLUME));
				doi.setVersion(resultSet.getString(VERSIONCOLUME));
				doi.setDoi(resultSet.getString(DOICOLUME));
				doi.setDate(resultSet.getString(DATECOLUME));
				doi.setUrl(resultSet.getString(URLCOLUME));
				doi.setEmail(resultSet.getString(EMAILCOLUME));
				doi.setComment(resultSet.getString(COMMENTCOLUME));
			}	

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return doi;
		}
	}

	/**
	 * @param connect
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
