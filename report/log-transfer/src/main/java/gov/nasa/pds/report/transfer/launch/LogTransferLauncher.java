package gov.nasa.pds.report.transfer.launch;

import gov.nasa.pds.report.transfer.properties.EnvProperties;
import gov.nasa.pds.report.transfer.util.FileUtil;
import gov.nasa.pds.report.transfer.util.RemoteFileTransfer;
import gov.nasa.pds.report.transfer.util.SFTPConnect;

public class LogTransferLauncher {

	private String logDest;
	private String hostname;
	private String username;
	private String password;
	private String pathname;
	
	private RemoteFileTransfer transfer;
	private FileUtil dirStruct;

	/**
	 * Constructor for web interface when log destination directory is already determined
	 * 
	 * @param env
	 * @param logPath
	 * @param hostname
	 * @param username
	 * @param password
	 * @param pathname
	 * @param label
	 */
	public LogTransferLauncher(EnvProperties env, String logPath, String hostname, String username, String password, String pathname, String label) {
		this.logDest = env.getSawmillLogHome() + "/" + logPath + "/" + label;
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.pathname = pathname;
		
		this.dirStruct = new FileUtil();
		this.transfer = new SFTPConnect();
	}
	
	/**
	 * Constructor for command-line interface
	 * 
	 * @param logDest
	 * @param hostname
	 * @param username
	 * @param password
	 * @param pathname
	 * @param label
	 */
	public LogTransferLauncher(String logDest, String hostname, String username, String password, String pathname, String label) {
		this.logDest = logDest;
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.pathname = pathname;
		
		this.dirStruct = new FileUtil();
		this.transfer = new SFTPConnect();
	}

	public void execute() {
		this.dirStruct.createDirStruct(this.logDest);
		this.transfer.getLogs(this.hostname, this.username, this.password, this.pathname, this.logDest);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
