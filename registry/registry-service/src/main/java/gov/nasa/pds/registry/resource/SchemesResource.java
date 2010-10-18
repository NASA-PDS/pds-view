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

import gov.nasa.pds.registry.model.ClassificationScheme;
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

  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishScheme(ClassificationScheme scheme) {
    String guid = registryService.publishRegistryObject("Unknown", scheme);
    return Response.created(
        SchemesResource.getSchemeUri((ClassificationScheme) registryService
            .getRegistryObject(guid, scheme.getClass()), uriInfo)).entity(guid)
        .build();
  }

  @GET
  @Path("{schemeGuid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public ClassificationScheme getClassificationScheme(
      @PathParam("schemeGuid") String schemeGuid) {
    return (ClassificationScheme) registryService.getRegistryObject(schemeGuid,
        ClassificationScheme.class);
  }
  
  @DELETE
  @Path("{schemeGuid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response deleteClassificationScheme(@PathParam("schemeGuid") String schemeGuid) {
    registryService.deleteRegistryObject("Unknown", schemeGuid, ClassificationScheme.class);
    return Response.ok().build();
  }

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
