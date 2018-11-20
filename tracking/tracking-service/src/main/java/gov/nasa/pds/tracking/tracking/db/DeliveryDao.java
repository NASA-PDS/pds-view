/**
 * Copyright 2010-2017, by the California Institute of Technology.
 * 
 * The object class represents Delivery table.
 *  
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
public class DeliveryDao extends DBConnector {
	  
	  public static Logger logger = Logger.getLogger(DeliveryDao.class);

		private static String TABLENAME  = "delivery";
		private static String PRODUCTTABLENAME = "product";
	
		public static String LOG_IDENTIFIERCOLUMN  = "logical_identifier";
		public static String DEL_IDENTIFIERCOLUMN  = "delivery_identifier";		
		public static String VERSIONCOLUMN  = "version_id";
		public static String NAMECOLUMN  = "name";
		public static String STARTCOLUMN  = "start_date_time";
		public static String STOPCOLUMN  = "stop_date_time";
		public static String SOURCECOLUMN  = "source";
		public static String TARGETCOLUMN  = "target";
		public static String DUEDATECOLUMN = "due_date";
		
		private ResultSet resultSet = null;

	public DeliveryDao() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	public Delivery getDelivery(int delID) {
		
		Delivery  del = null;
		
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from " 
												+ TABLENAME 
												+ " WHERE " + DEL_IDENTIFIERCOLUMN + " = ?");
			
			while (resultSet.next()) {
				del = new Delivery();
				
				del.setLogIdentifier(resultSet.getString(LOG_IDENTIFIERCOLUMN));
				del.setDelIdentifier(resultSet.getInt(DEL_IDENTIFIERCOLUMN));								
				del.setVersion(resultSet.getString(VERSIONCOLUMN));				
				del.setName(resultSet.getString(NAMECOLUMN));				
				del.setStart(resultSet.getString(STARTCOLUMN));
				del.setStop(resultSet.getString(STOPCOLUMN));
				del.setSource(resultSet.getString(SOURCECOLUMN));
				del.setTarget(resultSet.getString(TARGETCOLUMN));
				del.setDueDate(resultSet.getString(DUEDATECOLUMN));
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
		}
		return del;
	}
	/**
	 * @return a list of all delivery objects
	 */
	@SuppressWarnings("finally")
	public List<Delivery> getDeliveries() {
		List<Delivery> delObjs = new ArrayList<Delivery>();
		Delivery  del = null;
		
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from " 
												+ TABLENAME
												+ " order by " + DUEDATECOLUMN);
			
			while (resultSet.next()) {
				del = new Delivery();
				
				del.setLogIdentifier(resultSet.getString(LOG_IDENTIFIERCOLUMN));
				del.setDelIdentifier(resultSet.getInt(DEL_IDENTIFIERCOLUMN));								
				del.setVersion(resultSet.getString(VERSIONCOLUMN));				
				del.setName(resultSet.getString(NAMECOLUMN));				
				del.setStart(resultSet.getString(STARTCOLUMN));
				del.setStop(resultSet.getString(STOPCOLUMN));
				del.setSource(resultSet.getString(SOURCECOLUMN));
				del.setTarget(resultSet.getString(TARGETCOLUMN));
				del.setDueDate(resultSet.getString(DUEDATECOLUMN));

				delObjs.add(del);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return delObjs;
		}
	}
	
	/**
	 * @return a list of delivery objects with the title.
	 */
	@SuppressWarnings("finally")
	public List<Delivery> getDeliveriesOrderByDueDate(String title) {
		List<Delivery> delObjs = new ArrayList<Delivery>();
		Delivery  del = null;
		
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			logger.debug("select * from " 
					+ PRODUCTTABLENAME + " p, " + TABLENAME + " d"
					+ " where p." + LOG_IDENTIFIERCOLUMN + " = " + "d." + LOG_IDENTIFIERCOLUMN 
					+ " and p." + ProductDao.TITLECOLUMN + " = '" + title
					+ "' order by " + DUEDATECOLUMN);
			resultSet = statement.executeQuery("select * from " 
												+ PRODUCTTABLENAME + " p, " + TABLENAME + " d"
												+ " where p." + LOG_IDENTIFIERCOLUMN + " = " + "d." + LOG_IDENTIFIERCOLUMN 
												+ " and p." + ProductDao.TITLECOLUMN + " = '" + title
												+ "' order by " + DUEDATECOLUMN);
			
			while (resultSet.next()) {
				del = new Delivery();
				
				del.setLogIdentifier(resultSet.getString(LOG_IDENTIFIERCOLUMN));
				del.setDelIdentifier(resultSet.getInt(DEL_IDENTIFIERCOLUMN));								
				del.setVersion(resultSet.getString(VERSIONCOLUMN));				
				del.setName(resultSet.getString(NAMECOLUMN));				
				del.setStart(resultSet.getString(STARTCOLUMN));
				del.setStop(resultSet.getString(STOPCOLUMN));
				del.setSource(resultSet.getString(SOURCECOLUMN));
				del.setTarget(resultSet.getString(TARGETCOLUMN));
				del.setDueDate(resultSet.getString(DUEDATECOLUMN));

				delObjs.add(del);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return delObjs;
		}
	}
	/**
	 * Delivery Query - Query the delivery table for a list of deliveries for a given product.
	 * @return a list of delivery objects with the logical identifier and version id .
	 */
	@SuppressWarnings("finally")
	public List<Delivery> getProductDeliveries(String log_identifer, String version) {
		List<Delivery> delObjs = new ArrayList<Delivery>();
		Delivery  del = null;
		
		Connection connect = null;
		Statement statement = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
			statement = connect.createStatement();
			
			logger.debug("select * from " 
					+ TABLENAME
					+ " where " + LOG_IDENTIFIERCOLUMN + " = '" + log_identifer 
					+ "' and " + VERSIONCOLUMN + " = '" + version
					+ "' order by " + DUEDATECOLUMN);
			resultSet = statement.executeQuery("select * from " 
												+ TABLENAME
												+ " where " + LOG_IDENTIFIERCOLUMN + " = '" + log_identifer 
												+ "' and " + VERSIONCOLUMN + " = '" + version
												+ "' order by " + DUEDATECOLUMN);
			
			while (resultSet.next()) {
				del = new Delivery();
				
				del.setLogIdentifier(resultSet.getString(LOG_IDENTIFIERCOLUMN));
				del.setDelIdentifier(resultSet.getInt(DEL_IDENTIFIERCOLUMN));								
				del.setVersion(resultSet.getString(VERSIONCOLUMN));				
				del.setName(resultSet.getString(NAMECOLUMN));				
				del.setStart(resultSet.getString(STARTCOLUMN));
				del.setStop(resultSet.getString(STOPCOLUMN));
				del.setSource(resultSet.getString(SOURCECOLUMN));
				del.setTarget(resultSet.getString(TARGETCOLUMN));
				del.setDueDate(resultSet.getString(DUEDATECOLUMN));

				delObjs.add(del);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return delObjs;
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

	/**
	 * @param Delivery del
	 */
	public int insertDelivery(Delivery del) {
		
		int newDelIdentifer = -1;
		
		Connection connect = null;
		PreparedStatement prepareStm = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" 
												+ LOG_IDENTIFIERCOLUMN + ", "
												+ VERSIONCOLUMN + ", "
												+ NAMECOLUMN + ", "
												+ STARTCOLUMN + ", "
												+ STOPCOLUMN + ", "
												+ SOURCECOLUMN + ", "
												+ TARGETCOLUMN + ", "
												+ DUEDATECOLUMN + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			prepareStm.setString(1, del.getLogIdentifier());
			prepareStm.setString(2, del.getVersion());
			prepareStm.setString(3, del.getName());
			prepareStm.setString(4, del.getStart());
			prepareStm.setString(5, del.getStop());
			prepareStm.setString(6, del.getSource());
			prepareStm.setString(7, del.getTarget());
			prepareStm.setString(8, del.getDueDate());
			
			prepareStm.executeUpdate();
			
			connect.commit();
			resultSet = prepareStm.getGeneratedKeys();
			while (resultSet.next()) {
				newDelIdentifer = resultSet.getInt(1);
				logger.info("New Delivery Identifier: " + newDelIdentifer);
				
			}
			logger.info("The delivery " + del.getName() + " has been added.");
			
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
		return newDelIdentifer;
	}

	/**
	 * @param Delivery del
	 */
	public Delivery updateDelivery(Delivery del) {
		
		
		Delivery updatedDel = null;
		Connection connect = null;
		PreparedStatement prepareStm = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " + NAMECOLUMN + " = ?, " 
																			   + STARTCOLUMN + " = ?, "
																			   + STOPCOLUMN + " = ?, "
																			   + SOURCECOLUMN + " = ?, "
																			   + TARGETCOLUMN + " = ?, "
																			   + DUEDATECOLUMN + " = ? "
													+ "WHERE " + DEL_IDENTIFIERCOLUMN + " = ?");
			
			prepareStm.setString(1, del.getName());
			prepareStm.setString(2, del.getStart());
			prepareStm.setString(3, del.getStop());
			prepareStm.setString(4, del.getSource());
			prepareStm.setString(5, del.getTarget());
			prepareStm.setString(6, del.getDueDate());
			prepareStm.setInt(7, del.getDelIdentifier());
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The delivery " + del.getName() + " has been updated.");
			updatedDel = getDelivery(del.getDelIdentifier());
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
		return updatedDel;
		
	}
}
