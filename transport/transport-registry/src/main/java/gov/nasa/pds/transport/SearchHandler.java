// Copyright 2012-2013, by the California Institute of Technology.
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
// $Id: PDS4Search.java 12759 2014-02-27 21:02:33Z hyunlee $

package gov.nasa.pds.transport;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.client.solrj.SolrRequest.METHOD.*;

import java.net.MalformedURLException;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
//import java.util.Arrays;
import java.util.Map;
import java.util.Collection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * This class is used by the PDS data set view web interface to retrieve values
 * for building the search parameter pull-down lists.
 * 
 * @author Hyun Lee
 */
public class SearchHandler {

	static String solrServerUrl = "http://pdsdev.jpl.nasa.gov:8080/search-service/";
	
	/**
	 * Constructor.
	 */
	public SearchHandler(String url) {
		this.solrServerUrl = url;
		//System.out.println("solServerUrl = "   + this.solrServerUrl);
	}
	
	public List<String> getValues(SolrDocument doc, String key) {
		Collection<Object> values = doc.getFieldValues(key);
		
		if (values==null || values.size()==0) {
			//System.out.println("key = " + key + "   values = " + values);
			return null;
		}
		
		List<String> results = new ArrayList<String>();
		for (Object obj: values) {
			
			if (obj instanceof java.util.Date) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
				df.setTimeZone(TimeZone.getTimeZone("GMT"));
				String dateValue = df.format(obj);
				results.add(dateValue);
				//System.out.println("key = " + key + "   date = " + obj.toString() + "  string date = " + dateValue);
			}
			else {
				results.add((String)obj);
				//System.out.println("key = " + key +  "   obj = " + (String)obj);
			}
		}
		return results;		
	}
	
	public List<SolrDocument> getSolrDocument(String identifier) 
			throws MalformedURLException, SolrServerException, IOException {	    
		SolrServer solr = new HttpSolrServer(solrServerUrl);
		
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("q", "identifier:"+identifier);
		params.set("indent", "on");
		params.set("wt", "xml");

		//System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
		//QueryResponse response = solr.query(params);
		
		SolrDocumentList solrResults = response.getResults();
		//System.out.println("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		List<SolrDocument> solrDocs = new ArrayList<SolrDocument>();
		int idx = 0;
		while (itr.hasNext()) {
			doc = itr.next();
			//System.out.println("*****************  idx = " + (idx++));

			/*
			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey() + "       Value = " + entry.getValue());
			}
			*/
			solrDocs.add(doc);
		}
		return solrDocs;
	}
	
	/**
	 * Command line invocation.
	 * 
	 * @param argv
	 *            Command-line arguments.
	 */
	public static void main(String[] argv) {
		try {
			SearchHandler searchHandler;

			if (argv.length == 1)
				searchHandler = new SearchHandler(argv[0]);
			else
				searchHandler = new SearchHandler(
						"http://pdsbeta.jpl.nasa.gov:8080/search-service");

			searchHandler.getSolrDocument("urn:nasa:pds:ladee_nms:data:nms_raw_msg_36246_20131224_170627");
		} catch (Exception ex) {
			System.err.println("Exception " + ex.getClass().getName() + ": "
					+ ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
