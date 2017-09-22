/**
 * Copyright 2010-2017, by the California Institute of Technology.
 * 
 * The object class represents Delivery table.
 *  
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
/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class Delivery extends DBConnector {
	  
	  public static Logger logger = Logger.getLogger(Delivery.class);

		private static String TABLENAME  = "delivery";
		private static String PRODUCTTABLENAME = "product";
	
		public static String LOG_IDENTIFIERCOLUME  = "logical_identifier";
		public static String DEL_IDENTIFIERCOLUME  = "delivery_identifier";		
		public static String VERSIONCOLUME  = "version_id";
		public static String NAMECOLUME  = "name";
		public static String STARTCOLUME  = "start_date_time";
		public static String STOPCOLUME  = "stop_date_time";
		public static String SOURCECOLUME  = "source";
		public static String TARGETCOLUME  = "target";
		public static String DUEDATECOLUME = "due_date";
		
		private Connection connect = null;
		private Statement statement = null;
		private PreparedStatement prepareStm = null;
		private ResultSet resultSet = null;
		
		private String log_identifier = null;
		private int del_identifier = 0;
		private String version = null;
		private String name = null;
		private String start = null;
		private String stop = null;
		private String source = null;
		private String target = null;
		private String dueDate = null;
		
		/**
		 * @return the log_identifier
		 */
		public String getLogIdentifier() {
			return log_identifier;
		}

		/**
		 * @param log_identifier, the log_identifier to set
		 */
		public void setLogIdentifier(String log_identifier) {
			this.log_identifier = log_identifier;
		}

		/**
		 * @return the del_identifier
		 */
		public int getDelIdentifier() {
			return del_identifier;
		}

		/**
		 * @param del_identifier, the del_identifier to set
		 */
		public void setDelIdentifier(int del_identifier) {
			this.del_identifier = del_identifier;
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
		 * @return the start
		 */
		public String getStart() {
			return start;
		}

		/**
		 * @param start, the start to set
		 */
		public void setStart(String start) {
			this.start = start;
		}

		/**
		 * @return the stop
		 */
		public String getStop() {
			return stop;
		}

		/**
		 * @param stop, the stop to set
		 */
		public void setStop(String stop) {
			this.stop = stop;
		}

		/**
		 * @return the source
		 */
		public String getSource() {
			return source;
		}

		/**
		 * @param source, the source to set
		 */
		public void setSource(String source) {
			this.source = source;
		}

		/**
		 * @return the target
		 */
		public String getTarget() {
			return target;
		}

		/**
		 * @param target, the target to set
		 */
		public void setTarget(String target) {
			this.target = target;
		}

		/**
		 * @return the dueDate
		 */
		public String getDueDate() {
			return dueDate;
		}

		/**
		 * @param dueDate, the dueDate to set
		 */
		public void setDueDate(String dueDate) {
			this.dueDate = dueDate;
		}


	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Delivery() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return a list of all delivery objects
	 */
	@SuppressWarnings("finally")
	public List<Delivery> getDeliveries() {
		List<Delivery> delObjs = new ArrayList<Delivery>();
		Delivery  del = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from " 
												+ TABLENAME
												+ " order by " + DUEDATECOLUME);
			
			while (resultSet.next()) {
				del = new Delivery();
				
				del.setLogIdentifier(resultSet.getString(LOG_IDENTIFIERCOLUME));
				del.setDelIdentifier(resultSet.getInt(DEL_IDENTIFIERCOLUME));								
				del.setVersion(resultSet.getString(VERSIONCOLUME));				
				del.setName(resultSet.getString(NAMECOLUME));				
				del.setStart(resultSet.getString(STARTCOLUME));
				del.setStop(resultSet.getString(STOPCOLUME));
				del.setSource(resultSet.getString(SOURCECOLUME));
				del.setTarget(resultSet.getString(TARGETCOLUME));
				del.setDueDate(resultSet.getString(DUEDATECOLUME));

				delObjs.add(del);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
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
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			/*System.out.println("select * from " 
					+ productTableName + " p, " + tableName + " d"
					+ " where p." + log_identifierColume + " = " + "d." + log_identifierColume 
					+ " and p." + Product.titleColume + " = " + title
					+ " order by " + dueDateColume);*/
			resultSet = statement.executeQuery("select * from " 
												+ PRODUCTTABLENAME + " p, " + TABLENAME + " d"
												+ " where p." + LOG_IDENTIFIERCOLUME + " = " + "d." + LOG_IDENTIFIERCOLUME 
												+ " and p." + Product.TITLECOLUME + " = '" + title
												+ "' order by " + DUEDATECOLUME);
			
			while (resultSet.next()) {
				del = new Delivery();
				
				del.setLogIdentifier(resultSet.getString(LOG_IDENTIFIERCOLUME));
				del.setDelIdentifier(resultSet.getInt(DEL_IDENTIFIERCOLUME));								
				del.setVersion(resultSet.getString(VERSIONCOLUME));				
				del.setName(resultSet.getString(NAMECOLUME));				
				del.setStart(resultSet.getString(STARTCOLUME));
				del.setStop(resultSet.getString(STOPCOLUME));
				del.setSource(resultSet.getString(SOURCECOLUME));
				del.setTarget(resultSet.getString(TARGETCOLUME));
				del.setDueDate(resultSet.getString(DUEDATECOLUME));

				delObjs.add(del);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return delObjs;
		}
	}
	/**
	 * @return a list of delivery objects with the logical identifier and version id .
	 */
	@SuppressWarnings("finally")
	public List<Delivery> getProductDeliveries(String log_identifer, String version) {
		List<Delivery> delObjs = new ArrayList<Delivery>();
		Delivery  del = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();
			statement = connect.createStatement();
			/*System.out.println("select * from " 
								+ TABLENAME
								+ " where " + LOG_IDENTIFIERCOLUME + " = '" + log_identifer 
								+ "' and " + VERSIONCOLUME + " = '" + version
								+ "' order by " + DUEDATECOLUME);*/
			resultSet = statement.executeQuery("select * from " 
												+ TABLENAME
												+ " where " + LOG_IDENTIFIERCOLUME + " = '" + log_identifer 
												+ "' and " + VERSIONCOLUME + " = '" + version
												+ "' order by " + DUEDATECOLUME);
			
			while (resultSet.next()) {
				del = new Delivery();
				
				del.setLogIdentifier(resultSet.getString(LOG_IDENTIFIERCOLUME));
				del.setDelIdentifier(resultSet.getInt(DEL_IDENTIFIERCOLUME));								
				del.setVersion(resultSet.getString(VERSIONCOLUME));				
				del.setName(resultSet.getString(NAMECOLUME));				
				del.setStart(resultSet.getString(STARTCOLUME));
				del.setStop(resultSet.getString(STOPCOLUME));
				del.setSource(resultSet.getString(SOURCECOLUME));
				del.setTarget(resultSet.getString(TARGETCOLUME));
				del.setDueDate(resultSet.getString(DUEDATECOLUME));

				delObjs.add(del);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return delObjs;
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

	/**
	 * @param logicalIdentifier
	 * @param versionId
	 * @param name
	 * @param startDateTime
	 * @param stopDateTime
	 * @param source
	 * @param target
	 * @param dueDate
	 * @return Delivery Identifier
	 */
	public int insertDelivery(String logicalIdentifier, String versionId, String name, String startDateTime,
			String stopDateTime, String source, String target, String dueDate) {
		
		int newDelIdentifer = -1;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" 
												+ LOG_IDENTIFIERCOLUME + ", "
												+ VERSIONCOLUME + ", "
												+ NAMECOLUME + ", "
												+ STARTCOLUME + ", "
												+ STOPCOLUME + ", "
												+ SOURCECOLUME + ", "
												+ TARGETCOLUME + ", "
												+ DUEDATECOLUME + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement. RETURN_GENERATED_KEYS);
			prepareStm.setString(1, logicalIdentifier);
			prepareStm.setString(2, versionId);
			prepareStm.setString(3, name);
			prepareStm.setString(4, startDateTime);
			prepareStm.setString(5, stopDateTime);
			prepareStm.setString(6, source);
			prepareStm.setString(7, target);
			prepareStm.setString(8, dueDate);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			resultSet = prepareStm.getGeneratedKeys();
			while (resultSet.next()) {
				newDelIdentifer = resultSet.getInt(1);
				logger.info("New Delivery Identifier: " + newDelIdentifer);
				
			}
			logger.info("The delivery " + name + " has been added.");
			
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
		return newDelIdentifer;
	}

	/**
	 * @param name
	 * @param startDateTime
	 * @param stopDateTime
	 * @param source
	 * @param target
	 * @param dueDate
	 * @param delIdentifier
	 */
	public void updateDelivery(String name, String startDateTime, String stopDateTime, String source, String target,
			String dueDate, String delIdentifier) {
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " + NAMECOLUME + " = ?, " 
																			   + STARTCOLUME + " = ?, "
																			   + STOPCOLUME + " = ?, "
																			   + SOURCECOLUME + " = ?, "
																			   + TARGETCOLUME + " = ?, "
																			   + DUEDATECOLUME + " = ? "
													+ "WHERE " + DEL_IDENTIFIERCOLUME + " = ?");
			
			prepareStm.setString(1, name);
			prepareStm.setString(2, startDateTime);
			prepareStm.setString(3, stopDateTime);
			prepareStm.setString(4, source);
			prepareStm.setString(5, target);
			prepareStm.setString(6, dueDate);
			prepareStm.setString(7, delIdentifier);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The delivery " + name + " has been updated.");
			
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
}
