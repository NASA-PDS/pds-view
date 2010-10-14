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

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

@MappedSuperclass
@XmlType(name = "identifiable", namespace = "http://registry.pds.nasa.gov")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Identifiable implements Serializable {

  private static final long serialVersionUID = -1707014666352827997L;

  @Id
  @XmlAttribute
  private String guid;

  @XmlAttribute
  private String home;

  @OneToMany(cascade = CascadeType.ALL)
  @XmlElementRef
  @OrderBy
  private Set<Slot> slots;

  public Identifiable() {
  }

  public Identifiable(String guid, String home, Set<Slot> slots) {
    this.guid = guid;
    this.home = home;
    // this.slots = slots;
  }

  /**
   * @return the guid
   */
  public String getGuid() {
    return guid;
  }

  /**
   * @return the home
   */
  public String getHome() {
    return home;
  }

  /**
   * @param guid
   *          the global unique identifier to set
   */
  public void setGuid(String guid) {
    this.guid = guid;
  }

  /**
   * @param home
   *          the home to set
   */
  public void setHome(String home) {
    this.home = home;
  }

  /**
   * @return the slots
   */
  public Set<Slot> getSlots() {
    return slots;
  }

  /**
   * @param slots
   *          the slots to set
   */
  public void setSlots(Set<Slot> slots) {
    this.slots = slots;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((guid == null) ? 0 : guid.hashCode());
    result = prime * result + ((home == null) ? 0 : home.hashCode());
    result = prime * result + ((slots == null) ? 0 : slots.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Identifiable other = (Identifiable) obj;
    if (guid == null) {
      if (other.guid != null)
        return false;
    } else if (!guid.equals(other.guid))
      return false;
    if (home == null) {
      if (other.home != null)
        return false;
    } else if (!home.equals(other.home))
      return false;
    if (slots == null) {
      if (other.slots != null)
        return false;
    } else if (!slots.equals(other.slots))
      return false;
    return true;
  }

}
