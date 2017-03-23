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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.ReadWriter;
import gov.nasa.pds.report.util.Utility;

/**
 * This is a custom {@link Processor} that removes the version information from
 * file names in EN FTP logs.
 * 
 * @author resneck
 */
public class ENFtpProcessor extends DetailByDetailProcessor{
	
	// These arrays are used to specify which paths of resources requested in
	// log entries are accepted for each PDS version (pds3/pds4).  Most array
	// elements are split by an underscore, the first portion being the expected
	// beginning of the requested resource path and the second being the name
	// of the requested resource.  For example, the array element
	// "/pub/toplevel/tools/bin/BulkDownloader_bulk-downloader" would specify
	// that every input log entry with a requested resource that began with
	// "/pub/toplevel/tools/bin/BulkDownloader" would result in an output log
	// entry with "bulk-downloader" as the requested resource.  Some of these
	// array elements will lack an underscore.  We use this when we want to
	// capture all requested resources within a given directory.  In such a
	// case, the input requested resource will require additional processing
	// to remove any version tags (i.e. "win", "linux", r42, etc.).
	private static final String[] pds3Resources = {
			"/pub/toplevel/tools/bin/BulkDownloader_bulk-downloader",
			"/pub/toplevel/tools/bin/bulk-downloader_bulk-downloader",
			"/pub/toplevel/tools/bin/citool_citool",
			"/pub/toplevel/tools/bin/datadictionary_datadictionary",
			"/pub/toplevel/tools/bin/krtool_krtool",
			"/pub/toplevel/tools/bin/ltdtool_ltdtool",
			"/pub/toplevel/tools/bin/nasaview_nasaview",
			"/pub/toplevel/tools/bin/pds2jpeg_pds2jpeg",
			"/pub/toplevel/tools/bin/pds-web-ps_pds-web-ps",
			"/pub/toplevel/tools/bin/product-tools_product-tools",
			"/pub/toplevel/tools/bin/tools_tools",
			"/pub/toplevel/tools/bin/vtool_vtool"};
	private static final String[] pds4Resources = {
			"/pub/toplevel/2010/preparation/generate_generate",
			"/pub/toplevel/2010/ingest/harvest_harvest",
			"/pub/toplevel/2010/registry/",
			"/pub/toplevel/2010/search/",
			"/pub/toplevel/2010/preparation/pds4-tools_pds4-tools",
			"/pub/toplevel/2010/preparation/transform_transform",
			"/pub/toplevel/2010/transport/",
			"/pub/toplevel/2010/preparation/validate_validate"};
	
	private static Logger log =
			Logger.getLogger(ENFtpProcessor.class.getName());
	
	private Map<String, String> pathMap;
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.configure()
	 */
	public void configure(Properties props) throws ProcessingException{

		// Perform basic configuration
		super.configure(props);
		
		// Tolerate unlimited errors since these logs are in good shape
		this.errorLinesAllowed = -1;
		
		// Setup the mapping for resources requested in input log entries
		this.pathMap = new HashMap<String, String>();
		String id = null;
		try{
			id = Utility.getNodePropsString(props,
					Constants.NODE_ID_KEY, true);
		}catch(ReportManagerException e){
			throw new ProcessingException("Failed to obtain profile ID to " +
					"determine pds3//pds4 processing for EN FTP logs");
		}
		String[] resourceArray = {};
		if(id.contains("pds3")){
			resourceArray = pds3Resources;
		}else if(id.contains("pds4")){
			resourceArray = pds4Resources;
		}else{
			throw new ProcessingException("EN FTP profile ID did not " +
					"contain 'pds3' or 'pds4'.  Cannot determine mapping " +
					"for resources requested");
		}
		for(int i = 0; i < resourceArray.length; i++){
			String resourceSpec = resourceArray[i];
			if(resourceSpec.contains("_")){
				this.pathMap.put(
						resourceSpec.substring(0, resourceSpec.indexOf("_")),
						resourceSpec.substring(resourceSpec.indexOf("_") + 1));
			}else{
				this.pathMap.put(resourceSpec, null);
			}
		}
		
	}
	
	/**
	 * @see gov.nasa.pds.report.processing.Processor.verifyConfiguration()
	 */
	public boolean verifyConfiguration(){
		
		if(this.inputDetailMap == null ||
				this.inputDetailMap.isEmpty() ||
				this.outputDetailMap == null ||
				this.outputDetailMap.isEmpty()){
			return false;
		}
		
		if(this.pathMap != null && !this.pathMap.isEmpty()){
			return true;
		}
		return false;
		
	}
	
	/**
	 * Use the provided reader to read in a line, reformat it, then write the
	 * new version of the line using the provided writer.
	 * 
	 * @param rw					A {@link ReadWriter} that is already
	 * 								initialized to read and write from the input
	 * 								and output files.
	 * @return						True if a line was read from the input
	 * 								file, otherwise false (indicating EOF).
	 * @throws ProcessingException	If an error occurs.
	 */
	protected boolean processLine(ReadWriter rw) throws ProcessingException{
		
		// Read the line from the file using the reader
		String line = null;
		try{
			line = rw.readLine();
		}catch(ReportManagerException e){
			throw new ProcessingException("An error occurred while reading " +
					"from the input file: " + e.getMessage());
		}
		
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
		
		// Discard any lines that don't represent tool downloads that we
		// care about
		String path = ((StringLogDetail)
				this.inputDetailMap.get("requested_resource")).getValue();
		if(!path.startsWith("/pub/toplevel/") ||
				!(path.endsWith(".zip") || path.endsWith(".tar.gz"))){
			this.resetDetailMaps();
			return true;
		}
		
		// Map the requested resource to a path prefix in order to determine
		// what tool was requested and truncate the requested file name if
		// needed
		boolean resourceLocated = false;
		Iterator<String> iter = this.pathMap.keySet().iterator();
		while(iter.hasNext() && !resourceLocated){
			String prefix = iter.next();
			if(path.startsWith(prefix)){
				String resourceRequested = this.pathMap.get(prefix);
				if(resourceRequested == null){
					resourceRequested = this.truncateFileName(path);
				}
				((StringLogDetail)this.inputDetailMap.get("requested_resource")).
						setValue(resourceRequested);
				resourceLocated = true;
			}
		}
		if(!resourceLocated){
			// The resource didn't match any of the path prefixes associated
			// with this version of PDS.  It might be from the other version of
			// PDS, something missing from the path prefix mapping, or a request
			// that we don't care about.
			this.resetDetailMaps();
			log.finer("EN FTP path not recognized: " + path);
			return true;
		}
		
		// Reformat the line using extracted values from input
		String reformattedLine = this.formatOutputLine();
		
		// Write the reformatted line to the output file
		if(reformattedLine != null && !reformattedLine.isEmpty()){
			rw.writeLine(reformattedLine);
		}
		
		// Reset the detail values so that they don't carry over to the
		// next line
		this.resetDetailMaps();
		
		return true;
		
	}
	
	// Strip the file path down to the file name and remove the version
	// information
	private String truncateFileName(String path) throws ProcessingException{
		
		// Strip the path down to the file name without version info
		if(path.contains("/")){
			path = path.substring(path.lastIndexOf("/") + 1);
		}
		
		if(path.matches("[a-zA-Z._-]+?[_-]\\d+\\.\\d+\\.\\d+[_-].+")){
			path = this.getFileName("([a-zA-Z._-]+?)[_-]\\d+\\.\\d+\\.\\d+[_-].+", path);
		}else if(path.matches("[a-zA-Z._-]+?[_-][r0-9]+\\..+")){
			path = this.getFileName("([a-zA-Z._-]+?)[_-][r0-9]+\\..+", path);
		}/*else if(path.matches("[a-zA-Z]+?\\.zip")){
			path = path.substring(0, path.length() - 4);
		}else if(path.matches("[a-zA-Z]+?\\.tar.gz")){
			path = path.substring(0, path.length() - 7);
		}else{
			throw new ProcessingException("The file name " + path +
					" did not match any truncating pattern");
		}*/
		
		return path;
		
	}
	
	// Use a provided regex pattern to extract the downloaded product from
	// the file name
	private String getFileName(String regex, String path)
			throws ProcessingException{
		Matcher match = Pattern.compile(regex).matcher(path);
		if(!match.matches()){
			// This really should never happen, as this method is only called
			// from truncateFileName(), a private method.  Just make sure that
			// all of the provided regex patterns are solid.
			throw new ProcessingException("The downloaded product could not " +
					"be found in the provided file name " + path + 
					" because it did not match the given regex pattern");
		}
		return match.group(1);
	}
	
}