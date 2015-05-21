package gov.nasa.pds.report.processing;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.CommandLineWorker;
import gov.nasa.pds.report.util.Utility;

public class SimpleCommandProcessor implements Processor{

	public static final String OUTPUT_DIR_NAME = "simple_command";
	
	private String command;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void process(File in, File out) throws ProcessingException {
		
		if(this.command == null){
			throw new ProcessingException("The simple command processor has " +
					"not yet been properly configured.");
		}
		
		if(in == null){
			throw new ProcessingException("No input directory provided to " +
					"simple command processor");
		}
		if(out == null){
			throw new ProcessingException("No output directory provided to " +
					"simple command processor");
		}else if(!out.exists()){
			throw new ProcessingException("The output directory for simple " +
					"command processor does not exist: " +
					out.getAbsolutePath());
		}
		
		log.info("Performing simple command: " + this.command);
		
		// Get a list of files in the input directory
		String[] filenames = in.list();
		if(filenames == null || filenames.length == 0){
			throw new ProcessingException("No logs found in basic command " +
					"input directory " + in.getAbsolutePath());
		}
		
		// Run the command for each of the files
		for(int i = 0; i < filenames.length; i++){
			
			// Get file absolute path
			File file = new File(in, filenames[i]);
			String path = file.getAbsolutePath();
			String fileName = file.getName();
			
			// Trim the output file name as needed
			// TODO: Find a better way to do this
			if(fileName.endsWith(".gz")){
				fileName = fileName.replace(".gz", "");
			}
			
			// Format the command string for each file
			String cmd = this.command.replace("<input>", path);
			cmd = cmd.replace("<output>",
					new File(out, fileName).getAbsolutePath());
			
			// Execute the command
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
	
	public void configure(Properties props) throws ProcessingException{
		
		log.info("Configuring simple command processor");
		
		// Get the command to run during processing
		try{
			this.command = Utility.getNodePropsString(props,
					Constants.NODE_SIMPLE_COMMAND_KEY, true);
		}catch(ReportManagerException e){
			throw new ProcessingException("No command was given for " +
					"processing");
		}
		
		// Validate the command to run
		if(!this.command.contains("<input>") ||
				!this.command.contains("<output>")){
			throw new ProcessingException("The <input> and <output> tags " +
					"were not included in the given processing command");
		}
		
	}
	
}