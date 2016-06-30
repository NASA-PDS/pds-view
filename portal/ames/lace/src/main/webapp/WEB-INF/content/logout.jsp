<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>

<head>
<title>Close Browser to Complete Logout</title>
<link rel="stylesheet" type="text/css" href="assets/normalize.css" />
<link rel="stylesheet" type="text/css" href="assets/auth.css" />
</head>

<body>
<div id="header">
<div id="logo"><img src="images/logo-lace.png" /><img src="images/logo-nasa.png" /></div>
<h1>Close Browser to Complete Logout</h1>
</div>

<div id="content">
<p>When using NASA NDC authentication, you must close your browser application
to complete the logout process.</p>

<p>If you do not wish to log out, you can <a href="<s:property value="rootUrl" />">return to LACE</a>.</p>
</div>

</body>
</html>
