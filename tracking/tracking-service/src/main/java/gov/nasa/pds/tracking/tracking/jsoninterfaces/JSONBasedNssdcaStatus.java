/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.jsoninterfaces;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
 
import org.json.JSONException;
import org.json.JSONObject;

import gov.nasa.pds.tracking.tracking.db.NssdcaStatus;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/nssdcastatus")
public class JSONBasedNssdcaStatus {
	
	public static Logger logger = Logger.getLogger(JSONBasedNssdcaStatus.class);

	/**
	 * @return
	 * @throws JSONException
	 */
	@GET
    @Produces("application/json")
    public Response defaultNssdcaStatus() throws JSONException {
 
        JSONObject jsonNStatuses = new JSONObject();
        
        JSONObject jsonNStatus = new JSONObject();
        
        NssdcaStatus nStatus;
		try {
			
			nStatus = new NssdcaStatus();
			List<NssdcaStatus> nStatuses = nStatus.getNssdcaStatusOrderByVersion();
			logger.info("number of NSSDCA Status: "  + nStatuses.size());
			
			Iterator<NssdcaStatus> itr = nStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				NssdcaStatus ns = itr.next();
		         logger.debug("NSSDCA Status " + count + ":\n " + ns.getLogIdentifier() + " : " + ns.getNssdca());
					
		         jsonNStatus = new JSONObject();
		     	
		         jsonNStatus.put(NssdcaStatus.LOGIDENTIFIERCOLUMN, ns.getLogIdentifier());
		         jsonNStatus.put(NssdcaStatus.VERSIONCOLUMN, ns.getVersion());
		         jsonNStatus.put(NssdcaStatus.DATECOLUMN, ns.getDate());
		         jsonNStatus.put(NssdcaStatus.NSSDCACOLUMN, ns.getNssdca());
		         jsonNStatus.put(NssdcaStatus.EMAILCOLUMN, ns.getEmail());		         
		         jsonNStatus.put(NssdcaStatus.COMMENTCOLUMN, ns.getComment() != null ? ns.getComment() : "");
		         
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
        
        NssdcaStatus nStatus;
		try {
			
			nStatus = new NssdcaStatus();
			List<NssdcaStatus> nStatuses = nStatus.getNssdcaStatusList(id, version);
			logger.info("number of NSSDCA Status: "  + nStatuses.size());
			
			Iterator<NssdcaStatus> itr = nStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				NssdcaStatus ns = itr.next();
		         logger.debug("NSSDCA Status " + count + ":\n " + ns.getLogIdentifier() + " : " + ns.getNssdca());
					
		         jsonNStatus = new JSONObject();
		     	
		         jsonNStatus.put(NssdcaStatus.LOGIDENTIFIERCOLUMN, ns.getLogIdentifier());
		         jsonNStatus.put(NssdcaStatus.VERSIONCOLUMN, ns.getVersion());
		         jsonNStatus.put(NssdcaStatus.DATECOLUMN, ns.getDate());
		         jsonNStatus.put(NssdcaStatus.NSSDCACOLUMN, ns.getNssdca());
		         jsonNStatus.put(NssdcaStatus.EMAILCOLUMN, ns.getEmail());		         
		         jsonNStatus.put(NssdcaStatus.COMMENTCOLUMN, ns.getComment() != null ? ns.getComment() : "");
		         
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
}
