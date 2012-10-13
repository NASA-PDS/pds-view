<%@ page language="java" session="true" contentType="text/html; charset=UTF-8" info="Config" isErrorPage="true" 
  import="gov.nasa.jpl.oodt.grid.AuthenticationRequiredException,java.io.StringWriter,java.io.PrintWriter"
%>
<%--
  Copyright 2005 California Institute of Technology. 
  ALL RIGHTS RESERVED. U.S. Government Sponsorship acknowledged.

  $Id$
--%>
<jsp:useBean id="cb" scope="session" class="gov.nasa.jpl.oodt.grid.ConfigBean"/>
<?xml version='1.0' encoding='UTF-8'?>
<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>
<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en' lang='en'>
  <head>
    <title>Web Grid</title>
    <link rel='stylesheet' type='text/css' href='style.css'/>
  </head>
  <body>
    <%! StringWriter s = new StringWriter();
    PrintWriter w = new PrintWriter(s); %>
    <% exception.printStackTrace(w); w.close(); %>

    <h1>Web Grid</h1>
    <% if (cb.getMessage().length() > 0) { %>
      <div class='error'><jsp:getProperty name='cb' property='message'/></div>
    <% } %>

    <% if (exception instanceof AuthenticationRequiredException) { %>
      <form action='login' method='post'>
        <fieldset>
    <legend>Log In</legend>
    <div class='field'>
      <label for='pw'>Administrator password:</label>
      <div class='fieldHelp'>
        Passwords are case sensitve; check your CAPS LOCK key, if necessary.
      </div>
      <input id='pw' type='password' name='password'/>
    </div>
    <div class='formControls'>
      <input type='submit' name='submit' value='Log In'/>
          </div>
        </fieldset>
      </form>
   <% } else { %>
     <h1>Error</h1>
     <div>Exception <code><%= exception.getClass().getName() %></code>: <%= exception.getMessage() %></div>
     <pre><%= s.getBuffer().toString() %></pre>
   <% } %>
  </body>
</html>
