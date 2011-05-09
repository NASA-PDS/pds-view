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

import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.RegistryResponse;
import gov.nasa.pds.registry.service.RegistryService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

/**
 * This is the resource responsible for managing Auditable Events.
 * 
 * @author pramirez
 * 
 */
public class EventsResource {

  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public EventsResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Retrieve all AuditableEvents found for a given registry object.
   * 
   * @response.representation.200.qname {http://registry.pds.nasa.gov}response
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#RESPONSE_AUDITABLE_EVENTS}
   * 
   * @param affectedObject
   *          guid for object
   * @return all events for the guid
   */
  @Path("{affectedObject}")
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public RegistryResponse<AuditableEvent> getAuditableEvents(
      @PathParam("affectedObject") String affectedObject) {
    return registryService.getAuditableEvents(affectedObject);
  }
}
