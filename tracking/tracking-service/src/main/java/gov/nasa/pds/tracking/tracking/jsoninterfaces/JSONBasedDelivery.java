/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.jsoninterfaces;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
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

import gov.nasa.pds.tracking.tracking.db.Delivery;
import gov.nasa.pds.tracking.tracking.db.DeliveryDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/delivery")
public class JSONBasedDelivery {
	
	public static Logger logger = Logger.getLogger(JSONBasedDelivery.class);
	
	private static final String FAILURE_RESULT="Failure";
	
	DeliveryDao dD;

	/**
	 * @return
	 * @throws JSONException
	 */
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response defaultDelivery() throws JSONException {
 
        JSONObject jsonDeliveries = new JSONObject();
        
        JSONObject jsonDelivery = new JSONObject();
        
        DeliveryDao delD;
		try {
			
			delD = new DeliveryDao();
			List<Delivery> dels = delD.getDeliveries();
			logger.info("number of deliveries: "  + dels.size());
			
			Iterator<Delivery> itr = dels.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         Delivery d = itr.next();
		         logger.debug("Deliveries " + count + ":\n " + d.getLogIdentifier() + " : " + d.getName());
		         
		         jsonDelivery = new JSONObject();	         
		         jsonDelivery.put(DeliveryDao.DEL_IDENTIFIERCOLUMN, d.getDelIdentifier());
		         jsonDelivery.put(DeliveryDao.LOG_IDENTIFIERCOLUMN, d.getLogIdentifier());
		         jsonDelivery.put(DeliveryDao.VERSIONCOLUMN, d.getVersion());
		         jsonDelivery.put(DeliveryDao.NAMECOLUMN, d.getName());
		         jsonDelivery.put(DeliveryDao.STARTCOLUMN, d.getStart());
		         jsonDelivery.put(DeliveryDao.STOPCOLUMN, d.getStop());		         
		         jsonDelivery.put(DeliveryDao.SOURCECOLUMN, d.getSource());
		         jsonDelivery.put(DeliveryDao.TARGETCOLUMN, d.getTarget());
		         jsonDelivery.put(DeliveryDao.DUEDATECOLUMN, d.getDueDate());
		         
		         jsonDeliveries.append("delivery", jsonDelivery);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonDeliveries.toString(4);
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response delivery(@PathParam("id") String id, @PathParam("version") String version)  throws JSONException {
		
		JSONObject jsonDeliveries = new JSONObject();
        
        JSONObject jsonDelivery = new JSONObject();
        
        DeliveryDao delD;
		try {
			
			delD = new DeliveryDao();
			List<Delivery> dels = delD.getProductDeliveries(id, version);
			logger.info("number of deliveries: "  + dels.size());
			
			Iterator<Delivery> itr = dels.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         Delivery d = itr.next();
		         logger.debug("Deliveries " + count + ":\n " + d.getLogIdentifier() + " : " + d.getName());
		         
		         jsonDelivery = new JSONObject();
		         jsonDelivery.put(DeliveryDao.DEL_IDENTIFIERCOLUMN, d.getDelIdentifier());
		         jsonDelivery.put(DeliveryDao.LOG_IDENTIFIERCOLUMN, d.getLogIdentifier());
		         jsonDelivery.put(DeliveryDao.VERSIONCOLUMN, d.getVersion());
		         jsonDelivery.put(DeliveryDao.NAMECOLUMN, d.getName());
		         jsonDelivery.put(DeliveryDao.STARTCOLUMN, d.getStart());
		         jsonDelivery.put(DeliveryDao.STOPCOLUMN, d.getStop());		         
		         jsonDelivery.put(DeliveryDao.SOURCECOLUMN, d.getSource());
		         jsonDelivery.put(DeliveryDao.TARGETCOLUMN, d.getTarget());
		         jsonDelivery.put(DeliveryDao.DUEDATECOLUMN, d.getDueDate());
		         
		         
		         
		         jsonDeliveries.append("delivery", jsonDelivery);
		         count++;
		    }
			
						
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonDeliveries.toString(4);
        return Response.status(200).entity(result).build();
	}

	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createDelivery(@FormParam("Log_Identifier") String logIdentifier,
								@FormParam("Version") String ver, 
								@FormParam("Name") String name, 
								@FormParam("Start_Time") String startTime, 
								@FormParam("Stop_Time") String stopTime, 
								@FormParam("Source") String src, 
								@FormParam("Target") String tgt, 
								@FormParam("Due_Date") String dueD ) throws IOException{
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			dD = new DeliveryDao();
	
			Delivery del = new Delivery(logIdentifier, -1, ver, name, startTime, stopTime,
					src, tgt, dueD);
			int result = dD.insertDelivery(del);
			
			if(result > 0){
				message.put(DeliveryDao.DEL_IDENTIFIERCOLUMN, result);
				message.put(DeliveryDao.LOG_IDENTIFIERCOLUMN, del.getLogIdentifier());
				message.put(DeliveryDao.VERSIONCOLUMN, del.getVersion());
				message.put(DeliveryDao.NAMECOLUMN, del.getName());
				message.put(DeliveryDao.STARTCOLUMN, del.getStart());
				message.put(DeliveryDao.STOPCOLUMN, del.getStop());		         
				message.put(DeliveryDao.SOURCECOLUMN, del.getSource());
				message.put(DeliveryDao.TARGETCOLUMN, del.getTarget());
				message.put(DeliveryDao.DUEDATECOLUMN, del.getDueDate());
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		//logger.debug("Result: " + message);
		relt.append("Delivery", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}

	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response updateDelivery(@FormParam("Log_Identifier") String logIdentifier, 
			@FormParam("Del_Identifier") String delIdentifier,
			@FormParam("Version") String ver, 
			@FormParam("Name") String name, 
			@FormParam("Start_Time") String startTime, 
			@FormParam("Stop_Time") String stopTime, 
			@FormParam("Source") String src, 
			@FormParam("Target") String tgt, 
			@FormParam("Due_Date") String dueD) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			dD = new DeliveryDao();
			
			Delivery del = new Delivery(logIdentifier, (new Integer(delIdentifier).intValue()), ver, name, startTime, stopTime,
					src, tgt, dueD);
			Delivery updatedDel = dD.updateDelivery(del);
			
			if(updatedDel != null){
				message.put(DeliveryDao.DEL_IDENTIFIERCOLUMN, updatedDel.getDelIdentifier());
				message.put(DeliveryDao.LOG_IDENTIFIERCOLUMN, updatedDel.getLogIdentifier());
				message.put(DeliveryDao.VERSIONCOLUMN, updatedDel.getVersion());
				message.put(DeliveryDao.NAMECOLUMN, updatedDel.getName());
				message.put(DeliveryDao.STARTCOLUMN, updatedDel.getStart());
				message.put(DeliveryDao.STOPCOLUMN, updatedDel.getStop());		         
				message.put(DeliveryDao.SOURCECOLUMN, updatedDel.getSource());
				message.put(DeliveryDao.TARGETCOLUMN, updatedDel.getTarget());
				message.put(DeliveryDao.DUEDATECOLUMN, updatedDel.getDueDate());
			}else{
				message.put("Message", FAILURE_RESULT);
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		relt.append("Delivery", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
}