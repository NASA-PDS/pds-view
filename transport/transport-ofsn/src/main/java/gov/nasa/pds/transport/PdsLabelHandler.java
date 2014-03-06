package gov.nasa.pds.transport;

import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.oodt.product.ProductException;

/**
 * PDS Handler that expands and returns a PDS3 label, as plain text.
 * 
 * @author Luca Cinquini
 *
 */
public class PdsLabelHandler extends AbstractPdsGetHandler {
	
	protected final static Logger LOG = Logger.getLogger(PdsLabelHandler.class.getName());

	/**
	 * This method generates the expanded label by delegating all processing to the product-tools package.
	 */
	@Override
	protected File getOutputFile(String inputFilePath) throws ProductException {
		
		File inputFile = new File(inputFilePath);
		String filename = inputFile.getName();
		File outputFile = new File(this.getCache(), filename);
		
		if (outputFile.exists()) {
			
			LOG.fine("Request product:" + outputFile.getAbsolutePath() +" already exists, will not regenerate");
			
		} else {
		
			LOG.fine("Generating product: inputFile="+inputFile.getAbsolutePath()+" outputFile="+outputFile.getAbsolutePath());
			
			try {
				StringBuffer sb = (new LabelReader(inputFile.toURI(), "")).read();
				FileUtils.writeStringToFile(outputFile, sb.toString());
				
			} catch(Exception e) {
				e.printStackTrace();
				throw new ProductException(e.getMessage());
			}	
					
		}
		
		return outputFile;
		
	}
	
}
