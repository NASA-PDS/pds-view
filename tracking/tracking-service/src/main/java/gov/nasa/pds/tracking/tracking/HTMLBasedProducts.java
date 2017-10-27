package gov.nasa.pds.tracking.tracking;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import gov.nasa.pds.tracking.tracking.db.Product;
import gov.nasa.pds.tracking.tracking.utils.HtmlConstants;
 
@Path("html/products")
public class HTMLBasedProducts {
	
	public static Logger logger = Logger.getLogger(HTMLBasedProducts.class);
	
    @GET
    @Produces("text/html")
    public String defaultProducts() {
 
    	//Get all Products in the product table
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +	
    			  "<h2>Products</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 10\" >" +
    			  "<tr align=\"center\">" +
    			  "<td width=\"20%\"><b>Title</b></td>"+
	              "<td width=\"10%\"><b>Version</b></td>" +
	              "<td width=\"10%\"><b>Logical Identifier</b></td>" +
	              "<td width=\"10%\"><b>Type</b></td>" +
	              "<td width=\"10%\"><b>Alternate</b></td>" +

    			  "<tr>");

		Product prod;
		try {
			prod = new Product();
			List<Product> prods = prod.getProductsOrderByTitle();
			logger.info("number of products: "  + prods.size());
			Iterator<Product> itr = prods.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         Product p = itr.next();
		         logger.debug("Product " + count + ":\n " + p.getIdentifier() + " : " + p.getTitle());
		         sb.append("<tr>" +
		    			  "<td><table><tr>" + p.getTitle() + "</br></br>" +
		    			  					"<a href=\"../html/delivery/" + p.getIdentifier() + "/" + p.getVersion() + "\">Deliveries</a></br>" +
		    			  					"<a href=\"../html/archivestatus/" + p.getIdentifier() + "/" + p.getVersion() + "/false\">Archive Status</a></br>" +
		    			  					"<a href=\"../html/archivestatus/" + p.getIdentifier() + "/" + p.getVersion() + "/true\">Latest Archive Status</a></br>" +
		    			  					"<a href=\"../html/certificationstatus/" + p.getIdentifier() + "/" + p.getVersion() + "/false\">Certification Status</a></br>" +
		    			  					"<a href=\"../html/certificationstatus/" + p.getIdentifier() + "/" + p.getVersion() + "/true\">Latest Certification Status</a></br>" +
		    			  					"<a href=\"../html/nssdcastatus/" + p.getIdentifier() + "/" + p.getVersion() + "\">NSSDCA Status</a>" +
		    			  					"</tr></table></td>"+
			              "<td>" + p.getVersion() + "</td>" +
			              "<td>" + p.getIdentifier() + "</td>" +
			              "<td>" + p.getType() + "</td>" +
			              "<td>" + p.getAlternate() + "</td>" +
		    			  "<tr>");
		         count++;
		    }

			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
    			
        
        sb.append("</table></div>");
 
        return HtmlConstants.PAGE_BEGIN + sb.toString() + HtmlConstants.PAGE_END;
    }
 
    @Path("{Delivery}")
    @GET
    @Produces("text/html")
    public String products(@PathParam("Delivery") boolean delivery) {
    	//Get a list of products that have associated deliveries.
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +	
    			  "<h2>Products that have associated deliveries</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 10\" >" +
    			  "<tr align=\"center\">" +
    			  "<td width=\"20%\"><b>Title</b></td>"+
	              "<td width=\"10%\"><b>Version</b></td>" +
	              "<td width=\"10%\"><b>Logical Identifier</b></td>" +
	              "<td width=\"10%\"><b>Type</b></td>" +
	              "<td width=\"10%\"><b>Alternate</b></td>" +

    			  "<tr>");

		Product prod;
		try {
			prod = new Product();
			List<Product> prods = prod.getProductsAssociatedDeliveriesOrderByTitle();
			logger.info("number of products: "  + prods.size());
			Iterator<Product> itr = prods.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         Product p = itr.next();
		         logger.debug("Product " + count + ":\n " + p.getIdentifier() + " : " + p.getTitle());
		         sb.append("<tr>" +
		    			  "<td><table><tr>" + p.getTitle() + "</br></br>" +
		    			  					"<a href=\"../../html/delivery/" + p.getIdentifier() + "/" + p.getVersion() + "\">Deliveries - HTML </a></br>" +
		    			  					"<a href=\"../../json/delivery/" + p.getIdentifier() + "/" + p.getVersion() + "\">Deliveries - JSON</a></br>" +
		    			  					"<a href=\"../../html/archivestatus/" + p.getIdentifier() + "/" + p.getVersion() + "/false\">Archive Status</a></br>" +
		    			  					"<a href=\"../../html/archivestatus/" + p.getIdentifier() + "/" + p.getVersion() + "/true\">Latest Archive Status</a></br>" +
		    			  					"<a href=\"../../html/certificationstatus/" + p.getIdentifier() + "/" + p.getVersion() + "/false\">Certification Status</a></br>" +
		    			  					"<a href=\"../../html/certificationstatus/" + p.getIdentifier() + "/" + p.getVersion() + "/true\">Latest Certification Status</a></br>" +
		    			  					"<a href=\"../../html/nssdcastatus/" + p.getIdentifier() + "/" + p.getVersion() + "\">NSSDCA Status</a>" +
		    			  					"</tr></table></td>"+
			              "<td>" + p.getVersion() + "</td>" +
			              "<td>" + p.getIdentifier() + "</td>" +
			              "<td>" + p.getType() + "</td>" +
			              "<td>" + p.getAlternate() + "</td>" +
		    			  "<tr>");
		         count++;
		    }

			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
    			
        
        sb.append("</table></div>");
 
        return HtmlConstants.PAGE_BEGIN + sb.toString() + HtmlConstants.PAGE_END;
    }
 
}
