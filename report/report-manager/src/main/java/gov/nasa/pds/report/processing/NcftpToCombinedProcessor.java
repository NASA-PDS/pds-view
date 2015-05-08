//	Copyright 2015, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//
package gov.nasa.pds.report.processing;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class NcftpToCombinedProcessor extends LogReformatProcessor{

	// The name of the directory where output is placed
	public static final String OUTPUT_DIR_NAME = "ncftp_to_combined_reformat";
	
	private static Logger log = Logger.getLogger(
			NcftpToCombinedProcessor.class.getName());
	
	@Override
	public void process(File in, File out) throws ProcessingException {
		
		if(in == null){
			throw new ProcessingException("No input directory provided to " +
					"ncftp reformat processor");
		}else if(!in.exists()){
			throw new ProcessingException("The input directory for ncftp " +
					"reformatting does not exist: " + in.getAbsolutePath());
		}
		if(out == null){
			throw new ProcessingException("No output directory provided to " +
					"ncftp reformat processor");
		}else if(!out.exists()){
			throw new ProcessingException("The output directory for ncftp " +
					"reformatting does not exist: " + out.getAbsolutePath());
		}
		
		log.info("Reformatting NcFTP logs in " + in.getAbsolutePath());
		
		// Get a list of files in the input directory
		String[] filenames = in.list();
		if(filenames == null || filenames.length == 0){
			log.warning("No logs found for reformatting in input directory " + 
					in.getAbsolutePath());
			return;
		}
		
		// Reformat each of the files
		for(int i = 0; i < filenames.length; i++){
			
			// Get file absolute path
			String fileName = filenames[i];
			File file = new File(in, filenames[i]);
			
			// Reformat the file
			try{
				this.processFile(file, out);
			}catch(ProcessingException e){
				log.warning("An error occurred while reformatting log file " +
						fileName + ": " + e.getMessage());
			}
			
		}
		
	}

	@Override
	public String getDirName(){
		
		return OUTPUT_DIR_NAME;
		
	}

	@Override
	public void configure(Properties props) throws ProcessingException {
		// TODO Auto-generated method stub
		
	}
	
	// Iterate over the substrings in the input pattern, capturing the
	// value of those substrings
	protected void parseInputLine(String line) throws ProcessingException{
		
		//TODO: Implement me!
		
	}
	
	// Iterate over the substrings in the output pattern, creating the
	// output version using currently stored input values
	protected String formatOutputLine() throws ProcessingException{
		
		//TODO: Implement me!
		return null;
		
	}
	
}