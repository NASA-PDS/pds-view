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
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.Utility;

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
 * <client-ip;required> <user-id> <username> [<date-time;required,datetime=dd/MMM/yyyy:HH:mm:ss Z>] "<http-method> <requested-resource> <http-version;default=HTTP/1.1>" <status-code> <bytes-transfered> "<referrer>" "<client-browser>"
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
 * default: A substring with this flag will default to the value following an
 * equals sign, as shown in the example above.
 * 
 * @author resneck
 * 
 */
public class LogReformatProcessor implements Processor{
	
	// The name of the directory where output is placed
	public static final String OUTPUT_DIR_NAME = "text_reformat";
	
	private static Logger log = Logger.getLogger(
			LogReformatProcessor.class.getName());
	
	/*
	 * These lists store the input and output patterns in a format that can be
	 * more readily used during processing.  In this format, the pattern is
	 * divided into segments stored in the list in the same order that these
	 * segments occur in the pattern.  Segments that represent literal strings
	 * are unchanged, while log details are represented by the name of the log
	 * detail (with "-dt" appended for date-time log details) inside of
	 * brackets.
	 * 
	 * For example, the input pattern
	 * 
	 * "Employee Name:<name;\w+;required> Age:<age;\d> Date of Hire:<doh;\d{4}-\d\d-\d\d;datetime>"
	 * 
	 * would have the following segment list after parsing:
	 * 
	 * "Employee Name:", "<name>", " Age:", "<age>", " Date of Hire:", "<doh-dt>"
	 */
	private List<String> segmentedInput;
	private List<String> segmentedOutput;
	
	// The number of error-causing lines that we allow before giving up on the
	// file.  A value of 0 allows no errors.  A value of -1 allows any number
	// of errors, so the input file will be processed completely, though the
	// output file will only contain the output from lines that did not cause
	// errors.
	private int errorLinesAllowed;
	private static String DEFAULT_ERRORS_ALLOWED = "0";
	
	// The Maps that we use to store the log details as objects
	protected Map<String, LogDetail> inputDetailMap;
	protected Map<String, LogDetail> outputDetailMap;

	/**
	 * @see gov.nasa.pds.report.processing.Processor.getDirName()
	 */
	public String getDirName(){
		
		return OUTPUT_DIR_NAME;
		
	}
	
	/**
	 * Read in a log file, reformat it as per configured, and place the output
	 * in the given directory.
	 * 
	 * @param in					The {@link File} that will be reformatted.
	 * @param out					The directory where output will be placed,
	 * 								represented as a {@link File}.
	 * @throws ProcessingException	If any parameters are null or invalid, if
	 * 								the processor hasn't been configured, or if
	 * 								an error occurs during processing.
	 */
	public void process(File in, File out) throws ProcessingException{
		
		if(in == null){
			throw new ProcessingException("No input directory provided to " +
					"log reformat processor");
		}
		if(out == null){
			throw new ProcessingException("No output directory provided to " +
					"log reformat processor");
		}else if(!out.exists()){
			throw new ProcessingException("The output directory for log " +
					"reformatting does not exist: " + out.getAbsolutePath());
		}
		
		// Check that the processor has been configured
		if(this.inputDetailMap == null || this.inputDetailMap.isEmpty() || 
				this.outputDetailMap == null || this.outputDetailMap.isEmpty()){
			throw new ProcessingException("The log reformat processor has " +
					"not been configured previously");
		}
		
		log.info("Now reformatting logs in " + in.getAbsolutePath());
		
		// Get a list of files in the input directory
		String[] filenames = in.list();
		if(filenames == null || filenames.length == 0){
			log.warning("No logs found for reformatting in input directory " + 
					in.getAbsolutePath());
			return;
		}
		
		// Reformat each of the files
		for(int i = 0; i < filenames.length; i++){
			
			// Get file absolute path
			String fileName = filenames[i];
			File file = new File(in, filenames[i]);
			
			// Reformat the file
			try{
				this.processFile(file, out);
			}catch(ProcessingException e){
				log.warning("An error occurred while reformatting log file " +
						file.getAbsolutePath() + ": " + e.getMessage());
			}
			
		}
		
	}

	/**
	 * TODO: Write some proper documentation here
	 */
	public void configure(Properties props) throws ProcessingException{
		
		log.info("Configuring log reformatting processor");
		
		// Extract the raw input and output patterns from the given Properties
		String inputPattern = null;
		String outputPattern = null;
		try{
			inputPattern = Utility.getNodePropsString(props,
					Constants.NODE_REFORMAT_INPUT_KEY, true);
			outputPattern = Utility.getNodePropsString(props,
					Constants.NODE_REFORMAT_OUTPUT_KEY, true);
		}catch(ReportManagerException e){
			throw new ProcessingException("Input and output patterns were " +
					"not provided for log reformatting");
		}
		
		// Parse the input pattern
		//log.finer("Parsing input pattern: " + inputPattern);
		this.inputDetailMap = new HashMap<String, LogDetail>();
		this.segmentedInput = new Vector<String>();
		this.parsePattern(inputPattern, this.segmentedInput,
				this.inputDetailMap, 2, 3);
		
		// Parse the output pattern
		//log.finer("Parsing output pattern: " + outputPattern);
		this.outputDetailMap = new HashMap<String, LogDetail>();
		this.segmentedOutput = new Vector<String>();
		this.parsePattern(outputPattern, this.segmentedOutput,
				this.outputDetailMap, 1, 2);	
		
		// Validate output pattern
		for(String outputKey: this.outputDetailMap.keySet()){
			if(this.outputDetailMap.get(outputKey).isRequired() && 
					!this.inputDetailMap.containsKey(outputKey)){
				throw new ProcessingException("The input log reformat " +
						"pattern does not specify a log detail required " +
						"by the output pattern: " + outputKey);
			}
			if(this.inputDetailMap.containsKey(outputKey)){
				String inputType = this.inputDetailMap.get(outputKey).getType();
				String outputType = this.outputDetailMap.get(outputKey).getType();
				//log.finest("Detail: " + outputKey + " input type: " +
				//		inputType + " output type: " + outputType);
				if(!inputType.equals(outputType)){
					throw new ProcessingException("The log reformat " +
							"patterns contain the detail " + outputKey +
							" which is specified as different types in " +
							"the input and output patterns");
				}
			}
		}
		
		// Determine how many errors we will tolerate per file
		try{
			this.errorLinesAllowed = Integer.parseInt(System.getProperty(
					Constants.REFORMAT_ERRORS_PROP, DEFAULT_ERRORS_ALLOWED));
		}catch(NumberFormatException e){
			log.warning("Using the default number of errors allowed during " +
					"reformatting (" + DEFAULT_ERRORS_ALLOWED + "), as the " +
					"value specified in default.properties is invalid: " +
					System.getProperty(Constants.REFORMAT_ERRORS_PROP));
			this.errorLinesAllowed = Integer.parseInt(DEFAULT_ERRORS_ALLOWED);
		}
		
		// Print debug info
		//log.finer("Input pattern: " + this.segmentedInput.toString());
		//log.finer("Output pattern: " + this.segmentedOutput.toString());
		
	}
	
	/**
	 * TODO: Change the algorithm of this method to read in each line,
	 * reformat it, and output it before moving on to the next.  This should
	 * reduce our memory usage.
	 * 
	 * Process a given {@link File}, placing the reformatted version inside a
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
		
		log.info("Reformatting log file " + in.getAbsolutePath());
		
		BufferedReader reader = null;
		PrintWriter writer = null;
		
		// Open reader for input file
		try{
			reader = new BufferedReader(new FileReader(in));
		}catch(FileNotFoundException e){
			throw new ProcessingException("The input log could not be found " +
					"for reformatting at " + in.getAbsolutePath() + ": " +
					e.getMessage());
		}
		
		// Open the output writer
		File out = new File(outputDir, in.getName());
		try{
			writer = new PrintWriter(out);
		}catch(FileNotFoundException e){
			this.closeReaderWriter(reader, writer);
			throw new ProcessingException("The output log could not be found " +
					"for reformatting at " + out.getAbsolutePath() + ": " +
					e.getMessage());
		}
			
		// Iterate over each line in the input file, processing it.  This
		// happens until we reach the EOF or a number of errors deemed too high.
		boolean keepProcessing = true;
		int lineNum = 0;
		int errors = 0;
		while(keepProcessing){
			try{
				if(!this.processLine(reader, writer)){
					keepProcessing = false;
				}
			}catch(ProcessingException e){
				log.warning("An error occurred while processing line " +
						lineNum + " in file " + in.getAbsolutePath() + ": " +
						e.getMessage());
				errors++;
				if(this.errorLinesAllowed != -1 && errors >
						this.errorLinesAllowed){
					log.warning("Too many errors occurred while processing " +
							"the file " + in.getAbsolutePath() + ". Please " +
							"see the preceeding warnings in the log.");
					keepProcessing = false;
				}
			}catch(IOException e){
				log.warning("An error occurred while reading from line " +
						lineNum + " in file " + in.getAbsolutePath() + ": " +
						e.getMessage());
				keepProcessing = false;
			}
			lineNum++;
		}
		
		// Close the reader and writer
		this.closeReaderWriter(reader, writer);
		
	}
	
	/**
	 * Parse a line from an input file and extract the values for presented
	 * details (e.g. date-time).
	 * 
	 * @param line					The input line, presented as a String.
	 * @return						True if the line was properly parsed,
	 * 								otherwise false.
	 * @throws ProcessingException	If a required detail is not defined, or if
	 * 								the line cannot be properly parsed using
	 * 								the pattern provided during configuration.
	 */
	protected void parseInputLine(String line) throws ProcessingException{
		
		//log.fine("Parsing log line: " + line);
		
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
						log.warning("Error with line remaining: " + lineRemaining);
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
	
	// Iterate over the substrings in the output pattern, creating the
	// output version using currently stored input values
	// TODO: Replace this with a proper javadoc
	protected String formatOutputLine() throws ProcessingException{
		
		String outputLine = null;
		for(String segment: this.segmentedOutput){
			
			//log.finest("Now formatting output for segment: " + segment);
			
			String value = null;
			
			// Date-time log detail
			if(segment.matches("<\\w+-dt>")){
				
				// Get the log detail and value
				String detailName =
						segment.substring(1, segment.length() - 4);
				DateTimeLogDetail detail = (DateTimeLogDetail)
						this.outputDetailMap.get(detailName);
				DateTimeLogDetail inputDetail = (DateTimeLogDetail)
						this.inputDetailMap.get(detailName);
				value = detail.getDate(inputDetail);
				
				//log.finest("Adding date-time log detail value: " + value);
				
				// Validate segment requirement
				if(value == null){
					if(detail.isRequired()){
						throw new ProcessingException("The log detail " +
								detailName + " required in the log " +
								"reformat output pattern has no value");
					}
					value = "-";
				}
				
			}
			
			// String log detail
			else if(segment.matches("<\\w+>")){	
				
				// Get the log detail and value
				String detailName =
						segment.substring(1, segment.length() - 1);
				StringLogDetail detail = (StringLogDetail)
						this.outputDetailMap.get(detailName);
				StringLogDetail inputDetail = (StringLogDetail)
						this.inputDetailMap.get(detailName);
				value = detail.getValue(inputDetail);
				
				//log.finest("Adding string log detail value: " + value);
				
				// Validate segment requirement
				if(value == null){
					if(detail.isRequired()){
						throw new ProcessingException("The log detail " +
								detailName + " required in the log " +
								"reformat output pattern has no value");
					}
					value = "-";
				}
				
			}
			
			// Literal String
			else{
				
				value = segment;
				
			}
			
			// Add the value of the segment to the line geing generated
			if(outputLine == null){
				outputLine = value;
			}else{
				outputLine = outputLine + value;
			}
			
			//log.finest("Output line is now: " + outputLine);
			
		}
		
		return outputLine;
		
	}
	
	/**
	 * Set the maps that store value of details back to null so that values for
	 * previous lines aren't carried forward.
	 */
	protected void resetDetailMaps() {
		
		for(String key: this.inputDetailMap.keySet()){
			this.inputDetailMap.get(key).reset();
		}
		
	}
	
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
			//log.finest("Log reformatter will ignore header line: " + line);
			return true;
		}
		
		// Parse the input line, extracting log detail values
		this.parseInputLine(line);		
		
		// Reformat the line using extracted values from input
		String reformattedLine = this.formatOutputLine();
		
		// Write the reformatted line to the output file
		writer.println(reformattedLine);
		
		// Reset the detail values so that they don't carry over to the
		// next line
		this.resetDetailMaps();
		
		return true;
		
	}
	
	/**
	 * Parse the given pattern, adding the proper elements to the provided
	 * segment list and detail Map.
	 * 
	 * @param pattern				The pattern to parse
	 * @param segmentList			A {@link List} of Strings created from the
	 * 								pattern that will hold the detail names and
	 * 								literal Strings that go between them
	 * @param detailMap				A {@link Map} that will be populated with
	 * 								{@link LogDetails} created from the pattern
	 * @param minSections			The maximum number of sections expected in
	 * 								a substring in the pattern
	 * @param maxSections			The minimum number of sections expected in
	 * 								a substring in the pattern
	 * @throws ProcessingException	If the pattern contains any mismatched
	 * 								brackets or a substring specifying a log
	 * 								detail is invalid
	 */
	private void parsePattern(String pattern, List<String> segmentList,
			Map<String, LogDetail> detailMap, int minSections,
			int maxSections) throws ProcessingException{
		
		// Basic validation of bracket matching	
		if(Utility.countSubstringInstances(pattern, "<") !=
				Utility.countSubstringInstances(pattern, ">")){
			throw new ProcessingException("The log reformat pattern " +
					"contains mismatched brackets demarking log details: " +
					pattern);
		}
		if(!pattern.contains("<")){
			throw new ProcessingException("The log reformat pattern " +
					"contains no brackets (< >) marking substrings to " +
					"capture as log details");
		}
		
		// Step through the pattern, parsing substrings noted by brackets
		String unparsedPattern = pattern;
		while(unparsedPattern.contains("<")){
			
			// Find the indices for the start and end of the next substring
			int i = unparsedPattern.indexOf("<");
			int j = unparsedPattern.indexOf(">");
			if(j == -1 || i > j){
				throw new ProcessingException("The log reformat " +
						"pattern contains mismatched brackets demarking " +
						"log details to capture: " + pattern);
			}
			
			// Capture any literal String that precedes the substring
			// specifying the log detail
			if(i > 0){
				String literalString = unparsedPattern.substring(0, i);
				segmentList.add(literalString);
				//log.finest("Encountered literal string " + literalString);
			}
			
			// Grab the substring specifying the log detail
			String substring = unparsedPattern.substring(i + 1, j);
			//log.finest("Encountered substring " + substring);
			
			// Remove the substring from the remaining pattern
			if(j + 1 == unparsedPattern.length()){
				unparsedPattern = "";
			}else{
				unparsedPattern = unparsedPattern.substring(j + 1);
			}
			
			// Validate the substring
			String[] substringComponents = substring.split(";");
			if(substringComponents.length < minSections || 
					substringComponents.length > maxSections){
				throw new ProcessingException("The log reformat " +
						"pattern conatins a log detail with an invalid " +
						"number of sections (" + substringComponents.length +
						"): " + substring);
			}
			if(substringComponents.length !=
					Utility.countSubstringInstances(substring, ";") + 1){
				throw new ProcessingException("The log reformat pattern " +
						"contains an empty section specifying a log detail: " +
						substring);
			}
			for(String section: substringComponents){
				//log.finest("Substring section " + section);
				if(section.trim().isEmpty()){
					throw new ProcessingException("The log reformat pattern " +
							"contains an empty section specifying a log " +
							"detail: " + substring);
				}
			}
			
			// Create a LogDetail object from the substring and add it to the
			// LogDetail Map
			String name = substringComponents[0];
			String detailPattern = null;
			String flags = null;
			if(minSections == 2){
				detailPattern = substringComponents[1];
				if(substringComponents.length == 3){
					flags = substringComponents[2];
				}
			}else{
				if(substringComponents.length == 2){
					flags = substringComponents[1];
				}
			}
			LogDetail detail = getLogDetail(name, detailPattern, flags);
			//log.finest("Found log detail: " + detail.toString());
			detailMap.put(detail.getName(), detail);
			
			// Add the name of the detail to the segment list
			String detailSegment = detail.getName();
			if(detail.getType().equals("datetime")){
				detailSegment = detailSegment + "-dt";
			}
			segmentList.add("<" + detailSegment + ">");
			
		}
		if(unparsedPattern.contains(">")){
			throw new ProcessingException("The log reformat pattern " +
					"contains mismatched brackets demarking log details to " +
					"capture: " + pattern);
		}
			
		// Capture any of the remaining pattern as a literal String
		if(!unparsedPattern.isEmpty()){
			segmentList.add(unparsedPattern);
		}
		
	}
	
	/**
	 * Create {@link LogDetail} object from the String array.  The array should
	 * contain the various sections of the substring specified in the pattern.
	 * 
	 * @param detail				The sections of the substring as a String
	 * 								array
	 * @return						The {@link LogDetail} object as specified
	 * 								by the String array
	 * @throws ProcessingException	If any specified date-time log details are
	 * 								not specified properly
	 */
	private LogDetail getLogDetail(String name, String pattern, String flags)
			throws ProcessingException{
		
		if(flags == null){
			return new StringLogDetail(name, pattern, false);
		}else{
			boolean required = false;
			String inputFormat = null;
			String defaultValue = null;
			for(String flag: flags.split(",")){
				if(flag.equals("required")){
					required = true;
				}else if(flag.startsWith("datetime")){
					if(flag.indexOf("=") == -1){
						throw new ProcessingException("The date-time log " +
								"detail is not properly formatted: " + flag);
					}
					inputFormat =
							flag.substring(flag.indexOf("=") + 1).trim();
					if(inputFormat.isEmpty()){
						throw new ProcessingException("The date-time format " +
								"was not given: " + flag);
					}
				}else if(flag.startsWith("default")){
					if(flag.indexOf("=") == -1){
						throw new ProcessingException("The log detail " +
								"default is not properly formatted: " + flag);
					}
					defaultValue =
						flag.substring(flag.indexOf("=") + 1).trim();
					if(defaultValue.isEmpty()){
						throw new ProcessingException("The log detail " +
								"default was not given: " + flag);
					}
				}else{
					log.warning("Unknown flag " + flag + " in log " +
							"reformat pattern");
				}
			}
			if(inputFormat == null){
				if(defaultValue == null){
					return new StringLogDetail(name, pattern, required);
				}
				return new StringLogDetail(name, pattern, required,
						defaultValue);
			}else{
				if(defaultValue == null){
					return new DateTimeLogDetail(name, pattern, required,
							inputFormat);
				}
				try{
					return new DateTimeLogDetail(name, pattern, required,
							inputFormat, defaultValue);
				}catch(ParseException e){
					throw new ProcessingException("An error occurred while " +
							"creating a date-time log detail: " +
							e.getMessage());
				}
			}
		}
		
	}
	
	private void closeReaderWriter(BufferedReader reader, PrintWriter writer){
		
		if(reader != null){
			try{
				reader.close();
			}catch(IOException e){
				log.warning("An error occurred while closing the reader to " +
						"the input file: " + e.getMessage());
			}
		}
		
		if(writer != null){
			writer.close();
		}
			
	}
	
	private abstract class LogDetail{
		
		protected String name;
		protected String pattern;
		protected boolean required = false;
		
		public LogDetail(String name, String pattern, boolean required){
			
			this.pattern = pattern;
			this.required = required;
			this.name = name;
			
		}
		
		public String getName(){
			return this.name;
		}
		
		public String getPattern(){
			return this.pattern;
		}
		
		abstract public String getType();
		
		public boolean isRequired(){
			return this.required;
		}
		
		public String toString(){
			return null;
		}
		
		abstract public void reset();
		
	}
	
	private class StringLogDetail extends LogDetail{

		protected String value;
		protected String defaultValue;
		
		public StringLogDetail(String name, String pattern, boolean required,
				String defaultValue){
			
			super(name, pattern, required);
			this.value = null;
			this.defaultValue = defaultValue;
			
		}
		
		public StringLogDetail(String name, String pattern, boolean required){
			
			super(name, pattern, required);
			this.value = null;
			this.defaultValue = null;
			
		}
		
		public String getValue(){
			if(this.value == null){
				return this.defaultValue;
			}
			return this.value;
		}
		
		public String getValue(StringLogDetail inputDetail){
			if(inputDetail == null || inputDetail.getValue() == null){
				return this.defaultValue;
			}
			return inputDetail.getValue();
		}
		
		public String getType(){
			return "string";
		}
		
		public void setValue(String value){
			this.value = value;
		}
		
		public void reset(){
			this.value = null;
		}
		
		public String toString(){
			return "String log detail: name: " + this.name + " value: " +
					this.value + " pattern: " + this.pattern + " required: " +
					this.required + " default: " + this.defaultValue;
		}
		
	}
	
	private class DateTimeLogDetail extends LogDetail{
		
		protected Date date;
		protected Date defaultDate;
		protected SimpleDateFormat dateFormat;
		
		public DateTimeLogDetail(String name, String pattern, boolean required,
				String inputFormat, String defaultDate) throws ParseException{
			
			super(name, pattern, required);
			this.date = null;
			this.dateFormat = new SimpleDateFormat(inputFormat);
			this.defaultDate = this.dateFormat.parse(defaultDate);
			
		}
		
		public DateTimeLogDetail(String name, String pattern, boolean required,
				String inputFormat){
			
			super(name, pattern, required);
			this.date = null;
			this.dateFormat = new SimpleDateFormat(inputFormat);
			this.defaultDate = null;
			
		}
		
		public String getFormat(){
			return this.dateFormat.toPattern();
		}
		
		public String getDate(String outputFormat){
			if(this.date == null){
				if(this.defaultDate != null){
					return new SimpleDateFormat(outputFormat).format(
							this.defaultDate);
				}
				return null;
			}
			return new SimpleDateFormat(outputFormat).format(this.date);
		}
		
		public String getDate(DateTimeLogDetail inputDetail){
			if(inputDetail == null){
				if(this.defaultDate != null){
					return this.dateFormat.format(this.defaultDate);
				}
			}
			String inputValue = inputDetail.getDate(this.dateFormat.toPattern());
			if(inputValue == null){
				if(this.defaultDate != null){
					return this.dateFormat.format(this.defaultDate);
				}
			}
			return inputValue;
		}
		
		public String getType(){
			return "datetime";
		}
		
		public void setDate(String value) throws ParseException{
			this.date = this.dateFormat.parse(value);
		}
		
		public void reset(){
			this.date = null;
		}
		
		public String toString(){
			return "Date-time log detail: name: " + this.name + " date: " +
					this.getDate(this.getFormat()) + " format: " +
					this.getFormat() + " pattern: " + this.pattern + 
					" required: " + this.required + " default: " +
					this.printDefault();
		}
		
		private String printDefault(){
			if(this.defaultDate == null){
				return null;
			}
			return this.dateFormat.format(this.defaultDate);
		}
		
	}
	
}
