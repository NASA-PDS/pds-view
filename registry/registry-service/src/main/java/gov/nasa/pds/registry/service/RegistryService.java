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
import gov.nasa.pds.registry.model.ClassificationScheme;
import gov.nasa.pds.registry.model.EventType;
import gov.nasa.pds.registry.model.ObjectAction;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.RegistryResponse;
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.Service;
import gov.nasa.pds.registry.model.ServiceBinding;
import gov.nasa.pds.registry.model.SpecificationLink;
import gov.nasa.pds.registry.model.StatusInfo;
import gov.nasa.pds.registry.model.naming.IdentifierGenerator;
import gov.nasa.pds.registry.model.naming.Versioner;
import gov.nasa.pds.registry.query.AssociationQuery;
import gov.nasa.pds.registry.query.ProductQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This class contains all the logic for publishing, versioning, updating, and
 * deleting registry objects. The registry aims to support the registry portion
 * of the CCSDS regrep specification at the same time it leverages the much of
 * the ebXML information model.
 * 
 * @author pramirez
 * 
 */
@org.springframework.stereotype.Service("registryService")
public class RegistryService {

  @Autowired
  MetadataStore metadataStore;

  @Autowired
  @Qualifier("versioner")
  Versioner versioner;

  @Autowired
  @Qualifier("idGenerator")
  IdentifierGenerator idGenerator;

  @Autowired
  @Qualifier("statusInfo")
  StatusInfo statusInfo;

  /**
   * Set where to store all the metadata for registry objects. Typically this
   * has a database back end.
   * 
   * @param metadataStore
   *          for registry object metadata
   */
  public void setMetadataStore(MetadataStore metadataStore) {
    this.metadataStore = metadataStore;
  }

  /**
   * Get access to the back end store for the registry service. Mostly used for
   * internal purposes.
   * 
   * @return metadata store for registry objects
   */
  public MetadataStore getMetadataStore() {
    return metadataStore;
  }

  /**
   * Sets the class used to generate and sort versions for registry objects.
   * 
   * @param versioner
   *          to use when generating a new version of a registry object.
   */
  public void setVersioner(Versioner versioner) {
    this.versioner = versioner;
  }

  /**
   * @return versioner used to generate version for registry objects
   */
  public Versioner getVersioner() {
    return this.versioner;
  }

  /**
   * Sets the class used to generate unique ids for registry objects
   * 
   * @param idGenerator
   *          to use to generate a guid for registry objects when there is not
   *          one supplied by clients.
   */
  public void setIdentifierGenerator(IdentifierGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  /**
   * @return id generator used to generate guids for registry objects
   */
  public IdentifierGenerator getIdentifierGenerator() {
    return this.idGenerator;
  }

  /**
   * This method allows one to page through the {@link Product}'s in the
   * registry.
   * 
   * @param start
   *          the index at which to start the result list from
   * @param rows
   *          how many results to return
   * @return a list of products
   */
  public RegistryResponse getProducts(Integer start, Integer rows) {
    RegistryResponse page = new RegistryResponse(start, metadataStore
        .getNumRegistryObjects(Product.class), metadataStore
        .getRegistryObjects(start, rows, Product.class));
    return page;
  }

  /**
   * Retrieves the first set of products that match the query
   * 
   * @param query
   *          holds a set of filters to match against products
   * @return a list of products
   */
  public RegistryResponse getProducts(ProductQuery query) {
    return this.getProducts(query, 1, 20);
  }

  /**
   * Retrieves a set of products that match the given query. Allows one to page
   * through results.
   * 
   * @param query
   *          holds a set of filters to match against {@link Product}'s
   * @param start
   *          the index at which to start the result list from. This index
   *          starts at one and if anything less than one is provided it will
   *          default to one.
   * @param rows
   *          how many results to return
   * @return a list of products
   */
  public RegistryResponse getProducts(ProductQuery query, Integer start,
      Integer rows) {
    if (start <= 0) {
      start = 1;
    }
    return metadataStore.getProducts(query, start, rows);
  }

  /**
   * Gives back some basic summary information about the registry. This summary
   * information includes the amount of managed objects.
   * 
   * @return registry status
   */
  public StatusInfo getStatus() {
    StatusInfo status = new StatusInfo(this.statusInfo);
    status.setAssociations(metadataStore
        .getNumRegistryObjects(Association.class));
    status.setProducts(metadataStore.getNumRegistryObjects(Product.class));
    status.setServices(metadataStore.getNumRegistryObjects(Service.class));
    status.setClassificationNodes(metadataStore
        .getNumRegistryObjects(ClassificationNode.class));
    status.setClassificationSchemes(metadataStore
        .getNumRegistryObjects(ClassificationScheme.class));
    return status;
  }

  /**
   * Versions a {@link Product} in the registry and publishes the contents of
   * the provided product.
   * 
   * @param user
   *          that has taken the action. Typically this should point to a unique
   *          username.
   * @param lid
   *          logical identifier of the parent registry object
   * @param product
   *          the contents for this version of the product
   * @param major
   *          flag to indicate whether this is a minor or major version
   * @return the guid of the versioned product
   */
  public String versionProduct(String user, String lid, Product product,
      boolean major) {
    Product referencedProduct = this.getLatestProduct(lid);
    product.setGuid(idGenerator.getGuid());
    product.setHome(idGenerator.getHome());
    product.setLid(referencedProduct.getLid());
    product.setVersionName(versioner.getNextVersion(referencedProduct
        .getVersionName(), major));
    product.setStatus(referencedProduct.getStatus());
    metadataStore.saveRegistryObject(product);
    AuditableEvent createEvent = new AuditableEvent(EventType.Created, Arrays
        .asList(product.getGuid()), user);
    createEvent.setGuid(idGenerator.getGuid());
    createEvent.setHome(idGenerator.getHome());
    this.validate(product);
    metadataStore.saveRegistryObject(createEvent);
    AuditableEvent event = new AuditableEvent(EventType.Versioned, Arrays
        .asList(referencedProduct.getGuid()), user);
    event.setGuid(idGenerator.getGuid());
    event.setHome(idGenerator.getHome());
    metadataStore.saveRegistryObject(event);
    return product.getGuid();
  }

  /**
   * Retrieves the latest version of the {@link Product} with the given logical
   * identifier
   * 
   * @param lid
   *          of product to look up
   * @return latest version of product
   */
  public Product getLatestProduct(String lid) {
    // TODO: Make this throw an exception if the lid does not exist
    List<RegistryObject> products = metadataStore.getRegistryObjectVersions(
        lid, Product.class);
    Collections.sort(products, versioner.getComparator());
    if (products.size() > 0) {
      return (Product) products.get(products.size() - 1);
    }
    return null;
  }

  /**
   * Retrieves the earliest version of the {@link Product} with the given
   * logical identifier
   * 
   * @param lid
   *          of product to look up
   * @return earliest version of product
   */
  public Product getEarliestProduct(String lid) {
    // TODO: Make this throw an exception if the lid does not exist
    List<RegistryObject> products = metadataStore.getRegistryObjectVersions(
        lid, Product.class);
    Collections.sort(products, versioner.getComparator());
    if (products.size() > 0) {
      return (Product) products.get(0);
    }
    return null;
  }

  /**
   * Retrieves the next version of the {@link Product}
   * 
   * @param lid
   *          of the current product
   * @param versionId
   *          of the current product. This is the user provided version.
   * @return the next version of the product otherwise null if there is no more
   *         versions
   */
  public Product getNextProduct(String lid, String versionId) {
    // TODO: Make this method throw an exception if the lid or version is not
    // found
    List<RegistryObject> products = metadataStore.getRegistryObjectVersions(
        lid, Product.class);
    Collections.sort(products, versioner.getComparator());
    for (int i = 0; i < products.size(); i++) {
      Product product = (Product) products.get(i);
      if (versionId.equals(product.getVersionId())) {
        if (i < products.size() - 1) {
          return (Product) products.get(i + 1);
        } else {
          return null;
        }
      }
    }
    return null;
  }

  /**
   * Retrieves the previous version of the {@link Product}
   * 
   * @param lid
   *          of the current product
   * @param versionId
   *          of the current product. This is the user provided version.
   * @return the previous version of the product otherwise null if there is no
   *         versions before the current one
   */
  public Product getPreviousProduct(String lid, String versionId) {
    // TODO: Make this method throw an exception if the lid or version is not
    // found
    List<RegistryObject> products = metadataStore.getRegistryObjectVersions(
        lid, Product.class);
    Collections.sort(products, versioner.getComparator());
    for (int i = 0; i < products.size(); i++) {
      Product product = (Product) products.get(i);
      if (versionId.equals(product.getVersionId())) {
        if (i > 0) {
          return (Product) products.get(i - 1);
        } else {
          return null;
        }
      }
    }
    return null;
  }

  /**
   * Retrieves all versions of a {@link Product}
   * 
   * @param lid
   *          of the product of interest
   * @return all versions of the product that share the given lid
   */
  @SuppressWarnings("unchecked")
  public List<Product> getProductVersions(String lid) {
    ArrayList products = new ArrayList<Product>();
    products
        .addAll(metadataStore.getRegistryObjectVersions(lid, Product.class));
    return products;
  }

  /**
   * Retrieves a {@link Product} from the registry with the given identifying
   * information.
   * 
   * @param lid
   *          of the product of interest.
   * @param versionId
   *          of the product of interest. This is the user provided version.
   * @return a product
   */
  public Product getProduct(String lid, String versionId) {
    return (Product) metadataStore.getRegistryObject(lid, versionId,
        Product.class);
  }

  /**
   * Retrieves all {@link ClassificationNode} for a given
   * {@link ClassificationScheme}
   * 
   * @param scheme
   *          guid for which to get the classification nodes for
   * @return all classification nodes for the scheme's guid
   */
  public List<ClassificationNode> getClassificationNodes(String scheme) {
    ArrayList<ClassificationNode> nodes = new ArrayList<ClassificationNode>();
    nodes.addAll(metadataStore.getClassificationNodes(scheme));
    return nodes;
  }

  /**
   * Changes the {@link Product} status with the given identifying information.
   * 
   * @param user
   *          that is requesting the change
   * @param lid
   *          logical identifier of the product
   * @param versionId
   *          of the product. This is the user supplied version
   * @param action
   *          which to take (i.e. approve, deprecate, etc.)
   */
  public void changeStatus(String user, String lid, String versionId,
      ObjectAction action) {
    Product product = (Product) metadataStore.getRegistryObject(lid, versionId,
        Product.class);
    this.changeRegistryObjectStatus(user, product, action);
  }

  /**
   * Changes the status of registry object with the given guid and of the given
   * type
   * 
   * @param user
   *          that is requesting the change
   * @param guid
   *          of the product to uniquely identify it
   * @param action
   *          which to take (i.e. approve, deprecate, etc.)
   * @param objectClass
   *          identifies the type of registry object
   */
  public void changeRegistryObjectStatus(String user, String guid,
      ObjectAction action, Class<? extends RegistryObject> objectClass) {
    RegistryObject registryObject = metadataStore.getRegistryObject(guid,
        objectClass);
    this.changeRegistryObjectStatus(user, registryObject, action);
  }

  private void changeRegistryObjectStatus(String user,
      RegistryObject registryObject, ObjectAction action) {
    registryObject.setStatus(action.getObjectStatus());
    metadataStore.updateRegistryObject(registryObject);
    AuditableEvent event = new AuditableEvent(action.getEventType(), Arrays
        .asList(registryObject.getGuid()), user);
    event.setGuid(idGenerator.getGuid());
    event.setHome(idGenerator.getHome());
    metadataStore.saveRegistryObject(event);
  }

  /**
   * This method allows one to update all the metadata associated with a
   * registry object.
   * 
   * @param user
   *          that is requesting the update
   * @param registryObject
   *          to update too. The update is made to the object with the same guid
   */
  public void updateRegistryObject(String user, RegistryObject registryObject) {
    metadataStore.updateRegistryObject(registryObject);
  }

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
  public RegistryResponse getAssociations(AssociationQuery query,
      Integer start, Integer rows) {
    return metadataStore.getAssociations(query, start, rows);
  }

  /**
   * Retrieves the list of (@link AuditableEvent}'s for the affected object
   * 
   * @param affectedObject
   *          guid for the registry object of interest
   * @return list of events associated with the guid
   */
  public RegistryResponse getAuditableEvents(String affectedObject) {
    RegistryResponse response = new RegistryResponse();
    response.setResults(metadataStore.getAuditableEvents(affectedObject));
    return response;
  }

  /**
   * Retrieves all associations for a given registry object. The registry object
   * can be the source or target in the association
   * 
   * @param lid
   *          of the object of interest
   * @param versionId
   *          of the object of interest. This is the user provided version.
   * @param start
   *          the index at which to start the result list from. This index
   *          starts at one and if anything less than one is provided it will
   *          default to one.
   * @param rows
   *          how many results to return
   * @return a list of associations
   */
  public RegistryResponse getAssociations(String lid, String versionId,
      Integer start, Integer rows) {
    if (start <= 0) {
      start = 1;
    }
    return metadataStore.getAssociations(lid, versionId, start, rows);
  }

  /**
   * Publishes a registry object to the registry.
   * 
   * @param user
   *          that is requesting the object to be published
   * @param registryObject
   *          to publish
   * @return guid of the published object
   */
  public String publishRegistryObject(String user, RegistryObject registryObject) {
    // Check to see if there is an existing product with the same lid and
    // versionId
    // TODO: Make this throw some exception instead
    if (metadataStore.hasRegistryObject(registryObject.getLid(), registryObject
        .getVersionId(), registryObject.getClass())) {
      return null;
    }
    if (registryObject.getGuid() == null) {
      registryObject.setGuid(idGenerator.getGuid());
    }
    if (registryObject.getHome() == null) {
      registryObject.setHome(idGenerator.getHome());
    }
    registryObject.setVersionName(versioner.getInitialVersion());
    if (registryObject.getLid() == null) {
      registryObject.setLid(idGenerator.getGuid());
    }
    if (registryObject.getVersionId() == null) {
      registryObject.setVersionId(registryObject.getVersionName());
    }
    registryObject.setStatus(ObjectStatus.Submitted);
    this.validate(registryObject);
    metadataStore.saveRegistryObject(registryObject);
    this.createAuditableEvents(user, registryObject, EventType.Created);
    return registryObject.getGuid();
  }

  private void createAuditableEvents(String user,
      RegistryObject registryObject, EventType eventType) {
    List<String> affectedObjects = new ArrayList<String>();
    affectedObjects.add(registryObject.getGuid());
    // If this is a service we have other affected objects
    if (registryObject instanceof Service) {
      Service service = (Service) registryObject;
      for (ServiceBinding binding : service.getServiceBindings()) {
        affectedObjects.add(binding.getGuid());
        for (SpecificationLink link : binding.getSpecificationLinks()) {
          affectedObjects.add(link.getGuid());
        }
      }
    }
    AuditableEvent event = new AuditableEvent(eventType, affectedObjects, user);
    event.setGuid(idGenerator.getGuid());
    event.setHome(idGenerator.getHome());
    metadataStore.saveRegistryObject(event);
  }

  private void validate(RegistryObject registryObject) {
    if (registryObject instanceof Product) {
      this.validateProduct((Product) registryObject);
    } else if (registryObject instanceof Association) {
      this.validateAssociation((Association) registryObject);
    } else if (registryObject instanceof ClassificationNode) {
      this.validateNode((ClassificationNode) registryObject);
    } else if (registryObject instanceof Service) {
      this.validateService((Service) registryObject);
    }
  }

  private void validateProduct(Product product) {
    // Nothing to validate at this point
  }

  private void validateAssociation(Association association) {
    // Only thing to validate at this point is to make sure the home values are
    // set
    if (association.getSourceHome() == null) {
      association.setSourceHome(idGenerator.getHome());
    }
    if (association.getTargetHome() == null) {
      association.setTargetHome(idGenerator.getHome());
    }
  }

  private void validateNode(ClassificationNode node) {
    StringBuffer path = new StringBuffer(node.getCode());
    path.insert(0, "/");
    boolean done = false;
    ClassificationNode currentNode = node;
    while (!done) {
      boolean nodeParentFound = false;
      try {
        currentNode = (ClassificationNode) this.getRegistryObject(currentNode
            .getParent(), ClassificationNode.class);
        path.insert(0, currentNode.getCode());
        path.insert(0, "/");
        nodeParentFound = true;
      } catch (NoResultException nre) {
        // Suppress as it could be the parent is a classification scheme
      }
      if (!nodeParentFound) {
        ClassificationScheme parent = (ClassificationScheme) this
            .getRegistryObject(currentNode.getParent(),
                ClassificationScheme.class);
        path.insert(0, parent.getGuid());
        path.insert(0, "/");
        done = true;
      }
    }
    node.setPath(path.toString());
  }

  private void validateService(Service service) {
    for (ServiceBinding binding : service.getServiceBindings()) {
      if (binding.getGuid() == null) {
        binding.setGuid(idGenerator.getGuid());
      }
      if (binding.getGuid() == null) {
        binding.setHome(idGenerator.getHome());
      }
      if (binding.getService() == null) {
        binding.setService(service.getGuid());
      }
      for (SpecificationLink link : binding.getSpecificationLinks()) {
        if (link.getGuid() == null) {
          link.setGuid(idGenerator.getGuid());
        }
        if (link.getHome() == null) {
          link.setHome(idGenerator.getHome());
        }
        if (link.getServiceBinding() == null) {
          link.setServiceBinding(binding.getGuid());
        }
      }
    }
  }

  /**
   * Deletes a {@link RegistryObject} from the registry which share the logical
   * identifier and version.
   * 
   * @param user
   *          that requested the delete
   * @param lid
   *          logical identifier of registry object
   * @param versionId
   *          user defined version for the registry object
   * @param objectClass
   *          type of registry object
   */
  public void deleteRegistryObject(String user, String lid, String versionId,
      Class<? extends RegistryObject> objectClass) {
    RegistryObject registryObject = metadataStore.getRegistryObject(lid,
        versionId, objectClass);
    this.deleteRegistryObject(user, registryObject, objectClass);
  }

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
  public void deleteRegistryObject(String user, String guid,
      Class<? extends RegistryObject> objectClass) {
    RegistryObject registryObject = metadataStore.getRegistryObject(guid,
        objectClass);
    this.deleteRegistryObject(user, registryObject, objectClass);
  }

  private void deleteRegistryObject(String user, RegistryObject registryObject,
      Class<? extends RegistryObject> objectClass) {
    metadataStore.deleteRegistryObject(registryObject.getGuid(), objectClass);
    this.createAuditableEvents(user, registryObject, EventType.Deleted);
  }

  /**
   * Retrieves the {@link Association} from the registry with the given guid
   * 
   * @param guid
   *          globally unique identifier of the registry object
   * @return the identified association
   */
  public Association getAssocation(String guid) {
    return (Association) this.getRegistryObject(guid, Association.class);
  }

  public Product getProduct(String guid) {
    return (Product) this.getRegistryObject(guid, Product.class);
  }

  public RegistryObject getRegistryObject(String guid,
      Class<? extends RegistryObject> objectClass) {
    return metadataStore.getRegistryObject(guid, objectClass);
  }
}
