<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
%>
<html>
<head>
   <title>PDS: Product Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1"
            import="gov.nasa.pds.registry.model.*, java.util.*, java.net.*, java.io.*"%>

   <SCRIPT LANGUAGE="JavaScript">
      <%@ include file="/pds/utils.js"%>
   </SCRIPT>
</head>

<%!
String getValue(HttpServletRequest req, String param, int len ) {
   String connector = "";
   if (req.getParameterValues(param) != null &&
       ! req.getParameterValues(param)[0].equals("")) {
      if (len > 0) { 
         connector = " AND "; 
      }
      return(connector + param +"=\"" + req.getParameterValues(param)[0] + "\"");
   } else
      return "";
}
%>

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

<body class="menu_data menu_item_data_data_search ">

   <%@ include file="/pds/header.html" %>
   <%@ include file="/pds/main_menu.html" %>

   <div id="submenu">
   <div id="submenu_data">
   <h2 class="nonvisual">Menu: PDS Data</h2>
   <ul>
      <li id="data_data_search"><a href="http://pds.jpl.nasa.gov/tools/data-search/">Data Search</a></li>
      <li><a href="/ds-view/pds/index.jsp">Form Search</a></li>
      <li id="data_how_to_search"><a href="http://pds.jpl.nasa.gov/data/how-to-search.shtml">How to Search</a></li>
      <li id="data_data_set_status"><a href="http://pds.jpl.nasa.gov/tools/dsstatus/">Data Set Status</a></li>
      <li id="data_release_summary"><a href="http://pds.jpl.nasa.gov/tools/subscription_service/SS-Release.shtml">Data Release Summary</a></li>
   </ul>
   </div>
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
                  <b>Product Information</b><br/>
               </td>
            </tr>               
<%
// need to query from Product_Proxy_PDS3
String lid = null;
if (cleanParam(request.getParameter("Identifier"))==null) {  
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a <b>dsid</b> or <b>Identifier</b>
               </td>
            </tr>
         </table>
<%  
}
else {
   
   lid = request.getParameter("Identifier");
   lid = lid.toUpperCase();
   // just for testing...
   lid = "urn:nasa:pds:node." + lid;
   out.println("lid = " + lid);

   // TODO: if the Identifier contains invalid chars...need to replace or throws exceptions - need to look the harvest tool
   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);  
   // it will change to Product_Context for next build
   
   //List<ExtrinsicObject> masterDSObjs = searchRegistry.getObjects(lid, "Product_Proxy_PDS3");
   List<ExtrinsicObject> masterDSObjs = searchRegistry.getObjects(lid, "Product_Context");
   out.println("masterDSObjs = " + masterDSObjs.size());
%>
            <!-- Display profile attributes -->
            <!-- Display resource attributes -->      
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top>IDENTIFIER</td>
               <td bgcolor="#F0EFEF">test identifier</td>
            </TR>
      
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top>TITLE</td>
               <td bgcolor="#F0EFEF">test title</td>
            </TR>
      
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top>FORMAT</td>
               <td bgcolor="#F0EFEF">test format</td>
            </TR>
      
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top>RESOURCE CONTEXT</td>
               <td bgcolor="#F0EFEF">test resource context</td>
            </TR>
      
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top>RESOURCE LOCATION</td>
               <td bgcolor="#F0EFEF">test resource location</td>
            </TR>
      
   <!-- need to display all slot values here -->
<% 
   if (masterDSObjs!=null) {
      out.println("masterDSObjs.size = " + masterDSObjs.size());
      
      for (ExtrinsicObject obj: masterDSObjs) { 
         // need to display all slot values      
         for (Slot slot: obj.getSlots()) {
            String slotName = slot.getName();
            //List<String> slotValues = slot.getValues();
            String slotValues = slot.getValues().toString();
            if (slot.getValues().size()>0) {
%>        
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top><%=slotName%></td> 
               <td bgcolor="#F0EFEF">
                  <%=slotValues%>
               </td>
            </TR>
         
<%   
            } // end inner if 
         }
      } // end outer for loop
   } // end if (masterObjs!=null)
%>
         </table>
<%
}
%>
      </td>
   </tr>
</TABLE>
</div>
</div>

<%@ include file="/pds/footer.html" %>

</BODY>
</HTML>

