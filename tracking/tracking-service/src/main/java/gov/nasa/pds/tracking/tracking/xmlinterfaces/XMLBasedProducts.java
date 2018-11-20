/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.xmlinterfaces;

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

import gov.nasa.pds.tracking.tracking.db.Product;
import gov.nasa.pds.tracking.tracking.db.ProductDao;
import gov.nasa.pds.tracking.tracking.db.Reference;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("xml/products")
public class XMLBasedProducts {
	
	public static Logger logger = Logger.getLogger(XMLBasedProducts.class);
	
	private ProductDao prodD;

	@GET
    @Produces("application/xml")
    public Response defaultProducts() {
        
		StringBuffer xmlOutput = new StringBuffer();
		
        ProductDao prod;
		try {
			prod = new ProductDao();
			List<Product> prods = prod.getProductsOrderByTitle();
			logger.info("number of products: "  + prods.size());					
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder prodsBuilder;

			prodsBuilder = factory.newDocumentBuilder();
            Document doc = prodsBuilder.newDocument();
            Element rootElement = doc.createElement("Products");
            
			if (prods.size() > 0){	            
	            Iterator<Product> itr = prods.iterator();
				int count = 1;	 			
				while(itr.hasNext()) {
			        Product p = itr.next();
			        logger.debug("Product " + count + ":\n " + p.getIdentifier() + " : " + p.getTitle());
			         
			        Element subRootElement = doc.createElement("product");

		            Element idElement = doc.createElement(ProductDao.IDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode(p.getIdentifier()));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(ProductDao.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(p.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element titleElement = doc.createElement(ProductDao.TITLECOLUMN);
		            titleElement.appendChild(doc.createTextNode(p.getTitle()));
		            subRootElement.appendChild(titleElement);
		            
		            Element typeElement = doc.createElement(ProductDao.TYPECOLUMN);
		            typeElement.appendChild(doc.createTextNode(p.getType()));
		            subRootElement.appendChild(typeElement);
		            
		            Element altElement = doc.createElement(ProductDao.ALTERNATECOLUMN);
		            altElement.appendChild(doc.createTextNode(p.getAlternate() != null ? p.getAlternate() : ""));
		            subRootElement.appendChild(altElement);
		            
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
            
            logger.debug("Products:\n" + writer.toString());
            
			xmlOutput.append(writer.toString());
            
        
			}else{
				xmlOutput.append("Can Not find any products!");
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

	@Path("type/{Type}")
    @GET
    @Produces("application/xml")
	public Response products(@PathParam("Type") String type){
		
		StringBuffer xmlOutput = new StringBuffer();
        
        ProductDao prod;
		try {
			prod = new ProductDao();
			List<Product> prods = prod.getProducts(type);			
			logger.info("number of products: "  + prods.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder prodsBuilder;

			prodsBuilder = factory.newDocumentBuilder();
            Document doc = prodsBuilder.newDocument();
            Element rootElement = doc.createElement("Products");
            
            if (prods.size() > 0){
				Iterator<Product> itr = prods.iterator();
				int count = 1;
				
				while(itr.hasNext()) {
			        Product p = itr.next();
			        logger.debug("Product " + count + ":\n " + p.getIdentifier() + " : " + p.getTitle());
			         
			        Element subRootElement = doc.createElement("product");

		            Element idElement = doc.createElement(ProductDao.IDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode(p.getIdentifier()));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(ProductDao.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(p.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element titleElement = doc.createElement(ProductDao.TITLECOLUMN);
		            titleElement.appendChild(doc.createTextNode(p.getTitle()));
		            subRootElement.appendChild(titleElement);
		            
		            Element typeElement = doc.createElement(ProductDao.TYPECOLUMN);
		            typeElement.appendChild(doc.createTextNode(p.getType()));
		            subRootElement.appendChild(typeElement);
		            
		            Element altElement = doc.createElement(ProductDao.ALTERNATECOLUMN);
		            altElement.appendChild(doc.createTextNode(p.getAlternate() != null ? p.getAlternate() : ""));
		            subRootElement.appendChild(altElement);
			         
			         /*Element refElement = doc.createElement("instrument");
			         
			         Element instRefElement = doc.createElement(Reference.REFERENCECOLUMN);
			         instRefElement.appendChild(doc.createTextNode(p.getInstRef()));
			         Element instTitleRefElement = doc.createElement(Reference.TITLECOLUMN);
			         instTitleRefElement.appendChild(doc.createTextNode(p.getInstTitle()));
			         refElement.appendChild(instRefElement);
			         refElement.appendChild(instTitleRefElement);
			         
			         subRootElement.appendChild(refElement);
			         
			         refElement = doc.createElement("investigation");
			         
			         Element inveRefElement = doc.createElement(Reference.REFERENCECOLUMN);
			         inveRefElement.appendChild(doc.createTextNode(p.getInveRef()));
			         Element inveTitleRefElement = doc.createElement(Reference.TITLECOLUMN);
			         inveTitleRefElement.appendChild(doc.createTextNode(p.getInveTitle()));
			         refElement.appendChild(inveRefElement);
			         refElement.appendChild(inveTitleRefElement);
			         
			         subRootElement.appendChild(refElement);
		            
			         refElement = doc.createElement("node");
			         
			         Element nodeRefElement = doc.createElement(Reference.REFERENCECOLUMN);
			         nodeRefElement.appendChild(doc.createTextNode(p.getNodeRef()));
			         Element nodeTitleRefElement = doc.createElement(Reference.TITLECOLUMN);
			         nodeTitleRefElement.appendChild(doc.createTextNode(p.getNodeTitle()));
			         refElement.appendChild(nodeRefElement);
			         refElement.appendChild(nodeTitleRefElement);
			         
			         subRootElement.appendChild(refElement);*/
			         
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
	            
	            logger.debug("Products:\n" + writer.toString());
	            
				xmlOutput.append(writer.toString());
            }else{
				xmlOutput.append("Can Not find any products!");
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
	
	@Path("{instRef : (.+)?}/{investRef : (.+)?}")
    @GET
    @Produces("application/xml")
	public Response products(@PathParam("instRef") String insRef, @PathParam("investRef") String invRef){
		
		StringBuffer xmlOutput = new StringBuffer();
        
        ProductDao prod;
		try {
			prod = new ProductDao();
			List<Product> prods = prod.getProductsAssociatedDeliveriesOrderByTitle(insRef, invRef);			
			logger.info("number of products: "  + prods.size());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder prodsBuilder;

			prodsBuilder = factory.newDocumentBuilder();
            Document doc = prodsBuilder.newDocument();
            Element rootElement = doc.createElement("Products");
            
            if (prods.size() > 0){
				Iterator<Product> itr = prods.iterator();
				int count = 1;
				
				while(itr.hasNext()) {
			        Product p = itr.next();
			        logger.debug("Product " + count + ":\n " + p.getIdentifier() + " : " + p.getTitle());
			         
			        Element subRootElement = doc.createElement("product");

		            Element idElement = doc.createElement(ProductDao.IDENTIFIERCOLUMN);
		            idElement.appendChild(doc.createTextNode(p.getIdentifier()));
		            subRootElement.appendChild(idElement);
		            
		            Element verElement = doc.createElement(ProductDao.VERSIONCOLUMN);
		            verElement.appendChild(doc.createTextNode(p.getVersion()));
		            subRootElement.appendChild(verElement);
		            
		            Element titleElement = doc.createElement(ProductDao.TITLECOLUMN);
		            titleElement.appendChild(doc.createTextNode(p.getTitle()));
		            subRootElement.appendChild(titleElement);
		            
		            Element typeElement = doc.createElement(ProductDao.TYPECOLUMN);
		            typeElement.appendChild(doc.createTextNode(p.getType()));
		            subRootElement.appendChild(typeElement);
		            
		            Element altElement = doc.createElement(ProductDao.ALTERNATECOLUMN);
		            altElement.appendChild(doc.createTextNode(p.getAlternate() != null ? p.getAlternate() : ""));
		            subRootElement.appendChild(altElement);
			         
			         Element refElement = doc.createElement("instrument");
			         
			         Element instRefElement = doc.createElement(Reference.REFERENCECOLUMN);
			         instRefElement.appendChild(doc.createTextNode(p.getInstRef()));
			         Element instTitleRefElement = doc.createElement(Reference.TITLECOLUMN);
			         instTitleRefElement.appendChild(doc.createTextNode(p.getInstTitle()));
			         refElement.appendChild(instRefElement);
			         refElement.appendChild(instTitleRefElement);
			         
			         subRootElement.appendChild(refElement);
			         
			         refElement = doc.createElement("investigation");
			         
			         Element inveRefElement = doc.createElement(Reference.REFERENCECOLUMN);
			         inveRefElement.appendChild(doc.createTextNode(p.getInveRef()));
			         Element inveTitleRefElement = doc.createElement(Reference.TITLECOLUMN);
			         inveTitleRefElement.appendChild(doc.createTextNode(p.getInveTitle()));
			         refElement.appendChild(inveRefElement);
			         refElement.appendChild(inveTitleRefElement);
			         
			         subRootElement.appendChild(refElement);
		            
			         refElement = doc.createElement("node");
			         
			         Element nodeRefElement = doc.createElement(Reference.REFERENCECOLUMN);
			         nodeRefElement.appendChild(doc.createTextNode(p.getNodeRef()));
			         Element nodeTitleRefElement = doc.createElement(Reference.TITLECOLUMN);
			         nodeTitleRefElement.appendChild(doc.createTextNode(p.getNodeTitle()));
			         refElement.appendChild(nodeRefElement);
			         refElement.appendChild(nodeTitleRefElement);
			         
			         subRootElement.appendChild(refElement);
			         
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
	            
	            logger.debug("Products:\n" + writer.toString());
	            
				xmlOutput.append(writer.toString());
            }else{
				xmlOutput.append("Can Not find any products!");
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
	public Response createProduct(@FormParam("LogicalIdentifier") String logicalIdentifier,
			@FormParam("Version") String ver,
			@FormParam("Title") String title,
			@FormParam("Type") String type,
			@FormParam("AlternateId") String altId) throws IOException{
		
		StringBuffer xmlOutput = new StringBuffer();
		try {
			prodD = new ProductDao();

			Product prod = new Product(logicalIdentifier, ver, title, type, altId);
			int result = prodD.insertProduct(prod);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("Products");
            
			if(result == 1){
				Element subRootElement = doc.createElement("Product");
				
	            Element idElement = doc.createElement(ProductDao.IDENTIFIERCOLUMN);
	            idElement.appendChild(doc.createTextNode(prod.getIdentifier()));
	            subRootElement.appendChild(idElement);
	            
	            Element verElement = doc.createElement(ProductDao.VERSIONCOLUMN);
	            verElement.appendChild(doc.createTextNode(prod.getVersion()));
	            subRootElement.appendChild(verElement);
	            
	            Element titleElement = doc.createElement(ProductDao.TITLECOLUMN);
	            titleElement.appendChild(doc.createTextNode(prod.getTitle()));
	            subRootElement.appendChild(titleElement);
	            
	            Element typeElement = doc.createElement(ProductDao.TYPECOLUMN);
	            typeElement.appendChild(doc.createTextNode(prod.getType()));
	            subRootElement.appendChild(typeElement);
	            
	            Element altIdElement = doc.createElement(ProductDao.ALTERNATECOLUMN);
	            altIdElement.appendChild(doc.createTextNode(prod.getAlternate() != null ? prod.getAlternate() : ""));
	            subRootElement.appendChild(altIdElement);
	            
	            rootElement.appendChild(subRootElement);

			}else{
			
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Add Product for " + prod.getIdentifier()  + " failure!"));
				rootElement.appendChild(messageElement);
			}
			doc.appendChild(rootElement);
			
			DOMSource domSource = new DOMSource(doc);
	        StringWriter writer = new StringWriter();
	        StreamResult strResult = new StreamResult(writer);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.transform(domSource, strResult);
	        
	        logger.debug("Product:\n" + writer.toString());
	        
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
	public Response updateSubmissionStatus(@FormParam("LogicalIdentifier") String logicalIdentifier,
			@FormParam("Version") String ver,
			@FormParam("Title") String title,
			@FormParam("Type") String type,
			@FormParam("AlternateId") String altId) throws IOException{
		
		StringBuffer xmlOutput = new StringBuffer();
		try {
			prodD = new ProductDao();

			Product prod = new Product(logicalIdentifier, ver, title, type, altId);
			Product prodUpdated = prodD.updateProduct(prod);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder statusBuilder;

            statusBuilder = factory.newDocumentBuilder();
            Document doc = statusBuilder.newDocument();
            Element rootElement = doc.createElement("Products");
            
			if(prodUpdated != null && prodUpdated.getIdentifier() != null){
				Element subRootElement = doc.createElement("Product");
				
	            Element idElement = doc.createElement(ProductDao.IDENTIFIERCOLUMN);
	            idElement.appendChild(doc.createTextNode(prodUpdated.getIdentifier()));
	            subRootElement.appendChild(idElement);
	            
	            Element verElement = doc.createElement(ProductDao.VERSIONCOLUMN);
	            verElement.appendChild(doc.createTextNode(prodUpdated.getVersion()));
	            subRootElement.appendChild(verElement);
	            
	            Element titleElement = doc.createElement(ProductDao.TITLECOLUMN);
	            titleElement.appendChild(doc.createTextNode(prodUpdated.getTitle()));
	            subRootElement.appendChild(titleElement);
	            
	            Element typeElement = doc.createElement(ProductDao.TYPECOLUMN);
	            typeElement.appendChild(doc.createTextNode(prodUpdated.getType()));
	            subRootElement.appendChild(typeElement);
	            
	            Element altIdElement = doc.createElement(ProductDao.ALTERNATECOLUMN);
	            altIdElement.appendChild(doc.createTextNode(prodUpdated.getAlternate() != null ? prodUpdated.getAlternate() : ""));
	            subRootElement.appendChild(altIdElement);
	            
	            rootElement.appendChild(subRootElement);

			}else{
			
				Element messageElement = doc.createElement("Message");
				messageElement.appendChild(doc.createTextNode("Add Product for " + prod.getIdentifier()  + " failure!"));
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
