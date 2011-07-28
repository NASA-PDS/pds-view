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

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.service.RegistryService;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.UriInfo;

/**
 * This is the resource responsible for managing Associations
 * 
 * @author pramirez
 * 
 */
public class AssociationsResource {

  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public AssociationsResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Retrieves all associations managed by the registry given a set of filters.
   * 
   * @response.representation.200.qname {http://registry.pds.nasa.gov}response
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#RESPONSE_ASSOCIATION_QUERY}
   * 
   * @param start
   *          the index at which to start the result list from
   * @param rows
   *          how many results to return
   * @param targetObject
   *          filter on the identifier of the target in the association supports
   *          wildcard (*)
   * @param sourceObject
   *          filter on the identifier of the source in the association supports
   *          wildcard (*)
   * @param associationType
   *          filter on the type of association supports wildcard (*)
   * @param operator
   *          to apply to filters, valid values are AND or OR. Defaults to AND.
   * @param sort
   *          defines what parameters to sort on. The format is
   *          "parameter order" the order is optional. The default is "guid ASC"
   *          and if unspecified the ordering is ASC.
   * @return all matching associations in the registry
   */
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getAssociations(
      @QueryParam("start") @DefaultValue("1") Integer start,
      @QueryParam("rows") @DefaultValue("20") Integer rows,
      @QueryParam("targetObject") String targetObject,
      @QueryParam("sourceObject") String sourceObject,
      @QueryParam("associationType") String associationType,
      @QueryParam("queryOp") @DefaultValue("AND") QueryOperator operator,
      @QueryParam("sort") List<String> sort) {
    AssociationFilter filter = new AssociationFilter.Builder().targetObject(
        targetObject).sourceObject(sourceObject).associationType(
        associationType).build();
    RegistryQuery.Builder<AssociationFilter> queryBuilder = new RegistryQuery.Builder<AssociationFilter>()
        .filter(filter).operator(operator);
    if (sort != null) {
      queryBuilder.sort(sort);
    }

    PagedResponse<Association> pr = registryService.getAssociations(
        queryBuilder.build(), start, rows);
    Response.ResponseBuilder builder = Response.ok(pr);
    return builder.build();
  }

  /**
   * Publishes an association to the registry. Publishing includes validation,
   * assigning an internal version, validating the submission, and notification.
   * 
   * @request.representation.qname {http://registry.pds.nasa.gov}association
   * @request.representation.mediaType application/xml
   * @request.representation.example {@link gov.nasa.pds.registry.util.Examples#REQUEST_ASSOCIATION}
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * 
   * @param association
   *          to publish to registry
   * @param packageGuid
   *          optional package guid which this registry object is a member of
   * @return returns an HTTP response that indicates an error or the location of
   *         the created association and its guid
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishAssociation(Association association,
      @QueryParam("packageGuid") String packageGuid) {
    // TODO: Change to add user
    try {
      String guid = (packageGuid == null) ? registryService.publishObject(
          "Unkown", association) : registryService.publishObject("Unkown",
          association, packageGuid);
      return Response.created(
          AssociationsResource.getAssociationUri(registryService
              .getAssocation(guid), uriInfo)).entity(guid).build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  protected static URI getAssociationUri(Association association,
      UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getAssociationsResource").path(
            association.getGuid()).build();
  }

  /**
   * Retrieves an association with the given global identifier.
   * 
   * @response.representation.200.qname 
   *                                    {http://registry.pds.nasa.gov}association
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#RESPONSE_ASSOCIATION}
   * @return the association
   */
  @GET
  @Path("{guid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Association getAssociation(@PathParam("guid") String guid) {
    try {
      Association association = registryService.getAssocation(guid);
      return association;
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Deletes the association with the given guid
   * 
   * @param guid
   *          of association
   * @return Response indicating whether the operation succeeded or had an error
   */
  @DELETE
  @Path("{guid}")
  public Response deleteAssociation(@PathParam("guid") String guid) {
    registryService.deleteObject("Unknown", guid, Association.class);
    return Response.ok().build();
  }

}
