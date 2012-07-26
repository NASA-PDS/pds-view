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
// $Id$

package jpl.pds.beans;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import jpl.eda.Configuration;
import jpl.eda.profile.*;
import jpl.eda.util.*;
import jpl.eda.query.*;
import jpl.eda.xmlquery.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/** The search bean handles searching.
 *
 * This bean is used by the PDS single point of entry search page.
 *
 * @author Thuy Tran
 */
public class SearchBean {
/** Tabular query -> HTML stylesheet. */
private static String defaultProfileStylesheetID;

/** Stylesheet base URL */
private static String webServerBaseURL;

/** Stylesheet factory. */
private static final TransformerFactory FACTORY = TransformerFactory.newInstance();

/** Raw XML display in HTML stylesheet. */
private static Transformer rawStylesheet;

/** Stylesheet to take out the element values that we don't want for the PDS data set search interface */
private static Transformer PDSprofileStylesheet;

/** The OODT configuration. */
private static Configuration configuration;

/** The parameter <-> keyword mapping */
private static Map PDSkeywords;  /* key = param, value = keyword */
private static Map PDSparams;    /* key = keyword, value = param */

/** Initialize the static instances related to XSLT.
         */
static {
         // Load up our properties, inheriting from the system properties.
         Properties properties = new Properties(System.getProperties());
         jpl.eda.util.Utility.loadProperties(properties, SearchBean.class, "pds.properties");
         System.setProperties(properties);

         try {
                 configuration = Configuration.getConfiguration();
                 configuration.mergeProperties(System.getProperties());
         } catch (RuntimeException ex) {
                 throw ex;
         } catch (Exception ex) {
                 ex.printStackTrace();
                 System.exit(1);
         }


			// Get the stylesheets web server base URL. It is assume that
			// the xsl rarely get changed, so we are pointing to the production
			// application server always
			webServerBaseURL = properties.getProperty("jpl.pds.beans.SearchBean.stylesheets.WebServerBaseURL");

         // Set up the stylesheets system IDs.
         defaultProfileStylesheetID = "xsl/"
                 + properties.getProperty("jpl.pds.beans.SearchBean.stylesheets.tableProfile");

         // load the parameter-keyword file
         String filename = properties.getProperty("jpl.pds.beans.SearchBean.keywords.mapping",
                                 "PDSKeywords.data");
         String line = new String();
                try
         {
                 BufferedReader br = new BufferedReader(new InputStreamReader(
                         jpl.pds.beans.SearchBean.class.getResourceAsStream(filename)));
                 PDSkeywords = new HashMap();
                 PDSparams = new HashMap();
                        while ((line = br.readLine()) != null)
                        {
                                StringTokenizer st = new StringTokenizer(line, ",");
                         String param = st.nextToken().trim();
                         String keyword = st.nextToken().trim();
                         PDSkeywords.put(param,keyword);
                         PDSparams.put(keyword,param);
                        }
                }
         catch (IOException ie)
         {
                 ie.printStackTrace();
                 System.exit(1);
         }
 
}

/** Constructor.
         *
         * This bean is constructed by the web server.
         */
public SearchBean() throws IOException, TransformerConfigurationException, ProfileException {
         setProfileStylesheet(defaultProfileStylesheetID);
         profileClient = new ProfileClient("urn:eda:rmi:JPL.PDS.Profile");
}

/** Set the profile stylesheet.
         *
         * @param systemID The URI/URL/system ID of the profile stylesheet.
         */
public void setProfileStylesheet(String systemID) throws TransformerConfigurationException {
         if (profileStylesheetID.equals(systemID)) return;
         profileStylesheetID = systemID;
         //profileStylesheet = FACTORY.newTransformer(new StreamSource(configuration.getWebServerBaseURL() + "/" + systemID));
         profileStylesheet = FACTORY.newTransformer(new StreamSource(webServerBaseURL + "/" + systemID));
}

/** Set the profile metadata item.
         *
         * @param text Button number.
         */
public void setProfileMetadataItem(String text) {
         profileItem = Integer.parseInt(text) -1;
}
       
/*
** Just for testing
*/
public void setSearchText (String text) {
         searchPhrase = text;
} 

/** Set profile search text.
         *
         * The user sets the search phrase by entering text into the "profileSearchText"
         * text area on the page.  When submitted, this method is given the text and it
         * starts the search.
         *
         * @param text Search text.
         */
public void setProfileSearchText(String text) throws Exception {
         searchPhrase = text;
         XMLQuery xml_query = new XMLQuery(/*keywordQuery*/text, 
                 /*id*/"OODT_XML_QUERY_V0.1",
                 /*title*/"OODT_XML_QUERY - Bean Query", /*desc*/"This query can be handled by the OODT System",
                 /*ddId*/null, /*resultModeId*/"profile", /*propType*/"BROADCAST", /*propLevels*/"N/A",
                 XMLQuery.DEFAULT_MAX_RESULTS);
         long time0 = System.currentTimeMillis();
         theResults = profileClient.query(xml_query);
         long time = System.currentTimeMillis();
         System.err.println("execute search: " + (time - time0));
}

/** Return the result sublist 
         *
         * Go to the row <startRow> and fetch <numRowsToFetch> from theResults,
         *
         * @param startRow row number from which to start fetching from theResults;
         *                 first row is 1
         * @param numRowsToFetch number of rows to fetch for this round
         * @return result sublist
         */

public List getSearchResultList (int startRow, int numRowsToFetch) throws Exception {
         return getSubList(theResults, startRow, numRowsToFetch);
}

public List getDatasetReleaseResultList (int startRow, int numRowsToFetch) throws Exception {
         return getSubList(datasetReleaseResults, startRow, numRowsToFetch);
}

List getSubList (List list, int startRow, int numRowsToFetch) throws Exception {

         if (startRow > list.size()) return null;

         int lastRow = (startRow+numRowsToFetch <= list.size()) ? startRow-1+numRowsToFetch : list.size();
         return list.subList(startRow-1,lastRow);
}



/** Format the given XML with the given stylesheet.
         *
         * @param xml The XML string to format.
         * @param stylesheet What stylesheet to use.
         * @return The results in HTML format.
         * @throws Exception On any error.
         */
private static String format(String xml, Transformer stylesheet) throws Exception {
         StringReader reader = new StringReader(xml);
         StreamSource source = new StreamSource(reader);
         StringWriter writer = new StringWriter();
         StreamResult target = new StreamResult(writer);
         stylesheet.transform(source, target);
         reader.close();
         writer.close();
         return writer.getBuffer().toString();
}

/** Format the given XML with the given stylesheet.
         *
         * @param xml The XML node to format.
         * @param stylesheet What stylesheet to use.
         * @return The results in HTML format.
         * @throws Exception On any error.
         */
private static String format(Node node, Transformer stylesheet) throws Exception {
         StringWriter writer = new StringWriter();
         StreamResult target = new StreamResult(writer);
         stylesheet.transform(new DOMSource(node), target);
         writer.close();
         return writer.getBuffer().toString();
}

/** Got profile search results?
         *
         * @return True if we have profile results, false otherwise.
         */
public boolean haveSearchResults() {
         return theResults != null;
}

/** How many items are there in the results?
         *
         * @return the size of the results
         */
public int getSearchResultsSize() {
         return ((theResults == null) ? 0 : theResults.size());
}


/** How many items are there in the dataset release results?
         *
         * @return the size of the results
         */
public int getDatasetReleaseSearchResultsSize() {
         return ((datasetReleaseResults == null) ? 0 : datasetReleaseResults.size());
}

/** Get the XML result.
         *
         * @return The XMLQuery as an XML string.
         */
public String getXMLResult() {
         return xmlResult;
}

/** Get the profile search phrase, safe for inclusion in HTML.
         *
         * @return The current profile search phrase, safe for HTML inclusion.
         */
public String getHTMLEscapedProfileSearchText() {
         return jpl.eda.util.XML.escape(getProfileSearchText());
}

/** Get the profile search phrase.
         *
         * @return The current profile search phrase.
         */
public String getProfileSearchText() {
         if (searchPhrase== null)
                 return "";
         else
                 return searchPhrase;
}

 
/** Get the full profile for the specified data set
         *
         * @return The profile
         */
public Profile getDatasetProfile (String id) throws Exception {
         String text = "resclass=DATASET AND dsid=" + id + allProfileElements;
         XMLQuery xml_query = new XMLQuery(/*keywordQuery*/text, 
                 /*id*/"OODT_XML_QUERY_V0.1",
                 /*title*/"OODT_XML_QUERY - Bean Query", /*desc*/"This query can be handled by the OODT System",
                 /*ddId*/null, /*resultModeId*/"profile", /*propType*/"BROADCAST", /*propLevels*/"N/A",
                 XMLQuery.DEFAULT_MAX_RESULTS);
         long time0 = System.currentTimeMillis();
         List results = profileClient.query(xml_query);
         long time = System.currentTimeMillis();
         System.err.println("execute dataset profile search: " + (time - time0));
         if (results == null || results.isEmpty()) {
                 System.err.println("results are null");
                 return null;
         }
         /* 
         ** query for dataset description and confidence level note
         ** in separate queries as required by profile server
         */
         Profile profile = (Profile) results.get(profileItem); 
         text = "resclass=DATASETDESC AND dsid=" + id + " AND RETURN=dsd";
         xml_query = new XMLQuery(text, 
                 "OODT_XML_QUERY_V0.1", "OODT_XML_QUERY - Bean Query",
                 "This query can be handled by the OODT System",
                 null, "profile", "BROADCAST", "N/A", XMLQuery.DEFAULT_MAX_RESULTS);
         results = profileClient.query(xml_query);
         if (results != null && ! results.isEmpty()) {
                 // get the dataset description and add it to the original profile
                 Profile dsdProfile = (Profile) results.get(0);
                        ProfileElement element = (ProfileElement) dsdProfile.getProfileElements().get("DATASET_DESCRIPTION");
                 profile.getProfileElements().put(element.getName(), element);
         }

         text = "resclass=DATASETCONFIDENCE AND dsid=" + id + " AND RETURN=conflvlnote";
         xml_query = new XMLQuery(text, 
                 "OODT_XML_QUERY_V0.1", "OODT_XML_QUERY - Bean Query",
                 "This query can be handled by the OODT System",
                 null, "profile", "BROADCAST", "N/A", XMLQuery.DEFAULT_MAX_RESULTS);
         results = profileClient.query(xml_query);
         if (results != null && ! results.isEmpty()) {
                 // get the confidence level note and add it to the original profile
                 Profile confProfile = (Profile) results.get(0);
                        ProfileElement element = (ProfileElement) confProfile.getProfileElements().get("CONFIDENCE_LEVEL_NOTE");
                 profile.getProfileElements().put(element.getName(), element);
         }
         return profile;

}


/** Get the full profile for the specified target
         *
         * @return The profile
         */
public Profile getTargetProfile (String id) throws Exception {
         String text = "resclass=target AND targname=" + id + allTargetProfileElements;
         XMLQuery xml_query = new XMLQuery(/*keywordQuery*/text, 
                 /*id*/"OODT_XML_QUERY_V0.1",
                 /*title*/"OODT_XML_QUERY - Bean Query", /*desc*/"This query can be handled by the OODT System",
                 /*ddId*/null, /*resultModeId*/"profile", /*propType*/"BROADCAST", /*propLevels*/"N/A",
                 XMLQuery.DEFAULT_MAX_RESULTS);
         long time0 = System.currentTimeMillis();
         List results = profileClient.query(xml_query);
         long time = System.currentTimeMillis();
         System.err.println("execute target profile search: " + (time - time0));
         if (results == null || results.isEmpty()) return null;
         Profile profile = (Profile) results.get(profileItem); 
         return profile;

}


/** Get the resource profiles for all the data sets in the input list
         *
         * @return The map of datasets and associated resources
         */
public Map getResourceProfiles (List dsList) throws Exception {
         StringBuffer idList =  new StringBuffer();
         for (Iterator i = dsList.iterator(); i.hasNext();) {
                 Profile profile = (Profile) i.next();
                 Map elements = profile.getProfileElements();
                 ProfileElement pe = null;
                 if (elements.containsKey("DATA_SET_ID")) {
                         pe = (ProfileElement)elements.get("DATA_SET_ID");
                         for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                                 if (idList.length() == 0) {
                                         idList.append("(dsid=").append((String)e.next());
                                 }
                                 else {
                                         idList.append(" OR dsid=").append((String)e.next());
                                 }
                         }
                 }
         }
         if (idList.length() == 0) {
                 return null;
         } 
         String text = "resclass=RESOURCE AND " + idList + ") AND RETURN=resname AND RETURN=reslink AND RETURN=dsid AND RETURN=resourceid AND RETURN=resclass"; 

         XMLQuery xml_query = new XMLQuery(/*keywordQuery*/text, 
                 /*id*/"OODT_XML_QUERY_V0.1",
                 /*title*/"OODT_XML_QUERY - Bean Query", /*desc*/"This query can be handled by the OODT System",
                 /*ddId*/null, /*resultModeId*/"profile", /*propType*/"BROADCAST", /*propLevels*/"N/A",
                 XMLQuery.DEFAULT_MAX_RESULTS);
         long time0 = System.currentTimeMillis();
         System.err.println("sending to profile server: " + text);
         List theResources = profileClient.query(xml_query);
         long time = System.currentTimeMillis();
         System.err.println("execute resource profile search: " + (time - time0));

         Map resourceMap = new HashMap(); // key = dsid; value = list of DatasetResource
         List resList;
         for (Iterator r = theResources.iterator(); r.hasNext();) {
                 Profile profile = (Profile) r.next();
                 String resName = "";
                 String resLocation = "";
                 String resClass =  profile.getResourceAttributes().getResClass();
                 for (Iterator l = profile.getResourceAttributes().getResLocations().iterator(); l.hasNext();) {
                         resLocation = (String)l.next();
                 }
                 Map elements = profile.getProfileElements();
                 ProfileElement pe = null;

                 if (elements.containsKey("RESOURCE_NAME")) {
                         pe = (ProfileElement)elements.get("RESOURCE_NAME");
                         for (Iterator p = pe.getValues().iterator(); p.hasNext();) {
                                 resName = (String)p.next();
                         }                                 
                 }
                 if (resName.equals("")) {
                         resName = profile.getResourceAttributes().getIdentifier();
                 }
                 DatasetResource thisResource = new DatasetResource(resName, resLocation, resClass);

                 if (elements.containsKey("DATA_SET_ID")) {
                         pe = (ProfileElement)elements.get("DATA_SET_ID");
                         for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                                 String dsid = (String)e.next();
                                 if (resourceMap.containsKey(dsid)) {
                                         resList = (List)resourceMap.get(dsid);
                                 } else {
                                         resList = new ArrayList();
                                 }
                                 resList.add(thisResource);
                                 resourceMap.put(dsid, resList);
                         }
                 }
         }
         time = System.currentTimeMillis();
         System.err.println("formatting resource profile search: " + (time - time0));

         return resourceMap;
}


/** Send query to get the list of dataset releases to the profile server
         *
         */
public void getDatasetRelease() throws Exception {
         String text = "resclass=RELEASE " + allReleaseElements;
         XMLQuery xml_query = new XMLQuery(/*keywordQuery*/text, 
                 /*id*/"OODT_XML_QUERY_V0.1",
                 /*title*/"OODT_XML_QUERY - Bean Query", /*desc*/"This query can be handled by the OODT System",
                 /*ddId*/null, /*resultModeId*/"profile", /*propType*/"BROADCAST", /*propLevels*/"N/A",
                 XMLQuery.DEFAULT_MAX_RESULTS);
         long time0 = System.currentTimeMillis();
         datasetReleaseResults = profileClient.query(xml_query);
         long time = System.currentTimeMillis();
         System.err.println("execute dataset release search: " + (time - time0));
}

public String getFormParameter (String keyword) {
         if (PDSparams != null && PDSparams.containsKey(keyword)) {
                 return (String)PDSparams.get(keyword);
         } else {
                 return keyword;
         }
}

public String getPDSKeyword (String param) {
         if (PDSkeywords != null && PDSkeywords.containsKey(param)) {
                 return (String)PDSkeywords.get(param);
         } else {
                 return param;
         }

}

/*
** Set mime type
*/
public void setMimeType (String mimeType) {
         mimeTypes.add(mimeType);
}

public List getMimeType() {
         if (mimeTypes.isEmpty()) {
                 mimeTypes.add("*/*");
         }
         return mimeTypes;
}

public String getDesiredMimeType() {
         if (mimeTypes.isEmpty()) {
                 mimeTypes.add("*/*");
         }
         String mimeType = "";
                for (Iterator i = mimeTypes.iterator(); i.hasNext();) try {
                 mimeType += "&mimeType=" + URLEncoder.encode((String) i.next(), "UTF-8");
         } catch (UnsupportedEncodingException cantHappen) {
                 throw new IllegalStateException("Unexpected UnsupportedEncodingException: " + cantHappen.getMessage());
         }
         return mimeType;
}

/** What does the profile queries. */
private ProfileClient profileClient;

/** The name of the profile service.  
         */
private String profileServiceName;

/** The name server.
         */
private String nameServerIOR;

/** Current profile search results (XML format).  If null, we don't have any, yet.
         */
private String resultsXML;

/** Current profile search results
         */
private List theResults;

/** Current dataset release search results
         */
private List datasetReleaseResults;

/** XMLQuery, as an XML string. */
private String xmlResult;

/** Current search phrase.
         */
private String searchPhrase;

/** Index of the profile result item
         */
private int profileItem = 0;

/** Profile stylesheet. */
private Transformer profileStylesheet;

/** System ID of the profile stylesheet. */
private String profileStylesheetID = "";

/*
** Desired product mime type
*/
private List mimeTypes = new ArrayList();

private static String allProfileElements = " AND RETURN=dsid AND RETURN=dsname AND RETURN=targname " +
         "AND RETURN=targtype AND RETURN=msnname AND RETURN=msnstrtdate AND RETURN=msnstopdate " +
         //"AND RETURN=targtersedec AND RETURN=sbntarglocator " +
 
         "AND RETURN=dataobjtype AND RETURN=strttime " +
         "AND RETURN=stoptime AND RETURN=instid AND RETURN=instname AND RETURN=insttype " +
         "AND RETURN=insthostid AND RETURN=insthostname AND RETURN=insthosttype AND RETURN=dsreleasedt " +
         "AND RETURN=nodename AND RETURN=archivestat "  +
         "AND RETURN=citdesc AND RETURN=abstract " +
         "AND RETURN=fullname AND RETURN=telephonenum AND RETURN=reslink " +
         "AND RETURN=dstersedesc AND RETURN=nssdcdsid AND RETURN=resname ";

private static String allTargetProfileElements = " AND RETURN=dsid AND RETURN=dsname AND RETURN=targname " +
         "AND RETURN=targtype AND RETURN=msnname AND RETURN=msnstrtdate AND RETURN=msnstopdate " +
         "AND RETURN=targtersedec AND RETURN=sbntarglocator " +
 
         "AND RETURN=dataobjtype AND RETURN=strttime " +
         "AND RETURN=stoptime AND RETURN=instid AND RETURN=instname AND RETURN=insttype " +
         "AND RETURN=insthostid AND RETURN=insthostname AND RETURN=insthosttype AND RETURN=dsreleasedt " +
         "AND RETURN=nodename AND RETURN=archivestat "  +
         "AND RETURN=citdesc AND RETURN=abstract " +
         "AND RETURN=fullname AND RETURN=telephonenum AND RETURN=reslink AND RETURN=resname " +
         "AND RETURN=dstersedesc AND RETURN=nssdcdsid";

private static String allReleaseElements = " AND RETURN=dsid AND RETURN=reldate " +
         "AND RETURN=relmedium AND RETURN=producttype AND RETURN=relarchstat " +
         "AND RETURN=reldesc AND RETURN=userid AND RETURN=revdate " +
         "AND RETURN=releaseid AND RETURN=relparatext " +
         "AND RETURN=resourceid AND RETURN=resname AND RETURN=reslink";
         //"AND RETURN=releasedir"

/** Command line invocation.
         *
         * @param argv Command-line arguments.
         */
public static void main(String[] argv) {
         if (argv.length < 1) {
                 System.err.println("Usage: <dsid>");
                 System.exit(1);
         }

         try {
                 SearchBean bean = new SearchBean();
                 Profile profile = bean.getDatasetProfile(argv[0]);                 
         } catch (Exception ex) {
                 System.err.println("Exception " + ex.getClass().getName() + ": " + ex.getMessage());
                 ex.printStackTrace();
                 System.exit(1);
         }
         System.exit(0);
}
}

 
 
