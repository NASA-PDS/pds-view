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
import gov.nasa.pds.registry.model.ClassificationNode;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.query.AssociationQuery;
import gov.nasa.pds.registry.query.ExtrinsicQuery;
import gov.nasa.pds.registry.query.ObjectQuery;

import java.util.List;

/**
 * This interface provides the methods required by the
 * {@link RegistryServiceImpl} in order for it to operate. This is the extension
 * point one would implement when providing a different back end storage.
 * 
 * @author pramirez
 * 
 */
public interface MetadataStore {

  /**
   * Retrieves products from back end store that match the query and are within
   * the requested result list range
   * 
   * @param query
   *          to filter against extrinsics
   * @param start
   *          index within the results to start at. This index is one based
   * @param rows
   *          number of results to get
   * @return list of extrinsics
   */
  public PagedResponse<ExtrinsicObject> getExtrinsics(ExtrinsicQuery query,
      Integer start, Integer rows);

  /**
   * Retrieves associations from the back end store that match the query and are
   * within the requested results list range
   * 
   * @param query
   *          to filter against associations.
   * @param start
   *          index within the results to start at. This index is one based
   * @param rows
   *          number of results to get. If equal to -1 return all.
   * @return list of associations
   */
  public PagedResponse<Association> getAssociations(AssociationQuery query,
      Integer start, Integer rows);

  /**
   * Retrieves the list of events that the given affected object was referenced
   * in
   * 
   * @param affectedObject
   *          guid of a registry object
   * @return list of events
   */
  public List<AuditableEvent> getAuditableEvents(String affectedObject);

  /**
   * Retrieves the list of classification nodes which fall under a given
   * classification scheme
   * 
   * @param scheme
   *          guid of the scheme
   * @return list of classification nodes
   */
  public List<ClassificationNode> getClassificationNodes(String scheme);

  /**
   * Checks to see if a code exists within a classification scheme
   * 
   * @param scheme
   *          guid of the scheme
   * @param code
   *          to check for in scheme
   * @return flag to indicate existence
   */
  public boolean hasClassificationNode(String scheme, String code);

  /**
   * Returns the registry object with a given guid and of the given type
   * 
   * @param guid
   *          of requested registry object
   * @param objectClass
   *          type of object. For instance, a {@link ExtrinsicObject},
   *          {@link ClassificationNode}, {@link Service}, etc. Anything that
   *          extends from a {@link RegistryObject}
   * @return
   */
  public RegistryObject getRegistryObject(String guid,
      Class<? extends RegistryObject> objectClass);

  /**
   * Retrieves a registry object that is identified by its logical identifier
   * and user supplied version.
   * 
   * @param lid
   *          logical identifier of registry object.
   * @param versionName
   *          version of registry object.
   * @param objectClass
   *          type of registry object.
   * @return The identfied registry object of the requested type.
   */
  public RegistryObject getRegistryObject(String lid, String versionName,
      Class<? extends RegistryObject> objectClass);

  /**
   * Stores a registry object into the back end.
   * 
   * @param registryObject
   *          to store
   */
  public void saveRegistryObject(RegistryObject registryObject);

  /**
   * Gets the count of registry objects managed in the backed of a given type.
   * 
   * @param objectClass
   *          type of object to look up
   * @return count of objects
   */
  public long getNumRegistryObjects(Class<? extends RegistryObject> objectClass);

  /**
   * Removes a registry object from the back end store.
   * 
   * @param guid
   *          of object to remove
   * @param objectClass
   *          type of object to remove
   */
  public void deleteRegistryObject(String guid,
      Class<? extends RegistryObject> objectClass);

  /**
   * Updates a registry object that shares the guid of the given object
   * 
   * @param registryObject
   *          to update too
   */
  public void updateRegistryObject(RegistryObject registryObject);

  /**
   * Returns all versions of a registry object that share a logical identifier
   * 
   * @param lid
   *          logical identifier of objects to look up
   * @param objectClass
   *          type of registry object
   * @return list of matching registry objects
   */
  public List<? extends RegistryObject> getRegistryObjectVersions(String lid,
      Class<? extends RegistryObject> objectClass);

  /**
   * This method allows paging through registry objects of a given type.
   * 
   * @param start
   *          index within the results to start at. This index is one based
   * @param rows
   *          number of results to get
   * @param objectClass
   *          type of object to get
   * @return list of registry objects that share the type requested
   */
  public List<? extends RegistryObject> getRegistryObjects(Integer start, Integer rows,
      Class<? extends RegistryObject> objectClass);

  /**
   * Generic query for a given class of registry objects. This query only
   * contains attributes that are applicable across all registry objects.
   * 
   * @param query
   *          based on a set of filters
   * @param start
   *          index within the results to start at. This index is one based
   * @param rows
   *          number of results to get
   * @param objectClass
   *          the type of registry object to look for
   * @return list of {@link RegistryObject} with the given class
   */
  public PagedResponse<? extends RegistryObject> getRegistryObjects(
      ObjectQuery query, Integer start, Integer rows,
      Class<? extends RegistryObject> objectClass);

  /**
   * Test to see if a registry object exists with a logical identifier, version,
   * and type requested.
   * 
   * @param lid
   *          logical id of object
   * @param versionName
   *          registry generated version
   * @param objectClass
   *          type of registry object
   * @return flag indicating existence of registry object
   */
  public boolean hasRegistryObject(String lid, String versionName,
      Class<? extends RegistryObject> objectClass);

  /**
   * Test to see if a registry object exists with a guid and type requested.
   * 
   * @param guid
   *          globally unique identifier of object
   * @param objectClass
   *          type of registry object
   * @return flag indicating existence of registry object
   */
  public boolean hasRegistryObject(String guid,
      Class<? extends RegistryObject> objectClass);

  /**
   * Test to see if there are any versions of the registry object with the
   * logical id and type requested
   * 
   * @param lid
   *          logical id of object
   * @param objectClass
   *          type of registry object
   * @return flag indicating existence of registry object
   */
  public boolean hasRegistryObjectVersions(String lid,
      Class<? extends RegistryObject> objectClass);

}
