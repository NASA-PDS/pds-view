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
public class Releases extends DBConnector {

	public static Logger logger = Logger.getLogger(NssdcaStatus.class);

	private static final String TABLENAME = "releases";
	private static final String PRODUCTTABLENAME= "product";
  	  
	public static final String LOGIDENTIFIERCOLUME = "logical_identifier";
	public static final String VERSIONCOLUME = "version_id";
	public static final String DATECOLUME = "release_date_time";
	public static final String NAMECOLUME = "name";
	public static final String DESCCOLUME = "description";
	public static final String EMAILCOLUME = "electronic_mail_address";
	public static final String COMMENTCOLUME = "comment";

	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement prepareStm = null;
	private ResultSet resultSet = null;

	private String logIdentifier = null;
	private String version = null;
	private String date = null;
	private String name = null;
	private String description = null;
	private String email = null;
	private String comment = null;
	
	
	/**
	 * @return the logIdentifier
	 */
	public String getLogIdentifier() {
		return logIdentifier;
	}
	/**
	 * @param logIdentifier, the logIdentifier to set
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
	 * @param version, the version to set
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
	 * @param date, the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name, the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description, the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	public Releases() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	public void insesrtReleases(String logical_identifier, String ver, String date, String name, String description, String email, String comment) {
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
			prepareStm.setString(1, logical_identifier);
			prepareStm.setString(2, ver);
			prepareStm.setString(3, date);
			prepareStm.setString(4, name);
			prepareStm.setString(5, description);
			prepareStm.setString(6, email);
			prepareStm.setString(7, comment);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The releases " + name + " for the product: " + logical_identifier + ", has been added.");
			
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
	public Releases getLatestReleases(String logical_identifier, String ver) {

		Releases rel = null;
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
				rel.setDescription(resultSet.getString(Releases.DESCCOLUME));
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
			close(statement);
			return rel;
		}
	}
	
	/**
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public List<Releases> getReleasesList(String logical_identifier, String ver) {
		
		List<Releases> rels = new ArrayList<Releases>();
		Releases rel = null;
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
				rel.setDescription(resultSet.getString(Releases.DESCCOLUME));
				rel.setEmail(resultSet.getString(EMAILCOLUME));
				rel.setComment(resultSet.getString(COMMENTCOLUME));
				rel.setDate(resultSet.getString(DATECOLUME));
				
				rels.add(rel);
			}	

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return rels;
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
