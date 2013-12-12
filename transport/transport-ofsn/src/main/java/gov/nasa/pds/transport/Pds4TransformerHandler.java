package gov.nasa.pds.transport;

import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.product.Pds4ImageTransformer;
import gov.nasa.pds.transform.product.ProductTransformer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oodt.product.ProductException;
import org.apache.oodt.product.handlers.ofsn.OFSNGetHandler;

public class Pds4TransformerHandler implements OFSNGetHandler {
	
	ProductTransformer transformer = new Pds4ImageTransformer();
	
	private final static String CACHE_DIR = "cacheDir";
	
	File cache = null;
	// FIXME: set extension depending on mime type
	String extension = "jpg";

	@Override
	public void configure(Properties properties) {
		
		String cacheDir = properties.getProperty(CACHE_DIR);
		if (StringUtils.isEmpty(cacheDir)) cacheDir = "/tmp";
		System.out.println("CACHE DIR="+cacheDir);
		this.cache = new File(cacheDir);
		
		String mimeType = properties.getProperty("mimeType");
		System.out.println("MIME TYPE="+mimeType);
		
	}

	@Override
	public byte[] retrieveChunk(String inputFilePath, long offset, int length) throws ProductException {
		
		System.out.println("retrieveChunk(): filepath="+inputFilePath+" offset="+offset+" length="+length);
		
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
		
		System.out.println("sizeOf():"+inputFilePath);
		
		try {
			File outputFile = this.getOutputFile(inputFilePath);
			return outputFile.length();
		} catch(ProductException e) {
			return -1;
		}
		
	}
	
	/**
	 * Method that returns the output file,
	 * creating it if not existing already
	 */
	private File getOutputFile(String inputFilePath) throws ProductException {
		
		File inputFile = new File(inputFilePath);
		File outputFile = getOutputFilePath(inputFile);
		if (outputFile.exists()) {
			System.out.println("Output file: "+outputFile.getAbsolutePath()+" already exist, will not regenerate");
		} else {
			System.out.println("Generating output file: "+outputFile.getAbsolutePath());
			createOutputFile(inputFile, this.cache, this.extension);
		} 
		return outputFile;
	}
	
	
	private File getOutputFilePath(File inputFile) {
		
		String filename = inputFile.getName();
		String extension= inputFile.getAbsolutePath().substring(
				          inputFile.getAbsolutePath().lastIndexOf(".")+1);
		File outputFile = new File(this.cache, filename.replace(extension, this.extension));
		return outputFile;
		
	}
	
	private void createOutputFile(File inputFile, File outputDir, String extension) throws ProductException {
		
		try {
			transformer.transform(inputFile, outputDir, extension);
		} catch(TransformException e) {
			e.printStackTrace();
			throw new ProductException(e.getMessage());
		}
		
	}

}
