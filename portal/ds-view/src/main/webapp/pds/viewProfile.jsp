<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");
%>
<html>
<head>
        <title>PDS Data Set Profile</title>
        <META  NAME="keywords"  CONTENT="Planetary Data System">

        <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary
archives.">
<link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">
<%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" contentType="text/html; charset=ISO-8859-1" import="jpl.pds.beans.*,jpl.eda.profile.*,java.util.*,java.net.*,java.io.*,java.sql.*,javax.naming.*,javax.sql.*" %>

<SCRIPT LANGUAGE="JavaScript">
<%@ include file="/pds/utils.js"%>
</SCRIPT>
</head>

<body BGCOLOR="#000000">

<%!
String constructURL (String url, String dsid) {

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
       url.indexOf("pds-imaging")>0 ) {
      return url;
   }

   int pos = filename.indexOf('?');
   String browserURL = url;
   if (pos > 0) {
         browserURL += "&DATA_SET_ID=" + dsid;
   } else {
         browserURL += "?DATA_SET_ID=" + dsid;
   }

   return browserURL;

  } catch (java.net.MalformedURLException e) {
     e.printStackTrace();
     return url;
  }
}
%>

<%
//Connection to database to get SBN aliases
Connection connection = null;
LinkedList target = new LinkedList();
LinkedList alias = new LinkedList();
try
	{
		//create connection to database
		javax.naming.Context init = new javax.naming.InitialContext();
		javax.naming.Context env = (Context)init.lookup("java:comp/env");
		DataSource ds = (DataSource) env.lookup("jdbc/pdsprofile");
		connection = ds.getConnection();
		Statement statement = connection.createStatement();
		/*Class.forName("com.sybase.jdbc2.jdbc.SybDriver");
		connection = DriverManager.getConnection("jdbc:sybase::Tds:schema.jpl.nasa.gov:4100/dbonlinedev?user=pdsnode&password=pdsnode");
		Statement stmt = connection.createStatement();*/
		String query = "select targname, sbntarglocator from targetinfo ORDER by targname";
		ResultSet rs = statement.executeQuery(query);
		while(rs.next()){
			String trimmed = rs.getString(1);
			trimmed = trimmed.trim();
			target.add(trimmed);
			trimmed = rs.getString(2);
			trimmed = trimmed.trim();
			alias.add(trimmed);
		}
		statement.close();
	} catch (NamingException e){
%>
		<h1>Error Occurred</h1>
		<b>Exception: Naming Exception</b>  <%= e.getMessage() %>
<%
	}catch (SQLException e){
%>
		<h1>Error Occurred</h1>
		<b>Exception: Database Access Failed</b>  <%= e.getMessage() %>
<%
	}finally{
		if (connection != null ){
			connection.close();
		}
	}



String[] displayedElements = {"DATA_SET_NAME", "DATA_SET_ID", "NSSDC_DATA_SET_ID",
		"DATA_SET_TERSE_DESCRIPTION", "DATASET_DESCRIPTION",
		"DATA_SET_RELEASE_DATE", "RESOURCE_LINK",
		"DATA_OBJECT_TYPE",  "START_TIME", "STOP_TIME",
		"MISSION_NAME", "MISSION_START_DATE", "MISSION_STOP_DATE",
		"TARGET_NAME", "TARGET_TYPE", "INSTRUMENT_HOST_ID",
		"INSTRUMENT_NAME", "INSTRUMENT_ID", "INSTRUMENT_TYPE",
		"NODE_NAME","ARCHIVE_STATUS", "CONFIDENCE_LEVEL_NOTE",
		"CITATION_DESCRIPTION", "ABSTRACT_TEXT", "FULL_NAME", "TELEPHONE_NUMBER",
		"RESOURCES"};

String queryString = (request.getSession().getAttribute("queryString") != null ?
                      (String)request.getSession().getAttribute("queryString") : "");

String orgSearch = (request.getSession().getAttribute("requestURI") != null ?
                     (String)request.getSession().getAttribute("requestURI") :
                     "/pds/index.jsp");

jpl.pds.beans.SearchBean searchBean =  new jpl.pds.beans.SearchBean();
/*
** SearchBean.getDatasetProfile() assumes that you pass it "dsid"
*/
Enumeration names = request.getParameterNames();
String dsid = "";
int desc_flag = 0;

while (names.hasMoreElements()) {
   dsid = request.getParameterValues((String) names.nextElement())[0];
}
if (dsid.length() == 0) {
   response.setStatus(javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
   return;
}
Profile profile = searchBean.getDatasetProfile(dsid);
Map resourceMap = null;
if  (profile != null && ! profile.getProfileElements().isEmpty()) {
   resourceMap = searchBean.getResourceProfiles(Collections.singletonList(profile));
}

%>
<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
  <tr>
    <td>
      <%@ include file="/pds/pds_header.html" %>

      <table width="760" border="0" cellspacing="3" cellpadding="2">

<% if (profile == null || profile.getProfileElements().isEmpty()) { %>
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Data Set Not Found</b></font><br><br>
                </td>
        </tr>

<% } else { %>
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Data Set Information</b></font><br><br>
                </td>
        </tr>
<%
Map elements = profile.getProfileElements();
String hostId = "";
%>
      <% for (int i=0; i<displayedElements.length; i++) {
             if (elements.containsKey(displayedElements[i]) || 
		 displayedElements[i].equals("RESOURCES")) { 
        %>

               <TR>
                 <td bgcolor="#F0EFEF" width=200 valign=top><%=displayedElements[i]%></td> 
                 <td bgcolor="#F0EFEF" valign=top>

              <%  if (displayedElements[i].equals("RESOURCES")) { 
                      if (resourceMap != null && resourceMap.containsKey(dsid)) {
                        List list = (List)resourceMap.get(dsid);
                        for (int l=0; l<list.size(); l++) {
                          String resname = ((DatasetResource)list.get(l)).getResourceName();
                          String reslink = ((DatasetResource)list.get(l)).getResourceLink(); %>
                        <li><a href=<%=constructURL(reslink, dsid)%> target="_new"><%=resname%></a><br> 
                   <%   }
                      }    %>
                   </td>   

         <%       } else { // else11


                      if ( displayedElements[i].equals ("DATASET_DESCRIPTION") ) {
						        desc_flag = 1; %>
                      <pre><tt> <%
                       } else {
                          desc_flag = 0;
                       }  
                        ProfileElement pe = (ProfileElement)elements.get(displayedElements[i]); 
                        for (Iterator e = pe.getValues().iterator(); e.hasNext();) 
                        { // loop within a displayed Element
                           String val = (String)e.next();

                           if (desc_flag==1) {
	                             val = val.replaceAll("\r","");
                                val = val.replaceAll("\n","");
                           }
			

                           if (val.startsWith("http://") || val.startsWith("HTTP://")) { %>
                        <a href="<%=val%>" target="_blank"><%=val%><br></a>
        <%                 } else if (displayedElements[i].equals("MISSION_NAME")) {
                                 String missionProfileServerURL = "/pds/viewMissionProfile.jsp?MISSION_NAME=" + val;
%>
                        <a href="<%=missionProfileServerURL%>"  target="_blank"><%=val%><br></a>
			<%   
					           } else if (displayedElements[i].equals("TARGET_NAME")) {
						             String targetProfileServerURL = "";
						             for (int x = 0; x < target.size(); x++){
							             if (((String)target.get(x)).equals(val.toUpperCase())){
								               String SBNalias = (String)alias.get(x);
								               if (SBNalias.equals(("CN_CATALOG").trim())){
									               targetProfileServerURL = "/pds/viewTargetProfile.jsp?TARGET_NAME=" + val;
								               }else if (SBNalias.equals(("UNK").trim())){
									               targetProfileServerURL = "";
								               }else{
									               targetProfileServerURL = "http://pds-smallbodies.astro.umd.edu/SBNcgi/sbdbatt?mstring=" + SBNalias + "&mtype=part";
								               }
							             }
						             }
						             if (targetProfileServerURL == null || targetProfileServerURL.equals("")){
%>
							<%=val%><br><%
						             }else{%>
							<a href="<%=targetProfileServerURL%>"  target="_blank"><%=val%><br></a>
             
             <%
						             }

			                 } else if (displayedElements[i].equals("INSTRUMENT_HOST_ID")) {
                                 hostId = val;
                                 String hostProfileServerURL = "/pds/viewHostProfile.jsp?INSTRUMENT_HOST_ID=" + val;
%>
                      <a href="<%=hostProfileServerURL%>"  target="_blank"><%=val%><br></a>

             <%           } else if (displayedElements[i].equals("INSTRUMENT_ID")) {
                                 String instrumentProfileServerURL = "/pds/viewInstrumentProfile.jsp?INSTRUMENT_ID=" + val + "&INSTRUMENT_HOST_ID=" + hostId;
%>
                      <a href="<%=instrumentProfileServerURL%>"  target="_blank"><%=val%><br></a>
            <%            } else { 
                                 if (desc_flag==1) { %>
<%=val%><%} // this code has to locat at the beginning of the source line, otherwise the alignment of the PRE would be off. 
                                 else { %>
                      <%=val%><br>
             <%                   }
                           }
                   } // end loop within a displayed Element  

                   if (desc_flag == 1) { %>
                </tt></pre>
             <%     } %>
                </td>
             <%     } // end else11 %>
               </TR>
         <%
             } // end if
         } // for loop
         %>
        <%@ include file="/pds/footer.html" %>
        </table>

    </td>
  </tr>
</table>
<% } %>
</BODY>
</HTML>

