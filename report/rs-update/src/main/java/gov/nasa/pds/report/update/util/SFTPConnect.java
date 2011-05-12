package gov.nasa.pds.report.update.util;

import gov.nasa.pds.report.update.constants.Constants;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Utility that provides all SFTP connection functionality
 * @author jpadams
 *
 */
public class SFTPConnect implements RemoteFileTransfer {
	
	private Logger log = Logger.getLogger(this.getClass().getName());

	protected Session session;
	protected ChannelSftp sftpChannel;
	protected String logDestBase;
	
	public SFTPConnect() {
		this.session = null;
		this.sftpChannel = new ChannelSftp();
	}

	/**
	 * Provides connection to the remote machine via SFTP
	 * 
	 * @param logSet
	 * @throws JSchException
	 */
	private void connect(final String hostname, final String username, final String password) throws JSchException {
		JSch jsch = new JSch();

		this.session = jsch.getSession(username, hostname, 22);

		this.session.setConfig("StrictHostKeyChecking", "no");	// Ignore HostKeyChecking                  
		this.session.setPassword(decrypt(password));			// set the password for authentication
		this.session.connect();

		Channel channel = this.session.openChannel("sftp");		// Getting the channel using sftp
		channel.connect();
		this.sftpChannel = (ChannelSftp) channel;
		
		this.log.info("Connection: " + channel.isConnected());
	}
	
	private String decrypt(String password) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(Constants.CRYPT_PASSWORD);
		return encryptor.decrypt(password);
	}

	/**
	 * Disconnect SFTP and the session
	 */
	private void disconnect() {
		if (this.sftpChannel.isConnected()) {	// Exits the channel
			this.sftpChannel.exit();
		}

		if (this.session.isConnected()) {		// Disconnect the session
			this.session.disconnect();
		}
	}

	/**
	 * Check the SFTP connection to the specified machine according the login information
	 * 
	 * @param hostname
	 * @param username
	 * @param password
	 * @param pathname
	 * @return
	 */
	public final JsonObject checkConnection(final JsonObject start, final String hostname, final String username, final String password, final String pathname) {
		byte connect = 1;
		byte sftp = 1;

		String error = "";

		JsonObject jObj = start;
		try {
			connect(hostname, username, password);
			
			JsonArray matches = getFileList(pathname);

			jObj.add("files", matches);
		} catch (JSchException e) {
			this.log.warning("Jsch Error: "+e.getMessage());
			connect = 0;
			error = e.getMessage();
		} catch (SftpException e) {
			this.log.warning("SFTP Error: "+e.getMessage());
			sftp = 0;
			error = e.getMessage();   
		} finally {
			disconnect();
		}

		jObj.add("connect", new JsonPrimitive(connect));
		jObj.add("sftp", new JsonPrimitive(sftp));
		jObj.add("error", new JsonPrimitive(error));
		return jObj;
	}
	
	/**
	 * Retrieves the file listing from a remote machine
	 * 
	 * @param pathname
	 * @return
	 * @throws SftpException
	 */
	private final JsonArray getFileList (String pathname) throws SftpException {
		String[] dirList;
		String filename;
		
		Vector lsOut = this.sftpChannel.ls(pathname);

		JsonArray matches = new JsonArray();
		for (Object obj : lsOut) {
			dirList = obj.toString().split(" ");

			//TODO May need to change this
			filename = dirList[dirList.length - 1];
			if (!filename.equals(".") && !filename.equals("..")) {
				matches.add(new JsonPrimitive(filename));
			}
		}
		
		return matches;
	}
	
	/**
	 * Retrieve the logs using the specified login information and host
	 */
	public final void getLogs(final String hostname, final String username, final String password, final String pathname, final String logDestPath) {
		try {
			connect(hostname, username, password);
			
			this.log.info("SFTP GET - " + pathname + " to " + logDestPath);
			
			JsonArray array = getFileList(pathname);
			
			List<String> localFileList = BasicUtil.getLocalFileList(logDestPath);
			
			String filename;
			String basePath = getBasePath(pathname);
			
			for (JsonElement element : array) {
				filename = element.getAsString();
				if (!localFileList.contains(filename)) {
					this.log.info("Getting " + basePath + "/" + filename + " to " + logDestPath);
					this.sftpChannel.get(basePath + "/" + filename, logDestPath);
				} else {
					this.log.info(basePath + "/" + filename + " already exists in " + logDestPath);
				}
			}
		} catch (JSchException e) {
			this.log.warning("Jsch Error: "+e.getMessage());
		} catch (SftpException e) {
			this.log.warning("SFTP Error: "+e.getMessage());
		} finally {
			disconnect();
		}
	}
	
	/**
	 * Remove the filename/expression from the pathname and return base path
	 * @param pathname
	 * @return
	 */
	private final String getBasePath(String pathname) {
		String basePath = "";
		String[] pathArray = pathname.split("/");
		for (int i=0; i < pathArray.length-1; i++) {
			if (!pathArray[i].equals(""))
				basePath += "/" + pathArray[i];
		}
		return basePath;
	}
}
