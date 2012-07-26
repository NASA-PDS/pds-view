<html>
<head>
        <title>PDS Target Profile</title>
        <META  NAME="keywords"  CONTENT="Planetary Data System">

        <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary
archives.">
<link href="http://pds.jpl.nasa.gov/css/pds_style.css" rel="stylesheet" type="text/css">
<%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" contentType="text/html; charset=ISO-8859-1" import="jpl.eda.xmlquery.*,jpl.eda.profile.*,jpl.eda.profile.ProfileElement,java.util.*, java.io.*,java.sql.*" %>

<SCRIPT LANGUAGE="JavaScript">
<%@ include file="/pds/utils.js"%>
</SCRIPT>

</head>

<body BGCOLOR="#000000">

<%//get list of target display elements for this
String[] displayedElements = {"TARGET_NAME", "RESOURCE_LINK", "RESOURCE_NAME"};

String allTargetProfileElements = " AND RETURN=dsid AND RETURN=dsname AND RETURN=targname " +
         "AND RETURN=targtype AND RETURN=msnname AND RETURN=msnstrtdate AND RETURN=msnstopdate " +
         "AND RETURN=targtersedec AND RETURN=sbntarglocator " +
 
         "AND RETURN=dataobjtype AND RETURN=strttime " +
         "AND RETURN=stoptime AND RETURN=instid AND RETURN=instname AND RETURN=insttype " +
         "AND RETURN=insthostid AND RETURN=insthostname AND RETURN=insthosttype AND RETURN=dsreleasedt " +
         "AND RETURN=nodename AND RETURN=archivestat "  +
         "AND RETURN=citdesc AND RETURN=abstract " +
         "AND RETURN=fullname AND RETURN=telephonenum AND RETURN=reslink AND RETURN=resname " +
         "AND RETURN=dstersedesc AND RETURN=nssdcdsid";

				//find out of the profile client just replace Host with Target
//System.setProperty("rmiregistries","rmi://jwang-dev");
ProfileClient profileClient = new ProfileClient("urn:eda:rmi:JPL.PDS.Profile");
String targetId = request.getParameterValues("TARGET_NAME")[0];
String text = "resclass=target AND targname=" + targetId + allTargetProfileElements;
XMLQuery xml_query = new XMLQuery(/*keywordQuery*/text,
                        /*id*/"OODT_XML_QUERY_V0.1",
                        /*title*/"OODT_XML_QUERY - Bean Query", 
			/*desc*/"This query can be handled by the OODT System",
                        /*ddId*/null,
			/*resultModeId*/"profile",
			/*propType*/"BROADCAST",
			/*propLevels*/"N/A",
                        0);
List profile = profileClient.query(xml_query);
// we should get back only 1 profile
Map elements = ((Profile)profile.get(0)).getProfileElements(); 
%>
<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
  <tr>
    <td>
      <%@ include file="/pds/pds_header.html" %>

      <table width="760" border="0" cellspacing="3" cellpadding="2">
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Target Information</b></font><br><br>
                </td>
        </tr>
      <% 
	  for (int i=0; i<displayedElements.length; i++) {
             if (elements.containsKey(displayedElements[i])) { 
                ProfileElement pe = (ProfileElement)elements.get(displayedElements[i]); %>
               <TR>
                 <td bgcolor="#F0EFEF" width=200 valign=top><%=displayedElements[i]%></td> 
                 <td bgcolor="#F0EFEF">
             <% 
			 for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                  String val = (String)e.next();
                  val = val.replaceAll("\n","<br>");
                  if (val.startsWith("http://") || val.startsWith("HTTP://")) { %>
                      <a href="<%=val%>" target="_blank"><%=val%></a>
             <%   } else { %>
                      <%=val%><br>
             <%   }
			} %>
                </td>
               </TR>
         <%
             } else { // this element does not have a value %>
               <TR>
                  <td bgcolor="#F0EFEF" width=200 valign=top><%=displayedElements[i]%></td>
                  <td bgcolor="#F0EFEF"></td>
               </TR>
         <%  }
         } // for loop
         %>

        <%@ include file="/pds/footer.html" %>
        </table>

    </td>
  </tr>

</TABLE>

</BODY>
</HTML>

