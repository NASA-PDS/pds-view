package gov.nasa.pds.transport;

import gov.nasa.pds.transform.TransformException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oodt.product.ProductException;
import org.apache.oodt.product.handlers.ofsn.OFSNGetHandler;

public class PdsLabelHandler implements OFSNGetHandler {
	
	private final static String CACHE_DIR = "cacheDir";
	
	// temporary location where products are generated
	File cache = null;
	
	@Override
	public void configure(Properties properties) {
		
		String cacheDir = properties.getProperty(CACHE_DIR);
		if (StringUtils.isEmpty(cacheDir)) cacheDir = "/tmp";
		System.out.println("CACHE DIR="+cacheDir);
		this.cache = new File(cacheDir);
				
	}

	@Override
	public byte[] retrieveChunk(String inputFilePath, long offset, int length) throws ProductException {
		
		System.out.println("PdsLabelHandler retrieveChunk(): filepath="+inputFilePath+" offset="+offset+" length="+length);

		File outputFile = this.getOutputFile(inputFilePath);
		
	    try {
	        byte[] bytes = FileUtils.readFileToByteArray(outputFile);
	        byte[] retBytes = new byte[length];  
	        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
	        is.skip(offset);
	        is.read(retBytes, 0, length);
	        return retBytes;
	      } catch (IOException e) {
	        e.printStackTrace();
	        throw new ProductException("Error reading bytes from file: " + outputFile.getAbsolutePath()
	            + " Message: " + e.getMessage());
	      }
	      
	}

	@Override
	public long sizeOf(String inputFilePath) {
		
		System.out.println("PdsLabelHandler sizeOf()");
		
		try {
			File outputFile = this.getOutputFile(inputFilePath);
			return outputFile.length();
		} catch(ProductException e) {
			return -1;
		}
		
	}
	
	/**
	 * Method that returns the output file, creating it if not existing already.
	 */
	private File getOutputFile(String inputFilePath) throws ProductException {
		
		File inputFile = new File(inputFilePath);
		File outputFile = getOutputFilePath(inputFile);
		if (outputFile.exists()) {
			System.out.println("Output file: "+outputFile.getAbsolutePath()+" already exist, will not regenerate");
		} else {
			System.out.println("Generating output file: "+outputFile.getAbsolutePath());
			createOutputFile(inputFile, outputFile);
		} 
		return outputFile;
	}
	
	/**
	 * Method to build the full path of the requested product.
	 * 
	 * @param inputFile
	 * @return
	 */
	private File getOutputFilePath(File inputFile) {
		
		String filename = inputFile.getName() + ".txt";
		File outputFile = new File(this.cache, filename);
		return outputFile;
	}

	/**
	 * Method to generate the requested product.
	 * 
	 * @param inputFile
	 * @param outputFile
	 */
	private void createOutputFile(File inputFile, File outputFile) throws ProductException {
		
		System.out.println("PdsLabelHandler.createOutputFile(): inputFile="
				           +inputFile.getAbsolutePath()+" outputFile="+outputFile.getAbsolutePath());
		
		try {
			StringBuffer sb = (new LabelReader(inputFile.toURI(), "")).read();
			FileUtils.writeStringToFile(outputFile, sb.toString());
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new ProductException(e.getMessage());
		}
		
		
		
	}
	
}
