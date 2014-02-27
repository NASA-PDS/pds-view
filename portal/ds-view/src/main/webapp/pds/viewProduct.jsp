<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");
%>
<html>
<head>
   <title>PDS: Observational Product Information</title>
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
                  <b>Observational Product Information</b><br/>
               </td>
            </tr>
            
<%
   PDS4Search pds4Search = new PDS4Search(searchUrl);
   long listsize = 0;
   int rowsFetch = 50;
   int curPage;
   int totalPage = 0;

   if (request.getParameterValues("pageNo") == null) {
      curPage = 0;
   } else {
      curPage = Integer.parseInt(request.getParameterValues("pageNo")[0]);
   }
   int startRow = (curPage * rowsFetch);

   //out.println("curPage = " + curPage + "<br>startRow = " + startRow + "<br>");
   
   if (request.getParameter("identifier")==null) {
      //SolrDocumentList obsObjs = pds4Search.getObservationals();
      SolrDocumentList obsObjs = pds4Search.getObservationals(startRow);
      listsize = obsObjs.getNumFound();
      totalPage = (int) Math.ceil((double)listsize/(double)rowsFetch);
      
   //out.println("obsObjs = " + obsObjs);
   //out.println("size of obsObjs = " + obsObjs.size() + "   numFound = " + obsObjs.getNumFound());
   
      if (obsObjs==null || obsObjs.size()==0) {  
   %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Observational Product Information found in the registry. 
                  </td>
               </tr>    
   <%
      }
      else{ 
         for (SolrDocument doc: obsObjs) {
            Collection<Object> values = doc.getFieldValues("identifier");
            //out.println("values.size() = " + values.size());
            for (Object value: values) {
               String val = (String) value;
               //out.println("val = " + val);
            %>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top>IDENTIFIER</td> 
               <td bgcolor="#F0EFEF" valign=top>
                  <a href="/ds-view/pds/viewProduct.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
            </TR>
            <%
            }
         }
      }
   } // end if (request.getParameter("identifier")==null)
   else {
   
      String objLid = request.getParameter("identifier");
      //out.println("objLid = " + objLid);
      
      SolrDocument doc = pds4Search.getContext(objLid);
      if (doc==null) {
       %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Observational Product Information found in the registry. 
                  </td>
               </tr>    
   <%
      }
      else{ 
 
 	  //out.println("in else...");              
      for (java.util.Map.Entry<String, String> entry: Constants.observationalPds4ToSearch.entrySet()) {
         String key = entry.getKey();
		 String tmpValue = entry.getValue(); 
		 //out.println("key = " + key + "  tmpValue = " + tmpValue);
         %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
                  <td bgcolor="#F0EFEF" valign=top>

		 <%         
		 if (key.equals("IDENTIFIER"))
            out.println(pds4Search.getValues(doc, "identifier").get(0));
         else if (key.equals("NAME"))
            out.println(pds4Search.getValues(doc, "title").get(0));
         else if (key.equals("FILE(S)")) {
            String filelink = "";
            String filename = "";
            List<String> filenames = pds4Search.getValues(doc, "file_ref_name");
            if (filenames!=null && filenames.size()==1) {
               filename = pds4Search.getValues(doc, "file_ref_name").get(0);
               // make sure it's not NULL
               if (pds4Search.getValues(doc, "file_ref_url")!=null) {
                  filelink = pds4Search.getValues(doc, "file_ref_url").get(0);
            %>
            <a href="<%=filelink%>" target="_new"><%=filename%></a><br>
            <%
               }
               else 
                  out.println(filename);
            } // end if
            else if (filenames.size()>1) {
               List<String> filelinks = pds4Search.getValues(doc, "file_ref_url");
               for (int i=0; i<filenames.size(); i++) {
                  filename = filenames.get(i);
                  if (filelinks!=null) {
                     filelink = filelinks.get(i);
                  %>
                  <a href="<%=filelink%>" target="_new"><%=filename%></a><br>
                  <%
                  }
                  else 
                     out.println(filename);
               } // end for
            }
         }
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
                    else if (tmpValue.equals("observing_system_component_name")) {
                       List<String> componentTypes = pds4Search.getValues(doc, "observing_system_component_type");
					   String componentType = componentTypes.get(j);                    
                       if (componentType.equalsIgnoreCase("spacecraft")) {
                          if (pds4Search.getValues(doc, "instrument_host_ref")!=null) {
                             for (String instHostRef: pds4Search.getValues(doc, "instrument_host_ref")) {
                                if (instHostRef.contains("::"))
                                   instHostRef = instHostRef.substring(0, instHostRef.indexOf("::"));
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=instHostRef%>" target="_blank"><%=val%></a><br>
                         <%
                             }
                          }
                          else
                             out.println(val);
                       }
                       else if (componentType.equalsIgnoreCase("instrument")) {                   
                          if (pds4Search.getValues(doc, "instrument_ref")!=null) {
                             for (String instRef: pds4Search.getValues(doc, "instrument_ref")) {
                                if (instRef.contains("::"))
                                   instRef = instRef.substring(0, instRef.indexOf("::"));
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=instRef%>" target="_blank"><%=val%></a><br>
                         <%
                             }
                          }
                          else
                             out.println(val);
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
	<%-- Result Page Navigation --%>
	        <tr></tr>
            <tr valign="TOP">
               <td valign="TOP" colspan="5" align=center>
<% if (listsize > 0) { %>
Page&nbsp;
<%
}
for(int k=1; k<=totalPage;k++) {
   if (k==curPage+1) { %>
       <%=k%>&nbsp;&nbsp;
<% } else { %>
   <a href="/ds-view/pds/viewProduct.jsp?pageNo=<%=k-1%>"><%= k%></a>&nbsp;&nbsp;
<% }
}

// is there a previous page?
if (curPage > 0) { %>
<A href="/ds-view/pds/viewProduct.jsp?pageNo=<%=curPage-1%>">Previous</A>&nbsp;&nbsp;
<% }
// is there a next page?
if (curPage+1 < totalPage) { %>
<A href="/ds-view/pds/viewProduct.jsp?pageNo=<%=curPage+1%>">Next</A>&nbsp;&nbsp;
<% } %>       
         </table>
      </td>
   </tr>
</TABLE>
</div>
</div>

<%@ include file="/pds/footer.html" %>

</BODY>
</HTML>

