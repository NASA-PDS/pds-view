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

import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.query.AssociationQuery;
import gov.nasa.pds.registry.query.ProductQuery;

import java.util.List;

public interface MetadataStore {

  public PagedResponse getProducts(ProductQuery query, Integer start,
      Integer rows);

  public PagedResponse getAssociations(AssociationQuery query, Integer start,
      Integer rows);

  public PagedResponse getAssociations(String lid, String versionId,
      Integer start, Integer rows);

  public RegistryObject getRegistryObject(String guid,
      Class<? extends RegistryObject> objectClass);

  public RegistryObject getRegistryObject(String lid, String versionId,
      Class<? extends RegistryObject> objectClass);

  public void saveRegistryObject(RegistryObject registryObject);

  public long getNumRegistryObjects(Class<? extends RegistryObject> objectClass);

  public void deleteRegistryObject(String guid,
      Class<? extends RegistryObject> objectClass);

  public void updateRegistryObject(RegistryObject registryObject);

  public List<RegistryObject> getRegistryObjectVersions(String lid,
      Class<? extends RegistryObject> objectClass);

  public List<RegistryObject> getRegistryObjects(Integer start, Integer rows,
      Class<? extends RegistryObject> objectClass);

  public boolean hasRegistryObject(String lid, String versionId,
      Class<? extends RegistryObject> objectClass);

  public boolean hasRegistryObject(String guid,
      Class<? extends RegistryObject> objectClass);
}
