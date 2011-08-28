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

import java.util.Arrays;
import java.util.List;

import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.EventType;
import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.query.EventFilter;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.service.RegistryService;
import gov.nasa.pds.registry.util.DateParam;

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
 * This resource is responsible for managing Auditable Events with the 
 * registry service.
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
   * Retrieves all auditable events for a given registry object.
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

  /**
   * Retrieves all auditable events managed by the registry given a set of 
   * filters.
   */
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getEvents(
      @QueryParam("start") @DefaultValue("1") Integer start,
      @QueryParam("rows") @DefaultValue("20") Integer rows,
      @QueryParam("eventStart") DateParam eventStart,
      @QueryParam("eventEnd") DateParam eventEnd,
      @QueryParam("eventType") EventType eventType,
      @QueryParam("requestId") String requestId,
      @QueryParam("user") String user,
      @QueryParam("queryOp") @DefaultValue("AND") QueryOperator operator,
      @QueryParam("sort") List<String> sort) {
    EventFilter.Builder filterBuilder = new EventFilter.Builder().eventType(
        eventType).requestId(requestId).user(user);
    if (eventStart != null) {
      filterBuilder.eventStart(eventStart.getDate());
    }
    if (eventEnd != null) {
      filterBuilder.eventEnd(eventEnd.getDate());
    }
    if (sort.size() == 0) {
      sort = Arrays.asList("timestamp DESC");
    }
    RegistryQuery.Builder<EventFilter> queryBuilder = new RegistryQuery.Builder<EventFilter>()
        .filter(filterBuilder.build()).sort(sort).operator(operator);
    PagedResponse<AuditableEvent> rr = registryService.getAuditableEvents(
        queryBuilder.build(), start, rows);
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
