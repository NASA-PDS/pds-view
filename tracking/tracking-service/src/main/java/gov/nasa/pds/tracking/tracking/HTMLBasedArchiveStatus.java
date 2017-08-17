package gov.nasa.pds.tracking.tracking;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import gov.nasa.pds.tracking.tracking.db.DBConnector;
import gov.nasa.pds.tracking.tracking.db.Status;
import gov.nasa.pds.tracking.tracking.utils.HtmlConstants;

@Path("/tracking/archivestatus")
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

    	Status aStatus;
		try {
			String tableName = null;
			
			if (appConsts.containsKey(ARCHIVE_STATUS_TABLE_NAME)) {
				tableName = appConsts.getProperty(ARCHIVE_STATUS_TABLE_NAME);
	        
				aStatus = new Status(tableName);
				
				List<Status> aStatuses = aStatus.getStatusOrderByVersion();
				
				logger.info("number of Archive Status: "  + aStatuses.size());
				
				Iterator<Status> itr = aStatuses.iterator();
	
				while(itr.hasNext()) {
					Status as = itr.next();
			         
			         sb.append("<tr>" +
			    			  "<td>" + as.getDate() + "</td>"+
				              "<td>" + as.getStatus() + "</td>" +
				              "<td>" + as.getVersion() + "</td>" +
				              "<td>" + as.getEmail() + "</td>" +
				              "<td>" + as.getComment() + "</td>" +
				              "<td>" + as.getLogIdentifier() + "</td>" +			              
			    			  "<tr>");
	
			    }
			}else{
				logger.error("Please check the properties file and give the table name of archive status.");
			}
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
    			
        
        sb.append("</table></div>");
 
        return HtmlConstants.PAGE_BEGIN + sb.toString() + HtmlConstants.PAGE_END;
    }
 
    @Path("{title}")
    @GET
    @Produces("text/html")
    public String ArchiveStatus(@PathParam("title") String title) {
    	
    	//Get all archive status for the title in the archive_status table
    	String realTitle = title.replaceAll("&", "/");
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>Archive status for " + realTitle + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	Status aStatus;
		try {
			String tableName = null;
			
			if (appConsts.containsKey(ARCHIVE_STATUS_TABLE_NAME)) {
				tableName = appConsts.getProperty(ARCHIVE_STATUS_TABLE_NAME);
				aStatus = new Status(tableName);
				
				List<Status> aStatuses = aStatus.getStatusOrderByVersion(realTitle);
				
				logger.info("number of Archive Status: "  + aStatuses.size());
				
				Iterator<Status> itr = aStatuses.iterator();
	
				while(itr.hasNext()) {
					Status as = itr.next();
			         
			         sb.append("<tr>" +
			    			  "<td>" + as.getDate() + "</td>"+
				              "<td>" + as.getStatus() + "</td>" +
				              "<td>" + as.getVersion() + "</td>" +
				              "<td>" + as.getEmail() + "</td>" +
				              "<td>" + as.getComment() + "</td>" +
				              "<td>" + as.getLogIdentifier() + "</td>" +			              
			    			  "<tr>");
	
			    }
			}else{
				logger.error("Please check the properties file and give the table name of archive status.");
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
