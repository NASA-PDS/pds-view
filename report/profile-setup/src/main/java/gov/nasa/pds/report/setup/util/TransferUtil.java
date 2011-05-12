package gov.nasa.pds.report.setup.util;

import gov.nasa.pds.report.update.model.LogSet;
import gov.nasa.pds.report.update.util.RemoteFileTransfer;
import gov.nasa.pds.report.update.util.SFTPConnect;

import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Utility that provides all SFTP connection functionality
 * @author jpadams
 *
 */
public class TransferUtil {
	
	public TransferUtil() { }
	
	public final JsonObject checkAllConnections(List<LogSet> lsList) {
		//byte empty = 1;
		byte connect = 1;
		byte sftp = 1;

		String error = "";

		JsonObject root = new JsonObject();
		JsonArray jArray = new JsonArray();
		JsonObject jObj = null;
		LogSet ls = null;
		
		RemoteFileTransfer transfer = new SFTPConnect();

		for (Iterator<LogSet> it1 = lsList.iterator(); it1.hasNext();) {
			ls = it1.next();
			jObj = transfer.checkConnection(ls.toJson(), ls.getHostname(), ls.getUsername(), ls.getPassword(), ls.getPathname());
			jArray.add(jObj);
		}
		root.add("logs", jArray);
		return root;
	}
}
