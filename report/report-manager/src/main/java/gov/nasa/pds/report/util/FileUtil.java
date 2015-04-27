package gov.nasa.pds.report.util;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

public class FileUtil {
	
	private static Logger log = Logger.getLogger(FileUtil.class.getName());
	
	/**
	 * Create a directory tree with the given name directly under the root of
	 * the Report Service directory tree.
	 * 
	 * @param props						A {@link List} of Properties of created
	 * 									from profiles
	 * @param dirName					The name of the directory that will be
	 * 									placed at the root of the tree being
	 * 									created (i.e. staging, final, backup,
	 * 									or processing)
	 * @throws ReportManagerException	If any error occurs during the creation
	 * 									of the tree
	 */
	public static void createDirTree(List<Properties> props, String dirName)
			throws ReportManagerException{
		
		for(Properties p: props){
			
			String profileID = null;
			try{
				profileID = Utility.getNodePropsString(p,
						Constants.NODE_ID_KEY, true);
				String nodeName = Utility.getNodePropsString(p,
						Constants.NODE_NODE_KEY, true);
				getDir(dirName, nodeName, profileID);
			}catch(ReportManagerException e){
				throw new ReportManagerException("An error occurred while " +
						"creating the " + dirName + " directory tree: " +
						e.getMessage());
			}
			
		}

	}
	
	/**
	 * Get a {@link File} object pointing to the directory under the 
	 * root directory and create all directories and sub-directories as needed.
	 * The path to the directory will be 
	 * DIR_ROOT/[dirname]/[node name]/[profile ID] 
	 * 
	 * @param dirName					The name of the directory (i.e. staging,
	 * 									final, backup, or processing)
	 * @param nodeName					The name of the node from which the
	 * 									logs come
	 * @param profileID					The ID of the profile specifying
	 * 									where/how to obtain the logs
	 * @return							A {@link File} object pointing to the
	 * 									new directory
	 * @throws ReportManagerException	If any of the parameters are missing
	 */
	public static File getDir(String dirName, String nodeName,
			String profileID) throws ReportManagerException{
		
		String dirRoot = System.getProperty(Constants.DIR_ROOT_PROP);
		
		if (nodeName == null || nodeName.equals("") ||
				profileID == null || profileID.equals("") ||
				dirName == null || dirName.equals("")){
			throw new ReportManagerException(
					"The specified staging directory path " + dirRoot +
					File.separator + dirName + 
					File.separator + nodeName + 
					File.separator + profileID + 
					" is missing components");
		}
		
		File file = new File(dirRoot + File.separator +
				dirName + File.separator +
				nodeName + File.separator +
				profileID);
		if(!file.exists()){
			if(!file.mkdirs()){
				throw new ReportManagerException("Failed to create the " +
						"directory at " + file.getAbsolutePath());
			}
		}
		return file;
		
	}
	
	/**
	 * Get a {@link File} object pointing to the directory in the tree where
	 * output for the process with the given name will be placed
	 * 
	 * @param nodeName					The name of the node from which the
	 * 									logs come
	 * @param profileID					The ID of the profile specifying
	 * 									where/how to obtain the logs
	 * @param processName				The name of the process
	 * @return							A {@link File} object pointing to the
	 * 									new directory
	 * @throws ReportManagerException	If any of the parameters are missing
	 */
	public static File getProcessingDir(String nodeName, String profileID,
			String processName) throws ReportManagerException{
		
		File superDir = null;
		try{
			superDir = getDir(Constants.PROCESSING_DIR, nodeName, profileID);
		}catch(ReportManagerException e){
			throw new ReportManagerException("An error occurred while " +
					"creating the super directory for processing directory " + 
					processName + ": " + e.getMessage());
		}
		
		if(processName == null || processName.equals("")){
			throw new ReportManagerException("Cannot create a processing " +
					"output directory with a null process name");
		}
		
		File file = new File(superDir, processName);
		if(!file.exists()){
			if(!file.mkdir()){
				throw new ReportManagerException("Failed to create the " +
						"processing directory at " + file.getAbsolutePath());
			}
		}
		
		return file;
		
	}
	
	/**
	 * Backup a directory in the given source directory structure to a
	 * location under the given destination directory structure.
	 * 
	 * @param p							The profile specifying the source
	 * 									location
	 * @param from						The source directory name
	 * @param to						The destination directory name
	 * @throws ReportManagerException	If the profile is incomplete or
	 * 									parameters are missing
	 */
	public static void backupDir(Properties p, String from, String to)
			throws ReportManagerException{
		
		String profileID = null;
		try{
			
			profileID = Utility.getNodePropsString(p,
					Constants.NODE_ID_KEY, true);
			String nodeName = Utility.getNodePropsString(p,
					Constants.NODE_NODE_KEY, true);
			
			File srcDir = getDir(from, nodeName, profileID);
			File destDir = getDir(to, nodeName, profileID);
			List<String> destFileList = Utility.getLocalFileList(
					destDir.getAbsolutePath());
			
			for(File srcFile: srcDir.listFiles()){
				if(srcFile.isDirectory()){
					continue;	// We don't recurse
				}
				String filename = srcFile.getName();
				if(!destFileList.contains(filename)){
					try{
						FileUtils.copyFileToDirectory(srcFile, destDir);
					}catch(IOException ex){
						log.warning("An error occurred while backing up " + 
								srcFile.getAbsolutePath() + " to " + 
								destDir.getAbsolutePath() + ": " +
								ex.getMessage());
					}
				}else{
					log.finer(filename + " will not be copied to the backup " +
							"directory since a copy is already there");
				}
			}
			
		}catch(ReportManagerException e){
			throw new ReportManagerException("An error occurred while " +
					"backing up logs from profile " + profileID + ": " +
					e.getMessage());
		}
		
	}
	
	/**
	 * Delete all files under the given directory
	 * 
	 * @param dir	The directory under which all files will be deleted
	 */
	public static void cleanupLogs(File dir){
		
		for(File f: dir.listFiles()){
			if(f.isDirectory()){
				cleanupLogs(f);
			}else{
				try{
					log.finest("Cleaning out log " + f.getName());
					FileUtils.forceDelete(f);
				}catch(IOException e){
					log.warning("An error occurred while cleaning up log " + 
							f.getName() + ": " + e.getMessage());
				}
			}
		}
		
	}
	
	/**
	 * Delete all files under the given directory that are past a given age
	 * 
	 * @param dir	The directory under which all old files will be deleted
	 * @param age	The maximum age (number of milliseconds passed the Epoch)
	 * 				of files that will be deleted
	 */
	public static void cleanupOldLogs(File dir, long age){
		
		for(File f: dir.listFiles()){
			if(f.isDirectory()){
				cleanupOldLogs(f, age);
			}else{
				if(f.lastModified() < age){
					try{
						log.fine("Cleaning out old log " + f.getName());
						FileUtils.forceDelete(f);
					}catch(IOException e){
						log.warning("An error occurred while cleaning up " +
								"old log " + f.getName() + ": " +
								e.getMessage());
					}
				}
			}
		}
		
	}

}
