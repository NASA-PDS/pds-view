<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
%>
<HTML>
<head>
   <title>PDS Data Set Profile</title>
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

   <div id="submenu">
   <div id="submenu_data">
   <h2 class="nonvisual">Menu: PDS data</h2>
   <ul>
      <li id="data_data_search"><a href="http://pds.jpl.nasa.gov/tools/data-search/">Data Search</a></li>
      <li id="data_text_search"><a href="http://pds.jpl.nasa.gov/tools/text-search/">Text Search</a></li>
      <li><a href="/ds-view/pds/index.jsp">Form Search</a></li>
      <li id="data_how_to_search"><a href="http://pds.jpl.nasa.gov/data/how-to-search.shtml">How to Search</a></li>
      <li id="data_data_set_status"><a href="http://pds.jpl.nasa.gov/tools/dsstatus/">Data Set Status</a></li>
      <li id="data_release_summary"><a href="http://pds.jpl.nasa.gov/tools/subscription_service/SS-Release.shtml">Data Release Summary</a></li>
   </ul>
   </div>
   <div class="clear"></div>
   </div>

<!-- Main content -->
<div id="content">
   <div style="border-top: 1px solid_white;">
   <table align="center" bgColor="#FFFFFF" border="0" CELLPADDING="10" CELLSPACING="0">
     <tr>
       <td>    
<%!
String constructURL (String url, String dsid) {
   try {
      URL aURL = new URL(url);
      String filename = aURL.getFile();

      // do not pass any params to reslink that ends with /
      if (filename.endsWith("/")) {
         return url;
      }
   
      //added for NAIF ftp site - added 02.07.05 - was not working with dsid being appended to url
      if (url.indexOf("ftp://naif") != -1) {
         return url;
      }

      // per Pam Woncik 03/12/03 - if the URL contains "pdsimg" or "pdsimage" (ie. Atlas browsers),
      // don not append the query string
      if (url.indexOf("pdsimg") > 0 || url.indexOf("pdsimage") > 0 ||
         url.indexOf("pds-imaging")>0 ) {
         return url;
      }

      int pos = filename.indexOf('?');
      String browserURL = url;
      if (pos > 0) {
         browserURL += "&DATA_SET_ID=" + dsid;
      } 
      else {
         browserURL += "?DATA_SET_ID=" + dsid;
      }

      return browserURL;

   } catch (java.net.MalformedURLException e) {
      e.printStackTrace();
      return url;
   }
} // end of constructURL method


String fixQuery(String q) {
   String[] entity = {"&","<",">","\"","'","\\"};
   String[] replace = {"&amp;","&lt;","&gt;","&quot;","&#x27;","&#x2F","_"};

   String query = "", next="";

   for (int i=0; i<q.length(); i++) {
      next = String.valueOf(q.charAt(i));
      for (int j=0; j<entity.length; j++)
         if (next.equals(entity[j]))
            next = replace[j];

      query += next;
   }
   return query;
} // end of fixQuey method
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
String dsid_lower = "";
String volume = "";
String ancillary  = "";
String nodename = "";
String psclass =  "";
String localdsid =  "";
while (names.hasMoreElements()) {
   String param = (String) names.nextElement();
   if (param.equals("volume")) {
      volume = URLEncoder.encode(request.getParameter("volume"),"UTF-8");
   } else if (param.equals("nodename")) {
      nodename = URLEncoder.encode(request.getParameter("nodename"),"UTF-8");
   } else if (param.equals("ancillary")) {
      ancillary = URLEncoder.encode(request.getParameter("ancillary"),"UTF-8");
   } else if (param.equals("psclass")) {
      psclass = URLEncoder.encode(request.getParameter("psclass"),"UTF-8");
   } else if (param.equals("localdsid")) {
      localdsid = URLEncoder.encode(request.getParameter("localdsid"),"UTF-8");
   } else if (param.equals("datasetid")) {
      dsid = URLEncoder.encode(request.getParameter("datasetid"),"UTF-8");
   } else if (param.equals("dsid")) {
      dsid = URLEncoder.encode(request.getParameter("dsid"),"UTF-8");
   } else if (param.equals("Identifier")) {
      dsid = URLEncoder.encode(request.getParameter("Identifier"),"UTF-8");
   } else if (param.equals("identifier")) {
      dsid = URLEncoder.encode(request.getParameter("identifier"),"UTF-8");
   }
}
%>
   
<%      
if (dsid.length() == 0) {
%>
   <table align="center" width="760" border="0" cellspacing="3" cellpadding="10">
      <tr valign="TOP">
         <td valign="TOP" colspan="2" class="pageTitle">
            <br><FONT color="#6F4D0E"><b>Dataset Information</b></font><br><br>
         </td>
      </tr>
      <tr valign="TOP">
         <td bgcolor="#F0EFEF" width=200 valign=top>
            Please specify a <b>dsid</b> or <b>Identifier</b>
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
   dsid_lower = dsid.toLowerCase();

   String tmpDsid = dsid.replaceAll("/", "-");
   //out.println("dsid = " + dsid + "    dsid_lower = " + dsid_lower);
   ExtrinsicObject product = searchRegistry.getExtrinsic("urn:nasa:pds:data_set."+tmpDsid);

   if (product==null) {
%>
   <table align="center" width="760" border="0" cellspacing="3" cellpadding="10">
      <tr valign="TOP">
         <td valign="TOP" colspan="2" class="pageTitle">
            <br><FONT color="#6F4D0E"><b>Dataset Information</b></font><br><br>
         </td>
      </tr>
      <tr valign="TOP">
         <td bgcolor="#F0EFEF" width=200 valign=top>
            Information not found for dsid <b><%=dsid%></b>. Please verify the value.
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
	                       <font color="#6F4D0E">
	                       <%
	                       if (searchRegistry.getSlotValues(product, "data_set_terse_description")!=null) {
	                          out.println(searchRegistry.getSlotValues(product, "data_set_terse_description").get(0));
	                       }%>
	                       </font>
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
                        <td>Access/Download Data Set</td>
                        <td>
    	                   <%
    		               List<String> rvalues = searchRegistry.getResourceRefs(product);
    		               String resname, reslink;
       	                   String refLid = rvalues.get(0);
       	                   if (refLid!=null) {
                    	      refLid = refLid.substring(0, refLid.indexOf("::"));
         	                  ExtrinsicObject resource1 = searchRegistry.getExtrinsic(refLid);
         
         	                  resname = searchRegistry.getSlotValues(resource1, "resource_name").get(0);
         	                  reslink = searchRegistry.getSlotValues(resource1, "resource_url").get(0); 
         	               
                           %>
                           <a href=<%=constructURL(reslink, dsid)%> target="_new">Search for Products with the <%=resname%></a> 
                           <%
                           } %>   
                        </td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Data set abstract</td>
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
    	                   for (int i=0; i<mvalues.size(); i++) {
    	   		              String tmpValue = (String) mvalues.get(i);
    	       	              String lid = tmpValue.substring(0, tmpValue.indexOf("::"));
    	       	              ExtrinsicObject msnObj = searchRegistry.getExtrinsic(lid);
    	       	              //out.println(searchRegistry.getSlotValues(msnObj, "mission_name").get(0).toUpperCase() + "<br>");
    	       	              val = searchRegistry.getSlotValues(msnObj, "mission_name").get(0).toUpperCase(); 
    	       	           %>
    	       	           <a href="/ds-view/pds/viewMissionProfile.jsp?MISSION_NAME=<%=val%>" target="_blank"><%=val%></a><br>  	       	
                         <%} // end for   %>
                        </td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Dataset Information</td>
                        <td><a href="/ds-view/pds/viewProfile.jsp?dsid=<%=dsid%>" target="_blank"><%=dsid%></a></td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Instrument Host Information</td>
                        <td>
                           <%
    	                   List<String> svalues = searchRegistry.getSlotValues(product, "instrument_host_ref");
                           //need to test this later after solving problem on pdsdev with INSTRUMENT_HOST_NAME
    	 
    	                   // should be alternate_id??? can't find this (ask Sean) 
    	                   for (int i=0; i<svalues.size(); i++) {
    	 	                  String tmpValue = (String) svalues.get(i);
    	   	                  String lid = tmpValue.substring(0, tmpValue.indexOf("::"));
    	                      ExtrinsicObject insthostObj = searchRegistry.getExtrinsic(lid);
    	                      //out.println(searchRegistry.getSlotValues(insthostObj, "instrument_host_name").get(0).toUpperCase() + "<br>"); 
    	                      val = searchRegistry.getSlotValues(insthostObj, "instrument_host_id").get(0).toUpperCase();
    	                   %>
    	                   <a href="/ds-view/pds/viewHostProfile.jsp?INSTRUMENT_HOST_ID=<%=val%>" target="_blank"><%=val%></a><br>  	       	
                         <%} // end for %>    	
                        </td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Instrument Information</td>
                        <td>
    	                   <%
       		               svalues =searchRegistry.getSlotValues(product, "instrument_ref");
    	 	               for (int i=0; i<svalues.size(); i++) {
    	 		              String tmpValue = (String) svalues.get(i);
    	   		              String lid = tmpValue.substring(0, tmpValue.indexOf("::"));
    	   		              //out.println("lid = " + lid);
    	    	              ExtrinsicObject instObj = searchRegistry.getExtrinsic(lid);
    	    	              //out.println(searchRegistry.getSlotValues(instObj, "instrument_id").get(0).toUpperCase() + "<br>");
    	                      val = searchRegistry.getSlotValues(instObj, "instrument_id").get(0).toUpperCase(); 
    	                   %>
    	                   <a href="/ds-view/pds/viewInstrumentProfile.jsp?INSTRUMENT_ID=<%=val%>" target="_blank"><%=val%></a><br>  	       	
                         <%}  // end for %>    	
    	                </td>
                     </tr>
  
                     <!--added target information link which links to viewTargetProfile.jsp-->
                     <tr bgcolor="#E7EEF9">
                        <td>Target Information</td>
                        <td>
                           <%
    		               svalues =searchRegistry.getSlotValues(product, "target_ref");
    	 	               for (int i=0; i<svalues.size(); i++) {
    	 		              String tmpValue = (String) svalues.get(i);
    	   		              String lid = tmpValue.substring(0, tmpValue.indexOf("::"));
    	   		              //out.println("lid = " + lid);
    	    	              ExtrinsicObject targetObj = searchRegistry.getExtrinsic(lid);
    	    	              val = searchRegistry.getSlotValues(targetObj, "target_name").get(0).toUpperCase();
    	    	              //out.println(searchRegistry.getSlotValues(targetObj, "target_name").get(0).toUpperCase() + "<br>");
    	    	           %>
    	    	           <a href="/ds-view/pds/viewTargetProfile.jsp?TARGET_NAME=<%=val%>" target="_blank"><%=val%></a><br>
    	                 <%}
    	                   %>
                        </td>
                     </tr>

                     <tr bgcolor="#E7EEF9">
                        <td>Other Resources</td>
                        <td>
                           <% 
                           for (int i=1; i<rvalues.size(); i++) {
                              //out.println(rvalues.get(i) + "<br>");
                              refLid = rvalues.get(i);
                              if (refLid!=null) {
                                 refLid = refLid.substring(0, refLid.indexOf("::"));
                                 ExtrinsicObject resource = searchRegistry.getExtrinsic(refLid);
                                 //out.println("resource_name = " + searchRegistry.getSlotValues(resource, "resource_name").get(0) + "<br>");
                                 //out.println("resource_url = " + searchRegistry.getSlotValues(resource, "resource_url").get(0) + "<br>");
                                 resname = searchRegistry.getSlotValues(resource, "resource_name").get(0);
                                 reslink = searchRegistry.getSlotValues(resource, "resource_url").get(0); 
                              %>
                              <a href="<%=constructURL(reslink, dsid)%>" target="_new"><%=resname%></a><br>
                         <%   }
                           } %>
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

