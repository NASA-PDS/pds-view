/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.xmlinterfaces;

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

import gov.nasa.pds.tracking.tracking.db.DBConnector;
import gov.nasa.pds.tracking.tracking.db.SubmissionAndStatus;
import gov.nasa.pds.tracking.tracking.db.SubmissionAndStatusDao;
import gov.nasa.pds.tracking.tracking.db.SubmissionStatus;;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/submissionstatus")
public class XMLBasedSubmissionAndStatus {
	
	public static Logger logger = Logger.getLogger(XMLBasedSubmissionAndStatus.class);


	private SubmissionAndStatusDao subMD;
	
	@GET
    @Produces(MediaType.APPLICATION_XML)
    public Response defaultSubmissionstatus() {
        
		StringBuffer xmlOutput = new StringBuffer();

		try {
			subMD = new SubmissionAndStatusDao();
			List<SubmissionAndStatus> subMs = subMD.getSubmissionsAndStatusList();
			logger.info("number of Submission Status: " + subMs.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("Submission_Status");
            
			if (subMs.size() > 0){	            
				Iterator<SubmissionAndStatus> itr = subMs.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					SubmissionAndStatus s = itr.next();
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
    @Produces(MediaType.APPLICATION_XML)
	public Response getsubmissionstatus(@PathParam("id") String id){
		
		StringBuffer xmlOutput = new StringBuffer();
		
		try {
			subMD = new SubmissionAndStatusDao();
			List<SubmissionAndStatus> subMs = subMD.getSubmissionsAndStatusList();
			logger.info("number of Submission Status: " + subMs.size());					
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("Submission_Status");
            
			if (subMs.size() > 0){	            
	            Iterator<SubmissionAndStatus> itr = subMs.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					SubmissionAndStatus s = itr.next();
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
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createSubmissionAndStatus(@FormParam("Delivery_ID") int id,
			@FormParam("Status") String status,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{
		
		StringBuffer xmlOutput = new StringBuffer();
		try {
			subMD = new SubmissionAndStatusDao();
		
			String currentTime = DBConnector.ISO_BASIC.format(new Date());
			SubmissionAndStatus subMS = new SubmissionAndStatus(id, currentTime, currentTime,
					status, email, comment);
			int result = subMD.insertSubmissionAndStatus(subMS);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("Submission_Status");
            
			if(result == 1){
				Element subRootElement = doc.createElement("Status");
		        
	            Element idElement = doc.createElement(SubmissionStatus.DEL_IDENTIFIERCOLUME);
	            idElement.appendChild(doc.createTextNode(String.valueOf(subMS.getDel_identifier())));
	            subRootElement.appendChild(idElement);
	            
	            Element dateElement = doc.createElement(SubmissionStatus.SUBMISSIONDATECOLUME);
	            dateElement.appendChild(doc.createTextNode(subMS.getSubmissionDate()));
	            subRootElement.appendChild(dateElement);
	            
	            Element statusDateElement = doc.createElement(SubmissionStatus.STATUSDATECOLUME);
	            statusDateElement.appendChild(doc.createTextNode(subMS.getStatusDate()));
	            subRootElement.appendChild(statusDateElement);
	            
	            Element statusElement = doc.createElement(SubmissionStatus.STATUSCOLUME);
	            statusElement.appendChild(doc.createTextNode(subMS.getStatus()));
	            subRootElement.appendChild(statusElement);
	            
	            Element emailElement = doc.createElement(SubmissionStatus.EMAILCOLUME);
	            emailElement.appendChild(doc.createTextNode(subMS.getEmail()));
	            subRootElement.appendChild(emailElement);
	            
	            Element commentElement = doc.createElement(SubmissionStatus.COMMENTCOLUME);
	            commentElement.appendChild(doc.createTextNode(subMS.getComment() != null ? subMS.getComment(): ""));
	            subRootElement.appendChild(commentElement);
	            
	            rootElement.appendChild(subRootElement);

			}else{
			
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Add Submission Status for " + id  + " failure!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
	        StringWriter writer = new StringWriter();
	        StreamResult strResult = new StreamResult(writer);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.transform(domSource, strResult);
	        
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
	
	@POST
	@Path("/addstatus")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createSubmissionAndStatus(@FormParam("Delivery_ID") int id,
			@FormParam("SubmissionDate") String submissionDate,
			@FormParam("Status") String status,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{
		
		StringBuffer xmlOutput = new StringBuffer();
		try {
			subMD = new SubmissionAndStatusDao();
		
			String currentTime = DBConnector.ISO_BASIC.format(new Date());
			SubmissionAndStatus subMS = new SubmissionAndStatus(id, submissionDate, currentTime,
					status, email, comment);
			int result = subMD.insertSubmissionStatus(subMS);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("Submission_Status");
            
			if(result == 1){
				Element subRootElement = doc.createElement("Status");
		        
	            Element idElement = doc.createElement(SubmissionStatus.DEL_IDENTIFIERCOLUME);
	            idElement.appendChild(doc.createTextNode(String.valueOf(subMS.getDel_identifier())));
	            subRootElement.appendChild(idElement);
	            
	            Element dateElement = doc.createElement(SubmissionStatus.SUBMISSIONDATECOLUME);
	            dateElement.appendChild(doc.createTextNode(subMS.getSubmissionDate()));
	            subRootElement.appendChild(dateElement);
	            
	            Element statusDateElement = doc.createElement(SubmissionStatus.STATUSDATECOLUME);
	            statusDateElement.appendChild(doc.createTextNode(subMS.getStatusDate()));
	            subRootElement.appendChild(statusDateElement);
	            
	            Element statusElement = doc.createElement(SubmissionStatus.STATUSCOLUME);
	            statusElement.appendChild(doc.createTextNode(subMS.getStatus()));
	            subRootElement.appendChild(statusElement);
	            
	            Element emailElement = doc.createElement(SubmissionStatus.EMAILCOLUME);
	            emailElement.appendChild(doc.createTextNode(subMS.getEmail()));
	            subRootElement.appendChild(emailElement);
	            
	            Element commentElement = doc.createElement(SubmissionStatus.COMMENTCOLUME);
	            commentElement.appendChild(doc.createTextNode(subMS.getComment() != null ? subMS.getComment(): ""));
	            subRootElement.appendChild(commentElement);
	            
	            rootElement.appendChild(subRootElement);

			}else{
			
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Add Submission Status for " + id  + " failure!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
	        StringWriter writer = new StringWriter();
	        StreamResult strResult = new StreamResult(writer);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.transform(domSource, strResult);
	        
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
	
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response updateSubmissionStatus(@FormParam("Delivery_ID") int id,
			@FormParam("SubmissionDate") String submissionDate,
			@FormParam("StatusnDate") String statusDate,
			@FormParam("Status") String status,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{
		
		StringBuffer xmlOutput = new StringBuffer();
		try {
			subMD = new SubmissionAndStatusDao();
		
			//String currentTime = DBConnector.ISO_BASIC.format(new Date());
			SubmissionAndStatus subMS = new SubmissionAndStatus(id, submissionDate, statusDate,
					status, email, comment);
			int result = subMD.updateSubmissionStatus(subMS);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("Submission_Status");
            
			if(result == 1){
				Element subRootElement = doc.createElement("Status");
		        
	            Element idElement = doc.createElement(SubmissionStatus.DEL_IDENTIFIERCOLUME);
	            idElement.appendChild(doc.createTextNode(String.valueOf(subMS.getDel_identifier())));
	            subRootElement.appendChild(idElement);
	            
	            Element dateElement = doc.createElement(SubmissionStatus.SUBMISSIONDATECOLUME);
	            dateElement.appendChild(doc.createTextNode(subMS.getSubmissionDate()));
	            subRootElement.appendChild(dateElement);
	            
	            Element statusDateElement = doc.createElement(SubmissionStatus.STATUSDATECOLUME);
	            statusDateElement.appendChild(doc.createTextNode(subMS.getStatusDate()));
	            subRootElement.appendChild(statusDateElement);
	            
	            Element statusElement = doc.createElement(SubmissionStatus.STATUSCOLUME);
	            statusElement.appendChild(doc.createTextNode(subMS.getStatus()));
	            subRootElement.appendChild(statusElement);
	            
	            Element emailElement = doc.createElement(SubmissionStatus.EMAILCOLUME);
	            emailElement.appendChild(doc.createTextNode(subMS.getEmail()));
	            subRootElement.appendChild(emailElement);
	            
	            Element commentElement = doc.createElement(SubmissionStatus.COMMENTCOLUME);
	            commentElement.appendChild(doc.createTextNode(subMS.getComment() != null ? subMS.getComment(): ""));
	            subRootElement.appendChild(commentElement);
	            
	            rootElement.appendChild(subRootElement);

			}else{
			
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Update Submission Status for " + id  + " failure!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
	        StringWriter writer = new StringWriter();
	        StreamResult strResult = new StreamResult(writer);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.transform(domSource, strResult);
	        
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
