// Copyright 2015, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id: SearchProductHandler.java 13469 2014-09-12 13:47:47Z hyunlee $

package gov.nasa.pds.transport;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.net.URL;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.oodt.product.LargeProductQueryHandler;
import org.apache.oodt.product.ProductException;
import org.apache.oodt.xmlquery.LargeResult;
import org.apache.oodt.xmlquery.QueryElement;
import org.apache.oodt.xmlquery.XMLQuery;
import org.apache.tika.Tika;


// to query by the search servide
import org.apache.solr.common.SolrDocument;

/**
 * This Apache OODT Product Server handler accepts a query containing one
 * or more Registry Service identifiers, queries the Search Service for 
 * the associated files and returns a package containing those files back 
 * to the calling application.
 *
 * @author hyunlee
 * @version $Revision: 13469 $
 */
public class SearchProductHandler implements LargeProductQueryHandler {
	
  /**
   * Run a query.
   *
   * @param q The query.
   * @return The response.
   * @throws ProductException if an error occurs.
   */
  @Override
  public XMLQuery query(XMLQuery q) throws ProductException {
	  
    List<File> files = new ArrayList<File>();
    List<String> checksums = new ArrayList<String>();

    // Get handler properties.
    String searchUrl = System.getProperty("gov.nasa.pds.transport.SearchProductHandler.searchUrl", "http://localhost:8080/search-service");
    String[] searchUrls = searchUrl.split("\\s*,\\s*");
    for (String _searchUrl : searchUrls) {
    	System.out.println("Using PDS Search URL: "+_searchUrl);
    }
    String tmpDir = System.getProperty("gov.nasa.pds.transport.searchProductHandler.tmpDir", "/tmp");
    String fileRefUrlFlag = System.getProperty("gov.nasa.pds.transport.SearchProductHandler.useFileRefUrl", "true");
    boolean useFileRefUrl = Boolean.parseBoolean(fileRefUrlFlag);

    // Setup the search server url and initialize the search handler.
    SearchHandler searchHandler = new SearchHandler(searchUrl);

    // Get the package format if specified in the query.
    String archivePackage = extractFieldFromQuery(q, "package");    
    if (archivePackage == null) {
      archivePackage = "ZIP";
    } else if ( (!archivePackage.equalsIgnoreCase("ZIP")) && (!archivePackage.equalsIgnoreCase("TGZ"))
    		&& (!archivePackage.equalsIgnoreCase("SIZE"))) {
      throw new ProductException("Invalid package type specified.");
    }
    archivePackage = archivePackage.toUpperCase();
    
    List<String> identifiers = new ArrayList<String>();    
    //System.out.println("SearchProductHandler....q.getKwdQueryString() = " + q.getKwdQueryString());
    
    // get a filename containing identifier list (bulk transport)
    if (q.getKwdQueryString().contains("identifier-list")) {
    	List<String> listFilenames = extractListFileFromQuery(q); 
    	File listFile = getFileURLContent(tmpDir, listFilenames.get(0));
    	//System.out.println("file = " + listFile.getAbsolutePath());

    	try {
    		// Open the file
    		FileInputStream fstream = new FileInputStream(listFile);
    		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
    		String strLine;
    		//Read File Line By Line
    		while ((strLine = br.readLine()) != null)   {
    			// Print the content on the console
    			System.out.println ("SearchProductHandler...line = " + strLine + "      strLine.length() = " + strLine.length());
    			
    			// ignore end of line
    			if (strLine.length()>0)
    				identifiers.add(strLine);
    		}
    		//Close the input stream
    		br.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    else {
        // Get all of the identifiers passed in the query.
    	identifiers = extractIdentifiersFromQuery(q);
    }   
    if (identifiers.size()==0) throw new ProductException("Error: product identifier(s) not specified in request.");

    // Get the files for each product identified by the identifier and
    // add them to the list.
    int i=0; 
    long totalFileRefSize = 0;
    for (String identifier : identifiers) {
      try {
    	  List<SolrDocument> objDocs = searchHandler.getSolrDocument(identifier);
    	  SolrDocument aObj = objDocs.get(0);  
    	  List<String> sizeValues = searchHandler.getValues(aObj, "file_ref_size");
    	  for (String sizeValue: sizeValues) {
			  totalFileRefSize += Long.parseLong(sizeValue);
			  //System.out.println("Size = " + sizeValue);
		  }
    	  if (!useFileRefUrl) {
    		  // file_ref_location & file_ref_name
    		  List<String> refLocValues = searchHandler.getValues(aObj, "file_ref_location");
    		  List<String> nameValues = searchHandler.getValues(aObj, "file_ref_name");
    		  
    		  for (String nameValue: nameValues) {
    			  //System.out.println("file_ref_location & file_ref_name values = " + refLocValues.get(0) + "    " + nameValue);	  
    			  File file = new File(refLocValues.get(0), nameValue);
    			  files.add(file);
    			  
    			  String checksum = MD5Checksum.getMD5Checksum(file.getAbsolutePath()) ;
    			  checksums.add( checksum );
    		  }
    	  }
    	  else {
    		  // file_ref_url 
    		  List<String> urlValues = searchHandler.getValues(aObj, "file_ref_url");
    		  for (String urlValue: urlValues) {
    			  //System.out.println("file_ref_url value = " + urlValue);
    			  File file = getURLContent(tmpDir, urlValue);
    			  files.add(file);

    			  String checksum = MD5Checksum.getMD5Checksum(file.getAbsolutePath()) ;
    			  checksums.add(checksum);
    		  }
    	  }
      } catch (Exception e) {
    	  e.printStackTrace();
        String message = e.getMessage();
        if (message == null) {
          throw new ProductException("Error querying the search service or no products found.");
        } else {
          throw new ProductException("Error querying the search service or no products found. Message: " + message);
        }
      }
      i++;
    }
    
    // Create checksum manifest
    totalFileRefSize += this.addChecksumManifest(files, checksums);    
    //System.out.println("SearchProductHandler.... totalFileRefSize  = " + totalFileRefSize);

    // Build the archive package file
    if (archivePackage.equals("ZIP") || archivePackage.equals("TGZ")) {
 
	    try {
	      String archiveFilePath = null;
	      File archiveFile = null;
	      DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	      Date date = new Date();
	      if (archivePackage.equals("ZIP")) {
	        archiveFilePath = tmpDir + "/pds-package-" + dateFormat.format(date) + ".zip";
	        archiveFile = buildZIPFile(archiveFilePath, files);
	      } else if (archivePackage.equals("TGZ")) {
	        archiveFilePath = tmpDir + "/pds-package-" + dateFormat.format(date) + ".tar.gz";
	        archiveFile = buildTGZFile(archiveFilePath, files);
	      } else {
	        // Not sure how we got here, but bail.
	        throw new ProductException("Invalid package type specified.");
	      }
	      String mimeType = new Tika().detect(archiveFile.getName());
	      q.getResults().add(new LargeResult(archiveFilePath, mimeType, null, archiveFile.getName(), Collections.EMPTY_LIST, archiveFile.length()));
	    } catch (Exception e) {
	      throw new ProductException("Error creating the archive file. Message: " + e.getMessage());
	    }
	    
	// Build the XML file with size information
    } else if (archivePackage.equals("SIZE")) {
		try {

			// create XML document
			String xml = XmlWriter.writeSizeDocument(totalFileRefSize);
			
			// write out XML document
			File tempFile = File.createTempFile(archivePackage.toLowerCase(), ".xml", new File(tmpDir));
			FileUtils.writeStringToFile(tempFile, xml);
			tempFile.deleteOnExit();
			
			// return XML file
			String mimeType = new Tika().detect(tempFile.getName());
		    q.getResults().add(new LargeResult(tempFile.getAbsolutePath(), "text/xml", null, tempFile.getName(), Collections.EMPTY_LIST, tempFile.length()));
			
		} catch (IOException e) {
			throw new ProductException(e.getMessage());
		}
    }

    // Return the original query object, with or without results.
    return q;
  }

  /**
   * Retrieve a chunk of a product.
   *
   * The product is identified by a string ID.  The query handler should return a
   * binary chunk of the product using the given offset and length.  If the ID isn't
   * recognized, it should return null.  It should throw an exception if retrieval
   * fails for some reason.
   *
   * @param id Identifier of the product in the form of a file specification.
   * @param offset Where in the product to get a chunk of it.
   * @param length How much of the product to get.
   * @return A chunk, or null if the <var>id</var> is unknown.
   * @throws ProductException if an error occurs.
   */
  @Override
  public byte[] retrieveChunk(String id, long offset, int length) throws ProductException {

    File file = new File(id);
    try {
      byte[] bytes = FileUtils.readFileToByteArray(file);
      byte[] retBytes = new byte[length];  
      ByteArrayInputStream is = new ByteArrayInputStream(bytes);
      is.skip(offset);
      is.read(retBytes, 0, length);
      return retBytes;
    } catch (IOException e) {
      e.printStackTrace();
      throw new ProductException("Error reading bytes from file: " + file.getAbsolutePath() + " Message: " + e.getMessage());
    }
  }

  /**
   * Close off a product.
   *
   * This method indicates that the product is no longer required and its resources
   * can be freed by the query handler.  If the ID is unknown, no untoward action is
   * required.  It should throw an exception if there is an error during the
   * resource release (such as an {@link java.io.IOException} when closing a file.
   *
   * @param id Product ID.
   * @throws ProductException if an error occurs.
   */
  @Override
  public void close(String id) {
    // Remove the staged archive file.
    try {
      File archiveFile = new File(id);
      archiveFile.delete();
    } catch (Exception ignore) {}
  }


  private static String extractFieldFromQuery(XMLQuery query, String name) {
    for (Iterator<QueryElement> i = query.getWhereElementSet().iterator(); i.hasNext();) {
      QueryElement element = i.next();
      if (element.getRole().equals("elemName") && element.getValue().equalsIgnoreCase(name)) {
        // Get the next element and ensure that it is a LITERAL,
        // and return that.
        QueryElement litElement = i.next();
        return litElement.getValue();
      }
    }
    return null;
  }

  private static List<String> extractIdentifiersFromQuery(XMLQuery query) {
    List<String> identifiers = new ArrayList<String>();
    for (Iterator<QueryElement> i = query.getWhereElementSet().iterator(); i.hasNext();) {
      QueryElement element = i.next();
      if (element.getRole().equals("elemName") && element.getValue().equalsIgnoreCase("identifier")) {
        // Get the next element and ensure that it is a LITERAL,
        // and return that.
        QueryElement litElement = i.next();
        identifiers.add(litElement.getValue());
      }
    }
    return identifiers;
  }

  private static List<String> extractListFileFromQuery(XMLQuery query) {
	    List<String> listFiles = new ArrayList<String>();
	    for (Iterator<QueryElement> i = query.getWhereElementSet().iterator(); i.hasNext();) {
	      QueryElement element = i.next();
	      if (element.getRole().equals("elemName") && element.getValue().equalsIgnoreCase("identifier-list")) {
	        // Get the next element and ensure that it is a LITERAL,
	        // and return that.
	        QueryElement litElement = i.next();
	        listFiles.add(litElement.getValue());
	        System.out.println("list filename = " + litElement.getValue());
	      }
	    }
	    return listFiles;
	  }
  
  private static File buildZIPFile(String zipFilePath, List<File> files) throws IOException {
    byte[] buf = new byte[1024];
    ZipOutputStream out = null;

    try {
      // Create the ZIP file.
      out = new ZipOutputStream(new FileOutputStream(zipFilePath));

      // Add each input file to the ZIP file.
      for (File file : files) {
        // Open the input file and add it to the ZIP file.
        FileInputStream in = new FileInputStream(file);
        out.putNextEntry(new ZipEntry(file.getName()));

        // Transfer bytes from the input file to the ZIP file.
        int len;
        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }

        // Close out the streams.
        out.closeEntry();
        in.close();
      }
    } catch (IOException e) {
      throw e;
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (Exception ignore) {}
      }
    }
    return new File(zipFilePath);
  }

  private static File buildTGZFile(String tgzFilePath, List<File> files) throws IOException {

    byte[] buf = new byte[1024];
    TarArchiveOutputStream out = null;

    try {
      // Create the TAR/GZIP file.
      out = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(tgzFilePath)));

      // Add each input file to the TAR/GZIP file.
      for (File file : files) {
        // Open the input file and add it to the TAR/GZIP file.
        FileInputStream in = new FileInputStream(file);
        TarArchiveEntry archiveEntry = new TarArchiveEntry(file.getName());
        archiveEntry.setSize(file.length());
        out.putArchiveEntry(archiveEntry);

        // Transfer bytes from the input file to the TAR/GZIP file.
        int len;
        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }

        // Close out the streams.
        out.closeArchiveEntry();
        in.close();
      }
    } catch (IOException e) {
      throw e;
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (Exception ignore) {}
      }
    }
    return new File(tgzFilePath);
  }
  
  // Method to add a checksum manifest to the files collection
  private long addChecksumManifest(List<File> files, List<String> checksums) throws ProductException {
	  
  	try	{
  		 
	    // create manifest file in temporary directory
  		String tmpDir = System.getProperty("java.io.tmpdir");
  		File tmpFile = new File(tmpDir, "md5_checksums.txt");

	    // loop over files
	    BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
	    for (int i=0; i<files.size(); i++) {
	    	bw.write(checksums.get(i) + " " + files.get(i).getName());
	    	bw.newLine();
	    }
	    bw.close();
	    
	    // add manifest to the list of files to pack
	    files.add(tmpFile);
	    
	    //System.out.println("File: "+tmpFile.getName()+" size="+tmpFile.length());
	    return tmpFile.length();
	    
  	} catch(IOException e) {
  		e.printStackTrace();
  		throw new ProductException(e.getMessage());
  	}	  
  }
  
  // used by file_ref_url
  private File getURLContent(String tmpDir, String urlString) {
	  URL url = null;
	  File file = null;
	  try {
		  // get URL content
		  url = new URL(urlString);		  
		  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		 
		  boolean redirect = false;
		  // normally, 3xx is redirect
		  int status = conn.getResponseCode();
		  if (status != HttpURLConnection.HTTP_OK) {
			  if (status == HttpURLConnection.HTTP_MOVED_TEMP
					  || status == HttpURLConnection.HTTP_MOVED_PERM
					  || status == HttpURLConnection.HTTP_SEE_OTHER)
				  redirect = true;
		  }

		  if (redirect) {
			  // get redirect url from "location" header field
			  String newUrl = conn.getHeaderField("Location");
			  // get the cookie if need, for login
			  //String cookies = conn.getHeaderField("Set-Cookie");

			  // open the new connnection again
			  conn = (HttpURLConnection) new URL(newUrl).openConnection();
		  }

		  // open the stream and put it into BufferedReader
		  BufferedReader br = new BufferedReader(
				  new InputStreamReader(conn.getInputStream()));

		  String inputLine;
		  //save to this filename
		  String filename = tmpDir + File.separator + urlString.substring(urlString.lastIndexOf(File.separator)+1); 
		  file = new File(filename);

		  if (!file.exists()) {
			  file.createNewFile();
		  }

		  //use FileWriter to write file
		  FileWriter fw = new FileWriter(file.getAbsoluteFile());
		  BufferedWriter bw = new BufferedWriter(fw);

		  while ((inputLine = br.readLine()) != null) {
			  bw.write(inputLine);
		  }

		  bw.close();
		  br.close();

	  } catch (MalformedURLException e) {
		  e.printStackTrace();
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
	  return file;
  }
  
  private File getFileURLContent(String tmpDir, String urlString) {
	  URL url = null;
	  File file = null;
	  try {
		  // get URL content
		  url = new URL(urlString);
		  URLConnection conn = url.openConnection();
			 
		  //System.out.println("Request URL ... " + url);
		  // open the stream and put it into BufferedReader
		  BufferedReader br = new BufferedReader(
				  new InputStreamReader(conn.getInputStream()));

		  String inputLine;
		  //save to this filename
		  String filename = tmpDir + File.separator + urlString.substring(urlString.lastIndexOf(File.separator)+1); 
		  //System.out.println("local path = " + filename);
		  file = new File(filename);

		  if (!file.exists()) {
			  file.createNewFile();
		  }

		  //use FileWriter to write file
		  FileWriter fw = new FileWriter(file.getAbsoluteFile());
		  BufferedWriter bw = new BufferedWriter(fw);

		  while ((inputLine = br.readLine()) != null) {
			  bw.write(inputLine + "\r\n");
		  }

		  bw.close();
		  br.close();

		  //System.out.println("Done");

	  } catch (MalformedURLException e) {
		  e.printStackTrace();
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
	  return file;
  }
}
