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

import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.ObjectAction;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.service.RegistryService;

import java.net.URI;

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
 * This class delegates all functions involving a particular extrinsic. This is
 * defined as a sub-resource to the registry resource merely to partition off
 * the operations involving extrinsics.
 * 
 * @author pramirez
 * 
 */
public class ExtrinsicResource {
  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  String lid;

  @Context
  String versionId;

  @Context
  RegistryService registryService;

  public ExtrinsicResource(UriInfo uriInfo, Request request,
      RegistryService registryService, String lid, String versionId) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.versionId = versionId;
    this.lid = lid;
    this.registryService = registryService;
  }

  /**
   * Retrieves a single extrinsic from the registry. The local identifier with the
   * version uniquely identifies one extrinsic.
   * 
   * @response.representation.200.qname {http://registry.pds.nasa.gov}extrinsicObject
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link gov.nasa.pds.registry.util.Examples#RESPONSE_EXTRINSIC}
   * 
   * @return ExtrinsicObject within the registry with the lid and version
   */
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getExtrinsic() {
    ExtrinsicObject extrinsic = (ExtrinsicObject) registryService.getObject(lid, versionId, ExtrinsicObject.class);
    Response.ResponseBuilder builder = Response.ok(extrinsic);
    ExtrinsicResource.addPreviousExtrinsicLink(builder, uriInfo, registryService,
        extrinsic);
    ExtrinsicResource.addNextExtrinsicLink(builder, uriInfo, registryService,
        extrinsic);
    ExtrinsicResource.addEarliestExtrinsicLink(builder, uriInfo, registryService,
        extrinsic);
    ExtrinsicResource.addLatestExtrinsicLink(builder, uriInfo, registryService,
        extrinsic);
    // TODO: Should be adding an approve and deprecate link based upon
    // if the user has permission
    if (ObjectStatus.Submitted.equals(extrinsic.getStatus())) {
      ExtrinsicResource.addApproveExtrinsicLink(builder, uriInfo, extrinsic);
    } else if (ObjectStatus.Approved.equals(extrinsic.getStatus())) {
      ExtrinsicResource.addDeprecateExtrinsicLink(builder, uriInfo, extrinsic);
    }
    return builder.build();
  }

  /**
   * Updates an existing extrinsic with the given local identifier and version.
   * 
   * @param extrinsic
   *          to update to
   * @return returns an HTTP response that indicates an error or ok
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response update(ExtrinsicObject extrinsic) {
    // TODO handle error condition mapping
    registryService.updateObject("Unknown", extrinsic);
    return Response.ok().build();
  }

  /**
   * Removes an extrinsic from the registry
   */
  @DELETE
  public Response delete() {
    // TODO figure out what to do if there was a problem deleting the
    // extrinsic
    registryService.deleteObject("Unknown", lid, versionId,
        ExtrinsicObject.class);
    return Response.ok().build();
  }

  /**
   * This will change the status of the registered extrinsic
   * 
   * @param action
   *          to take on extrinsic which will result in an update of status
   *          {@link ObjectAction}
   * @return the updated extrinsic
   */
  @POST
  @Path("{status}")
  public Response changeStatus(@PathParam("status") ObjectAction action) {
    registryService.changeObjectStatus("Unknown", lid, versionId, action, ExtrinsicObject.class);
    return Response.ok().build();
  }

  protected static void addPreviousExtrinsicLink(
      Response.ResponseBuilder builder, UriInfo uriInfo,
      RegistryService registryService, ExtrinsicObject extrinsic) {
    ExtrinsicObject previous = (ExtrinsicObject) registryService.getPreviousObject(extrinsic.getLid(),
        extrinsic.getVersionId(), ExtrinsicObject.class);
    if (previous != null) {
      String previousUri = uriInfo.getBaseUriBuilder().clone().path(
          RegistryResource.class).path(RegistryResource.class,
          "getExtrinsicsResource").path(previous.getLid()).path(
          previous.getVersionId()).build().toString();
      builder.header("Link", new Link(previousUri, "previous", null));
    }
  }

  protected static void addNextExtrinsicLink(Response.ResponseBuilder builder,
      UriInfo uriInfo, RegistryService registryService, ExtrinsicObject extrinsic) {
    ExtrinsicObject next = (ExtrinsicObject) registryService.getNextObject(extrinsic.getLid(), extrinsic
        .getVersionId(), ExtrinsicObject.class);
    if (next != null) {
      String nextUri = uriInfo.getBaseUriBuilder().clone().path(
          RegistryResource.class).path(RegistryResource.class,
          "getExtrinsicsResource").path(next.getLid()).path(next.getVersionId())
          .build().toString();
      builder.header("Link", new Link(nextUri, "next", null));
    }
  }

  protected static void addEarliestExtrinsicLink(
      Response.ResponseBuilder builder, UriInfo uriInfo,
      RegistryService registryService, ExtrinsicObject extrinsic) {
    ExtrinsicObject earliest = (ExtrinsicObject) registryService.getEarliestObject(extrinsic.getLid(), ExtrinsicObject.class);
    if (earliest != null) {
      String earliestUri = uriInfo.getBaseUriBuilder().clone().path(
          RegistryResource.class).path(RegistryResource.class,
          "getExtrinsicsResource").path(earliest.getLid()).path(
          earliest.getVersionId()).path("earliest").build().toString();
      builder.header("Link", new Link(earliestUri, "earliest", null));
    }
  }

  protected static void addLatestExtrinsicLink(Response.ResponseBuilder builder,
      UriInfo uriInfo, RegistryService registryService, ExtrinsicObject extrinsic) {
    ExtrinsicObject latest = (ExtrinsicObject) registryService.getLatestObject(extrinsic.getLid(), ExtrinsicObject.class);
    if (latest != null) {
      String latestUri = uriInfo.getBaseUriBuilder().clone().path(
          RegistryResource.class).path(RegistryResource.class,
          "getExtrinsicsResource").path(latest.getLid()).path(
          latest.getVersionId()).build().toString();
      builder.header("Link", new Link(latestUri, "latest", null));
    }
  }

  protected static void addDeprecateExtrinsicLink(
      Response.ResponseBuilder builder, UriInfo uriInfo, ExtrinsicObject extrinsic) {
    String deprecateUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getExtrinsicsResource").path(extrinsic.getLid()).path(
        extrinsic.getVersionId()).path("deprecate").build().toString();
    builder.header("Link", new Link(deprecateUri, "deprecate", null));
  }

  protected static void addApproveExtrinsicLink(Response.ResponseBuilder builder,
      UriInfo uriInfo, ExtrinsicObject extrinsic) {
    String approveUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getExtrinsicsResource").path(extrinsic.getLid()).path(
        extrinsic.getVersionId()).path("approve").build().toString();
    builder.header("Link", new Link(approveUri, "approve", null));
  }

  protected static URI getExtrinsicUri(ExtrinsicObject extrinsic, UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getExtrinsicsResource").path(
            extrinsic.getLid()).path(extrinsic.getVersionId()).build();
  }
}
