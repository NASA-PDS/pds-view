// Copyright 2002-2012 California Institute of Technology.
// ALL RIGHTS RESERVED. U.S. Government Sponsorship acknowledged.
//
// $Id$

package jpl.pds.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jpl.eda.xmlquery.XMLQuery;
import junit.framework.TestCase;

/**
 * Abstract test case for file query handlers.
 *
 * @author Kelly.
 */
abstract class FileHandlerTestCase extends TestCase {
  /**
   * Creates a new <code>FileHandlerTestCase</code> instance.
   *
   * @param name a <code>String</code> value.
   */
  protected FileHandlerTestCase(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    super.setUp();

    oldValue = System.getProperty(FileQueryHandler.PRODUCT_DIR_PROPERTY);
    tempDir = File.createTempFile("fqh", ".d");
    tempDir.delete();
    tempDir.mkdir();
    System.setProperty(FileQueryHandler.PRODUCT_DIR_PROPERTY, tempDir.toString());
    labelFile = new File(tempDir, LABEL_FILE_NAME);

    int n;
    byte[] buf = new byte[512];
    testFiles = new File[TEST_FILES.length];
    for (int i = 0; i < TEST_FILES.length; ++i) {
      InputStream resource = getClass().getResourceAsStream("/" + TEST_FILES[i]);
      if (resource == null) throw new IOException("/" + TEST_FILES[i] + " cannot be found");
      InputStream in = new BufferedInputStream(resource);
      testFiles[i] = new File(tempDir, TEST_FILES[i]);
      OutputStream out = new BufferedOutputStream(new FileOutputStream(testFiles[i]));
      while ((n = in.read(buf)) != -1)
        out.write(buf, 0, n);
      in.close();
      out.close();
    }
  } 

  public void tearDown() throws Exception {
    for (int i = 0; i < TEST_FILES.length; ++i)
      new File(tempDir, TEST_FILES[i]).delete();
    tempDir.delete();
    if (oldValue == null)
      System.getProperties().remove(FileQueryHandler.PRODUCT_DIR_PROPERTY);
    else
      System.setProperty(FileQueryHandler.PRODUCT_DIR_PROPERTY, oldValue);

    super.tearDown();
  }

  /**
   * Create an XML query for a named file.
   *
   * @param filename Name of file.
   * @return a <code>XMLQuery</code> value.
   */
  protected XMLQuery createQuery(String filename) {
    return new XMLQuery("ONLINE_FILE_SPECIFICATION_NAME = " + filename, null, null, null, null, null, null,
      null, 100, null);
  }

  /** Old value of the PRODUCT_DIR property. */
  private String oldValue;

  /** Temporary product dir for testing. */ 
  protected File tempDir;

  /** Temporary label file for testing. */
  protected File labelFile;

  /** Temporary test data files for testing. */
  protected File[] testFiles;

  /** Name of label file. */
  protected static final String LABEL_FILE_NAME = "pds.lbl";

  /** Name of test files. */
  protected static final String[] TEST_FILES = { "raw.dat", "pds.lbl", "img.jpg" };
}
