package gov.nasa.pds.transport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oodt.product.ProductException;
import org.apache.oodt.product.handlers.ofsn.OFSNGetHandler;

/**
 * Abstract superclass of OFSN handlers that generate PDS products.
 * Each subclass should:
 * 1) define the mime type of the output file it generates, specified in the handler XML configuration
 * 2) define the rule to create the output file name for the requested input file name
 * 3) implement the specific process to generate teh output file
 * 
 * @author Luca Cinquini
 *
 */
public abstract class AbstractPdsGetHandler implements OFSNGetHandler {
	
	private final static String CACHE_DIR = "cacheDir";
	
	protected final static Logger LOG = Logger.getLogger(AbstractPdsGetHandler.class.getName());
	
	// temporary location where products are generated
	private File cache;
	
	/**
	 * Configuration method sets the temporary cache directory.
	 * Overriding subclasses should also invoke this superclass method.
	 */
	@Override
	public void configure(Properties properties) {
		
		String cacheDir = properties.getProperty(CACHE_DIR);
		if (StringUtils.isEmpty(cacheDir)) cacheDir = "/tmp";
		LOG.info("Cache dir="+cacheDir);
		this.cache = new File(cacheDir);
				
	}

	/**
	 * Method that returns the generated product, piece by piece.
	 * Invoked after the size of the product has been determined.
	 */
	@Override
	public byte[] retrieveChunk(String inputFilePath, long offset, int length) throws ProductException {
		
		LOG.fine("Retrieving product chunk: filepath="+inputFilePath+" offset="+offset+" length="+length);

		File outputFile = this.getOrCreateOutputFile(inputFilePath);
		
	    try {
	        byte[] bytes = FileUtils.readFileToByteArray(outputFile);
	        byte[] retBytes = new byte[length];  
	        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
	        is.skip(offset);
	        is.read(retBytes, 0, length);
	        return retBytes;
	      } catch (IOException e) {
	        e.printStackTrace();
	        throw new ProductException("Error reading bytes from file: " + outputFile.getAbsolutePath()+ " Message: " + e.getMessage());
	      }
	      
	}

	/**
	 * Method to establish the size of the product to be returned.
	 * This method implicitly triggers the creation of the product.
	 */
	@Override
	public long sizeOf(String inputFilePath) {
		
		try {
			File outputFile = this.getOrCreateOutputFile(inputFilePath);
			return outputFile.length();
		} catch(ProductException e) {
			return -1;
		}
		
	}
	
	/**
	 * Method that returns the output file, creating it if not existing already.
	 */
	private File getOrCreateOutputFile(String inputFilePath) throws ProductException {
		
		File inputFile = new File(inputFilePath);
		File outputFile = getOutputFilePath(inputFile);
		if (outputFile.exists()) {
			LOG.fine("Output file: "+outputFile.getAbsolutePath()+" already exists, will not regenerate");
		} else {
			LOG.fine("Generating output file: "+outputFile.getAbsolutePath());
			createOutputFile(inputFile, outputFile);
		} 
		return outputFile;
	}
	
	protected File getCache() {
		return this.cache;
	}
 	
	/**
	 * Method to build the full path of the requested product.
	 * Must be implemented by subclasses.
	 * 
	 * @param inputFile
	 * @return
	 */
	protected abstract File getOutputFilePath(File inputFile);

	/**
	 * Method to generate the requested product.
	 * Must be implemented by subclasses.
	 * 
	 * @param inputFile
	 * @param outputFile
	 */
	protected abstract void createOutputFile(File inputFile, File outputFile) throws ProductException;
	
}
