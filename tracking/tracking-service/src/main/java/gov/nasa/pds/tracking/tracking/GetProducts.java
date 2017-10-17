/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import gov.nasa.pds.tracking.tracking.db.Product;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class GetProducts {
	
	public static Logger logger = Logger.getLogger(GetProducts.class);

	/**
	 * 
	 */
	public GetProducts() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String type = null;
		if (args != null && args.length == 1){
			type = args[0];
		}
		logger.info("Type = " + type);
		Product prod;
		try {
			prod = new Product();
			
			List<Product> prods = prod.getProducts(type);
			
			logger.info(" ============== number of products: " + prods.size() + " =====================");
			Iterator<Product> itr = prods.iterator();
			int count = 1;
			while (itr.hasNext()) {
				Product p = itr.next();
				logger.info("Product " + count + ":\n " + p.getIdentifier() + " : " + p.getType() + " : " + p.getTitle());
				count++;
			}

		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
	}

}
