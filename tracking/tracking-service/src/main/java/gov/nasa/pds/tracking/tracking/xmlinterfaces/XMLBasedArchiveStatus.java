/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.xmlinterfaces;

import org.apache.log4j.Logger;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.pds.tracking.tracking.db.ArchiveStatus;

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
		
		ArchiveStatus status;
		try {
											
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("archive_status");
            
            status = new ArchiveStatus();
			List<ArchiveStatus> statuses = status.getArchiveStatusOrderByVersion();
						
			logger.info("number of Archive Status: "  + statuses.size());
            
			if (statuses.size() > 0){
				
	            Iterator<ArchiveStatus> itr = statuses.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					ArchiveStatus as = itr.next();
					logger.debug("Archive Status " + count + ":\n " + as.getLogIdentifier() + " : " + as.getStatus());			     	 
			        
					Element subRootElement = doc.createElement("status");
					
		            Element idElement = doc.createElement(ArchiveStatus.LOGIDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode(as.getLogIdentifier()));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(ArchiveStatus.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(as.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element dateElement = doc.createElement(ArchiveStatus.DATECOLUMN);
		            dateElement.appendChild(doc.createTextNode(as.getDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element statusElement = doc.createElement(ArchiveStatus.STATUSCOLUMN);
		            statusElement.appendChild(doc.createTextNode(as.getStatus()));
		            subRootElement.appendChild(statusElement);
		            
		            Element emailElement = doc.createElement(ArchiveStatus.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(as.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(ArchiveStatus.COMMENTCOLUMN);
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
		
		ArchiveStatus aStatus;
		try {
											
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("archive_status");
            
            aStatus = new ArchiveStatus();
			List<ArchiveStatus> aStatuses = new ArrayList<ArchiveStatus>();
			
			if (latest) {
				aStatus = aStatus.getLatestArchiveStatus(id, version);
				if (aStatus != null)
				aStatuses.add(aStatus);
			}else{
				aStatuses = aStatus.getArchiveStatusList(id, version);
			}
						
			logger.info("number of Archive Status: "  + aStatuses.size());
            
			if (aStatuses.size() > 0){
				
	            Iterator<ArchiveStatus> itr = aStatuses.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					ArchiveStatus as = itr.next();
					logger.debug("Archive Status " + count + ":\n " + as.getLogIdentifier() + " : " + as.getStatus());			     	 
			        
					Element subRootElement = doc.createElement("status");
					
		            Element idElement = doc.createElement(ArchiveStatus.LOGIDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode(as.getLogIdentifier()));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(ArchiveStatus.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(as.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element dateElement = doc.createElement(ArchiveStatus.DATECOLUMN);
		            dateElement.appendChild(doc.createTextNode(as.getDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element statusElement = doc.createElement(ArchiveStatus.STATUSCOLUMN);
		            statusElement.appendChild(doc.createTextNode(as.getStatus()));
		            subRootElement.appendChild(statusElement);
		            
		            Element emailElement = doc.createElement(ArchiveStatus.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(as.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(ArchiveStatus.COMMENTCOLUMN);
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
	
}
