package gov.nasa.pds.report.util;

import gov.nasa.pds.report.constants.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

// TODO: Write some javadocs for this class

/**
 * This class is used while processing logs to run commands on those logs from
 * the command line.
 * 
 * @author resneck
 *
 */
public class CommandLineWorker{
	
	private String command = null;
	private boolean debugMode = false;
	private int timeout = 0;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public CommandLineWorker(String command){
		this.command = command;
		this.timeout = Integer.parseInt(System.getProperty(
				Constants.COMMANDLINE_TIMEOUT_PROP, "0"));
	}
	
	public void setDebugMode(boolean debug){
		this.debugMode = debug;
	}
	
	public int execute(){
		
		Process p = null;
		ProcessWatcher watcher = null;
		
		try{
			
			log.finer("Running command: " + command);
			// TODO: Allow the user to configure which shell is used
			ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", command);
			pb.redirectErrorStream(true);
			p = pb.start();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			watcher = new ProcessWatcher(p);
			watcher.start();
			if(this.timeout != 0){
				watcher.join(this.timeout);
			}else{
				watcher.join();
			}
			Integer exitValue = watcher.getExitValue();
			if(exitValue != null){
				if(this.debugMode && exitValue.intValue() != 0){
					for(String line = reader.readLine(); line != null; 
							line = reader.readLine()){
						System.out.println("Output from command: " + line);
					}
				}
				return exitValue.intValue();
			}else{
				log.warning("The command '" + command + "' timed out");
				watcher.interrupt();
				return -1;
			}
			
		}catch(IOException e){
			log.warning("An I/O error occurred while running the command: " + 
					command);
			return -1;
		}catch(InterruptedException e){
			log.warning("The command '" + command + "' has been interrupted");
			watcher.interrupt();
			return -1;
		}
		
	}
	
	private class ProcessWatcher extends Thread{
		
		private Process p = null;
		private Integer exitValue = null;
		
		private ProcessWatcher(Process p){
			this.p = p;
		}
		
		public void run(){
			
			try{
				exitValue = p.waitFor();
			}catch(InterruptedException e){
				p.destroy();
			}
			
		}
		
		public Integer getExitValue(){
			return exitValue;
		}
		
	}
	
}