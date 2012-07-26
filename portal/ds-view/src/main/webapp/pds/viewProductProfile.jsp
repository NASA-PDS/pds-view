<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");
%>
<html>
<head>
        <title>PDS Product Profile</title>
        <META  NAME="keywords"  CONTENT="Planetary Data System">

        <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary
archives.">
<link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">
<%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" contentType="text/html; charset=ISO-8859-1" import="jpl.eda.xmlquery.*,jpl.eda.profile.*,jpl.eda.profile.ProfileElement,java.util.*,java.io.*" %>

<%!
        String getValue(HttpServletRequest req, String param, int len ) {
                String connector = "";
                if (req.getParameterValues(param) != null &&
                    ! req.getParameterValues(param)[0].equals("")) {
                        if (len > 0) { connector = " AND "; }
                        return(connector + param +"=\"" + req.getParameterValues(param)[0] + "\"");
                } else
                        return "";
        }

%>

<SCRIPT LANGUAGE="JavaScript">
<%@ include file="/pds/utils.js"%>
</SCRIPT>

</head>

<body BGCOLOR="#000000">
<%
ProfileClient profileClient = new ProfileClient("urn:eda:rmi:JPL.PDS.MasterProd");
StringBuffer query = new StringBuffer();
Enumeration names = request.getParameterNames();
while (names.hasMoreElements()) {
   query.append(getValue(request, (String) names.nextElement(), query.length()));
}
if (query.length() == 0) {
   response.setStatus(javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
   return;
} 
XMLQuery xml_query = new XMLQuery(/*keywordQuery*/query.toString(),
                        /*id*/"OODT_XML_QUERY_V0.1",
                        /*title*/"OODT_XML_QUERY - Bean Query", 
			/*desc*/"This query can be handled by the OODT System",
                        /*ddId*/null,
			/*resultModeId*/"profileFull",
			/*propType*/"BROADCAST",
			/*propLevels*/"N/A",
                        0);
// we should get back only <=1 profile
List profile = profileClient.query(xml_query);
%>

<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
  <tr>
    <td>
      <%@ include file="/pds/pds_header.html" %>

      <table width="760" border="0" cellspacing="3" cellpadding="2">
<%
if (profile == null || profile.isEmpty()) { %>
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Product Not Found</b></font><br><br>
                </td>
        </tr>
</table>
<% } else { %>
        <tr valign="TOP">
                <td valign="TOP" colspan="2">
                        <br><FONT color="#6F4D0E"><b>Product Information</b></font><br><br>
                </td>
        </tr>

<%
// Set up the display order for profile elements.
Map elements = ((Profile)profile.get(0)).getProfileElements(); 
TreeSet elemNames = new TreeSet( new Comparator() {
   public int compare(Object o1, Object o2) {
      String s1 = (String)o1;
      String s2 = (String)o2;
      if (s1.equals("DATA_SET_ID")) {
         return -1;
      } else if (s2.equals("DATA_SET_ID")) {
         return 1;
      } else if (s1.equals("DATA_SET_NAME")) {
         return -1;
      } else if (s2.equals("DATA_SET_NAME")) {
         return 1;
      } else if (s1.equals("MISSION_NAME")) {
         return -1;
      } else if (s2.equals("MISSION_NAME")) { 
         return 1;
      } else {
          return s1.compareTo(s2);
      }
                                
   }
});
elemNames.addAll(elements.keySet());
%>

<!-- Display profile attributes -->
<!-- Display resource attributes -->
      <% ResourceAttributes resAttb = ((Profile)profile.get(0)).getResourceAttributes();
         if (resAttb.getIdentifier() != null && ! resAttb.getIdentifier().equals("")) { %>
           <TR>
              <td bgcolor="#F0EFEF" width=200 valign=top>IDENTIFIER</td>
              <td bgcolor="#F0EFEF"><%=resAttb.getIdentifier()%></td>
           </TR>
      <% }
         if (resAttb.getTitle() != null && ! resAttb.getTitle().equals("")) { %>
           <TR>
              <td bgcolor="#F0EFEF" width=200 valign=top>TITLE</td>
              <td bgcolor="#F0EFEF"><%=resAttb.getTitle()%></td>
           </TR>
      <% }
         if (resAttb.getFormats() != null && ! resAttb.getFormats().isEmpty()) { %>
           <TR>
              <td bgcolor="#F0EFEF" width=200 valign=top>FORMAT</td>
              <td bgcolor="#F0EFEF">
           <% for (Iterator i = resAttb.getFormats().iterator(); i.hasNext();) { %>
              <%=(String)i.next()%><br>
           <% } %>
              </td>
           </TR>
      <% }
         if (resAttb.getResContexts() != null && ! resAttb.getResContexts().isEmpty()) { %>
           <TR>
              <td bgcolor="#F0EFEF" width=200 valign=top>RESOURCE CONTEXT</td>
              <td bgcolor="#F0EFEF">
           <% for (Iterator i = resAttb.getResContexts().iterator(); i.hasNext();) { %>
              <%=(String)i.next()%><br>
           <% } %>
              </td>
           </TR>
      <% } 
         if (resAttb.getResLocations() != null && ! resAttb.getResLocations().isEmpty()) { %>
           <TR>
              <td bgcolor="#F0EFEF" width=200 valign=top>RESOURCE LOCATION</td>
              <td bgcolor="#F0EFEF">
           <% for (Iterator i = resAttb.getResLocations().iterator(); i.hasNext();) {
                  String val = (String)i.next();
                  if (val.startsWith("http://") || val.startsWith("HTTP://")) { %>
                      <a href="<%=jpl.eda.util.XML.escape(val)%>" target="_blank"><%=val%><br></a>
           <%     } else { %>
                      <%=val%><br>
           <%     }
              } %>
              </td>
           </TR>
      <% } %>
<!-- Display profile elements -->
      <% for (Iterator i = elemNames.iterator(); i.hasNext();) {
                String name = (String)i.next();
                ProfileElement pe = (ProfileElement)elements.get(name); %>
               <TR>
                 <td bgcolor="#F0EFEF" width=200 valign=top><%=name%></td> 
                 <td bgcolor="#F0EFEF">
             <% for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                  String val = (String)e.next();
                  val = val.replaceAll("\n","<br>");
                  if (name.equals("DATA_SET_ID")) { %>
                      <a href="/pds/viewProfile.jsp?identifier=<%=val%>" target="_blank"><%=val%><br></a>
             <%   } else if (val.startsWith("http://") || val.startsWith("HTTP://")) { %>
                      <a href="<%=val%>" target="_blank"><%=val%><br></a>
             <%   } else { %>
                      <%=val%><br>
             <%   }
                } %>
                </td>
               </TR>
         <%
         } // for loop
         %>
        
        </table>
		<% } %>
<%@ include file="/pds/footer.html" %>

    </td>
  </tr>

</TABLE>
</BODY>
</HTML>

