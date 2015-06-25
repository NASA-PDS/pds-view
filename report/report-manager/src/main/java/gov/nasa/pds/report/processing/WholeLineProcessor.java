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

import gov.nasa.pds.report.constants.Constants;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to perform basic reformatting on log files.  Unlike the
 * {@link LogReformatProcessor} which looks for individual values while
 * progressively parsing lines in the input log, this processor will parse an
 * entire log line at a time.  Such an approach is better for simple
 * modifications (such inserting quotes around particular log details), while
 * the LogReformatProcessor is better at reformatting log lines by seeking
 * particular details within each line to manipulate how those details are
 * displayed.
 * 
 * Using the previous example (inserting quotes), let's assume we are presented
 * with the following line as input:
 * 
 * 2015-03-01 09:50:21 W3SVC1234849874 10.10.1.73 GET /data/messenger/MSGRMDS_1001/DATA/2011_159/EW0216024357G.IMG - 80 - 106.188.24.48 Mozilla/5.0 (Windows NT 6.1; Trident/7.0; rv:11.0) like Gecko - 200 0 0 270336 -
 * 
 * This line is rather difficult to interpret, since one of it's values
 * contains spaces--thanks for that, GEO!  We want to insert some quotes around
 * the log detail containing spaces, making it look like this:
 * 
 * 2015-03-01 09:50:21 W3SVC1234849874 10.10.1.73 GET /data/messenger/MSGRMDS_1001/DATA/2011_159/EW0216024357G.IMG - 80 - 106.188.24.48 "Mozilla/5.0 (Windows NT 6.1; Trident/7.0; rv:11.0) like Gecko" "-" 200 0 0 270336 -
 * 
 * To make this modification, we specify our input line specification:
 * 
 * <pre;\\d{4}-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d \\S+ [0-9.-]+ [A-Z-]+ \\S+ \\S+ [0-9-]+ \\S+ [0-9.-]+> <client_browser;\\S+> <referrer;\\S+> <post;[0-9-]+ [0-9-]+ [0-9-]+ [0-9-]+ [0-9-]+>
 * 
 * The processor uses this pattern to generate a regular expression pattern
 * that matches the entire line, captures all details within brackets, and
 * generates output using an output line specification:
 * 
 * <pre> "<client_browser>" "<referrer>" <post>
 * 
 * It is important to note that unlike the DetailByDetailProcessor, this
 * processor does not allow flags to be specified in either input or output
 * line specifications.  Indeed all log details noted in the input line
 * specification are required.
 * 
 * @author resneck
 *
 */
public class WholeLineProcessor extends LogReformatProcessor{

	private static Logger log = Logger.getLogger(
			WholeLineProcessor.class.getName());
	
	// The name of the directory where output is placed
	public static final String OUTPUT_DIR_NAME = "wholeline_reformat";
	
	// The String used to create an RE pattern to parse input log lines
	private Pattern inputRegexPattern = null;
	
	// A list of detail names corresponding to the order of the groups that
	// represent them in the RE pattern
	private List<String> regexGroupList = null;
	
	@Override
	public void process(File in, File out) throws ProcessingException {
		
		// Check that the processor has been configured
		if(this.inputRegexPattern == null || this.regexGroupList == null ||
				this.regexGroupList.isEmpty()){
			throw new ProcessingException("The whole line log reformat " +
					"processor has not been configured previously");
		}
		
		super.process(in, out);
		
	}

	@Override
	public String getDirName() {
		
		return OUTPUT_DIR_NAME;
	
	}

	@Override
	public void configure(Properties props) throws ProcessingException {
		
		log.info("Configuring whole line log reformatting processor");
		
		configure(props, Constants.NODE_LINE_REFORMAT_INPUT,
				Constants.NODE_LINE_REFORMAT_OUTPUT, true);
		
		this.regexGroupList = new Vector<String>();
		
		// Initialize the input RE pattern
		String inputRegexPatternStr = null;
		for(String segment: super.segmentedInput){
			String reSegment = null;
			if(segment.matches("<.*>")){
				String detailName = segment.substring(1, segment.length() - 1);
				reSegment = "(" + 
						this.inputDetailMap.get(detailName).getPattern() + ")";
				this.regexGroupList.add(detailName);
			}else{
				reSegment = segment;
			}
			if(inputRegexPatternStr == null){
				inputRegexPatternStr = reSegment;
			}else{
				inputRegexPatternStr = inputRegexPatternStr + reSegment;
			}
		}
		this.inputRegexPattern = Pattern.compile(inputRegexPatternStr);
		
		log.info("Whole line reformatter RE pattern: " + inputRegexPatternStr);	// TODO: Delete me!
		
	}

	@Override
	protected void parseInputLine(String line) throws ProcessingException {
		
		// Match RE pattern
		Matcher matcher = this.inputRegexPattern.matcher(line);
		if(!matcher.matches()){
			throw new ProcessingException("The whole line processor has not " +
					"been configured to handle the input line: " + line);
		}
		
		// Assign values to log details
		for(int i = 0; i < this.regexGroupList.size(); i++){
			StringLogDetail detail = (StringLogDetail)this.inputDetailMap.get(
					this.regexGroupList.get(i));
			detail.setValue(matcher.group(i + 1));
		}
		
	}
	
}