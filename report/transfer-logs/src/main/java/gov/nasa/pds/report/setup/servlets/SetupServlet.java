package gov.nasa.pds.report.setup.servlets;

import gov.nasa.pds.report.setup.model.Host;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

/**
 * Servlet implementation class SetupServlet
 */
public class SetupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected Logger log = Logger.getLogger(this.getClass().getName());
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetupServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       	JSch jsch = new JSch(); 
        Gson gson = new Gson();
        JsonObject root = new JsonObject();
        byte connect = 1;
        byte sftp = 1;
        String error = "";
        byte empty = 1;
       
       Session session = null;
       
       Host host = new Host();
       
       // Profile name to create/update
       host.setProfile(request.getParameter("profile"));
       // Remote machine host name or IP
       host.setHostname(request.getParameter("hostname"));
       // User name to connect the remote machine
       host.setUsername(request.getParameter("username"));
       // Password for remote machine authentication
       host.setPassword(request.getParameter("password"));    
       // Source file path on local machine
       //String srcPath = "D:\\Test.txt";
       // Destination directory location on remote machine
       host.setSrcPath(request.getParameter("srcPath"));
       // Regex to find files at source
       host.setRegex(request.getParameter("regex"));
       
       log.info("profile: "+host.getProfile());
       log.info("hostname: "+host.getHostname());
       log.info("username: "+host.getUsername());
       log.info("srcPath: "+host.getSrcPath());
       log.info("regex: "+host.getRegex());
       
       try {
        // Getting the session
        session = jsch.getSession(host.getUsername(), host.getHostname(), 22);
                    
        // Ignore HostKeyChecking
        session.setConfig("StrictHostKeyChecking", "no");                  
      
        // set the password for authentication
        session.setPassword(host.getPassword());
        session.connect();
                    
        // Getting the channel using sftp
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;

        String[] dirList;
        String filename;
        Vector lsOut = sftpChannel.ls(host.getSrcPath());
        
        JsonArray matches = new JsonArray();
        for (Iterator it = lsOut.iterator(); it.hasNext();) {
        	dirList = it.next().toString().split(" ");
        	filename = dirList[dirList.length-1];
        	if (filename.matches(host.getRegex())) {
        		matches.add(new JsonPrimitive(filename));
        	}
        }
        
        if (!matches.isJsonNull()) {
        	empty = 0;
        	root.add("files", matches);
        }
        
        //root.add("count", new JsonPrimitive(matches.size()));
        
        // Found JSONLib has memory leak
//	        JSONObject jsonRoot = new JSONObject();
//	        JSONArray fileList = new JSONArray();
//	        if (!lsOut.isEmpty()) {
//		        for (Iterator it = lsOut.iterator(); it.hasNext();) {
//		        	dirList = it.next().toString().split(" ");
//		        	filename = dirList[dirList.length-1];
//		        	if (filename.matches(regex)) {
//		        		fileList.add(filename);
//		        		log.info("file found: "+filename);
//		        	}
//		        }
//		        empty = 0;
//		        jsonRoot.put("files", fileList);
//	        }
//	        jsonRoot.put("empty", empty);
//	        
//	        PrintWriter out = response.getWriter();
//	        out.print(jsonRoot.toString());
        
        // Exits the channel
         sftpChannel.exit();
                    
         // Disconnect the session
         session.disconnect();

       } catch (JSchException e) {
    	   //root.add("connect", new JsonPrimitive(0));
    	   connect = 0;
    	   error = e.getMessage();
       } catch (SftpException e) {
           sftp = 0;
           error = e.getMessage();   
       } finally {
	        root.add("connect", new JsonPrimitive(connect));
	        root.add("sftp", new JsonPrimitive(sftp));
	        root.add("error", new JsonPrimitive(error));
	        root.add("empty", new JsonPrimitive(empty));
    	   gson.toJson(root, response.getWriter());
       }
	}

}
