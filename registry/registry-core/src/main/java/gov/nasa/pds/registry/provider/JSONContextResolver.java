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

package gov.nasa.pds.registry.provider;

import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.Classification;
import gov.nasa.pds.registry.model.ClassificationNode;
import gov.nasa.pds.registry.model.ClassificationScheme;
import gov.nasa.pds.registry.model.RegistryResponse;
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.Service;
import gov.nasa.pds.registry.model.ServiceBinding;
import gov.nasa.pds.registry.model.SpecificationLink;
import gov.nasa.pds.registry.model.StatusInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import org.springframework.stereotype.Component;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

@Provider
@Produces( { MediaType.APPLICATION_JSON })
@Component
public final class JSONContextResolver implements ContextResolver<JAXBContext> {

  private final JAXBContext context;

  @SuppressWarnings("unchecked")
  private final Set<Class> types;

  @SuppressWarnings("unchecked")
  private final Class[] cTypes = { Association.class, Product.class,
      AuditableEvent.class, Classification.class, ClassificationNode.class,
      ClassificationScheme.class, Service.class, ServiceBinding.class,
      SpecificationLink.class, RegistryResponse.class, RegistryObject.class,
      StatusInfo.class };

  @SuppressWarnings("unchecked")
  public JSONContextResolver() throws Exception {
    this.types = new HashSet(Arrays.asList(cTypes));
    this.context = new JSONJAXBContext(JSONConfiguration.natural().build(),
        cTypes);
  }

  public JAXBContext getContext(Class<?> objectType) {
    return (types.contains(objectType)) ? context : null;
  }
}
