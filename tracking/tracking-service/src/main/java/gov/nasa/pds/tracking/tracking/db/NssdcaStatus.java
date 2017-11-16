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
  
	public static final String LOGIDENTIFIERCOLUMN = "logical_identifier";
	public static final String VERSIONCOLUMN = "version_id";
	public static final String DATECOLUMN = "status_date_time";
	public static final String NSSDCACOLUMN = "nssdca_identifier";
	public static final String EMAILCOLUMN = "electronic_mail_address";
	public static final String COMMENTCOLUMN = "comment";

	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement prepareStm = null;
	private ResultSet resultSet = null;

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
	
	public void insertNssdcaStatus(String logical_identifier, String version, String date, String nssdca, String email, String comment) {
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" 
													+ LOGIDENTIFIERCOLUMN + ", " 
													+ VERSIONCOLUMN + ", "
													+ DATECOLUMN + ", "
													+ NSSDCACOLUMN + ", "
													+ EMAILCOLUMN + ", "
													+ COMMENTCOLUMN + ") VALUES (?, ?, ?, ?, ?, ?)");
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
	public List<NssdcaStatus> getNssdcaStatusList(String logical_identifier, String ver) {
		
		List<NssdcaStatus> nssdcaStatuses = new ArrayList<NssdcaStatus>();
		NssdcaStatus nssdcaStatus = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOGIDENTIFIERCOLUMN + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUMN + " = '" + ver + "'");

			while (resultSet.next()){
				nssdcaStatus = new NssdcaStatus();
				
				nssdcaStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				nssdcaStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				nssdcaStatus.setNssdca(resultSet.getString(NSSDCACOLUMN));
				nssdcaStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				nssdcaStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				nssdcaStatus.setDate(resultSet.getString(DATECOLUMN));
				
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
	 * @return a list of Certification Status objects with the title.
	 */
	@SuppressWarnings("finally")
	public List<NssdcaStatus> getNssdcaStatusOrderByVersion(String title) {

		List<NssdcaStatus> nStatuses = new ArrayList<NssdcaStatus>();
		NssdcaStatus nStatus = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			resultSet = statement.executeQuery("select a.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " a"
					+ " where p." + LOGIDENTIFIERCOLUMN + " = " + "a." + LOGIDENTIFIERCOLUMN + " and p."
					+ Product.TITLECOLUMN + " = '" + title + "' order by a." + VERSIONCOLUMN);

			while (resultSet.next()) {
				nStatus = new NssdcaStatus();

				nStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				nStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				nStatus.setNssdca(resultSet.getString(NSSDCACOLUMN));
				nStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				nStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				nStatus.setDate(resultSet.getString(DATECOLUMN));

				nStatuses.add(nStatus);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return nStatuses;
		}
	}
	/**
	 * @return a list of all Certification Status objects.
	 */
	@SuppressWarnings("finally")
	public List<NssdcaStatus> getNssdcaStatusOrderByVersion() {

		List<NssdcaStatus> nStatuses = new ArrayList<NssdcaStatus>();
		NssdcaStatus nStatus = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			resultSet = statement
					.executeQuery("select n.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " n" + " where p."
							+ LOGIDENTIFIERCOLUMN + " = " + "n." + LOGIDENTIFIERCOLUMN + " order by n." + VERSIONCOLUMN);

			while (resultSet.next()) {
				nStatus = new NssdcaStatus();

				nStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				nStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				nStatus.setNssdca(resultSet.getString(NSSDCACOLUMN));
				nStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				nStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				nStatus.setDate(resultSet.getString(DATECOLUMN));

				nStatuses.add(nStatus);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return nStatuses;
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
