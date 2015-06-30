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
import gov.nasa.pds.report.util.Utility;

import java.text.ParseException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This {@link LogReformatProcessor} sub-class is used to transform logs in
 * a more detail-oriented manner.  This allows the user to perform operations
 * such as reordering log details or changing the format of a particular log
 * detail (such as a date-time).
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
 * To make this happen, we specify the input line specification like this:
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
 * Finally, we specify the output line specification like this:
 * 
 * <client-ip;required> <user-id> <username> [<date-time;required,datetime=dd/MMM/yyyy:HH:mm:ss Z>] "<http-method> <requested-resource> <http-version;default=HTTP/1.1>" <status-code> <bytes-transfered> "<referrer>" "<client-browser>"
 * 
 * This causes the data from the original log line to be output in the desired
 * format at the beginning of this example!
 * 
 * @author resneck
 *
 */
public class DetailByDetailProcessor extends LogReformatProcessor{
	
	// The name of the directory where output is placed
	public static final String OUTPUT_DIR_NAME = "detail_text_reformat";
	
	private static Logger log = Logger.getLogger(
			DetailByDetailProcessor.class.getName());
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.getDirName()
	 */
	public String getDirName(){
		
		return OUTPUT_DIR_NAME;
		
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.configure()
	 */
	public void configure(Properties props) throws ProcessingException{
		
		log.info("Configuring detail-by-detail log reformatting processor");
		
		configure(props, Constants.NODE_DETAIL_REFORMAT_INPUT,
				Constants.NODE_DETAIL_REFORMAT_OUTPUT, true);
		
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.LogReformatProcessor.parseInputLine()
	 */
	protected void parseInputLine(String line) throws ProcessingException{
		
		String lineRemaining = line;
		for(int segmentIndex = 0; segmentIndex < this.segmentedInput.size();
				segmentIndex++){
			
			String segment = this.segmentedInput.get(segmentIndex);
			
			// Date-time log detail
			if(segment.matches("<\\w+-dt>")){	
				
				// Get the log detail
				String detailName =
						segment.substring(1, segment.length() - 4);
				DateTimeLogDetail detail = (DateTimeLogDetail)
						this.inputDetailMap.get(detailName);
				
				// Get the value of the log detail
				String value = this.getNextDetail(lineRemaining, segmentIndex,
						detail, lineRemaining);
				if(value == null){
					lineRemaining = lineRemaining.substring(
							detail.getEmptyValue().length());
					continue;
				}
				try{
					detail.setDate(value);
				}catch(ParseException e){
					throw new ProcessingException("An error occurred " +
							"while parsing date " + value + 
							" using format " + detail.getFormat() +
							" for log detail " + detail.getName() +
							" in input log line: " + e.getMessage());
				}
				
				// Remove the value from the line, since it has been parsed
				lineRemaining = lineRemaining.substring(value.length());
				
			}
			
			// String log detail
			else if(segment.matches("<\\w+>")){	
				
				// Get the log detail
				String detailName =
						segment.substring(1, segment.length() - 1);
				StringLogDetail detail = (StringLogDetail)
						this.inputDetailMap.get(detailName);
				
				// Get the value of the log detail
				String value = this.getNextDetail(lineRemaining, segmentIndex,
						detail, lineRemaining);
				if(value == null){
					lineRemaining = lineRemaining.substring(
							detail.getEmptyValue().length());
					continue;
				}
				detail.setValue(value);
				
				// Remove the value from the line, since it has been parsed
				lineRemaining = lineRemaining.substring(value.length());
				
			}
			
			// Literal string (other than those expected at the end of the
			// line, which we can safely discard)
			else if(segmentIndex < this.segmentedInput.size() - 1){	
				
				// Check if the literal string contains only whitespace
				if(!segment.trim().isEmpty()){
					
					if(!lineRemaining.startsWith(segment)){
						throw new ProcessingException("The expected line " +
								"segment \"" + segment + "\" was not found " +
								"where expected in input log line: " + line);
					}
					
					// Remove the literal string from the line being parsed,
					// since it doesn't contain any log details
					lineRemaining = lineRemaining.substring(segment.length());
					
				}else{
					
					// Remove whitespace at the start of the remaining line
					Pattern pattern = Pattern.compile("([ \t\r]+)\\S+[^\n]*");
					Matcher matcher = pattern.matcher(lineRemaining);
					if(!matcher.matches()){
						throw new ProcessingException("The expected " +
								"whitespace was not found where expected in " +
								"input log line: " + line);
					}
					String value = matcher.group(1);
					lineRemaining = lineRemaining.substring(value.length());
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Get the value of the next detail in the remaining input log line.
	 * 
	 * @param lineRemaining			The portion of the input log line that has
	 * 								not yet been processed.
	 * @param segmentIndex			The segment index used to determine if the
	 * 								log detail sought is at the end of the input
	 * 								log line. 
	 * @param detail				The {@link LogDetail} sought at the start of
	 * 								the remaining input log line.
	 * @param line					The input log line.
	 * @return						A substring from the remaining input log
	 * 								line representing the log detail value.
	 * @throws ProcessingException	If the value for a required log detail is
	 * 								not provided or the start of the remaining
	 * 								log input line does not match the pattern
	 * 								provided for the log detail being sought.
	 */
	protected String getNextDetail(String lineRemaining, int segmentIndex,
			LogDetail detail, String line) throws ProcessingException{

		String detailName = detail.getName();
		
		// Skip this log detail if no value is given
		if((segmentIndex + 1 < this.segmentedInput.size() &&
				lineRemaining.startsWith(detail.getEmptyValue() + " ")) ||
				(segmentIndex + 1 == this.segmentedInput.size() &&
				lineRemaining.startsWith(detail.getEmptyValue()))){
			if(detail.isRequired()){
				throw new ProcessingException("The required log detail " +
						detailName + " was not found in input log line: " +
						line);
			}
			return null;
		}
		
		// Get the value of the log detail
		Pattern pattern = Pattern.compile("(" +
				detail.getPattern() + ")[^\n]*");
		Matcher matcher = pattern.matcher(lineRemaining);
		if(!matcher.matches()){
			throw new ProcessingException("The log detail " + detailName +
					" could not be found with pattern " + detail.getPattern() +
					" in the input log line: " + line);
		}
		String value = matcher.group(1);
		
		return value;
		
	}
	
}