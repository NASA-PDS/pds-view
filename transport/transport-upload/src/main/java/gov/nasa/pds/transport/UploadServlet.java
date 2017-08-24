// Copyright 2016-2017, by the California Institute of Technology.
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import gov.nasa.pds.email.SendEmail;

/**
 * Servlet that supports uploading a file. Based on the Java 6 EE
 * file upload servlet example.
 *
 * @author shardman
 */
@WebServlet(name = "UploadServlet", urlPatterns = {"/transport-upload"})
@MultipartConfig
public class UploadServlet extends HttpServlet {
  /** Setup the logger. */
  private final static Logger LOGGER = Logger.getLogger(UploadServlet.class.getCanonicalName());
  /** The path for storing the uploaded files. */
  private String repositoryPath;
  /** Email related variables. */
  private String emailFlag;
  private String emailFrom;
  private String emailTo;
  private String emailSubject;

  /**
   * Constructor for the upload servlet.
   */
  public UploadServlet() {}

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Servlet that uploads files to a server-configured destination.";
  }

  /**
   * Initialize the servlet.
   *
   * @param servletConfig The servlet configuration.
   * @throws ServletException If an error occurs.
  */
  public void init(ServletConfig servletConfig) throws ServletException {
    // Grab the repositoryPath parameter from the servlet config.
    repositoryPath = servletConfig.getInitParameter("repositoryPath");
    if (repositoryPath == null) {
      repositoryPath = "/tmp";
    }

    // Grab the emailFlag parameter from the servlet config.
    emailFlag = servletConfig.getInitParameter("emailFlag");
    if (emailFlag == null) {
      emailFlag = "False";
    }

    // Grab the other email parameters from the servlet config.
    if (emailFlag.equals("True")) {
      emailFrom = servletConfig.getInitParameter("emailFrom");
      if (emailFrom == null) {
        emailFrom = "pds_operator@jpl.nasa.gov";
      }
      emailTo = servletConfig.getInitParameter("emailTo");
      if (emailTo == null) {
        emailTo = "pds_operator@jpl.nasa.gov";
      }
      emailSubject = servletConfig.getInitParameter("emailSubject");
      if (emailSubject == null) {
        emailSubject = "File Uploaded";
      }
    }
  }

  /**
   * Handles the HTTP <code>GET</code> method. This method returns an 
   * exception since only <code>POST</code> requests are allowed.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    throw new ServletException("The UploadServlet only supports POST requests.");
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");

    // Create file components to save the file.
    Part filePart = request.getPart("file");
    if (filePart == null) {
      throw new ServletException("The file name for the file to upload was not provided in the request.");
    }
    String fileName = getFileName(filePart);

    // Determine the output file specification.
    File fileSpec = null;
    File baseDir = new File(repositoryPath);
    String path = request.getParameter("path");
    if (path != null) {
      if (path.contains("..")) {
        throw new ServletException("The path specified contains an invalid directory.");
      }
      File subDir = new File(baseDir, path);
      if (!subDir.exists()) {
        subDir.mkdirs();
      }
      fileSpec = new File(subDir, fileName);
    } else {
      fileSpec = new File(baseDir, fileName);
    }

    if (fileSpec.exists()) {
      throw new ServletException("The file and path specified already exist on the server.");
    }

    // Open the target file, read the bytes and write out the file.
    OutputStream out = null;
    InputStream filecontent = null;
    final PrintWriter writer = response.getWriter();
    try {
      out = new FileOutputStream(fileSpec);
      filecontent = filePart.getInputStream();

      int read = 0;
      final byte[] bytes = new byte[1024];

      while ((read = filecontent.read(bytes)) != -1) {
        out.write(bytes, 0, read);
      }
      String message = "File " + fileName + " uploaded to " + fileSpec.getPath() + ".";
      writer.println(message);
      LOGGER.log(Level.INFO, message);

    } catch (FileNotFoundException e) {
      String message = "Problems during file upload. Error: " + e.getMessage();
      LOGGER.log(Level.SEVERE, message);
      response.sendError(response.SC_INTERNAL_SERVER_ERROR, message);
    } finally {
      if (out != null) {
        out.close();
      }
      if (filecontent != null) {
        filecontent.close();
      }
      if (writer != null) {
        writer.close();
      }
    }

    // Send an email about the file upload.
    try {
      if (emailFlag.equals("True")) {
        // Use the default localhost:25 SMTP server.
        SendEmail sm = new SendEmail();
        sm.setFrom(emailFrom);
        sm.send(emailTo, emailSubject, "File " + fileName + " uploaded to " + fileSpec.getPath() + ".");
      }
    } catch (Exception e) {
      String message = "An error occurred sending an email: " + e.getMessage();
      writer.println(message);
      LOGGER.log(Level.WARNING, message);
    }
  }

  /**
   * Extract the file name from the file part.
   */
  private String getFileName(final Part part) {
    final String partHeader = part.getHeader("content-disposition");
    for (String content : part.getHeader("content-disposition").split(";")) {
      if (content.trim().startsWith("filename")) {
        return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
      }
    }
    return null;
  }
}
