/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.xmlinterfaces;

import java.io.IOException;
import java.io.StringWriter;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.pds.tracking.tracking.db.Reference;
import gov.nasa.pds.tracking.tracking.db.Role;
import gov.nasa.pds.tracking.tracking.db.RoleDao;
import gov.nasa.pds.tracking.tracking.db.User;
import gov.nasa.pds.tracking.tracking.db.UserDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/users")
public class XMLBasedUsers {

	public static Logger logger = Logger.getLogger(XMLBasedUsers.class);
	
	private UserDao uD;
	private RoleDao rD;
	
	@GET
    @Produces(MediaType.APPLICATION_XML)
    public Response defaultUsers() {
 
		StringBuffer xmlOutput = new StringBuffer();
        
        UserDao userD;
		try {
			userD = new UserDao();
			List<User> users = userD.getUsers();
			logger.info("number of users: "  + users.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder usersBuilder;

            usersBuilder = factory.newDocumentBuilder();
            Document doc = usersBuilder.newDocument();
            Element rootElement = doc.createElement("users");
            
			if (users.size() > 0){
				Iterator<User> itr = users.iterator();
				int count = 1;
				
				while(itr.hasNext()) {
			         User u = itr.next();
			         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName());
			         
			         Element subRootElement = doc.createElement("user");

		            Element emailElement = doc.createElement(UserDao.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(u.getUserEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element nameElement = doc.createElement(UserDao.NAMECOLUMN);
		            nameElement.appendChild(doc.createTextNode(u.getUserName()));
		            subRootElement.appendChild(nameElement);		            
		            		            
		            rootElement.appendChild(subRootElement);
		            count++;
			    }
				doc.appendChild(rootElement);
				
				DOMSource domSource = new DOMSource(doc);
	            StringWriter writer = new StringWriter();
	            StreamResult result = new StreamResult(writer);
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();
	            transformer.transform(domSource, result);
	            
	            logger.debug("Users:\n" + writer.toString());
	            
				xmlOutput.append(writer.toString());            
        
			}else{
				xmlOutput.append("Can Not find any User!");
			}			
		} catch (ParserConfigurationException ex) {
			logger.error(ex);
        } catch (TransformerConfigurationException ex) {
        	logger.error(ex);
        }catch (TransformerException ex) {
        	logger.error(ex);
        } catch (ClassNotFoundException ex) {
        	logger.error(ex);
		} catch (SQLException ex) {
			logger.error(ex);
		}

        return Response.status(200).entity(xmlOutput.toString()).build();
    }

	@Path("{email}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
	public Response userRole(@PathParam("email") String email) {
		
		StringBuffer xmlOutput = new StringBuffer();
        
        UserDao userD;
		try {
			userD = new UserDao();
			List<User> users = userD.getUserRole(email);
			logger.info("number of users: "  + users.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder usersBuilder;

            usersBuilder = factory.newDocumentBuilder();
            Document doc = usersBuilder.newDocument();
            Element rootElement = doc.createElement("users");
            
			if (users.size() > 0){
				Iterator<User> itr = users.iterator();
				int count = 1;
				
				while(itr.hasNext()) {
			         User u = itr.next();
			         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName() + " : " + u.getReference());
			         
			         Element subRootElement = doc.createElement("user");
	
			            Element emailElement = doc.createElement(UserDao.EMAILCOLUMN);
			            emailElement.appendChild(doc.createTextNode(u.getUserEmail()));
			            subRootElement.appendChild(emailElement);
			            
			            Element nameElement = doc.createElement(UserDao.NAMECOLUMN);
			            nameElement.appendChild(doc.createTextNode(u.getUserName()));
			            subRootElement.appendChild(nameElement);
			            
			            Element refElement = doc.createElement("role_" + RoleDao.REFERENCECOLUMN);
			            refElement.appendChild(doc.createTextNode(u.getReference()));
			            subRootElement.appendChild(refElement);
			            		            
			            rootElement.appendChild(subRootElement);
	
			         count++;
			    }
				doc.appendChild(rootElement);
				
				DOMSource domSource = new DOMSource(doc);
	            StringWriter writer = new StringWriter();
	            StreamResult result = new StreamResult(writer);
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();
	            transformer.transform(domSource, result);
	            
	            logger.debug("Users:\n" + writer.toString());
	            
				xmlOutput.append(writer.toString());            
	    
			}else{
				xmlOutput.append("Can Not find any User!");
			}			
		} catch (ParserConfigurationException ex) {
			logger.error(ex);
	    } catch (TransformerConfigurationException ex) {
	    	logger.error(ex);
	    }catch (TransformerException ex) {
	    	logger.error(ex);
	    } catch (ClassNotFoundException ex) {
	    	logger.error(ex);
		} catch (SQLException ex) {
			logger.error(ex);
		}
	
	    return Response.status(200).entity(xmlOutput.toString()).build();
	}
	
	@Path("{id : (.+)?}/{refType : (.+)?}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response userProductRole(@PathParam("id") String id, @PathParam("refType") String refType) {
		
		StringBuffer xmlOutput = new StringBuffer();
        
        UserDao userD;
		try {
			userD = new UserDao();
			List<User> users = userD.getProductRoleUsers(id, refType);
			logger.info("number of users: "  + users.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder usersBuilder;

            usersBuilder = factory.newDocumentBuilder();
            Document doc = usersBuilder.newDocument();
            Element rootElement = doc.createElement("users");
            
			if (users.size() > 0){
				
				Iterator<User> itr = users.iterator();
				int count = 1;
				
				
				while(itr.hasNext()) {
			        User u = itr.next();
			        logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName() + " : " + u.getType() + " : " + u.getReference());
			         
			        Element subRootElement = doc.createElement("user");
			     	
		            Element emailElement = doc.createElement(UserDao.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(u.getUserEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element nameElement = doc.createElement(UserDao.NAMECOLUMN);
		            nameElement.appendChild(doc.createTextNode(u.getUserName()));
		            subRootElement.appendChild(nameElement);
		            
		            Element refElement = doc.createElement("role_" + RoleDao.REFERENCECOLUMN);
		            refElement.appendChild(doc.createTextNode(u.getReference()));
		            subRootElement.appendChild(refElement);
		            
		            Element titleElement = doc.createElement("role_" + Reference.TITLECOLUMN);
		            titleElement.appendChild(doc.createTextNode(u.getType()));
		            subRootElement.appendChild(titleElement);
		            		            
		            rootElement.appendChild(subRootElement);
	
			         count++;
			    }
				doc.appendChild(rootElement);
				
				DOMSource domSource = new DOMSource(doc);
	            StringWriter writer = new StringWriter();
	            StreamResult result = new StreamResult(writer);
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();
	            transformer.transform(domSource, result);
	            
	            logger.debug("Users:\n" + writer.toString());
	            
				xmlOutput.append(writer.toString());            
	    
			}else{
				xmlOutput.append("Can Not find any User!");
			}			
		} catch (ParserConfigurationException ex) {
			logger.error(ex);
	    } catch (TransformerConfigurationException ex) {
	    	logger.error(ex);
	    }catch (TransformerException ex) {
	    	logger.error(ex);
	    } catch (ClassNotFoundException ex) {
	    	logger.error(ex);
		} catch (SQLException ex) {
			logger.error(ex);
		}
	
	    return Response.status(200).entity(xmlOutput.toString()).build();
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createUser(@FormParam("Email") String email, @FormParam("Name") String name) throws IOException{
		
		StringBuffer xmlOutput = new StringBuffer();
		
		try {
			uD = new UserDao();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder usersBuilder;

            usersBuilder = factory.newDocumentBuilder();
            Document doc = usersBuilder.newDocument();
            Element rootElement = doc.createElement("user");
            
			User user = new User(email, name);
			int result = uD.insertUser(user);
			
			if(result == 1){
				Element emailElement = doc.createElement(UserDao.EMAILCOLUMN);
	            emailElement.appendChild(doc.createTextNode(user.getUserEmail()));
	            rootElement.appendChild(emailElement);
	            
	            Element nameElement = doc.createElement(UserDao.NAMECOLUMN);
	            nameElement.appendChild(doc.createTextNode(user.getUserName()));
	            rootElement.appendChild(nameElement);
		    }else{
			
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Add user for " + email  + " failure!"));
				rootElement.appendChild(messageElement);
			}
		doc.appendChild(rootElement);
		
		DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult strResult = new StreamResult(writer);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, strResult);
        
        logger.debug("User:\n" + writer.toString());
        
		xmlOutput.append(writer.toString());
        
	} catch (ParserConfigurationException ex) {
		logger.error(ex);
    } catch (TransformerConfigurationException ex) {
    	logger.error(ex);
    }catch (TransformerException ex) {
    	logger.error(ex);
    } catch (ClassNotFoundException ex) {
    	logger.error(ex);
	} catch (SQLException ex) {
		logger.error(ex);
	}

		return Response.status(200).entity(xmlOutput.toString()).build();
	}
	
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response updateUser(@FormParam("Email") String email, @FormParam("Name") String name) throws IOException{
		
		StringBuffer xmlOutput = new StringBuffer();
		
		try {
			uD = new UserDao();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder usersBuilder;

            usersBuilder = factory.newDocumentBuilder();
            Document doc = usersBuilder.newDocument();
            Element rootElement = doc.createElement("user");
            
			User user = new User(email, name);
			User updatedUser = uD.updateUser(user);
			
			if(updatedUser != null){
				Element emailElement = doc.createElement(UserDao.EMAILCOLUMN);
	            emailElement.appendChild(doc.createTextNode(updatedUser.getUserEmail()));
	            rootElement.appendChild(emailElement);
	            
	            Element nameElement = doc.createElement(UserDao.NAMECOLUMN);
	            nameElement.appendChild(doc.createTextNode(updatedUser.getUserName()));
	            rootElement.appendChild(nameElement);
		    }else{
			
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Updated user for " + email  + " failure!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
	        StringWriter writer = new StringWriter();
	        StreamResult strResult = new StreamResult(writer);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.transform(domSource, strResult);
	        
	        logger.debug("Updated user:\n" + writer.toString());
	        
			xmlOutput.append(writer.toString());
        
		} catch (ParserConfigurationException ex) {
			logger.error(ex);
	    } catch (TransformerConfigurationException ex) {
	    	logger.error(ex);
	    }catch (TransformerException ex) {
	    	logger.error(ex);
	    } catch (ClassNotFoundException ex) {
	    	logger.error(ex);
		} catch (SQLException ex) {
			logger.error(ex);
		}
	
	    return Response.status(200).entity(xmlOutput.toString()).build();
	}
	
	@POST
	@Path("/addrole")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createRole(@FormParam("Email") String email, @FormParam("Reference") String ref) throws IOException{
		
		StringBuffer xmlOutput = new StringBuffer();
		
		try {
			rD = new RoleDao();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder roleBuilder;

            roleBuilder = factory.newDocumentBuilder();
            Document doc = roleBuilder.newDocument();
            Element rootElement = doc.createElement("reference");
            
            Role role = new Role(email, ref);
			int result = rD.insertRole(role);
			
			if(result == 1){
				Element emailElement = doc.createElement(RoleDao.EMAILCOLUMN);
	            emailElement.appendChild(doc.createTextNode(role.getEmail()));
	            rootElement.appendChild(emailElement);
	            
	            Element nameElement = doc.createElement(RoleDao.REFERENCECOLUMN);
	            nameElement.appendChild(doc.createTextNode(role.getReference()));
	            rootElement.appendChild(nameElement);
		    }else{
			
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Add role for " + email  + " failure!"));
				rootElement.appendChild(messageElement);
			}
		doc.appendChild(rootElement);
		
		DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult strResult = new StreamResult(writer);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, strResult);
        
        logger.debug("User:\n" + writer.toString());
        
		xmlOutput.append(writer.toString());
        
	} catch (ParserConfigurationException ex) {
		logger.error(ex);
    } catch (TransformerConfigurationException ex) {
    	logger.error(ex);
    }catch (TransformerException ex) {
    	logger.error(ex);
    } catch (ClassNotFoundException ex) {
    	logger.error(ex);
	} catch (SQLException ex) {
		logger.error(ex);
	}

    return Response.status(200).entity(xmlOutput.toString()).build();
	}
}
