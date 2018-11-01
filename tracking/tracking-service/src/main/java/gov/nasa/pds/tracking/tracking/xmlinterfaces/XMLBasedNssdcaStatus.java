/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.xmlinterfaces;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
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

import gov.nasa.pds.tracking.tracking.db.NssdcaStatus;
import gov.nasa.pds.tracking.tracking.db.NssdcaStatusDao;;

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
        
        NssdcaStatusDao nStatus;
		try {
			
			nStatus = new NssdcaStatusDao();
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
	
			        Element idElement = doc.createElement(NssdcaStatusDao.LOGIDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode((ns.getLogIdentifier())));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(NssdcaStatusDao.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(ns.getVersion()));
		            subRootElement.appendChild(verElement);
			         
		            Element doiElement = doc.createElement(NssdcaStatusDao.NSSDCACOLUMN);
		            doiElement.appendChild(doc.createTextNode(ns.getNssdca()));
		            subRootElement.appendChild(doiElement);
		            
		            Element dateElement = doc.createElement(NssdcaStatusDao.DATECOLUMN);
		            dateElement.appendChild(doc.createTextNode(ns.getDate()));
		            subRootElement.appendChild(dateElement);
		            		            
		            Element emailElement = doc.createElement(NssdcaStatusDao.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(ns.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(NssdcaStatusDao.COMMENTCOLUMN);
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
        
		NssdcaStatusDao nStatus;
		try {
			
			nStatus = new NssdcaStatusDao();
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
	
			        Element idElement = doc.createElement(NssdcaStatusDao.LOGIDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode((ns.getLogIdentifier())));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(NssdcaStatusDao.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(ns.getVersion()));
		            subRootElement.appendChild(verElement);
			         
		            Element doiElement = doc.createElement(NssdcaStatusDao.NSSDCACOLUMN);
		            doiElement.appendChild(doc.createTextNode(ns.getNssdca()));
		            subRootElement.appendChild(doiElement);
		            
		            Element dateElement = doc.createElement(NssdcaStatusDao.DATECOLUMN);
		            dateElement.appendChild(doc.createTextNode(ns.getDate()));
		            subRootElement.appendChild(dateElement);
		            		            
		            Element emailElement = doc.createElement(NssdcaStatusDao.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(ns.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(NssdcaStatusDao.COMMENTCOLUMN);
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

	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createNssdcaStatus(@FormParam("LogicalIdentifier") String logicalIdentifier,
		@FormParam("Version") String ver,
		@FormParam("Datte") String date,
		@FormParam("NssdcaIdentifier") String nssdca,
		@FormParam("Email") String email,
		@FormParam("Comment") String comment) throws IOException{

		StringBuffer xmlOutput = new StringBuffer();
		NssdcaStatusDao nsD;
		
		try {
			nsD = new NssdcaStatusDao();

			NssdcaStatus ns = new NssdcaStatus(logicalIdentifier, ver, date, nssdca, email, comment);
			int result = nsD.insertNssdcaStatus(ns);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder doiBuilder;

			doiBuilder = factory.newDocumentBuilder();
            Document doc = doiBuilder.newDocument();
            Element rootElement = doc.createElement("nssdca_statuses");
            
			if(result == 1){
				Element subRootElement = doc.createElement("nssdca_status");
				
		        Element idElement = doc.createElement(NssdcaStatusDao.LOGIDENTIFIERCOLUMN);
	            idElement.appendChild(doc.createTextNode((ns.getLogIdentifier())));
	            subRootElement.appendChild(idElement);
	            
	            Element verElement = doc.createElement(NssdcaStatusDao.VERSIONCOLUMN);
	            verElement.appendChild(doc.createTextNode(ns.getVersion()));
	            subRootElement.appendChild(verElement);
		         
	            Element doiElement = doc.createElement(NssdcaStatusDao.NSSDCACOLUMN);
	            doiElement.appendChild(doc.createTextNode(ns.getNssdca()));
	            subRootElement.appendChild(doiElement);
	            
	            Element dateElement = doc.createElement(NssdcaStatusDao.DATECOLUMN);
	            dateElement.appendChild(doc.createTextNode(ns.getDate()));
	            subRootElement.appendChild(dateElement);
	            		            
	            Element emailElement = doc.createElement(NssdcaStatusDao.EMAILCOLUMN);
	            emailElement.appendChild(doc.createTextNode(ns.getEmail()));
	            subRootElement.appendChild(emailElement);
	            
	            Element commentElement = doc.createElement(NssdcaStatusDao.COMMENTCOLUMN);
	            commentElement.appendChild(doc.createTextNode(ns.getComment() != null ? ns.getComment() : ""));
	            subRootElement.appendChild(commentElement);
	            
	            rootElement.appendChild(subRootElement);                    
		}else{
			Element messageElement = doc.createElement("Message");
			messageElement.appendChild(doc.createTextNode("Add Nssdca status for " + ns.getLogIdentifier() + ", " + ns.getVersion() + " failure!"));
			rootElement.appendChild(messageElement);
		}
		doc.appendChild(rootElement);
		
		DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult strResult = new StreamResult(writer);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, strResult);
        
        logger.debug("Nssdca status:\n" + writer.toString());
        
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
