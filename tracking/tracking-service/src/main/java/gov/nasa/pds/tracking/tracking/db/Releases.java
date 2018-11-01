/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.db;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

@XmlRootElement(name = "releases")
/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class Releases implements Serializable  {

	private static final long serialVersionUID = 1L;

	public static Logger logger = Logger.getLogger(NssdcaStatus.class);

	private String logIdentifier = null;
	private String version = null;
	private String date = null;
	private String name = null;
	private String description = null;
	private String email = null;
	private String comment = null;
	
	public Releases(String logIdenf, String ver, String date, String name, String desc, String email, String comm){
		this.logIdentifier = logIdenf;
		this.version = ver;
		this.date = date;
		this.name = name;
		this.description = desc;
		this.email = email;
		this.comment = comm;		
	}
	
	/**
	 * @return the logIdentifier
	 */
	public String getLogIdentifier() {
		return logIdentifier;
	}
	/**
	 * @param logIdentifier, the logIdentifier to set
	 */
	public void setLogIdentifier(String logIdentifier) {
		this.logIdentifier = logIdentifier;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name, the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description, the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	public Releases(){
		// TODO Auto-generated constructor stub
	}
	
}
