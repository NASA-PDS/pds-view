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

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import gov.nasa.pds.registry.provider.JAXBContextResolver;
import gov.nasa.pds.registry.provider.JacksonObjectMapperProvider;
import gov.nasa.pds.registry.model.Association;
import gov.nasa.pds.registry.model.RegistryResponse;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.query.AssociationFilter;
import gov.nasa.pds.registry.query.AssociationQuery;
import gov.nasa.pds.registry.query.ObjectFilter;
import gov.nasa.pds.registry.query.ExtrinsicQuery;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author pramirez
 * 
 */
public class RegistryClient {
  private WebResource registryResource;
  private String token;
  private String mediaType;

  public RegistryClient(String baseUrl) {
    this(baseUrl, null);
  }

  public RegistryClient(String baseUrl, String token) {
    ClientConfig clientConfig = new DefaultClientConfig();
    clientConfig.getClasses().add(JacksonObjectMapperProvider.class);
    clientConfig.getClasses().add(JAXBContextResolver.class);
    registryResource = Client.create(clientConfig).resource(baseUrl).path(
        "registry");
    this.token = token;
    mediaType = MediaType.APPLICATION_XML;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  public ClientResponse publishExtrinsic(String user, ExtrinsicObject extrinsic) {
    WebResource.Builder builder = registryResource.path("extrinsics")
        .getRequestBuilder();
    if (token != null) {
      builder = builder.header("Cookie", "iPlanetDirectoryPro=\"" + token
          + "\"");
    }
    return builder.accept(mediaType).post(ClientResponse.class, extrinsic);
  }

  public ClientResponse versionExtrinsic(String user,
      ExtrinsicObject extrinsic, String lid) {
    return this.versionExtrinsic(user, extrinsic, lid, true);
  }

  public ClientResponse versionExtrinsic(String user,
      ExtrinsicObject extrinsic, String lid, Boolean major) {
    WebResource.Builder builder = registryResource.path("extrinsics").path(
        extrinsic.getLid()).queryParam("major", major.toString())
        .getRequestBuilder();
    if (token != null) {
      builder = builder.header("Cookie", "iPlanetDirectoryPro=\"" + token
          + "\"");
    }
    return builder.accept(mediaType).post(ClientResponse.class, extrinsic);
  }

  public ClientResponse getLatestExtrinsic(String lid) {
    WebResource.Builder builder = registryResource.path("extrinsics").path(lid)
        .getRequestBuilder();
    if (token != null) {
      builder = builder.header("Cookie", "iPlanetDirectoryPro=\"" + token
          + "\"");
    }
    return builder.accept(mediaType).get(ClientResponse.class);
  }

  public ClientResponse getExtrinsic(String lid, String version) {
    WebResource.Builder builder = registryResource.path("extrinsics").path(lid)
        .path(version).getRequestBuilder();
    if (token != null) {
      builder = builder.header("Cookie", "iPlanetDirectoryPro=\"" + token
          + "\"");
    }
    return builder.accept(mediaType).get(ClientResponse.class);
  }

  public ClientResponse getStatus() {
    WebResource.Builder builder = registryResource.path("status")
        .getRequestBuilder();
    if (token != null) {
      builder = builder.header("Cookie", "iPlanetDirectoryPro=\"" + token
          + "\"");
    }
    return builder.accept(mediaType).get(ClientResponse.class);
  }

  public ClientResponse publishAssociation(String user, Association association) {
    WebResource.Builder builder = registryResource.path("associations")
        .getRequestBuilder();
    if (token != null) {
      builder = builder.header("Cookie", "iPlanetDirectoryPro=\"" + token
          + "\"");
    }
    return builder.accept(mediaType).post(ClientResponse.class, association);
  }

  public ClientResponse getExtrinsics(Integer start, Integer rows) {
    return this
        .getExtrinsics(new ExtrinsicQuery.Builder().build(), start, rows);
  }

  public ClientResponse getExtrinsics(ExtrinsicQuery query, Integer start,
      Integer rows) {
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    if (start != null) {
      params.add("start", start.toString());
    }
    if (rows != null) {
      params.add("rows", rows.toString());
    }

    ObjectFilter filter = query.getFilter();
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
    }

    List<String> sort = query.getSort();
    for (String s : sort) {
      params.add("sort", s);
    }

    params.add("queryOp", query.getOperator().toString());

    WebResource.Builder builder = registryResource.path("extrinsics")
        .queryParams(params).getRequestBuilder();
    if (token != null) {
      builder = builder.header("Cookie", "iPlanetDirectoryPro=\"" + token
          + "\"");
    }

    return builder.accept(mediaType).get(ClientResponse.class);
  }

  public ClientResponse getAssociations(AssociationQuery query, Integer start,
      Integer rows) {
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

    WebResource.Builder builder = registryResource.path("associations")
        .queryParams(params).getRequestBuilder();
    if (token != null) {
      builder = builder.header("Cookie", "iPlanetDirectoryPro=\"" + token
          + "\"");
    }

    return builder.accept(mediaType).get(ClientResponse.class);
  }

  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws Exception {
    RegistryClient client = new RegistryClient(args[0]);
    RegistryResponse response = client.getExtrinsics(null, null).getEntity(
        RegistryResponse.class);
    System.out.println("Total results: " + response.getNumFound());
    System.out.println("Number displayed: " + response.getResults().size());
    for (RegistryObject object : (List<RegistryObject>)response.getResults()) {
      System.out.println(object.getClass().getSimpleName() + " "
          + object.getGuid());
    }

    if (args.length > 1) {
      ExtrinsicObject extrinsic = client.getLatestExtrinsic(args[1]).getEntity(
          ExtrinsicObject.class);
      System.out.println("Latest Extrinsic");
      System.out.println("Extrinsic " + extrinsic.getGuid());
      if (args.length > 2) {
        ObjectFilter filter = new ObjectFilter.Builder().lid(args[1]).build();
        ExtrinsicQuery query = new ExtrinsicQuery.Builder().filter(filter)
            .build();
        response = client.getExtrinsics(query, null, null).getEntity(
            RegistryResponse.class);
        for (RegistryObject object : (List<RegistryObject>)response.getResults()) {
          System.out.println(object.getClass().getSimpleName() + " "
              + object.getGuid() + " " + object.getLid() + " "
              + object.getVersionName());
        }
      }
    }
  }
}
