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
import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.RegistryResponse;
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.model.RegistryStatus;
import gov.nasa.pds.registry.model.Slot;
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
 * Class to hold some sample model objects for WADL generation and skeleton
 * implementations
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
						"http://pdsbeta.jpl.nasa.gov/registry-service/registry/storage/1234-v1.0/label",
						"label", null));
	}
	public final static Product REQUEST_PRODUCT = new Product();
	static {
		REQUEST_PRODUCT.setDescription("Default Description");
		REQUEST_PRODUCT.setHome("http://pdsbeta.jpl.nasa.gov/registry-service");
		REQUEST_PRODUCT.setLid("1234");
		REQUEST_PRODUCT.setVersionName("1.0");
		REQUEST_PRODUCT.setObjectType("person");
		REQUEST_PRODUCT.setSlots(RESPONSE_SLOTS);
	}

	public final static Product RESPONSE_PRODUCT = new Product();
	static {
		RESPONSE_PRODUCT.setGuid(UUID.randomUUID().toString());
		RESPONSE_PRODUCT.setDescription("Default Description");
		RESPONSE_PRODUCT
				.setHome("http://pdsbeta.jpl.nasa.gov/registry-service");
		RESPONSE_PRODUCT.setLid("1234");
		RESPONSE_PRODUCT.setVersionName("1.0");
		RESPONSE_PRODUCT.setVersionId("1.0");
		RESPONSE_PRODUCT.setStatus(ObjectStatus.Submitted);
		RESPONSE_PRODUCT.setObjectType("person");
		RESPONSE_PRODUCT.setSlots(RESPONSE_SLOTS);
	}

	public final static Product RESPONSE_PRODUCT_UPDATED = new Product();
	static {
		RESPONSE_PRODUCT_UPDATED.setGuid(UUID.randomUUID().toString());
		RESPONSE_PRODUCT_UPDATED.setDescription("Default Description");
		RESPONSE_PRODUCT_UPDATED
				.setHome("http://pdsbeta.jpl.nasa.gov/registry-service");
		RESPONSE_PRODUCT_UPDATED.setLid("1234");
		RESPONSE_PRODUCT_UPDATED.setVersionName("1.0");
		RESPONSE_PRODUCT_UPDATED.setVersionId("1.1");
		RESPONSE_PRODUCT_UPDATED.setStatus(ObjectStatus.Submitted);
		RESPONSE_PRODUCT_UPDATED.setObjectType("person");
		RESPONSE_PRODUCT_UPDATED.setSlots(RESPONSE_SLOTS);
	}

	public final static Product RESPONSE_PRODUCT_APPROVED = new Product();
	static {
		RESPONSE_PRODUCT_APPROVED.setGuid(UUID.randomUUID().toString());
		RESPONSE_PRODUCT_APPROVED.setDescription("Default Description");
		RESPONSE_PRODUCT_APPROVED
				.setHome("http://pdsbeta.jpl.nasa.gov/registry-service");
		RESPONSE_PRODUCT_APPROVED.setLid("1234");
		RESPONSE_PRODUCT_APPROVED.setVersionId("1.1");
		RESPONSE_PRODUCT_APPROVED.setVersionName("1.0");
		RESPONSE_PRODUCT_APPROVED.setStatus(ObjectStatus.Approved);
		RESPONSE_PRODUCT_APPROVED.setObjectType("person");
		RESPONSE_PRODUCT_APPROVED.setSlots(RESPONSE_SLOTS);
	}

	public final static Product RESPONSE_PRODUCT_DEPRECATED = new Product();
	static {
		RESPONSE_PRODUCT_DEPRECATED.setGuid(UUID.randomUUID().toString());
		RESPONSE_PRODUCT_DEPRECATED.setDescription("Default Description");
		RESPONSE_PRODUCT_DEPRECATED
				.setHome("http://pdsbeta.jpl.nasa.gov/registry-service");
		RESPONSE_PRODUCT_DEPRECATED.setLid("1234");
		RESPONSE_PRODUCT_DEPRECATED.setVersionId("1.1");
		RESPONSE_PRODUCT_DEPRECATED.setVersionName("1.0");
		RESPONSE_PRODUCT_DEPRECATED.setStatus(ObjectStatus.Deprecated);
		RESPONSE_PRODUCT_DEPRECATED.setObjectType("person");
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
		REQUEST_PRODUCT_VERSIONED.setGuid(UUID.randomUUID().toString());
		REQUEST_PRODUCT_VERSIONED.setDescription("Default Description");
		REQUEST_PRODUCT_VERSIONED
				.setHome("http://pdsbeta.jpl.nasa.gov/registry-service");
		REQUEST_PRODUCT_VERSIONED.setLid("1234");
		REQUEST_PRODUCT_VERSIONED.setVersionName("2.0");
		REQUEST_PRODUCT_VERSIONED.setVersionId("1.0");
		REQUEST_PRODUCT_VERSIONED.setStatus(ObjectStatus.Submitted);
		REQUEST_PRODUCT_VERSIONED.setObjectType("person");
		REQUEST_PRODUCT_VERSIONED.setSlots(RESPONSE_SLOTS);
	}

	public final static Product RESPONSE_PRODUCT_VERSIONED = new Product();
	static {
		RESPONSE_PRODUCT_VERSIONED.setGuid(UUID.randomUUID().toString());
		RESPONSE_PRODUCT_VERSIONED.setDescription("Default Description");
		RESPONSE_PRODUCT_VERSIONED
				.setHome("http://pdsbeta.jpl.nasa.gov/registry-service");
		RESPONSE_PRODUCT_VERSIONED.setLid("1234");
		RESPONSE_PRODUCT_VERSIONED.setVersionName("2.0");
		RESPONSE_PRODUCT_VERSIONED.setVersionId("1.1");
		RESPONSE_PRODUCT_VERSIONED.setStatus(ObjectStatus.Submitted);
		RESPONSE_PRODUCT_VERSIONED.setObjectType("person");
		RESPONSE_PRODUCT_VERSIONED.setSlots(RESPONSE_SLOTS);
	}

	public final static RegistryResponse RESPONSE_PRODUCT_VERSIONS = new RegistryResponse();
	static {
		List<Product> ros = new ArrayList<Product>();
		ros.add(RESPONSE_PRODUCT);
		ros.add(RESPONSE_PRODUCT_VERSIONED);
		RESPONSE_PRODUCT_VERSIONS.setResults(ros);
	}

	public final static RegistryResponse RESPONSE_PAGED = new RegistryResponse(1, 1L);
	static {
		List<Product> results = new ArrayList<Product>();
		results.add(RESPONSE_PRODUCT);
		RESPONSE_PAGED.setResults(results);
	}

	public final static Association REQUEST_ASSOCIATION = new Association();
	static {
		REQUEST_ASSOCIATION.setGuid(UUID.randomUUID().toString());
		REQUEST_ASSOCIATION
				.setHome("http://pdsbeta.jpl.nasa.gov/registry-service");
		REQUEST_ASSOCIATION.setSourceLid("1234");
		REQUEST_ASSOCIATION
				.setSourceHome("http://pdsbeta.jpl.nasa.gov/registry-service");
		REQUEST_ASSOCIATION.setSourceVersionId("1.0");
		REQUEST_ASSOCIATION.setTargetLid("1234");
		REQUEST_ASSOCIATION
				.setTargetHome("http://pdsbeta.jpl.nasa.gov/registry-service");
		REQUEST_ASSOCIATION.setTargetVersionId("3.0");
		REQUEST_ASSOCIATION.setStatus(ObjectStatus.Submitted);
	}

}
