<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
%>
<html>
<head>
   <title>PDS Instrument Profile</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">

   <META  NAME="description" CONTENT="This website serves as a mechanism for displaying the instrument information in PDS planetary archives.">
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
   <%@ page language="java" session="true" isThreadSafe="true" 
            info="PDS Search" isErrorPage="false" errorPage="error.jsp" 
            contentType="text/html; charset=ISO-8859-1" 
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

<body class="menu_data menu_item_data_data_search ">

   <%@ include file="/pds/header.html" %>
   <%@ include file="/pds/main_menu.html" %>

   <div id="submenu">
   <div id="submenu_data">
   <h2 class="nonvisual">Menu: PDS data</h2>
   <ul>
      <li id="data_data_search"><a href="http://pds.jpl.nasa.gov/tools/data-search/">Data Search</a></li>
      <li id="data_text_search"><a href="http://pds.jpl.nasa.gov/tools/text-search/">Text Search</a></li>
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
                  <b>Instrument Information</b><br/>
               </td>
            </tr>
            
<%
if (cleanParam(request.getParameter("INSTRUMENT_ID"))==null) {
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
   String instrumentId = request.getParameter("INSTRUMENT_ID");
   instrumentId = instrumentId.toUpperCase();
   boolean instFlag = false;
    
   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);   
   //out.println("instrumentId = " + instrumentId +  "instrument_host_id = " + request.getParameter("INSTRUMENT_HOST_ID"));

   String instLid = "urn:nasa:pds:instrument." + instrumentId;
   
   // INSTRUMENT_HOST_ID is optional
   if (cleanParam(request.getParameter("INSTRUMENT_HOST_ID")) != null) {
      instrumentHostId = request.getParameter("INSTRUMENT_HOST_ID");
      instrumentHostId = instrumentHostId.toUpperCase();  
      //out.println("there is an instrument_host_id....instrument_host_id = " + instrumentHostId);
   }
  
   // can use wildcard for the instrument_host_id...
   // it will change to Product_Context for next build
  
   List<ExtrinsicObject> instObjs = searchRegistry.getObjects(instLid+"*", "Product_Instrument*"); 
   if (instObjs != null)  { 
  	  instFlag = true;
  	  //out.println("instObjs.size = " + instObjs.size());
   }
  
   if (instrumentHostId == null) {
      if (instObjs!=null && instObjs.size()>0) {
         for (ExtrinsicObject obj: instObjs) {
            //out.println("obj = " + obj.toString() + "<br>");
         	List<String> slotValues = searchRegistry.getSlotValues(obj, "instrument_host_ref");
         	String val = slotValues.get(0);
         	String lid = val.substring(0, val.indexOf("::"));
    	 	ExtrinsicObject insthostObj = searchRegistry.getExtrinsic(lid);
    	 	val = searchRegistry.getSlotValues(insthostObj, "instrument_host_id").get(0).toUpperCase(); 
    	 	//out.println(val + "<br>");
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
      instLid += "__" + instrumentHostId;
      //out.println("instLid = " + instLid);
      ExtrinsicObject instObj = searchRegistry.getExtrinsic(instLid);
   
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
             List<String> slotValues = searchRegistry.getSlotValues(instObj, tmpValue);
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
                      if (tmpValue.equals("external_reference_description")) 
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
   } // end else instrumentHostId!=null
} // end else instrumentId!=null
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

