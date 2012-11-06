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
import java.util.ArrayList;
import java.util.Arrays;

import gov.nasa.pds.dsview.registry.SearchRegistry;
import gov.nasa.pds.registry.model.ExtrinsicObject;

/** 
 * This class is used by the PDS data set view web interface to retrieve
 * values for building the search parameter pull-down lists.
 *
 * @author Hyun Lee
 */
public class GetSearchParams {
	private String[] msnname = new String[]{};
	private String[] instname = new String[]{}; 
	private String[] insthostname = new String[]{};
	private String[] targname = new String[]{};
	private String[] insttype = new String[]{};
	private String[] targtype = new String[]{};
	private String[] dsname = new String[]{};
	private String[] dsid = new String[]{};
	private String[] dataobjtype = new String[]{};
	private String[] insthosttype = new String[]{};
	private String[] nodename = new String[]{};
	private String[] archivestat = new String[]{};
	private final int max = 10000; // to set facet.limit, default is 100
	
	static String dontDisplay = "|UNKNOWN|UNK|NA|N/A|";	
	
	static String solrServerUrl = "http://pdsdev.jpl.nasa.gov:8080/search-service/pds/";	
	static List<String> dslists = null;	
	private static boolean formFlag = true;

	public void getParams() {
		String facetName = null;
		try {			
			SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);
	
			ModifiableSolrParams params = new ModifiableSolrParams();
			QueryResponse qr = new QueryResponse();
			
			List<String> list = new ArrayList<String>();

			params.set("q", "-archive-status:SUPERSEDED");
		    params.set("start", 0);
		    //params.set("rows", 0);
		    params.set("wt", "xml");
		    params.set("facet", true);
		    params.set("facet.limit", max);
		    	    
			/*
			** Build the msnname list
			*/
			//System.out.println("Building msnname list");
			//params.set("facet.field", "mission_name");
			if (formFlag) 
				facetName = "form-mission";
			else 
				facetName = "mission_name";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
			
		    List<FacetField> facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {		          
		           list.add(fcount.getName());
		        }
		    }
		    //System.out.println("mission name (" + list.size() + ")= " + list.toString());
			msnname = (String[]) list.toArray(msnname);

			/*
			** Build the targname list
			*/
			//System.err.println("Building targname list");
			list = new ArrayList();
			list.add("All");
			//params.set("facet.field", "form-target");
			if (formFlag) 
				facetName = "form-target";
			else 
				facetName = "target_name";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
			
		    facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {
		           list.add(fcount.getName());
		        }
		    }
		    //System.out.println("target name (" + list.size() + ")= " + list.toString());
			targname = (String[]) list.toArray(targname);

			/*
			** Build the tartype list
			*/
			//System.err.println("Building targtype list");
			list = new ArrayList();
			list.add("All");
			//params.set("facet.field", "form-target-type");
			if (formFlag) 
				facetName = "form-target-type";
			else 
				facetName = "target_type";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
			
		    facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {
		           list.add(fcount.getName());
		        }
		    }
		    //System.out.println("target type (" + list.size() + ")= " +  list.toString());
			targtype = (String[]) list.toArray(targtype);

			/*
			** Build the instname list
			*/
			//System.err.println("Building instname list");
			list = new ArrayList();
			//list.add("All");
			//params.set("facet.field", "form-instrument");
			if (formFlag) 
				facetName = "form-instrument";
			else 
				facetName = "instrument_name";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
			
		    facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {
		           list.add(fcount.getName());
		        }
		    }
		    //System.out.println("instrument(" + list.size() + ")= " + list.toString());
			instname = (String[]) list.toArray(instname);

			/*
			** Build the insthostname list
			*/
			//System.err.println("Building insthostname list");
			list = new ArrayList();
			//list.add("All");
			//params.set("facet.field", "form-instrument-host");
			if (formFlag) 
				facetName = "form-instrument-host";
			else 
				facetName = "instrument_host_name";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
			
		    facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {		       
		           list.add(fcount.getName());
		        }
		    }
		    //System.out.println("instrument host name(" + list.size() + ")= " + list.toString());
			insthostname = (String[]) list.toArray(insthostname);

			/*
			** Build the insttype list
			*/
			//System.err.println("Building insttype list");
			list = new ArrayList();
			list.add("All");
			//params.set("facet.field", "form-instrument-type");
			if (formFlag) 
				facetName = "form-instrument-type";
			else 
				facetName = "instrument_type";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
			
		    facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {
		           list.add(fcount.getName());
		        }
		    }
		    //System.out.println("instrument type(" + list.size() + ")= " +  list.toString());
			insttype= (String[]) list.toArray(insttype);
			
			/*
			** Build the dsname list
			*/
			//System.err.println("Building dsname list");
			list = new ArrayList();
			list.add("All");
			//params.set("facet.field", "form-data-set-name");
			if (formFlag) 
				facetName = "form-data-set-name";
			else 
				facetName = "data_set_name";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
			
		    facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {
		           list.add(fcount.getName());
		        }
		    }
		    //System.out.println("data_set_name (" + list.size() + ")= " +  list.toString());
			dsname = (String[]) list.toArray(dsname);
			
			/*
			** Build the dsid list
			*/
			//System.err.println("Building dsid list");
			list = new ArrayList();
			list.add("All");
			//params.set("facet.field", "form-data-set-id");
			if (formFlag) 
				facetName = "form-data-set-id";
			else 
				facetName = "data_set_id";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
			
		    facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {
		           list.add(fcount.getName());
		        }
		    }
		    //System.out.println("data_set_id (" + list.size() + ")= " + list.toString());
			dsid = (String[]) list.toArray(dsid);

			/*
			** Build the dataobjtype list
			*/
			//System.err.println("Building dataobjtype list");
			list = new ArrayList();
			list.add("All");
			//params.set("facet.field", "form-data-object-type");
			if (formFlag) 
				facetName = "form-data-object-type";
			else 
				facetName = "data_object_type";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
			
		    facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {
		           list.add(fcount.getName());
		        }
		    }
		    //System.out.println("data_object_type (" + list.size() + ")= " + list.toString());
			dataobjtype= (String[]) list.toArray(dataobjtype);

			/*
			** Build the insthosttype list
			*/
			//System.err.println("Building insthosttype list");
			list = new ArrayList();
			list.add("All");
			//params.set("facet.field", "form-instrument-host-type");
			if (formFlag) 
				facetName = "form-instrument-host-type";
			else 
				facetName = "instrument_host_type";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);			
		    facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {
		           list.add(fcount.getName());
		        }
		    }
			insthosttype = (String[]) list.toArray(insthosttype);
			//System.out.println("instrument host type(" + list.size() + ")= " +  Arrays.toString(insthosttype));

			/*
			** Build the nodename list
			*/
		
			//System.err.println("Building nodename list");
			list = new ArrayList();
			list.add("All");
			//params.set("facet.field", "form-node-name");
			if (formFlag) 
				facetName = "form-node-name";
			else 
				facetName = "node_name";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);			
		    facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {
		           list.add(fcount.getName());
		        }
		    }
		    //System.out.println("node-name(" + list.size() + ")= " + list.toString());
			nodename = (String[]) list.toArray(nodename);
			
			/*
			** Build the archivestat list
			*/
			//System.err.println("Building archivestat list");
			list = new ArrayList();
			list.add("All");
			//params.set("facet.field", "form-archive-status");
			if (formFlag) 
				facetName = "form-archive-status";
			else 
				facetName = "archive_status";
			params.set("facet.field", facetName);
		    qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);			
		    facets = qr.getFacetFields();
		    for (FacetField facet: facets) {
		        List<FacetField.Count> facetEntries = facet.getValues();
		        for(FacetField.Count fcount : facetEntries) {
		           list.add(fcount.getName());
		        }
		    }
            archivestat = (String[]) list.toArray(archivestat);
            //System.out.println("archive status(" + list.size() + ")= " + Arrays.toString(archivestat));
		    
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
    public void getParams(String queryStr) {    	
		try {			
			SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);	
			ModifiableSolrParams params = new ModifiableSolrParams();
			QueryResponse qr = new QueryResponse();
					
			params.set("q", queryStr);
		    params.set("start", 0);
		    //params.set("rows", 0);
		    //params.set("wt", "xml");
		    params.set("facet", true);
		    params.set("facet.limit", max);
		    
		    params.add("facet.field", "form-mission");
		    params.add("facet.field", "form-target");
		    params.add("facet.field", "form-target-type");
		    params.add("facet.field", "form-instrument");
		    params.add("facet.field", "form-instrument-type");
		    params.add("facet.field", "form-data-set-id");
		    params.add("facet.field", "form-data-set-name");
		    params.add("facet.field", "form-instrument-host");
		    params.add("facet.field", "form-instrument-host-type");
		    //params.add("facet.field", "form-node-name");
		    //params.add("facet.field", "form-archive-status");
		    	    
			qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
		    List<FacetField> facets = qr.getFacetFields();
		    String facetName = null;
		    List<String> list = null;
		    for (FacetField facet: facets) {
		    	facetName = facet.getName();		    		
		        List<FacetField.Count> facetEntries = facet.getValues();
		        if (facetEntries!=null) {
		        	list = new ArrayList<String>();
		        	for(FacetField.Count fcount : facetEntries) {
		        		list.add(fcount.getName());
		        	}

					if (facetName.equals("form-mission"))
						msnname = (String[]) list.toArray(msnname);
					else if (facetName.equals("form-target"))
						targname = (String[]) list.toArray(targname);
					else if (facetName.equals("form-target-type"))
						targtype = (String[]) list.toArray(targtype);
					else if (facetName.equals("form-instrument"))
						instname = (String[]) list.toArray(instname);
					else if (facetName.equals("form-instrument-type")) {
						list.add("All");
						insttype= (String[]) list.toArray(insttype);
					}
					else if (facetName.equals("form-instrument-host"))
						insthostname = (String[]) list.toArray(insthostname);
					else if (facetName.equals("form-instrument-host-type"))
						insthosttype = (String[]) list.toArray(insthosttype);
					else if (facetName.equals("form-data-set-id")) {
						list.add("All");
						dsid = (String[]) list.toArray(dsid);
					}
					else if (facetName.equals("form-data-set-name")) {
						list.add("All");
						dsname = (String[]) list.toArray(dsname);
					}
					/*
					else if (facetName.equals("form-node-name"))
						nodename = (String[]) list.toArray(nodename);
					else if (facetName.equals("form-archive-status"))
						archivestat = (String[]) list.toArray(archivestat);
					*/
		        }
		    }
		    /*
		    System.out.println("msnname = " + Arrays.toString(msnname));
		    System.out.println("targname = " + Arrays.toString(targname));
		    System.out.println("targtype = " + Arrays.toString(targtype));
		    System.out.println("instname = " + Arrays.toString(instname));
		    System.out.println("insttype = " + Arrays.toString(insttype));
		    System.out.println("insthostname = " + Arrays.toString(insthostname));
		    System.out.println("insthosttype = " + Arrays.toString(insthosttype));
		    System.out.println("dsid = " + Arrays.toString(dsid));
		    System.out.println("dsname = " + Arrays.toString(dsname));
		    //System.out.println("nodename = " + Arrays.toString(nodename));
		    //System.out.println("archivestat = " + Arrays.toString(archivestat));
		     */
		    
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.exit(-1);
    	}
    }
    
    public void getSearchResult(String queryStr) {
    	try {			
			SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);	
			ModifiableSolrParams params = new ModifiableSolrParams();
			QueryResponse qr = new QueryResponse();
					
			params.set("q", queryStr);
		    params.set("start", 0);
			params.set("rows", max);

		    //System.out.println("params = " + params.toString());
			qr = solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);
			
			SolrDocumentList sdl = qr.getResults();
			
		    System.out.println("Found: " + sdl.getNumFound());
		    /*
		    System.out.println("Start: " + sdl.getStart());
		    System.out.println("Max Score: " + sdl.getMaxScore());
		    System.out.println("--------------------------------");
		    */
		    int idx=0;
		    dslists = new ArrayList<String>();
		    for (SolrDocument d: sdl) {
		    	/*
		    	if (idx==0)
		    		System.out.println(d.getFieldNames().toString());
		    	*/
		    	if (d.getFieldValues("data_set_id")!=null) {
		    		java.util.Collection collection = d.getFieldValues("data_set_id");
		    		List<String> vals = new ArrayList<String>();
		    		vals.addAll(collection);

		    		String dsid = vals.get(0);
		    		//System.out.println(idx + ".  data_set_id = " + dsid);
		    		dslists.add(dsid);    		
		    	}	
		    	/*
		    	else {
		    		System.out.println(idx + ".   data_set_id is NULL....");		    		
		    	}
		    	*/   	
		    	idx++;
		    }
    	} catch (Exception e) {
    		e.printStackTrace();
    		System.exit(-1);
    	}
    }
    
    public int getDSSize() {
    	return this.dslists.size();
    }
    
    public List<String> getDsLists() {
    	return this.dslists;
    }
      
    public List<String> getDsLists(int startIndex, int stopIndex) {
    	return this.dslists.subList(startIndex, stopIndex);
    }

	/** Constructor.
	 *
	 * This bean is constructed by the web server.
	 */
	public GetSearchParams(String url) {
		this.solrServerUrl = url;
	}
	
	public String[] getMissionName() {
		Arrays.sort(msnname);
		return msnname;
	}

	public String[] getDatasetId() {
		Arrays.sort(dsid);
		return dsid;
	}

	public String[] getDatasetName() {
		Arrays.sort(dsname);
		return dsname;
	}
	
	public String[] getTargetName() {
		List<String> numericList = new ArrayList<String>();
		List<String> alphaList = new ArrayList<String>();
		for (String sval: targname) {
			if (isNumeric(sval))
				numericList.add(sval);
			else 
				alphaList.add(sval);
		}
		if (numericList.size()>1)
			java.util.Collections.sort(numericList);
		if (alphaList.size()>1)
			java.util.Collections.sort(alphaList);
		
		List<String> newList = new ArrayList<String>(alphaList);
		newList.addAll(numericList);
		targname = (String[]) newList.toArray(targname);
		return targname;
	}
	
	public boolean isNumeric(String str)
	{
	    if (Character.isDigit(str.charAt(0))) 
	    	return true;
	    else
	    	return false;
	}

	public String[] getTargetType() {
		Arrays.sort(targtype);
		return targtype;
	}

	public String[] getInstrumentName() {
		Arrays.sort(instname);
		return instname;
	}

	public String[] getInstrumentHostName() {
		Arrays.sort(insthostname);
		return insthostname;
	}

	public String[] getNodeName() {
		Arrays.sort(nodename);
		return nodename;
	}

	public String[] getArchiveStat() {
		Arrays.sort(archivestat);
		return archivestat;
	}

	public String[] getInstrumentHostType() {
		Arrays.sort(insthosttype);
		return insthosttype;
	}

	public String[] getInstrumentType() {
		Arrays.sort(insttype);
		return insttype;
	}

	public String[] getDataObjectType() {
		return dataobjtype;
	}
	
	public void initialize() {
		msnname = new String[]{};
		instname = new String[]{}; 
		insthostname = new String[]{};
		targname = new String[]{};
		insttype = new String[]{};
		targtype = new String[]{};
		dsname = new String[]{};
		dsid = new String[]{};
		//dataobjtype = new String[]{};
		insthosttype = new String[]{};
		//nodename = new String[]{};
		//archivestat = new String[]{};
	}
	
	/** Command line invocation.
	 *
	 * @param argv Command-line arguments.
	 */
	public static void main(String[] argv) {
		try {
			//GetSearchParams sparms = new GetSearchParams("http://pdsdev.jpl.nasa.gov:8080/search-service/pds/");
			GetSearchParams sparms;
			if (argv.length==1) 
				sparms = new GetSearchParams(argv[0]);
			else
			    sparms = new GetSearchParams("http://pdsbeta.jpl.nasa.gov/tools/search-service/pds/");
			if (argv.length==2) 
				formFlag = false;

			//sparms.getParams("mission:phoenix");
			//sparms.getParams("target:MARS");
			//sparms.getParams("target_type:planet AND mission:phoenix");
			//sparms.getParams("instrument_type:robotic_arm AND mission:phoenix");
			//sparms.getParams("instrument:ROBOTIC ARM");
			sparms.getParams();
			
			//sparms.getSearchResult("mission:cassini-huygens and target:Callisto");
		} catch (Exception ex) {
			System.err.println("Exception " + ex.getClass().getName() + ": " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
	
