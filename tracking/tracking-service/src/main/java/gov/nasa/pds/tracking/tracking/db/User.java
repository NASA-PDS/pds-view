/**
 * Copyright 2010-2017, by the California Institute of Technology. 
 */
package gov.nasa.pds.tracking.tracking.db;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

@XmlRootElement(name = "user")
/**
 * @author danyu Dan.Yu@jpl.nasa.gov
 *
 */
public class User implements Serializable  {

	private static final long serialVersionUID = 1L;	
	public static Logger logger = Logger.getLogger(User.class);
	
	private String userName = null;
	private String userEmail = null;
	private String reference = null;
	private String type = null;

	public User(){
		// TODO Auto-generated constructor stub
	}
	
	public User(String email, String name){
		
		this.userEmail = email;
		this.userName = name;
	}
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
}
