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

import java.io.File;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to reformat logs created by an NcFTP server.  As such,
 * it uses an input line specification determined by the version of NcFTP that
 * created the logs.  The version is specified using the ncftp_version property.
 * The class also requires an output line specification that uses the usual
 * format parsed by the {@link LogReformatProcessor}, allowing flags.
 * 
 * The output line specification can use the following log details:
 * 
 * date_time (must have the date-time flag)
 * child_process
 * operation
 * requested_resource
 * bytes_transfered
 * bytes_received
 * time_taken
 * transfer_rate
 * username
 * email
 * client_ip
 * suffix
 * transfer_status
 * transfer_protocol
 * transfer_notes
 * transfer_start_time
 * session_id
 * file_size
 * offset
 * wildcard_pattern
 * recursion
 * file_mode
 * symlink_target
 * new_filename
 * 
 * You can find more information about the NcFTP log format and the details
 * that NcFTP logs contain at http://www.ncftp.com/ncftpd/doc/xferlog.html.
 * 
 * @author resneck
 *
 */
public class NcftpReformatProcessor extends LogReformatProcessor{

	// The name of the directory where output is placed
	public static final String OUTPUT_DIR_NAME = "ncftp_to_combined_reformat";
	
	// Some fields in NcFTP log lines are reserved, based upon the operation
	// that the line represents.  We use this value to recognize that we can
	// skip such fields, as they are left empty or contain no log detail of
	// value.
	private static final String RESERVED_DETAIL_NAME = "reserved";
	
	// The String used to indicate that no version of of NcFTP was given,
	// meaning that we should use the default behavior.
	private static final String DEFAULT_VERSION = "default";
	
	private static Logger log = Logger.getLogger(
			NcftpReformatProcessor.class.getName());
	
	private Map<String, List<String>> layoutMap;
	
	public NcftpReformatProcessor(){
		
		this.layoutMap = new HashMap<String, List<String>>();
		
		// Populate input log details.  We don't need to specify patterns to
		// acquire the log detail from input log lines, because we know where
		// they are in the input log line based upon the operation that the
		// line represents.  We include a few extra log details that are used
		// in later versions of NcFTP, just in case.
		this.inputDetailMap = new HashMap<String, LogDetail>();
		this.inputDetailMap.put("date_time", new DateTimeLogDetail(
				"date-time", null, true, "yyyy-MM-dd HH:mm:ss"));
		this.inputDetailMap.put("child_process", new StringLogDetail(
				"child-process", null, true));
		this.inputDetailMap.put("operation", new StringLogDetail(
				"operation", null, true));
		this.inputDetailMap.put("http_method", new StringLogDetail(
				"operation", null, false));
		this.inputDetailMap.put("requested_resource", new StringLogDetail(
				"requested_resource", null, false));
		this.inputDetailMap.put("bytes_transfered", new StringLogDetail(
				"bytes_transfered", null, false));
		this.inputDetailMap.put("bytes_received", new StringLogDetail(
				"bytes_received", null, false));
		this.inputDetailMap.put("time_taken", new StringLogDetail(
				"time_taken", null, false));
		this.inputDetailMap.put("transfer_rate", new StringLogDetail(
				"transfer_rate", null, false));
		this.inputDetailMap.put("username", new StringLogDetail(
				"username", null, false));
		this.inputDetailMap.put("email", new StringLogDetail(
				"email", null, false));
		this.inputDetailMap.put("client_ip", new StringLogDetail(
				"client_ip", null, true));
		this.inputDetailMap.put("suffix", new StringLogDetail(
				"suffix", null, false));
		this.inputDetailMap.put("transfer_status", new StringLogDetail(
				"transfer_status", null, false));
		this.inputDetailMap.put("transfer_protocol", new StringLogDetail(
				"transfer_protocol", null, false));
		this.inputDetailMap.put("transfer_notes", new StringLogDetail(
				"transfer_notes", null, false));
		this.inputDetailMap.put("transfer_start_time", new StringLogDetail(
				"transfer_start_time", null, false));
		this.inputDetailMap.put("session_id", new StringLogDetail(
				"session_id", null, false));
		this.inputDetailMap.put("file_size", new StringLogDetail(
				"file_size", null, false));
		this.inputDetailMap.put("offset", new StringLogDetail(
				"offset", null, false));
		this.inputDetailMap.put("wildcard_pattern", new StringLogDetail(
				"wildcard_pattern", null, false));
		this.inputDetailMap.put("recursion", new StringLogDetail(
				"recursion", null, false));
		this.inputDetailMap.put("file_mode", new StringLogDetail(
				"file_mode", null, false));
		this.inputDetailMap.put("symlink_target", new StringLogDetail(
				"symlink_target", null, false));
		this.inputDetailMap.put("new_filename", new StringLogDetail(
				"new_filename", null, false));
		
	}
	
	@Override
	/**
	 * Reformat all of the NcFTP logs in the given directory and place them in
	 * the given output directory.
	 * 
	 * @param in					The directory containing the logs to be
	 * 								reformatted, given as a {@link File}.
	 * @param out					The directory where output is placed, given
	 * 								as a {@link File}.
	 * @throws ProcessingException	If the Processor has not been properly
	 * 								configured or if an error occurs during
	 * 								processing.
	 */
	public void process(File in, File out) throws ProcessingException {
		
		// Check that the processor has been configured
		if(!this.verifyConfiguration()){
			throw new ProcessingException("The NcFTP log reformatter has not " +
					"been previously configured");
		}
		
		super.process(in, out);
		
	}

	/**
	 * Reformat all of the NcFTP logs in the given list and place them in
	 * the given output directory.
	 * 
	 * @param in					A {@link List} of {@link File}s,
	 * 								representing the logs to be reformatted.
	 * @param out					The directory where output is placed, given
	 * 								as a {@link File}.
	 * @throws ProcessingException	If the Processor has not been properly
	 * 								configured or if an error occurs during
	 * 								processing.
	 */
	public void process(List<File> in, File out) throws ProcessingException{
		
		// Check that the processor has been configured
		if(!this.verifyConfiguration()){
			throw new ProcessingException("The NcFTP log reformatter has not " +
					"been previously configured");
		}
		
		super.process(in, out);
		
	}
	
	@Override
	/**
	 * @see gov.nasa.pds.report.processing.Processor.getDirName()
	 */
	public String getDirName(){
		
		return OUTPUT_DIR_NAME;
		
	}

	@Override
	/**
	 * @see gov.nasa.pds.report.processing.Processor.configure()
	 */
	public void configure(Properties props) throws ProcessingException {
		
		log.info("Configuring NcFTP log reformatter");
		
		// Extract the raw  output line specification from the given Properties
		String outputLineSpec = null;
		try{
			outputLineSpec = Utility.getNodePropsString(props,
					Constants.NODE_NCFTP_REFORMAT_OUTPUT, true);
		}catch(ReportManagerException e){
			throw new ProcessingException("Output line specification was " +
					"not provided for NcFTP log reformatting");
		}
		
		// Parse the output line specification
		this.parseOutputSpec(outputLineSpec, true);
		
		// Validate the output line specification
		this.validateLineSpecs(true);
		
		// Determine how many errors we will tolerate per file
		this.determineErrorTolerance();
		
		// Setup the layout lists as determined by the version of NcFTP that
		// generated the logs.
		String version = null;
		try{
			version = Utility.getNodePropsString(props,
					Constants.NODE_NCFTP_VERSION, false);
			if(version == null){
				log.info("The NcFTP version was not provided, so the " +
						"reformatter will use the default log line format.");
				version = DEFAULT_VERSION;
			}
		}catch(ReportManagerException e){
			throw new ProcessingException("An error occurred while " +
					"determining the the version of NcFTP used: " +
					e.getMessage());
		}
		List<String> list = null;
		if(version.equals("2.8.7")){
			list = Arrays.asList("requested_resource", "bytes_transfered", "time_taken", "transfer_rate", "username", "email", "client_ip", "suffix", "transfer_status", "transfer_protocol", "transfer_notes", "transfer_start_time", "session_id", "file_size", "offset");
			this.layoutMap.put("R", list);
			list = Arrays.asList("requested_resource", "bytes_received", "time_taken", "transfer_rate", "username", "email", "client_ip", "suffix", "transfer_status", "transfer_protocol", "transfer_notes", "transfer_start_time", "session_id", "file_size", "offset");
			this.layoutMap.put("S", list);
			list = Arrays.asList("requested_resource", "transfer_status", "wildcard_pattern", "recursion", "username", "email", "client_ip", "session_id");
			this.layoutMap.put("T", list);
			list = Arrays.asList("requested_resource", RESERVED_DETAIL_NAME, RESERVED_DETAIL_NAME, RESERVED_DETAIL_NAME, "username", "email", "client_ip", "session_id");
			this.layoutMap.put("D", list);
			list = Arrays.asList("requested_resource", RESERVED_DETAIL_NAME, RESERVED_DETAIL_NAME, RESERVED_DETAIL_NAME, "username", "email", "client_ip", "session_id");
			this.layoutMap.put("M", list);
			list = Arrays.asList("requested_resource", "file_mode", RESERVED_DETAIL_NAME, RESERVED_DETAIL_NAME, "username", "email", "client_ip", "session_id");
			this.layoutMap.put("C", list);
			list = Arrays.asList("requested_resource", RESERVED_DETAIL_NAME, "symlink_target", RESERVED_DETAIL_NAME, "username", "email", "client_ip", "session_id");
			this.layoutMap.put("L", list);
			list = Arrays.asList("requested_resource", RESERVED_DETAIL_NAME, "new_filename", RESERVED_DETAIL_NAME, "username", "email", "client_ip", "session_id");
			this.layoutMap.put("N", list);
		}else if(version.equals(DEFAULT_VERSION)){
			list = Arrays.asList("requested_resource", "bytes_transfered", "time_taken", "transfer_rate", "username", "email", "client_ip", "suffix", "transfer_status");
			this.layoutMap.put("R", list);
			list = Arrays.asList("requested_resource", "bytes_received", "time_taken", "transfer_rate", "username", "email", "client_ip", "suffix", "transfer_status");
			this.layoutMap.put("S", list);
			list = Arrays.asList("requested_resource", RESERVED_DETAIL_NAME, RESERVED_DETAIL_NAME, RESERVED_DETAIL_NAME, "username", "email", "client_ip");
			this.layoutMap.put("D", list);
			list = Arrays.asList("requested_resource", RESERVED_DETAIL_NAME, RESERVED_DETAIL_NAME, RESERVED_DETAIL_NAME, "username", "email", "client_ip");
			this.layoutMap.put("M", list);
			list = Arrays.asList("requested_resource", "file_mode", RESERVED_DETAIL_NAME, RESERVED_DETAIL_NAME, "username", "email", "client_ip");
			this.layoutMap.put("C", list);
			list = Arrays.asList("requested_resource", RESERVED_DETAIL_NAME, "new_filename", RESERVED_DETAIL_NAME, "username", "email", "client_ip");
			this.layoutMap.put("N", list);
		}else{
			throw new ProcessingException("The NcFTP log reformatter does " +
					"not recognize the given NcFTP version: " + version);
		}
		
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.LogReformatProcessor.getOutputFileName()
	 */
	public String getOutputFileName(String inputFileName){
		
		return inputFileName;
		
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.verifyConfiguration()
	 */
	public boolean verifyConfiguration(){
		
		return !this.layoutMap.isEmpty();
		
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.LogReformatProcessor.parseInputLine()
	 */
	protected void parseInputLine(String line) throws ProcessingException{
		
		// Get the date-time, child process, and operation
		Pattern lineStartPattern = Pattern.compile(
				"((\\d{4}-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d) (#u\\d+)\\s+\\| (R|S|T|D|M|C|L|N),).+");
		Matcher matcher = lineStartPattern.matcher(line);
		if(!matcher.matches()){
			throw new ProcessingException("The date-time and operation could " +
					"not be extracted from the input log line: " + line);
		}
		try{
			((DateTimeLogDetail)this.inputDetailMap.get("date_time")).
					setDate(matcher.group(2));
		}catch(ParseException e){
			throw new ProcessingException("An error occurred while parsing " +
					"the date-time from input log line: " + line);
		}
		((StringLogDetail)this.inputDetailMap.get("child_process")).
				setValue(matcher.group(3));
		String operation = matcher.group(4);
		((StringLogDetail)this.inputDetailMap.get("operation")).
				setValue(operation);
		
		// Fetch the line layout that corresponds to the operation that
		// the line represents
		List<String> layoutList = this.layoutMap.get(operation);
		if(layoutList == null){
			throw new ProcessingException("The NcFTP log reformatter does " +
					"not recognize the operation: " + operation);
		}
		
		// Set the HTTP method using the NcFTP operation from the line
		StringLogDetail httpDetail =
				(StringLogDetail)this.inputDetailMap.get("http_method");
		if(operation.equals("R")){
			httpDetail.setValue("GET");
		}else if(operation.equals("S")){
			httpDetail.setValue("PUT");
		}else if(operation.equals("D")){
			httpDetail.setValue("DELETE");
		}
		
		// Split the remaining log line using commas
		String lineRemaining = line.replace(matcher.group(1), "");
		String[] inputDetails = lineRemaining.split(",");
		
		// Verify that the input line contains the proper list of log details
		if(inputDetails.length != layoutList.size()){
			throw new ProcessingException("The input log line does not " +
					"contain the expected number of log details (" + 
					Integer.toString(layoutList.size() + 3) + "): " + line);
		}
		
		// Iterate over the details specified in the line layout,
		// assigning their values from the log line.  Since NcFTP log lines end
		// in a comma, we don't want to use the detail at the last index.
		for(int i = 0; i < inputDetails.length; i++){
			String detailName = layoutList.get(i);
			if(!detailName.equals(RESERVED_DETAIL_NAME)){
				StringLogDetail detail =
						(StringLogDetail)this.inputDetailMap.get(detailName);
				String inputValue = inputDetails[i];
				if(detail.isRequired() && inputValue.isEmpty()){
					throw new ProcessingException("The required log detail " +
							detailName + " was not found in input log line: " +
							line);
				}
				detail.setValue(inputValue);
			}
		}
		
	}
	
}