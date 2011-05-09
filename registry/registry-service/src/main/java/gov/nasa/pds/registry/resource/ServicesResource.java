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

import java.net.URI;

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.Service;
import gov.nasa.pds.registry.query.ObjectFilter;
import gov.nasa.pds.registry.query.ObjectQuery;
import gov.nasa.pds.registry.service.RegistryService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * This resource is responsible for managing Service descriptions.
 * 
 * @author pramirez
 * 
 */
public class ServicesResource {

  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public ServicesResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Publishes a service to the registry.
   * 
   * @request.representation.qname {http://registry.pds.nasa.gov}service
   * @request.representation.mediaType application/xml
   * @request.representation.example {@link gov.nasa.pds.registry.util.Examples#SERVICE}
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * @param service
   *          to publish
   * @return a HTTP response that indicates an error or the location of the
   *         created association and its guid
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishService(Service service) {
    try {
      String guid = registryService.publishObject("Unknown", service);
      return Response.created(
          ServicesResource.getServiceUri((Service) registryService.getObject(
              guid, service.getClass()), uriInfo)).entity(guid).build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Retrieves the service with the given identifier.
   * 
   * @response.representation.200.qname {http://registry.pds.nasa.gov}service
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#SERVICE}
   * 
   * @param guid
   *          globally unique identifier of service
   * @return the matching service
   */
  @GET
  @Path("{guid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Service getService(@PathParam("guid") String guid) {
    return (Service) registryService.getObject(guid, Service.class);
  }

  /**
   * Deletes the service from the registry.
   * 
   * @param guid
   *          globally unique identifier of service
   * @return Response indicating whether the operation succeeded or an error
   */
  @DELETE
  @Path("{guid}")
  public Response deleteService(@PathParam("guid") String guid) {
    registryService.deleteObject("Unknown", guid, Service.class);
    return Response.ok().build();
  }

  /**
   * Allows update of a Service and its contained objects.
   * 
   * @param guid
   *          globally unique identifier of the service
   * @param service
   *          updates to the service
   * @return returns an HTTP response that indicates an error or ok
   */
  @PUT
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  @Path("{guid}")
  public Response updateService(@PathParam("guid") String guid, Service service) {
    registryService.updateObject("Unknown", service);
    return Response.ok().build();
  }

  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  @Path("{guid}")
  public Response updateServiceWithPost(@PathParam("guid") String guid, Service service) {
    return this.updateService(guid, service);
  }

  @SuppressWarnings("unchecked")
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getServices(
      @QueryParam("start") @DefaultValue("1") Integer start,
      @QueryParam("rows") @DefaultValue("20") Integer rows) {
    ObjectFilter filter = new ObjectFilter.Builder().build();
    ObjectQuery.Builder queryBuilder = new ObjectQuery.Builder().filter(filter);
    PagedResponse<Service> rr = (PagedResponse<Service>) registryService
        .getObjects(queryBuilder.build(), start, rows, Service.class);
    Response.ResponseBuilder builder = Response.ok(rr);
    UriBuilder absolute = uriInfo.getAbsolutePathBuilder();
    absolute.queryParam("start", "{start}");
    absolute.queryParam("rows", "{rows}");
    // Add in next link
    if (start - 1 + rows < rr.getNumFound()) {
      int next = start + rows;
      String nextUri = absolute.clone().build(next, rows).toString();
      builder.header("Link", new Link(nextUri, "next", null));
    }
    // Add in previous link
    if (start > 1) {
      int previous = start - rows;
      if (previous < 1)
        previous = 1;
      String previousUri = absolute.clone().build(previous, rows).toString();
      builder.header("Link", new Link(previousUri, "previous", null));
    }
    return builder.build();
  }

  protected static URI getServiceUri(Service service, UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getServicesResource").path(
            service.getGuid()).build();
  }
}
