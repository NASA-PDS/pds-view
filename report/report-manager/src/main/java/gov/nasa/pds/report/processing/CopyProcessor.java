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

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/**
 * This {@link Processor} implementation is used to copy logs from a given
 * source directory--specified in the configuration--to a given destination.
 * This is useful when you need to use the same logs for different Sawmill
 * profiles.
 * 
 * @author resneck
 */
public class CopyProcessor implements Processor{

	// The name of the directory where output is placed
	public static final String OUTPUT_DIR_NAME = "copy";
	
	private static Logger log = Logger.getLogger(
			CopyProcessor.class.getName());
	
	public File inputDirectory;
	
	@Override
	/**
	 * @see gov.nasa.pds.report.processing.Processor.process()
	 */
	public void process(File in, File out) throws ProcessingException {
		
		if(out == null){
			throw new ProcessingException("No output directory provided to " +
					"copy processor");
		}else if(!out.exists()){
			throw new ProcessingException("The output directory for copying " +
					"logs does not exist: " + out.getAbsolutePath());
		}
		
		// Check that the processor has been configured
		if(!this.verifyConfiguration()){
			throw new ProcessingException("The copy processor has " +
			"not been configured previously");
		}
		
		log.info("Copying logs from " + this.inputDirectory.getAbsolutePath());
		
		// Get a list of files in the input directory
		List<File> files = null;
		try{
			files = FileUtil.getFileList(this.inputDirectory);
		}catch(ReportManagerException e){
			throw new ProcessingException("An error occurred while finding " +
					"the logs in input directory " +
					this.inputDirectory.getAbsolutePath());
		}
		
		// Copy the files
		this.processFileList(files, out);
		
	}

	@Override
	/**
	 * @see gov.nasa.pds.report.processing.Processor.process()
	 */
	public void process(List<File> in, File out) throws ProcessingException {
		
		if(out == null){
			throw new ProcessingException("No output directory provided to " +
					"copy processor");
		}else if(!out.exists()){
			throw new ProcessingException("The output directory for " +
					"log copying does not exist: " + out.getAbsolutePath());
		}
		
		// Check that the processor has been configured
		if(!this.verifyConfiguration()){
			throw new ProcessingException("The copy processor has " +
				"not been configured previously");
		}
		
		// Copy the files
		this.processFileList(in, out);
		
	}

	@Override
	/**
	 * @see gov.nasa.pds.report.processing.Processor.getDirName()
	 */
	public String getDirName() {
		
		return OUTPUT_DIR_NAME;
		
	}

	@Override
	/**
	 * @see gov.nasa.pds.report.processing.Processor.getOutputFileName()
	 */
	public String getOutputFileName(String inputFileName) {
		return inputFileName;
	}

	@Override
	/**
	 * @see gov.nasa.pds.report.processing.Processor.configure()
	 */
	public void configure(Properties props) throws ProcessingException {
		
		log.info("Configuring copy processor");
		
		String path = props.getProperty(Constants.NODE_COPY_INPUT);
		
		// Validate path
		if(path == null){
			throw new ProcessingException("No copy source provided");
		}
		File file = new File(path);
		if(!file.isDirectory()){
			throw new ProcessingException("Non-directory provided to copy " +
					"processor: " + path);
		}
		
		this.inputDirectory = file;
		
	}

	@Override
	/**
	 * @see gov.nasa.pds.report.processing.Processor.verifyConfiguration()
	 */
	public boolean verifyConfiguration() {
		
		if(this.inputDirectory == null){
			return false;
		}
		return true;
		
	}
	
	protected void processFileList(List<File> in, File out)
			throws ProcessingException{
		
		if(in.isEmpty()){
			throw new ProcessingException("No files to copy in input directory");
		}
		
		// Copy the files to the given output directory
		for(int i = 0; i < in.size(); i++){
			File file = in.get(i);
			try{
				log.info("Copying log file " + file.getAbsolutePath() +
						" (" + Integer.toString(i + 1) + "/" + in.size() + ")");
				FileUtils.copyFileToDirectory(file, out);
			}catch(IOException e){
				log.warning("An error occurred while copying " +
						file.getAbsolutePath() + " to " +
						out.getAbsolutePath() + ": " + e.getMessage());
			}
		}
		
	}
	
}
