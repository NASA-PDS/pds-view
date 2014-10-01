package gov.nasa.pds.report.logs.pushpull;

import gov.nasa.pds.report.util.Utility;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HttpPull implements PDSPull{
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private static final int connectionTimeout = (int)TimeUnit.MINUTES.toMillis(1);
	private static final int readTimeout = (int)TimeUnit.MINUTES.toMillis(5);
	
	private String host;
	
	// This is a bit misleading, since we connect at the same time as we pull
	// the file
	public boolean connect(final String hostname, final String username,
			final String password, final boolean encrypted)
			throws PushPullException {
		
		this.host = hostname;
		
		return true;
		
	}
	
	public final void pull(String path, String destination)
			throws PushPullException{
		
		// Formulate the page URL using the host name and the path
		String urlStr = this.host;
		if(urlStr.endsWith("/")){
			urlStr = urlStr.substring(0, urlStr.length() - 1);
		}else{
			urlStr = this.host;
		}
		if(!path.startsWith("/")){
			urlStr += "/";
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
			}catch(IOException e){
				log.severe("An error occurred while getting the list of " +
						"files at " + urlPath + ": " + e.getMessage());
				return;
			}
			for(URL url: urlFileList){
				downloadFileAtURL(url, destination, localFileList);
			}
		}
		
	}
	
	public void disconnect() throws PushPullException{
		
		this.host = null;
		
	}
	
	private void downloadFileAtURL(URL url, String destination,
			List<String> localFileList){
		
		// Determine the file name
		String urlStr = url.toString();
		String filename = urlStr.substring(urlStr.lastIndexOf("/") + 1);
		
		// Check if the file already exists at the destination
		if(localFileList.contains(filename)){
			this.log.info(filename + " already exists in " + 
					destination + "\n");
			return;
		}
		
		// Download the file
		File localFile = new File(destination, filename);
		try{
			this.log.info("Transferring: " + url + " to " + destination);
			FileUtils.copyURLToFile(url, localFile, connectionTimeout,
					readTimeout);
		}catch(IOException e){
			log.severe("An error occurred while downloading the file from " + 
					url + ": " + e.getMessage());
		}
		
	}
	
	private boolean isPathToDirectory(String path){
		
		String extension = FilenameUtils.getExtension(path);
		return extension != null && extension.length() == 0;
		
	}
	
	private List<URL> getURLFileList(URL url) throws IOException{
		Document doc =
				Jsoup.connect(url.toString()).timeout(connectionTimeout).get();
		Set<URL> results = new LinkedHashSet<URL>();
		for (Element file : doc.select("a")) {
			String value = file.attr("abs:href");
			// Check if the given url is a subset of the href value. If it is,
			// assume it is a file or a directory we will need to process.
			if (value.contains(url.toString())) {
				// Check if the value has a 3-character extension. If so, it
				// is most likely a file
				String extension = FilenameUtils.getExtension(value);
				if (extension != null && extension.length() > 0) {
					results.add(new URL(value));
				}
			}
		}
		return new ArrayList<URL>(results);
	}
	
}