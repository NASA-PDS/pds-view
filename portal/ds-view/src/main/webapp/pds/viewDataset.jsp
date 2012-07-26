<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");

%>
<html>
<head>
        <title>PDS Data Set Profile</title>
        <META  NAME="keywords"  CONTENT="Planetary Data System">

        <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">

<link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">

<%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" contentType="text/html; charset=ISO-8859-1" import="jpl.pds.beans.*,jpl.eda.profile.*,java.util.*,java.net.*, java.io.*,java.sql.*,javax.naming.*,javax.sql.*" %>

<SCRIPT LANGUAGE="JavaScript">
<%@ include file="/pds/utils.js"%>
</SCRIPT>
</head>
<body onLoad="preloadImages();">
<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
  <tr>
    <td>
      <%@ include file="/pds/pds_header.html" %>

<%!
String getSingleElementValue (String name, Map elements) {
   if (elements != null && elements.containsKey(name)) {
       ProfileElement pe = (ProfileElement)elements.get(name);
       for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
          return (String)e.next();
       } 
   }
   return "";
}

String constructURL (String url, String dsid) {

  try {
   URL aURL = new URL(url);
   String filename = aURL.getFile();

   // do not pass any params to reslink that ends with /
   if (filename.endsWith("/")) {
      return url;
   }
   
   //added for NAIF ftp site - added 02.07.05 - was not working with dsid being appended to url
   if (url.indexOf("ftp://naif") != -1){
   		return url;
   }

   // per Pam Woncik 03/12/03 - if the URL contains "pdsimg" or "pdsimage" (ie. Atlas browsers),
   // don not append the query string
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

String fixQuery(String q) {
  String[] entity = {"&","<",">","\"","'","\\"};
  String[] replace = {"&amp;","&lt;","&gt;","&quot;","&#x27;","&#x2F","_"};

  String query = "", next="";

  for (int i=0; i<q.length(); i++) {
    next = String.valueOf(q.charAt(i));
    for (int j=0; j<entity.length; j++)
      if (next.equals(entity[j]))
        next = replace[j];

      query += next;
  }

  return query;
}
%>

<%
Connection connection = null;
LinkedList target = new LinkedList();
LinkedList alias = new LinkedList();
String resLoc = null;
String resName = null;
try
	{
	//create connection to database
	javax.naming.Context init = new javax.naming.InitialContext();
	javax.naming.Context env = (Context)init.lookup("java:comp/env");
	DataSource ds = (DataSource) env.lookup("jdbc/pdsprofile");
	connection = ds.getConnection();
	Statement statement = connection.createStatement();
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
		rs.close();
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
		"TARGET_NAME", "TARGET_TYPE", 
		"INSTRUMENT_NAME", "INSTRUMENT_ID", "INSTRUMENT_TYPE", "INSTRUMENT_HOST_ID",
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
String dsid_lower = "";
String volume = "";
String ancillary  = "";
String nodename = "";
String psclass =  "";
String localdsid =  "";
while (names.hasMoreElements()) {
   String param = (String) names.nextElement();
   if (param.equals("volume")) {
      volume = URLEncoder.encode(request.getParameter("volume"),"UTF-8");
   } else if (param.equals("nodename")) {
      nodename = URLEncoder.encode(request.getParameter("nodename"),"UTF-8");
   } else if (param.equals("ancillary")) {
      ancillary = URLEncoder.encode(request.getParameter("ancillary"),"UTF-8");
   } else if (param.equals("psclass")) {
      psclass = URLEncoder.encode(request.getParameter("psclass"),"UTF-8");
   } else if (param.equals("localdsid")) {
      localdsid = URLEncoder.encode(request.getParameter("localdsid"),"UTF-8");
   } else if (param.equals("datasetid")) {
      dsid = URLEncoder.encode(request.getParameter("datasetid"),"UTF-8");
   } else if (param.equals("dsid")) {
      dsid = URLEncoder.encode(request.getParameter("dsid"),"UTF-8");
   } else if (param.equals("Identifier")) {
      dsid = URLEncoder.encode(request.getParameter("Identifier"),"UTF-8");
   } else if (param.equals("identifier")) {
      dsid = URLEncoder.encode(request.getParameter("identifier"),"UTF-8");
   }
}



dsid = dsid.toUpperCase();
dsid_lower = dsid.toLowerCase();

if (dsid.length() == 0) {
%>
     <table width="760" border="0" cellspacing="3" cellpadding="2">
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Dataset Information</b>
                            </font><br><br>
                </td>
        </tr>

        <tr valign="TOP">
                <td bgcolor="#F0EFEF" width=200 valign=top>
                Please specify a <b>dsid</b> or <b>Identifier</b>
                </td>
        </tr>

      </table>
<%
}

else {

Profile profile = searchBean.getDatasetProfile(dsid);
Map resourceMap = null;
Map elements = null;

if (profile==null || profile.getProfileElements().isEmpty()) {
%>
  <table width="760" border="0" cellspacing="3" cellpadding="2">
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Dataset Information</b>
                            </font><br><br>
                </td>
        </tr>

        <tr valign="TOP">
                <td bgcolor="#F0EFEF" width=200 valign=top>
                  Information not found for dsid or identifier <b><%=dsid%></b>. Please verify the value.
                </td> 
        </tr>
         
      </table>

<%
}
else {
   resourceMap = searchBean.getResourceProfiles(Collections.singletonList(profile));
   elements = profile.getProfileElements();

//adding target type value so that we know where to link the userfor target information  ArrayList targetType = new ArrayList();
%>

      <table width="760" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td><img src="<%=pdshome%>images/spacer.gif" width="5" height="5" border="0"></td>
	</tr>
	<tr>
	  <td valign="top"><table width="578" border="0" cellspacing="0" cellpadding="0">
	    <tr>
	  <td>
	<!-- main content begin -->
	<table border="0" cellpadding="0" cellspacing="0">
	<tr>
	<td class="pageTitle"><font color="#6F4D0E"><%=getSingleElementValue("DATA_SET_TERSE_DESCRIPTION", elements)%></font></td>
	</tr>
	<tr>
	<td><img src="<%=pdshome%>images/gray.gif" width="760" height="1" alt="" border="0"></td>
	</tr>
	</table>
<br>
<table width="100%" border="0" cellspacing="1" cellpadding="2">
<% 
     resLoc = getSingleElementValue("RESOURCE_LINK", elements);
     resName = getSingleElementValue("RESOURCE_NAME", elements);
%>

  <tr bgcolor="#efefef">
    <td colspan=2>&nbsp;</td>
  </tr>

   <tr bgcolor="#E7EEF9">
    <td>Citation</td>
    <td><%=getSingleElementValue("CITATION_DESCRIPTION", elements)%></td>
  </tr>

  <tr bgcolor="#E7EEF9">
    <td>Access/Download Data Set</td>
    <td><a href="<%=resLoc%>">Search for Products with the <%=resName%></a></td>
  </tr>

  <tr bgcolor="#E7EEF9">
   <td>Data set abstract</td>
   <td><%=getSingleElementValue("ABSTRACT_TEXT", elements)%></td>
  </tr>


  <tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
  </tr>

  <tr bgcolor="#efefef">
    <td colspan=2><b>Additional Information</b></td>
  </tr>

  <tr bgcolor="#E7EEF9">
    <td>Mission Information</td>
    <td>
<% ProfileElement pe = null;
   String val = "";
   if (elements.containsKey("MISSION_NAME")) {
       pe = (ProfileElement) elements.get("MISSION_NAME"); 
       for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
           val = (String)e.next(); %>
           <a href="/pds/viewMissionProfile.jsp?MISSION_NAME=<%=val%>" 
             target="_blank"><%=val%></a><br>
<%     } 
   }%>
    </td>
  </tr>

  <tr bgcolor="#E7EEF9">
   <td>Dataset Information</td>
   <td><a href="/pds/viewProfile.jsp?dsid=<%=dsid%>" target="_blank"><%=dsid%></a></td>
  </tr>

  <tr bgcolor="#E7EEF9">
    <td>Instrument Host Information</td>
    <td>
<%
   String instHostId = "";
   if (elements.containsKey("INSTRUMENT_HOST_ID")) {
       pe = (ProfileElement) elements.get("INSTRUMENT_HOST_ID"); 
       for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
           instHostId = (String)e.next(); %>
           <a href="/pds/viewHostProfile.jsp?INSTRUMENT_HOST_ID=<%=instHostId%>" target="_blank"><%=instHostId%></a><br>
<%     } 
   }%>
    </td>
  </tr>

  <tr bgcolor="#E7EEF9">
    <td>Instrument Information</td>
    <td>

<%
   if (elements.containsKey("INSTRUMENT_ID")) {
       pe = (ProfileElement) elements.get("INSTRUMENT_ID"); 
       for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
           val = (String)e.next(); %>
           <a href="/pds/viewInstrumentProfile.jsp?INSTRUMENT_ID=<%=val%>&INSTRUMENT_HOST_ID=<%=instHostId%>" 
             target="_blank"><%=val%></a><br>
<%     } 
   }%>
    </td>
  </tr>
  <!--added target information link which links to viewTargetProfile.jsp-->
  <tr bgcolor="#E7EEF9">
    <td>Target Information</td>
    <td>

<%
	/*if (elements.containsKey("TARGET_TYPE")){
		pe = (ProfileElement) elements.get("TARGET_TYPE");
		for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
           val = (String)e.next();
		   targetType.add(val); 
		}
	}*/
   if (elements.containsKey("TARGET_NAME")) {
       pe = (ProfileElement) elements.get("TARGET_NAME"); 
	for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
		val = (String)e.next(); 
		//if (((String)targetType.get(0)).equals("ASTEROID") || ((String)targetType.get(0)).equals("COMET") || ((String)targetType.get(0)).equals("TRANS-NEPTUNIAN OBJ")){
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
			<%=val%><br>
		<%}else{%>
			<a href="<%=targetProfileServerURL%>"  target="_blank"><%=val%><br></a>
             
             <%
		}
		//}else {
			//String targetProfileServerURL = "/pds/viewTargetProfile.jsp?TARGET_NAME=" + val;
%>
			<!--<a href="<%//=targetProfileServerURL%>"  target="_blank"><%//=val%><br></a> -->
             
	<%	//}
	} //close   for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
   }//close  if (elements.containsKey("TARGET_NAME")) {
   %>
    </td>
  </tr>


  <tr bgcolor="#E7EEF9">
    <td>Other Resources</td>
    <td>
<% // Display "application.directory" resources first and other resources
   if (resourceMap != null && resourceMap.containsKey(dsid)) {
       List list = (List)resourceMap.get(dsid);
       for (int l=0; l<list.size(); l++) {
          String resclass = ((DatasetResource)list.get(l)).getResourceClass();
          if (resclass.equals("application.directory")) {
             String resname = ((DatasetResource)list.get(l)).getResourceName();
             String reslink = ((DatasetResource)list.get(l)).getResourceLink(); %>
              <a href=<%=constructURL(reslink, dsid)%> target="_new"><%=resname%></a><br>
   <%     }
       }
       for (int l=0; l<list.size(); l++) {
          String resclass = ((DatasetResource)list.get(l)).getResourceClass();
          if (! resclass.equals("application.directory")) {
             String resname = ((DatasetResource)list.get(l)).getResourceName();
             String reslink = ((DatasetResource)list.get(l)).getResourceLink(); %>
              <a href=<%=constructURL(reslink, dsid)%> target="_new"><%=resname%></a><br>
   <%     }
       }

   } %>
   </td>
  </tr>
<% 


  // get ancillary information 
   if (! volume.equals("")) { %>
      <tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
      </tr>
<%    

      if (! ancillary.equals("")) { %>      
        <jsp:include page="/pds/viewAncillary.jsp" flush="true">	
        <jsp:param name="dsid" value="<%=dsid_lower%>" />	
        <jsp:param name="volume" value="<%=volume%>" />
		  <jsp:param name="nodename" value="<%=nodename%>" />
		  <jsp:param name="localdsid" value="<%=localdsid%>" />
        <jsp:param name="psclass" value="<%=psclass%>" />
        <jsp:param name="ancillary" value="<%=ancillary%>" />
        </jsp:include>
<%    } else {  %>
        <jsp:include page="/pds/viewAncillary.jsp" flush="true">	
        <jsp:param name="dsid" value="<%=dsid_lower%>" />	
        <jsp:param name="volume" value="<%=volume%>" />
		  <jsp:param name="nodename" value="<%=nodename%>" />
		  <jsp:param name="localdsid" value="<%=localdsid%>" />
        <jsp:param name="psclass" value="<%=psclass%>" />
        </jsp:include>

<%    }
   }

  } // if matching profile is found
} // if dsid is specified    %>

   <%@ include file="/pds/footer.html" %>
   </table>
  </td>
  </tr>
</table>
</BODY>
</HTML>

