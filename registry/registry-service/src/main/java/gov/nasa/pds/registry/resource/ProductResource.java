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
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.service.RegistryService;
import gov.nasa.pds.registry.util.Examples;

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
 * This class delegates all functions involving a particular product. This is
 * defined as a sub-resource to the registry resource merely to partition off
 * the operations involving products.
 * 
 * @author pramirez
 * 
 */
public class ProductResource {
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

  public ProductResource(UriInfo uriInfo, Request request,
      RegistryService registryService, String lid, String versionId) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.versionId = versionId;
    this.lid = lid;
    this.registryService = registryService;
  }

  /**
   * Retrieves a single product from the registry. The local identifier with the
   * version uniquely identifies one product.
   * 
   * @response.representation.200.qname {http://registry.pds.nasa.gov}product
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link Examples#RESPONSE_PRODUCT}
   * 
   * @return Product within the registry with the lid and version
   */
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getProduct() {
    Product product = registryService.getProduct(lid, versionId);
    Response.ResponseBuilder builder = Response.ok(product);
    ProductResource.addPreviousProductLink(builder, uriInfo, registryService,
        product);
    ProductResource.addNextProductLink(builder, uriInfo, registryService,
        product);
    ProductResource.addEarliestProductLink(builder, uriInfo, registryService,
        product);
    ProductResource.addLatestProductLink(builder, uriInfo, registryService,
        product);
    // TODO: Should be adding an approve and deprecate link based upon
    // if the user has permission
    if (ObjectStatus.Submitted.equals(product.getStatus())) {
      ProductResource.addApproveProductLink(builder, uriInfo, product);
    } else if (ObjectStatus.Approved.equals(product.getStatus())) {
      ProductResource.addDeprecateProductLink(builder, uriInfo, product);
    }
    return builder.build();
  }

  /**
   * Updates an existing product with the given local identifier and version.
   * 
   * @param product
   *          to update to
   * @return returns an HTTP response that indicates an error or ok
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response updateProduct(Product product) {
    // TODO handle error condition mapping
    registryService.updateRegistryObject("Unknown", product);
    return Response.ok().build();
  }

  /**
   * Removes an product from the registry
   */
  @DELETE
  public Response deleteProduct() {
    // TODO figure out what to do if there was a problem deleting the
    // product
    registryService.deleteRegistryObject("Unknown", lid, versionId,
        Product.class);
    return Response.ok().build();
  }

  /**
   * This will change the status of the registered product
   * 
   * @param action
   *          to take on product which will result in an update of status
   *          {@link ObjectAction}
   * @return the updated product
   */
  @POST
  @Path("{status}")
  public Response changeStatus(@PathParam("status") ObjectAction action) {
    registryService.changeStatus("Unknown", lid, versionId, action);
    return Response.ok().build();
  }

  protected static void addPreviousProductLink(
      Response.ResponseBuilder builder, UriInfo uriInfo,
      RegistryService registryService, Product product) {
    Product previous = registryService.getPreviousProduct(product.getLid(),
        product.getVersionId());
    if (previous != null) {
      String previousUri = uriInfo.getBaseUriBuilder().clone().path(
          RegistryResource.class).path(RegistryResource.class,
          "getProductsResource").path(previous.getLid()).path(
          previous.getVersionId()).build().toString();
      builder.header("Link", new Link(previousUri, "previous", null));
    }
  }

  protected static void addNextProductLink(Response.ResponseBuilder builder,
      UriInfo uriInfo, RegistryService registryService, Product product) {
    Product next = registryService.getNextProduct(product.getLid(), product
        .getVersionId());
    if (next != null) {
      String nextUri = uriInfo.getBaseUriBuilder().clone().path(
          RegistryResource.class).path(RegistryResource.class,
          "getProductsResource").path(next.getLid()).path(next.getVersionId())
          .build().toString();
      builder.header("Link", new Link(nextUri, "next", null));
    }
  }

  protected static void addEarliestProductLink(
      Response.ResponseBuilder builder, UriInfo uriInfo,
      RegistryService registryService, Product product) {
    Product earliest = registryService.getEarliestProduct(product.getLid());
    if (earliest != null) {
      String earliestUri = uriInfo.getBaseUriBuilder().clone().path(
          RegistryResource.class).path(RegistryResource.class,
          "getProductsResource").path(earliest.getLid()).path(
          earliest.getVersionId()).path("earliest").build().toString();
      builder.header("Link", new Link(earliestUri, "earliest", null));
    }
  }

  protected static void addLatestProductLink(Response.ResponseBuilder builder,
      UriInfo uriInfo, RegistryService registryService, Product product) {
    Product latest = registryService.getLatestProduct(product.getLid());
    if (latest != null) {
      String latestUri = uriInfo.getBaseUriBuilder().clone().path(
          RegistryResource.class).path(RegistryResource.class,
          "getProductsResource").path(latest.getLid()).path(
          latest.getVersionId()).build().toString();
      builder.header("Link", new Link(latestUri, "latest", null));
    }
  }

  protected static void addDeprecateProductLink(
      Response.ResponseBuilder builder, UriInfo uriInfo, Product product) {
    String deprecateUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getProductsResource").path(product.getLid()).path(
        product.getVersionId()).path("deprecate").build().toString();
    builder.header("Link", new Link(deprecateUri, "deprecate", null));
  }

  protected static void addApproveProductLink(Response.ResponseBuilder builder,
      UriInfo uriInfo, Product product) {
    String approveUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).path(RegistryResource.class,
        "getProductsResource").path(product.getLid()).path(
        product.getVersionId()).path("approve").build().toString();
    builder.header("Link", new Link(approveUri, "approve", null));
  }

  protected static URI getProductUri(Product product, UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getProductsResource").path(
            product.getLid()).path(product.getVersionId()).build();
  }
}
