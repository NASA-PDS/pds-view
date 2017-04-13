package gov.nasa.pds.report.logs.pushpull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.util.DateLogFilter;
import gov.nasa.pds.report.util.FileUtil;
import gov.nasa.pds.report.util.Utility;

/*
 * We could track progress while downloading files with this class by passing
 * org.apache.commons.io.output.CountingOutputStream to FTPClient.retrieveFile()
 * or by creating a CopyStreamListener implementation and passing it to the 
 * FTPClient using setCopyStreamListener()--see FTPClientExample.java.
 */
public class FtpPull implements PDSPull {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private String host;
	
	protected FTPClient client;
	
	public FtpPull(){
		
		this.client = new FTPClient();
		this.client.setListHiddenFiles(false);
		
	}
	
	public boolean connect(final String hostname, final String username,
			final String password, final boolean encrypted)
			throws PushPullException {
		
		// Connect to the server and login
		try{
			this.client.connect(hostname);
			if(!FTPReply.isPositiveCompletion(client.getReplyCode())){
				this.log.warning("An error occurred while connecting to " +
						hostname + ": " + client.getReplyString());
				this.disconnect();
				return false;
			}
			if(!this.client.login(username, password)){
				this.log.warning("An error occurred while logging in to " + 
						hostname + ": " + this.client.getReplyString());
				this.client.logout();
				return false;
			}
		}catch(IOException e){
			throw new PushPullException("An error occurred while " +
					"connecting to " + hostname + ": " + e.getMessage());
		}
		
		// Configure the FTP client
		this.client.enterLocalPassiveMode();
		//this.client.setUseEPSVwithIPv4(false);
		FTPClientConfig config = new FTPClientConfig();
        config.setLenientFutureDates(true);
        this.client.configure(config);
        
        this.host = hostname;
        
		return true;
		
	}
	
	public final void pull(String path, String destination)
			throws PushPullException{
		
		// If connection has not been made, alert the user
		if(!client.isConnected()){
			throw new PushPullException("Cannot pull files from " + this.host +
					" since we are not currently connected to the host");
		}
		
		// The path to the directory containing the file specified in the
		// node path (this will just be the node path if the path is to a
		// directory)
		String dirPath = path;
		if(!isPathToDirectory(path)){
			dirPath = Utility.getDirPath(path);
		}
		
		List<String> array = getFileList(path);
		
		List<String> localFileList = new ArrayList<String>();
		try{
			localFileList.addAll(FileUtil.getFilenameList(destination));
		}catch(ReportManagerException e){
			throw new PushPullException("An error occured while determining "
					+ "the already pulled files at " + destination + ": " +
					e.getMessage());
		}
		
		for (String filename : array) {

			String remoteFilePath = dirPath + "/" + filename;
			String localFilePath = destination + "/" + filename;
			
			if (!localFileList.contains(filename)) {
				
				this.log.info("Transferring: " + remoteFilePath + " to " + 
						destination);
				OutputStream output;
				try{
					output = new FileOutputStream(localFilePath);
				}catch(FileNotFoundException e){
					log.severe("An error occurred while attempting to " +
							"establish an output stream to " + localFilePath + 
							": " + e.getMessage());
					continue;
				}
				try{
					
					// Get the log
					this.client.retrieveFile(remoteFilePath, output);
					
					// Validate the log file
					File logFile = new File(localFilePath);
					if(!logFile.exists()){
						log.warning("The log at " + localFilePath +
								" did not download");
					}else if(logFile.length() == 0){
						log.warning("The log at " + localFilePath +
								" is empty and will be deleted");
						FileUtils.forceDelete(logFile);
					}
				}catch(IOException e){
					log.severe("An error occurred while pulling file " + 
							remoteFilePath + " from " + this.host + ": " + 
							e.getMessage());
				}
				
			} else {
				this.log.info(dirPath + "/" + filename
						+ " already exists in " + destination);
			}
			
		}
		
		return;
		
	}
	
	public void disconnect() throws PushPullException{
		
		if(this.client.isConnected()){
			try{
				this.client.logout();
				this.client.disconnect();
			}catch(IOException e){
				throw new PushPullException("An error occurred while " +
						"disconnecting: " + e.getMessage());
			}
		}
		
	}
	
	private List<String> getFileList(String path) throws PushPullException{
		
		List<String> fileList = new Vector<String>();
		
		if(!client.isConnected()){
			log.warning("Could not provide a list of files at " + 
					this.host + ":" + path + 
					" because the FTP client is not connected");
			return fileList;
		}
		
		FTPFile[] lsOut = new FTPFile[1];
		try{
			lsOut = this.client.listFiles(path);
		}catch(IOException e){
			throw new PushPullException("An error occurred while obtaining " +
					"the file list at " + path + ": " + e.getMessage());
		}
		try{
			for(FTPFile file: lsOut){
				String filename = file.getName();
				try{
					if(DateLogFilter.match(filename)){
						fileList.add(filename);
					}
				}catch(ParseException e){
					log.warning("An error occurred while parsing a " +
								"filename for date filtering: " + 
								e.getMessage());
				}
			}
		}catch(ReportManagerException e){
			throw new PushPullException("The Date Log Filter was improperly " +
					"initialized before pulling files: " + e.getMessage());
		}
		
		log.info("Found " + fileList.size() + " out of " + lsOut.length + 
		" logs that match filters");
		
		return fileList;
		
	}
	
	private boolean isPathToDirectory(String path) throws PushPullException{
		
		if(path.contains("*")){
			return false;
		}
		FTPFile[] files;
		try{
			files = this.client.listFiles(path);
		}catch(IOException e){
			String errorMsg = "An error occurred while gathering file " +
					"details for " + this.host + ":" + path + ": " + 
					e.getMessage();
			log.severe(errorMsg);
			throw new PushPullException(errorMsg);
		}
		if(files.length == 0){
			String errorMsg = "Nothing exists at path " + path + " on host " + 
					this.host;
			log.severe(errorMsg);
			throw new PushPullException(errorMsg);
		}else if(files.length == 1){
			if(files[0].isDirectory()){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
		
	}
	
}
