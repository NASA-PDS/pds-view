<%
String pdshome = "";
pdshome = application.getInitParameter("pdshome.url");
%>
<HTML><HEAD><TITLE>Planetary Data System: Data Set Search</TITLE>
<META content="Planetary Data System" name=keywords>
<META content="This website serves as a mechanism for searching the PDS planetary archives." name=description>
<link href="<%=pdshome%>css/pds_style.css" rel="stylesheet" type="text/css">
<%@ page language="java" session="true" isThreadSafe="true" info="PDS Search" isErrorPage="false"
contentType="text/html; charset=ISO-8859-1" import="javax.servlet.http.*, jpl.pds.util.*, jpl.pds.beans.*, java.io.*, java.sql.*, java.util.*" %>
<jsp:useBean class="jpl.pds.beans.SearchBean" id="searchBean" scope="session" />
<jsp:useBean class="jpl.pds.beans.GetSearchParams" id="paramBean" scope="session" />

<script language="JavaScript"
src="<%=pdshome%>js/lastMod.js"></script>
<script language="JavaScript"
src="<%=pdshome%>js/popWindow.js"></script>

<SCRIPT language=JavaScript>
<%@ include file="/pds/utils.js"%>

function isDate(param) {
        return (param.value.match(/^\d{4,4}-\d{2,2}-\d{2,2}$/) ||
                param.value.match(/^\d{4,4}$/));
}

function isTextChanged (param)
{
   return (param.value != "" && trim(param.value) != "YYYY-MM-DD");
}

function validateSearchParams()
{
   var sel_count = 0;
   if (isParamSelected(document.postForm.msnname) ||
       isParamSelected(document.postForm.targname) ||
       isParamSelected(document.postForm.targtype) ||
       isParamSelected(document.postForm.insttype) ||
       isParamSelected(document.postForm.instname) ||
       isTextChanged(document.postForm.strttime) ||
       isTextChanged(document.postForm.stoptime) ||
       isParamSelected(document.postForm.dataobjtype) ||
       isParamSelected(document.postForm.dsid) ||
       isParamSelected(document.postForm.dsname) ||
       isParamSelected(document.postForm.insthostname) ||
       isParamSelected(document.postForm.insthosttype) ||
       isParamSelected(document.postForm.archivestat) ||
       isParamSelected(document.postForm.nodename)) {
       sel_count++;
   }
   if (sel_count == 0) {
      alert ('You have not selected any parameters. \nPlease go back and select at least one.');
      return false;
   }
   if (isTextChanged(document.postForm.strttime) && (! isDate(document.postForm.strttime))) {
      alert ('Please enter Start Date in YYYY-MM-DD format');
      return false;
   }

   if (isTextChanged(document.postForm.stoptime) && (! isDate(document.postForm.stoptime))) {
      alert ('Please enter Stop Date in YYYY-MM-DD format');
      return false;
   }
   return true;
}

function changeSearchSpec()
{
  var frm = document.postForm;

   var msnname_sel = isParamSelected(document.postForm.msnname);
   var j = 0;
   var a = 0;
   var msn = document.postForm.msnname;

   if (msnname_sel) {
      for  (j=0; j<msn.options.length;j++) {
         if (msn.options[j].selected) {
                   document.postForm.msntext[a++].value=msn.options[j].text;
         }
       }
    }
    else {
       for (j=0; j<msn.options.length;j++) {
                  document.postForm.msntext[a++].value=msn.options[j].text;
       }
    }

  frm.action = '/pds/power.jsp';
  frm.onSubmit = '';
//  frm.method = 'get';
  frm.submit();
}

</SCRIPT>
<%@ include file="/pds/searchParamsJSP.jsp" %>

</HEAD>

<BODY bgcolor="#000000" onLoad="preloadImages();">

<%-- Save the request URI in the user's session so we know to take him back here from the results page --%>
<% request.getSession(true).setAttribute("requestURI", request.getRequestURI()); %>

<FORM action=/servlet/jpl.pds.servlets.QueryServlet id=postForm method=post name=postForm onSubmit="return validateSearchParams()">
<input type=hidden name=targnamechoices value="(<%=opts[TARGNAMES]%>)">
<input type=hidden name=hasParams value="<%=((request.getParameterValues("targname")!=null)?"1":"0")%>">
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>
<input type="hidden" name="msntext"  value=""/>



<table align="center" bgColor="#FFFFFF" BORDER="0" CELLPADDING="10" CELLSPACING="0">
  <tr>
    <td>
      <%@ include file="/pds/pds_header.html" %>

      <table width="760" border="0" cellspacing="0" cellpadding="0">
       <tr>
        <td><img src="/pds/images/spacer.gif" width="5" height="5" border="0">
        </td>
       </tr>
       <tr>
         <td valign="top">
          <table width="578" border="0" cellspacing="0" cellpadding="0">
	   <tr>
		<td>
   		<!-- main content begin -->
		   <table border="0" cellpadding="0" cellspacing="0">
                   <tr>
                     <td>
                      <table border="0" cellpadding="0" cellspacing="0" width="760">
                        <tr>
                          <td class="pageTitle" align=left><font color="#6F4D0E">Data Set Power Search</fo
nt>
                          </td>
                          <td align=right color="#6F4D0E" valign=center>
                            <A href="javascript:openWindow('data_search_help.jsp', 'Result',550,500)">
                            <IMG SRC="/pds/images/btn_help.gif" BORDER=0></A>
                          </td>
                        </tr>
                      </table>
                     </td>
                   </tr>

		   <tr>
		    <td><img src="/pds/images/gray.gif" width="760" height="1" alt="" border="0"></td>
		   </tr>
                   <tr>
                     <td>
                        <FONT FACE="verdana" size="2">
                        <ul>
                                <li>Please click on one or more parameters, then hit the Filter icon <img src="/pds/images/btn_filter.gif" border=0 alt="Filter">&nbsp;<A href="javascript:openWindow('data_search_help.jsp', 'Result',550,500)">to narrow your search.</A></li>
                                <li>Click on parameter title for more information.</li>
                                <li><A href="javascript:openWindow('data_search_help.jsp', 'Result',550,500)">Select one or more parameters</A> from below, then hit Go!</li>
                        </ul>
                        </font>
                     </td>
                   </tr>
	           <tr>
	            <td><img src="/pds/images/spacer.gif" width="5" height="5" border="0"></td>
	           </tr>

                   <tr bgcolor="#123261">
                            <td align="right" valign="center" height=22>
                              <A href="/pds/power.jsp"><IMG SRC="/pds/images/btn_reset.gif" BORDER=0></A>
                               &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                              <INPUT alt="Go Button" border=0 src="/pds/images/btn_go.gif" type=image>
                               &nbsp;&nbsp;
                            </td>
                   </tr>

                   <tr>
                     <td>
                       <table border="0" cellspacing="0" cellpadding="0" bgcolor="#EFEFEF" width=760>
                       <%@ include file="/pds/quickSearchParams.jsp" %>
                       <%@ include file="/pds/advancedSearchParams.jsp" %>
                       <%@ include file="/pds/powerSearchParams.jsp" %>
                       </table>
                     </td>
                   </tr>

                   <tr bgcolor="#123261">
                     <td>
                       <table border="0" cellpadding="0" cellspacing="0" width="760">
                          <tr>
                            <td valign="center" bgcolor="#003366" align="left">
                              <a href="javascript:switchPage(0)"><font face="verdana" size="2" 
                               color="#FFFFFF"><b>&nbsp;Quick Search</b></font></a>&nbsp;&nbsp;
                               <font color="#FFFFFF">|</font>&nbsp;&nbsp
                              <a href="javascript:switchPage(1)"><font face="verdana" size="2" 
                               color="#FFFFFF"><b>&nbsp;Advanced Search</b></font></a>
                               <font color="#FFFFFF"></font>&nbsp;&nbsp
                             <!-- <a href="javascript:switchPage(3)"><font face="verdana" size="2" 
                               color="#FFFFFF"><b>&nbsp;Text Search</b></font></a>&nbsp;&nbsp; -->
                            </td>
                            <td align="right" valign="center" height=22>
                              <A href="/pds/power.jsp"><IMG SRC="/pds/images/btn_reset.gif" BORDER=0></A>
                               &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                              <INPUT alt="Go Button" border=0 src="/pds/images/btn_go.gif" type=image>
                               &nbsp;&nbsp;
                            </td>

                          </tr>
                       </table>
                     </td>
                   </tr>

                  </table>
	        <!-- main content end -->
                </td>
	     </tr>
	     <tr>
	       <td><img src="/pds/images/spacer.gif" width="5" height="5" border="0"></td>
	     </tr>
           </td>
	</table>

        <%@ include file="/pds/ds_footer.html" %> 

        <%@ include file="/pds/footer.html" %>
    </td>
  </tr>
</table>
</FORM>
<%@ include file="/pds/ds_map.html" %>
</BODY>
</HTML>
