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
public class RegistryQuery<T extends ObjectFilter> {
	protected T filter;
	protected List<String> sort;
	protected QueryOperator operator;
	
	private RegistryQuery() { 
	}
	
	public static class Builder<T extends ObjectFilter> extends AbstractBuilder {
    private RegistryQuery<T> query;
    
    public Builder() {
      query = new RegistryQuery<T>();
      this.query.sort = new ArrayList<String>();
      this.query.sort.add("guid");
      this.query.operator = QueryOperator.AND;
    }
    
    public Builder<T> filter(T filter) {
      this.checkBuilt();
      this.query.filter = filter;
      return this;
    }
    
    public Builder<T> sort(List<String> sort) {
      this.checkBuilt();
      this.query.sort = sort;
      return this;
    }
    
    public Builder<T> operator(QueryOperator operator) {
      this.checkBuilt();
      this.query.operator = operator;
      return this;
    }
    
    public RegistryQuery<T> build() {
      this.checkBuilt();
      this.isBuilt = true;
      return this.query;
    }
  }
	
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
