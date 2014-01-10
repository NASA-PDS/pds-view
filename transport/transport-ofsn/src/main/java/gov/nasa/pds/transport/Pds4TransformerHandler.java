package gov.nasa.pds.transport;

import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.product.Pds4ImageTransformer;
import gov.nasa.pds.transform.product.ProductTransformer;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.oodt.product.ProductException;

public class Pds4TransformerHandler extends AbstractPdsGetHandler {
	
	ProductTransformer transformer = new Pds4ImageTransformer();
	
	protected final static Logger LOG = Logger.getLogger(Pds4TransformerHandler.class.getName());
	
	private String extension = null;

	@Override
	public void configure(Properties properties) {

		// superclass configuration initializes the cache directory
		super.configure(properties);
		
		// initialize product extension depending on configured mime type
		String mimeType = properties.getProperty("mimeType");
		LOG.info("Configured mime type: "+mimeType);
		
		if (mimeType.equalsIgnoreCase("image/jpeg")) {
			this.extension = "jpg";
		} else {
			LOG.warning("Unrecognized mime type from configuration file");
		}
		
	}
	
	/**
	 * Method that generates the output image by delegating processing to the PDS image transformer.
	 */
	@Override
	protected File getOutputFile(String inputFilePath) throws ProductException {	
		
		LOG.fine("Generating product for inputFile="+inputFilePath+" in cache directory="+this.getCache().getAbsolutePath());
		
		try {
			return transformer.transform(new File(inputFilePath), this.getCache(), this.extension);

		} catch(TransformException e) {
			e.printStackTrace();
			throw new ProductException(e.getMessage());
		
		}
		
	}

}
