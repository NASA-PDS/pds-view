package gov.nasa.pds.report.processing;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.util.Utility;
import gov.nasa.pds.report.util.CommandLineWorker;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class RingsDecryptionProcessor implements Processor{

	public static final String OUTPUT_DIR_NAME = "rings_decrypt";
	
	private static final String COMMAND = 
			"dd if=<input> | openssl des3 -d -k FeeFieFoeRings | tar xzvf - -C <output>";
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void process(File in) throws ProcessingException {
		
		if(in == null){
			throw new ProcessingException("No input directory provided to " +
					"find encrypted Rings logs");
		}
		
		log.info("Decrypting Rings logs");
		
		// Get a list of files in the input directory
		String[] filenames = in.list();
		if(filenames == null){
			log.warning("No Rings logs found in input directory " + 
					in.getAbsolutePath());
			return;
		}
		
		// Get the output directory
		File out = null;
		try{
			out = Utility.getStagingDir(in, OUTPUT_DIR_NAME);
		}catch(ReportManagerException e){
			throw new ProcessingException("An error occurred while creating " +
					"the staging directory for Rings log files: " + 
					e.getMessage());
		}
		
		// Run the decryption command for each of the files
		for(int i = 0; i < filenames.length; i++){
			
			// Get file absolute path
			String path = new File(in, filenames[i]).getAbsolutePath();
			
			// Format the decryption command string for each file
			log.fine("Decrypting Rings log " + path);
			String cmd = COMMAND.replace("<input>", path);
			cmd = cmd.replace("<output>", out.getAbsolutePath());
			
			// Execute the decryption command
			CommandLineWorker worker = new CommandLineWorker(cmd);
			int exitValue = worker.execute();
			if(exitValue != 0){
				log.warning("The command '" + cmd + "' failed with exit code " +
						exitValue);
			}
			
		}
	
	}
	
	public String getDirName(){
		return OUTPUT_DIR_NAME;
	}
	
	// This particular Processor doesn't require any configuration since its
	// purpose is so specific
	public void configure(Properties props){
		return;
	}
	
}