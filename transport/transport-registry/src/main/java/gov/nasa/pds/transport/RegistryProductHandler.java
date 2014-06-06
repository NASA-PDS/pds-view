// Copyright 2013, by the California Institute of Technology.
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
// $Id$

package gov.nasa.pds.transport;

import gov.nasa.pds.registry.client.results.RegistryHandler;
import gov.nasa.pds.registry.model.wrapper.ExtendedExtrinsicObject;

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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.oodt.product.LargeProductQueryHandler;
import org.apache.oodt.product.ProductException;
import org.apache.oodt.xmlquery.LargeResult;
import org.apache.oodt.xmlquery.QueryElement;
import org.apache.oodt.xmlquery.XMLQuery;
import org.apache.tika.Tika;

/**
 * This Apache OODT Product Server handler accepts a query containing one
 * or more Registry Service identifiers, queries the Registry Service for 
 * the associated files and returns a package containing those files back 
 * to the calling application.
 *
 * @author shardman
 * @author luca
 * @version $Revision$
 */
public class RegistryProductHandler implements LargeProductQueryHandler {
	
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
    String registryUrl = System.getProperty("gov.nasa.pds.transport.RegistryProductHandler.registryUrl", "http://localhost:8080/registry");
    String[] registryUrls = registryUrl.split("\\s*,\\s*");
    for (String _registryUrl : registryUrls) {
    	System.out.println("Using PDS Registry URL: "+_registryUrl);
    }
    String tmpDir = System.getProperty("gov.nasa.pds.transport.RegistryProductHandler.tmpDir", "/tmp");

    // Setup the registry list and initialize the registry handler.
    ArrayList<String> primaryRegistries = new ArrayList<String>();
    primaryRegistries.addAll(Arrays.asList(registryUrls));
    ArrayList<String> secondaryRegistries = new ArrayList<String>();
    RegistryHandler registryHandler = new RegistryHandler(primaryRegistries, secondaryRegistries, RegistryHandler.DFLT_QUERY_MAX, false);

    // Get the package format if specified in the query.
    String archivePackage = extractFieldFromQuery(q, "package");
    if (archivePackage == null) {
      archivePackage = "ZIP";
    } else if ((!archivePackage.equalsIgnoreCase("ZIP")) && (!archivePackage.equalsIgnoreCase("TGZ"))) {
      throw new ProductException("Invalid package type specified.");
    }
    archivePackage = archivePackage.toUpperCase();

    // Get all of the identifiers passed in the query.
    List<String> identifiers = extractIdentifiersFromQuery(q);
    if (identifiers.size()==0) throw new ProductException("Error: product identifier(s) not specified in request.");

    // Get the files for each product identified by the identifier and
    // add them to the list.
    for (String identifier : identifiers) {
      try {
        // Search for the requested product and get the associated 
        // Product_File_Repository products that have the file information.
    	ExtendedExtrinsicObject extrinsic = registryHandler.getExtrinsicByLidvid(identifier);
        List<ExtendedExtrinsicObject> fileRefList = registryHandler.getAssociationsBySourceObject(extrinsic, "file_ref");

        // Get the file information for each associated file product and add
        // it to the list of files.
        for (ExtendedExtrinsicObject fileRef : fileRefList) {
          List<String> fileLocation = fileRef.getSlotValues("file_location");
          List<String> fileName = fileRef.getSlotValues("file_name");
          File file = new File(fileLocation.get(0), fileName.get(0));
          files.add(file);
          // use existing file checksum, or compute anew
          if (fileRef.getSlot("md5_checksum")!=null) {
        	  checksums.add( fileRef.getSlotValues("md5_checksum").get(0) );
          } else {
          	  String checksum = MD5Checksum.getMD5Checksum(file.getAbsolutePath()) ;
        	  checksums.add( checksum );
          }
        }
      } catch (Exception e) {
        String message = e.getMessage();
        if (message == null) {
          throw new ProductException("Error querying the registry service or no products found.");
        } else {
          throw new ProductException("Error querying the registry service or no products found. Message: " + message);
        }
      }
    }
    
    // Create checksum manifest
    this.addChecksumManifest(files, checksums);

    // Build the archive package file.
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
  public void close(String id) throws ProductException {
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
    System.out.println("Entered buildTGZFile()");
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
  private void addChecksumManifest(List<File> files, List<String> checksums) throws ProductException {
	  
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
	    
  	} catch(IOException e) {
  		e.printStackTrace();
  		throw new ProductException(e.getMessage());
  	}
  		  
  }
  
}
