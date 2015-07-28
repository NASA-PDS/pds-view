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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.Utility;

/**
 * Sub-classes of this class are used to reformat text-based log files so that
 * they can be parsed by Sawmill, using a profile created from our template
 * profile.  This reformatting uses regular expression patterns to determine
 * how to break down input and restructure it for output.  Therefore, the class
 * must be configured before the processing can begin.  This is done using
 * Strings called line specifications.  Depending upon the function of the
 * sub-class, the user may have to provide a line specification for the input
 * and/or output.
 * 
 * The specifications use the less-than and greater-than symbols to label the
 * log details that are captured and rearranged.  The specification for each
 * detail is split into sections by one or more semicolons.  The first section
 * is the name of the log detail.  In log details in the input pattern, the
 * second section specifies the RE pattern used to capture that detail.  Some
 * sub-classes also allow an additional optional section to supply extra
 * information by setting flags to label the log detail.  For example, these
 * flags might label the log detail as a date-time or require a valid value to
 * be present.
 * 
 * Each sub-class will interpret these line specifications differently, so
 * please examine the javadocs for those classes.
 * 
 *
 *
 * Example 1: <client-ip;[0-9.]+;required>
 * 
 * This log detail specification would be part of an input line specification,
 * as it specifies the RE pattern [0-9.]+ to capture the log detail.  That
 * detail will be given the name "client-ip" and will have the captured value
 * assigned to it.  There is also a flag specifying that the log detail is
 * required; if the sub-class fails to find a valid value while attempting to
 * capture the log detail an exception will be thrown.
 * 
 * Example 2: <date-time;\d{4}-\d\d-\d\d \d\d:\d\d:\d\d;required,datetime=yyyy-MM-dd HH:mm:ss>
 * 
 * This log detail specification would also be part of an input line
 * specification.  You can see that it has the name "date-time" and uses a long
 * RE pattern to capture the log detail.  Like the previous example, the
 * sub-class is required to capture a valid value for this log detail or an
 * exception will be thrown.  This log detail also uses the datetime flag.  The
 * characters following the equals sign specify format used to interpret the
 * log detail as a date-time.  You can read more about the datetime flag and
 * other flags below.
 * 
 * Example 3: <user-id>
 * 
 * This log detail specification would be part of an output line specification,
 * as it does not contain an RE pattern.  The log detail would be given the
 * name "user-id" and no flags are given for this log detail.
 * 
 * 
 * 
 * Log detail specifications in both the input and output line specifications
 * can optionally be given flags, separated by commas.
 * 
 * required: A substring with this flag must have a valid value, otherwise the
 * input line is discarded.  This will happen for an input substring if the
 * given value is "-" and for an output substring if the name is not present as
 * a key in the map created from input.
 * 
 * datetime: A substring with this flag designates a date-time using the format
 * following an equals sign (e.g. "datetime=yyyy-MM-dd HH:mm:ss").  The format
 * is used to interpret the log detail by a {@link SimpleDateFormat}.
 * 
 * default: A substring with this flag will default to the value following an
 * equals sign, as shown in the example above.
 * 
 * emptyvalue: A substring with this flag will treat the value following an
 * equals sign as a null value (e.g. <username;\\S+;emptyvalue=*>).
 * 
 * @author resneck
 * 
 */
public abstract class LogReformatProcessor implements Processor{
	
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
	 * For example, the input line specification
	 * 
	 * "Employee Name:<name;\w+;required> Age:<age;\d> Date of Hire:<doh;\d{4}-\d\d-\d\d;datetime>"
	 * 
	 * would have the following segment list after parsing:
	 * 
	 * "Employee Name:", "<name>", " Age:", "<age>", " Date of Hire:", "<doh-dt>"
	 */
	protected List<String> segmentedInput;
	protected List<String> segmentedOutput;
	
	// The Maps that we use to store the log details as objects
	protected Map<String, LogDetail> inputDetailMap;
	protected Map<String, LogDetail> outputDetailMap;
	
	// The number of error-causing lines that we allow before giving up on the
	// file.  A value of 0 allows no errors.  A value of -1 allows any number
	// of errors, so the input file will be processed completely, though the
	// output file will only contain the output from lines that did not cause
	// errors.
	private int errorLinesAllowed;
	private static String DEFAULT_ERRORS_ALLOWED = "0";
	
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
		if(!this.verifyConfiguration()){
			throw new ProcessingException("The log reformat processor has " +
			"not been configured previously");
		}
		
		log.info("Now reformatting logs in " + in.getAbsolutePath());
		
		// Get a list of files in the input directory
		List<File> files = null;
		try{
			files = Utility.getFileList(in);
		}catch(ReportManagerException e){
			throw new ProcessingException("An error occurred while finding " +
					"the logs at " + in.getAbsolutePath());
		}
		
		// Reformat each of the files
		this.processFileList(files, out);
		
	}

	/**
	 * Read in the log files in the given directory, reformat them as per 
	 * configured, and place the output in the given directory.
	 * 
	 * @param in					A {@link List} of {@link File}s that will
	 * 								be reformatted.
	 * @param out					The directory where output will be placed,
	 * 								represented as a {@link File}.
	 * @throws ProcessingException	If any parameters are null or invalid, if
	 * 								the processor hasn't been configured, or if
	 * 								an error occurs during processing.
	 */
	public void process(List<File> in, File out) throws ProcessingException{
		
		if(in == null || in.isEmpty()){
			throw new ProcessingException("No input file list provided to " +
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
		if(!this.verifyConfiguration()){
			throw new ProcessingException("The log reformat processor has " +
			"not been configured previously");
		}
		
		this.processFileList(in, out);
		
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.verifyConfiguration()
	 */
	public boolean verifyConfiguration(){
		
		return (this.inputDetailMap != null &&
				!this.inputDetailMap.isEmpty() &&
				this.outputDetailMap != null &&
				!this.outputDetailMap.isEmpty());
		
	}
	
	/**
	 * This is a convenience method to assist sub-classes in configuring
	 * their log detail maps and means of parsing input and structuring output.
	 * As such, this method will most frequently be invoked from within the
	 * configure() methods of sub-classes.
	 * 
	 * @param props			The {@link Properties} containing the configuration
	 * 						for the log source.
	 * @param inputSpecKey	The key in the properties storing the input line
	 * 						specification.
	 * @param outputSpecKey	The key in the properties storing the output line
	 * 						specification.
	 * @param allowFlags	A boolean specifying whether flags are allowed in
	 * 						the input and output line specifications.
	 * @see gov.nasa.pds.report.processing.Processor.configure()
	 */
	protected void configure(Properties props, String inputSpecKey,
			String outputSpecKey, boolean allowFlags)
			throws ProcessingException{
		
		log.info("Configuring log reformatting processor");
		
		// Extract the raw input and output line specifications from the given
		// Properties
		String inputLineSpec = null;
		String outputLineSpec = null;
		try{
			inputLineSpec = Utility.getNodePropsString(props, inputSpecKey,
					true);
			outputLineSpec = Utility.getNodePropsString(props, outputSpecKey,
					true);
		}catch(ReportManagerException e){
			throw new ProcessingException("Input and output line " +
					"specifications were not provided for log reformatting");
		}
		
		// Parse the input specification
		this.parseInputSpec(inputLineSpec, allowFlags);
		
		// Parse the output specification
		this.parseOutputSpec(outputLineSpec, allowFlags);
		
		// Validate the line specifications
		this.validateLineSpecs(allowFlags);
		
		// Determine how many errors we will tolerate per file
		this.determineErrorTolerance();
		
	}
	
	/**
	 * Process the provided {@link List} of {@link File}s.
	 * 
	 * @param files					A list of File objects pointing to log
	 * 								files to be processed.
	 * @param out					A {@link File} object pointing to the
	 * 								directory where output will be placed.
	 * @throws ProcessingException	If an error occurs.
	 */
	protected void processFileList(List<File> files, File out)
			throws ProcessingException{
		
		for(int i = 0; i < files.size(); i++){
			File file = files.get(i);
			try{
				log.info("Reformatting log file " + file.getAbsolutePath() +
						" (" + Integer.toString(i + 1) + "/" +
						files.size() + ")");
				this.processFile(file, out);
			}catch(ProcessingException e){
				log.warning("An error occurred while reformatting log file " +
						file.getAbsolutePath() + ": " + e.getMessage());
			}
		}
		
	}
	
	/**
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
		// happens until we reach the EOF or the number of errors exceeds the
		// specified threshold.
		boolean keepProcessing = true;
		boolean keepFile = true;
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
					keepFile = false;
				}
			}catch(IOException e){
				log.warning("An I/O error occurred while reading from line " +
						lineNum + " in file " + in.getAbsolutePath() + ": " +
						e.getMessage());
				keepProcessing = false;
				keepFile = false;
			}
			lineNum++;
		}
		
		// Close the reader and writer
		this.closeReaderWriter(reader, writer);
		
		// Delete the created file if too many errors (or an I/O error)
		// occurred
		if(!keepFile){
			try{
				FileUtils.forceDelete(out);
			}catch(IOException e){
				log.warning("An error occurred while cleaning up a " +
						"potentially erroneous output file " +
						out.getAbsolutePath() + ": " + e.getMessage());
			}
		}
		
	}
	
	/**
	 * Following the output pattern, create the reformatted version of the log
	 * line, using the log detail values from the input log line.
	 */
	protected String formatOutputLine() throws ProcessingException{
		
		String outputLine = null;
		for(String segment: this.segmentedOutput){
			
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
	 * Parse the line specification used for input.  This creates a map of
	 * log details to store log detail input values and divides the input log
	 * line into discrete segments containing log details and literal Strings.
	 * 
	 * @param inputLineSpec	The input line specification as a String.
	 * @param allowFlags	A boolean indicating whether flags are allowed in
	 * 						the input line specification.
	 */
	protected void parseInputSpec(String inputLineSpec, boolean allowFlags)
			throws ProcessingException{
		
		this.inputDetailMap = new HashMap<String, LogDetail>();
		this.segmentedInput = new Vector<String>();
		int maxSubstringSections = 2;
		if(allowFlags){
			maxSubstringSections = 3;
		}
		this.parsePattern(inputLineSpec, this.segmentedInput,
				this.inputDetailMap, 2, maxSubstringSections);
		
	}
	
	/**
	 * Parse the line specification used for output.  This creates a map of
	 * log details to store output log details and divides the output log
	 * line into discrete segments containing log details and literal Strings.
	 * 
	 * @param outputLineSpec	The output line specification as a String.
	 * @param allowFlags		A boolean indicating whether flags are allowed
	 * 							in the output line specification.
	 */
	protected void parseOutputSpec(String outputLineSpec, boolean allowFlags)
			throws ProcessingException{
		
		this.outputDetailMap = new HashMap<String, LogDetail>();
		this.segmentedOutput = new Vector<String>();
		int maxSubstringSections = 1;
		if(allowFlags){
			maxSubstringSections = 2;
		}
		this.parsePattern(outputLineSpec, this.segmentedOutput,
				this.outputDetailMap, 1, maxSubstringSections);	
		
	}
	
	/**
	 * Validate the line specifications.  This should be called from the
	 * sub-class' implementation of the configure() method.
	 * 
	 * @param allowFlags			A boolean indicating whether flags are
	 * 								allowed in the line specifications for the
	 * 								sub-class.
	 * @throws ProcessingException	If the line specifications are invalid.
	 * @see gov.nasa.pds.report.processing.Processor.configure()
	 */
	protected void validateLineSpecs(boolean allowFlags)
			throws ProcessingException{
		
		if(!allowFlags){
			return;
		}
		
		for(String outputKey: this.outputDetailMap.keySet()){
			
			// Verify that required output details are included in the input
			// specification
			if( this.outputDetailMap.get(outputKey).isRequired() &&
					!this.inputDetailMap.containsKey(outputKey)){
				throw new ProcessingException("The log reformat input line" +
						"specification does not specify a log detail" +
						"required by the output specification: " + outputKey);
			}
			
			// Verify that input and output line specifications treat log
			// details as the same type
			if(this.inputDetailMap.containsKey(outputKey)){
				String inputType = this.inputDetailMap.get(outputKey).getType();
				String outputType = this.outputDetailMap.get(outputKey).getType();
				if(!inputType.equals(outputType)){
					throw new ProcessingException("The log reformat " +
							"patterns contain the detail " + outputKey +
							" which is specified as different types in " +
							"the input and output patterns");
				}
			}
			
		}
		
	}
	
	/**
	 * Using the system properties during execution, determine how many lines
	 * in an input log can cause errors before the logs is discarded.
	 */
	protected void determineErrorTolerance(){
		
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
	protected abstract void parseInputLine(String line) throws ProcessingException;
	
	/**
	 * Parse the given pattern, adding the proper elements to the provided
	 * segment list and detail Map.
	 * 
	 * @param pattern				The pattern to parse.
	 * @param segmentList			A {@link List} of Strings created from the
	 * 								pattern that will hold the detail names and
	 * 								literal Strings that go between them.
	 * @param detailMap				A {@link Map} that will be populated with
	 * 								{@link LogDetails} created from the pattern.
	 * @param minSections			The minimum number of sections expected in
	 * 								a log detail specification in the pattern.
	 * @param maxSections			The maximum number of sections expected in
	 * 								a log detail specification in the pattern.
	 * @throws ProcessingException	If the pattern contains any mismatched
	 * 								brackets or an invalid log detail
	 * 								specification.
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
			}
			
			// Grab the substring specifying the log detail
			String substring = unparsedPattern.substring(i + 1, j);
			
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
	 * Create {@link LogDetail} object using the pieces of the log detail
	 * specification provided in the input or output line specification.
	 * 
	 * @param name					The name of the log detail.
	 * @param pattern				The RE pattern used to acquire the log
	 * 								detail value from the input log line.
	 * @param flags					Any flags specifying the log detail.
	 * @return						The {@link LogDetail} object.
	 * @throws ProcessingException	If any invalid flags are specified.
	 */
	private LogDetail getLogDetail(String name, String pattern, String flags)
			throws ProcessingException{
		
		if(flags == null){
			return new StringLogDetail(name, pattern, false);
		}else{
			boolean required = false;
			String dateTimeFormat = null;
			String defaultValue = null;
			String emptyValue = null;
			
			// Iterate over each flag specified for the log detail
			for(String flag: flags.split(",")){
				if(flag.equals("required")){
					required = true;
				}else if(flag.startsWith("datetime")){
					if(flag.indexOf("=") == -1){
						throw new ProcessingException("The date-time log " +
								"detail is not properly formatted: " + flag);
					}
					dateTimeFormat =
							flag.substring(flag.indexOf("=") + 1).trim();
					if(dateTimeFormat.isEmpty()){
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
				}else if(flag.startsWith("emptyvalue")){
					if(flag.indexOf("=") == -1){
						throw new ProcessingException("The log detail " +
								"empty value is not properly formatted: " +
								flag);
					}
					emptyValue = flag.substring(flag.indexOf("=") + 1).trim();
					if(emptyValue.isEmpty()){
						throw new ProcessingException("The log detail " +
								"empty value is not properly formatted: " +
								flag);
					}
				}else{
					log.warning("Unknown flag " + flag + " in log " +
							"reformat pattern");
				}
			}
			
			// Create the proper LogDetail using the specified flags
			if(dateTimeFormat == null){
				StringLogDetail sdt = null; 
				if(defaultValue == null){
					sdt = new StringLogDetail(name, pattern, required);
				}else{
					sdt = new StringLogDetail(name, pattern, required,
							defaultValue);
				}
				if(emptyValue != null){
					sdt.setEmptyValue(emptyValue);
				}
				return sdt;
			}else{
				if(defaultValue == null){
					return new DateTimeLogDetail(name, pattern, required,
							dateTimeFormat);
				}
				try{
					return new DateTimeLogDetail(name, pattern, required,
							dateTimeFormat, defaultValue);
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
	
	private String debugValueDump(){
		
		String output = "";
		for(LogDetail d: inputDetailMap.values()){
			output = output + d.getName() + ":";
			if(d.getType().equals("string")){
				output = output + ((StringLogDetail)d).getValue() + " ";
			}else{
				output = output + ((DateTimeLogDetail)d).getDate(
						"yyyy-MM-dd HH:mm:ss") + " ";
			}
		}
		return output;
		
	}
	
}
