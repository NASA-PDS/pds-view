/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.jsoninterfaces;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import gov.nasa.pds.tracking.tracking.db.Reference;
import gov.nasa.pds.tracking.tracking.db.Role;
import gov.nasa.pds.tracking.tracking.db.User;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/users")
public class JSONBasedUsers {

	public static Logger logger = Logger.getLogger(JSONBasedUsers.class);

	@GET
    @Produces("application/json")
    public Response defaultUsers() throws JSONException {
 
        JSONObject jsonUsers = new JSONObject();
        
        JSONObject jsonUser = new JSONObject();
        
        User user;
		try {
			user = new User();
			List<User> users = user.getUsers();
			logger.info("number of users: "  + users.size());
			Iterator<User> itr = users.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
		         User u = itr.next();
		         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName());
		         
		         jsonUser = new JSONObject();
		         jsonUser.put(User.EMAILCOLUMN, u.getUserEmail());
		         jsonUser.put(User.NAMECOLUMN, u.getUserName());

		         jsonUsers.append("Users", jsonUser);
		         count++;
		    }
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonUsers.toString(4);
        return Response.status(200).entity(result).build();
    }

	@Path("{email}")
    @GET
    @Produces("application/json")
	public Response userRole(@PathParam("email") String email)  throws JSONException {	
		JSONObject jsonUsers = new JSONObject();
        
        JSONObject jsonUser = new JSONObject();
        
        User user;
		try {
			user = new User();
			List<User> users = user.getUserRole(email);
			logger.info("number of users: "  + users.size());
			Iterator<User> itr = users.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
		         User u = itr.next();
		         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName() + " : " + u.getReference());
		         
		         jsonUser = new JSONObject();
		         jsonUser.put(User.EMAILCOLUMN, u.getUserEmail());
		         jsonUser.put(User.NAMECOLUMN, u.getUserName());
		         jsonUser.put("role_" + Role.REFERENCECOLUMN, u.getReference());

		         jsonUsers.append("Users", jsonUser);
		         count++;
		    }
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonUsers.toString(4);
        return Response.status(200).entity(result).build();
	}
	@Path("{id : (.+)?}/{refType : (.+)?}")
    @GET
    @Produces("application/json")
    public Response userProductRole(@PathParam("id") String id, @PathParam("refType") String refType)  throws JSONException {
		
		JSONObject jsonUsers = new JSONObject();
        
        JSONObject jsonUser = new JSONObject();
        
        User user;
		try {
			user = new User();
			List<User> users = user.getProductRoleUsers(id, refType);
			logger.info("number of users: "  + users.size());
			Iterator<User> itr = users.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
		         User u = itr.next();
		         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName() + " : " + u.getType() + " : " + u.getReference());
		         
		         jsonUser = new JSONObject();
		         jsonUser.put(User.EMAILCOLUMN, u.getUserEmail());
		         jsonUser.put(User.NAMECOLUMN, u.getUserName());
		         jsonUser.put("role_" + Reference.TITLECOLUMN, u.getType());
		         jsonUser.put("role_" + Role.REFERENCECOLUMN, u.getReference());

		         jsonUsers.append("Users", jsonUser);
		         count++;
		    }
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonUsers.toString(4);
        return Response.status(200).entity(result).build();
	}
}
