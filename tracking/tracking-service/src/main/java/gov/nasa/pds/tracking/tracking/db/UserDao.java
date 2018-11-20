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
public class UserDao extends DBConnector {

	public UserDao() throws ClassNotFoundException, SQLException {

	}
	public static Logger logger = Logger.getLogger(UserDao.class);

	private static final String TABLENAME = "user";
	private static final String ROLETABLENAME = "role";
	private static final String PRODUCTTABLENAME = "product";
	
	public static final String NAMECOLUMN = "name";
	public static final String EMAILCOLUMN = "electronic_mail_address";

	private ResultSet resultSet = null;		

	/**
	 * User Query – Query the user table for a list of users.
	 * @return a list of users
	 */
	@SuppressWarnings("finally")
	public List<User> getUsers() {
		List<User> userObjs = new ArrayList<User>();
		User user = null;
		
		Connection connect = null;
		Statement statement = null;
		
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
			close(connect, statement);
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
	
	Connection connect = null;
	Statement statement = null;
	
	try {
		// Setup the connection with the DB
		connect = getConnection();
		statement = connect.createStatement();
		String query = "SELECT u." + EMAILCOLUMN + ", u." + NAMECOLUMN + ", r." + RoleDao.REFERENCECOLUMN +
				" FROM " + TABLENAME + " u, " + ROLETABLENAME + " r" +
				" WHERE u." + EMAILCOLUMN + " = '" + email + "' AND u." + EMAILCOLUMN + " = r." + EMAILCOLUMN +
				" ORDER BY r." + RoleDao.REFERENCECOLUMN;
		
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
			user.setReference(resultSet.getString(RoleDao.REFERENCECOLUMN));
			userRoles.add(user);
		}
		
	} catch (Exception e) {
		logger.error(e);
	} finally {
		close(connect, statement);
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
	
	Connection connect = null;
	Statement statement = null;
	
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
											" where p." + ProductDao.IDENTIFIERCOLUMN + " = '" + log_identifer +
											"' and p." + ProductDao.IDENTIFIERCOLUMN + " = f." + Reference.LOG_IDENTIFIERCOLUMN +
											" and f." + Reference.REFERENCECOLUMN + " = r." +  RoleDao.REFERENCECOLUMN +
											" and r." + RoleDao.EMAILCOLUMN + " = u." + EMAILCOLUMN +
											" ORDER BY " + EMAILCOLUMN);
			
			resultSet = statement.executeQuery("SELECT u." + EMAILCOLUMN + ", u." + NAMECOLUMN + ", f." + Reference.TITLECOLUMN + ", f." + Reference.REFERENCECOLUMN +
											" FROM " + TABLENAME + " u, " + ROLETABLENAME + " r, " + tableName + " f, " + PRODUCTTABLENAME + " p" +
											" where p." + ProductDao.IDENTIFIERCOLUMN + " = '" + log_identifer +
											"' and p." + ProductDao.IDENTIFIERCOLUMN + " = f." + Reference.LOG_IDENTIFIERCOLUMN +
											" and f." + Reference.REFERENCECOLUMN + " = r." +  RoleDao.REFERENCECOLUMN +
											" and r." + RoleDao.EMAILCOLUMN + " = u." + EMAILCOLUMN +
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
			close(connect, statement);
			//return productRoleUsers;
		}
	}else{
		logger.error("Please check the reference table name: instrument_reference, investigation_reference or node_reference.");
	}
	return productRoleUsers;
}

/**
 * @param user
 */
public User updateUser(User user) {

	
	Connection connect = null;
	PreparedStatement prepareStm = null;
	
	User updatedUser = null;
	
	try {
		// Setup the connection with the DB
		connect = getConnection();
		connect.setAutoCommit(false);
		
		prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " + NAMECOLUMN + " = ? WHERE " + EMAILCOLUMN + " = ?");
		prepareStm.setString(1, user.getUserName());
		prepareStm.setString(2, user.getUserEmail());
		
		if(prepareStm.executeUpdate() == 1){	
			connect.commit();
			updatedUser = new User(user.getUserEmail(), user.getUserName());		
			logger.info("The user with the email: " + user.getUserEmail() + ", has been updated to " + user.getUserName() + ".");
		}
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
	return updatedUser;
}

/**
 * @param user
 */
public int insertUser(User user) {
	int success = 0;
	
	Connection connect = null;
	PreparedStatement prepareStm = null;
	
	try {
		// Setup the connection with the DB
		connect = getConnection();
		connect.setAutoCommit(false);
		
		prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" + EMAILCOLUMN + ", " + NAMECOLUMN + ") VALUES (?, ?)");
		prepareStm.setString(1, user.getUserEmail());
		prepareStm.setString(2, user.getUserName());
		
		if(prepareStm.executeUpdate() ==1){
		
			connect.commit();
			logger.info("The user " + user.getUserName() + " with the email: " + user.getUserEmail() + ", has been added.");
			success = 1;
		}
		
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
 * @param connect
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
