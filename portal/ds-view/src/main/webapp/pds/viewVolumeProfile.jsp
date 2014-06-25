<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
   //String searchUrl = application.getInitParameter("search.url");   
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Volume Information</title>
      <META  NAME="keywords"  CONTENT="Planetary Data System">
      <META  NAME="description" CONTENT="This website serves as a mechanism for displaying the volume information in PDS planetary archives.">
      <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
      
      <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" 
               contentType="text/html; charset=ISO-8859-1" 
               import="gov.nasa.pds.registry.model.ExtrinsicObject, gov.nasa.pds.dsview.registry.Constants, 
                       java.util.*, java.net.*, java.io.*, java.lang.*"
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
                  <b>Volume Information</b><br/>
               </td>
            </tr>
            
<%
String volumeId = request.getParameter("VOLUME_ID");
if ((volumeId == null) || (volumeId == "")) {
%>      
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>VOLUME_ID</b>.
               </td>
            </tr>
<%
}
// Volume id specified. Check if VolumeSet ID is provided
else {
   String volumeSetId = null;

   // can use wildcard for the volume_set_id
   volumeSetId = request.getParameter("VOLUME_SET_ID");
   
   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);
   String volumeLid = "urn:nasa:pds:context_pds3:volume:volume." + volumeId.toLowerCase();

   ExtrinsicObject volumeObj = null;
   if ((volumeSetId == null) || (volumeSetId == "")) {
      // what to do when there are more than one volume object exists
      List<ExtrinsicObject> volObjs = searchRegistry.getObjects(volumeLid+"*", "Product_Volume*");
      if (volObjs!=null) 
         volumeObj = volObjs.get(0);
   } // end if (volumeSetId==null)
   else {
      volumeLid += "__" + volumeSetId;
      volumeObj = searchRegistry.getExtrinsic(volumeLid);
   }
   
   //out.println("volumeLid = " + volumeLid);
          
   if (volumeObj==null) {  
   %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     Information not found for VOLUME_ID <b><%=volumeId%></b>. Please verify the value. 
                  </td>
               </tr>    
   <%
   }
   else{ 
      //out.println("volumeObj guid = " + volumeObj.getGuid());
               
      for (java.util.Map.Entry<String, String> entry: Constants.volumePds3ToRegistry.entrySet()) {
         String key = entry.getKey();
		 String tmpValue = entry.getValue(); 
         %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
                  <td bgcolor="#F0EFEF" valign=top>

		 <% 
         //out.println("tmpValue = " + tmpValue + "<br>");
         List<String> slotValues = searchRegistry.getSlotValues(volumeObj, tmpValue);
         if (slotValues!=null) {
            if (tmpValue.equals("volume_description")){                          
               String val = slotValues.get(0);
               //val = val.replaceAll("                ", "  ");
               //val = val.replaceAll("\n", "<br>");
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
         %>
                   </td>
                </TR>
      <%  
      } // for loop           
   } // if volumeObj!=null
}// if volume id is specified
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

