<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");
%>
<html>
<head>
   <title>PDS: PDS4 Product Class Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.dsview.registry.PDS4Search, gov.nasa.pds.dsview.registry.Constants, 
                       org.apache.solr.common.SolrDocument, org.apache.solr.common.SolrDocumentList,
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

   try {
     if (str != null) {
        decodedStr = URLDecoder.decode(str);
        for(int i = 0; i < badChars.length; i++) {
           if (decodedStr.indexOf(badChars[i]) >= 0) {
              return null;
           }
         }
     }
   } catch (IllegalArgumentException e) {
      return null;
   }
   return decodedStr;
}
%>

<body class="menu_data menu_item_data_data_search ">

   <%@ include file="/pds/header.html" %>
   <%@ include file="/pds/main_menu.html" %>
   <%@ include file="/pds/data_menu.html" %>

<!-- Main content -->
<div id="content">
   <div style="border-top: 1px solid_white;">
   <table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>
         <table width="760" border="0" cellspacing="3" cellpadding="2">
                  
<%
if (cleanParam(request.getParameter("identifier"))==null) {
%>       
            <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <b>Context Information</b><br/>
               </td>
            </tr>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>IDENTIFIER</b>.
               </td>
            </tr>
<%
}
else {

   String lid = request.getParameter("identifier");
   //out.println("lid = " + lid);
   
   PDS4Search pds4Search = new PDS4Search(searchUrl);
   SolrDocument doc = pds4Search.getContext(lid);

   if (doc==null) { 
   %>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Information not found for IDENTIFIER <b><%=lid%></b>. Please verify the value.
               </td>
            </tr>
 
   <%   
   }
   else {
      //out.println("extObj guid = " + extObj.getGuid());
      //out.println("mission_start_date = " + searchRegistry.getSlotValues(extObj, "mission_start_date").get(0));
   
      if (lid.contains("investigation") || lid.contains("mission")) {
         %>
         <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <b>Investigation Information</b><br/>
               </td>
            </tr>
          <%
         for (java.util.Map.Entry<String, String> entry: Constants.msnPds4ToRegistry.entrySet()) {
            String key = entry.getKey();
		    String tmpValue = entry.getValue();
         %>
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top><%=key%></td>
               <td bgcolor="#F0EFEF">
         <% 
            //out.println("tmpValue = " + tmpValue + "<br>");
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null) {
               if (tmpValue.equals("investigation_description") || 
                  tmpValue.equals("investigation_objectives_summary")) {
                          
                   String val = values.get(0);
                   %>
                   <pre><tt><%=val%></tt></pre>
                   <%
                   //out.println(val);
                
                }
                else {
                   for (int j=0; j<values.size(); j++) {
                      //if (tmpValue.equals("external_reference_description")) 
                      //   out.println(values.get(j) + "<br>");
                      //else 
                         out.println(values.get(j) + "<br>");
                         	   
                      if (values.size()>1) 
                         out.println("<br>");
                   } // end for
                } // end else
             } // end if (values!=null)
          %>
               </td>
            </TR>
         <% 
         } // for loop
      } // end if (investigation)
      else if (lid.contains("target")) {
      %>
         <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <b>Target Information</b><br/>
               </td>
            </tr>
          <%
          
         //for (java.util.Map.Entry<String, String> entry: Constants.targetPds3ToRegistry.entrySet()) {
         for (java.util.Map.Entry<String, String> entry: Constants.targetPds4ToRegistry.entrySet()) {
	        String key = entry.getKey();
		    String tmpValue = entry.getValue();
         %>
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top><%=key%></td> 
               <td bgcolor="#F0EFEF" valign=top>
         <%
         String val = "";
         List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null) {
            if (tmpValue.equals("target_description")){                          
               val = values.get(0);
               //val = val.replaceAll("            ", "  ");
               %>
                  <pre><tt><%=val%></tt></pre>
            <%
            }
            else {
               for (int j=0; j<values.size(); j++) {
                  out.println(values.get(j) + "<br>");
               }
            }
         }  // end if (values!=null)
         else {
            if (tmpValue.equals("resource_link")) {
               //List<String> rvalues = searchRegistry.getResourceRefs(extObj);
               List<String> rvalues = pds4Search.getValues(doc, tmpValue);
               if (rvalues != null) {
     	          String refLid = rvalues.get(0);
         	      refLid = refLid.substring(0, refLid.indexOf("::"));
         	      //out.println("refLid = " + refLid + "<br>");
         	      
         	      SolrDocument resource1 = pds4Search.getContext(refLid);
         	      if (resource1!=null) {
         	      	String resname, reslink;
                  	if (tmpValue.equals("resource_link")) {
         	        	List<String> reslinks = pds4Search.getValues(resource1, "resource_url");
         	         	reslink = reslinks.get(0);
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
      } // end if target
      else if (lid.contains("instrument_host")) {
      %>
         <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <b>Instrument Host Information</b><br/>
               </td>
            </tr>
          <%
          //for (java.util.Map.Entry<String, String> entry: Constants.instHostPds3ToRegistry.entrySet()) {
          for (java.util.Map.Entry<String, String> entry: Constants.instHostCtxPds4ToSearch.entrySet()) {
             String key = entry.getKey();
		     String tmpValue = entry.getValue();
		 %>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td>
               <td bgcolor="#F0EFEF">
                  <%                        
                  //out.println("tmpValue = " + tmpValue + "<br>");
                  List<String> values = pds4Search.getValues(doc, tmpValue);
                  if (values!=null) {
                     if (tmpValue.equals("instrument_host_desc")){                          
                        String val = values.get(0);
                        //val = val.replaceFirst("            ", "  ");
                  %>
                  <pre><tt><%=val%></tt></pre>
                  <%
                     } // end if (tmpValue.equals("instrument_host_desc")
                     else {
                        for (int j=0; j<values.size(); j++) {
                           //if (tmpValue.equals("external_reference_description")) 
                           //   out.println(values.get(j) + "<br>");
                           //else 
                         	  out.println(values.get(j) + "<br>");
                         	   
                           if (values.size()>1) 
                              out.println("<br>");
                        } // end for
                     } // end else
                  } // end if (values!=null)
                  %>
               </td>
            </TR>
         <%  
       } // for loop
      }// end if (instrument_host)
      else if (lid.contains("instrument.")) {
      %>
         <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <b>Instrument Information</b><br/>
               </td>
            </tr>
          <%
          //for (java.util.Map.Entry<String, String> entry: Constants.instPds3ToRegistry.entrySet()) {
          for (java.util.Map.Entry<String, String> entry: Constants.instCtxPds4ToSearch.entrySet()) {
		    String key = entry.getKey();
		    String tmpValue = entry.getValue();
            %>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
               <td bgcolor="#F0EFEF" valign=top>
			 <%
             //out.println("tmpValue = " + tmpValue + "<br>");
             List<String> values = pds4Search.getValues(doc, tmpValue);
             if (values!=null) {
                if (tmpValue.equals("instrument_desc")){                          
                   String val = values.get(0);
                   //val = val.replaceFirst("            ", "  ");                         
                   %>
                   <pre><tt><%=val%></tt></pre>
                <%
                }
                else {
                   for (int j=0; j<values.size(); j++) {
                      //if (tmpValue.equals("external_reference_description")) 
                      //   out.println(values.get(j) + "<br>");
                      //else 
                         out.println(values.get(j) + "<br>");
                         	      
                      if (values.size()>1) 
                         out.println("<br>");
                   } // end for
                } // end else 
             } // end if (values!=null)
             else {
                if (tmpValue.equals("instrument_host_id")) {
                   //List<String> instHostValues = searchRegistry.getSlotValues(extObj, "instrument_host_ref");
                   List<String> instHostValues = pds4Search.getValues(doc, "instrument_host_ref");
                   if (instHostValues != null) {
     	              String instHostLid = instHostValues.get(0);
     	         
         	          //ExtrinsicObject instHostObj = searchRegistry.getExtrinsic(instHostLid);
         	          SolrDocument instHostObj = pds4Search.getContext(instHostLid); 
         	          if (instHostObj!=null) {
         	             List<String> instHostIdValues = pds4Search.getValues(instHostObj, "instrument_host_name");
         	             String instHostId="";
                         if (instHostIdValues!=null) 
         	                instHostId = instHostIdValues.get(0);
         	      	     out.println(instHostId + "<br>");
         	      	  }     
         	       }  
         	     } // end if instrument_host_id       
               } // end else
             //}
             %>
                </td>
             </TR>
         <%  
         } // for loop
      }// end if (instrument)
     
   } // if extObj !=null
}// if mission name is specified
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

