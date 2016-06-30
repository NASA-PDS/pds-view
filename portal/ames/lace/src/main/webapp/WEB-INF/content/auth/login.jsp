<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>

<head>
<title>Login/Register</title>
<link rel="stylesheet" type="text/css" href="../assets/normalize.css" />
<link rel="stylesheet" type="text/css" href="../assets/auth.css" />
</head>

<body>
<div id="header">
<div id="logo"><img src="../images/logo-lace.png" /><img src="../images/logo-nasa.png" /></div>
<h1>Login/Register</h1>
</div>

<div id="content">
<div class="halfColumn first">
<h2>Log In with an External Account</h2>
<p>If you have an account with one of the external service providers below,
you may follow the appropriate link to login using those credentials. The
system uses your external account username and email address in order to
store and retrieve the labels you create within LACE.</p>

<p>
  <s:a action="login">
    <s:param name="oauthProvider">google</s:param>
    Log in using Google
  </s:a>
</p>
<p>
  <s:a action="login">
    <s:param name="oauthProvider">linkedin</s:param>
    Log in using Linked-In
  </s:a>
</p>
</div>

<div class="halfColumn last">
<h2>Log In with NASA NDC</h2>
<p>If you are already a NASA employee or affiliate with an NDC account,
you can follow the link below to log in. The system will use your Agency
User ID to store and retrieve the labels you create within LACE.</p>

<p>If you do not have an NDC account but wish to use NASA authentication,
click the link below to initiate the NDC account registration process.
In your request, include your 1) name, 2) email address, 3) affiliation,
and 4) your connection to PDS. You will be invited in a follow-up email
to complete the registration. The process is managed by NASA's Enterprise
IT group and takes about a week to completely provision your account.</p>
<p>
  <s:url var="eAuthURL" namespace="/auth/ndc" action="authenticate" />
  <s:a href="%{eAuthURL}">
    Log in using NASA NDC
  </s:a>
</p>
<p>
  <s:a href="mailto:rich.keller@nasa.gov">Request an NDC account</s:a>
</p>
</div>

</div>
</body>
</html>
