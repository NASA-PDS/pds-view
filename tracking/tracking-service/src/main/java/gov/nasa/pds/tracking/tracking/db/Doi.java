/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.db;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@XmlRootElement(name = "submission_status")

public class Doi  implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	public static Logger logger = Logger.getLogger(Doi.class);
	
	public final static String LOG_IDENTIFIERCOLUME = "logical_identifier";
	public final static String VERSIONCOLUME  = "version_id";
	public final static String DOICOLUME  = "doi";
	public final static String DATECOLUME  = "registration_date";
	public final static String URLCOLUME  = "site_url";
	public final static String EMAILCOLUME  = "electronic_mail_address";
	public final static String COMMENTCOLUME = "comment";
	
	private String log_identifier;
	private String version;
	private String doi;
	private String url;
	private String date;
	private String email;
	private String comment;

	
	/**
	 * @return the log_identifier
	 */
	public String getLog_identifier() {
		return log_identifier;
	}


	/**
	 * @param log_identifier, the log_identifier to set
	 */
	@XmlElement
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
	@XmlElement
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
	@XmlElement
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
	@XmlElement
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
	@XmlElement
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
	@XmlElement
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
	@XmlElement
	public void setComment(String comment) {
		this.comment = comment;
	}


	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Doi(String logical_id, String ver, String doi, String date, String url, String email, String comment) {
		this.log_identifier = logical_id;
		this.version = ver;
		this.doi = doi;
		this.date = date;
		this.url = url;
		this.email = email;
		this.comment = comment;
	}


	public Doi() {
		// TODO Auto-generated constructor stub
	}
		
}
