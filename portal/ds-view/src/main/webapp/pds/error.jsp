<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>

<HEAD>
<META http-equiv="Content-Style-Type" content="text/css">
<TITLE>Error</TITLE>

<%@ page language="java" session="false" isThreadSafe="false" 
info="JSP Error Page" isErrorPage="true" 
import="java.io.StringWriter, java.io.PrintWriter" %>

</HEAD>

<BODY bgcolor="#FFFFFF">
<H1>Ooops!</H1>
<HR>
<%! StringWriter s = new StringWriter();
PrintWriter w = new PrintWriter(s); %>
<% exception.printStackTrace(w);
w.close(); %>
<P>Some error occurred! You might
want to let the system administrators know.</P>
<P>If you're a system administrator, then the
following might be useful to you:</P>
<UL>
  <LI>The exception object was of class <B><%= exception.getClass().getName() %></B>
  <LI>The exception's message is <B><%= exception.getMessage() %></B>
  <LI>The stack trace is:
  <PRE><%= s.getBuffer().toString() %></PRE>
  
</UL>
</BODY>
</HTML>

