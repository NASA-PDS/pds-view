package gov.nasa.pds.transport;

import gov.nasa.pds.transport.utils.PDS2FName;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.oodt.product.ProductException;

/**
 * PDS Handler to return a zipped package of a PDS3 label and its referenced files.
 * Adapted from jpl.pds.server.ZipFileHandler to conform to the OFSNGetHandler API.
 *
 * @author Crichton and Hughes
 * @author Luca Cinquini
 */
public class ZipFileHandler extends AbstractPdsGetHandler {
	
	protected final static Logger LOG = Logger.getLogger(ZipFileHandler.class.getName());
	
	private String productRoot = "/"; // default product root
	
	@Override
	public void configure(Properties properties) {
		
		// parse properties, configuration
		super.configure(properties);
		
		// set productRoot
		if (this.getConfiguration()!=null) {
			productRoot = this.getConfiguration().getProductRoot();
			LOG.info("Using productRoot="+productRoot);
		}
		
	}
	
	@Override
	protected File getOutputFile(String inputFilePath) throws ProductException {
		
		File file = new File(inputFilePath);
		LOG.info("Input file path="+inputFilePath);
		if (!file.exists()) {
			throw new ProductException("File: "+file.getName()+" not found in the archive");
		}
		
		File tempFile = new File(this.getCache(), "products_"+file.getName().replaceAll("\\..+",".zip"));
		
		// do not regenerate file if it exists
		if (tempFile.exists()) {
			LOG.info("Zip file="+tempFile.getAbsolutePath()+" already exists, will not regenerate");
			
		// if not, generate the zip file
		} else {
		
			ZipOutputStream out = null;
			BufferedInputStream source = null;
			try {
	
				LOG.info("Generating zip file path="+tempFile.getAbsolutePath());
				
				int lenProductRootName = productRoot.length() + (productRoot.endsWith(File.separator) ? 0:1);
	
				out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));
	
				// list of all files that need to be included in zip package
				String filenames = PDS2FName.getNames(file,null);
				if (filenames == null) {
					throw new ProductException("PDS2FName returned null file list");
				}
				
				byte[] buf = new byte[512];
				int numRead;
				for (StringTokenizer tokens = new StringTokenizer(filenames); tokens.hasMoreTokens();) {
					String filenameToAdd = tokens.nextToken();
					File fileToAdd = new File(filenameToAdd);
					if (filenameToAdd.startsWith(productRoot))      // strip productDirName
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
						
					out.closeEntry();
					LOG.info("Added " + fileToAdd.getName() + " of size " + sizeOfFile);
				}
	
				LOG.info("Wrote zip file to " + tempFile.getAbsolutePath());
	
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
	 * Debug method.
	 * 
	 * @param args
	 * @throws ProductException
	 */
	public static void main(String[] args) throws ProductException  {
		
		//String inputFilePath = args[0];	
		String inputFilePath = "/usr/local/transport-ofsn/testdata//data/ear-a-i0028-4-sbn0001_smassii-v1.0/sbn_0001/DATA/DATA14/354_00.LBL";
		ZipFileHandler self = new ZipFileHandler();
		self.configure(new Properties());
		File outputFile = self.getOutputFile(inputFilePath);
		LOG.info("Output file path="+outputFile.getAbsolutePath());
		
	}

}