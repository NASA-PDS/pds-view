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
import gov.nasa.pds.tracking.tracking.db.CertificationStatus;
import gov.nasa.pds.tracking.tracking.utils.HtmlConstants;

@Path("html/certificationstatus")
public class HTMLBasedCertificationStatus  extends DBConnector {
	
	public HTMLBasedCertificationStatus() throws ClassNotFoundException, SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static Logger logger = Logger.getLogger(HTMLBasedCertificationStatus.class);
	
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
    public String defaultCertificationStatus() {
 
    	//Get all archive status in the archive_status table
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>Certification status</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	CertificationStatus cStatus;
		try {
			cStatus = new CertificationStatus();
				
				List<CertificationStatus> cStatuses = cStatus.getCertificationStatusOrderByVersion();
				
				logger.info("number of Certification Status: "  + cStatuses.size());
				
				Iterator<CertificationStatus> itr = cStatuses.iterator();
	
				while(itr.hasNext()) {
					CertificationStatus cs = itr.next();
			         
			         sb.append("<tr>" +
			    			  "<td>" + cs.getDate() + "</td>"+
				              "<td>" + cs.getStatus() + "</td>" +
				              "<td>" + cs.getVersion() + "</td>" +
				              "<td>" + cs.getEmail() + "</td>" +
				              "<td>" + cs.getComment() + "</td>" +
				              "<td>" + cs.getLogIdentifier() + "</td>" +			              
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
    public String CertificationStatus(@PathParam("id") String id, @PathParam("version") String version, @PathParam("latest") boolean latest) {
    	
    	String headerStart = "";
    	if (latest){
    		headerStart = "The latest of ";
    	}

    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>" + headerStart + "Certification status for the product: \n" + id + ", " + version + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	CertificationStatus cStatus;
		try {
				cStatus = new CertificationStatus();
				
				List<CertificationStatus> cStatuses = new ArrayList<CertificationStatus>();
				
				if (latest) {
					cStatus = cStatus.getLatestCertificationStatus(id, version);
					if (cStatus != null)
					cStatuses.add(cStatus);
				}else{
					cStatuses = cStatus.getCertificationStatusList(id, version);
				}
				
				
				logger.info("number of Certification Status: "  + cStatuses.size());
				
				Iterator<CertificationStatus> itr = cStatuses.iterator();
	
				while(itr.hasNext()) {
					CertificationStatus cs = itr.next();
			         
					sb.append("<tr>" +
			    			  "<td>" + cs.getDate() + "</td>"+
				              "<td>" + cs.getStatus() + "</td>" +
				              "<td>" + cs.getVersion() + "</td>" +
				              "<td>" + cs.getEmail() + "</td>" +
				              "<td>" + cs.getComment() + "</td>" +
				              "<td>" + cs.getLogIdentifier() + "</td>" +			              
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
    @Path("{title : (.+)?}")
    @GET
    @Produces("text/html")
    public String CertificationStatus(@PathParam("title") String title) {
    	
    	//Get all archive status for the title in the archive_status table

    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>Certification status for " + title + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	CertificationStatus cStatus;
		try {
			cStatus = new CertificationStatus();
			
			List<CertificationStatus> cStatuses = cStatus.getCertificationStatusOrderByVersion(title);
			
			logger.info("number of Certification Status: "  + cStatuses.size());
			
			Iterator<CertificationStatus> itr = cStatuses.iterator();

			while(itr.hasNext()) {
				CertificationStatus cs = itr.next();
		         
		         sb.append("<tr>" +
		    			  "<td>" + cs.getDate() + "</td>"+
			              "<td>" + cs.getStatus() + "</td>" +
			              "<td>" + cs.getVersion() + "</td>" +
			              "<td>" + cs.getEmail() + "</td>" +
			              "<td>" + cs.getComment() + "</td>" +
			              "<td>" + cs.getLogIdentifier() + "</td>" +			              
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
