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

import gov.nasa.pds.tracking.tracking.db.Releases;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/releases")
public class JSONBasedReleases {
	
	public static Logger logger = Logger.getLogger(JSONBasedReleases.class);

	/**
	 * @return
	 * @throws JSONException
	 */
	@GET
    @Produces("application/json")
    public Response defaultReleases() throws JSONException {
 
        JSONObject jsonReleases = new JSONObject();
        
        JSONObject jsonRelease = new JSONObject();
        
        Releases rel;
		try {
			
			rel = new Releases();
			List<Releases> rels = rel.getReleasesList();
			logger.info("number of Releases: "  + rels.size());
			
			Iterator<Releases> itr = rels.iterator();
			int count = 1;
			while(itr.hasNext()) {
				Releases r = itr.next();
		         logger.debug("Releases " + count + ":\n " + r.getLogIdentifier() + " : " + r.getDate());
					
		         jsonRelease = new JSONObject();
		     	
		     	jsonRelease.put(Releases.LOGIDENTIFIERCOLUME, r.getLogIdentifier());
		     	jsonRelease.put(Releases.VERSIONCOLUME, r.getVersion());
		     	jsonRelease.put(Releases.DATECOLUME, r.getDate());
		     	jsonRelease.put(Releases.NAMECOLUME, r.getName());
		     	jsonRelease.put(Releases.DESCCOLUME, r.getDescription());
		     	jsonRelease.put(Releases.EMAILCOLUME, r.getEmail());
		     	jsonRelease.put(Releases.COMMENTCOLUME, r.getComment() != null ? r.getComment() : "");
		         
		     	jsonReleases.append("Archive Status", jsonRelease);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonReleases.toString(4);
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
    public Response Releases(@PathParam("id") String id, @PathParam("version") String version, @PathParam("latest") boolean latest)  throws JSONException {
		
		JSONObject jsonReleases = new JSONObject();
        
        JSONObject jsonRelease = new JSONObject();
        
        Releases rel;
		try {
			
			rel = new Releases();
			List<Releases> rels = new ArrayList<Releases>();
			
			if (latest) {
				rel = rel.getLatestReleases(id, version);
				if (rel != null)
				rels.add(rel);
			}else{
				rels = rel.getReleasesList(id, version);
			}
			logger.info("number of Releases: "  + rels.size());
			
			Iterator<Releases> itr = rels.iterator();
			int count = 1;
			while(itr.hasNext()) {
				Releases r = itr.next();
		         logger.debug("Releases " + count + ":\n " + r.getLogIdentifier() + " : " + r.getDate());
					
		         jsonRelease = new JSONObject();
		     	
		     	jsonRelease.put(Releases.LOGIDENTIFIERCOLUME, r.getLogIdentifier());
		     	jsonRelease.put(Releases.VERSIONCOLUME, r.getVersion());
		     	jsonRelease.put(Releases.DATECOLUME, r.getDate());
		     	jsonRelease.put(Releases.NAMECOLUME, r.getName());
		     	jsonRelease.put(Releases.DESCCOLUME, r.getDescription());
		     	jsonRelease.put(Releases.EMAILCOLUME, r.getEmail());
		     	jsonRelease.put(Releases.COMMENTCOLUME, r.getComment() != null ? r.getComment() : "");
		         
		     	jsonReleases.append("Archive Status", jsonRelease);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonReleases.toString(4);
        return Response.status(200).entity(result).build();
	
	}
}
