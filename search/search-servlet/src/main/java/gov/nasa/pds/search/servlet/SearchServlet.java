// Copyright 2014, by the California Institute of Technology.
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

package gov.nasa.pds.search.servlet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SearchServlet extends HttpServlet {

  /** Setup the logger. */
  private static Logger LOG = Logger.getLogger(SearchServlet.class.getName());

  /** URL for accessing the Search Service. */
  static String solrServerUrl;

  /**
   * Constructor for the search servlet.
   */
  public SearchServlet() {}

  /**
   * Initialize the servlet.
   *
   * It turns out that we don't need the solrServerUrl property for this
   * implementation, but I left the code in as an example.
   *
   * @param servletConfig The servlet configuration.
   * @throws ServletException
  */
  public void init(ServletConfig servletConfig) throws ServletException {

    // Grab the solrServerUrl parameter from the servlet config.
    solrServerUrl = servletConfig.getInitParameter("solrServerUrl");
    if (solrServerUrl == null) {
      solrServerUrl = "http://localhost:8080/search-service";
    }
    LOG.info("Solr Server URL: " + solrServerUrl);

    super.init(servletConfig);
  }

  /**
   * Handle a GET request.
   *
   * @param req The servlet request.
   * @param res The servlet response.
   * @throws ServletException
   * @throws IOException
  */
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    LOG.info("GET request received.");
    try {
      // Forward the query to the results page.
      LOG.info("Query: " + req.getQueryString());
      RequestDispatcher rd = req.getRequestDispatcher("/results.jsp?q=" + req.getQueryString());
      rd.forward(req, res);
    } catch (Exception e) {
      LOG.severe(e.getMessage());
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  /**
   * Handle a POST request.
   *
   * @param req The servlet request.
   * @param res The servlet response.
   * @throws ServletException
   * @throws IOException
  */
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    LOG.info("POST request received.");
    String startTime = null;
    String stopTime = null;
    StringBuffer query = new StringBuffer("");
    try {
      // Populate the query string with product type to return.
      query.append("product_class:Product_Observational AND ");

      // Populate the query string with the primary result purpose constraints.
      if (req.getParameter("primary_result_purpose") != null) {
        String[] values = req.getParameterValues("primary_result_purpose");
        for (int i = 0; i < values.length; i++) {
          if (i == 0){
            query.append("(primary_result_purpose:" + values[i]);
          } else {
            query.append(" OR primary_result_purpose:" + values[i]);
          }
        }
        query.append(") AND ");
      }

      // Validate the start and stop date/time entries.
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      if (req.getParameter("start_time") != null) {
        startTime = req.getParameter("start_time");
        if (startTime != "") {
          df.parse(startTime);
        }
      } else {
        startTime = "";
      }
      if (req.getParameter("stop_time") != null) {
        stopTime = req.getParameter("stop_time");
        if (stopTime != "") {
          df.parse(stopTime);
        }
      } else {
        stopTime = "";
      }

      // Populate the query string with start and stop date/time constraints.
      if (startTime != "" && stopTime != "") {
        query.append("(observation_start_date_time:[" + startTime + " TO " + stopTime + "] OR observation_stop_date_time:[" + startTime + " TO " + stopTime + "])");
      } else if (startTime == "" && stopTime != "") {
        query.append("(observation_start_date_time:[* TO " + stopTime + "])");
      } else if (startTime != "" && stopTime == "") {
        query.append("(observation_stop_date_time:[" + startTime + " TO *])");
      } else {
        // Start and stop date/time constraints were not supplied so remove
        // the trailing AND.
        if (query.length() > 0) {
          query.delete(query.length()-5, query.length());
        }
      }

      // No constraints were supplied by the calling application so query 
      // for all products.
      if (query.length() == 0) {
        query.append("*:*");
      }

      // Forward the query to the results page.
      LOG.info("Query: " + query);
      RequestDispatcher rd = req.getRequestDispatcher("/results.jsp?q=" + query.toString());
      rd.forward(req, res);
    } catch (ParseException e) {
      LOG.severe(e.getMessage());
      res.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    } catch (Exception e) {
      LOG.severe(e.getMessage());
      res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }
}
