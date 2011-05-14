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

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.ClassificationNode;
import gov.nasa.pds.registry.model.ClassificationScheme;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.ObjectAction;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.Report;
import gov.nasa.pds.registry.model.naming.IdentifierGenerator;
import gov.nasa.pds.registry.model.naming.Versioner;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.ObjectFilter;
import gov.nasa.pds.registry.query.RegistryQuery;

import java.util.List;

/**
 * @author pramirez
 * 
 */
public interface RegistryService {

  /**
   * Set where to store all the metadata for registry objects. Typically this
   * has a database back end.
   * 
   * @param metadataStore
   *          for registry object metadata
   */
  public void setMetadataStore(MetadataStore metadataStore);

  /**
   * Get access to the back end store for the registry service. Mostly used for
   * internal purposes.
   * 
   * @return metadata store for registry objects
   */
  public MetadataStore getMetadataStore();

  /**
   * Sets the class used to generate and sort versions for registry objects.
   * 
   * @param versioner
   *          to use when generating a new version of a registry object.
   */
  public void setVersioner(Versioner versioner);

  /**
   * @return versioner used to generate version for registry objects
   */
  public Versioner getVersioner();

  /**
   * Sets the class used to generate unique ids for registry objects
   * 
   * @param idGenerator
   *          to use to generate a guid for registry objects when there is not
   *          one supplied by clients.
   */
  public void setIdentifierGenerator(IdentifierGenerator idGenerator);

  /**
   * @return id generator used to generate guids for registry objects
   */
  public IdentifierGenerator getIdentifierGenerator();

  /**
   * This method allows one to page through the {@link ExtrinsicObject}'s in the
   * registry.
   * 
   * @param start
   *          the index at which to start the result list from
   * @param rows
   *          how many results to return
   * @return a list of extrinsics
   */
  public PagedResponse<ExtrinsicObject> getExtrinsics(Integer start,
      Integer rows);

  /**
   * Retrieves the first set of extrinsics that match the query
   * 
   * @param query
   *          holds a set of filters to match against extrinsics
   * @return a list of extrinsics
   */
  public PagedResponse<ExtrinsicObject> getExtrinsics(RegistryQuery<ExtrinsicFilter> query);

  /**
   * Retrieves a set of extinsics that match the given query. Allows one to page
   * through results.
   * 
   * @param query
   *          holds a set of filters to match against {@link ExtrinsicObject}'s
   * @param start
   *          the index at which to start the result list from. This index
   *          starts at one and if anything less than one is provided it will
   *          default to one.
   * @param rows
   *          how many results to return
   * @return a list of extrinsics
   */
  public PagedResponse<ExtrinsicObject> getExtrinsics(RegistryQuery<ExtrinsicFilter> query,
      Integer start, Integer rows);

  /**
   * Gives back some basic summary information about the registry. This summary
   * information includes the amount of managed objects.
   * 
   * @return registry status
   */
  public Report getReport();

  /**
   * Versions a {@link RegistryObject} in the registry and publishes the
   * contents of the provided extrinsic object. A registry object with the the
   * same lid must be already published otherwise there will be nothing to
   * version off of.
   * 
   * @param user
   *          that has taken the action. Typically this should point to a unique
   *          username.
   * @param object
   *          the contents for this version of the extrinsic object
   * @param major
   *          flag to indicate whether this is a minor or major version
   * @return the guid of the versioned extrinsic object
   * @throws RegistryServiceException
   */
  public String versionObject(String user, RegistryObject object, boolean major)
      throws RegistryServiceException;

  /**
   * Retrieves the latest version of the {@link RegistryObject} with the given
   * logical identifier
   * 
   * @param lid
   *          of extrinsic to look up
   * @return latest version of extrinsic
   */
  public RegistryObject getLatestObject(String lid,
      Class<? extends RegistryObject> objectClass);

  /**
   * Retrieves the earliest version of the {@link RegsitryObject} with the given
   * logical identifier
   * 
   * @param lid
   *          of registry object to look up
   * @param objectClass
   *          the type of object to look up
   * @return earliest version of registry object
   */
  public RegistryObject getEarliestObject(String lid,
      Class<? extends RegistryObject> objectClass);

  /**
   * Retrieves the next version of the {@link RegsitryObject}
   * 
   * @param guid
   *          of the registry object to uniquely identify it
   * @param objectClass
   *          the type of object to look up
   * @return the next version of the registry object otherwise null if there is
   *         no more versions
   * @throws RegistryServiceException 
   */
  public RegistryObject getNextObject(String guid,
      Class<? extends RegistryObject> objectClass) throws RegistryServiceException;

  /**
   * Retrieves the previous version of the {@link RegistryObject}
   * 
   * @param guid
   *          of the registry object to uniquely identify it
   * @param objectClass
   *          the type of object to look up
   * @return the previous version of the registry object otherwise null if there
   *         is no versions before the current one
   * @throws RegistryServiceException 
   */
  public RegistryObject getPreviousObject(String guid,
      Class<? extends RegistryObject> objectClass) throws RegistryServiceException;

  /**
   * Retrieves all versions of a {@link RegistryObject}
   * 
   * @param lid
   *          of the registry object of interest
   * @param objectClass
   *          the type of object to look up
   * @return all versions of the registry object that share the given lid
   */
  public List<RegistryObject> getObjectVersions(String lid,
      Class<? extends RegistryObject> objectClass);

  /**
   * Retrieves all {@link ClassificationNode} for a given
   * {@link ClassificationScheme}
   * 
   * @param scheme
   *          guid for which to get the classification nodes for
   * @return all classification nodes for the scheme's guid
   */
  public List<ClassificationNode> getClassificationNodes(String scheme);

  /**
   * Changes the status of registry object with the given guid and of the given
   * type
   * 
   * @param user
   *          that is requesting the change
   * @param guid
   *          of the registry object to uniquely identify it
   * @param action
   *          which to take (i.e. approve, deprecate, etc.)
   * @param objectClass
   *          identifies the type of registry object
   */
  public void changeObjectStatus(String user, String guid, ObjectAction action,
      Class<? extends RegistryObject> objectClass);

  /**
   * This method allows one to update all the metadata associated with a
   * registry object.
   * 
   * @param user
   *          that is requesting the update
   * @param registryObject
   *          to update too. The update is made to the object with the same guid
   */
  public void updateObject(String user, RegistryObject registryObject);

  /**
   * Retrieves a set of associations that match the given query. Allows one to
   * page through results.
   * 
   * @param query
   *          holds a set of filters to match against {@link Association}'s
   * @param start
   *          the index at which to start the result list from. This index
   *          starts at one and if anything less than one is provided it will
   *          default to one.
   * @param rows
   *          how many results to return
   * @return a list of associations
   */
  public PagedResponse<Association> getAssociations(RegistryQuery<AssociationFilter> query,
      Integer start, Integer rows);

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
  public PagedResponse<? extends RegistryObject> getObjects(
      RegistryQuery<ObjectFilter> query, Integer start, Integer rows,
      Class<? extends RegistryObject> objectClass);

  /**
   * Retrieves the list of (@link AuditableEvent}'s for the affected object
   * 
   * @param affectedObject
   *          guid for the registry object of interest
   * @return list of events associated with the guid
   */
  public PagedResponse<AuditableEvent> getAuditableEvents(
      String affectedObject);

  /**
   * Publishes a registry object to the registry.
   * 
   * @param user
   *          that is requesting the object to be published
   * @param registryObject
   *          to publish
   * @return guid of the published object
   * @throws RegistryServiceException
   */
  public String publishObject(String user, RegistryObject registryObject)
      throws RegistryServiceException;

  /**
   * Publishes a registry object to the registry.
   * 
   * @param user
   *          that is requesting the object to be published
   * @param registryObject
   *          to publish
   * @param packageId
   *          to associate this publish event with
   * @return guid of the published object
   * @throws RegistryServiceException
   */
  public String publishObject(String user, RegistryObject registryObject,
      String packageId) throws RegistryServiceException;

  /**
   * Deletes a {@link RegistryObject} from the registry which share the logical
   * identifier and version.
   * 
   * @param user
   *          that requested the delete
   * @param guid
   *          globally unique identifier of the registry object
   * @param objectClass
   *          type of registry object
   */
  public void deleteObject(String user, String guid,
      Class<? extends RegistryObject> objectClass);

  /**
   * Retrieves the {@link Association} from the registry with the given guid
   * 
   * @param guid
   *          globally unique identifier of the registry object
   * @return the identified association
   * @throws RegistryServiceException 
   */
  public Association getAssocation(String guid) throws RegistryServiceException;

  /**
   * Retrieves a {@link ExtrinsicObject} from the registry
   * 
   * @param guid
   *          globally unique identifier of the extrinsic object
   * @return matching extrinsic object
   * @throws RegistryServiceException 
   */
  public ExtrinsicObject getExtrinsic(String guid) throws RegistryServiceException;

  /**
   * Retrieves a registry object of the requested type
   * 
   * @param guid
   *          globally unique identifier of the object
   * @param objectClass
   *          type of the registry object
   * @return matching registry object
   * @throws RegistryServiceException 
   */
  public RegistryObject getObject(String guid,
      Class<? extends RegistryObject> objectClass) throws RegistryServiceException;

  /**
   * 
   * @param lid logical identifier which correlates to a group of related registry objects
   * @param versionName that specifially identifies an object withing the group
   * @param objectClass type of registry object that is being looked for
   * @return matching registry object
   */
  public RegistryObject getObject(String lid, String versionName,
      Class<? extends RegistryObject> objectClass);

  /**
   * Configures the registry with a list of registry objects as input. This
   * should be limited to publishing a set of Classification Schemes and Nodes
   * that drive registry function. This would include but not limited to object
   * types and association types.
   * 
   * @param user
   *          that has taken the action. Typically this should point to a unique
   *          username.
   * @param registryPackage
   *          to associate objects to
   * @param list
   *          classification schemes and nodes that are apart of this config
   * @return identifier for the package with which the configuration is
   *         associated
   */
  public String configure(String user, RegistryPackage registryPackage,
      List<? extends RegistryObject> list) throws RegistryServiceException;
}
