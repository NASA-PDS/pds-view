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
import gov.nasa.pds.registry.model.ClassificationNode;
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
 * This resource is responsible for managing Classification Nodes for a 
 * given Classification
 * Scheme.
 * 
 * @author pramirez
 * 
 */
public class NodesResource {

  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public NodesResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Publishes a classification node to the registry.
   * 
   * @request.representation.qname 
   *                               {http://registry.pds.nasa.gov}classificationNode
   * @request.representation.mediaType application/xml
   * @request.representation.example {@link gov.nasa.pds.registry.util.Examples#REQUEST_NODE}
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * @param node
   *          to publish to registry
   * @param packageGuid
   *          optional package guid which this registry object is a member of
   * @return a HTTP response that indicates an error or the location of the
   *         created association and its guid
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishNode(ClassificationNode node,
      @QueryParam("packageGuid") String packageGuid) {
    try {
      String guid = (packageGuid == null) ? registryService.publishObject(
          "Unknown", node) : registryService.publishObject("Unknown", node,
          packageGuid);
      return Response.created(
          NodesResource.getNodeUri((ClassificationNode) registryService
              .getObject(guid, node.getClass()), uriInfo)).entity(guid).build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Retrieves the classification node with the given global identifier.
   * 
   * @response.representation.200.qname 
   *                                    {http://registry.pds.nasa.gov}classificationNode
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#RESPONSE_NODE}
   * 
   * @param nodeGuid
   *          globally unique identifier of classification node
   * @return classification node
   */
  @GET
  @Path("{nodeGuid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public ClassificationNode getClassificationNode(
      @PathParam("nodeGuid") String nodeGuid) {
    try {
      return (ClassificationNode) registryService.getObject(nodeGuid,
          ClassificationNode.class);
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Deletes the classification node with the given global identifier.
   * 
   * @param nodeGuid
   *          globally unique identifier of node
   * @return Response indicating whether the operation succeeded or had an error
   */
  @DELETE
  @Path("{nodeGuid}")
  public Response deleteClassificationNode(
      @PathParam("nodeGuid") String nodeGuid) {
    registryService.deleteObject("Unknown", nodeGuid, ClassificationNode.class);
    return Response.ok().build();
  }

  protected static URI getNodeUri(ClassificationNode node, UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getNodesResource").path(node.getGuid())
        .build();
  }
}
