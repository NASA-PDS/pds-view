/**
 * Copyright 2010-2017, by the California Institute of Technology.
 * 
 * The object class represents Product table.
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
public class Product extends DBConnector {

	public static Logger logger = Logger.getLogger(Product.class);

	private static final String TABLENAME = "product";

	public static final String IDENTIFIERCOLUME  = "logical_identifier";	
	public static final String VERSIONCOLUME  = "version_id";	
	public static final String TITLECOLUME  = "title";
	public static final String TYPECOLUME  = "type";
	public static final String ALTERNATECOLUME = "alternate_id";

	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement prepareStm = null;
	private ResultSet resultSet = null;
	
	private String identifier = null;
	private String version = null;
	private String title = null;
	private String type = null;
	private String alternate = null;

	/**
	 * @return
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return
	 */
	public String getAlternate() {
		return alternate;
	}

	/**
	 * @param alternate
	 */
	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Product() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param type (can be null)
	 * @return a list of all products by type (optional) order by title
	 */
	@SuppressWarnings("finally")
	public List<Product> getProducts(String type) {
		List<Product> prodObjs = new ArrayList<Product>();
		Product  prod = null;
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);

			statement = connect.createStatement();
			if(type != null && type.length() > 0) {
				
				resultSet = statement.executeQuery("select * from " + TABLENAME 
						+ " where " + TYPECOLUME + " = '" + type
						+ "' order by " + TITLECOLUME);
			}
			else{
				resultSet = statement.executeQuery("select * from " + TABLENAME + " order by " + TITLECOLUME);
			}
			
			while (resultSet.next()) {
				prod = new Product();
				prod.setIdentifier(resultSet.getString(IDENTIFIERCOLUME));
				prod.setVersion(resultSet.getString(VERSIONCOLUME));
				prod.setTitle(resultSet.getString(TITLECOLUME));
				prod.setType(resultSet.getString(TYPECOLUME));
				prod.setAlternate(resultSet.getString(ALTERNATECOLUME));

				prodObjs.add(prod);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return prodObjs;
		}
	}
	
	/**
	 * @return a list of all products order by title 
	 */
	@SuppressWarnings("finally")
	public List<Product> getProductsOrderByTitle() {
		List<Product> prodObjs = new ArrayList<Product>();
		Product  prod = null;
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);

			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from " + TABLENAME + " order by " + TITLECOLUME);
			
			while (resultSet.next()) {
				prod = new Product();
				prod.setIdentifier(resultSet.getString(IDENTIFIERCOLUME));
				prod.setVersion(resultSet.getString(VERSIONCOLUME));
				prod.setTitle(resultSet.getString(TITLECOLUME));
				prod.setType(resultSet.getString(TYPECOLUME));
				prod.setAlternate(resultSet.getString(ALTERNATECOLUME));

				prodObjs.add(prod);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return prodObjs;
		}
	}
	
	/**
	 * @param logical identifier
	 * @param version
	 * @return a product with logical identifier and version
	 */
	@SuppressWarnings("finally")
	public Product getProduct(String idf, String ver) {
		Product prodObj = null;
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);
			
			String query = "select * from " + TABLENAME 
							+ " where " + IDENTIFIERCOLUME + " = '" + idf 
							+ "' AND " + VERSIONCOLUME + " = '" + ver  + "'";
			statement = connect.createStatement();
			
			resultSet = statement.executeQuery(query);
			
			while (resultSet.next()) {
				prodObj = new Product();
				prodObj.setIdentifier(resultSet.getString(IDENTIFIERCOLUME));
				prodObj.setVersion(resultSet.getString(VERSIONCOLUME));
				prodObj.setTitle(resultSet.getString(TITLECOLUME));
				prodObj.setType(resultSet.getString(TYPECOLUME));
				prodObj.setAlternate(resultSet.getString(ALTERNATECOLUME));
			
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return prodObj;
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
	 * @param title
	 * @param type
	 * @param alternateId
	 */
	public void insertProduct(String logicalIdentifier, String versionId, String title, String type,
			String alternateId) {
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME 
												+ " (" + IDENTIFIERCOLUME + ", " 
												+ VERSIONCOLUME  + ", "
												+ TITLECOLUME  + ", "
												+ TYPECOLUME  + ", "
												+ ALTERNATECOLUME
												+ ") VALUES (?, ?, ?, ?, ?)");
			prepareStm.setString(1, logicalIdentifier);
			prepareStm.setString(2, versionId);
			prepareStm.setString(3, title);
			prepareStm.setString(4, type);
			prepareStm.setString(5, alternateId);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The product " + title + " with the type: " + type + ", has been added.");
			
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
	 * @param logicalIdentifier
	 * @param versionId
	 * @param title2
	 * @param type2
	 * @param alternateId
	 */
	public void updateProduct(String logicalIdentifier, String versionId, String title, String type,
			String alternateId) {
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);
			connect.setAutoCommit(false);

			prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " + TITLECOLUME + " = ?, "
																				  + TYPECOLUME + " = ?, "
																				  + ALTERNATECOLUME + " = ? "
																				  + "WHERE " + IDENTIFIERCOLUME + " = ? "
																				  + "AND " + VERSIONCOLUME + " = ?");
			
			prepareStm.setString(1, title);
			prepareStm.setString(2, type);
			prepareStm.setString(3, alternateId);
			prepareStm.setString(4, logicalIdentifier);
			prepareStm.setString(5, versionId);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The product " + title + " with the type: " + type + ", has been updated.");
			
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
