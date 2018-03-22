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

import gov.nasa.pds.tracking.tracking.db.Doi;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/doi")
public class JSONBasedDOI {
	
	public static Logger logger = Logger.getLogger(JSONBasedDOI.class);

	/**
	 * @return
	 * @throws JSONException
	 */
	@GET
    @Produces("application/json")
    public Response defaultDOI() throws JSONException {
 
        JSONObject jsonDOIs = new JSONObject();
        
        JSONObject jsonDOI = new JSONObject();
        
        Doi doi;
		try {
			
			doi = new Doi();
			List<Doi> dois = doi.getDOIList();
			logger.info("number of DOI: "  + dois.size());
			
			Iterator<Doi> itr = dois.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         Doi d = itr.next();
		         logger.debug("DOI " + count + ":\n " + d.getLog_identifier() + " : " + d.getDoi());
					
		         jsonDOI = new JSONObject();	         
		         jsonDOI.put(Doi.LOG_IDENTIFIERCOLUME, d.getLog_identifier());
		         jsonDOI.put(Doi.VERSIONCOLUME, d.getVersion());
		         jsonDOI.put(Doi.DOICOLUME, d.getDoi());
		         jsonDOI.put(Doi.DATECOLUME, d.getDate());
		         jsonDOI.put(Doi.URLCOLUME, d.getUrl());		         
		         jsonDOI.put(Doi.EMAILCOLUME, d.getEmail());
		         jsonDOI.put(Doi.COMMENTCOLUME, d.getComment() != null ? d.getComment() : "");
		         
		         jsonDOIs.append("Doi", jsonDOI);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonDOIs.toString(4);
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
    public Response DOI(@PathParam("id") String id, @PathParam("version") String version)  throws JSONException {
		
		JSONObject jsonDOIs = new JSONObject();
        
        JSONObject jsonDOI = new JSONObject();
        
        Doi doi;
		try {
			
			doi = new Doi();
			List<Doi> dois = doi.getDOIList(id, version);
			logger.info("number of DOI: "  + dois.size());
			
			Iterator<Doi> itr = dois.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         Doi d = itr.next();
		         logger.debug("DOI " + count + ":\n " + d.getLog_identifier() + " : " + d.getDoi());
					
		         jsonDOI = new JSONObject();	         
		         jsonDOI.put(Doi.LOG_IDENTIFIERCOLUME, d.getLog_identifier());
		         jsonDOI.put(Doi.VERSIONCOLUME, d.getVersion());
		         jsonDOI.put(Doi.DOICOLUME, d.getDoi());
		         jsonDOI.put(Doi.DATECOLUME, d.getDate());
		         jsonDOI.put(Doi.URLCOLUME, d.getUrl());		         
		         jsonDOI.put(Doi.EMAILCOLUME, d.getEmail());
		         jsonDOI.put(Doi.COMMENTCOLUME, d.getComment() != null ? d.getComment() : "");
		         
		         jsonDOIs.append("Doi", jsonDOI);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonDOIs.toString(4);
        return Response.status(200).entity(result).build();
	}
	
}
