<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");
%>
<html>
<head>
        <title>PDS Target Profile</title>
        <META  NAME="keywords"  CONTENT="Planetary Data System">

        <META  NAME="description" CONTENT="This website serves as a mechanism for searching the PDS planetary
archives.">
<link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">
<%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false" contentType="text/html; charset=ISO-8859-1" 
import="jpl.eda.xmlquery.*, jpl.eda.profile.*, jpl.eda.profile.ProfileElement, java.util.*, java.io.*, java.sql.*, java.net.URLDecoder"
%>

<SCRIPT LANGUAGE="JavaScript">
<%@ include file="/pds/utils.js"%>
</SCRIPT>

</head>

<%!
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

<body BGCOLOR="#000000">

<%//get list of target display elements for this
String[] displayedElements = {"TARGET_NAME","PRIMARY_BODY_NAME", 
                    "ORBIT_DIRECTION", "ROTATION_DIRECTION", 
                    "TARGET_TYPE" , "TARGET_DESCRIPTION", 
                    "REFERENCE_DESCRIPTION", "RESOURCE_LINK"};

ProfileClient profileClient = new ProfileClient("urn:eda:rmi:JPL.PDS.Target");
%>

<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
  <tr>
    <td>
      <%@ include file="/pds/pds_header.html" %>

<%
if  (cleanParam(request.getParameter("TARGET_NAME"))==null) {
%>
     <table width="760" border="0" cellspacing="3" cellpadding="2">
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Target Information</b>
                            </font><br><br>
                </td>
        </tr>

        <tr valign="TOP">
                <td bgcolor="#F0EFEF" width=200 valign=top>
                Please specify a valid TARGET_NAME
                </td>
        </tr>

      </table>

<%
}
else {


   String targetId = request.getParameter("TARGET_NAME");
   targetId = targetId.toUpperCase();

   XMLQuery xml_query = new XMLQuery(
         /*keywordQuery*/"TARGET_NAME=\"" + targetId + "\"",
         /*id*/"OODT_XML_QUERY_V0.1",
         /*title*/"OODT_XML_QUERY - Bean Query", 
			/*desc*/"This query can be handled by the OODT System",
         /*ddId*/null,
			/*resultModeId*/"profile",
			/*propType*/"BROADCAST",
			/*propLevels*/"N/A",
                        0);
   List profile = profileClient.query(xml_query);

   if (profile.size() <1) { %>

   <table width="760" border="0" cellspacing="3" cellpadding="2">
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Target Information</b>
                            </font><br><br>
                </td>
        </tr>
            
        <tr valign="TOP">
                <td bgcolor="#F0EFEF" width=200 valign=top>
                <b><%=targetId%></b> is not a valid target name 
                </td>
        </tr>
      
      </table>

<% }
   else {

      // we should get back only 1 profile
      Map elements = ((Profile)profile.get(0)).getProfileElements(); 
%>

      <table width="760" border="0" cellspacing="3" cellpadding="2">
        <tr valign="TOP">
                <td valign="TOP" colspan="2" class="pageTitle">
                        <br><FONT color="#6F4D0E"><b>Target Information</b></font><br><br>
                </td>
        </tr>

      <% 
	     LinkedList resname = new LinkedList();
	     ProfileElement pe_resname = (ProfileElement)elements.get("RESOURCE_NAME");
	     if (pe_resname == null){
	  	      resname.add("noResname");
	     }else{
	  	     for (Iterator e = pe_resname.getValues().iterator(); e.hasNext();) {
	  		     String val = (String)e.next();
        	     val = val.replaceAll("\n","<br>");
			     resname.add(val);
		     }
	     }

	     for (int i=0; i<displayedElements.length; i++) {
             if (elements.containsKey(displayedElements[i])) { 
                ProfileElement pe = (ProfileElement)elements.get(displayedElements[i]); %>
               <TR>
                 <td bgcolor="#F0EFEF" width=200 valign=top><%=displayedElements[i]%></td> 
                 <td bgcolor="#F0EFEF" valign=top>

				 <%-- Preserve exact spacing.  This is important for
                    data such as diagrams and tables.
                    Both <pre> and <tt> are used here to overwrite font
                    defined for the <pre> tags in the top level css file --%>

				 <%    if (displayedElements[i].equals("TARGET_DESCRIPTION")) {
                       String val = (String)pe.getValues().iterator().next();
             %>
							<pre> <tt><%=val%></tt></pre>
				 <%    } else { //non-description

                     // RESOURCE_LINK is handled separately because
                     // it displays resname
                     if (displayedElements[i].equals("RESOURCE_LINK")){
			 		      int a = 0;
			 		      for (Iterator e = pe.getValues().iterator(); e.hasNext();){
                        String val = (String)e.next();
                        val = val.replaceAll("\n","<br>");
                        if (val.startsWith("http://") || 
                            val.startsWith("HTTP://")) { %>
                      <a href="<%=val%>" target="_blank"> <%if((resname.get(a)) != null)out.print(resname.get(a));%></a><br>
             <%          } else { %>
                      <%=val%><br>
             <%          }
			 		          a++;
                       }
					       } 
					       else {

						      for (Iterator e = pe.getValues().iterator(); e.hasNext();) {
                  	     String val = (String)e.next();
                  	     val = val.replaceAll("\n","<br>");
                  	     if (val.startsWith("http://") 
                            || val.startsWith("HTTP://")) { %>
                      <a href="<%=val%>" target="_blank"><%=val%></a>
             		<%      } else { %>
                      <%=val%><br>
             		<%      }
                        } //for
					       } 
                } %>

                </td>
               </TR>
         <%
             } else { // this element does not have a value %>
               <TR>
                  <td bgcolor="#F0EFEF" width=200 valign=top><%=displayedElements[i]%></td>
                  <td bgcolor="#F0EFEF"><%out.print("N/A");%></td>
               </TR>
         <%  }
         } // for loop
       } // if a profile is found
   } // if target name is specified 
         %>

        <%@ include file="/pds/footer.html" %>
        </table>

    </td>
  </tr>

</TABLE>

</BODY>
</HTML>

