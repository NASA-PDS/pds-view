<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
%>
<html>
<head>
   <title>PDS Mission Profile</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.registry.model.ExtrinsicObject, gov.nasa.pds.dsview.registry.Constants, 
                    java.util.*, java.net.*, java.io.*, java.net.URLDecoder"
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
<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>
         <%@ include file="/pds/pds_header.html" %>
         <table width="760" border="0" cellspacing="3" cellpadding="2">
            <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <br><FONT color="#6F4D0E"><b>Mission Information</b></font><br><br>
               </td>
            </tr>
         
<%
if (cleanParam(request.getParameter("MISSION_NAME"))==null) {
%>       
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>MISSION_NAME</b>.
               </td>
            </tr>
<%
}
else {
   String missionName = request.getParameter("MISSION_NAME");
   missionName = missionName.toUpperCase();

   String missionLid = "urn:nasa:pds:mission." + missionName;
   missionLid = missionLid.replace(' ', '_');
   
   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);
   ExtrinsicObject msnObj = searchRegistry.getExtrinsic(missionLid);
   
   if (msnObj==null) {  
   %>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Information not found for MISSION_NAME <b><%=missionName%></b>. Please verify the value.
               </td>
            </tr>
 
   <%   
   }
   else {
      //out.println("msnObj guid = " + msnObj.getGuid());
      //out.println("mission_start_date = " + searchRegistry.getSlotValues(msnObj, "mission_start_date").get(0));
   
      for (java.util.Map.Entry<String, String> entry: Constants.msnPds3ToRegistry.entrySet()) {
         String key = entry.getKey();
		 String tmpValue = entry.getValue();
         %>
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top><%=key%></td>
               <td bgcolor="#F0EFEF">
         <% 
         //out.println("tmpValue = " + tmpValue + "<br>");
         List<String> slotValues = searchRegistry.getSlotValues(msnObj, tmpValue);
         if (slotValues!=null) {
            if (tmpValue.equals("mission_description") || 
                tmpValue.equals("mission_objectives_summary")) {
                          
                String val = slotValues.get(0);
                //val = val.replaceAll("\n","<br>");
                //val = val.replaceFirst("            ", "  ");
                %>
                   <pre><tt><%=val%></tt><pre>
                <%
             }
             else {
                for (int j=0; j<slotValues.size(); j++) {
                   if (tmpValue.equals("external_reference_description")) 
                      out.println(slotValues.get(j) + "<br>");
                   else 
                      out.println(slotValues.get(j).toUpperCase() + "<br>");
                         	   
                   if (slotValues.size()>1) 
                      out.println("<br>");
                } // end for
             } // end else
          } // end if (slotValues!=null)
          %>
               </td>
            </TR>
         <% 
      } // for loop
   } // if msnObj !=null
}// if mission name is specified
         %>

         <%@ include file="/pds/footer.html" %>
         </table>
      </td>
   </tr>
</TABLE>

</BODY>
</HTML>

