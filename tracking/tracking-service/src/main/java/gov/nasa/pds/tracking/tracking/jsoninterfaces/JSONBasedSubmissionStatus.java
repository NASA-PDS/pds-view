/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.jsoninterfaces;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import gov.nasa.pds.tracking.tracking.db.SubmissionStatus;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/submissionstatusold")
public class JSONBasedSubmissionStatus {

	public static Logger logger = Logger.getLogger(JSONBasedSubmissionStatus.class);

	@GET
    @Produces("application/json")
    public Response defaultSubmissionStatus() throws JSONException {
 
        JSONObject jsonSubMStatuses = new JSONObject();
        
        JSONObject jsonSubMStatus = new JSONObject();
        
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
		         
		         jsonSubMStatus = new JSONObject();					
				 jsonSubMStatus.put(SubmissionStatus.DEL_IDENTIFIERCOLUME, s.getDel_identifier());
				 jsonSubMStatus.put(SubmissionStatus.SUBMISSIONDATECOLUME, s.getSubmissionDate());
				 jsonSubMStatus.put(SubmissionStatus.STATUSDATECOLUME, s.getStatusDate());
				 jsonSubMStatus.put(SubmissionStatus.STATUSCOLUME, s.getStatus());
				 jsonSubMStatus.put(SubmissionStatus.EMAILCOLUME, s.getEmail());
				 jsonSubMStatus.put(SubmissionStatus.COMMENTCOLUME, s.getComment() != null ? s.getComment(): "");
				
				 jsonSubMStatuses.append("Submission Status", jsonSubMStatus);
		         count++;
		    }
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonSubMStatuses.toString(4);
        return Response.status(200).entity(result).build();
    }

	@Path("{id}")
    @GET
    @Produces("application/json")
	public Response getSubmissionStatus(@PathParam("id") String id)  throws JSONException {	
		JSONObject jsonSubMStatuses = new JSONObject();
        
        JSONObject jsonSubMStatus = new JSONObject();
        
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
		         
		         jsonSubMStatus = new JSONObject();					
				 jsonSubMStatus.put(SubmissionStatus.DEL_IDENTIFIERCOLUME, s.getDel_identifier());
				 jsonSubMStatus.put(SubmissionStatus.SUBMISSIONDATECOLUME, s.getSubmissionDate());
				 jsonSubMStatus.put(SubmissionStatus.STATUSDATECOLUME, s.getStatusDate());
				 jsonSubMStatus.put(SubmissionStatus.STATUSCOLUME, s.getStatus());
				 jsonSubMStatus.put(SubmissionStatus.EMAILCOLUME, s.getEmail());
				 jsonSubMStatus.put(SubmissionStatus.COMMENTCOLUME, s.getComment() != null ? s.getComment(): "");
				
				 jsonSubMStatuses.append("Submission Status", jsonSubMStatus);
		         count++;
		    }
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonSubMStatuses.toString(4);
        return Response.status(200).entity(result).build();
        
	}
}
