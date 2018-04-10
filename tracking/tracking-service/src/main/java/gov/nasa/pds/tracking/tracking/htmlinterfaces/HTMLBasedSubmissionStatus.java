package gov.nasa.pds.tracking.tracking.htmlinterfaces;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import gov.nasa.pds.tracking.tracking.db.SubmissionStatus;
import gov.nasa.pds.tracking.tracking.utils.HtmlConstants;
 
@Path("html/submissionstatus")
public class HTMLBasedSubmissionStatus {
	
	public static Logger logger = Logger.getLogger(HTMLBasedSubmissionStatus.class);
	
    @GET
    @Produces("text/html")
    public String defaultSubmissionStatus() {

    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +	
    			  "<h2>Submission Status</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 10\" >" +
    			  "<tr align=\"center\">" +
    			  "<td><b>" + SubmissionStatus.DEL_IDENTIFIERCOLUME + "</b></td>"+
	              "<td><b>" + SubmissionStatus.SUBMISSIONDATECOLUME + "</b></td>" +
	              "<td><b>" + SubmissionStatus.STATUSDATECOLUME + "</b></td>"+
	              "<td><b>" + SubmissionStatus.STATUSCOLUME + "</b></td>" +
	              "<td><b>" + SubmissionStatus.EMAILCOLUME + "</b></td>"+
	              "<td><b>" + SubmissionStatus.COMMENTCOLUME + "</b></td>" +
    			  "</tr>");

    	SubmissionStatus subMStatus;
		try {
			subMStatus = new SubmissionStatus();
			List<SubmissionStatus> subMStatuses = subMStatus.getSubmissionStatus();
			logger.info("number of Submission Status: "  + subMStatuses.size());
			Iterator<SubmissionStatus> itr = subMStatuses.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
				SubmissionStatus s = itr.next();
		         logger.debug("Submission Status " + count + ":\n " + s.getStatus() + " : " + s.getSubmissionDate());
				 
				 sb.append("<tr>" +
		    			  "<td>" + s.getDel_identifier() + "</td>" +		    			  					
			              "<td>" + s.getSubmissionDate() + "</td>" +
			              "<td>" + s.getStatusDate() + "</td>" +
			              "<td>" + s.getStatus() + "</td>" +		    			  					
			              "<td>" + s.getEmail() + "</td>" +
			              "<td>" + s.getComment() != null ? s.getComment(): "" + "</td>" +
			              "</tr>");
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
    
    @Path("{id}")
    @GET
    @Produces("text/html")
	public String getSubmissionStatus(@PathParam("id") String id) {
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +	
    			  "<h2>Submission Status</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 10\" >" +
    			  "<tr align=\"center\">" +
    			  "<td><b>" + SubmissionStatus.DEL_IDENTIFIERCOLUME + "</b></td>"+
	              "<td><b>" + SubmissionStatus.SUBMISSIONDATECOLUME + "</b></td>" +
	              "<td><b>" + SubmissionStatus.STATUSDATECOLUME + "</b></td>"+
	              "<td><b>" + SubmissionStatus.STATUSCOLUME + "</b></td>" +
	              "<td><b>" + SubmissionStatus.EMAILCOLUME + "</b></td>"+
	              "<td><b>" + SubmissionStatus.COMMENTCOLUME + "</b></td>" +
    			  "</tr>");

    	SubmissionStatus subMStatus;
		try {
			subMStatus = new SubmissionStatus();
			List<SubmissionStatus> subMStatuses = subMStatus.getDeliveryStatus(id);
			logger.info("number of Submission Status: "  + subMStatuses.size());
			Iterator<SubmissionStatus> itr = subMStatuses.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
				SubmissionStatus s = itr.next();
		         logger.debug("Submission Status " + count + ":\n " + s.getStatus() + " : " + s.getSubmissionDate());
				 
				 sb.append("<tr>" +
		    			  "<td>" + s.getDel_identifier() + "</td>" +		    			  					
			              "<td>" + s.getSubmissionDate() + "</td>" +
			              "<td>" + s.getStatusDate() + "</td>" +
			              "<td>" + s.getStatus() + "</td>" +		    			  					
			              "<td>" + s.getEmail() + "</td>" +
			              "<td>" + s.getComment() != null ? s.getComment(): "" + "</td>" +
			              "</tr>");
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
