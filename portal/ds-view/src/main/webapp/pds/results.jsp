<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");
%>
<html>
<head>
<title>PDS Data Set Search Results</title>
<META  NAME="keywords"  CONTENT="Planetary Data System">

<META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS plane
tary archives.">
<link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">

<%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" contentType="text/html; charset=ISO-8859-1" import="jpl.pds.beans.SearchBean, jpl.pds.beans.DatasetResource, jpl.eda.xmlquery.Result , jpl.eda.profile.*, java.util.*, java.net.*, java.io.*" %>

<script language="JavaScript"
src="<%=pdshome%>js/popWindow.js"></script>

<SCRIPT LANGUAGE="JavaScript">
<%@ include file="/pds/utils.js"%>
</SCRIPT>
</head>

<body onLoad="preloadImages();">

<jsp:useBean class="jpl.pds.beans.SearchBean" id="searchBean" scope="session"/>
<% System.err.println("=== SearchBean in result.jsp is " + System.identityHashCode(searchBean)); %>

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
int rowsFetch = 25;
int curPage;
int listsize = searchBean.getSearchResultsSize();
int totalPage = (int) Math.ceil((double)listsize/(double)rowsFetch);


System.err.println("List size from results.jsp = " + listsize);


if (request.getParameterValues("pageNo") == null) {
   curPage = 0;
} else {
   curPage = Integer.parseInt(request.getParameterValues("pageNo")[0]);
}

int startRow = (curPage * rowsFetch) + 1;
List curList = null;
Map resourceMap = null;
if (listsize > 0) {
   curList = searchBean.getSearchResultList(startRow,rowsFetch);
   if (curList != null && curList.size() > 0) {
      resourceMap = searchBean.getResourceProfiles(curList);
   }
}

String queryString = (request.getSession().getAttribute("queryString") != null ?
                      (String)request.getSession().getAttribute("queryString") : "");

String userQueryString = (request.getSession().getAttribute("message") != null ?
                      (String)request.getSession().getAttribute("message") : "");

String title = "Search Results";
if (queryString.indexOf("MISSION_STOP_DATE=UNK") > -1) {
   // this page is called to display active mission data sets
   title =  "Active Mission Data Sets";
   queryString = "";
}
String orgSearch = (request.getSession().getAttribute("requestURI") != null ? 
                     (String)request.getSession().getAttribute("requestURI") : 
	             "/pds/index.jsp");
// replace blank with underscore because Atlas cannot handle blank


if (! queryString.equals("")) {
    //queryString = queryString.replace('+','_');
queryString = queryString.trim();
queryString = java.net.URLEncoder.encode(queryString);
}

%>

<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
  <tr>
    <td>
      <%@ include file="/pds/pds_header.html" %>

      <table width="760" border="0" cellspacing="2" cellpadding="2">
       <tr>
                     <td colspan=5>
                      <table border="0" cellpadding="0" cellspacing="0" width="760">
                        <tr>
                          <td class="pageTitle" align=left><font color="#6F4D0E"><b><%=title%></b>&nbsp;(<%= listsize%> data sets found)</font>
                          </td>
                          <td align=right valign=center color="#6F4D0E">
                            <A href="javascript:openWindow('/pds/search_result_help.html', 'Result',550,500)"> <IMG SRC="/pds/images/btn_help.gif" BORDER=0></A>
                          </td>
                        </tr>
                      </table>
                     </td>
	</tr>
        <tr><td>&nbsp;</td></tr>
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
              Profile profile = (Profile) i.next();
              Map elements = profile.getProfileElements();
              ProfileElement pe = null;
              String dsdesc = "";
 	      if (elements.containsKey("DATA_SET_TERSE_DESCRIPTION")) {
                 pe = (ProfileElement)elements.get("DATA_SET_TERSE_DESCRIPTION");
                 // data set should only have 1 description
                 for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                    dsdesc = (String)e.next();
                 }
              }
	      String dsname = "";
	      if (elements.containsKey("DATA_SET_NAME")) {
                  pe = (ProfileElement)elements.get("DATA_SET_NAME");
                  for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                       dsname = (String)e.next();
                  }
              }   

              // display data_set_name if description is null
              if (dsdesc.equals("") || dsdesc.equalsIgnoreCase("NULL") ||
                  dsdesc.equalsIgnoreCase("N/A") || dsdesc.equalsIgnoreCase("NA") ||
                  dsdesc.equalsIgnoreCase("TBD")) {
                  dsdesc = dsname;
              }

              String dsid = "";
              if (elements.containsKey("DATA_SET_ID")) {
                pe = (ProfileElement)elements.get("DATA_SET_ID");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   dsid = (String)e.next();
                } 
              } 

              String instname= "";
	      String instid = "";
              if (elements.containsKey("INSTRUMENT_HOST_ID")) {
                pe = (ProfileElement)elements.get("INSTRUMENT_HOST_ID");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   instid = (String)e.next();
                } 
              }
 
              String profileURL = "/pds/viewDataset.jsp?dsid=" + 
                                  URLEncoder.encode(dsid, "UTF-8");
	/*
              for (Iterator e= profile.getResourceAttributes().getResLocations().iterator(); e.hasNext();) {
                 resLocURL = (String)e.next();
              }
              if (resLocURL.equalsIgnoreCase("TBD") || resLocURL.equalsIgnoreCase("NA") ||
                  resLocURL.equalsIgnoreCase("N/A")) {
                  resLocURL = "";
              }
	*/
              String resLocURL = "";
              if (elements.containsKey("RESOURCE_LINK")) {
                pe = (ProfileElement)elements.get("RESOURCE_LINK");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   resLocURL = (String)e.next();
                } 
              }
              String resName = "";
              if (elements.containsKey("RESOURCE_NAME")) {
                pe = (ProfileElement)elements.get("RESOURCE_NAME");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   resName = (String)e.next();
                }  
              }
              %>
 
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
			<a href="<%=profileURL%>">View Information for</a><br>
                           <%=dsid%>
                   <% } %>
		</td>

		<td valign="center" bgcolor="#EFEFEF" align="center" width="150">
                   <% if (! resLocURL.equals("") && ! resLocURL.equalsIgnoreCase("OFFLINE")) { %>
			<a href="<%=constructURL(resLocURL, queryString, "")%>" target="_blank">
			Search for Products<br>with <%=resName%></a>
                   <% } else { %>
                        <%=resLocURL%>
                   <% } %>
		</td>
		<td valign="center" bgcolor="#EFEFEF" align="center" width="200">
			<ultype="disc">
                  <%  // find the resource profiles for this data set from the resource map
                      if (resourceMap != null && resourceMap.containsKey(dsid)) {
                        List list = (List)resourceMap.get(dsid);
                        for (int l=0; l<list.size(); l++) {
                          String resname = ((DatasetResource)list.get(l)).getResourceName();
                          String reslink = ((DatasetResource)list.get(l)).getResourceLink(); %>
			<li><a href=<%=constructURL(reslink, queryString, "")%> target="_new"><%=resname%></a><br>
                  <%    }
                      } else { %>
                        &nbsp;
                  <%  } %>
		</td>
	</tr>
    <%  } %>
    <%} %>
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
   <a href="/pds/results.jsp?pageNo=<%=k-1%>"><%= k%></a>&nbsp;&nbsp;
<% }
}

// is there a previous page?
if (curPage > 0) { %>
<A href="/pds/results.jsp?pageNo=<%=curPage-1%>">Previous</A>&nbsp;&nbsp;
<% } 
// is there a next page?
if (curPage+1 < totalPage) { %>
<A href="/pds/results.jsp?pageNo=<%=curPage+1%>">Next</A>&nbsp;&nbsp;
<% } %>
&nbsp; | &nbsp;<A href="<%=orgSearch%>">New Search</A>
</td>

	     </tr>
           </td>
        <%@ include file="/pds/footer.html" %>
	</table>

    </td>
  </tr>
</table>
</body>
</html>
