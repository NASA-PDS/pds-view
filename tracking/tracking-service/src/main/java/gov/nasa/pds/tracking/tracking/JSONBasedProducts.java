/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking;

import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
 
import org.json.JSONException;
import org.json.JSONObject;

import gov.nasa.pds.tracking.tracking.db.Product;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/products")
public class JSONBasedProducts {
	
	public static Logger logger = Logger.getLogger(JSONBasedProducts.class);

	@GET
    @Produces("application/json")
    public Response defaultProducts() throws JSONException {
 
        JSONObject jsonProducts = new JSONObject();
        
        JSONObject jsonProd = new JSONObject();
        
        Product prod;
		try {
			prod = new Product();
			List<Product> prods = prod.getProductsOrderByTitle();
			logger.info("number of products: "  + prods.size());
			Iterator<Product> itr = prods.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
		         Product p = itr.next();
		         logger.debug("Product " + count + ":\n " + p.getIdentifier() + " : " + p.getTitle());
		         
		         jsonProd = new JSONObject();
		         jsonProd.put("logical_identifier", p.getIdentifier());
		         jsonProd.put("version_id", p.getVersion());
		         jsonProd.put("title", p.getTitle());
		         jsonProd.put("type", p.getType());
		         jsonProd.put("alternate_id", p.getAlternate());

		         jsonProducts.append("products", jsonProd);
		         count++;
		    }
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonProducts.toString(4);
        return Response.status(200).entity(result).build();
    }
	
	@Path("{Delivery}")
    @GET
    @Produces("application/json")
    public Response products(@PathParam("Delivery") boolean delivery)  throws JSONException {
		JSONObject jsonProducts = new JSONObject();
        
        JSONObject jsonProd = new JSONObject();
        
        Product prod;
		try {
			prod = new Product();
			List<Product> prods = prod.getProductsAssociatedDeliveriesOrderByTitle();
			logger.info("number of products: "  + prods.size());
			Iterator<Product> itr = prods.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
		         Product p = itr.next();
		         logger.debug("Product " + count + ":\n " + p.getIdentifier() + " : " + p.getTitle());
		         
		         jsonProd = new JSONObject();
		         jsonProd.put("logical_identifier", p.getIdentifier());
		         jsonProd.put("version_id", p.getVersion());
		         jsonProd.put("title", p.getTitle());
		         jsonProd.put("type", p.getType());
		         jsonProd.put("alternate_id", p.getAlternate());

		         jsonProducts.append("products", jsonProd);
		         count++;
		    }
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonProducts.toString(4);
        return Response.status(200).entity(result).build();
	}
	
}
