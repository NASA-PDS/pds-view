//	Copyright 2009-2013, by the California Institute of Technology.
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
//	$Id$
//

package gov.nasa.pds.search;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.handler.StandardRequestHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

/**
 * @author pramirez
 * @author jpadams
 * 
 */
public class PDSSearchProtocol extends StandardRequestHandler {
	private Logger LOG = Logger.getLogger(this.getClass().getName());

	public final static String[] MULTI_PARAMS = { "identifier", "instrument",
			"instrument-host", "instrument-host-type", "instrument-type",
			"investigation", "observing-system", "person", "product-class", "target",
			"target-type", "title" };
	public final static String QUERY_PARAM = "q";
	public final static String START_TIME_PARAM = "start-time";
	public final static String STOP_TIME_PARAM = "stop-time";
	public final static String TERM_PARAM = "term";
	public final static String ARCHIVE_STATUS_PARAM = "archive-status";
	public final static String RETURN_TYPE_PARAM = "return-type";
	public final static String VOTABLE = "votable";

	@Override
	public void handleRequestBody(SolrQueryRequest request,
			SolrQueryResponse response) throws Exception {
		ModifiableSolrParams pdsParams = new ModifiableSolrParams(
				request.getParams());
		request.setParams(pdsParams);
		
		/** Query string that will be returned as response **/
		StringBuilder queryString = new StringBuilder();
		
		int start = 0;
		
		// Handle multi valued parameters
		for (String parameter : MULTI_PARAMS) {
			if (request.getOriginalParams().getParams(parameter) != null) {
				
				start = queryString.length();
				
				// Loop through and add in the identifier to the query string
				for (String value : request.getOriginalParams().getParams(parameter)) {
					if (!value.trim().isEmpty()) {
						queryString.append(parameter);
						queryString.append(":");
						queryString.append(value);
						queryString.append(" OR ");
					}
				}
				// Remove the last OR
				queryString.delete(queryString.length() - 4, queryString.length());

				
				if (request.getOriginalParams().getParams(parameter).length > 1) {
					queryString.insert(start, "(");
					queryString.append(")");
				}
				queryString.append(" AND ");
			}
		}

		this.LOG.info("Multi-param Query String pre-clean: " + queryString.toString());
		
		// Remove the last AND
		if (queryString.length() != 0) {
			queryString.delete(queryString.length() - 5, queryString.length());
		}

		// Handle start time
		if (request.getOriginalParams().getParams(START_TIME_PARAM) != null) {
			// if there is already a portion of the query string group with AND
			if (queryString.length() != 0) {
				queryString.append(" AND ");
			}
			queryString.append(START_TIME_PARAM);
			queryString.append(":");
			queryString.append(request.getOriginalParams()
					.getParams(START_TIME_PARAM)[0]);
		}

		// Handle stop time
		if (request.getOriginalParams().getParams(STOP_TIME_PARAM) != null) {
			// if there is already a portion of the query string group with AND
			if (queryString.length() != 0) {
				queryString.append(" AND ");
			}
			queryString.append(STOP_TIME_PARAM);
			queryString.append(":");
			queryString.append(request.getOriginalParams().getParams(STOP_TIME_PARAM)[0]);
		}

		if (request.getOriginalParams().getParams(ARCHIVE_STATUS_PARAM) != null) {
			// if there is already a portion of the query string group with AND
			if (queryString.length() != 0) {
				queryString.append(" AND ");
			}
			queryString.append(ARCHIVE_STATUS_PARAM);
			queryString.append(":");
			queryString.append(request.getOriginalParams().getParams(
					ARCHIVE_STATUS_PARAM)[0]);
		}

		// Handle terms
		if (request.getOriginalParams().getParams(TERM_PARAM) != null) {
			// if there is already a portion of the query string group with AND
			if (queryString.length() != 0) {
				queryString.append(" ");
			}
			queryString.append(request.getOriginalParams().getParams(TERM_PARAM)[0]);
		}

		// Handle query by appending to query string.
		if (request.getOriginalParams().getParams(QUERY_PARAM) != null) {
			// if there is already a portion of the generic query string
			if (queryString.length() != 0) {
				queryString.append(" ");
			}
			
			// If query parameter contains and OR|AND then surround in parentheses
			this.LOG.info("q value in lower case: " + request.getOriginalParams().getParams(QUERY_PARAM)[0].toLowerCase());
			if (request.getOriginalParams().getParams(QUERY_PARAM)[0].toLowerCase().matches("[^\\(]* or [^\\)]*")) {
				queryString.append("(");
				queryString.append(request.getOriginalParams().getParams(QUERY_PARAM)[0]);
				queryString.append(")");
			} else {
				queryString.append(request.getOriginalParams().getParams(QUERY_PARAM)[0]);
			}
		}

		// If there is a query pass it onto Solr as the q param
		if (queryString.length() > 0) {
			pdsParams.remove("q");
			pdsParams.add("q", queryString.toString());
		}

		// Handle return type maps to Solrs wt param
		if (request.getOriginalParams().getParams(RETURN_TYPE_PARAM) != null) {
			String returnType = request.getOriginalParams().getParams(
					RETURN_TYPE_PARAM)[0];
			pdsParams.remove("wt");
			if (VOTABLE.equals(returnType)) {
				// Use Solr's Velocity Response Writer
				pdsParams.add("wt", "velocity");
				// Use votable.vm for the response style
				pdsParams.add("v.layout", "votable");
				// Use the results.vm to format the result set
				pdsParams.add("v.template", "results");
			} else {
				// Just use Solr's default response writers
				pdsParams.add("wt",
						request.getOriginalParams().getParams(RETURN_TYPE_PARAM)[0]);
			}
		}

		this.LOG.info("Solr Query String: " + queryString);

		super.handleRequestBody(request, response);
	}
	
}
