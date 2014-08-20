package gov.nasa.pds.report.logs.pushpull;

public interface PDSPull {

	boolean connect(final String hostname, final String username,
			final String password, final boolean encrypted)
			throws PushPullException;

	void pull(String path, String destination)
					throws PushPullException;
	
	void disconnect() throws PushPullException;
	
}
