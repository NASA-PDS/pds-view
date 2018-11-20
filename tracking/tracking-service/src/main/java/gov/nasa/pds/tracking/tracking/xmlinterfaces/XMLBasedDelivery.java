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

import gov.nasa.pds.tracking.tracking.db.Delivery;
import gov.nasa.pds.tracking.tracking.db.DeliveryDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/delivery")
public class XMLBasedDelivery {
	
	public static Logger logger = Logger.getLogger(XMLBasedDelivery.class);
	
	DeliveryDao dD;
	
	/**
	 * @return
	 */
	@GET
    @Produces(MediaType.APPLICATION_XML)
    public Response defaultDelivery(){
 
		StringBuffer xmlOutput = new StringBuffer();
        
        DeliveryDao delD;
		try {
			
			delD = new DeliveryDao();
			List<Delivery> dels = delD.getDeliveries();
			logger.info("number of deliveries: "  + dels.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder prodsBuilder;

			prodsBuilder = factory.newDocumentBuilder();
            Document doc = prodsBuilder.newDocument();
            Element rootElement = doc.createElement("deliveries");
            
			if (dels.size() > 0){
				Iterator<Delivery> itr = dels.iterator();
				int count = 1;
				
				while(itr.hasNext()) {
			         Delivery d = itr.next();
			         logger.debug("Deliveries " + count + ":\n " + d.getLogIdentifier() + " : " + d.getName());
			         
			         Element subRootElement = doc.createElement("delivery");
	
		            Element idElement = doc.createElement(DeliveryDao.DEL_IDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode((new Integer(d.getDelIdentifier()).toString())));
		            subRootElement.appendChild(idElement);
		            
		            Element logIdElement = doc.createElement(DeliveryDao.LOG_IDENTIFIERCOLUMN);
		            logIdElement.appendChild(doc.createTextNode(d.getLogIdentifier()));
		            subRootElement.appendChild(logIdElement);
		            
		            Element verElement = doc.createElement(DeliveryDao.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(d.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element nameElement = doc.createElement(DeliveryDao.NAMECOLUMN);
		            nameElement.appendChild(doc.createTextNode(d.getName()));
		            subRootElement.appendChild(nameElement);
		            
		            Element startElement = doc.createElement(DeliveryDao.STARTCOLUMN);
		            startElement.appendChild(doc.createTextNode(d.getStart()));
		            subRootElement.appendChild(startElement);
		            
		            Element stopElement = doc.createElement(DeliveryDao.STOPCOLUMN);
		            stopElement.appendChild(doc.createTextNode(d.getStop()));
		            subRootElement.appendChild(stopElement);
		            
		            Element srcElement = doc.createElement(DeliveryDao.SOURCECOLUMN);
		            srcElement.appendChild(doc.createTextNode(d.getSource()));
		            subRootElement.appendChild(srcElement);
		            
		            Element targetElement = doc.createElement(DeliveryDao.TARGETCOLUMN);
		            targetElement.appendChild(doc.createTextNode(d.getTarget()));
		            subRootElement.appendChild(targetElement);
		            
		            Element dueDateElement = doc.createElement(DeliveryDao.DUEDATECOLUMN);
		            dueDateElement.appendChild(doc.createTextNode(d.getDueDate()));
		            subRootElement.appendChild(dueDateElement);
		            
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
            
            logger.debug("Delivries:\n" + writer.toString());
            
			xmlOutput.append(writer.toString());
                    
			}else{
				xmlOutput.append("Can Not find any delivery!");
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
	
	/**
	 * @param id
	 * @param version
	 * @return
	 */
	@Path("{id : (.+)?}/{version : (.+)?}")
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response delivery(@PathParam("id") String id, @PathParam("version") String version){
		
		StringBuffer xmlOutput = new StringBuffer();
        
		DeliveryDao delD;
		try {
			
			delD = new DeliveryDao();
			List<Delivery> dels = delD.getProductDeliveries(id, version);
			logger.info("number of deliveries: "  + dels.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder prodsBuilder;

			prodsBuilder = factory.newDocumentBuilder();
            Document doc = prodsBuilder.newDocument();
            Element rootElement = doc.createElement("deliveries");
            
			if (dels.size() > 0){
				Iterator<Delivery> itr = dels.iterator();
				int count = 1;
				
				while(itr.hasNext()) {
			         Delivery d = itr.next();
			         logger.debug("Deliveries " + count + ":\n " + d.getLogIdentifier() + " : " + d.getName());
			         
			         Element subRootElement = doc.createElement("delivery");
	
		            Element idElement = doc.createElement(DeliveryDao.DEL_IDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode((new Integer(d.getDelIdentifier()).toString())));
		            subRootElement.appendChild(idElement);
		            
		            Element logIdElement = doc.createElement(DeliveryDao.LOG_IDENTIFIERCOLUMN);
		            logIdElement.appendChild(doc.createTextNode(d.getLogIdentifier()));
		            subRootElement.appendChild(logIdElement);
		            
		            Element verElement = doc.createElement(DeliveryDao.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(d.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element nameElement = doc.createElement(DeliveryDao.NAMECOLUMN);
		            nameElement.appendChild(doc.createTextNode(d.getName()));
		            subRootElement.appendChild(nameElement);
		            
		            Element startElement = doc.createElement(DeliveryDao.STARTCOLUMN);
		            startElement.appendChild(doc.createTextNode(d.getStart()));
		            subRootElement.appendChild(startElement);
		            
		            Element stopElement = doc.createElement(DeliveryDao.STOPCOLUMN);
		            stopElement.appendChild(doc.createTextNode(d.getStop()));
		            subRootElement.appendChild(stopElement);
		            
		            Element srcElement = doc.createElement(DeliveryDao.SOURCECOLUMN);
		            srcElement.appendChild(doc.createTextNode(d.getSource()));
		            subRootElement.appendChild(srcElement);
		            
		            Element targetElement = doc.createElement(DeliveryDao.TARGETCOLUMN);
		            targetElement.appendChild(doc.createTextNode(d.getTarget()));
		            subRootElement.appendChild(targetElement);
		            
		            Element dueDateElement = doc.createElement(DeliveryDao.DUEDATECOLUMN);
		            dueDateElement.appendChild(doc.createTextNode(d.getDueDate()));
		            subRootElement.appendChild(dueDateElement);
		            
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
            
            logger.debug("Delivries:\n" + writer.toString());
            
			xmlOutput.append(writer.toString());
            
        
			}else{
				xmlOutput.append("Can Not find any delivery!");
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
	public Response createDelivery(@FormParam("Log_Identifier") String logIdentifier,
			@FormParam("Version") String ver, 
			@FormParam("Name") String name, 
			@FormParam("Start_Time") String startTime, 
			@FormParam("Stop_Time") String stopTime, 
			@FormParam("Source") String src, 
			@FormParam("Target") String tgt, 
			@FormParam("Due_Date") String dueD ) throws IOException{
		
		StringBuffer xmlOutput = new StringBuffer();
		
		try {
			dD = new DeliveryDao();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder delBuilder;

            delBuilder = factory.newDocumentBuilder();
            Document doc = delBuilder.newDocument();
            Element rootElement = doc.createElement("delivery");
            
            Delivery del = new Delivery(logIdentifier, -1, ver, name, startTime, stopTime,
					src, tgt, dueD);
			int result = dD.insertDelivery(del);
			
			if(result > 0){
				Element idElement = doc.createElement(DeliveryDao.DEL_IDENTIFIERCOLUMN);
	            idElement.appendChild(doc.createTextNode((new Integer(result).toString())));
	            rootElement.appendChild(idElement);
	            
	            Element logIdElement = doc.createElement(DeliveryDao.LOG_IDENTIFIERCOLUMN);
	            logIdElement.appendChild(doc.createTextNode(del.getLogIdentifier()));
	            rootElement.appendChild(logIdElement);
	            
	            Element verElement = doc.createElement(DeliveryDao.VERSIONCOLUMN);
	            verElement.appendChild(doc.createTextNode(del.getVersion()));
	            rootElement.appendChild(verElement);
	            
	            Element nameElement = doc.createElement(DeliveryDao.NAMECOLUMN);
	            nameElement.appendChild(doc.createTextNode(del.getName()));
	            rootElement.appendChild(nameElement);
	            
	            Element startElement = doc.createElement(DeliveryDao.STARTCOLUMN);
	            startElement.appendChild(doc.createTextNode(del.getStart()));
	            rootElement.appendChild(startElement);
	            
	            Element stopElement = doc.createElement(DeliveryDao.STOPCOLUMN);
	            stopElement.appendChild(doc.createTextNode(del.getStop()));
	            rootElement.appendChild(stopElement);
	            
	            Element srcElement = doc.createElement(DeliveryDao.SOURCECOLUMN);
	            srcElement.appendChild(doc.createTextNode(del.getSource()));
	            rootElement.appendChild(srcElement);
	            
	            Element targetElement = doc.createElement(DeliveryDao.TARGETCOLUMN);
	            targetElement.appendChild(doc.createTextNode(del.getTarget()));
	            rootElement.appendChild(targetElement);
	            
	            Element dueDateElement = doc.createElement(DeliveryDao.DUEDATECOLUMN);
	            dueDateElement.appendChild(doc.createTextNode(del.getDueDate()));
	            rootElement.appendChild(dueDateElement);
		    }else{
			
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Add delivery for " + logIdentifier  + " failure!"));
				rootElement.appendChild(messageElement);
			}
		doc.appendChild(rootElement);
		
		DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult strResult = new StreamResult(writer);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, strResult);
        
        logger.debug("Delivery:\n" + writer.toString());
        
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
	public Response updateDelivery(@FormParam("Log_Identifier") String logIdentifier, 
			@FormParam("Del_Identifier") String delIdentifier,
			@FormParam("Version") String ver, 
			@FormParam("Name") String name, 
			@FormParam("Start_Time") String startTime, 
			@FormParam("Stop_Time") String stopTime, 
			@FormParam("Source") String src, 
			@FormParam("Target") String tgt, 
			@FormParam("Due_Date") String dueD) throws IOException{
		
		StringBuffer xmlOutput = new StringBuffer();
		
		try {
			dD = new DeliveryDao();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder delBuilder;

            delBuilder = factory.newDocumentBuilder();
            Document doc = delBuilder.newDocument();
            Element rootElement = doc.createElement("delivery");
            
            Delivery del = new Delivery(logIdentifier, (new Integer(delIdentifier).intValue()), ver, name, startTime, stopTime,
					src, tgt, dueD);
			Delivery updatedDel = dD.updateDelivery(del);
			
			if(updatedDel != null){
				Element idElement = doc.createElement(DeliveryDao.DEL_IDENTIFIERCOLUMN);
	            idElement.appendChild(doc.createTextNode((new Integer(updatedDel.getDelIdentifier()).toString())));
	            rootElement.appendChild(idElement);
	            
	            Element logIdElement = doc.createElement(DeliveryDao.LOG_IDENTIFIERCOLUMN);
	            logIdElement.appendChild(doc.createTextNode(updatedDel.getLogIdentifier()));
	            rootElement.appendChild(logIdElement);
	            
	            Element verElement = doc.createElement(DeliveryDao.VERSIONCOLUMN);
	            verElement.appendChild(doc.createTextNode(updatedDel.getVersion()));
	            rootElement.appendChild(verElement);
	            
	            Element nameElement = doc.createElement(DeliveryDao.NAMECOLUMN);
	            nameElement.appendChild(doc.createTextNode(updatedDel.getName()));
	            rootElement.appendChild(nameElement);
	            
	            Element startElement = doc.createElement(DeliveryDao.STARTCOLUMN);
	            startElement.appendChild(doc.createTextNode(updatedDel.getStart()));
	            rootElement.appendChild(startElement);
	            
	            Element stopElement = doc.createElement(DeliveryDao.STOPCOLUMN);
	            stopElement.appendChild(doc.createTextNode(updatedDel.getStop()));
	            rootElement.appendChild(stopElement);
	            
	            Element srcElement = doc.createElement(DeliveryDao.SOURCECOLUMN);
	            srcElement.appendChild(doc.createTextNode(updatedDel.getSource()));
	            rootElement.appendChild(srcElement);
	            
	            Element targetElement = doc.createElement(DeliveryDao.TARGETCOLUMN);
	            targetElement.appendChild(doc.createTextNode(updatedDel.getTarget()));
	            rootElement.appendChild(targetElement);
	            
	            Element dueDateElement = doc.createElement(DeliveryDao.DUEDATECOLUMN);
	            dueDateElement.appendChild(doc.createTextNode(updatedDel.getDueDate()));
	            rootElement.appendChild(dueDateElement);
		    }else{
			
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Updated delivery for " + delIdentifier  + " failure!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
	        StringWriter writer = new StringWriter();
	        StreamResult strResult = new StreamResult(writer);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.transform(domSource, strResult);
	        
	        logger.debug("Updated delivery:\n" + writer.toString());
	        
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
