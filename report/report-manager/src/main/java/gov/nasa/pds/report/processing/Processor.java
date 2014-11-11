package gov.nasa.pds.report.processing;

import java.io.File;

public interface Processor{
	
	/**
	 * Process the files in the input directory and place them in the output
	 * directory.  The process performed will vary based on the implementation
	 * and the output will be placed in a sibling directory.  The name of that
	 * directory will vary based upon the implementation being used.
	 * 
	 * @param in					The directory containing the input files
	 * @throws ProcessingException	If an error occurs.	
	 */
	public void process(File in) throws ProcessingException;
	
}