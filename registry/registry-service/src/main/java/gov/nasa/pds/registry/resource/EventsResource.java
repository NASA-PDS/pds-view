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

import java.util.Arrays;

import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.query.ObjectFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.service.RegistryService;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
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
  public PagedResponse<AuditableEvent> getAuditableEvents(
      @PathParam("affectedObject") String affectedObject) {
    return registryService.getAuditableEvents(affectedObject);
  }
  
  @SuppressWarnings("unchecked")
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getEvents(
      @QueryParam("start") @DefaultValue("1") Integer start,
      @QueryParam("rows") @DefaultValue("20") Integer rows) {
    ObjectFilter filter = new ObjectFilter.Builder().build();
    RegistryQuery.Builder<ObjectFilter> queryBuilder = new RegistryQuery.Builder<ObjectFilter>()
        .filter(filter).sort(Arrays.asList("timestamp DESC"));
    PagedResponse<AuditableEvent> rr = (PagedResponse<AuditableEvent>) registryService
        .getObjects(queryBuilder.build(), start, rows, AuditableEvent.class);
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
}
