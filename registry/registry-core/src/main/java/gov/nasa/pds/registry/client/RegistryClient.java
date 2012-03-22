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

import gov.nasa.pds.registry.exception.RegistryClientException;
import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.AuditableEvent;
import gov.nasa.pds.registry.model.ClassificationNode;
import gov.nasa.pds.registry.model.ClassificationScheme;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.model.Service;
import gov.nasa.pds.registry.provider.JAXBContextResolver;
import gov.nasa.pds.registry.provider.JacksonObjectMapperProvider;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.EventFilter;
import gov.nasa.pds.registry.query.ExtrinsicFilter;
import gov.nasa.pds.registry.query.RegistryQuery;

import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
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
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss");
  private WebResource service;
  private SecurityContext securityContext;
  private String mediaType;
  private String registrationPackageGuid;

  private final static HashMap<Class<? extends RegistryObject>, String> resourceMap = new HashMap<Class<? extends RegistryObject>, String>();
  static {
    resourceMap.put(ExtrinsicObject.class, "extrinsics");
    resourceMap.put(Association.class, "associations");
    resourceMap.put(Service.class, "services");
    resourceMap.put(RegistryPackage.class, "packages");
    resourceMap.put(AuditableEvent.class, "events");
    resourceMap.put(ClassificationScheme.class, "schemes");
    resourceMap.put(ClassificationNode.class, "nodes");
  }

  public RegistryClient(String baseUrl) throws RegistryClientException {
    this(baseUrl, null, null, null);
  }

  public RegistryClient(String baseUrl, SecurityContext securityContext,
      String username, String password) throws RegistryClientException {
    ClientConfig config = new DefaultClientConfig();
    config.getClasses().add(JacksonObjectMapperProvider.class);
    config.getClasses().add(JAXBContextResolver.class);
    if (securityContext != null) {
      try {
        // With the current setup of the Security Service, we need to set up
        // an insecure SSL connection.
        this.securityContext = securityContext;
        HostnameVerifier hv = getHostnameVerifier();
        SSLContext ctx = this.getSSLContext();
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
            new HTTPSProperties(hv, ctx));
        service = Client.create(config).resource(baseUrl);
        service.addFilter(new HTTPBasicAuthFilter(username, password));
      } catch (Exception e) {
        throw new RegistryClientException("Error occurred while initializing "
            + "the registry client: " + e.getMessage());
      }
    } else {
      service = Client.create(config).resource(baseUrl);
    }

    mediaType = MediaType.APPLICATION_JSON;
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
    WebResource.Builder builder = service.path(resourceMap.get(objectClass))
        .path(guid).getRequestBuilder();
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
   * Removes an object from the registry of the given type
   * 
   * @param <T>
   * @param guid
   *          identifier for the object
   * @param objectClass
   *          of object interested in deleting
   * @throws RegistryServiceException
   */
  public <T extends RegistryObject> void deleteObject(String guid,
      Class<T> objectClass) throws RegistryServiceException {
    WebResource.Builder builder = service.path(resourceMap.get(objectClass))
        .path(guid).getRequestBuilder();
    ClientResponse response = builder.accept(mediaType).delete(
        ClientResponse.class);
    if (response.getClientResponseStatus() != Status.OK) {
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
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    if (registrationPackageGuid != null) {
      params.add("packageGuid", registrationPackageGuid);
    }
    WebResource.Builder builder = service.path(
        resourceMap.get(object.getClass())).queryParams(params)
        .getRequestBuilder();
    ClientResponse response = builder.accept(mediaType).post(
        ClientResponse.class, object);
    if (response.getClientResponseStatus() == Status.CREATED) {
      String guid = response.getEntity(String.class);
      // If the client didn't have a guid the service generated it so set it as
      // a convenience
      object.setGuid(guid);
      return guid;
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
    WebResource.Builder builder = service.path(
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
    WebResource.Builder builder = service.path(
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
    WebResource.Builder builder = service.path(resourceMap.get(objectClass))
        .path("logicals").path(lid).path("latest").getRequestBuilder();
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
    WebResource.Builder builder = service.path(resourceMap.get(objectClass))
        .queryParams(params).getRequestBuilder();
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

    WebResource.Builder builder = service.path(
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

    WebResource.Builder builder = service.path(
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
  public PagedResponse<AuditableEvent> getAuditableEvents(
      RegistryQuery<EventFilter> query, Integer start, Integer rows)
      throws RegistryServiceException {
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    if (start != null) {
      params.add("start", start.toString());
    }
    if (rows != null) {
      params.add("rows", rows.toString());
    }
    EventFilter filter = query.getFilter();
    if (filter != null) {
      if (filter.getEventStart() != null) {
        params.add("eventStart", dateFormat.format(filter.getEventStart()));
      }
      if (filter.getEventEnd() != null) {
        params.add("eventEnd", dateFormat.format(filter.getEventEnd()));
      }
      if (filter.getEventType() != null) {
        params.add("eventType", filter.getEventType().toString());
      }
    }

    WebResource.Builder builder = service.path(
        resourceMap.get(AuditableEvent.class)).queryParams(params)
        .getRequestBuilder();
    ClientResponse response = builder.accept(mediaType).get(
        ClientResponse.class);
    if (response.getClientResponseStatus() == Status.OK) {
      return response
          .getEntity(new GenericType<PagedResponse<AuditableEvent>>() {
          });
    } else {
      throw new RegistryServiceException(response.getEntity(String.class),
          Response.Status.fromStatusCode(response.getStatus()));
    }
  }

  public String getRegistrationPackageGuid() {
    return registrationPackageGuid;
  }

  public void setRegistrationPackageId(String registrationPackageGuid) {
    this.registrationPackageGuid = registrationPackageGuid;
  }

  /**
   * Mehthod for SSL connection.
   * 
   * @return HostnameVerifier object
   */
  private HostnameVerifier getHostnameVerifier() {
    HostnameVerifier hv = new HostnameVerifier() {

      @Override
      public boolean verify(String hostname, SSLSession session) {
        // TODO Auto-generated method stub
        return true;
      }
    };
    return hv;
  }

  private SSLContext getSSLContext() {
    TrustManager mytm[] = null;
    KeyManager mykm[] = null;

    try {
      mytm = new TrustManager[] { new MyX509TrustManager(securityContext
          .getTruststorePath(), securityContext.getKeystorePassword()
          .toCharArray()) };
      mykm = new KeyManager[] { new MyX509KeyManager(securityContext
          .getKeystorePath(), securityContext.getKeystorePassword()
          .toCharArray()) };
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    SSLContext ctx = null;
    try {
      ctx = SSLContext.getInstance("SSL");
      ctx.init(mykm, mytm, null);
    } catch (java.security.GeneralSecurityException ex) {
    }
    return ctx;
  }

  /**
   * Taken from
   * http://java.sun.com/javase/6/docs/technotes/guides/security/jsse/
   * JSSERefGuide.html
   * 
   */
  static class MyX509TrustManager implements X509TrustManager {

    /*
     * The default PKIX X509TrustManager9. We'll delegate decisions to it, and
     * fall back to the logic in this class if the default X509TrustManager
     * doesn't trust it.
     */
    X509TrustManager pkixTrustManager;

    MyX509TrustManager(String trustStore, char[] password) throws Exception {
      this(new File(trustStore), password);
    }

    MyX509TrustManager(File trustStore, char[] password) throws Exception {
      // create a "default" JSSE X509TrustManager.

      KeyStore ks = KeyStore.getInstance("JKS");

      ks.load(new FileInputStream(trustStore), password);

      TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
      tmf.init(ks);

      TrustManager tms[] = tmf.getTrustManagers();

      /*
       * Iterate over the returned trustmanagers, look for an instance of
       * X509TrustManager. If found, use that as our "default" trust manager.
       */
      for (int i = 0; i < tms.length; i++) {
        if (tms[i] instanceof X509TrustManager) {
          pkixTrustManager = (X509TrustManager) tms[i];
          return;
        }
      }

      /*
       * Find some other way to initialize, or else we have to fail the
       * constructor.
       */
      throw new Exception("Couldn't initialize");
    }

    /*
     * Delegate to the default trust manager.
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
      try {
        pkixTrustManager.checkClientTrusted(chain, authType);
      } catch (CertificateException excep) {
        // do any special handling here, or rethrow exception.
      }
    }

    /*
     * Delegate to the default trust manager.
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
      try {
        pkixTrustManager.checkServerTrusted(chain, authType);
      } catch (CertificateException excep) {
        /*
         * Possibly pop up a dialog box asking whether to trust the cert chain.
         */
      }
    }

    /*
     * Merely pass this through.
     */
    public X509Certificate[] getAcceptedIssuers() {
      return pkixTrustManager.getAcceptedIssuers();
    }
  }

  /**
   * Inspired from
   * http://java.sun.com/javase/6/docs/technotes/guides/security/jsse
   * /JSSERefGuide.html
   * 
   */
  static class MyX509KeyManager implements X509KeyManager {

    /*
     * The default PKIX X509KeyManager. We'll delegate decisions to it, and fall
     * back to the logic in this class if the default X509KeyManager doesn't
     * trust it.
     */
    X509KeyManager pkixKeyManager;

    MyX509KeyManager(String keyStore, char[] password) throws Exception {
      this(new File(keyStore), password);
    }

    MyX509KeyManager(File keyStore, char[] password) throws Exception {
      // create a "default" JSSE X509KeyManager.

      KeyStore ks = KeyStore.getInstance("JKS");
      ks.load(new FileInputStream(keyStore), password);

      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509",
          "SunJSSE");
      kmf.init(ks, password);

      KeyManager kms[] = kmf.getKeyManagers();

      /*
       * Iterate over the returned keymanagers, look for an instance of
       * X509KeyManager. If found, use that as our "default" key manager.
       */
      for (int i = 0; i < kms.length; i++) {
        if (kms[i] instanceof X509KeyManager) {
          pkixKeyManager = (X509KeyManager) kms[i];
          return;
        }
      }

      /*
       * Find some other way to initialize, or else we have to fail the
       * constructor.
       */
      throw new Exception("Couldn't initialize");
    }

    public PrivateKey getPrivateKey(String arg0) {
      return pkixKeyManager.getPrivateKey(arg0);
    }

    public X509Certificate[] getCertificateChain(String arg0) {
      return pkixKeyManager.getCertificateChain(arg0);
    }

    public String[] getClientAliases(String arg0, Principal[] arg1) {
      return pkixKeyManager.getClientAliases(arg0, arg1);
    }

    public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
      return pkixKeyManager.chooseClientAlias(arg0, arg1, arg2);
    }

    public String[] getServerAliases(String arg0, Principal[] arg1) {
      return pkixKeyManager.getServerAliases(arg0, arg1);
    }

    public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
      return pkixKeyManager.chooseServerAlias(arg0, arg1, arg2);
    }
  }

  public static void main(String[] args) throws Exception {
    RegistryClient client = new RegistryClient(args[0]);
    ExtrinsicObject eo = client.getObject(args[1], ExtrinsicObject.class);
    System.out.println(eo.getGuid());
  }
}
