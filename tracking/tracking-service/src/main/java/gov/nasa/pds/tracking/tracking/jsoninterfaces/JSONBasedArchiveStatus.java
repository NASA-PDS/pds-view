/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.jsoninterfaces;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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

import gov.nasa.pds.tracking.tracking.db.ArchiveStatus;
import gov.nasa.pds.tracking.tracking.db.ArchiveStatusDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/archivestatus")
public class JSONBasedArchiveStatus {
	
	public static Logger logger = Logger.getLogger(JSONBasedArchiveStatus.class);
	
	private static final String FAILURE_RESULT="Failure";
	private ArchiveStatusDao asD;
	
	/**
	 * @return
	 * @throws JSONException
	 */
	@GET
    @Produces("application/json")
    public Response defaultArchiveStatus() throws JSONException {
 
        JSONObject jsonAStatuses = new JSONObject();
        
        JSONObject jsonAStatus = new JSONObject();
        
        ArchiveStatusDao aStatus;
		try {
			
			aStatus = new ArchiveStatusDao();
			List<ArchiveStatus> aStatuses = aStatus.getArchiveStatusOrderByVersion();
			logger.info("number of Archive Status: "  + aStatuses.size());
			
			Iterator<ArchiveStatus> itr = aStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				ArchiveStatus as = itr.next();
		         logger.debug("Archive Status " + count + ":\n " + as.getLogIdentifier() + " : " + as.getStatus());
					
		         jsonAStatus = new JSONObject();
		     	
		     	 jsonAStatus.put(ArchiveStatusDao.LOGIDENTIFIERCOLUMN, as.getLogIdentifier());
		     	 jsonAStatus.put(ArchiveStatusDao.VERSIONCOLUMN, as.getVersion());
		     	 jsonAStatus.put(ArchiveStatusDao.DATECOLUMN, as.getDate());
		     	 jsonAStatus.put(ArchiveStatusDao.STATUSCOLUMN, as.getStatus());
		     	 jsonAStatus.put(ArchiveStatusDao.EMAILCOLUMN, as.getEmail());		         
		     	 jsonAStatus.put(ArchiveStatusDao.COMMENTCOLUMN, as.getComment() != null ? as.getComment() : "");
		         
		     	 jsonAStatuses.append("Archive Status", jsonAStatus);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonAStatuses.toString(4);
        return Response.status(200).entity(result).build();
    }
	
	/**
	 * @param id
	 * @param version
	 * @return
	 * @throws JSONException
	 */
	@Path("{id : (.+)?}/{version : (.+)?}/{latest}")
    @GET
    @Produces("application/json")
    public Response archiveStatus(@PathParam("id") String id, @PathParam("version") String version, @PathParam("latest") boolean latest)  throws JSONException {
		
		JSONObject jsonAStatuses = new JSONObject();
        
        JSONObject jsonAStatus = new JSONObject();
        
        ArchiveStatusDao aStatusO;
        ArchiveStatus aStatus;
		try {
			
			aStatusO = new ArchiveStatusDao();
			
			List<ArchiveStatus> aStatuses = new ArrayList<ArchiveStatus>();
			
			if (latest) {
				aStatus = aStatusO.getLatestArchiveStatus(id, version);
				if (aStatus != null)
				aStatuses.add(aStatus);
			}else{
				aStatuses = aStatusO.getArchiveStatusList(id, version);
			}
			
			logger.info("number of Archive Status: "  + aStatuses.size());
			
			Iterator<ArchiveStatus> itr = aStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				ArchiveStatus as = itr.next();
		         logger.debug("Archive Status " + count + ":\n " + as.getLogIdentifier() + " : " + as.getStatus());
					
		         jsonAStatus = new JSONObject();
		     	
		     	 jsonAStatus.put(ArchiveStatusDao.LOGIDENTIFIERCOLUMN, as.getLogIdentifier());
		     	 jsonAStatus.put(ArchiveStatusDao.VERSIONCOLUMN, as.getVersion());
		     	 jsonAStatus.put(ArchiveStatusDao.DATECOLUMN, as.getDate());
		     	 jsonAStatus.put(ArchiveStatusDao.STATUSCOLUMN, as.getStatus());
		     	 jsonAStatus.put(ArchiveStatusDao.EMAILCOLUMN, as.getEmail());		         
		     	 jsonAStatus.put(ArchiveStatusDao.COMMENTCOLUMN, as.getComment() != null ? as.getComment() : "");
		         
		     	 jsonAStatuses.append("Archive Status", jsonAStatus);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonAStatuses.toString(4);
        return Response.status(200).entity(result).build();
	
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createArchiveStatus(@FormParam("LogicalIdentifier") String logicalIdentifier,
			@FormParam("Version") String ver,
			@FormParam("Date") String date,
			@FormParam("Status") String status,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			asD = new ArchiveStatusDao();

			ArchiveStatus as = new ArchiveStatus(logicalIdentifier, ver, date, status, email, comment);
			int result = asD.insertArchiveStatus(as);
			
			if(result == 1){
				message.put(ArchiveStatusDao.LOGIDENTIFIERCOLUMN, as.getLogIdentifier());
				message.put(ArchiveStatusDao.VERSIONCOLUMN, as.getVersion());
				message.put(ArchiveStatusDao.DATECOLUMN, as.getDate());
				message.put(ArchiveStatusDao.STATUSCOLUMN, as.getStatus());
				message.put(ArchiveStatusDao.EMAILCOLUMN, as.getEmail());		         
				message.put(ArchiveStatusDao.COMMENTCOLUMN, as.getComment() != null ? as.getComment() : "");
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		//logger.debug("Result: " + message);
		relt.append("ArchiveStatus", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
}
