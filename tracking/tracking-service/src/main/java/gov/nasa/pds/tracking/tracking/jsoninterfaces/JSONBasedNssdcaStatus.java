/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.jsoninterfaces;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
 
import org.json.JSONException;
import org.json.JSONObject;

import gov.nasa.pds.tracking.tracking.db.DBConnector;
import gov.nasa.pds.tracking.tracking.db.NssdcaStatus;
import gov.nasa.pds.tracking.tracking.db.NssdcaStatusDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/nssdcastatus")
public class JSONBasedNssdcaStatus {
	
	public static Logger logger = Logger.getLogger(JSONBasedNssdcaStatus.class);
	
	private static final String FAILURE_RESULT="Failure";
	private NssdcaStatusDao nsD;
	/**
	 * @return
	 * @throws JSONException
	 */
	@GET
    @Produces("application/json")
    public Response defaultNssdcaStatus() throws JSONException {
 
        JSONObject jsonNStatuses = new JSONObject();
        
        JSONObject jsonNStatus = new JSONObject();
        
        NssdcaStatusDao nStatusO;
		try {
			
			nStatusO = new NssdcaStatusDao();
			List<NssdcaStatus> nStatuses = nStatusO.getNssdcaStatusOrderByVersion();
			logger.info("number of NSSDCA Status: "  + nStatuses.size());
			
			Iterator<NssdcaStatus> itr = nStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				NssdcaStatus ns = itr.next();
		         logger.debug("NSSDCA Status " + count + ":\n " + ns.getLogIdentifier() + " : " + ns.getNssdca());
					
		         jsonNStatus = new JSONObject();
		     	
		         jsonNStatus.put(NssdcaStatusDao.LOGIDENTIFIERCOLUMN, ns.getLogIdentifier());
		         jsonNStatus.put(NssdcaStatusDao.VERSIONCOLUMN, ns.getVersion());
		         jsonNStatus.put(NssdcaStatusDao.DATECOLUMN, ns.getDate());
		         jsonNStatus.put(NssdcaStatusDao.NSSDCACOLUMN, ns.getNssdca());
		         jsonNStatus.put(NssdcaStatusDao.EMAILCOLUMN, ns.getEmail());		         
		         jsonNStatus.put(NssdcaStatusDao.COMMENTCOLUMN, ns.getComment() != null ? ns.getComment() : "");
		         
		     	 jsonNStatuses.append("NSSDCA Status", jsonNStatus);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonNStatuses.toString(4);
        return Response.status(200).entity(result).build();
    }
	
	/**
	 * @param id
	 * @param version
	 * @return
	 * @throws JSONException
	 */
	@Path("{id : (.+)?}/{version : (.+)?}")
    @GET
    @Produces("application/json")
    public Response NssdcaStatus(@PathParam("id") String id, @PathParam("version") String version)  throws JSONException {
		
		JSONObject jsonNStatuses = new JSONObject();
        
        JSONObject jsonNStatus = new JSONObject();
        
        NssdcaStatusDao nStatus;
		try {
			
			nStatus = new NssdcaStatusDao();
			List<NssdcaStatus> nStatuses = nStatus.getNssdcaStatusList(id, version);
			logger.info("number of NSSDCA Status: "  + nStatuses.size());
			
			Iterator<NssdcaStatus> itr = nStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				NssdcaStatus ns = itr.next();
		         logger.debug("NSSDCA Status " + count + ":\n " + ns.getLogIdentifier() + " : " + ns.getNssdca());
					
		         jsonNStatus = new JSONObject();
		     	
		         jsonNStatus.put(NssdcaStatusDao.LOGIDENTIFIERCOLUMN, ns.getLogIdentifier());
		         jsonNStatus.put(NssdcaStatusDao.VERSIONCOLUMN, ns.getVersion());
		         jsonNStatus.put(NssdcaStatusDao.DATECOLUMN, ns.getDate());
		         jsonNStatus.put(NssdcaStatusDao.NSSDCACOLUMN, ns.getNssdca());
		         jsonNStatus.put(NssdcaStatusDao.EMAILCOLUMN, ns.getEmail());		         
		         jsonNStatus.put(NssdcaStatusDao.COMMENTCOLUMN, ns.getComment() != null ? ns.getComment() : "");
		         
		     	 jsonNStatuses.append("NSSDCA Status", jsonNStatus);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonNStatuses.toString(4);
        return Response.status(200).entity(result).build();
	
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createNssdcaStatus(@FormParam("LogicalIdentifier") String logicalIdentifier,
			@FormParam("Version") String ver,
			@FormParam("NssdcaIdentifier") String nssdca,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			nsD = new NssdcaStatusDao();
			
			String currentTime = DBConnector.ISO_BASIC.format(new Date());
			NssdcaStatus ns = new NssdcaStatus(logicalIdentifier, ver, currentTime, nssdca, email, comment);
			
			int result = nsD.insertNssdcaStatus(ns);
			
			if(result == 1){
				message.put(NssdcaStatusDao.LOGIDENTIFIERCOLUMN, ns.getLogIdentifier());
				message.put(NssdcaStatusDao.VERSIONCOLUMN, ns.getVersion());
				message.put(NssdcaStatusDao.DATECOLUMN, ns.getDate());
				message.put(NssdcaStatusDao.NSSDCACOLUMN, ns.getNssdca());
				message.put(NssdcaStatusDao.EMAILCOLUMN, ns.getEmail());		         
				message.put(NssdcaStatusDao.COMMENTCOLUMN, ns.getComment() != null ? ns.getComment() : "");
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		//logger.debug("Result: " + message);
		relt.append("NssdcaStatus", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
	
}
