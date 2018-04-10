package gov.nasa.pds.tracking.tracking.htmlinterfaces;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import gov.nasa.pds.tracking.tracking.db.DBConnector;
import gov.nasa.pds.tracking.tracking.db.Doi;
import gov.nasa.pds.tracking.tracking.utils.HtmlConstants;

@Path("html/doi")
public class HTMLBasedDOI  extends DBConnector {
	
	public HTMLBasedDOI() throws ClassNotFoundException, SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static Logger logger = Logger.getLogger(HTMLBasedDOI.class);

	private String tableTiltes =  "<tr align=\"center\">" +
			  "<td width=\"20%\"><b>Date</b></td>"+
              "<td width=\"10%\"><b>DOI</b></td>" +
              "<td width=\"10%\"><b>URL</b></td>" +
              "<td width=\"10%\"><b>Version</b></td>" +
              "<td width=\"10%\"><b>Email</b></td>" +
              "<td width=\"10%\"><b>Comment</b></td>" +              
              "<td width=\"10%\"><b>Logical Identifier</b></td>" +
			  "</tr>";
	
    @GET
    @Produces("text/html")
    public String defaultDOI() {
 
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>DOI</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	Doi doi;
		try {
			doi = new Doi();
			
			List<Doi> dois = doi.getDOIList();
			
			logger.info("number of DOIs: "  + dois.size());
			
			Iterator<Doi> itr = dois.iterator();

			while(itr.hasNext()) {
				Doi d = itr.next();
		         
		         sb.append("<tr>" +
		    			  "<td>" + d.getDate() + "</td>"+
			              "<td>" + d.getDoi() + "</td>" +
			              "<td>" + d.getUrl() + "</td>" +
			              "<td>" + d.getVersion() + "</td>" +
			              "<td>" + d.getEmail() + "</td>" +
			              "<td>" + d.getComment() + "</td>" +
			              "<td>" + d.getLog_identifier() + "</td>" +			              
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
     * @return
     */
    @Path("{id : (.+)?}/{version : (.+)?}")
    @GET
    @Produces("text/html")
    public String Doi(@PathParam("id") String id, @PathParam("version") String version) {
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +
    			"  <h2>DOI</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 12\" >" +
    			  tableTiltes);

    	Doi doi;
		try {
			doi = new Doi();
			
			List<Doi> dois = doi.getDOIList(id, version);
			
			logger.info("number of DOIs: "  + dois.size());
			
			Iterator<Doi> itr = dois.iterator();

			while(itr.hasNext()) {
				Doi d = itr.next();
		         
		         sb.append("<tr>" +
		    			  "<td>" + d.getDate() + "</td>"+
			              "<td>" + d.getDoi() + "</td>" +
			              "<td>" + d.getUrl() + "</td>" +
			              "<td>" + d.getVersion() + "</td>" +
			              "<td>" + d.getEmail() + "</td>" +
			              "<td>" + d.getComment() + "</td>" +
			              "<td>" + d.getLog_identifier() + "</td>" +			              
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
