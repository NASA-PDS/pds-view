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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a custom {@link Processor} that removes the version information from
 * file names in EN FTP logs.
 * 
 * @author resneck
 */
public class ENFtpProcessor extends DetailByDetailProcessor{
	
	/**
	 * Use the provided reader to read in a line, reformat it, then write the
	 * new version of the line using the provided writer.
	 * 
	 * @param reader				The reader already initialized to read from
	 * 								the input file.
	 * @param writer				A writer already initialized to write to
	 * 								the output file.
	 * @return						True if a line was read from the input
	 * 								file, otherwise false (indicating EOF).
	 * @throws ProcessingException	If an error occurs.
	 */
	protected boolean processLine(BufferedReader reader, PrintWriter writer)
			throws ProcessingException, IOException{
		
		// Read the line from the file using the reader
		String line = reader.readLine();
		
		// Signal if we have reached the end of the file
		if(line == null){
			return false;
		}
		
		// Check if line is part of the header, which we can ignore
		if(line.startsWith("#")){
			return true;
		}
		
		// Parse the input line, extracting log detail values
		this.parseInputLine(line);		
		
		// Discard any lines that don't represent tool downloads that we
		// care about
		String path = ((StringLogDetail)
				this.inputDetailMap.get("requested_resource")).getValue();
		if(!path.startsWith("/pub/toplevel/") ||
				!(path.endsWith(".zip") || path.endsWith(".tar.gz"))){
			this.resetDetailMaps();
			return true;
		}
		
		// Truncate the file name
		try{
			this.truncateFileName();
		}catch(ProcessingException e){
			throw new ProcessingException("An error occurred (" + 
					e.getMessage() + ") while truncating the requested file " +
					"name on line: " + line);
		}
		
		// Reformat the line using extracted values from input
		String reformattedLine = this.formatOutputLine();
		
		// Write the reformatted line to the output file
		if(reformattedLine != null && !reformattedLine.isEmpty()){
			writer.println(reformattedLine);
		}
		
		// Reset the detail values so that they don't carry over to the
		// next line
		this.resetDetailMaps();
		
		return true;
		
	}
	
	// Strip the file path down to the file name and remove the version
	// information
	private void truncateFileName() throws ProcessingException{
		
		// Fetch the file path from the LogDetail that stores the input value
		StringLogDetail pathDetail =
				(StringLogDetail)this.inputDetailMap.get("requested_resource");
		String path = pathDetail.getValue();
		
		// Strip the path down to the file name without version info
		if(path.contains("/")){
			path = path.substring(path.lastIndexOf("/") + 1);
		}
		Matcher match = Pattern.compile("([A-Za-z0-9\\.]+)[_-].+").matcher(path);
		if(!match.matches()){
			throw new ProcessingException("The file name " + path +
					" did not match the truncating pattern");
		}
		path = match.group(1);
		
		// Set the value of the LogDetail to the truncated file name
		pathDetail.setValue(path);
		
	}
	
}