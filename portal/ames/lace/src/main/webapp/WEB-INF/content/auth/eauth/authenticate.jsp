<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>

<head>
<title>eAuth Authentication</title>
<link rel="stylesheet" type="text/css" href="../../assets/normalize.css" />
<link rel="stylesheet" type="text/css" href="../../assets/auth.css" />
<style type="text/css">
th, td {
  border: 1px solid gray;
  padding: 5px;
}
</style>
</head>

<body>
<div id="header">
<div id="logo"><img src="../../images/logo-lace.png" /><img src="../../images/logo-nasa.png" /></div>
<h1>eAuth Authentication</h1>
</div>

<div id="content">
<p>Headers in the request:</p>
<table>
  <tr><th>Name</th><th>Value</th></tr>
  <s:iterator value="headerNames" var="name">
    <tr>
      <td><s:property value="name" /></td>
      <td><s:property value="headers[#name]" /></td>
    </tr>
  </s:iterator>
</table>
<p>You have logged in as user: <s:property value="remoteUser" /></p>
</div>
</body>
</html>
