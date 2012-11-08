<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
%>
<html>
<head>
   <title>PDS Data Set Profile</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.registry.model.ExtrinsicObject, gov.nasa.pds.dsview.registry.Constants, 
                    java.util.*, java.net.*, java.io.*" %>
   <SCRIPT LANGUAGE="JavaScript">
      <%@ include file="/pds/utils.js"%>
   </SCRIPT>
</head>

<%!
String constructURL (String url, String dsid) {
   try {
      URL aURL = new URL(url);
      String filename = aURL.getFile();

      // don't pass any params to reslink that ends with /
      if (filename.endsWith("/")) {
         return url;
      }

      // per Pam Woncik 03/12/03 - if the URL contains "pdsimg" or "pdsimage" (ie. Atlas browsers),
      // don't append the query string
      if (url.indexOf("pdsimg") > 0 || url.indexOf("pdsimage") > 0 ||
         url.indexOf("pds-imaging")>0 ) {
         return url;
      }

      int pos = filename.indexOf('?');
      String browserURL = url;
      if (pos > 0) {
         browserURL += "&DATA_SET_ID=" + dsid;
      } else {
         browserURL += "?DATA_SET_ID=" + dsid;
      }

      return browserURL;
   } catch (java.net.MalformedURLException e) {
      e.printStackTrace();
      return url;
   }
}

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
   <table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
   <tr>
      <td>
         <table width="760" border="0" cellspacing="3" cellpadding="2">
		    <tr valign="TOP">
               <td valign="TOP" colspan="2" class="pageTitle">
                  <br><FONT color="#6F4D0E"><b>Dataset Information</b></font><br><br>
               </td>
            </tr>
<%      
if (cleanParam(request.getParameter("dsid"))==null) {
%>    
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>dsid</b>.
               </td>
            </tr>
<%
}
else {
   String dsid = request.getParameter("dsid");
   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);

   String tmpDsid = dsid.replaceAll("/", "-");
   //out.println("dsid = " + dsid + "    dsid_lower = " + dsid_lower);
   ExtrinsicObject dsObj = searchRegistry.getExtrinsic("urn:nasa:pds:data_set."+tmpDsid);
   //out.println("<br>citation = " + searchRegistry.getSlotValues(product, "data_set_citation_text").toString());
 
   if (dsObj==null) { 
   %>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Information not found for dsid <b><%=dsid%></b>. Please verify the value.
               </td> 
            </tr>
   <% 
   }  // end if (dsObj==null)
   else { 
       //out.println("<br>guid = " + dsObj.getGuid());
        
       for (java.util.Map.Entry<String, String> entry: Constants.dsPds3ToRegistry.entrySet()) {
          String key = entry.getKey();
	      String tmpValue = entry.getValue();
	      //out.println("key = " + key + "   tmpValue = " + tmpValue);
          %>
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top><%=key%></td> 
               <td bgcolor="#F0EFEF" valign=top>
          <% 
          String val = "";
          List<String> slotValues = searchRegistry.getSlotValues(dsObj, tmpValue);
          
          if (slotValues!=null) {
             if (tmpValue.equals("data_set_description") ||
                 tmpValue.equals("data_set_confidence_level_note")) {                      
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
          } // end if (slotValues!=null)
          else {
             if (tmpValue.startsWith("mission_")) {
                String msnValue = searchRegistry.getMissionName(dsObj).get(0);
                String lid = msnValue.substring(0, msnValue.indexOf("::"));
    	       	ExtrinsicObject msnObj = searchRegistry.getExtrinsic(lid);   	       		       	
    	        //out.println(searchRegistry.getSlotValues(msnObj, tmpValue).get(0).toUpperCase() + "<br>"); 
    	       	val = searchRegistry.getSlotValues(msnObj, tmpValue).get(0).toUpperCase();	
    	       	if (tmpValue.equals("mission_name")) { 
    	       	%>
    	       	   <a href="/ds-view/pds/viewMissionProfile.jsp?MISSION_NAME=<%=val%>" target="_blank"><%=val%></a><br> 
    	       	<%
    	       	}
    	       	else {
    	       	   out.println(val+ "<br>");
    	       	}
             } 
             else if (tmpValue.startsWith("instrument_host_")) {
                List<String> svalues = searchRegistry.getSlotValues(dsObj, "instrument_host_ref");     	 
    	 		for (int j=0; j<svalues.size(); j++) {
    	 		   String aValue = (String) svalues.get(j);
    	   	       String lid = aValue.substring(0, aValue.indexOf("::"));
    	    	   ExtrinsicObject insthostObj = searchRegistry.getExtrinsic(lid);
    	    	   val = searchRegistry.getSlotValues(insthostObj, tmpValue).get(0).toUpperCase();
    	    	   if (tmpValue.equals("instrument_host_id")) {
    	    	   %>
    	    	   <a href="/ds-view/pds/viewHostProfile.jsp?INSTRUMENT_HOST_ID=<%=val%>" target="_blank"><%=val%></a><br> 
    	    	   <%
    	    	   }	
    	    	   else {
    	              out.println(val + "<br>");
    	           }
    	        }
             }
             else if (tmpValue.startsWith("instrument_") && !tmpValue.startsWith("instrument_host_")) {
                List<String> svalues = searchRegistry.getSlotValues(dsObj, "instrument_ref");     	 
    	 		for (int j=0; j<svalues.size(); j++) {
    	 		   String aValue = (String) svalues.get(j);
    	   		   String lid = aValue.substring(0, aValue.indexOf("::"));
    	    	   ExtrinsicObject instObj = searchRegistry.getExtrinsic(lid);
    	    	   val = searchRegistry.getSlotValues(instObj, tmpValue).get(0).toUpperCase();
    	    	   if (tmpValue.equals("instrument_id")) {
    	    	   %>
    	    	   <a href="/ds-view/pds/viewInstrumentProfile.jsp?INSTRUMENT_ID=<%=val%>" target="_blank"><%=val%></a><br>
    	    	   <%
    	    	   }
    	    	   else {
    	        	  out.println(val + "<br>");
    	           }
    	        }
             }
             else if (tmpValue.startsWith("target_")) {
                String targetValue = searchRegistry.getTargetName(dsObj).get(0);
                String lid = targetValue.substring(0, targetValue.indexOf("::"));
    	       	ExtrinsicObject targetObj = searchRegistry.getExtrinsic(lid);   	       		       	
    	       	out.println(searchRegistry.getSlotValues(targetObj, tmpValue).get(0).toUpperCase() + "<br>"); 	
             }
             else if (tmpValue.equals("resource_link") ||
                      tmpValue.equals("resources")) {
                List<String> rvalues = searchRegistry.getResourceRefs(dsObj);
     	        String refLid = rvalues.get(0);
         	    refLid = refLid.substring(0, refLid.indexOf("::"));
         	    //out.println("refLid = " + refLid + "<br>");
         	    ExtrinsicObject resource1 = searchRegistry.getExtrinsic(refLid);
         	    if (resource1!=null) {
         			String resname, reslink;
                	if (tmpValue.equals("resource_link")) {
         	       	//resname = searchRegistry.getSlotValues(resource1, "resource_name").get(0);
         	       	reslink = searchRegistry.getSlotValues(resource1, "resource_url").get(0);
         	       %>
         		   <a href=<%=constructURL(reslink, dsid)%> target="_new"><%=reslink%></a> 
         		<%
         	    } 
         	    else {
         	       for (int j=1; j<rvalues.size(); j++) {
         		      //out.println(rvalues.get(j) + "<br>");
         			  refLid = rvalues.get(j);
         			  refLid = refLid.substring(0, refLid.indexOf("::"));
         			  ExtrinsicObject resource = searchRegistry.getExtrinsic(refLid);
         			  //out.println("resource_name = " + searchRegistry.getSlotValues(resource, "resource_name").get(0) + "<br>");
         			  //out.println("resource_url = " + searchRegistry.getSlotValues(resource, "resource_url").get(0) + "<br>");
         			  resname = searchRegistry.getSlotValues(resource, "resource_name").get(0);
         			  reslink = searchRegistry.getSlotValues(resource, "resource_url").get(0); 
         		      %>
         			  <li><a href=<%=constructURL(reslink, dsid)%> target="_new"><%=resname%></a><br>
         			  <%
         		   } // end for
         	    } // end else
         	    } // end if resource!=null
             } // end resource_link
             else if (tmpValue.startsWith("node_name")) {
                List<String> svalues = searchRegistry.getSlotValues(dsObj, "node_ref");    
                if (svalues!=null) { 	 
    	 	       for (int j=0; j<svalues.size(); j++) {
    	 		      String aValue = (String) svalues.get(j);
    	   		      String lid = aValue.substring(0, aValue.indexOf("::"));
    	    	      ExtrinsicObject nodeObj = searchRegistry.getExtrinsic(lid);
    	    	      if (nodeObj!=null) {
    	    	         val = searchRegistry.getSlotValues(nodeObj, tmpValue).get(0).toUpperCase();
    	    	         out.println(val + "<br>");
    	    	      }
    	           }
    	        }
             }
                         
             if (key.equals("TELEPHONE_NUMBER")) {
                List<String> svalues = searchRegistry.getSlotValues(dsObj, "node_ref");     
                if (svalues!=null) {	 
    	 	       for (int j=0; j<svalues.size(); j++) {
    	 		      String aValue = (String) svalues.get(j);
    	   		      String lid = aValue.substring(0, aValue.indexOf("::"));
    	    	      ExtrinsicObject nodeObj = searchRegistry.getExtrinsic(lid);
    	    	      if (nodeObj!=null) {
    	    	         List<String> svalues2 = searchRegistry.getSlotValues(nodeObj, "node_to_data_archivist");
    	    	         if (svalues2!=null)  {
    	    	   	        String tmpVal = svalues2.get(0);
    	    	   	  	    String personLid = tmpVal.substring(0, tmpVal.indexOf("::"));
    	    	   	  	    ExtrinsicObject personObj = searchRegistry.getExtrinsic(personLid);
    	           	        String tmpVal2 = searchRegistry.getSlotValues(personObj, tmpValue).get(0).toUpperCase();
    	    	  	        out.println(tmpVal2 + "<br>");
    	    	         }
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
   } // else
}
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

