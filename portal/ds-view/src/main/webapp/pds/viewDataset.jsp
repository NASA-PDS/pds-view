<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
%>
<HTML>
<head>
   <title>PDS: Data Set Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
   
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">

   <%@page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" 
           contentType="text/html; charset=ISO-8859-1"
           import="gov.nasa.pds.registry.model.ExtrinsicObject,java.util.*,java.net.*, java.io.*" %>

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
   <table align="center" bgColor="#FFFFFF" border="0" CELLPADDING="10" CELLSPACING="0">
     <tr>
       <td>    
<%!
  /**
   * Null out the parameter value if any of the bad characters are present
   * that facilitate Cross-Site Scripting and Blind SQL Injection.
   */
  public String cleanParam(String str) {
    char badChars [] = {'|', ';', '$', '@', '\'', '"', '<', '>', '(', ')', ',', '\\', /* CR */ '\r' , /* LF */ '\n' , /* Backspace */ '\b'};
    String decodedStr = "";

    if (str != null) {
      decodedStr = URLDecoder.decode(str);
      for(int i = 0; i < badChars.length; i++) {
        if (decodedStr.indexOf(badChars[i]) >= 0) {
          return "";
        }
      }
    }
    return decodedStr;
  }
%>

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
      volume = cleanParam(request.getParameter("volume"));
   } else if (param.equals("nodename")) {
      nodename = cleanParam(request.getParameter("nodename"));
   } else if (param.equals("ancillary")) {
      ancillary = cleanParam(request.getParameter("ancillary"));
   } else if (param.equals("psclass")) {
      psclass = cleanParam(request.getParameter("psclass"));
   } else if (param.equals("localdsid")) {
      localdsid = cleanParam(request.getParameter("localdsid"));
   } else if (param.equals("datasetid")) {
      dsid = cleanParam(request.getParameter("datasetid"));
   } else if (param.equals("dsid")) {
      dsid = cleanParam(request.getParameter("dsid"));
   } else if (param.equals("Identifier")) {
      dsid = cleanParam(request.getParameter("Identifier"));
   } else if (param.equals("identifier")) {
      dsid = cleanParam(request.getParameter("identifier"));
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

   // need to call the registry service with dsid... to get Product_Data_Set_PDS3 & Product_Resource
   // 1. get Product_Data_Set_PDS3 extrinsic object with dsid (to get investigation_ref, instrument_ref, instrument_host_ref, target_ref &  resource_ref 
   // 2. get Product_Resource extrinsic object for the resource info

   // TODO: should get registryUrl from the properties file or env variable.
   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);
  
   dsid = dsid.toUpperCase();
   dsid = dsid.replaceAll("%2F", "/");

   String tmpDsid = dsid.replaceAll("%2F", "-");
   tmpDsid = tmpDsid.replaceAll("/", "-");
   tmpDsid = tmpDsid.toLowerCase();
   //out.println("dsid = " + dsid + "    dsid_lower = " + dsid_lower);
   ExtrinsicObject product = searchRegistry.getExtrinsic("urn:nasa:pds:context_pds3:data_set:data_set."+tmpDsid);

   if (product==null) {
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
   } // end if (product==null)
   else { 
   
      // for debugging, should comment out this if block  
      //out.println("<br>guid = " + product.getGuid());
      
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
	                       if (searchRegistry.getSlotValues(product, "data_set_terse_description")!=null) {
	                          out.println(searchRegistry.getSlotValues(product, "data_set_terse_description").get(0));
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
                        <td><%=searchRegistry.getSlotValues(product, "data_set_citation_text").get(0)%></td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Data Set Abstract</td>
                        <td><%=searchRegistry.getSlotValues(product, "data_set_abstract_description").get(0)%></td>
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
                           List<String> mvalues = searchRegistry.getMissionName(product);
                           String val = "";
                           if (mvalues!=null) {
    	                      for (int i=0; i<mvalues.size(); i++) {
    	   		                 String lid = (String) mvalues.get(i);
    	   		                 if (lid.indexOf("::")!=-1) 
    	       	                    lid = lid.substring(0, lid.indexOf("::"));
    	       	                 ExtrinsicObject msnObj = searchRegistry.getExtrinsic(lid);
    	       	                 //out.println(searchRegistry.getSlotValues(msnObj, "mission_name").get(0).toUpperCase() + "<br>");
    	       	                 if (msnObj!=null) 
    	       	              	   val = searchRegistry.getSlotValues(msnObj, "mission_name").get(0).toUpperCase(); 
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
    	                   List<String> svalues = searchRegistry.getSlotValues(product, "instrument_host_ref");
                           //need to test this later after solving problem on pdsdev with INSTRUMENT_HOST_NAME
    	                   if (svalues!=null) {
    	                   // should be alternate_id??? can't find this (ask Sean) 
    	                   for (int i=0; i<svalues.size(); i++) {
    	 	                  String lid = (String) svalues.get(i);
    	   	                  if (lid.indexOf("::")!=-1) 
    	       	                    lid = lid.substring(0, lid.indexOf("::"));
    	                      ExtrinsicObject insthostObj = searchRegistry.getExtrinsic(lid);
    	                      //out.println(searchRegistry.getSlotValues(insthostObj, "instrument_host_name").get(0).toUpperCase() + "<br>"); 
 							  if (insthostObj!=null) 
    	                         val = searchRegistry.getSlotValues(insthostObj, "instrument_host_id").get(0).toUpperCase();
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
       		               svalues =searchRegistry.getSlotValues(product, "instrument_ref");
       		               if (svalues!=null && svalues.size()>0) {
    	 	               for (int i=0; i<svalues.size(); i++) {
    	 		              String lid = (String) svalues.get(i);
    	   		              if (lid.indexOf("::")!=-1) 
    	       	                    lid = lid.substring(0, lid.indexOf("::"));
    	   		              //out.println("lid = " + lid);
    	    	              ExtrinsicObject instObj = searchRegistry.getExtrinsic(lid);
    	    	              //out.println(searchRegistry.getSlotValues(instObj, "instrument_id").get(0).toUpperCase() + "<br>");
    	    	              if (instObj!=null) 
    	                         val = searchRegistry.getSlotValues(instObj, "instrument_id").get(0).toUpperCase(); 
    	                   %>
    	                   <a href="/ds-view/pds/viewInstrumentProfile.jsp?INSTRUMENT_ID=<%=val%>" target="_blank"><%=val%></a><br>  	       	
                         <%}  // end for 
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
    		               svalues =searchRegistry.getSlotValues(product, "target_ref");
    		               if (svalues!=null && svalues.size()>0) {
    	 	               for (int i=0; i<svalues.size(); i++) {
    	 		              String lid = (String) svalues.get(i);
    	 		              if (lid.indexOf("::")!=-1) 
    	       	                    lid = lid.substring(0, lid.indexOf("::"));
    	   		              //out.println("lid = " + lid);
    	    	              ExtrinsicObject targetObj = searchRegistry.getExtrinsic(lid);
    	    	              if (targetObj!=null) 
    	    	                 val = searchRegistry.getSlotValues(targetObj, "target_name").get(0).toUpperCase();
    	    	              //out.println(searchRegistry.getSlotValues(targetObj, "target_name").get(0).toUpperCase() + "<br>");
    	    	           %>
    	    	           <a href="/ds-view/pds/viewTargetProfile.jsp?TARGET_NAME=<%=val%>" target="_blank"><%=val%></a><br>
    	                 <%} // end for
    	                   } // end if
    	                   else 
    	                      out.println(val);
    	                   %>
                        </td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Resources</td>
                        <td>
                           <% 
                           List<String> rvalues = searchRegistry.getResourceRefs(product);
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
                                    ExtrinsicObject resource = searchRegistry.getExtrinsic(refLid);
                                    //out.println("resource_name = " + searchRegistry.getSlotValues(resource, "resource_name").get(0) + "<br>");
                                    //out.println("resource_url = " + searchRegistry.getSlotValues(resource, "resource_url").get(0) + "<br>");
                                    if (resource!=null) {
                                       resname = searchRegistry.getSlotValues(resource, "resource_name").get(0);
                                       reslink = searchRegistry.getSlotValues(resource, "resource_url").get(0);
                              %>
                              <a href="<%=reslink%>" target="_new"><%=resname%></a><br>
                         <%         }
                                 }
                              } 
                           }%>
                        </td>
                     </tr>
                  </table>
               </td>
            </tr>
<%
	} // if matching product is found in the registry
} // if dsid is specified   
%>             
            </table>
         </td>         
      </tr>
   </table>
   </div>
</div>

<%@ include file="/pds/footer.html" %>

</BODY>
</HTML>

