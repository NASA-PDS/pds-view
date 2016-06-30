<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="stylesheet" type="text/css" href="<s:url value="/web/css/default.css" />" />
		<link rel="stylesheet" type="text/css" href="<s:url value="/web/css/minimal.css" />" />
		<link rel="shortcut icon" href="<s:url value="/web/favicon.ico" />" />
		<script type="text/javascript" language="javascript" src="<s:url value="/Constants.js" />L=<s:property value="userLocale" />"></script>
		<script type="text/javascript" language="javascript" src="<s:url value="/web/resources/js/render/full.js" />"></script>
		<script id="_fed_an_js_tag" type="text/javascript" language="javascript" src="<s:url value="/web/js/federated-analytics.all.min.js?agency=NASA&sub-agency=ARC&vcto=12" />"></script>
		<title><s:if test="title != null"><s:property value="title"/> : </s:if><s:text name="application.title"/></title>
	</head>
	<body>
		<noscript><link rel="stylesheet" type="text/css" href="<s:url value="/web/css/noScript.css" />" /></noscript>
		<tiles:insertAttribute name="body" />
	</body>
</html>