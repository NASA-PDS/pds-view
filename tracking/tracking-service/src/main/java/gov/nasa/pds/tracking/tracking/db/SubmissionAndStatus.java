/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.db;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

@XmlRootElement(name = "submission_status")
/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class SubmissionAndStatus implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static Logger logger = Logger.getLogger(SubmissionAndStatus.class);
	
	private int del_identifier;
	private String submissionDate;
	private String statusDate;
	private String status;
	private String email;
	private String comment;
	
 
	    
   public SubmissionAndStatus(int deliveryIdentifier, String subDateTime, String statusDateTime,
			String status, String email, String comment){
	   
      this.del_identifier = deliveryIdentifier; 
      this.submissionDate = subDateTime;
      this.statusDate = statusDateTime;
      this.status = status;
      this.email = email;
      this.comment = comment;
      
   }  
	   
	public SubmissionAndStatus() {
	// TODO Auto-generated constructor stub
	}

	/**
	 * @return the del_identifier
	 */
	public int getDel_identifier() {
		return del_identifier;
	}

	/**
	 * @param del_identifier, the del_identifier to set
	 */
	@XmlElement
	public void setDel_identifier(int del_identifier) {
		this.del_identifier = del_identifier;
	}

	/**
	 * @return the submissionDate
	 */
	public String getSubmissionDate() {
		return submissionDate;
	}

	/**
	 * @param submissionDate, the submissionDate to set
	 */
	@XmlElement
	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}
	/**
	 * @return the statusDate
	 */
	public String getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statusDate, the statusDate to set
	 */
	@XmlElement
	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status, the status to set
	 */
	@XmlElement
	public void setStatus(String status) {
		this.status = status;
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

}
