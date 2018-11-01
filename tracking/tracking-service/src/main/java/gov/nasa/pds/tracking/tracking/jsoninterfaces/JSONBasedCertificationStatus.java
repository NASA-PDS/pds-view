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

import gov.nasa.pds.tracking.tracking.db.CertificationStatus;
import gov.nasa.pds.tracking.tracking.db.CertificationStatusDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/certificationstatus")
public class JSONBasedCertificationStatus {
	
	public static Logger logger = Logger.getLogger(JSONBasedCertificationStatus.class);

	private static final String FAILURE_RESULT="Failure";
	private CertificationStatusDao csD;
	
	/**
	 * @return
	 * @throws JSONException
	 */
	@GET
    @Produces("application/json")
    public Response defaultCertificationStatus() throws JSONException {
 
        JSONObject jsonCStatuses = new JSONObject();
        
        JSONObject jsonCStatus = new JSONObject();
        
        CertificationStatusDao cStatus;
		try {
			
			cStatus = new CertificationStatusDao();
			List<CertificationStatus> cStatuses = cStatus.getCertificationStatusOrderByVersion();
			logger.info("number of Certification Status: "  + cStatuses.size());
			
			Iterator<CertificationStatus> itr = cStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				CertificationStatus cs = itr.next();
		         logger.debug("Certification Status " + count + ":\n " + cs.getLogIdentifier() + " : " + cs.getStatus());
					
		         jsonCStatus = new JSONObject();
		     	
		     	 jsonCStatus.put(CertificationStatusDao.LOGIDENTIFIERCOLUMN, cs.getLogIdentifier());
		     	 jsonCStatus.put(CertificationStatusDao.VERSIONCOLUMN, cs.getVersion());
		     	 jsonCStatus.put(CertificationStatusDao.DATECOLUMN, cs.getDate());
		     	 jsonCStatus.put(CertificationStatusDao.STATUSCOLUMN, cs.getStatus());
		     	 jsonCStatus.put(CertificationStatusDao.EMAILCOLUMN, cs.getEmail());		         
		     	 jsonCStatus.put(CertificationStatusDao.COMMENTCOLUMN, cs.getComment() != null ? cs.getComment() : "");
		         
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
        
        CertificationStatusDao cStatusO;
        CertificationStatus cStatus;
		try {
			
			cStatusO = new CertificationStatusDao();
			
			List<CertificationStatus> cStatuses = new ArrayList<CertificationStatus>();
			
			if (latest) {
				cStatus = cStatusO.getLatestCertificationStatus(id, version);
				if (cStatus != null)
				cStatuses.add(cStatus);
			}else{
				cStatuses = cStatusO.getCertificationStatusList(id, version);
			}
			
			logger.info("number of Certification Status: "  + cStatuses.size());
			
			Iterator<CertificationStatus> itr = cStatuses.iterator();
			int count = 1;
			while(itr.hasNext()) {
				CertificationStatus cs = itr.next();
		         logger.debug("Certification Status " + count + ":\n " + cs.getLogIdentifier() + " : " + cs.getStatus());
					
		         jsonCStatus = new JSONObject();
		     	
		     	 jsonCStatus.put(CertificationStatusDao.LOGIDENTIFIERCOLUMN, cs.getLogIdentifier());
		     	 jsonCStatus.put(CertificationStatusDao.VERSIONCOLUMN, cs.getVersion());
		     	 jsonCStatus.put(CertificationStatusDao.DATECOLUMN, cs.getDate());
		     	 jsonCStatus.put(CertificationStatusDao.STATUSCOLUMN, cs.getStatus());
		     	 jsonCStatus.put(CertificationStatusDao.EMAILCOLUMN, cs.getEmail());		         
		     	 jsonCStatus.put(CertificationStatusDao.COMMENTCOLUMN, cs.getComment() != null ? cs.getComment() : "");
		         
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
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createCertificationStatus(@FormParam("LogicalIdentifier") String logicalIdentifier,
			@FormParam("Version") String ver,
			@FormParam("Date") String date,
			@FormParam("Status") String status,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			csD = new CertificationStatusDao();

			CertificationStatus cs = new CertificationStatus(logicalIdentifier, ver, date, status, email, comment);
			int result = csD.insertCertificationStatus(cs);
			
			if(result == 1){
				message.put(CertificationStatusDao.LOGIDENTIFIERCOLUMN, cs.getLogIdentifier());
				message.put(CertificationStatusDao.VERSIONCOLUMN, cs.getVersion());
				message.put(CertificationStatusDao.DATECOLUMN, cs.getDate());
				message.put(CertificationStatusDao.STATUSCOLUMN, cs.getStatus());
				message.put(CertificationStatusDao.EMAILCOLUMN, cs.getEmail());		         
				message.put(CertificationStatusDao.COMMENTCOLUMN, cs.getComment() != null ? cs.getComment() : "");
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		//logger.debug("Result: " + message);
		relt.append("CertificationStatus", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
}
