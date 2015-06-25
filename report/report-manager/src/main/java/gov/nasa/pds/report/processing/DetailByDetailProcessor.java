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
 * TODO: Move the pertinent javadoc details here from LogReformatProcessor as
 * the refactoring progresses.
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
				
				// Skip this log detail if no value is given
				if((segmentIndex + 1 < this.segmentedInput.size() &&
						lineRemaining.startsWith("- ")) ||
						(segmentIndex + 1 == this.segmentedInput.size() &&
						lineRemaining.startsWith("-"))){
					if(detail.isRequired()){
						throw new ProcessingException("The required log " +
								"detail " + detailName + " was not found " +
								"in input log line: " + line);
					}
					lineRemaining = lineRemaining.substring(1);
					continue;
				}
				
				// Get the value of the log detail
				Pattern pattern = Pattern.compile("(" +
						detail.getPattern() + ")[^\n]*");
				Matcher matcher = pattern.matcher(lineRemaining);
				if(!matcher.matches()){
					throw new ProcessingException("The date-time log detail " + 
							detailName + " with pattern " +
							detail.getPattern() + " was not found in " +
							"input log line: " + line);
				}
				String value = matcher.group(1);
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
				
				// Skip this log detail if no value is given
				if((segmentIndex + 1 < this.segmentedInput.size() &&
						lineRemaining.startsWith("- ")) ||
						(segmentIndex + 1 == this.segmentedInput.size() &&
						lineRemaining.startsWith("-"))){
					if(detail.isRequired()){
						throw new ProcessingException("The required log " +
								"detail " + detailName + " was not found " +
								"in input log line: " + line);
					}
					lineRemaining = lineRemaining.substring(1);
					continue;
				}
				
				// Get the value of the log detail
				Pattern pattern = Pattern.compile("(" +
						detail.getPattern() + ")[^\n]*");
				Matcher matcher = pattern.matcher(lineRemaining);
				if(!matcher.matches()){
					throw new ProcessingException("The log detail " + 
							detailName + " with pattern " +
							detail.getPattern() + " was not found in " +
							"input log line: " + line);
				}
				String value = matcher.group(1);
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
	
}