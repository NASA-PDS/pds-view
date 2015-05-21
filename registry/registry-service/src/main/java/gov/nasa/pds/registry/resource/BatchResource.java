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
//  $Id: BatchResource.java 9404 2011-08-28 22:46:34Z shardman $
//

package gov.nasa.pds.registry.resource;

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.RegistryObjectList;
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.service.RegistryService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * This resource is responsible for managing Batch of the 
 * registry service.
 *
 * @author hyunee
 */
public class BatchResource {
  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public BatchResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Configures the registry with a set of classification schemes and 
   * nodes.
   * 
   * @param packageId
   * @param name
   * @param description
   * @param objectList
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML })
  public Response publishObjects(RegistryObjectList objList, 
		  @QueryParam("packageGuid") String packageGuid) {
	  try {
		  if (packageGuid==null) {
			  registryService.publishObjects("Unknown", objList.getObjects());
		  }
		  else {
			  registryService.publishObjects("Unknown", objList.getObjects(), packageGuid);
		  }
	  } catch (RegistryServiceException ex) {
		  ex.printStackTrace();
		  throw new WebApplicationException(Response.status(
				  ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
	  }
	  return Response.ok().build();
  }
}
