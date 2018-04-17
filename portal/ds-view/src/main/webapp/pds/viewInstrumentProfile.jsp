<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");   
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Instrument Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for displaying the instrument information in PDS planetary archives.">
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
   <%@ page language="java" session="true" isThreadSafe="true" 
            info="PDS Search" isErrorPage="false" errorPage="error.jsp" 
            contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.dsview.registry.PDS3Search, gov.nasa.pds.dsview.registry.Constants, 
                    org.apache.solr.common.SolrDocument, gov.nasa.pds.registry.model.ExtrinsicObject,
                    java.util.*, java.net.*, java.io.*, java.lang.*"
   %>

   <SCRIPT LANGUAGE="JavaScript">
      <%@ include file="/pds/utils.js"%>
   </SCRIPT>
</head>

<body class="menu_data menu_item_data_keyword_search ">

<c:import url="/header.html" context="/include" />
<c:import url="/main_menu.html" context="/include" />
<c:import url="/datasearch_menu.html" context="/include" />

<!-- Main content -->
<div id="content">
   <div style="border-top: 1px solid_white;">
   <table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>
         <table width="760" border="0" cellspacing="3" cellpadding="2">
            <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <b>Instrument Information</b><br/>
               </td>
            </tr>
            
<%
String instrumentId = request.getParameter("INSTRUMENT_ID");
if ((instrumentId == null) || (instrumentId == "")) {
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>INSTRUMENT_ID</b>.
               </td>
            </tr>
<%
}
// Instrument id specified. Check if received instrument host id. 
else {
   String instrumentHostId = null;
   instrumentId = instrumentId.toUpperCase();
   boolean instFlag = false;
   
   //String instLid = "urn:nasa:pds:context_pds3:instrument:instrument." + instrumentId;
   String instLid = instrumentId;
   instLid = instLid.toLowerCase();
    
   PDS3Search pds3Search = new PDS3Search(searchUrl);
   
   // INSTRUMENT_HOST_ID is optional
   instrumentHostId = request.getParameter("INSTRUMENT_HOST_ID");
   if ((instrumentHostId != null) && (instrumentHostId != "")) {
      instrumentHostId = instrumentHostId.toUpperCase();  
      //out.println("there is an instrument_host_id....instrument_host_id = " + instrumentHostId);
   }
  
   // can use wildcard for the instrument_host_id...
   // it will change to Product_Context for next build
  
   List<SolrDocument> instObjs = pds3Search.getInst(instrumentId); 
   if (instObjs != null)  { 
  	  instFlag = true;
  	  //out.println("instObjs.size = " + instObjs.size());
   }
  
   if (instrumentHostId == null) {
      if (instObjs!=null && instObjs.size()>0) {
         for (SolrDocument obj: instObjs) {
         	List<String> slotValues = pds3Search.getValues(obj, "instrument_host_id");
         	String val = slotValues.get(0).toUpperCase();
      
    	 	%>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top>INSTRUMENT_HOST_ID</td> 
               <td bgcolor="#F0EFEF" valign=top>
                  <a href="/ds-view/pds/viewInstrumentProfile.jsp?INSTRUMENT_ID=<%=instrumentId%>&INSTRUMENT_HOST_ID=<%=val%>" target="_blank"><%=val%></a>
               </td>
            </TR>
         <%
      	 } // end for
      } // if (instObjs!=null && instObjs.size()>0)
      else {
      %>
    	    <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
               <% if (instFlag) { %>
                  Information not found for INSTRUMENT_HOST_ID <b><%=instrumentHostId%></b>. Please verify the value.
               </td> 
               <% } else { %>
                  Information not found for INSTRUMENT_ID <b><%=instrumentId%></b>. Please verify the value.            
               <% } %>
               </td>
            </tr>	
      <%  
	  } // end else
   } // end instrumentHostId = null
   else { // instrumentHostId!=null
      try {
      SolrDocument instObj = pds3Search.getInst(instrumentId, instrumentHostId);
   
      if (instObj==null || !instFlag)  { 
      %>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
               <% if (instFlag) { %>
                  Information not found for INSTRUMENT_HOST_ID <b><%=instrumentHostId%></b>. Please verify the value.
               </td> 
               <% } else { %>
                  Information not found for INSTRUMENT_ID <b><%=instrumentId%></b> and INSTRUMENT_HOST_ID <b><%=instrumentHostId%></b>. Please verify the values.
               <% } %>
               </td>
            </tr>
      <%
      } // end if (instObj==null || !instFlag)
      else {
         //out.println("instObj guid = " + instObj.getGuid());
                
         for (java.util.Map.Entry<String, String> entry: Constants.instPds3ToRegistry.entrySet()) {
		    String key = entry.getKey();
		    String tmpValue = entry.getValue();
            %>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
               <td bgcolor="#F0EFEF" valign=top>
			 <%
             //out.println("tmpValue = " + tmpValue + "<br>");
             List<String> slotValues = pds3Search.getValues(instObj, tmpValue);
             if (slotValues!=null) {
                if (tmpValue.equals("instrument_description")){                          
                   String val = slotValues.get(0);
                   //val = val.replaceFirst("            ", "  ");                         
                   %>
                   <pre><tt><%=val%></tt></pre>
                <%
                }
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
             } // end if (slotValues!=null)
             else {
                if (tmpValue.equals("instrument_host_id")) {
                   out.println(instrumentHostId + "<br>");
                }
             }
             %>
                </td>
             </TR>
         <%  
         } // for loop
      } // end else
      } catch (Exception e) {
      }
   
   } // end else instrumentHostId!=null
} // end else instrumentId!=null
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

