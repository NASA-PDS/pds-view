<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");
%>
<html>
<head>
   <title>PDS: Collection Information</title>
      <META  NAME="keywords"  CONTENT="Planetary Data System">
      <META  NAME="description" CONTENT="This website serves as a mechanism for displaying the volume information in PDS planetary archives.">
      <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
      
      <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" 
               contentType="text/html; charset=ISO-8859-1" 
               import="gov.nasa.pds.dsview.registry.PDS4Search, gov.nasa.pds.dsview.registry.Constants, 
                       org.apache.solr.common.SolrDocument, org.apache.solr.common.SolrDocumentList,
                       java.util.*, java.net.*, java.io.*, java.lang.*"
      %>

      <SCRIPT LANGUAGE="JavaScript">
         <%@ include file="/pds/utils.js"%>
      </SCRIPT>
</head>

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
            <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <b>Collection Information</b><br/>
               </td>
            </tr>
            
<%
   PDS4Search pds4Search = new PDS4Search(searchUrl);
   
   if (request.getParameter("identifier")==null) {
      SolrDocumentList collectionObjs = pds4Search.getCollections();
   
   //out.println("collectionObj = " + collectionObjs);
   //out.println("size of collectionObj = " + collectionObjs.size());
   
      if (collectionObjs==null || collectionObjs.size()==0) {  
   %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Collection(s) Information found in the registry. 
                  </td>
               </tr>    
   <%
      }
      else{ 
         for (SolrDocument doc: collectionObjs) {
/*
            for (Map.Entry<String, Object> entry : doc.entrySet()) {
                    out.println("Key = " + entry.getKey()
                            + "    Value = " + entry.getValue());
                }
*/
            Collection<Object> values = doc.getFieldValues("identifier");
            //out.println("vales.size() = " + values.size());
            for (Object value: values) {
               String val = (String) value;
               //out.println("val = " + val);
            %>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top>IDENTIFIER</td> 
               <td bgcolor="#F0EFEF" valign=top>
                  <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
            </TR>
            <%
            }
         }
      }
   } // end if (request.getParameter("identifier")==null)
   else {
   
      String collectionLid = request.getParameter("identifier");
      //out.println("collectionLid = " + collectionLid);
      
      SolrDocument doc = pds4Search.getContext(collectionLid);
      if (doc==null) {
       %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Collection Information found in the registry. 
                  </td>
               </tr>    
   <%
      }
      else{ 
               
      for (java.util.Map.Entry<String, String> entry: Constants.collectionPds4ToSearch.entrySet()) {
         String key = entry.getKey();
		 String tmpValue = entry.getValue(); 
         %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
                  <td bgcolor="#F0EFEF" valign=top>

		 <% 
         //out.println("key = " + key);
		 if (key.equals("IDENTIFIER"))
            out.println(pds4Search.getValues(doc, "identifier").get(0));
         else if (key.equals("NAME"))
            out.println(pds4Search.getValues(doc, "title").get(0));
         else {
            //out.println("tmpValue = " + tmpValue + "<br>");
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null) {
               for (int j=0; j<values.size(); j++) {
                    out.println(values.get(j) + "<br>");

                  if (values.size()>1)
                    out.println("<br>");
               } // end for
             } // end if (values!=null)
          } // end else
         %>
                   </td>
                </TR>
      <%  
      } // for loop  
      
      boolean anyCitationValue = false;
      for (java.util.Map.Entry<String, String> entry: Constants.bundleCitationPds4ToRegistry.entrySet()) {
            //String key = entry.getKey();
		    String tmpValue = entry.getValue(); 
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null && values.size()>0) {
               anyCitationValue = true;
            }
      }     
      //out.println("anyCitationValue = " + anyCitationValue);
      
      boolean anyContextValue = false;
      for (java.util.Map.Entry<String, String> entry: Constants.bundleContextPds4ToRegistry.entrySet()) {
            //String key = entry.getKey();
		    String tmpValue = entry.getValue(); 
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null && values.size()>0) {
               anyContextValue = true;
            }
      }
      //out.println("anyContextValue = " + anyContextValue);
            
      if (anyCitationValue) {
         %>
         <tr>
		                <td>&nbsp;</td>
		                <td>&nbsp;</td>
                     </tr>
         <tr bgcolor="#efefef">
            <td colspan=2><b>Citation</b></td>
         </tr>
         <%
         for (java.util.Map.Entry<String, String> entry: Constants.bundleCitationPds4ToRegistry.entrySet()) {
            String key = entry.getKey();
		    String tmpValue = entry.getValue(); 
		    %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
                  <td bgcolor="#F0EFEF" valign=top>

		 <% 
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null) {
               for (int j=0; j<values.size(); j++) {

                    out.println(values.get(j) + "<br>");

                  if (values.size()>1)
                    out.println("<br>");
               } // end for
             } // end if (values!=null)
             %>
             </td>
             </TR>
             <%
          }   // end for
      } // end if (anyCitationValue)
      
      if (anyContextValue) {
         %>
         <tr>
		                <td>&nbsp;</td>
		                <td>&nbsp;</td>
                     </tr>
                     
         <tr bgcolor="#efefef">
            <td colspan=2><b>Context</b></td>
         </tr>
         <%
         for (java.util.Map.Entry<String, String> entry: Constants.bundleContextPds4ToRegistry.entrySet()) {
            String key = entry.getKey();
		    String tmpValue = entry.getValue(); 
		    %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
                  <td bgcolor="#F0EFEF" valign=top>

		 <% 
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null) {
               for (int j=0; j<values.size(); j++) {
                    String val = values.get(j);      			
           		  
           		    if (tmpValue.equals("target_name")) {
                       if (pds4Search.getValues(doc, "target_ref")!=null) {
                          for (String targetRef: pds4Search.getValues(doc, "target_ref")) {
                             if (targetRef.contains("::"))
                                targetRef = targetRef.substring(0, targetRef.indexOf("::"));
                             SolrDocument targetDoc = pds4Search.getContext(targetRef);

                             if (targetDoc!=null && pds4Search.getValues(targetDoc, "title")!=null) {
                                val = pds4Search.getValues(targetDoc, "title").get(0);
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=targetRef%>" target="_blank"><%=val%></a><br>
                         <%
                             } // end if
                             else
                                out.println(val);
                          } // end for
                       }
                       else
                          out.println(val);
                    }
    	            else if (tmpValue.equals("investigation_name")) {
                       String missionRef = "";
                       if (pds4Search.getValues(doc, "investigation_ref")!=null) {
                          missionRef = pds4Search.getValues(doc, "investigation_ref").get(0);
                          if (missionRef.contains("::"))
                             missionRef = missionRef.substring(0, missionRef.indexOf("::"));

                          SolrDocument missionDoc = pds4Search.getContext(missionRef);
                          if (missionDoc!=null && pds4Search.getValues(missionDoc, "investigation_name")!=null) {
                             val = pds4Search.getValues(missionDoc, "investigation_name").get(0);

                         %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=missionRef%>" target="_blank"><%=val%></a><br>
                         <%
                          }
                          else
                             out.println(val);
                       }
                       else
                          out.println(val);
                    }
                    else if (tmpValue.equals("observing_system_name")) {
                       if (pds4Search.getValues(doc, "instrument_host_ref")!=null) {
                          for (String instHostRef: pds4Search.getValues(doc, "instrument_host_ref")) {
                             if (instHostRef.contains("::"))
                                instHostRef = instHostRef.substring(0, instHostRef.indexOf("::"));
                             SolrDocument instHostDoc = pds4Search.getContext(instHostRef);

                             if (instHostDoc!=null && pds4Search.getValues(instHostDoc, "instrument_host_name")!=null) {
                                val = pds4Search.getValues(instHostDoc, "instrument_host_name").get(0);
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=instHostRef%>" target="_blank"><%=val%></a><br>
                         <%
                             }
                             else
                                out.println(val);
                          }
                       }
                       else
                          out.println(val);
                    }
                    else if (tmpValue.equals("observing_system_component_name")) {
                       if (pds4Search.getValues(doc, "instrument_ref")!=null) {
                          for (String instRef: pds4Search.getValues(doc, "instrument_ref")) {
                             if (instRef.contains("::"))
                                instRef = instRef.substring(0, instRef.indexOf("::"));
                             SolrDocument instDoc = pds4Search.getContext(instRef);
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=instRef%>" target="_blank"><%=val%></a><br>
                         <%
                          }
                       }
                       else
                          out.println(val);

                    } // end else if (observing_system_component_name)
                    else
                        out.println(val + "<br>");
         	   } // end for
             } // end if (values!=null)
             %>
             </td>
             </TR>
             <%
          }   // end for
      } // end if (anyContextValue)
                 
      } // end else      
   } 
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

