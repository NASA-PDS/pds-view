package gov.nasa.pds.report.setup.util;

import gov.nasa.pds.report.setup.model.LogSet;
import gov.nasa.pds.report.setup.model.Profile;
import gov.nasa.pds.report.setup.properties.DBProperties;
import gov.nasa.pds.report.setup.properties.EnvProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPUtil {
	private Logger _log = Logger.getLogger(this.getClass().getName());

	private Session _session;
	private ChannelSftp _sftpChannel;
	private String _logDestBase;

	private Profile _profile;
	private ArrayList<LogSet> _lsList;

	public SFTPUtil(String logDest, Profile profile) {
		this._session = null;
		this._sftpChannel = new ChannelSftp();

		this._profile = profile;
		this._lsList = profile.getLogSetList();

		setLogDest(logDest);
	}

	public SFTPUtil(ArrayList<LogSet> logInfoList) {
		this._session = null;
		this._sftpChannel = new ChannelSftp();

		this._lsList = logInfoList;
	}

	private void connect(LogSet logSet) throws JSchException {
		JSch jsch = new JSch();
		
		this._session = jsch.getSession(logSet.getUsername(), logSet.getHostname(), 22);

		// Ignore HostKeyChecking
		this._session.setConfig("StrictHostKeyChecking", "no");                  

		// set the password for authentication
		this._session.setPassword(logSet.getPassword());
		this._session.connect();

		// Getting the channel using sftp
		Channel channel = this._session.openChannel("sftp");
		channel.connect();
		this._sftpChannel = (ChannelSftp) channel;
	}

	private void disconnect() {
		// Exits the channel
		if (this._sftpChannel.isConnected())
			this._sftpChannel.exit();

		// Disconnect the session
		if (this._session.isConnected())
			this._session.disconnect();
	}

	public JsonObject checkConnections() {
		//byte empty = 1;
		byte connect = 1;
		byte sftp = 1;

		String error = "";

		JsonObject root = new JsonObject();
		JsonArray jArray = new JsonArray();
		JsonObject jObj = null;
		LogSet ls = null;

		for (Iterator<LogSet> it1 = this._lsList.iterator(); it1.hasNext();) {
			jObj = new JsonObject();

			ls = it1.next();
			if (ls.getActiveFlag().equals("y")) {
				try {
					jObj = ls.toJson();
					
					connect(ls);
	
					// Getting the session
					String[] dirList;
					String filename;
	
					Vector lsOut = this._sftpChannel.ls(ls.getPathname());
	
					JsonArray matches = new JsonArray();
					for (Iterator it2 = lsOut.iterator(); it2.hasNext();) {
						dirList = it2.next().toString().split(" ");
	
						//TODO May need to change this
						filename = dirList[dirList.length-1];
						if (!filename.equals(".") && !filename.equals("..")) {
							matches.add(new JsonPrimitive(filename));
						}
					}
	
					/*if (!matches.isJsonNull()) {
						empty = 0;
					}*/
					
					jObj.add("files", matches);
				} catch (JSchException e) {
					//root.add("connect", new JsonPrimitive(0));
					this._log.warning("Jsch Error: "+e.getMessage());
					connect = 0;
					error = e.getMessage();
				} catch (SftpException e) {
					this._log.warning("SFTP Error: "+e.getMessage());
					sftp = 0;
					error = e.getMessage();   
				} finally {
					
					disconnect();
				}
				
				//logObj.add("label", new JsonPrimitive(logInfo.getLabel()));
				jObj.add("connect", new JsonPrimitive(connect));
				jObj.add("sftp", new JsonPrimitive(sftp));
				jObj.add("error", new JsonPrimitive(error));
				//jObj.add("empty", new JsonPrimitive(empty));
				jArray.add(jObj);
			}
		}

		root.add("logs", jArray);
		this._log.info(root.toString());
		return root;
	}

	public void getLogs() {
		//byte empty = 1;
		LogSet logSet;
		byte connect = 1;
		byte sftp = 1;
		String error = "";
		String logDestPath = "";

		for (Iterator<LogSet> it = this._lsList.iterator(); it.hasNext();) {
			try {
				logSet = it.next();
				
				logDestPath = this._logDestBase + logSet.getLabel(); //TODO Create LogPath object since it is reused in SawmillUtil
				
				this._log.info(logDestPath);

				// Create the dir structure to put the logs
				createDirStruct(logDestPath);

				connect(logSet);

				// Getting the session
				this._sftpChannel.get(logSet.getPathname(), logDestPath);
			} catch (JSchException e) {
				//root.add("connect", new JsonPrimitive(0));
				this._log.warning("Jsch Error: "+e.getMessage());
				connect = 0;
				error = e.getMessage();
			} catch (SftpException e) {
				this._log.warning("SFTP Error: "+e.getMessage());
				sftp = 0;
				error = e.getMessage();   
			} finally {
				//root.add("connect", new JsonPrimitive(connect));
				//root.add("sftp", new JsonPrimitive(sftp));
				//root.add("error", new JsonPrimitive(error));
				//root.add("empty", new JsonPrimitive(empty));
				disconnect();
			}
		}
		//return root;
	}

	private boolean createDirStruct(String path) {
		File dirStruct = new File(path);
		if (!dirStruct.exists())
			return dirStruct.mkdirs();
		else
			return true;
	}

	public void setLogDest(String logDest) {
		this._logDestBase = logDest;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Profile profile = new Profile();
		profile.setIdentifier("ident10");
		//profile.setMethod("pull1");
		profile.setName("name10");
		profile.setNode("eng");

		ArrayList<LogSet> logInfoList = new ArrayList<LogSet>();
		LogSet logSet = new LogSet();

		// Profile name to create/update
		//logInfo.setProfileId(request.getParameter("profile"));
		// Remote machine host name or IP
		logSet.setLabel("pdsweb");
		logSet.setHostname("pdsdev.jpl.nasa.gov");
		// User name to connect the remote machine
		logSet.setUsername("jpadams");
		// Password for remote machine authentication
		logSet.setPassword("test");    
		// Source file path on local machine
		//String srcPath = "D:\\Test.txt";
		// Destination directory location on remote machine
		logSet.setPathname("/home/jpadams/temp.*.txt");
		logInfoList.add(logSet);

		profile.setLogSetList(logInfoList);
		/*logInfo = new LogSet();
		logInfo.setLabel("pdsweb2");
	       logInfo.setHostname("pdsdev.jpl.nasa.gov");
	       // User name to connect the remote machine
	       logInfo.setUsername("jpadams");
	       // Password for remote machine authentication
	       logInfo.setPassword("Ph1ll1es8008");    
	       // Source file path on local machine
	       //String srcPath = "D:\\Test.txt";
	       // Destination directory location on remote machine
	       logInfo.setPathname("/home/jpadams/temp.*.txt");
	       logInfoList.add(logInfo);*/

		SFTPUtil util = new SFTPUtil("/Users/jpadams/Documents/Report_Service/metrics/test",profile);
		util.checkConnections();
		//util.getLogs();
	}

}
