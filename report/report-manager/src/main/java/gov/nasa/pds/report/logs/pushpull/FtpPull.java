package gov.nasa.pds.report.logs.pushpull;

import gov.nasa.pds.report.util.Debugger;
import gov.nasa.pds.report.util.Utility;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/*
 * We could track progress while downloading files with this class by passing
 * org.apache.commons.io.output.CountingOutputStream to FTPClient.retrieveFile()
 * or by creating a CopyStreamListener implementation and passing it to the 
 * FTPClient using setCopyStreamListener()--see FTPClientExample.java.
 */
public class FtpPull implements PDSPull {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
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
		
        // TODO: Consider holding on to the host name so long as we're
        // connected, especially if client.getPassiveHost() doesn't work
        
		return true;
		
	}
	
	public final void pull(String path, String destination)
			throws PushPullException{
		
		// If connection has not been made, alert the user
		if(!client.isConnected()){
			// TODO: Throw an exception or log an error
			return;
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
		localFileList.addAll(Utility.getLocalFileList(destination));
		
		for (String filename : array) {

			String remoteFilePath = dirPath + "/" + filename;
			String localFilePath = destination + "/" + filename;
			
			if (!localFileList.contains(filename)) {
				
				this.log.info("Transferring: " + remoteFilePath + " to " + 
						destination + "\n");
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
					this.client.retrieveFile(remoteFilePath, output);
				}catch(IOException e){
					log.severe("An error occurred while pulling file " + 
							remoteFilePath + " from " + 
							this.client.getPassiveHost() + ": " + 
							e.getMessage());
				}
				
			} else {
				this.log.info(dirPath + "/" + filename
						+ " already exists in " + destination + "\n");
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
					this.client.getPassiveHost() + ":" + path + 
					" because the FTP client is not connected");
			return fileList;
		}
		
		try{
			for(FTPFile file: this.client.listFiles(path)){
				fileList.add(file.getName());
			}
		}catch(IOException e){
			throw new PushPullException("An error occurred while obtaining " +
					"the file list at " + path + ": " + e.getMessage());
		}
		
		Debugger.debug("Found " + fileList.size() + " files at " + path);
		
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
					"details for " + this.client.getPassiveHost() + ":" + 
					path + ": " + e.getMessage();
			log.severe(errorMsg);
			throw new PushPullException(errorMsg);
		}
		if(files.length == 0){
			String errorMsg = "Nothing exists at path " + path + " on host " + 
					this.client.getPassiveHost();
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
