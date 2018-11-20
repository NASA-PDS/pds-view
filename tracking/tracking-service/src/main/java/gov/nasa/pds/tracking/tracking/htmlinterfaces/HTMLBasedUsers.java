package gov.nasa.pds.tracking.tracking.htmlinterfaces;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import gov.nasa.pds.tracking.tracking.db.Reference;
import gov.nasa.pds.tracking.tracking.db.RoleDao;
import gov.nasa.pds.tracking.tracking.db.User;
import gov.nasa.pds.tracking.tracking.db.UserDao;
import gov.nasa.pds.tracking.tracking.utils.HtmlConstants;
 
@Path("html/users")
public class HTMLBasedUsers {
	
	public static Logger logger = Logger.getLogger(HTMLBasedUsers.class);
	
    @GET
    @Produces("text/html")
    public String defaultUsers() {

    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +	
    			  "<h2>Users</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 10\" >" +
    			  "<tr align=\"center\">" +
    			  "<td width=\"50%\"><b>Name</b></td>"+
	              "<td width=\"50%\"><b>Email</b></td>" +
    			  "</tr>");
    	UserDao userD;
		try {
			userD = new UserDao();
			List<User> users = userD.getUsers();
			logger.info("number of users: "  + users.size());
							         
			Iterator<User> itr = users.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         User u = itr.next();
		         logger.debug("User " + count + ":\n " + u.getUserName() + " : " + u.getUserEmail());
		         sb.append("<tr>" +
		    			  "<td>" + u.getUserName() + "</td>" +	  					 
			              "<td>" + u.getUserEmail() + "</td>" +
		    			  "</tr>");
		         count++;
		    }

			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
    			
        
        sb.append("</table></div>");
 
        return HtmlConstants.PAGE_BEGIN + sb.toString() + HtmlConstants.PAGE_END;
    }
    
    @Path("{email}")
    @GET
    @Produces("text/html")
	public String userRole(@PathParam("email") String email) {
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +	
    			  "<h2> Roles for " + email + " </h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 10\" >" +
    			  "<tr align=\"center\">" +
    			  "<td width=\"30%\"><b>Name</b></td>"+
	              "<td width=\"30%\"><b>Email</b></td>" +
	              "<td width=\"40%\"><b>Role References</b></td>" +
	              "</tr>");
        
		UserDao userD;
		try {
			userD = new UserDao();
			List<User> users = userD.getUserRole(email);
			logger.info("Number of Role References for " + email + ": "  + users.size());
			Iterator<User> itr = users.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         User u = itr.next();
		         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getReference());
		         sb.append("<tr>" +
		    			  "<td>" + u.getUserName() + "</td>" +		    			  					
			              "<td>" + u.getUserEmail() + "</td>" +
			              "<td>" + u.getReference() + "</td>" +
			              "</tr>");
		         count++;
		    }			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
    			
        
        sb.append("</table></div>");
 
        return HtmlConstants.PAGE_BEGIN + sb.toString() + HtmlConstants.PAGE_END;
    }
    
    @Path("{id : (.+)?}/{refType : (.+)?}")
    @GET
    @Produces("text/html")
    public String userProductRole(@PathParam("id") String id, @PathParam("refType") String refType){
    	StringBuilder sb = new StringBuilder();
    	sb.append("<h1>Tracking Service</h1>" +	
    			  "<h2>Users with " + refType + " for product " + id + "</h2>" +
	              "<div>" +
    			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 10\" >" +
    			  "<tr align=\"center\">" +
    			  "<td width=\"20%\"><b>Email</b></td>"+
	              "<td width=\"20%\"><b>Name</b></td>" +
	              "<td width=\"25%\"><b>" + Reference.TITLECOLUMN + "</b></td>" +
	              "<td width=\"25%\"><b>" + RoleDao.REFERENCECOLUMN + "</b></td>" +
	              "</tr>");

		UserDao userD;
		try {
			userD = new UserDao();
			List<User> users = userD.getProductRoleUsers(id, refType);
			logger.info("Number of " + refType  + " for " + id + ": "  + users.size());
			Iterator<User> itr = users.iterator();
			int count = 1;
			while(itr.hasNext()) {
		         User u = itr.next();
		         
		         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName() + " : " + u.getType() + " : " + u.getReference());
		         
		         sb.append("<tr>" +
		    			  "<td>" + u.getUserEmail() + "</td>" +
		        		  "<td>" + u.getUserName() + "</td>" +
			              "<td>" + u.getType() + "</td>" +
			              "<td>" + u.getReference() + "</td>" + "</tr>");
		         count++;
		    }
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
    			
        
        sb.append("</table></div>");
 
        return HtmlConstants.PAGE_BEGIN + sb.toString() + HtmlConstants.PAGE_END;
    }
}
