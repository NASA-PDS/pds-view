package gov.nasa.pds.report.processing;

import java.io.File;
import java.util.Properties;

public interface Processor{
	
	/**
	 * Process the files in the input directory and place them in the output
	 * directory.  The process performed will vary based on the implementation
	 * and the output will be placed in a sibling directory.  The name of that
	 * directory will vary based upon the implementation being used.
	 * 
	 * @param in					The directory containing the input files
	 * @param out					The directory where output is placed
	 * @throws ProcessingException	If an error occurs.	
	 */
	public void process(File in, File out) throws ProcessingException;
	
	/**
	 * Get the name of the directory where the output of the processor is
	 * placed.
	 * 
	 * @return	The name of the directory created by the Processor.
	 */
	public String getDirName();
	
	/**
	 * Configure the Processor, providing the details needed to process logs. 
	 * 
	 * @param props					A {@link Properties} containing the needed
	 * 								configuration values.
	 * @throws ProcessingException	If the provided Properties do not contain
	 * 								the needed configuration values.
	 */
	public void configure(Properties props) throws ProcessingException;
	
}