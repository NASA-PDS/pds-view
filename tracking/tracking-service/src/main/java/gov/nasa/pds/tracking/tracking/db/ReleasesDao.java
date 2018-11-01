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
public class ReleasesDao extends DBConnector {

	public static Logger logger = Logger.getLogger(NssdcaStatus.class);

	private static final String TABLENAME = "releases";
  	  
	public static final String LOGIDENTIFIERCOLUME = "logical_identifier";
	public static final String VERSIONCOLUME = "version_id";
	public static final String DATECOLUME = "release_date_time";
	public static final String NAMECOLUME = "name";
	public static final String DESCCOLUME = "description";
	public static final String EMAILCOLUME = "electronic_mail_address";
	public static final String COMMENTCOLUME = "comment";

	private ResultSet resultSet = null;

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public ReleasesDao() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	public int insertReleases(Releases rels) {
		int success = 0;
		
		Connection connect = null;
		PreparedStatement prepareStm = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" 
													+ LOGIDENTIFIERCOLUME + ", " 
													+ VERSIONCOLUME + ", "
													+ DATECOLUME + ", "
													+ NAMECOLUME + ", "
													+ DESCCOLUME + ", "
													+ EMAILCOLUME + ", "
													+ COMMENTCOLUME + ") VALUES (?, ?, ?, ?, ?, ?, ?)");
			prepareStm.setString(1, rels.getLogIdentifier());
			prepareStm.setString(2, rels.getVersion());
			prepareStm.setString(3, rels.getDate());
			prepareStm.setString(4, rels.getName());
			prepareStm.setString(5, rels.getDescription());
			prepareStm.setString(6, rels.getEmail());
			prepareStm.setString(7, rels.getComment());
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The releases " + rels.getName() + " for the product: " + rels.getLogIdentifier() + ", has been added.");
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
	
	@SuppressWarnings("finally")
	public List<Releases> getReleasesList() {
		
		List<Releases> rels = new ArrayList<Releases>();
		Releases rel = null;
		
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME + " order by " + DATECOLUME);

			while (resultSet.next()){
				rel = new Releases();

				rel.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUME));
				rel.setVersion(resultSet.getString(VERSIONCOLUME));
				rel.setName(resultSet.getString(NAMECOLUME));
				rel.setDescription(resultSet.getString(ReleasesDao.DESCCOLUME));
				rel.setEmail(resultSet.getString(EMAILCOLUME));
				rel.setComment(resultSet.getString(COMMENTCOLUME));
				rel.setDate(resultSet.getString(DATECOLUME));
				
				rels.add(rel);
			}	

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return rels;
		}
	}
	/**
	 * Release Query - Query the releases table for the latest release of a given product.
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public Releases getLatestReleases(String logical_identifier, String ver) {

		Releases rel = null;
		
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOGIDENTIFIERCOLUME + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUME + " = '" + ver + "' order by " + DATECOLUME + " DESC");

			if (resultSet.next()){
				rel = new Releases();
				
				rel.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUME));
				rel.setVersion(resultSet.getString(VERSIONCOLUME));
				rel.setName(resultSet.getString(NAMECOLUME));
				rel.setDescription(resultSet.getString(ReleasesDao.DESCCOLUME));
				rel.setEmail(resultSet.getString(EMAILCOLUME));
				rel.setComment(resultSet.getString(COMMENTCOLUME));
				rel.setDate(resultSet.getString(DATECOLUME));
				
			}	
			else{
				logger.info("Can not find any Releases!");
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return rel;
		}
	}
	
	/**
	 * Release List Query - Query the releases table for the release progression of a given product.
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public List<Releases> getReleasesList(String logical_identifier, String ver) {
		
		List<Releases> rels = new ArrayList<Releases>();
		Releases rel = null;
		
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOGIDENTIFIERCOLUME + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUME + " = '" + ver + "' order by " + DATECOLUME);

			while (resultSet.next()){
				rel = new Releases();

				rel.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUME));
				rel.setVersion(resultSet.getString(VERSIONCOLUME));
				rel.setName(resultSet.getString(NAMECOLUME));
				rel.setDescription(resultSet.getString(ReleasesDao.DESCCOLUME));
				rel.setEmail(resultSet.getString(EMAILCOLUME));
				rel.setComment(resultSet.getString(COMMENTCOLUME));
				rel.setDate(resultSet.getString(DATECOLUME));
				
				rels.add(rel);
			}	

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return rels;
		}
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
