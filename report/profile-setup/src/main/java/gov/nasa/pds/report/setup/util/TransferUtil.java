package gov.nasa.pds.report.setup.util;

import gov.nasa.pds.report.transfer.model.LogSet;
import gov.nasa.pds.report.transfer.model.Profile;
import gov.nasa.pds.report.transfer.util.RemoteFileTransfer;
import gov.nasa.pds.report.transfer.util.SFTPConnect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
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
public class TransferUtil {
	private Logger log = Logger.getLogger(this.getClass().getName());

	//private Session session;
	//private ChannelSftp sftpChannel;
	private String logDestBase;

	private Profile profile;
	private List<LogSet> lsList;

	public TransferUtil(final List<LogSet> lsList) {
		this.lsList = lsList;
	}
	
	public final JsonObject checkAllConnections() {
		//byte empty = 1;
		byte connect = 1;
		byte sftp = 1;

		String error = "";

		JsonObject root = new JsonObject();
		JsonArray jArray = new JsonArray();
		JsonObject jObj = null;
		LogSet ls = null;
		
		RemoteFileTransfer transfer = new SFTPConnect();

		for (Iterator<LogSet> it1 = this.lsList.iterator(); it1.hasNext();) {
			ls = it1.next();
			jObj = transfer.checkConnection(ls.toJson(), ls.getHostname(), ls.getUsername(), ls.getPassword(), ls.getPathname());
			jArray.add(jObj);
		}
		root.add("logs", jArray);
		return root;
	}
}
