package gov.nasa.pds.report.update.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Utility {
	
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

	public static String getFileDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date date = Calendar.getInstance().getTime();
		return df.format(date);
	}

	public final static List<String> getLocalFileList(String path) {
		File dir = new File(path);
		return Arrays.asList(dir.list());
	}
	
	public final static String getRSUpdateHome() {
		String home = "";
		String[] fullPath = System.getProperty("java.class.path").split("/");	// Gives full path where jar file is located
		for (int i=1; i<fullPath.length-2; i++) {	// We know jar is in RS_UPDATE_HOME/lib/rs-update-x.x.x.jar
			home += "/" + fullPath[i];
		}
		return home;
	}
}
