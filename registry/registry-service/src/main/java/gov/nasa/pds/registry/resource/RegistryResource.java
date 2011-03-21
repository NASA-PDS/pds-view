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

import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.Report;
import gov.nasa.pds.registry.service.RegistryService;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This is the registry resource for managing the registry.
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

  @GET
  public Response listResources() {
    Response.ResponseBuilder builder = Response
        .ok("Welcome to the Registry Service.");
    String associationsUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getAssociationsResource").build().toString();
    builder.header("Link", new Link(associationsUri, "associations", null));
    String classificationsUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getClassificationsResource").build().toString();
    builder.header("Link",
        new Link(classificationsUri, "classifications", null));
    String extrinsicsUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getExtrinsicsResource").build().toString();
    builder.header("Link", new Link(extrinsicsUri, "extrinsics", null));
    String externalIdentifiersUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getExternalIdentifiersResource").build().toString();
    builder.header("Link",
        new Link(externalIdentifiersUri, "identifiers", null));
    String eventsUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getEventsResource").build().toString();
    builder.header("Link", new Link(eventsUri, "events", null));
    String schemesUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getSchemesResource").build().toString();
    builder.header("Link", new Link(schemesUri, "schemes", null));
    String servicesUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getServicesResource").build().toString();
    builder.header("Link", new Link(servicesUri, "services", null));
    String packagesUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getPackagesResource").build().toString();
    builder.header("Link", new Link(packagesUri, "packages", null));
    return builder.build();
  }

  /**
   * Retrieve the status of the registry service. This can be used to monitor
   * the health of the registry.
   * 
   * @response.representation.200.qname {http://registry.pds.nasa.gov}report
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#RESPONSE_REPORT}
   * 
   * @return registry status
   */
  @GET
  @Path("report")
  @Produces( { MediaType.APPLICATION_XML, MediaType.TEXT_XML,
      MediaType.APPLICATION_JSON })
  public Report getReport() {
    return registryService.getReport();
  }

  /**
   * Synchronizes the incoming registry objects with those already present in
   * the registry.
   * 
   * @param products
   *          from some other registry
   */
  @PUT
  @Path("sync")
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public void synchronize(Collection<RegistryObject> products) {
    // TODO implement
  }

  /**
   * Provides access to operations that can be done on extrinsics. The extrinsic
   * resource is simply a subresource of the registry resource.
   * 
   * @return product resource that will process the remaining portion of the
   *         request
   */
  @Path("extrinsics")
  public ExtrinsicsResource getExtrinsicsResource() {
    return new ExtrinsicsResource(uriInfo, request, this.registryService);
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

  /**
   * Provides access to configure the registry.
   * 
   * @return resource to deal with configuration
   */
  @Path("configure")
  public ConfigurationResource getConfigurationResource() {
    return new ConfigurationResource(this.uriInfo, this.request,
        this.registryService);
  }

  /**
   * Provides access to operations on RegistryPackages.
   * 
   * @return resource to deal with packages
   */
  @Path("packages")
  public PackagesResource getPackagesResource() {
    return new PackagesResource(this.uriInfo, this.request,
        this.registryService);
  }
  
  /**
   * Provides access to operations on ExternalIdentifiers.
   * 
   * @return resource to deal with external identifiers
   */
  @Path("identifiers")
  public ExternalIdentifiersResource getExternalIdentifiersResource() {
    return new ExternalIdentifiersResource(this.uriInfo, this.request,
        this.registryService);
  }
}
