package gov.nasa.pds.transport;

import gov.nasa.pds.transport.utils.XmlWriter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.oodt.product.ProductException;

/**
 * Handler that returns the total size of all files contained in a product directory,
 * i.e. the full (expanded) content of the directory that would be returned from a PDS_ZIPD request.
 * 
 * @author cinquini
 *
 */
public class ZipSizeDirHandler extends AbstractPdsGetHandler {
	
	protected final static Logger LOG = Logger.getLogger(ZipSizeDirHandler.class.getName());
	

	@Override
	protected File getOutputFile(String inputDirPath) throws ProductException {
		
		// validate input directory
		LOG.info("Input directory path="+inputDirPath);
		File inputDir = new File(inputDirPath);
		if (!inputDir.exists() || !inputDir.isDirectory()) {
			throw new ProductException("Invalid input directory: "+inputDirPath);
		}
		
		// recursively list all files in directory tree
		String[] extensions = null; // to list all files
		boolean recursive = true;
		Collection<File> fileNames = (Collection<File>)FileUtils.listFiles(inputDir, extensions, recursive); 

		long totalSize = 0;
		for (File fileToAdd : fileNames) {
			totalSize += fileToAdd.length();
		}
		
		// create XML document
		String xml = XmlWriter.writeSizeDocument(totalSize);
		
		// write XML document to temporary cache
		try {
			File tempFile = File.createTempFile(inputDir.getName(), ".xml");
			FileUtils.writeStringToFile(tempFile, xml);
			tempFile.deleteOnExit();
			return tempFile;
			
		} catch (IOException e) {
			throw new ProductException(e.getMessage());
		}
		
	}

	/**
	 * Debug method
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws ProductException, IOException {

		String inputFilePath = "/usr/local/transport-ofsn/testdata/data/mgn-v-rdrs-5-dim-v1.0/mg_1416/fo42s186";
		ZipSizeDirHandler self = new ZipSizeDirHandler();
		self.configure(new Properties());
		File outputFile = self.getOutputFile(inputFilePath);
		String xml = FileUtils.readFileToString(outputFile, "UTF-8");
		System.out.println(xml);

	}

}
