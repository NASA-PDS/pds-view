package gov.nasa.pds.tracking.tracking.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class CertificationStatusDao extends DBConnector {
	
	public static Logger logger = Logger.getLogger(CertificationStatusDao.class);

	private static final String TABLENAME = "certification_status";
	private static final String PRODUCTTABLENAME= "product";

	public static final String LOGIDENTIFIERCOLUMN = "logical_identifier";
	public static final String VERSIONCOLUMN = "version_id";
	public static final String DATECOLUMN = "status_date_time";
	public static final String STATUSCOLUMN = "status";
	public static final String EMAILCOLUMN = "electronic_mail_address";
	public static final String COMMENTCOLUMN = "comment";

	private ResultSet resultSet = null;
		
	public CertificationStatusDao() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param cStatus a object of CertificationStatus
	 */
	public int insertCertificationStatus(CertificationStatus cStatus){
		int success = 0;
		
		Connection connect = null;
		PreparedStatement prepareStm = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" 
													+ LOGIDENTIFIERCOLUMN + ", " 
													+ VERSIONCOLUMN + ", "
													+ DATECOLUMN + ", "
													+ STATUSCOLUMN + ", "
													+ EMAILCOLUMN + ", "
													+ COMMENTCOLUMN + ") VALUES (?, ?, ?, ?, ?, ?)");
			prepareStm.setString(1, cStatus.getLogIdentifier());
			prepareStm.setString(2, cStatus.getVersion());
			prepareStm.setString(3, cStatus.getDate());
			prepareStm.setString(4, cStatus.getStatus());
			prepareStm.setString(5, cStatus.getEmail());
			prepareStm.setString(6, cStatus.getComment());
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The Certification Status " + cStatus.getStatus() + " for the product: " + cStatus.getLogIdentifier() + ", has been added.");
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
	
	/**
	 * Certification Status Query - Query the certification_status table for the latest certification status of a given product.
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public CertificationStatus getLatestCertificationStatus(String logical_identifier, String ver) {

		CertificationStatus certifStatus = null;
		
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOGIDENTIFIERCOLUMN + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUMN + " = '" + ver + "' order by " + DATECOLUMN + " DESC");

			if (resultSet.next()){
				certifStatus = new CertificationStatus();

				certifStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				certifStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				certifStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				certifStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				certifStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				certifStatus.setDate(resultSet.getString(DATECOLUMN));
				
			}	
			else{
				logger.info("Can not find Vertifacation Status!");
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return certifStatus;
		}
	}
	
	/**
	 * Certification Status List Query - Query the certification_status table for the certification status progression of a given product.
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public List<CertificationStatus> getCertificationStatusList(String logical_identifier, String ver) {
		
		List<CertificationStatus> certifStatuses = new ArrayList<CertificationStatus>();
		CertificationStatus certifStatus = null;
		
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			
			resultSet = statement.executeQuery("select * from " + TABLENAME 
					+ " where " + LOGIDENTIFIERCOLUMN + " = '" + logical_identifier + "' and "
					+ VERSIONCOLUMN + " = '" + ver + "' order by " + DATECOLUMN);

			while (resultSet.next()){
				certifStatus = new CertificationStatus();

				certifStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				certifStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				certifStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				certifStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				certifStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				certifStatus.setDate(resultSet.getString(DATECOLUMN));
				
				certifStatuses.add(certifStatus);
			}	

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return certifStatuses;
		}
	}
	/**
	 * @return a list of Certification Status objects with the title.
	 */
	@SuppressWarnings("finally")
	public List<CertificationStatus> getCertificationStatusOrderByVersion(String title) {

		List<CertificationStatus> cStatuses = new ArrayList<CertificationStatus>();
		CertificationStatus cStatus = null;
		
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			resultSet = statement.executeQuery("select a.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " a"
					+ " where p." + LOGIDENTIFIERCOLUMN + " = " + "a." + LOGIDENTIFIERCOLUMN + " and p."
					+ ProductDao.TITLECOLUMN + " = '" + title + "' order by a." + VERSIONCOLUMN);

			while (resultSet.next()) {
				cStatus = new CertificationStatus();

				cStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				cStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				cStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				cStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				cStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				cStatus.setDate(resultSet.getString(DATECOLUMN));

				cStatuses.add(cStatus);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return cStatuses;
		}
	}
	/**
	 * @return a list of all Certification Status objects.
	 */
	@SuppressWarnings("finally")
	public List<CertificationStatus> getCertificationStatusOrderByVersion() {

		List<CertificationStatus> cStatuses = new ArrayList<CertificationStatus>();
		CertificationStatus cStatus = null;
		
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			resultSet = statement
					.executeQuery("select c.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " c" + " where p."
							+ LOGIDENTIFIERCOLUMN + " = " + "c." + LOGIDENTIFIERCOLUMN + " order by c." + VERSIONCOLUMN);

			while (resultSet.next()) {
				cStatus = new CertificationStatus();

				cStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				cStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				cStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				cStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				cStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				cStatus.setDate(resultSet.getString(DATECOLUMN));

				cStatuses.add(cStatus);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return cStatuses;
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
