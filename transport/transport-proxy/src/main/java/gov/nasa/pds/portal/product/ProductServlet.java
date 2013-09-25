// Copyright 1999-2012, by the California Institute of Technology.
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
// This class was copied from version 3.0.11 of the grid-product package and 
// modified to remove dependencies on the early OODT infrastructure like 
// the RMI Registry.
//
// $Id$

package gov.nasa.pds.portal.product;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.NumberFormatException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jpl.eda.product.ProductException;
import jpl.eda.xmlquery.Result;
import jpl.eda.xmlquery.XMLQuery;

/**
 * Servlet that returns products in response to OODT queries.
 *
 * <p>This servlet accepts the following required parameters: 
 * <ul>
 *   <li><code>object</code>: URN of the Product Server to receive the 
 *   query.</li>
 *   <li><code>keywordQuery</code>: DIS-style query string.</li>
 * </ul>
 *
 * <p>It also takes the following optional parameters:
 * <ul>
 *   <li><code>id</code>: product ID to return in the case of multiple 
 *   products.</li>
 *   <li><code>mimeType</code>: desired MIME type of the product. This 
 *   parameter may be specified multiple times to show an in-order preference 
 *   for MIME types.</li>
 * </ul>
 *
 * <p>It responds with a single product in the MIME type of the product.
 *
 * @author Kelly
 */
public class ProductServlet extends HttpServlet {

  /** Setup the logger. */
  private static Logger LOG = Logger.getLogger(ProductServlet.class.getName());

  /** Preferred namespace prefixed to unqualified URNs. */
  private String preferredNamespace;

  /** Size of chunks to retrieve from product servers. */
  private int chunkSize;

  /** Object alias properties. */
  private Properties aliasProperties;

  /** MIME types commonly displayable by browsers. */
  private static final String[] DISPLAYABLE_TYPES = {
    "text/plain", "text/richtext", "text/enriched", "text/tab-separated-values", "text/html", "text/xml", "text/rtf",
    "message/rfc822", "message/partial", "message/external-body", "message/news", "message/http",
    "message/delivery-status", "message/disposition-notification", "message/s-http", "application/rtf",
    "application/pdf", "image/jpeg", "image/gif", "image/tiff", "image/png", "audio/basic", "audio/32kadpcm",
    "audio/mpeg", "video/mpeg", "video/quicktime"
  };

  /**
   * Constructor for the product servlet.
   */
  public ProductServlet() {}

  /**
   * Initialize the servlet.
   *
   * @param servletConfig The servlet configuration.
   * @throws ServletException If an error occurs.
  */
  public void init(ServletConfig servletConfig) throws ServletException {
    // Grab the aliasFileSpec parameter from the servlet config and load the
    // alias properties. If the file specification was not provided, just
    // load the properties file included in the WAR.
    try {
      String aliasFileSpec = servletConfig.getInitParameter("aliasFileSpec");
      aliasProperties = new Properties();
      if (aliasFileSpec == null) {
        aliasProperties.load(ProductServlet.class.getResourceAsStream("/aliases.properties"));
      } else {
        aliasProperties.load(new FileReader(aliasFileSpec));
      }
    } catch (FileNotFoundException ex) {
      throw new ServletException("The file specified in the \"aliasFileSpec\" servlet parameter was not found. " + ex.getMessage());
    } catch (IOException ex) {
      throw new ServletException("An error occurred reading the aliases properties file. " + ex.getMessage());
    }

    // Grab the preferredNamespace parameter from the servlet config.
    preferredNamespace = servletConfig.getInitParameter("preferredNamespace");
    if (preferredNamespace == null) {
      preferredNamespace = "urn:eda:rmi:";
    }

    // Grab the chunkSize parameter from the servlet config.
    try {
      String chunkSizeString = servletConfig.getInitParameter("chunkSize");
      if (chunkSizeString == null) {
        chunkSize = 8192;
      } else {
        chunkSize = new Integer(chunkSizeString).intValue();
      }
    } catch (NumberFormatException ex) {
      throw new ServletException("The value specified in the \"chunkSize\" servlet parameter is not a valid number. " + ex.getMessage());
    }

    super.init(servletConfig);
  }

  /**
   * Handle a GET request.
   *
   * @param req The request.
   * @param res The response.
   * @throws ServletException If an error occurs.
   * @throws IOException If an error occurs.
  */
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    doIt(req, res);
  }

  /**
   * Handle a GET request.
   *
   * @param req The request.
   * @param res The response.
   * @throws ServletException If an error occurs.
   * @throws IOException If an error occurs.
  */
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    doIt(req, res);
  }

  /**
   * Process the request for the query parameters, query, and deliver a product.
   *
   * @param req Servlet request.
   * @param res Servlet response.
   * @throws ServletException If there's something wrong with the request or response.
   * @throws IOException If there's an I/O error writing the image.
   */
  private void doIt(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    String remoteHost = req.getRemoteHost();
    try {
      // Grab the object parameter, prepend the URN namespace if necessary
      // and then get its URL mapped value from the properties.
      String object = req.getParameter("object");
      if (object == null) {
        throw new IllegalArgumentException("The \"object\" parameter is required and was not specified. The value for this parameter should be a URN matching a known Product Server.");
      }
      if (!object.startsWith("urn:")) {
        object = preferredNamespace + object;
      }
      String objectUrl = aliasProperties.getProperty(object);
      if (objectUrl == null) {
        throw new IllegalArgumentException("The value specified in the \"object\" parameter does not match a known Product Server.");
      }

      // Grab the keywordQuery parameter.
      String keywordQuery = req.getParameter("keywordQuery");
      if (keywordQuery == null) {
        throw new IllegalArgumentException("The \"keywordQuery\" parameter is required and was not specified. The value for this parameter should be a DIS-style query string.");
      }

      // Grab the id parameter.
      String id = req.getParameter("id");

      // Grab the mimeType parameter.
      String[] mimeAccept = req.getParameterValues("mimeType");
      if (mimeAccept == null) {
        mimeAccept = new String[]{"*/*"};
      }
      List mimeAcceptList = Arrays.asList(mimeAccept);

      // Construct the query from the parameters and send it in.
      XMLQuery xmlQuery = new XMLQuery(keywordQuery, "queryServlet", "Query from QueryServlet",
        "This query comes from the query servlet currently handling a client at "
        + remoteHost, /*ddID*/null, /*resultModeID*/null, /*propType*/null, /*propLevels*/null,
        XMLQuery.DEFAULT_MAX_RESULTS, mimeAcceptList);
      ProductClient pc = new ProductClient(objectUrl);
      xmlQuery = pc.query(xmlQuery);

      // Get the results of the query, if any.
      List results = xmlQuery.getResults();
      boolean found;
      long resultSize = 0L;
      if (results.isEmpty()) {
        res.sendError(HttpServletResponse.SC_NOT_FOUND, "No matching results to query \"" + keywordQuery
          + "\" for object \"" + object + "\"");
        found = false;
      } else {
        // Default to the first result.  Then look for a specific result, if specified.
        Result result = (Result) results.get(0);
        if (id != null) {
          for (Iterator i = results.iterator(); i.hasNext();) {
            Result r = (Result) i.next();
            if (id.equals(r.getID())) {
              result = r;
              break;
            }
          }
          throw new IllegalArgumentException("Result with ID " + id + " not in results list");
        }

        // Characterize.
        res.setContentType(result.getMimeType());
        if (!displayableByBrowser(result.getMimeType()))
          suggestFilename(result.getResourceID(), res);
        resultSize = result.getSize();
        if (resultSize <= Integer.MAX_VALUE)
          res.setContentLength((int) resultSize);

        // Deliver.
        BufferedInputStream productInputStream = null;
        try {
          res.setBufferSize(chunkSize);
          productInputStream = new BufferedInputStream(result.getInputStream());
          byte[] buf = new byte[chunkSize];
          int numRead;
          while ((numRead = productInputStream.read(buf)) != -1)
            res.getOutputStream().write(buf, 0, numRead);
          productInputStream.close();
          found = true;
        } finally {
          if (productInputStream != null) try {
            productInputStream.close();
          } catch (IOException ignore) {}
        }
      }
    } catch (IllegalArgumentException ex) {
      LOG.severe("Invalid argument passed by host \"" + remoteHost + "\": " + ex.getMessage());
      res.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
    } catch (ProductException ex) {
      LOG.severe("Product query from host \"" + remoteHost + "\" failed: " + ex.getMessage());
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Product query failed: " + ex.getMessage());
    }
  }

  /**
   * Tell if the given MIME type is generally displayable by the majority 
   * of web browsers.
   *
   * @param mimeType a <code>String</code> value
   * @return True if the type named by <var>mimeType</var> is generally displayable
   */
  private boolean displayableByBrowser(String mimeType) {
    for (int i = 0; i < DISPLAYABLE_TYPES.length; ++i)
      if (DISPLAYABLE_TYPES[i].equals(mimeType)) return true;
    return false;
  }

  /**
   * Add a header to suggest a filename to the browser.
   *
   * @param resourceID The name of the resource to suggest.
   * @param res The response in which to suggest the filename.
   */
  private void suggestFilename(String resourceID, HttpServletResponse res) {
    if (resourceID == null || resourceID.length() <= 0)
      resourceID = "product.dat";
    res.addHeader("Content-disposition", "attachment; filename=\"" + resourceID + "\"");
  }
}
