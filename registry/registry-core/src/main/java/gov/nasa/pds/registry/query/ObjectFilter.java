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

import java.util.GregorianCalendar;
import java.util.List;

import gov.nasa.pds.registry.model.EventType;
import gov.nasa.pds.registry.model.ObjectStatus;

/**
 * This class identifies all the attributes one would filter on for any type of {@link RegistryObject}. This is used to build up a query from classes that extend {@link RegistryQuery}
 * @author pramirez
 *
 */
public class ObjectFilter {
	protected String guid;
	protected String name;
	protected String lid;
	protected String versionName;
	protected String objectType;
	protected String submitter;
	protected ObjectStatus status;
	protected EventType eventType;
	protected GregorianCalendar eventStart;
	protected GregorianCalendar eventEnd;
	protected List<String> sort;
  protected QueryOperator operator;
	
	protected ObjectFilter() {
	}
	
	public static class Builder extends AbstractBuilder {
		private ObjectFilter filter;
		
		public Builder() {
			filter = new ObjectFilter();
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
		
		public Builder submitter(String submitter) {
			this.checkBuilt();
			this.filter.submitter = submitter;
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
		
		public Builder eventStart(GregorianCalendar eventStart) {
			this.checkBuilt();
			this.filter.eventStart = eventStart;
			return this;
		}
		
		public Builder eventEnd(GregorianCalendar eventEnd) {
			this.checkBuilt();
			this.filter.eventEnd = eventEnd;
			return this;
		}
		
		public Builder sort(List<String> sort) {
      this.checkBuilt();
      this.filter.sort = sort;
      return this;
    }
    
    public Builder operator(QueryOperator operator) {
      this.checkBuilt();
      this.filter.operator = operator;
      return this;
    }
    
		public ObjectFilter build() {
			this.checkBuilt();
			this.isBuilt = true;
			return this.filter;
		}
		
	}

	public String getGuid() {
		return guid;
	}

	public String getName() {
		return name;
	}

	public String getLid() {
		return lid;
	}

	public String getVersionName() {
		return versionName;
	}

	public String getObjectType() {
		return objectType;
	}

	public String getSubmitter() {
		return submitter;
	}

	public ObjectStatus getStatus() {
		return status;
	}

	public EventType getEventType() {
		return eventType;
	}

	public GregorianCalendar getEventStart() {
		return eventStart;
	}

	public GregorianCalendar getEventEnd() {
		return eventEnd;
	}
}
