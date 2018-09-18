/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.xmlinterfaces;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Date;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.pds.tracking.tracking.db.DBConnector;
import gov.nasa.pds.tracking.tracking.db.Doi;
import gov.nasa.pds.tracking.tracking.db.DoiDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/doi")
public class XMLBasedDOI {
	
	public static Logger logger = Logger.getLogger(XMLBasedDOI.class);

	/**
	 * @return
	 */
	@GET
    @Produces(MediaType.APPLICATION_XML)
    public Response defaultDOI(){
 
		StringBuffer xmlOutput = new StringBuffer();
        
        DoiDao doi;
		try {
			
			doi = new DoiDao();
			List<Doi> dois = doi.getDOIList();
			logger.info("number of DOIs: "  + dois.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder doiBuilder;

			doiBuilder = factory.newDocumentBuilder();
            Document doc = doiBuilder.newDocument();
            Element rootElement = doc.createElement("dois");
            
			if (dois.size() > 0){
				Iterator<Doi> itr = dois.iterator();
				int count = 1;
				
				while(itr.hasNext()) {
			         Doi d = itr.next();
			         logger.debug("DOIs " + count + ":\n " + d.getLog_identifier() + " : " + d.getDoi());
			         
			         Element subRootElement = doc.createElement("doi");
	
			        Element idElement = doc.createElement(Doi.LOG_IDENTIFIERCOLUME);
		            idElement.appendChild(doc.createTextNode((d.getLog_identifier())));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(Doi.VERSIONCOLUME);
		            verElement.appendChild(doc.createTextNode(d.getVersion()));
		            subRootElement.appendChild(verElement);
			         
		            Element doiElement = doc.createElement(Doi.DOICOLUME);
		            doiElement.appendChild(doc.createTextNode(d.getDoi()));
		            subRootElement.appendChild(doiElement);
		            
		            Element dateElement = doc.createElement(Doi.DATECOLUME);
		            dateElement.appendChild(doc.createTextNode(d.getDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element urlElement = doc.createElement(Doi.URLCOLUME);
		            urlElement.appendChild(doc.createTextNode(d.getUrl()));
		            subRootElement.appendChild(urlElement);
		            
		            Element emailElement = doc.createElement(Doi.EMAILCOLUME);
		            emailElement.appendChild(doc.createTextNode(d.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(Doi.COMMENTCOLUME);
		            commentElement.appendChild(doc.createTextNode(d.getComment() != null ? d.getComment() : ""));
		            subRootElement.appendChild(commentElement);
		            
		            rootElement.appendChild(subRootElement);
	
			        count++;
			    }						
                    
			}else{
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Can Not find any Doi!!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, result);
            
            logger.debug("DOIs:\n" + writer.toString());
            
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
	
	/**
	 * @param id
	 * @param version
	 * @return
	 */
	@Path("{id : (.+)?}/{version : (.+)?}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response DOI(@PathParam("id") String id, @PathParam("version") String version){
		
		StringBuffer xmlOutput = new StringBuffer();
        
        DoiDao dd;
		try {
			
			dd = new DoiDao();
			Doi d = dd.getDOI(id, version);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder doiBuilder;

			doiBuilder = factory.newDocumentBuilder();
            Document doc = doiBuilder.newDocument();
            Element rootElement = doc.createElement("dois");
			if (d != null){         
			         Element subRootElement = doc.createElement("doi");
	
			        Element idElement = doc.createElement(Doi.LOG_IDENTIFIERCOLUME);
		            idElement.appendChild(doc.createTextNode((d.getLog_identifier())));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(Doi.VERSIONCOLUME);
		            verElement.appendChild(doc.createTextNode(d.getVersion()));
		            subRootElement.appendChild(verElement);
			         
		            Element doiElement = doc.createElement(Doi.DOICOLUME);
		            doiElement.appendChild(doc.createTextNode(d.getDoi()));
		            subRootElement.appendChild(doiElement);
		            
		            Element dateElement = doc.createElement(Doi.DATECOLUME);
		            dateElement.appendChild(doc.createTextNode(d.getDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element urlElement = doc.createElement(Doi.URLCOLUME);
		            urlElement.appendChild(doc.createTextNode(d.getUrl()));
		            subRootElement.appendChild(urlElement);
		            
		            Element emailElement = doc.createElement(Doi.EMAILCOLUME);
		            emailElement.appendChild(doc.createTextNode(d.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(Doi.COMMENTCOLUME);
		            commentElement.appendChild(doc.createTextNode(d.getComment() != null ? d.getComment() : ""));
		            subRootElement.appendChild(commentElement);
		            
		            rootElement.appendChild(subRootElement);                    
			}else{
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Can Not find any Doi for " + id + ", " + version));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, result);
            
            logger.debug("DOIs:\n" + writer.toString());
            
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
	@Path("/add")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createDOI(@FormParam("LOGICAL_ID") String id,
			@FormParam("Version") String ver,
			@FormParam("Doi") String doi,
			@FormParam("URL") String url,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{

		StringBuffer xmlOutput = new StringBuffer();
		DoiDao dd;
		
		try {
			dd = new DoiDao();
		
			String currentTime = DBConnector.ISO_BASIC.format(new Date());
			Doi d = new Doi(id, ver, doi, currentTime, url, email, comment);
			
			int result = dd.insertDOI(d);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder doiBuilder;

			doiBuilder = factory.newDocumentBuilder();
            Document doc = doiBuilder.newDocument();
            Element rootElement = doc.createElement("dois");
            
			if(result == 1){
				Element subRootElement = doc.createElement("doi");
				
		        Element idElement = doc.createElement(Doi.LOG_IDENTIFIERCOLUME);
	            idElement.appendChild(doc.createTextNode((d.getLog_identifier())));
	            subRootElement.appendChild(idElement);
	            
	            Element verElement = doc.createElement(Doi.VERSIONCOLUME);
	            verElement.appendChild(doc.createTextNode(d.getVersion()));
	            subRootElement.appendChild(verElement);
		         
	            Element doiElement = doc.createElement(Doi.DOICOLUME);
	            doiElement.appendChild(doc.createTextNode(d.getDoi()));
	            subRootElement.appendChild(doiElement);
	            
	            Element dateElement = doc.createElement(Doi.DATECOLUME);
	            dateElement.appendChild(doc.createTextNode(d.getDate()));
	            subRootElement.appendChild(dateElement);
	            
	            Element urlElement = doc.createElement(Doi.URLCOLUME);
	            urlElement.appendChild(doc.createTextNode(d.getUrl()));
	            subRootElement.appendChild(urlElement);
	            
	            Element emailElement = doc.createElement(Doi.EMAILCOLUME);
	            emailElement.appendChild(doc.createTextNode(d.getEmail()));
	            subRootElement.appendChild(emailElement);
	            
	            Element commentElement = doc.createElement(Doi.COMMENTCOLUME);
	            commentElement.appendChild(doc.createTextNode(d.getComment() != null ? d.getComment() : ""));
	            subRootElement.appendChild(commentElement);
	            
	            rootElement.appendChild(subRootElement);                    
		}else{
			Element messageElement = doc.createElement("Message");
			messageElement.appendChild(doc.createTextNode("Add Doi for " + id + ", " + ver + " failure!"));
			rootElement.appendChild(messageElement);
		}
		doc.appendChild(rootElement);
		
		DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult strResult = new StreamResult(writer);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, strResult);
        
        logger.debug("DOIs:\n" + writer.toString());
        
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
	public Response updateDOI(@FormParam("LOGICAL_ID") String id,
			@FormParam("Version") String ver,
			@FormParam("URL") String url,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{
		
		StringBuffer xmlOutput = new StringBuffer();
		DoiDao dd;
		try {
			dd = new DoiDao();
		
			Doi d = new Doi(id, ver, null, null, url, email, comment);
			
			int result = dd.updateDOI(d);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder doiBuilder;

			doiBuilder = factory.newDocumentBuilder();
            Document doc = doiBuilder.newDocument();
            Element rootElement = doc.createElement("dois");
			
			if(result == 1){		
				//get doi and registration_date for updated DOI 
				Doi updatedDOI = dd.getDOI(id, ver);
				Element subRootElement = doc.createElement("doi");
				
		        Element idElement = doc.createElement(Doi.LOG_IDENTIFIERCOLUME);
	            idElement.appendChild(doc.createTextNode((updatedDOI.getLog_identifier())));
	            subRootElement.appendChild(idElement);
	            
	            Element verElement = doc.createElement(Doi.VERSIONCOLUME);
	            verElement.appendChild(doc.createTextNode(updatedDOI.getVersion()));
	            subRootElement.appendChild(verElement);
		         
	            Element doiElement = doc.createElement(Doi.DOICOLUME);
	            doiElement.appendChild(doc.createTextNode(updatedDOI.getDoi()));
	            subRootElement.appendChild(doiElement);
	            
	            Element dateElement = doc.createElement(Doi.DATECOLUME);
	            dateElement.appendChild(doc.createTextNode(updatedDOI.getDate()));
	            subRootElement.appendChild(dateElement);
	            
	            Element urlElement = doc.createElement(Doi.URLCOLUME);
	            urlElement.appendChild(doc.createTextNode(updatedDOI.getUrl()));
	            subRootElement.appendChild(urlElement);
	            
	            Element emailElement = doc.createElement(Doi.EMAILCOLUME);
	            emailElement.appendChild(doc.createTextNode(updatedDOI.getEmail()));
	            subRootElement.appendChild(emailElement);
	            
	            Element commentElement = doc.createElement(Doi.COMMENTCOLUME);
	            commentElement.appendChild(doc.createTextNode(updatedDOI.getComment() != null ? d.getComment() : ""));
	            subRootElement.appendChild(commentElement);
	            
	            rootElement.appendChild(subRootElement);                    
			}else{
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Update Doi for " + id + ", " + ver + " failure!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
	        StringWriter writer = new StringWriter();
	        StreamResult strResult = new StreamResult(writer);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.transform(domSource, strResult);
	        
	        logger.debug("DOIs:\n" + writer.toString());
	        
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
