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
public class Doi extends DBConnector {

	public static Logger logger = Logger.getLogger(Doi.class);

	private final static String TABLENAME = "doi";
	
	private final static String LOG_IDENTIFIERCOLUME = "logical_identifier";
	private final static String VERSIONCOLUME  = "version_id";
	private final static String DOICOLUME  = "doi";
	private final static String DATECOLUME  = "registration_date";
	private final static String URLCOLUME  = "site_url";
	private final static String EMAILCOLUME  = "electronic_mail_address";
	private final static String COMMENTCOLUME = "comment";
	
	private static Connection connect = null;
	private static Statement statement = null;
	private static PreparedStatement prepareStm = null;
	private static ResultSet resultSet = null;
	
	private String log_identifier = null;
	private String version = null;
	private String doi = null;
	private String url = null;
	private String date = null;
	private String email = null;
	private String comment = null;

	
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
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}


	/**
	 * @param version, the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}


	/**
	 * @return the doi
	 */
	public String getDoi() {
		return doi;
	}


	/**
	 * @param doi, the doi to set
	 */
	public void setDoi(String doi) {
		this.doi = doi;
	}


	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}


	/**
	 * @param url, the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}


	/**
	 * @param date, the date to set
	 */
	public void setDate(String date) {
		this.date = date;
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
	public Doi() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param logical_identifier
	 * @param ver
	 * @param doi
	 * @param date
	 * @param url
	 * @param email
	 * @param comment
	 */
	public void insertDOI(String logical_identifier, String ver, String doi, String date, String url, String email, String comment) {
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" 
													+ LOG_IDENTIFIERCOLUME + ", " 
													+ VERSIONCOLUME + ", "
													+ DOICOLUME + ", "
													+ DATECOLUME + ", "
													+ URLCOLUME + ", "
													+ EMAILCOLUME + ", "
													+ COMMENTCOLUME + ") VALUES (?, ?, ?, ?, ?, ?, ?)");
			prepareStm.setString(1, logical_identifier);
			prepareStm.setString(2, version);
			prepareStm.setString(3, doi);
			prepareStm.setString(4, date);
			prepareStm.setString(5, url);
			prepareStm.setString(6, email);
			prepareStm.setString(7, comment);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The DOI " + doi + " for the product: " + logical_identifier + ", has been added.");
			
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
	 * @param logical_identifier
	 * @param ver
	 * @param url
	 */
	public void update(String logical_identifier, String ver, String url) {
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " + URLCOLUME + " = ? WHERE " 
															+ LOG_IDENTIFIERCOLUME + " = ? AND " + VERSIONCOLUME + " = ?");
			prepareStm.setString(1, url);
			prepareStm.setString(2, logical_identifier);
			prepareStm.setString(3, ver);

			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The site url for product: " + logical_identifier + ", has been updated to " + url + ".");
			
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
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public static List<Doi> getDOIList(String logical_identifier, String ver) {
		
		List<Doi> DOIList = new ArrayList<Doi>();
		Doi doi = null;
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOG_IDENTIFIERCOLUME + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUME + " = '" + ver + "'");

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
			close(statement);
			return DOIList;
		}
	}
	
	/**
	 * @param stm
	 */
	private static void close(Statement stm) {
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
