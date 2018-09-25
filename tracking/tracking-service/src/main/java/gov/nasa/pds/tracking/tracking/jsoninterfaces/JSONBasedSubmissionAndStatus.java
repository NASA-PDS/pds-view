/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.jsoninterfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

//import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.Consumes;
//import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import gov.nasa.pds.tracking.tracking.db.DBConnector;
import gov.nasa.pds.tracking.tracking.db.SubmissionAndStatus;
import gov.nasa.pds.tracking.tracking.db.SubmissionAndStatusDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/submissionstatus")
public class JSONBasedSubmissionAndStatus {

	public static Logger logger = Logger.getLogger(JSONBasedSubmissionAndStatus.class);
	
	private static final String FAILURE_RESULT="Failure";

	private SubmissionAndStatusDao subMD;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response defaultSubmission() throws JSONException {

		JSONObject jsonSubMs = new JSONObject();

		JSONObject jsonSubMStatus;

		try {
			subMD = new SubmissionAndStatusDao();
			List<SubmissionAndStatus> subMs = subMD.getSubmissionsAndStatusList();
			logger.info("number of Submission: " + subMs.size());
			Iterator<SubmissionAndStatus> itr = subMs.iterator();
			int count = 1;

			while (itr.hasNext()) {
				SubmissionAndStatus s = itr.next();
				logger.debug("Submission " + count + ":\n " + s.getDel_identifier() + " : " + s.getSubmissionDate());

				jsonSubMStatus = new JSONObject();
				jsonSubMStatus.put(SubmissionAndStatusDao.DEL_IDENTIFIERCOLUME, s.getDel_identifier());
				jsonSubMStatus.put(SubmissionAndStatusDao.SUBMISSIONDATECOLUME, s.getSubmissionDate());
				jsonSubMStatus.put(SubmissionAndStatusDao.STATUSDATECOLUME, s.getStatusDate());
				jsonSubMStatus.put(SubmissionAndStatusDao.STATUSCOLUME, s.getStatus());
				jsonSubMStatus.put(SubmissionAndStatusDao.EMAILCOLUME, s.getEmail());
				jsonSubMStatus.put(SubmissionAndStatusDao.COMMENTCOLUME, s.getComment() != null ? s.getComment() : "");

				jsonSubMs.append("Submission Status ", jsonSubMStatus);
				count++;
			}

		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
		String result = "" + jsonSubMs.toString(4);
		return Response.status(200).entity(result).build();
	}

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSubmissionStatus(@PathParam("id") String id) throws JSONException {

		JSONObject jsonSubMStatuses = new JSONObject();
		JSONObject jsonSubMStatus = new JSONObject();

		try {
			subMD = new SubmissionAndStatusDao();

			List<SubmissionAndStatus> subMStatuses = subMD.getDeliveryStatus(id);
			logger.info("number of Submission Status: " + subMStatuses.size());

			Iterator<SubmissionAndStatus> itr = subMStatuses.iterator();
			int count = 1;

			while (itr.hasNext()) {
				SubmissionAndStatus s = itr.next();
				logger.debug("Submission Status " + count + ":\n " + s.getStatus() + " : " + s.getSubmissionDate());

				jsonSubMStatus = new JSONObject();
				jsonSubMStatus.put(SubmissionAndStatusDao.DEL_IDENTIFIERCOLUME, s.getDel_identifier());
				jsonSubMStatus.put(SubmissionAndStatusDao.SUBMISSIONDATECOLUME, s.getSubmissionDate());
				jsonSubMStatus.put(SubmissionAndStatusDao.STATUSDATECOLUME, s.getStatusDate());
				jsonSubMStatus.put(SubmissionAndStatusDao.STATUSCOLUME, s.getStatus());
				jsonSubMStatus.put(SubmissionAndStatusDao.EMAILCOLUME, s.getEmail());
				jsonSubMStatus.put(SubmissionAndStatusDao.COMMENTCOLUME, s.getComment() != null ? s.getComment() : "");

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
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createSubmissionAndStatus(@FormParam("Delivery_ID") int id,
			@FormParam("Status") String status,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			subMD = new SubmissionAndStatusDao();
		
			String currentTime = DBConnector.ISO_BASIC.format(new Date());
			SubmissionAndStatus subMS = new SubmissionAndStatus(id, currentTime, currentTime,
					status, email, comment);
			int result = subMD.insertSubmissionAndStatus(subMS);
			
			if(result == 1){
				message.put(SubmissionAndStatusDao.DEL_IDENTIFIERCOLUME, subMS.getDel_identifier());
				message.put(SubmissionAndStatusDao.SUBMISSIONDATECOLUME, subMS.getSubmissionDate());
				message.put(SubmissionAndStatusDao.STATUSDATECOLUME, subMS.getStatusDate());
				message.put(SubmissionAndStatusDao.STATUSCOLUME, subMS.getStatus());
				message.put(SubmissionAndStatusDao.EMAILCOLUME, subMS.getEmail());
				message.put(SubmissionAndStatusDao.COMMENTCOLUME, subMS.getComment() != null ? subMS.getComment() : "");
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		//logger.debug("Result: " + message);
		relt.append("Submission Status", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
	
	@POST
	@Path("/addstatus")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createSubmissionStatus(@FormParam("Delivery_ID") int id,
			@FormParam("SubmissionDate") String submissionDate,
			@FormParam("Status") String status,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			subMD = new SubmissionAndStatusDao();
		
			String currentTime = DBConnector.ISO_BASIC.format(new Date());
			SubmissionAndStatus subMS = new SubmissionAndStatus(id, submissionDate, currentTime,
					status, email, comment);
			int result = subMD.insertSubmissionStatus(subMS);
			
			if(result == 1){
				message.put(SubmissionAndStatusDao.DEL_IDENTIFIERCOLUME, subMS.getDel_identifier());
				message.put(SubmissionAndStatusDao.SUBMISSIONDATECOLUME, subMS.getSubmissionDate());
				message.put(SubmissionAndStatusDao.STATUSDATECOLUME, subMS.getStatusDate());
				message.put(SubmissionAndStatusDao.STATUSCOLUME, subMS.getStatus());
				message.put(SubmissionAndStatusDao.EMAILCOLUME, subMS.getEmail());
				message.put(SubmissionAndStatusDao.COMMENTCOLUME, subMS.getComment() != null ? subMS.getComment() : "");
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		//logger.debug("Result: " + message);
		relt.append("Submission Status", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
	
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response updateSubmissionStatus(@FormParam("Delivery_ID") int id,
			@FormParam("SubmissionDate") String submissionDate,
			@FormParam("StatusnDate") String statusDate,
			@FormParam("Status") String status,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			subMD = new SubmissionAndStatusDao();
		
			//String currentTime = DBConnector.ISO_BASIC.format(new Date());
			SubmissionAndStatus subMS = new SubmissionAndStatus(id, submissionDate, statusDate,
					status, email, comment);
			int result = subMD.updateSubmissionStatus(subMS);
			
			if(result == 1){
				message.put(SubmissionAndStatusDao.DEL_IDENTIFIERCOLUME, subMS.getDel_identifier());
				message.put(SubmissionAndStatusDao.SUBMISSIONDATECOLUME, subMS.getSubmissionDate());
				message.put(SubmissionAndStatusDao.STATUSDATECOLUME, subMS.getStatusDate());
				message.put(SubmissionAndStatusDao.STATUSCOLUME, subMS.getStatus());
				message.put(SubmissionAndStatusDao.EMAILCOLUME, subMS.getEmail());
				message.put(SubmissionAndStatusDao.COMMENTCOLUME, subMS.getComment() != null ? subMS.getComment() : "");
			}else{
				message.put("Message", FAILURE_RESULT);
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		//logger.debug("Rerult: " + message);
		relt.append("Submission Status", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
	
	/*@DELETE
	@Path("/delete")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response deleteSubmissionStatus(@FormParam("Delivery_ID") int id,
			@FormParam("SubmissionDate") String submissionDate) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			subMD = new SubmissionAndStatusDao();
		
			String currentTime = DBConnector.ISO_BASIC.format(new Date());
			SubmissionAndStatus subMS = new SubmissionAndStatus(id, submissionDate, null,
					null, null, null);
			int result = subMD.deleteSubmissionAndStatus(subMS);
			
			if(result == 1){
				message.put("Message", "Deleted");
				message.put(SubmissionAndStatusDao.DEL_IDENTIFIERCOLUME, subMS.getDel_identifier());
				message.put(SubmissionAndStatusDao.SUBMISSIONDATECOLUME, subMS.getSubmissionDate());
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		//logger.debug("Rerult: " + message);
		relt.append("Result", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}*/
}
