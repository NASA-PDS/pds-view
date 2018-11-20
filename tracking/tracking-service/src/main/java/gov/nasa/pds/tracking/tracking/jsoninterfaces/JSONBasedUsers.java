/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.jsoninterfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import gov.nasa.pds.tracking.tracking.db.Reference;
import gov.nasa.pds.tracking.tracking.db.Role;
import gov.nasa.pds.tracking.tracking.db.RoleDao;
import gov.nasa.pds.tracking.tracking.db.User;
import gov.nasa.pds.tracking.tracking.db.UserDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/users")
public class JSONBasedUsers {

	public static Logger logger = Logger.getLogger(JSONBasedUsers.class);
	
	private static final String FAILURE_RESULT="Failure";
	
	private UserDao uD;
	private RoleDao rD;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response defaultUsers() throws JSONException {
 
        JSONObject jsonUsers = new JSONObject();
        
        JSONObject jsonUser = new JSONObject();
        
        UserDao userD;
		try {
			userD = new UserDao();
			List<User> users = userD.getUsers();
			logger.info("number of users: "  + users.size());
			Iterator<User> itr = users.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
		         User u = itr.next();
		         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName());
		         
		         jsonUser = new JSONObject();
		         jsonUser.put(UserDao.EMAILCOLUMN, u.getUserEmail());
		         jsonUser.put(UserDao.NAMECOLUMN, u.getUserName());

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
    @Produces(MediaType.APPLICATION_JSON)
	public Response userRole(@PathParam("email") String email)  throws JSONException {	
		JSONObject jsonUsers = new JSONObject();
        
        JSONObject jsonUser = new JSONObject();
        
        UserDao userD;
		try {
			userD = new UserDao();
			List<User> users = userD.getUserRole(email);
			logger.info("number of users: "  + users.size());
			Iterator<User> itr = users.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
		         User u = itr.next();
		         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName() + " : " + u.getReference());
		         
		         jsonUser = new JSONObject();
		         jsonUser.put(UserDao.EMAILCOLUMN, u.getUserEmail());
		         jsonUser.put(UserDao.NAMECOLUMN, u.getUserName());
		         jsonUser.put("role_" + RoleDao.REFERENCECOLUMN, u.getReference());

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response userProductRole(@PathParam("id") String id, @PathParam("refType") String refType)  throws JSONException {
		
		JSONObject jsonUsers = new JSONObject();
        
        JSONObject jsonUser = new JSONObject();
        
        UserDao userD;
		try {
			userD = new UserDao();
			List<User> users = userD.getProductRoleUsers(id, refType);
			logger.info("number of users: "  + users.size());
			Iterator<User> itr = users.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
		         User u = itr.next();
		         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName() + " : " + u.getType() + " : " + u.getReference());
		         
		         jsonUser = new JSONObject();
		         jsonUser.put(UserDao.EMAILCOLUMN, u.getUserEmail());
		         jsonUser.put(UserDao.NAMECOLUMN, u.getUserName());
		         jsonUser.put("role_" + Reference.TITLECOLUMN, u.getType());
		         jsonUser.put("role_" + RoleDao.REFERENCECOLUMN, u.getReference());

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
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createUser(@FormParam("Email") String email, @FormParam("Name") String name) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			uD = new UserDao();

			User user = new User(email, name);
			int result = uD.insertUser(user);
			
			if(result == 1){
				message.put(UserDao.EMAILCOLUMN, user.getUserEmail());
				message.put(UserDao.NAMECOLUMN, user.getUserName());
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		//logger.debug("Result: " + message);
		relt.append("User", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
	
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response updateUser(@FormParam("Email") String email, @FormParam("Name") String name) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			uD = new UserDao();
			
			User user = new User(email, name);
			User updatedUser = uD.updateUser(user);
			
			if(updatedUser != null){
				message.put(UserDao.EMAILCOLUMN, updatedUser.getUserEmail());
				message.put(UserDao.EMAILCOLUMN, updatedUser.getUserName());
			}else{
				message.put("Message", FAILURE_RESULT);
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		relt.append("User", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
	
	@POST
	@Path("/addrole")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createRole(@FormParam("Email") String email, @FormParam("Reference") String ref) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			rD = new RoleDao();

			Role role = new Role(email, ref);
			int result = rD.insertRole(role);
			
			if(result == 1){
				message.put(RoleDao.EMAILCOLUMN, role.getEmail());
				message.put(RoleDao.REFERENCECOLUMN, role.getReference());
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		//logger.debug("Result: " + message);
		relt.append("Role", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
}
