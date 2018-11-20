/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.xmlinterfaces;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.pds.tracking.tracking.db.ArchiveStatus;
import gov.nasa.pds.tracking.tracking.db.ArchiveStatusDao;
import gov.nasa.pds.tracking.tracking.db.DBConnector;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/archivestatus")
public class XMLBasedArchiveStatus {
	
	public static Logger logger = Logger.getLogger(XMLBasedArchiveStatus.class);

	@GET
    @Produces("application/xml")
    public Response defaultArchiveStatus() {
        
		StringBuffer xmlOutput = new StringBuffer();
		
		ArchiveStatusDao status;
		try {
											
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("archive_status");
            
            status = new ArchiveStatusDao();
			List<ArchiveStatus> statuses = status.getArchiveStatusOrderByVersion();
						
			logger.info("number of Archive Status: "  + statuses.size());
            
			if (statuses.size() > 0){
				
	            Iterator<ArchiveStatus> itr = statuses.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					ArchiveStatus as = itr.next();
					logger.debug("Archive Status " + count + ":\n " + as.getLogIdentifier() + " : " + as.getStatus());			     	 
			        
					Element subRootElement = doc.createElement("status");
					
		            Element idElement = doc.createElement(ArchiveStatusDao.LOGIDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode(as.getLogIdentifier()));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(ArchiveStatusDao.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(as.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element dateElement = doc.createElement(ArchiveStatusDao.DATECOLUMN);
		            dateElement.appendChild(doc.createTextNode(as.getDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element statusElement = doc.createElement(ArchiveStatusDao.STATUSCOLUMN);
		            statusElement.appendChild(doc.createTextNode(as.getStatus()));
		            subRootElement.appendChild(statusElement);
		            
		            Element emailElement = doc.createElement(ArchiveStatusDao.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(as.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(ArchiveStatusDao.COMMENTCOLUMN);
		            commentElement.appendChild(doc.createTextNode(as.getComment() != null ? as.getComment() : ""));
		            subRootElement.appendChild(commentElement);
		            
		            rootElement.appendChild(subRootElement);

		            count++;
				}
			}					
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, result);
            
            logger.debug("Archive Status:\n" + writer.toString());
            
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

	@Path("{id : (.+)?}/{version : (.+)?}/{latest}")
    @GET
    @Produces("application/xml")
	public Response archiveStatus(@PathParam("id") String id, @PathParam("version") String version, @PathParam("latest") boolean latest){
		
		StringBuffer xmlOutput = new StringBuffer();
		
		ArchiveStatusDao aStatusO;
		ArchiveStatus aStatus;
		try {
											
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("archive_status");
            
            aStatusO = new ArchiveStatusDao();
			List<ArchiveStatus> aStatuses = new ArrayList<ArchiveStatus>();
			
			if (latest) {
				aStatus = aStatusO.getLatestArchiveStatus(id, version);
				if (aStatus != null)
				aStatuses.add(aStatus);
			}else{
				aStatuses = aStatusO.getArchiveStatusList(id, version);
			}
						
			logger.info("number of Archive Status: "  + aStatuses.size());
            
			if (aStatuses.size() > 0){
				
	            Iterator<ArchiveStatus> itr = aStatuses.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					ArchiveStatus as = itr.next();
					logger.debug("Archive Status " + count + ":\n " + as.getLogIdentifier() + " : " + as.getStatus());			     	 
			        
					Element subRootElement = doc.createElement("status");
					
		            Element idElement = doc.createElement(ArchiveStatusDao.LOGIDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode(as.getLogIdentifier()));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(ArchiveStatusDao.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(as.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element dateElement = doc.createElement(ArchiveStatusDao.DATECOLUMN);
		            dateElement.appendChild(doc.createTextNode(as.getDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element statusElement = doc.createElement(ArchiveStatusDao.STATUSCOLUMN);
		            statusElement.appendChild(doc.createTextNode(as.getStatus()));
		            subRootElement.appendChild(statusElement);
		            
		            Element emailElement = doc.createElement(ArchiveStatusDao.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(as.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(ArchiveStatusDao.COMMENTCOLUMN);
		            commentElement.appendChild(doc.createTextNode(as.getComment() != null ? as.getComment() : ""));
		            subRootElement.appendChild(commentElement);
		            
		            rootElement.appendChild(subRootElement);

		            count++;
				}
			}					
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, result);
            
            logger.debug("Archive Status:\n" + writer.toString());
            
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
	public Response createArchiveStatus(@FormParam("LogicalIdentifier") String logicalIdentifier,
			@FormParam("Version") String ver,
			@FormParam("Status") String status,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{

		StringBuffer xmlOutput = new StringBuffer();
		ArchiveStatusDao asD;
		
		try {
			asD = new ArchiveStatusDao();

			String currentTime = DBConnector.ISO_BASIC.format(new Date());
			ArchiveStatus as = new ArchiveStatus(logicalIdentifier, ver, currentTime, status, email, comment);
			
			int result = asD.insertArchiveStatus(as);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder doiBuilder;

			doiBuilder = factory.newDocumentBuilder();
            Document doc = doiBuilder.newDocument();
            Element rootElement = doc.createElement("archive_status");
            
			if(result == 1){
				Element subRootElement = doc.createElement("status");
				
				Element idElement = doc.createElement(ArchiveStatusDao.LOGIDENTIFIERCOLUMN);
	            idElement.appendChild(doc.createTextNode(as.getLogIdentifier()));
	            subRootElement.appendChild(idElement);
	            
	            Element verElement = doc.createElement(ArchiveStatusDao.VERSIONCOLUMN);
	            verElement.appendChild(doc.createTextNode(as.getVersion()));
	            subRootElement.appendChild(verElement);
	            
	            Element dateElement = doc.createElement(ArchiveStatusDao.DATECOLUMN);
	            dateElement.appendChild(doc.createTextNode(as.getDate()));
	            subRootElement.appendChild(dateElement);
	            
	            Element statusElement = doc.createElement(ArchiveStatusDao.STATUSCOLUMN);
	            statusElement.appendChild(doc.createTextNode(as.getStatus()));
	            subRootElement.appendChild(statusElement);
	            
	            Element emailElement = doc.createElement(ArchiveStatusDao.EMAILCOLUMN);
	            emailElement.appendChild(doc.createTextNode(as.getEmail()));
	            subRootElement.appendChild(emailElement);
	            
	            Element commentElement = doc.createElement(ArchiveStatusDao.COMMENTCOLUMN);
	            commentElement.appendChild(doc.createTextNode(as.getComment() != null ? as.getComment() : ""));
	            subRootElement.appendChild(commentElement);
	            
	            rootElement.appendChild(subRootElement);                    
		}else{
			Element messageElement = doc.createElement("Message");
			messageElement.appendChild(doc.createTextNode("Add archive status for " + as.getLogIdentifier() + ", " + as.getVersion() + " failure!"));
			rootElement.appendChild(messageElement);
		}
		doc.appendChild(rootElement);
		
		DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult strResult = new StreamResult(writer);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, strResult);
        
        logger.debug("archive status:\n" + writer.toString());
        
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
