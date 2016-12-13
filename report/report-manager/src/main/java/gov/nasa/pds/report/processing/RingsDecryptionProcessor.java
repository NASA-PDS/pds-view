package gov.nasa.pds.report.processing;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.util.CommandLineWorker;
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
		return "";
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.verifyConfiguration()
	 */
	public boolean verifyConfiguration(){
		return true;
	}
	
}