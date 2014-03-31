//	Copyright 2013, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id: RegistryExtractor.java 11862 2013-08-15 19:21:24Z jpadams $
//

package gov.nasa.pds.search.core.post;

import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;
import gov.nasa.pds.search.core.util.Utility;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.w3c.dom.Document;

/**
 * Class to manage the index used for the Search Service. Includes method to clear,
 * update, and optimize the stored index. Currently implementations use HTTP Client
 * connections to communicate with server. Potential future refactoring could use
 * SolrJ to interact with client. Decision was made to use this method
 * because SolrJ would make more sense if we were posting data incrementally 
 * throughout the indexing process.  Since that functionality has been
 * separated into distinct components, this makes more sense.
 * 
 * @author jpadams
 *
 */
public class SolrPost {
	
	/** Search Service endpoint that allows for optimizing of the stored Solr index **/
	private static final String OPTIMIZE_URL= "/update/?optimize=true";
	
	/** Search Service endpoint that allows for basic updates of Solr index. 
	 *  Parameters include commit after update.
	 **/
	private static final String UPDATE_URL= "/update/?commit=true";
	
	/** Search Service endpoint use for updating Solr index using XSLT transformation
	 *  Parameters include commit after update.
	 */
	private static final String UPDATE_XSLT_URL= "/update/xslt?commit=true&tr=add-hierarchy.xsl";
	
	/** Standard output logger. **/
	private static Logger log = Logger.getLogger(SolrPost.class.getName());
	
	/** Search Service base endpoint **/
	public String searchServiceUrl;
	
	/**
	 * Solr Post constructor. Requires specification of Search Service base endpoint.
	 * 
	 * @param serviceUrl
	 * @throws SolrPostException 
	 */
	public SolrPost(String serviceUrl) throws SolrPostException {
		if (Utility.urlExists(serviceUrl)) {
			this.searchServiceUrl = serviceUrl;
		} else {
			throw new SolrPostException("Error: " + serviceUrl + " not found. Check connections.");
		}
	}
	
	/**
	 * Calls the post and optimize methods to complete a post of a list of index files
	 * to the Search Service
	 * 
	 * @param dir
	 * @param filePrefixList
	 * @throws SolrPostException
	 */
	public void postIndex(String dir, List<String> filePrefixList) throws SolrPostException {
		post(dir, filePrefixList);
		optimize();
	}
	
	/**
	 * Deletes all data from the Search Service.
	 * 
	 * @throws SolrPostException
	 */
	public void clean() throws SolrPostException {
        log.log(new ToolsLogRecord(ToolsLevel.INFO,
        		"Cleaning Search Service Index"));
		InputStream deleteXml = new ByteArrayInputStream("<delete><query>*:*</query></delete>".getBytes());
		
		try {
			if (!httpPost(deleteXml, this.searchServiceUrl + UPDATE_URL)) {
				throw new SolrPostException("SolrPost.clean failed. Verify " + this.searchServiceUrl + UPDATE_URL + " exists.");
			}
		} finally {
			try {
				deleteXml.close();
			} catch (Exception e) {
				// We don't care if an error is thrown here
			}
		}
	}
	
	/**
	 * Posts data to Search Service. Data is taking from the Solr documents found using the
	 * directory and filePrefixList.
	 * 
	 * @param dir
	 * @param filePrefixList
	 * @throws SolrPostException
	 */
	public void post(String dir, List<String> filePrefixList) throws SolrPostException {
		//System.out.println("\n\nPOST");
		FileInputStream stream = null;
		try {
			
			for (File file : Utility.getFileList(dir, filePrefixList)) {
		        log.log(new ToolsLogRecord(ToolsLevel.INFO,
		        		"Posting: " + file.getAbsolutePath()));
				stream = new FileInputStream(file);
				if (!httpPost(stream, this.searchServiceUrl + UPDATE_XSLT_URL)) {
					throw new SolrPostException("SolrPost.post failed. Verify url: " + this.searchServiceUrl + UPDATE_XSLT_URL + " and file: " + file.getAbsolutePath() + " exist.");
				}
			}
		} catch (FileNotFoundException e) {	// Error should only throw if someone moves files after we generate the file list
			throw new SolrPostException("Solr index file not found.");
		} finally {
			try {
				stream.close();
			} catch (Exception e) {
				// We don't care about this error
			}
		}
	}
	
	/**
	 * Final step in Search Service index process that optimizes the stored index.
	 * 
	 * @throws SolrPostException
	 */
	public void optimize() throws SolrPostException {
        log.log(new ToolsLogRecord(ToolsLevel.INFO,
        		"Optimizing Search Service index."));
        
		if (!httpGet(this.searchServiceUrl + OPTIMIZE_URL)) {
			throw new SolrPostException("SolrPost.optimize failed. Verify " + this.searchServiceUrl + OPTIMIZE_URL + " exists.");
		}
	}
	
	/**
	 * Utility method for retrieving an HTTP GET connection based on the input
	 * URL.
	 * 
	 * @param url
	 * @throws SolrPostException
	 */
	private boolean httpGet(String url) throws SolrPostException {
            HttpGet httpget = new HttpGet(url);

	        log.log(new ToolsLogRecord(ToolsLevel.DEBUG,
	        		"Executing request: " + httpget.getURI()));
            
            //System.out.println("----------------------------------------");
            //System.out.println(responseBody);
            //System.out.println("----------------------------------------");
            
	        InputStream responseStream = null;
	        
	        try {
	        	responseStream = Utility.execHttpRequest(httpget);
	        	return verifyStatus(responseStream);
	        } catch (IOException e) {
	        	throw new SolrPostException("Error extracting data from " + url + ". Verify connections.");
	        } finally {
	        	try {
					responseStream.close();
				} catch (IOException e) {
					// Ignore exception
				}
	        }
    }
	
	/**
	 * Utility method for retrieving an HTTP PUT connection based on the input
	 * URL and input stream of data.
	 * 
	 * @param stream
	 * @param url
	 * @throws SolrPostException
	 */
	private boolean httpPost(InputStream stream, String url) throws SolrPostException {
        HttpPost httppost = new HttpPost(url);

        InputStreamEntity entity = new InputStreamEntity(
                stream, -1);
        
        entity.setContentType(ContentType.APPLICATION_XML.getMimeType());
        entity.setChunked(true);
        
        // It may be more appropriate to use FileEntity class in this particular
        // instance but we are using a more generic InputStreamEntity to demonstrate
        // the capability to stream out data from any arbitrary source
        //
        //FileEntity entity = new FileEntity(file, ContentType.APPLICATION_XML);

        httppost.setEntity(entity);

        log.log(new ToolsLogRecord(ToolsLevel.DEBUG,
        		"Executing request: " + httppost.getURI()));

        InputStream responseStream = null;
        try {
        	responseStream = Utility.execHttpRequest(httppost);
        	return verifyStatus(responseStream);
        } catch (IOException e) {
        	throw new SolrPostException("Error with " + url + ": " + e.getMessage());
        } finally {
        	try {
				responseStream.close();
			} catch (Exception e) {
				// Ignore exception
			}
        }
	}
	
	private boolean verifyStatus(InputStream responseStream) throws SolrPostException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(responseStream);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/response/lst/int[@name='status']");
			String status = expr.evaluate(doc);
			
			//System.out.println("Status: " + status);
			StringWriter writer = new StringWriter();
			IOUtils.copy(responseStream, writer, "UTF-8");
	        log.log(new ToolsLogRecord(ToolsLevel.DEBUG, "Status: " + status));
			
			if (status.equals("0")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
				throw new SolrPostException("Error parsing Solr Post response. Check connections and URLs.");
		}
	}
}
