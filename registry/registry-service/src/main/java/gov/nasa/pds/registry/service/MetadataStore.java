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

package gov.nasa.pds.registry.service;

import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.query.AssociationQuery;
import gov.nasa.pds.registry.query.ProductQuery;

import java.util.List;

public interface MetadataStore {

	public void saveProduct(Product product);

	public Product getProduct(String lid, String userVersion);

	public Product updateProduct(Product product);

	public void deleteProduct(String lid, String userVersion);

	public List<Product> getProducts(Integer start, Integer rows);

	public PagedResponse getProducts(ProductQuery query, Integer start, Integer rows);

	public List<Product> getProductVersions(String lid);

	public long getNumProducts();

	public boolean hasProduct(String lid, String userVersion);

	public Product getProduct(String guid);

	public void saveAuditableEvent(AuditableEvent event);

	public void saveAssociation(Association association);

	public Association updateAssociation(Association association);

	public Association getAssociation(String guid);
	
	public PagedResponse getAssociations(AssociationQuery query, Integer start, Integer rows);
	
	public PagedResponse getAssociations(String lid, String userVersion, Integer start, Integer rows);
}
