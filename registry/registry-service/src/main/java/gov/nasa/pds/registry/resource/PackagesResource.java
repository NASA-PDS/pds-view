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
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.service.RegistryService;

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
 * @author pramirez
 * 
 */
public class PackagesResource {
  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public PackagesResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Publishes a package to the registry. Publishing includes validation,
   * assigning an internal version, validating the submission, and notification.
   * 
   * @request.representation.qname {http://registry.pds.nasa.gov}registryPackage
   * @request.representation.mediaType application/xml
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * 
   * @param registryPackage
   *          to publish
   * @return returns an HTTP response that indicates an error or the location of
   *         the created package and its guid
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishPackage(RegistryPackage registryPackage) {
    // TODO: Change to add user
    try {
      String guid = registryService.publishObject("Unkown", registryPackage);
      return Response.created(
          PackagesResource.getPackageUri((RegistryPackage) registryService
              .getObject(guid, RegistryPackage.class), uriInfo)).entity(guid)
          .build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  protected static URI getPackageUri(RegistryPackage registryPackage,
      UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getPackagesResource").path(
            registryPackage.getGuid()).build();
  }

  /**
   * Retrieves a package with the given global identifier.
   * 
   * @response.representation.200.qname 
   *                                    {http://registry.pds.nasa.gov}registryPackage
   * @response.representation.200.mediaType application/xml
   * @return the package
   */
  @GET
  @Path("{guid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public RegistryPackage getPackage(@PathParam("guid") String guid) {
    try {
      RegistryPackage registryPackage = (RegistryPackage) registryService
          .getObject(guid, RegistryPackage.class);
      return registryPackage;
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Deletes the package with the given guid
   * 
   * @param guid
   *          of package
   * @return Response indicating whether the operation succeeded or had an error
   */
  @DELETE
  @Path("{guid}")
  public Response deletePackage(@PathParam("guid") String guid) {
    registryService.deleteObject("Unknown", guid, RegistryPackage.class);
    return Response.ok().build();
  }
}
