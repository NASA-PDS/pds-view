/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking;

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

import gov.nasa.pds.tracking.tracking.db.Delivery;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/delivery")
public class JSONBasedDelivery {
	
	public static Logger logger = Logger.getLogger(JSONBasedDelivery.class);

	/**
	 * @return
	 * @throws JSONException
	 */
	@GET
    @Produces("application/json")
    public Response defaultDelivery() throws JSONException {
 
        JSONObject jsonDeliveries = new JSONObject();
        
        JSONObject jsonDelivery = new JSONObject();
        
        Delivery del;
		try {
			
			del = new Delivery();
			List<Delivery> dels = del.getDeliveries();
			logger.info("number of deliveries: "  + dels.size());
			
			Iterator<Delivery> itr = dels.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         Delivery d = itr.next();
		         logger.debug("Deliveries " + count + ":\n " + d.getLogIdentifier() + " : " + d.getName());
		         
		         jsonDelivery = new JSONObject();	         
		         jsonDelivery.put(Delivery.DEL_IDENTIFIERCOLUMN, d.getDelIdentifier());
		         jsonDelivery.put(Delivery.LOG_IDENTIFIERCOLUMN, d.getLogIdentifier());
		         jsonDelivery.put(Delivery.VERSIONCOLUMN, d.getVersion());
		         jsonDelivery.put(Delivery.NAMECOLUMN, d.getName());
		         jsonDelivery.put(Delivery.STARTCOLUMN, d.getStart());
		         jsonDelivery.put(Delivery.STOPCOLUMN, d.getStop());		         
		         jsonDelivery.put(Delivery.SOURCECOLUMN, d.getSource());
		         jsonDelivery.put(Delivery.TARGETCOLUMN, d.getTarget());
		         jsonDelivery.put(Delivery.DUEDATECOLUMN, d.getDueDate());
		         
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
    @Produces("application/json")
    public Response delivery(@PathParam("id") String id, @PathParam("version") String version)  throws JSONException {
		
		JSONObject jsonDeliveries = new JSONObject();
        
        JSONObject jsonDelivery = new JSONObject();
        
        Delivery del;
		try {
			
			del = new Delivery();
			List<Delivery> dels = del.getProductDeliveries(id, version);
			logger.info("number of deliveries: "  + dels.size());
			
			Iterator<Delivery> itr = dels.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         Delivery d = itr.next();
		         logger.debug("Deliveries " + count + ":\n " + d.getLogIdentifier() + " : " + d.getName());
		         
		         jsonDelivery = new JSONObject();
		         jsonDelivery.put(Delivery.DEL_IDENTIFIERCOLUMN, d.getDelIdentifier());
		         jsonDelivery.put(Delivery.LOG_IDENTIFIERCOLUMN, d.getLogIdentifier());
		         jsonDelivery.put(Delivery.VERSIONCOLUMN, d.getVersion());
		         jsonDelivery.put(Delivery.NAMECOLUMN, d.getName());
		         jsonDelivery.put(Delivery.STARTCOLUMN, d.getStart());
		         jsonDelivery.put(Delivery.STOPCOLUMN, d.getStop());		         
		         jsonDelivery.put(Delivery.SOURCECOLUMN, d.getSource());
		         jsonDelivery.put(Delivery.TARGETCOLUMN, d.getTarget());
		         jsonDelivery.put(Delivery.DUEDATECOLUMN, d.getDueDate());
		         
		         
		         
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
	
}
