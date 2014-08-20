package gov.nasa.pds.report.logs.pushpull;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

/*
 * We could track progress while downloading files with this class by passing
 * org.apache.commons.io.output.CountingOutputStream to FTPClient.retrieveFile()
 * or by creating a CopyStreamListener implementation and passing it to the 
 * FTPClient using setCopyStreamListener()--see FTPClientExample.java.
 */
public class FtpPull implements PDSPull {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	protected FTPClient client;
	
	public FtpPull(){
		
		this.client = new FTPClient();
		this.client.setListHiddenFiles(false);
		
	}
	
	public boolean connect(final String hostname, final String username,
			final String password, final boolean encrypted)
			throws PushPullException {
		
		this.log.info("Connecting to " + username + "@" + hostname +
				" using " + this.getClass().getName());
		
		// Connect to the server and login
		try{
			this.client.connect(hostname);
			if(!FTPReply.isPositiveCompletion(client.getReplyCode())){
				this.log.warning("An error occurred while connecting to " +
						hostname + ": " + client.getReplyString());
				this.disconnect();
				return false;
			}
			if(!this.client.login(username, password)){
				this.client.logout();
				return false;
			}
		}catch(IOException e){
			throw new PushPullException("En error occurred while " +
					"connecting to " + hostname + ": " + e.getMessage());
		}
		
		// Configure the FTP client
		this.client.enterLocalPassiveMode();
		this.client.setUseEPSVwithIPv4(false);
		FTPClientConfig config = new FTPClientConfig();
        config.setLenientFutureDates(true);
        this.client.configure(config);
		
		return true;
		
	}
	
	public final void pull(String path, String destination)
			throws PushPullException {
		
		
		
		return;
		
	}
	
	public void disconnect() throws PushPullException{
		
		if(this.client.isConnected()){
			try{
				this.client.logout();
				this.client.disconnect();
			}catch(IOException e){
				throw new PushPullException("An error occurred while " +
						"disconnecting: " + e.getMessage());
			}
		}
		
	}
	
}
