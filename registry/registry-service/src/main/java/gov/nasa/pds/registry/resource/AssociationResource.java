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
import gov.nasa.pds.registry.service.RegistryService;
import gov.nasa.pds.registry.util.Examples;

import java.net.URI;

import javax.ws.rs.Consumes;
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
	 * Retrieves all associations managed by the registry. This needs to be
	 * switched over to a paged response as it is likely to grow to a large set
	 * of associations.
	 * 
	 * @return all associations in the registry
	 */
	@GET
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public PagedResponse getAssociations() {
		return registryService.getAssociations();
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

	/**
	 * Retrieves all associations where the identified artifact is the source of
	 * the relationship.
	 * 
	 * @param lid
	 *            local identifier of the source artifact
	 * @param version
	 *            of the given local identifier
	 * @return all source associations managed by the registry for the given
	 *         artifact
	 */
	@GET
	@Path("source/{lid}/{version}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public PagedResponse getSourceAssociations(@PathParam("lid") String lid,
			@PathParam("version") String version) {
		return registryService.getSourceAssociations(lid, version);
	}

	/**
	 * Retrieves all named associations where the identified artifact is the
	 * source of the relationship.
	 * 
	 * @param lid
	 *            local identifier of the source artifact
	 * @param version
	 *            of the given local identifier
	 * @param associationType
	 *            that exists between the source and target
	 * @return all named source associations managed by the registry for the
	 *         given artifact
	 */
	@GET
	@Path("source/{lid}/{version}/{assocationType}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public PagedResponse getSourceNamedAssociations(
			@PathParam("lid") String lid, @PathParam("version") String version,
			@PathParam("associationType") String associationType) {
		return registryService.getSourceNamedAssociations(lid, version,
				associationType);
	}

	/**
	 * Retrieves all associations where the identified artifact is the target of
	 * the relationship.
	 * 
	 * @param lid
	 *            local identifier of the source artifact
	 * @param version
	 *            of the given local identifier
	 * @return all target associations managed by the registry for the given
	 *         artifact
	 */
	@GET
	@Path("target/{lid}/{version}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public PagedResponse getTargetAssociations(@PathParam("lid") String lid,
			@PathParam("version") String version) {
		return registryService.getTargetAssociations(lid, version);
	}

	/**
	 * Retrieves all named associations where the identified artifact is the
	 * target of the relationship.
	 * 
	 * @param lid
	 *            local identifier of the source artifact
	 * @param version
	 *            of the given local identifier
	 * @param associationType
	 *            that exists between the source and target
	 * @return all named target associations managed by the registry for the
	 *         given artifact
	 */
	@GET
	@Path("target/{lid}/{version}/{associationType}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public PagedResponse getTargetNamedAssociations(
			@PathParam("lid") String lid, @PathParam("version") String version,
			@PathParam("associationType") String associationType) {
		return registryService.getTargetNamedAssociations(lid, version,
				associationType);
	}

	/**
	 * Retrieves all associations where the identified artifact is part of
	 * irregardless if it is the source or target.
	 * 
	 * @param lid
	 *            local identifier of the source artifact
	 * @param version
	 *            of the given local identifier
	 * @return all associations managed by the registry for the given artifact
	 */
	@GET
	@Path("all/{lid}/{version}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public PagedResponse getAllAssociations(@PathParam("lid") String lid,
			@PathParam("version") String version) {
		return registryService.getAssociations(lid, version);
	}

	/**
	 * Retrieves all named associations where the identified artifact is part of
	 * irregardless if it is the source or target.
	 * 
	 * @param lid
	 *            local identifier of the source artifact
	 * @param version
	 *            of the given local identifier
	 * @param associationType
	 *            that exists between the source and target
	 * @return all named associations managed by the registry for the given
	 *         artifact
	 */
	@GET
	@Path("all/{lid}/{version}/{associationType}")
	@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public PagedResponse getAllNamedAssociations(@PathParam("lid") String lid,
			@PathParam("version") String version,
			@PathParam("associationType") String associationType) {
		return registryService.getNamedAssociations(lid, version,
				associationType);
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
		System.out.println("guid: " + association.getGuid());
		System.out.println("sourceLid: " + association.getSourceLid());
		System.out.println("sourceVersion: " + association.getSourceVersion());
		System.out.println("targetLid: " + association.getTargetLid());
		System.out.println("targetVersion: " + association.getTargetVersion());
		System.out.println("associationType: "
				+ association.getAssociationType());
		System.out.println("Class: " + association.getClass().getSimpleName());
		return association;
	}
}
