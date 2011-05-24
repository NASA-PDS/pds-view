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

package gov.nasa.pds.registry.client;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import gov.nasa.pds.registry.provider.JAXBContextResolver;
import gov.nasa.pds.registry.provider.JacksonObjectMapperProvider;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.Service;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * This class is a Java client to be used to exchange information with a
 * registry service. In the background it simply uses HTTP calls but returns
 * Java objects to ease integration.
 * 
 * @author pramirez
 * 
 */
public class RegistryClient {
  private WebResource service;
  private String username;
  private String password;
  private String mediaType;
  private final static HashMap<Class<? extends RegistryObject>, String> resourceMap = new HashMap<Class<? extends RegistryObject>, String>();
  static {
    resourceMap.put(ExtrinsicObject.class, "extrinsics");
    resourceMap.put(Association.class, "associations");
    resourceMap.put(Service.class, "services");
  }

  public RegistryClient(String baseUrl) {
    this(baseUrl, null, null);
  }

  public RegistryClient(String baseUrl, String username, String password) {
    ClientConfig config = new DefaultClientConfig();
    config.getClasses().add(JacksonObjectMapperProvider.class);
    config.getClasses().add(JAXBContextResolver.class);
    service = Client.create(config).resource(baseUrl);
    mediaType = MediaType.APPLICATION_JSON;
    this.username = username;
    this.password = password;
  }

  /**
   * Used to override the type of messages the client will exchange. Currently
   * the regsitry supports application/xml and application/json but defaults to
   * json. The end client will not see these calls so to cut down on data
   * transferred the more compact json should be used.
   * 
   * @param mediaType
   *          to use for exchanging messages
   */
  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  /**
   * Retrieves an object from the registry of the given type
   * 
   * @param guid
   *          identifier for the object
   * @param objectClass
   *          of object interested in retrieving
   * @return the identified object
   * @throws RegistryServiceException
   */
  public <T extends RegistryObject> T getObject(String guid,
      Class<T> objectClass) throws RegistryServiceException {
    WebResource.Builder builder = service.path("registry").path(
        resourceMap.get(objectClass)).path(guid).getRequestBuilder();
    ClientResponse response = builder.accept(mediaType).get(
        ClientResponse.class);
    if (response.getClientResponseStatus() == Status.OK) {
      return response.getEntity(objectClass);
    } else {
      throw new RegistryServiceException(response.getEntity(String.class),
          Response.Status.fromStatusCode(response.getStatus()));
    }
  }

  /**
   * Publish a registry object to the service
   * 
   * @param object
   *          to publish
   * @return the globally unique identifier
   * @throws RegistryServiceException
   */
  public String publishObject(RegistryObject object)
      throws RegistryServiceException {
    WebResource.Builder builder = service.path("registry").path(
        resourceMap.get(object.getClass())).getRequestBuilder();
    ClientResponse response = builder.accept(mediaType).post(
        ClientResponse.class, object);
    if (response.getClientResponseStatus() == Status.CREATED) {
      return response.getEntity(String.class);
    } else {
      throw new RegistryServiceException(response.getEntity(String.class),
          Response.Status.fromStatusCode(response.getStatus()));
    }
  }

  /**
   * Publishes a version of the given object that is considered a major version
   * update.
   * 
   * @param object
   *          to publish
   * @return globally unique identifier of versioned object
   * @throws RegistryServiceException
   */
  public String versionObject(RegistryObject object)
      throws RegistryServiceException {
    return this.versionObject(object, true);
  }

  /**
   * Publishes a version of the given object
   * 
   * @param object
   *          to publish
   * @param major
   *          flag to indicate major or minor version
   * @return globally unique identifier of versioned object
   * @throws RegistryServiceException
   */
  public String versionObject(RegistryObject object, Boolean major)
      throws RegistryServiceException {
    WebResource.Builder builder = service.path("registry").path(
        resourceMap.get(object.getClass())).path("logicals").path(
        object.getLid()).queryParam("major", major.toString())
        .getRequestBuilder();
    ClientResponse response = builder.accept(mediaType).post(
        ClientResponse.class, object);
    if (response.getClientResponseStatus() == Status.CREATED) {
      return response.getEntity(String.class);
    } else {
      throw new RegistryServiceException(response.getEntity(String.class),
          Response.Status.fromStatusCode(response.getStatus()));
    }
  }

  /**
   * Updates the given registry object by using its guid to indicate the object
   * to update.
   * 
   * @param object
   *          to update to
   * @throws RegistryServiceException
   */
  public void updateObject(RegistryObject object)
      throws RegistryServiceException {
    WebResource.Builder builder = service.path("registry").path(
        resourceMap.get(object.getClass())).path(object.getGuid())
        .getRequestBuilder();
    ClientResponse response = builder.accept(mediaType).post(
        ClientResponse.class, object);
    if (response.getClientResponseStatus() == Status.OK) {
      return;
    } else {
      throw new RegistryServiceException(response.getEntity(String.class),
          Response.Status.fromStatusCode(response.getStatus()));
    }
  }

  /**
   * Retrieve the latest version of a registry object
   * 
   * @param lid
   *          logical identifier which is associated with a collection of
   *          objects
   * @param objectClass
   *          of object interested in retrieving
   * @return latest managed copy of the requested object
   * @throws RegistryServiceException
   */
  public <T extends RegistryObject> T getLatestObject(String lid,
      Class<T> objectClass) throws RegistryServiceException {
    WebResource.Builder builder = service.path("registry").path(
        resourceMap.get(objectClass)).path("logicals").path(lid).path("latest")
        .getRequestBuilder();
    ClientResponse response = builder.accept(mediaType).get(
        ClientResponse.class);
    if (response.getClientResponseStatus() == Status.OK) {
      return response.getEntity(objectClass);
    } else {
      throw new RegistryServiceException(response.getEntity(String.class),
          Response.Status.fromStatusCode(response.getStatus()));
    }
  }

  /**
   * Retrieves a paged set of registry objects from the collection of objects of
   * the specified type.
   * 
   * @param start
   *          indicates where in the set of objects to begin
   * @param rows
   *          indicates how many objects to return
   * @param objectClass
   *          the type of object to retrieve
   * @return all objects found with the given constraints
   * @throws RegistryServiceException
   */
  public <T extends RegistryObject> PagedResponse<T> getObjects(Integer start,
      Integer rows, Class<T> objectClass) throws RegistryServiceException {
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    if (start != null) {
      params.add("start", start.toString());
    }
    if (rows != null) {
      params.add("rows", rows.toString());
    }
    WebResource.Builder builder = service.path("registry").path(
        resourceMap.get(objectClass)).queryParams(params).getRequestBuilder();
    ClientResponse response = builder.accept(mediaType).get(
        ClientResponse.class);
    if (response.getClientResponseStatus() == Status.OK) {
      return response.getEntity(new GenericType<PagedResponse<T>>() {
      });
    } else {
      throw new RegistryServiceException(response.getEntity(String.class),
          Response.Status.fromStatusCode(response.getStatus()));
    }
  }

  /**
   * Retrieves a set of extrinsic objects that match the query.
   * 
   * @param query
   *          filters for the extrinsic
   * @param start
   *          indicates where in the set of objects to begin
   * @param rows
   *          indicates how many objects to return
   * @return paged set of extrisic objects
   * @throws RegistryServiceException
   */
  public PagedResponse<ExtrinsicObject> getExtrinsics(
      RegistryQuery<ExtrinsicFilter> query, Integer start, Integer rows)
      throws RegistryServiceException {
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    if (start != null) {
      params.add("start", start.toString());
    }
    if (rows != null) {
      params.add("rows", rows.toString());
    }

    ExtrinsicFilter filter = query.getFilter();
    if (filter != null) {
      if (filter.getGuid() != null) {
        params.add("guid", filter.getGuid());
      }
      if (filter.getLid() != null) {
        params.add("lid", filter.getLid());
      }
      if (filter.getName() != null) {
        params.add("name", filter.getName());
      }
      if (filter.getObjectType() != null) {
        params.add("objectType", filter.getObjectType());
      }
      if (filter.getStatus() != null) {
        params.add("status", filter.getStatus().toString());
      }
      if (filter.getVersionName() != null) {
        params.add("versionName", filter.getVersionName());
      }
      if (filter.getContentVersion() != null) {
        params.add("contentVersion", filter.getContentVersion());
      }
    }

    List<String> sort = query.getSort();
    for (String s : sort) {
      params.add("sort", s);
    }

    params.add("queryOp", query.getOperator().toString());

    WebResource.Builder builder = service.path("registry").path(
        resourceMap.get(ExtrinsicObject.class)).queryParams(params)
        .getRequestBuilder();

    ClientResponse response = builder.accept(mediaType).get(
        ClientResponse.class);

    if (response.getClientResponseStatus() == Status.OK) {
      return response
          .getEntity(new GenericType<PagedResponse<ExtrinsicObject>>() {
          });
    } else {
      throw new RegistryServiceException(response.getEntity(String.class),
          Response.Status.fromStatusCode(response.getStatus()));
    }
  }

  /**
   * Retrieves a set of association objects that match the query.
   * 
   * @param query
   *          filters for the association
   * @param start
   *          indicates where in the set of objects to begin
   * @param rows
   *          indicates how many objects to return
   * @return paged set of association objects
   * @throws RegistryServiceException
   */
  public PagedResponse<Association> getAssociations(
      RegistryQuery<AssociationFilter> query, Integer start, Integer rows)
      throws RegistryServiceException {
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    if (start != null) {
      params.add("start", start.toString());
    }
    if (rows != null) {
      params.add("rows", rows.toString());
    }

    AssociationFilter filter = query.getFilter();
    if (filter != null) {
      if (filter.getTargetObject() != null) {
        params.add("targetObject", filter.getTargetObject());
      }
      if (filter.getSourceObject() != null) {
        params.add("sourceObject", filter.getSourceObject());
      }
      if (filter.getAssociationType() != null) {
        params.add("associationType", filter.getAssociationType());
      }
    }

    List<String> sort = query.getSort();
    for (String s : sort) {
      params.add("sort", s);
    }

    params.add("queryOp", query.getOperator().toString());

    WebResource.Builder builder = service.path("registry").path(
        resourceMap.get(Association.class)).queryParams(params)
        .getRequestBuilder();

    ClientResponse response = builder.accept(mediaType).get(
        ClientResponse.class);

    if (response.getClientResponseStatus() == Status.OK) {
      return response.getEntity(new GenericType<PagedResponse<Association>>() {
      });
    } else {
      throw new RegistryServiceException(response.getEntity(String.class),
          Response.Status.fromStatusCode(response.getStatus()));
    }

  }

  public static void main(String[] args) throws Exception {
    RegistryClient client = new RegistryClient(args[0]);
    ExtrinsicObject eo = client.getObject(args[1], ExtrinsicObject.class);
    PagedResponse<ExtrinsicObject> pr = client.getObjects(1, 10,
        ExtrinsicObject.class);
    System.out.println(eo.getGuid());
    System.out.println(pr.getNumFound());
    ExtrinsicFilter filter = new ExtrinsicFilter.Builder().guid(args[1])
        .build();
    RegistryQuery<ExtrinsicFilter> query = new RegistryQuery.Builder<ExtrinsicFilter>()
        .filter(filter).build();
  }
}
