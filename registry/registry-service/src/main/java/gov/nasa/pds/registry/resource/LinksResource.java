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

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.ExternalLink;
import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.query.ObjectFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.service.RegistryService;

import java.net.URI;

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
 * This resource manages ExternalLinks which are essentially URIs to items that
 * are outside the control of the registry.
 * 
 * @author pramirez
 * 
 */
public class LinksResource {

  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public LinksResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Publishes a link to the registry.
   * 
   * @request.representation.qname {http://registry.pds.nasa.gov}externalLink
   * @request.representation.mediaType application/xml
   * @request.representation.example {@link gov.nasa.pds.registry.util.Examples#EXTERNAL_LINK}
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * @param link
   *          to publish
   * @return a HTTP response that indicates an error or the location of the
   *         created association and its guid
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishLink(ExternalLink link) {
    try {
      String guid = registryService.publishObject("Unknown", link);
      return Response.created(
          LinksResource.getLinkUri((ExternalLink) registryService.getObject(
              guid, link.getClass()), uriInfo)).entity(guid).build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Retrieves the external link with the given identifier.
   * 
   * @response.representation.200.qname 
   *                                    {http://registry.pds.nasa.gov}externalLink
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#EXTERNAL_LINK}
   * 
   * @param guid
   *          globally unique identifier of service
   * @return the matching service
   */
  @GET
  @Path("{guid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public ExternalLink getLInk(@PathParam("guid") String guid) {
    try {
      return (ExternalLink) registryService.getObject(guid, ExternalLink.class);
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Deletes the external link from the registry.
   * 
   * @param guid
   *          globally unique identifier of link
   * @return Response indicating whether the operation succeeded or an error
   */
  @DELETE
  @Path("{guid}")
  public Response deleteLink(@PathParam("guid") String guid) {
    registryService.deleteObject("Unknown", guid, ExternalLink.class);
    return Response.ok().build();
  }

  /**
   * Allows update of a ExternalLink.
   * 
   * @param guid
   *          globally unique identifier of the service
   * @param link
   *          updates to the liunk
   * @return returns an HTTP response that indicates an error or ok
   */
  @PUT
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  @Path("{guid}")
  public Response updateLink(@PathParam("guid") String guid, ExternalLink link) {
    registryService.updateObject("Unknown", link);
    return Response.ok().build();
  }

  /**
   * This method is to support clients that can not do a PUT operation
   * 
   * @param guid
   *          globally unique identifier of the service
   * @param link
   *          updates to the liunk
   * @return returns an HTTP response that indicates an error or ok
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  @Path("{guid}")
  public Response updateLinkWithPost(@PathParam("guid") String guid,
      ExternalLink link) {
    return this.updateLink(guid, link);
  }

  @SuppressWarnings("unchecked")
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getLink(
      @QueryParam("start") @DefaultValue("1") Integer start,
      @QueryParam("rows") @DefaultValue("20") Integer rows) {
    ObjectFilter filter = new ObjectFilter.Builder().build();
    RegistryQuery.Builder<ObjectFilter> queryBuilder = new RegistryQuery.Builder<ObjectFilter>().filter(filter);
    PagedResponse<ExternalLink> rr = (PagedResponse<ExternalLink>) registryService
        .getObjects(queryBuilder.build(), start, rows, ExternalLink.class);
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

  protected static URI getLinkUri(ExternalLink link, UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getLinksResource").path(link.getGuid())
        .build();
  }
}
