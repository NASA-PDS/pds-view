package gov.nasa.pds.report.processing;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;
import gov.nasa.pds.report.util.CommandLineWorker;

// TODO: Refactor this class to extend the SimpleCommandProcessor
public class RingsDecryptionProcessor implements Processor{

	public static final String OUTPUT_DIR_NAME = "rings_decrypt";
	
	private static final String COMMAND = 
			"dd if=<input> | openssl des3 -d -k FeeFieFoeRings | " +
			"tar xzvf - -C <output>";
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void process(File in, File out) throws ProcessingException {
		
		if(in == null){
			throw new ProcessingException("No input directory provided to " +
					"find encrypted Rings logs");
		}
		if(out == null){
			throw new ProcessingException("No output directory provided to " +
					"store unencrypted Rings logs");
		}else if(!out.exists()){
			throw new ProcessingException("The output directory for Rings " +
					"unencyption does not exist: " + out.getAbsolutePath());
		}
		
		log.info("Decrypting Rings logs");
		
		// Get a list of files in the input directory
		String[] filenames = in.list();
		if(filenames == null || filenames.length == 0){
			log.warning("No Rings logs found in input directory " + 
					in.getAbsolutePath());
			return;
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