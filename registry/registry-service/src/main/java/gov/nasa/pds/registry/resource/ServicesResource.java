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

import gov.nasa.pds.registry.model.Service;
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

  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishService(Service service) {
    String guid = registryService.publishRegistryObject("Unknown", service);
    return Response.created(
        ServicesResource.getServiceUri((Service) registryService
            .getRegistryObject(guid, service.getClass()), uriInfo)).entity(guid)
        .build();
  }
  
  @GET
  @Path("{guid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Service getService(
      @PathParam("guid") String guid) {
    return (Service) registryService.getRegistryObject(guid,
        Service.class);
  }
  
  @DELETE
  @Path("{guid}")
  public Response deleteService(@PathParam("guid") String guid) {
    registryService.deleteRegistryObject("Unknown", guid, Service.class);
    return Response.ok().build();
  }
  
  protected static URI getServiceUri(Service service, UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getServicesResource").path(
            service.getGuid()).build();
  }
}
