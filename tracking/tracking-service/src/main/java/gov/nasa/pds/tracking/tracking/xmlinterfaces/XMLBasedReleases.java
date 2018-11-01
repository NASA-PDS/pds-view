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

import gov.nasa.pds.tracking.tracking.db.Releases;
import gov.nasa.pds.tracking.tracking.db.ReleasesDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/releases")
public class XMLBasedReleases {
	
	public static Logger logger = Logger.getLogger(XMLBasedReleases.class);

	@GET
    @Produces("application/xml")
    public Response defaultReleases() {
        
		StringBuffer xmlOutput = new StringBuffer();
		
		ReleasesDao rel;
		try {			
											
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("releases");
            
            
			rel = new ReleasesDao();
			List<Releases> rels = rel.getReleasesList();
			logger.info("number of Releases: "  + rels.size());
            
			if (rels.size() > 0){
				
	            Iterator<Releases> itr = rels.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					Releases r = itr.next();
					logger.debug("Releases " + count + ":\n " + r.getLogIdentifier() + " : " + r.getDate());			     	 
			        
					Element subRootElement = doc.createElement("release");
			     	
		            Element idElement = doc.createElement(ReleasesDao.LOGIDENTIFIERCOLUME);
		            idElement.appendChild(doc.createTextNode(r.getLogIdentifier()));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(ReleasesDao.VERSIONCOLUME);
		            verElement.appendChild(doc.createTextNode(r.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element dateElement = doc.createElement(ReleasesDao.DATECOLUME);
		            dateElement.appendChild(doc.createTextNode(r.getDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element nameElement = doc.createElement(ReleasesDao.NAMECOLUME);
		            nameElement.appendChild(doc.createTextNode(r.getName()));
		            subRootElement.appendChild(nameElement);
		            
		            Element descriptElement = doc.createElement(ReleasesDao.DESCCOLUME);
		            descriptElement.appendChild(doc.createTextNode(r.getDescription()));
		            subRootElement.appendChild(descriptElement);
		            
		            Element emailElement = doc.createElement(ReleasesDao.EMAILCOLUME);
		            emailElement.appendChild(doc.createTextNode(r.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(ReleasesDao.COMMENTCOLUME);
		            commentElement.appendChild(doc.createTextNode(r.getComment() != null ? r.getComment() : ""));
		            subRootElement.appendChild(commentElement);
		            
		            rootElement.appendChild(subRootElement);

		            count++;
				}
			
			}else{
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Can Not find any Releases!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, result);
            
            logger.debug("Releases:\n" + writer.toString());
            
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
	public Response Releases(@PathParam("id") String id, @PathParam("version") String version, @PathParam("latest") boolean latest){
		
		StringBuffer xmlOutput = new StringBuffer();
		
		ReleasesDao relO;
		Releases rel;
		try {			
											
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("releases");
            
            
            relO = new ReleasesDao();
			List<Releases> rels = new ArrayList<Releases>();
			
			if (latest) {
				rel = relO.getLatestReleases(id, version);
				if (rel != null)
				rels.add(rel);
			}else{
				rels = relO.getReleasesList(id, version);
			}
			logger.info("number of Releases: "  + rels.size());
            
			if (rels.size() > 0){
				
	            Iterator<Releases> itr = rels.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
					Releases r = itr.next();
					logger.debug("Releases " + count + ":\n " + r.getLogIdentifier() + " : " + r.getDate());			     	 
			        
					Element subRootElement = doc.createElement("release");
			     	
		            Element idElement = doc.createElement(ReleasesDao.LOGIDENTIFIERCOLUME);
		            idElement.appendChild(doc.createTextNode(r.getLogIdentifier()));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(ReleasesDao.VERSIONCOLUME);
		            verElement.appendChild(doc.createTextNode(r.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element dateElement = doc.createElement(ReleasesDao.DATECOLUME);
		            dateElement.appendChild(doc.createTextNode(r.getDate()));
		            subRootElement.appendChild(dateElement);
		            
		            Element nameElement = doc.createElement(ReleasesDao.NAMECOLUME);
		            nameElement.appendChild(doc.createTextNode(r.getName()));
		            subRootElement.appendChild(nameElement);
		            
		            Element descriptElement = doc.createElement(ReleasesDao.DESCCOLUME);
		            descriptElement.appendChild(doc.createTextNode(r.getDescription()));
		            subRootElement.appendChild(descriptElement);
		            
		            Element emailElement = doc.createElement(ReleasesDao.EMAILCOLUME);
		            emailElement.appendChild(doc.createTextNode(r.getEmail()));
		            subRootElement.appendChild(emailElement);
		            
		            Element commentElement = doc.createElement(ReleasesDao.COMMENTCOLUME);
		            commentElement.appendChild(doc.createTextNode(r.getComment() != null ? r.getComment() : ""));
		            subRootElement.appendChild(commentElement);
		            
		            rootElement.appendChild(subRootElement);

		            count++;
				}
			}else{
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Can Not find any Releases!"));
				rootElement.appendChild(messageElement);
			}					
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, result);
            
            logger.debug("Releases:\n" + writer.toString());
            
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
	public Response createReleases(@FormParam("LogicalIdentifier") String logicalIdentifier,
			@FormParam("Version") String ver,
			@FormParam("Datte") String date,
			@FormParam("Name") String name,
			@FormParam("Desc") String desc,
			@FormParam("Email") String email,
			@FormParam("Comment") String comment) throws IOException{

		StringBuffer xmlOutput = new StringBuffer();
		ReleasesDao relD;
		
		try {
			relD = new ReleasesDao();

			Releases rel = new Releases(logicalIdentifier, ver, date, name, desc, email, comment);
			int result = relD.insertReleases(rel);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder doiBuilder;

			doiBuilder = factory.newDocumentBuilder();
            Document doc = doiBuilder.newDocument();
            Element rootElement = doc.createElement("releases");
            
			if(result == 1){
				Element subRootElement = doc.createElement("release");
		     	
	            Element idElement = doc.createElement(ReleasesDao.LOGIDENTIFIERCOLUME);
	            idElement.appendChild(doc.createTextNode(rel.getLogIdentifier()));
	            subRootElement.appendChild(idElement);
	            
	            Element verElement = doc.createElement(ReleasesDao.VERSIONCOLUME);
	            verElement.appendChild(doc.createTextNode(rel.getVersion()));
	            subRootElement.appendChild(verElement);
	            
	            Element dateElement = doc.createElement(ReleasesDao.DATECOLUME);
	            dateElement.appendChild(doc.createTextNode(rel.getDate()));
	            subRootElement.appendChild(dateElement);
	            
	            Element nameElement = doc.createElement(ReleasesDao.NAMECOLUME);
	            nameElement.appendChild(doc.createTextNode(rel.getName()));
	            subRootElement.appendChild(nameElement);
	            
	            Element descriptElement = doc.createElement(ReleasesDao.DESCCOLUME);
	            descriptElement.appendChild(doc.createTextNode(rel.getDescription()));
	            subRootElement.appendChild(descriptElement);
	            
	            Element emailElement = doc.createElement(ReleasesDao.EMAILCOLUME);
	            emailElement.appendChild(doc.createTextNode(rel.getEmail()));
	            subRootElement.appendChild(emailElement);
	            
	            Element commentElement = doc.createElement(ReleasesDao.COMMENTCOLUME);
	            commentElement.appendChild(doc.createTextNode(rel.getComment() != null ?rel.getComment() : ""));
	            subRootElement.appendChild(commentElement);
	            
	            rootElement.appendChild(subRootElement);                    
		}else{
			Element messageElement = doc.createElement("Message");
			messageElement.appendChild(doc.createTextNode("Add release for " + rel.getLogIdentifier() + ", " + rel.getVersion() + " failure!"));
			rootElement.appendChild(messageElement);
		}
		doc.appendChild(rootElement);
		
		DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult strResult = new StreamResult(writer);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, strResult);
        
        logger.debug("Release:\n" + writer.toString());
        
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
