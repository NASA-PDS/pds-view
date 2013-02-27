// This software was developed by the Object Oriented Data Technology task of the Science
// Data Engineering group of the Engineering and Space Science Directorate of the Jet
// Propulsion Laboratory of the National Aeronautics and Space Administration, an
// independent agency of the United States Government.
// 
// This software is copyrighted (c) 2000 by the California Institute of Technology.  All
// rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modification, is not
// permitted under any circumstance without prior written permission from the California
// Institute of Technology.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHORS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
// THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
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

		params.add("q", "*:*");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_class:\"1,collection\"");

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

		params.add("q", "*:*");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_class:\"1,bundle\"");

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

	public void getInvestigation() throws MalformedURLException,
			SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "data_class:Investigation");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_class:\"1,context\"");

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
	}

	public void getInstHost() throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "data_class:Instrument_Host");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_class:\"1,context\"");

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
	}

	public void getInst() throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "data_class:Instrument");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_class:\"1,context\"");

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
	}
	
	//public Map<String, Object> getContext(String identifier) {
	public SolrDocument getContext(String identifier) throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "identifier:"+identifier);
		params.set("indent", "on");
		params.set("wt", "xml");
		//params.set("fq", "facet_class:\"1,context\"");

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
			//System.out.println("obj = " + (String)obj);
			if (obj instanceof java.util.Date) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
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
/*
	public void getSearchResult(String queryStr) {
		try {
			SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);
			ModifiableSolrParams params = new ModifiableSolrParams();
			QueryResponse qr = new QueryResponse();

			params.set("q", queryStr);
			params.set("start", 0);
			params.set("rows", max);

			// System.out.println("params = " + params.toString());
			qr = solr.query(params,
					org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

			SolrDocumentList sdl = qr.getResults();

			System.out.println("Found: " + sdl.getNumFound());
			
			int idx = 0;
			dslists = new ArrayList<String>();
			for (SolrDocument doc : sdl) {
				for (Map.Entry<String, Object> entry : doc.entrySet()) {
					System.out.println("Key = " + entry.getKey()
							+ "    Value = " + entry.getValue());
				}
				idx++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
*/	
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
			//pds4Search.getInvestigation();
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
