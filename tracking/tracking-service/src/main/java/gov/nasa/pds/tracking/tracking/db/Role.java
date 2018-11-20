/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.db;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

@XmlRootElement(name = "role")
/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class Role implements Serializable  {

	private static final long serialVersionUID = 1L;
	public static Logger logger = Logger.getLogger(Role.class);
	
	private String email = null;
	private String reference = null;
	
	public Role(String email, String ref){
		this.email = email;
		this.reference = ref;
		
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

	public Role(){
		// TODO Auto-generated constructor stub
	}
	
}
