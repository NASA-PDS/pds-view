/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.xmlinterfaces;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
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

import gov.nasa.pds.tracking.tracking.db.NssdcaStatus;;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/nssdcastatus")
public class XMLBasedNssdcaStatus {
	
	public static Logger logger = Logger.getLogger(XMLBasedNssdcaStatus.class);

	/**
	 * @return
	 */
	@GET
    @Produces("application/xml")
    public Response defaultNssdcaStatus(){
 
		StringBuffer xmlOutput = new StringBuffer();
        
        NssdcaStatus nStatus;
		try {
			
			nStatus = new NssdcaStatus();
			List<NssdcaStatus> nStatuses = nStatus.getNssdcaStatusOrderByVersion();
			logger.info("number of Nssdca Statuses: "  + nStatuses.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder nStatusBuilder;

            nStatusBuilder = factory.newDocumentBuilder();
            Document doc = nStatusBuilder.newDocument();
            Element rootElement = doc.createElement("nssdca_statuses");
            
			if (nStatuses.size() > 0){
				Iterator<NssdcaStatus> itr = nStatuses.iterator();
				int count = 1;
				
				while(itr.hasNext()) {
			         NssdcaStatus ns = itr.next();
			         logger.debug("Nssdca Statuses " + count + ":\n " + ns.getLogIdentifier() + " : " + ns.getNssdca());
			         
			         Element subRootElement = doc.createElement("nssdca_status");
	
			        Element idElement = doc.createElement(NssdcaStatus.LOGIDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode((ns.getLogIdentifier())));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(NssdcaStatus.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(ns.getVersion()));
		            subRootElement.appendChild(verElement);
			         
		            Element doiElement = doc.createElement(NssdcaStatus.NSSDCACOLUMN);
		            doiElement.appendChild(doc.createTextNode(ns.getNssdca()));
		            subRootElement.appendChild(doiElement);
		            
		            Element dateElement = doc.createElement(NssdcaStatus.DATECOLUMN);
		            dateElement.appendChild(doc.createTextNode(ns.getDate()));
		            subRootElement.appendChild(dateElement);
		            		            
		            Element emailElement = doc.createElement(NssdcaStatus.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(ns.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(NssdcaStatus.COMMENTCOLUMN);
		            commentElement.appendChild(doc.createTextNode(ns.getComment() != null ? ns.getComment() : ""));
		            subRootElement.appendChild(commentElement);
		            
		            rootElement.appendChild(subRootElement);
	
			        count++;
			    }						
                    
			}else{
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Can Not find any NssdcaStatus!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, result);
            
            logger.debug("NssdcaStatuses :\n" + writer.toString());
            
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
    @Produces("application/xml")
    public Response NssdcaStatus(@PathParam("id") String id, @PathParam("version") String version){
		
		StringBuffer xmlOutput = new StringBuffer();
        
        NssdcaStatus nStatus;
		try {
			
			nStatus = new NssdcaStatus();
			List<NssdcaStatus> nStatuses = nStatus.getNssdcaStatusList(id, version);
			logger.info("number of Nssdca Statuses: "  + nStatuses.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder nStatusBuilder;

            nStatusBuilder = factory.newDocumentBuilder();
            Document doc = nStatusBuilder.newDocument();
            Element rootElement = doc.createElement("nssdca_statuses");
            
			if (nStatuses.size() > 0){
				Iterator<NssdcaStatus> itr = nStatuses.iterator();
				int count = 1;
				
				while(itr.hasNext()) {
			         NssdcaStatus ns = itr.next();
			         logger.debug("Nssdca Statuses " + count + ":\n " + ns.getLogIdentifier() + " : " + ns.getNssdca());
			         
			         Element subRootElement = doc.createElement("nssdca_status");
	
			        Element idElement = doc.createElement(NssdcaStatus.LOGIDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode((ns.getLogIdentifier())));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(NssdcaStatus.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(ns.getVersion()));
		            subRootElement.appendChild(verElement);
			         
		            Element doiElement = doc.createElement(NssdcaStatus.NSSDCACOLUMN);
		            doiElement.appendChild(doc.createTextNode(ns.getNssdca()));
		            subRootElement.appendChild(doiElement);
		            
		            Element dateElement = doc.createElement(NssdcaStatus.DATECOLUMN);
		            dateElement.appendChild(doc.createTextNode(ns.getDate()));
		            subRootElement.appendChild(dateElement);
		            		            
		            Element emailElement = doc.createElement(NssdcaStatus.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(ns.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(NssdcaStatus.COMMENTCOLUMN);
		            commentElement.appendChild(doc.createTextNode(ns.getComment() != null ? ns.getComment() : ""));
		            subRootElement.appendChild(commentElement);
		            
		            rootElement.appendChild(subRootElement);
	
			        count++;
			    }						
                    
			}else{
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Can Not find any NssdcaStatus!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, result);
            
            logger.debug("NssdcaStatuses :\n" + writer.toString());
            
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
