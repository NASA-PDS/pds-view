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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * This class contains utility methods to help with reformatting log files.
 * 
 * @author resneck
 *
 */
public abstract class ReformatUtil{
	
	private static Logger log = Logger.getLogger(
			ReformatUtil.class.getName());
	
	/**
	 * Read in the lines of a {@link File} as a {@link List} of Strings.
	 * 
	 * @param file					The file to be read in.
	 * @return						The contents of the file as a List of
	 * 								Strings.
	 * @throws ProcessingException	If the input file does not exist or an I/O
	 * 								error occurs.
	 */
	public static List<String> getFileLines(File file)
			throws ProcessingException{
		
		List<String> fileContent = new Vector<String>();
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while(line != null){
				fileContent.add(line);
				line = reader.readLine();
			}
			reader.close();
		}catch(FileNotFoundException e){
			throw new ProcessingException("The input log could not be found " +
					"for reformatting: " + e.getMessage());
		}catch(IOException e){
			try{
				reader.close();
			}catch(IOException ex){
				log.warning("An error occurred while closing the reader to " +
						"the input log for reformatting: " + ex.getMessage());
			}
			throw new ProcessingException("An error occurred while reading " +
					"the input log for reformatting: " + e.getMessage());
		}
		return fileContent;
		
	}
	
}