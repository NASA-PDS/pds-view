/**
 * Copyright 2010-2017, by the California Institute of Technology.
 * 
 * The object class represents Product table.
 *
 */
package gov.nasa.pds.tracking.tracking.db;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

@XmlRootElement(name = "product")

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */

public class Product  implements Serializable  {

	private static final long serialVersionUID = 1L;

	public static Logger logger = Logger.getLogger(Product.class);
	
	public static final String IDENTIFIERCOLUMN  = "logical_identifier";	
	public static final String VERSIONCOLUMN  = "version_id";	
	public static final String TITLECOLUMN  = "title";
	public static final String TYPECOLUMN  = "type";
	public static final String ALTERNATECOLUMN = "alternate_id";
	
	public static final String INVESTREFCOLUMN = "investigation_reference";	
	public static final String INVESTTITLECOLUMN = "investigation_title";
	public static final String INSTREFCOLUMN = "instrument_reference";
	public static final String INSTTITLECOLUMN = "instrument_title";
	public static final String NODEREFCOLUMN = "node_reference";
	public static final String NODETITLECOLUMN = "node_title";
	
	private String identifier = null;
	private String version = null;
	private String title = null;
	private String type = null;
	private String alternate = null;
	private String instRef = null;
	/**
	 * @return the instRef
	 */
	public String getInstRef() {
		return instRef;
	}

	/**
	 * @param instRef, the instRef to set
	 */
	public void setInstRef(String instRef) {
		this.instRef = instRef;
	}

	/**
	 * @return the instTitle
	 */
	public String getInstTitle() {
		return instTitle;
	}

	/**
	 * @param instTitle, the instTitle to set
	 */
	public void setInstTitle(String instTitle) {
		this.instTitle = instTitle;
	}

	/**
	 * @return the inveRef
	 */
	public String getInveRef() {
		return inveRef;
	}

	/**
	 * @param inveRef, the inveRef to set
	 */
	public void setInveRef(String inveRef) {
		this.inveRef = inveRef;
	}

	/**
	 * @return the inveTitle
	 */
	public String getInveTitle() {
		return inveTitle;
	}

	/**
	 * @param inveTitle, the inveTitle to set
	 */
	public void setInveTitle(String inveTitle) {
		this.inveTitle = inveTitle;
	}

	/**
	 * @return the nodeRef
	 */
	public String getNodeRef() {
		return nodeRef;
	}

	/**
	 * @param nodeRef, the nodeRef to set
	 */
	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	/**
	 * @return the nodeTitle
	 */
	public String getNodeTitle() {
		return nodeTitle;
	}

	/**
	 * @param nodeTitle, the nodeTitle to set
	 */
	public void setNodeTitle(String nodeTitle) {
		this.nodeTitle = nodeTitle;
	}

	private String instTitle = null;
	private String inveRef = null;
	private String inveTitle = null;
	private String nodeRef = null;
	private String nodeTitle = null;

	/**
	 * @return
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return
	 */
	public String getAlternate() {
		return alternate;
	}

	/**
	 * @param alternate
	 */
	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}

	public Product(String identifier, String version, String title, String type, String alternate) {
		this.identifier = identifier;
		this.version = version;
		this.title = title;
		this.type = type;
		this.alternate = alternate;
	}

	public Product() {
		// TODO Auto-generated constructor stub
	}
}
