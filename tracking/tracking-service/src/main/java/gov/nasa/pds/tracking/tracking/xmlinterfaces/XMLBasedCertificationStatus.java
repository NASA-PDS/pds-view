/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.xmlinterfaces;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
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

import gov.nasa.pds.tracking.tracking.db.CertificationStatus;
import gov.nasa.pds.tracking.tracking.db.CertificationStatusDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/certificationstatus")
public class XMLBasedCertificationStatus {
	
	public static Logger logger = Logger.getLogger(XMLBasedCertificationStatus.class);

	@GET
    @Produces("application/xml")
    public Response defaultArchiveStatus() {
        
		StringBuffer xmlOutput = new StringBuffer();
		
		CertificationStatusDao status;
		try {
											
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("certification_status");
            
            status = new CertificationStatusDao();
			List<CertificationStatus> statuses = status.getCertificationStatusOrderByVersion();
						
			logger.info("number of Certification Status: "  + statuses.size());
            
			if (statuses.size() > 0){
				
	            Iterator<CertificationStatus> itr = statuses.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					CertificationStatus as = itr.next();
					logger.debug("Certification Status " + count + ":\n " + as.getLogIdentifier() + " : " + as.getStatus());			     	 
			        
					Element subRootElement = doc.createElement("status");
					
		            Element idElement = doc.createElement(CertificationStatusDao.LOGIDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode(as.getLogIdentifier()));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(CertificationStatusDao.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(as.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element dateElement = doc.createElement(CertificationStatusDao.DATECOLUMN);
		            dateElement.appendChild(doc.createTextNode(as.getDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element statusElement = doc.createElement(CertificationStatusDao.STATUSCOLUMN);
		            statusElement.appendChild(doc.createTextNode(as.getStatus()));
		            subRootElement.appendChild(statusElement);
		            
		            Element emailElement = doc.createElement(CertificationStatusDao.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(as.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(CertificationStatusDao.COMMENTCOLUMN);
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
            
            logger.debug("Certification Status:\n" + writer.toString());
            
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
		
		CertificationStatusDao cStatusO;
		CertificationStatus cStatus;
		try {
											
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("certification_status");
            
            cStatusO = new CertificationStatusDao();
			List<CertificationStatus> cStatuses = new ArrayList<CertificationStatus>();
			
			if (latest) {
				cStatus = cStatusO.getLatestCertificationStatus(id, version);
				if (cStatus != null)
					cStatuses.add(cStatus);
			}else{
				cStatuses = cStatusO.getCertificationStatusList(id, version);
			}
						
			logger.info("number of Certification Status: "  + cStatuses.size());
            
			if (cStatuses.size() > 0){
				
	            Iterator<CertificationStatus> itr = cStatuses.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					CertificationStatus as = itr.next();
					logger.debug("Certification Status " + count + ":\n " + as.getLogIdentifier() + " : " + as.getStatus());			     	 
			        
					Element subRootElement = doc.createElement("status");
					
		            Element idElement = doc.createElement(CertificationStatusDao.LOGIDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode(as.getLogIdentifier()));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(CertificationStatusDao.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(as.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element dateElement = doc.createElement(CertificationStatusDao.DATECOLUMN);
		            dateElement.appendChild(doc.createTextNode(as.getDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element statusElement = doc.createElement(CertificationStatusDao.STATUSCOLUMN);
		            statusElement.appendChild(doc.createTextNode(as.getStatus()));
		            subRootElement.appendChild(statusElement);
		            
		            Element emailElement = doc.createElement(CertificationStatusDao.EMAILCOLUMN);
		            emailElement.appendChild(doc.createTextNode(as.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(CertificationStatusDao.COMMENTCOLUMN);
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
            
            logger.debug("Certification Status:\n" + writer.toString());
            
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
	public Response createCertificationStatus(@FormParam("LogicalIdentifier") String logicalIdentifier,
			@FormParam("Version") String ver,
			@FormParam("Datte") String date,
			@FormParam("Status") String status,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{

		StringBuffer xmlOutput = new StringBuffer();
		CertificationStatusDao csD;
		
		try {
			csD = new CertificationStatusDao();

			CertificationStatus cs = new CertificationStatus(logicalIdentifier, ver, date, status, email, comment);
			
			int result = csD.insertCertificationStatus(cs);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder doiBuilder;

			doiBuilder = factory.newDocumentBuilder();
            Document doc = doiBuilder.newDocument();
            Element rootElement = doc.createElement("certification_status");
            
			if(result == 1){
				Element subRootElement = doc.createElement("status");
				
				Element idElement = doc.createElement(CertificationStatusDao.LOGIDENTIFIERCOLUMN);
	            idElement.appendChild(doc.createTextNode(cs.getLogIdentifier()));
	            subRootElement.appendChild(idElement);
	            
	            Element verElement = doc.createElement(CertificationStatusDao.VERSIONCOLUMN);
	            verElement.appendChild(doc.createTextNode(cs.getVersion()));
	            subRootElement.appendChild(verElement);
	            
	            Element dateElement = doc.createElement(CertificationStatusDao.DATECOLUMN);
	            dateElement.appendChild(doc.createTextNode(cs.getDate()));
	            subRootElement.appendChild(dateElement);
	            
	            Element statusElement = doc.createElement(CertificationStatusDao.STATUSCOLUMN);
	            statusElement.appendChild(doc.createTextNode(cs.getStatus()));
	            subRootElement.appendChild(statusElement);
	            
	            Element emailElement = doc.createElement(CertificationStatusDao.EMAILCOLUMN);
	            emailElement.appendChild(doc.createTextNode(cs.getEmail()));
	            subRootElement.appendChild(emailElement);
	            
	            Element commentElement = doc.createElement(CertificationStatusDao.COMMENTCOLUMN);
	            commentElement.appendChild(doc.createTextNode(cs.getComment() != null ? cs.getComment() : ""));
	            subRootElement.appendChild(commentElement);
	            
	            rootElement.appendChild(subRootElement);                    
		}else{
			Element messageElement = doc.createElement("Message");
			messageElement.appendChild(doc.createTextNode("Add certification status for " + cs.getLogIdentifier() + ", " + cs.getVersion() + " failure!"));
			rootElement.appendChild(messageElement);
		}
		doc.appendChild(rootElement);
		
		DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult strResult = new StreamResult(writer);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, strResult);
        
        logger.debug("certification status:\n" + writer.toString());
        
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
