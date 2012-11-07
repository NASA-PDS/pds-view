<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
   String searchUrl = application.getInitParameter("search.url");
%>
<html>
<head>
   <title>PDS Data Set Search Results</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">

   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.dsview.registry.GetSearchParams, gov.nasa.pds.dsview.registry.SearchRegistry, 
                    gov.nasa.pds.registry.model.ExtrinsicObject, java.util.*, java.net.*, java.io.*" %>

   <script language="JavaScript"
           src="<%=pdshome%>js/popWindow.js"></script>

   <SCRIPT LANGUAGE="JavaScript">
      <%@ include file="/pds/utils.js"%>
   </SCRIPT>
</head>

<body class="menu_data menu_item_data_data_search ">

   <%@ include file="/pds/header.html" %>
   <%@ include file="/pds/main_menu.html" %>

   <div id="submenu">
   <div id="submenu_data">
   <h2 class="nonvisual">Menu: PDS data</h2>
   <ul>
      <li id="data_data_search"><a href="http://pds.jpl.nasa.gov/tools/data-search/">Data Search</a></li>
      <li id="data_text_search"><a href="http://pds.jpl.nasa.gov/tools/text-search/">Text Search</a></li>
      <li><a href="/ds-view/pds/index.jsp">Form Search</a></li>
      <li id="data_how_to_search"><a href="http://pds.jpl.nasa.gov/data/how-to-search.shtml">How to Search</a></li>
      <li id="data_data_set_status"><a href="http://pds.jpl.nasa.gov/tools/dsstatus/">Data Set Status</a></li>
      <li id="data_release_summary"><a href="http://pds.jpl.nasa.gov/tools/subscription_service/SS-Release.shtml">Data Release Summary</a></li>
   </ul>
   </div>
   <div class="clear"></div>
   </div>

<!-- Main content -->
<div id="content">
   <div style="border-top: 1px solid_white;">

<%! 
String constructURL (String url, String queryString, String dsdesc) {
  try {
   URL aURL = new URL(url);
   String filename = aURL.getFile();

   // don't pass any params to reslink that ends with /
   if (filename.endsWith("/")) {
      return url;
   }

   // per Pam Woncik 03/12/03 - if the URL contains "pdsimg" or "pdsimage" (ie. Atlas browsers), 
   // don't append the query string
   if (url.indexOf("pdsimg") > 0 || url.indexOf("pdsimage") > 0
       || url.indexOf("pds-imaging") > 0 ) { 
      return url;
   }

   // per Steve Hughes 04/16/04 - don't append query string to FTP URL
   if (url.startsWith("ftp://") || url.startsWith("FTP://")) { 
      return url;
   }

   // always pass DATA_SET_TERSE_DESCRIPTION if not blank 
   // per Pam Woncik - don't do this in D01, keep this code here for D02
   int pos = filename.indexOf('?');
   String browserURL = url;
   if (! dsdesc.equals("")) {
      if (pos > 0) {
         browserURL += "&DATA_SET_TERSE_DESCRIPTION=" + URLEncoder.encode(dsdesc, "UTF-8");
      } else {
         browserURL += "?DATA_SET_TERSE_DESCRIPTION=" + URLEncoder.encode(dsdesc, "UTF-8");
      }
      if (! queryString.equals("")) {
         browserURL += "&" + queryString;
      }
   // dataset_terse_description is blank
   } else if (! queryString.equals("")) {
      if (pos > 0) {
         browserURL += "&" + queryString;
      } else {
         browserURL += "?" + queryString;
      }

   }

   return browserURL;

  } catch (java.net.MalformedURLException e) {
     e.printStackTrace();
     return url;
  } catch (java.io.UnsupportedEncodingException e) {
     e.printStackTrace();
     return url;
  }
}
%>

<%-- Get the subset of results to be displayed for this invocation --%>
<%

String queryString = (request.getSession().getAttribute("queryString") != null ?
                      (String)request.getSession().getAttribute("queryString") : "");

String userQueryString = (request.getSession().getAttribute("message") != null ?
                      (String)request.getSession().getAttribute("message") : "");
                      
System.out.println("in results.jsp...queryString = " + queryString);
//out.println("in results.jsp...queryString = " + queryString);
                      
GetSearchParams sparms = new GetSearchParams(searchUrl);
if (queryString!=null)
   sparms.getSearchResult(queryString);
	
SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);

int listsize = sparms.getDSSize();
//System.err.println("List size from results.jsp = " + listsize);

int rowsFetch = 25;
int curPage;
int totalPage = (int) Math.ceil((double)listsize/(double)rowsFetch);

if (request.getParameterValues("pageNo") == null) {              
   curPage = 0;
} else {
   curPage = Integer.parseInt(request.getParameterValues("pageNo")[0]);
}
int startRow = (curPage * rowsFetch);

//out.println("curPage = " + curPage + "<br>startRow = " + startRow + "<br>");

List<String> curList = null;
Map resourceMap = null;

if (listsize > 0) {
   // need to modify this with small size
   // recalculate for rowsFetch when the size is smaller than 25
   if (startRow+rowsFetch>listsize) {
      rowsFetch = listsize-startRow;
      //out.println("rowsFetch = " + rowsFetch);
   }
   curList = sparms.getDsLists(startRow, startRow+rowsFetch);
   
   //out.println("curList size = " + curList.size());
   
   //curList = searchBean.getSearchResultList(startRow,rowsFetch);
   //if (curList != null && curList.size() > 0) {
   //   resourceMap = searchBean.getResourceProfiles(curList);
   //}
}

//out.println("queryString = " + queryString);

String title = "Search Results";
if (queryString.indexOf("MISSION_STOP_DATE=UNK") > -1) {
   // this page is called to display active mission data sets
   title =  "Active Mission Data Sets";
   queryString = "";
}

String orgSearch = (request.getSession().getAttribute("requestURI") != null ? 
                     (String)request.getSession().getAttribute("requestURI") : 
	             "/ds-view/pds/index.jsp");

if (! queryString.equals("")) {
    //queryString = queryString.replace('+','_');
	queryString = queryString.trim();
	queryString = java.net.URLEncoder.encode(queryString);
}
%>

<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>
         <table width="760" border="0" cellspacing="2" cellpadding="2">
            <tr>
               <td colspan=5>
                  <table border="0" cellpadding="0" cellspacing="0" width="760">
                     <tr>
                        <td class="pageTitle" align=left><font color="#6F4D0E"><b><%=title%></b>&nbsp;(<%= listsize%> data sets found)</font>
                        </td>
                        <td align=right valign=center color="#6F4D0E">
                           <A href="javascript:openWindow('/pds/search_result_help.html', 'Result',550,500)"> <IMG SRC="/ds-view/pds/images/btn_help.gif" BORDER=0></A>
                        </td>
                     </tr>
                  </table>
               </td>
	        </tr>
            <tr><td>&nbsp;</td></tr>
            <!-- HEADER of TABLE -->
	        <tr valign="TOP">
		       <td valign="TOP" bgcolor="#003366" align="center" width="200">
			      <FONT size="2" color="#FFFFFF"><b>Data Set</b></font>
		       </td>
               <td valign="TOP" bgcolor="#003366" align="center" width="50">
                  <FONT size="2" color="#FFFFFF"><b>Instrument<br>Host</b></font>
               </td>
		       <td valign="TOP" bgcolor="#003366" align="center" width="200">
			      <FONT size="2" color="#FFFFFF"><b>Information About<br>the Data Set</b></font>
		       </td>
		       <td valign="TOP" bgcolor="#003366" align="center" width="150">
			      <FONT size="2" color="#FFFFFF"><b>Data Products &amp;<br>Related Files</b></font>
		       </td>
		       <td valign="TOP" bgcolor="#003366" align="center" width="200">
			      <FONT size="2" color="#FFFFFF"><b>Other Resources</b></font>
		       </td>
	        </tr>
   <% if (curList == null) { %>
            <tr valign="TOP">
               <td valign="TOP" colspan="5">
                  <font color="#FFFFFF" size="2"><b>No data sets found matching your search criteria:<br> <%=userQueryString%></b></font></td>
	        </tr>
   <% } else { 
           // Loop through the profile results list 
           int c=1;
           for (Iterator i = curList.iterator(); i.hasNext(); c++) {
              String dsid = (String)i.next();
              String tmpDsid = dsid.replaceAll("/", "-");
              ExtrinsicObject dsObj = searchRegistry.getExtrinsic("urn:nasa:pds:data_set."+tmpDsid);
                         
              String dsdesc = searchRegistry.getSlotValues(dsObj, "data_set_terse_description").get(0);             
 	          String dsname = searchRegistry.getSlotValues(dsObj, "data_set_name").get(0);
 	          
              // display data_set_name if description is null
              if (dsdesc.equals("") || dsdesc.equalsIgnoreCase("NULL") ||
                  dsdesc.equalsIgnoreCase("N/A") || dsdesc.equalsIgnoreCase("NA") ||
                  dsdesc.equalsIgnoreCase("TBD")) {
                  dsdesc = dsname;
              }

              String instname= "";
	          String instid = "";
              List<String> svalues = searchRegistry.getSlotValues(dsObj, "instrument_host_ref");     	 
		      for (int j=0; j<svalues.size(); j++) {
		         String aValue = (String) svalues.get(j);
		    	 String insthostlid = aValue.substring(0, aValue.indexOf("::"));
		    	 ExtrinsicObject insthostObj = searchRegistry.getExtrinsic(insthostlid);
		    	 instid = searchRegistry.getSlotValues(insthostObj, "instrument_host_id").get(0).toUpperCase();
		    	 instname = searchRegistry.getSlotValues(insthostObj, "instrument_host_name").get(0);
		      }
 
 			  //String tmpDsid = dsid.replaceAll("/", "-");
              String profileURL = "/ds-view/pds/viewDataset.jsp?dsid=" + 
                                  URLEncoder.encode(tmpDsid, "UTF-8");
	
              String resLocURL = "";
              String resName = "";
            
              List<String> rvalues = searchRegistry.getResourceRefs(dsObj);
		      String refLid = rvalues.get(0);
		      refLid = refLid.substring(0, refLid.indexOf("::"));
		      ExtrinsicObject resource = searchRegistry.getExtrinsic(refLid);
		      resName = searchRegistry.getSlotValues(resource, "resource_name").get(0);
		    	 resLocURL = searchRegistry.getSlotValues(resource, "resource_url").get(0); 
		    	//System.out.println("resource_name = " + resName);
		    	//System.out.println("resource_url = " + resLocURL);
              
              %>
 
 			<!-- RESULTS -->
            <tr valign="top">
		       <td valign="TOP" align="left" width="200" bgcolor="#EFEFEF">
			      <%= c+(curPage*25)%>.&nbsp;<%=dsdesc%>
		       </td>

               <td valign="center" align="center" bgcolor="#EFEFEF" width="50">
                  <%=instid%>
               </td>

		       <td valign="center" bgcolor="#EFEFEF" align="center" width="200">
            <% if (orgSearch.indexOf("/pds/power.jsp") > -1) { %>
			      <a href="<%=profileURL%>"><%=dsid%></a>
            <% } else { %>
			      <a href="<%=profileURL%>">View Information for</a><br><%=dsid%>
            <% } %>
		       </td>

		       <td valign="center" bgcolor="#EFEFEF" align="center" width="150">
		          <a href="<%=constructURL(resLocURL, queryString, "")%>" target="_blank">
                  Search for Products<br>with <%=resName%></a>
		       </td>
		       <td valign="center" bgcolor="#EFEFEF" align="center" width="200">
			      <ultype="disc">       
			      <% 
			         for (int j=1; j<rvalues.size(); j++) {
			            refLid = rvalues.get(j);
			            refLid = refLid.substring(0, refLid.indexOf("::"));
		    	        resource = searchRegistry.getExtrinsic(refLid);
		    	        resName = searchRegistry.getSlotValues(resource, "resource_name").get(0);
		    	        resLocURL = searchRegistry.getSlotValues(resource, "resource_url").get(0);    		    	      
		    	   %>
			      <li><a href=<%=constructURL(resLocURL, queryString, "")%> target="_new"><%=resName%></a><br>
			      <% } %>
		       </td>
            </tr>
    <% } // end for %>         
   <%} // end else %>
         <!--/table-->
   
<%-- Result Page Navigation --%>
            <tr valign="TOP">
               <td valign="TOP" colspan="5" align=center>
<% if (listsize > 0) { %>
Page&nbsp;
<%
}
for(int k=1; k<=totalPage;k++) { 
   if (k==curPage+1) { %>
       <%=k%>&nbsp;&nbsp;
<% } else { %>
   <a href="/ds-view/pds/results.jsp?pageNo=<%=k-1%>"><%= k%></a>&nbsp;&nbsp;
<% }
}

// is there a previous page?
if (curPage > 0) { %>
<A href="/ds-view/pds/results.jsp?pageNo=<%=curPage-1%>">Previous</A>&nbsp;&nbsp;
<% } 
// is there a next page?
if (curPage+1 < totalPage) { %>
<A href="/ds-view/pds/results.jsp?pageNo=<%=curPage+1%>">Next</A>&nbsp;&nbsp;
<% } %>
&nbsp; | &nbsp;<A href="<%=orgSearch%>">New Search</A>
               </td>
             </tr>
            </td>            
         </table>
      </td>
   </tr>
</table>
</div>
</div>
<%@ include file="/pds/footer.html" %>

<%@ include file="/pds/ds_map.html" %>
</body>
</html>
