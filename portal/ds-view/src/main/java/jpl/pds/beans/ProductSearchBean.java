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
import jpl.eda.profile.Profile;
import jpl.eda.profile.ProfileClient;
import jpl.eda.profile.ProfileElement;
import jpl.eda.profile.ProfileException;
import jpl.eda.profile.EnumeratedProfileElement;
import jpl.eda.xmlquery.*;
import jpl.eda.util.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/** The search bean handles searching.
 *
 * This bean is used by the PDS ProductQueryServlet to send queries to the JPL.PDS.MasterProd server.
 *
 * @author Thuy Tran
 */
public class ProductSearchBean {
	/** Constructor.
	 *
	 * This bean is constructed by the PDS ProductQueryServlet
	 */
	public ProductSearchBean() throws IOException, ProfileException {
		profileClient = new ProfileClient("urn:eda:rmi:JPL.PDS.MasterProd");
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
		System.err.println(URLEncoder.encode(text,"UTF-8"));
		XMLQuery xml_query = new XMLQuery(
			/*keywordQuery*/text, 
			/*id*/"OODT_XML_QUERY_V0.1",
			/*title*/"OODT_XML_QUERY - Bean Query", 
			/*desc*/"This query can be handled by the OODT System",
			/*ddId*/null, 
			/*resultModeId*/"profile", 
			/*propType*/"BROADCAST", 
			/*propLevels*/"N/A",
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

	List getSubList (List list, int startRow, int numRowsToFetch) throws Exception {

		if (startRow > list.size()) return null;

		int lastRow = (startRow+numRowsToFetch <= list.size()) ? startRow-1+numRowsToFetch : list.size();
		return list.subList(startRow-1,lastRow);
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
	public Profile getDatasetProfile (String text) throws Exception {
		XMLQuery xml_query = new XMLQuery(/*keywordQuery*/text, 
			/*id*/"OODT_XML_QUERY_V0.1",
			/*title*/"OODT_XML_QUERY - Bean Query", /*desc*/"This query can be handled by the OODT System",
			/*ddId*/null, /*resultModeId*/"profile", /*propType*/"BROADCAST", /*propLevels*/"N/A",
			XMLQuery.DEFAULT_MAX_RESULTS);
		long time0 = System.currentTimeMillis();
		List results = profileClient.query(xml_query);
		long time = System.currentTimeMillis();
		System.err.println("execute dataset profile search: " + (time - time0));
		return (Profile) results.get(profileItem);
	}

        /** What does the profile queries. */
        private ProfileClient profileClient;

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
	private int profileItem;

	/** Command line invocation.
	 *
	 * @param argv Command-line arguments.
	 */
	public static void main(String[] argv) {
		try {
			ProductSearchBean bean = new ProductSearchBean();
			bean.setProfileSearchText(argv[0]);
		} catch (Exception ex) {
			System.err.println("Exception " + ex.getClass().getName() + ": " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
