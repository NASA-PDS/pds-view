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
/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class ArchiveStatusDao extends DBConnector {

	public static Logger logger = Logger.getLogger(ArchiveStatusDao.class);

	private static final String TABLENAME = "archive_status";
	private static final String PRODUCTTABLENAME= "product";

	public static final String LOGIDENTIFIERCOLUMN = "logical_identifier";
	public static final String VERSIONCOLUMN = "version_id";
	public static final String DATECOLUMN = "status_date_time";
	public static final String STATUSCOLUMN = "status";
	public static final String EMAILCOLUMN = "electronic_mail_address";
	public static final String COMMENTCOLUMN = "comment";

	private ResultSet resultSet = null;

	public ArchiveStatusDao() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	@SuppressWarnings("finally")
	public List<ArchiveStatus> getArchiveStatusOrderByVersion() {

		List<ArchiveStatus> archStatuses = new ArrayList<ArchiveStatus>();
		ArchiveStatus archStatus = null;
		
		Connection connect = null;
		Statement statement = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			logger.debug("select a.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " a" + " where p."
					+ LOGIDENTIFIERCOLUMN + " = " + "a." + LOGIDENTIFIERCOLUMN + " order by a." + VERSIONCOLUMN);
			resultSet = statement
					.executeQuery("select a.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " a" + " where p."
							+ LOGIDENTIFIERCOLUMN + " = " + "a." + LOGIDENTIFIERCOLUMN + " order by a." + VERSIONCOLUMN);

			while (resultSet.next()) {
				archStatus = new ArchiveStatus();

				archStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				archStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				archStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				archStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				archStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				archStatus.setDate(resultSet.getString(DATECOLUMN));

				archStatuses.add(archStatus);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return archStatuses;
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("finally")
	public List<ArchiveStatus> getArchiveStatusOrderByVersion(String title) {

		List<ArchiveStatus> archStatuses = new ArrayList<ArchiveStatus>();
		ArchiveStatus archStatus = null;
		
		Connection connect = null;
		Statement statement = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();

			logger.debug("select a.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " a" + " where p."
					+ LOGIDENTIFIERCOLUMN + " = " + "a." + LOGIDENTIFIERCOLUMN + " and p." + Product.TITLECOLUMN + " = '"
					+ title + "' order by a." + VERSIONCOLUMN);
			resultSet = statement.executeQuery("select a.* from " + PRODUCTTABLENAME + " p, " + TABLENAME + " a"
					+ " where p." + LOGIDENTIFIERCOLUMN + " = " + "a." + LOGIDENTIFIERCOLUMN + " and p."
					+ Product.TITLECOLUMN + " = '" + title + "' order by a." + VERSIONCOLUMN);

			while (resultSet.next()) {
				archStatus = new ArchiveStatus();

				archStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				archStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				archStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				archStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				archStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				archStatus.setDate(resultSet.getString(DATECOLUMN));

				archStatuses.add(archStatus);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return archStatuses;
		}
	}
	
	/**
	 * Archive Status Query - Query the archive_status table for the latest archive status of a given product.
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public ArchiveStatus getLatestArchiveStatus(String logical_identifier, String ver) {

		ArchiveStatus archStatus = null;
		
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
				archStatus = new ArchiveStatus();

				archStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				archStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				archStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				archStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				archStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				archStatus.setDate(resultSet.getString(DATECOLUMN));
			}	
			else{
				logger.info("Can not find any Archive Status!");
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return archStatus;
		}
	}
	
	/**
	 * Archive Status List Query – Query the archive_status table for the archive status progression of a given product.
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public List<ArchiveStatus> getArchiveStatusList(String logical_identifier, String ver) {
		
		List<ArchiveStatus> archStatuses = new ArrayList<ArchiveStatus>();
		ArchiveStatus archStatus = null;
		
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
				archStatus = new ArchiveStatus();

				archStatus.setLogIdentifier(resultSet.getString(LOGIDENTIFIERCOLUMN));
				archStatus.setVersion(resultSet.getString(VERSIONCOLUMN));
				archStatus.setStatus(resultSet.getString(STATUSCOLUMN));
				archStatus.setEmail(resultSet.getString(EMAILCOLUMN));
				archStatus.setComment(resultSet.getString(COMMENTCOLUMN));
				archStatus.setDate(resultSet.getString(DATECOLUMN));
				
				archStatuses.add(archStatus);
			}	

		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return archStatuses;
		}
	}
	
	/**
	 * @param aStatus a object of ArchiveStatus
	 */
	public int insertArchiveStatus(ArchiveStatus aStatus) {
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
			prepareStm.setString(1, aStatus.getLogIdentifier());
			prepareStm.setString(2, aStatus.getVersion());
			prepareStm.setString(3, aStatus.getDate());
			prepareStm.setString(4, aStatus.getStatus());
			prepareStm.setString(5, aStatus.getEmail());
			prepareStm.setString(6, aStatus.getComment());
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The archive status " + aStatus.getStatus() + " for the product: " + aStatus.getLogIdentifier() + ", has been added.");
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
