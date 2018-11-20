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
		          "<h2>HTML</h2>" +
	              "<div>" +
	              "<li><a href=\"html/products\">All products</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" +
	              "<li><a href=\"html/products/null/null\">All products that have associated deliveries</a><br/></li>" +
	              "</div>" +
	              "<p></p><h2>JSON</h2><div>" +
	              "<li><a href=\"json/products\">All products</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" +
	              "<li><a href=\"json/products/urn:nasa:pds:context_pds3:instrument:waves.jno/urn:nasa:pds:context_pds3:investigation:mission.juno\">" +
	              		"All products that have associated deliveries with designated instrument refernce and investication reference</a><br/></li>" +
	              		"(instrument_reference = 'urn:nasa:pds:context_pds3:instrument:waves.jno'" +
	              			"AND investigation.reference = 'urn:nasa:pds:context_pds3:investigation:mission.juno')" +
	              "<li><a href=\"json/products/urn:nasa:pds:context_pds3:instrument:waves.jno/null\">" + 
            		"All products that have associated deliveries with designated instrument refernce</a><br/></li>" +
            		"(instrument_reference = 'urn:nasa:pds:context_pds3:instrument:waves.jno')" +
            	  "<li><a href=\"json/products/null/urn:nasa:pds:context_pds3:investigation:mission.juno\">" + 
            		"All products that have associated deliveries with designated investication reference</a><br/></li>" +
      				"(investigation.reference = 'urn:nasa:pds:context_pds3:investigation:mission.juno')" +
	              "<li><a href=\"json/products/null/null\">All products that have associated deliveries</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" + 
	              "<li><a href=\"json/delivery\">All Deliveries</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" + 
	              "<li><a href=\"json/users\">All Users</a><br/></li>" +
	              "</div>" +
	              /*"<p></p><div>" + 
	              "<li><a href=\"json/users/null\">All Users with Role</a><br/></li>" +
	              "</div>" +*/
	              "<p></p><div>" + 
	              "<li><a href=\"json/users/sean.hardman@jpl.nasa.gov\">User Role for sean.hardman@jpl.nasa.gov</a><br/></li>" +
	              "</div>"	              +
	              "<p></p><div>" +
	              "<li><a href=\"json/users/urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0/" + Reference.INST_TABLENAME + "\">" +
	              	"Product (urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0) Instrument Role Query</a><br/></li>" +
	              "<li><a href=\"json/users/urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0/" + Reference.INVES_TABLENAME + "\">" + 
            		"Product (urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0) Investigation Role Query</a><br/></li>" +
            	  "<li><a href=\"json/users/urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0/" + Reference.NODE_TABLENAME + "\">" + 
            		"Product (urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0) Node Role Query</a><br/></li>" +
	              "</div>" +
	              "<p></p><h2>XML</h2><div>" +
	              "<li><a href=\"xml/products\">All products</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" +
	              "<li><a href=\"xml/products/urn:nasa:pds:context_pds3:instrument:waves.jno/urn:nasa:pds:context_pds3:investigation:mission.juno\">" +
	              		"All products that have associated deliveries with designated instrument refernce and investication reference</a><br/></li>" +
	              		"(instrument_reference = 'urn:nasa:pds:context_pds3:instrument:waves.jno'" +
	              			"AND investigation.reference = 'urn:nasa:pds:context_pds3:investigation:mission.juno')" +
	              "<li><a href=\"xml/products/urn:nasa:pds:context_pds3:instrument:waves.jno/null\">" + 
            		"All products that have associated deliveries with designated instrument refernce</a><br/></li>" +
            		"(instrument_reference = 'urn:nasa:pds:context_pds3:instrument:waves.jno')" +
            	  "<li><a href=\"xml/products/null/urn:nasa:pds:context_pds3:investigation:mission.juno\">" + 
            		"All products that have associated deliveries with designated investication reference</a><br/></li>" +
      				"(investigation.reference = 'urn:nasa:pds:context_pds3:investigation:mission.juno')" +
	              "<li><a href=\"xml/products/null/null\">All products that have associated deliveries</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" + 
	              "<li><a href=\"xml/delivery\">All Deliveries</a><br/></li>" +
	              "</div>" +
	              "<p></p><div>" + 
	              "<li><a href=\"xml/users\">All Users</a><br/></li>" +
	              "</div>" +
	              /*"<p></p><div>" + 
	              "<li><a href=\"xml/users/null\">All Users with Role</a><br/></li>" +
	              "</div>" +*/
	              "<p></p><div>" + 
	              "<li><a href=\"xml/users/sean.hardman@jpl.nasa.gov\">User Role for sean.hardman@jpl.nasa.gov</a><br/></li>" +
	              "</div>"+
	              "<p></p><div>" +
	              "<li><a href=\"xml/users/urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0/" + Reference.INST_TABLENAME + "\">" +
	              	"Product (urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0) Instrument Role Query </a><br/></li>" +
	              "<li><a href=\"xml/users/urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0/" + Reference.INVES_TABLENAME + "\">" + 
            		"Product (urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0) Investigation Role Query</a><br/></li>" +
            	  "<li><a href=\"xml/users/urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0/" + Reference.NODE_TABLENAME + "\">" + 
            		"Product (urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0) Node Role Query</a><br/></li>" +
	              "</div>"
              + HtmlConstants.PAGE_END;
    }
    
    @GET
    @Path("/submissionstatusadd")
    @Produces("text/html")
    public String submissionStatusForm() {
    	return HtmlConstants.PAGE_BEGIN +
		          "<h1>Submission Status</h1>" +
		          "<h2>Add Submission and Submission Status</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/submissionstatus/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Delivery ID: </td><td><input id=\"id\" name=\"Delivery_ID\" /></td></tr>" +
		          "<tr><td>Status: </td><td><input id=\"status\" name=\"Status\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/submissionstatus/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Delivery ID: </td><td><input id=\"id\" name=\"Delivery_ID\" /></td></tr>" +
		          "<tr><td>Status: </td><td><input id=\"status\" name=\"Status\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<p></p>" +
		          "<h2>Add Submission Status</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/submissionstatus/addstatus\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Delivery ID: </td><td><input id=\"id\" name=\"Delivery_ID\" /></td></tr>" +
		          "<tr><td>Submission Time: </td><td><input id=\"submissionDate\" name=\"SubmissionDate\" /> (yyyy-MM-ddTHH:mm:ss)</td></tr>" +
		          "<tr><td>Status: </td><td><input id=\"status\" name=\"Status\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/submissionstatus/addstatus\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Delivery ID: </td><td><input id=\"id\" name=\"Delivery_ID\" /></td></tr>" +
		          "<tr><td>Submission Time: </td><td><input id=\"submissionDate\" name=\"SubmissionDate\" /> (yyyy-MM-ddTHH:mm:ss)</td></tr>" +
		          "<tr><td>Status: </td><td><input id=\"status\" name=\"Status\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<p></p>" +
		          "<h2>Update submission status</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/submissionstatus/update\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Delivery ID: </td><td><input id=\"id\" name=\"Delivery_ID\" /></td></tr>" +
		          "<tr><td>Submission Time: </td><td><input id=\"submissionDate\" name=\"SubmissionDate\" /> (yyyy-MM-ddTHH:mm:ss)</td></tr>" +
		          "<tr><td>Status Time: </td><td><input id=\"statusDate\" name=\"StatusDate\" /> (yyyy-MM-ddTHH:mm:ss)</td></tr>" +
		          "<tr><td>Status: </td><td><input id=\"status\" name=\"Status\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Update\" /></td></tr>" +
		          "</table>" +
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/submissionstatus/update\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Delivery ID: </td><td><input id=\"id\" name=\"Delivery_ID\" /></td></tr>" +
		          "<tr><td>Submission Time: </td><td><input id=\"submissionDate\" name=\"SubmissionDate\" /> (yyyy-MM-ddTHH:mm:ss)</td></tr>" +
		          "<tr><td>Status Time: </td><td><input id=\"statusDate\" name=\"StatusDate\" /> (yyyy-MM-ddTHH:mm:ss)</td></tr>" +
		          "<tr><td>Status: </td><td><input id=\"status\" name=\"Status\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Update\" /></td></tr>" +
		          "</table>" +
		          "</form>" +
		          "</div>"
            + HtmlConstants.PAGE_END;
    }
    @GET
    @Path("/doiadd")
    @Produces("text/html")
    public String doiForm() {
    	return HtmlConstants.PAGE_BEGIN +
		          "<h1>DOI</h1>" +
		          "<h2>Add</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/doi/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LOGICAL_ID\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>DOI: </td><td><input id=\"doi\" name=\"Doi\" /></td></tr>" +
		          "<tr><td>URL: </td><td><input id=\"url\" name=\"URL\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/doi/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LOGICAL_ID\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>DOI: </td><td><input id=\"doi\" name=\"Doi\" /></td></tr>" +
		          "<tr><td>URL: </td><td><input id=\"url\" name=\"URL\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<p></p>" +
		          "<h2>Update</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/doi/update\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LOGICAL_ID\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>URL: </td><td><input id=\"url\" name=\"URL\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Update\" /></td></tr>" +
		          "</table>" +
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/doi/update\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LOGICAL_ID\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>URL: </td><td><input id=\"url\" name=\"URL\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Update\" /></td></tr>" +
		          "</table>" +
		          "</form>" +
		          "</div>"
            + HtmlConstants.PAGE_END;
    }
    @GET
    @Path("/productadd")
    @Produces("text/html")
    public String productForm() {
    	return HtmlConstants.PAGE_BEGIN +
		          "<h1>Product</h1>" +
		          "<h2>Add</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/products/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Title: </td><td><input id=\"title\" name=\"Title\" /></td></tr>" +
		          "<tr><td>Type: </td><td><input id=\"type\" name=\"Type\" /></td></tr>" +
		          "<tr><td>Alternate ID: </td><td><input id=\"alt_id\" name=\"AlternateId\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/products/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Title: </td><td><input id=\"title\" name=\"Title\" /></td></tr>" +
		          "<tr><td>Type: </td><td><input id=\"type\" name=\"Type\" /></td></tr>" +
		          "<tr><td>Alternate ID: </td><td><input id=\"alt_id\" name=\"AlternateId\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<p></p>" +
		          "<h2>Update</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/products/update\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Title: </td><td><input id=\"title\" name=\"Title\" /></td></tr>" +
		          "<tr><td>Type: </td><td><input id=\"type\" name=\"Type\" /></td></tr>" +
		          "<tr><td>Alternate ID: </td><td><input id=\"alt_id\" name=\"AlternateId\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Update\" /></td></tr>" +
		          "</table>" +
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/products/update\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Title: </td><td><input id=\"title\" name=\"Title\" /></td></tr>" +
		          "<tr><td>Type: </td><td><input id=\"type\" name=\"Type\" /></td></tr>" +
		          "<tr><td>Alternate ID: </td><td><input id=\"alt_id\" name=\"AlternateId\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Update\" /></td></tr>" +
		          "</table>" +
		          "</form>" +
		          "</div>"
            + HtmlConstants.PAGE_END;
    }
    @GET
    @Path("/statusadd")
    @Produces("text/html")
    public String statusForm() {
    	return HtmlConstants.PAGE_BEGIN +
		          "<h1>Archive Status</h1>" +
		          "<h2>Add</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/archivestatus/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Status: </td><td><input id=\"status\" name=\"Status\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/archivestatus/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Status: </td><td><input id=\"status\" name=\"Status\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          
		          "<h1>Certification Status</h1>" +
		          "<h2>Add</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/certificationstatus/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Status: </td><td><input id=\"status\" name=\"Status\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/certificationstatus/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Status: </td><td><input id=\"status\" name=\"Status\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          
		          "<h1>NSSDCA Status</h1>" +
		          "<h2>Add</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/nssdcastatus/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Nssdca Identifier: </td><td><input id=\"nssdca_id\" name=\"NssdcaIdentifier\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/nssdcastatus/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Nssdca Identifier: </td><td><input id=\"nssdca_id\" name=\"NssdcaIdentifier\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>"
            + HtmlConstants.PAGE_END;
    }
    @GET
    @Path("/releaseadd")
    @Produces("text/html")
    public String releaseForm() {
    	return HtmlConstants.PAGE_BEGIN +
		          "<h1>Release</h1>" +
		          "<h2>Add</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/releases/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Release Date Time: </td><td><input id=\"date\" name=\"Date\" /></td></tr>" +
		          "<tr><td>Name: </td><td><input id=\"name\" name=\"Name\" /></td></tr>" +
		          "<tr><td>Description: </td><td><input id=\"desc\" name=\"Desc\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/releases/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"LogicalIdentifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Release Date Time: </td><td><input id=\"date\" name=\"Date\" /></td></tr>" +
		          "<tr><td>Name: </td><td><input id=\"name\" name=\"Name\" /></td></tr>" +
		          "<tr><td>Description: </td><td><input id=\"desc\" name=\"Desc\" /></td></tr>" +
		          "<tr><td>Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Comment: </td><td><input id=\"comment\" name=\"Comment\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>"		          
            + HtmlConstants.PAGE_END;
    }
    
    @GET
    @Path("/deliveryadd")
    @Produces("text/html")
    public String deliveryForm() {
    	return HtmlConstants.PAGE_BEGIN +
		          "<h1>Delivery</h1>" +
		          "<h2>Add</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/delivery/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"Log_Identifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Name: </td><td><input id=\"name\" name=\"Name\" /></td></tr>" +
		          "<tr><td>Start Time: </td><td><input id=\"startT\" name=\"Start_Time\" /></td></tr>" +
		          "<tr><td>Stop Time: </td><td><input id=\"stopT\" name=\"Stop_Time\" /></td></tr>" +
		          "<tr><td>Source: </td><td><input id=\"src\" name=\"Source\" /></td></tr>" +
		          "<tr><td>Target: </td><td><input id=\"tgt\" name=\"Target\" /></td></tr>" +
		          "<tr><td>Due Date: </td><td><input id=\"dueD\" name=\"Due_Date\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/delivery/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"Log_Identifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Name: </td><td><input id=\"name\" name=\"Name\" /></td></tr>" +
		          "<tr><td>Start Time: </td><td><input id=\"startT\" name=\"Start_Time\" /></td></tr>" +
		          "<tr><td>Stop Time: </td><td><input id=\"stopT\" name=\"Stop_Time\" /></td></tr>" +
		          "<tr><td>Source: </td><td><input id=\"src\" name=\"Source\" /></td></tr>" +
		          "<tr><td>Target: </td><td><input id=\"tgt\" name=\"Target\" /></td></tr>" +
		          "<tr><td>Due Date: </td><td><input id=\"dueD\" name=\"Due_Date\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<p></p>" +
		          "<h2>Update</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/delivery/update\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"Log_Identifier\" /></td></tr>" +
		          "<tr><td>Deliveryl Identifier: </td><td><input id=\"delId\" name=\"Del_Identifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Name: </td><td><input id=\"name\" name=\"Name\" /></td></tr>" +
		          "<tr><td>Start Time: </td><td><input id=\"startT\" name=\"Start_Time\" /></td></tr>" +
		          "<tr><td>Stop Time: </td><td><input id=\"stopT\" name=\"Stop_Time\" /></td></tr>" +
		          "<tr><td>Source: </td><td><input id=\"src\" name=\"Source\" /></td></tr>" +
		          "<tr><td>Target: </td><td><input id=\"tgt\" name=\"Target\" /></td></tr>" +
		          "<tr><td>Due Date: </td><td><input id=\"dueD\" name=\"Due_Date\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Update\" /></td></tr>" +
		          "</table>" +
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/delivery/update\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>Logical Identifier: </td><td><input id=\"id\" name=\"Log_Identifier\" /></td></tr>" +
		          "<tr><td>Deliveryl Identifier: </td><td><input id=\"delId\" name=\"Del_Identifier\" /></td></tr>" +
		          "<tr><td>Version: </td><td><input id=\"ver\" name=\"Version\" /></td></tr>" +
		          "<tr><td>Name: </td><td><input id=\"name\" name=\"Name\" /></td></tr>" +
		          "<tr><td>Start Time: </td><td><input id=\"startT\" name=\"Start_Time\" /></td></tr>" +
		          "<tr><td>Stop Time: </td><td><input id=\"stopT\" name=\"Stop_Time\" /></td></tr>" +
		          "<tr><td>Source: </td><td><input id=\"src\" name=\"Source\" /></td></tr>" +
		          "<tr><td>Target: </td><td><input id=\"tgt\" name=\"Target\" /></td></tr>" +
		          "<tr><td>Due Date: </td><td><input id=\"dueD\" name=\"Due_Date\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Update\" /></td></tr>" +
		          "</table>" +
		          "</form>" +
		          "</div>"
            + HtmlConstants.PAGE_END;
    }
    
    @GET
    @Path("/useradd")
    @Produces("text/html")
    public String userForm() {
    	return HtmlConstants.PAGE_BEGIN +
		          "<h1>User</h1>" +
		          "<h2>Add</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/users/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>User Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>User Name: </td><td><input id=\"name\" name=\"Name\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/users/add\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>User Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>User Name: </td><td><input id=\"name\" name=\"Name\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<p></p>" +
		          "<h2>Update</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/users/update\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>User Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>User Name: </td><td><input id=\"name\" name=\"Name\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Update\" /></td></tr>" +
		          "</table>" +
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/users/update\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>User Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>User Name: </td><td><input id=\"name\" name=\"Name\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Update\" /></td></tr>" +
		          "</table>" +
		          "</form>" +
		          "</div>"
            + HtmlConstants.PAGE_END;
    }
    
    @GET
    @Path("/roleadd")
    @Produces("text/html")
    public String roleForm() {
    	return HtmlConstants.PAGE_BEGIN +
		          "<h1>User Role</h1>" +
		          "<h2>Add</h2>" +
		          "<h3>JSON</h3>" +
		          "<div>" +
		          "<form action=\"json/users/addrole\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>User Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Reference: </td><td><input id=\"ref\" name=\"Reference\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>" +
		          "<h3>XML</h3>" +
		          "<div>" +
		          "<form action=\"xml/users/addrole\" method=\"POST\">" +
		          "<table>" +
		          "<tr><td>User Email: </td><td><input id=\"email\" name=\"Email\" /></td></tr>" +
		          "<tr><td>Reference: </td><td><input id=\"ref\" name=\"Reference\" /></td></tr>" +
		          "<tr><td></td><td><input type=\"submit\" value=\"Add\" /></td></tr>" +
		          "</table>" +    
		          "</form>" +
		          "</div>"
            + HtmlConstants.PAGE_END;
    }
}

