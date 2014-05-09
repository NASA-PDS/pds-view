package gov.nasa.pds.report.logs.pushpull;

import gov.nasa.pds.report.constants.Constants;
import gov.nasa.pds.report.util.Debugger;
import gov.nasa.pds.report.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

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
			final String password, final boolean encrypted) throws PushPullException {

		try {
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
	private void disconnect() {
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
				
			List<String> array = getFileList(path);

			List<String> localFileList = new ArrayList<String>();
			localFileList.addAll(Utility.getLocalFileList(destination));

			String basePath = getBasePath(path);

			for (String filename : array) {
				if (!localFileList.contains(filename)) {
					this.log.info("Transferring: "
							+ basePath + "/" + filename + " to " + destination
							+ "\n");
					this.sftpChannel
							.get(basePath + "/" + filename, destination);
				} else {
					this.log.info(basePath + "/" + filename
							+ " already exists in " + destination + "\n");
				}
			}
		} catch (SftpException e) {
			throw new PushPullException("SftpException: " + e.getMessage());
		} finally {
			disconnect();
		}
	}

	/**
	 * Remove the filename/expression from the pathname and return base path
	 * TODO There is a FileUtil or File method that can do this for me
	 * @param pathname
	 * @return
	 */
	private final String getBasePath(String pathname) {
		String basePath = "";
		this.log.info(pathname);
		String[] pathArray = pathname.split("/");
		for (int i = 0; i < pathArray.length - 1; i++) {
			if (!pathArray[i].equals(""))
				basePath += "/" + pathArray[i];
		}
		return basePath;
	}
}
