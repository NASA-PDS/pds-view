<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
%>
<html>
<head>
   <title>PDS Host Profile</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
   
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.registry.model.ExtrinsicObject,gov.nasa.pds.dsview.registry.Constants,
                    java.util.*,java.net.*,java.io.*, java.net.URLDecoder"
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
} // end of cleanParam method
%>

<body class="home menu_home menu_item_ ">

   <%@ include file="/pds/header.html" %>
   <%@ include file="/pds/main_menu.html" %>

   <div id="submenu">
   <div class="clear"></div>
   </div>

<!-- Main content -->
<div id="content">
   <div style="border-top: 1px solid_white;">
   <table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>         
         <table width="760" border="0" cellspacing="3" cellpadding="2">
            <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <br><FONT color="#6F4D0E"><b>Host Information</b></font><br><br>
               </td>
            </tr>

<%
if (cleanParam(request.getParameter("INSTRUMENT_HOST_ID"))==null) {
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>INSTRUMENT_HOST_ID</b>.
               </td>
            </tr>
<%
}
else {
   String hostId = request.getParameter("INSTRUMENT_HOST_ID");
   hostId = hostId.toUpperCase();

   String hostLid = "urn:nasa:pds:instrument_host." + hostId;
   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);
   ExtrinsicObject hostObj = searchRegistry.getExtrinsic(hostLid);
   
   if (hostObj==null)  { 
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Information not found for INSTRUMENT_HOST_ID <b><%=hostId%></b>. Please verify the value.
               </td> 
            </tr>
<% 
   } 
   else {

      //out.println("hostObj guid = " + hostObj.getGuid());
 
      for (java.util.Map.Entry<String, String> entry: Constants.instHostPds3ToRegistry.entrySet()) {
         String key = entry.getKey();
		 String tmpValue = entry.getValue();
		 %>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td>
               <td bgcolor="#F0EFEF">
                  <%                        
                  //out.println("tmpValue = " + tmpValue + "<br>");
                  List<String> slotValues = searchRegistry.getSlotValues(hostObj, tmpValue);
                  if (slotValues!=null) {
                     if (tmpValue.equals("instrument_host_description")){                          
                        String val = slotValues.get(0);
                        //val = val.replaceFirst("            ", "  ");
                  %>
                  <pre><tt><%=val%></tt><pre>
                  <%
                     } // end if (tmpValue.equals("instrument_host_description")
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
                  } // end if (slotValue!=null)
                  %>
               </td>
            </TR>
         <%  
       } // for loop
    } // end else (hostObject!=null)
 } // end else 
         %>

         </table>
      </td>
   </tr>
</TABLE>
</div>
</div>

<%@ include file="/pds/footer.html" %>

</BODY>
</HTML>

