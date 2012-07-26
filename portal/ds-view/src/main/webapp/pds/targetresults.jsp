<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");
%>
<html>
<head>
<title>PDS Target Search Results</title>
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

<jsp:useBean class="jpl.pds.beans.SearchBean" id="targetSearchBean" scope="session"/>
<% System.err.println("=== SearchBean in targetresults.jsp is " + System.identityHashCode(targetSearchBean)); %>

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
   if (url.indexOf("pdsimg") > 0 || url.indexOf("pdsimage") > 0 ||
       url.indexOf("pds-imaging")>0) { 
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
String profileURL = null;
int rowsFetch = 25;
int curPage;
int listsize = targetSearchBean.getSearchResultsSize();
int totalPage = (int) Math.ceil((double)listsize/(double)rowsFetch);


System.err.println("List size from targetresults.jsp = " + listsize);


if (request.getParameterValues("pageNo") == null) {
   curPage = 0;
} else {
   curPage = Integer.parseInt(request.getParameterValues("pageNo")[0]);
}

int startRow = (curPage * rowsFetch) + 1;
List curList = null;
Map resourceMap = null;
if (listsize > 0) {
   curList = targetSearchBean.getSearchResultList(startRow,rowsFetch);
   /*if (curList != null && curList.size() > 0) {
      resourceMap = targetSearchBean.getResourceProfiles(curList);
   }*/
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
	             "/pds/targetSearch.jsp");
// replace blank with underscore because Atlas cannot handle blank
if (! queryString.equals("")) {
    queryString = queryString.replace('+','_');
}

%>

<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
  <tr>
    <td>
      <%@ include file="/pds/pds_header.html" %>

      <table width="760" border="0" cellspacing="2" cellpadding="2">
       <tr>
                     <td colspan=4>
                      <table border="0" cellpadding="0" cellspacing="0" width="760">
                        <tr>
                          <td class="pageTitle" align=left><font color="#6F4D0E"><b><%=title%></b>&nbsp;(<%= listsize%> targets found)</font>
                          </td>
                          <td align=right valign=center color="#6F4D0E">
                            <A href="javascript:openWindow('/pds/target_search_result_help.html', 'Result',550,700)"> <IMG SRC="/pds/images/btn_help.gif" BORDER=0></A>
                          </td>
                        </tr>
                      </table>
                     </td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
		<tr valign="TOP">
		<td valign="TOP" bgcolor="#003366" align="center" width="50">
			<font size="2" color="#FFFFFF"><b>Target</b></font>
		</td>
         <td valign="TOP" bgcolor="#003366" align="center" width="350">
			<font size="2" color="#FFFFFF"><b>Terse Description</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="150">
			<font size="2" color="#FFFFFF"><b>Information About<br>the Target</b></font>
		</td>
		<td valign="TOP" bgcolor="#003366" align="center" width="200">
			<font size="2" color="#FFFFFF"><b>Resources</b></font>
		</td>
	</tr>

       <% if (curList == null) { %>
	<tr valign="TOP">
         <td valign="TOP" colspan="4">
          <font color="#FFFFFF" size="2"><b>No targets found matching your search criteria:<br> <%=userQueryString%></b></font></td>
	</tr>
       <% } else { 
        // Loop through the profile results list 
           int c=1;
           for (Iterator i = curList.iterator(); i.hasNext(); c++) {
              Profile profile = (Profile) i.next();
              Map elements = profile.getProfileElements();
              ProfileElement pe = null;
              String targdesc = "";
 	      if (elements.containsKey("TARGET_TERSE_DESCRIPTION")) {
                 pe = (ProfileElement)elements.get("TARGET_TERSE_DESCRIPTION");
                 // target should only have 1 description
                 for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                    targdesc = (String)e.next();
                 }
              }
	      String targname = "";
	      if (elements.containsKey("TARGET_NAME")) {
                  pe = (ProfileElement)elements.get("TARGET_NAME");
                  for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                       targname = (String)e.next();
                  }
              }
			  
			  
			String sbnlocator = "";
	      	if (elements.containsKey("SBN_TARGET_LOCATOR")) {
                  pe = (ProfileElement)elements.get("SBN_TARGET_LOCATOR");
                  for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                       sbnlocator = (String)e.next();
                  }
              } 

 			if (sbnlocator.equals(("CN_CATALOG").trim())){
					profileURL = "/pds/viewTargetProfile.jsp?TARGET_NAME=" + 
                                  URLEncoder.encode(targname, "UTF-8");
				}else if (sbnlocator.equals(("UNK").trim())){
					profileURL = "";
				}else{
					profileURL = "http://pds-smallbodies.astro.umd.edu/SBNcgi/sbdbatt?mstring=" + sbnlocator + "&mtype=part";
				}
              //String profileURL = "/pds/viewTargetProfile.jsp?TARGET_NAME=" + URLEncoder.encode(targname, "UTF-8");
	/*
              for (Iterator e= profile.getResourceAttributes().getResLocations().iterator(); e.hasNext();) {
                 resLocURL = (String)e.next();
              }
              if (resLocURL.equalsIgnoreCase("TBD") || resLocURL.equalsIgnoreCase("NA") ||
                  resLocURL.equalsIgnoreCase("N/A")) {
                  resLocURL = "";
              }
	*/
              ArrayList resLocURL = new ArrayList();
              if (elements.containsKey("RESOURCE_LINK")) {
                pe = (ProfileElement)elements.get("RESOURCE_LINK");
                for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                   resLocURL.add(e.next());
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
		<td valign="center" align="left" width="50" bgcolor="#EFEFEF">
			<%= c+(curPage*25)%>.&nbsp;<%=targname%>
		</td>

                <td valign="center" align="center" bgcolor="#EFEFEF" width="350">
                  <%=targdesc%>
                </td>

		<td valign="center" bgcolor="#EFEFEF" align="center" width="150">
                   <%if (profileURL != null && !profileURL.equals("")){
				   		%><a href="<%=profileURL%>" target="_blank">View Information for</a><br>
                           <%=targname%>
				   <%}else{
				   		%>No information exists for this target
				   <%}%>
		</td>

		<td valign="center" bgcolor="#EFEFEF" align="center" width="200">
			<ultype="disc">
                  <%  /* find the resource profiles for this data set from the resource map
                      if (resourceMap != null && resourceMap.containsKey(targname)) {
                        List list = (List)resourceMap.get(targname);
                        for (int l=0; l<list.size(); l++) {
                          String resname = ((DatasetResource)list.get(l)).getResourceName();
                          String reslink = ((DatasetResource)list.get(l)).getResourceLink(); */
						  
			for (int x = 0; x < resLocURL.size(); x++){%>
				<li><a href=<%=resLocURL.get(x)%> target="_new"><%
				if (((String)resLocURL.get(x)).indexOf("http://solarsystem.jpl.nasa.gov/missions") != -1){ 
					out.print("Solar System Exploration Missions");
				}else if (((String)resLocURL.get(x)).indexOf("http://solarsystem.jpl.nasa.gov/planets") != -1){
					out.print("Solar System Exploration");
				}else{
				%>&nbsp;<%
				}
				%><%//=resName.get(x)%></a><br>
          <%}
                      //} else { %>
                        <!--&nbsp;-->
                  <% // } %>
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
   <a href="/pds/targetresults.jsp?pageNo=<%=k-1%>"><%= k%></a>&nbsp;&nbsp;
<% }
}

// is there a previous page?
if (curPage > 0) { %>
<A href="/pds/targetresults.jsp?pageNo=<%=curPage-1%>">Previous</A>&nbsp;&nbsp;
<% } 
// is there a next page?
if (curPage+1 < totalPage) { %>
<A href="/pds/targetresults.jsp?pageNo=<%=curPage+1%>">Next</A>&nbsp;&nbsp;
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
