package gov.nasa.pds.tracking.tracking.htmlinterfaces;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import gov.nasa.pds.tracking.tracking.db.DBConnector;
import gov.nasa.pds.tracking.tracking.db.ArchiveStatus;
import gov.nasa.pds.tracking.tracking.utils.HtmlConstants;

@Path("html/archivestatus")
public class HTMLBasedArchiveStatus  extends DBConnector {
	
	public HTMLBasedArchiveStatus() throws ClassNotFoundException, SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static Logger logger = Logger.getLogger(HTMLBasedArchiveStatus.class);
	
	private String tableTiltes =  "<tr align=\"center\">" +
			  "<td width=\"20%\"><b>Date</b></td>"+
              "<td width=\"10%\"><b>Status</b></td>" +
              "<td width=\"10%\"><b>Version</b></td>" +
              "<td width=\"10%\"><b>Email</b></td>" +
              "<td width=\"10%\"><b>Comment</b></td>" +              
              "<td width=\"10%\"><b>Logical Identifier</b></td>" +
			  "<tr>";
	
    /**
     * @return
     */
    @GET
    @Produces("text/html")
    public String defaultArchiveStatus() {
 
    	//Get all archive status in the archive_status table
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>Archive status</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	ArchiveStatus aStatus;
		try {	        
				aStatus = new ArchiveStatus();
				
				List<ArchiveStatus> aStatuses = aStatus.getArchiveStatusOrderByVersion();
				
				logger.info("number of Archive Status: "  + aStatuses.size());
				
				Iterator<ArchiveStatus> itr = aStatuses.iterator();
	
				while(itr.hasNext()) {
					ArchiveStatus as = itr.next();
			         
			         sb.append("<tr>" +
			    			  "<td>" + as.getDate() + "</td>"+
				              "<td>" + as.getStatus() + "</td>" +
				              "<td>" + as.getVersion() + "</td>" +
				              "<td>" + as.getEmail() + "</td>" +
				              "<td>" + as.getComment() + "</td>" +
				              "<td>" + as.getLogIdentifier() + "</td>" +			              
			    			  "<tr>");
	
			    }
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
    			
        
        sb.append("</table></div>");
 
        return HtmlConstants.PAGE_BEGIN + sb.toString() + HtmlConstants.PAGE_END;
    }
 
    /**
     * @param id
     * @param version
     * @return
     */
    @Path("{id : (.+)?}/{version : (.+)?}")
    @GET
    @Produces("text/html")
    public String ArchiveStatus(@PathParam("id") String id, @PathParam("version") String version) {
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>Archive status for the product: " + id + ", " + version + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	ArchiveStatus aStatus;
		try {
				aStatus = new ArchiveStatus();
				
				List<ArchiveStatus> aStatuses = aStatus.getArchiveStatusList(id, version);
				
				logger.info("number of Archive Status: "  + aStatuses.size());
				
				Iterator<ArchiveStatus> itr = aStatuses.iterator();
	
				while(itr.hasNext()) {
					ArchiveStatus as = itr.next();
			         
			         sb.append("<tr>" +
			    			  "<td>" + as.getDate() + "</td>"+
				              "<td>" + as.getStatus() + "</td>" +
				              "<td>" + as.getVersion() + "</td>" +
				              "<td>" + as.getEmail() + "</td>" +
				              "<td>" + as.getComment() + "</td>" +
				              "<td>" + as.getLogIdentifier() + "</td>" +			              
			    			  "<tr>");
				}
	
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
    			
        
        sb.append("</table></div>");
 
        return HtmlConstants.PAGE_BEGIN + sb.toString() + HtmlConstants.PAGE_END;
    }
    
    /**
     * @param id
     * @param version
     * @param latest
     * @return
     */
    @Path("{id : (.+)?}/{version : (.+)?}/{latest}")
    @GET
    @Produces("text/html")
    public String ArchiveStatus(@PathParam("id") String id, @PathParam("version") String version, @PathParam("latest") boolean latest) {
    	
    	String headerStart = "";
    	if (latest){
    		headerStart = "The latest of ";
    	}

    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>" + headerStart + "Archive status for the product: \n" + id + ", " + version + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	ArchiveStatus aStatus;
		try {
				aStatus = new ArchiveStatus();
				
				List<ArchiveStatus> aStatuses = new ArrayList<ArchiveStatus>();
				
				if (latest) {
					aStatus = aStatus.getLatestArchiveStatus(id, version);
					if (aStatus != null)
					aStatuses.add(aStatus);
				}else{
					aStatuses = aStatus.getArchiveStatusList(id, version);
				}
				
				
				logger.info("number of Archive Status: "  + aStatuses.size());
				
				Iterator<ArchiveStatus> itr = aStatuses.iterator();
	
				while(itr.hasNext()) {
					ArchiveStatus as = itr.next();
			         
			         sb.append("<tr>" +
			    			  "<td>" + as.getDate() + "</td>"+
				              "<td>" + as.getStatus() + "</td>" +
				              "<td>" + as.getVersion() + "</td>" +
				              "<td>" + as.getEmail() + "</td>" +
				              "<td>" + as.getComment() + "</td>" +
				              "<td>" + as.getLogIdentifier() + "</td>" +			              
			    			  "<tr>");
				}
	
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
    			
        
        sb.append("</table></div>");
 
        return HtmlConstants.PAGE_BEGIN + sb.toString() + HtmlConstants.PAGE_END;
    }
    
    /**
     * @param title
     * @return
     */
    @Path("{title : (.+)?}")
    @GET
    @Produces("text/html")
    public String ArchiveStatus(@PathParam("title") String title) {
    	    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>Archive status for " + title + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	ArchiveStatus aStatus;
		try {
				aStatus = new ArchiveStatus();
				
				List<ArchiveStatus> aStatuses = aStatus.getArchiveStatusOrderByVersion(title);
				
				logger.info("number of Archive Status: "  + aStatuses.size());
				
				Iterator<ArchiveStatus> itr = aStatuses.iterator();
	
				while(itr.hasNext()) {
					ArchiveStatus as = itr.next();
			         
			         sb.append("<tr>" +
			    			  "<td>" + as.getDate() + "</td>"+
				              "<td>" + as.getStatus() + "</td>" +
				              "<td>" + as.getVersion() + "</td>" +
				              "<td>" + as.getEmail() + "</td>" +
				              "<td>" + as.getComment() + "</td>" +
				              "<td>" + as.getLogIdentifier() + "</td>" +			              
			    			  "<tr>");
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
