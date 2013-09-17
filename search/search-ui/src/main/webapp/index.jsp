<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en"><!-- InstanceBegin template="/Templates/jsp.dwt" codeOutsideHTMLIsLocked="false" -->

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- %@ page language="java" session="true" isThreadSafe="true" 
info="PDS Search" isErrorPage="false"
contentType="text/html; charset=ISO-8859-1" 
import="javax.servlet.http.*, jpl.pds.util.*, jpl.pds.beans.*, java.sql.*, java.util.*, java.io.*" %-->
<%
String pdshome = application.getInitParameter("pdshome.url");
String contextPath = request.getContextPath() + "/";
%>

<head>
<!-- InstanceBeginEditable name="doctitle" -->
<title>PDS: Data Search</title>
<!-- InstanceEndEditable -->
<c:import url="/includes.html" context="/include" />
<!-- InstanceBeginEditable name="head" --><!-- InstanceEndEditable -->
<!-- InstanceParam name="menu_section" type="text" value="data" -->
<!-- InstanceParam name="menu_item" type="text" value="data_data_search" -->
<!-- InstanceParam name="standard_page_content" type="boolean" value="true" -->
<!-- InstanceParam name="custom_page_class" type="text" value="" -->
</head>


<body class="menu_data menu_item_data_data_search ">
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

<!-- Main content -->
<div id="content">
<h1><a name="mainContent"></a><!-- InstanceBeginEditable name="pageTitle" -->Data Search<!-- InstanceEndEditable --></h1>
<div>
<!-- InstanceBeginEditable name="content" -->
<form action="search.jsp" method="get">
  <input type="text" name="q" size="60" /><input type="submit" value="Search" />
</form>
<p style="margin-top: 2em; margin-bottom: .5em;">Begin your search with one of the quick searches on the left, or try any of the following:</p>
<div id="IEBug">
  <ul>
    <li>A target name like <strong>mars</strong> or <strong>eros</strong></li>
    <li>An instrument name or type like <strong>spectrometer</strong> or <strong>laser altimeter</strong> or <strong>MOLA</strong></li>
    <li>A target body type like <strong>asteroid</strong></li>
    <li>A word or phrase to find in the description of a data set or search tool</li>
  </ul>

<p style="margin-top: 1em; margin-bottom: .5em;">You can further refine your query by:</p>
  <ul>
    <li>Use quotation marks to bind words that occur together, e.g. <strong>&quot;mars express&quot;</strong></li>
    <li>Specifying a search field before the word to search, e.g. <strong>target:mars</strong></li>
      <ul>
	<li>The legal search fields are <strong>target:</strong>, <strong>instrument:</strong>, <strong>investigation:</strong>, <strong>instrument_type:</strong></li>
      </ul>
    <li>Inserting logical operator <strong>OR</strong> or <strong>AND</strong>, e.g. <strong>target:uranus OR target:neptune</strong></li>
 </ul>
</div>
<!-- InstanceEndEditable -->
</div>
</div>



<c:import url="/footer.html" context="/include" />

    <!--[if IE]>
    </div>
    <![endif]--> 
  </body>
<!-- InstanceEnd --></html>
