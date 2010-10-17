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

import gov.nasa.pds.registry.model.ClassificationNode;
import gov.nasa.pds.registry.model.RegistryResponse;
import gov.nasa.pds.registry.service.RegistryService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author pramirez
 * 
 */
public class NodesResource {

  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  String schemeGuid;

  @Context
  RegistryService registryService;

  public NodesResource(UriInfo uriInfo, Request request,
      RegistryService registryService, String schemeGuid) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
    this.schemeGuid = schemeGuid;
  }

  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishNode(ClassificationNode node) {
    if (node.getParent() == null) {
      node.setParent(schemeGuid);
    }
    String guid = registryService.publishRegistryObject("Unknown", node);
    return Response.created(
        NodesResource.getNodeUri(schemeGuid, (ClassificationNode) registryService
            .getRegistryObject(guid, node.getClass()), uriInfo)).entity(guid)
        .build();
  }
  
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public RegistryResponse getClassificationNodes() {
    return new RegistryResponse(registryService.getClassificationNodes(schemeGuid));
  }

  @GET
  @Path("{nodeGuid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public ClassificationNode getClassificationNode(@PathParam("nodeGuid") String nodeGuid) {
    return (ClassificationNode) registryService.getRegistryObject(nodeGuid, ClassificationNode.class);
  }
  
  @DELETE
  @Path("{nodeGuid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response deleteClassificationNode(@PathParam("nodeGuid") String nodeGuid) {
    registryService.deleteRegistryObject("Unknown", nodeGuid, ClassificationNode.class);
    return Response.ok().build();
  }

  protected static URI getNodeUri(String schemeGuid, ClassificationNode node,
      UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getSchemesResource").path(schemeGuid)
        .path("nodes").path(node.getGuid()).build();
  }
}
