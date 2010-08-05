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

import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.AssociationQuery;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.service.RegistryService;
import gov.nasa.pds.registry.util.Examples;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class AssociationResource {

	@Context
	UriInfo uriInfo;

	@Context
	Request request;

	@Context
	RegistryService registryService;

	public AssociationResource(UriInfo uriInfo, Request request,
			RegistryService registryService) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.registryService = registryService;
	}

	/**
	 * Retrieves all associations managed by the registry given a set of filters. 
	 * 
	 * @return all matching associations in the registry
	 */
	@GET
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAssociations(
			@QueryParam("start") @DefaultValue("1") Integer start,
			@QueryParam("rows") @DefaultValue("20") Integer rows,
			@QueryParam("targetLid") String targetLid,
			@QueryParam("targetVersion") String targetVersion,
			@QueryParam("targetHome") String targetHome,
			@QueryParam("sourceLid") String sourceLid,
			@QueryParam("sourceVersion") String sourceVersion,
			@QueryParam("sourceHome") String sourceHome,
			@QueryParam("associationType") String associationType,
			@QueryParam("queryOp") @DefaultValue("AND") QueryOperator operator,
			@QueryParam("sort") List<String> sort) {
		AssociationFilter filter = new AssociationFilter.Builder().targetLid(
				targetLid).targetVersion(targetVersion).targetHome(targetHome)
				.sourceLid(sourceLid).sourceVersion(sourceVersion).sourceHome(
						sourceHome).associationType(associationType)
				.associationType(associationType).build();
		AssociationQuery.Builder queryBuilder = new AssociationQuery.Builder().filter(filter).operator(operator);
		if (sort != null) {
			queryBuilder.sort(sort);
		}
		
		PagedResponse pr = registryService.getAssociations(queryBuilder.build(),
				start, rows);
		Response.ResponseBuilder builder = Response.ok(pr);
		return builder.build();
	}
	
	@GET
	@Path("{lid}/{userVersion}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAssociations(
			@QueryParam("start") @DefaultValue("1") Integer start,
			@QueryParam("rows") @DefaultValue("20") Integer rows,
			@PathParam("lid") String lid,
			@PathParam("userVersion") String userVersion) {
		PagedResponse pr = registryService.getAssociations(lid, userVersion, start, rows);
		Response.ResponseBuilder builder = Response.ok(pr);
		return builder.build();
	}

	/**
	 * Publishes an association to the registry. Publishing includes validation,
	 * assigning an internal version, validating the submission, and
	 * notification.
	 * 
	 * @request.representation.qname {http://registry.pds.nasa.gov}association
	 * @request.representation.mediaType application/xml
	 * @request.representation.example {@link Examples#REQUEST_ASSOCIATION}
	 * @response.param {@name Location} {@style header} {@type
	 *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
	 *                 where the created item is accessible.}
	 * 
	 * @param product
	 *            to update to
	 * @return returns an HTTP response that indicates an error or the location
	 *         of the created product
	 */
	@POST
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response publishAssociation(Association association) {
		// TODO: Change to add user
		Association created = registryService.publishAssociation("Unkown",
				association);
		return Response.created(
				AssociationResource.getAssociationUri(created, uriInfo))
				.build();
	}

	protected static URI getAssociationUri(Association association,
			UriInfo uriInfo) {
		return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
				.path(RegistryResource.class, "getAssociationResource").path(
						association.getGuid()).build();
	}

	/**
	 * Retrieves an association with the given global identifier.
	 * 
	 * @retun the association
	 */
	@GET
	@Path("{guid}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Association getAssociation(@PathParam("guid") String guid) {
		Association association = (Association) registryService
				.getAssocation(guid);
		return association;
	}
}
