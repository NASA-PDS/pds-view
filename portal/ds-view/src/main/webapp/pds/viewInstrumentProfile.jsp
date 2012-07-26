<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");
%>
<html>
<head>
        <title>PDS Instrument Profile</title>
        <META  NAME="keywords"  CONTENT="Planetary Data System">

        <META  NAME="description" CONTENT="This website serves as a mechanism for displaying the instrument information in PDS planetary archives.">
<link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">
<%@ page language="java" session="true" isThreadSafe="true" 
info="PDS Search" isErrorPage="false" errorPage="error.jsp" 
contentType="text/html; charset=ISO-8859-1" 
import="jpl.eda.xmlquery.*, jpl.eda.profile.*, javax.naming.*, javax.sql.*"
import="jpl.eda.profile.ProfileElement, java.util.*, java.io.*, java.sql.*, java.net.URLDecoder"
%>

<SCRIPT LANGUAGE="JavaScript">
<%@ include file="/pds/utils.js"%>
</SCRIPT>
</head>

<%!
  /**
   * Null out the parameter value if any of the bad characters are present
   * that facilitate Cross-Site Scripting and Blind SQL Injection.
   */
  public String cleanParam(String str) {
    char badChars [] = {'|', ';', '$', '@', '\'', '"', '<', '>', '(', ')', ',', '\\', /* CR */ '\r' , /* LF */ '\n' , /* Backspace */ '\b'};
    String decodedStr = null;

    if (str != null) {
      decodedStr = URLDecoder.decode(str);
      for(int i = 0; i < badChars.length; i++) {
        if (decodedStr.indexOf(badChars[i]) >= 0) {
          return null;
        }
      }
    }
    return decodedStr;
  }
%>

<body BGCOLOR="#000000">

<%
String[] displayedElements = {"INSTRUMENT_ID","INSTRUMENT_NAME" ,
                  "INSTRUMENT_TYPE", "INSTRUMENT_HOST_ID", //"RESLOCATION", 
                  "INSTRUMENT_DESC", "REFERENCE_DESCRIPTION"};

String instrumentId = null;
String instrumentHostId = null;

String query = null;
Connection connection = null;
Statement stmt = null;
ResultSet rs = null;
int inst_count =0;
int insthost_count = 0;

java.text.DateFormat df = new java.text.SimpleDateFormat ("yyyy.MM.dd HH:mm:ss");


ProfileClient profileClient = new ProfileClient("urn:eda:rmi:JPL.PDS.Instrument");
%>

<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
  <tr>
    <td>
      <%@ include file="/pds/pds_header.html" %>

<%
if (cleanParam(request.getParameter("INSTRUMENT_ID"))==null) {
%>
      <table width="760" border="0" cellspacing="3" cellpadding="2">
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Instrument Information</b>
                            </font><br><br>
                </td>
        </tr>
        
        <tr valign="TOP">
                <td bgcolor="#F0EFEF" width=200 valign=top>
                Please specify a valid INSTRUMENT_ID
                </td>
        </tr>
<%
}
// Instrument id specified. Check if received instrument host id. 
else {
   instrumentId = request.getParameter("INSTRUMENT_ID");
   instrumentId = instrumentId.toUpperCase();

   XMLQuery xml_query = null;

  // INSTRUMENT_HOST_ID is optional
  if (cleanParam(request.getParameter("INSTRUMENT_HOST_ID")) != null) {
        instrumentHostId = request.getParameter("INSTRUMENT_HOST_ID");
        instrumentHostId = instrumentHostId.toUpperCase();

        xml_query = new XMLQuery(
           /*keywordQuery*/"INSTRUMENT_ID=\"" + instrumentId + "\"" +
                        " AND INSTRUMENT_HOST_ID=\"" + instrumentHostId
                        + "\"",
           /*id*/"OODT_XML_QUERY_V0.1",
           /*title*/"OODT_XML_QUERY - Bean Query",
           /*desc*/"This query can be handled by the OODT System",
           /*ddId*/null,
           /*resultModeId*/"profile",
           /*propType*/"BROADCAST",
           /*propLevels*/"N/A",
           0);
    }
    else {
        xml_query = new XMLQuery(
           /*keywordQuery*/"INSTRUMENT_ID=\"" + instrumentId + "\"" ,
           /*id*/"OODT_XML_QUERY_V0.1",
           /*title*/"OODT_XML_QUERY - Bean Query",
           /*desc*/"This query can be handled by the OODT System",
           /*ddId*/null,
           /*resultModeId*/"profile",
           /*propType*/"BROADCAST",
           /*propLevels*/"N/A",
           0);
    }

    List profile = profileClient.query(xml_query);

    // no result found
    if (profile.size() < 1) {

       // find out exactly which parameter contains invalid value
       try {
         javax.naming.Context init = new javax.naming.InitialContext();
         javax.naming.Context env = (Context)init.lookup("java:comp/env");
         DataSource ds = (DataSource) env.lookup("jdbc/pdsprofile");
         connection = ds.getConnection();
         stmt = connection.createStatement();
         query = "select count(*) from instinfo where instid = '"
                + instrumentId + "'";
         rs = stmt.executeQuery (query);
         while (rs.next()) {
           inst_count = rs.getInt(1);
         }
         if (instrumentHostId != null) {
            query = "select count(*) from instinfo where insthostid = '"
                 + instrumentHostId + "'";
            rs = stmt.executeQuery (query);
            while (rs.next()) {
              insthost_count = rs.getInt(1);
            }
         }
         rs.close(); 
         stmt.close();
         connection.close();
      }
      catch (SQLException es) {
          if (connection!=null) connection.close();
          System.err.println ("viewInstrumentProfile.jsp: SQLException occurred while verifying instid and insthostid at time "+ df.format(new java.util.Date()));
          throw es;
       }
       finally {
        if (connection != null)  {
           try {
               connection.close();
           }
           catch (SQLException ess) {
               //do nothing
           }
         }
       }
%>
      <table width="760" border="0" cellspacing="3" cellpadding="2">
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Instrument Information</b>
                            </font><br><br>
                </td>
        </tr>

        <tr valign="TOP">
                <td bgcolor="#F0EFEF" width=200 valign=top>

<%     if (instrumentHostId!=null) {
            if (inst_count==0 && insthost_count==0) { 
%>
                Instrument ID <b><%=instrumentId%></b> and 
                Instrument Host ID <b><%=instrumentHostId%></b> 
                are not valid.
<%
            }
            else if (inst_count==0) { 
%>
                <b><%=instrumentId%></b> is not a valid Instrument ID.
<%
            }
            else if (insthost_count==0) { 
%>
                <b><%=instrumentHostId%></b> is not a valid Instrument Host ID.
<%
             }
            // found both values in catalog, then the combination must be
            // invalid
            else {
%>
                Instrument information not found for 
                   Instrument ID <b><%=instrumentId%></b> and 
                   Instrument Host ID <b><%=instrumentHostId%></b>.
<%
            }
        }
        else { 
%>
                <b><%=instrumentId%></b> is not a valid Instrument ID.
<%      } %>
                </td>
        </tr>
         
<%
    }

   // found some result passed from profile client
   else {

       // we should get back only 1 profile
       Map elements = ((Profile)profile.get(0)).getProfileElements(); 
%>

      <table width="760" border="0" cellspacing="3" cellpadding="2">
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Instrument Information</b></font><br><br>
                </td>
        </tr>
      <% for (int i=0; i<displayedElements.length; i++) {
             if (elements.containsKey(displayedElements[i])) { 
                ProfileElement pe = (ProfileElement)elements.get(displayedElements[i]); %>
               <TR>
                 <td bgcolor="#F0EFEF" width=200 valign=top><%=displayedElements[i]%></td> 
                 <td bgcolor="#F0EFEF" valign=top>

				 <%-- Preserve exact spacing.  This is important for
                    data such as diagrams and tables.
                    Both <pre> and <tt> are used here to overwrite font
                    defined for the <pre> tags in the top level css file --%>

				 <% if (displayedElements[i].equals("INSTRUMENT_DESC")) {
                    String val= (String)pe.getValues().iterator().next();
              %>
                    <pre><tt><%=val%></tt></pre>
              <%
               }  else  { // non-description attributes %>

             <%     for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                          String val = (String)e.next();
                          val = val.replaceAll("\n","<br>");
                          if (val.startsWith("http://") || 
                             val.startsWith("HTTP://")) { %>
                      <a href="<%=val%>" target="_blank"><%=val%></a>
             <%           } else { %>
                      <%=val%><br>
             <%        }
                    } //for 
               }
             %>
          
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
   }// if profile is found
}// if instrument id is specified
         %>
         <%@ include file="/pds/footer.html" %>
        </table>

    </td>
  </tr>

</TABLE>

</BODY>
</HTML>

