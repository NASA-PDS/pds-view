//	Copyright 2013, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id: SearchCoreLauncher.java 11350 2013-02-22 00:55:41Z jpadams $
//

package gov.nasa.pds.report.profile.manager.util;

import gov.nasa.pds.report.update.model.LogSet;
import gov.nasa.pds.report.update.util.RemoteFileTransfer;
import gov.nasa.pds.report.update.util.SFTPConnect;

import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Utility that provides all SFTP connection functionality
 * 
 * @author jpadams
 * 
 */
public class TransferUtil {

	public TransferUtil() {
	}

	public final JsonObject checkAllConnections(List<LogSet> lsList) {
		JsonObject root = new JsonObject();
		JsonArray jArray = new JsonArray();
		JsonObject jObj = null;
		LogSet ls = null;

		RemoteFileTransfer transfer = new SFTPConnect();

		for (Iterator<LogSet> it1 = lsList.iterator(); it1.hasNext();) {
			ls = it1.next();
			jObj = transfer.checkConnection(ls.toJson(), ls.getHostname(), ls
					.getUsername(), ls.getPassword(), ls.getPathname());
			jArray.add(jObj);
		}
		root.add("logs", jArray);
		return root;
	}
}
