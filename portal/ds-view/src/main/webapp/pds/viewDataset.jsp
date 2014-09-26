<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");     
%>
<HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Data Set Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />   
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">

   <%@page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" 
           contentType="text/html; charset=ISO-8859-1"
           import="gov.nasa.pds.dsview.registry.PDS3Search, gov.nasa.pds.dsview.registry.Constants, 
                   org.apache.solr.common.SolrDocument, org.apache.solr.common.SolrDocumentList,
                   java.util.*,java.net.*,java.io.*,java.lang.*" %>

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
   <table align="center" bgColor="#FFFFFF" border="0" CELLPADDING="10" CELLSPACING="0">
     <tr>
       <td>
<%
String[] displayedElements = {"DATA_SET_NAME", "DATA_SET_ID", "NSSDC_DATA_SET_ID",
		"DATA_SET_TERSE_DESCRIPTION", "DATASET_DESCRIPTION",
		"DATA_SET_RELEASE_DATE", "RESOURCE_LINK",
		"DATA_OBJECT_TYPE",  "START_TIME", "STOP_TIME",
		"MISSION_NAME", "MISSION_START_DATE", "MISSION_STOP_DATE",
		"TARGET_NAME", "TARGET_TYPE", 
		"INSTRUMENT_NAME", "INSTRUMENT_ID", "INSTRUMENT_TYPE", "INSTRUMENT_HOST_ID",
		"NODE_NAME","ARCHIVE_STATUS", "CONFIDENCE_LEVEL_NOTE",
		"CITATION_DESCRIPTION", "ABSTRACT_TEXT", "FULL_NAME", "TELEPHONE_NUMBER",
		"RESOURCES"};

Enumeration names = request.getParameterNames();
String dsid = "";
//String dsid_lower = "";
String volume = "";
String ancillary  = "";
String nodename = "";
String psclass =  "";
String localdsid =  "";
while (names.hasMoreElements()) {
   String param = (String) names.nextElement();
   if (param.equals("volume")) {
      volume = request.getParameter("volume");
   } else if (param.equals("nodename")) {
      nodename = request.getParameter("nodename");
   } else if (param.equals("ancillary")) {
      ancillary = request.getParameter("ancillary");
   } else if (param.equals("psclass")) {
      psclass = request.getParameter("psclass");
   } else if (param.equals("localdsid")) {
      localdsid = request.getParameter("localdsid");
   } else if (param.equals("datasetid")) {
      dsid = request.getParameter("datasetid");
   } else if (param.equals("dsid")) {
      dsid = request.getParameter("dsid");
   } else if (param.equals("Identifier")) {
      dsid = request.getParameter("Identifier");
   } else if (param.equals("identifier")) {
      dsid = request.getParameter("identifier");
   }
}
%>
   
<%      
if (dsid.length() == 0) {
%>
   <table align="center" width="760" border="0" cellspacing="3" cellpadding="10">
      <tr valign="TOP">
         <td valign="TOP" colspan="2" class="pageTitle">
            <b>Data Set Information</b><br/>
         </td>
      </tr>
      <tr valign="TOP">
         <td bgcolor="#F0EFEF" width=200 valign=top>
            Please specify a valid <b>dsid</b> or <b>Identifier</b>.
         </td>
      </tr>
    </table>
<%
}
else {

   dsid = dsid.toUpperCase();
   String tmpDsid = dsid.toLowerCase();
   /*
   //tmpDsid = tmpDsid.replaceAll("%2F", "-"); // '/'
   tmpDsid = tmpDsid.replaceAll(" ", "_");
   tmpDsid = tmpDsid.replaceAll("/", "-");
   tmpDsid = tmpDsid.replaceAll("\\(", "");
   tmpDsid = tmpDsid.replaceAll("\\)", "");
   tmpDsid = tmpDsid.replaceAll("&", "-");
   */
   
   //out.println("dsid = " + dsid + "    dsid_lower = " + tmpDsid);
  
   PDS3Search pds3Search = new PDS3Search(searchUrl);
   try {
     //SolrDocument doc = pds3Search.getDataSet("urn:nasa:pds:context_pds3:data_set:data_set."+tmpDsid);
   	 SolrDocument doc = pds3Search.getDataSet(tmpDsid);
   
   if (doc==null) {
%>
   <table align="center" width="760" border="0" cellspacing="3" cellpadding="10">
      <tr valign="TOP">
         <td valign="TOP" colspan="2" class="pageTitle">
            <b>Data Set Information</b><br/>
         </td>
      </tr>
      <tr valign="TOP">
         <td bgcolor="#F0EFEF" width=200 valign=top>
            Please specify a valid <b>dsid</b> or <b>Identifier</b>.
         </td> 
      </tr>
   </table>
<%
   } // end if (doc==null)
   else { 
      //adding target type value so that we know where to link the user 
      //for target information  ArrayList targetType = new ArrayList(); ?????
%>
   <table align="center" padding="18px" width="760" border="0" cellspacing="0" cellpadding="10">
      <tr>
         <td><img src="/ds-view/pds/images/spacer.gif" width="5" height="5" border="0"></td>
	  </tr>
	  <tr>
	     <td valign="top">
	        <table width="578" border="0" cellspacing="0" cellpadding="10">

	              <!-- main content begin -->
	              <table border="0" cellpadding="10" cellspacing="0">
	                 <tr>
	                    <td class="pageTitle">	                    
	                       <%
	                       if (pds3Search.getValues(doc, "data_set_terse_description")!=null) {
	                          out.println(pds3Search.getValues(doc, "data_set_terse_description").get(0));
	                       }%>
	                    </td>
	                 </tr>
	                 <tr>
	                    <td><img src="<%=pdshome%>images/gray.gif"  height="1" width="100%" alt="" border="0"></td>
	                 </tr>
	              </table>
                  <br>
                  
                  <table width="100%" border="0" cellspacing="1" cellpadding="10">
                     <tr bgcolor="#efefef">
                        <td colspan=2>&nbsp;</td>
                     </tr>
                     
                     <tr bgcolor="#E7EEF9">
                        <td>Citation</td>
                        <td><%=pds3Search.getValues(doc,"citation_description").get(0)%></td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Data Set Abstract</td>
                        <td><%=pds3Search.getValues(doc, "abstract_text").get(0)%></td>
                     </tr>
                     
                     <tr bgcolor="#E7EEF9">
                        <td>Search/Access Data</td>
                        <td>
                           <% 
                           List<String> rvalues = pds3Search.getValues(doc, "resource_ref");
                           //List<String> rvalues = pds3Search.getValues(doc, "resource_id");
                           String resname="", reslink="";
                           String refLid = "";
                           if (rvalues !=null) {
                              for (int i=0; i<rvalues.size(); i++) {
                                 //out.println(rvalues.get(i) + "<br>");
                                 refLid = rvalues.get(i);
                                 if (refLid!=null) {
                                    if (refLid.indexOf("::")!=-1) {
                                       refLid = refLid.substring(0, refLid.indexOf("::"));   
                                    }
                                    //refLid = refLid.replace("context_pds3", "context");
                                    SolrDocument refDoc = pds3Search.getResource(refLid);
                                    if (refDoc!=null) {
                                       resname = pds3Search.getValues(refDoc, "resource_name").get(0);
                                       reslink = pds3Search.getValues(refDoc, "resource_url").get(0);
                                       
                          %>
                              <li><a href="<%=reslink%>" target="_new"><%=resname%></a><br>
                         <%   
                                    }
                                    else {
                                       resname = refLid;
                                       reslink = refLid;
                                    }                                                             
                                 } // end if (refLid!=null)
                              }  // end for
                           }%>
                        </td>
                     </tr>

                     <tr>
		                <td>&nbsp;</td>
		                <td>&nbsp;</td>
                     </tr>

                     <tr bgcolor="#efefef">
                        <td colspan=2><b>Additional Information</b></td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Mission Information</td>
                        <td>
                           <%
                           List<String> mvalues = pds3Search.getValues(doc, "investigation_name");
                           String val = "";
                           if (mvalues!=null) {
    	                      for (int i=0; i<mvalues.size(); i++) {
    	   		                 String lid = (String) mvalues.get(i);
    	   		                 if (lid.indexOf("::")!=-1) 
    	       	                    lid = lid.substring(0, lid.indexOf("::"));
    	       	                    
    	       	                 val = lid;
    	       	           %>
    	       	           <a href="/ds-view/pds/viewMissionProfile.jsp?MISSION_NAME=<%=val%>" target="_blank"><%=val%></a><br>  	       	
                         <%   } // end for
                           } // end if
                           else 
                              out.println(val);
                          %>
                        </td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Data Set Information</td>
                        <td><a href="/ds-view/pds/viewProfile.jsp?dsid=<%=dsid%>" target="_blank"><%=dsid%></a></td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Instrument Host Information</td>
                        <td>
                           <%
    	                   List<String> svalues = pds3Search.getValues(doc, "instrument_host_id");
    	                   val = "";
                           //need to test this later after solving problem on pdsdev with INSTRUMENT_HOST_NAME
    	                   if (svalues!=null) {
    	                   // should be alternate_id??? can't find this (ask Sean) 
    	                   for (int i=0; i<svalues.size(); i++) {
    	 	                  String lid = (String) svalues.get(i);
    	   	                  if (lid.indexOf("::")!=-1) 
    	       	                 lid = lid.substring(0, lid.indexOf("::"));
    	                      
    	                      val = lid;
    	                   %>
    	                   <a href="/ds-view/pds/viewHostProfile.jsp?INSTRUMENT_HOST_ID=<%=val%>" target="_blank"><%=val%></a><br>  	       	
                         <%} // end for 
                           } // end if
                           else 
                              out.println(val);
                         %>    	
                        </td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Instrument Information</td>
                        <td>
    	                   <%
    	                   // need to pass instrument_host_id to instProfile.jsp
       		               svalues = pds3Search.getValues(doc, "instrument_id");
       		               val = "";
       		               if (svalues!=null && svalues.size()>0) {
    	 	               for (int i=0; i<svalues.size(); i++) {
    	 		              String lid = (String) svalues.get(i);
    	   		              if (lid.indexOf("::")!=-1) 
    	       	                 lid = lid.substring(0, lid.indexOf("::"));
    	   		              //out.println("lid = " + lid);
    	   		              val = lid;
    	   		              
    	   		              String instHostId = pds3Search.getValues(doc, "instrument_host_id").get(0);
    	   		              if (instHostId!=null) {
    	   		              %>
    	                   <a href="/ds-view/pds/viewInstrumentProfile.jsp?INSTRUMENT_ID=<%=val%>&INSTRUMENT_HOST_ID=<%=instHostId%>" target="_blank"><%=val%></a><br>  	       	
                         <%   }
    	   		              else {              
    	                   %>
    	                   <a href="/ds-view/pds/viewInstrumentProfile.jsp?INSTRUMENT_ID=<%=val%>" target="_blank"><%=val%></a><br>  	       	
                         <% }
                           }  // end for 
                           } // end if
                           else 
                              out.println(val);
                         %>    	
    	                </td>
                     </tr>
  
                     <!--added target information link which links to viewTargetProfile.jsp-->
                     <tr bgcolor="#E7EEF9">
                        <td>Target Information</td>
                        <td>
                           <%
                           // need to get target_type info to pass to target
    		               svalues = pds3Search.getValues(doc, "target_name");
    		               val = "";
    		               if (svalues!=null && svalues.size()>0) {
    	 	               for (int i=0; i<svalues.size(); i++) {
    	 		              String lid = (String) svalues.get(i);
    	 		              if (lid.indexOf("::")!=-1) 
    	       	                 lid = lid.substring(0, lid.indexOf("::"));
    	   		              //out.println("lid = " + lid);
    	    	              val = lid;
    	    	              
    	    	              // need to pass target_type, how to make sure the order with the target_name and target_type????
    	    	           %>
    	    	           <a href="/ds-view/pds/viewTargetProfile.jsp?TARGET_NAME=<%=val%>" target="_blank"><%=val%></a><br>
    	                 <%} // end for
    	                   } // end if
    	                   else 
    	                      out.println(val);
    	                   %>
                        </td>
                     </tr>
                  </table>
               </td>
            </tr>
<%
	} // if matching product is found in the registry
	
	} catch (Exception e) {
    }
   
} // if dsid is specified  
%>             
            </table>
         </td>         
      </tr>
   </table>
   </div>
</div>

<c:import url="/footer.html" context="/include" />

</BODY>
</HTML>

