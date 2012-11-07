<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
%>
<html>
<head>
   <title>PDS Target Profile</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
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
                  <br><FONT color="#6F4D0E"><b>Target Information</b></font><br><br>
               </td>
            </tr>

<%
if  (cleanParam(request.getParameter("TARGET_NAME"))==null) {
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>TARGET_NAME</b>.
               </td>
            </tr>
<%
}
else {
   String targetId = request.getParameter("TARGET_NAME");
   targetId = targetId.toUpperCase();

   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);
   String targetLid = "urn:nasa:pds:target." + targetId;
   
   //out.println("targetLid = " + targetLid);
   ExtrinsicObject targetObj = searchRegistry.getExtrinsic(targetLid);

   if (targetObj==null) { 
   %>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Information not found for TARGET_NAME <b><%=targetId%></b>. Please verify the value.
               </td> 
            </tr>
   <% 
   }
   else {
      //out.println("targetObj guid = " + targetObj.getGuid());
      for (java.util.Map.Entry<String, String> entry: Constants.targetPds3ToRegistry.entrySet()) {
	     String key = entry.getKey();
		 String tmpValue = entry.getValue();
         %>
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top><%=key%></td> 
               <td bgcolor="#F0EFEF" valign=top>
         <%
         String val = "";
         List<String> slotValues = searchRegistry.getSlotValues(targetObj, tmpValue);
         if (slotValues!=null) {
            if (tmpValue.equals("target_description")){                          
               val = slotValues.get(0);
               //val = val.replaceAll("            ", "  ");
               %>
                  <pre><tt><%=val%></tt></pre>
            <%
            }
            else {
               for (int j=0; j<slotValues.size(); j++) {
                  out.println(slotValues.get(j) + "<br>");
               }
            }
         }  // end if (slotValues!=null)
         else {
            if (tmpValue.equals("resource_link")) {
               List<String> rvalues = searchRegistry.getResourceRefs(targetObj);
               if (rvalues != null) {
     	          String refLid = rvalues.get(0);
         	      refLid = refLid.substring(0, refLid.indexOf("::"));
         	      //out.println("refLid = " + refLid + "<br>");
         	      
         	      ExtrinsicObject resource1 = searchRegistry.getExtrinsic(refLid);
         	      if (resource1!=null) {
         	      	String resname, reslink;
                  	if (tmpValue.equals("resource_link")) {
         	        	 //resname = searchRegistry.getSlotValues(resource1, "resource_name").get(0);
         	         	reslink = searchRegistry.getSlotValues(resource1, "resource_url").get(0);
         	         	out.println(reslink);
         	      	}     
         	      }  
         	   }     	                
            }
         }
         %>
               </td>
            </TR>
         <%  
         } // for loop
      } // end targetObj!=null
   } // if target name is specified 
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

