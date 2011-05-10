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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author pramirez
 *
 */

@Path("/")
@Component
@Scope("request")
public class RootResource {
  @Context
  UriInfo uriInfo;

  @Context
  Request request;
  
  @GET
  public Response getRegistryResource() {
    Response.ResponseBuilder builder = Response.ok("Registry Service Root");
    String registryUri = uriInfo.getBaseUriBuilder().clone().path(
        RegistryResource.class).build().toString();
    builder.header("Link", new Link(registryUri, "registry", null));
    return builder.build();
  }

}
