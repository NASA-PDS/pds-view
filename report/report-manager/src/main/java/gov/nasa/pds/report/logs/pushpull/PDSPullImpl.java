package gov.nasa.pds.report.logs.pushpull;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.jasypt.util.text.StrongTextEncryptor;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.DateLogFilter;
import gov.nasa.pds.report.util.FileUtil;
import gov.nasa.pds.report.util.Utility;

/**
 * Utility that provides all SFTP connection functionality
 * 
 * @author jpadams
 * 
 */
public class PDSPullImpl implements PDSPull {

	private Logger log = Logger.getLogger(this.getClass().getName());

	protected Session session;
	protected ChannelSftp sftpChannel;
	protected String logDestBase;

	public PDSPullImpl() {
		this.session = null;
		this.sftpChannel = new ChannelSftp();
	}
	
	/**
	 * Connects to external machine using SFTP. If encrypted, same encryption
	 * as PBEEncryptor expected. 
	 * 
	 * @param hostname
	 * @param username
	 * @param password			password to external machine
	 * @param encrypted			tells whether or not the password is encrypted
	 * @return					whether or not the connection was successful
	 * @throws PushPullException 
	 * @throws JSchException
	 */
	public boolean connect(final String hostname, final String username,
			final String password, final boolean encrypted)
			throws PushPullException {

		try {
			JSch jsch = new JSch();
			this.session = jsch.getSession(username, hostname, 22);
			this.session.setConfig("StrictHostKeyChecking", "no"); // Ignore HostKeyChecking
			
			if (encrypted) {
				this.session.setPassword(decrypt(password)); // set the password for authentication
			} else {
				this.session.setPassword(password); // set the password for authentication
			}
			this.session.connect();
			
			// Getting the channel using sftp
			ChannelSftp channel = (ChannelSftp)this.session.openChannel("sftp"); 
			
			channel.connect();
			this.sftpChannel = (ChannelSftp) channel;
	
			log.info("Connection to " + hostname + ": " +
					channel.isConnected());
			return channel.isConnected();
		} catch (JSchException e) {
			 throw new PushPullException("JschException: " + e.getMessage());
		}
	}

	private String decrypt(String password) {
		StrongTextEncryptor encryptor = new StrongTextEncryptor();
		encryptor.setPassword(Constants.CRYPT_PASSWORD);
		return encryptor.decrypt(password);
	}

	/**
	 * Disconnect SFTP and the session
	 */
	public void disconnect() {
		if (this.sftpChannel.isConnected()) { // Exits the channel
			this.sftpChannel.exit();
		}

		if (this.session.isConnected()) { // Disconnect the session
			this.session.disconnect();
		}
	}

	/**
	 * Retrieves the file listing from a remote machine
	 * 
	 * @param path
	 * @return
	 * @throws SftpException
	 */
	@SuppressWarnings("rawtypes")
	private final List<String> getFileList(String path)
			throws PushPullException {
		
		try {
			
			String[] dirList;
			String filename;
			
			Vector lsOut = this.sftpChannel.ls(path);
	
			List<String> matches = new ArrayList<String>();
			for (Object obj : lsOut) {
				dirList = obj.toString().split(" ");
				filename = dirList[dirList.length - 1];
				try{
					if (!filename.equals(".") && !filename.equals("..") &&
							DateLogFilter.match(filename)) {
						matches.add(filename);
					}
				}catch(ParseException e){
					log.warning("An error occurred while parsing a " +
							"filename for date filtering: " + e.getMessage());
				}
			}
			
			log.info("Found " + matches.size() + " out of " + lsOut.size() + 
					" logs that match filters");

			return matches;
			
		} catch (SftpException e) {
			throw new PushPullException("SftpException: " + e.getMessage());
		} catch (ReportManagerException e) {
			throw new PushPullException("The Date Log Filter was improperly " +
					"initialized before pulling files: " + e.getMessage());
		}
	}
	
	/**
	 * Retrieve the logs using the specified login information and host
	 * @throws PushPullException 
	 */
	public final void pull(String path, String destination)
			throws PushPullException {
		
		try {
			
			// If connection has not been made, alert the user
			if (!this.session.isConnected()) {
				throw new PushPullException("Cannot pull files from " + 
						this.session.getHost() + " since we are not " +
						"currently connected to the host");
			}
			
			// The path to the directory containing the file specified in the
			// node path (this will just be the node path if the path is to a
			// directory)
			String dirPath = path;
			if(path.contains("*") || !this.sftpChannel.lstat(path).isDir()){
				dirPath = Utility.getDirPath(path);
			}

			List<String> localFileList = FileUtil.getFilenameList(destination);

			List<String> array = getFileList(path);
			for (String filename : array) {
				if (!localFileList.contains(filename)) {
					
					// Get the log
					String remoteFilePath = dirPath + File.separator + filename;
					this.log.info("Transferring: "
							+ remoteFilePath + " to " + destination);
					// TODO: Consider using an implementation of this method
					// that leverages a progress monitor, perhaps when the
					// given file is over a certain threshold in size
					// (see PDS-305)
					this.sftpChannel.get(remoteFilePath, destination);
					
					// Validate the log file
					String localFilePath = destination + File.separator +
							filename;
					File logFile = new File(localFilePath);
					if(!logFile.exists()){
						log.warning("The log at " + localFilePath +
								" did not download");
					}else if(logFile.length() == 0){
						log.warning("The log at " + localFilePath +
								" is empty and will be deleted");
						try{
							FileUtils.forceDelete(logFile);
						}catch(IOException e){
							log.fine("An error occurred while attempting to " +
									"delete an empty log: " + e.getMessage());
						}
					}
					
				} else {
					this.log.info(dirPath + "/" + filename
							+ " already exists in " + destination);
				}
			}
		}catch(SftpException e){
			throw new PushPullException("An error occurred while pulling " +
					"files from " + this.session.getHost() + ":" + path +
					": " + e.getMessage());
		}catch(ReportManagerException e){
			throw new PushPullException("An error occured while determining "
					+ "the already pulled files at " + destination + ": " +
					e.getMessage());
		}
	}
	
}
