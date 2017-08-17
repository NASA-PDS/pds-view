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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.util.FileUtil;
import gov.nasa.pds.report.util.ReadWriter;

/**
 * This processor filters Apache/Combined logs to remove proxies from the
 * client IP details.
 * 
 * @author resneck
 */
public class ProxyFilterProcessor implements Processor{

	// The name of the directory where output is placed
	public static final String OUTPUT_DIR_NAME = "proxy_filter";
	
	private static Logger log = Logger.getLogger(
			ProxyFilterProcessor.class.getName());
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.process()
	 */
	public void process(File in, File out) throws ProcessingException {
		
		if(in == null){
			throw new ProcessingException("No input directory provided to " +
					"proxy filter processor");
		}
		if(out == null){
			throw new ProcessingException("No output directory provided to " +
					"proxy filter processor");
		}else if(!out.exists()){
			throw new ProcessingException("The output directory for proxy " +
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
	public void process(List<File> in, File out) throws ProcessingException {
		
		if(in == null){
			throw new ProcessingException("No input file list provided to " +
					"proxy filter processor");
		}
		if(out == null){
			throw new ProcessingException("No output directory provided to " +
					"proxy filter processor");
		}else if(!out.exists()){
			throw new ProcessingException("The output directory for proxy " +
					"filter does not exist: " + out.getAbsolutePath());
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
	 * @see gov.nasa.pds.report.processing.Processor.getOutputFileName()
	 */
	public String getOutputFileName(String inputFileName) {
		return inputFileName;
	}

	/**
	 * This particular Processor doesn't require any configuration since its
	 * purpose is so specific.
	 * 
	 * @see gov.nasa.pds.report.processing.Processor.configure()
	 */
	public void configure(Properties props) throws ProcessingException {
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
		}
		
	}
	
	/**
	 * Use the provided reader to read in a line, strip away any proxy IPs from
	 * the client details, and then write it to output using the provided writer.
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
		
		// Look for proxies in the client IP (the first log detail).
		Pattern p = Pattern.compile("([ 0-9.,]+) -.+");
		Matcher m = p.matcher(line);
		if(!m.matches()){
			throw new ProcessingException("Proxy filter could not find " +
					"client IP detail in line: " + line);
		}
		String clientIP = m.group(1);
		
		// Proxies are shown as a comma-separated list in the client IP detail.
		// Only keep the original client (the first IP in the list) if proxy IPs
		// are listed.
		if(clientIP.contains(",")){
			String restOfLine = line.substring(clientIP.length());
			clientIP = clientIP.substring(0, clientIP.indexOf(",")).trim();
			line = clientIP + restOfLine;
		}
		rw.writeLine(line);
		
		return true;
		
	}
	
}