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

import java.util.List;

/**
 * @author pramirez
 *
 */
public abstract class RegistryQuery<T extends ObjectFilter> {
	protected T filter;
	protected List<String> sort;
	protected QueryOperator operator;
	
	public T getFilter() {
		return this.filter;
	}
	
	public List<String> getSort() {
		return this.sort;
	}
	
	public QueryOperator getOperator() {
		return this.operator;
	}
}
