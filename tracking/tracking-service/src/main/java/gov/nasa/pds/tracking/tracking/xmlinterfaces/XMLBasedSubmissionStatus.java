/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.xmlinterfaces;

import org.apache.log4j.Logger;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nasa.pds.tracking.tracking.db.SubmissionStatus;;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/submissionstatusold")
public class XMLBasedSubmissionStatus {
	
	public static Logger logger = Logger.getLogger(XMLBasedSubmissionStatus.class);

	@GET
    @Produces("application/xml")
    public Response defaultSubmissionstatus() {
        
		StringBuffer xmlOutput = new StringBuffer();
		
		SubmissionStatus subMStatus;
		try {
			subMStatus = new SubmissionStatus();
			List<SubmissionStatus> subMStatuses = subMStatus.getSubmissionStatus();
			logger.info("number of Submission Status: "  + subMStatuses.size());					
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("Submission_Status");
            
			if (subMStatuses.size() > 0){	            
	            Iterator<SubmissionStatus> itr = subMStatuses.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					SubmissionStatus s = itr.next();
			        logger.debug("Submission Status " + count + ":\n " + s.getStatus() + " : " + s.getSubmissionDate());
			         
			        Element subRootElement = doc.createElement("Status");
			        
		            Element idElement = doc.createElement(SubmissionStatus.DEL_IDENTIFIERCOLUME);
		            idElement.appendChild(doc.createTextNode(String.valueOf(s.getDel_identifier())));
		            subRootElement.appendChild(idElement);
		            
		            Element dateElement = doc.createElement(SubmissionStatus.SUBMISSIONDATECOLUME);
		            dateElement.appendChild(doc.createTextNode(s.getSubmissionDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element statusDateElement = doc.createElement(SubmissionStatus.STATUSDATECOLUME);
		            statusDateElement.appendChild(doc.createTextNode(s.getStatusDate()));
		            subRootElement.appendChild(statusDateElement);
		            
		            Element statusElement = doc.createElement(SubmissionStatus.STATUSCOLUME);
		            statusElement.appendChild(doc.createTextNode(s.getStatus()));
		            subRootElement.appendChild(statusElement);
		            
		            Element emailElement = doc.createElement(SubmissionStatus.EMAILCOLUME);
		            emailElement.appendChild(doc.createTextNode(s.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(SubmissionStatus.COMMENTCOLUME);
		            commentElement.appendChild(doc.createTextNode(s.getComment() != null ? s.getComment(): ""));
		            subRootElement.appendChild(commentElement);
		            
		            rootElement.appendChild(subRootElement);

		            count++;
				}

			}else{
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Can Not find any Submission Status!"));
				rootElement.appendChild(messageElement);
			}
			
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, result);
            
            logger.debug("Submission Status:\n" + writer.toString());
            
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

	@Path("{id}")
    @GET
    @Produces("application/xml")
	public Response getsubmissionstatus(@PathParam("id") String id){
		
		StringBuffer xmlOutput = new StringBuffer();
		
		SubmissionStatus subMStatus;
		try {
			subMStatus = new SubmissionStatus();
			List<SubmissionStatus> subMStatuses = subMStatus.getDeliveryStatus(id);
			logger.info("number of Submission Status: "  + subMStatuses.size());					
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("Submission_Status");
            
			if (subMStatuses.size() > 0){	            
	            Iterator<SubmissionStatus> itr = subMStatuses.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					SubmissionStatus s = itr.next();
			        logger.debug("Submission Status " + count + ":\n " + s.getStatus() + " : " + s.getSubmissionDate());
			         
			        Element subRootElement = doc.createElement("Status");
			        
		            Element idElement = doc.createElement(SubmissionStatus.DEL_IDENTIFIERCOLUME);
		            idElement.appendChild(doc.createTextNode(String.valueOf(s.getDel_identifier())));
		            subRootElement.appendChild(idElement);
		            
		            Element dateElement = doc.createElement(SubmissionStatus.SUBMISSIONDATECOLUME);
		            dateElement.appendChild(doc.createTextNode(s.getSubmissionDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element statusDateElement = doc.createElement(SubmissionStatus.STATUSDATECOLUME);
		            statusDateElement.appendChild(doc.createTextNode(s.getStatusDate()));
		            subRootElement.appendChild(statusDateElement);
		            
		            Element statusElement = doc.createElement(SubmissionStatus.STATUSCOLUME);
		            statusElement.appendChild(doc.createTextNode(s.getStatus()));
		            subRootElement.appendChild(statusElement);
		            
		            Element emailElement = doc.createElement(SubmissionStatus.EMAILCOLUME);
		            emailElement.appendChild(doc.createTextNode(s.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(SubmissionStatus.COMMENTCOLUME);
		            commentElement.appendChild(doc.createTextNode(s.getComment() != null ? s.getComment(): ""));
		            subRootElement.appendChild(commentElement);
		            
		            rootElement.appendChild(subRootElement);

		            count++;
				}
			        
			}else{
				
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Can Not find any Submission Status!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, result);
            
            logger.debug("Submission Status:\n" + writer.toString());
            
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
