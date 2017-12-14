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

public class Product extends DBConnector {

	public static Logger logger = Logger.getLogger(Product.class);

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

	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement prepareStm = null;
	private ResultSet resultSet = null;
	
	private String identifier = null;
	private String version = null;
	private String title = null;
	private String type = null;
	private String alternate = null;
	private String instRef = null;
	/**
	 * @return the instRef
	 */
	public String getInstRef() {
		return instRef;
	}

	/**
	 * @param instRef, the instRef to set
	 */
	public void setInstRef(String instRef) {
		this.instRef = instRef;
	}

	/**
	 * @return the instTitle
	 */
	public String getInstTitle() {
		return instTitle;
	}

	/**
	 * @param instTitle, the instTitle to set
	 */
	public void setInstTitle(String instTitle) {
		this.instTitle = instTitle;
	}

	/**
	 * @return the inveRef
	 */
	public String getInveRef() {
		return inveRef;
	}

	/**
	 * @param inveRef, the inveRef to set
	 */
	public void setInveRef(String inveRef) {
		this.inveRef = inveRef;
	}

	/**
	 * @return the inveTitle
	 */
	public String getInveTitle() {
		return inveTitle;
	}

	/**
	 * @param inveTitle, the inveTitle to set
	 */
	public void setInveTitle(String inveTitle) {
		this.inveTitle = inveTitle;
	}

	/**
	 * @return the nodeRef
	 */
	public String getNodeRef() {
		return nodeRef;
	}

	/**
	 * @param nodeRef, the nodeRef to set
	 */
	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	/**
	 * @return the nodeTitle
	 */
	public String getNodeTitle() {
		return nodeTitle;
	}

	/**
	 * @param nodeTitle, the nodeTitle to set
	 */
	public void setNodeTitle(String nodeTitle) {
		this.nodeTitle = nodeTitle;
	}

	private String instTitle = null;
	private String inveRef = null;
	private String inveTitle = null;
	private String nodeRef = null;
	private String nodeTitle = null;

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
			close(statement);
			return prodObjs;
		}
	}
	
	/**
	 * @return a list of all products that have associated deliveries order by title 
	 */
	@SuppressWarnings("finally")
	public List<Product> getProductsAssociatedDeliveriesOrderByTitle(String instRef, String investRef) {
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
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME 
												+ " (" + IDENTIFIERCOLUMN + ", " 
												+ VERSIONCOLUMN  + ", "
												+ TITLECOLUMN  + ", "
												+ TYPECOLUMN  + ", "
												+ ALTERNATECOLUMN
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
			connect = getConnection();
			connect.setAutoCommit(false);

			prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " + TITLECOLUMN + " = ?, "
																				  + TYPECOLUMN + " = ?, "
																				  + ALTERNATECOLUMN + " = ? "
																				  + "WHERE " + IDENTIFIERCOLUMN + " = ? "
																				  + "AND " + VERSIONCOLUMN + " = ?");
			
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
