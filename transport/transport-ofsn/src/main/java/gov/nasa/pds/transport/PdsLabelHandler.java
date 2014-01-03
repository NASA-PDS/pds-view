package gov.nasa.pds.transport;

import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.oodt.product.ProductException;

/**
 * PDS Handler that expands and returns a PDS label, as plain text.
 * 
 * @author Luca Cinquini
 *
 */
public class PdsLabelHandler extends AbstractPdsGetHandler {
	
	protected final static Logger LOG = Logger.getLogger(PdsLabelHandler.class.getName());
	
	/**
	 * The expanded PDS label has the same name as the original label,
	 * and is generated in the cache directory.
	 */
	@Override
	protected File getOutputFilePath(File inputFile) {
		
		String filename = inputFile.getName();
		File outputFile = new File(this.getCache(), filename);
		return outputFile;
	}

	/**
	 * This method generates the expanded label by delegating all processing to the product-tools package.
	 */
	@Override
	protected void createOutputFile(File inputFile, File outputFile) throws ProductException {
		
		LOG.fine("Generating product: inputFile="+inputFile.getAbsolutePath()+" outputFile="+outputFile.getAbsolutePath());
		
		try {
			StringBuffer sb = (new LabelReader(inputFile.toURI(), "")).read();
			FileUtils.writeStringToFile(outputFile, sb.toString());
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new ProductException(e.getMessage());
		}	
		
	}
	
}
