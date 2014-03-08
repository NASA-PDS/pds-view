package gov.nasa.pds.transport;

import gov.nasa.pds.transport.utils.PDS2FName;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.oodt.product.ProductException;

/**
 * PDS Handler to return the total size of a PDS3 label and its referenced files
 * (which would be contained in the compressed package returned by the ZipFileHandler).
 * 
 * Adapted from jpl.pds.server.ZipFileHandler to conform to the OFSNGetHandler API.
 *
 * @author Crichton and Hughes
 * @author Luca Cinquini
 */
public class ZipSizeFileHandler extends AbstractPdsGetHandler {
	
	protected final static Logger LOG = Logger.getLogger(ZipSizeFileHandler.class.getName());
	
	/** PDS newline */
	private static final String NL = "\r\n";
			
	@Override
	public void configure(Properties properties) {}
	
	@Override
	protected File getOutputFile(String inputFilePath) throws ProductException {
		
		File file = new File(inputFilePath);
		LOG.info("Input file path="+inputFilePath);
		if (!file.exists()) {
			throw new ProductException("File: "+file.getName()+" not found in the archive");
		}

		// list of all files that need to be included in zip package
		String filenames = PDS2FName.getNames(file, null);
		if (filenames == null) {
			throw new ProductException("PDS2FName returned null file list");
		}

		// compute total size
		long totalSize = 0;
		for (StringTokenizer tokens = new StringTokenizer(filenames); tokens.hasMoreTokens();) {
			File fileToAdd = new File(tokens.nextToken());
			totalSize += fileToAdd.length();
		}

		// create XML file
		StringBuffer b = new StringBuffer("");
		b.append("<?xml version=\"1.0\"?>").append(NL);
		b.append("<!DOCTYPE dirresult PUBLIC \"-//JPL/DTD OODT dirresult 1.0//EN\" \"http://starbrite.jpl.nasa.gov:80/dtd/dirresult.dtd\">").append(NL);
		b.append("<dirResult>").append(NL);
		b.append("<dirEntry>").append(NL);
		b.append("<fileSize>");
		b.append(totalSize);
		b.append("</fileSize>").append(NL);
		b.append("</dirEntry>").append(NL);
		b.append("</dirResult>").append(NL);
		
		// write XML file to temporary cache
		try {
			File tempFile = File.createTempFile("oodt", ".zip");
			FileUtils.writeStringToFile(tempFile, b.toString());
			tempFile.deleteOnExit();
			return tempFile;
			
		} catch (IOException e) {
			throw new ProductException(e.getMessage());
		}
		
	}
	
	/**
	 * Debug method.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception  {
			
		String inputFilePath = "/usr/local/transport-ofsn/testdata//data/ear-a-i0028-4-sbn0001_smassii-v1.0/sbn_0001/DATA/DATA14/354_00.LBL";
		ZipSizeFileHandler self = new ZipSizeFileHandler();
		File outputFile = self.getOutputFile(inputFilePath);
		String xml = FileUtils.readFileToString(outputFile, "UTF-8");
		System.out.println(xml);
		
	}

}
