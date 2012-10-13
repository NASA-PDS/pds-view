// Copyright 2002-2012 California Institute of Technology.
// ALL RIGHTS RESERVED. U.S. Government Sponsorship acknowledged.
//
// $Id$

package jpl.pds.server;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import jpl.eda.xmlquery.*;
import jpl.eda.product.LargeProductQueryHandler;
import jpl.eda.product.ProductException;
import java.util.Map;
import java.util.HashMap;

/** Handle product queries for files from the PDS jukebox.
 *
 * This class is a product server query handler that accepts queries of the form
 * ONLINE_FILE_SPECIFICATION_NAME = some/file and RETURN_TYPE = something.
 *
 * @author Kelly.
 */
public class FileQueryHandler implements LargeProductQueryHandler {
  public FileQueryHandler() {
    handlers.put("DIRLIST", new DirListHandler());
    handlers.put("DIRLIST1", new DirListHandler());
    handlers.put("DIRFILELIST", new DirFileListHandler());
    handlers.put("DIRFILELIST1", new DirFileListHandler());
    handlers.put("FILELIST", new FileListHandler());
    handlers.put("FILELISTZIP", new FileListHandler());
    handlers.put("PDS_FILELIST", new FileListHandler());
    handlers.put("PDS_FILELISTZIP", new FileListHandler());
    handlers.put("PDS_ZIP", new ZipFileHandler());
    handlers.put("PDS_ZIP_SIZE", new ZipFileHandler());
    handlers.put("PDS_ZIPN", new ZipNFileHandler());
    handlers.put("PDS_ZIPN_SIZE", new ZipNFileHandler());
    handlers.put("PDS_ZIPN_TES", new ZipTESFileHandler());
    handlers.put("PDS_ZIPN_TES_SIZE", new ZipTESFileHandler());
    handlers.put("PDS_ZIPD", new ZipDFileHandler());
    handlers.put("PDS_ZIPD_SIZE", new ZipDFileHandler());
    handlers.put("PDS_JPEG", new JPEGFileHandler());
    handlers.put("PDS_JPEG_SIZE", new JPEGFileHandler());
    handlers.put("PDS_LABEL", new LabelQueryHandler());
    handlers.put("RAW", defaultHandler);
    handlers.put("RAW_SIZE", defaultHandler);

    try {
      String accessorName = System.getProperty(ACCESSOR_PROPERTY);
      if (accessorName != null) {
        Class clazz = Class.forName(accessorName);
        fileAccessor = (FileAccessor) clazz.newInstance();
      } else
        fileAccessor = new StdFileAccessor();
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new IllegalStateException("Could not create file accessor: " + ex.getClass().getName() + ": "
        + ex.getMessage());
    }
  }

  public XMLQuery query(XMLQuery query) {
    // We handle queries of the form ONLINE_FILE_SPECIFICATION_NAME =
    // some/file, so make sure there's at least three WHERE set elements, and
    // that they're an elemName of "ONLINE_FILE_SPECIFICATION_NAME", a literal
    // (with the filename), and a RELOP of "EQ".

    List where = query.getWhereElementSet();
    if (where.size() < 3) {
      System.err.println("FileQueryHandler received non-ONLINE_FILE_SPECIFICATION_NAME query");
      return query;
    }     
    String filename = null;
    ArrayList filenameList = new ArrayList();
    boolean gotFile = false;
    boolean gotFileElement = false; 
    boolean gotReturnElement = false; 
    boolean gotZipNameElement = false; 
    boolean gotOp = false;
    boolean gotReturnType = false;
    boolean gotZipName = false;
    String return_type = null;
    String zipName = null;
    String optArg = null;

    for (Iterator i = where.iterator(); i.hasNext();) {
      QueryElement queryElement = (QueryElement) i.next();
      if (queryElement.getRole().equals("LITERAL")) {
        // We pop of stack so value will be traversed after element
        if ((gotFile!=true) && (gotFileElement==true)) {
          filenameList.add(queryElement.getValue());
          gotFile = true;
        } else if ((gotReturnType!=true) && (gotReturnElement==true)) {
          return_type = queryElement.getValue();
          gotReturnType = true;
        } else if ((gotZipName!=true) && (gotZipNameElement==true)) {
          zipName = queryElement.getValue();
          gotZipName = true;
        }
      } else if (queryElement.getRole().equals("elemName")
        && (queryElement.getValue().equals("ONLINE_FILE_SPECIFICATION_NAME")
        || queryElement.getValue().equals("OFSN"))) {
        if (gotFileElement == true) gotFile = false;  // Looking for another filename 
        gotFileElement = true;
      } else if (queryElement.getRole().equals("elemName")
        && (queryElement.getValue().equals("RETURN_TYPE")
        || queryElement.getValue().equals("RT"))) {
        gotReturnElement = true;
      } else if (queryElement.getRole().equals("RELOP") && queryElement.getValue().equals("EQ")) {
        gotOp = true;
      } else if (queryElement.getRole().equals("elemName")
        && (queryElement.getValue().equals("ZIP_FILE_NAME")
        || queryElement.getValue().equals("ZFN"))) {
        gotZipNameElement = true;
      }
    }

    if (!gotFile || !gotFileElement || !gotOp || !gotReturnType) {
      System.err.println("FileQueryHandler received non-ONLINE_FILE_SPECIFICATION_NAME query");
      System.err.println("Query object is " + query);
      return query;
    }

    if (filenameList.size() > 1 && !return_type.equals("DIRLIST") && !return_type.equals("DIRLIST1")
      && !return_type.equals("DIRFILELIST") && !return_type.equals("DIRFILELIST1")  
      && !return_type.equals("PDS_ZIPN") && !return_type.equals("PDS_ZIPN_SIZE")
      && !return_type.equals("PDS_ZIPD") && !return_type.equals("PDS_ZIPD_SIZE")) {
      System.err.println("FileQueryHandler - " + return_type + " does not support multiple OFSN query");
      System.err.println("Query object is " + query);
      return query;
    }

    if (gotZipName && !return_type.equals("PDS_ZIP") && !return_type.equals("PDS_ZIPN")
      && !return_type.equals("PDS_ZIPD")) { 
      System.err.println("FileQueryHandler only uses ZIP_FILE_NAME with ZIP products");
      System.err.println("Query object is " + query);
      return query;
    }

    if (gotZipName) {
      optArg = zipName;
    } else if (return_type.equals("PDS_ZIP") || return_type.equals("PDS_ZIPN")
      || return_type.equals("PDS_ZIPD") || return_type.equals("PDS_JPEG")) {
      // make each products.zip download filename unique
      DateFormat myformat = new SimpleDateFormat("yyyyMMddHHmmss");
      optArg = "products_" + myformat.format(new Date());
    } else {
      optArg = return_type;
    }

    // Okay, got the file names, now determine what to do. Perform a check
    // for '..' within any of the file names. Bail if we find them because
    // we don't anyone trying to traverse up the directory tree.
    try {
      File[] fileArray = new File[filenameList.size()];
      for (int i = 0; i<fileArray.length; i++) {
        filename = (String)filenameList.get(i);
        if (filename.contains("..")) {
          System.err.println("FileQueryHandler received non-ONLINE_FILE_SPECIFICATION_NAME query");
          return query;
        }
        fileArray[i] = fileAccessor.locateFile(filename);
      }
      FileQuerier handler = (FileQuerier) handlers.get(return_type);
      if (handler == null) handler = defaultHandler;
      return handler.queryForFile(query, fileArray, optArg);

    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      System.err.println("Exception " + ex.getClass().getName() + " querying for file " + filename + ": "
        + ex.getMessage());
      ex.printStackTrace();
      return query;
    }
  }

  public byte[] retrieveChunk(String id, long offset, int length) throws ProductException {
    try {
      for (Iterator i = handlers.values().iterator(); i.hasNext();) {
        FileQuerier q = (FileQuerier) i.next();
        byte[] rc = q.retrieveChunk(id, offset, length);
        if (rc != null) return rc;
      }
      throw new ProductException(id + " is an invalid chunked product ID");
    } catch (IOException ex) {
      throw new ProductException(ex);
    }
  }

  public void close(String id) throws ProductException {
    try {
      for (Iterator i = handlers.values().iterator(); i.hasNext();) {
        FileQuerier q = (FileQuerier) i.next();
        q.close(id);
      }
    } catch (IOException ex) {
      throw new ProductException(ex);
    }
  }


  /** All handlers, including the raw file handler. */
  private Map handlers = new HashMap();

  /** File accessor to use to locate files. */
  private FileAccessor fileAccessor;

  /** Default handler is the raw file handler. */
  private RawFileHandler defaultHandler = new RawFileHandler();

  /** Name of the property that specifies where products are kept. */
  public static final String PRODUCT_DIR_PROPERTY = "jpl.pds.server.FileQueryHandler.productDir";

  /** Name of the property that specifies what file accessor to use. */
  public static final String ACCESSOR_PROPERTY = "jpl.pds.server.accessor";

  /**
   * Command-line driver.
   *
   * @param argv Command-line arguments.
   */
  public static void main(String[] argv) {
    if (argv.length != 1) {
      System.err.println("Usage: filename");
      System.exit(1);
    }

    XMLQuery query = new XMLQuery(argv[0], /*id*/"cli-1",
      /*title*/"Command-line Query",
      /*desc*/"This query came from the command-line and is directed to the FileQueryHandler for the return type",
      /*ddId*/null, /*resultModeId*/null, /*propType*/null, /*propLevels*/null,
      XMLQuery.DEFAULT_MAX_RESULTS, null);
    FileQueryHandler handler = new FileQueryHandler();
    handler.query(query);
    System.out.println(query.getXMLDocString());
    System.exit(0);
  }
}


