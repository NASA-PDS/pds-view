package gov.nasa.pds.report.logs.pushpull;

import gov.nasa.pds.report.ReportManagerException;
import gov.nasa.pds.report.util.DateLogFilter;
import gov.nasa.pds.report.util.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

// If at some point a node decides to require authentication to download their 
// logs, see:
// http://stackoverflow.com/questions/955624/download-a-file-from-the-internet-using-java-how-to-authenticate
public class HttpPull implements PDSPull{
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private static final int connectionTimeout = (int)TimeUnit.MINUTES.toMillis(1);
	private static final int readTimeout = (int)TimeUnit.MINUTES.toMillis(5);
	
	private String host;
	private String username;
	private String password;
	
	// This is a bit misleading, since we connect at the same time as we pull
	// the file
	public boolean connect(final String hostname, final String username,
			final String password, final boolean encrypted)
			throws PushPullException {
		
		this.host = hostname;
		this.username = username;
		this.password = password;
		
		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication (username, password.toCharArray());
		    }
		});
		
		return true;
		
	}
	
	public final void pull(String path, String destination)
			throws PushPullException{
		
		// Formulate the page URL using the host name and the path
		String urlStr = this.host;
		if(urlStr.endsWith(File.separator)){
			urlStr = urlStr.substring(0, urlStr.length() - 1);
		}else{
			urlStr = this.host;
		}
		if(!path.startsWith(File.separator)){
			urlStr += File.separator;
		}
		urlStr += path;
		URL urlPath = null;
		try{
			urlPath = new URL(urlStr);
		}catch(MalformedURLException e){
			throw new PushPullException("The URL " + urlStr + " is not valid");
		}
		
		// Fetch the list of files at the destination
		List<String> localFileList = new ArrayList<String>();
		localFileList.addAll(Utility.getLocalFileList(destination));
		
		// Pull the file or files at the path
		if(!isPathToDirectory(path)){
			downloadFileAtURL(urlPath, destination, localFileList);
		}else{
			List<URL> urlFileList = null;
			try{
				urlFileList = getURLFileList(urlPath);
			}catch(PushPullException e){
				throw new PushPullException("An error occurred while " +
						"getting the list of files at " + urlPath + ": " +
						e.getMessage());
			}
			if(urlFileList.isEmpty()){
				throw new PushPullException("No files found at " + urlPath);
			}
			for(URL url: urlFileList){
				try{
					downloadFileAtURL(url, destination, localFileList);
				}catch(PushPullException e){
					log.warning("An error occurred while downloading the " +
							"file at " + url + ": " + e.getMessage());
				}
			}
		}
		
	}
	
	public void disconnect() throws PushPullException{
		
		this.host = null;
		
	}
	
	private void downloadFileAtURL(URL url, String destination,
			List<String> localFileList) throws PushPullException{
		
		// Determine the file name
		String urlStr = url.toString();
		String filename =
				urlStr.substring(urlStr.lastIndexOf(File.separator) + 1);
		
		// Check if the file already exists at the destination
		if(localFileList.contains(filename)){
			this.log.info(filename + " already exists in " + 
					destination);
			return;
		}
		
		// Check if filename matches date filter
		try{
			if(!DateLogFilter.match(filename)){
				log.fine(filename + " does not match the date filter");
				return;
			}
		}catch(ParseException e){
			throw new PushPullException("An error occurred while parsing " +
					"log name " + filename + " for date filtering: " +
					e.getMessage());
		}catch(ReportManagerException e){
			throw new PushPullException("The Date Log Filter was improperly " +
					"initialized before pulling files: " + e.getMessage());
		}
		
		try{
		
			// Connect to the file URL
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			
			// Check that the connection is viable
			if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){
				throw new PushPullException("The connection to log " + url +
						" failed (" +  conn.getResponseCode() + "): " +
						conn.getResponseMessage()); 
			}
			
			// Configure the connection timeout
			conn.setConnectTimeout(connectionTimeout);
			conn.setReadTimeout(readTimeout);
			
			// Read from the connection and stream it into the file
			FileOutputStream fos = new FileOutputStream(destination +
					File.separator + filename);
			this.log.info("Transferring: " + url.toString() + " to " + 
					destination);
			fos.getChannel().transferFrom(Channels.newChannel(
					conn.getInputStream()), 0, Long.MAX_VALUE);
			
		}catch(IOException e){
			throw new PushPullException("An error occurred while " +
					"downloading the file at " + url + ": " + e.getMessage());
		}
		
	}
	
	private boolean isPathToDirectory(String path){
		
		String extension = FilenameUtils.getExtension(path);
		return extension != null && extension.length() == 0;
		
	}
	
	private List<URL> getURLFileList(URL url) throws PushPullException{
		
		String login = null;
		if(this.password != null){
			login = this.username + ":" + this.password;
		}
		
		Connection conn = Jsoup.connect(url.toString()).
				timeout(connectionTimeout).ignoreHttpErrors(true);
		if(login != null){
			String base64login = new String(Base64.encodeBase64(
					login.getBytes()));
			conn = conn.header("Authorization", "Basic " + base64login);
		}
		Response res = null;
		try{
			res = conn.execute();
		}catch(Exception e){
			throw new PushPullException("An error occurred while connecting " +
					"to URL " + url + ": " + e.getMessage());
		}
		if(res == null){
			throw new PushPullException("No response received while " +
					"requesting files at " + url);
		}
		if(res.statusCode() != 200){
			throw new PushPullException("The request for files at URL " + url +
					" returned status (" + res.statusCode() + "): " +
					res.statusMessage());
		}
		Document doc = null;
		try{
			doc = conn.get();
		}catch(IOException e){
			log.warning("An error occurred while fetching the document from " +
					url + ": " + e.getMessage());
		}
		
		ArrayList<URL> results = new ArrayList<URL>();
		for(Element link : doc.select("a[href]")){
			
			String value = link.attr("abs:href");
			log.finest("Found file element: " + value);
			
			// Check if the resource at the found URL resides within the remote
			// path containing the logs we want
			if(!value.contains(url.toString())){
				log.finest("Found link pointing outside of diectory " +
						"containing logs " + value);
				continue;
			}
				
			// Check if the link contains any strange characters,
			// indicating that it is not a log
			Pattern p = Pattern.compile("[=;]");
			if(p.matcher(value).find()){
				log.finest("Found non-file link " + value);
				continue;
			}
			
			try{
				URL logUrl = new URL(value);
				results.add(logUrl);
			}catch(MalformedURLException e){
				log.warning("Found illegal URL " + value);
			}
				
		}
		
		return results;
	}
	
}