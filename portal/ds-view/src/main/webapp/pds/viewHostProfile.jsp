<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");  
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Instrument Host Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">   
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
   
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.dsview.registry.PDS3Search, gov.nasa.pds.dsview.registry.Constants, 
                    org.apache.solr.common.SolrDocument,
                    java.util.*,java.net.*,java.io.*, java.lang.*"
   %>

   <SCRIPT LANGUAGE="JavaScript">
      <%@ include file="/pds/utils.js"%>
   </SCRIPT>
</head>

<body class="menu_data menu_item_data_data_search ">

<c:import url="/header.html" context="/include" />
<c:import url="/main_menu.html" context="/include" />
<c:import url="/data_menu.html" context="/include" />

<!-- Main content -->
<div id="content">
   <div style="border-top: 1px solid_white;">
   <table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>         
         <table width="760" border="0" cellspacing="3" cellpadding="2">
            <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <b>Instrument Host Information</b><br/>
               </td>
            </tr>

<%
String hostId = request.getParameter("INSTRUMENT_HOST_ID");
if ((hostId == null) || (hostId == "")) {
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>INSTRUMENT_HOST_ID</b>.
               </td>
            </tr>
<%
}
else {
   String hostLid = hostId.toLowerCase();   
   PDS3Search pds3Search = new PDS3Search(searchUrl);
   //String hostLid = "urn:nasa:pds:context_pds3:instrument_host:instrument_host." + hostId.toLowerCase();
   
   try {
      SolrDocument hostObj = pds3Search.getInstHost(hostLid);
   
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
      for (java.util.Map.Entry<String, String> entry: Constants.instHostPds3ToRegistry.entrySet()) {
         String key = entry.getKey();
		 String tmpValue = entry.getValue();
		 %>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td>
               <td bgcolor="#F0EFEF">
                  <%                        
                  //out.println("tmpValue = " + tmpValue + "<br>");
                  List<String> slotValues = pds3Search.getValues(hostObj, tmpValue);
                  if (slotValues!=null) {
                     if (tmpValue.equals("instrument_host_description")){                          
                        String val = slotValues.get(0);
                        //val = val.replaceFirst("            ", "  ");
                  %>
                  <pre><tt><%=val%></tt></pre>
                  <%
                     } // end if (tmpValue.equals("instrument_host_description")
                     else {
                        for (int j=0; j<slotValues.size(); j++) {
                           if (tmpValue.equals("external_reference_text")) 
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
    } catch (Exception e) {
    }
 } // end else 
         %>

         </table>
      </td>
   </tr>
</TABLE>
</div>
</div>

<c:import url="/footer.html" context="/include" />

</BODY>
</HTML>

