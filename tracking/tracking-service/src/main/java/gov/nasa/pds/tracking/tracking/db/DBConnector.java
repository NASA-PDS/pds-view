/**
 * Copyright 2010-2017, by the California Institute of Technology. 
 */
package gov.nasa.pds.tracking.tracking.db;

/**
 * @author danyu Dan.Yu@jpl.nasa.gov
 *
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DBConnector {

	public static Logger logger = Logger.getLogger(DBConnector.class);

	public static final String DB_URL = "javax.persistence.jdbc.url";
	public static final String DB_USER = "javax.persistence.jdbc.user";
	public static final String DB_PASSWD = "javax.persistence.jdbc.password";
	public static final String DB_DRIVER = "javax.persistence.jdbc.driver";
	
	public static final String ARCHIVE_STATUS_TABLE_NAME = "archive_status";
	public static final String CERTIFICATION_STATUS_TABLE_NAME = "certification_status";
	public static final String NSSDCA_STATUS_TABLE_NAME = "nssdca_status";

	public static final DateFormat ISO_BASIC = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss");
	
	protected Properties appConsts = null;

	protected static String db_url = null;
	protected static String db_user = null;
	protected static String db_pwd = null;
	
	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public DBConnector() throws ClassNotFoundException, SQLException {
		InputStream is = null;
		this.appConsts = new Properties();
		try {
			String propName = System.getProperty("JDBC_CONSTS");
			if (propName != null) {
				is = new FileInputStream(propName);
			} else {
				is = this.getClass().getClassLoader().getResourceAsStream("mysql.properties");
			}
			appConsts.load(is);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					logger.error(ex);
				}
			}
		}
		
        if (appConsts.containsKey(DB_URL)) {
            db_url = appConsts.getProperty(DB_URL);
        }
        if (appConsts.containsKey(DB_USER)) {
            db_user = appConsts.getProperty(DB_USER);
        }
        if (appConsts.containsKey(DB_PASSWD)) {
            db_pwd = appConsts.getProperty(DB_PASSWD);
        }
        
		// load JDBC driver
		Class.forName(appConsts.getProperty(DB_DRIVER));
	}

}
