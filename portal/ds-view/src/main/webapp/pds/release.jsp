<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");
%>
<html>
<head>
	<title>PDS New Data Sets Releases</title>
	<META  NAME="keywords"  CONTENT="Planetary Data System">

	<META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
<link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">	
		<SCRIPT LANGUAGE="JavaScript">

<%@ include file="/pds/utils.js"%>



function popup(newpage) { 
window.open(newpage, "", "width=640, height=480, scrollbars=yes, resizable=yes, toolbar=yes, status=no, location=no, menubar=no"); 
}


</SCRIPT>

<%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" contentType="text/html; charset=ISO-8859-1" import="jpl.pds.beans.SearchBean, jpl.pds.beans.DatasetResource, jpl.eda.xmlquery.Result , jpl.eda.profile.*, java.util.*, java.net.*, java.io.*" %>

</head>

<body BGCOLOR="#000000">

<jsp:useBean class="jpl.pds.beans.SearchBean" id="searchBean" scope="session" />

<%! 
String constructURL (String url, String releaseid) {

  try {
   URL aURL = new URL(url);
   String filename = aURL.getFile();

   // don't pass any params to reslink that ends with /
   if (filename.endsWith("/")) {
      return url;
   }

   int pos = filename.indexOf('?');
   String browserURL = url;
   if (! releaseid.equals("")) {
      String id = "";
      if (releaseid.length() == 1 ) {
         id =  "000" + releaseid;
      } else if (releaseid.length() == 2) {
         id = "00" + releaseid;
      } else if (releaseid.length() == 3) {
         id = "0" + releaseid;
      }
      if (pos > 0) {
         browserURL += "&RELEASE_ID=" + URLEncoder.encode(id, "UTF-8");
      } else {
         browserURL += "?RELEASE_ID=" + URLEncoder.encode(id, "UTF-8");
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

if (request.getParameterValues("pageNo") == null) {
   curPage = 0;
   searchBean.getDatasetRelease();
} else {
   curPage = Integer.parseInt(request.getParameterValues("pageNo")[0]);
}

int totalPage = (int) Math.ceil((double)searchBean.getDatasetReleaseSearchResultsSize()/(double)rowsFetch);
int startRow = (curPage * rowsFetch) + 1;
int listsize = searchBean.getDatasetReleaseSearchResultsSize();
List curList = searchBean.getDatasetReleaseResultList(startRow,rowsFetch);

String queryString = (request.getSession().getAttribute("queryString") != null ?
                      (String)request.getSession().getAttribute("queryString") : "");

String orgSearch = (request.getSession().getAttribute("requestURI") != null ? 
                     (String)request.getSession().getAttribute("requestURI") : 
	             "/pds/index.jsp");

%>

<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
  <tr>
    <td>
      <%@ include file="/pds/pds_header.html" %>

      <table width="760" border="0" cellspacing="2" cellpadding="2">
       <tr>
                <td valign="TOP" colspan="5" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Latest Mission Data Releases</b></font> &nbsp;
                       <% if (curList != null) {
                           if (listsize > 1) { %>
                             <FONT size="2">(<%= listsize%> data sets found)</FONT>
                       <%  } else { %>
                             <FONT size="2">(<%= listsize%> data set found)</FONT>
                       <%  }
                          } %>
                 <br><br>
                </td>
        </tr>

	<tr valign="TOP">
		<td valign="TOP" bgcolor="#003366" align="center" width="200">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Data Set</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="25">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Release<br>ID</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="300">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Release Description</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="75">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Release<br> Date</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="50">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Release<br>Medium</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="50">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Product<br>Type</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="50">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Status</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="50">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Data Products</b></font>
		</td>
	</tr>

       <% if (curList == null) { %>
        <tr valign="TOP">
         <td valign="TOP" colspan="8">
          <font face="verdana" size="2" color="#000033"><b>No Data Sets Found</b></font>
         </td>
       </tr>
       <% } else { 
        // Loop through the profile results list 
           int c=1;
           for (Iterator i = curList.iterator(); i.hasNext(); c++) {
	      Profile profile = (Profile) i.next();
              Map elements = profile.getProfileElements();
              ProfileElement pe = null;
	      String dsname = "";
	      if (elements.containsKey("DATA_SET_NAME")) {
                  pe = (ProfileElement)elements.get("DATA_SET_NAME");
                  for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                       dsname = (String)e.next();
                  }
              }   

	      // display data_set_id if name is null
              if (dsname.equals("") || dsname.equalsIgnoreCase("NULL")) {
			if (elements.containsKey("DATA_SET_ID")) {
				pe = (ProfileElement)elements.get("DATA_SET_ID");
				for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
					dsname = (String)e.next();
				}
			}
	      }

              String reldesc = "";
              if (elements.containsKey("RELEASE_DESCRIPTION")) {
                pe = (ProfileElement)elements.get("RELEASE_DESCRIPTION");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   reldesc = (String)e.next();
                } 
              } 
 
              String reldate = "";
              if (elements.containsKey("RELEASE_DATE")) {
                pe = (ProfileElement)elements.get("RELEASE_DATE");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   reldate = (String)e.next();
                } 
              }
 
              String relmedium = "";
              if (elements.containsKey("RELEASE_MEDIUM")) {
                pe = (ProfileElement)elements.get("RELEASE_MEDIUM");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   relmedium = (String)e.next();
                } 
              }

              String archstat = "";
              if (elements.containsKey("RELEASE_ARCHIVE_STATUS")) {
                pe = (ProfileElement)elements.get("RELEASE_ARCHIVE_STATUS");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   archstat = (String)e.next();
                } 
              }

              String releaseid = "";
              if (elements.containsKey("RELEASE_ID")) {
                pe = (ProfileElement)elements.get("RELEASE_ID");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   releaseid = (String)e.next();
                } 
              }

              String paramText = "";
              if (elements.containsKey("RELEASE_PARAMETER_TEXT")) {
                pe = (ProfileElement)elements.get("RELEASE_PARAMETER_TEXT");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   paramText = (String)e.next();
                } 
              }
              String prodtype = "";
              if (elements.containsKey("PRODUCT_TYPE")) {
                pe = (ProfileElement)elements.get("PRODUCT_TYPE");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   prodtype= (String)e.next();
                } 
              }

              String resLocURL = "";
              for (Iterator e= profile.getResourceAttributes().getResLocations().iterator(); e.hasNext();) {
                 resLocURL = (String)e.next();
              }
              if (resLocURL.equalsIgnoreCase("TBD") || resLocURL.equalsIgnoreCase("NA") ||
                  resLocURL.equalsIgnoreCase("N/A")) {
                  resLocURL = "";
              }
              %>
 
	<tr valign="top">
		<td valign="TOP" bgcolor="#EFEFEF" align="left" width="200">
			<%= c+(curPage*25)%>.&nbsp; <%=dsname%>
		</td>

		<td valign="TOP" bgcolor="#EFEFEF" align="center" width="25">
			<%=releaseid%>
		</td>
		<td valign="TOP" bgcolor="#EFEFEF" align="left" width="300">
			<%=reldesc%>
		</td>

		<td nowrap valign="TOP" bgcolor="#EFEFEF" align="center" width="75">
			<%=reldate%>
		</td>
		<td valign="TOP" bgcolor="#EFEFEF" align="center" width="50">
			<%=relmedium%>
		</td>
		<td valign="TOP" bgcolor="#EFEFEF" align="center" width="50">
			<%=prodtype%>
		</td>
                <td valign="TOP" bgcolor="#EFEFEF" align="center" width="50">
                        <%=archstat%>
                </td>
		<td valign="TOP" bgcolor="#EFEFEF" align="center" width="50">
                   <% if (! resLocURL.equals("") && ! resLocURL.equalsIgnoreCase("OFFLINE")) { %>
			<a href="<%=resLocURL+paramText%>" target="_blank">Get Data</a>
                   <% } else { %>
                        <%=resLocURL%>
                   <% } %>
		</td>
	</tr>
    <%  } 
    } %>
<%-- Result Page Navigation --%>

<tr valign="TOP">
<td valign="TOP" colspan="8" align=center>
<% if (listsize > 0) { %>
Page&nbsp;
<%
}
for(int k=1; k<=totalPage;k++) {
   if (k==curPage+1) { %>
       <%=k%>&nbsp;&nbsp;
<% } else { %>
   <a href="/pds/release.jsp?pageNo=<%=k-1%>"><%= k%></a>&nbsp;&nbsp;
<% }
}

// is there a previous page?
if (curPage > 0) { %>
<A href="/pds/release.jsp?pageNo=<%=curPage-1%>">Previous</A>&nbsp;&nbsp;
<% }
// is there a next page?
if (curPage+1 < totalPage) { %>
<A href="/pds/release.jsp?pageNo=<%=curPage+1%>">Next</A>&nbsp;&nbsp;
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
