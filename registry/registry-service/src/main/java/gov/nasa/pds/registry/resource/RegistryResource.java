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

package gov.nasa.pds.registry.resource;

import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.model.StatusInfo;
import gov.nasa.pds.registry.service.RegistryService;
import gov.nasa.pds.registry.util.Examples;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This is the root resource for managing the registry.
 * 
 * @author pramirez
 * 
 */
@Path("registry")
@Component
@Scope("request")
public class RegistryResource {

  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Autowired
  RegistryService registryService;

  /**
   * Retrieve the status of the registry service. This can be used to monitor
   * the health of the registry.
   * 
   * @response.representation.200.qname 
   *                                    {http://registry.pds.nasa.gov}status_information
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link Examples#RESPONSE_STATUS}
   * 
   * @return registry status
   */
  @GET
  @Path("status")
  @Produces( { MediaType.APPLICATION_XML, MediaType.TEXT_XML,
      MediaType.APPLICATION_JSON })
  public StatusInfo getStatus() {
    return registryService.getStatus();
  }

  /**
   * Synchronizes the incoming registry products with those already present in
   * the registry.
   * 
   * @param products
   *          from some other registry
   */
  @PUT
  @Path("sync")
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public void synchronize(Collection<Product> products) {
    // TODO implement
  }

  /**
   * Provides access to operations that can be done on products. The product
   * resource is simply a subresource of the registry resource.
   * 
   * @return product resource that will process the remaining portion of the
   *         request
   */
  @Path("products")
  public ProductsResource getProductsResource() {
    return new ProductsResource(uriInfo, request, this.registryService);
  }

  /**
   * Provides access to operations that can be done on associations. The
   * association resource deals with links between registry objects. At this
   * level this is merely a method to be able to delegate along this resource
   * path.
   * 
   * @return association resource that will process the remaining portion of the
   *         request
   * 
   */
  @Path("associations")
  public AssociationsResource getAssociationsResource() {
    return new AssociationsResource(this.uriInfo, this.request,
        this.registryService);
  }

  /**
   * Provides access to operations that can be done with Auditable Events.
   * Auditable Events are recorded for many actions involved with
   * RegistryObject's such as approved, created, deleted, deprecated, etc..
   * 
   * @return resource to deal with auditable events
   */
  @Path("events")
  public EventsResource getEventsResource() {
    return new EventsResource(this.uriInfo, this.request, this.registryService);
  }

  /**
   * Provides access to operations that can be done with ClassificationSchemes.
   * Additionally manages ClassificationNodes as each node can be traced back to
   * a parent and should not exist without a scheme.
   * 
   * @return resource to deal with classification schemes
   */
  @Path("schemes")
  public SchemesResource getSchemesResource() {
    return new SchemesResource(this.uriInfo, this.request, this.registryService);
  }

  /**
   * Provides access to operations that can be done with Service registrations.
   * 
   * @return resource to deal with services
   */
  @Path("services")
  public ServicesResource getServicesResource() {
    return new ServicesResource(this.uriInfo, this.request,
        this.registryService);
  }

  /**
   * Provides access to operations on Classifications that are made on
   * RegistryObjects.
   * 
   * @return resource to deal with classifications
   */
  @Path("classifications")
  public ClassificationsResource getClassificationsResource() {
    return new ClassificationsResource(this.uriInfo, this.request,
        this.registryService);
  }
}
