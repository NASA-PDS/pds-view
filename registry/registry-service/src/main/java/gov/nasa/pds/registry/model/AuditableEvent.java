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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@Entity
@XmlType(name = "auditableEvent", namespace = "http://registry.pds.nasa.gov")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuditableEvent extends RegistryObject {

	private static final long serialVersionUID = -1091556687686639668L;

	@XmlAttribute
	private EventType eventType;

	@XmlAttribute
	private String registryObject;

	@XmlAttribute
	private GregorianCalendar timestamp;

	@XmlAttribute
	@Column(name = "userid")
	private String user;

	public AuditableEvent() {
	}

	public AuditableEvent(EventType eventType, String registryObject,
			String user) {
		this.eventType = eventType;
		this.registryObject = registryObject;
		this.user = user;
		this.timestamp = new GregorianCalendar();
	}

	/**
	 * @return the eventType
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the guid of the registry object this event is associated with
	 */
	public String getRegistryObject() {
		return registryObject;
	}

	/**
	 * @param registryObject
	 *            the guid of the registry object this event is associated with
	 */
	public void setRegistryObject(String registryObject) {
		this.registryObject = registryObject;
	}

	/**
	 * @return the timestamp when the event occurred
	 */
	public GregorianCalendar getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to when event occured
	 */
	public void setTimestamp(GregorianCalendar timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return identifier that maps to a user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user
	 *            an unique user id to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
}
