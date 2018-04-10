/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.xmlinterfaces;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import gov.nasa.pds.tracking.tracking.db.User;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/users")
public class XMLBasedUsers {

	public static Logger logger = Logger.getLogger(XMLBasedUsers.class);

	@GET
    @Produces("application/xml")
    public Response defaultUsers() {
 
		StringBuffer xmlOutput = new StringBuffer();
        
        User user;
		try {
			user = new User();
			List<User> users = user.getUsers();
			logger.info("number of users: "  + users.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder prodsBuilder;

			prodsBuilder = factory.newDocumentBuilder();
            Document doc = prodsBuilder.newDocument();
            Element rootElement = doc.createElement("users");
            
			if (users.size() > 0){
				Iterator<User> itr = users.iterator();
				int count = 1;
				
				while(itr.hasNext()) {
			         User u = itr.next();
			         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName());
			         
			         Element subRootElement = doc.createElement("user");

		            Element emailElement = doc.createElement(User.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(u.getUserEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element nameElement = doc.createElement(User.NAMECOLUMN);
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
    @Produces("application/xml")
	public Response userRole(@PathParam("email") String email) {
		
		StringBuffer xmlOutput = new StringBuffer();
        
        User user;
		try {
			user = new User();
			List<User> users = user.getUserRole(email);
			logger.info("number of users: "  + users.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder prodsBuilder;

			prodsBuilder = factory.newDocumentBuilder();
            Document doc = prodsBuilder.newDocument();
            Element rootElement = doc.createElement("users");
            
			if (users.size() > 0){
				Iterator<User> itr = users.iterator();
				int count = 1;
				
				while(itr.hasNext()) {
			         User u = itr.next();
			         logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName() + " : " + u.getReference());
			         
			         Element subRootElement = doc.createElement("user");
	
			            Element emailElement = doc.createElement(User.EMAILCOLUMN);
			            emailElement.appendChild(doc.createTextNode(u.getUserEmail()));
			            subRootElement.appendChild(emailElement);
			            
			            Element nameElement = doc.createElement(User.NAMECOLUMN);
			            nameElement.appendChild(doc.createTextNode(u.getUserName()));
			            subRootElement.appendChild(nameElement);
			            
			            Element refElement = doc.createElement("role_" + Role.REFERENCECOLUMN);
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
    @Produces("application/xml")
    public Response userProductRole(@PathParam("id") String id, @PathParam("refType") String refType) {
		
		StringBuffer xmlOutput = new StringBuffer();
        
        User user;
		try {
			user = new User();
			List<User> users = user.getProductRoleUsers(id, refType);
			logger.info("number of users: "  + users.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder prodsBuilder;

			prodsBuilder = factory.newDocumentBuilder();
            Document doc = prodsBuilder.newDocument();
            Element rootElement = doc.createElement("users");
            
			if (users.size() > 0){
				
				Iterator<User> itr = users.iterator();
				int count = 1;
				
				
				while(itr.hasNext()) {
			        User u = itr.next();
			        logger.debug("User " + count + ":\n " + u.getUserEmail() + " : " + u.getUserName() + " : " + u.getType() + " : " + u.getReference());
			         
			        Element subRootElement = doc.createElement("user");
			     	
		            Element emailElement = doc.createElement(User.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(u.getUserEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element nameElement = doc.createElement(User.NAMECOLUMN);
		            nameElement.appendChild(doc.createTextNode(u.getUserName()));
		            subRootElement.appendChild(nameElement);
		            
		            Element refElement = doc.createElement("role_" + Role.REFERENCECOLUMN);
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
}
