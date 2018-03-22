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

import gov.nasa.pds.tracking.tracking.db.CertificationStatus;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/certificationstatus")
public class JSONBasedCertificationStatus {
	
	public static Logger logger = Logger.getLogger(JSONBasedCertificationStatus.class);

	/**
	 * @return
	 * @throws JSONException
	 */
	@GET
    @Produces("application/json")
    public Response defaultCertificationStatus() throws JSONException {
 
        JSONObject jsonCStatuses = new JSONObject();
        
        JSONObject jsonCStatus = new JSONObject();
        
        CertificationStatus cStatus;
		try {
			
			cStatus = new CertificationStatus();
			List<CertificationStatus> cStatuses = cStatus.getCertificationStatusOrderByVersion();
			logger.info("number of Certification Status: "  + cStatuses.size());
			
			Iterator<CertificationStatus> itr = cStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				CertificationStatus cs = itr.next();
		         logger.debug("Certification Status " + count + ":\n " + cs.getLogIdentifier() + " : " + cs.getStatus());
					
		         jsonCStatus = new JSONObject();
		     	
		     	 jsonCStatus.put(CertificationStatus.LOGIDENTIFIERCOLUMN, cs.getLogIdentifier());
		     	 jsonCStatus.put(CertificationStatus.VERSIONCOLUMN, cs.getVersion());
		     	 jsonCStatus.put(CertificationStatus.DATECOLUMN, cs.getDate());
		     	 jsonCStatus.put(CertificationStatus.STATUSCOLUMN, cs.getStatus());
		     	 jsonCStatus.put(CertificationStatus.EMAILCOLUMN, cs.getEmail());		         
		     	 jsonCStatus.put(CertificationStatus.COMMENTCOLUMN, cs.getComment() != null ? cs.getComment() : "");
		         
		     	 jsonCStatuses.append("Certification Status", jsonCStatus);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonCStatuses.toString(4);
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
    public Response certificationStatus(@PathParam("id") String id, @PathParam("version") String version, @PathParam("latest") boolean latest)  throws JSONException {
		
		JSONObject jsonCStatuses = new JSONObject();
        
        JSONObject jsonCStatus = new JSONObject();
        
        CertificationStatus cStatus;
		try {
			
			cStatus = new CertificationStatus();
			List<CertificationStatus> cStatuses = new ArrayList<CertificationStatus>();
			
			if (latest) {
				cStatus = cStatus.getLatestCertificationStatus(id, version);
				if (cStatus != null)
				cStatuses.add(cStatus);
			}else{
				cStatuses = cStatus.getCertificationStatusList(id, version);
			}
			
			logger.info("number of Certification Status: "  + cStatuses.size());
			
			Iterator<CertificationStatus> itr = cStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				CertificationStatus cs = itr.next();
		         logger.debug("Certification Status " + count + ":\n " + cs.getLogIdentifier() + " : " + cs.getStatus());
					
		         jsonCStatus = new JSONObject();
		     	
		     	 jsonCStatus.put(CertificationStatus.LOGIDENTIFIERCOLUMN, cs.getLogIdentifier());
		     	 jsonCStatus.put(CertificationStatus.VERSIONCOLUMN, cs.getVersion());
		     	 jsonCStatus.put(CertificationStatus.DATECOLUMN, cs.getDate());
		     	 jsonCStatus.put(CertificationStatus.STATUSCOLUMN, cs.getStatus());
		     	 jsonCStatus.put(CertificationStatus.EMAILCOLUMN, cs.getEmail());		         
		     	 jsonCStatus.put(CertificationStatus.COMMENTCOLUMN, cs.getComment() != null ? cs.getComment() : "");
		         
		     	 jsonCStatuses.append("Certification Status", jsonCStatus);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonCStatuses.toString(4);
        return Response.status(200).entity(result).build();
	
	}
}
