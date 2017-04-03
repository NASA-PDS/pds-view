<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Document Information</title>
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
                  <b>Document Information</b><br/>
               </td>
            </tr>
            
<%
   PDS4Search pds4Search = new PDS4Search(searchUrl);
   
   if (request.getParameter("identifier")==null) {
      try {
         SolrDocumentList documentObjs = pds4Search.getDocuments();
         //out.println("documentObj = " + documentObjs);
         //out.println("size of documentObj = " + documentObjs.size());
   
         if (documentObjs==null || documentObjs.size()==0) {  
   %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Document(s) Information found in the registry. 
                  </td>
               </tr>    
   <%
         }
         else{ 
            for (SolrDocument doc: documentObjs) {
               Collection<Object> values = doc.getFieldValues("identifier");
               //out.println("vales.size() = " + values.size());
               for (Object value: values) {
                  String val = (String) value;
                  //out.println("val = " + val);
            %>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top>IDENTIFIER</td> 
               <td bgcolor="#F0EFEF" valign=top>
                  <a href="/ds-view/pds/viewDocument.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
            </TR>
            <%
               }
            }
         } // end else
      } catch (Exception e) {
  
      %>
        <TR>
         <td bgcolor="#F0EFEF" width=200 valign=top> 
            <b>No Search Service found: <%=searchUrl%></b> to retrieve the Document Information.
         </td>
        </TR>
        <%
  
      }
   } // end if (request.getParameter("identifier")==null)
   else {
   
      String documentLid = request.getParameter("identifier");
      //out.println("documentLid = " + documentLid);
      
      try {
      SolrDocument doc = pds4Search.getContext(documentLid);
      if (doc==null) {
       %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Document Information found in the registry. 
                  </td>
               </tr>    
   <%
      }
      else{ 
               
      for (java.util.Map.Entry<String, String> entry: Constants.documentPds4ToSearch.entrySet()) {
         String key = entry.getKey();
     String tmpValue = entry.getValue(); 
         %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top><%=key%></td> 
                  <td bgcolor="#F0EFEF" valign=top>

     <% 
         //out.println("key = " + key);
     if (key.equals("IDENTIFIER")) {
        String lidvid = pds4Search.getValues(doc, "identifier").get(0);
      String version = pds4Search.getValues(doc, "version_id").get(0);
            if (version!=null)
               lidvid += "::" + version;         
            out.println(lidvid);
         }
         else if (key.equals("NAME"))
            out.println(pds4Search.getValues(doc, "title").get(0));
         //else if (key.equals("RESOURCES")) {
         else if (tmpValue.equals("resource_ref")) {
            String resname = "";
            String reslink = "";
            List<String> resnames = pds4Search.getValues(doc, "resource_name");
            if (resnames!=null) {
               if (resnames.size()==1) {
                  resname = pds4Search.getValues(doc, "resource_name").get(0);
                  if (pds4Search.getValues(doc, "resource_url")!=null)
                     reslink = pds4Search.getValues(doc, "resource_url").get(0);
               %>
               <a href="<%=reslink%>" target="_new"><%=resname%></a><br>
               <%    
               } // end if
               else if (resnames.size()>1) {
                  List<String> reslinks = pds4Search.getValues(doc, "resource_url");
                  for (int i=0; i<resnames.size(); i++) {
                     resname = resnames.get(i);
                     reslink = reslinks.get(i);
                     %>
                     <a href="<%=reslink%>" target="_new"><%=resname%></a><br>
                     <%
                  } // end for
               }
            } // end if (resnames!=null)
         }
         else {
            //out.println("tmpValue = " + tmpValue + "<br>");
            List<String> values = pds4Search.getValues(doc, tmpValue);
            if (values!=null) {
               for (int j=0; j<values.size(); j++) {
                    out.println(values.get(j) + "<br>");

                    //if (values.size()>1)
                    //  out.println("<br>");
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
                  //out.println("j = " + j + "    val = " + val + "   tmpValue = " + tmpValue);
                  if (tmpValue.equals("target_name")) {
                       if (pds4Search.getValues(doc, "target_ref")!=null) {
                       int i=0;
                          for (String targetRef: pds4Search.getValues(doc, "target_ref")) {
                          //out.println("i = " + (i++));
                             if (targetRef.contains("::"))
                                targetRef = targetRef.substring(0, targetRef.indexOf("::"));
                                
                             //out.println("targetRef = " + targetRef);
                             SolrDocument targetDoc = pds4Search.getContext(targetRef);

               String targetName = null;
                             if (targetDoc!=null && pds4Search.getValues(targetDoc, "title")!=null) {
                                targetName = pds4Search.getValues(targetDoc, "title").get(0);
                                
                                if (targetName.equalsIgnoreCase(val)) {
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=targetRef%>" target="_blank"><%=val%></a><br>
                         <%
                                }
                             } // end if (targetDoc!=null ....
                             else
                                out.println(val + "<br>");
                          } // end for
                       }
                       else
                          out.println(val + "<br>");
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
                      out.println(val);
                    }
                    else if (tmpValue.equals("observing_system_component_name")) {
                       List<String> compTypes = pds4Search.getValues(doc, "observing_system_component_type");
                       List<String> refInfos = null;
                       String compType = compTypes.get(j);
                       String refLink = null;
                       if (compType!=null) {
                       //out.println("compType = " + compType + "    name = " + val);                      
                       if (compType.equalsIgnoreCase("instrument")) {                      
                          if (pds4Search.getValues(doc, "instrument_ref")!=null) {                            
                             refInfos = pds4Search.getValues(doc, "instrument_ref");                            
                             for (String instRef: refInfos) {
                                if (instRef.contains("::"))
                                instRef = instRef.substring(0, instRef.indexOf("::"));
                                SolrDocument instDoc = pds4Search.getContext(instRef);
                                if (instDoc!=null) {
                                   String instName = "";
                                   if (pds4Search.getValues(instDoc, "instrument_name")!=null) {
                                      instName = pds4Search.getValues(instDoc, "instrument_name").get(0);
                                      if (instName.equalsIgnoreCase(val))
                                         refLink = instRef;
                                   }
                                }                     
                             } // end for
                             if (refLink!=null) {
                                if (refLink.contains("::"))
                                   refLink = refLink.substring(0, refLink.indexOf("::"));
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=refLink%>" target="_blank"><%=val%></a><br>
                         <%
                             }
                             else 
                                out.println(val + "<br>");
                          } // end if (instrument_ref !=null)
                          else
                             out.println(val + "<br>");
                       } // end if (compType == "instrument")
                       else if (compType.equalsIgnoreCase("spacecraft")) {
                          if (pds4Search.getValues(doc, "instrument_host_ref")!=null) {
                             refInfos = pds4Search.getValues(doc, "instrument_host_ref");                           
                             for (String instHostRef: refInfos) {
                                if (instHostRef.contains("::"))
                                   instHostRef = instHostRef.substring(0, instHostRef.indexOf("::"));
                                SolrDocument instHostDoc = pds4Search.getContext(instHostRef);
                                if (instHostDoc!=null) {
                                   String instHostName = "";
                                   if (pds4Search.getValues(instHostDoc, "instrument_host_name")!=null) {
                                      instHostName = pds4Search.getValues(instHostDoc, "instrument_host_name").get(0);
                                      if (instHostName.equalsIgnoreCase(val))
                                         refLink = instHostRef;
                                   }
                                }
                             } // end for                           
                             if (refLink!=null) {
                                if (refLink.contains("::"))
                                   refLink = refLink.substring(0, refLink.indexOf("::"));
                              //out.println("refLink = " + refLink);
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=refLink%>" target="_blank"><%=val%></a><br>
                         <%
                             }
                             else 
                                out.println(val + "<br>");
                          } // end if (instrument_host_ref!=null)
                          else
                             out.println(val + "<br>");
                       } // end if (compType=="spacecraft")
                       else 
                          out.println(val + "<br>");
                       } // end if (compType!=null)
                       //else 
                       //   out.println(val);
                    } // end else if (observing_system_component_name)
                    else if (tmpValue.equals("external_reference_text")) {
                     out.println(val + "<br>");
              if (values.size()>1)
                         out.println("<br>");
                  }
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
      } catch (Exception e) {
  
      %>
        <TR>
         <td bgcolor="#F0EFEF" width=200 valign=top> 
            <b>No Search Service found: <%=searchUrl%></b> to retrieve the Document Information.
         </td>
        </TR>
        <%
  
      }    
   } 
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

