/**
 * Copyright 2010-2017, by the California Institute of Technology. 
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
 * @author danyu Dan.Yu@jpl.nasa.gov
 *
 */
public class User extends DBConnector {

	public static Logger logger = Logger.getLogger(User.class);

	private static final String TABLENAME = "user";
	private static final String ROLETABLENAME = "role";
	private static final String REFTABLENAME = "reference";
	private static final String PRODUCTTABLENAME = "product";
	
	public static final String NAMECOLUME = "name";
	public static final String EMAILCOLUME = "electronic_mail_address";


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
	 * @return a list of users
	 */
	@SuppressWarnings("finally")
	public List<User> getUsers() {
		List<User> userObjs = new ArrayList<User>();
		User user = null;
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection(db_url, db_user, db_pwd);

			statement = connect.createStatement();
			resultSet = statement.executeQuery("select * from " + TABLENAME + " order by " + EMAILCOLUME);
			
			while (resultSet.next()) {
				user = new User();
				user.setUserEmail(resultSet.getString(EMAILCOLUME));
				user.setUserName(resultSet.getString(NAMECOLUME));
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
 * @param role
 * @return a list of roles for a given user (email)
 */
@SuppressWarnings("finally")
public List<User> getUserRole(String email){
	List<User> userRoles = new ArrayList<User>();
	User user = null;
	try {
		// Setup the connection with the DB
		connect = DriverManager.getConnection(db_url, db_user, db_pwd);

		statement = connect.createStatement();
		resultSet = statement.executeQuery("SELECT u." + EMAILCOLUME + ", u." + NAMECOLUME + ", r." + Role.REFERENCECOLUME +
										" FROM " + TABLENAME + " u, " + ROLETABLENAME + " r" +
										" WHERE u." + EMAILCOLUME + " = '" + email + "' AND u." + EMAILCOLUME + " = r." + EMAILCOLUME +
										" ORDER BY r." + Role.REFERENCECOLUME);
		
		while (resultSet.next()) {
			user = new User();
			user.setUserEmail(resultSet.getString(EMAILCOLUME));
			user.setUserName(resultSet.getString(NAMECOLUME));
			user.setReference(resultSet.getString(Role.REFERENCECOLUME));
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
 * @param log_identifer
 * @param type
 * @return a list of users
 */
@SuppressWarnings("finally")
public List<User> getProductRoleUsers(String log_identifer, String type) {
	
	List<User> productRoleUsers = new ArrayList<User>();
	User user = null;
	
	try {
		// Setup the connection with the DB
		connect = DriverManager.getConnection(db_url, db_user, db_pwd);

		statement = connect.createStatement();
		
		resultSet = statement.executeQuery("SELECT u." + EMAILCOLUME + ", u." + NAMECOLUME + ", f." + Reference.TYPECOLUME +
										" FROM " + TABLENAME + " u, " + ROLETABLENAME + " r, " + REFTABLENAME + " f, " + PRODUCTTABLENAME + " p" +
										" where p." + Product.IDENTIFIERCOLUME + " = '" + log_identifer +
										"' and p." + Product.IDENTIFIERCOLUME + " = f." + Reference.LOG_IDENTIFIERCOLUME +
										" and f." + Reference.TYPECOLUME + " = '" + type +
										"' and f." + Reference.REFERENCECOLUME + " = r." +  Role.REFERENCECOLUME +
										" and r." + Role.EMAILCOLUME + " = u." + EMAILCOLUME +
										" ORDER BY " + EMAILCOLUME);
		
		while (resultSet.next()) {
			user = new User();
			user.setUserEmail(resultSet.getString(EMAILCOLUME));
			user.setUserName(resultSet.getString(NAMECOLUME));
			user.setType(resultSet.getString(Reference.TYPECOLUME));
			productRoleUsers.add(user);
		}
		
	} catch (Exception e) {
		logger.error(e);
	} finally {
		close(statement);
		return productRoleUsers;
	}
}
/**
 * @param email
 * @param name
 */
public void updateUser(String email, String name) {
	try {
		// Setup the connection with the DB
		connect = DriverManager.getConnection(db_url, db_user, db_pwd);
		connect.setAutoCommit(false);
		
		prepareStm = connect.prepareStatement("UPDATE " + TABLENAME + " SET " + NAMECOLUME + " = ? WHERE " + EMAILCOLUME + " = ?");
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
		connect = DriverManager.getConnection(db_url, db_user, db_pwd);
		connect.setAutoCommit(false);
		
		prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" + EMAILCOLUME + ", " + NAMECOLUME + ") VALUES (?, ?)");
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
