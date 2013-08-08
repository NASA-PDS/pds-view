// Copyright 2009-2013, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id: Utility.java 10558 2012-05-25 22:20:10Z mcayanan $
package gov.nasa.pds.search.core.util;

import gov.nasa.pds.search.core.cli.options.InvalidOptionException;
import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Utility class
 * 
 * @author jpadams
 * @author mcayanan
 *
 */
public class Utility {
	/** Logger object. */
	private static Logger log = Logger.getLogger(Utility.class.getName());
	
    /**
     * Get the current date time.
     *
     * @return A date time.
     */
    public static String getDateTime() {
        SimpleDateFormat df = new SimpleDateFormat(
        "EEE, MMM dd yyyy 'at' hh:mm:ss a");
        Date date = Calendar.getInstance().getTime();
        return df.format(date);
    }
    
	/**
	 * Method to convert the file path to absolute, if relative, and check if
	 * file exists.
	 * 
	 * @param fileType
	 *            File type denoted to allow for usable error msgs
	 * @param filePath
	 *            Current path given through the command-line
	 * @param isDir
	 *            Designates if filePath specified is a directory. False means
	 *            filePath is a file.
	 * @return	the absolute path from the input file path
	 * @throws InvalidOptionException	thrown if directory does not exist
	 */
	public static String getAbsolutePath(final String fileType,
			final String filePath, final boolean isDir)
			throws InvalidOptionException {
		String finalPath = "";
		File tFile = new File(filePath);
		if (!tFile.isAbsolute()) {
			finalPath = System.getProperty("user.dir") + "/" + filePath;
		} else {
			finalPath = filePath;
		}

		tFile = new File(finalPath);
		if ((isDir && !tFile.isDirectory()) || (!isDir && !tFile.isFile())) {
			throw new InvalidOptionException(fileType + " does not exist: "
					+ filePath);
		}

		return finalPath;
	}
	
	public static boolean urlExists(final String url) {
		try {
			URL u = new URL(url); 
		    HttpURLConnection huc =  (HttpURLConnection)  u.openConnection(); 
		    huc.setRequestMethod("GET"); 
		    huc.connect(); 
		    if (huc.getResponseCode() != 404) {
		    	return true;
		    } else {
		    	return false;
		    }
		} catch (Exception e) {
		      log.log(new ToolsLogRecord(ToolsLevel.SEVERE, e.getMessage(),
		              url + " cannot be found."));
		      return false;
		}
	}
}
