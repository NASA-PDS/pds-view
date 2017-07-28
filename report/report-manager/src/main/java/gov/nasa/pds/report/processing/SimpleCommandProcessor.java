package gov.nasa.pds.report.processing;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.CommandLineWorker;
import gov.nasa.pds.report.util.FileUtil;
import gov.nasa.pds.report.util.Utility;

/**
 * This Processor implementation runs a specified command.
 * 
 * @author resneck
 */
public class SimpleCommandProcessor implements Processor{

	public static final String OUTPUT_DIR_NAME = "simple_command";
	
	protected String command;
	protected boolean useOutputFileName = true;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.process()
	 */
	public void process(File in, File out) throws ProcessingException {
		
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
		
		if(!this.verifyConfiguration()){
			throw new ProcessingException("The simple command processor has " +
			"not yet been properly configured.");
		}
		
		// Get a list of files in the input directory
		List<File> files = null;
		try{
			files = FileUtil.getFileList(in);
		}catch(ReportManagerException e){
			throw new ProcessingException("An error occurred while finding " +
					"the logs at " + in.getAbsolutePath());
		}
		
		// Run the command for each of the files
		this.processFileList(files, out);
	
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.process()
	 */
	public void process(List<File> in, File out) throws ProcessingException{
		
		if(in == null){
			throw new ProcessingException("No input file list provided to " +
					"log reformat processor");
		}
		if(out == null){
			throw new ProcessingException("No output directory provided to " +
					"log reformat processor");
		}else if(!out.exists()){
			throw new ProcessingException("The output directory for log " +
					"reformatting does not exist: " + out.getAbsolutePath());
		}
		
		if(!this.verifyConfiguration()){
			throw new ProcessingException("The simple command processor has " +
			"not yet been properly configured.");
		}
		
		this.processFileList(in, out);
		
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.getDirName()
	 */
	public String getDirName(){
		return OUTPUT_DIR_NAME;
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.configure()
	 */
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
		if(!this.validateCommand()){
			throw new ProcessingException("The <input> and <output> tags " +
					"were not included in the given processing command");
		}
		
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.LogReformatProcessor.getOutputFileName()
	 * 
	 * TODO: Make this more robust or figure out a better way to determine how
	 * files will be renamed.  Perhaps include something to specify this in
	 * configuration.  For example, we could specify the gunzip renaming with a
	 * config value of <inputname>-<.gz>+<>.  The value to untar a text file might
	 * look like <inputname>-<tar.gz>+<txt>.
	 */
	public String getOutputFileName(String inputFileName){
		
		if(this.command.startsWith("gunzip")){
			return inputFileName.replace(".gz", "");
		}else if(this.command.startsWith("zcat")){
			return inputFileName.replace(".gz", "");
		}else{
			return inputFileName;
		}
		
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.verifyConfiguration()
	 */
	public boolean verifyConfiguration(){
		
		return (this.command != null && !this.command.equals(""));
		
	}
	
	/**
	 * Run the given command on the list of provided files.
	 * 
	 * @param files					A {@link List} of {@link File}s to process
	 * @param out					A File object pointing to the directory
	 * 								where output is placed
	 * @throws ProcessingException	If an error occurs
	 */
	protected void processFileList(List<File> files, File out)
			throws ProcessingException{
		
		if(files.isEmpty()){
			throw new ProcessingException(
					"No files to process in input directory");
		}
		
		for(int i = 0; i < files.size(); i++){
			
			File file = files.get(i);
			
			// Create the command to run
			String cmd = this.command.replace("<input>",
					file.getAbsolutePath());
			String outputPath = "";
			if(this.useOutputFileName){
				outputPath = new File(out, this.getOutputFileName(
						file.getName())).getAbsolutePath();
			}else{
				outputPath = out.getAbsolutePath();
			}
			cmd = cmd.replace("<output>", outputPath);
			log.info("Running simple command: " + cmd + " (" +
					Integer.toString(i + 1) + "/" + files.size() + ")");
			
			// Execute the command
			CommandLineWorker worker = new CommandLineWorker(cmd);
			int exitValue = worker.execute();
			if(exitValue != 0){
				log.warning("The command '" + cmd + "' failed with exit code " +
						exitValue);
			}
			
		}
		
	}
	
	/**
	 * Validate the currently specified command to run.
	 * 
	 * @return	True if the command is valid, otherwise false
	 */
	protected boolean validateCommand(){
		
		return (this.command != null && this.command.contains("<input>") &&
				this.command.contains("<output>"));
		
	}
	
}