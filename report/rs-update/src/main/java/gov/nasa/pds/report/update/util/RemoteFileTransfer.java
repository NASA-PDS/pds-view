package gov.nasa.pds.report.update.util;

import com.google.gson.JsonObject;

public interface RemoteFileTransfer {
	
	JsonObject checkConnection(final JsonObject start, final String hostname, final String username, final String password, final String pathname);
	
	void getLogs(final String hostname, final String username, final String password, final String pathname, final String logDest);
}
