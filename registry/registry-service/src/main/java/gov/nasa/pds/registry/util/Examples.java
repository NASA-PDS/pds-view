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

package gov.nasa.pds.registry.util;

import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.ClassificationNode;
import gov.nasa.pds.registry.model.ClassificationScheme;
import gov.nasa.pds.registry.model.EventType;
import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.NodeType;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.RegistryResponse;
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.model.RegistryStatus;
import gov.nasa.pds.registry.model.Service;
import gov.nasa.pds.registry.model.ServiceBinding;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.model.SpecificationLink;
import gov.nasa.pds.registry.model.StatusInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Class to hold some sample model objects for WADL generation.
 * 
 * @author pramirez
 * 
 */
public class Examples {
	public final static Set<Slot> RESPONSE_SLOTS = new HashSet<Slot>();
	static {
		RESPONSE_SLOTS.add(new Slot("first-name", Arrays.asList("John")));
		RESPONSE_SLOTS.add(new Slot("last-name", Arrays.asList("Doe")));
		RESPONSE_SLOTS.add(new Slot("phone", Arrays.asList("(818)123-4567",
				"(818)765-4321")));
	}

	public final static Collection<Link> RESPONSE_LINKS = new ArrayList<Link>();
	static {
		RESPONSE_LINKS
				.add(new Link(
						"http://pds.jpl.nasa.gov/registry-service/registry/storage/1234-v1.0/label",
						"label", null));
	}
	public final static Product REQUEST_PRODUCT = new Product();
	static {
		REQUEST_PRODUCT.setDescription("Default Description");
		REQUEST_PRODUCT.setHome("http://pds.jpl.nasa.gov/registry-service");
		REQUEST_PRODUCT.setLid("1234");
		REQUEST_PRODUCT.setVersionName("1.0");
		REQUEST_PRODUCT.setObjectType("Product");
		REQUEST_PRODUCT.setSlots(RESPONSE_SLOTS);
	}

	public final static Product RESPONSE_PRODUCT = new Product();
	static {
		RESPONSE_PRODUCT.setGuid("urn:uuid" + UUID.randomUUID().toString());
		RESPONSE_PRODUCT.setDescription("Default Description");
		RESPONSE_PRODUCT
				.setHome("http://pds.jpl.nasa.gov/registry-service");
		RESPONSE_PRODUCT.setLid("1234");
		RESPONSE_PRODUCT.setVersionName("1.0");
		RESPONSE_PRODUCT.setVersionId("1.0");
		RESPONSE_PRODUCT.setStatus(ObjectStatus.Submitted);
		RESPONSE_PRODUCT.setObjectType("Product");
		RESPONSE_PRODUCT.setSlots(RESPONSE_SLOTS);
	}

	public final static Product RESPONSE_PRODUCT_UPDATED = new Product();
	static {
		RESPONSE_PRODUCT_UPDATED.setGuid("urn:uuid" + UUID.randomUUID().toString());
		RESPONSE_PRODUCT_UPDATED.setDescription("Default Description");
		RESPONSE_PRODUCT_UPDATED
				.setHome("http://pds.jpl.nasa.gov/registry-service");
		RESPONSE_PRODUCT_UPDATED.setLid("1234");
		RESPONSE_PRODUCT_UPDATED.setVersionName("1.0");
		RESPONSE_PRODUCT_UPDATED.setVersionId("1.1");
		RESPONSE_PRODUCT_UPDATED.setStatus(ObjectStatus.Submitted);
		RESPONSE_PRODUCT_UPDATED.setObjectType("Produc");
		RESPONSE_PRODUCT_UPDATED.setSlots(RESPONSE_SLOTS);
	}

	public final static Product RESPONSE_PRODUCT_APPROVED = new Product();
	static {
		RESPONSE_PRODUCT_APPROVED.setGuid(UUID.randomUUID().toString());
		RESPONSE_PRODUCT_APPROVED.setDescription("Default Description");
		RESPONSE_PRODUCT_APPROVED
				.setHome("http://pds.jpl.nasa.gov/registry-service");
		RESPONSE_PRODUCT_APPROVED.setLid("1234");
		RESPONSE_PRODUCT_APPROVED.setVersionId("1.1");
		RESPONSE_PRODUCT_APPROVED.setVersionName("1.0");
		RESPONSE_PRODUCT_APPROVED.setStatus(ObjectStatus.Approved);
		RESPONSE_PRODUCT_APPROVED.setObjectType("Product");
		RESPONSE_PRODUCT_APPROVED.setSlots(RESPONSE_SLOTS);
	}

	public final static Product RESPONSE_PRODUCT_DEPRECATED = new Product();
	static {
		RESPONSE_PRODUCT_DEPRECATED.setGuid("urn:uuid" + UUID.randomUUID().toString());
		RESPONSE_PRODUCT_DEPRECATED.setDescription("Default Description");
		RESPONSE_PRODUCT_DEPRECATED
				.setHome("http://pds.jpl.nasa.gov/registry-service");
		RESPONSE_PRODUCT_DEPRECATED.setLid("1234");
		RESPONSE_PRODUCT_DEPRECATED.setVersionId("1.1");
		RESPONSE_PRODUCT_DEPRECATED.setVersionName("1.0");
		RESPONSE_PRODUCT_DEPRECATED.setStatus(ObjectStatus.Deprecated);
		RESPONSE_PRODUCT_DEPRECATED.setObjectType("Product");
		RESPONSE_PRODUCT_DEPRECATED.setSlots(RESPONSE_SLOTS);
	}

	public final static StatusInfo RESPONSE_STATUS = new StatusInfo();
	static {
		RESPONSE_STATUS.setStatus(RegistryStatus.OK);
		RESPONSE_STATUS.setServerStarted(new GregorianCalendar());
	}

	public final static RegistryResponse RESPONSE_REGISTRY_OBJECT_REVISIONS = new RegistryResponse();
	static {
		List<Product> ros = new ArrayList<Product>();
		ros.add(RESPONSE_PRODUCT);
		ros.add(RESPONSE_PRODUCT_UPDATED);
		ros.add(RESPONSE_PRODUCT_APPROVED);
		ros.add(RESPONSE_PRODUCT_DEPRECATED);
		RESPONSE_REGISTRY_OBJECT_REVISIONS.setResults(ros);
	}

	public final static Product REQUEST_PRODUCT_VERSIONED = new Product();
	static {
		REQUEST_PRODUCT_VERSIONED.setGuid("urn:uuid" + UUID.randomUUID().toString());
		REQUEST_PRODUCT_VERSIONED.setDescription("Default Description");
		REQUEST_PRODUCT_VERSIONED
				.setHome("http://pds.jpl.nasa.gov/registry-service");
		REQUEST_PRODUCT_VERSIONED.setLid("1234");
		REQUEST_PRODUCT_VERSIONED.setVersionName("2.0");
		REQUEST_PRODUCT_VERSIONED.setVersionId("1.0");
		REQUEST_PRODUCT_VERSIONED.setStatus(ObjectStatus.Submitted);
		REQUEST_PRODUCT_VERSIONED.setObjectType("Product");
		REQUEST_PRODUCT_VERSIONED.setSlots(RESPONSE_SLOTS);
	}

	public final static Product RESPONSE_PRODUCT_VERSIONED = new Product();
	static {
		RESPONSE_PRODUCT_VERSIONED.setGuid("urn:uuid" + UUID.randomUUID().toString());
		RESPONSE_PRODUCT_VERSIONED.setDescription("Default Description");
		RESPONSE_PRODUCT_VERSIONED
				.setHome("http://pds.jpl.nasa.gov/registry-service");
		RESPONSE_PRODUCT_VERSIONED.setLid("1234");
		RESPONSE_PRODUCT_VERSIONED.setVersionName("2.0");
		RESPONSE_PRODUCT_VERSIONED.setVersionId("1.1");
		RESPONSE_PRODUCT_VERSIONED.setStatus(ObjectStatus.Submitted);
		RESPONSE_PRODUCT_VERSIONED.setObjectType("Product");
		RESPONSE_PRODUCT_VERSIONED.setSlots(RESPONSE_SLOTS);
	}

	public final static RegistryResponse RESPONSE_PRODUCT_VERSIONS = new RegistryResponse();
	static {
		List<Product> vers = new ArrayList<Product>();
		vers.add(RESPONSE_PRODUCT);
		vers.add(RESPONSE_PRODUCT_VERSIONED);
		RESPONSE_PRODUCT_VERSIONS.setResults(vers);
	}

	public final static RegistryResponse RESPONSE_PAGED = new RegistryResponse(1, 1L);
	static {
		List<Product> results = new ArrayList<Product>();
		results.add(RESPONSE_PRODUCT);
		RESPONSE_PAGED.setResults(results);
	}

	public final static Association REQUEST_ASSOCIATION = new Association();
	static {
		REQUEST_ASSOCIATION.setGuid("urn:uuid" + UUID.randomUUID().toString());
		REQUEST_ASSOCIATION
				.setHome("http://pds.jpl.nasa.gov/registry-service");
		REQUEST_ASSOCIATION.setSourceLid("1234");
		REQUEST_ASSOCIATION
				.setSourceHome("http://pds.jpl.nasa.gov/registry-service");
		REQUEST_ASSOCIATION.setSourceVersionId("1.0");
		REQUEST_ASSOCIATION.setTargetLid("1234");
		REQUEST_ASSOCIATION
				.setTargetHome("http://pds.jpl.nasa.gov/registry-service");
		REQUEST_ASSOCIATION.setTargetVersionId("3.0");
		REQUEST_ASSOCIATION.setStatus(ObjectStatus.Submitted);
	}
	
	public final static Association RESPONSE_ASSOCIATION = new Association();
	static {
    RESPONSE_ASSOCIATION.setGuid("urn:uuid" + UUID.randomUUID().toString());
    RESPONSE_ASSOCIATION
        .setHome("http://pds.jpl.nasa.gov/registry-service");
    RESPONSE_ASSOCIATION.setSourceLid("1234");
    RESPONSE_ASSOCIATION
        .setSourceHome("http://pds.jpl.nasa.gov/registry-service");
    RESPONSE_ASSOCIATION.setSourceVersionId("1.0");
    RESPONSE_ASSOCIATION.setTargetLid("1234");
    RESPONSE_ASSOCIATION
        .setTargetHome("http://pds.jpl.nasa.gov/registry-service");
    RESPONSE_ASSOCIATION.setTargetVersionId("3.0");
    RESPONSE_ASSOCIATION.setStatus(ObjectStatus.Submitted);
	}

	public final static RegistryResponse RESPONSE_ASSOCIATION_QUERY = new RegistryResponse();
	static {
	  List<Association> results = new ArrayList<Association>();
	  results.add(REQUEST_ASSOCIATION);
	  results.add(RESPONSE_ASSOCIATION);
	  RESPONSE_ASSOCIATION_QUERY.setResults(results);
	}
	
	public final static AuditableEvent CREATE_EVENT = new AuditableEvent(EventType.Created, Arrays.asList("urn:uuid:foo"), "username");
	static {
	  CREATE_EVENT.setGuid("urn:uuid" + UUID.randomUUID().toString());
	  CREATE_EVENT.setHome("http://pds.jpl.nasa.gov/registry-service");
	}
	
	public final static AuditableEvent APPROVE_EVENT = new AuditableEvent(EventType.Approved, Arrays.asList("urn:uuid:foo"), "username");
	static {
    APPROVE_EVENT.setGuid("urn:uuid" + UUID.randomUUID().toString());
    APPROVE_EVENT.setHome("http://pds.jpl.nasa.gov/registry-service");
	}
	
	public final static RegistryResponse RESPONSE_AUDITABLE_EVENTS = new RegistryResponse();
	static {
	  List<AuditableEvent> results = new ArrayList<AuditableEvent>();
	  results.add(CREATE_EVENT);
	  results.add(APPROVE_EVENT);
	  RESPONSE_AUDITABLE_EVENTS.setResults(results);
	}
	
	public final static ClassificationScheme REQUEST_SCHEME = new ClassificationScheme();
	static {
    REQUEST_SCHEME.setGuid("urn:registry:ObjectTypeScheme");
    REQUEST_SCHEME.setName("Test Canonical Object Type Classification Scheme");
    REQUEST_SCHEME.setDescription("This is the canonical object type classification that is one of the core registry objects");
    REQUEST_SCHEME.setIsInternal(true);
    REQUEST_SCHEME.setNodeType(NodeType.UniqueCode);
	}
	
	public final static ClassificationScheme RESPONSE_SCHEME = new ClassificationScheme();
	static {
    RESPONSE_SCHEME.setGuid("urn:registry:ObjectTypeScheme");
    RESPONSE_SCHEME.setLid("urn:uuid" + UUID.randomUUID().toString());
    RESPONSE_SCHEME.setVersionName("1.0");
    RESPONSE_SCHEME.setVersionId("1.0");
    RESPONSE_SCHEME.setHome("http://pds.jpl.nasa.gov/registry-service");
    RESPONSE_SCHEME.setName("Test Canonical Object Type Classification Scheme");
    RESPONSE_SCHEME.setDescription("This is the canonical object type classification that is one of the core registry objects");
    RESPONSE_SCHEME.setIsInternal(true);
    RESPONSE_SCHEME.setNodeType(NodeType.UniqueCode);
	}

	public final static ClassificationNode REQUEST_NODE = new ClassificationNode();
	static {
	  REQUEST_NODE.setGuid("urn:registry:ObjectTypeScheme:Product");
	  REQUEST_NODE.setName("Product Node");
	  REQUEST_NODE.setDescription("This is the classification node for product.");
	  REQUEST_NODE.setCode("Product");
	  REQUEST_NODE.setParent("urn:registry:ObjectTypeScheme");
	}
	
	public final static ClassificationNode RESPONSE_NODE = new ClassificationNode();
	static {
    RESPONSE_NODE.setGuid("urn:registry:ObjectTypeScheme:Product");
    RESPONSE_NODE.setHome("http://pds.jpl.nasa.gov/registry-service");
    RESPONSE_NODE.setName("Product Node");
    RESPONSE_NODE.setDescription("This is the classification node for product.");
    RESPONSE_NODE.setCode("Product");
    RESPONSE_NODE.setPath("/urn:registry:ObjectTypeScheme/Product");
    RESPONSE_NODE.setParent("urn:registry:ObjectTypeScheme");
	}
	
	public final static ClassificationNode RESPONSE_NODE_OTHER = new ClassificationNode();
  static {
    RESPONSE_NODE_OTHER.setGuid("urn:registry:ObjectTypeScheme:Product:Test");
    RESPONSE_NODE_OTHER.setHome("http://pds.jpl.nasa.gov/registry-service");
    RESPONSE_NODE_OTHER.setName("Test Product Node");
    RESPONSE_NODE_OTHER.setDescription("This is the classification node for product.");
    RESPONSE_NODE_OTHER.setCode("Test");
    RESPONSE_NODE_OTHER.setPath("/urn:registry:ObjectTypeScheme/Product/Test");
    RESPONSE_NODE_OTHER.setParent("urn:registry:ObjectTypeScheme:Product");
  }
	
	public final static RegistryResponse RESPONSE_NODES = new RegistryResponse();
	static {
	  List<ClassificationNode> results = new ArrayList<ClassificationNode>();
	  results.add(RESPONSE_NODE);
	  results.add(RESPONSE_NODE_OTHER);
	  RESPONSE_NODES.setResults(results);
	}
	
	public final static SpecificationLink SPECIFICATION_LINK = new SpecificationLink();
	static {
	  SPECIFICATION_LINK.setName("HTTP Specification Link");
	  SPECIFICATION_LINK.setDescription("This is a link to the HTTP specification.");
	  SPECIFICATION_LINK.setSpecificationObject("urn:uuid:HTTPSpecificationDocument");
	  SPECIFICATION_LINK.setUsageDescription("Use a browser to access the PDS site. The acceptable browsers are listed in the usage parameters.");
	  SPECIFICATION_LINK.setUsageParameters(Arrays.asList("Firefox", "Internet Explorer", "Chrome", "Safari"));
	}
	
	public final static ServiceBinding SERVICE_BINDING = new ServiceBinding();
	static {
	  SERVICE_BINDING.setName("PDS Main Site");
	  SERVICE_BINDING.setDescription("This is the PDS main web site");
	  SERVICE_BINDING.setAccessURI("http://pds.jpl.nasa.gov");
	  Set<SpecificationLink> links = new HashSet<SpecificationLink>();
	  links.add(SPECIFICATION_LINK);
	  SERVICE_BINDING.setSpecificationLinks(links);
	}
	
	public final static Service SERVICE = new Service();
	static {
	  Set<ServiceBinding> bindings = new HashSet<ServiceBinding>();
	  bindings.add(SERVICE_BINDING);
	  SERVICE.setServiceBindings(bindings);
	  SERVICE.setName("PDS Service");
	  SERVICE.setDescription("This is a service to test adding a service description to the registry");
	}
}
