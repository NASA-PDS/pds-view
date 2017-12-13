package gov.nasa.pds.tracking.tracking.htmlinterfaces;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import gov.nasa.pds.tracking.tracking.db.ArchiveStatus;
import gov.nasa.pds.tracking.tracking.db.DBConnector;
import gov.nasa.pds.tracking.tracking.db.NssdcaStatus;
import gov.nasa.pds.tracking.tracking.utils.HtmlConstants;

@Path("html/nssdcastatus")
public class HTMLBasedNssdcaStatus  extends DBConnector {
	
	public HTMLBasedNssdcaStatus() throws ClassNotFoundException, SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static Logger logger = Logger.getLogger(HTMLBasedNssdcaStatus.class);
	
	private String tableTiltes =  "<tr align=\"center\">" +
			  "<td width=\"20%\"><b>Date</b></td>"+
              "<td width=\"10%\"><b>NSSDCA Identifier</b></td>" +
              "<td width=\"10%\"><b>Version</b></td>" +
              "<td width=\"10%\"><b>Email</b></td>" +
              "<td width=\"10%\"><b>Comment</b></td>" +              
              "<td width=\"10%\"><b>Logical Identifier</b></td>" +
			  "<tr>";
	
    @GET
    @Produces("text/html")
    public String defaultNssdcaStatus() {
 
    	//Get all archive status in the archive_status table
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>NSSDCA status</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	NssdcaStatus nStatus;
		try {
			nStatus = new NssdcaStatus();
			
			List<NssdcaStatus> nStatuses = nStatus.getNssdcaStatusOrderByVersion();
			
			logger.info("number of NSSDCA Status: "  + nStatuses.size());
			
			Iterator<NssdcaStatus> itr = nStatuses.iterator();

			while(itr.hasNext()) {
				NssdcaStatus ns = itr.next();
		         
		         sb.append("<tr>" +
		    			  "<td>" + ns.getDate() + "</td>"+
			              "<td>" + ns.getNssdca() + "</td>" +
			              "<td>" + ns.getVersion() + "</td>" +
			              "<td>" + ns.getEmail() + "</td>" +
			              "<td>" + ns.getComment() + "</td>" +
			              "<td>" + ns.getLogIdentifier() + "</td>" +			              
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
    public String NssdcaStatus(@PathParam("id") String id, @PathParam("version") String version) {
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>Nssdca Status for the product: " + id + ", " + version + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	NssdcaStatus nStatus;
		try {
				nStatus = new NssdcaStatus();
				
				List<NssdcaStatus> nStatuses = nStatus.getNssdcaStatusList(id, version);
				
				logger.info("number of Nssdca Status: "  + nStatuses.size());
				
				Iterator<NssdcaStatus> itr = nStatuses.iterator();
	
				while(itr.hasNext()) {
					NssdcaStatus ns = itr.next();
			         
					sb.append("<tr>" +
			    			  "<td>" + ns.getDate() + "</td>"+
				              "<td>" + ns.getNssdca() + "</td>" +
				              "<td>" + ns.getVersion() + "</td>" +
				              "<td>" + ns.getEmail() + "</td>" +
				              "<td>" + ns.getComment() + "</td>" +
				              "<td>" + ns.getLogIdentifier() + "</td>" +			              
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
    public String NssdcaStatus(@PathParam("title") String title) {
    	
    	//Get all archive status for the title in the archive_status table
    	//String realTitle = title.replaceAll("&", "/");
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>NSSDCA status for " + title + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	NssdcaStatus nStatus;
		try {
			
			nStatus = new NssdcaStatus();
			
			List<NssdcaStatus> nStatuses = nStatus.getNssdcaStatusOrderByVersion(title);
			
			logger.info("number of NSSDCA Status: "  + nStatuses.size());
			
			Iterator<NssdcaStatus> itr = nStatuses.iterator();

			while(itr.hasNext()) {
				NssdcaStatus ns = itr.next();
		         
		         sb.append("<tr>" +
		    			  "<td>" + ns.getDate() + "</td>"+
			              "<td>" + ns.getNssdca() + "</td>" +
			              "<td>" + ns.getVersion() + "</td>" +
			              "<td>" + ns.getEmail() + "</td>" +
			              "<td>" + ns.getComment() + "</td>" +
			              "<td>" + ns.getLogIdentifier() + "</td>" +			              
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
