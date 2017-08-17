//	Copyright 2017, by the California Institute of Technology.
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
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.util.FileUtil;
import gov.nasa.pds.report.util.ReadWriter;

/**
 * This class is meant to filter logs in Apache/Combined format remove requests
 * that lack a client IP, such as those produced by Xymon and similar monitoring
 * software. 
 * 
 * @author resneck
 */
public class XymonFilterProcessor implements Processor{
	
	// The name of the directory where output is placed
	public static final String OUTPUT_DIR_NAME = "xymon_filter";
	
	private static Logger log = Logger.getLogger(
			XymonFilterProcessor.class.getName());
	
	// A simple counter to track the number of lines removed in a file
	private int linesRemoved = 0;
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.process()
	 */
	public void process(File in, File out) throws ProcessingException {
		
		if(in == null){
			throw new ProcessingException("No input directory provided to " +
					"Xymon filter processor");
		}
		if(out == null){
			throw new ProcessingException("No output directory provided to " +
					"Xymon filter processor");
		}else if(!out.exists()){
			throw new ProcessingException("The output directory for Xymon " +
					"filter processor does not exist: " +
					out.getAbsolutePath());
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
					"Xymon filter processor");
		}
		if(out == null){
			throw new ProcessingException("No output directory provided to " +
					"Xymon filter processor");
		}else if(!out.exists()){
			throw new ProcessingException("The output directory for Xymon " +
					"filter does not exist: " + out.getAbsolutePath());
		}
		
		this.processFileList(in, out);
		
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
	
	/**
	 * This particular Processor doesn't require any configuration since its
	 * purpose is so specific.
	 * 
	 * @see gov.nasa.pds.report.processing.Processor.verifyConfiguration()
	 */
	public boolean verifyConfiguration(){
		return true;
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.getDirName()
	 */
	public String getDirName(){
		return OUTPUT_DIR_NAME;
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.getOutputFileName()
	 */
	public String getOutputFileName(String inputFileName) {
		return inputFileName;
	}
	
	/**
	 * Process the provided {@link List} of {@link File}s.
	 * 
	 * @param files					A list of File objects pointing to log
	 * 								files to be processed.
	 * @param out					A {@link File} object pointing to the
	 * 								directory where output will be placed.
	 * @throws ProcessingException	If an error occurs.
	 */
	protected void processFileList(List<File> files, File out)
			throws ProcessingException{
		
		if(files.isEmpty()){
			throw new ProcessingException("No files found in input directory");
		}
		
		for(int i = 0; i < files.size(); i++){
			File file = files.get(i);
			try{
				log.info("Filtering log file " + file.getAbsolutePath() +
						" (" + Integer.toString(i + 1) + "/" +
						files.size() + ")");
				this.processFile(file, out);
			}catch(ProcessingException e){
				log.warning("An error occurred while filtering requests " +
						"with no client IP log file " +
						file.getAbsolutePath() + ": " + e.getMessage());
			}
		}
		
	}
	
	/**
	 * Process a given {@link File}, placing the filtered version inside a
	 * given directory.
	 * 
	 * @param in					The File that will be processed.
	 * @param outputDir				The directory where the output will be
	 * 								placed, represented as a {@link File}.
	 * @throws ProcessingException	If an error occurs while parsing the input
	 * 								file or while writing output.
	 */
	protected void processFile(File in, File outputDir)
			throws ProcessingException{
		
		ReadWriter rw = null;
		
		// Create ReadWriter to input and output file
		File output = new File(outputDir, in.getName());
		try{
			rw = new ReadWriter(in, output);
		}catch(ReportManagerException e){
			throw new ProcessingException("An error occurred while opening " +
					"streams to the input and output files: " + e.getMessage());
		}
			
		// Iterate over each line in the input file, filtering it.  This
		// happens until we reach the EOF or an error occurs.
		this.linesRemoved = 0;
		boolean keepProcessing = true;
		while(keepProcessing){
			try{
				if(!processLine(rw)){
					keepProcessing = false;	// EOF found
				}
			}catch(ProcessingException e){
				log.warning("An error occurred while reading line " +
						rw.getLineNum() + " in file " + in.getAbsolutePath() +
						": " + e.getMessage());
				keepProcessing = false;
			}
		}
		
		// Close the reader and writer
		rw.close();
		
		// Delete the created file if it's empty
		if(output.length() == 0){
			log.warning("Output log " + output.getAbsolutePath() + " will be " +
					"deleted since it's empty");
			rw.deleteOutput();
		}else if(this.linesRemoved > 0){
			log.info("Removed " + this.linesRemoved + "/" + rw.getLineNum() +
					" requests with no client IP from log " +
					in.getAbsolutePath());
		}
		
	}
	
	/**
	 * Use the provided reader to read in a line and only write it to output
	 * (using the provided writer) if the client IP is provided.
	 * 
	 * @param rw					A {@link ReadWriter} that is already
	 * 								initialized to read and write from the input
	 * 								and output files.
	 * @return						True if a line was read from the input
	 * 								file, otherwise false (indicating EOF).
	 * @throws ProcessingException	If an error occurs.
	 */
	protected boolean processLine(ReadWriter rw) throws ProcessingException{
		
		// Read the line from the file using the reader
		String line = null;
		try{
			line = rw.readLine();
		}catch(ReportManagerException e){
			throw new ProcessingException("An error occurred while reading " +
					"from the input file: " + e.getMessage());
		}
		
		// Signal if we have reached the end of the file
		if(line == null){
			return false;
		}
		
		// Check if line is part of the header, which we can ignore
		if(line.startsWith("#")){
			return true;
		}
		
		// Write the line to the output file if it doesn't start with a dash 
		// (indicating that the client IP is missing)
		if(!line.startsWith("-")){
			rw.writeLine(line);
		}else{
			this.linesRemoved = this.linesRemoved + 1;
		}
		
		return true;
		
	}
	
}