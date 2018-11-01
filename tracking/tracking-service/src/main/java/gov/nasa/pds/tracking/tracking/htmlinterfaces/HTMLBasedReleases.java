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
import gov.nasa.pds.tracking.tracking.db.Releases;
import gov.nasa.pds.tracking.tracking.db.ReleasesDao;
import gov.nasa.pds.tracking.tracking.utils.HtmlConstants;

@Path("html/releases")
public class HTMLBasedReleases  extends DBConnector {
	
	public HTMLBasedReleases() throws ClassNotFoundException, SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static Logger logger = Logger.getLogger(HTMLBasedReleases.class);
 	
	private String tableTiltes =  "<tr align=\"center\">" +
			  "<td width=\"20%\"><b>Date</b></td>"+
              "<td width=\"10%\"><b>Name</b></td>" +
              "<td width=\"10%\"><b>Description</b></td>" +
              "<td width=\"10%\"><b>Version</b></td>" +
              "<td width=\"10%\"><b>Email</b></td>" +
              "<td width=\"10%\"><b>Comment</b></td>" +              
              "<td width=\"10%\"><b>Logical Identifier</b></td>" +
			  "</tr>";
	
    /**
     * @return
     */
    @GET
    @Produces("text/html")
    public String defaultReleases() {
 
    	//Get all Releases in the releases table
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>Releases</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	ReleasesDao rel;
		try {	        
				rel = new ReleasesDao();
				
				List<Releases> rels = rel.getReleasesList();
				
				logger.info("number of releases: "  + rels.size());
				
				Iterator<Releases> itr = rels.iterator();
	
				while(itr.hasNext()) {
					Releases r = itr.next();
			         
			         sb.append("<tr>" +
			    			  "<td>" + r.getDate() + "</td>"+
			    			  "<td>" + r.getName() + "</td>"+
			    			  "<td>" + r.getDescription() + "</td>"+				              
				              "<td>" + r.getVersion() + "</td>" +
				              "<td>" + r.getEmail() + "</td>" +
				              "<td>" + r.getComment() + "</td>" +
				              "<td>" + r.getLogIdentifier() + "</td>" +			              
			    			  "</tr>");
	
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
    public String Releases(@PathParam("id") String id, @PathParam("version") String version, @PathParam("latest") boolean latest) {
    	
    	String headerStart = "";
    	if (latest){
    		headerStart = "The latest of ";
    	}

    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>" + headerStart + "Releases for the product: \n" + id + ", " + version + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);
    	
    	ReleasesDao relD;
    	Releases rel;
		try {	        
				relD = new ReleasesDao();
				
				List<Releases> rels = new ArrayList<Releases>();
				
				if (latest) {
					rel = relD.getLatestReleases(id, version);
					if (rel != null)
					rels.add(rel);
				}else{
					rels = relD.getReleasesList(id, version);
				}
				
				logger.info("number of releases: "  + rels.size());
				
				Iterator<Releases> itr = rels.iterator();
	
				while(itr.hasNext()) {
					Releases r = itr.next();
			         
			         sb.append("<tr>" +
			    			  "<td>" + r.getDate() + "</td>"+
			    			  "<td>" + r.getName() + "</td>"+
			    			  "<td>" + r.getDescription() + "</td>"+				              
				              "<td>" + r.getVersion() + "</td>" +
				              "<td>" + r.getEmail() + "</td>" +
				              "<td>" + r.getComment() + "</td>" +
				              "<td>" + r.getLogIdentifier() + "</td>" +			              
			    			  "</tr>");	
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
