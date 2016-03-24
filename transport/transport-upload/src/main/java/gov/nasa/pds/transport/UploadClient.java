// Copyright 2016, by the California Institute of Technology.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeTypes;

/**
 * Client access to a upload servlet. Several of the methods and possibly
 * classes used below are deprecated after version 4.0.3 of the 
 * Apache HttpClient package.
 *
 * @author shardman
 */
public class UploadClient {
  /** Setup the logger. */
  private final static Logger LOGGER = Logger.getLogger(UploadClient.class.getCanonicalName());
  /** The URL for accessing the Upload service. */
  private String serverUrl;
  /** Initialize the Tika mime type detector. */
  private static final Detector DETECTOR = new DefaultDetector(MimeTypes.getDefaultMimeTypes());

  /**
   * Constructor for the UploadClient.
   *
   * @param serverUrl The URL for accessing the Upload service.
   */
  public UploadClient(String serverUrl) {
    this.serverUrl = serverUrl;
  }

  /**
   * Upload a file represented as an InputStream to the upload service.
   *
   * @param fileName The name of the file to upload.
   * @param inputStream The input stream for the file to upload.
   * @param path The sub-directory to write the file to on the server.
   * @throws IOException if an I/O error occurs
   */
  public void upload(String fileName, InputStream inputStream, String path) throws IOException {
    try {
      // Initialize the HTTP client.
      HttpClient httpclient = new DefaultHttpClient();
      httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
      HttpPost httppost = new HttpPost(serverUrl);

      // Determine mime type.
      TikaInputStream tikaIS = TikaInputStream.get(inputStream);
      final Metadata metadata = new Metadata();
      metadata.set(Metadata.RESOURCE_NAME_KEY, fileName);
      String mimeType = DETECTOR.detect(tikaIS, metadata).toString();

      // Setup the multi-part request and send the file.
      MultipartEntity mpEntity = new MultipartEntity();
      if (path != null || !path.isEmpty()) {
        mpEntity.addPart("path", new StringBody(path));
      }
      ContentBody cbBody = new InputStreamBody(inputStream, mimeType, fileName);
      mpEntity.addPart("file", cbBody);
      httppost.setEntity(mpEntity);
      HttpResponse response = httpclient.execute(httppost);
      HttpEntity resEntity = response.getEntity();

      // Report the returned status.
      StatusLine statusLine = response.getStatusLine();
      String status = Integer.toString(statusLine.getStatusCode());
      Level level = Level.INFO;
      if (status.startsWith("1") || status.startsWith("2")) {
        level = Level.INFO;
      } else if (status.startsWith("3")) {
        level = Level.WARNING;
      } else if (status.startsWith("4") || status.startsWith("5")) {
        level = Level.SEVERE;
      }
      LOGGER.log(level, statusLine.toString());
      if (resEntity != null) {
        LOGGER.log(level, EntityUtils.toString(resEntity));
      }

      // Close the connection.
      if (resEntity != null) {
        resEntity.consumeContent();
      }
      httpclient.getConnectionManager().shutdown();
    } catch (IOException e) {
      throw e;
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
  }

  /**
   * Command-line invocation.
   * 
   * @param argv Command-line arguments.
   */
  public static void main(String[] argv) {
    try {
      // Handle the file specification and server URL arguments.
      String fileSpec = "";
      String path = "";
      String serverUrl = "";
      if (argv.length == 3) {
        fileSpec = argv[0];
        path = argv[1];
        serverUrl = argv[2];
      } else if (argv.length == 2) {
        fileSpec = argv[0];
        path = argv[1];
        serverUrl = "http://localhost:8080/transport-upload/upload";
      } else if (argv.length == 1) {
        fileSpec = argv[0];
        serverUrl = "http://localhost:8080/transport-upload/upload";
      } else {
        LOGGER.log(Level.INFO, "Execute as follows: java gov.nasa.pds.transport.UpdateClient <fileSpec> [<path>[, <serverUrl>}]");
        System.exit(1);
      }

      // Upload the specified file.
      LOGGER.log(Level.INFO, "Uploading file " + fileSpec + " to server " + serverUrl + ".");
      UploadClient uploadClient = new UploadClient(serverUrl);
      File file = new File(fileSpec);
      uploadClient.upload(file.getName(), new FileInputStream(file), path);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Exception " + e.getClass().getName() + ": " + e.getMessage());
      System.exit(1);
    }
    System.exit(0);
  }
}