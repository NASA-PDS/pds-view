package gov.nasa.pds;

import gov.nasa.pds.web.ui.utils.HTTPUtils;

import java.io.File;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * A generic logging service.
 * 
 * NOTE: not currently used
 * 
 * @author jagander
 */
public class LogService {

	public static final Logger log = Logger.getLogger(LogService.class);

	public static boolean initd = false;

	/**
	 * Initialize logger with a predefined level, format, and location
	 */
	public static void initLog() {
		if (initd == false) {
			log.setLevel(Level.DEBUG);

			PatternLayout layout = new PatternLayout(
					"%-4r [%t] %-5p %c %x - %m%n"); //$NON-NLS-1$
			try {
				File tempDir = HTTPUtils.getTempDir();
				File outFile = new File(tempDir, "pdsWeb.log"); //$NON-NLS-1$
				outFile.createNewFile();
				FileAppender appender = new FileAppender(layout, outFile
						.getPath(), false);
				log.addAppender(appender);
			} catch (Exception e) {
				ConsoleAppender consoleAppender = new ConsoleAppender(layout);
				log.addAppender(consoleAppender);
			}
			initd = true;
		}
	}

	/**
	 * Get the logger and initialize if necessary.
	 * 
	 * @return the logger
	 */
	public static Logger getLogger() {
		initLog();
		return log;
	}
}
