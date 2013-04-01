//  Copyright 2009-2011, by the California Institute of Technology.
//  ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//  Any commercial use must be negotiated with the Office of Technology 
//  Transfer at the California Institute of Technology.
//  
//  This software is subject to U. S. export control laws and regulations 
//  (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//  is subject to U.S. export control laws and regulations, the recipient has 
//  the responsibility to obtain export licenses or other export authority as 
//  may be required before exporting such information to foreign countries or 
//  providing access to foreign nationals.
//  
//  $Id$
//

package gov.nasa.pds.registry.resource;

import java.net.URI;

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Classification;
import gov.nasa.pds.registry.service.RegistryService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.UriInfo;

/**
 * This resource is responsible for managing Classifications with the 
 * registry service.
 * 
 * @author pramirez
 */
public class ClassificationsResource {

  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public ClassificationsResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Publishes a classification to the registry. Publishing includes validation,
   * assigning an internal version, validating the submission, and notification.
   * 
   * @request.representation.qname {http://registry.pds.nasa.gov}classification
   * @request.representation.mediaType application/xml
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * 
   * @param classification
   *          to publish to registry
   * @param packageGuid
   *          optional package guid which this registry object is a member of
   * @return returns an HTTP response that indicates an error or the location of
   *         the created classification and its guid
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishClassification(Classification classification,
      @QueryParam("packageGuid") String packageGuid) {
    // TODO: Change to add user
    try {
      String guid = (packageGuid == null) ? registryService.publishObject(
          "Unknown", classification) : registryService.publishObject("Unknown",
          classification, packageGuid);
      return Response.created(
          ClassificationsResource.getClassificationUri(
              (Classification) registryService.getObject(guid,
                  Classification.class), uriInfo)).entity(guid).build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  protected static URI getClassificationUri(Classification classification,
      UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getClassificationsResource").path(
            classification.getGuid()).build();
  }

  /**
   * Retrieves a classification with the given global identifier.
   * 
   * @response.representation.200.qname 
   *                                    {http://registry.pds.nasa.gov}classification
   * @response.representation.200.mediaType application/xml
   * @return the classification
   */
  @GET
  @Path("{guid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Classification getClassification(@PathParam("guid") String guid) {
    try {
      Classification classification = (Classification) registryService
          .getObject(guid, Classification.class);
      return classification;

    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Deletes the classification with the given global identifier.
   * 
   * @param guid
   *          of classification
   * @return Response indicating whether the operation succeeded or had an error
   */
  @DELETE
  @Path("{guid}")
  public Response deleteClassification(@PathParam("guid") String guid) {
    registryService.deleteObject("Unknown", guid, Classification.class);
    return Response.ok().build();
  }

}
