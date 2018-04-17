<%
   String pdshome = application.getInitParameter("pdshome.url");
   String searchUrl = application.getInitParameter("search.url");  
%>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>PDS: Data Set Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.dsview.registry.PDS3Search, gov.nasa.pds.dsview.registry.Constants, 
                    org.apache.solr.common.SolrDocument, 
                    java.util.*, java.net.*, java.io.*,java.lang.*" %>
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
                  <b>Data Set Information</b><br/>
               </td>
            </tr>
<%
String dsid = request.getParameter("dsid");
if ((dsid == null) || (dsid == "")) {
%>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Please specify a valid <b>dsid</b>.
               </td>
            </tr>
<%
}
else {
   PDS3Search pds3Search = new PDS3Search(searchUrl);
   
   String tmpDsid = dsid.toLowerCase();
   /*
   //dsid = tmpDsid.replaceAll("%2F", "/");
   tmpDsid = tmpDsid.replaceAll("%2F", "-");
   tmpDsid = tmpDsid.replaceAll("/", "-");
   tmpDsid = tmpDsid.replaceAll(" ", "_");
   tmpDsid = tmpDsid.replaceAll("\\(", "");
   tmpDsid = tmpDsid.replaceAll("\\)", "");
   */
   
   //out.println("dsid = " + dsid + "    dsid_lower = " + dsid_lower);
   
   try {
   	SolrDocument doc = pds3Search.getDataSet(tmpDsid.toLowerCase());
   	//SolrDocument doc = pds3Search.getDataSet("urn:nasa:pds:context_pds3:data_set:data_set."+tmpDsid.toLowerCase());
   	
   if (doc==null) { 
   %>
            <tr valign="TOP">
               <td bgcolor="#F0EFEF" width=200 valign=top>
                  Information not found for dsid <b><%=dsid%></b>. Please verify the value.
               </td> 
            </tr>
   <% 
   }  // end if (doc==null)
   else {         
       for (java.util.Map.Entry<String, String> entry: Constants.dsPds3ToSearch.entrySet()) {
          String key = entry.getKey();
	      String tmpValue = entry.getValue();
	      //out.println("key = " + key + "   tmpValue = " + tmpValue);
          %>
            <TR>
               <td bgcolor="#F0EFEF" width=200 valign=top><%=key%></td> 
               <td bgcolor="#F0EFEF" valign=top>
          <% 
          String val = "";
          List<String> slotValues = pds3Search.getValues(doc, tmpValue);         
          if (slotValues!=null) {
             if (tmpValue.equals("data_set_description") ||
                 tmpValue.equals("confidence_level_note")) {                      
                val = slotValues.get(0);
             %>
                  <pre><tt><%=val%></tt></pre>
             <%
             }
             else if (tmpValue.equals("investigation_name")) {
                List<String> mvalues = pds3Search.getValues(doc, "investigation_name");
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
             } 
             else if (tmpValue.equals("instrument_host_id")) {
                List<String> svalues = pds3Search.getValues(doc, tmpValue);     	 
    	 		for (int j=0; j<svalues.size(); j++) {
    	 		   String aValue = (String) svalues.get(j);
    	   	       val = aValue;
    	    	   %>
    	    	   <a href="/ds-view/pds/viewHostProfile.jsp?INSTRUMENT_HOST_ID=<%=val%>" target="_blank"><%=val%></a><br> 
    	    	   <%
    	        }
             }
             //else if (tmpValue.equals("instrument_id") && !tmpValue.startsWith("instrument_host_")) {
             else if (tmpValue.equals("instrument_id")) {
                List<String> svalues = pds3Search.getValues(doc, tmpValue);      	 
    	 		for (int j=0; j<svalues.size(); j++) {
    	 		   String aValue = (String) svalues.get(j);
    	    	   val = aValue;
    	    	   
    	    	   String instHostId = pds3Search.getValues(doc, "instrument_host_id").get(0);
    	   		   if (instHostId!=null) {
    	      %>
    	              <a href="/ds-view/pds/viewInstrumentProfile.jsp?INSTRUMENT_ID=<%=val%>&INSTRUMENT_HOST_ID=<%=instHostId%>" target="_blank"><%=val%></a><br>  	       	
              <%   }
    	   		   else {              
    	      %>
    	              <a href="/ds-view/pds/viewInstrumentProfile.jsp?INSTRUMENT_ID=<%=val%>" target="_blank"><%=val%></a><br>  	       	
              <%   }
    	        }
             }
             else if (tmpValue.equals("target_name")) {
                if (pds3Search.getValues(doc, tmpValue)!=null) {                   
                   List<String> targetValues = pds3Search.getValues(doc, tmpValue);
    		       val = "";
    		       if (targetValues!=null && targetValues.size()>0) {
    	 	          for (int i=0; i<targetValues.size(); i++) {
    	 		         val = (String) targetValues.get(i);
    	 		               	    	              
    	    	         // need to pass target_type, how to make sure the order with the target_name and target_type????
    	    	     %>
    	    	         <a href="/ds-view/pds/viewTargetProfile.jsp?TARGET_NAME=<%=val%>" target="_blank"><%=val%></a><br>
    	            <%} // end for
    	           } // end if
    	           else 
    	              out.println(val);
    	        }
             }
             else if (tmpValue.equals("resource_ref")) {
                List<String> rvalues = pds3Search.getValues(doc, tmpValue);           
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
                   } // end for
         	    }  // end if (rvalues!=null)  	
             } // end resoure_id
             else if (tmpValue.startsWith("node_id")) {
                List<String> svalues = pds3Search.getValues(doc, tmpValue);
                if (svalues!=null) { 	 
    	 	       for (int j=0; j<svalues.size(); j++) {
    	 		      String lid = (String) svalues.get(j);
    	   		      if (lid.indexOf("::")!=-1) 
    	   		         lid = lid.substring(0, lid.indexOf("::"));
    	    	      val = lid;
    	    	      out.println(val + "<br>");
    	           }
    	        }
             }
             else {
                for (int j=0; j<slotValues.size(); j++) {
                   out.println(slotValues.get(j) + "<br>");
                }
             }
             
             //}   // end  else
          } // end if (slotValues!=null)
          %>
               </td>
            </TR>     
      <%        
       } // for loop
   } // else
   } catch (Exception e) {
   }
}
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

