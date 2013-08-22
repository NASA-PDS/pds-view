<%
   String pdshome = application.getInitParameter("pdshome.url");
   String registryUrl = application.getInitParameter("registry.url");
%>
<html>
<head>
   <title>PDS: Data Set Information</title>
   <META  NAME="keywords"  CONTENT="Planetary Data System">
   <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary archives.">
   <link href="/ds-view/pds/css/pds_style.css" rel="stylesheet" type="text/css">
   <%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" 
            isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
            import="gov.nasa.pds.registry.model.ExtrinsicObject, gov.nasa.pds.dsview.registry.Constants, 
                    java.util.*, java.net.*, java.io.*,java.lang.*" %>
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
   gov.nasa.pds.dsview.registry.SearchRegistry searchRegistry = new gov.nasa.pds.dsview.registry.SearchRegistry(registryUrl);

   String tmpDsid = dsid.replaceAll("/", "-");
   //out.println("dsid = " + dsid + "    dsid_lower = " + dsid_lower);
   ExtrinsicObject dsObj = searchRegistry.getExtrinsic("urn:nasa:pds:context_pds3:data_set:data_set."+tmpDsid.toLowerCase());
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
                if (searchRegistry.getMissionName(dsObj)!=null) {
                	String msnValue = searchRegistry.getMissionName(dsObj).get(0);
                	String lid = msnValue.substring(0, msnValue.indexOf("::"));
    	       		ExtrinsicObject msnObj = searchRegistry.getExtrinsic(lid);   	       		       	
    	        	//out.println(searchRegistry.getSlotValues(msnObj, tmpValue).get(0).toUpperCase() + "<br>"); 
    	        	if (msnObj!=null) {
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
    	       	}
    	       	else
    	       	   out.println(val+"<br>");
             } 
             else if (tmpValue.startsWith("instrument_host_")) {
                List<String> svalues = searchRegistry.getSlotValues(dsObj, "instrument_host_ref");     	 
    	 		for (int j=0; j<svalues.size(); j++) {
    	 		   String aValue = (String) svalues.get(j);
    	   	       String lid = aValue.substring(0, aValue.indexOf("::"));
    	    	   ExtrinsicObject insthostObj = searchRegistry.getExtrinsic(lid);
    	    	   if (insthostObj!=null) {
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
             }
             else if (tmpValue.startsWith("instrument_") && !tmpValue.startsWith("instrument_host_")) {
                List<String> svalues = searchRegistry.getSlotValues(dsObj, "instrument_ref");     	 
    	 		for (int j=0; j<svalues.size(); j++) {
    	 		   String aValue = (String) svalues.get(j);
    	   		   String lid = aValue.substring(0, aValue.indexOf("::"));
    	    	   ExtrinsicObject instObj = searchRegistry.getExtrinsic(lid);
    	    	   if (instObj!=null) {
    	    	      val = searchRegistry.getSlotValues(instObj, tmpValue).get(0).toUpperCase();
    	    	      if (tmpValue.equals("instrument_id")) {
    	    	   %>
    	    	   <a href="/ds-view/pds/viewInstrumentProfile.jsp?INSTRUMENT_ID=<%=val%>" target="_blank"><%=val%></a><br>
    	    	   <%
    	    	      }
    	    	      else {
    	        	     out.println(val + "<br>");
    	              }
    	           } // end if (instObj!=null)
    	        }
             }
             else if (tmpValue.startsWith("target_")) {
                if (searchRegistry.getTargetName(dsObj)!=null) {
                   String targetValue = searchRegistry.getTargetName(dsObj).get(0);
                   String lid = targetValue.substring(0, targetValue.indexOf("::"));
    	       	   ExtrinsicObject targetObj = searchRegistry.getExtrinsic(lid);   	
    	       	   if (targetObj!=null)        		       	
    	              out.println(searchRegistry.getSlotValues(targetObj, tmpValue).get(0).toUpperCase() + "<br>"); 
    	        }
    	        else 
    	           out.println(val + "<br>");	
             }
             else if (tmpValue.equals("resources")) {
                List<String> rvalues = searchRegistry.getResourceRefs(dsObj);
     	        String refLid = "";
         	    String resname="", reslink="";
         	    if (rvalues!=null) {
         	       for (int j=0; j<rvalues.size(); j++) {
         		      //out.println(rvalues.get(j) + "<br>");
         		      refLid = rvalues.get(j);
         		      if (refLid.indexOf("::")!=-1) 
         		      	refLid = refLid.substring(0, refLid.indexOf("::"));
         		      ExtrinsicObject resource = searchRegistry.getExtrinsic(refLid);
         		      //out.println("resource_name = " + searchRegistry.getSlotValues(resource, "resource_name").get(0) + "<br>");
         		      //out.println("resource_url = " + searchRegistry.getSlotValues(resource, "resource_url").get(0) + "<br>");
         		      if (resource!=null) {
         		         resname = searchRegistry.getSlotValues(resource, "resource_name").get(0);
         			     reslink = searchRegistry.getSlotValues(resource, "resource_url").get(0); 
         		      }
         		   %>
         		   <li><a href=<%=reslink%> target="_new"><%=resname%></a><br>
         		   <%
         		} // end for
         		} // end if
             } // end resources
             else if (tmpValue.startsWith("node_name")) {
                List<String> svalues = searchRegistry.getSlotValues(dsObj, "node_ref");    
                if (svalues!=null) { 	 
    	 	       for (int j=0; j<svalues.size(); j++) {
    	 		      String lid = (String) svalues.get(j);
    	   		      if (lid.indexOf("::")!=-1) 
    	   		         lid = lid.substring(0, lid.indexOf("::"));
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
    	 		      String lid = (String) svalues.get(j);
    	   		      if (lid.indexOf("::")!=-1) 
    	   		         lid = lid.substring(0, lid.indexOf("::"));
    	    	      ExtrinsicObject nodeObj = searchRegistry.getExtrinsic(lid);
    	    	      if (nodeObj!=null) {
    	    	         List<String> svalues2 = searchRegistry.getSlotValues(nodeObj, "node_to_data_archivist");
    	    	         if (svalues2!=null)  {
    	    	   	        String personLid = svalues2.get(0);
    	   		            if (personLid.indexOf("::")!=-1) 
    	   		               personLid = personLid.substring(0, personLid.indexOf("::"));
    	   		               
    	    	   	  	    ExtrinsicObject personObj = searchRegistry.getExtrinsic(personLid);
    	    	   	  	    if (personObj!=null) {
    	           	           String tmpVal2 = searchRegistry.getSlotValues(personObj, tmpValue).get(0).toUpperCase();
    	    	  	           out.println(tmpVal2 + "<br>");
    	    	  	        }
    	    	         }
    	    	      } // end if (nodeObj!=null)
    	           } // end for
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

