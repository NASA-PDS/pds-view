<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Bundle Information</title>
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
                  <b>Bundle Information</b><br/>
               </td>
            </tr>
            
<%
   PDS4Search pds4Search = new PDS4Search(searchUrl);

   if (request.getParameter("identifier")==null) { 
      try {
         SolrDocumentList bundleObjs = pds4Search.getBundles();
   
         if (bundleObjs==null || bundleObjs.size()==0) {  
   %> 
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Bundle(s) Information found in the registry. 
                  </td>
               </tr>    
   <%
         }
         else{ 
            for (SolrDocument doc: bundleObjs) {			
		       Collection<Object> values = doc.getFieldValues("identifier");
		       //out.println("vales.size() = " + values.size());
		       for (Object value: values) {
                  String val = (String) value;
                  //out.println("val = " + val);
            %>
            <TR>
               <td bgcolor="#F0EFEF" width=215 valign=top>IDENTIFIER</td> 
               <td bgcolor="#F0EFEF" valign=top>
                  <a href="/ds-view/pds/viewBundle.jsp?identifier=<%=val%>" target="_blank"><%=val%></a><br>
            </TR>
            <%
               } // end for
            } // end for
         } // end else
      } catch (Exception e) {
  
      %>
        <TR>
         <td bgcolor="#F0EFEF" width=200 valign=top> 
            <b>No Search Service found: <%=searchUrl%></b> to retrieve the Bundle Information.
         </td>
        </TR>
        <%
  
      }
   } // end if (request.getParameter("identifier")==null)
   else {
   
      String bundleLid = request.getParameter("identifier");
      //out.println("bundleLid = " + bundleLid);

	  try {
      SolrDocument doc = pds4Search.getContext(bundleLid);
      
      if (doc==null) {
       %>
               <tr valign="TOP">
                  <td bgcolor="#F0EFEF" width=200 valign=top>
                     No Bundle Information found in the registry. 
                  </td>
               </tr>    
   <%
      }
      else{ 
        
         for (java.util.Map.Entry<String, String> entry: Constants.bundlePds4ToSearch.entrySet()) {
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
         else if (key.equals("RESOURCES")) {
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
      
      List<String> collVals = pds4Search.getValues(doc, "collection_ref");
      boolean anyCollectionValue = false;
      if (collVals!=null && collVals.size()>0) {
         anyCollectionValue = true;
      }
      
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
    	               out.println(val);         
    	            }
    	            else if (tmpValue.equals("observing_system_component_name")) {
    	                List<String> compTypes = pds4Search.getValues(doc, "observing_system_component_type");
                       //List<String> compNames = pds4Search.getValues(doc, "observing_system_component_name");
                       //for (String compType: compTypes) {
                       String compType = compTypes.get(j);
                       //out.println("compType = " + compType + "    name = " + val);
                       
                       if (compType.equalsIgnoreCase("instrument")) {                      
                          if (pds4Search.getValues(doc, "instrument_ref")!=null) {
                             for (String instRef: pds4Search.getValues(doc, "instrument_ref")) {
                                if (instRef.contains("::"))
                                   instRef = instRef.substring(0, instRef.indexOf("::"));
								//out.println("instRef = " + instRef);
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=instRef%>" target="_blank"><%=val%></a><br>
                         <%
                             }  // end for
                          } // end if (instrument_ref !=null)
                          else
                             out.println(val);
                       } // end if (compType == "instrument")
                       else if (compType.equalsIgnoreCase("spacecraft")) {
                          if (pds4Search.getValues(doc, "instrument_host_ref")!=null) {
                             for (String instHostRef: pds4Search.getValues(doc, "instrument_host_ref")) {
                                if (instHostRef.contains("::"))
                                   instHostRef = instHostRef.substring(0, instHostRef.indexOf("::"));
                    %>
                   <a href="/ds-view/pds/viewContext.jsp?identifier=<%=instHostRef%>" target="_blank"><%=val%></a><br>
                         <%
                             } // end for
                          } // end if (instrument_host_ref!=null)
                          else
                             out.println(val);
                       } // end if (compType=="spacecraft")
                       else 
                          out.println(val);
    	            } // end else if (observing_system_component_name)
    	            else if (tmpValue.equals("external_reference_text")) {
    	               out.println(val + "<br>");
					    if (values.size()>1)
                	       out.println("<br>");
                	}
    	            else {
                	    out.println(val + "<br>");
                	}
         	   } // end for
             } // end if (values!=null)
             %>
             </td>
             </TR>
             <%
          }   // end for
      } // end if (anyContextValue)
      
      String linkVal = "";
      if (anyCollectionValue) {
      %>
      <tr>
		                <td>&nbsp;</td>
		                <td>&nbsp;</td>
                     </tr>
         <tr bgcolor="#efefef">
            <td colspan=2><b>Collections</b></td>
         </tr>
         <%
         if (collVals!=null) {
               List<String> dataCollection = new ArrayList<String>();
               List<String> docCollection = new ArrayList<String>();
               List<String> ctxCollection = new ArrayList<String>();
               List<String> calibCollection = new ArrayList<String>();
               List<String> browseCollection = new ArrayList<String>();
               List<String> geomCollection = new ArrayList<String>();
               List<String> miscCollection = new ArrayList<String>();
               List<String> spiceCollection = new ArrayList<String>();
               List<String> xmlCollection = new ArrayList<String>();
               
         	   for (int j=0; j<collVals.size(); j++) {          			
           		  String collectionName = collVals.get(j);
           		  //collectionName = collectionName.substring(collectionName.lastIndexOf(":")+1);
           		  String lowCollName = collectionName.toLowerCase();
           		  
           		  if (lowCollName.contains("data")) {
           		     dataCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("document")) {
           		     docCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("context")) {
           		     ctxCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("browse")) {
           		     browseCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("calibration")) {
           		     calibCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("geometry")) {
           		     geomCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("miscellaneous")) {
           		     miscCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("spice_kernel")) {
           		     spiceCollection.add(collectionName);
           		  }
           		  else if (lowCollName.contains("xml_schema")) {
           		     xmlCollection.add(collectionName);
           		  }
           	   }
           	   
           	   if (dataCollection.size()>0) {
           		  %>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>DATA COLLECTION</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		 <%
		          for (int i=0; i<dataCollection.size(); i++) {
		             String val = dataCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
    	                
    	              SolrDocument collDoc = pds4Search.getContext(val);
    	              if (collDoc!=null && pds4Search.getValues(collDoc, "title")!=null) {
    	                 linkVal = pds4Search.getValues(collDoc, "title").get(0);
		             %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=linkVal%></a><br>
    	             <%	
    	             }
    	             else {
    	                out.println(val+"<br>");
    	             }
           		  }
           		  %>
           		  </td>
           	   </TR>
           	   <%
           	    }
           		if (docCollection.size()>0) {
           		%>
               <TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>DOCUMENT COLLECTION</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<docCollection.size(); i++) {
		             String val = docCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          
		          		SolrDocument collDoc = pds4Search.getContext(val);
    	                if (collDoc!=null && pds4Search.getValues(collDoc, "title")!=null) {
    	                   linkVal = pds4Search.getValues(collDoc, "title").get(0);
		               %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=linkVal%></a><br>
    	             <%	
    	             }
    	             else 
    	                out.println(val);	
           		  }        		  
           		  %>
           		  </td>
           		</TR>
           		<%
           		}
           		if (ctxCollection.size()>0) {
           		%>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>CONTEXT COLLECTION</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<ctxCollection.size(); i++) {
		             String val = ctxCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          		
		          		SolrDocument collDoc = pds4Search.getContext(val);
    	                if (collDoc!=null && pds4Search.getValues(collDoc, "title")!=null) {
    	                   linkVal = pds4Search.getValues(collDoc, "title").get(0);
		               %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=linkVal%></a><br>
    	             <%	
    	             }
    	             else 
    	                out.println(val);	
           		  }          		  
           		  %>
           		  </td>
           		</TR>
           		<%
           		}
           		if (browseCollection.size()>0) {
           		%>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>BROWSE COLLECTION</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<browseCollection.size(); i++) {
		             String val = browseCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          		SolrDocument collDoc = pds4Search.getContext(val);
    	                if (collDoc!=null && pds4Search.getValues(collDoc, "title")!=null) {
    	                   linkVal = pds4Search.getValues(collDoc, "title").get(0);
		               %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=linkVal%></a><br>
    	             <%	
    	             }
    	             else 
    	                out.println(val);	
           		  }              		  
           		  %>
           		  </td>
           		</TR>
           		<%
           		}
           		if (calibCollection.size()>0) {
           		%>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>CALIBRATION COLLECTION</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<calibCollection.size(); i++) {
		             String val = calibCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          		          		SolrDocument collDoc = pds4Search.getContext(val);
    	                if (collDoc!=null && pds4Search.getValues(collDoc, "title")!=null) {
    	                   linkVal = pds4Search.getValues(collDoc, "title").get(0);
		               %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=linkVal%></a><br>
    	             <%	
    	             }
    	             else 
    	                out.println(val);	
           		  }    
           		  %>
           		  </td>
           		</TR>
           		<%
           		}
           		if (geomCollection.size()>0) {
           		%>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>GEOMETRY COLLECTION</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<geomCollection.size(); i++) {
		             String val = geomCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          		          		SolrDocument collDoc = pds4Search.getContext(val);
    	                if (collDoc!=null && pds4Search.getValues(collDoc, "title")!=null) {
    	                   linkVal = pds4Search.getValues(collDoc, "title").get(0);
		               %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=linkVal%></a><br>
    	             <%	
    	             }
    	             else 
    	                out.println(val);	
           		  }    
           		  %>
           		  </td>
           		</TR>
           		<%
           		}
           		if (spiceCollection.size()>0) {
           		%>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>SPICE KERNAL COLLECTION</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<spiceCollection.size(); i++) {
		             String val = spiceCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          		          		SolrDocument collDoc = pds4Search.getContext(val);
    	                if (collDoc!=null && pds4Search.getValues(collDoc, "title")!=null) {
    	                   linkVal = pds4Search.getValues(collDoc, "title").get(0);
		               %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=linkVal%></a><br>
    	             <%	
    	             }
    	             else 
    	                out.println(val);	
           		  }    
           		  %>
           		  </td>
           		</TR>
           		<%
           		}
           		if (xmlCollection.size()>0) {
           		%>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>XML SCHEMA COLLECTION</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<xmlCollection.size(); i++) {
		             String val = xmlCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          		          		SolrDocument collDoc = pds4Search.getContext(val);
    	                if (collDoc!=null && pds4Search.getValues(collDoc, "title")!=null) {
    	                   linkVal = pds4Search.getValues(collDoc, "title").get(0);
		               %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=linkVal%></a><br>
    	             <%	
    	             }
    	             else 
    	                out.println(val);	
           		  }    
           		  %>
           		  </td>
           		</TR>
           		<%
           		}
           		if (miscCollection.size()>0) {
           		%>
           		<TR>
                  <td bgcolor="#F0EFEF" width=215 valign=top>MISCELLANEOUS COLLECTION</td> 
                  <td bgcolor="#F0EFEF" valign=top>

		          <%
		          for (int i=0; i<miscCollection.size(); i++) {
		             String val = miscCollection.get(i);
		             if (val.contains("::"))
    	                val = val.substring(0, val.indexOf("::")); 
		          		          		SolrDocument collDoc = pds4Search.getContext(val);
    	                if (collDoc!=null && pds4Search.getValues(collDoc, "title")!=null) {
    	                   linkVal = pds4Search.getValues(collDoc, "title").get(0);
		               %>
    	    	   <a href="/ds-view/pds/viewCollection.jsp?identifier=<%=val%>" target="_blank"><%=linkVal%></a><br>
    	             <%	
    	             }
    	             else 
    	                out.println(val);	
           		  }    
           		  %>
           		  </td>
           		</TR>
           		  <%
           		  }
             } // end if (collVals!=null)
         } // end if (anyCollectionValue)            
      } // end else 
      } catch (Exception e) {
  
      %>
        <TR>
         <td bgcolor="#F0EFEF" width=200 valign=top> 
            <b>No Search Service found: <%=searchUrl%></b> to retrieve the Bundle Information.
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

