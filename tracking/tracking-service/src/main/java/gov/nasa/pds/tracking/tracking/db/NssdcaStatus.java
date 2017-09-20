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

public class NssdcaStatus extends DBConnector {
	
	public static Logger logger = Logger.getLogger(NssdcaStatus.class);

	private static final String TABLENAME = "nssdca_status";
	private static final String PRODUCTTABLENAME= "product";
  
	public static final String LOGIDENTIFIERCOLUME = "logical_identifier";
	public static final String VERSIONCOLUME = "version_id";
	public static final String DATECOLUME = "status_date_time";
	public static final String NSSDCACOLUME = "nssdca_identifier";
	public static final String EMAILCOLUME = "electronic_mail_address";
	public static final String COMMENTCOLUME = "comment";

	private static Connection connect = null;
	private static Statement statement = null;
	private static PreparedStatement prepareStm = null;
	private static ResultSet resultSet = null;

	private String logIdentifier = null;
	private String version = null;
	private String date = null;
	private String nssdca = null;
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
	 * @return the nssdca
	 */
	public String getNssdca() {
		return nssdca;
	}



	/**
	 * @param nssdca, the nssdca to set
	 */
	public void setNssdca(String nssdca) {
		this.nssdca = nssdca;
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



	public NssdcaStatus() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	public void insertNssdcaStatus(String logical_identifier, String version, String date, String nssdca, String mail, String comment) {
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" 
													+ LOGIDENTIFIERCOLUME + ", " 
													+ VERSIONCOLUME + ", "
													+ DATECOLUME + ", "
													+ NSSDCACOLUME + ", "
													+ EMAILCOLUME + ", "
													+ COMMENTCOLUME + ") VALUES (?, ?, ?, ?, ?, ?)");
			prepareStm.setString(1, logical_identifier);
			prepareStm.setString(2, version);
			prepareStm.setString(3, date);
			prepareStm.setString(4, nssdca);
			prepareStm.setString(5, email);
			prepareStm.setString(6, comment);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The NSSDCA status " + nssdca + " for the product: " + logical_identifier + ", has been added.");
			
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
	public static List<NssdcaStatus> getNssdcaStatusList(String logical_identifier, String ver) {
		
		List<NssdcaStatus> nssdcaStatuses = new ArrayList<NssdcaStatus>();
		NssdcaStatus nssdcaStatus = null;
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOGIDENTIFIERCOLUME + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUME + " = '" + ver + "'");

			while (resultSet.next()){
				nssdcaStatus = new NssdcaStatus();
				
				nssdcaStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUME));
				nssdcaStatus.setVersion(resultSet.getString(VERSIONCOLUME));
				nssdcaStatus.setNssdca(resultSet.getString(NSSDCACOLUME));
				nssdcaStatus.setEmail(resultSet.getString(EMAILCOLUME));
				nssdcaStatus.setComment(resultSet.getString(COMMENTCOLUME));
				nssdcaStatus.setDate(resultSet.getString(DATECOLUME));
				
				nssdcaStatuses.add(nssdcaStatus);
			}	

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return nssdcaStatuses;
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
