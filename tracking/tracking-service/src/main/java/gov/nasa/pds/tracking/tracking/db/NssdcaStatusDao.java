package gov.nasa.pds.tracking.tracking.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class NssdcaStatusDao extends DBConnector {
	
	public static Logger logger = Logger.getLogger(NssdcaStatusDao.class);

	private static final String TABLENAME = "nssdca_status";
	private static final String PRODUCTTABLENAME= "product";
  
	public static final String LOGIDENTIFIERCOLUMN = "logical_identifier";
	public static final String VERSIONCOLUMN = "version_id";
	public static final String DATECOLUMN = "status_date_time";
	public static final String NSSDCACOLUMN = "nssdca_identifier";
	public static final String EMAILCOLUMN = "electronic_mail_address";
	public static final String COMMENTCOLUMN = "comment";

	private ResultSet resultSet = null;
	
	public NssdcaStatusDao() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	public int insertNssdcaStatus(NssdcaStatus nStatus) {
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
													+ NSSDCACOLUMN + ", "
													+ EMAILCOLUMN + ", "
													+ COMMENTCOLUMN + ") VALUES (?, ?, ?, ?, ?, ?)");
			prepareStm.setString(1, nStatus.getLogIdentifier());
			prepareStm.setString(2, nStatus.getVersion());
			prepareStm.setString(3, nStatus.getDate());
			prepareStm.setString(4, nStatus.getNssdca());
			prepareStm.setString(5, nStatus.getEmail());
			prepareStm.setString(6, nStatus.getComment());
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The NSSDCA status " + nStatus.getNssdca() + " for the product: " + nStatus.getLogIdentifier() + ", has been added.");
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
	 * NSSDCA Query - Query the nssdca table for the NSSDCA information of a given product.
	 * @param logical_identifier
	 * @param ver
	 * @return
	 */
	@SuppressWarnings("finally")
	public List<NssdcaStatus> getNssdcaStatusList(String logical_identifier, String ver) {
		
		List<NssdcaStatus> nssdcaStatuses = new ArrayList<NssdcaStatus>();
		NssdcaStatus nssdcaStatus = null;
		
		Connection connect = null;
		Statement statement = null;
		
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
			close(connect, statement);
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
		
		Connection connect = null;
		Statement statement = null;
		
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
			close(connect, statement);
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
		
		Connection connect = null;
		Statement statement = null;
		
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
			close(connect, statement);
			return nStatuses;
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
