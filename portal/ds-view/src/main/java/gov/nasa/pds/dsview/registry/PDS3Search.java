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

/**
 * This class is used by the PDS data set view web interface to retrieve values
 * for building the search parameter pull-down lists.
 * 
 * @author Hyun Lee
 */
public class PDS3Search {

	static String solrServerUrl = "http://pdsdev.jpl.nasa.gov:8080/search-service/";
	
	/**
	 * Constructor.
	 */
	public PDS3Search(String url) {
		this.solrServerUrl = url;
	}

	public SolrDocumentList getDataSetList() throws MalformedURLException,
			SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "pds_model_version:pds3");
		params.set("indent", "on");
		params.set("wt", "xml");
		//facet_object_type:"1,product_data_set_pds3"
		params.set("fq", "facet_type:\"1,data_set\"");

		//params.set("start", start);
		
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
	
	public SolrDocument getDataSet(String identifier) throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		//params.add("q", "pds_model_version:pds3 AND data_set_id:\""+identifier+"\"");
		params.add("q", "identifier:"+identifier);
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,data_set\"");

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
	
	public SolrDocument getMission(String identifier) throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		//params.add("q", "pds_model_version:pds3 AND investigation_name:\""+identifier+"\"");
		params.add("q", "pds_model_version:pds3 AND identifier:"+identifier);
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,investigation\"");

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

	public SolrDocument getInstHost(String identifier) throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		//params.add("q", "data_product_type:instrument_host AND pds_model_version:pds3 AND instrument_host_id:\""+identifier+"\"");
		params.add("q", "identifier:"+identifier);
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,instrument_host\"");

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
	
	public List<SolrDocument> getInst(String identifier) throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		//params.add("q", "pds_model_version:pds3 AND instrument_id:\""+identifier+"\"");
		params.add("q", "identifier:"+identifier);
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,instrument\"");

		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		List<SolrDocument> instDocs = new ArrayList<SolrDocument>();
		int idx = 0;
		while (itr.hasNext()) {
			doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
			instDocs.add(doc);
		}
		return instDocs;
	}
	
	public SolrDocument getInst(String instId, String instHostId) throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "instrument_id:\""+instId+
				"\" AND instrument_host_id:\""+instHostId+"\"");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,instrument\"");

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
	
	public SolrDocument getTarget(String identifier) throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "target_name:\""+identifier+"\" AND pds_model_version:pds3");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,target\"");

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
	
	public SolrDocument getResource(String identifier) throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "identifier:\""+identifier+"\"");
		params.set("indent", "on");
		params.set("wt", "xml");
		//params.set("fq", "facet_type:\"1,resource\"");

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
				System.out.println("key = " + key + "   date = " + obj.toString() + "  string date = " + dateValue);
			}
			else {
				results.add((String)obj);
				System.out.println("key = " + key +  "   obj = " + (String)obj);
			}
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
			PDS3Search pds3Search;

			if (argv.length == 1)
				pds3Search = new PDS3Search(argv[0]);
			else
				pds3Search = new PDS3Search(
						"http://pdsbeta.jpl.nasa.gov:8080/search-service");

			pds3Search.getDataSetList();
			//pds3Search.getContext("urn:nasa:pds:context:investigation:investigation.PHOENIX");

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
