package gov.nasa.pds.tracking.tracking;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import gov.nasa.pds.tracking.tracking.db.Reference;
import gov.nasa.pds.tracking.tracking.utils.*;
 
@Path("/")
public class ServicesHomePage {
    @GET
    @Produces("text/html")
    public String defaultProducts() {

 
        return HtmlConstants.PAGE_BEGIN +
		          "<h1>Tracking Service</h1>" +		
	              "<div>" +
	              "<li><a href=\"html/products\">All products - HTML</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" +
	              "<li><a href=\"html/products/true\">All products that have associated deliveries - HTML</a><br/></li>" +
	              "</div>" +
	              "<p></p><p></p><div>" +
	              "<li><a href=\"json/products\">All products - JSON</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" +
	              "<li><a href=\"json/products/urn:nasa:pds:context_pds3:instrument:waves.jno/urn:nasa:pds:context_pds3:investigation:mission.juno\">" +
	              		"All products that have associated deliveries with designated instrument refernce and investication reference - JSON</a><br/></li>" +
	              		"(instrument_reference = 'urn:nasa:pds:context_pds3:instrument:waves.jno'" +
	              			"AND investigation.reference = 'urn:nasa:pds:context_pds3:investigation:mission.juno')" +
	              "<li><a href=\"json/products/urn:nasa:pds:context_pds3:instrument:waves.jno/null\">" + 
            		"All products that have associated deliveries with designated instrument refernce - JSON</a><br/></li>" +
            		"(instrument_reference = 'urn:nasa:pds:context_pds3:instrument:waves.jno')" +
            	  "<li><a href=\"json/products/null/urn:nasa:pds:context_pds3:investigation:mission.juno\">" + 
            		"All products that have associated deliveries with designated investication reference - JSON</a><br/></li>" +
      				"(investigation.reference = 'urn:nasa:pds:context_pds3:investigation:mission.juno')" +
	              "<li><a href=\"json/products/null/null\">All products that have associated deliveries - JSON</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" + 
	              "<li><a href=\"json/delivery\">All Deliveries - JSON</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" + 
	              "<li><a href=\"json/Users\">All Users - JSON</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" + 
	              "<li><a href=\"json/Users/null\">All Users with Role - JSON</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" + 
	              "<li><a href=\"json/Users/sean.hardman@jpl.nasa.gov\">User Role for sean.hardman@jpl.nasa.gov - JSON</a><br/></li>" +
	              "</div>"	              +
	              "<p></p><div>" +
	              "<li><a href=\"json/Users/urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0/" + Reference.INST_TABLENAME + "\">" +
	              	"Product (urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0) Instrument Role Query  - JSON</a><br/></li>" +
	              "<li><a href=\"json/Users/urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0/" + Reference.INVES_TABLENAME + "\">" + 
            		"Product (urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0) Investigation Role Query - JSON</a><br/></li>" +
            	  "<li><a href=\"json/Users/urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0/" + Reference.NODE_TABLENAME + "\">" + 
            		"Product (urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0) Node Role Query - JSON</a><br/></li>" +
	              "</div>"
              + HtmlConstants.PAGE_END;
    }

}

