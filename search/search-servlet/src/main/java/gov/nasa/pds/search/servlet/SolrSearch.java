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

import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrRequest.METHOD.*;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;

public class SolrSearch {

  /** Setup the logger. */
  private static Logger LOG = Logger.getLogger(SolrSearch.class.getName());

  /** URL for accessing the Search Service. */
  static String solrServerUrl;

  /**
   * Constructor for the Solr search.
   */
  public SolrSearch(String url) {
    this.solrServerUrl = url;
    LOG.info("Solr Server URL: " + url);
  }

  /**
   * Search method for querying the Solr Service and returning a list
   * of Solr Documents.
   */
  public SolrDocumentList search(String query, int start, int rows) throws MalformedURLException, SolrServerException {
    SolrServer solr = new HttpSolrServer(solrServerUrl);
    ModifiableSolrParams params = new ModifiableSolrParams();
    params.add("q", query);
    params.set("start", start);
    params.set("rows", rows);
    params.set("wt", "xml");

    QueryResponse response = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

    if (response == null) {
      return null;
    }

    SolrDocumentList docList = response.getResults();
    return docList;
  }

  /**
   * Command-line invocation.
   * 
   * @param argv Command-line arguments.
   */
  public static void main(String[] argv) {
    try {
      SolrSearch solrSearch = null;

      if (argv.length == 1) {
        solrSearch = new SolrSearch(argv[0]);
      } else
        solrSearch = new SolrSearch("http://localhost:8080/search-service");

      SolrDocumentList docList = solrSearch.search("product_class:Product_Observational", 0, 50);
      for (SolrDocument doc : docList) {
        LOG.info("identifier: " + doc.getFirstValue("identifier"));
      }
    } catch (Exception e) {
      System.err.println("Exception " + e.getClass().getName() + ": " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    System.exit(0);
  }
}