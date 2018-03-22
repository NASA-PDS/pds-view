/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.jsoninterfaces;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
 
import org.json.JSONException;
import org.json.JSONObject;

import gov.nasa.pds.tracking.tracking.db.ArchiveStatus;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/archivestatus")
public class JSONBasedArchiveStatus {
	
	public static Logger logger = Logger.getLogger(JSONBasedArchiveStatus.class);

	/**
	 * @return
	 * @throws JSONException
	 */
	@GET
    @Produces("application/json")
    public Response defaultArchiveStatus() throws JSONException {
 
        JSONObject jsonAStatuses = new JSONObject();
        
        JSONObject jsonAStatus = new JSONObject();
        
        ArchiveStatus aStatus;
		try {
			
			aStatus = new ArchiveStatus();
			List<ArchiveStatus> aStatuses = aStatus.getArchiveStatusOrderByVersion();
			logger.info("number of Archive Status: "  + aStatuses.size());
			
			Iterator<ArchiveStatus> itr = aStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				ArchiveStatus as = itr.next();
		         logger.debug("Archive Status " + count + ":\n " + as.getLogIdentifier() + " : " + as.getStatus());
					
		         jsonAStatus = new JSONObject();
		     	
		     	 jsonAStatus.put(ArchiveStatus.LOGIDENTIFIERCOLUMN, as.getLogIdentifier());
		     	 jsonAStatus.put(ArchiveStatus.VERSIONCOLUMN, as.getVersion());
		     	 jsonAStatus.put(ArchiveStatus.DATECOLUMN, as.getDate());
		     	 jsonAStatus.put(ArchiveStatus.STATUSCOLUMN, as.getStatus());
		     	 jsonAStatus.put(ArchiveStatus.EMAILCOLUMN, as.getEmail());		         
		     	 jsonAStatus.put(ArchiveStatus.COMMENTCOLUMN, as.getComment() != null ? as.getComment() : "");
		         
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
        
        ArchiveStatus aStatus;
		try {
			
			aStatus = new ArchiveStatus();
			List<ArchiveStatus> aStatuses = new ArrayList<ArchiveStatus>();
			
			if (latest) {
				aStatus = aStatus.getLatestArchiveStatus(id, version);
				if (aStatus != null)
				aStatuses.add(aStatus);
			}else{
				aStatuses = aStatus.getArchiveStatusList(id, version);
			}
			
			logger.info("number of Archive Status: "  + aStatuses.size());
			
			Iterator<ArchiveStatus> itr = aStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				ArchiveStatus as = itr.next();
		         logger.debug("Archive Status " + count + ":\n " + as.getLogIdentifier() + " : " + as.getStatus());
					
		         jsonAStatus = new JSONObject();
		     	
		     	 jsonAStatus.put(ArchiveStatus.LOGIDENTIFIERCOLUMN, as.getLogIdentifier());
		     	 jsonAStatus.put(ArchiveStatus.VERSIONCOLUMN, as.getVersion());
		     	 jsonAStatus.put(ArchiveStatus.DATECOLUMN, as.getDate());
		     	 jsonAStatus.put(ArchiveStatus.STATUSCOLUMN, as.getStatus());
		     	 jsonAStatus.put(ArchiveStatus.EMAILCOLUMN, as.getEmail());		         
		     	 jsonAStatus.put(ArchiveStatus.COMMENTCOLUMN, as.getComment() != null ? as.getComment() : "");
		         
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
}
