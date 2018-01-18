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

import gov.nasa.pds.tracking.tracking.db.Delivery;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/delivery")
public class XMLBasedDelivery {
	
	public static Logger logger = Logger.getLogger(XMLBasedDelivery.class);

	/**
	 * @return
	 */
	@GET
    @Produces("application/xml")
    public Response defaultDelivery(){
 
		StringBuffer xmlOutput = new StringBuffer();
        
        Delivery del;
		try {
			
			del = new Delivery();
			List<Delivery> dels = del.getDeliveries();
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
	
		            Element idElement = doc.createElement(Delivery.DEL_IDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode((new Integer(d.getDelIdentifier()).toString())));
		            subRootElement.appendChild(idElement);
		            
		            Element logIdElement = doc.createElement(Delivery.LOG_IDENTIFIERCOLUMN);
		            logIdElement.appendChild(doc.createTextNode(d.getLogIdentifier()));
		            subRootElement.appendChild(logIdElement);
		            
		            Element verElement = doc.createElement(Delivery.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(d.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element nameElement = doc.createElement(Delivery.NAMECOLUMN);
		            nameElement.appendChild(doc.createTextNode(d.getName()));
		            subRootElement.appendChild(nameElement);
		            
		            Element startElement = doc.createElement(Delivery.STARTCOLUMN);
		            startElement.appendChild(doc.createTextNode(d.getStart()));
		            subRootElement.appendChild(startElement);
		            
		            Element stopElement = doc.createElement(Delivery.STOPCOLUMN);
		            stopElement.appendChild(doc.createTextNode(d.getStop()));
		            subRootElement.appendChild(stopElement);
		            
		            Element srcElement = doc.createElement(Delivery.SOURCECOLUMN);
		            srcElement.appendChild(doc.createTextNode(d.getSource()));
		            subRootElement.appendChild(srcElement);
		            
		            Element targetElement = doc.createElement(Delivery.TARGETCOLUMN);
		            targetElement.appendChild(doc.createTextNode(d.getTarget()));
		            subRootElement.appendChild(targetElement);
		            
		            Element dueDateElement = doc.createElement(Delivery.DUEDATECOLUMN);
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
            
            logger.debug("Elivries:\n" + writer.toString());
            
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
    @Produces("application/xml")
    public Response delivery(@PathParam("id") String id, @PathParam("version") String version){
		
		StringBuffer xmlOutput = new StringBuffer();
        
        Delivery del;
		try {
			
			del = new Delivery();
			List<Delivery> dels = del.getProductDeliveries(id, version);
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
	
		            Element idElement = doc.createElement(Delivery.DEL_IDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode((new Integer(d.getDelIdentifier()).toString())));
		            subRootElement.appendChild(idElement);
		            
		            Element logIdElement = doc.createElement(Delivery.LOG_IDENTIFIERCOLUMN);
		            logIdElement.appendChild(doc.createTextNode(d.getLogIdentifier()));
		            subRootElement.appendChild(logIdElement);
		            
		            Element verElement = doc.createElement(Delivery.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(d.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element nameElement = doc.createElement(Delivery.NAMECOLUMN);
		            nameElement.appendChild(doc.createTextNode(d.getName()));
		            subRootElement.appendChild(nameElement);
		            
		            Element startElement = doc.createElement(Delivery.STARTCOLUMN);
		            startElement.appendChild(doc.createTextNode(d.getStart()));
		            subRootElement.appendChild(startElement);
		            
		            Element stopElement = doc.createElement(Delivery.STOPCOLUMN);
		            stopElement.appendChild(doc.createTextNode(d.getStop()));
		            subRootElement.appendChild(stopElement);
		            
		            Element srcElement = doc.createElement(Delivery.SOURCECOLUMN);
		            srcElement.appendChild(doc.createTextNode(d.getSource()));
		            subRootElement.appendChild(srcElement);
		            
		            Element targetElement = doc.createElement(Delivery.TARGETCOLUMN);
		            targetElement.appendChild(doc.createTextNode(d.getTarget()));
		            subRootElement.appendChild(targetElement);
		            
		            Element dueDateElement = doc.createElement(Delivery.DUEDATECOLUMN);
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
	
}
