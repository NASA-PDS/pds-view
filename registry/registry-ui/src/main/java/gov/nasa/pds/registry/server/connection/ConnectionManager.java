package gov.nasa.pds.registry.server.connection;

import gov.nasa.pds.registry.client.RegistryClient;
import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.ui.shared.StatusInformation;
import gov.nasa.pds.registry.ui.shared.ViewAssociation;
import gov.nasa.pds.registry.ui.shared.ViewProduct;
import gov.nasa.pds.registry.ui.shared.ViewProducts;
import gov.nasa.pds.registry.ui.shared.ViewSlot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Manager to send and receive info from remote registry service.
 * 
 * Note that connection to localhost is currently hard wired in.
 * 
 * @author jagander
 */
public class ConnectionManager {

  /**
   * Default location of service endpoint
   */
  public static final String DEFAULT_SERVICE_ENDPOINT = "http://localhost:8080/registry-service/"; //$NON-NLS-1$

  /**
   * Name of properties file that contains override for service endpoint
   */
  public static final String PROPS_NAME = "application.properties"; //$NON-NLS-1$

  /**
   * Instance of applicaiton properties
   */
  public static Properties props;

  // Synchronizes the incoming registry artifacts with those already present
  // in the registry.
  public static void synch() {
    // stub
  }

  // Retrieve the status of the registry service. This can be used to monitor
  // the health of the registry.
  public static StatusInformation getStatus() {
    // stub
    return null;
  }

  /**
   * Get products starting from the first record, with no filtering, the default
   * sort and the default number of records.
   * 
   * @products default product retrieval
   * 
   * @see #getProducts(RegistryQuery, Integer, Integer)
   */
  public static ViewProducts getProducts() {
    return getProducts(null, null, null);
  }

  /**
   * Get products starting from the given record number, with no filtering, the
   * default sort and the default number of records.
   * 
   * @return products from the given start
   * 
   * @see #getProducts(RegistryQuery, Integer, Integer)
   */
  public static ViewProducts getProducts(Integer start) {
    return getProducts(null, start, null);
  }

  /**
   * Get products starting from the given record number and number of records.
   * 
   * @return given number of products from the given start
   * 
   * @see #getProducts(RegistryQuery, Integer, Integer)
   */
  public static ViewProducts getProducts(Integer start, Integer numResults) {
    return getProducts(null, start, numResults);
  }

  /**
   * Retrieve products from remote registry service. Note that this wraps the
   * returned results in a view-only version of the products so that they may be
   * used on the GWT client side.
   * 
   * @param query
   *          a query object containing filter and search params for retrieving
   *          products
   * @param start
   *          start index for retrieved results. Defaults to 1.
   * @param numResults
   *          number of results to return. Defaults to 20.
   * 
   * @return given number of products from the given start with the given filter
   *         conditions
   * 
   */
  public static ViewProducts getProducts(RegistryQuery<ExtrinsicFilter> query,
      Integer start, Integer numResults) {

    RegistryClient client = getRegistry();

    PagedResponse<ExtrinsicObject> pagedResp = null;
    try {
      if (query != null) {
        pagedResp = client.getExtrinsics(query, start, numResults);
      } else {
        pagedResp = client.getObjects(start, numResults, ExtrinsicObject.class);
      }
    } catch (RegistryServiceException e) {
      System.out.println(e.getMessage());
    }

    // convert response to view products
    ViewProducts viewProducts = respToViewProducts(pagedResp);

    return viewProducts;
  }

  public static List<ViewAssociation> getAssociations(
      final RegistryQuery<AssociationFilter> query) {
    return getAssociations(query, null, null);

  }

  public static List<ViewAssociation> getAssociations(
      final RegistryQuery<AssociationFilter> query, Integer start,
      Integer numResults) {
    RegistryClient client = getRegistry();

    PagedResponse<Association> pagedResp = null;
    try {
      pagedResp = client.getAssociations(query, start,
          numResults);
    } catch (RegistryServiceException e) {
      System.out.println(e.getMessage());
    }

    List<ViewAssociation> viewAssociations = respToViewAssociation(pagedResp);

    return viewAssociations;
  }

  // TODO: determine the response format, returns errors for duplicate id for
  // instance
  public static void postArtifacts() {
    // stub
  }

  // Creates a new revision of an artifact in the registry. Follows the same
  // procedures as publishing with the caveat that the logical identifier this
  // artifact carries should already exist in the registry (412 Precondition
  // Failed).
  public static void postRevision() {
    // stub
  }

  /**
   * Get instance of RegistryClient to work with REST interface of registry.
   * Instantiates if necessary.
   */
  public static RegistryClient getRegistry() {
    String serviceEndpoint = null;
    // TODO: determine why the below does not work
    if (false) {
      try {

        // Get a handle to the JNDI environment naming context
        Context env = (Context) new InitialContext().lookup("java:comp/env"); //$NON-NLS-1$

        // get value from context
        serviceEndpoint = (String) env.lookup("serviceEndpoint"); //$NON-NLS-1$
      } catch (NamingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    if (true) {
      // TODO: remove this when the above is working
      Properties props = getProperties();
      if (props != null) {
        serviceEndpoint = props.getProperty(
            "service.endpoint", DEFAULT_SERVICE_ENDPOINT); //$NON-NLS-1$
      } else {
        serviceEndpoint = DEFAULT_SERVICE_ENDPOINT;
      }
    }

    // if unable to get endpoint from context, use the default
    if (serviceEndpoint == null) {
      serviceEndpoint = DEFAULT_SERVICE_ENDPOINT;
    }

    // create an instance of the registry client
    try {
      return new RegistryClient(serviceEndpoint);
    } catch (RegistryClientException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  /**
   * Get an instance of the application properties.
   */
  @SuppressWarnings("nls")
  public static Properties getProperties() {
    if (props == null) {

      final InputStream is = ConnectionManager.class.getResourceAsStream("/"
          + PROPS_NAME);

      try {
        props = new Properties();
        props.load(is);
        is.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return props;
  }

  private static ViewProducts respToViewProducts(
      final PagedResponse<ExtrinsicObject> pagedResp) {
    ViewProducts viewProducts = new ViewProducts();
    viewProducts.setStart(pagedResp.getStart());
    viewProducts.setSize(pagedResp.getNumFound());
    List<ExtrinsicObject> products = pagedResp.getResults();
    for (RegistryObject product : products) {
      ViewProduct vProduct = new ViewProduct();
      viewProducts.add(vProduct);
      vProduct.setGuid(product.getGuid());
      vProduct.setHome(product.getHome());
      vProduct.setLid(product.getLid());
      vProduct.setObjectType(product.getObjectType());
      vProduct.setStatus(product.getStatus().toString());
      vProduct.setName(product.getName());
      // There is no longer a PDS internal version so copy name
      vProduct.setVersionId(product.getVersionName());
      vProduct.setVersionName(product.getVersionName());

      List<ViewSlot> vslots = new ArrayList<ViewSlot>();
      vProduct.setSlots(vslots);
      Set<Slot> slots = product.getSlots();
      if (slots != null) {
        for (Slot slot : slots) {
          ViewSlot vslot = new ViewSlot();
          vslots.add(vslot);
          vslot.setId(String.valueOf(slot.getId()));
          vslot.setName(slot.getName());
          vslot.setValues(slot.getValues());
        }
      }
    }
    return viewProducts;
  }

  private static List<ViewAssociation> respToViewAssociation(
      final PagedResponse<Association> pagedResp) {
    List<ViewAssociation> viewAssociations = new ArrayList<ViewAssociation>();
    List<Association> associations = pagedResp.getResults();
    for (RegistryObject ro : associations) {
      Association association = (Association) ro;
      ViewAssociation vAssociation = new ViewAssociation();
      viewAssociations.add(vAssociation);
      vAssociation.setAssociationType(association.getAssociationType());
      vAssociation.setSourceGuid(association.getSourceObject());
      vAssociation.setTargetGuid(association.getTargetObject());

      vAssociation.setLid(association.getLid());
      vAssociation.setObjectType(association.getObjectType());
      vAssociation.setStatus(association.getStatus().toString());
      vAssociation.setName(association.getName());
      // There is no longer a PDS internal version so copy name
      vAssociation.setVersionId(association.getVersionName());
      vAssociation.setVersionName(association.getVersionName());

    }
    return viewAssociations;
  }
}
