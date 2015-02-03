package gov.nasa.pds.report.processing;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.util.Utility;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * This class is used to reformat text-based log files so that they can be
 * parsed with a common Sawmill profile.  This reformatting uses regular
 * expression patterns to determine how to break down input and restructure
 * it for output.  Therefore, the class must be configured before
 * the processing can begin.
 * 
 * The patterns use the less-than and greater-than symbols to label the
 * substrings that are captured and rearranged.  Each such substring is split
 * into sections by one or more semicolons.  The first section is the name of
 * the substring.  In substrings in the input pattern, the second section is
 * the RE pattern used to capture that substring.  There can also be an
 * additional optional section to supply extra information, by setting flags
 * to label the substrings as a date-time or requiring a valid value to be present.
 * 
 * 
 * 
 * For example, when switching from IIS7 to the Apache/Combined format, we
 * start with a log that looks like this:
 * 
 * 2014-12-01 06:00:47 10.10.1.46 GET /merb/merxbrowser/help/Content/About+the+mission/MSL/Instruments/MSL+Navcam.htm - 443 - 66.249.69.46 Mozilla/5.0+(compatible;+Googlebot/2.1;++http://www.google.com/bot.html) - 200 0 0 10757 314 312
 * 
 * We want to reformat this log into something that looks like this:
 * 
 * 66.249.69.46 - - [01/Dec/2014:06:00:47 -0800] "GET /merb/merxbrowser/help/Content/About+the+mission/MSL/Instruments/MSL+Navcam.htm HTTP/1.1" 200 10757 "-" "Mozilla/5.0+(compatible;+Googlebot/2.1;++http://www.google.com/bot.html)"
 * 
 * To make this happen, we specify the input pattern like this:
 * 
 * <date-time;\d{4}-\d\d-\d\d \d\d:\d\d:\d\d;required,datetime=yyyy-MM-dd HH:mm:ss> <server-ip;[0-9.]+> <http-method;GET|PUT|POST|DELETE> <requested-resource;\S+> <uri-query;\S+> <server-port;\d+> <username;\S+> <client-ip;[0-9.]+;required> <client-browser;\S+> <referrer;\S+> <status-code;\d{3}> <substatus;\d+> <win32-status;\d+> <bytes-transfered;\d+> <bytes-received;\d+> <time-taken;\d+>
 * 
 * This processor then parses the lines in the input log and stores the log
 * details in a map.  Log details that are not specified (such as the URI query
 * in the example above), do not have their keys added to the map.  Using the
 * example input line above, the map would look like this:
 * 
 * date-time: 2014-12-01 06:00:47 (stored as a Date object)
 * server-ip: 10.10.1.46
 * http-method: GET
 * requested-resource: /merb/merxbrowser/help/Content/About+the+mission/MSL/Instruments/MSL+Navcam.htm
 * server-port: 443
 * client-ip: 66.249.69.46
 * client-browser: Mozilla/5.0+(compatible;+Googlebot/2.1;++http://www.google.com/bot.html)
 * status-code: 200
 * substatus: 0
 * win32-status: 0
 * bytes-transfered: 10757
 * bytes-received: 314
 * time-taken: 312
 * 
 * Finally, we specify the output pattern like this:
 * 
 * <client-ip;required> <user-id> <username> [<date-time;required,datetime=dd/MMM/yyyy:HH:mm:ss Z>] "<http-method> <requested-resource> <http-version>" <status-code> <bytes-transfered> "<referrer>" "<client-browser>"
 * 
 * This causes the data from the original log line to be output in the desired
 * format at the beginning of this example!
 * 
 * 
 * 
 * The substrings in the input and output patterns can optional be given
 * flags, separated by commas.
 * 
 * required: A substring with this flag must have a valid value, otherwise the
 * input line is discarded.  This will happen for an input substring if the
 * given value is "-" and for an output substring if the name is not present as
 * a key in the map created from input.
 * 
 * datetime: A substring with this flag designates a date-time using the format
 * following an equals sign, as shown in the examples above.
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
	public void process(File in, File out) throws ProcessingException{
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
	
	private class LogDetail{
		
		protected String name;
		protected String value;
		protected String pattern;
		protected boolean required = false;
		
		public LogDetail(String name, String pattern){
			
			this.name = name;
			this.pattern = pattern;
			
		}
		
		public void setRequired(boolean r){
			
			this.required = r;
			
		}
		
		public void parse(String input){
			
			// TODO: Parse the input using the pattern
			
		}
		
	}
	
	private class LogDateTimeDetail extends LogDetail{
		
		private Date date;
		private String inputFormat;
		
		public LogDateTimeDetail(String name, String pattern, String inputFormat){
			
			super(name, pattern);
			this.inputFormat = inputFormat;
			
		}
		
	}
	
}