package gov.nasa.pds.transport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.oodt.product.ProductException;

/**
 * Handler that returns a ZIP file containing all files ion the requested directory.
 * Sub-directories are not included.
 * 
 * Adapted from jpl.pds.server.ZipDFileHandler to conform to the OFSNGetHandler API.
 *
 * @author Crichton and Hughes.
 * @author Luca Cinquini
 */
public class ZipDirHandler extends AbstractPdsGetHandler {
	
	protected final static Logger LOG = Logger.getLogger(ZipDirHandler.class.getName());
	
	private String productRoot = "/"; // default product root
	int lenProductRootName = 0; // number of characters to remove from zip filenames
	
	@Override
	public void configure(Properties properties) {
		
		// parse properties, configuration
		super.configure(properties);
		
		// set productRoot
		if (this.getConfiguration()!=null) {
			productRoot = this.getConfiguration().getProductRoot();
			LOG.info("Using productRoot="+productRoot);
		}
		
		lenProductRootName = productRoot.length() + (productRoot.endsWith(File.separator) ? 0:1);
		
	}

	@Override
	protected File getOutputFile(String inputDirPath) throws ProductException {
		
		// validate input directory
		LOG.info("Input directory path="+inputDirPath);
		File inputDir = new File(inputDirPath);
		if (!inputDir.exists() || !inputDir.isDirectory()) {
			throw new ProductException("Invalid input directory: "+inputDirPath);
		}
		
		// create output zip file
		File tempFile = new File(this.getCache(), inputDir.getName()+".zip");
		
		// do not regenerate file if it exists
		if (tempFile.exists()) {
			LOG.info("Zip file="+tempFile.getAbsolutePath()+" already exists, will not regenerate");
			
		// if not, generate the zip file
		} else {
						
			ZipOutputStream out = null;
			BufferedInputStream source = null;
			try {
				
				LOG.info("Generating zip file path="+tempFile.getAbsolutePath());
				out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));
				
				// recursively list all files in directory tree
				String[] extensions = null; // to list all files
				boolean recursive = true;
				Collection<File> fileNames = (Collection<File>)FileUtils.listFiles(inputDir, extensions, recursive); 
				
				// create zip file
				byte[] buf = new byte[512];
				int numRead;
				for (File fileToAdd : fileNames) {		
						
					String filenameToAdd = fileToAdd.getAbsolutePath();
					LOG.info("Adding fileName="+filenameToAdd);
					if (filenameToAdd.startsWith(productRoot)) 	// strip productRoot
						filenameToAdd = filenameToAdd.substring(lenProductRootName);
					if (filenameToAdd.matches("[a-zA-Z]:.*"))           // strip windows drive
						filenameToAdd = filenameToAdd.substring(2);
					if (filenameToAdd.charAt(0) == File.separatorChar) // strip root separatorChar
						filenameToAdd = filenameToAdd.substring(1);
					
					ZipEntry entry = new ZipEntry(filenameToAdd.replace(File.separatorChar, '/'));
					entry.setMethod(ZipEntry.DEFLATED);
					out.putNextEntry(entry);	
					source = new BufferedInputStream(new FileInputStream(fileToAdd));
					int sizeOfFile = 0;
					while ((numRead = source.read(buf)) != -1) {
						out.write(buf, 0, numRead);
						sizeOfFile += numRead;
					}
					source.close();	
					out.closeEntry();
					LOG.info("Added " + fileToAdd.getName() + " of size " + sizeOfFile);
						
				}
				
			} catch (IOException ex) {
				throw new ProductException(ex.getMessage());
				
			} finally {
				if (out != null) try {
					out.close();
				} catch (IOException ignore) {}
				if (source != null) try {
					source.close();
				} catch (IOException ignore) {}
			}

		}
		
		return tempFile;
	}

	/**
	 * Debug method
	 * 
	 * @param args
	 * @throws ProductException
	 */
	public static void main(String[] args) throws ProductException {
		
		String inputFilePath = "/usr/local/transport-ofsn/testdata/data/mgn-v-rdrs-5-dim-v1.0/mg_1416/fo42s186";
		ZipDirHandler self = new ZipDirHandler();
		self.configure(new Properties());
		File outputFile = self.getOutputFile(inputFilePath);
		LOG.info("Output file path="+outputFile.getAbsolutePath());

	}

}
