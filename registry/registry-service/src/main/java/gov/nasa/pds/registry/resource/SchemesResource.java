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
import gov.nasa.pds.registry.model.ClassificationScheme;
import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.query.ObjectFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.service.RegistryService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
 * This resource is responsible for managing operations involving Classification
 * Schemes.
 * 
 * @author pramirez
 * 
 */
public class SchemesResource {

  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public SchemesResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Publishes a Classification Scheme to the registry.
   * 
   * @request.representation.qname 
   *                               {http://registry.pds.nasa.gov}classificationScheme
   * @request.representation.mediaType application/xml
   * @request.representation.example {@link gov.nasa.pds.registry.util.Examples#REQUEST_SCHEME}
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * 
   * @param scheme
   *          to publish to registry
   * @param packageGuid
   *          optional package guid which this registry object is a member of
   * @return a HTTP response that indicates an error or the location of the
   *         created association and its guid
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishScheme(ClassificationScheme scheme,
      @QueryParam("packageGuid") String packageGuid) {
    try {
      String guid = (packageGuid == null) ? registryService.publishObject(
          "Unknown", scheme) : registryService.publishObject("Unknown", scheme,
          packageGuid);
      return Response.created(
          SchemesResource.getSchemeUri((ClassificationScheme) registryService
              .getObject(guid, scheme.getClass()), uriInfo)).entity(guid)
          .build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Retrieves the classification scheme with the given identifier
   * 
   * @response.representation.200.qname 
   *                                    {http://registry.pds.nasa.gov}classificationScheme
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#RESPONSE_SCHEME}
   * @param schemeGuid
   *          globally unique id of scheme
   * @return the classification scheme
   */
  @GET
  @Path("{schemeGuid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public ClassificationScheme getClassificationScheme(
      @PathParam("schemeGuid") String schemeGuid) {
    try {
      return (ClassificationScheme) registryService.getObject(schemeGuid,
          ClassificationScheme.class);
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Deletes the classification scheme with the given guid
   * 
   * @param schemeGuid
   *          globally unique id of scheme
   * @return Response indicating whether the operation succeeded or had an error
   */
  @DELETE
  @Path("{schemeGuid}")
  public Response deleteClassificationScheme(
      @PathParam("schemeGuid") String schemeGuid) {
    registryService.deleteObject("Unknown", schemeGuid,
        ClassificationScheme.class);
    return Response.ok().build();
  }

  /**
   * This returns a resource that manages the classification nodes for the given
   * scheme
   * 
   * @param schemeGuid
   *          globally unique id of scheme
   * @return classification node resource
   */
  @Path("{schemeGuid}/nodes")
  public NodesResource getNodesResource(
      @PathParam("schemeGuid") String schemeGuid) {
    try {
      ClassificationScheme scheme = (ClassificationScheme) registryService
          .getObject(schemeGuid, ClassificationScheme.class);
      return new NodesResource(this.uriInfo, this.request,
          this.registryService, scheme);
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }
  
  @SuppressWarnings("unchecked")
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getServices(
      @QueryParam("start") @DefaultValue("1") Integer start,
      @QueryParam("rows") @DefaultValue("20") Integer rows) {
    ObjectFilter filter = new ObjectFilter.Builder().build();
    RegistryQuery.Builder<ObjectFilter> queryBuilder = new RegistryQuery.Builder<ObjectFilter>()
        .filter(filter);
    // TODO: Fix so cast is not needed
    PagedResponse<ClassificationScheme> pr = (PagedResponse<ClassificationScheme>) registryService
        .getObjects(queryBuilder.build(), start, rows, ClassificationScheme.class);
    Response.ResponseBuilder builder = Response.ok(pr);
    UriBuilder absolute = uriInfo.getAbsolutePathBuilder();
    absolute.queryParam("start", "{start}");
    absolute.queryParam("rows", "{rows}");
    // Add in next link
    if (start - 1 + rows < pr.getNumFound()) {
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

  protected static URI getSchemeUri(ClassificationScheme scheme, UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getSchemesResource").path(
            scheme.getGuid()).build();
  }
}
