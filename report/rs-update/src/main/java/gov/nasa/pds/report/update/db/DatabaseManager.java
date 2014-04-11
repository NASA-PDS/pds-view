/** 
 * Copyright (c) 2011, California Institute of Technology.
 * ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
 *
 * $Id$ 
 * 
 */

package gov.nasa.pds.report.update.db;

import gov.nasa.pds.report.update.properties.DBProperties;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * @author jpadams
 * 
 * TODO This is insane that I created a database. This should absolutely be stored in an XML of some sort.
 * 
 */
public class DatabaseManager {

	protected Connection conn;
	// private String qString;
	protected DBProperties dbProps;
	protected Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	protected DatabaseManager(final String path) throws FileNotFoundException {
		try {
			this.dbProps = new DBProperties(path);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * 
	 */
	protected final void closeConnect() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @throws SQLException
	 */
	protected void connect() throws SQLException {
		try {
			Class.forName(this.dbProps.getDriver());
			this.conn = DriverManager.getConnection(this.dbProps.getUrl(),
					this.dbProps.getUsername(), this.dbProps.getPassword());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	protected final int executeUpdate(final String sql) throws SQLException {
		this.log.fine(sql);
		connect();
		Statement stmt1 = null;
		try {
			stmt1 = this.conn.createStatement();
			stmt1.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt1.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} finally {
			try {
				stmt1.close();
				closeConnect();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1;
	}
}
