<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>

<head>
<title>NDC Authentication</title>
<link rel="stylesheet" type="text/css" href="../../assets/normalize.css" />
<link rel="stylesheet" type="text/css" href="../../assets/auth.css" />
</head>

<body>
<div id="header">
<div id="logo"><img src="../../images/logo-lace.png" /><img src="../../images/logo-nasa.png" /></div>
<h1>NDC Authentication</h1>
</div>

<div id="content">
<p>You have logged in as user: <s:property value="remoteUser" /></p>
</div>
</body>
</html>
