<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");
%>
<html>
<head>
	<title>PDS Product Search Results</title>
	<META  NAME="keywords"  CONTENT="Planetary Data System">

	<META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
	
<link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">
		<SCRIPT LANGUAGE="JavaScript">

<%@ include file="/pds/utils.js"%>


function popup(newpage) { 
window.open(newpage,'window1','toolbar=0,location=1,directories=0,status=0,menubar=0,scrollbars=yes,resizable=yes,width=640,height=480'); 
}

</SCRIPT>

<%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" 
contentType="text/html; charset=ISO-8859-1" 
import="jpl.pds.beans.ProductSearchBean, jpl.eda.xmlquery.Result , jpl.eda.profile.*, java.util.*, java.net.*, java.io.*" %>

</head>

<body BGCOLOR="#000000">

<jsp:useBean class="jpl.pds.beans.ProductSearchBean" id="searchBean" scope="session" />

<%-- Get the subset of results to be displayed for this invocation --%>
<%
String userQueryString = "";
int rowsFetch = 25;
int curPage;
int listsize = searchBean.getSearchResultsSize();
int totalPage = (int) Math.ceil((double)listsize/(double)rowsFetch);

if (searchBean == null) {
   out.println("Search Bean is null");
}

if (request.getParameterValues("pageNo") == null) {
   curPage = 0;
} else {
   curPage = Integer.parseInt(request.getParameterValues("pageNo")[0]);
}

int startRow = (curPage * rowsFetch) + 1;
List curList = searchBean.getSearchResultList(startRow,rowsFetch);
%>

<table align=center bgColor="#FFFFFF" BORDER="0" CELLPADDING="2" CELLSPACING="0" WIDTH="760">
	<tr valign="TOP">
		<td valign="TOP" colspan="5">
                    <%@ include file="pds_header.html" %>
		</td>
	</tr>
	<tr valign="TOP">
		<td valign="TOP" colspan="5">
			<br><FONT FACE="verdana" size="5"><b>Search Results</b></font> &nbsp;
                       <% if (curList != null) {
                           if (listsize > 1) { %> 
                             <FONT FACE="verdana" size="2">(<%= listsize%> products found)
                       <%  } else { %>
                             <FONT FACE="verdana" size="2">(<%= listsize%> products found)
                       <%  }
                          } %>
                 <br><br>
		</td>
	</tr>
	<tr valign="TOP">
		<td valign="TOP" bgcolor="#003366" align="center" width="300">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Product Name</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="200">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Dataset Name</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="150">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Information About<br>the Product</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="150">
			<FONT FACE="verdana" size="2" color="#FFFFFF"><b>Get Product</b></font>
		</td>
	</tr>

       <% if (curList == null) { %>
          </table>
          <font face="verdana" size="2" color="#000033"><b>No products found matching your search criteria:<br> <%=userQueryString%></b></font>
       <% } else { 
        // Loop through the profile results list 
           int c=1;
           for (Iterator i = curList.iterator(); i.hasNext(); c++) {
              Profile profile = (Profile) i.next();
              Map elements = profile.getProfileElements();
              ProfileElement pe = null;

              String productTitle = profile.getResourceAttributes().getTitle();
              	   
              String dsid = "";
              if (elements.containsKey("DATA_SET_ID")) {
                pe = (ProfileElement)elements.get("DATA_SET_ID");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   dsid = (String)e.next();
                } 
              } 
              String profileURL = "/pds/viewProfile.jsp?dsid=" + 
                                  URLEncoder.encode(dsid, "UTF-8");

              String productURL = "/pds/viewProductProfile.jsp?profId=" +
			URLEncoder.encode(profile.getProfileAttributes().getID(),"UTF-8");
              
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
		<td valign="TOP" bgcolor="#EFEFEF" align="left" width="300">
			<font face="verdana" size="1" color="#000033"><%= c+(curPage*25)%>.&nbsp; <b><%=productTitle%></b></font><br>
		</td>

		<td valign="TOP" bgcolor="#EFEFEF" align="center" width="200">
			<a href="<%=profileURL%>" target="_blank"><font face="verdana" size="1" color="#000033"><b><%=dsid%></b></font></a>
		</td>

		<td valign="TOP" bgcolor="#EFEFEF" align="center" width="150">
			<a href="<%=productURL%>" target="_blank"><font face="verdana" size="1" color="#000033"><b>View Profile</b></font></a>
		</td>

		<td valign="TOP" bgcolor="#EFEFEF" align="center" width="150">
                        <a href="<%=resLocURL%>"><font face="verdana" size="1" color="#000033"><b>Get Product</b></font></a>
		</td>
	</tr>
    <%  } %>
    </table>
    <%} %>
<p>
<%-- Result Page Navigation --%>
<center>
<% if (listsize > 0) { %>
Page&nbsp;
<%
}
for(int k=1; k<=totalPage;k++) { 
   if (k==curPage+1) { %>
       <%=k%>&nbsp;&nbsp;
<% } else { %>
   <a href="/pds/productResults.jsp?pageNo=<%=k-1%>"> <%= k%></a>&nbsp;&nbsp;
<% }
}

// is there a previous page?
if (curPage > 0) { %>
<A href="/pds/productResults.jsp?pageNo=<%=curPage-1%>">Previous</A>&nbsp;&nbsp;
<% } 
// is there a next page?
if (curPage+1 < totalPage) { %>
<A href="/pds/productResults.jsp?pageNo=<%=curPage+1%>">Next</A>&nbsp;&nbsp;
<% } %>
</center>

<%-- Footer --%>
<FONT FACE="verdana" size="1">
<P>Last Updated: 
<SCRIPT LANGUAGE="JavaScript">
document.write(new Date())
</SCRIPT>
</font>

</body>
</html>
