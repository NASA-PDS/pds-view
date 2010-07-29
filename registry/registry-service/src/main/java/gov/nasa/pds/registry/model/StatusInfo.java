//	Copyright 2009-2010, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//

package gov.nasa.pds.registry.model;

import java.util.GregorianCalendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "status_information", namespace = "http://registry.pds.nasa.gov")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatusInfo {

	@XmlAttribute
	private RegistryStatus status;

	@XmlAttribute
	private GregorianCalendar serverStarted;

	public StatusInfo() {
		serverStarted = new GregorianCalendar();
	}

	public StatusInfo(RegistryStatus status) {
		this();
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public RegistryStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(RegistryStatus status) {
		this.status = status;
	}

	/**
	 * @return the serverStarted
	 */
	public GregorianCalendar getServerStarted() {
		return serverStarted;
	}

	/**
	 * @param serverStarted
	 *            the serverStarted to set
	 */
	public void setServerStarted(GregorianCalendar serverStarted) {
		this.serverStarted = serverStarted;
	}

}
