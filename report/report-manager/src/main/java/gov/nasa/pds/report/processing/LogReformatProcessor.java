package gov.nasa.pds.report.processing;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.util.Utility;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * This class is used to reformat text-based log files so that they can be
 * parsed with a common Sawmill profile.  This reformatting requires patterns
 * to determine the format of how logs are input and output.   The processor
 * then uses these patterns to determine how to break down input and
 * restructure it for output.  Therefore, the class must be configured before
 * the processing can begin.
 * 
 * The patterns use the less-than and greater-than symbols to label the
 * substrings that are captured and rearranged.  Each such substring is split
 * by a vertical bar.  The first section is the name of the substring and the
 * second is the RE pattern used to capture that substring.  There can also be
 * a third optional section to supply extra information, such as labeling the
 * substring as a date-time.
 * 
 * For example, the following patterns are used to switch from IIS7 to
 * the Apache/Combined format.
 * 
 * TODO: Actually write the examples and explain how they work
 * 
 * input:
 * 
 * 
 * output:
 * 
 * 
 * @author resneck
 *
 */
public class LogReformatProcessor implements Processor{
	
	// The name of the directory where output is placed
	public static final String OUTPUT_DIR_NAME = "text_reformat";
	
	// The profiles keys used to designate the input and output patterns
	private static final String INPUT_KEY = "input_log_pattern";
	private static final String OUTPUT_KEY = "output_log_pattern";
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private Vector<HashMap<String, String>> inputMetaPattern = null;
	private Vector<HashMap<String, String>> outputMetaPattern = null;

	@Override
	public void process(File in) throws ProcessingException{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDirName(){
		
		return OUTPUT_DIR_NAME;
		
	}

	@Override
	public void configure(Properties props) throws ProcessingException{
		
		try{
			String rawInputPattern =
					Utility.getNodePropsString(props, INPUT_KEY, true);
			String rawOutputPattern =
				Utility.getNodePropsString(props, OUTPUT_KEY, true);
			this.inputMetaPattern = processPattern(rawInputPattern);
			this.outputMetaPattern = processPattern(rawOutputPattern);
		}catch(ReportManagerException e){
			throw new ProcessingException("Input and output patterns were " +
					"not provided for log reformatting");
		}catch(ProcessingException e){
			throw new ProcessingException("An error occurred while " +
					"interpretting input and output patterns: " +
					e.getMessage());
		}
		
	}
	
	// Take the provided pattern and break it down into a List of Maps (called
	// a meta-pattern here).  Each Map will specify the RE pattern and any
	// other details about the substring, such as name, date-time format, etc.
	// In both the input and output pattern, we use the substrings in the order
	// in which they are presented.
	private Vector<HashMap<String, String>> processPattern(String pattern)
			throws ProcessingException{
		
		Vector<HashMap<String, String>> metaPattern =
				new Vector<HashMap<String, String>>();
		
		// TODO: Implement me!
		
		return metaPattern;
		
	}
	
}