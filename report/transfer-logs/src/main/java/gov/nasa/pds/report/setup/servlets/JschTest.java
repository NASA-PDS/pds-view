package gov.nasa.pds.report.setup.servlets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class JschTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	      
	       JSch jsch = new JSch();

	       Session session = null;
	       // Remote machine host name or IP
	       String hostName = "pdsdev.jpl.nasa.gov";
	       // User name to connect the remote machine
	       String userName = "jpadams";
	       // Password for remote machine authentication
	       String password = "Ph1ll1es8008";    
	       // Source file path on local machine
	       //String srcPath = "D:\\Test.txt";
	       // Destination directory location on remote machine
	       String destPath = "/home/shardman/metrics/en/pdsweb1-apache";
	       // Regex to find files at source
	       String regex = "access_log.*.txt";
	       try {
	        // Getting the session
	        session = jsch.getSession(userName, hostName, 22);
	                    
	        // Ignore HostKeyChecking
	        session.setConfig("StrictHostKeyChecking", "no");                  
	      
	        // set the password for authentication
	        session.setPassword(password);
	        session.connect();
	                    
	        // Getting the channel using sftp
	        Channel channel = session.openChannel("sftp");
	        channel.connect();
	        ChannelSftp sftpChannel = (ChannelSftp) channel;

	        String[] dirList;
	        String filename;
	        Vector lsOut = sftpChannel.ls(destPath);
	        if (!lsOut.isEmpty()) {
		        for (Iterator it = lsOut.iterator(); it.hasNext();) {
		        	dirList = it.next().toString().split(" ");
		        	filename = dirList[dirList.length-1];
		        	if (filename.matches(regex))
		        		System.out.println(filename);
		        }
	        } else {
	        	System.out.println("No files found matching regex in "+destPath);
	        }
	             
	        // Exits the channel
	         sftpChannel.exit();
	                    
	         // Disconnect the session
	         session.disconnect();

	       } catch (JSchException e) {
	              e.printStackTrace();

	       } catch (SftpException e) {
	              e.printStackTrace();
	       }
	     }
}
