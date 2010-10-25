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
import gov.nasa.pds.registry.service.RegistryService;
import gov.nasa.pds.registry.util.Examples;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
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
   * @request.representation.example {@link Examples#REQUEST_SCHEME}
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * 
   * @param scheme
   *          to publish
   * @return a HTTP response that indicates an error or the location of the
   *         created association and its guid
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishScheme(ClassificationScheme scheme) {
    try {
      String guid = registryService.publishRegistryObject("Unknown", scheme);
      return Response.created(
          SchemesResource.getSchemeUri((ClassificationScheme) registryService
              .getRegistryObject(guid, scheme.getClass()), uriInfo)).entity(
          guid).build();
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
   * @response.representation.200.example {@link Examples#RESPONSE_SCHEME}
   * @param schemeGuid
   *          globally unique id of scheme
   * @return the classification scheme
   */
  @GET
  @Path("{schemeGuid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public ClassificationScheme getClassificationScheme(
      @PathParam("schemeGuid") String schemeGuid) {
    return (ClassificationScheme) registryService.getRegistryObject(schemeGuid,
        ClassificationScheme.class);
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
    registryService.deleteRegistryObject("Unknown", schemeGuid,
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
    return new NodesResource(this.uriInfo, this.request, this.registryService,
        schemeGuid);
  }

  protected static URI getSchemeUri(ClassificationScheme scheme, UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getSchemesResource").path(
            scheme.getGuid()).build();
  }
}
