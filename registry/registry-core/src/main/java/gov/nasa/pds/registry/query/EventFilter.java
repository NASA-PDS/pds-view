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

package gov.nasa.pds.registry.query;

import gov.nasa.pds.registry.model.EventType;
import gov.nasa.pds.registry.model.ObjectStatus;

import java.util.Date;

/**
 * @author pramirez
 *
 */
public class EventFilter extends ObjectFilter {
  private EventType eventType;
  private Date eventStart;
  private Date eventEnd;
  private String requestId;
  private String user;

  private EventFilter() {
    super();
  }

  public static class Builder extends AbstractBuilder {
    private EventFilter filter;

    public Builder() {
      filter = new EventFilter();
    }
    
    public Builder guid(String guid) {
      this.checkBuilt();
      this.filter.guid = guid;
      return this;
    }
    
    public Builder name(String name) {
      this.checkBuilt();
      this.filter.name = name;
      return this;
    }
    
    public Builder lid(String lid) {
      this.checkBuilt();
      this.filter.lid = lid;
      return this;
    }
    
    public Builder versionName(String versionName) {
      this.checkBuilt();
      this.filter.versionName = versionName;
      return this;
    }
    
    public Builder objectType(String objectType) {
      this.checkBuilt();
      this.filter.objectType = objectType;
      return this;
    }
    
    public Builder user(String user) {
      this.checkBuilt();
      this.filter.user = user;
      return this;
    }
    
    public Builder status(ObjectStatus status) {
      this.checkBuilt();
      this.filter.status = status;
      return this;
    }
    
    public Builder eventType(EventType eventType) {
      this.checkBuilt();
      this.filter.eventType = eventType;
      return this;
    }
    
    public Builder eventStart(Date eventStart) {
      this.checkBuilt();
      this.filter.eventStart = eventStart;
      return this;
    }
    
    public Builder eventEnd(Date eventEnd) {
      this.checkBuilt();
      this.filter.eventEnd = eventEnd;
      return this;
    }
    
    public Builder requestId(String requestId) {
      this.checkBuilt();
      this.filter.requestId = requestId;
      return this;
    }

    public EventFilter build() {
      this.checkBuilt();
      this.isBuilt = true;
      return this.filter;
    }
  }
  
  public Date getEventStart() {
    return eventStart;
  }

  public Date getEventEnd() {
    return eventEnd;
  }

  public EventType getEventType() {
    return eventType;
  }
  
  public String getRequestId() {
    return requestId;
  }

  public String getUser() {
    return user;
  }
}
