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

import gov.nasa.pds.registry.model.EventType;
import gov.nasa.pds.registry.model.ObjectStatus;

/**
 * @author pramirez
 *
 */
public class ObjectFilter {
	private String guid;
	private String name;
	private String lid;
	private String version;
	private String userVersion;
	private String objectType;
	private String submitter;
	private ObjectStatus status;
	private EventType eventType;
	private GregorianCalendar eventStart;
	private GregorianCalendar eventEnd;
	
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
		
		public Builder version(String version) {
			this.checkBuilt();
			this.filter.version = version;
			return this;
		}
		
		public Builder userVersion(String userVersion) {
			this.checkBuilt();
			this.filter.userVersion = userVersion;
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

	public String getVersion() {
		return version;
	}

	public String getUserVersion() {
		return userVersion;
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
