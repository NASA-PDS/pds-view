//	Copyright 2009-2010, by the California Institute of Technology.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.handler.StandardRequestHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

/**
 * @author pramirez
 * 
 */
public class PDAPHandler extends StandardRequestHandler {

  public enum RESOURCE_CLASS {
    DATA_SET(), PRODUCT, MAP_PROJECTED;
  }

  private Logger LOG = Logger.getLogger(this.getClass().getName());
  private final static Map<RESOURCE_CLASS, Map<String, String>> resourceParams;
  private final static Map<String, String> resourceMap;
  private final static Map<String, String> generalParams;
  private final static List<String> rangedParams = new ArrayList<String>(Arrays
      .asList("LATITUDE", "LONGITUDE"));
  private final static String RESOURCE = "RESOURCE_CLASS";
  private final static String RETURN_TYPE = "RETURN_TYPE";
  private final static String PAGE_SIZE = "PAGE_SIZE";
  private final static String PAGE_NUMBER = "PAGE_NUMBER";
  private final static String VOTABLE = "VOTABLE";
  private final static String RESOURCE_FIELD = "resClass";

  static {
    resourceMap = new HashMap<String, String>();
    resourceMap.put("DATA_SET", "DataSet");
    resourceMap.put("PRODUCT", "Product");
    resourceMap.put("MAP_PROJECTED", "Product");
    
    generalParams = new HashMap<String, String>();
    generalParams.put("INSTRUMENT_TYPE", "instrument_type");
    generalParams.put("INSTRUMENT_NAME", "instrument_name");
    generalParams.put("START_TIME", "start_time");
    generalParams.put("STOP_TIME", "stop_time");
    generalParams.put("TARGET_TYPE", "target_type");
    generalParams.put("TARGET_NAME", "target_name");
    Collections.unmodifiableMap(generalParams);

    resourceParams = new HashMap<RESOURCE_CLASS, Map<String, String>>();

    Map<String, String> datasetParams = new HashMap<String, String>();
    datasetParams.put("DATA_SET_ID", "data_set_id");
    Collections.unmodifiableMap(datasetParams);
    resourceParams.put(RESOURCE_CLASS.DATA_SET, datasetParams);

    Map<String, String> productParams = new HashMap<String, String>();
    productParams.put("DATA_SET_ID", "data_set_id");
    productParams.put("PRODUCT_ID", "product_id");
    Collections.unmodifiableMap(productParams);
    resourceParams.put(RESOURCE_CLASS.PRODUCT, productParams);

    Map<String, String> projectedParams = new HashMap<String, String>();
    projectedParams.put("LONGITUDE", "longitude");
    projectedParams.put("LATITUDE", "latitude");
  }

  private static void appendRanged(StringBuilder query, String parameter, String value) {
    String[] ranges = value.split(",");
    for (String range : ranges) {
      query.append(parameter);
      query.append(":");
      if (!range.contains("/")) {
        query.append(range);
      } else {
        String[] extrema = range.split("/");
        query.append("[");
        if (extrema.length == 1) {
          query.append(extrema[0]);
          query.append(" TO *");
        } else {
          if (extrema[0].length() == 0) {
            query.append("*");
          } else {
            query.append(extrema[0]);
          }
          query.append(" TO ");
          query.append(extrema[1]);
        }
        query.append("]");
      }
      query.append(" OR ");
    }
    // Remove the last OR
    query.delete(query.length() - 4, query.length());
  }

  @Override
  public void handleRequestBody(SolrQueryRequest request,
      SolrQueryResponse response) throws Exception {
    ModifiableSolrParams pdapParams = new ModifiableSolrParams(request
        .getParams());
    request.setParams(pdapParams);
    StringBuilder queryString = new StringBuilder();

    // Handle the general parameters that are the same across all resource
    // classes
    for (String parameter : generalParams.keySet()) {
      if (request.getOriginalParams().getParams(parameter) != null) {
        queryString.append("(");
        // Loop through and add in the identifier to the query string
        for (String value : request.getOriginalParams().getParams(parameter)) {
          if (!value.trim().isEmpty()) {
            if (rangedParams.contains(parameter)) {
              appendRanged(queryString, generalParams.get(parameter), value);
            } else {
              queryString.append(generalParams.get(parameter));
              queryString.append(":");
              queryString.append(value);
            }
            queryString.append(" OR ");
          }
        }
        // Remove the last OR
        queryString.delete(queryString.length() - 4, queryString.length());
        queryString.append(")");
        queryString.append(" AND ");
      }
    }

    // Remove the dangling AND
    if (queryString.length() != 0) {
      queryString.delete(queryString.length() - 5, queryString.length());
    }

    RESOURCE_CLASS resource = RESOURCE_CLASS.valueOf(request
        .getOriginalParams().getParams("RESOURCE_CLASS")[0]);
    Map<String, String> resourceParam = resourceParams.get(resource);
    // if there is already a portion of the query string group with AND
    if (queryString.length() != 0) {
      queryString.append(" AND ");
    }
    for (String parameter : resourceParam.keySet()) {
      if (request.getOriginalParams().getParams(parameter) != null) {
        queryString.append("(");
        // Loop through and add in the identifier to the query string
        for (String value : request.getOriginalParams().getParams(parameter)) {
          if (!value.trim().isEmpty()) {
            if (rangedParams.contains(parameter)) {
              appendRanged(queryString, resourceParam.get(parameter), value);
            } else {
              queryString.append(resourceParam.get(parameter));
              queryString.append(":");
              queryString.append(value);
            }
            queryString.append(" OR ");
          }
        }
        // Remove the last OR
        queryString.delete(queryString.length() - 4, queryString.length());
        queryString.append(")");
        queryString.append(" AND ");
      }
    }
      
    // Inject the resource class into the query
    queryString.append(RESOURCE_FIELD);
    queryString.append(":");
    queryString.append(resourceMap.get(request.getOriginalParams().getParams(RESOURCE)[0]));

    // If there is a query pass it on to Solr as the q param
    if (queryString.length() > 0) {
      pdapParams.remove("q");
      pdapParams.add("q", queryString.toString());
    }

    // Handle return type maps to Solrs wt param
    if (request.getOriginalParams().getParams(RETURN_TYPE) != null) {
      String returnType = request.getOriginalParams().getParams(RETURN_TYPE)[0];
      pdapParams.remove("wt");
      if (!VOTABLE.equals(returnType)) {
        // Just use Solr's default response writers
        pdapParams.add("wt",
            request.getOriginalParams().getParams(RETURN_TYPE)[0]);
      }
    }
    
    // Handle paging parameters
    if (request.getOriginalParams().getParams(PAGE_NUMBER) != null) {
      int pageNum = Integer.parseInt(request.getOriginalParams().getParams(PAGE_NUMBER)[0]);
      pdapParams.remove("start");
      pdapParams.add("start","" + (pageNum-1));
    }
    if (request.getOriginalParams().getParams(PAGE_SIZE) != null) {
      int pageSize = Integer.parseInt(request.getOriginalParams().getParams(PAGE_SIZE)[0]);
      pdapParams.remove("rows");
      pdapParams.add("rows","" + pageSize);
    }

    this.LOG.info("Solr Query String: " + queryString);
  }
  
}
