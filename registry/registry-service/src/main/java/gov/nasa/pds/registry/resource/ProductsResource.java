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

import gov.nasa.pds.registry.model.EventType;
import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.Product;
import gov.nasa.pds.registry.model.Products;
import gov.nasa.pds.registry.query.ObjectFilter;
import gov.nasa.pds.registry.query.ProductQuery;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.service.RegistryService;
import gov.nasa.pds.registry.util.Examples;

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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ProductsResource {
  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  RegistryService registryService;

  public ProductsResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * 
   * Allows access to all the products managed by this repository. This list of
   * products is based on the latest received product's logical identifier
   * (lid). The header will contain pointers to next and previous when
   * applicable.
   * 
   * @response.representation.200.qname 
   *                                    {http://registry.pds.nasa.gov}pagedResponse
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link Examples#RESPONSE_PAGED}
   * @response.param {@name Link} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI to
   *                 the next and previous pages.}
   *                 
   * @param start the index at which to start the result list from
   * @param rows how many results to return
   * @param guid filter to apply on the global unique id, supports wildcard (*)
   * @param name filter to apply to name, support wildcard (*)
   * @param lid filter to apply to logical id, supports wildcard (*)
   * @param versionName filter to apply to registry object version, supports wildcard (*)
   * @param versionId filter to apply on the user version, supports wildcard (*)
   * @param objectType filter to apply on the user defined registry object types,supports
   *          wildcard (*)
   * @param submitter CURRENTLY UNSUPPORTED
   * @param status filter to apply on the object status, maps to {@link ObjectStatus}
   *          enum 
   * @param eventType CURRENTLY UNSUPPORTED
   * @param operator to apply to filters, valid values are AND or OR. Defaults
   *          to AND.
   * @param sort defines what parameters to sort on. The format is
   *          "parameter order" the order is optional. The default is "guid ASC"
   *          and if unspecified the ordering is ASC.
   * @return subset of managed products
   */
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getProducts(
      @QueryParam("start") @DefaultValue("1") Integer start,
      @QueryParam("rows") @DefaultValue("20") Integer rows,
      @QueryParam("guid") String guid, @QueryParam("name") String name,
      @QueryParam("lid") String lid, @QueryParam("versionName") String versionName,
      @QueryParam("versionId") String versionId,
      @QueryParam("objectType") String objectType,
      @QueryParam("submitter") String submitter,
      @QueryParam("status") ObjectStatus status,
      @QueryParam("eventType") EventType eventType,
      @QueryParam("queryOp") @DefaultValue("AND") QueryOperator operator,
      @QueryParam("sort") List<String> sort) {
    ObjectFilter filter = new ObjectFilter.Builder().guid(guid).name(name).lid(
        lid).versionName(versionName).versionId(versionId).objectType(objectType)
        .submitter(submitter).status(status).eventType(eventType).build();
    ProductQuery.Builder queryBuilder = new ProductQuery.Builder().filter(
        filter).operator(operator);
    if (sort != null) {
      queryBuilder.sort(sort);
    }
    PagedResponse pr = registryService.getProducts(queryBuilder.build(), start,
        rows);
    Response.ResponseBuilder builder = Response.ok(pr);
    UriBuilder absolute = uriInfo.getAbsolutePathBuilder();
    absolute.queryParam("start", "{start}");
    absolute.queryParam("rows", "{rows}");
    // Add in next link
    if (start - 1 + rows < pr.getNumFound()) {
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
   * Publishes a product to the registry. Publishing includes validation,
   * assigning an internal version, validating the submission, and notification.
   * The submitted product should not contain the same logical identifier as
   * previously submitted product (412 Precondition Failed), in that scenario
   * the version interface should be used.
   * 
   * @request.representation.qname {http://registry.pds.nasa.gov}product
   * @request.representation.mediaType application/xml
   * @request.representation.example {@link Examples#REQUEST_PRODUCT}
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * 
   * @param product
   *          to update to
   * @return returns an HTTP response that indicates an error or the location of
   *         the created product
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishProduct(Product product) {
    // TODO: Change to set user
    Product created = registryService.publishProduct("Unknown", product);
    return Response.created(ProductResource.getProductUri(created, uriInfo))
        .build();
  }

  /**
   * Creates a new version of a product in the registry. Follows the same
   * procedures as publishing with the caveat that the logical identifier this
   * product carries should already exist in the registry (412 Precondition
   * Failed).
   * 
   * @request.representation.qname {http://registry.pds.nasa.gov}product
   * @request.representation.mediaType application/xml
   * @request.representation.example {@link Examples#REQUEST_PRODUCT_VERSIONED}
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * @param product
   *          to update to
   * @param lid
   *          the logical identifier to the product
   * @param major
   *          if true indicates a major revision otherwise considered minor
   * @return returns an HTTP response that indicates an error or the location of
   *         the created product
   */
  @POST
  @Path("{lid}")
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response versionProduct(Product product, @PathParam("lid") String lid,
      @QueryParam("major") @DefaultValue("true") boolean major) {
    // TODO Check to make sure the path lid matches that of the product
    // provided
    // TODO Change to set user
    Product created = registryService.versionProduct("Unknown", lid, product,
        major);
    return Response.created(ProductResource.getProductUri(created, uriInfo))
        .build();
  }

  /**
   * Retrieves the collection of products that share the same local identifier.
   * This method supports finding all versions of an product.
   * 
   * @response.representation.200.qname {http://registry.pds.nasa.gov}products
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link Examples#RESPONSE_PRODUCT_VERSIONS}
   * 
   * @param lid
   *          local identifier of set products to retrieve
   * @return collection of products
   */
  @GET
  @Path("{lid}/all")
  public Products getProductVersions(@PathParam("lid") String lid) {
    return new Products(registryService.getProductVersions(lid));
  }

  /**
   * Retrieves the earliest product from the registry. The local identifier
   * points to a collection of versions of the same product.
   * 
   * @response.representation.200.qname {http://registry.pds.nasa.gov}product
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link Examples#RESPONSE_PRODUCT}
   * 
   * @param lid
   *          local identifier of product to retrieve
   * 
   * @return Product within the registry with the lid and version
   */
  @GET
  @Path("{lid}/earliest")
  public Response getEarliestVersion(@PathParam("lid") String lid) {
    Product product = registryService.getEarliestProduct(lid);
    Response.ResponseBuilder builder = Response.ok(product);
    ProductResource.addNextProductLink(builder, uriInfo, registryService,
        product);
    ProductResource.addLatestProductLink(builder, uriInfo, registryService,
        product);
    return builder.build();
  }

  /**
   * Retrieves the latest product from the registry. The local identifier points
   * to a collection of versions of the same product.
   * 
   * @response.representation.200.qname {http://registry.pds.nasa.gov}product
   * @response.representation.200.mediaType application/xml
   * @response.representation.200.example {@link Examples#RESPONSE_PRODUCT}
   * 
   * @param lid
   *          the logical identifier to the product
   * 
   * @return Product within the registry with the lid and version
   */
  @GET
  @Path("{lid}")
  public Response getLatestVersion(@PathParam("lid") String lid) {
    Product product = registryService.getLatestProduct(lid);
    Response.ResponseBuilder builder = Response.ok(product);
    ProductResource.addPreviousProductLink(builder, uriInfo, registryService,
        product);
    ProductResource.addEarliestProductLink(builder, uriInfo, registryService,
        product);
    return builder.build();
  }

  /**
   * @param versionId
   *          of the product's local identifier
   * @param lid
   *          local identifier which identifies a unique set of products
   * @return the resource that manages an Product in the registry
   */
  @Path("{lid}/{versionId}")
  public ProductResource getProductResource(@PathParam("lid") String lid,
      @PathParam("versionId") String versionId) {
    return new ProductResource(uriInfo, request, registryService, lid,
        versionId);
  }

}
