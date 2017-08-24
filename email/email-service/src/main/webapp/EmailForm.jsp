<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>Email Service</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  </head>
  <body>
    <table>
      <tr>
        <td><img src="images/pds4_logo.png" alt="PDS Logo" /></td>
        <td><img src="images/spacer.gif" alt="Spacer" width="50"/></td>
        <td><h1>Email Service</h1></td>
      </tr>
    </table>

    <hr/>

    <p>The Email Service provides functionality to accept email requests from a Java application as well as Javascript-based clients (e.g., Feedback Form, Tool Registry, etc.). The service provides two interfaces: API and Web interface for interacting with the service. The Email Service accepts a list of email addresses, along with the subject and message content. The service can also be exercised with the form below:
    </p>

    <form action="EmailSendingServlet" method="post">   
      <table border="0" width="35%" align="center">
        <tr>
          <td width="50%">Recipient Address(es)</td>
          <td><input type="text" name="recipients" size="50" multiple=true required /></td>
        </tr>
        <tr>
          <td>Subject</td>
          <td><input type="text" name="subject" size="50" /></td>
        </tr>
        <tr>
          <td>Content</td>
          <td><textarea rows="10" cols="70" name="content"></textarea></td>
        </tr>
        <tr>
          <td colspan="2" align="center"><input type="submit"
            value="Send" /></td>
        </tr>
      </table>
    </form>
  </body>
</html>