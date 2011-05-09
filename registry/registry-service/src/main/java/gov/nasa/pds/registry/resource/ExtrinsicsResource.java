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

import java.util.List;

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.EventType;
import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.RegistryResponse;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.ExtrinsicQuery;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.service.RegistryService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * This resource is responsible for managing collections of Products.
 * 
 * @author pramirez
 * 
 */
@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ExtrinsicsResource {
  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public ExtrinsicsResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Allows access to all the extrinsics managed by this repository. This list
   * of extrinsics is based on the latest received extrinsic's logical
   * identifier (lid). The header will contain pointers to next and previous
   * when applicable.
   * 
   * @response.representation.200.qname {http://registry.pds.nasa.gov}response
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#RESPONSE_PAGED}
   * @response.param {@name Link} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI to
   *                 the next and previous pages.}
   * 
   * @param start
   *          the index at which to start the result list from
   * @param rows
   *          how many results to return
   * @param guid
   *          filter to apply on the global unique id, supports wildcard (*)
   * @param name
   *          filter to apply to name, support wildcard (*)
   * @param lid
   *          filter to apply to logical id, supports wildcard (*)
   * @param versionName
   *          filter to apply to registry object version, supports wildcard (*)
   * @param objectType
   *          filter to apply on the user defined registry object types,supports
   *          wildcard (*)
   * @param submitter
   *          CURRENTLY UNSUPPORTED
   * @param status
   *          filter to apply on the object status, maps to {@link ObjectStatus}
   *          enum
   * @param eventType
   *          CURRENTLY UNSUPPORTED
   * @param operator
   *          to apply to filters, valid values are AND or OR. Defaults to AND.
   * @param sort
   *          defines what parameters to sort on. The format is
   *          "parameter order" the order is optional. The default is "guid ASC"
   *          and if unspecified the ordering is ASC.
   * @return subset of managed products
   */
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getExtrinsics(
      @QueryParam("start") @DefaultValue("1") Integer start,
      @QueryParam("rows") @DefaultValue("20") Integer rows,
      @QueryParam("guid") String guid, @QueryParam("name") String name,
      @QueryParam("lid") String lid,
      @QueryParam("versionName") String versionName,
      @QueryParam("objectType") String objectType,
      @QueryParam("submitter") String submitter,
      @QueryParam("contentVersion") String contentVersion,
      @QueryParam("mimeType") String mimeType,
      @QueryParam("status") ObjectStatus status,
      @QueryParam("eventType") EventType eventType,
      @QueryParam("queryOp") @DefaultValue("AND") QueryOperator operator,
      @QueryParam("sort") List<String> sort) {
    ExtrinsicFilter filter = new ExtrinsicFilter.Builder().guid(guid).name(name).lid(
        lid).versionName(versionName).objectType(objectType).submitter(
submitter).status(status).contentVersion(contentVersion)
        .mimeType(mimeType).eventType(eventType).build();
    ExtrinsicQuery.Builder queryBuilder = new ExtrinsicQuery.Builder().filter(
        filter).operator(operator);
    if (sort != null) {
      queryBuilder.sort(sort);
    }
    RegistryResponse<ExtrinsicObject> rr = registryService.getExtrinsics(
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

  /**
   * Publishes a extrinsic object to the registry. Publishing includes
   * validation, assigning an internal version, validating the submission, and
   * notification. The submitted extrinsic object should not contain the same
   * logical identifier as previously submitted extrinsic (412 Precondition
   * Failed), in that scenario the version interface should be used.
   * 
   * @request.representation.qname {http://registry.pds.nasa.gov}extrinsicObject
   * @request.representation.mediaType application/xml
   * @request.representation.example {@link gov.nasa.pds.registry.util.Examples#REQUEST_EXTRINSIC}
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * 
   * @param extrinsic
   *          to update to
   * @return returns an HTTP response that indicates an error or the location of
   *         the created product and its guid
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishExtrinsic(ExtrinsicObject extrinsic) {
    // TODO: Change to set user
    try {
      String guid = registryService.publishObject("Unknown", extrinsic);
      return Response.created(
          ExtrinsicResource.getExtrinsicUri(registryService.getExtrinsic(guid),
              uriInfo)).entity(guid).build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Creates a new version of a product in the registry. Follows the same
   * procedures as publishing with the caveat that the logical identifier this
   * product carries should already exist in the registry (412 Precondition
   * Failed).
   * 
   * @request.representation.qname {http://registry.pds.nasa.gov}extrinsic
   * @request.representation.mediaType application/xml
   * @request.representation.example {@link gov.nasa.pds.registry.util.Examples#REQUEST_EXTRINSIC_VERSIONED}
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * @param extrinsic
   *          to update to
   * @param lid
   *          the logical identifier to the extrinsic
   * @param major
   *          if true indicates a major revision otherwise considered minor
   * @return returns an HTTP response that indicates an error or the location of
   *         the versioned extrinsic and its guid
   */
  @POST
  @Path("logicals/{lid}")
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response versionExtrinsic(ExtrinsicObject extrinsic,
      @PathParam("lid") String lid,
      @QueryParam("major") @DefaultValue("true") boolean major) {
    // TODO Check to make sure the path lid matches that of the product
    // provided
    // TODO Change to set user
    try {
      String guid = registryService.versionObject("Unknown", extrinsic, major);
      return Response.created(
          ExtrinsicResource.getExtrinsicUri(registryService.getExtrinsic(guid),
              uriInfo)).entity(guid).build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Retrieves the collection of extrinsics that share the same local
   * identifier. This method supports finding all versions of extrinsic.
   * 
   * @response.representation.200.qname {http://registry.pds.nasa.gov}response
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#RESPONSE_EXTRINSIC_VERSIONS}
   * 
   * @param lid
   *          local identifier of set extrinsics to retrieve
   * @return collection of extrinsics
   */
  @SuppressWarnings("unchecked")
  @GET
  @Path("{lid}/all")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public RegistryResponse<ExtrinsicObject> getExtrinsicVersions(
      @PathParam("lid") String lid) {
    return new RegistryResponse(registryService.getObjectVersions(lid,
        ExtrinsicObject.class));
  }

  /**
   * Retrieves the earliest product from the registry. The local identifier
   * points to a collection of versions of the same product.
   * 
   * @response.representation.200.qname 
   *                                    {http://registry.pds.nasa.gov}extrinsicObject
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#RESPONSE_EXTRINSIC}
   * 
   * @param lid
   *          local identifier of product to retrieve
   * 
   * @return ExtrinsicObject within the registry with the lid and version
   */
  @GET
  @Path("{lid}/earliest")
  public Response getEarliestVersion(@PathParam("lid") String lid) {
    ExtrinsicObject extrinsic = (ExtrinsicObject) registryService
        .getEarliestObject(lid, ExtrinsicObject.class);
    Response.ResponseBuilder builder = Response.ok(extrinsic);
    ExtrinsicResource.addNextExtrinsicLink(builder, uriInfo, registryService,
        extrinsic);
    ExtrinsicResource.addLatestExtrinsicLink(builder, uriInfo, registryService,
        extrinsic);
    return builder.build();
  }

  /**
   * Retrieves the latest extrinsic from the registry. The local identifier
   * points to a collection of versions of the same extrinsic.
   * 
   * @response.representation.200.qname 
   *                                    {http://registry.pds.nasa.gov}extrinsicObject
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#RESPONSE_EXTRINSIC}
   * 
   * @param lid
   *          the logical identifier to the extrinsic
   * 
   * @return Extrinsic within the registry with the lid and version
   */
  @GET
  @Path("logicals/{lid}")
  public Response getLatestVersion(@PathParam("lid") String lid) {
    ExtrinsicObject extrinsic = (ExtrinsicObject) registryService
        .getLatestObject(lid, ExtrinsicObject.class);
    Response.ResponseBuilder builder = Response.ok(extrinsic);
    ExtrinsicResource.addPreviousExtrinsicLink(builder, uriInfo,
        registryService, extrinsic);
    ExtrinsicResource.addEarliestExtrinsicLink(builder, uriInfo,
        registryService, extrinsic);
    return builder.build();
  }

  /**
   * @param versionName
   *          of the extrinsic object's local identifier
   * @param lid
   *          local identifier which identifies a unique set of products
   * @return the resource that manages an Product in the registry
   */
  @Path("{guid}")
  public ExtrinsicResource getExtrinsicResource(@PathParam("guid") String guid) {
    return new ExtrinsicResource(uriInfo, request, registryService, guid);
  }

}
