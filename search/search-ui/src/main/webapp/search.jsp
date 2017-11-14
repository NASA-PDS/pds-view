<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- InstanceBegin template="/Templates/jsp.dwt" codeOutsideHTMLIsLocked="false" -->

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" session="true" isThreadSafe="true"
	info="PDS Search" isErrorPage="false"
	contentType="text/html; charset=ISO-8859-1"
	import="java.net.*,java.util.*,java.io.*"%>
<%

String pdshome = application.getInitParameter("pdshome.url");
String contextPath = request.getContextPath() + "/";

String qString = request.getParameter("q");
String query = "", newQString = "";
if (qString == null) {
  response.sendRedirect("index.jsp");
  return;
}

// Remove specific parameter included from PDS Header Search
List<String> ignoredParams = new ArrayList<String>();
ignoredParams.add("in");
ignoredParams.add("words");
ignoredParams.add("search_scope");


// Loop through request params to ignore params that come from search
// Allows for parameters to be appended to search-service query
query = "";
Map<String, String[]> params = request.getParameterMap();
for (String name : params.keySet()) {
  if (!ignoredParams.contains(name)) {
    for (String value : Arrays.asList(params.get(name))) {
      if (value != null) {
        query += name + "=" + URLEncoder.encode(value, "UTF-8")+ "&";
      }
    }
  }
}

%>

<head>
<!-- InstanceBeginEditable name="doctitle" -->
<title>PDS: Search Results</title>
<!-- InstanceEndEditable -->
<c:import url="/includes.html" context="/include" />
<!-- InstanceBeginEditable name="head" -->
<!-- InstanceEndEditable -->
<!-- InstanceParam name="menu_section" type="text" value="data" -->
<!-- InstanceParam name="menu_item" type="text" value="data_data_search" -->
<!-- InstanceParam name="left_sidebar" type="boolean" value="false" -->
<!-- InstanceParam name="standard_sidebar" type="boolean" value="false" -->
<!-- InstanceParam name="standard_page_content" type="boolean" value="false" -->
<!-- InstanceParam name="custom_page_class" type="text" value="sidebar" -->

<script type="text/javascript"
	src="https://code.jquery.com/jquery.min.js"></script>

<script type="text/javascript">
$(function() {
    //var query = window.location.href.slice(window.location.href.indexOf('?') + 1).split('@@');
    var query = '<%= query %>';
		$.get('../search-service/pds/archive-filter?' + query, function(data) {
			$('.output').html(data);
			//alert('Load was performed.');
		});
	
		$('div').on('click', 'a.tools-button', function() {
			$('.tool').slideToggle('fast', function() {
				if ($('.tool').is(":visible")) {
					$('a.tools-button').text("Hide");
				} else {
					$('a.tools-button').text("More related search tools...");
				}
			});

		});

		$('div').on('click', 'a.info-button', function() {
			$('.info').slideToggle('fast', function() {
				if ($('.info').is(":visible")) {
					$('a.info-button').text("Hide");
				} else {
					$('a.info-button').text("More related archive resources...");
				}
			});

		});

	});
</script>

</head>

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
<!-- InstanceEnd -->
</html>
