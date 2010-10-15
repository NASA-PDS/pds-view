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
import gov.nasa.pds.registry.model.EventType;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.RegistryResponse;
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.StatusInfo;
import gov.nasa.pds.registry.model.naming.IdentifierGenerator;
import gov.nasa.pds.registry.model.naming.Versioner;
import gov.nasa.pds.registry.query.AssociationQuery;
import gov.nasa.pds.registry.query.ProductQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("registryService")
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
		RegistryResponse page = new RegistryResponse(start, metadataStore.getNumRegistryObjects(Product.class));
		page.setResults(metadataStore.getRegistryObjects(start, rows, Product.class));
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
		return this.statusInfo;
	}

	public String versionProduct(String user, String lid, Product product,
			boolean major) {
		Product referencedProduct = this.getLatestProduct(lid);
		product.setGuid(idGenerator.getGuid());
		product.setHome(idGenerator.getHome());
		product.setVersionName(versioner.getNextVersion(referencedProduct
				.getVersionName(), major));
		product.setStatus(referencedProduct.getStatus());
		metadataStore.saveRegistryObject(product);
		AuditableEvent event = new AuditableEvent(EventType.Versioned,
				referencedProduct.getGuid(), user);
		event.setGuid(idGenerator.getGuid());
		event.setHome(idGenerator.getHome());
		metadataStore.saveRegistryObject(event);
		return product.getGuid();
	}

  //TODO: Make this throw an exception if the lid does not exist
	public Product getLatestProduct(String lid) {
		List<RegistryObject> products = metadataStore.getRegistryObjectVersions(lid, Product.class);
		Collections.sort(products, versioner.getComparator());
		if (products.size() > 0) {
			return (Product) products.get(products.size() - 1);
		}
		return null;
	}

	//TODO: Make this throw an exception if the lid does not exist
	public Product getEarliestProduct(String lid) {
    List<RegistryObject> products = metadataStore.getRegistryObjectVersions(lid, Product.class);
		Collections.sort(products, versioner.getComparator());
		if (products.size() > 0) {
			return (Product) products.get(0);
		}
		return null;
	}

	// TODO: Make this method throw an exception if the lid or version is not
	// found
	public Product getNextProduct(String lid, String versionId) {
    List<RegistryObject> products = metadataStore.getRegistryObjectVersions(lid, Product.class);
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
    List<RegistryObject> products = metadataStore.getRegistryObjectVersions(lid, Product.class);
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
		products.addAll(metadataStore.getRegistryObjectVersions(lid, Product.class));
		return products;
	}

	public Product getProduct(String lid, String versionId) {
		return (Product) metadataStore.getRegistryObject(lid, versionId, Product.class);
	}

	public Product changeStatus(String lid, String versionId,
			ObjectStatus status) {
		Product product = (Product) metadataStore.getRegistryObject(lid, versionId, Product.class);
		product.setStatus(status);
		metadataStore.updateRegistryObject(product);
		return product;
	}

	public void deleteProduct(String user, String lid, String versionId) {
		Product product = (Product) metadataStore.getRegistryObject(lid, versionId, Product.class);
		if (product != null) {
			metadataStore.deleteRegistryObject(product.getGuid(), Product.class);
			AuditableEvent event = new AuditableEvent(EventType.Deleted,
					product.getGuid(), user);
	    event.setGuid(idGenerator.getGuid());
	    event.setHome(idGenerator.getHome());
			metadataStore.saveRegistryObject(event);
		}
	}

	public Product updateProduct(Product product) {
		return null;
	}

	public RegistryResponse getAssociations(AssociationQuery query, Integer start,
			Integer rows) {
		return metadataStore.getAssociations(query, start, rows);
	}

	public RegistryResponse getAssociations(String lid, String versionId,
			Integer start, Integer rows) {
		return metadataStore.getAssociations(lid, versionId, start, rows);
	}

  public String publishProduct(String user, Product product) {
    // Check to see if there is an existing product with the same lid and
    // versionId
    // TODO: Make this throw some exception instead
    if (metadataStore
        .hasRegistryObject(product.getLid(), product.getVersionId(), Product.class)) {
      return null;
    }
    product.setGuid(idGenerator.getGuid());
    product.setHome(idGenerator.getHome());
    product.setVersionName(versioner.getInitialVersion());
    product.setStatus(ObjectStatus.Submitted);
    metadataStore.saveRegistryObject(product);
    AuditableEvent event = new AuditableEvent(EventType.Created, product
        .getGuid(), user);
    event.setGuid(idGenerator.getGuid());
    event.setHome(idGenerator.getHome());
    metadataStore.saveRegistryObject(event);
    return product.getGuid();
  }
  
	public String publishAssociation(String user, Association association) {
		association.setGuid(idGenerator.getGuid());
		association.setHome(idGenerator.getHome());
		if (association.getSourceHome() == null) {
			association.setSourceHome(idGenerator.getHome());
		}
		if (association.getTargetHome() == null) {
			association.setTargetHome(idGenerator.getHome());
		}
		association.setLid(association.getGuid());
		association.setVersionName(versioner.getInitialVersion());
		association.setStatus(ObjectStatus.Submitted);
		metadataStore.saveRegistryObject(association);
		AuditableEvent event = new AuditableEvent(EventType.Created,
				association.getGuid(), user);
		event.setGuid(idGenerator.getGuid());
		event.setHome(idGenerator.getHome());
    metadataStore.saveRegistryObject(event);
		return association.getGuid();
	}
	
	public void deleteAssociation(String user, String guid) {
	  Association association = (Association) metadataStore.getRegistryObject(guid, Association.class);
	  if (association != null) {
	    metadataStore.deleteRegistryObject(guid, Association.class);
      AuditableEvent event = new AuditableEvent(EventType.Deleted,
          association.getGuid(), user);
      event.setGuid(idGenerator.getGuid());
      event.setHome(idGenerator.getHome());
      metadataStore.saveRegistryObject(event);
	  }
	}

  public Association getAssocation(String guid) {
    return (Association) this.getRegistryObject(guid, Association.class);
  }

  
  public Product getProduct(String guid) {
    return (Product) this.getRegistryObject(guid, Product.class);
  }
  
	private RegistryObject getRegistryObject(String guid, Class<? extends RegistryObject> objectClass) {
	  return metadataStore.getRegistryObject(guid, objectClass);
	}
}
