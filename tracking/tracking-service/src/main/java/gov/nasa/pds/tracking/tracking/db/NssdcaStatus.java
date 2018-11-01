package gov.nasa.pds.tracking.tracking.db;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

@XmlRootElement(name = "nssdca_status")
public class NssdcaStatus implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	public static Logger logger = Logger.getLogger(NssdcaStatus.class);

	private String logIdentifier = null;
	private String version = null;
	private String date = null;
	private String nssdca = null;
	private String email = null;
	private String comment = null;
	
	
	public NssdcaStatus(String logIdenf, String ver, String date, String nda, String email, String comm) {
		this.logIdentifier = logIdenf;
		this.version = ver;
		this.date = date;
		this.nssdca = nda;
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
	 * @return the nssdca
	 */
	public String getNssdca() {
		return nssdca;
	}



	/**
	 * @param nssdca, the nssdca to set
	 */
	public void setNssdca(String nssdca) {
		this.nssdca = nssdca;
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



	public NssdcaStatus(){
		// TODO Auto-generated constructor stub
	}

}
