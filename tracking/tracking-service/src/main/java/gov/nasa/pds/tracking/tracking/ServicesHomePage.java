package gov.nasa.pds.tracking.tracking;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import gov.nasa.pds.tracking.tracking.utils.*;
 
@Path("/")
public class ServicesHomePage {
    @GET
    @Produces("text/html")
    public String defaultProducts() {
 
        /*StringBuilder sb = new StringBuilder();
        sb.append("Services!");*/
 
        return HtmlConstants.PAGE_BEGIN +
		          "<h1>PDS Services:</h1>" +		
	              "<div>" +
	              "<li><a href=\"tracking/products\">Tracking Service</a><br/></li>" +
	              "</div>" 
              + HtmlConstants.PAGE_END;
    }

}

