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

  public void setMetadataStore(MetadataStore metadataStore) {
    this.metadataStore = metadataStore;
  }

  public MetadataStore getMetadataStore() {
    return metadataStore;
  }

  public void setVersioner(Versioner versioner) {
    this.versioner = versioner;
  }

  public Versioner getVersioner() {
    return this.versioner;
  }

  public void setIdentifierGenerator(IdentifierGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  public IdentifierGenerator getIdentifierGenerator() {
    return this.idGenerator;
  }

  public RegistryResponse getProducts(Integer start, Integer rows) {
    RegistryResponse page = new RegistryResponse(start, metadataStore
        .getNumRegistryObjects(Product.class), metadataStore
        .getRegistryObjects(start, rows, Product.class));
    return page;
  }

  public RegistryResponse getProducts(ProductQuery query) {
    return this.getProducts(query, 1, 20);
  }

  public RegistryResponse getProducts(ProductQuery query, Integer start,
      Integer rows) {
    return metadataStore.getProducts(query, start, rows);
  }

  public StatusInfo getStatus() {
    StatusInfo status = new StatusInfo(this.statusInfo);
    status.setAssociations(metadataStore.getNumRegistryObjects(Association.class));
    status.setProducts(metadataStore.getNumRegistryObjects(Product.class));
    status.setServices(metadataStore.getNumRegistryObjects(Service.class));
    status.setClassificationNodes(metadataStore.getNumRegistryObjects(ClassificationNode.class));
    status.setClassificationSchemes(metadataStore.getNumRegistryObjects(ClassificationScheme.class));
    return status;
  }

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

  // TODO: Make this throw an exception if the lid does not exist
  public Product getLatestProduct(String lid) {
    List<RegistryObject> products = metadataStore.getRegistryObjectVersions(
        lid, Product.class);
    Collections.sort(products, versioner.getComparator());
    if (products.size() > 0) {
      return (Product) products.get(products.size() - 1);
    }
    return null;
  }

  // TODO: Make this throw an exception if the lid does not exist
  public Product getEarliestProduct(String lid) {
    List<RegistryObject> products = metadataStore.getRegistryObjectVersions(
        lid, Product.class);
    Collections.sort(products, versioner.getComparator());
    if (products.size() > 0) {
      return (Product) products.get(0);
    }
    return null;
  }

  // TODO: Make this method throw an exception if the lid or version is not
  // found
  public Product getNextProduct(String lid, String versionId) {
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

  // TODO: Make this method throw an exception if the lid or version is not
  // found
  public Product getPreviousProduct(String lid, String versionId) {
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

  @SuppressWarnings("unchecked")
  public List<Product> getProductVersions(String lid) {
    ArrayList products = new ArrayList<Product>();
    products
        .addAll(metadataStore.getRegistryObjectVersions(lid, Product.class));
    return products;
  }

  public Product getProduct(String lid, String versionId) {
    return (Product) metadataStore.getRegistryObject(lid, versionId,
        Product.class);
  }

  public List<ClassificationNode> getClassificationNodes(String scheme) {
    ArrayList<ClassificationNode> nodes = new ArrayList<ClassificationNode>();
    nodes.addAll(metadataStore.getClassificationNodes(scheme));
    return nodes;
  }

  public void changeStatus(String user, String lid, String versionId,
      ObjectAction action) {
    Product product = (Product) metadataStore.getRegistryObject(lid, versionId,
        Product.class);
    product.setStatus(action.getObjectStatus());
    metadataStore.updateRegistryObject(product);
    AuditableEvent event = new AuditableEvent(action.getEventType(), Arrays
        .asList(product.getGuid()), user);
    event.setGuid(idGenerator.getGuid());
    event.setHome(idGenerator.getHome());
    metadataStore.saveRegistryObject(event);
  }

  public Product updateProduct(Product product) {
    return null;
  }

  public RegistryResponse getAssociations(AssociationQuery query,
      Integer start, Integer rows) {
    return metadataStore.getAssociations(query, start, rows);
  }

  public RegistryResponse getAuditableEvents(String affectedObject) {
    RegistryResponse response = new RegistryResponse();
    response.setResults(metadataStore.getAuditableEvents(affectedObject));
    return response;
  }

  public RegistryResponse getAssociations(String lid, String versionId,
      Integer start, Integer rows) {
    return metadataStore.getAssociations(lid, versionId, start, rows);
  }

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

  public void deleteRegistryObject(String user, String lid, String versionId,
      Class<? extends RegistryObject> objectClass) {
    RegistryObject registryObject = metadataStore.getRegistryObject(lid,
        versionId, objectClass);
    this.deleteRegistryObject(user, registryObject, objectClass);
  }

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
