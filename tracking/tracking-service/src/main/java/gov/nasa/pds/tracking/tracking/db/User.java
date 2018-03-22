/**
 * Copyright 2010-2017, by the California Institute of Technology. 
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
 * @author danyu Dan.Yu@jpl.nasa.gov
 *
 */
public class User extends DBConnector {

	public static Logger logger = Logger.getLogger(User.class);

	private static final String TABLENAME = "user";
	private static final String ROLETABLENAME = "role";
	private static final String PRODUCTTABLENAME = "product";
	
	public static final String NAMECOLUMN = "name";
	public static final String EMAILCOLUMN = "electronic_mail_address";


	private Connection connect = null;
	private PreparedStatement prepareStm = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	private String userName = null;
	private String userEmail = null;
	private String reference = null;
	private String type = null;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type, the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference, the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return the user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the email of the user
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public User() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}

	/**
	 * User Query – Query the user table for a list of users.
	 * @return a list of users
	 */
	@SuppressWarnings("finally")
	public List<User> getUsers() {
		List<User> userObjs = new ArrayList<User>();
		User user = null;
		try {
			// Setup the connection with the DB
			connect = getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from " + TABLENAME + " order by " + EMAILCOLUMN);
			
			while (resultSet.next()) {
				user = new User();
				user.setUserEmail(resultSet.getString(EMAILCOLUMN));
				user.setUserName(resultSet.getString(NAMECOLUMN));
				userObjs.add(user);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return userObjs;
		}
	}
	
/**
 * User Role Query - Query the user and role tables for a list of roles for a given user.
 * @param role
 * @return a list of roles for a given user (email)
 */
@SuppressWarnings("finally")
public List<User> getUserRole(String email){
	List<User> userRoles = new ArrayList<User>();
	User user = null;
	try {
		// Setup the connection with the DB
		connect = getConnection();
		statement = connect.createStatement();
		String query = "SELECT u." + EMAILCOLUMN + ", u." + NAMECOLUMN + ", r." + Role.REFERENCECOLUMN +
				" FROM " + TABLENAME + " u, " + ROLETABLENAME + " r" +
				" WHERE u." + EMAILCOLUMN + " = '" + email + "' AND u." + EMAILCOLUMN + " = r." + EMAILCOLUMN +
				" ORDER BY r." + Role.REFERENCECOLUMN;
		
		/*		" WHERE ";
		if (email !=null &&  !email.equalsIgnoreCase("null") && email.length() > 0){
			query = query + "u." + EMAILCOLUMN + " = '" + email + "' AND ";
		}
		
		query = query + "u." + EMAILCOLUMN + " = r." + EMAILCOLUMN +
				" ORDER BY r." + Role.REFERENCECOLUMN;
		*/
		resultSet = statement.executeQuery(query);
		
		while (resultSet.next()) {
			user = new User();
			user.setUserEmail(resultSet.getString(EMAILCOLUMN));
			user.setUserName(resultSet.getString(NAMECOLUMN));
			user.setReference(resultSet.getString(Role.REFERENCECOLUMN));
			userRoles.add(user);
		}
		
	} catch (Exception e) {
		logger.error(e);
	} finally {
		close(statement);
		return userRoles;
	}
}

/**
 * Product instrument/investigation/node Role Query – Query the product, instrument/investigation/node_reference, role and user tables for a list of users with the instrument/investigation/node role for a product.
 * @param log_identifer
 * @param type
 * @return a list of users
 */
public List<User> getProductRoleUsers(String log_identifer,String tableName) {
	
	List<User> productRoleUsers = new ArrayList<User>();
	
	if (tableName.equalsIgnoreCase(Reference.INST_TABLENAME) || tableName.equalsIgnoreCase(Reference.INVES_TABLENAME) 
			||tableName.equalsIgnoreCase(Reference.NODE_TABLENAME)){
		User user = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();
	
			statement = connect.createStatement();
			
			logger.debug("SELECT u." + EMAILCOLUMN + ", u." + NAMECOLUMN + ", f." + Reference.TITLECOLUMN + ", f." + Reference.REFERENCECOLUMN +
											" FROM " + TABLENAME + " u, " + ROLETABLENAME + " r, " + tableName + " f, " + PRODUCTTABLENAME + " p" +
											" where p." + Product.IDENTIFIERCOLUMN + " = '" + log_identifer +
											"' and p." + Product.IDENTIFIERCOLUMN + " = f." + Reference.LOG_IDENTIFIERCOLUMN +
											" and f." + Reference.REFERENCECOLUMN + " = r." +  Role.REFERENCECOLUMN +
											" and r." + Role.EMAILCOLUMN + " = u." + EMAILCOLUMN +
											" ORDER BY " + EMAILCOLUMN);
			
			resultSet = statement.executeQuery("SELECT u." + EMAILCOLUMN + ", u." + NAMECOLUMN + ", f." + Reference.TITLECOLUMN + ", f." + Reference.REFERENCECOLUMN +
											" FROM " + TABLENAME + " u, " + ROLETABLENAME + " r, " + tableName + " f, " + PRODUCTTABLENAME + " p" +
											" where p." + Product.IDENTIFIERCOLUMN + " = '" + log_identifer +
											"' and p." + Product.IDENTIFIERCOLUMN + " = f." + Reference.LOG_IDENTIFIERCOLUMN +
											" and f." + Reference.REFERENCECOLUMN + " = r." +  Role.REFERENCECOLUMN +
											" and r." + Role.EMAILCOLUMN + " = u." + EMAILCOLUMN +
											" ORDER BY " + EMAILCOLUMN);
			
			while (resultSet.next()) {
				user = new User();
				user.setUserEmail(resultSet.getString(EMAILCOLUMN));
				user.setUserName(resultSet.getString(NAMECOLUMN));
				user.setReference(resultSet.getString(Reference.REFERENCECOLUMN));
				user.setType(resultSet.getString(Reference.TITLECOLUMN));
				productRoleUsers.add(user);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			//return productRoleUsers;
		}
	}else{
		logger.error("Please check the reference table name: instrument_reference, investigation_reference or node_reference.");
	}
	return productRoleUsers;
}

/**
 * @param email
 * @param name
 */
public void updateUser(String email, String name) {
	try {
		// Setup the connection with the DB
		connect = getConnection();
		connect.setAutoCommit(false);
		
		prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " + NAMECOLUMN + " = ? WHERE " + EMAILCOLUMN + " = ?");
		prepareStm.setString(1, name);
		prepareStm.setString(2, email);
		
		prepareStm.executeUpdate();
		
		connect.commit();
		logger.info("The user with the email: " + email + ", has been updated to " + name + ".");
		
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
 * @param email
 * @param name
 */
public void insertUser(String email, String name) {
	try {
		// Setup the connection with the DB
		connect = getConnection();
		connect.setAutoCommit(false);
		
		prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" + EMAILCOLUMN + ", " + NAMECOLUMN + ") VALUES (?, ?)");
		prepareStm.setString(1, email);
		prepareStm.setString(2, name);
		
		prepareStm.executeUpdate();
		
		connect.commit();
		logger.info("The user " + name + " with the email: " + email + ", has been added.");
		
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
