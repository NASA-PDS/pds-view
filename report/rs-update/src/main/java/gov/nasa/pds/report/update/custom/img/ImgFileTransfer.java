package gov.nasa.pds.report.update.custom.img;

import gov.nasa.pds.report.update.util.BasicUtil;
import gov.nasa.pds.report.update.util.SFTPConnect;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Performs special case transfer of logs from PDS machines.
 * 
 * Logs are rotated weekly, instead of daily, and only kept for 8 weeks total.
 * To handle this, the last three weeks are downloaded to allow for some overlap to
 * ensure we catch duplicate data.  We then compare the tail of the oldest
 * of the downloaded access logs (i.e. if we download the last 3 weeks, this is
 * access_log.3) with the logs already present in the log set's base directory.
 * If a log matches, we know the previously downloaded version is not needed, along
 * with any more recent logs.
 * 
 * For example, lets say we run this software on April 11, 2011 and April 18,2011.
 * On April 11, we will get the following files at <log_set_base_dir> :
 * 		/20110411/access_log.1
 * 		/20110411/access_log.2
 * 		/20110411/access_log.3
 * 
 * When we run it on April 18, we get:
 * 		/20110418/access_log.1
 * 		/20110418/access_log.2
 * 		/20110418/access_log.3
 * 
 * Then we loop through the files in the 20110411 directory, and find /20110418/access_log.3
 * matches /20110411/access_log.2 .  We will then remove /20110411/access_log.2 and 
 * /20110411/access_log.1 because they will be captured in the remaining /20110418 files.
 * 
 * @author jpadams
 *
 */
public class ImgFileTransfer extends SFTPConnect{

	private Logger log = Logger.getLogger(this.getClass().getName());

	private static final String OLDEST_LOG="access_log.3";
	
	public void getImgLogs(final String hostname, final String username, final String password, final String pathname, final String logDestPath) throws IOException {
		/* Use SFTPConnect to get the logs */
		getLogs(hostname, username, password, pathname, logDestPath);
		
		removeDuplicateLogs(logDestPath);
	}
	
	/**
	 * Loops through the new logs from the oldest, and compare to already existing logs
	 * Remove any duplicate/incomplete logs previously downloaded.
	 * 
	 * @param logDestPath
	 * @throws IOException
	 */
	private void removeDuplicateLogs(String logDestPath) throws IOException {		
		String[] pathArray = logDestPath.split("/");				// Splits the logDestPath into an array

		String basePath = getBasePath(pathArray);					// Method to remove the date directory from the path
		
		List<String> dirList = BasicUtil.getLocalFileList(basePath);
		
		String currDirName = pathArray[pathArray.length-1];			// The date directory truncated from the basePath
		
		File currLog = new File(logDestPath + "/" + OLDEST_LOG);	// Set the oldest log from the current transfer
		String currLogTail = getTail(currLog);						// Get the tail of the current transfer's oldest log
		
		int logNum;						// Will hold the access log number of previous log that matches currLog
		
		File subDir = null;
		/* Loop through the list of directories */
		for (String dir : dirList) {
			logNum = 0;
			if (!dir.equals(currDirName)) {
				this.log.info("DIR: "+dir);
				subDir = new File(basePath + "/" + dir);
				List<File> fileList = Arrays.asList(subDir.listFiles());
				
				/* Loops through the fileList and compare the currLogTail to previous log's tail */
				for (File file : fileList) {
					this.log.info("CurrLog: "+currLog.getAbsolutePath());
					this.log.info("FILE: "+file.getAbsolutePath());
					if (currLogTail.equals(getTail(file))) {
						this.log.info("MATCHING FILE: "+file.getAbsolutePath());
						logNum = Integer.parseInt(String.valueOf(file.getName().charAt(11)));
						break;
					}
				}
				
				/* Break out of loop if logNum has been found */
				if (logNum != 0) {
					removeFiles(logNum, subDir.getAbsolutePath());
				}
			}
		}		
	}
	
	/**
	 * Removes the duplicate access_log files
	 * @param logNum
	 * @param path
	 */
	private void removeFiles(int logNum, String path) {
		for (int i = logNum; i > 0; i--) {
			File tempFile = new File(path + "/access_log." + String.valueOf(i));
			if (tempFile.exists()) {
				this.log.info("Removing duplicate file: " + tempFile.getAbsolutePath());
				tempFile.delete();
			}
		}
	}
	
	/**
	 * Extracts the String base path of the log destination, removing the filename/wildcard
	 * 
	 * @param pathArray
	 * @return
	 */
	private String getBasePath(String[] pathArray) {
		String basePath = "";
		for (int i=0; i < pathArray.length-1; i++) {
			if (!pathArray[i].equals(""))
				basePath += "/" + pathArray[i];
		}
		return basePath;
	}
	
	/**
	 * Created a Process to run the UNIX tail function on a file.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private String getTail(File file) throws IOException {
		Runtime r = Runtime.getRuntime();
		Process p = r.exec("tail -50 " + file.getAbsolutePath());
		Scanner s = new Scanner(p.getInputStream());
		String text = "";
		while (s.hasNextLine()) {
		     text += s.nextLine();
		}
		return text;
	}
}
