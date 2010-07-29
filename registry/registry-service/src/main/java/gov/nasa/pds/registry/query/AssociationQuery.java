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

import java.util.ArrayList;
import java.util.List;

/**
 * @author pramirez
 *
 */
public class AssociationQuery extends RegistryQuery<AssociationFilter> {
	private AssociationQuery() {
		this.filter = new AssociationQuery.Builder().build().getFilter();
	}
	
	public static class Builder extends AbstractBuilder {
		private AssociationQuery query;
		
		public Builder() {
			query = new AssociationQuery();
			this.query.sort = new ArrayList<String>();
			this.query.sort.add("guid");
			this.query.operator = QueryOperator.AND;
		}
		
		public Builder filter(AssociationFilter filter) {
			this.checkBuilt();
			this.query.filter = filter;
			return this;
		}
		
		public Builder sort(List<String> sort) {
			this.checkBuilt();
			this.query.sort = sort;
			return this;
		}
		
		public Builder operator(QueryOperator operator) {
			this.checkBuilt();
			this.query.operator = operator;
			return this;
		}
		
		public AssociationQuery build() {
			this.checkBuilt();
			this.isBuilt = true;
			return this.query;
		}
	}
}
