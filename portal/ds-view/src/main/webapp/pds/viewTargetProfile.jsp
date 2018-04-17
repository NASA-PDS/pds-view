<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");  
   //String registryUrl = application.getInitParameter("registry.url"); 
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Target Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.dsview.registry.PDS3Search, gov.nasa.pds.dsview.registry.Constants, 
            gov.nasa.pds.dsview.registry.SearchRegistry, gov.nasa.pds.registry.model.ExtrinsicObject,
                    org.apache.solr.common.SolrDocument,  
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
                  <b>Target Information</b><br/>
               </td>
            </tr>

<%
String targetId = request.getParameter("TARGET_NAME");
if ((targetId == null) || (targetId == "")) {
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>TARGET_NAME</b>.
               </td>
            </tr>
<%
}
else {
   //out.println("targetId = " + targetId + "<br>");
   /*
   targetId = targetId.replaceAll(" ", "_");
   targetId = targetId.replaceAll("/", "-");
   targetId = targetId.replaceAll("\\(", "");
   targetId = targetId.replaceAll("\\)", "");
   targetId = targetId.replaceAll("&", "-");
   */
   
   PDS3Search pds3Search = new PDS3Search(searchUrl);
   targetId = targetId.toLowerCase();
   //out.println("targetId = " + targetId);
      
   try {
      SolrDocument targetDoc = pds3Search.getTarget(targetId);

   if (targetDoc==null) { 
   %>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Information not found for TARGET_NAME <b><%=targetId%></b>. Please verify the value.
               </td> 
            </tr>
   <% 
   }
   else {
      for (java.util.Map.Entry<String, String> entry: Constants.targetPds3ToSearch.entrySet()) {
	     String key = entry.getKey();
		 String tmpValue = entry.getValue();
		 //out.println("key = " + key + "   value = " + tmpValue);
         %>
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top><%=key%></td> 
               <td bgcolor="#F0EFEF" valign=top>
         <%
         String val = "";
         List<String> slotValues = pds3Search.getValues(targetDoc, tmpValue);
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
               List<String> rvalues = pds3Search.getValues(targetDoc, "resource_ref");
               //out.println("rvalues.size() = " + rvalues.size());
               
               String resname="", reslink="";
               String refLid = "";
               if (rvalues != null) {
     	          for (int i=0; i<rvalues.size(); i++) {
                     //out.println(rvalues.get(i) + "<br>");
                     refLid = rvalues.get(i);
                     if (refLid!=null) {
                        if (refLid.indexOf("::")!=-1) {
                           refLid = refLid.substring(0, refLid.indexOf("::"));   
                        }
                        //refLid = refLid.replace("context_pds3", "context");
                        //out.println("reflid = " + refLid);
                        SolrDocument refDoc = pds3Search.getResource(refLid);
                        if (refDoc!=null) {
                           resname = pds3Search.getValues(refDoc, "resource_name").get(0);
                           reslink = pds3Search.getValues(refDoc, "resource_url").get(0);
                  %>
                        <li><a href="<%=reslink%>"><%=resname%></a><br>
                 <% 
                        }
                        else {
                           resname = refLid;
                           reslink = refLid;
                        }                                         
                      
                     } // end if (refLid!=null)
                  } // end for
         	   }  // end if (rvalues!=null)  	                
            }  // end if (tmpValue.equals("resource_link")
         }   // end else
         %>
               </td>
            </TR>
         <%  
         } // for loop
         
        
      } // end targetObj!=null
      } catch (Exception e) {
      }
   } // if target name is specified 
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

