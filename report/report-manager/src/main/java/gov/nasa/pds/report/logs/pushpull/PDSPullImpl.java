package gov.nasa.pds.report.logs.pushpull;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.Debugger;
import gov.nasa.pds.report.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.jasypt.util.text.StrongTextEncryptor;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

// TODO: Make a the class take a regex filter for what to download

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
	 * Connects to external machine using SFTP. If encrypted, same encryption as PBEEncryptor
	 * expected. 
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
			this.log.info("Connecting to " + username + "@" + hostname + " using " + this.getClass().getName());
			Debugger.debug(hostname + " - " + username);
			JSch jsch = new JSch();
			this.session = jsch.getSession(username, hostname, 22);
			this.session.setConfig("StrictHostKeyChecking", "no"); // Ignore HostKeyChecking
			
			if (encrypted) {
				this.session.setPassword(decrypt(password)); // set the password for authentication
			} else {
				this.session.setPassword(password); // set the password for authentication
			}
			this.session.connect();
	
			Channel channel = this.session.openChannel("sftp"); // Getting the
																// channel using
																// sftp
			channel.connect();
			this.sftpChannel = (ChannelSftp) channel;
	
			Debugger.debug("Connection: " + channel.isConnected());
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
	private final List<String> getFileList(String path) throws PushPullException {
		try {
			String[] dirList;
			String filename;
			
			Vector lsOut = this.sftpChannel.ls(path);
	
			List<String> matches = new ArrayList<String>();
			for (Object obj : lsOut) {
				dirList = obj.toString().split(" ");
	
				// TODO May need to change this
				filename = dirList[dirList.length - 1];
				if (!filename.equals(".") && !filename.equals("..")) {
					matches.add(filename);
				}
			}

			return matches;
		} catch (SftpException e) {
			throw new PushPullException("SftpException: " + e.getMessage());
		}
	}
	
	/**
	 * Retrieve the logs using the specified login information and host
	 * @throws PushPullException 
	 */
	public final void pull(String path, String destination) throws PushPullException {
		try {
			
			// If connection has not been made, make one
			if (!this.session.isConnected()) {
				return;
			}
			
			// The path to the directory containing the file specified in the
			// node path (this will just be the node path if the path is to a
			// directory)
			String dirPath = path;
			if(path.contains("*") || !this.sftpChannel.lstat(path).isDir()){
				Debugger.debug("Path " + path + " is a file path.");
				dirPath = Utility.getDirPath(path);
			}
			Debugger.debug("Directory path: " + dirPath);
			
			List<String> array = getFileList(path);

			List<String> localFileList = new ArrayList<String>();
			localFileList.addAll(Utility.getLocalFileList(destination));

			for (String filename : array) {
				if (!localFileList.contains(filename)) {
					this.log.info("Transferring: "
							+ dirPath + "/" + filename + " to " + destination
							+ "\n");
					// TODO: Consider using an implementation of this method
					// that leverages a progress monitor, perhaps when the
					// given file is over a certain threshold in size
					this.sftpChannel
							.get(dirPath + "/" + filename, destination);
				} else {
					this.log.info(dirPath + "/" + filename
							+ " already exists in " + destination + "\n");
				}
			}
		} catch (SftpException e) {
			throw new PushPullException("An error occurred while pulling " +
					"files from " + this.session.getHost() + ":" + path +
					": " + e.getMessage());
		} finally {
			disconnect();
		}
	}
	
}
