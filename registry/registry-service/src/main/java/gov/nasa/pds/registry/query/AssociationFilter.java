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

public class AssociationFilter extends ObjectFilter {
	private String sourceLid;
	private String sourceVersion;
	private String sourceHome;
	private String targetLid;
	private String targetVersion;
	private String targetHome;
	private String associationType;
	
	private AssociationFilter() {
		super();
	}
	
	public static class Builder extends AbstractBuilder {
		private AssociationFilter filter;
		
		public Builder() {
			filter = new AssociationFilter();
		}
		
		public Builder sourceLid(String sourceLid) {
			this.checkBuilt();
			this.filter.sourceLid = sourceLid;
			return this;
		}
		
		public Builder sourceVersion(String sourceVersion) {
			this.checkBuilt();
			this.filter.sourceVersion = sourceVersion;
			return this;
		}
		
		public Builder sourceHome(String sourceHome) {
			this.checkBuilt();
			this.filter.sourceHome = sourceHome;
			return this;
		}
		
		public Builder targetLid(String targetLid) {
			this.checkBuilt(); 
			this.filter.targetLid = targetLid;
			return this;
		}
		
		public Builder targetVersion(String targetVersion) {
			this.checkBuilt();
			this.filter.targetVersion = targetVersion;
			return this;
		}
		
		public Builder targetHome(String targetHome) {
			this.checkBuilt();
			this.filter.targetHome = targetHome;
			return this;
		}
		
		public Builder associationType(String associationType) {
			this.checkBuilt();
			this.filter.associationType = associationType;
			return this;
		}
		
		public AssociationFilter build() {
			this.checkBuilt();
			this.isBuilt = true;
			return this.filter;
		}
	}

	public String getSourceLid() {
		return sourceLid;
	}

	public String getSourceVersion() {
		return sourceVersion;
	}

	public String getSourceHome() {
		return sourceHome;
	}

	public String getTargetLid() {
		return targetLid;
	}

	public String getTargetVersion() {
		return targetVersion;
	}

	public String getTargetHome() {
		return targetHome;
	}

	public String getAssociationType() {
		return associationType;
	}
}
