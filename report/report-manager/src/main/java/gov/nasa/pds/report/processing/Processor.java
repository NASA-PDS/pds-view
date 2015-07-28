package gov.nasa.pds.report.processing;

import java.io.File;
import java.util.List;
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
	 * Process the files in the given list and place them in the output
	 * directory.  The process performed will vary based on the implementation
	 * and the output will be placed in a sibling directory.  The name of that
	 * directory will vary based upon the implementation being used.
	 * 
	 * @param in					A {@link List} of {@link File} objects
	 * 								pointing to files that will be processed
	 * @param out					The directory where output is placed
	 * @throws ProcessingException	If an error occurs.	
	 */
	public void process(List<File> in, File out) throws ProcessingException;
	
	/**
	 * Get the name of the directory where the output of the processor is
	 * placed.
	 * 
	 * @return	The name of the directory created by the Processor.
	 */
	public String getDirName();
	
	/**
	 * Get the name that will be assigned to an output file, given the input
	 * file name.
	 * 
	 * @param inputFileName	The name of the input file
	 * @return				The name that will be assigned to the output file
	 * 						created from the input file
	 */
	public String getOutputFileName(String inputFileName);
	
	/**
	 * Configure the Processor, providing the details needed to process logs. 
	 * 
	 * @param props					A {@link Properties} containing the needed
	 * 								configuration values.
	 * @throws ProcessingException	If the provided Properties do not contain
	 * 								the needed configuration values.
	 */
	public void configure(Properties props) throws ProcessingException;
	
	/**
	 * Verify that the Processor has been properly configured.  This method 
	 * should be invoked at the beginning or both process() methods.
	 * 
	 * @return	True if the Processor has been properly configured, otherwise
	 * 			false.
	 */
	public boolean verifyConfiguration();
	
}