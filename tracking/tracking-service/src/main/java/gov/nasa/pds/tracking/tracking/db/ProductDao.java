/**
 * Copyright 2010-2017, by the California Institute of Technology.
 * 
 * The object class represents Product table.
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

public class ProductDao extends DBConnector {

	public static Logger logger = Logger.getLogger(ProductDao.class);

	private static final String TABLENAME = "product";
	private static final String DELIVERYTABLENAME = "delivery";

	public static final String IDENTIFIERCOLUMN  = "logical_identifier";	
	public static final String VERSIONCOLUMN  = "version_id";	
	public static final String TITLECOLUMN  = "title";
	public static final String TYPECOLUMN  = "type";
	public static final String ALTERNATECOLUMN = "alternate_id";
	
	public static final String INVESTREFCOLUMN = "investigation_reference";	
	public static final String INVESTTITLECOLUMN = "investigation_title";
	public static final String INSTREFCOLUMN = "instrument_reference";
	public static final String INSTTITLECOLUMN = "instrument_title";
	public static final String NODEREFCOLUMN = "node_reference";
	public static final String NODETITLECOLUMN = "node_title";

	private ResultSet resultSet = null;

	public ProductDao() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Product Query - Query the product table for a list of products by type.
	 * @param type (can be null)
	 * @return a list of all products by type (optional) order by title
	 */
	@SuppressWarnings("finally")
	public List<Product> getProducts(String type) {
		
		Connection connect = null;
		Statement statement = null;
		
		List<Product> prodObjs = new ArrayList<Product>();
		Product  prod = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			if(type != null && type.length() > 0) {
				
				resultSet = statement.executeQuery("select * from " + TABLENAME 
						+ " where " + TYPECOLUMN + " = '" + type
						+ "' order by " + TITLECOLUMN);
			}
			else{
				resultSet = statement.executeQuery("select * from " + TABLENAME + " order by " + TITLECOLUMN);
			}
			
			while (resultSet.next()) {
				prod = new Product();
				prod.setIdentifier(resultSet.getString(IDENTIFIERCOLUMN));
				prod.setVersion(resultSet.getString(VERSIONCOLUMN));
				prod.setTitle(resultSet.getString(TITLECOLUMN));
				prod.setType(resultSet.getString(TYPECOLUMN));
				prod.setAlternate(resultSet.getString(ALTERNATECOLUMN));

				prodObjs.add(prod);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return prodObjs;
		}
	}
	
	/**
	 * @return a list of all products order by title 
	 */
	@SuppressWarnings("finally")
	public List<Product> getProductsOrderByTitle() {
		
		Connection connect = null;
		Statement statement = null;
		
		List<Product> prodObjs = new ArrayList<Product>();
		Product  prod = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from " + TABLENAME + " order by " + TITLECOLUMN);
			
			while (resultSet.next()) {
				prod = new Product();
				prod.setIdentifier(resultSet.getString(IDENTIFIERCOLUMN));
				prod.setVersion(resultSet.getString(VERSIONCOLUMN));
				prod.setTitle(resultSet.getString(TITLECOLUMN));
				prod.setType(resultSet.getString(TYPECOLUMN));
				prod.setAlternate(resultSet.getString(ALTERNATECOLUMN));

				prodObjs.add(prod);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return prodObjs;
		}
	}
	
	/**
	 * Product Delivery Query - Query the product, delivery and *_reference tables for a list of products that have associated deliveries.
	 * @return a list of all products that have associated deliveries order by title 
	 */
	@SuppressWarnings("finally")
	public List<Product> getProductsAssociatedDeliveriesOrderByTitle(String instRef, String investRef) {
		
		Connection connect = null;
		Statement statement = null;
		
		List<Product> prodObjs = new ArrayList<Product>();
		Product  prod = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			String defaultQueryPartOne = "select DISTINCT p.*, ivr.reference AS investigation_reference, " +
										"ivr.title AS investigation_title, " +
										"isr.reference AS instrument_reference, " +
										"isr.title AS instrument_title, " +
										"nr.reference AS node_reference, " +
										"nr.title AS node_title " +
							"from " + TABLENAME + " p, " + 
									DELIVERYTABLENAME + " d, " + 
									Reference.INVES_TABLENAME + " ivr," + 
									Reference.INST_TABLENAME + " isr," + 
									Reference.NODE_TABLENAME + " nr " +
							"where p." + IDENTIFIERCOLUMN + " = d." + IDENTIFIERCOLUMN +
								" and p." + VERSIONCOLUMN + " = d." + VERSIONCOLUMN + 
								" and p." + IDENTIFIERCOLUMN + " = ivr." + IDENTIFIERCOLUMN +
								" and p." + IDENTIFIERCOLUMN + " = isr." + IDENTIFIERCOLUMN +
								" and p." + IDENTIFIERCOLUMN + " = nr." + IDENTIFIERCOLUMN;
			String defaultQueryOrder = " order by " + TITLECOLUMN;
			
			String queryInstRefPart = "";
			String queryInveRefPart = "";
			if (instRef != null && !instRef.equalsIgnoreCase("null") && instRef.length() > 0)
				queryInstRefPart = " and isr." + Reference.REFERENCECOLUMN + " = '" + instRef + "'";
			if (investRef != null && !investRef.equalsIgnoreCase("null") && investRef.length() > 0)
				queryInveRefPart = " and ivr." + Reference.REFERENCECOLUMN + " = '" + investRef + "'";
			
			String query = defaultQueryPartOne + queryInstRefPart + queryInveRefPart + defaultQueryOrder;
										
			logger.debug(query);
			
			resultSet = statement.executeQuery(query);
			
			while (resultSet.next()) {
				prod = new Product();
				prod.setIdentifier(resultSet.getString(IDENTIFIERCOLUMN));
				prod.setVersion(resultSet.getString(VERSIONCOLUMN));
				prod.setTitle(resultSet.getString(TITLECOLUMN));
				prod.setType(resultSet.getString(TYPECOLUMN));
				prod.setAlternate(resultSet.getString(ALTERNATECOLUMN));
				prod.setInstRef(resultSet.getString(INSTREFCOLUMN));
				prod.setInstTitle(resultSet.getString(INSTTITLECOLUMN));
				prod.setInveRef(resultSet.getString(INVESTREFCOLUMN));
				prod.setInveTitle(resultSet.getString(INVESTTITLECOLUMN));
				prod.setNodeRef(resultSet.getString(NODEREFCOLUMN));
				prod.setNodeTitle(resultSet.getString(NODETITLECOLUMN));

				prodObjs.add(prod);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
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
		
		Connection connect = null;
		Statement statement = null;
		
		Product prodObj = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();
			
			String query = "select * from " + TABLENAME 
							+ " where " + IDENTIFIERCOLUMN + " = '" + idf 
							+ "' AND " + VERSIONCOLUMN + " = '" + ver  + "'";
			statement = connect.createStatement();
			
			resultSet = statement.executeQuery(query);
			
			while (resultSet.next()) {
				prodObj = new Product();
				prodObj.setIdentifier(resultSet.getString(IDENTIFIERCOLUMN));
				prodObj.setVersion(resultSet.getString(VERSIONCOLUMN));
				prodObj.setTitle(resultSet.getString(TITLECOLUMN));
				prodObj.setType(resultSet.getString(TYPECOLUMN));
				prodObj.setAlternate(resultSet.getString(ALTERNATECOLUMN));
			
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(connect, statement);
			return prodObj;
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
	 * @param prod - an object of product
	 */
	public int insertProduct(Product prod) {
		int success = 0;
		
		Connection connect = null;
		PreparedStatement prepareStm = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME 
												+ " (" + IDENTIFIERCOLUMN + ", " 
												+ VERSIONCOLUMN  + ", "
												+ TITLECOLUMN  + ", "
												+ TYPECOLUMN  + ", "
												+ ALTERNATECOLUMN
												+ ") VALUES (?, ?, ?, ?, ?)");
			prepareStm.setString(1, prod.getIdentifier());
			prepareStm.setString(2, prod.getVersion());
			prepareStm.setString(3, prod.getTitle());
			prepareStm.setString(4, prod.getType());
			prepareStm.setString(5, prod.getAlternate());
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The product " + prod.getTitle() + " with the type: " + prod.getType() + ", has been added.");
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
	 * @param prod - an object of product
	 */
	public Product updateProduct(Product prod) {
		
		Product prodUpdated = null;
		
		Connection connect = null;
		PreparedStatement prepareStm = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			boolean hasAlternate = false;
			String setAlternate = "";
			if (prod.getAlternate() != null && prod.getAlternate().length() > 0){
				hasAlternate = true;
				setAlternate = ", " + ALTERNATECOLUMN + " = ? ";
			}

			prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " + TITLECOLUMN + " = ?, "
																				  + TYPECOLUMN + " = ?"
																				  + setAlternate
																				  + " WHERE " + IDENTIFIERCOLUMN + " = ? "
																				  + "AND " + VERSIONCOLUMN + " = ?");
			
			prepareStm.setString(1, prod.getTitle());
			prepareStm.setString(2, prod.getType());
			if (hasAlternate)
				prepareStm.setString(3, prod.getAlternate());
			prepareStm.setString(hasAlternate ? 4:3, prod.getIdentifier());
			prepareStm.setString(hasAlternate ? 5:4, prod.getVersion());
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The product " + prod.getTitle() + " with the type: " + prod.getType() + ", has been updated.");

			prodUpdated = getProduct(prod.getIdentifier(), prod.getVersion());
			
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
		return prodUpdated;
	}
}
