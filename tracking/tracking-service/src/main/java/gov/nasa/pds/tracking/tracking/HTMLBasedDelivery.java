package gov.nasa.pds.tracking.tracking;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import gov.nasa.pds.tracking.tracking.db.Delivery;
import gov.nasa.pds.tracking.tracking.utils.HtmlConstants;
 
@Path("html/delivery")
public class HTMLBasedDelivery {
	public static Logger logger = Logger.getLogger(HTMLBasedProducts.class);
		
	private String tableTiltes =  "<tr align=\"center\">" +
			  "<td width=\"20%\"><b>Name</b></td>"+
              "<td width=\"10%\"><b>Start Date</b></td>" +
              "<td width=\"10%\"><b>Stop Date</b></td>" +
              "<td width=\"10%\"><b>Version</b></td>" +
              "<td width=\"10%\"><b>Source</b></td>" +              
              "<td width=\"10%\"><b>Target</b></td>" +
              "<td width=\"10%\"><b>Due Date</b></td>" +
              "<td width=\"10%\"><b>Logical Identifier</b></td>" +
              "<td width=\"10%\"><b>Delivery Identifier</b></td>" +
			  "<tr>";
	
    @GET
    @Produces("text/html")
    public String defaultDelivery() {
 
    	//Get all deliveries in the delivery table
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>Deliveries</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 14\" >" +
    			  tableTiltes);

    	Delivery del;
		try {
			del = new Delivery();
			List<Delivery> dels = del.getDeliveries();
			logger.info("number of deliveris: "  + dels.size());
			
			Iterator<Delivery> itr = dels.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         Delivery d = itr.next();
		         logger.debug("Deliveries " + count + ":\n " + d.getLogIdentifier() + " : " + d.getName());
		         sb.append("<tr>" +
		    			  "<td>" + d.getName() + "</td>"+
			              "<td>" + d.getStart() + "</td>" +
			              "<td>" + d.getStop() + "</td>" +
			              "<td>" + d.getVersion() + "</td>" +
			              "<td>" + d.getSource() + "</td>" +
			              "<td>" + d.getTarget() + "</td>" +
			              "<td>" + d.getDueDate() + "</td>" +
			              "<td>" + d.getLogIdentifier() + "</td>" +
			              "<td>" + d.getDelIdentifier() + "</td>" +
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
 
    @Path("{id : (.+)?}/{version : (.+)?}")
    @GET
    @Produces("text/html")
    public String deliveries(@PathParam("id") String id, @PathParam("version") String version) {
    	
    	
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>Deliveries for " + id + ", " + version + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 14\" >" +
    			  tableTiltes);

    	Delivery del;
		try {
			del = new Delivery();
			List<Delivery> dels = del.getProductDeliveries(id, version);
			logger.info("number of deliveris: "  + dels.size());
			Iterator<Delivery> itr = dels.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         Delivery d = itr.next();
		         logger.debug("Deliveries " + count + ":\n " + d.getLogIdentifier() + " : " + d.getName());
		         sb.append("<tr>" +
		    			  "<td>" + d.getName() + "</td>"+
			              "<td>" + d.getStart() + "</td>" +
			              "<td>" + d.getStop() + "</td>" +
			              "<td>" + d.getVersion() + "</td>" +
			              "<td>" + d.getSource() + "</td>" +
			              "<td>" + d.getTarget() + "</td>" +
			              "<td>" + d.getDueDate() + "</td>" +
			              "<td>" + d.getLogIdentifier() + "</td>" +
			              "<td>" + d.getDelIdentifier() + "</td>" +
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
    
    @Path("{title : (.+)?}")
    @GET
    @Produces("text/html")
    public String deliveries(@PathParam("title") String title) {
    	
    	//Get all deliveries for the title in the delivery table
    	
    	logger.debug("Title: "  + title);
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>Deliveries for " + title + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 14\" >" +
    			  tableTiltes);

    	Delivery del;
		try {
			del = new Delivery();
			List<Delivery> dels = del.getDeliveriesOrderByDueDate(title);
			logger.info("number of deliveris: "  + dels.size());
			Iterator<Delivery> itr = dels.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         Delivery d = itr.next();
		         logger.debug("Deliveris " + count + ":\n " + d.getLogIdentifier() + " : " + d.getName());
		         sb.append("<tr>" +
		    			  "<td>" + d.getName() + "</td>"+
			              "<td>" + d.getStart() + "</td>" +
			              "<td>" + d.getStop() + "</td>" +
			              "<td>" + d.getVersion() + "</td>" +
			              "<td>" + d.getSource() + "</td>" +
			              "<td>" + d.getTarget() + "</td>" +
			              "<td>" + d.getDueDate() + "</td>" +
			              "<td>" + d.getLogIdentifier() + "</td>" +
			              "<td>" + d.getDelIdentifier() + "</td>" +
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
