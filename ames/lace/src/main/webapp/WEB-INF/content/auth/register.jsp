<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>

<head>
<title>Register to Use LACE</title>
<link rel="stylesheet" type="text/css" href="../assets/normalize.css" />
<link rel="stylesheet" type="text/css" href="../assets/auth.css" />
</head>

<body>
<div id="header">
<div id="logo"><img src="../images/logo-lace.png" /><img src="../images/logo-nasa.png" /></div>
<h1>Register to Use LACE</h1>
</div>

<div id="content">
<p>NASA requires you to register before using the LACE tool. Please tell us your
affiliation (your university, institute, NASA site, or research project) and the
reason you need to use the LACE tool. A NASA civil servant will review your
request and grant you access, usually within 1 business day. You will receive
an email when access has been granted.</p>
<s:fielderror />
<form method="POST">
<div>
<input type="hidden" name="issuer" value="<s:property value='userInfo.issuer' />" />
<input type="hidden" name="subject" value="<s:property value='userInfo.subject' />" />
<input type="hidden" name="givenName" value="<s:property value='userInfo.givenName' />" />
<input type="hidden" name="familyName" value="<s:property value='userInfo.familyName' />" />
<input type="hidden" name="email" value="<s:property value='userInfo.email' />" />
</div>
<p><label><span class="label">Email address:</span> <span><s:property value='userInfo.email' />"</span></label></p>
<p><label><span class="label">Name:</span> <input type="text" name="name" value="<s:property value='userInfo.formattedName' />" /></label></p>
<p><label><span class="label">Affiliation:</span> <input type="text" name="affiliation" value="<s:property value='affiliation' />" /></label></p>
<p><label><span class="label-above">Reason for using LACE:</span> <textarea name="reason"><s:property value='reason' /></textarea></label>

<p class="terms"><input type="checkbox" name="agree" value="true" /> I agree to the
<s:a action="terms-and-conditions" target="_blank">Terms and Conditions</s:a>
and acknowledge that all information, documents, and images comply with my organization's
rules regarding Export Control, Intellectual Property, and Copyright. I confirm that all
information provided here is accurate and true to the best of my knowledge. I understand
that including false information may lead to suspension from the site. By checking this
button, I agree that I have already met these provisions.</p>

<p class="buttons">
  <button type="submit" name="action" value="register">Register</button>
  <button type="submit" name="action" value="cancel">Cancel</button>
</p>
</form>
</div>
</body>
</html>