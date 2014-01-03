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
	 * The output file is located in the cache directory, 
	 * and has the same name as the input file but with the specific image extension.
	 */
	@Override
	protected File getOutputFilePath(File inputFile) {
		
		String filename = inputFile.getName();
		String extension= inputFile.getAbsolutePath().substring(
				          inputFile.getAbsolutePath().lastIndexOf(".")+1);
		File outputFile = new File(this.getCache(), filename.replace(extension, this.extension));
		return outputFile;
		
	}
	
	/**
	 * Method that generates the output image by delegating processing to the PDS image transformer.
	 */
	@Override
	protected void createOutputFile(File inputFile, File outputFile) throws ProductException {	
		
		LOG.fine("Generating product: inputFile="+inputFile.getAbsolutePath()+" outputFile="+outputFile.getAbsolutePath());
		
		try {
			// FIXME: simply invoke transformer with parameter 'outputFile' when API supports it
			//transformer.transform(inputFile, outputFile);
			transformer.transform(inputFile, this.getCache(), this.extension);
		} catch(TransformException e) {
			e.printStackTrace();
			throw new ProductException(e.getMessage());
		}
		
	}

}
