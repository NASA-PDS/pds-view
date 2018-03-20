package gov.nasa.pds.transport;

import gov.nasa.pds.transform.TransformException;
import gov.nasa.pds.transform.product.ProductTransformer;
import gov.nasa.pds.transform.product.ProductTransformerFactory;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.oodt.product.ProductException;

/**
 * PDS Handler that transforms PDS3/PDS4 images to other formats.
 * 
 * @author Luca Cinquini
 */
public class PdsLabelTransformerHandler extends AbstractPdsGetHandler {
		
	ProductTransformerFactory factory = ProductTransformerFactory.getInstance();
	
	private String extension = null;
	
	protected final static Logger LOG = Logger.getLogger(PdsLabelTransformerHandler.class.getName());


	@Override
	public void configure(Properties properties) {

		// superclass configuration initializes the cache directory
		super.configure(properties);
		
		// initialize product extension
		this.extension = properties.getProperty("extension").toLowerCase();
		String mimeType = properties.getProperty("mimeType").toLowerCase();
		LOG.info("Configured extension="+this.extension+" mime type="+mimeType);
		
		// external program directory
		String externalProgramsHome = System.getProperties().getProperty("external.programs.home");
		LOG.info("Using 'external.programs.home'="+externalProgramsHome);
				
		// more debugging information about Java image formats
		LOG.info("Java supported formats: "+Arrays.asList(ImageIO.getReaderFormatNames()));
		
	}
	
	/**
	 * Method that generates the output image by delegating processing to the PDS image transformer.
	 */
	@Override
	public File getOutputFile(String inputFilePath) throws ProductException {	
		
		LOG.fine("Generating product for inputFile="+inputFilePath+" in cache directory="+this.getCache().getAbsolutePath());
		
		try {
			
			File input = new File(inputFilePath);
			ProductTransformer pt = factory.newInstance(input, this.extension);
			File output = null;
			List<File> outputs = pt.transform(input, this.getCache(), this.extension);
			if (!outputs.isEmpty()) {
			  output = outputs.get(0);
			}
			return output;

		} catch(TransformException e) {
			e.printStackTrace();
			throw new ProductException(e.getMessage());
		
		}
		
	}
	
	/**
	 * Debug method
	 * @param args
	 * @throws ProductException
	 */
	public static void main(String[] args) throws ProductException {
		
		// instantiation
		PdsLabelTransformerHandler self = new PdsLabelTransformerHandler();
		Properties props = new Properties();
		//props.put("extension", "jp2");
		//props.put("mimeType", "image/jp2");
		props.put("extension", "raw");
		props.put("mimeType", "application/x-binary");
		self.configure(props);
		
		// invocation
		String inputFilePath = "/usr/local/transport-ofsn/testdata/i943630r.xml";
		File outputFile = self.getOutputFile(inputFilePath);
		System.out.println(outputFile.getAbsolutePath());
		
	}

}
