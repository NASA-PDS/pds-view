// Copyright 2012-2017, by the California Institute of Technology.
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

package gov.nasa.pds.dsview.registry;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.client.solrj.SolrRequest.METHOD.*;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Map;
import java.util.Collection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

//import gov.nasa.pds.dsview.registry.SearchRegistry;
//import gov.nasa.pds.registry.model.ExtrinsicObject;

/**
 * This class is used by the PDS data set view web interface to retrieve values
 * for building the search parameter pull-down lists.
 * 
 * @author Hyun Lee
 */
public class PDS4Search {

	static String solrServerUrl = "http://pdsdev.jpl.nasa.gov:8080/search-service/";
	
	/**
	 * Constructor.
	 */
	public PDS4Search(String url) {
		this.solrServerUrl = url;
	}

	public SolrDocumentList getCollections() throws MalformedURLException,
			SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "");
		params.set("wt", "xml");
		//params.set("fq", "facet_object_type:\"1,product_collection\"");
		params.set("fq", "facet_type:\"1,product_collection\"");
		
		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		if (response==null) 
			return null;
		
		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return solrResults;
	}

	public SolrDocumentList getBundles() throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "");
		params.set("wt", "xml");
		//params.set("fq", "facet_object_type:\"1,product_bundle\"");
		params.set("fq", "facet_type:\"1,product_bundle\"");
		//params.set("start", start);
		
		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return solrResults;
	}

	public SolrDocumentList getObservationals(int start) throws MalformedURLException,
	SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);
		
		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "");
		params.set("wt", "xml");
		//params.set("fq", "facet_object_type:\"1,product_observational\"");
		params.set("fq", "facet_type:\"1,product_observational\"");
		params.set("start", start);

		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		if (response==null) 
			return null;

		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());

		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return solrResults;
	}

	public SolrDocumentList getDocuments() throws MalformedURLException,
			SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "");
		params.set("wt", "xml");
		//params.set("fq", "facet_object_type:\"1,product_document\"");
		params.set("fq", "facet_type:\"1,product_document\"");
		
		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		if (response==null) 
			return null;
		
		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return solrResults;
	}
	
	public SolrDocument getContext(String identifier) throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);
		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "identifier:"+identifier);
		params.set("indent", "on");
		params.set("wt", "xml");

		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		while (itr.hasNext()) {
			doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return doc;
	}
/*	
	public SolrDocument getContext(String facet_type, String identifier) throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "identifier:"+identifier);
		params.set("indent", "on");
		params.set("wt", "xml");
		if (facet_type!=null)
			params.set("fq", "facet_type:\"1," + facet_type + "\"");

		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		while (itr.hasNext()) {
			doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return doc;
	}
*/	
	public List<String> getValues(SolrDocument doc, String key) {
		Collection<Object> values = doc.getFieldValues(key);
		
		if (values==null || values.size()==0) {
			//System.out.println("key = " + key + "   values = " + values);
			return null;
		}
		
		List<String> results = new ArrayList<String>();
		for (Object obj: values) {
			//System.out.println("obj = " + (String)obj);
			if (obj instanceof java.util.Date) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
				df.setTimeZone(TimeZone.getTimeZone("GMT"));
				String dateValue = df.format(obj);
				results.add(dateValue);
				System.out.println("date = " + obj.toString() + "  string date = " + dateValue);
			}
			else 
				results.add((String)obj);
		}
		return results;		
	}
	
	/**
	 * Command line invocation.
	 * 
	 * @param argv
	 *            Command-line arguments.
	 */
	public static void main(String[] argv) {
		try {
			PDS4Search pds4Search;

			if (argv.length == 1)
				pds4Search = new PDS4Search(argv[0]);
			else
				pds4Search = new PDS4Search(
						"http://pdsbeta.jpl.nasa.gov/tools/search-service/pds/");

			pds4Search.getCollections();
			pds4Search.getBundles();
			pds4Search.getContext("urn:nasa:pds:context:investigation:investigation.PHOENIX");

			// sparms.getSearchResult("mission:cassini-huygens and target:Callisto");
		} catch (Exception ex) {
			System.err.println("Exception " + ex.getClass().getName() + ": "
					+ ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
