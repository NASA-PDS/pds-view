/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class Doi extends DBConnector {

	public static Logger logger = Logger.getLogger(Doi.class);

	private final static String TABLENAME = "doi";
	
	private final static String LOG_IDENTIFIERCOLUME = "logical_identifier";
	private final static String VERSIONCOLUME  = "version_id";
	private final static String DOICOLUME  = "doi";
	private final static String DATECOLUME  = "registration_date";
	private final static String URLCOLUME  = "site_url";
	private final static String EMAILCOLUME  = "electronic_mail_address";
	private final static String COMMENTCOLUME = "comment";
	
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	private String log_identifier = null;
	private String version = null;
	private String doi = null;
	private String url = null;
	private String date = null;
	private String email = null;
	private String comment = null;

	
	/**
	 * @return the log_identifier
	 */
	public String getLog_identifier() {
		return log_identifier;
	}


	/**
	 * @param log_identifier, the log_identifier to set
	 */
	public void setLog_identifier(String log_identifier) {
		this.log_identifier = log_identifier;
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
	 * @return the doi
	 */
	public String getDoi() {
		return doi;
	}


	/**
	 * @param doi, the doi to set
	 */
	public void setDoi(String doi) {
		this.doi = doi;
	}


	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}


	/**
	 * @param url, the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}


	/**
	 * @param date, the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}


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
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}


	/**
	 * @param comment, the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}


	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Doi() throws ClassNotFoundException, SQLException {
		// TODO Auto-generated constructor stub
	}

}
