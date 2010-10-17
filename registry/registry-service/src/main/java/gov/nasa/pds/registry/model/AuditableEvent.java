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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@XmlRootElement(name = "auditableEvent", namespace = "http://registry.pds.nasa.gov")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuditableEvent extends RegistryObject {

  private static final long serialVersionUID = -1091556687686639668L;

  @XmlAttribute
  private EventType eventType;

  @XmlAttribute
  private String affectedObject;

  @XmlAttribute
  private String requestId;

  @XmlAttribute
  private GregorianCalendar timestamp;

  @XmlAttribute
  @Column(name = "userid")
  private String user;

  public AuditableEvent() {
  }

  public AuditableEvent(EventType eventType, String affectedObject, String user) {
    this.eventType = eventType;
    this.affectedObject = affectedObject;
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
   *          the eventType to set
   */
  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }

  /**
   * @return the guid of the registry object this event is associated with
   */
  public String getAffectedObject() {
    return affectedObject;
  }

  /**
   * @param affectedObject
   *          the guid of the registry object this event is associated with
   */
  public void setAffectedObject(String affectedObject) {
    this.affectedObject = affectedObject;
  }

  /**
   * @return the timestamp when the event occurred
   */
  public GregorianCalendar getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp
   *          the timestamp to when event occured
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
   *          an unique user id to set
   */
  public void setUser(String user) {
    this.user = user;
  }

  /**
   * @return identifies the request made to cause this event
   */
  public String getRequestId() {
    return requestId;
  }

  /**
   * @param requestId
   *          unique id for request that generated this event
   */
  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result
        + ((affectedObject == null) ? 0 : affectedObject.hashCode());
    result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
    result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    AuditableEvent other = (AuditableEvent) obj;
    if (affectedObject == null) {
      if (other.affectedObject != null)
        return false;
    } else if (!affectedObject.equals(other.affectedObject))
      return false;
    if (eventType == null) {
      if (other.eventType != null)
        return false;
    } else if (!eventType.equals(other.eventType))
      return false;
    if (requestId == null) {
      if (other.requestId != null)
        return false;
    } else if (!requestId.equals(other.requestId))
      return false;
    if (timestamp == null) {
      if (other.timestamp != null)
        return false;
    } else if (!timestamp.equals(other.timestamp))
      return false;
    if (user == null) {
      if (other.user != null)
        return false;
    } else if (!user.equals(other.user))
      return false;
    return true;
  }
}
