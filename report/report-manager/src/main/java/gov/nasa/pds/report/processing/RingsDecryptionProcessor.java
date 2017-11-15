package gov.nasa.pds.report.processing;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.util.CommandLineWorker;
import gov.nasa.pds.report.util.FileUtil;
import gov.nasa.pds.report.util.Utility;

/**
 * This sub-class of the SimpleCommandProcessor, performs the specific command
 * necessary to decryprt and decompress log files from the Rings node.
 * 
 * @author resneck
 */
public class RingsDecryptionProcessor extends SimpleCommandProcessor{

	public static final String OUTPUT_DIR_NAME = "rings_decrypt";
	
	private static final String COMMAND = 
			"dd if=\"<input>\" | openssl des3 -d -k FeeFieFoeRings | " +
			"tar xzvf - -C \"<output>\"";
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public RingsDecryptionProcessor(){
		this.command = COMMAND;
		this.useOutputFileName = false;
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.getDirName()
	 */
	public String getDirName(){
		return OUTPUT_DIR_NAME;
	}
	
	/**
	 * This particular Processor doesn't require any configuration since its
	 * purpose is so specific.
	 * 
	 * @see gov.nasa.pds.report.processing.Processor.configure()
	 */
	public void configure(Properties props){
		return;
	}

	@Override
	/**
	 * @see gov.nasa.pds.report.processing.Processor.getOutputFileName()
	 */
	public String getOutputFileName(String inputFileName) {
		return inputFileName.replace("tar.gz", "txt");
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.verifyConfiguration()
	 */
	public boolean verifyConfiguration(){
		return true;
	}
	
	@Override
	protected void processFileList(List<File> files, File out)
			throws ProcessingException{
		
		// Perform nominal processing on the file list
		super.processFileList(files, out);
		
		// Some of the raw Rings create a large directory structure when
		// decompressed, not just an output file.  We have to recursively
		// search for these logs inside the directory trees created during
		// decompression.
		//
		// The root of this directory tree is usually called "usr".
		File root = new File(out, "usr");
		if(root.exists()){
			try{
				
				// Rescue the log files
				FileUtil.getFilesFromDirTree(root, out);
				
				// Delete the left-over directory tree created during
				// decompression.  This ensures that subsequent processors do
				// not try to process the directory(s) and that the report
				// manager doesn't try to copy it into the final directory
				// (which results in an error).
				FileUtils.forceDelete(root);
				
			}catch(ReportManagerException e){
				throw new ProcessingException("An error occurred while copying" +
						"the log out of the uncompressed directory tree: " +
						e.getMessage());
			}catch(IOException e) {
				throw new ProcessingException("En error occurred while cleaning " +
						"up the directory left-over from decompression: " +
						e.getMessage());
			}
		}
		
	}
	
}