//	Copyright 2012-2018, by the California Institute of Technology.
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
//	$Id: PDAPHandler.java 12889 2014-03-22 16:24:17Z shardman $
//

package gov.nasa.pds.search;

import java.lang.IllegalArgumentException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.handler.StandardRequestHandler;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

/**
 * This class handles query requests conforming to IPDA's 
 * Planetary Data Access Protocol (PDAP) and maps that query into
 * a Solr conformant query for the Search Service to process.
 *
 * @author pramirez
 */
public class PDAPHandler extends StandardRequestHandler {

  public enum RESOURCE_CLASS {
    DATA_SET, PRODUCT, MAP_PROJECTED, METADATA;
  }

  private Logger LOG = Logger.getLogger(this.getClass().getName());
  private final static Map<RESOURCE_CLASS, Map<String, String>> resourceParams;
  private final static Map<String, String> resourceMap;
  private final static Map<String, String> generalParams;
  private final static List<String> rangedParams = new ArrayList<String>(Arrays
      .asList("INSTRUMENT_NAME","INSTRUMENT_TYPE", "LATITUDE", "LONGITUDE", 
              "START_TIME", "STOP_TIME", "TARGET_NAME", "TARGET_TYPE"));
  private final static String RESOURCE = "RESOURCE_CLASS";
  private final static String RETURN_TYPE = "RETURN_TYPE";
  private final static String PAGE_SIZE = "PAGE_SIZE";
  private final static String PAGE_NUMBER = "PAGE_NUMBER";
  private final static String VOTABLE = "VOTABLE";
  private final static String RESOURCE_FIELD = "objectType";

  static {
    resourceMap = new HashMap<String, String>();
    resourceMap.put("DATA_SET", "Product_Data_Set_PDS3");
    resourceMap.put("PRODUCT", "Product_Observational");
    resourceMap.put("MAP_PROJECTED", "Product_Observational");
    resourceMap.put("METADATA", "Product_Null");
    
    generalParams = new HashMap<String, String>();
    generalParams.put("INSTRUMENT_NAME", "instrument_name");
    generalParams.put("INSTRUMENT_TYPE", "instrument_type");
    generalParams.put("START_TIME", "start_time");
    generalParams.put("STOP_TIME", "stop_time");
    generalParams.put("TARGET_NAME", "target_name");
    generalParams.put("TARGET_TYPE", "target_type");
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
    Collections.unmodifiableMap(projectedParams);
    resourceParams.put(RESOURCE_CLASS.MAP_PROJECTED, projectedParams);

    Map<String, String> metadataParams = new HashMap<String, String>();
    Collections.unmodifiableMap(metadataParams);
    resourceParams.put(RESOURCE_CLASS.METADATA, metadataParams);
  }

  // Patterns for Cross-Site Scripting filter.
  private static Pattern[] patterns = new Pattern[]{
    // script fragments
    Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
    // src='...'
    Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // lonely script tags
    Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
    Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // eval(...)
    Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // expression(...)
    Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // javascript:...
    Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
    // vbscript:...
    Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
    // onload(...)=...
    Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
    // alert(...)
    Pattern.compile("alert\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
  };

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
    ModifiableSolrParams pdapParams = new ModifiableSolrParams(request.getParams());
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
              appendRanged(queryString, generalParams.get(parameter), clean(value));
            } else {
              queryString.append(generalParams.get(parameter));
              queryString.append(":");
              queryString.append(clean(value));
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

    // Grab the resource class, default to METADATA if not specified.
    String resourceClass = null;
    RESOURCE_CLASS resource = null;
    if (request.getOriginalParams().getParams(RESOURCE) != null) {
      resourceClass = clean(request.getOriginalParams().getParams(RESOURCE)[0]);
      try {
        resource = RESOURCE_CLASS.valueOf(resourceClass);
      } catch (Exception e) {
        resourceClass = "METADATA";
        resource = RESOURCE_CLASS.valueOf(resourceClass);
        pdapParams.remove(RESOURCE);
        pdapParams.add(RESOURCE, "METADATA");
      }
    } else {
      resourceClass = "METADATA";
      resource = RESOURCE_CLASS.valueOf(resourceClass);
      pdapParams.add(RESOURCE, "METADATA");
    }
    Map<String, String> resourceParam = resourceParams.get(resource);

    // If there is already a portion of the query string group with AND
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
              appendRanged(queryString, resourceParam.get(parameter), clean(value));
            } else {
              queryString.append(resourceParam.get(parameter));
              queryString.append(":");
              queryString.append(clean(value));
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
    queryString.append(resourceMap.get(resourceClass));

    // If there is a query pass it on to Solr as the q param
    if (queryString.length() > 0) {
      pdapParams.remove("q");
      pdapParams.add("q", queryString.toString());
    }

    // Handle return type maps to Solrs wt param
    if (request.getOriginalParams().getParams(RETURN_TYPE) != null) {
      String returnType = clean(request.getOriginalParams().getParams(RETURN_TYPE)[0]);
      if (!VOTABLE.equals(returnType)) {
        // Just use Solr's default response writers
        pdapParams.remove("wt");
        pdapParams.add("wt", clean(request.getOriginalParams().getParams(RETURN_TYPE)[0]));
      }
    }

    // Handle the page size if specified. If not, get the default page size.
    int pageSize = 0;
    if (request.getOriginalParams().getParams(PAGE_SIZE) != null) {
      pageSize = Integer.parseInt(clean(request.getOriginalParams().getParams(PAGE_SIZE)[0]));
      pdapParams.remove("rows");
      pdapParams.add("rows","" + pageSize);
    } else {
      pageSize = Integer.parseInt(clean(request.getParams().getParams("rows")[0]));
    }
    
    // Handle the page number and convert it for Solr's start parameter.
    if (request.getOriginalParams().getParams(PAGE_NUMBER) != null) {
      int pageNum = Integer.parseInt(clean(request.getOriginalParams().getParams(PAGE_NUMBER)[0]));
      if (pageNum > 1) {
        pdapParams.remove("start");
        pdapParams.add("start","" + (pageNum*pageSize-pageSize));
      }
    }

    this.LOG.info("Solr Query String: " + queryString);
    super.handleRequestBody(request, response);
  }
  
  // This method makes up a simple anti cross-site scripting (XSS) filter
  // written for Java web applications. What it basically does is remove 
  // all suspicious strings from request parameters before returning them 
  // to the application.
  public String clean(String value) {
    if (value != null) {
      // Avoid null characters
      value = value.replaceAll("\0", "");

      // Remove all sections that match a pattern
      for (Pattern scriptPattern : patterns){
        value = scriptPattern.matcher(value).replaceAll("");
      }

      // After all of the above has been removed just blank out the value 
      // if any of the offending characters are present that facilitate 
      // Cross-Site Scripting and Blind SQL Injection.
      // We normally exclude () but they often show up in queries.
      char badChars [] = {'|', ';', '$', '@', '\'', '"', '<', '>', ',', '\\', /* CR */ '\r' , /* LF */ '\n' , /* Backspace */ '\b'};
      try {
        String decodedStr = URLDecoder.decode(value);
        for(int i = 0; i < badChars.length; i++) {
          if (decodedStr.indexOf(badChars[i]) >= 0) {
            value = "";
          }
        }
      } catch (IllegalArgumentException e) {
        value = "";
      }
    }
    return value;
  }
}
