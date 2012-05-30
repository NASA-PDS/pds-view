<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en"><!-- InstanceBegin template="/Templates/jsp.dwt" codeOutsideHTMLIsLocked="false" -->

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!--  %@ page language="java" session="true" isThreadSafe="true" 
info="PDS Search" isErrorPage="false"
contentType="text/html; charset=ISO-8859-1" 
import="javax.servlet.http.*, jpl.pds.util.*, jpl.pds.beans.*, java.sql.*, java.util.*, java.io.*" %-->
<%
String pdshome = application.getInitParameter("pdshome.url");
String contextPath = request.getContextPath() + "/";
%>

<head>
<!-- InstanceBeginEditable name="doctitle" -->
<title>PDS: Search Results</title>
<!-- InstanceEndEditable -->
<c:import url="/includes.html" context="/include" />
<!-- InstanceBeginEditable name="head" --><!-- InstanceEndEditable -->
<!-- InstanceParam name="menu_section" type="text" value="data" -->
<!-- InstanceParam name="menu_item" type="text" value="data_data_search" -->
<!-- InstanceParam name="left_sidebar" type="boolean" value="false" -->
<!-- InstanceParam name="standard_sidebar" type="boolean" value="false" -->
<!-- InstanceParam name="standard_page_content" type="boolean" value="false" -->
<!-- InstanceParam name="custom_page_class" type="text" value="sidebar" -->
</head>

<%
String qString = request.getParameter("q");
String query = "", newQString = "";
if (qString == null) {
  response.sendRedirect("index.jsp");
  return;
}

if (qString.equals("")) {
  newQString = qString + "-archive-status%3ASUPERSEDED";
} else {
  newQString = qString + "%20AND%20-archive-status%3ASUPERSEDED";
}
  query = request.getQueryString().replace("q="+qString, "q="+newQString);

%>


<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>

<script type="text/javascript">
$(function() {
    //var query = window.location.href.slice(window.location.href.indexOf('?') + 1).split('@@');
    var query = '<%= query %>';
    //alert(query);
    $.get('/search-service/pds/search?' + query, function(data) {
      $('.output').html(data);
      //alert('Load was performed.');
    });
});
</script>



<body class="fullWidth  menu_data menu_item_data_data_search sidebar">
<!--[if IE]>
<div id="IE">
<![endif]--> 

<c:import url="/header.html" context="/include" />
<c:import url="/main_menu.html" context="/include" />

<div id="submenu">



<c:import url="/data_menu.html" context="/include" />


<!-- <ul id="submenu_standards"><li>&nbsp;</li></ul> -->







<div class="clear"></div>
</div>

<!-- Sidebar -->





<!-- InstanceBeginEditable name="custom_content" -->

<!-- Added to contain search service  output -->
<div class="output"></div>

<!-- Commented out for new search service functionality -->
<!-- jsp:include page="http://pdsbeta:8080/search-service/select/" / -->

<!-- InstanceEndEditable -->

<c:import url="/footer.html" context="/include" />

    <!--[if IE]>
    </div>
    <![endif]--> 
  </body>
<!-- InstanceEnd --></html>
