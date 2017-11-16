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
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class Role extends DBConnector {
	public static Logger logger = Logger.getLogger(Role.class);

	private static final String TABLENAME  = "role";
	
	public static final String EMAILCOLUMN = "electronic_mail_address";
	public static final String REFERENCECOLUMN = "reference";
		
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement prepareStm = null;
	private ResultSet resultSet = null;
	
	private String email = null;
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email, the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
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

	private String reference = null;

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Role() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return a list of roles
	 */
	@SuppressWarnings("finally")
	public List<Role> getRoles() {
		
		List<Role> roles = new ArrayList<Role>();
		Role role = null;
		
		try {
			// Setup the connection with the DB
			connect = getConnection();

			statement = connect.createStatement();
			resultSet = statement.executeQuery("SELECT * " +
											"FROM " + TABLENAME +
											" ORDER BY " + REFERENCECOLUMN);
			
			while (resultSet.next()) {
				role = new Role();
				role.setEmail(resultSet.getString(EMAILCOLUMN));
				role.setReference(resultSet.getString(REFERENCECOLUMN));
				roles.add(role);
			}
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			close(statement);
			return roles;
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
	 * @param email
	 * @param reference
	 */
	public void insertRole(String email, String reference) {
		try {
			// Setup the connection with the DB
			connect = getConnection();
			connect.setAutoCommit(false);
			
			prepareStm = connect.prepareStatement("INSERT INTO " + TABLENAME + " (" + EMAILCOLUMN + ", " + REFERENCECOLUMN + ") VALUES (?, ?)");
			prepareStm.setString(1, email);
			prepareStm.setString(2, reference);
			
			prepareStm.executeUpdate();
			
			connect.commit();
			logger.info("The role " + reference + " for email: " + email + ", has been added.");
			
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
